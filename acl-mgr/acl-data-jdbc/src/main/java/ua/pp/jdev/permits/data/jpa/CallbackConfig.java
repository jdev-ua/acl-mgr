package ua.pp.jdev.permits.data.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
			// Save Accessors
			doomsday(
					// Collect all Accessors for further removal
					() -> getAccessorRepository().findAllByAclId(acl.getId()),
					// Accessors to save
					acl.getAccessors(),
					// Return false to save Accessor in any case
					skip -> false,
					// Provide new Accessor with Acl's ID than save
					accessor -> {
						accessor.setAclId(acl.getId());
						getAccessorRepository().save(accessor);
					},
					// Finally remove unnecessary Accessors
					junk -> getAccessorRepository().deleteAll(junk));

			// Save Obj.Types
			doomsday(
					// Collect all Obj.Types for further removal
					() -> getObjTypeRepository().findAllByAclId(acl.getId()),
					// Obj.Types to save
					acl.getObjTypes(),
					// Just pass value itself 
					Boolean::booleanValue,
					// Provide new Obj.Type with Acl's ID than save
					objType -> {
						objType.setAclId(acl.getId());
						getObjTypeRepository().save(objType);
					},
					// Finally remove unnecessary Obj.Types
					junk -> getObjTypeRepository().deleteAll(junk));
			
			// Save Statuses
			doomsday(
					// Collect all Statuses for further removal
					() -> getStatusRepository().findAllByAclId(acl.getId()),
					// Statuses to save
					acl.getStatuses(),
					// Just pass value itself 
					Boolean::booleanValue,
					// Provide new Status with Acl's ID than save
					status -> {
						status.setAclId(acl.getId());
						getStatusRepository().save(status);
					},
					// Finally remove unnecessary Statuses
					junk -> getStatusRepository().deleteAll(junk));
			
			return acl;
		};
	}

	@Bean
	protected AfterSaveCallback<TableAccessor> accessorAfterSaveCallback() {
		return (accessor) -> {
			// Save XPermits
			doomsday(
					// Collect all XPermits for further removal
					() -> getXPermitRepository().findAllByAccessorId(accessor.getId()),
					// XPermits to save
					accessor.getXPermits(),
					// Just pass value itself 
					Boolean::booleanValue,
					// Provide new XPermit with Accessor's ID than save
					xPermit -> {
						xPermit.setAccessorId(accessor.getId());
						getXPermitRepository().save(xPermit);
						},
					// Finally remove unnecessary XPermits
					junk -> getXPermitRepository().deleteAll(junk));
			
			// Save Org.Levels
			doomsday(
					// Collect all Org.Levels for further removal
					() -> getOrgLevelRepository().findAllByAccessorId(accessor.getId()),
					// Org.Levels to save
					accessor.getOrgLevels(),
					// Just pass value itself 
					Boolean::booleanValue,
					// Provide new Org.Level with Accessor's ID than save
					orgLevel -> {
						orgLevel.setAccessorId(accessor.getId());
						getOrgLevelRepository().save(orgLevel);
						},
					// Finally remove unnecessary Org.Levels
					junk -> getOrgLevelRepository().deleteAll(junk));
			
			return accessor;
		};
	}
	
	private <T> void doomsday(Supplier<Iterable<T>> doomsdayBook, Set<T> survivors, Predicate<Boolean> skipJudgment, Consumer<T> savior, Consumer<List<T>> annihilator) {
		List<T> doomed = new ArrayList<>();
		// Populate list of candidates for removal from supplier
		doomsdayBook.get().forEach(doomed::add);

		// Process required values 
		survivors.forEach(entity -> {
			// Remove current value from the list for removal with fixing result.
			// True means that value already exists in repository so there is no sense to save it again.
			boolean skipAdvice = doomed.remove(entity);
			// Make final decision whether save is required or not
			if (!skipJudgment.test(skipAdvice)) {
				// Pass value to the corresponding consumer for saving. 
				savior.accept(entity);
			}
		});

		// Pass remaining values for removal
		annihilator.accept(doomed);
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
