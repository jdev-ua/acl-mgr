package ua.pp.jdev.permits.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pp.jdev.permits.enums.Role;
import ua.pp.jdev.permits.util.IDGenerator;

@Data
@NoArgsConstructor
public class User {
	private String id = IDGenerator.genStringID();

	@NotBlank(message = "{validation.notblank.name}")
	@Length(max = 32, message = "{validation.length.name}")
	private String username = "";
	@Length(min = 8, message = "{validation.length.password}")
	private String password = "";
	@NotBlank(message = "{validation.notblank.email}")
	@Email
	private String email = "";
	private boolean enabled = true;
	private String firstName = "";
	private String lastName = "";
	private String position = "";
	private boolean isNew = true;
	private List<Role> roles = new ArrayList<>();
}
