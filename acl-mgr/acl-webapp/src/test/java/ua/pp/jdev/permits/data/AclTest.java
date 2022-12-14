package ua.pp.jdev.permits.data;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ua.pp.jdev.permits.data.Accessor.AccessorBuilder;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.util.IDGenerator;

public class AclTest {
	private final static Set<String> statuses_2 = Set.of("LIM_S_NEW", "LIM_S_ACTIVE");
	private final static Set<String> statuses_3 = Set.of("LIM_S_NEW", "LIM_S_ACTIVE", "LIM_S_CLOSED");
	
	private final static Set<String> objTypes_2 = Set.of("bnk_client", "bnk_not_client");
	private final static Set<String> objTypes_3 = Set.of("bnk_client", "bnk_not_client", "bnk_limit");
	
	@Test
	public void testHasAccessor() {
		Accessor accessor = Accessor.builder().name("test").build();

		Acl acl = Acl.builder().build();
		acl.addAccessor(accessor);

		Assertions.assertTrue(acl.hasAccessor("test"));
	}

	@Test
	public void testGetAccessor() {
		Accessor accessor = Accessor.builder().name("test").build();

		Acl acl = Acl.builder().build();
		acl.addAccessor(accessor);

		Assertions.assertTrue(acl.getAccessor("test").isPresent());
		Assertions.assertEquals(acl.getAccessor("test").get(), accessor);
	}

	@Test
	public void testRemoveAccessor() {
		Accessor accessor = Accessor.builder().name("test").build();

		Acl acl = Acl.builder().build();
		acl.addAccessor(accessor);
		acl.removeAccessor("test");

		Assertions.assertTrue(acl.getAccessor("test").isEmpty());
	}

	@Test
	public void testRemoveRequiredAccessors() {
		Acl acl = Acl.builder().build();

		acl.removeAccessor("dm_owner");
		acl.removeAccessor("dm_world");

		Assertions.assertTrue(acl.getAccessor("dm_owner").isPresent());
		Assertions.assertTrue(acl.getAccessor("dm_world").isPresent());
	}

	@Test
	public void testSetAccessors() {
		Set<Accessor> accessors = createAccessors4Test(3);

		Acl acl = Acl.builder().build();
		acl.setAccessors(accessors);

		// Test where ACL contains all Accessors added by 'setAccessors' method
		for (Accessor accessor : accessors) {
			Assertions.assertTrue(acl.getAccessor(accessor.getName()).isPresent());
		}
	}

	private Acl createAcl4Test(State state, Set<Accessor> accessors) {
		return Acl.builder()
				.id(IDGenerator.genStringID())
				.name("name")
				.description("description")
				.objTypes(objTypes_2)
				.statuses(statuses_2)
				.accessors(accessors)
				.build();
	}

	private Set<Accessor> createAccessors4Test(int num) {
		AccessorBuilder builder = Accessor.builder();

		Set<Accessor> result = new HashSet<>();
		for (int i = 0; i <= num; i++) {
			result.add(builder.name("accessor" + i).build());
		}
		return result;
	}
}
