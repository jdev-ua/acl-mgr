package ua.pp.jdev.permits.data.web;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;
import ua.pp.jdev.permits.data.Page;

@Lazy
@Component
public class RestAclDAO implements AclDAO {
	@Value("${base.uri:http://localhost:${server.port}/api/${api.version:v1.1}/acls}")
	private String baseUri;
	@Value("${api.version:v1.1}")
	private String apiVersion;

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public Collection<Acl> readAll() {
		if(apiVersion.equalsIgnoreCase("v1")) {
			System.out.println("[readAll]: Use v1 version");
			ResponseEntity<List<Acl>> result = restTemplate.exchange(baseUri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Acl>>() {
					});

			return result.getBody();
		}
		
		System.out.println("[readAll]: Use v1.1 version");
		
		ResponseEntity<PageWrapper<Acl>> result = restTemplate.exchange(baseUri, HttpMethod.GET, null,
				new ParameterizedTypeReference<PageWrapper<Acl>>() {
				});

		return result.getBody().page().getContent();
		/*ResponseEntity<List<Acl>> result = restTemplate.exchange(baseUri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Acl>>() {
				});

		return result.getBody();*/
	}

	public Optional<Acl> read(URI url) {
		Optional<Acl> result;
		try {
			result = Optional.of(restTemplate.getForObject(url, Acl.class));
		} catch (HttpClientErrorException hcee) {
			if (HttpStatus.NOT_FOUND.equals(hcee.getStatusCode())) {
				result = Optional.empty();
			} else {
				// TODO Use corresponding exception
				throw new RuntimeException("Hueston, we have a problem here: ", hcee);
			}
		}

		return result;
	}

	@Override
	public Optional<Acl> read(String id) {
		URI url = UriComponentsBuilder
				.fromHttpUrl(baseUri)
				.path("/{id}")
				.build(id);

		return read(url);
	}

	@Override
	public Acl create(Acl origin) {
		// Create new ACL
		URI newAclUrl = restTemplate.postForLocation(baseUri, origin);
		// Return newly created ACL
		return read(newAclUrl)
				// TODO Use corresponding exception
				.orElseThrow(() -> new RuntimeException("Hueston, we have a problem there.."));
	}

	@Override
	public Acl update(Acl origin) {
		URI url = UriComponentsBuilder
				.fromHttpUrl(baseUri)
				.path("/{id}")
				.build(origin.getId());

		// Update ACL
		restTemplate.put(url, origin);

		// Return actualizedACL
		return read(url)
				// TODO Use corresponding exception
				.orElseThrow(() -> new RuntimeException("Hueston, we have a problem there.."));
	}

	@Override
	public boolean delete(String id) {
		URI url = UriComponentsBuilder
				.fromHttpUrl(baseUri)
				.path("/{id}")
				.build(id);

		// Delete ACL
		restTemplate.delete(url);

		return true;
	}

	@Override
	public Optional<Acl> readByName(String name) {
		URI url = UriComponentsBuilder
				.fromHttpUrl(baseUri)
				.path("/{name}")
				.queryParam("byName", true)
				.build(name);

		return read(url);
	}

	@Override
	public Page<Acl> readPage(int pageNo, int pageSize) {
		if(apiVersion.equalsIgnoreCase("v1")) {
			System.out.println("[readPage]: Use v1 version");
			return AclDAO.super.readPage(pageNo, pageSize);
		}
		
		System.out.println("[readPage]: Use v1.1 version");
		
		URI url = UriComponentsBuilder
				.fromHttpUrl(baseUri)
				.queryParam("page", pageNo)
				.queryParam("size", pageSize)
				.build(new Object[] {}) ;
		
		ResponseEntity<PageWrapper<Acl>> result = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<PageWrapper<Acl>>() {
				});

		return result.getBody().page();
	}
	
	private static class PageWrapper<T> {
		Page<T> page;

		@JsonCreator()
		public PageWrapper(@JsonProperty("content") Collection<T> content, @JsonProperty("number") int pageNo,
				@JsonProperty("size") int pageSize, @JsonProperty("itemCount") int itemCount,
				@JsonProperty("pageCount") int pageCount) {
			page = Page.of(content, pageNo, pageSize, itemCount, pageCount);
		}

		Page<T> page() {
			return page;
		}
	}
}
