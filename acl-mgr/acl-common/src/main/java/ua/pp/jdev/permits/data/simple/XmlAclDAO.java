package ua.pp.jdev.permits.data.simple;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;

@Lazy
@Component
public class XmlAclDAO implements AclDAO {
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
				if (dao.readByName(acl.getName()).isEmpty()) {
					dao.create(acl);
				}
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
	public Acl create(Acl acl) {
		Acl result = dao.create(acl);

		// save changes to XML data source
		saveToXml();
		
		return result;
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
	public Acl update(Acl acl) {
		Acl result = dao.update(acl);

		// save changes to XML data source
		saveToXml();
		
		return result;
	}

	@Override
	public Collection<Acl> readAll() {
		return dao.readAll();
	}

	@Override
	public Optional<Acl> read(String id) {
		return dao.read(id);
	}

	@Override
	public Optional<Acl> readByName(String name) {
		return dao.readByName(name);
	}
}
