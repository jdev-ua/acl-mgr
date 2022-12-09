package ua.pp.jdev.permits.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AclDAO;

@Slf4j
@Configuration
public class DataSourceConfig {
	static final String DEFAULT_BEAN_NAME = "simpleAclDAO";
	
	@Autowired
    private ApplicationContext appContext;
	
	@Value("${app.datasource.bean:simpleAclDAO}")
	private String dataSourceBeanName;

	@Bean
	@Primary
	protected AclDAO aclDAO() {
		AclDAO result;
		
		String beanName = dataSourceBeanName;
		// Use default default DAO bean if application context doesn't contain specified one
		if(!appContext.containsBean(beanName)) {
			log.warn("Failed to get '{}' bean, default bean will be used instead", beanName);
			beanName = DEFAULT_BEAN_NAME;
		}
		result = appContext.getBean(beanName, AclDAO.class);
		log.info("Initialized primary ACL DAO '{}': [{}]", beanName, result.getClass().getCanonicalName());

		return result;
	}
}
