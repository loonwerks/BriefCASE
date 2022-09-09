package com.collins.trustedsystems.briefcase.staircase.requirements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.collins.trustedsystems.briefcase.util.JsonUtil;

public class RequirementsDatabaseFile {
	private String date = "";
	private String filename = "";
	private List<CyberRequirement> requirements = new ArrayList<>();

	public RequirementsDatabaseFile() {

	}

	public RequirementsDatabaseFile(String date, String filename, List<CyberRequirement> requirements) {
		this.date = date;
		this.filename = filename;
		this.requirements = requirements;
	}

	public String getDate() {
		return date;
	}

	public String getFilename() {
		return filename;
	}

	public List<CyberRequirement> getRequirements() {
		return requirements;
	}

	public boolean importFile(File file) {
		try {
			final JsonUtil<RequirementsDatabaseFile> json = new JsonUtil<RequirementsDatabaseFile>(
					RequirementsDatabaseFile.class);
			final RequirementsDatabaseFile reqDbFile = json.readObject(file);
			date = reqDbFile.getDate();
			filename = reqDbFile.getFilename();
			requirements = reqDbFile.getRequirements();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean exportFile(File file) {
		try {
			final JsonUtil<RequirementsDatabaseFile> json = new JsonUtil<RequirementsDatabaseFile>(
					RequirementsDatabaseFile.class);
			json.writeObject(this, file);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
