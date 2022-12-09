package ua.pp.jdev.permits.data.jpa;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.mapping.event.AfterSaveCallback;

import com.google.common.collect.Sets;

/**
 * Configures a set of {@code EntityCallback} methods for {@link TableACL}
 * and {@link TableAccessor} domain objects.<br><br>
 *  
 * Since Spring Data Framework doesn't supply cascade update
 * contained entities with ID of aggregation root it is required
 * to provide explicit binding while save them into or read from a database. 
 * 
 * @author jdev.pp.ua
 *
 */
@Configuration
class CallbackConfig implements ApplicationContextAware {
	private static ApplicationContext ctx = null;

	protected AccessorRepository getAccessorRepository() {
		return ctx.getBean(AccessorRepository.class);
	}

	protected ObjTypeRepository getObjTypeRepository() {
		return ctx.getBean(ObjTypeRepository.class);
	}
	
	protected StatusRepository getStatusRepository() {
		return ctx.getBean(StatusRepository.class);
	}
	
	protected XPermitRepository getXPermitRepository() {
		return ctx.getBean(XPermitRepository.class);
	}
	
	protected OrgLevelRepository getOrgLevelRepository() {
		return ctx.getBean(OrgLevelRepository.class);
	}
	
	@Bean
	protected AfterSaveCallback<TableACL> aclAfterSaveCallback() {
		return (acl) -> {
			// Setup contained Accessors with ID of current Acl than save them
			acl.getAccessors().forEach(accessor -> {
				accessor.setAclId(acl.getId());
				getAccessorRepository().save(accessor);
			});

			// Setup contained Statuses with ID of current Acl than save them
			acl.getObjTypes().forEach(objType -> {
				objType.setAclId(acl.getId());
				getObjTypeRepository().save(objType);
			});
			
			// Setup contained Statuses with ID of current Acl than save them
			acl.getStatuses().forEach(status -> {
				status.setAclId(acl.getId());
				getStatusRepository().save(status);
			});
			
			return acl;
		};
	}

	@Bean
	protected AfterSaveCallback<TableAccessor> accessorAfterSaveCallback() {
		return (accessor) -> {
			// Setup contained XPermits with ID of current Accessor than save them
			accessor.getXPermits().forEach(xPermit -> {
				xPermit.setAccessorId(accessor.getId());
				getXPermitRepository().save(xPermit);
			});
			
			// Setup contained Org.Levels with ID of current Accessor than save them
			accessor.getOrgLevels().forEach(orgLevel -> {
				orgLevel.setAccessorId(accessor.getId());
				getOrgLevelRepository().save(orgLevel);
			});
			
			return accessor;
		};
	}
	
	@Bean
	protected AfterConvertCallback<TableACL> aclAfterConvertCallback() {
		return acl -> {
			// Map corresponding Accessors
			Set<TableAccessor> accessors = Sets.newHashSet(getAccessorRepository().findAllByAclId(acl.getId()));
			acl.setAccessors(accessors);
			
			// Map corresponding Obj.Types
			Set<TableObjType> objTypes = Sets.newHashSet(getObjTypeRepository().findAllByAclId(acl.getId()));
			acl.setObjTypes(objTypes ); 
			
			// Map corresponding Statuses
			Set<TableStatus> statuses = Sets.newHashSet(getStatusRepository().findAllByAclId(acl.getId()));
			acl.setStatuses(statuses); 

			return acl;
		};
	}

	/**
	 * Provides an {@code AfterConvertCallback} for {@link TableAccessor}
	 * entity to map collections of corresponding XPermits and Org.Levels
	 * after an aggregate was converted from the database into an entity
	 * that maps corresponding XPermits and Org.Levels with aggregation root
	 * @return a callback method
	 */
	@Bean
	protected AfterConvertCallback<TableAccessor> accessorAfterConvertCallback() {
		return accessor -> {
			// Map corresponding XPermits
			Set<TableXPermit> xPermits = Sets.newHashSet(getXPermitRepository().findAllByAccessorId(accessor.getId()));
			accessor.setXPermits(xPermits); 
			
			// Map corresponding Org.Levels
			Set<TableOrgLevel> orgLevels = Sets.newHashSet(getOrgLevelRepository().findAllByAccessorId(accessor.getId()));
			accessor.setOrgLevels(orgLevels); 
			
			return accessor;
		};
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CallbackConfig.ctx = applicationContext;
	}
}
