package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.json.export.Aadl2Json;
import com.collins.trustedsystems.briefcase.json.export.AadlTranslate.AgreePrintOption;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RunDcryppsHandler extends AadlHandler {

	private final static String DCRYPPS_GET_REQUIREMENTS_ENDPOINT = "http://localhost/routers/DCRYPPSRoute/getRequirements";
	private final static String JOB_NAME = "Running DCRYPPS";
	private final static String REQ_FILE_NAME = "DCRYPPS_Cyber_Requirements.json";

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
		header.addProperty("project", project.getName());
		header.addProperty("implementation", ci.getQualifiedName());
		header.addProperty("date", System.currentTimeMillis());

		try {
			// Generate json
			final JsonElement json = Aadl2Json.generateJson(AadlUtil.getContainingPackage(ci), null,
					AgreePrintOption.BOTH);
			header.addProperty("hash", json.toString().hashCode());
			header.add("modelUnits", json);

		} catch (Exception e) {
			Dialog.showError("Run DCRYPPS", "Unable to export model to JSON format.");
			return false;
		}

		request.add("model", header);
		final JsonObject results = postDcryppsRequest(request.toString());
		// TODO: Check results status
		if (!checkResults(results)) {
			return false;
		}

		// Save results
		if (writeRequirementsFile(project, results) == null) {
			return false;
		}

		BriefcaseNotifier.notify("DCRYPPS", "DCRYPPS has completed successfully");

		return true;
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

	private IFile writeRequirementsFile(IProject project, JsonObject reqs) {

		Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();

		IFile reqFile = null;
		try {

			// Create Requirements folder if it doesn't exist
			IFolder reqFolder = CaseUtils.createRequirementsFolder(project);

			final URI reqFileUri = URI.createURI(reqFolder.getFullPath().toString()).appendSegment(REQ_FILE_NAME);
			reqFile = Filesystem.getFile(reqFileUri);
			Filesystem.writeFile(reqFile, gson.toJson(reqs).getBytes());

		} catch (Exception e) {
			System.err.println("Unable to write requirements to filesystem.");
			return null;
		}

		return reqFile;
	}

}
