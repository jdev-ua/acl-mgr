package ua.pp.jdev.permits.data;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

public class AccessControlListTest {
	private final static Set<String> statuses_2 = Set.of("LIM_S_NEW", "LIM_S_ACTIVE");
	private final static Set<String> statuses_3 = Set.of("LIM_S_NEW", "LIM_S_ACTIVE", "LIM_S_CLOSED");
	
	private final static Set<String> objTypes_2 = Set.of("bnk_client", "bnk_not_client");
	private final static Set<String> objTypes_3 = Set.of("bnk_client", "bnk_not_client", "bnk_limit");
	
	@Test
	public void testHasAccessor() {
		Accessor accessor = new Accessor();
		accessor.setName("test");

		AccessControlList acl = new AccessControlList();
		acl.addAccessor(accessor);

		Assertions.assertTrue(acl.hasAccessor("test"));
	}

	@Test
	public void testGetAccessor() {
		Accessor accessor = new Accessor();
		accessor.setName("test");

		AccessControlList acl = new AccessControlList();
		acl.addAccessor(accessor);

		Assertions.assertTrue(acl.getAccessor("test").isPresent());
		Assertions.assertEquals(acl.getAccessor("test").get(), accessor);
	}

	@Test
	public void testRemoveAccessor() {
		Accessor accessor = new Accessor();
		accessor.setName("test");

		AccessControlList acl = new AccessControlList();
		acl.addAccessor(accessor);
		acl.removeAccessor("test");

		Assertions.assertTrue(acl.getAccessor("test").isEmpty());
	}

	@Test
	public void testRemoveRequiredAccessors() {
		AccessControlList acl = new AccessControlList();

		acl.removeAccessor("dm_owner");
		acl.removeAccessor("dm_world");

		Assertions.assertTrue(acl.getAccessor("dm_owner").isPresent());
		Assertions.assertTrue(acl.getAccessor("dm_world").isPresent());
	}

	@Test
	public void testSetAccessors() {
		Set<Accessor> accessors = new HashSet<>();
		for (int i = 0; i <= 3; i++) {
			Accessor accessor = new Accessor();
			accessor.setName("accessor" + i);
		}

		AccessControlList acl = new AccessControlList();
		acl.setAccessors(accessors);

		// Test where ACL contains all Accessors added by 'setAccessors' method
		for (Accessor accessor : accessors) {
			Assertions.assertTrue(acl.getAccessor(accessor.getName()).isPresent());
		}
	}

	@Test
	public void testSoftCopy() {
		Set<Accessor> accessors = createAccessors4Test(3);

		for (State state : State.values()) {
			AccessControlList origin = createAcl4Test(state, accessors);
			AccessControlList copy = AccessControlList.softCopy(origin);

			// Test 
			Assertions.assertNotEquals(origin, copy);

			// Test whether
			if (State.NEW.equals(state)) {
				Assertions.assertEquals(origin.getState(), copy.getState());
			} else {
				Assertions.assertNotEquals(origin.getState(), copy.getState());
			}
			Assertions.assertNotEquals(origin.getId(), copy.getId());

			Assertions.assertEquals(origin.getName(), copy.getName());
			Assertions.assertEquals(origin.getDescription(), copy.getDescription());

			Assertions.assertEquals(origin.getStatuses(), copy.getStatuses());
			copy.setStatuses(statuses_3);
			Assertions.assertNotEquals(origin.getStatuses(), copy.getStatuses());

			Assertions.assertEquals(origin.getObjTypes(), copy.getObjTypes());
			copy.setObjTypes(objTypes_3);
			Assertions.assertNotEquals(origin.getObjTypes(), copy.getObjTypes());

			Assertions.assertEquals(origin.getAccessors(), copy.getAccessors());

			Accessor accessor = new Accessor();
			accessor.setName("accessor" + accessors.size());
			accessors.add(accessor);
			copy.setAccessors(accessors);

			Assertions.assertNotEquals(origin.getAccessors(), copy.getAccessors());
		}
	}

	@Test
	public void testDeepCopy() {
		Set<Accessor> accessors = createAccessors4Test(3);

		for (State state : State.values()) {
			AccessControlList origin = createAcl4Test(state, accessors);
			AccessControlList clone = AccessControlList.deepCopy(origin);

			// Test whether origin and clone are identical
			Assertions.assertEquals(origin, clone);

			// Next change repeating clone's fields and test
			// whether it will impacts on origin or not to reveal coupling between them

			clone.setStatuses(statuses_3);
			Assertions.assertNotEquals(origin.getStatuses(), clone.getStatuses());

			clone.setObjTypes(objTypes_3);
			Assertions.assertNotEquals(origin.getObjTypes(), clone.getObjTypes());

			Accessor accessor = new Accessor();
			accessor.setName("accessor" + accessors.size());
			accessors.add(accessor);
			clone.setAccessors(accessors);

			Assertions.assertNotEquals(origin.getAccessors(), clone.getAccessors());
		}
	}

	private AccessControlList createAcl4Test(State state, Set<Accessor> accessors) {
		AccessControlList result = new AccessControlList(state);
		result.setId(IDGenerator.genStringID());
		result.setName("name");
		result.setDescription("description");
		result.setObjTypes(objTypes_2);
		result.setStatuses(statuses_2);
		result.setAccessors(accessors);
		return result;
	}

	private Set<Accessor> createAccessors4Test(int num) {
		Set<Accessor> result = new HashSet<>();
		for (int i = 0; i <= num; i++) {
			Accessor accessor = new Accessor();
			accessor.setName("accessor" + i);
			result.add(accessor);
		}
		return result;
	}
}
