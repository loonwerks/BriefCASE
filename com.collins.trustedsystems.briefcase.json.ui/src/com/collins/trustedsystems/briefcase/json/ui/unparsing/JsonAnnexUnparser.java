package com.collins.trustedsystems.briefcase.json.ui.unparsing;

import org.eclipse.xtext.serializer.ISerializer;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.annexsupport.AnnexUnparser;

import com.collins.trustedsystems.briefcase.json.serializer.JsonSerializer;
import com.collins.trustedsystems.briefcase.json.ui.internal.JsonActivator;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class JsonAnnexUnparser implements AnnexUnparser {

	@Inject
	private ISerializer serializer;

	protected ISerializer getSerializer() {
		if (serializer == null) {
			Injector injector = JsonActivator.getInstance()
					.getInjector(JsonActivator.COM_COLLINS_TRUSTEDSYSTEMS_BRIEFCASE_JSON_JSON);
			serializer = injector.getInstance(JsonSerializer.class);
		}
		return serializer;
	}

	@Override
	public String unparseAnnexLibrary(AnnexLibrary library, String indent) {
		library.setName(null);
		return System.lineSeparator() + getSerializer().serialize(library).replaceAll("^\\\n*", "");
	}

	@Override
	public String unparseAnnexSubclause(AnnexSubclause subclause, String indent) {
		subclause.setName(null);
		return System.lineSeparator() + getSerializer().serialize(subclause).replaceAll("^\\\n*", "");
	}

}
