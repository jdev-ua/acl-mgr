package ua.pp.jdev.permits.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("OBJ_TYPE")
class ObjType implements Serializable {
	private static final long serialVersionUID = -5566939475849907389L;
	
	@Id
	private Long id;
	private Long aclId;
	private String objType;
}
