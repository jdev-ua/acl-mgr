package ua.pp.jdev.permits.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.enums.Role;

@Component
public class StringToRole implements Converter<String, Role> {

	@Override
	public Role convert(String source) {
        return Role.getRole(source).orElseThrow(RuntimeException::new);
	}
}
