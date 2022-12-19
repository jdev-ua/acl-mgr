package ua.pp.jdev.permits.data.jpa;

import java.util.ArrayList;
import java.util.List;
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
			List<TableXPermit> xPermitsToDelete = new ArrayList<>();
			// Read from repository all XPermits associated with current Accessor
			// and place them into list of candidates to be deleted
			getXPermitRepository().findAllByAccessorId(accessor.getId()).forEach(xPermitsToDelete::add);
			
			// Save XPermits that is not currently in repository
			accessor.getXPermits().forEach(xPermit -> {
				// Remove XPermit that still in use from deletion list and skip saving it again into repository
				boolean skip = xPermitsToDelete.removeIf(t -> t.getXPermit().equalsIgnoreCase(xPermit.getXPermit()));
				if(!skip) {
					// Setup new XPermits with ID of current Accessor than save them
					xPermit.setAccessorId(accessor.getId());
					getXPermitRepository().save(xPermit);
				}
			});
			// Remove all unused XPermits from repository
			getXPermitRepository().deleteAll(xPermitsToDelete);
			
			List<TableOrgLevel> orgLevelsToDelete = new ArrayList<>();
			// Read from repository all Org.Levels associated with current Accessor
			// and place them into list of candidates to be deleted
			getOrgLevelRepository().findAllByAccessorId(accessor.getId()).forEach(orgLevelsToDelete::add);
			
			// Save Org.Levels that is not currently in repository
			accessor.getOrgLevels().forEach(orgLevel -> {
				// Remove Org.Level that still in use from deletion list and skip saving it again into repository
				boolean skip = orgLevelsToDelete.removeIf(t -> t.getOrgLevel().equalsIgnoreCase(orgLevel.getOrgLevel()));
				if (!skip) {
					// Setup new Org.Level with ID of current Accessor than save them
					orgLevel.setAccessorId(accessor.getId());
					getOrgLevelRepository().save(orgLevel);
				}
			});
			// Remove all unused Org.Levels from repository
			getOrgLevelRepository().deleteAll(orgLevelsToDelete);			
			
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
