package ua.pp.jdev.permits.data.simple;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;

@Component
public class XmlAclDAO implements AccessControlListDAO {
	@Value("${data.src}")
	private String dataSource;

	private XmlDataProvider provider;
	private SimpleAclDAO dao;

	public XmlAclDAO() {
		this.dao = new SimpleAclDAO();
	}

	@PostConstruct
	protected void populate() {
		try {
			provider = new XmlDataProvider(dataSource);
			provider.read().forEach(acl -> {
				dao.create(acl);
			});
		} catch (FileNotFoundException | XmlDataException e) {
			throw new RuntimeException(e);
		}
	}

	protected void saveToXml() {
		try {
			provider.write(readAll());
		} catch (XmlDataException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create(AccessControlList acl) {
		dao.create(acl);

		// save changes to XML data source
		saveToXml();
	}

	@Override
	public boolean delete(String id) {
		boolean result = dao.delete(id);

		// save changes to XML data source
		if (result) {
			saveToXml();
		}

		return result;
	}

	@Override
	public void update(AccessControlList acl) {
		dao.update(acl);

		// save changes to XML data source
		saveToXml();
	}

	@Override
	public Collection<AccessControlList> readAll() {
		return dao.readAll();
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		return dao.read(id);
	}

	@Override
	public Optional<AccessControlList> readByName(String name) {
		return dao.readByName(name);
	}
}
