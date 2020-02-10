package com.collins.trustedsystems.briefcase.json.ui.parsing;

import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.annexsupport.AnnexParseUtil;
import org.osate.annexsupport.AnnexParser;

import com.collins.trustedsystems.briefcase.json.parser.antlr.JsonParser;
import com.collins.trustedsystems.briefcase.json.services.JsonGrammarAccess;
import com.collins.trustedsystems.briefcase.json.ui.internal.JsonActivator;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class JsonAnnexParser implements AnnexParser {

	@Inject
	public JsonParser parser;

	protected JsonParser getParser() {
		if (parser == null) {
			Injector injector = JsonActivator.getInstance()
					.getInjector(JsonActivator.COM_COLLINS_TRUSTEDSYSTEMS_BRIEFCASE_JSON_JSON);
			parser = injector.getInstance(JsonParser.class);
		}
		return parser;
	}

	protected JsonGrammarAccess getGrammarAccess() {
		return getParser().getGrammarAccess();
	}

	@Override
	public AnnexLibrary parseAnnexLibrary(String annexName, String source, String filename, int line, int column,
			ParseErrorReporter errReporter) {
		return (AnnexLibrary) AnnexParseUtil.parse(getParser(), source, getGrammarAccess().getJsonAnnexLibraryRule(),
				filename, line, column, errReporter);
	}

	@Override
	public AnnexSubclause parseAnnexSubclause(String annexName, String source, String filename, int line, int column,
			ParseErrorReporter errReporter) {
		return (AnnexSubclause) AnnexParseUtil.parse(getParser(), source,
				getGrammarAccess().getJsonAnnexSubclauseRule(),
				filename, line, column, errReporter);
	}

}
