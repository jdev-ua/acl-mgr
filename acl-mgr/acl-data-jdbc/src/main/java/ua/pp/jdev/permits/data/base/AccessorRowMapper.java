package ua.pp.jdev.permits.data.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

import ua.pp.jdev.permits.data.Accessor;

class AccessorRowMapper implements RowMapper<Accessor> {
	private Map<Long, Accessor> cache = new HashMap<>();

	@Override
	public Accessor mapRow(ResultSet rs, int rowNum) throws SQLException {
		Long id = rs.getLong("id");

		Accessor accessor;
		if (cache.containsKey(id)) {
			accessor = cache.get(id);
		} else {
			accessor = Accessor.builder()
					.id(String.valueOf(id))
					.name(rs.getString("name"))
					.permit(rs.getInt("permit"))
					.alias(rs.getBoolean("alias"))
					.svc(rs.getBoolean("svc"))
					.build();
		}

		String orgLevel = rs.getString("org_level");
		if (orgLevel != null && orgLevel.length() > 0) {
			Set<String> orgLevels = accessor.getOrgLevels();
			orgLevels.add(orgLevel);
			accessor.setOrgLevels(orgLevels);
		}

		String xPermit = rs.getString("xpermit");
		if (xPermit != null && xPermit.length() > 0) {
			Set<String> xPermits = accessor.getXPermits();
			xPermits.add(xPermit);
			accessor.setXPermits(xPermits);
		}

		cache.putIfAbsent(id, accessor);

		return accessor;
	}
}
