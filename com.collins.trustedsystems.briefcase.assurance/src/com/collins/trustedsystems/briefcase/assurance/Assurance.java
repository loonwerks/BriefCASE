package com.collins.trustedsystems.briefcase.assurance;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.collins.trustedsystems.briefcase.assurance.preferences.AssurancePreferenceConstants;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.JsonRequirementsFile;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsDatabaseHelper;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.ModelHashcode;
import com.rockwellcollins.atc.resolute.analysis.execution.EvaluationContext;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteExternalFunctionLibrary;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteFailException;
import com.rockwellcollins.atc.resolute.analysis.values.BoolValue;
import com.rockwellcollins.atc.resolute.analysis.values.ResoluteValue;
import com.rockwellcollins.atc.resolute.analysis.values.StringValue;

public class Assurance extends ResoluteExternalFunctionLibrary {

	private EvaluationContext context;

	@Override
	public ResoluteValue run(EvaluationContext context, String function, List<ResoluteValue> args) {

		this.context = context;

		switch (function.toLowerCase()) {
		case "security_requirements_imported_or_omitted_with_rationale":
			return security_requirements_imported_or_omitted_with_rationale();
		case "security_analysis_performed_on_current_model":
			return security_analysis_performed_on_current_model();
		case "security_analysis_produces_no_applicable_requirements":
			return security_analysis_produces_no_applicable_requirements();
		case "get_security_requirements_review":
			return get_security_requirements_review();
		case "get_agree_results_review":
			return get_agree_results_review();
		}

		throw new ResoluteFailException("Function " + function + " not part of BriefCASE Assurance library.",
				context.getThisInstance().getSubcomponent());
	}

	private IProject getCurrentProject() {
		return Filesystem.getFile(context.getThisInstance().eResource().getURI()).getProject();
	}

	private BoolValue security_requirements_imported_or_omitted_with_rationale() {

		final IProject currentProject = getCurrentProject();
		final RequirementsDatabaseHelper reqDb = new RequirementsDatabaseHelper(currentProject);
		final List<CyberRequirement> importedReqs = RequirementsManager.findImportedRequirements(currentProject);

		// Each requirement should have a corresponding goal in CASE_Requirements
		for (CyberRequirement req : reqDb.getAddRequirements()) {
			if (!importedReqs.contains(req)) {
				throw new ResoluteFailException(
						"All imported requirements have a corresponding goal in CASE_Requirements",
						context.getThisInstance().getSubcomponent());
			}
		}

		// There should be no unaddressed ("todo") requirements
		if (!reqDb.getToDoRequirements().isEmpty()) {
			throw new ResoluteFailException(
					"All generated requirements from the most recent cyber analysis have been addressed",
					context.getThisInstance().getSubcomponent());
		}

		// All omitted requirements should have rationale
		for (CyberRequirement req : reqDb.getOmittedRequirements()) {
			if (req.getRationale().isBlank()) {
				throw new ResoluteFailException(
						"All omitted requirements have rationale provided",
						context.getThisInstance().getSubcomponent());
			}
		}

		return new BoolValue(true);
	}

	private BoolValue security_analysis_performed_on_current_model() {

		// Get analysis results file from .reqdb
		final RequirementsDatabaseHelper reqDb = new RequirementsDatabaseHelper(getCurrentProject());
		final String analysisOutputFilename = reqDb.getAnalysisOutputFilename();
		if (analysisOutputFilename.isBlank()) {
			throw new ResoluteFailException(
					"[ERROR] Unable to determine most recent cyber analysis output",
					context.getThisInstance().getSubcomponent());
		}
		final File file = new File(analysisOutputFilename);
		if (!file.exists()) {
			throw new ResoluteFailException(
					"[ERROR] Unable to find most recent cyber analysis output " + analysisOutputFilename,
					context.getThisInstance().getSubcomponent());
		}
		// Parse Json
		final JsonRequirementsFile jsonFile = new JsonRequirementsFile();
		if (!jsonFile.importFile(file)) {
			throw new ResoluteFailException(
					"[ERROR] Unable to read most recent cyber analysis output " + analysisOutputFilename,
					context.getThisInstance().getSubcomponent());
		}
		// Get model hashcode from analysis results file
		final String analysisModelHashcode = jsonFile.getHashcode();

		try {
			// Get current model hashcode
			final String currentModelHashcode = ModelHashcode
					.getHashcode(context.getThisInstance().getComponentClassifier());
			// Compare
			return new BoolValue(analysisModelHashcode.contentEquals(currentModelHashcode));
		} catch (Exception e) {
			throw new ResoluteFailException(
					"[ERROR] Unable to determine if most recent security analysis was performed on current model",
					context.getThisInstance().getSubcomponent());
		}

	}

	private BoolValue security_analysis_produces_no_applicable_requirements() {

		// Get analysis results file from .reqdb
		final RequirementsDatabaseHelper reqDb = new RequirementsDatabaseHelper(getCurrentProject());
		final String analysisOutputFilename = reqDb.getAnalysisOutputFilename();
		if (analysisOutputFilename.isBlank()) {
			throw new ResoluteFailException("[ERROR] Unable to determine most recent cyber analysis output",
					context.getThisInstance().getSubcomponent());
		}
		final File file = new File(analysisOutputFilename);
		if (!file.exists()) {
			throw new ResoluteFailException(
					"[ERROR] Unable to find most recent cyber analysis output " + analysisOutputFilename,
					context.getThisInstance().getSubcomponent());
		}

		// Parse Json
		final JsonRequirementsFile jsonFile = new JsonRequirementsFile();
		if (!jsonFile.importFile(file)) {
			throw new ResoluteFailException(
					"[ERROR] Unable to read most recent cyber analysis output " + analysisOutputFilename,
					context.getThisInstance().getSubcomponent());
		}

		// Any requirements are in omission log (in .reqdb as omitted)
		for (CyberRequirement req : jsonFile.getRequirements()) {
			if (!reqDb.getOmittedRequirements().contains(req)) {
				return new BoolValue(false);
			}
		}

		return new BoolValue(true);
	}

	private StringValue get_security_requirements_review() {
		final String filename = Activator.getDefault()
				.getPreferenceStore()
				.getString(AssurancePreferenceConstants.REQUIREMENTS_REVIEW_FILENAME);
		final File file = new File(filename);
		if (file.exists()) {
			return new StringValue(filename);
		} else {
			return new StringValue("");
		}
	}

	private StringValue get_agree_results_review() {
		final String filename = Activator.getDefault()
				.getPreferenceStore()
				.getString(AssurancePreferenceConstants.AGREE_REVIEW_FILENAME);
		final File file = new File(filename);
		if (file.exists()) {
			return new StringValue(filename);
		} else {
			return new StringValue("");
		}
	}

}
