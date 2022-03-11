package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.collins.trustedsystems.briefcase.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class JsonRequirementsFile {
	private String tool = "";
	private String implementation = "";
	private long date = 0L;
	private String hash = "";
	private List<CyberRequirement> requirements = new ArrayList<>();

	private final static String GEARCASE = "gearcase";
	private final static String DCRYPPS = "dcrypps";
	private final static String TOOL = "Tool";
	private final static String IMPLEMENTATION = "Implementation";
	private final static String DATE = "Date";
	private final static String HASH = "Hash";
	private final static String REQUIREMENTS = "Requirements";
	private final static String DESCRIPTION = "Description";
	private final static String NIST_SECURITY_CONTROLS = "NIST 800-53 Security Controls";
	private final static String COMPONENT = "Component";

	public JsonRequirementsFile(String tool, long date, String implementation, String hash,
			List<CyberRequirement> requirements) {
		this.tool = tool;
		this.implementation = implementation;
		this.date = date;
		this.hash = hash;
		this.requirements = requirements;
	}

	public JsonRequirementsFile() {

	}

	public String getTool() {
		return this.tool;
	}

	public String getImplementation() {
		return this.implementation;
	}

	public long getDate() {
		return this.date;
	}

	public String getHash() {
		return this.hash;
	}

	public List<CyberRequirement> getRequirements() {
		if (this.requirements == null) {
			this.requirements = new ArrayList<CyberRequirement>();
		}
		return this.requirements;
	}

	public boolean importFile(File file) {
		try {

			final JsonReader jsonReader = new JsonReader(new FileReader(file));
			final JsonParser jsonParser = new JsonParser();
			final JsonElement jsonFile = jsonParser.parse(jsonReader);
			if (!jsonFile.isJsonObject()) {
				return false;
			}
			final JsonObject jsonObject = jsonFile.getAsJsonObject();

			if (!jsonObject.has(TOOL)) {
				return false;
			}
			this.tool = jsonObject.get(TOOL).getAsString();
//			if (jsonObject.has(DATE)) {
//				this.date = jsonObject.get(DATE).getAsLong();
//			}
//			if (jsonObject.has(HASH)) {
//				this.hash = jsonObject.get(HASH).getAsString();
//			}
			if (!jsonObject.has(IMPLEMENTATION)) {
				return false;
			}
			this.implementation = jsonObject.get(IMPLEMENTATION).getAsString();

			if (this.tool.equalsIgnoreCase(GEARCASE)) {
				if (!jsonObject.has(REQUIREMENTS)) {
					return false;
				}
				final JsonObject reqObject = jsonObject.get(REQUIREMENTS).getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : reqObject.entrySet()) {
					if (entry.getValue().isJsonObject()) {
						final JsonObject jsonReq = entry.getValue().getAsJsonObject();
						if (!jsonReq.has(NIST_SECURITY_CONTROLS) || !jsonReq.has(DESCRIPTION)
								|| !jsonReq.has(COMPONENT)) {
							continue;
						}
						final JsonArray typeArray = jsonReq.get(NIST_SECURITY_CONTROLS).getAsJsonArray();
						if (typeArray.size() == 0) {
							continue;
						}
						final String type = typeArray.get(0).getAsString();
						final String description = jsonReq.get(DESCRIPTION).getAsString();
						final String context = jsonReq.get(COMPONENT).getAsString();
						this.requirements.add(new CyberRequirement(this.date, this.tool, CyberRequirement.toDo, type,
								"", description, context, false, ""));
					}
				}
			} else if (this.tool.equalsIgnoreCase(DCRYPPS)) {

			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean exportFile(File file) {
		try {
			final JsonUtil<JsonRequirementsFile> json = new JsonUtil<JsonRequirementsFile>(JsonRequirementsFile.class);
			json.writeObject(this, file);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Removes requirements from jsonFile if they appear in reqList
	 * @param reqList
	 */
	public void removeRequirements(final List<CyberRequirement> reqList) {
//		Iterator<CyberRequirement> i = getRequirements().iterator();
//		while (i.hasNext()) {
//			CyberRequirement jsonReq = i.next();
//			for (CyberRequirement req : reqList) {
//				if (req.getType().equalsIgnoreCase(jsonReq.getType())
//						&& req.getContext().equalsIgnoreCase(jsonReq.getContext())) {
//					i.remove();
//					break;
//				}
//			}
//		}

		reqList.forEach(r -> removeRequirement(r));
	}

	private void removeRequirement(final CyberRequirement req) {
		for (Iterator<CyberRequirement> reqIter = getRequirements().iterator(); reqIter.hasNext();) {
			final CyberRequirement jsonReq = reqIter.next();
			if (equals(jsonReq, req)) {
				reqIter.remove();
				return;
			}
		}
	}

	private boolean equals(final CyberRequirement req1, final CyberRequirement req2) {
		return req1.getType().equalsIgnoreCase(req2.getType()) && req1.getContext().equalsIgnoreCase(req2.getContext());
	}

}
