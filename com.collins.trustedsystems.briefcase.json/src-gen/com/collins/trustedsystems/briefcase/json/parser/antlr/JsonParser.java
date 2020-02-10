/*
 * generated by Xtext
 */
package com.collins.trustedsystems.briefcase.json.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import com.collins.trustedsystems.briefcase.json.services.JsonGrammarAccess;

public class JsonParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private JsonGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_SL_COMMENT");
	}
	
	@Override
	protected com.collins.trustedsystems.briefcase.json.parser.antlr.internal.InternalJsonParser createParser(XtextTokenStream stream) {
		return new com.collins.trustedsystems.briefcase.json.parser.antlr.internal.InternalJsonParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "AnnexLibrary";
	}
	
	public JsonGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(JsonGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}