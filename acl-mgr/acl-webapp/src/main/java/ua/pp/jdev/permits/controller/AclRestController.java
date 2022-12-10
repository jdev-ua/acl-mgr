package ua.pp.jdev.permits.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/acls", produces = { "application/json" })
public class AclRestController {
	private AclDAO aclDAO;

	@Autowired
	private void setAccessControlListDAO(AclDAO aclDAO) {
		this.aclDAO = aclDAO;
	}

	@ApiOperation(response = Acl.class, value = "Retrieves all ACLs", responseContainer = "List")
	@GetMapping
	public Iterable<Acl> getAll() {
		log.debug("Starting get list of all ACLs");
		
		return aclDAO.readAll();
	}

	@ApiOperation(response = Acl.class, value = "Retrieves an ACL with ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = Acl.class),
			@ApiResponse(code = 404, message = "ACL not found")
	})
	@GetMapping("/{id}")
	public ResponseEntity<Acl> getAcl(@PathVariable("id") String id) {
		log.debug("Start getting ACL with ID '{}'", id);

		Optional<Acl> optAcl = aclDAO.read(id);
		
		log.debug("Result of getting ACL with ID '{}': {}", id, optAcl.orElse(null));
		
		return (optAcl.isPresent())
				? new ResponseEntity<Acl>(optAcl.get(), HttpStatus.OK)
				: new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(consumes = {"application/json"})
	@ResponseStatus(HttpStatus.CREATED)
	public Acl postAcl(@RequestBody Acl acl) {
		log.debug("Start creating new ACL: {}", acl);
		
		Acl result = aclDAO.create(acl);
		
		log.debug("New ACL sucessfully created: {}", result);
		
		return result;
	}

	@PutMapping(consumes = {"application/json"})
	public Acl updateAcl(@RequestBody Acl acl) {
		log.debug("Start updating ACL: {} ", acl);
		
		Acl result = aclDAO.update(acl);
		
		log.debug("ACL sucessfully updated: {}", result);
		
		return result;
	}
	
	@ApiOperation(response = Void.class, value = "Deletes an ACL with specified ID")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAcl(@PathVariable String id) {
		log.debug("Start deleting ACL with ID '{}'", id);
		
		boolean result = aclDAO.delete(id);
		
		log.debug("Result of deleting ACL with ID '{}': {}", id, result);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
