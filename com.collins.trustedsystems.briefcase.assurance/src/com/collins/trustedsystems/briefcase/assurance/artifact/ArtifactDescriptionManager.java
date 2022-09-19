package com.collins.trustedsystems.briefcase.assurance.artifact;

import java.io.File;
import java.io.FileReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
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
import com.collins.trustedsystems.briefcase.util.Filesystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	public final static String SELECT_EVALUATION = "<Select Evaluation>";
	public final static String EXISTS = "exists";
	public final static String REGEX = "regex";
	public final static String ARGS = "args";

	private final static String DEFAULT_ARTIFACT_DESCRIPTION_FILE_NAME = "Artifacts.json";
	private final static String REVIEW_DOCUMENTS_FOLDER = "Review_Documents";

	private final static String[] claims = { AssuranceType.SECURITY_REQUIREMENTS_REVIEW,
			AssuranceType.AGREE_RESULTS_REVIEW, AssuranceType.REQUIREMENTS_STATED_IN_TERMS_OF_INTERFACE,
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
	private JsonObject artifactDescriptionFile = new JsonObject();
	private int hashcode = artifactDescriptionFile.hashCode();
	private int claimIndex = -1;

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
		parentShell.setText("Assurance Artifact Description Manager");
		this.project = project;

	}

	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(4, true);
		container.setLayout(layout);

		createArtifactDescriptionFileField(container);
		createClaimsField(container);
		createArtifactLocationField(container);
		createEvaluationField(container);
		createRegexField(container);

		populateClaims();

		return area;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Close", true);
	}

	@Override
	protected void okPressed() {
		updateAndSave();
		close();
	}

	private void createArtifactDescriptionFileField(Composite container) {

		final Label lblArtifactDescriptionFile = new Label(container, SWT.NONE);
		lblArtifactDescriptionFile.setText("Artifact description file:");
		lblArtifactDescriptionFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		final Button newButton = new Button(container, SWT.PUSH);
		newButton.setText("New");
		newButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				updateAndSave();

				lstClaims.removeAll();
				// Initialize artifact definition object
				for (String claim : claims) {
					lstClaims.add(claim);
					initializeArtifactDescription(claim);
				}
				hashcode = artifactDescriptionFile.hashCode();

				lstClaims.setSelection(0);
				claimIndex = 0;
				lstClaims.notifyListeners(SWT.Selection, null);

			}
		});
		newButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		txtArtifactDescriptionFile = new Text(container, SWT.BORDER);
		txtArtifactDescriptionFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		txtArtifactDescriptionFile.setEditable(false);
		txtArtifactDescriptionFile
				.setText(Activator.getDefault().getPreferenceStore().getDefaultString(ARTIFACT_DESCRIPTION_FILE));

		final Button openButton = new Button(container, SWT.PUSH);
		openButton.setText("Open...");
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final FileDialog fileDialog = new FileDialog(getParentShell());
				fileDialog.setText("Select artifact description file");
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
				artifactDescriptionFile = jsonFile.getAsJsonObject();
				hashcode = artifactDescriptionFile.hashCode();
				lstClaims.removeAll();
				// Make sure all the claims have entries
				for (String claim : claims) {
					lstClaims.add(claim);
					if (!artifactDescriptionFile.has(claim)) {
						initializeArtifactDescription(claim);
					}
				}

				// Add filename to textbox
				txtArtifactDescriptionFile.setText(filename);

				// Update preference store
				Activator.getDefault().getPreferenceStore().setValue(ARTIFACT_DESCRIPTION_FILE, filename);

				lstClaims.setSelection(0);
				claimIndex = -1;
				lstClaims.notifyListeners(SWT.Selection, null);

			}
		});
		openButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	private void createClaimsField(Composite container) {

		final Label lblClaims = new Label(container, SWT.NONE);
		lblClaims.setText("Claims:");
		lblClaims.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));

		lstClaims = new List(container, SWT.BORDER | SWT.V_SCROLL);
		lstClaims.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 4, 5));
		lstClaims.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (claimIndex >= 0) {
					updateArtifactDescription(lstClaims.getItem(claimIndex));
				}
				claimIndex = lstClaims.getSelectionIndex();
				txtArtifactLocation.setText(artifactDescriptionFile.get(lstClaims.getItem(claimIndex))
						.getAsJsonObject()
						.get(ARTIFACT_FILE_LOCATION)
						.getAsString());
				cboEvaluation.setText(artifactDescriptionFile.get(lstClaims.getItem(claimIndex))
						.getAsJsonObject()
						.get(EVALUATION)
						.getAsJsonObject()
						.get(TYPE)
						.getAsString());
				cboEvaluation.notifyListeners(SWT.Selection, null);
			}
		});

	}

	private void createArtifactLocationField(Composite container) {

		final Label lblArtifactLocation = new Label(container, SWT.NONE);
		lblArtifactLocation.setText("Artifact location:");
		lblArtifactLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		txtArtifactLocation = new Text(container, SWT.BORDER);
		txtArtifactLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtArtifactLocation.setEditable(false);

		final Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final FileDialog fileDialog = new FileDialog(getParentShell());
				fileDialog.setText("Select artifact file");
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
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	}

	private void createEvaluationField(Composite container) {

		final Label lblEvaluation = new Label(container, SWT.NONE);
		lblEvaluation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblEvaluation.setText("Evaluation:");

		cboEvaluation = new Combo(container, SWT.BORDER);
		cboEvaluation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		cboEvaluation.add("<Select Evaluation>");
		cboEvaluation.add(REGEX);
		cboEvaluation.add(EXISTS);

		cboEvaluation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (SELECT_EVALUATION.equals(cboEvaluation.getText())) {
					return;
				} else if (REGEX.equals(cboEvaluation.getText())) {
					lblRegex.setVisible(true);
					txtRegex.setVisible(true);
					String regex = "";
					if (artifactDescriptionFile.get(lstClaims.getItem(claimIndex))
							.getAsJsonObject()
							.get(EVALUATION)
							.getAsJsonObject()
							.has(REGEX)) {
						regex = artifactDescriptionFile.get(lstClaims.getItem(claimIndex))
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
					if (artifactDescriptionFile.get(lstClaims.getItem(claimIndex))
							.getAsJsonObject()
							.get(EVALUATION)
							.getAsJsonObject()
							.has(ARGS)) {
						final JsonArray argArray = artifactDescriptionFile.get(lstClaims.getItem(claimIndex))
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
					lblArgs.setVisible(false);
					txtArgs.setVisible(false);
				}
			}
		});

		cboEvaluation.select(0);
		cboEvaluation.notifyListeners(SWT.Selection, null);

	}

	private void createRegexField(Composite container) {

		lblRegex = new Label(container, SWT.NONE);
		lblRegex.setText("    Regular Expression:");
		lblRegex.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblRegex.setVisible(false);

		txtRegex = new Text(container, SWT.BORDER);
		txtRegex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		txtRegex.setVisible(false);
		txtRegex.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				super.mouseHover(e);
				txtRegex.setToolTipText(txtRegex.getText());
			}
		});

		lblArgs = new Label(container, SWT.NONE);
		lblArgs.setText("    Claim argument indices:");
		lblArgs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblArgs.setVisible(false);

		txtArgs = new Text(container, SWT.BORDER);
		txtArgs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtArgs.setVisible(false);
		txtArgs.setToolTipText("Comma-separated indices of Resolute claim arguments to use in regex");
		new Label(container, SWT.NONE);

	}

	private void initializeArtifactDescription(String claimName) {
		final JsonObject claim = new JsonObject();
		claim.addProperty(ARTIFACT_FILE_LOCATION, "");
		final JsonObject evaluation = new JsonObject();
		evaluation.addProperty(TYPE, "");
		claim.add(EVALUATION, evaluation);
		artifactDescriptionFile.add(claimName, claim);
	}

	private void updateArtifactDescription(String claimName) {
		final JsonObject claim = new JsonObject();
		claim.addProperty(ARTIFACT_FILE_LOCATION, txtArtifactLocation.getText());
		final JsonObject evaluation = new JsonObject();
		evaluation.addProperty(TYPE, cboEvaluation.getText());
		switch (cboEvaluation.getText()) {
		case REGEX:
			evaluation.addProperty(REGEX, txtRegex.getText());
			final JsonArray argsArray = new JsonArray();
			for (String arg : txtArgs.getText().split(",")) {
				try {
					if (!arg.isBlank()) {
						argsArray.add(Integer.parseInt(arg));
					}
				} catch (Exception e) {
					org.osate.ui.dialogs.Dialog.showError("Artifact Description Manager",
							"Args malformed for " + claimName);
					return;
				}
			}
			evaluation.add(ARGS, argsArray);
			break;
		case EXISTS:
			break;
		}
		claim.add(EVALUATION, evaluation);
		artifactDescriptionFile.add(claimName, claim);
	}

	private void updateAndSave() {
		if (claimIndex >= 0) {
			updateArtifactDescription(lstClaims.getItem(claimIndex));
		}
		if (artifactDescriptionFile.hashCode() != hashcode) {
			if (org.osate.ui.dialogs.Dialog.askQuestion("Artifact Description Manager",
					"Would you like to save your changes?")) {
				if (txtArtifactDescriptionFile.getText().isBlank()) {
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
				}
				final URI uri = URI.createURI(txtArtifactDescriptionFile.getText());
				final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();
				final IFile file = Filesystem.getFile(uri);
				Filesystem.writeFile(file, gson.toJson(artifactDescriptionFile).getBytes());

				hashcode = artifactDescriptionFile.hashCode();
			}
		}
	}

	private void populateClaims() {
		for (String claim : claims) {
			lstClaims.add(claim);
			initializeArtifactDescription(claim);
		}
		lstClaims.setSelection(0);
		claimIndex = 0;
	}

}
