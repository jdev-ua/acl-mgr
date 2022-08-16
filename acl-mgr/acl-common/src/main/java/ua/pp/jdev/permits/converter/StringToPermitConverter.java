package ua.pp.jdev.permits.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.enums.Permit;

@Component
public class StringToPermitConverter implements Converter<String, Permit> {
	@Override
	public Permit convert(String source) {
		return Permit.getPermit(Integer.parseInt(source)).orElse(Permit.NONE);
	}
}
