package ua.pp.jdev.permits.data.jdbc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.relational.core.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.mapping.event.AfterSaveCallback;

/**
 * Provides stubs for AfterSaveCallback and AfterConvertCallback callbacks.
 * <br>NOTE: disabled by default, remove comment from @Configuration annotation to enable.
 * 
 * @author jdev.pp.ua
 *
 */
//@Configuration
class TableACLCallbackConfig implements ApplicationContextAware {
	private static ApplicationContext ctx = null;

	protected JdbcAccessorRepository getRepository() {
		return ctx.getBean(JdbcAccessorRepository.class);
	}

	@Bean
	protected AfterSaveCallback<TableACL> afterSaveCallback() {
		return (acl) -> {
			// Commented code provides explicit save contained accessors into database by
			// corresponding AccessorRepository
			// acl.setState(State.PURE);
			// acl.getAccessors().forEach(t -> {
			// getRepository().save(t);
			// });

			// TODO Do something you need

			return acl;
		};
	}

	@Bean
	protected AfterConvertCallback<TableACL> afterConvertCallback() {
		return acl -> {
			// Commented code provides initialization of current ACL object by explicit read
			// accessors data from database explicit corresponding AccessorRepository
			// acl.setAccessors(Sets.newHashSet(getRepository().findAllByAclId(acl.getId())));

			// TODO Do something you need

			return acl;
		};
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		TableACLCallbackConfig.ctx = applicationContext;
	}
}
