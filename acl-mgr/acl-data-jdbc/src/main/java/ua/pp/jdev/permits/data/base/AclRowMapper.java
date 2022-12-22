package ua.pp.jdev.permits.data.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

import ua.pp.jdev.permits.data.Acl;

class AclRowMapper implements RowMapper<Acl> {
	private Map<Long, Acl> cache = new HashMap<>();

	@Override
	public Acl mapRow(ResultSet rs, int rowNum) throws SQLException {
		Long id = rs.getLong("id");

		Acl acl;
		if (cache.containsKey(id)) {
			acl = cache.get(id);
		} else {
			acl = Acl.builder()
					.id(String.valueOf(id))
					.name(rs.getString("name"))
					.description(rs.getString("description"))
					.build();
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
