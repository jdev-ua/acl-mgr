package ua.pp.jdev.permits.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("XPERMIT")
class XPermit implements Serializable {
	private static final long serialVersionUID = -8018322317901356555L;
	
	@Id
	private Long id;
	private Long accessorId;
	@Column("XPERMIT")
	private String xPermit;
}
