package com.collins.trustedsystems.briefcase.assurance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.instance.ComponentInstance;

import com.collins.trustedsystems.briefcase.assurance.artifact.ArtifactDescriptionManager;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.JsonRequirementsFile;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsDatabaseHelper;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.ModelHashcode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.rockwellcollins.atc.resolute.analysis.execution.EvaluationContext;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteExternalFunctionLibrary;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteFailException;
import com.rockwellcollins.atc.resolute.analysis.values.BoolValue;
import com.rockwellcollins.atc.resolute.analysis.values.ResoluteValue;

public class Assurance extends ResoluteExternalFunctionLibrary {

	private EvaluationContext context;

	@Override
	public ResoluteValue run(EvaluationContext context, String function, List<ResoluteValue> args) {

		this.context = context;

		switch (function.toLowerCase()) {
		case AssuranceType.SECURITY_REQUIREMENTS_IMPORTED_OR_OMITTED_WITH_RATIONALE:
			return security_requirements_imported_or_omitted_with_rationale();
		case AssuranceType.SECURITY_ANALYSIS_PERFORMED_ON_CURRENT_MODEL:
			return security_analysis_performed_on_current_model();
		case AssuranceType.SECURITY_ANALYSIS_PRODUCES_NO_APPLICABLE_REQUIREMENTS:
			return security_analysis_produces_no_applicable_requirements();
		case AssuranceType.SECURITY_REQUIREMENTS_REVIEW:
		case AssuranceType.AGREE_RESULTS_REVIEW:
		case AssuranceType.REQUIREMENTS_STATED_IN_TERMS_OF_INTERFACE:
		case AssuranceType.TOOL_GENERATED_APIS_CORRECTLY_DERIVED_FROM_MODEL:
		case AssuranceType.APPLICATION_CODE_USES_TOOL_GENERATED_APIS:
		case AssuranceType.LIBRARY_USAGE_HAS_NO_EFFECT_OUTSIDE_OF_COMPONENT_BOUNDARY:
		case AssuranceType.APPLICATION_CODE_RUNS_TO_COMPLETION:
		case AssuranceType.COMPONENT_OUTPUT_SATISFIES_CONTRACT_ON_EVERY_DISPATCH:
		case AssuranceType.COMPONENT_OUTPUT_SATISFIES_INFORMATION_FLOW_SPECIFICATIONS_ON_EVERY_DISPATCH:
		case AssuranceType.COMPONENT_EXECUTION_TIME_CONFORMS_TO_SPECIFIED_WCET_ON_EVERY_DISPATCH:
		case AssuranceType.COMPONENT_INFRASTRUCTURE_CODE_SATISFIES_AADL_SEMANTICS:
		case AssuranceType.IDENTIFIABLE_COMPONENT_PLATFORM_DEPLOYMENT:
		case AssuranceType.APPLICATION_INFRASTRUCTURE_DEPLOYMENT_INTERFACE_IDENTIFICATION:
		case AssuranceType.PORT_CORRESPONDENCE_TRACEABILITY:
		case AssuranceType.COMPONENT_DISPATCH:
		case AssuranceType.INFRASTRUCTURE_APPLICATION_NON_INTERFERENCE:
		case AssuranceType.SPECIFIED_INFORMATION_FLOW_DIRECTIONALITY_ENFORCED_BY_DEPLOYMENT:
		case AssuranceType.DEPLOYED_COMPONENT_SATISFIES_FUNCTIONAL_REQUIREMENTS:
		case AssuranceType.DEPLOYED_COMPONENT_SATISFIES_INTRA_COMPONENT_FLOWS:
		case AssuranceType.PLATFORM_COMPONENTS_GUARANTEE_REQUIRED_PROPERTIES:
			return evaluate_artifact(function, args);
		}

		throw new ResoluteFailException(
				"[ERROR] Function " + function + " is not part of the BriefCASE Assurance library",
				context.getThisInstance().getSubcomponent());
	}

	private IProject getCurrentProject() {
		return Filesystem.getFile(context.getThisInstance().eResource().getURI()).getProject();
	}

	private BoolValue evaluate_artifact(String name, List<ResoluteValue> args) {

		// Get artifact definition doc
		final String artifactDefinitionFilename = Activator.getDefault()
				.getPreferenceStore()
				.getString(ArtifactDescriptionManager.ARTIFACT_DESCRIPTION_FILE);
		if (artifactDefinitionFilename.isBlank()) {
			throw new ResoluteFailException("[ERROR] Artifact definition file not specified in BriefCASE preferences",
					context.getThisInstance().getSubcomponent());
		}

		File artifactDefinitionFile = null;
		try {
			artifactDefinitionFile = getFile(artifactDefinitionFilename);
		} catch (Exception e) {
			throw new ResoluteFailException("[ERROR] Unable to locate artifact definition file",
					context.getThisInstance().getSubcomponent());
		}

		JsonElement jsonFile = null;
		try {
			final JsonReader jsonReader = new JsonReader(new FileReader(artifactDefinitionFile));
			final JsonParser jsonParser = new JsonParser();
			jsonFile = jsonParser.parse(jsonReader);
			jsonReader.close();
		} catch (Exception e) {
			throw new ResoluteFailException("[ERROR] Unable to read artifact definition file",
					context.getThisInstance().getSubcomponent());
		}

		if (!jsonFile.isJsonObject()) {
			throw new ResoluteFailException("[ERROR] Malformed artifact definition file",
					context.getThisInstance().getSubcomponent());
		}
		// Look up artifact object
		if (!jsonFile.getAsJsonObject().has(name)) {
			throw new ResoluteFailException("[ERROR] Artifact definition file does not contain " + name,
					context.getThisInstance().getSubcomponent());
		} else if (!jsonFile.getAsJsonObject().get(name).isJsonObject()) {
			throw new ResoluteFailException("[ERROR] Malformed artifact definition file",
					context.getThisInstance().getSubcomponent());
		}
		final JsonObject artifact = jsonFile.getAsJsonObject().get(name).getAsJsonObject();

		// Get artifact contents
		if (!artifact.has(ArtifactDescriptionManager.ARTIFACT_FILE_LOCATION)) {
			throw new ResoluteFailException(
					"[ERROR] Artifact definition for " + name + " does not specify a file location",
					context.getThisInstance().getSubcomponent());
		} else if (!artifact.has(ArtifactDescriptionManager.EVALUATION)
				|| !artifact.get(ArtifactDescriptionManager.EVALUATION)
						.getAsJsonObject()
						.has(ArtifactDescriptionManager.TYPE)) {
			throw new ResoluteFailException(
					"[ERROR] Artifact definition for " + name + " does not specify an evaluation method",
					context.getThisInstance().getSubcomponent());
		}

		final String artifactFileLocation = artifact.get(ArtifactDescriptionManager.ARTIFACT_FILE_LOCATION)
				.getAsString();
		final JsonObject evaluation = artifact.get(ArtifactDescriptionManager.EVALUATION).getAsJsonObject();
		final String evaluationType = evaluation.get(ArtifactDescriptionManager.TYPE).getAsString();

		File artifactFile = null;
		try {
			artifactFile = getFile(artifactFileLocation);
		} catch (Exception e) {
			throw new ResoluteFailException("[ERROR] Unable to locate artifact file " + artifactFileLocation,
					context.getThisInstance().getSubcomponent());
		}
		if (artifactFile.exists() && ArtifactDescriptionManager.EXISTS.equals(evaluationType)) {
			return new BoolValue(true);
		} else if (!artifactFile.exists()) {
			if (ArtifactDescriptionManager.EXISTS.equals(evaluationType)) {
				return new BoolValue(false);
			}
			throw new ResoluteFailException("[ERROR] Artifact file " + artifactFileLocation + " could not be found",
					context.getThisInstance().getSubcomponent());
		}

		String contents = "";
		try {
			contents = getContents(artifactFile);
		} catch (Exception e) {
			throw new ResoluteFailException("[ERROR] Unable to read artifact file " + artifactFileLocation,
					context.getThisInstance().getSubcomponent());
		}

		if (ArtifactDescriptionManager.REGEX.equals(evaluationType)) {
			if (!evaluation.has(ArtifactDescriptionManager.REGEX)) {
				throw new ResoluteFailException(
						"[ERROR] Artifact " + name + " has evaluation method 'regex' but no regex is specified",
						context.getThisInstance().getSubcomponent());
			}
			String regex = evaluation.get(ArtifactDescriptionManager.REGEX).getAsString();

			JsonArray argArray = new JsonArray();
			if (evaluation.has(ArtifactDescriptionManager.ARGS)
					&& evaluation.get(ArtifactDescriptionManager.ARGS).isJsonArray()) {
				argArray = evaluation.get(ArtifactDescriptionManager.ARGS).getAsJsonArray();
			}
			if (regex.contains("%s") && argArray.size() > 0) {
				final List<String> regexArgs = new ArrayList<>();
				for (JsonElement arg : argArray) {
					try {
						final ResoluteValue resoluteArg = args.get(arg.getAsNumber().intValue());
						if (resoluteArg.isString()) {
							regexArgs.add(resoluteArg.getString());
						} else if (resoluteArg.isNamedElement()) {
							final NamedElement namedElement = resoluteArg.getNamedElement();
							if (namedElement instanceof ComponentInstance) {
								final ComponentInstance ci = (ComponentInstance) resoluteArg.getNamedElement();
								String componentName = context.getThisInstance().getComponentClassifier().getName();
								if (!ci.getComponentInstancePath().isBlank()) {
									componentName += "." + ci.getComponentInstancePath();
								}
								regexArgs.add(componentName);
							} else {
								regexArgs.add(namedElement.getName());
							}
						} else if (resoluteArg.isInt()) {
							regexArgs.add(Long.toString(resoluteArg.getInt()));
						} else if (resoluteArg.isReal()) {
							regexArgs.add(Double.toString(resoluteArg.getReal()));
						} else if (resoluteArg.isBool()) {
							regexArgs.add(Boolean.toString(resoluteArg.getBool()));
						} else {
							throw new ResoluteFailException("[ERROR] Arg type " + arg.getClass()
									+ " not handled as regex type for claim " + name,
									context.getThisInstance().getSubcomponent());
						}
					} catch (Exception e) {
						throw new ResoluteFailException("[ERROR] Invalid arg index for claim " + name,
								context.getThisInstance().getSubcomponent());
					}
				}
				regex = String.format(regex, regexArgs.toArray());
			}

			// Match regex
			return new BoolValue(Pattern.compile(regex, Pattern.DOTALL).matcher(contents).find());
		}

		throw new ResoluteFailException("[ERROR] Unable to evaluate artifact " + name,
				context.getThisInstance().getSubcomponent());

	}

	private String getContents(File file) throws Exception {
		String contents = "";
		final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (!contents.isEmpty()) {
				contents += System.lineSeparator();
			}
			contents += line;
		}
		bufferedReader.close();
		return contents.toString();
	}

	// Returns a file corresponding to either an absolute or relative path
	private File getFile(String filename) throws Exception {
		File file = new File(filename);
		if (file.exists()) {
			return file;
		} else if (!file.isAbsolute()) {
			final Resource r = context.getThisInstance().eResource();
			final Path p = new Path(r.getURI().segment(1) + File.separator + filename);
			final IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
			if (f.exists()) {
				return f.getRawLocation().makeAbsolute().toFile();
			}
		}

		throw new Exception("Filename " + filename + " not found");

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
					"[ERROR] Unable to determine most recent security analysis output",
					context.getThisInstance().getSubcomponent());
		}
		final File file = new File(analysisOutputFilename);
		if (!file.exists()) {
			throw new ResoluteFailException(
					"[ERROR] Unable to find most recent security analysis output " + analysisOutputFilename,
					context.getThisInstance().getSubcomponent());
		}
		// Parse Json
		final JsonRequirementsFile jsonFile = new JsonRequirementsFile();
		if (!jsonFile.importFile(file)) {
			throw new ResoluteFailException(
					"[ERROR] Unable to read most recent security analysis output " + analysisOutputFilename,
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
			throw new ResoluteFailException("[ERROR] Unable to determine most recent security analysis output",
					context.getThisInstance().getSubcomponent());
		}
		final File file = new File(analysisOutputFilename);
		if (!file.exists()) {
			throw new ResoluteFailException(
					"[ERROR] Unable to find most recent security analysis output " + analysisOutputFilename,
					context.getThisInstance().getSubcomponent());
		}

		// Parse Json
		final JsonRequirementsFile jsonFile = new JsonRequirementsFile();
		if (!jsonFile.importFile(file)) {
			throw new ResoluteFailException(
					"[ERROR] Unable to read most recent security analysis output " + analysisOutputFilename,
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

}
