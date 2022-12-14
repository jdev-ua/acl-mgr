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
import ua.pp.jdev.permits.enums.Permit;

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
			
			Accessor scaner = Accessor.builder()
					.name("bnk-scaner")
					.permit(Permit.READ.getValue())
					.alias(true)
					.svc(false)
					.build();

			Accessor bAdmin = Accessor.builder()
					.name("bnk_business_admin")
					.permit(Permit.WRITE.getValue())
					.alias(false)
					.svc(false)
					.xPermits(Set.of("EXECUTE_PROC", "CHANGE_LOCATION"))
					.build();

			Accessor tAdmin = Accessor.builder()
					.name("bnk_tech_admin")
					.permit(Permit.DELETE.getValue())
					.alias(false).svc(false)
					.xPermits(Set.of("EXECUTE_PROC", "CHANGE_LOCATION"))
					.build();

			Accessor pam1 = Accessor.builder()
					.name("bnk_grc_pam1")
					.permit(Permit.READ.getValue())
					.alias(true)
					.svc(false)
					.xPermits(Set.of("EXECUTE_PROC"))
					.build();

			Accessor pam2 = Accessor.builder()
					.name("bnk_grc_pam2")
					.permit(Permit.READ.getValue())
					.alias(true)
					.svc(false)
					.xPermits(Set.of("EXECUTE_PROC"))
					.build();

			Accessor legalPerf = Accessor.builder()
					.name("bnk-ls-perf")
					.permit(Permit.READ.getValue())
					.alias(true)
					.svc(true)
					.orgLevels(Set.of("CO", "VR", "MR", "RD"))
					.build();

			Accessor legalLM = Accessor.builder()
					.name("bnk-ls-lm")
					.permit(Permit.READ.getValue())
					.alias(true)
					.svc(true)
					.orgLevels(Set.of("CO", "VR", "MR", "RD"))
					.build();

			Set<Accessor> accessors = Set.of(scaner, legalPerf, legalLM, pam1, pam2, bAdmin, tAdmin);

			// Client ACL
			Acl aclClient = dao.create(
					Acl.builder()
					.name("test_client_acl")
					.description("Client ACL")
					.objTypes(Set.of("bnk_client", "bnk_not_client"))
					.accessors(accessors.stream().map(t -> t.toBuilder().build()).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclClient);

			// Committee ACL
			Acl aclCommittee = dao.create(
					Acl.builder()
					.name("test_committee_acl")
					.description("Credit committee ACL")
					.objTypes(Set.of("bnk_committee"))
					.accessors(accessors.stream().map(t -> t.toBuilder().build()).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclCommittee);

			// GRL ACL
			Acl aclGRC = dao.create(
					Acl.builder()
					.name("test_grc_acl")
					.description("Group of related companies ACL")
					.objTypes(Set.of("bnk_grc_acl"))
					.accessors(accessors.stream().map(t -> t.toBuilder().build()).collect(Collectors.toSet()))
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
					.accessors(accessors.stream().map(t -> t.toBuilder().build()).collect(Collectors.toSet()))
					.build());
			log.debug("New test ACL successfully created: {}", aclConclPS);
			log.debug("Finish initialize CommandLineRunner");
		};
	}
}
