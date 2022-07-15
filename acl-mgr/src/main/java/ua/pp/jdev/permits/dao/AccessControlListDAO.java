package ua.pp.jdev.permits.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.domain.AccessControlList;
import ua.pp.jdev.permits.domain.Accessor;

@Component
public class AccessControlListDAO {
	private Map<String, AccessControlList> storage = new HashMap<>();

	public AccessControlListDAO() {
		Accessor scaner = new Accessor();
		scaner.setName("bnk-scaner");
		scaner.setPermit(3);
		scaner.setAlias(true);
		scaner.setSvc(false);
		scaner.setOrgLevels(Set.of("co","vr","mr","rd"));
		
		Accessor bAdmin = new Accessor();
		bAdmin.setName("bnk_business_admin");
		bAdmin.setPermit(6);
		bAdmin.setAlias(false);
		bAdmin.setSvc(false);
		bAdmin.setXPermits(Set.of("EXECUTE_PROC", "CHANGE_LOCATION"));
		
		Accessor tAdmin = new Accessor();
		tAdmin.setName("bnk_tech_admin");
		tAdmin.setPermit(7);
		tAdmin.setAlias(false);
		tAdmin.setSvc(false);
		tAdmin.setXPermits(Set.of("EXECUTE_PROC", "CHANGE_LOCATION"));
		
		Accessor pam1 = new Accessor();
		pam1.setName("bnk_grc_pam1");
		pam1.setPermit(3);
		pam1.setAlias(true);
		pam1.setSvc(false);
		pam1.setXPermits(Set.of("EXECUTE_PROC"));
		
		Accessor pam2 = new Accessor();
		pam2.setName("bnk_grc_pam2");
		pam2.setPermit(3);
		pam2.setAlias(true);
		pam2.setSvc(false);
		pam2.setXPermits(Set.of("EXECUTE_PROC"));
		
		Accessor legalPerf = new Accessor();
		legalPerf.setName("bnk-ls-perf");
		legalPerf.setPermit(3);
		legalPerf.setAlias(true);
		legalPerf.setSvc(true);
		legalPerf.setOrgLevels(Set.of("co","vr","mr","rd"));
		
		Accessor legalLM = new Accessor();
		legalLM.setName("bnk-ls-lm");
		legalLM.setPermit(3);
		legalLM.setAlias(true);
		legalLM.setSvc(true);
		legalLM.setOrgLevels(Set.of("co","vr","mr","rd"));
		
		// Client ACL
		AccessControlList aclClient = new AccessControlList();
		aclClient.setName("bnk_client_acl");
		aclClient.setDescription("Client ACL");
		aclClient.addObjType("bnk_client");
		aclClient.addObjType("bnk_not_client");
		aclClient.setAccessors(List.of(scaner, legalPerf, legalLM, pam1, pam2, bAdmin, tAdmin));
		create(aclClient);

		// Committee ACL
		AccessControlList aclCommittee = new AccessControlList();
		aclCommittee.setName("bnk_committee_acl");
		aclCommittee.setDescription("Credit committee ACL");
		aclCommittee.addObjType("bnk_committee");
		aclCommittee.setAccessors(List.of(scaner, legalPerf, legalLM, pam1, pam2, bAdmin, tAdmin));
		create(aclCommittee);

		// GRL ACL
		AccessControlList aclGRC = new AccessControlList();
		aclGRC.setName("bnk_grc_acl");
		aclGRC.setDescription("Group of related companies ACL");
		aclGRC.addObjType("bnk_grc_acl");
		aclGRC.setAccessors(List.of(scaner, legalPerf, legalLM, pam1, pam2, bAdmin, tAdmin));
		create(aclGRC);
		
		// PS Conclusion ACL
		AccessControlList aclConclPS = new AccessControlList();
		aclConclPS.setName("bnk_concl_ps_acl");
		aclConclPS.setDescription("Pledge service conclusion ACL");
		aclConclPS.addObjType("bnk_conclusion");
		aclConclPS.addStatus("PS_S_CO_APPROVE");
		aclConclPS.addStatus("PS_S_CO_ASSIGN");
		aclConclPS.addStatus("PS_S_CO_CONCL");
		aclConclPS.addStatus("PS_S_CO_DONE");
		aclConclPS.addStatus("PS_S_CO_REJECT");
		aclConclPS.addStatus("PS_S_CO_REV");
		aclConclPS.addStatus("PS_S_RD_APPROVE");
		aclConclPS.addStatus("PS_S_RD_ASSIGN");
		aclConclPS.addStatus("PS_S_RD_CONCL");
		aclConclPS.addStatus("PS_S_RD_DONE");
		aclConclPS.addStatus("PS_S_RD_REJECT");
		aclConclPS.addStatus("PS_S_RD_REV");
		aclConclPS.setAccessors(List.of(scaner, legalPerf, legalLM, pam1, pam2, bAdmin, tAdmin));
		create(aclConclPS);
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
