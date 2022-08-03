package ua.pp.jdev.permits.data.simple;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

@Slf4j
@Component
@Profile("simple")
public class SimpleAclDAO implements AccessControlListDAO {
	private Map<String, AccessControlList> storage = new HashMap<>();

	public SimpleAclDAO() {
		log.info(getInitMessage());
	}

	protected String getInitMessage() {
		return "Initializing simple ACL datasource storing data in memory";
	}

	@PostConstruct
	protected void populate() {
		Accessor scaner = new Accessor();
		scaner.setName("bnk-scaner");
		scaner.setPermit(3);
		scaner.setAlias(true);
		scaner.setSvc(false);

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
		legalPerf.setOrgLevels(Set.of("CO", "VR", "MR", "RD"));

		Accessor legalLM = new Accessor();
		legalLM.setName("bnk-ls-lm");
		legalLM.setPermit(3);
		legalLM.setAlias(true);
		legalLM.setSvc(true);
		legalLM.setOrgLevels(Set.of("CO", "VR", "MR", "RD"));

		try {
			// Client ACL
			AccessControlList aclClient = new AccessControlList();
			aclClient.setName("bnk_client_acl");
			aclClient.setDescription("Client ACL");
			aclClient.setObjTypes(Set.of("bnk_client", "bnk_not_client"));
			aclClient.setAccessors(Set.of(scaner.clone(), legalPerf.clone(), legalLM.clone(), pam1.clone(),
					pam2.clone(), bAdmin.clone(), tAdmin.clone()));
			create(aclClient);

			// Committee ACL
			AccessControlList aclCommittee = new AccessControlList();
			aclCommittee.setName("bnk_committee_acl");
			aclCommittee.setDescription("Credit committee ACL");
			aclCommittee.setObjTypes(Set.of("bnk_committee"));
			aclCommittee.setAccessors(Set.of(scaner.clone(), legalPerf.clone(), legalLM.clone(), pam1.clone(),
					pam2.clone(), bAdmin.clone(), tAdmin.clone()));
			create(aclCommittee);

			// GRL ACL
			AccessControlList aclGRC = new AccessControlList();
			aclGRC.setName("bnk_grc_acl");
			aclGRC.setDescription("Group of related companies ACL");
			aclGRC.setObjTypes(Set.of("bnk_grc_acl"));
			aclGRC.setAccessors(Set.of(scaner.clone(), legalPerf.clone(), legalLM.clone(), pam1.clone(), pam2.clone(),
					bAdmin.clone(), tAdmin.clone()));
			create(aclGRC);

			// PS Conclusion ACL
			AccessControlList aclConclPS = new AccessControlList();
			aclConclPS.setName("bnk_concl_ps_acl");
			aclConclPS.setDescription("Pledge service conclusion ACL");
			aclConclPS.setObjTypes(Set.of("bnk_conclusion"));
			aclConclPS.setStatuses(Set.of("PS_S_CO_APPROVE", "PS_S_CO_ASSIGN", "PS_S_CO_CONCL", "PS_S_CO_DONE",
					"PS_S_CO_REJECT", "PS_S_CO_REV", "PS_S_RD_APPROVE", "PS_S_RD_ASSIGN", "PS_S_RD_CONCL",
					"PS_S_RD_DONE", "PS_S_RD_REJECT", "PS_S_RD_REV"));
			aclConclPS.setAccessors(Set.of(scaner.clone(), legalPerf.clone(), legalLM.clone(), pam1.clone(),
					pam2.clone(), bAdmin.clone(), tAdmin.clone()));
			create(aclConclPS);
		} catch (CloneNotSupportedException cnse) {
			throw new RuntimeException(cnse);
		}
	}

	@Override
	public Collection<AccessControlList> readAll() {
		// Disable direct access to stored data by getting every single ACL by
		// appropriate read method
		return storage.values().stream().map(acl -> read(String.valueOf(acl.getId())).get())
				.sorted(Comparator.comparing(AccessControlList::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		Optional<AccessControlList> result;

		// Create a clone of current ACL to avoid unwanted changes by consumer
		AccessControlList acl = storage.get(id);
		if (acl != null) {
			try {
				result = Optional.of(acl.clone());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			result = Optional.empty();
		}

		return result;
	}

	@Override
	public void create(AccessControlList newAcl) {
		if (newAcl != null) {
			newAcl.setId(IDGenerator.genStringID());
			newAcl.setAccessors(normilizeAccessors(newAcl.getAccessors()));
			storage.put(newAcl.getId(), newAcl);
		}
	}

	@Override
	public void update(AccessControlList data) {
		if (data != null && storage.containsKey(data.getId())) {
			AccessControlList acl = storage.get(data.getId());

			acl.setName(data.getName());
			acl.setDescription(data.getDescription());
			acl.setStatuses(data.getStatuses());
			acl.setObjTypes(data.getObjTypes());
			acl.setAccessors(normilizeAccessors(data.getAccessors()));
		}
	}

	private Set<Accessor> normilizeAccessors(Collection<Accessor> source) {
		// Purge Accessors in VOID state and setup others with PURE state
		return source.stream().filter(accessor -> !State.VOID.equals(accessor.getState()))
				.peek(accessor -> accessor.setState(State.PURE)).collect(Collectors.toSet());
	}

	@Override
	public boolean delete(String id) {
		return (storage.remove(id) != null);
	}
}
