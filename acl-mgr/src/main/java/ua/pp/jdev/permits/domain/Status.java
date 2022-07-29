package ua.pp.jdev.permits.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("STATUS")
class Status implements Serializable {
	private static final long serialVersionUID = 8071180708610099408L;
	
	@Id
	private Long id;
	private Long aclId;
	private String status;
}
