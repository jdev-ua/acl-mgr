package ua.pp.jdev.permits.data.jpa;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Table("OBJ_TYPE")
@EqualsAndHashCode(of = {"objType"})
class TableObjType implements Serializable {
	private static final long serialVersionUID = -5566939475849907389L;
	
	@Id
	private Long id;
	private Long aclId;
	private String objType;
}
