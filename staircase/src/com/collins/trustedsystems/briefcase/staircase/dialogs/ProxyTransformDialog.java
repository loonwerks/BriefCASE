package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.PortSelectorControl.PortDirection;
import com.collins.trustedsystems.briefcase.staircase.handlers.ProxyTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

public class ProxyTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context;
	private Text txtHighProxyComponentName;
	private Text txtHighProxySubcomponentName;
	private PortSelectorControl pscHighProxyPortNames = null;
	private List<Button> btnHighProxyDispatchProtocol = new ArrayList<>();
	private Label lblHighProxyPeriodField;
	private Text txtHighProxyPeriod;
	private Text txtLowProxyComponentName;
	private Text txtLowProxySubcomponentName;
	private PortSelectorControl pscLowProxyPortNames = null;
	private List<Button> btnLowProxyDispatchProtocol = new ArrayList<>();
	private Label lblLowProxyPeriodField;
	private Text txtLowProxyPeriod;
//	private Button btnAddProxiedComponent;
//	private Label lblProxiedComponentName;
//	private Text txtProxiedComponentName;
//	private Label lblProxiedSubcomponentName;
//	private Text txtProxiedSubcomponentName;
	private Combo cboProxyRequirement;
	private String highProxyComponentName = "";
	private String highProxySubcomponentName = "";
	private Map<String, List<String>> highProxyPortNames = new HashMap<>();
	private String highProxyDispatchProtocol = "";
	private String highProxyPeriod = "";
	private String lowProxyComponentName = "";
	private String lowProxySubcomponentName = "";
	private Map<String, List<String>> lowProxyPortNames = new HashMap<>();
	private String lowProxyDispatchProtocol = "";
	private String lowProxyPeriod = "";
//	private boolean addProxiedComponent = false;
//	private String proxiedComponentName = "";
//	private String proxiedSubcomponentName = "";
	private String proxyRequirement = "";

	private List<String> inports = new ArrayList<>();
	private List<String> outports = new ArrayList<>();

	private List<String> requirements = new ArrayList<>();
	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public ProxyTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Proxy Transform");
		setMessage("Enter Proxy Transform details for " + context.getName() + ".", IMessageProvider.NONE);
	}

	public void create(ComponentImplementation context) {
		this.context = context;
		// Provide list of outports and inports that can be connected to the proxies
		outports = ModelTransformUtils.getOutports(context);
		// Provide list of inports that monitor's alert out port can be connected to
		inports = ModelTransformUtils.getInports(context);
		// Populate requirements list
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));
		create();
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(2);
		return size;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);

		final TabFolder folder = new TabFolder(area, SWT.NONE);
		createHighProxyTab(folder);
		createLowProxyTab(folder);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		// Add component between proxies
//		createAddProxiedComponentField(container);
		// Add requirement field
		createRequirementField(container);

		return area;
	}

	private void createHighProxyTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		final TabItem highProxyTab = new TabItem(folder, SWT.NONE);
		highProxyTab.setText("High-side Proxy");

		final Composite namesContainer = new Composite(container, SWT.NONE);
		namesContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		namesContainer.setLayout(new GridLayout(2, false));

		// Proxy high component name
		createHighProxyComponentNameField(namesContainer);
		// Proxy high subcomponent name
		createHighProxySubcomponentNameField(namesContainer);

		final Composite portsContainer = new Composite(container, SWT.NONE);
		portsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		portsContainer.setLayout(new GridLayout(1, false));

		// Proxy high ports
		createHighProxyPortsField(portsContainer);

		final Composite miscContainer = new Composite(container, SWT.NONE);
		miscContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		miscContainer.setLayout(new GridLayout(2, false));

		// Only display dispatch protocol if proxy is a thread
		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation) {
			createHighProxyDispatchProtocolField(miscContainer);
		}

		highProxyTab.setControl(container);
	}

	private void createLowProxyTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		final TabItem lowProxyTab = new TabItem(folder, SWT.NONE);
		lowProxyTab.setText("Low-side Proxy");

		final Composite namesContainer = new Composite(container, SWT.NONE);
		namesContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		namesContainer.setLayout(new GridLayout(2, false));

		// Proxy low component name
		createLowProxyComponentNameField(namesContainer);
		// Proxy low subcomponent name
		createLowProxySubcomponentNameField(namesContainer);

		final Composite portsContainer = new Composite(container, SWT.NONE);
		portsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		portsContainer.setLayout(new GridLayout(1, false));

		// Proxy low ports
		createLowProxyPortsField(portsContainer);

		final Composite miscContainer = new Composite(container, SWT.NONE);
		miscContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		miscContainer.setLayout(new GridLayout(2, false));

		// Only display dispatch protocol if proxy is a thread
		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation) {
			createLowProxyDispatchProtocolField(miscContainer);
		}

		lowProxyTab.setControl(container);
	}

	/**
	 * Creates the input text field for specifying the high proxy component name
	 * @param container
	 */
	private void createHighProxyComponentNameField(Composite container) {
		final Label lblHighProxyComponentNameField = new Label(container, SWT.NONE);
		lblHighProxyComponentNameField.setText("High proxy component type name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtHighProxyComponentName = new Text(container, SWT.BORDER);
		txtHighProxyComponentName.setLayoutData(dataInfoField);
		txtHighProxyComponentName.setText(ProxyTransformHandler.HIGH_PROXY_COMP_TYPE_NAME);
	}

	/**
	 * Creates the input text field for specifying the high proxy subcomponent name
	 * @param container
	 */
	private void createHighProxySubcomponentNameField(Composite container) {
		final Label lblHighProxySubcomponentNameField = new Label(container, SWT.NONE);
		lblHighProxySubcomponentNameField.setText("High proxy subcomponent name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtHighProxySubcomponentName = new Text(container, SWT.BORDER);
		txtHighProxySubcomponentName.setLayoutData(dataInfoField);
		txtHighProxySubcomponentName.setText(ProxyTransformHandler.HIGH_PROXY_SUBCOMP_NAME);
	}

	private void createHighProxyPortsField(Composite container) {
		final Label lblPortNamesField = new Label(container, SWT.NONE);
		lblPortNamesField.setText("High Proxy ports");
		lblPortNamesField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		final List<String> connectionEnds = new ArrayList<>();
//		connectionEnds.add(NO_PORT_SELECTED);
		connectionEnds.addAll(outports);
		pscHighProxyPortNames = new PortSelectorControl(container, PortDirection.INPUT, connectionEnds);
	}

	/**
	 * Creates the input field for selecting the high-side proxy dispatch protocol
	 * @param container
	 */
	private void createHighProxyDispatchProtocolField(Composite container) {
		final Label lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnHighProxyDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblHighProxyPeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtHighProxyPeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtHighProxyPeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		lblHighProxyPeriodField = new Label(container, SWT.NONE);
		lblHighProxyPeriodField.setText("Period");
		lblHighProxyPeriodField.setEnabled(false);
		lblHighProxyPeriodField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtHighProxyPeriod = new Text(container, SWT.BORDER);
		txtHighProxyPeriod.setLayoutData(dataInfoField);
		txtHighProxyPeriod.setEnabled(false);

		btnHighProxyDispatchProtocol.add(btnNoProtocol);
		btnHighProxyDispatchProtocol.add(btnPeriodic);
		btnHighProxyDispatchProtocol.add(btnSporadic);

	}

	/**
	 * Creates the input text field for specifying the low proxy component name
	 * @param container
	 */
	private void createLowProxyComponentNameField(Composite container) {
		final Label lblLowProxyComponentNameField = new Label(container, SWT.NONE);
		lblLowProxyComponentNameField.setText("Low proxy component type name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtLowProxyComponentName = new Text(container, SWT.BORDER);
		txtLowProxyComponentName.setLayoutData(dataInfoField);
		txtLowProxyComponentName.setText(ProxyTransformHandler.LOW_PROXY_COMP_TYPE_NAME);
	}

	/**
	 * Creates the input text field for specifying the low proxy subcomponent name
	 * @param container
	 */
	private void createLowProxySubcomponentNameField(Composite container) {
		final Label lblLowProxySubcomponentNameField = new Label(container, SWT.NONE);
		lblLowProxySubcomponentNameField.setText("Low proxy subcomponent name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		txtLowProxySubcomponentName = new Text(container, SWT.BORDER);
		txtLowProxySubcomponentName.setLayoutData(dataInfoField);
		txtLowProxySubcomponentName.setText(ProxyTransformHandler.LOW_PROXY_SUBCOMP_NAME);
	}

	private void createLowProxyPortsField(Composite container) {
		final Label lblPortNamesField = new Label(container, SWT.NONE);
		lblPortNamesField.setText("Port names");
		lblPortNamesField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		final List<String> connectionEnds = new ArrayList<>();
//		connectionEnds.add(NO_PORT_SELECTED);
		connectionEnds.addAll(inports);
		pscLowProxyPortNames = new PortSelectorControl(container, PortDirection.OUTPUT, connectionEnds);
	}

	/**
	 * Creates the input field for selecting the low-side proxy dispatch protocol
	 * @param container
	 */
	private void createLowProxyDispatchProtocolField(Composite container) {
		final Label lblDispatchProtocolField = new Label(container, SWT.NONE);
		lblDispatchProtocolField.setText("Dispatch protocol");
		lblDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group protocolGroup = new Group(container, SWT.NONE);
		protocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		protocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnLowProxyDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(protocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblLowProxyPeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtLowProxyPeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtLowProxyPeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(protocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(protocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		lblLowProxyPeriodField = new Label(container, SWT.NONE);
		lblLowProxyPeriodField.setText("Period");
		lblLowProxyPeriodField.setEnabled(false);
		lblLowProxyPeriodField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		txtLowProxyPeriod = new Text(container, SWT.BORDER);
		txtLowProxyPeriod.setLayoutData(dataInfoField);
		txtLowProxyPeriod.setEnabled(false);

		btnLowProxyDispatchProtocol.add(btnNoProtocol);
		btnLowProxyDispatchProtocol.add(btnPeriodic);
		btnLowProxyDispatchProtocol.add(btnSporadic);

	}

	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the proxy transform in this design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		cboProxyRequirement = new Combo(container, SWT.BORDER);
		cboProxyRequirement.setLayoutData(dataInfoField);
		cboProxyRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboProxyRequirement.add(r));
		cboProxyRequirement.setText(NO_REQUIREMENT_SELECTED);
	}

//	/**
//	 * Creates the input field for selecting whether to add a component between
//	 * high and low proxies
//	 * @param container
//	 */
//	private void createAddProxiedComponentField(Composite container) {
//		final Label lblComponentField = new Label(container, SWT.NONE);
//		lblComponentField.setText("Add component between proxies");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
//		btnAddProxiedComponent = new Button(container, SWT.CHECK);
//		btnAddProxiedComponent.setSelection(false);
//		btnAddProxiedComponent.setLayoutData(dataInfoField);
//		btnAddProxiedComponent.addListener(SWT.Selection, e -> {
//			if (e.type == SWT.Selection) {
//				lblProxiedComponentName.setEnabled(btnAddProxiedComponent.getSelection());
//				txtProxiedComponentName.setEnabled(btnAddProxiedComponent.getSelection());
//				lblProxiedSubcomponentName.setEnabled(btnAddProxiedComponent.getSelection());
//				txtProxiedSubcomponentName.setEnabled(btnAddProxiedComponent.getSelection());
//				if (btnAddProxiedComponent.getSelection()) {
//					txtProxiedComponentName.setText(ProxyTransformHandler.PROXIED_COMP_TYPE_NAME);
//					txtProxiedSubcomponentName.setText(ProxyTransformHandler.PROXIED_SUBCOMP_NAME);
//				} else {
//					txtProxiedComponentName.setText("");
//					txtProxiedSubcomponentName.setText("");
//				}
//			}
//		});
//	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	/**
	 * Saves information entered into the text fields.  This is needed because the
	 * text fields are disposed when the dialog closes.
	 * @param container
	 */
	private boolean saveInput() {

		final List<Classifier> componentsInPackage = AadlUtil.getContainingPackageSection(context)
				.getOwnedClassifiers();
		final Set<String> portNames = new HashSet<>();

		// High Proxy Component Name
		if (!txtHighProxyComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtHighProxyComponentName.getText())) {
			Dialog.showError("Proxy Transform", "High Proxy component name " + txtHighProxyComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtHighProxyComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtHighProxyComponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Component " + txtHighProxyComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtHighProxyComponentName.setText(
					ModelTransformUtils.getUniqueName(txtHighProxyComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			highProxyComponentName = txtHighProxyComponentName.getText();
		}

		// High Proxy Subcomponent Name
		if (!txtHighProxySubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtHighProxySubcomponentName.getText())) {
			Dialog.showError("Proxy Transform",
					"High Proxy subcomponent instance name " + txtHighProxySubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtHighProxySubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtHighProxySubcomponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Subcomponent " + txtHighProxySubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtHighProxySubcomponentName.setText(ModelTransformUtils
					.getUniqueName(txtHighProxySubcomponentName.getText(), true, context.getAllSubcomponents()));
			return false;
		} else {
			highProxySubcomponentName = txtHighProxySubcomponentName.getText();
		}

		// High Proxy Ports
		highProxyPortNames = pscHighProxyPortNames.getContents();
		for (Map.Entry<String, List<String>> ports : highProxyPortNames.entrySet()) {
			for (int i = 0; i < ports.getValue().size(); ++i) {
				if (ports.getValue().get(i).isEmpty()) {
					Dialog.showError("Proxy Transform",
							"The high-side proxy " + (i == 0 ? "input" : "output")
									+ " port name must be specified in order to establish a connection with "
									+ ports.getKey() + ".");
					return false;
				} else if (!portNames.add(ports.getValue().get(i))) {
					Dialog.showError("Proxy Transform", "The high-side proxy " + (i == 0 ? "input" : "output")
							+ " port name " + ports.getValue().get(i)
							+ " cannot be used since it has already been specified for a different port in this component.");
					return false;
				}
			}
		}

		// High Proxy Dispatch Protocol and Period
		for (Button b : btnHighProxyDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				highProxyDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtHighProxyPeriod.getText().isEmpty()
						|| txtHighProxyPeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					highProxyPeriod = txtHighProxyPeriod.getText();
				} else {
					Dialog.showError("Proxy Transform", "High-side proxy period " + txtHighProxyPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

		// Low Proxy Component Name
		if (!txtLowProxyComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtLowProxyComponentName.getText())) {
			Dialog.showError("Proxy Transform", "Low Proxy component name " + txtLowProxyComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtLowProxyComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtLowProxyComponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Component " + txtLowProxyComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtLowProxyComponentName.setText(
					ModelTransformUtils.getUniqueName(txtLowProxyComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			lowProxyComponentName = txtLowProxyComponentName.getText();
		}

		// Low Proxy Subcomponent Name
		if (!txtLowProxySubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtLowProxySubcomponentName.getText())) {
			Dialog.showError("Proxy Transform",
					"Low Proxy subcomponent instance name " + txtLowProxySubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
		} else if (!txtLowProxySubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtLowProxySubcomponentName.getText()) != null) {
			Dialog.showError("Proxy Transform", "Subcomponent " + txtLowProxySubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtLowProxySubcomponentName.setText(ModelTransformUtils.getUniqueName(txtLowProxySubcomponentName.getText(),
					true, context.getAllSubcomponents()));
			return false;
		} else {
			lowProxySubcomponentName = txtLowProxySubcomponentName.getText();
		}

		// Low Proxy Ports
		lowProxyPortNames = pscLowProxyPortNames.getContents();
		for (Map.Entry<String, List<String>> ports : lowProxyPortNames.entrySet()) {
			for (int i = 0; i < ports.getValue().size(); ++i) {
				if (ports.getValue().get(i).isEmpty()) {
					Dialog.showError("Proxy Transform",
							"The low-side proxy " + (i == 0 ? "input" : "output")
									+ " port name must be specified in order to establish a connection with "
									+ ports.getKey() + ".");
					return false;
				} else if (!portNames.add(ports.getValue().get(i))) {
					Dialog.showError("Proxy Transform", "The low-side proxy " + (i == 0 ? "input" : "output")
							+ " port name " + ports.getValue().get(i)
							+ " cannot be used since it has already been specified for a different port in this component.");
					return false;
				}
			}
		}

		// Low Proxy Dispatch Protocol and Period
		for (Button b : btnLowProxyDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				lowProxyDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtLowProxyPeriod.getText().isEmpty()
						|| txtLowProxyPeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					lowProxyPeriod = txtLowProxyPeriod.getText();
				} else {
					Dialog.showError("Proxy Transform", "Low-side proxy period " + txtLowProxyPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

//		// Add proxied component
//		addProxiedComponent = btnAddProxiedComponent.getSelection();
//		if (addProxiedComponent) {
//			// Proxied component name
//			if (!txtProxiedComponentName.getText().isEmpty()
//					&& !ModelTransformUtils.isValidName(txtProxiedComponentName.getText())) {
//				Dialog.showError("Proxy Transform", "Proxied component name " + txtProxiedComponentName.getText()
//						+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
//				return false;
//			} else if (!txtProxiedComponentName.getText().isEmpty() && AadlUtil
//					.findNamedElementInList(componentsInPackage, txtProxiedComponentName.getText()) != null) {
//				Dialog.showError("Proxy Transform", "Component " + txtProxiedComponentName.getText()
//						+ " already exists in model. Use the suggested name or enter a new one.");
//				txtProxiedComponentName.setText(ModelTransformUtils.getUniqueName(txtProxiedComponentName.getText(),
//						true, componentsInPackage));
//				return false;
//			} else {
//				proxiedComponentName = txtProxiedComponentName.getText();
//			}
//			// Proxied subcomponent name
//			if (!txtProxiedSubcomponentName.getText().isEmpty()
//					&& !ModelTransformUtils.isValidName(txtProxiedSubcomponentName.getText())) {
//				Dialog.showError("Proxy Transform", "Proxied subcomponent name " + txtProxiedSubcomponentName.getText()
//						+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
//				return false;
//			} else if (!txtProxiedSubcomponentName.getText().isEmpty()
//					&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
//							txtProxiedSubcomponentName.getText()) != null) {
//				Dialog.showError("Proxy Transform", "Subcomponent " + txtProxiedSubcomponentName.getText()
//						+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
//				txtProxiedSubcomponentName.setText(ModelTransformUtils
//						.getUniqueName(txtProxiedSubcomponentName.getText(), true, context.getAllSubcomponents()));
//				return false;
//			} else {
//				proxiedSubcomponentName = txtProxiedSubcomponentName.getText();
//			}
//		}

		// Requirement
		proxyRequirement = cboProxyRequirement.getText();
		if (proxyRequirement.equals(NO_REQUIREMENT_SELECTED)) {
			proxyRequirement = "";
		} else if (!requirements.contains(proxyRequirement)) {
			Dialog.showError("Proxy Transform",
					"Proxy requirement " + proxyRequirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

		return true;
	}

	public String getHighProxyComponentName() {
		return highProxyComponentName;
	}

	public String getHighProxySubcomponentName() {
		return highProxySubcomponentName;
	}

	public Map<String, List<String>> getHighProxyPortNames() {
		return highProxyPortNames;
	}

	public String getHighProxyDispatchProtocol() {
		return highProxyDispatchProtocol;
	}

	public String getHighProxyPeriod() {
		return highProxyPeriod;
	}

	public String getLowProxyComponentName() {
		return lowProxyComponentName;
	}

	public String getLowProxySubcomponentName() {
		return lowProxySubcomponentName;
	}

	public Map<String, List<String>> getLowProxyPortNames() {
		return lowProxyPortNames;
	}

	public String getLowProxyDispatchProtocol() {
		return lowProxyDispatchProtocol;
	}

	public String getLowProxyPeriod() {
		return lowProxyPeriod;
	}

//	public boolean getAddProxiedComponent() {
//		return addProxiedComponent;
//	}
//
//	public String getProxiedComponentName() {
//		return proxiedComponentName;
//	}
//
//	public String getProxiedSubcomponentName() {
//		return proxiedSubcomponentName;
//	}

	public String getRequirement() {
		return proxyRequirement;
	}

}
