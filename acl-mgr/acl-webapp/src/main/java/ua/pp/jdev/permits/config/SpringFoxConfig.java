package ua.pp.jdev.permits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {                                    
    @Bean
    protected Docket apiV1() { 
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/api/v1/acls/*.*"))
				.build()
				.groupName("v1")
				.apiInfo(apiInfo("v1.0", "Base Api for creating and managing ACLs"))
				.useDefaultResponseMessages(false);
    }
    
    @Bean
	protected Docket apiV1_1() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/api/v1.1/acls/*.*"))
				.build()
				.groupName("v1.1")
				.apiInfo(apiInfo("v1.1", "Api for creating and managing ACLs (with pagination)"))
				.useDefaultResponseMessages(false);
	}
    
    protected ApiInfo apiInfo(String vesion, String description) {
    	return new ApiInfoBuilder()
    			.title("ACL REST API")
    			.description(description)
    			.version(vesion)
    			.build();
	}
}