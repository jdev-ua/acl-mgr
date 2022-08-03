package ua.pp.jdev.permits.data.orm.jdbc;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("STATUS")
class TableStatus implements Serializable {
	private static final long serialVersionUID = 8071180708610099408L;

	@Id
	private Long id;
	private Long aclId;
	private String status;
}
