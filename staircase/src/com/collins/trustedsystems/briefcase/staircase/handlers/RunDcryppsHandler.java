package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.json.export.Aadl2Json;
import com.collins.trustedsystems.briefcase.json.export.AadlTranslate.AgreePrintOption;
import com.collins.trustedsystems.briefcase.json.export.JsonTranslate;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.collins.trustedsystems.briefcase.util.ModelHashcode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RunDcryppsHandler extends AadlHandler {

	private final static String DCRYPPS_GET_REQUIREMENTS_ENDPOINT = "http://localhost:8888/routers/DCRYPPSRoute/getRequirements";
	private final static int TIMEOUT = 5 * 60 * 60 * 1000;
	private final static String JOB_NAME = "Running DCRYPPS";

	private final static String DESIRABLE_PROPERTIES = "desirableProperties";
	private final static String ATTACKER_DESCRIPTION = "attackerDescription";

//	private final static String CLASS_DICTIONARY = "classDictionary";
	private final static String TOOL = "Tool";
	private final static String PROJECT = "Project";
	private final static String IMPLEMENTATION = "Implementation";
	private final static String DATE = "Date";
	private final static String HASH = "Hashcode";
	private final static String MODEL_UNITS = "modelUnits";
	private final static String MODEL = "model";
	private final static String REQUIREMENT = "requirement";
	private final static String REQUIREMENTS = "Requirements";
	private final static String TYPE = "Type";
	private final static String CONTEXT = "Context";
	private final static String DESCRIPTION = "Description";
//	private final static String COST_GUIDANCE = "costGuidance";
//	private final static String NO_COST_GUIDANCE = "no-cost-guidance";
//	private final static String TARGET_CONFIDENCE = "targetConfidence";
//	private final static double TARGET_CONFIDENCE_VAL = 0.96;
	private final static String SUCCESS = "success";
	private final static String PCC = "pcc";

	@Override
	protected void runCommand(EObject eObj) {

		// Check if a component implementation is selected
		if (!(eObj instanceof ComponentImplementation)) {
			Dialog.showError("Run DCRYPPS", "Select the top-level component implementation for analysis.");
			return;
		}
		final ComponentImplementation ci = (ComponentImplementation) eObj;

		final WorkspaceJob job = new WorkspaceJob(JOB_NAME) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				monitor.beginTask(JOB_NAME, IProgressMonitor.UNKNOWN);

				runDcrypps(ci);

				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.schedule();

	}

	private boolean runDcrypps(ComponentImplementation ci) {

		final JsonObject request = new JsonObject();
		final JsonObject header = new JsonObject();

		final IPath path = new Path(ci.eResource().getURI().toPlatformString(true));
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getFile(path).getProject();

		header.addProperty(PROJECT.toLowerCase(), project.getName());
		header.addProperty(IMPLEMENTATION.toLowerCase(), ci.getQualifiedName());

		header.addProperty(DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")));

		try {
			header.addProperty(HASH, ModelHashcode.getHashcode(ci));
		} catch (Exception e) {
			BriefcaseNotifier.printWarning("Unable to determine model hashcode before running DCRYPPS.");
		}

		try {
			// Generate json
			final JsonElement json = Aadl2Json.generateJson(AadlUtil.getContainingPackage(ci), null,
					AgreePrintOption.BOTH);
			header.add(MODEL_UNITS, json);

		} catch (Exception e) {
			e.printStackTrace();
			Dialog.showError("Run DCRYPPS", "Unable to export model to JSON format.");
			return false;
		}


		request.add(MODEL, header);

		final JsonObject inputs = getInputsFromModel(ci);
		if (!inputs.has(DESIRABLE_PROPERTIES)) {
			Dialog.showError("Run DCRYPPS", "Component implementation " + ci.getName()
					+ " must include a JSON annex containing the " + DESIRABLE_PROPERTIES + " array.");
			return false;
		} else if (!inputs.has(ATTACKER_DESCRIPTION)) {
			Dialog.showError("Run DCRYPPS", "Component implementation " + ci.getName()
					+ " must include a JSON annex containing the " + ATTACKER_DESCRIPTION + " array.");
			return false;
		}

		final JsonObject results = postDcryppsRequest(request.toString());

		if (results == null) {
			return false;
		}

		// Save results
		if (writeRequirementsFile(project, header, results) == null) {
			return false;
		}

		BriefcaseNotifier.notify("DCRYPPS", "DCRYPPS has completed successfully");

		return true;
	}

	private JsonObject getInputsFromModel(ComponentImplementation ci) {

		JsonObject inputs = new JsonObject();
		for (AnnexSubclause annexSubclause : ci.getOwnedAnnexSubclauses()) {
			if (annexSubclause.getName().equalsIgnoreCase("json")) {
				JsonElement jsonElement = JsonTranslate.genAnnexSubclause(annexSubclause);
				if (jsonElement instanceof JsonObject) {
					return jsonElement.getAsJsonObject();
				}
				break;
			}
		}
		return inputs;
	}

	private JsonObject postDcryppsRequest(String requestBody) {

		final HttpPost httpPost = new HttpPost(DCRYPPS_GET_REQUIREMENTS_ENDPOINT);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		final RequestConfig config = RequestConfig.custom()
				.setSocketTimeout(TIMEOUT)
				.build();
		final CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		StringEntity stringEntity;
		try {
			stringEntity = new StringEntity(requestBody);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		httpPost.setEntity(stringEntity);

		try {
			final CloseableHttpResponse response = httpClient.execute(httpPost);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				final HttpEntity entity = response.getEntity();
				final String responseString = EntityUtils.toString(entity);
				final JsonParser parser = new JsonParser();
				final JsonObject responseJsonObject = parser.parse(responseString).getAsJsonObject();
				if (responseJsonObject.has(SUCCESS)
						&& !responseJsonObject.get(SUCCESS).getAsBoolean()) {
					Dialog.showError("Run DCRYPPS",
							"DCRYPPS encountered an error and could not complete analysis.  See console for details.");
					BriefcaseNotifier.println("DCRYPPS failure message: " + responseJsonObject.toString());
				} else {
					return responseJsonObject;
				}
			} else {
				Dialog.showError("Run DCRYPPS", "DCRYPPS returned error code " + statusCode + ".");
			}
		} catch (IOException e) {
			Dialog.showError("Run DCRYPPS", "Unable to connect to DCRYPPS.\n\n" + e.getMessage());
		}
		return null;

	}


	private IFile writeRequirementsFile(IProject project, JsonObject header, JsonObject results) {

		final JsonObject contents = new JsonObject();
		if (header.has(PROJECT.toLowerCase())) {
			contents.addProperty(PROJECT, header.get(PROJECT.toLowerCase()).getAsString());
		}
		contents.addProperty(TOOL, "DCRYPPS");
		if (header.has(DATE)) {
			contents.addProperty(DATE, header.get(DATE).getAsString());
		}
		if (header.has(HASH)) {
			contents.addProperty(HASH, header.get(HASH).getAsString());
		}

		if (header.has(IMPLEMENTATION.toLowerCase())) {
			String implementation = header.get(IMPLEMENTATION.toLowerCase()).getAsString();
			implementation = implementation.substring(implementation.lastIndexOf("::") + 2);
			implementation = implementation.replace(".", "_");
			implementation = implementation + "_Instance";
			contents.addProperty(IMPLEMENTATION, implementation);
		}

		final JsonArray requirements = new JsonArray();
		if (results.has(REQUIREMENTS.toLowerCase()) && results.get(REQUIREMENTS.toLowerCase()).isJsonArray()) {
			for (JsonElement reqElement : results.get(REQUIREMENTS.toLowerCase()).getAsJsonArray()) {
				final JsonObject importReq = new JsonObject();
				if (reqElement.isJsonObject()) {
					final JsonObject reqObj = reqElement.getAsJsonObject();
					if (reqObj.has(REQUIREMENT) && reqObj.get(REQUIREMENT).isJsonArray()
							&& reqObj.get(REQUIREMENT).getAsJsonArray().size() > 0) {

						final JsonArray reqArr = reqObj.get(REQUIREMENT).getAsJsonArray();

						importReq.addProperty(TYPE, reqArr.get(0).getAsString());

						// Context
						if (reqArr.size() > 1) {
							importReq.addProperty(CONTEXT, reqArr.get(reqArr.size() - 1).getAsString());
						}

					}
					if (reqObj.has(DESCRIPTION.toLowerCase())) {
						importReq.addProperty(DESCRIPTION, reqObj.get(DESCRIPTION.toLowerCase()).getAsString());
					}
					requirements.add(importReq);
				}
			}
		}
    contents.add(REQUIREMENTS, requirements);

		if (results.has(PCC) && !(results.get(PCC) instanceof JsonNull)) {
			contents.add(PCC, results.get(PCC));
		}

		final Gson gson = new GsonBuilder()
				.serializeNulls()
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create();

		IFile reqFile = null;
		try {

			// Create Requirements folder if it doesn't exist
			final IFolder reqFolder = CaseUtils.createRequirementsFolder(project);
			URI reqFileUri = URI.createURI(project.getFullPath().toString());
			if (reqFolder != null) {
				reqFileUri = reqFileUri.appendSegment(CaseUtils.CASE_REQUIREMENTS_DIR);

				// Create DCRYPPS folder if it doesn't exist
				IFolder dcryppsFolder = reqFolder.getFolder("DCRYPPS");
				if (!dcryppsFolder.exists()) {
					try {
						dcryppsFolder.create(false, true, new NullProgressMonitor());
						reqFileUri = reqFileUri.appendSegment("DCRYPPS");
					} catch (CoreException e) {
						System.out.println("DCRYPPS folder could not be created.");
						e.printStackTrace();
					}
				} else {
					reqFileUri = reqFileUri.appendSegment("DCRYPPS");
				}
			}

			final String filename = "Requirements" + (header.has(DATE) ? "-" + header.get(DATE).getAsString() : "")
					+ ".json";
			reqFileUri = reqFileUri.appendSegment(filename);
			reqFile = Filesystem.getFile(reqFileUri);
			Filesystem.writeFile(reqFile, gson.toJson(contents).getBytes());

		} catch (Exception e) {
			e.printStackTrace();
			BriefcaseNotifier.println("Unable to write requirements to filesystem.");
			return null;
		}

		return reqFile;
	}

}
