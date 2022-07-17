package ua.pp.jdev.permits.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.AccessLevel;
import lombok.Setter;
import ua.pp.jdev.permits.dao.IDGenerator;

@Data
public class Accessor {
	@Setter(AccessLevel.NONE)
	private String id = IDGenerator.NULL_ID;
	
	@NotBlank(message = "{validation.notblank.name}")
    private String name;
    private boolean alias;
    private boolean svc;
    @Min(1) @Max(7)
    private int permit;
    
    private Set<String> xPermits = new HashSet<>();
    private Set<String> orgLevels = new HashSet<>();
}
