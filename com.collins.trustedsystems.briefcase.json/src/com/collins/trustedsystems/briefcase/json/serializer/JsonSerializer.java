package com.collins.trustedsystems.briefcase.json.serializer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.impl.Serializer;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause;
import com.collins.trustedsystems.briefcase.json.services.JsonGrammarAccess;
import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class JsonSerializer extends Serializer {

	@Inject
	private JsonGrammarAccess grammarAccess;

	@Override
	protected ISerializationContext getIContext(EObject semanticObject) {
		ISerializationContext result = null;
		if (semanticObject instanceof JsonAnnexLibrary) {
			for (final ISerializationContext o : contextFinder.findByContents(semanticObject, null)) {
				if (o.getParserRule() == grammarAccess.getJsonAnnexLibraryRule()) {
					result = o;
					break;
				}
			}
		} else if (semanticObject instanceof JsonAnnexSubclause) {
			for (final ISerializationContext o : contextFinder.findByContents(semanticObject, null)) {
				if (o.getParserRule() == grammarAccess.getJsonAnnexSubclauseRule()) {
					result = o;
					break;
				}
			}
		} else {
			result = super.getIContext(semanticObject);
		}
		return result;

	}
}
