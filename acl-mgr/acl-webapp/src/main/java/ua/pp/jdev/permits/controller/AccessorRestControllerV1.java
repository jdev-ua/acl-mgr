package ua.pp.jdev.permits.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.Accessor;

@Slf4j
@RestController
@Api(tags = {"accessors"})
@RequestMapping(path = "/api/v1/acls/{aclId}/accessors", produces = { "application/json" })
public class AccessorRestControllerV1 extends BaseAccessorRestController {
	@ApiOperation(response = Accessor.class, value = "Retrieves all Accessors of the ACL", responseContainer = "List")
	@GetMapping()
	public ResponseEntity<List<Accessor>> getAllAccessors(@PathVariable String aclId) {
		log.debug("Start getting a list of all Accessors for ACL (id='{}')", aclId);

		return new ResponseEntity<>(doGetAll(aclId), HttpStatus.OK);
	}

	@ApiOperation(response = Accessor.class, value = "Retrieves an Accessor of the ACL")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = Accessor.class),
			@ApiResponse(code = 404, message = "ACL or Accessor not found") })
	@GetMapping("/{accessorId}")
	public ResponseEntity<Accessor> getAccessor(@PathVariable("aclId") String aclId,
			@PathVariable("accessorId") String accessorId) {
		log.debug("Start getting an Accessor (id='{}') for ACL (id='{}')", accessorId, aclId);

		Optional<Accessor> optAccessor = doGet(aclId, accessorId);
		
		log.debug("Result of getting an Accessor: {}", optAccessor.orElse(null));

		return (optAccessor.isPresent()) ?
				new ResponseEntity<>(optAccessor.get(), HttpStatus.OK) : 
					new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@ApiOperation(response = String.class, value = "Adds new Accessor to the ACL")
	@ApiResponse(code = 201, message = "Accessor created successfully")
	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<String> addAccessor(@PathVariable String aclId, @Valid @RequestBody Accessor data) {
		log.debug("Start creating new Accessor for ACL (id='{}'): ", aclId, data);

		Accessor accessor = doAdd(aclId, data);

		log.debug("New Accessor created sucessfully: {}", accessor);

		// Create Accessor's URI and place it to response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newAccessorURI = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{accessorId}")
				.buildAndExpand(accessor.getId())
				.toUri();
		responseHeaders.setLocation(newAccessorURI);

		return new ResponseEntity<>(accessor.getId(), responseHeaders, HttpStatus.CREATED);
	}

	@ApiOperation(response = Void.class, value = "Updates an Accessor of the ACL")
	@PutMapping(value = "/{accessorId}", consumes = { "application/json" })
	public ResponseEntity<?> updateAccessor(@PathVariable String aclId, @PathVariable String accessorId,
			@Valid @RequestBody Accessor data) {
		log.debug("Start updating an Accessor (id='{}') for ACL (id='{}'): ", accessorId, aclId, data);
		
		Accessor accessor = doUpdate(aclId, accessorId, data);
		
		log.debug("Accessor updated sucessfully: {}", accessor);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(response = Void.class, value = "Deletes an Accessor from the ACL")
	@DeleteMapping("/{accessorId}")
	public ResponseEntity<?> deleteAccessor(@PathVariable String aclId, @PathVariable String accessorId) {
		log.debug("Start deleting an Accessor from the ACL (id='{}'): ", aclId, accessorId);

		if (doDelete(aclId, accessorId)) {
			log.debug("Accessor (id='{}') deleted sucessfully from the ACL (id='{}')", accessorId, aclId);
		} else {
			log.debug("ACL (id='{}') doesn't contains specified Accessor (id='{}')", aclId, accessorId);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
