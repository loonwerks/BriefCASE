package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RunDcryppsHandler extends AadlHandler {

	private final static String DCRYPPS_GET_REQUIREMENTS_ENDPOINT = "http://localhost:8888/routers/DCRYPPSRoute/getRequirements";
	private final static String JOB_NAME = "Running DCRYPPS";
	private final static String REQ_FILE_NAME = "DCRYPPS_Cyber_Requirements.json";

	private final static String DESIRABLE_PROPERTIES = "desirableProperties";
	private final static String ATTACKER_DESCRIPTION = "attackerDescription";
	private final static String CLASS_DICTIONARY = "classDictionary";
	private final static String TOOL = "tool";
	private final static String PROJECT = "project";
	private final static String IMPLEMENTATION = "implementation";
	private final static String DATE = "date";
	private final static String HASH = "hash";
	private final static String MODEL_UNITS = "modelUnits";
	private final static String MODEL = "model";
	private final static String REQUIREMENT = "requirement";
	private final static String REQUIREMENTS = "requirements";

	@Override
	protected void runCommand(URI uri) {

		// Check if a component implementation is selected
		final EObject eObj = getEObject(uri);
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
		header.addProperty(PROJECT, project.getName());
		header.addProperty(IMPLEMENTATION, ci.getQualifiedName());
		header.addProperty(DATE, System.currentTimeMillis());

		try {
			// Generate json
			final JsonElement json = Aadl2Json.generateJson(AadlUtil.getContainingPackage(ci), null,
					AgreePrintOption.BOTH);
			header.addProperty(HASH, Integer.toString(json.toString().hashCode()));
			header.add(MODEL_UNITS, json);

		} catch (Exception e) {
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
		} else if (!inputs.has(CLASS_DICTIONARY)) {
			Dialog.showError("Run DCRYPPS", "Component implementation " + ci.getName()
					+ " must include a JSON annex containing the " + CLASS_DICTIONARY + " object.");
			return false;
		}

		// TODO: Remove after Tamas fixes bug
		for (Map.Entry<String, JsonElement> input : inputs.entrySet()) {
			request.add(input.getKey(), input.getValue());
		}

		final JsonObject results = postDcryppsRequest(request.toString());
//		writeRequirementsFile(project, request);
		// TODO: Check results status
		if (!checkResults(results)) {
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
//		Set<String> inputs = new HashSet<>();
		JsonObject inputs = new JsonObject();
		for (AnnexSubclause annexSubclause : ci.getOwnedAnnexSubclauses()) {
			if (annexSubclause.getName().equalsIgnoreCase("json")) {
//				final DefaultAnnexSubclause defaultAnnexSubclause = (DefaultAnnexSubclause) annexSubclause;
//				final JsonAnnexSubclause jsonAnnexSubclause = (JsonAnnexSubclause) defaultAnnexSubclause.getParsedAnnexSubclause();
				JsonElement jsonElement = JsonTranslate.genAnnexSubclause(annexSubclause);
				if (jsonElement instanceof JsonObject) {
					return jsonElement.getAsJsonObject();
				}
//				JsonAnnexElement jsonAnnexElement = jsonAnnexSubclause.getJsonAnnexElement();
//				if (jsonAnnexElement instanceof JsonAnnexObject) {
//					for (JsonAnnexMember jsonAnnexMember : ((JsonAnnexObject) jsonAnnexElement).getJsonAnnexMembers()) {
//						inputs.add(jsonAnnexMember.getKey().getValue().replace("\"", ""));
//
//					}
//				}
				break;
			}
		}
		return inputs;
	}

	private JsonObject postDcryppsRequest(String requestBody) {

		final CloseableHttpClient httpClient = HttpClients.createDefault();
		final HttpPost httpPost = new HttpPost(DCRYPPS_GET_REQUIREMENTS_ENDPOINT);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
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

				return responseJsonObject;
			} else {
				System.out.println("Error code " + statusCode);
				Dialog.showError("Run DCRYPPS", "DCRYPPS returned error code " + statusCode + ".");
			}
		} catch (IOException e) {
			Dialog.showError("Run DCRYPPS", "Unable to connect to DCRYPPS.\n\n" + e.getMessage());
		}
		return null;

	}

	private boolean checkResults(JsonObject results) {
		if (results == null) {
			return false;
		}
		return true;
	}

	private IFile writeRequirementsFile(IProject project, JsonObject header, JsonObject reqs) {

		final JsonObject contents = header;
		contents.addProperty(TOOL, "DCRYPPS");

		final JsonArray requirements = new JsonArray();
		if (!(reqs.has(REQUIREMENTS) && reqs.get(REQUIREMENTS).isJsonArray())) {
			for (JsonElement reqElement : reqs.get(REQUIREMENTS).getAsJsonArray()) {
				if (reqElement.isJsonObject()) {
					final JsonObject req = reqElement.getAsJsonObject();
					if (req.has(REQUIREMENT) && req.get(REQUIREMENT).isJsonArray()) {

					}
				}
			}
		}
		contents.add(REQUIREMENTS, requirements);

		final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();

		IFile reqFile = null;
		try {

			// Create Requirements folder if it doesn't exist
			IFolder reqFolder = CaseUtils.createRequirementsFolder(project);

			final URI reqFileUri = URI.createURI(reqFolder.getFullPath().toString()).appendSegment(REQ_FILE_NAME);
			reqFile = Filesystem.getFile(reqFileUri);
			Filesystem.writeFile(reqFile, gson.toJson(reqs).getBytes());

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to write requirements to filesystem.");
			return null;
		}

		return reqFile;
	}

}
