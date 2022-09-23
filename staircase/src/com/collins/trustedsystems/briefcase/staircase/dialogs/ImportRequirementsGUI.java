package com.collins.trustedsystems.briefcase.staircase.dialogs;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Connection;
import org.osate.aadl2.Subcomponent;

import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;

public class ImportRequirementsGUI extends Dialog {

	public final static int IMPORT = 0;
	public final static int MANAGE = 1;

	private int returnCode = SWT.OK;

	final private List<CyberRequirement> requirements = new ArrayList<CyberRequirement>();

	final Shell shlReqManager;
	final Table tblReqBrowser;
	final Combo cmbStatus;
	final Text txtID;
	final Text txtDesc;
	final Button btnFormalize;
	final Text txtReason;
	final Label lblContext2;
	final Label lblGenTool2;
	final Label lblType2;

	final Set<String> reqIds = new HashSet<String>();

	final List<String> status = Arrays.asList(CyberRequirement.toDo, CyberRequirement.add,
			CyberRequirement.omit);

	private int oldIndex = -1;

	public ImportRequirementsGUI(Shell parent) {
		this(parent, 0); // your default style bits go here (not the Shell's style bits)
	}

	public ImportRequirementsGUI(Shell parent, int style) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		shlReqManager = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);

		shlReqManager.setMinimumSize(1200, 420);
		shlReqManager.setSize(1200, 420);

		if (style == IMPORT) {
			shlReqManager.setText("Import Cyber Security Requirements");
		} else {
			shlReqManager.setText("Manage Cyber Security Requirements");
		}
		final GridLayout reqMgrLayout = new GridLayout(1, true);
		reqMgrLayout.verticalSpacing = 10;
		shlReqManager.setLayout(reqMgrLayout);

		final Composite cmpReqManager = new Composite(shlReqManager, SWT.NONE);
		cmpReqManager.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		cmpReqManager.setLayout(new GridLayout(2, true));

		final Composite grpReqBrowser = new Composite(cmpReqManager, SWT.NONE);
		grpReqBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grpReqBrowser.setLayout(new GridLayout(1, true));

		final Label lblReqBrowser = new Label(grpReqBrowser, SWT.NONE);
		lblReqBrowser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		final FontDescriptor boldDescriptorReqBrowser = FontDescriptor.createFrom(lblReqBrowser.getFont())
				.setStyle(SWT.BOLD);
		final Font boldFontReqBrowser = boldDescriptorReqBrowser.createFont(lblReqBrowser.getDisplay());
		lblReqBrowser.setFont(boldFontReqBrowser);
		lblReqBrowser.setText("Requirements Browser");
		lblReqBrowser.addDisposeListener(e -> boldFontReqBrowser.dispose());

		final ScrolledComposite reqBrowser = new ScrolledComposite(grpReqBrowser, SWT.BORDER);
		reqBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		reqBrowser.setLayout(new GridLayout(1, true));
		reqBrowser.setExpandHorizontal(true);
		reqBrowser.setExpandVertical(true);

		tblReqBrowser = new Table(reqBrowser, SWT.FULL_SELECTION);
		tblReqBrowser.setLinesVisible(true);
		tblReqBrowser.setHeaderVisible(true);

		final TableColumn tblclmnStatus = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnStatus.setWidth(45);
		tblclmnStatus.setText("Status");

		final TableColumn tblclmnType = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnType.setWidth(99);
		tblclmnType.setText("Type");

		final TableColumn tblclmnId = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");

		final TableColumn tblclmnShortDesciption = new TableColumn(tblReqBrowser, SWT.LEFT);
		tblclmnShortDesciption.setWidth(309);
		tblclmnShortDesciption.setText("Short Desciption");
		reqBrowser.setContent(tblReqBrowser);

		final Composite grpViewEditReq = new Composite(cmpReqManager, SWT.NONE);
		grpViewEditReq.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grpViewEditReq.setLayout(new GridLayout(1, true));

		final Label lblViewEdit = new Label(grpViewEditReq, SWT.NONE);
		lblViewEdit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		final FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblViewEdit.getFont()).setStyle(SWT.BOLD);
		final Font boldFont = boldDescriptor.createFont(lblViewEdit.getDisplay());
		lblViewEdit.setFont(boldFont);
		lblViewEdit.setText("View/Edit Requirement");
		lblViewEdit.addDisposeListener(e -> boldFont.dispose());

		final ScrolledComposite scrolledComposite = new ScrolledComposite(grpViewEditReq,
				SWT.BORDER);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setLayout(new GridLayout(1, true));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(5, true));

		final Label lblStatus = new Label(composite, SWT.NONE);
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setText("Status");

		cmbStatus = new Combo(composite, SWT.NONE);
		cmbStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbStatus.setItems(this.status.toArray(new String[0]));
		cmbStatus.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cmbStatus.getText().equalsIgnoreCase(CyberRequirement.omit)) {
					txtReason.setEnabled(true);
				} else {
					txtReason.setEnabled(false);
					txtReason.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		final Label lblGenTool = new Label(composite, SWT.CENTER);
		lblGenTool.setText("Generation Tool");

		lblGenTool2 = new Label(composite, SWT.BORDER);
		lblGenTool2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		scrolledComposite.setContent(composite);

		final Label lblType = new Label(composite, SWT.CENTER);
		lblType.setText("Type");

		lblType2 = new Label(composite, SWT.BORDER);
		lblType2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(new Point(600, 50));

		final Label lblID = new Label(composite, SWT.CENTER);
		lblID.setText("ID");

		txtID = new Text(composite, SWT.BORDER);
		txtID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		final Label lblDesc = new Label(composite, SWT.CENTER);
		lblDesc.setText("Descrption");

		txtDesc = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 4));

		new Label(composite, SWT.NONE);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		final Label lblContext = new Label(composite, SWT.CENTER);
		lblContext.setText("Component");

		lblContext2 = new Label(composite, SWT.BORDER);
		lblContext2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		final Label lblFormalize = new Label(composite, SWT.NONE);
		lblFormalize.setText("Formalize");

		btnFormalize = new Button(composite, SWT.CHECK);
		btnFormalize.setText("");
		btnFormalize.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

		final Label lblReason = new Label(composite, SWT.NONE);
		lblReason.setText("Reason for omission");

		txtReason = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtReason.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 3));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		final Composite cmpButtonBar = new Composite(shlReqManager, SWT.NONE);
		final FillLayout fl_cmpButtonBar = new FillLayout(SWT.HORIZONTAL);
		fl_cmpButtonBar.spacing = 20;
		cmpButtonBar.setLayout(fl_cmpButtonBar);
		cmpButtonBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		createButton(cmpButtonBar, SWT.OK, "        OK        ", false);
		createButton(cmpButtonBar, SWT.CANCEL, "        Cancel        ", true);

		tblReqBrowser.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = tblReqBrowser.getSelectionIndex();

				if (oldIndex == index) {
					return;
				}

				// Save previous requirement
				if (oldIndex >= 0) {
					if (!saveReqChanges()) {
						// error in saving changes
						tblReqBrowser.setSelection(oldIndex);
						return;
					}
				}

				// Update requirement dialog
				final CyberRequirement r = requirements.get(index);
				final String reqId = r.getId();
				cmbStatus.select(getStatusIndex(r.getStatus()));
				if (cmbStatus.getText().equalsIgnoreCase(CyberRequirement.omit)) {
					txtReason.setEnabled(true);
				} else {
					txtReason.setEnabled(false);
					txtReason.setText("");
				}
				lblGenTool2.setText(r.getTool());
				lblType2.setText(r.getType());
				txtID.setText(reqId);
				txtID.setEditable(reqId.isEmpty());
				txtDesc.setText(r.getText());
				txtDesc.setEditable(reqId.isEmpty());
				lblContext2.setText(r.getContext());
				btnFormalize.setSelection(r.getFormalize());
				txtReason.setText(r.getRationale());

				oldIndex = index;

				if (reqId != null && !reqId.isEmpty()) {
					reqIds.remove(reqId);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

	}

	private boolean saveReqChanges() {
		if (cmbStatus.getSelectionIndex() == -1) {
			return true; // nothing selected; therefore, nothing to be saved
		}

		final String newStatus = getStatusString(cmbStatus.getSelectionIndex());
		final String newId = txtID.getText();
		final String newDesc = txtDesc.getText();
		final String newReason = txtReason.getText();

		if (newStatus.equalsIgnoreCase(CyberRequirement.omit)) {
			if (newReason.isEmpty() || newReason.equalsIgnoreCase(CyberRequirement.notApplicable)) {
				org.osate.ui.dialogs.Dialog.showError("Requirements Manager",
					"Requirements that are marked as omitted must provide a rationale for the omission.");
				return false;
			}
		}

		if ((newStatus.equalsIgnoreCase(CyberRequirement.add))) {
			if (newId.isEmpty()) {
				org.osate.ui.dialogs.Dialog.showError("Requirements Manager",
						"Requirement IDs must be assigned before requirements can be imported into the model.");
				return false;
			}
		}

		// Make sure requirement ID starts with a letter and only contains letters, numbers, and underscores
		// (this is for compliance with Resolute)
		if ((newStatus.equalsIgnoreCase(CyberRequirement.add))) {
			if (!newId.matches("^[A-Za-z][A-Za-z0-9_]*")) {
				org.osate.ui.dialogs.Dialog.showError("Requirements Manager", newId
						+ ": Invalid requirement ID. Requirement IDs must begin with a letter and contain only letters, numbers, and underscores.");
				return false;
			}
		}

		if (!newId.isEmpty()) {
			if (reqIds.contains(newId)) {
				org.osate.ui.dialogs.Dialog.showError("Requirements Manager",
					"Duplicate requirement ID: " + newId + ". Requirement IDs must be unique.");
				return false;
			}
		} else {
			reqIds.add(newId);
		}

		final CyberRequirement req = requirements.get(oldIndex);
		req.setStatus(newStatus);
		req.setId(newId);
		req.setText(newDesc);
		req.setFormalize(btnFormalize.getSelection());
		req.setRationale(newReason);
		updateTableItem(oldIndex);

		return true;
	}

	private String getStatusString(int index) {
		return this.status.get(index);
	}

	private int getStatusIndex(String status) {
		return this.status.indexOf(status);
	}

	public int open() {

		populateTable(tblReqBrowser);

		if (!requirements.isEmpty()) {
			shlReqManager.open();
			Display display = getParent().getDisplay();
			while (!shlReqManager.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		return returnCode;
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setData(Integer.valueOf(id));
		button.addSelectionListener(
				widgetSelectedAdapter(event -> buttonPressed(((Integer) event.widget.getData()).intValue())));
		if (defaultButton) {
			final Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		return button;
	}

	private void buttonPressed(int intValue) {
		if (intValue == SWT.OK) {
			okPressed();
		} else if (intValue == SWT.CANCEL) {
			cancelPressed();
		}
	}

	private void populateTable(Table t) {
		if (requirements.isEmpty()) {
			return;
		}

		for (CyberRequirement r : requirements) {
			final TableItem tItem = new TableItem(t, SWT.NONE);
			if (r.getStatus().isEmpty()) {
				r.setStatus(CyberRequirement.toDo);
			}
			tItem.setText(new String[] { r.getStatus(), r.getType(), r.getId(), r.getText() });
		}

		packTable();
	}

	protected void okPressed() {
		// Save changes and exit dialog
		if (!saveReqChanges()) {
			// error in saving changes
			return;
		}
		if (!saveInput()) {
			return;
		}
		returnCode = SWT.OK;
		shlReqManager.close();
	}


	protected void cancelPressed() {
		// Exit dialog without saving
		returnCode = SWT.CANCEL;
		shlReqManager.close();
	}

	private void packTable() {
		for (TableColumn col : tblReqBrowser.getColumns()) {
			col.pack();
		}
	}

	private boolean updateTableItem(int index) {
		if (index < 0 || index >= tblReqBrowser.getItemCount()) {
			return false;
		}
		final CyberRequirement req = requirements.get(index);
		final TableItem tItem = tblReqBrowser.getItem(index);
		tItem.setText(new String[] { req.getStatus(), req.getType(), req.getId(), req.getText() });
		packTable();
		return true;
	}

	private boolean saveInput() {
		for (int i = 0; i < requirements.size(); i++) {
			final CyberRequirement req = requirements.get(i);

			// Check for invalid requirement
			if (!CyberRequirement.toDo.equals(req.getStatus())) {

				// Find the context (component, connection, etc) in the model
				final Classifier contextClassifier = CyberRequirement.getImplementationClassifier(req.getContext());
				if (!(contextClassifier instanceof ComponentImplementation)) {
					return false;
				}
				boolean contextFound = contextExists(req.getContext(), (ComponentImplementation) contextClassifier);

				// Check for invalid context
				if (contextClassifier == null || !contextFound) {
					org.osate.ui.dialogs.Dialog.showError("Requirements Manager", req.getContext()
							+ " could not be found in any AADL file in the project. A requirement context must be valid in order to import requirements into model. This requirement will be de-selected.");
					req.setStatus(CyberRequirement.toDo);
					updateTableItem(i);
					return false;
				}

				// Check for missing requirement ID
				if (req.getId().isEmpty() && req.getStatus() != CyberRequirement.omit) {
					org.osate.ui.dialogs.Dialog.showError("Requirements Manager", req.getType()
							+ " is missing a requirement ID. Requirement IDs must be assigned before requirements can be imported into model. This requirement will be de-selected.");
					// Uncheck this requirement
					req.setStatus(CyberRequirement.toDo);
					updateTableItem(i);
					return false;
				}

			}
		}

		return true;
	}

	public void setRequirements(List<CyberRequirement> newRequirements) {
		if (!this.requirements.isEmpty()) {
			this.requirements.clear();
			this.reqIds.clear();
		}
		this.requirements.addAll(newRequirements);
		this.requirements.forEach(r -> {
			final String id = r.getId();
			if (id != null && !id.isEmpty()) {
				this.reqIds.add(id);
			}
		});
	}

	public List<CyberRequirement> getRequirements() {
		return requirements;
	}

	private boolean contextExists(String context, ComponentImplementation compImpl) {
		// context will be in the form of pkg::comp.impl.<subcomp>.<subcomp>...
		final String[] parts = context.split("\\.");
		if (parts.length <= 1) {
			return false;
		}
		final String compName = parts[0] + "." + parts[1];
		if (!compName.equalsIgnoreCase(compImpl.getQualifiedName())) {
			return false;
		}

		ComponentImplementation ci = compImpl;

		try {
			for (int i = 2; i < parts.length; i++) {
				boolean partFound = false;
				for (Subcomponent subcomp : ci.getOwnedSubcomponents()) {
					if (subcomp.getName().equalsIgnoreCase(parts[i])) {
						ci = subcomp.getComponentImplementation();
						partFound = true;
						break;
					}
				}
				if (!partFound) {
					for (Connection conn : ci.getOwnedConnections()) {
						if (conn.getName().equalsIgnoreCase(parts[i])) {
							partFound = true;
							break;
						}
					}
				}
				if (!partFound) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
