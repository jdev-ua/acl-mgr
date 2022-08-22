package ua.pp.jdev.permits.service;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import ua.pp.jdev.permits.data.dict.DictDataProvider;

@Service
public class DictionaryService {
	DictDataProvider dataProvider;

	@PostConstruct
	private void populate() {
		try {
			// TODO Make it configurable!
			dataProvider = new DictDataProvider("/dict/dictionaries.json");
		} catch (FileNotFoundException|URISyntaxException fnfe) {
			// TODO: handle exception
			throw new RuntimeException(fnfe);
		}
	}
	
	public Map<String, String> getObjTypes() {
		return dataProvider.getSupportedTypes();
	}

	public Map<String, String> getStatuses(Collection<String> objTypes) {
		Map<String, String> dictStatuses = new TreeMap<>();
		objTypes.forEach(t -> {
			dictStatuses.putAll(dataProvider.getAvailableStatuses(t, ""));
		});

		return dictStatuses;
	}
	
	public Map<String, String> getAliases(boolean alias, boolean svc) {
		return alias ? dataProvider.getSupportedAliases(svc) : dataProvider.getSupportedRoles();
	}
}
