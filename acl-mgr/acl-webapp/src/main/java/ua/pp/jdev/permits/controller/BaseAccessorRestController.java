package ua.pp.jdev.permits.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.data.Acl;
import ua.pp.jdev.permits.data.AclDAO;
import ua.pp.jdev.permits.exception.ResourceNotFoundException;

abstract class BaseAccessorRestController {
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
	
	protected List<Accessor> doGetAll(String aclId) {
		return List.copyOf(findAclOrThrowException(aclId).getAccessors());
	}
	
	protected Optional<Accessor> doGet(String aclId, String accessorId) {
		Objects.requireNonNull(aclId);
		Objects.requireNonNull(accessorId);

		Optional<Accessor> optAccessor = findAccessor(findAclOrThrowException(aclId), accessorId);

		return optAccessor;
	}
	
	protected Accessor doAdd(String aclId, Accessor data) {
		Objects.requireNonNull(aclId);
		Objects.requireNonNull(data);
		
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
		
		return optAccessor.get();
	}
	
	protected Accessor doUpdate(String aclId, String accessorId, Accessor data) {
		Objects.requireNonNull(aclId);
		Objects.requireNonNull(accessorId);
		Objects.requireNonNull(data);
		
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

		return optAccessor.get();
	}
	
	protected boolean doDelete(String aclId, String accessorId) {
		boolean result = false;
		
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
			
			result = true;
		}

		return result;
	}
}
