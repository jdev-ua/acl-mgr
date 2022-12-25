package ua.pp.jdev.permits.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;

@Slf4j
@RestController
@Api(tags = { "acls" })
@RequestMapping(path = "/api/v1/acls", produces = { "application/json" })
public class AclRestControllerV1 {
	private AclDAO aclDAO;

	@Autowired
	private void setAclDAO(AclDAO aclDAO) {
		this.aclDAO = aclDAO;
	}

	@ApiOperation(response = Acl.class, value = "Retrieves all ACLs", responseContainer = "List")
	@GetMapping
	public ResponseEntity<List<Acl>> getAll() {
		log.debug("Starting get list of all ACLs");

		return new ResponseEntity<List<Acl>>(List.copyOf(aclDAO.readAll()), HttpStatus.OK);
	}

	@ApiOperation(response = Acl.class, value = "Retrieves an ACL")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = Acl.class),
			@ApiResponse(code = 404, message = "ACL not found") })
	@GetMapping("/{aclId}")
	public ResponseEntity<Acl> getAcl(@PathVariable("aclId") String aclId,
			@RequestParam(defaultValue = "false") boolean byName) {
		log.debug("Start getting ACL with {} '{}'", byName ? "name" : "ID", aclId);

		Optional<Acl> optAcl = byName ? aclDAO.readByName(aclId) : aclDAO.read(aclId);

		log.debug("Result of getting ACL with {} '{}': {}", byName ? "name" : "ID", aclId, optAcl.orElse(null));

		return (optAcl.isPresent()) ? new ResponseEntity<Acl>(optAcl.get(), HttpStatus.OK)
				: new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@ApiOperation(response = String.class, value = "Creates new ACL")
	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<String> createAcl(@Valid @RequestBody Acl acl) {
		log.debug("Start creating new ACL: {}", acl);

		if (aclDAO.readByName(acl.getName()).isPresent()) {
			throw new RuntimeException("ACL with name '" + acl.getName() + "' already exists!");
		}

		acl = aclDAO.create(acl);

		log.debug("New ACL sucessfully created: {}", acl);

		HttpHeaders responseHeaders = new HttpHeaders();
		URI newAclURI = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{aclId}")
				.buildAndExpand(acl.getId())
				.toUri();
		responseHeaders.setLocation(newAclURI);

		return new ResponseEntity<>(acl.getId(), responseHeaders, HttpStatus.CREATED);
	}

	@ApiOperation(response = Void.class, value = "Updates an ACL")
	@PutMapping(value = "/{aclId}", consumes = { "application/json" })
	public ResponseEntity<?> updateAcl(@PathVariable String aclId, @Valid @RequestBody Acl acl) {
		log.debug("Start updating ACL: {} ", acl);

		if (aclDAO.read(acl.getId()).isEmpty()) {
			throw new RuntimeException("ACL with ID '" + acl.getId() + "' doesn't exist!");
		}

		Acl result = aclDAO.update(acl);

		log.debug("ACL sucessfully updated: {}", result);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(response = Void.class, value = "Deletes an ACL")
	@DeleteMapping("/{aclId}")
	public ResponseEntity<?> deleteAcl(@PathVariable String aclId) {
		log.debug("Start deleting ACL with ID '{}'", aclId);

		boolean result = aclDAO.delete(aclId);

		log.debug("Result of deleting ACL with ID '{}': {}", aclId, result);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
