package ua.pp.jdev.permits.data.jpa;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("ORG_LEVEL")
class TableOrgLevel implements Serializable {
	private static final long serialVersionUID = 214464402970944058L;
	
	@Id
	private Long id;
	private Long accessorId;
	private String orgLevel;
}
