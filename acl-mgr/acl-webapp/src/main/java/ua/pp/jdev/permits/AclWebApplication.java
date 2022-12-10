package ua.pp.jdev.permits;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;
import ua.pp.jdev.permits.enums.State;

@Slf4j
@SpringBootApplication
public class AclWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(AclWebApplication.class, args);
	}
	
	@Bean
	@DependsOn("dataSourceConfig")
	protected CommandLineRunner dataLoader(AclDAO dao) {
		log.debug("Start initialize CommandLineRunner");
		
		return args -> {
			long size = dao.readAll().size();
			log.debug("Provided DAO ('{}') contains: {} items", dao.getClass().getCanonicalName(), size);
			
			if(size > 0) {
				log.debug("Skip populating not empty DAO");
				return;
			}
			
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
			Acl aclClient = dao.create(
					Acl.builder()
					.name("test_client_acl")
					.description("Client ACL")
					.objTypes(Set.of("bnk_client", "bnk_not_client"))
					.accessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclClient);

			// Committee ACL
			Acl aclCommittee = dao.create(
					Acl.builder()
					.name("test_committee_acl")
					.description("Credit committee ACL")
					.objTypes(Set.of("bnk_committee"))
					.accessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclCommittee);

			// GRL ACL
			Acl aclGRC = dao.create(
					Acl.builder()
					.name("test_grc_acl")
					.description("Group of related companies ACL")
					.objTypes(Set.of("bnk_grc_acl"))
					.accessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclGRC);

			// PS Conclusion ACL
			Acl aclConclPS = dao.create(
					Acl.builder()
					.name("test_concl_ps_acl")
					.description("Pledge service conclusion ACL")
					.objTypes(Set.of("Pledge service conclusion ACL"))
					.statuses(Set.of("PS_S_CO_APPROVE", "PS_S_CO_ASSIGN", "PS_S_CO_CONCL", "PS_S_CO_DONE",
							"PS_S_CO_REJECT", "PS_S_CO_REV", "PS_S_RD_APPROVE", "PS_S_RD_ASSIGN", "PS_S_RD_CONCL",
							"PS_S_RD_DONE", "PS_S_RD_REJECT", "PS_S_RD_REV"))
					.accessors(accessors.stream().map(Accessor::softCopy).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclConclPS);
			log.debug("Finish initialize CommandLineRunner");
		};
	}
}
