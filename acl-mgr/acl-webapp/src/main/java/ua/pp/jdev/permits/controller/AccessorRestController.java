package ua.pp.jdev.permits.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;
import ua.pp.jdev.permits.exception.ResourceNotFoundException;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/acls/{aclId}/accessors", produces = { "application/json" })
public class AccessorRestController {
	private AclDAO aclDAO;

	@Autowired
	private void setAclDAO(AclDAO aclDAO) {
		this.aclDAO = aclDAO;
	}
	
	private Acl findAclOrThrowException(String aclId) {
		Objects.requireNonNull(aclId);
		
		Optional<Acl> optAcl = aclDAO.read(aclId);
		if (optAcl.isEmpty()) {
			throw new ResourceNotFoundException("ACL with ID '" + aclId + "' doesn't exist!");
		}
		
		return optAcl.get();
	}

	private Optional<Accessor> findAccessor(Acl acl, String accessorId) {
		Objects.requireNonNull(acl);
		Objects.requireNonNull(accessorId);
		
		Optional<Accessor> optAccessor = acl.getAccessors().stream()
				.filter(t -> accessorId.equalsIgnoreCase(t.getId()))
				.findAny();
		
		return optAccessor;
	}
	
	@ApiOperation(response = Accessor.class, value = "Retrieves all Accessors of the ACL", responseContainer = "List")
	@GetMapping()
	public ResponseEntity<List<Accessor>> getAllAccessors(@PathVariable String aclId) {
		log.debug("Start getting a list of all Accessors for ACL (id='{}')", aclId);

		return new ResponseEntity<List<Accessor>>(List.copyOf(findAclOrThrowException(aclId).getAccessors()), HttpStatus.OK);
	}

	@ApiOperation(response = Accessor.class, value = "Retrieves an Accessor of the ACL")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = Accessor.class),
			@ApiResponse(code = 404, message = "ACL or Accessor not found") })
	@GetMapping("/{accessorId}")
	public ResponseEntity<Accessor> getAccessor(@PathVariable("aclId") String aclId,
			@PathVariable("accessorId") String accessorId) {
		log.debug("Start getting an Accessor (id='{}') for ACL (id='{}')", accessorId, aclId);

		Optional<Accessor> optAccessor = findAccessor(findAclOrThrowException(aclId), accessorId);
		
		log.debug("Result of getting an Accessor: {}", optAccessor.orElse(null));

		return (optAccessor.isPresent()) ?
				new ResponseEntity<Accessor>(optAccessor.get(), HttpStatus.OK) : 
					new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@ApiOperation(response = String.class, value = "Adds new Accessor to the ACL")
	@ApiResponse(code = 201, message = "Accessor created successfully")
	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<String> addAccessor(@PathVariable String aclId, @Valid @RequestBody Accessor data) {
		log.debug("Start creating new Accessor for ACL (id='{}'): ", aclId, data);

		// Get ACL that may contain specified Accessor
		Acl acl = findAclOrThrowException(aclId);
		
		// Add Accessor to ACL
		acl.addAccessor(data);
		// Apply changes and actualize ACL
		acl = aclDAO.update(acl);
		// Get newly created Accessor
		Optional<Accessor> optAccessor = acl.getAccessor(data.getName());
		// Throw corresponding exception if ACL doesn't contain this new Accessor
		if (optAccessor.isEmpty()) {
			throw new RuntimeException("Failed to add new Accessor to the ACL (id='" + aclId + "')");
		}

		log.debug("New Accessor created sucessfully: {}", optAccessor.get());

		// Create Accessor's URI and place it to response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newAccessorURI = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{accessorId}")
				.buildAndExpand(optAccessor.get().getId())
				.toUri();
		responseHeaders.setLocation(newAccessorURI);

		return new ResponseEntity<>(optAccessor.get().getId(), responseHeaders, HttpStatus.CREATED);
	}

	@ApiOperation(response = Void.class, value = "Updates an Accessor of the ACL")
	@PutMapping(value = "/{accessorId}", consumes = { "application/json" })
	public ResponseEntity<?> updateAccessor(@PathVariable String aclId, @PathVariable String accessorId,
			@Valid @RequestBody Accessor data) {
		log.debug("Start updating an Accessor (id='{}') for ACL (id='{}'): ", accessorId, aclId, data);
		
		// Get ACL that may contain specified Accessor
		Acl acl = findAclOrThrowException(aclId);
		// Get specified Accessor from the ACL
		Optional<Accessor> optAccessor = findAccessor(acl, accessorId);
		// Throw corresponding exception if ACL doesn't contain specified Accessor
		if (optAccessor.isEmpty() && acl.hasAccessor(data.getName())) {
			throw new ResourceNotFoundException(
					"ACL with ID '" + aclId + "' doesn't contain Accessor with ID '" + accessorId + "'!");
		}
		
		// Add Accessor to ACL
		acl.addAccessor(data);
		// Apply changes and actualize ACL
		acl = aclDAO.update(acl);
		// Get newly created Accessor
		optAccessor = acl.getAccessor(data.getName());
		// Throw corresponding exception if ACL doesn't contain Accessor
		if (optAccessor.isEmpty()) {
			throw new RuntimeException("Failed to update specified Accessor (id='"
					+ accessorId + "') from the ACL (id='" + aclId + "')");
		}

		log.debug("Accessor updated sucessfully: {}", optAccessor.get());		

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(response = Void.class, value = "Deletes an Accessor from the ACL")
	@DeleteMapping("/{accessorId}")
	public ResponseEntity<?> deleteAccessor(@PathVariable String aclId, @PathVariable String accessorId) {
		log.debug("Start deleting an Accessor from the ACL (id='{}'): ", aclId, accessorId);

		// Try to get an ACL that may contain specified Accessor
		Acl acl = findAclOrThrowException(aclId);
		// Get specified Accessor from the ACL
		Optional<Accessor> optAccessor = findAccessor(acl, accessorId);
		// Try to remove specified Accessor if the ACL contains it or do nothing otherwise 
		if (optAccessor.isPresent()) {
			acl.removeAccessor(optAccessor.get().getName());

			// Apply changes and actualize ACL
			acl = aclDAO.update(acl);
			
			// Try to get Accessor from the ACL
			optAccessor = acl.getAccessor(optAccessor.get().getName());
			// Throw corresponding exception if the ACL still contains specified Accessor
			if (optAccessor.isPresent()) {
				throw new RuntimeException("Failed to delete specified Accessor (id='"
				+ accessorId + "') from the ACL (id='" + aclId + "')");
			}

			log.debug("Accessor (id='{}') deleted sucessfully from the ACL (id='{}')", accessorId, aclId);
		} else {
			log.debug("ACL (id='{}') doesn't contains specified Accessor (id='{}')", aclId, accessorId);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
