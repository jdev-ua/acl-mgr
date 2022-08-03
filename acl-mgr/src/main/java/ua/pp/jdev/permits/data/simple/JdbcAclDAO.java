package ua.pp.jdev.permits.data.simple;

import java.sql.Types;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.enums.State;

@Slf4j
@Component
@Profile("jdbc")
public class JdbcAclDAO implements AccessControlListDAO {
	private JdbcOperations jdbcOperations;

	public JdbcAclDAO(JdbcOperations jdbcOperations) {
		log.info("Initializing ACL datasource persisting data in embedded H2 database by JdbcTemplate");

		this.jdbcOperations = jdbcOperations;
	}

	@Override
	public Collection<AccessControlList> readAll() {
		String sql = "select a.id, a.name, a.description, s.status, t.obj_type from acl a "
				+ " left outer join status s on s.acl_id=a.id left outer join obj_type t on t.acl_id=a.id";

		// Remove duplicates, read accessors and then sort result by ACL name
		return jdbcOperations.query(sql, new JdbcAclRowMapper()).stream().distinct()
				.peek(acl -> acl.setAccessors(readAllAccessors(acl.getId())))
				.sorted(Comparator.comparing(AccessControlList::getName)).collect(Collectors.toList());
	}

	@Override
	public Optional<AccessControlList> read(String id) {
		String sql = "SELECT a.id, a.name, a.description, s.status, t.obj_type "
				+ " from acl a left outer join status s on s.acl_id=a.id "
				+ " left outer join obj_type t on t.acl_id=a.id where a.id=?";

		// Remove duplicates, read accessors and then get first ACL as a result
		return jdbcOperations.query(sql, new JdbcAclRowMapper(), id).stream().distinct()
				.peek(acl -> acl.setAccessors(readAllAccessors(acl.getId()))).findFirst();
	}

	protected Set<Accessor> readAllAccessors(String id) {
		String sql = "select a.id, a.name, a.permit, a.alias, a.svc, o.org_level, x.xpermit "
				+ " from accessor a left outer join org_level o on o.accessor_id=a.id "
				+ " left outer join xpermit x on x.accessor_id=a.id where a.acl_id=?";

		return jdbcOperations.query(sql, new JdbcAccessorRowMapper(), id).stream().distinct().collect(Collectors.toSet());
	}

	@Override
	public void create(AccessControlList acl) {
		String query = "insert into acl (name, description) values (?,?)";

		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(query, Types.VARCHAR, Types.VARCHAR);
		pscf.setReturnGeneratedKeys(true);
		PreparedStatementCreator psc = pscf.newPreparedStatementCreator(List.of(acl.getName(), acl.getDescription()));
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcOperations.update(psc, keyHolder);
		acl.setId(String.valueOf(keyHolder.getKey().longValue()));

		// Save acl's statuses
		saveMultiValueField(acl.getStatuses(), acl.getId(), "status", "acl_id", "status");
		// Save acl's obj.types
		saveMultiValueField(acl.getObjTypes(), acl.getId(), "obj_type", "acl_id", "obj_type");
		// Save acl's accessors
		saveAccessors(acl);
	}

	protected void saveMultiValueField(Collection<String> values, String idParent, String table, String foreignKey,
			String column) {
		String sqlClear = String.format("delete %s where %s=?", table, foreignKey);
		jdbcOperations.update(sqlClear, idParent);

		String sqlCreate = String.format("insert into %s (%s, %s) values (?,?)", table, foreignKey, column);
		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlCreate, Types.BIGINT,
				Types.VARCHAR);

		values.forEach(value -> {
			PreparedStatementCreator psc = pscf.newPreparedStatementCreator(List.of(idParent, value));
			jdbcOperations.update(psc);
		});
	}

	protected void saveAccessors(AccessControlList acl) {
		acl.getAccessors().forEach(accessor -> {
			// Skip current accessor if it is not changed
			if (State.PURE.equals(accessor.getState()))
				return;

			// Delete current accessor than get to the next
			if (State.VOID.equals(accessor.getState())) {
				String deleteQuery = "delete accessor where id=?";
				jdbcOperations.update(deleteQuery, accessor.getId());
				return;
			}

			// Use INSERT query for new accessor or UPDATE otherwise
			if (State.NEW.equals(accessor.getState())) {
				String insertQuery = "insert into accessor (acl_id, name, permit, alias, svc) values (?,?,?,?,?)";
				PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(insertQuery, Types.BIGINT,
						Types.VARCHAR, Types.INTEGER, Types.BOOLEAN, Types.BOOLEAN);

				GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
				pscf.setReturnGeneratedKeys(true);
				PreparedStatementCreator psc = pscf.newPreparedStatementCreator(List.of(acl.getId(), accessor.getName(),
						accessor.getPermit(), accessor.isAlias(), accessor.isSvc()));

				jdbcOperations.update(psc, keyHolder);
				accessor.setId(String.valueOf(keyHolder.getKey().longValue()));
			} else {
				String updateQuery = "update accessor set name=?, permit=?, alias=?, svc=? where id=?";
				jdbcOperations.update(updateQuery, accessor.getName(), accessor.getPermit(), accessor.isAlias(),
						accessor.isSvc(), accessor.getId());
			}

			// Save accessor's org.levels
			saveMultiValueField(accessor.getOrgLevels(), accessor.getId(), "org_level", "accessor_id", "org_level");
			// Save accessor's xpermits
			saveMultiValueField(accessor.getXPermits(), accessor.getId(), "xpermit", "accessor_id", "xpermit");
		});
	}

	protected boolean existsById(String id) {
		String query = "select count(*) from acl where id=?";
		int count = jdbcOperations.queryForObject(query, Integer.class, id);
		return count > 0;
	}

	@Override
	public void update(AccessControlList acl) {
		if (!existsById(acl.getId())) {
			// TODO Localize error message!
			throw new RuntimeException("ACL with ID=" + acl.getId() + " does not exist in docbase!");
		}

		String query = "update acl set name=?, description=? where id=?";
		jdbcOperations.update(query, acl.getName(), acl.getDescription(), acl.getId());

		// Save acl's statuses
		saveMultiValueField(acl.getStatuses(), acl.getId(), "status", "acl_id", "status");
		// Save acl's obj.types
		saveMultiValueField(acl.getObjTypes(), acl.getId(), "obj_type", "acl_id", "obj_type");
		// Save acl's accessors
		saveAccessors(acl);
	}

	@Override
	public boolean delete(String id) {
		String query = "delete acl where id=?";
		return jdbcOperations.update(query, id) > 0;
	}
}
