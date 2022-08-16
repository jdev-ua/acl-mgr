package ua.pp.jdev.permits.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ua.pp.jdev.permits.enums.XPermit;

@Component
public class StringToXPermitConverter implements Converter<String, XPermit> {
	@Override
	public XPermit convert(String source) {
        return XPermit.getXPermit(source).orElseThrow(RuntimeException::new);
    }
}
