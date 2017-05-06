package org.openmrs.module.initializer.api.c;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
public class BaseConceptLineProcessor extends BaseLineProcessor<Concept, ConceptService> {
	
	protected static String HEADER_NAME = "name";
	
	protected static String HEADER_SHORTNAME = "short name";
	
	protected static String HEADER_FSNAME = "fully specified name";
	
	protected static String HEADER_DESC = "description";
	
	protected static String HEADER_CLASS = "data class";
	
	protected static String HEADER_DATATYPE = "data type";
	
	public BaseConceptLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	@Override
	protected Concept getByUuid(String[] line) throws IllegalArgumentException {
		String uuid = getUuid(line);
		Concept concept = service.getConceptByUuid(uuid);
		if (concept == null) {
			concept = new Concept();
			if (!StringUtils.isEmpty(uuid)) {
				concept.setUuid(uuid);
			}
		}
		return concept;
	}
	
	/*
	 * This is the base concept implementation.
	 */
	protected Concept fill(Concept concept, String[] line) throws IllegalArgumentException {
		
		LocalizedHeader lh = null;
		
		// Fully specified names
		lh = getLocalizedHeader(HEADER_FSNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line[getColumn(lh.getI18nHeader(locale))];
			if (!StringUtils.isEmpty(name)) {
				ConceptName conceptName = new ConceptName(name, locale);
				concept.setFullySpecifiedName(conceptName);
			}
		}
		
		// Short names
		lh = getLocalizedHeader(HEADER_SHORTNAME);
		for (Locale locale : lh.getLocales()) {
			String name = line[getColumn(lh.getI18nHeader(locale))];
			if (!StringUtils.isEmpty(name)) {
				ConceptName conceptName = new ConceptName(name, locale);
				concept.setShortName(conceptName);
			}
		}
		
		// Descriptions
		lh = getLocalizedHeader(HEADER_DESC);
		for (Locale locale : lh.getLocales()) {
			String desc = line[getColumn(lh.getI18nHeader(locale))];
			if (!StringUtils.isEmpty(desc)) {
				ConceptDescription conceptDesc = new ConceptDescription(desc, locale);
				concept.addDescription(conceptDesc);
			}
		}
		
		// Concept data class
		String conceptClassName = line[getColumn(HEADER_CLASS)];
		ConceptClass conceptClass = service.getConceptClassByName(conceptClassName);
		concept.setConceptClass(conceptClass);
		
		// Concept data type
		String conceptTypeName = line[getColumn(HEADER_DATATYPE)];
		ConceptDatatype conceptDatatype = service.getConceptDatatypeByName(conceptTypeName);
		concept.setDatatype(conceptDatatype);
		
		return concept;
	}
}