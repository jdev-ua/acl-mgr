package ua.pp.jdev.permits.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

import ua.pp.jdev.permits.data.AccessControlList;

class JdbcAclRowMapper implements RowMapper<AccessControlList> {
	private Map<Long, AccessControlList> cache = new HashMap<>();

	@Override
	public AccessControlList mapRow(ResultSet rs, int rowNum) throws SQLException {
		Long id = rs.getLong("id");

		AccessControlList acl;
		if (cache.containsKey(id)) {
			acl = cache.get(id);
		} else {
			acl = new AccessControlList();
			acl.setId(String.valueOf(id));
			acl.setName(rs.getString("name"));
			acl.setDescription(rs.getString("description"));
		}

		String status = rs.getString("status");
		if (status != null && status.length() > 0) {
			Set<String> statuses = acl.getStatuses();
			statuses.add(status);
			acl.setStatuses(statuses);
		}

		String objType = rs.getString("obj_type");
		if (objType != null && objType.length() > 0) {
			Set<String> objTypes = acl.getObjTypes();
			objTypes.add(objType);
			acl.setObjTypes(objTypes);
		}

		cache.putIfAbsent(id, acl);

		return acl;
	}
}
