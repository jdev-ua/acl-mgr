package ua.pp.jdev.permits.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.domain.AccessControlList;

@Component
public class AccessControlListDAO {
	private Map<String, AccessControlList> storage = new HashMap<>();

	public AccessControlListDAO() {
		AccessControlList acl1 = new AccessControlList();
		acl1.setName("bnk_client_acl");
		acl1.setDescription("Client ACL");
		acl1.addObjType("bnk_client");
		acl1.addObjType("bnk_not_client");
		create(acl1);

		AccessControlList acl2 = new AccessControlList();
		acl2.setName("bnk_committee_acl");
		acl2.setDescription("Credit committee ACL");
		acl1.addObjType("bnk_committee");
		create(acl2);

		AccessControlList acl3 = new AccessControlList();
		acl3.setName("bnk_grc_acl");
		acl3.setDescription("Group of related companies ACL");
		acl3.addObjType("bnk_grc_acl");
		create(acl3);
	}

	public List<AccessControlList> readAll() {
		return List.copyOf(storage.values());
	}

	public AccessControlList read(String id) {
		return storage.get(id);
	}

	public void create(AccessControlList acl) {
		acl.setId(IDGenerator.generateID("P%07d"));
		storage.put(acl.getId(), acl);
	}

	public void update(AccessControlList acl) {
		storage.put(acl.getId(), acl);
	}

	public AccessControlList delete(String id) {
		return storage.remove(id);
	}
}
