package ua.pp.jdev.permits;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;

@SpringBootApplication
public class AclWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(AclWebApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(AccessControlListDAO dao) {
		return args -> {
			if(dao.readAll().size() > 0) return;
			
			Accessor scaner = new Accessor(State.NEW);
			scaner.setName("bnk-scaner");
			scaner.setPermit(3);
			scaner.setAlias(true);
			scaner.setSvc(false);

			Accessor bAdmin = new Accessor(State.NEW);
			bAdmin.setName("bnk_business_admin");
			bAdmin.setPermit(6);
			bAdmin.setAlias(false);
			bAdmin.setSvc(false);
			bAdmin.setXPermits(Set.of("EXECUTE_PROC", "CHANGE_LOCATION"));

			Accessor tAdmin = new Accessor(State.NEW);
			tAdmin.setName("bnk_tech_admin");
			tAdmin.setPermit(7);
			tAdmin.setAlias(false);
			tAdmin.setSvc(false);
			tAdmin.setXPermits(Set.of("EXECUTE_PROC", "CHANGE_LOCATION"));

			Accessor pam1 = new Accessor(State.NEW);
			pam1.setName("bnk_grc_pam1");
			pam1.setPermit(3);
			pam1.setAlias(true);
			pam1.setSvc(false);
			pam1.setXPermits(Set.of("EXECUTE_PROC"));

			Accessor pam2 = new Accessor(State.NEW);
			pam2.setName("bnk_grc_pam2");
			pam2.setPermit(3);
			pam2.setAlias(true);
			pam2.setSvc(false);
			pam2.setXPermits(Set.of("EXECUTE_PROC"));

			Accessor legalPerf = new Accessor(State.NEW);
			legalPerf.setName("bnk-ls-perf");
			legalPerf.setPermit(3);
			legalPerf.setAlias(true);
			legalPerf.setSvc(true);
			legalPerf.setOrgLevels(Set.of("CO", "VR", "MR", "RD"));

			Accessor legalLM = new Accessor(State.NEW);
			legalLM.setName("bnk-ls-lm");
			legalLM.setPermit(3);
			legalLM.setAlias(true);
			legalLM.setSvc(true);
			legalLM.setOrgLevels(Set.of("CO", "VR", "MR", "RD"));

			Set<Accessor> accessors = Set.of(scaner, legalPerf, legalLM, pam1, pam2, bAdmin, tAdmin);

			// Client ACL
			AccessControlList aclClient = new AccessControlList(State.NEW);
			aclClient.setName("test_client_acl");
			aclClient.setDescription("Client ACL");
			aclClient.setObjTypes(Set.of("bnk_client", "bnk_not_client"));
			aclClient.setAccessors(accessors);
			dao.create(aclClient);

			// Committee ACL
			AccessControlList aclCommittee = new AccessControlList(State.NEW);
			aclCommittee.setName("test_committee_acl");
			aclCommittee.setDescription("Credit committee ACL");
			aclCommittee.setObjTypes(Set.of("bnk_committee"));
			aclCommittee.setAccessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()));
			dao.create(aclCommittee);

			// GRL ACL
			AccessControlList aclGRC = new AccessControlList(State.NEW);
			aclGRC.setName("test_grc_acl");
			aclGRC.setDescription("Group of related companies ACL");
			aclGRC.setObjTypes(Set.of("bnk_grc_acl"));
			aclGRC.setAccessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()));
			dao.create(aclGRC);

			// PS Conclusion ACL
			AccessControlList aclConclPS = new AccessControlList(State.NEW);
			aclConclPS.setName("test_concl_ps_acl");
			aclConclPS.setDescription("Pledge service conclusion ACL");
			aclConclPS.setObjTypes(Set.of("bnk_conclusion"));
			aclConclPS.setStatuses(Set.of("PS_S_CO_APPROVE", "PS_S_CO_ASSIGN", "PS_S_CO_CONCL", "PS_S_CO_DONE",
					"PS_S_CO_REJECT", "PS_S_CO_REV", "PS_S_RD_APPROVE", "PS_S_RD_ASSIGN", "PS_S_RD_CONCL",
					"PS_S_RD_DONE", "PS_S_RD_REJECT", "PS_S_RD_REV"));
			aclConclPS.setAccessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()));
			dao.create(aclConclPS);
		};
	}
}
