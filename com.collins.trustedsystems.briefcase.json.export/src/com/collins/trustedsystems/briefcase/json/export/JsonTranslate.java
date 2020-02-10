package com.collins.trustedsystems.briefcase.json.export;

import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexBoolean;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexFalse;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexNull;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexNumber;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexString;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexTrue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonTranslate {

	private static JsonElement genElement(JsonAnnexElement element) {
		if (element instanceof JsonAnnexObject) {
			return genObject((JsonAnnexObject) element);
		} else if (element instanceof JsonAnnexArray) {
			return genArray((JsonAnnexArray) element);
		} else if (element instanceof JsonAnnexString) {
			return genString((JsonAnnexString) element);
		} else if (element instanceof JsonAnnexNumber) {
			return genNumber((JsonAnnexNumber) element);
		} else if (element instanceof JsonAnnexBoolean) {
			return genBoolean((JsonAnnexBoolean) element);
		} else if (element instanceof JsonAnnexNull) {
			return genNull((JsonAnnexNull) element);
		}
		return JsonNull.INSTANCE;
	}

	private static JsonObject genObject(JsonAnnexObject object) {
		JsonObject jsonObject = new JsonObject();
		for (JsonAnnexMember member : object.getJsonAnnexMembers()) {
			jsonObject.add(member.getKey().getValue(), genElement(member.getJsonAnnexElement()));
		}
		return jsonObject;
	}

	private static JsonArray genArray(JsonAnnexArray array) {
		JsonArray jsonArray = new JsonArray();
		for (JsonAnnexElement element : array.getJsonAnnexElements()) {
			jsonArray.add(genElement(element));
		}
		return jsonArray;
	}

	private static JsonPrimitive genString(JsonAnnexString element) {
		return new JsonPrimitive(element.getValue());
	}

	private static JsonPrimitive genNumber(JsonAnnexNumber element) {
		if (element instanceof JsonAnnexInteger) {
			return new JsonPrimitive(((JsonAnnexInteger) element).getValue());
		} else if (element instanceof JsonAnnexReal) {
			return new JsonPrimitive(((JsonAnnexReal) element).getValue());
		}
		return null;
	}

	private static JsonPrimitive genBoolean(JsonAnnexBoolean element) {
		if (element instanceof JsonAnnexTrue) {
			return new JsonPrimitive(true);
		} else if (element instanceof JsonAnnexFalse) {
			return new JsonPrimitive(false);
		}
		return null;
	}

	private static JsonNull genNull(JsonAnnexNull element) {
		return JsonNull.INSTANCE;
	}

	public static JsonElement genAnnexLibrary(AnnexLibrary annexLib) {
		DefaultAnnexLibrary defaultAnnexLib = (DefaultAnnexLibrary) annexLib;
		JsonAnnexElement element = ((JsonAnnexLibrary) defaultAnnexLib.getParsedAnnexLibrary()).getJsonAnnexElement();
		return genElement(element);
	}

	public static JsonElement genAnnexSubclause(AnnexSubclause annexSub) {
		DefaultAnnexSubclause defaultAnnexSub = (DefaultAnnexSubclause) annexSub;
		JsonAnnexElement element = ((JsonAnnexSubclause) defaultAnnexSub.getParsedAnnexSubclause())
				.getJsonAnnexElement();
		return genElement(element);
	}

}
