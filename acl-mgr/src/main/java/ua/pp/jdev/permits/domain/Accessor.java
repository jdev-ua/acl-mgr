package ua.pp.jdev.permits.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class Accessor {
	private String id;
	
	@NotBlank(message = "{validation.notblank.name}")
    private String name;
    private Boolean alias;
    private Boolean svc;
    @Min(1) @Max(7)
    private Integer permit;
    
    private Set<String> xPermits = new HashSet<>();
    private Set<String> orgLevels = new HashSet<>();
}
