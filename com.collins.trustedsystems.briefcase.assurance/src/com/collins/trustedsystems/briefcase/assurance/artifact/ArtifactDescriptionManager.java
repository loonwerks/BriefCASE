package com.collins.trustedsystems.briefcase.assurance.artifact;

import java.io.File;
import java.io.FileReader;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collins.trustedsystems.briefcase.assurance.Activator;
import com.collins.trustedsystems.briefcase.assurance.AssuranceType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ArtifactDescriptionManager extends Dialog {

	public final static String ARTIFACT_DESCRIPTION_FILE = "artifactDescriptionFile";
	public final static String ARTIFACT_FILE_LOCATION = "file_location";
	public final static String EVALUATION = "evaluation";
	public final static String TYPE = "type";
	public final static String EXISTS = "exists";
	public final static String REGEX = "regex";
	public final static String ARGS = "args";

	private final static String DEFAULT_ARTIFACT_DESCRIPTION_FILE_NAME = "Artifacts.json";
	private final static String REVIEW_DOCUMENTS_FOLDER = "Review_Documents";

	private final static String[] claims = { AssuranceType.REQUIREMENTS_STATED_IN_TERMS_OF_INTERFACE,
			AssuranceType.TOOL_GENERATED_APIS_CORRECTLY_DERIVED_FROM_MODEL,
			AssuranceType.APPLICATION_CODE_USES_TOOL_GENERATED_APIS,
			AssuranceType.LIBRARY_USAGE_HAS_NO_EFFECT_OUTSIDE_OF_COMPONENT_BOUNDARY,
			AssuranceType.APPLICATION_CODE_RUNS_TO_COMPLETION,
			AssuranceType.COMPONENT_OUTPUT_SATISFIES_CONTRACT_ON_EVERY_DISPATCH,
			AssuranceType.COMPONENT_OUTPUT_SATISFIES_INFORMATION_FLOW_SPECIFICATIONS_ON_EVERY_DISPATCH,
			AssuranceType.COMPONENT_EXECUTION_TIME_CONFORMS_TO_SPECIFIED_WCET_ON_EVERY_DISPATCH,
			AssuranceType.COMPONENT_INFRASTRUCTURE_CODE_SATISFIES_AADL_SEMANTICS,
			AssuranceType.IDENTIFIABLE_COMPONENT_PLATFORM_DEPLOYMENT,
			AssuranceType.APPLICATION_INFRASTRUCTURE_DEPLOYMENT_INTERFACE_IDENTIFICATION,
			AssuranceType.PORT_CORRESPONDENCE_TRACEABILITY, AssuranceType.COMPONENT_DISPATCH,
			AssuranceType.INFRASTRUCTURE_APPLICATION_NON_INTERFERENCE,
			AssuranceType.SPECIFIED_INFORMATION_FLOW_DIRECTIONALITY_ENFORCED_BY_DEPLOYMENT,
			AssuranceType.DEPLOYED_COMPONENT_SATISFIES_FUNCTIONAL_REQUIREMENTS,
			AssuranceType.DEPLOYED_COMPONENT_SATISFIES_INTRA_COMPONENT_FLOWS,
			AssuranceType.PLATFORM_COMPONENTS_GUARANTEE_REQUIRED_PROPERTIES };

	private final IProject project;
	private JsonObject artifactDefinitionFile = new JsonObject();

	private Text txtArtifactDescriptionFile = null;
	private List lstClaims = null;
	private Text txtArtifactLocation = null;
	private Combo cboEvaluation = null;
	private Label lblRegex = null;
	private Text txtRegex = null;
	private Label lblArgs = null;
	private Text txtArgs = null;

	public ArtifactDescriptionManager(Shell parentShell, IProject project) {
		super(parentShell);
//		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		parentShell.setText("Assurance Artifact Description Manager");
		this.project = project;

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createArtifactDescriptionFileField(area);
		createClaimsField(area);
//		createSeparator(area);
		createArtifactLocationField(area);
		createEvaluationField(area);
		createRegexField(area);
//		createArgsField(area);
//		createButton(area, "Apply");

		populateClaims();

		return area;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, "Close", true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.CLOSE_ID == buttonId) {
			close();
		}
	}

	private void createArtifactDescriptionFileField(Composite container) {

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		final Label label = new Label(container, SWT.NONE);
		label.setText("Artifact description file");

		final Button newButton = new Button(container, SWT.PUSH);
		newButton.setLayoutData(container);
		newButton.setText("New...");
		newButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final FileDialog fileDialog = new FileDialog(getParentShell(), SWT.SAVE);
				fileDialog.setText("Choose artifact description file location");
				fileDialog.setFileName(DEFAULT_ARTIFACT_DESCRIPTION_FILE_NAME);
				fileDialog.setFilterExtensions(new String[] { "*.json", "*.*" });
				fileDialog.setFilterPath(project.getLocation().append(REVIEW_DOCUMENTS_FOLDER).toOSString());
				if (fileDialog.open() == null) {
					return;
				}
				String filename = fileDialog.getFilterPath();
				if (!filename.isEmpty()) {
					filename = filename.concat(File.separator);
				}
				filename = filename + fileDialog.getFileName();

				// Add filename to textbox
				txtArtifactDescriptionFile.setText(filename);

				// Update preference store
				Activator.getDefault().getPreferenceStore().setValue(ARTIFACT_DESCRIPTION_FILE, filename);

				// Initialize artifact definition object
				for (String claim : claims) {
					initializeArtifactDefinition(claim);
				}

//				// Populate Claims textbox
//				populateClaims();
				lstClaims.select(0);

			}
		});
		newButton.setLayoutData(dataInfoField);

		txtArtifactDescriptionFile = new Text(container, SWT.BORDER);
		txtArtifactDescriptionFile.setLayoutData(dataInfoField);
		txtArtifactDescriptionFile.setEditable(false);

		final Button openButton = new Button(container, SWT.PUSH);
		openButton.setLayoutData(container);
		openButton.setText("Open...");
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final FileDialog fileDialog = new FileDialog(getParentShell());
				fileDialog.setText("Select artifact description file");
//				fileDialog.setFileName(DEFAULT_ARTIFACT_DESCRIPTION_FILE_NAME);
				fileDialog.setFilterExtensions(new String[] { "*.json", "*.*" });
				fileDialog.setFilterPath(project.getLocation().append(REVIEW_DOCUMENTS_FOLDER).toOSString());
				if (fileDialog.open() == null) {
					return;
				}

				String filename = fileDialog.getFilterPath();
				if (!filename.isEmpty()) {
					filename = filename.concat(File.separator);
				}
				filename = filename + fileDialog.getFileName();

				// Load file
				JsonElement jsonFile = null;
				try {
					final JsonReader jsonReader = new JsonReader(new FileReader(new File(filename)));
					final JsonParser jsonParser = new JsonParser();
					jsonFile = jsonParser.parse(jsonReader);
					jsonReader.close();
				} catch (Exception e) {
					org.osate.ui.dialogs.Dialog.showError("Artifact Description Manager",
							"Unable to read artifact definition file.");
					return;
				}

				if (!jsonFile.isJsonObject()) {
					org.osate.ui.dialogs.Dialog.showError("Artifact Description Manager",
							"Malformed artifact definition file.");
					return;
				}
				artifactDefinitionFile = jsonFile.getAsJsonObject();
				// Make sure all the claims have entries
				for (String claim : claims) {
					if (!artifactDefinitionFile.has(claim)) {
						initializeArtifactDefinition(claim);
					}
				}

				// Add filename to textbox
				txtArtifactDescriptionFile.setText(filename);

				// Update preference store
				Activator.getDefault().getPreferenceStore().setValue(ARTIFACT_DESCRIPTION_FILE, filename);

//				// Populate Claims Artifact Description File textbox
//				populateClaims();
				lstClaims.select(0);

			}
		});
		openButton.setLayoutData(dataInfoField);
	}

	private void createClaimsField(Composite container) {

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		final Label label = new Label(container, SWT.NONE);
		label.setText("Claims");

		lstClaims = new List(container, SWT.BORDER | SWT.V_SCROLL);
		lstClaims.setLayoutData(dataInfoField);
		lstClaims.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				txtArtifactLocation.setText(artifactDefinitionFile.get(lstClaims.getSelection()[0])
						.getAsJsonObject()
						.get(ARTIFACT_FILE_LOCATION)
						.getAsString());
				cboEvaluation.setText(artifactDefinitionFile.get(lstClaims.getSelection()[0])
						.getAsJsonObject()
						.get(EVALUATION)
						.getAsJsonObject()
						.get(TYPE)
						.getAsString());
			}
		});

	}

	private void createArtifactLocationField(Composite container) {

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		final Label label = new Label(container, SWT.NONE);
		label.setText("Artifact location:");

		txtArtifactLocation = new Text(container, SWT.BORDER);
		txtArtifactLocation.setLayoutData(dataInfoField);
		txtArtifactLocation.setEditable(false);

		final Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setLayoutData(container);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final FileDialog fileDialog = new FileDialog(getParentShell());
				fileDialog.setText("Select artifact file");
//				fileDialog.setFileName(DEFAULT_ARTIFACT_DESCRIPTION_FILE_NAME);
				fileDialog.setFilterExtensions(new String[] { "*.json", "*.*" });
				fileDialog.setFilterPath(project.getLocation().append(REVIEW_DOCUMENTS_FOLDER).toOSString());
				if (fileDialog.open() == null) {
					return;
				}

				String filename = fileDialog.getFilterPath();
				if (!filename.isEmpty()) {
					filename = filename.concat(File.separator);
				}
				filename = filename + fileDialog.getFileName();

				// Add filename to textbox
				txtArtifactLocation.setText(filename);

			}
		});
		browseButton.setLayoutData(dataInfoField);
	}

	private void createEvaluationField(Composite container) {

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		final Label label = new Label(container, SWT.NONE);
		label.setText("Evaluation:");

		cboEvaluation = new Combo(container, SWT.BORDER);
		cboEvaluation.setLayoutData(dataInfoField);
		cboEvaluation.add("<Select Evaluation>");
		cboEvaluation.add(REGEX);
		cboEvaluation.add(EXISTS);

		cboEvaluation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (REGEX.equals(cboEvaluation.getText())) {
					lblRegex.setVisible(true);
					txtRegex.setVisible(true);
					String regex = "";
					if (artifactDefinitionFile.get(lstClaims.getSelection()[0])
							.getAsJsonObject()
							.get(EVALUATION)
							.getAsJsonObject()
							.has(REGEX)) {
						regex = artifactDefinitionFile.get(lstClaims.getSelection()[0])
								.getAsJsonObject()
								.get(EVALUATION)
								.getAsJsonObject()
								.get(REGEX)
								.getAsString();
					}
					txtRegex.setText(regex);
					lblArgs.setVisible(true);
					txtArgs.setVisible(true);
					String args = "";
					if (artifactDefinitionFile.get(lstClaims.getSelection()[0])
							.getAsJsonObject()
							.get(EVALUATION)
							.getAsJsonObject()
							.has(ARGS)) {
						JsonArray argArray = artifactDefinitionFile.get(lstClaims.getSelection()[0])
								.getAsJsonObject()
								.get(EVALUATION)
								.getAsJsonObject()
								.get(ARGS)
								.getAsJsonArray();
						for (int i = 0; i < argArray.size(); ++i) {
							args += argArray.get(i).getAsInt();
							if (i < argArray.size() - 1) {
								args += ",";
							}
						}
					}
					txtArgs.setText(args);
				} else {
					lblRegex.setVisible(false);
					txtRegex.setVisible(false);
//					txtRegex.setText("");
					lblArgs.setVisible(false);
					txtArgs.setVisible(false);
//					txtArgs.setText("");
				}
			}
		});

		cboEvaluation.select(0);

	}

	private void createRegexField(Composite container) {

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		lblRegex = new Label(container, SWT.NONE);
		lblRegex.setText("Regular Expression:");
		lblRegex.setVisible(false);

		txtRegex = new Text(container, SWT.BORDER);
		txtRegex.setLayoutData(dataInfoField);
		txtRegex.setVisible(false);

		lblArgs = new Label(container, SWT.NONE);
		lblArgs.setText("Claim argument indices");
		lblArgs.setVisible(false);

		txtArgs = new Text(container, SWT.BORDER);
		txtArgs.setLayoutData(dataInfoField);
		txtArgs.setVisible(false);
		txtArgs.setToolTipText("Comma-separated indices of Resolute claim arguments to use in regex");

	}

	private void initializeArtifactDefinition(String claimName) {
		final JsonObject claim = new JsonObject();
		claim.addProperty(ARTIFACT_FILE_LOCATION, "");
		final JsonObject evaluation = new JsonObject();
		evaluation.addProperty(TYPE, "");
		claim.add(EVALUATION, evaluation);
		artifactDefinitionFile.add(claimName, claim);
	}

	private void populateClaims() {
		for (String claim : claims) {
			lstClaims.add(claim);
		}
		lstClaims.select(0);
	}

}
