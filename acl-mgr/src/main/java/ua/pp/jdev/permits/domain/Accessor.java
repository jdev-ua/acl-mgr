package ua.pp.jdev.permits.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ua.pp.jdev.permits.dao.IDGenerator;
import ua.pp.jdev.permits.enums.State;

@Data
@Table("ACCESSOR")
public class Accessor implements Cloneable, Serializable, Persistable<Long> {
	private static final long serialVersionUID = -2252261108300139174L;

	@Id
	private Long id;

	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PACKAGE)
	private Long aclId;

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	@Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.pattern.name}")
	private String name;
	private boolean alias;
	private boolean svc;
	@Min(1)
	@Max(7)
	private int permit;

	@Transient
	private State state;

	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PACKAGE)
	@MappedCollection(idColumn = "ACCESSOR_ID")
	private Set<OrgLevel> rawOrgLevels = new HashSet<>();

	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PACKAGE)
	@MappedCollection(idColumn = "ACCESSOR_ID")
	private Set<XPermit> rawXPermits = new HashSet<>();

	public Accessor() {
		this(State.PURE);
	}

	public Accessor(State state) {
		if (State.NEW.equals(state) && getId() == null) {
			setId(IDGenerator.generateID());
		}

		this.state = state;
	}

	@PersistenceCreator
	protected Accessor(Long id, Long aclId, String name, int permit, boolean alias, boolean svc, Set<OrgLevel> rawOrgLevels,
			Set<XPermit> rawXPermits) {
		setName(name);
		setId(id);
		setAclId(aclId);
		setPermit(permit);
		setAlias(alias);
		setSvc(svc);
		setRawOrgLevels(rawOrgLevels);
		setRawXPermits(rawXPermits);
		setState(State.PURE);
	}

	@Override
	public Accessor clone() throws CloneNotSupportedException {
		Accessor clone = (Accessor) super.clone();

		// First untie clone from origin's org.level set by creation own new one
		clone.rawOrgLevels = new HashSet<>();
		// Than populate it with values from origin
		clone.setOrgLevels(getOrgLevels());

		// First untie clone from origin's xPermit set by creation own new one
		clone.rawXPermits = new HashSet<>();
		// Than populate it with values from origin
		clone.setXPermits(getXPermits());

		return clone;
	}

	public Set<String> getOrgLevels() {
		// Convert Org.Levels stored in internal data type into String
		return getRawOrgLevels().stream().map(t -> t.getOrgLevel()).collect(Collectors.toSet());
	}

	public void setOrgLevels(Set<String> orgLevels) {
		// Apply new not-null set of Org.Levels or clear current otherwise
		if (orgLevels != null) {
			// Convert new Org.Levels from String to internal data type 
			Set<OrgLevel> prepared = orgLevels.stream().map(t -> {
				OrgLevel orgLevel = new OrgLevel();
				orgLevel.setAccessorId(getId());
				orgLevel.setOrgLevel(t);
				return orgLevel;
			}).collect(Collectors.toSet());
			// Apply new values
			setRawOrgLevels(prepared);
		} else {
			rawOrgLevels.clear();
		}
	}

	public Set<String> getXPermits() {
		// Convert XPermits stored in internal data type into String
		return getRawXPermits().stream().map(t -> t.getXPermit()).collect(Collectors.toSet());
	}

	public void setXPermits(Set<String> xPermits) {
		// Apply new not-null set of XPermits or clear current otherwise
		if (xPermits != null) {
			// Convert new XPermits from String to internal data type
			Set<XPermit> prepared = xPermits.stream().map(t -> {
				XPermit xPermit = new XPermit();
				xPermit.setAccessorId(getId());
				xPermit.setXPermit(t);
				return xPermit;
			}).collect(Collectors.toSet());
			// Apply new values
			setRawXPermits(prepared);
		} else {
			rawXPermits.clear();
		}
	}
	
	public void setId(Long id) {
		this.id = id;
		// Update contained XPermits with new acessor's ID
		getRawXPermits().forEach(t -> t.setAccessorId(id));
		// Update contained Org.Levels with new acessor's ID
		getRawOrgLevels().forEach(t -> t.setAccessorId(id));
	}

	@Override
	public boolean isNew() {
		return State.NEW.equals(getState());
	}
}
