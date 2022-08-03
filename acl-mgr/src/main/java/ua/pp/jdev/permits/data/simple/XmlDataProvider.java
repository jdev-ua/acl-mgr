package ua.pp.jdev.permits.data.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.Accessor;

class XmlDataProvider {
	static final String ACL = "acl";
	static final String ACL_NAME = "name";
	static final String ACL_DESCRIPTION = "description";
	static final String ACL_OBJ_TYPES = "obj-types";
	static final String ACL_STATUSES = "statuses";
	static final String ACCESSOR = "accessor";
	static final String ACCESSOR_NAME = "name";
	static final String ACCESSOR_PERMIT = "permit";
	static final String ACCESSOR_XPERMITS = "xpermits";
	static final String ACCESSOR_ORG_LEVELS = "org-levels";
	static final String ACCESSOR_ALIAS = "alias";
	static final String ACCESSOR_SVC = "svc";
	static final String ROOT = "security-config";
	static final String REDUNDANT_XML_SYMBOLS = "[\t\n]*";

	static final String BACKUP_PREFIX_FORMAT_PATTERN = "%s/%s-%s.%s.bak";
	static final String BACKUP_SUFFIX_DATE_PATTERN = "yyyyMMdd_hhmmssSSS";

	private File file;

	public XmlDataProvider(String dataSource) throws FileNotFoundException {
		this(Paths.get(dataSource).toUri());
	}

	XmlDataProvider(URI dataSource) throws FileNotFoundException {
		file = ResourceUtils.getFile(dataSource);
	}

	public void write(Collection<AccessControlList> acls) throws XmlDataException {
		try {
			// Build backup-file name
			String backupLocation = String.format(BACKUP_PREFIX_FORMAT_PATTERN, file.getParentFile(),
					Files.getNameWithoutExtension(file.getName()),
					LocalDateTime.now().format(DateTimeFormatter.ofPattern(BACKUP_SUFFIX_DATE_PATTERN)),
					Files.getFileExtension(file.getName()));
			Path destination = Paths.get(backupLocation);
			// Backup current source file
			java.nio.file.Files.copy(file.toPath(), destination);

			// Create document from collection of ACLs
			Document doc = buildDocument(acls);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			// Make content easy for human read
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// Write new data to file
			transformer.transform(new DOMSource(doc), new StreamResult(file));
		} catch (TransformerException | ParserConfigurationException | IOException e) {
			throw new XmlDataException(e);
		}
	}

	private Document buildDocument(Collection<AccessControlList> acls) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		Element root = doc.createElement(ROOT);
		doc.appendChild(root);

		acls.stream().sorted((acl1, acl2) -> acl1.getName().compareTo(acl2.getName())).forEach(acl -> {
			// append acl
			Element aclElem = doc.createElement(ACL);
			root.appendChild(aclElem);
			// append acl.name
			aclElem.appendChild(newElement(doc, ACL_NAME, acl.getName()));
			// append acl.description
			aclElem.appendChild(newElement(doc, ACL_DESCRIPTION, acl.getDescription()));
			// append acl.objTypes
			aclElem.appendChild(newElement(doc, ACL_OBJ_TYPES, Joiner.on(",").join(acl.getObjTypes())));
			// append acl.statuses
			aclElem.appendChild(newElement(doc, ACL_STATUSES, Joiner.on(",").join(acl.getStatuses())));
			// append acl.accessors
			acl.getAccessors().stream()
					.sorted((accessor1, accessor2) -> accessor1.getName().compareTo(accessor2.getName()))
					.forEach(accessor -> {
						// append accessor
						Element accessorElem = doc.createElement(ACCESSOR);
						aclElem.appendChild(accessorElem);
						// append accessor.name
						accessorElem.appendChild(newElement(doc, ACCESSOR_NAME, accessor.getName()));
						// append accessor.permit
						accessorElem
								.appendChild(newElement(doc, ACCESSOR_PERMIT, String.valueOf(accessor.getPermit())));
						// append accessor.alias
						accessorElem.appendChild(newElement(doc, ACCESSOR_ALIAS, String.valueOf(accessor.isAlias())));
						// append accessor.svc
						accessorElem.appendChild(newElement(doc, ACCESSOR_SVC, String.valueOf(accessor.isSvc())));
						// append accessor.xPermits
						accessorElem.appendChild(
								newElement(doc, ACCESSOR_XPERMITS, Joiner.on(",").join(accessor.getXPermits())));
						// append accessor.orgLevels
						accessorElem.appendChild(
								newElement(doc, ACCESSOR_ORG_LEVELS, Joiner.on(",").join(accessor.getOrgLevels())));
					});
		});
		doc.normalizeDocument();

		return doc;
	}

	private Element newElement(Document doc, String name, String value) {
		Element result = doc.createElement(name);
		result.appendChild(doc.createTextNode(value));
		return result;
	}

	public Collection<AccessControlList> read() throws XmlDataException {
		Document doc;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new XmlDataException(e);
		}

		return parseDocument(doc);
	}

	private Collection<AccessControlList> parseDocument(Document doc) {
		List<AccessControlList> result = new ArrayList<>();

		doc.getDocumentElement().normalize();
		Element rootElement = doc.getDocumentElement();
		if (rootElement != null) {
			NodeList list = rootElement.getElementsByTagName(ACL);
			if (list != null && list.getLength() > 0) {
				for (int i = 0; i < list.getLength(); i++) {
					Element aclElement = (Element) list.item(i);
					NodeList childList = aclElement.getChildNodes();

					// parse acl
					AccessControlList acl = new AccessControlList();
					for (int j = 0; j < childList.getLength(); j++) {
						Node child = childList.item(j);

						// parse acl.name
						if (child.getNodeType() == Node.ELEMENT_NODE && ACL_NAME.equals(child.getNodeName())) {
							String name = child.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
							acl.setName(name);
						}
						// parse acl.name
						if (child.getNodeType() == Node.ELEMENT_NODE && ACL_DESCRIPTION.equals(child.getNodeName())) {
							String description = child.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
							acl.setDescription(description);
						}
						// parse acl.name
						if (child.getNodeType() == Node.ELEMENT_NODE && ACL_OBJ_TYPES.equals(child.getNodeName())) {
							String objTypes = child.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
							if (objTypes != null) {
								acl.setObjTypes(Sets.newHashSet(
										Splitter.on(',').trimResults().omitEmptyStrings().splitToList(objTypes)));
							}
						}
						// parse acl.name
						if (child.getNodeType() == Node.ELEMENT_NODE && ACL_STATUSES.equals(child.getNodeName())) {
							String statuses = child.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
							if (statuses != null) {
								acl.setStatuses(Sets.newHashSet(
										Splitter.on(',').trimResults().omitEmptyStrings().splitToList(statuses)));
							}
						}
						// parse acl.name
						if (child.getNodeType() == Node.ELEMENT_NODE && ACCESSOR.equals(child.getNodeName())) {
							acl.addAccessor(parseAccessor(child));
						}
					}

					result.add(acl);
				}
			}
		}

		return result;
	}

	private Accessor parseAccessor(Node node) {
		Accessor accessor = new Accessor();

		NodeList accessorConfig = node.getChildNodes();
		for (int k = 0; k < accessorConfig.getLength(); k++) {
			Node param = accessorConfig.item(k);
			// parse accessor.name
			if (param.getNodeType() == Node.ELEMENT_NODE && ACCESSOR_NAME.equals(param.getNodeName())) {
				String name = param.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
				accessor.setName(name);
			}
			// parse accessor.permit
			if (param.getNodeType() == Node.ELEMENT_NODE && ACCESSOR_PERMIT.equals(param.getNodeName())) {
				String permit = param.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
				accessor.setPermit(Integer.parseInt(permit));
			}
			// parse accessor.xPermits
			if (param.getNodeType() == Node.ELEMENT_NODE && ACCESSOR_XPERMITS.equals(param.getNodeName())) {
				String xPermits = param.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
				if (xPermits != null) {
					accessor.setXPermits(
							Sets.newHashSet(Splitter.on(',').trimResults().omitEmptyStrings().splitToList(xPermits)));
				}
			}
			// parse accessor.orgLevels
			if (param.getNodeType() == Node.ELEMENT_NODE && ACCESSOR_ORG_LEVELS.equals(param.getNodeName())) {
				String orgLevels = param.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
				if (orgLevels != null) {
					accessor.setOrgLevels(
							Sets.newHashSet(Splitter.on(',').trimResults().omitEmptyStrings().splitToList(orgLevels)));
				}
			}
			// parse accessor.alias
			if (param.getNodeType() == Node.ELEMENT_NODE && ACCESSOR_ALIAS.equals(param.getNodeName())) {
				String alias = param.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
				accessor.setAlias(Boolean.parseBoolean(alias));
			}
			// parse accessor.svc
			if (param.getNodeType() == Node.ELEMENT_NODE && ACCESSOR_SVC.equals(param.getNodeName())) {
				String svc = param.getTextContent().replaceAll(REDUNDANT_XML_SYMBOLS, "").trim();
				accessor.setSvc(Boolean.parseBoolean(svc));
			}
		}

		return accessor;
	}
}