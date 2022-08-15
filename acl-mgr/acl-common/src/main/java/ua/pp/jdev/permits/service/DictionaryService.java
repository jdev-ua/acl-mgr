package ua.pp.jdev.permits.service;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

@Service
public class DictionaryService {
    private Map<String, String> dictObjTypes = new TreeMap<>();
    private Map<String, String> dictStatuses = new TreeMap<>();

	public DictionaryService() {
		initDictObjTypes();
		initDictStatuses();
	}

	private void initDictObjTypes() {
    	dictObjTypes.put("bnk_agreement","bnk_agreement");
    	dictObjTypes.put("bnk_application","bnk_application");
    	dictObjTypes.put("bnk_borrower","bnk_borrower");
    	dictObjTypes.put("bnk_client","bnk_client");
    	dictObjTypes.put("bnk_committee","bnk_committee");
    	dictObjTypes.put("bnk_conclusion","bnk_conclusion");
    	dictObjTypes.put("bnk_covenant","bnk_covenant");
    	dictObjTypes.put("bnk_document","bnk_document");
    	dictObjTypes.put("bnk_grc","bnk_grc");
    	dictObjTypes.put("bnk_limit","bnk_limit");
    	dictObjTypes.put("bnk_not_client","bnk_not_client");
    	dictObjTypes.put("bnk_report","bnk_report");
    	
    }

	private void initDictStatuses() {
		dictStatuses.put("PS_S_CO_APPROVE","PS_S_CO_APPROVE");
		dictStatuses.put("PS_S_CO_ASSIGN","PS_S_CO_ASSIGN");
		dictStatuses.put("PS_S_CO_CONCL","PS_S_CO_CONCL");
		dictStatuses.put("PS_S_CO_DONE","PS_S_CO_DONE");
		dictStatuses.put("PS_S_CO_REJECT","PS_S_CO_REJECT");
		dictStatuses.put("PS_S_CO_REV","PS_S_CO_REV");
		dictStatuses.put("PS_S_RD_APPROVE","PS_S_RD_APPROVE");
		dictStatuses.put("PS_S_RD_ASSIGN","PS_S_RD_ASSIGN");
		dictStatuses.put("PS_S_RD_CONCL","PS_S_RD_CONCL");
		dictStatuses.put("PS_S_RD_DONE","PS_S_RD_DONE");
		dictStatuses.put("PS_S_RD_REJECT","PS_S_RD_REV");
		dictStatuses.put("COV_S_CLOSED","COV_S_CLOSED");
		dictStatuses.put("COV_S_INACTIVE","COV_S_INACTIVE");
		dictStatuses.put("COV_S_ACTIVE","COV_S_ACTIVE");
		dictStatuses.put("LIM_S_CLOSED","LIM_S_CLOSED");
		dictStatuses.put("LIM_S_INACTIVE","LIM_S_INACTIVE");
		dictStatuses.put("LIM_S_ACTIVE","LIM_S_ACTIVE");
	}
	
	public Map<String, String> getObjTypes() {
		return dictObjTypes;
	}
	
	public Map<String, String> getStatuses() {
		return dictStatuses;
	}
}
