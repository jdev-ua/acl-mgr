package ua.pp.jdev.permits.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.enums.OrgLevel;

@Component
public class StringToOrgLevelConverter implements Converter<String, OrgLevel> {
	@Override
    public OrgLevel convert(String source) {
        return OrgLevel.getOrgLevel(source).orElseThrow(RuntimeException::new);
    }
}
