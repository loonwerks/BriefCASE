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
import org.osate.aadl2.Context;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.handlers.AttestationTransformHandler;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;

/**
 * This class creates the Attestation Transform wizard
 */
public class AttestationTransformDialog extends TitleAreaDialog {

	private ComponentImplementation context = null;
	private Text txtMgrComponentName;
	private Text txtGateComponentName;
	private Text txtMgrSubcomponentName;
	private Text txtGateSubcomponentName;
	private Text txtRequestMessageDataType;
	private Text txtResponseMessageDataType;
	private Text txtCacheTimeout;
//	private Combo cboCacheSize;
	private Text txtIdListDataType;
	private PortNamesControl pncPortNames = null;
	private Label lblMgrDispatchProtocolField;
	private Group mgrProtocolGroup;
	private List<Button> btnMgrDispatchProtocol = new ArrayList<>();
	private Label lblMgrPeriodField;
	private Text txtMgrPeriod;
	private Label lblGateDispatchProtocolField;
	private Group gateProtocolGroup;
	private List<Button> btnGateDispatchProtocol = new ArrayList<>();
	private Label lblGatePeriodField;
	private Text txtGatePeriod;
	private List<Button> btnMgrLogPortType = new ArrayList<>();
	private List<Button> btnGateLogPortType = new ArrayList<>();
	private Button btnCreateThread = null;
	private Label lblUseKUImplementationField;
	private Button btnUseKUImplementation;
	private Combo cboRequirement;
//	private Button btnPropagateGuarantees;
//	private Text txtMgrAgreeProperty;
//	private Text txtGateAgreeProperty;
	private String attestationManagerComponentName;
	private String attestationGateComponentName;
	private String attestationManagerSubcomponentName;
	private String attestationGateSubcomponentName;
	private String requestMessageDataType = "";
	private String responseMessageDataType = "";
	private long cacheTimeout = 0;
//	private long cacheSize = 0;
	private String idListDataType = "";
	private Map<String, List<String>> gatePortNames = new HashMap<>();
	private String mgrDispatchProtocol = "";
	private String mgrPeriod = "";
	private String gateDispatchProtocol = "";
	private String gatePeriod = "";
	private PortCategory mgrLogPortType = null;
	private PortCategory gateLogPortType = null;
	private String requirement;
//	private boolean propagateGuarantees;
	private String commDriver = "";
	private boolean createThread = true;
	private boolean useKUImplementation = true;
	private List<String> requirements = new ArrayList<>();
//	private String mgrAgreeProperty;
//	private String gateAgreeProperty;
//	private Subcomponent attestationManager = null;
//	private Subcomponent attestationGate = null;

//	private static final int MAX_CACHE_SIZE = 6;
//	private static final int DEFAULT_CACHE_SIZE = 4;

	private static final String NO_REQUIREMENT_SELECTED = "<No requirement selected>";

	public AttestationTransformDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Attestation Transform");
		setMessage(
				"Enter Attestation details.  You may optionally leave these fields empty and manually edit the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();
		size.y += convertHeightInCharsToPixels(1);
		return size;
	}

//	public void create(Subcomponent commDriver, Subcomponent attestationManager,
//			Subcomponent attestationGate) {
	public void create(Subcomponent commDriver) {

		if (commDriver == null) {
			Dialog.showError("Attestation Transform", "Unknown communication driver.");
			return;
		}

		this.context = commDriver.getContainingComponentImpl();
		this.commDriver = commDriver.getName();
//		this.attestationManager = attestationManager;
//		this.attestationGate = attestationGate;

		// Populate requirements list
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> requirements.add(r.getId()));

		super.create();
		setTitle("Add Attestation to " + this.commDriver);
		setMessage("Enter Attestation details for adding attestation to " + this.commDriver
				+ ".  You may optionally leave these fields empty and manually edit the AADL attestation manager and gate components once they are added to the model.",
				IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		area.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final TabFolder folder = new TabFolder(area, SWT.NONE);
		createMgrTab(folder);
		createGateTab(folder);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		final Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		if (!(context instanceof ThreadImplementation)) {
			createCreateThreadField(container);
		}
		createRequirementField(container);

		return area;
	}

	private void createMgrTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		final TabItem mgrTab = new TabItem(folder, SWT.NONE);
		mgrTab.setText("Manager");

		createMgrComponentNameField(container);
		createMgrSubcomponentNameField(container);
		createRequestMessageDataTypeField(container);
		createResponseMessageDataTypeField(container);
		createCacheTimeoutField(container);
//		createCacheSizeField(container);
		createIdListDataTypeField(container);
//		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation
//				|| (context instanceof SystemImplementation && context.getTypeName().endsWith("_seL4"))) {
			createMgrDispatchProtocolField(container);
//		}
		createMgrLogPortField(container);
//		createMgrAgreePropertyField(container);
		createUseKUImplementationField(container);

		mgrTab.setControl(container);
	}

	private void createGateTab(TabFolder folder) {

		final Composite container = new Composite(folder, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		container.setLayout(new GridLayout(1, false));

		final TabItem gateTab = new TabItem(folder, SWT.NONE);
		gateTab.setText("Gate");

		final Composite namesContainer = new Composite(container, SWT.NONE);
		namesContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		namesContainer.setLayout(new GridLayout(2, false));

		createGateComponentNameField(namesContainer);
		createGateSubcomponentNameField(namesContainer);

		final Composite portsContainer = new Composite(container, SWT.NONE);
		portsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		portsContainer.setLayout(new GridLayout(1, false));
		createGatePortNamesField(portsContainer);

		final Composite miscContainer = new Composite(container, SWT.NONE);
		miscContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		miscContainer.setLayout(new GridLayout(2, false));
//		if (context instanceof ProcessImplementation || context instanceof ThreadGroupImplementation
//				|| (context instanceof SystemImplementation && context.getTypeName().endsWith("_seL4"))) {
			createGateDispatchProtocolField(miscContainer);
//		}
		createGateLogPortField(miscContainer);
//		createGateAgreePropertyField(miscContainer);

		gateTab.setControl(container);
	}

	private void createMgrComponentNameField(Composite container) {

		final Label lblComponentNameField = new Label(container, SWT.NONE);
		lblComponentNameField.setText("Attestation Manager component name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtMgrComponentName = new Text(container, SWT.BORDER);
		txtMgrComponentName.setLayoutData(dataInfoField);
//		if (attestationManager == null) {
			txtMgrComponentName
					.setText(ModelTransformUtils.getUniqueName(AttestationTransformHandler.AM_COMP_TYPE_NAME, true,
							AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
//		} else {
//			txtMgrComponentName.setText(attestationManager.getComponentType().getName());
//			txtMgrComponentName.setEnabled(false);
//		}

	}

	private void createMgrSubcomponentNameField(Composite container) {

		final Label lblSubcomponentNameField = new Label(container, SWT.NONE);
		lblSubcomponentNameField.setText("Attestation Manager subcomponent name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtMgrSubcomponentName = new Text(container, SWT.BORDER);
		txtMgrSubcomponentName.setLayoutData(dataInfoField);
//		if (attestationManager == null) {
			txtMgrSubcomponentName.setText(ModelTransformUtils.getUniqueName(
					AttestationTransformHandler.AM_SUBCOMP_NAME, true, context.getOwnedSubcomponents()));
//		} else {
//			txtMgrSubcomponentName.setText(attestationManager.getName());
//			txtMgrSubcomponentName.setEnabled(false);
//		}

	}


	private void createGateComponentNameField(Composite container) {

		final Label lblComponentNameField = new Label(container, SWT.NONE);
		lblComponentNameField.setText("Attestation Gate component name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtGateComponentName = new Text(container, SWT.BORDER);
		txtGateComponentName.setLayoutData(dataInfoField);
//		if (attestationGate == null) {
			txtGateComponentName
					.setText(ModelTransformUtils.getUniqueName(AttestationTransformHandler.AG_COMP_TYPE_NAME, true,
							AadlUtil.getContainingPackageSection(context).getOwnedClassifiers()));
//		} else {
//			txtGateComponentName.setText(attestationGate.getComponentType().getName());
//			txtGateComponentName.setEnabled(false);
//		}

	}

	private void createGateSubcomponentNameField(Composite container) {

		final Label lblSubcomponentNameField = new Label(container, SWT.NONE);
		lblSubcomponentNameField.setText("Attestation Gate subcomponent name");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtGateSubcomponentName = new Text(container, SWT.BORDER);
		txtGateSubcomponentName.setLayoutData(dataInfoField);
//		if (attestationGate == null) {
			txtGateSubcomponentName.setText(ModelTransformUtils.getUniqueName(
					AttestationTransformHandler.AG_SUBCOMP_NAME, true, context.getOwnedSubcomponents()));
//		} else {
//			txtGateSubcomponentName.setText(attestationGate.getName());
//			txtGateSubcomponentName.setEnabled(false);
//		}

	}

	private void createRequestMessageDataTypeField(Composite container) {

		final Label lblRequestMessageDataTypeField = new Label(container, SWT.NONE);
		lblRequestMessageDataTypeField.setText("Request Message data type");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtRequestMessageDataType = new Text(container, SWT.BORDER);
		txtRequestMessageDataType.setLayoutData(dataInfoField);

//		if (attestationManager != null) {
//
//			final ComponentType ct = attestationManager.getComponentType();
//			for (Feature f : ct.getOwnedFeatures()) {
//				if (f.getName().equalsIgnoreCase(AttestationTransformHandler.AM_PORT_ATTESTATION_REQUEST_NAME)) {
//					String dataType = "";
//					if (f instanceof EventDataPort) {
//						final EventDataPort port = (EventDataPort) f;
//						dataType = port.getDataFeatureClassifier().getQualifiedName();
//					} else if (f instanceof DataPort) {
//						final DataPort port = (DataPort) f;
//						dataType = port.getDataFeatureClassifier().getQualifiedName();
//					}
//					txtRequestMessageDataType.setText(dataType);
//				}
//			}
//
//			txtRequestMessageDataType.setEnabled(false);
//		}

	}

	private void createResponseMessageDataTypeField(Composite container) {

		final Label lblResponseMessageDataTypeField = new Label(container, SWT.NONE);
		lblResponseMessageDataTypeField.setText("Response Message data type");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtResponseMessageDataType = new Text(container, SWT.BORDER);
		txtResponseMessageDataType.setLayoutData(dataInfoField);

//		if (attestationManager != null) {
//
//			final ComponentType ct = attestationManager.getComponentType();
//			for (Feature f : ct.getOwnedFeatures()) {
//				if (f.getName().equalsIgnoreCase(AttestationTransformHandler.AM_PORT_ATTESTATION_RESPONSE_NAME)) {
//					String dataType = "";
//					if (f instanceof EventDataPort) {
//						final EventDataPort port = (EventDataPort) f;
//						dataType = port.getDataFeatureClassifier().getQualifiedName();
//					} else if (f instanceof DataPort) {
//						final DataPort port = (DataPort) f;
//						dataType = port.getDataFeatureClassifier().getQualifiedName();
//					}
//					txtResponseMessageDataType.setText(dataType);
//				}
//			}
//
//			txtResponseMessageDataType.setEnabled(false);
//		}

	}


	private void createCacheTimeoutField(Composite container) {

		final Label lblCacheTimeoutField = new Label(container, SWT.NONE);
		lblCacheTimeoutField.setText("Cache timeout (minutes)");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtCacheTimeout = new Text(container, SWT.BORDER);
		txtCacheTimeout.setLayoutData(dataInfoField);

//		if (attestationManager != null) {
//			final ComponentImplementation ci = attestationManager.getComponentImplementation();
//			final Property prop = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getProperty(),
//					CasePropertyUtils.CASE_PROPSET_NAME + "::" + CasePropertyUtils.CACHE_TIMEOUT);
//			final List<? extends PropertyExpression> propVals = ci.getPropertyValueList(prop);
//
//			if (propVals != null) {
//				// There should be only one property value
//				final PropertyExpression expr = propVals.get(0);
//				if (expr instanceof IntegerLiteral) {
//					txtCacheTimeout.setText(Long.toString(((IntegerLiteral) expr).getValue()));
//				}
//			}
//			txtCacheTimeout.setEnabled(false);
//		}

	}

//	private void createCacheSizeField(Composite container) {
//
//		final Label lblCacheSizeField = new Label(container, SWT.NONE);
//		lblCacheSizeField.setText("Cache size");
//
//		final GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
//		dataInfoField.grabExcessVerticalSpace = false;
//		cboCacheSize = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
//		cboCacheSize.setLayoutData(dataInfoField);
//		for (int i = 1; i <= MAX_CACHE_SIZE; i++) {
//			cboCacheSize.add(Integer.toString(i));
//		}
//		if (attestationManager == null) {
//			cboCacheSize.setText(Integer.toString(DEFAULT_CACHE_SIZE));
//		} else {
//			final ComponentImplementation ci = attestationManager.getComponentImplementation();
//			final Property prop = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getProperty(),
//					CasePropertyUtils.CASE_PROPSET_NAME + "::" + CasePropertyUtils.CACHE_SIZE);
//			final List<? extends PropertyExpression> propVals = ci.getPropertyValueList(prop);
//			if (propVals != null) {
//				// There should be only one property value
//				final PropertyExpression expr = propVals.get(0);
//				if (expr instanceof IntegerLiteral) {
//					cboCacheSize.setText(Long.toString(((IntegerLiteral) expr).getValue()));
//				}
//			}
//			cboCacheSize.setEnabled(false);
//		}
//
//	}

	private void createIdListDataTypeField(Composite container) {

		final Label lblIdListDataTypeField = new Label(container, SWT.NONE);
		lblIdListDataTypeField.setText("ID list data type");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		txtIdListDataType = new Text(container, SWT.BORDER);
		txtIdListDataType.setLayoutData(dataInfoField);

//		if (attestationManager != null) {
//
//			final ComponentType ct = attestationManager.getComponentType();
//			for (Feature f : ct.getOwnedFeatures()) {
//				if (f.getName().equalsIgnoreCase(AttestationTransformHandler.AM_PORT_TRUSTED_IDS_NAME)) {
//					String dataType = "";
//					if (f instanceof EventDataPort) {
//						final EventDataPort port = (EventDataPort) f;
//						dataType = port.getDataFeatureClassifier().getQualifiedName();
//					} else if (f instanceof DataPort) {
//						final DataPort port = (DataPort) f;
//						dataType = port.getDataFeatureClassifier().getQualifiedName();
//					}
//					txtIdListDataType.setText(dataType);
//				}
//			}
//
//			txtIdListDataType.setEnabled(false);
//		}

	}

	private void createGatePortNamesField(Composite container) {
		final Label lblPortNamesField = new Label(container, SWT.NONE);
		lblPortNamesField.setText("Port names");
		lblPortNamesField.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		// Get connections that will pass through gate
		final List<String> commPortNames = new ArrayList<>();
		final List<String> defaultInputPortNames = new ArrayList<>();
		final List<String> defaultOutputPortNames = new ArrayList<>();
		for (PortConnection conn : this.context.getOwnedPortConnections()) {
			if (conn.getSource().getContext() != null
					&& conn.getSource().getContext().getName().equalsIgnoreCase(commDriver)
					&& conn.getDestination().getContext() != null) {

				if (isAttestationManager(conn.getDestination().getContext())) {
					continue;
				}

				final String commPortName = conn.getSource().getConnectionEnd().getName();
				if (!commPortNames.contains(commPortName)) {
					commPortNames.add(commPortName);
					defaultInputPortNames.add(commPortName + "_in");
					defaultOutputPortNames.add(commPortName + "_out");
				}
			}
		}

		// Port names control
		pncPortNames = new PortNamesControl(container, commDriver + " Output Port Name", commPortNames,
				defaultInputPortNames, defaultOutputPortNames, "Gate");
	}

	private boolean isAttestationManager(Context context) {

		if (!(context instanceof Subcomponent)) {
			return false;
		}
		// Check if it's an attestation manager
		if (CasePropertyUtils.hasMitigationType(((Subcomponent) context).getClassifier(),
				MITIGATION_TYPE.ATTESTATION)) {
			return true;
		}

		return false;
	}

	/**
	 * Creates the input field for selecting the Manager dispatch protocol
	 * @param container
	 */
	private void createMgrDispatchProtocolField(Composite container) {
		lblMgrDispatchProtocolField = new Label(container, SWT.NONE);
		lblMgrDispatchProtocolField.setText("Dispatch protocol");
		lblMgrDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		mgrProtocolGroup = new Group(container, SWT.NONE);
		mgrProtocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		mgrProtocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnMgrDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(mgrProtocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblMgrPeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtMgrPeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtMgrPeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(mgrProtocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(mgrProtocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		lblMgrPeriodField = new Label(container, SWT.NONE);
		lblMgrPeriodField.setLayoutData(dataInfoField);
		lblMgrPeriodField.setText("Period");
		lblMgrPeriodField.setEnabled(false);

		txtMgrPeriod = new Text(container, SWT.BORDER);
		txtMgrPeriod.setLayoutData(dataInfoField);
		txtMgrPeriod.setEnabled(false);

//		if (attestationManager != null) {
//			Subcomponent mgrSubcomponent = attestationManager;
//			if (attestationManager.getComponentType() instanceof ProcessType
//					&& attestationManager.getComponentType().getName().endsWith("_seL4")) {
//				mgrSubcomponent = ((ProcessImplementation) mgrSubcomponent.getComponentImplementation())
//						.getOwnedThreadSubcomponents().get(0);
//			}
//			Property prop = GetProperties.lookupPropertyDefinition(mgrSubcomponent, ThreadProperties._NAME,
//					ThreadProperties.DISPATCH_PROTOCOL);
//			final List<? extends PropertyExpression> protocol = mgrSubcomponent.getPropertyValueList(prop);
//			if (!protocol.isEmpty()) {
//				final NamedValue nv = (NamedValue) protocol.get(0);
//				final EnumerationLiteral el = (EnumerationLiteral) nv.getNamedValue();
//				final String dispatchProtocol = el.getName();
//				if (dispatchProtocol.equalsIgnoreCase("Periodic")) {
//					btnNoProtocol.setSelection(false);
//					btnPeriodic.setSelection(true);
//				} else if (dispatchProtocol.equalsIgnoreCase("Sporadic")) {
//					btnNoProtocol.setSelection(false);
//					btnSporadic.setSelection(true);
//				} else {
//					btnNoProtocol.setSelection(true);
//				}
//			}
//
//			btnNoProtocol.setEnabled(false);
//			btnPeriodic.setEnabled(false);
//			btnSporadic.setEnabled(false);
//
//			prop = GetProperties.lookupPropertyDefinition(mgrSubcomponent, TimingProperties._NAME,
//					TimingProperties.PERIOD);
//			final List<? extends PropertyExpression> periodVals = mgrSubcomponent.getPropertyValueList(prop);
//			if (!periodVals.isEmpty()) {
//				String period = "";
//				if (periodVals.get(0) instanceof IntegerLiteral) {
//					final IntegerLiteral periodVal = (IntegerLiteral) periodVals.get(0);
//					period = Long.toString(periodVal.getValue());
//					if (periodVal.getUnit() != null) {
//						period += periodVal.getUnit().getName();
//					}
//				}
//				txtMgrPeriod.setText(period);
//			}
//			lblMgrPeriodField.setEnabled(false);
//			txtMgrPeriod.setEnabled(false);
//		}

		btnMgrDispatchProtocol.add(btnNoProtocol);
		btnMgrDispatchProtocol.add(btnPeriodic);
		btnMgrDispatchProtocol.add(btnSporadic);

	}

	/**
	 * Creates the input field for selecting the Gate dispatch protocol
	 * @param container
	 */
	private void createGateDispatchProtocolField(Composite container) {
		lblGateDispatchProtocolField = new Label(container, SWT.NONE);
		lblGateDispatchProtocolField.setText("Dispatch protocol");
		lblGateDispatchProtocolField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		gateProtocolGroup = new Group(container, SWT.NONE);
		gateProtocolGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		gateProtocolGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnGateDispatchProtocol.clear();

		final Button btnNoProtocol = new Button(gateProtocolGroup, SWT.RADIO);
		btnNoProtocol.setText("None");
		btnNoProtocol.setSelection(true);
		btnNoProtocol.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblGatePeriodField.setEnabled(!btnNoProtocol.getSelection());
				txtGatePeriod.setEnabled(!btnNoProtocol.getSelection());
				if (btnNoProtocol.getSelection()) {
					txtGatePeriod.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		final Button btnPeriodic = new Button(gateProtocolGroup, SWT.RADIO);
		btnPeriodic.setText("Periodic");
		btnPeriodic.setSelection(false);

		final Button btnSporadic = new Button(gateProtocolGroup, SWT.RADIO);
		btnSporadic.setText("Sporadic");
		btnSporadic.setSelection(false);

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;

		lblGatePeriodField = new Label(container, SWT.NONE);
		lblGatePeriodField.setLayoutData(dataInfoField);
		lblGatePeriodField.setText("Period");
		lblGatePeriodField.setEnabled(false);

		txtGatePeriod = new Text(container, SWT.BORDER);
		txtGatePeriod.setLayoutData(dataInfoField);
		txtGatePeriod.setEnabled(false);

//		if (attestationGate != null) {
//			Subcomponent gateSubcomponent = attestationGate;
//			if (attestationGate.getComponentType() instanceof ProcessType
//					&& attestationGate.getComponentType().getName().endsWith("_seL4")) {
//				gateSubcomponent = ((ProcessImplementation) gateSubcomponent.getComponentImplementation())
//						.getOwnedThreadSubcomponents().get(0);
//			}
//			Property prop = GetProperties.lookupPropertyDefinition(gateSubcomponent, ThreadProperties._NAME,
//					ThreadProperties.DISPATCH_PROTOCOL);
//			final List<? extends PropertyExpression> protocol = gateSubcomponent.getPropertyValueList(prop);
//			if (!protocol.isEmpty()) {
//				final NamedValue nv = (NamedValue) protocol.get(0);
//				final EnumerationLiteral el = (EnumerationLiteral) nv.getNamedValue();
//				final String dispatchProtocol = el.getName();
//				if (dispatchProtocol.equalsIgnoreCase("Periodic")) {
//					btnNoProtocol.setSelection(false);
//					btnPeriodic.setSelection(true);
//				} else if (dispatchProtocol.equalsIgnoreCase("Sporadic")) {
//					btnNoProtocol.setSelection(false);
//					btnSporadic.setSelection(true);
//				} else {
//					btnNoProtocol.setSelection(true);
//				}
//			}
//
//			btnNoProtocol.setEnabled(false);
//			btnPeriodic.setEnabled(false);
//			btnSporadic.setEnabled(false);
//
//			prop = GetProperties.lookupPropertyDefinition(gateSubcomponent, TimingProperties._NAME,
//					TimingProperties.PERIOD);
//			final List<? extends PropertyExpression> periodVals = gateSubcomponent.getPropertyValueList(prop);
//			if (!periodVals.isEmpty()) {
//				String period = "";
//				if (periodVals.get(0) instanceof IntegerLiteral) {
//					final IntegerLiteral periodVal = (IntegerLiteral) periodVals.get(0);
//					period = Long.toString(periodVal.getValue());
//					if (periodVal.getUnit() != null) {
//						period += periodVal.getUnit().getName();
//					}
//				}
//				txtGatePeriod.setText(period);
//			}
//			lblGatePeriodField.setEnabled(false);
//			txtGatePeriod.setEnabled(false);
//		}

		btnGateDispatchProtocol.add(btnNoProtocol);
		btnGateDispatchProtocol.add(btnPeriodic);
		btnGateDispatchProtocol.add(btnSporadic);

	}


	/**
	 * Creates the input field for specifying if the attestation manager should contain
	 * a port for logging messages
	 * @param container
	 */
	private void createMgrLogPortField(Composite container) {
		final Label lblLogField = new Label(container, SWT.NONE);
		lblLogField.setText("Create log port");
		lblLogField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group logGroup = new Group(container, SWT.NONE);
		logGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		logGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnMgrLogPortType.clear();

		final Button btnNoLogPort = new Button(logGroup, SWT.RADIO);
		btnNoLogPort.setText("None");
		btnNoLogPort.setSelection(true);

		final Button btnEventLogPort = new Button(logGroup, SWT.RADIO);
		btnEventLogPort.setText("Event");
		btnEventLogPort.setSelection(false);

		final Button btnDataLogPort = new Button(logGroup, SWT.RADIO);
		btnDataLogPort.setText("Data");
		btnDataLogPort.setSelection(false);

		final Button btnEventDataLogPort = new Button(logGroup, SWT.RADIO);
		btnEventDataLogPort.setText("Event Data");
		btnEventDataLogPort.setSelection(false);

//		if (attestationManager != null) {
//			final ComponentType ct = attestationManager.getComponentType();
//			for (Feature f : ct.getOwnedFeatures()) {
//				if (f.getName().equalsIgnoreCase(AttestationTransformHandler.LOG_PORT_NAME)) {
//					btnNoLogPort.setSelection(false);
//					if (f instanceof DataPort) {
//						btnDataLogPort.setSelection(true);
//					} else if (f instanceof EventPort) {
//						btnEventLogPort.setSelection(true);
//					} else if (f instanceof EventDataPort) {
//						btnEventDataLogPort.setSelection(true);
//					}
//					break;
//				}
//			}
//			btnDataLogPort.setEnabled(false);
//			btnEventLogPort.setEnabled(false);
//			btnEventDataLogPort.setEnabled(false);
//			btnNoLogPort.setEnabled(false);
//		}

		btnMgrLogPortType.add(btnDataLogPort);
		btnMgrLogPortType.add(btnEventLogPort);
		btnMgrLogPortType.add(btnEventDataLogPort);
		btnMgrLogPortType.add(btnNoLogPort);

	}

	/**
	 * Creates the input field for specifying if the attestation gate should contain
	 * a port for logging messages
	 * @param container
	 */
	private void createGateLogPortField(Composite container) {
		final Label lblLogField = new Label(container, SWT.NONE);
		lblLogField.setText("Create log port");
		lblLogField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Create a group to contain the log port options
		final Group logGroup = new Group(container, SWT.NONE);
		logGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		logGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnGateLogPortType.clear();

		final Button btnNoLogPort = new Button(logGroup, SWT.RADIO);
		btnNoLogPort.setText("None");
		btnNoLogPort.setSelection(true);

		final Button btnEventLogPort = new Button(logGroup, SWT.RADIO);
		btnEventLogPort.setText("Event");
		btnEventLogPort.setSelection(false);

		final Button btnDataLogPort = new Button(logGroup, SWT.RADIO);
		btnDataLogPort.setText("Data");
		btnDataLogPort.setSelection(false);

		final Button btnEventDataLogPort = new Button(logGroup, SWT.RADIO);
		btnEventDataLogPort.setText("Event Data");
		btnEventDataLogPort.setSelection(false);

//		if (attestationGate != null) {
//			final ComponentType ct = attestationGate.getComponentType();
//			for (Feature f : ct.getOwnedFeatures()) {
//				if (f.getName().equalsIgnoreCase(AttestationTransformHandler.LOG_PORT_NAME)) {
//					btnNoLogPort.setSelection(false);
//					if (f instanceof DataPort) {
//						btnDataLogPort.setSelection(true);
//					} else if (f instanceof EventPort) {
//						btnEventLogPort.setSelection(true);
//					} else if (f instanceof EventDataPort) {
//						btnEventDataLogPort.setSelection(true);
//					}
//					break;
//				}
//			}
//			btnDataLogPort.setEnabled(false);
//			btnEventLogPort.setEnabled(false);
//			btnEventDataLogPort.setEnabled(false);
//			btnNoLogPort.setEnabled(false);
//		}

		btnGateLogPortType.add(btnDataLogPort);
		btnGateLogPortType.add(btnEventLogPort);
		btnGateLogPortType.add(btnEventDataLogPort);
		btnGateLogPortType.add(btnNoLogPort);

	}

	/**
	 * Creates the input field for specifying the KU remote attestation
	 * implementation should be used
	 * @param container
	 */
	private void createUseKUImplementationField(Composite container) {
		lblUseKUImplementationField = new Label(container, SWT.NONE);
		lblUseKUImplementationField.setText("Use KU Remote Attestation implementation");
		lblUseKUImplementationField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnUseKUImplementation = new Button(container, SWT.CHECK);
		btnUseKUImplementation.setLayoutData(dataInfoField);
		btnUseKUImplementation.setSelection(true);
	}

	/**
	 * Creates the input field for specifying if a thread should also be created
	 * if filter is a process
	 * @param container
	 */
	private void createCreateThreadField(Composite container) {
		final Label lblCreateThreadField = new Label(container, SWT.NONE);
		lblCreateThreadField.setText("Create internal thread components");
		lblCreateThreadField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		btnCreateThread = new Button(container, SWT.CHECK);
		btnCreateThread.setLayoutData(dataInfoField);
		btnCreateThread.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				if (btnCreateThread.getSelection()) {
					for (Button b : btnMgrDispatchProtocol) {
						b.setEnabled(true);
					}
					for (Button b : btnGateDispatchProtocol) {
						b.setEnabled(true);
					}
				} else {
					for (Button b : btnMgrDispatchProtocol) {
						if (b.getText().equals("None")) {
							b.setSelection(true);
							txtMgrPeriod.setText("");
						} else {
							b.setSelection(false);
						}
						b.setEnabled(false);
					}
					for (Button b : btnGateDispatchProtocol) {
						if (b.getText().equals("None")) {
							b.setSelection(true);
							txtGatePeriod.setText("");
						} else {
							b.setSelection(false);
						}
						b.setEnabled(false);
					}
					btnUseKUImplementation.setSelection(false);
				}
				lblMgrDispatchProtocolField.setEnabled(btnCreateThread.getSelection());
				mgrProtocolGroup.setEnabled(btnCreateThread.getSelection());
				lblMgrPeriodField.setEnabled(btnCreateThread.getSelection());
				txtMgrPeriod.setEnabled(btnCreateThread.getSelection());

				lblGateDispatchProtocolField.setEnabled(btnCreateThread.getSelection());
				gateProtocolGroup.setEnabled(btnCreateThread.getSelection());
				lblGatePeriodField.setEnabled(btnCreateThread.getSelection());
				txtGatePeriod.setEnabled(btnCreateThread.getSelection());

				lblUseKUImplementationField.setEnabled(btnCreateThread.getSelection());
				btnUseKUImplementation.setEnabled(btnCreateThread.getSelection());
			}
		});
		btnCreateThread.setSelection(true);

	}


	/**
	 * Creates the input field for selecting the resolute clause that drives
	 * the addition of this filter to the design
	 * @param container
	 */
	private void createRequirementField(Composite container) {
		final Label lblResoluteField = new Label(container, SWT.NONE);
		lblResoluteField.setText("Requirement");

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		cboRequirement = new Combo(container, SWT.BORDER);
		cboRequirement.setLayoutData(dataInfoField);
		cboRequirement.add(NO_REQUIREMENT_SELECTED);
		requirements.forEach(r -> cboRequirement.add(r));
		cboRequirement.setText(NO_REQUIREMENT_SELECTED);

	}


//	/**
//	 * Creates the input text field for specifying the attestation manager agree property
//	 * @param container
//	 */
//	private void createMgrAgreePropertyField(Composite container) {
//		Label lblAgreeField = new Label(container, SWT.NONE);
//		lblAgreeField.setText("Attestation Manager AGREE contract");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = GridData.FILL;
//		dataInfoField.grabExcessVerticalSpace = false;
//		txtMgrAgreeProperty = new Text(container, SWT.BORDER);
//		txtMgrAgreeProperty.setLayoutData(dataInfoField);
//	}
//
//	/**
//	 * Creates the input text field for specifying the attestation gate agree property
//	 * @param container
//	 */
//	private void createGateAgreePropertyField(Composite container) {
//		Label lblAgreeField = new Label(container, SWT.NONE);
//		lblAgreeField.setText("Attestation Gate AGREE contract");
//
//		GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = GridData.FILL;
//		dataInfoField.grabExcessVerticalSpace = false;
//		txtGateAgreeProperty = new Text(container, SWT.BORDER);
//		txtGateAgreeProperty.setLayoutData(dataInfoField);
//	}

	@Override
	protected void okPressed() {
		if (!saveInput()) {
			return;
		}
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

		// Attestation Manager Component Name
		if (!txtMgrComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMgrComponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Manager component name " + txtMgrComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
//		} else if (attestationManager == null && !txtMgrComponentName.getText().isEmpty()
		} else if (!txtMgrComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtMgrComponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Component " + txtMgrComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtMgrComponentName.setText(
					ModelTransformUtils.getUniqueName(txtMgrComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			attestationManagerComponentName = txtMgrComponentName.getText();
		}

		// Attestation Gate Component Name
		if (!txtGateComponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtGateComponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Gate component name " + txtGateComponentName.getText()
					+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
//		} else if (attestationGate == null && !txtGateComponentName.getText().isEmpty()
		} else if (!txtGateComponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(componentsInPackage, txtGateComponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Component " + txtGateComponentName.getText()
					+ " already exists in model. Use the suggested name or enter a new one.");
			txtGateComponentName.setText(
					ModelTransformUtils.getUniqueName(txtGateComponentName.getText(), true, componentsInPackage));
			return false;
		} else {
			attestationGateComponentName = txtGateComponentName.getText();
		}

		// Attestation Manager Subcomponent Instance Name
		if (!txtMgrSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtMgrSubcomponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Manager subcomponent instance name " + txtMgrSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
//		} else if (attestationManager == null && !txtMgrSubcomponentName.getText().isEmpty()
		} else if (!txtMgrSubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtMgrSubcomponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Subcomponent " + txtMgrSubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtMgrSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtMgrSubcomponentName.getText(), true,
					context.getAllSubcomponents()));
			return false;
		} else {
			attestationManagerSubcomponentName = txtMgrSubcomponentName.getText();
		}

		// Attestation Gate Subcomponent Instance Name
		if (!txtGateSubcomponentName.getText().isEmpty()
				&& !ModelTransformUtils.isValidName(txtGateSubcomponentName.getText())) {
			Dialog.showError("Attestation Transform",
					"Attestation Gate subcomponent instance name " + txtGateSubcomponentName.getText()
							+ " contains invalid characters. Only 'A..Z', 'a..z', '0..9', and '_' are permitted");
			return false;
//		} else if (attestationGate == null && !txtGateSubcomponentName.getText().isEmpty()
		} else if (!txtGateSubcomponentName.getText().isEmpty()
				&& AadlUtil.findNamedElementInList(context.getAllSubcomponents(),
				txtGateSubcomponentName.getText()) != null) {
			Dialog.showError("Attestation Transform", "Subcomponent " + txtGateSubcomponentName.getText()
					+ " already exists in " + context.getName() + ". Use the suggested name or enter a new one.");
			txtGateSubcomponentName.setText(ModelTransformUtils.getUniqueName(txtGateSubcomponentName.getText(), true,
					context.getAllSubcomponents()));
			return false;
		} else {
			attestationGateSubcomponentName = txtGateSubcomponentName.getText();
		}

		// Request Message Data Type
		if (!txtRequestMessageDataType.getText().isEmpty() && !txtRequestMessageDataType.getText().contains("::")) {
			// Check if type is defined in current package
			if (AadlUtil.findNamedElementInList(componentsInPackage, txtRequestMessageDataType.getText()) == null) {
				Dialog.showError("Attestation Transform",
						"Request message data type was not found in package "
								+ AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				return false;
			} else {
				// Add the package name
				txtRequestMessageDataType.setText(
						AadlUtil.getContainingPackage(context).getName() + "::" + txtRequestMessageDataType.getText());
			}
		}
		requestMessageDataType = txtRequestMessageDataType.getText();

		// Response Message Data Type
		if (!txtResponseMessageDataType.getText().isEmpty() && !txtResponseMessageDataType.getText().contains("::")) {
			// Check if type is defined in current package
			if (AadlUtil.findNamedElementInList(componentsInPackage, txtResponseMessageDataType.getText()) == null) {
				Dialog.showError("Attestation Transform",
						"Response message data type was not found in package "
								+ AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				return false;
			} else {
				// Add the package name
				txtResponseMessageDataType.setText(
						AadlUtil.getContainingPackage(context).getName() + "::" + txtResponseMessageDataType.getText());
			}
		}
		responseMessageDataType = txtResponseMessageDataType.getText();


		// Timeout
		try {
			cacheTimeout = Long.parseLong(txtCacheTimeout.getText());
		} catch (NumberFormatException e) {
			Dialog.showError("Attestation Transform", "Value of Cache Timeout must be an integer.");
			return false;
		}

//		// Cache Size
//		try {
//			cacheSize = Long.parseLong(cboCacheSize.getText());
//		} catch (NumberFormatException e) {
//			Dialog.showError("Attestation Transform", "Value of Cache Size must be an integer.");
//			return false;
//		}

		// ID List Data Type
		if (!txtIdListDataType.getText().isEmpty() && !txtIdListDataType.getText().contains("::")) {
			// Check if type is defined in current package
			if (AadlUtil.findNamedElementInList(componentsInPackage, txtIdListDataType.getText()) == null) {
				Dialog.showError("Attestation Transform",
						"ID list data type was not found in package " + AadlUtil.getContainingPackage(context).getName()
								+ ". Enter the data type's qualified name if it is defined in a different package.");
				return false;
			} else {
				// Add the package name
				txtIdListDataType
						.setText(AadlUtil.getContainingPackage(context).getName() + "::" + txtIdListDataType.getText());
			}
		}
		idListDataType = txtIdListDataType.getText();

		// Gate port names
		gatePortNames = pncPortNames.getContents();
		// Make sure there are no duplicate names
		final Set<String> portNames = new HashSet<>();
		for (List<String> port : gatePortNames.values()) {
			if (!portNames.add(port.get(0).toLowerCase())) {
				Dialog.showError("Attestation Transform", "Attestation Gate has multiple ports named " + port.get(0)
							+ ". All port names must be unique.");
				return false;
			} else if (!portNames.add(port.get(1).toLowerCase())) {
				Dialog.showError("Attestation Transform", "Attestation Gate has multiple ports named " + port.get(1)
						+ ". All port names must be unique.");
				return false;
			} else if (port.get(0).equalsIgnoreCase(port.get(1))) {
				Dialog.showError("Attestation Transform", "Attestation Gate has multiple ports named " + port.get(0)
						+ ". All port names must be unique.");
				return false;
			}
		}

		// Create thread
		if (btnCreateThread != null) {
			createThread = btnCreateThread.getSelection();
		}

		// Mgr Dispatch Protocol and Period
		for (Button b : btnMgrDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				mgrDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtMgrPeriod.getText().isEmpty()
						|| txtMgrPeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					mgrPeriod = txtMgrPeriod.getText();
				} else {
					Dialog.showError("Attestation Transform", "Attestation Manager period " + txtMgrPeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

		// Gate Dispatch Protocol and Period
		for (Button b : btnGateDispatchProtocol) {
			if (b.getSelection() && !b.getText().equalsIgnoreCase("None")) {
				gateDispatchProtocol = b.getText();
				// make sure period is properly formatted
				if (txtGatePeriod.getText().isEmpty()
						|| txtGatePeriod.getText().matches("((\\d)+(\\s)*(ps|ns|us|ms|sec|min|hr)?)")) {
					gatePeriod = txtGatePeriod.getText();
				} else {
					Dialog.showError("Attestation Transform", "Attestation Gate period " + txtGatePeriod.getText()
							+ " is malformed. See the AADL definition of Period in Timing_Properties.aadl.");
					return false;
				}
				break;
			}
		}

		// Mgr Log Port
		mgrLogPortType = null;
		for (int i = 0; i < btnMgrLogPortType.size(); i++) {
			if (btnMgrLogPortType.get(i).getSelection()) {
				mgrLogPortType = PortCategory.get(i);
				break;
			}
		}

		// Gate Log Port
		gateLogPortType = null;
		for (int i = 0; i < btnGateLogPortType.size(); i++) {
			if (btnGateLogPortType.get(i).getSelection()) {
				gateLogPortType = PortCategory.get(i);
				break;
			}
		}

		// Use KU implementation
		useKUImplementation = btnUseKUImplementation.getSelection();

		// Requirement
		requirement = cboRequirement.getText();
		if (requirement.equals(NO_REQUIREMENT_SELECTED)) {
			requirement = "";
		} else if (!requirements.contains(requirement)) {
			Dialog.showError("Attestation Transform", "Attestation requirement " + requirement
							+ " does not exist in the model.  Select a requirement from the list, or choose "
							+ NO_REQUIREMENT_SELECTED + ".");
			return false;
		}

//		// AGREE
//		if (attestationManager == null) {
//			mgrAgreeProperty = txtMgrAgreeProperty.getText();
//			gateAgreeProperty = txtGateAgreeProperty.getText();
////			propagateGuarantees = btnPropagateGuarantees.getSelection();
//		}
		return true;
	}

	public String getAttestationManagerComponentName() {
		return attestationManagerComponentName;
	}

	public String getAttestationGateComponentName() {
		return attestationGateComponentName;
	}

	public String getAttestationManagerSubcomponentName() {
		return attestationManagerSubcomponentName;
	}

	public String getAttestationGateSubcomponentName() {
		return attestationGateSubcomponentName;
	}

	public String getRequestMessageDataType() {
		return requestMessageDataType;
	}

	public String getResponseMessageDataType() {
		return responseMessageDataType;
	}

	public long getCacheTimeout() {
		return cacheTimeout;
	}

//	public long getCacheSize() {
//		return cacheSize;
//	}

	public String getIdListDataType() {
		return idListDataType;
	}

	public Map<String, List<String>> getGatePortNames() {
		return gatePortNames;
	}

	public boolean createThread() {
		return createThread;
	}

	public String getMgrDispatchProtocol() {
		return mgrDispatchProtocol;
	}

	public String getMgrPeriod() {
		return mgrPeriod;
	}

	public String getGateDispatchProtocol() {
		return gateDispatchProtocol;
	}

	public String getGatePeriod() {
		return gatePeriod;
	}

	public PortCategory getMgrLogPortType() {
		return mgrLogPortType;
	}

	public PortCategory getGateLogPortType() {
		return gateLogPortType;
	}

//	public boolean getPropagateGuarantees() {
//		return propagateGuarantees;
//	}

	public boolean useKUImplementation() {
		return useKUImplementation;
	}

	public String getRequirement() {
		return requirement;
	}

//	public String getMgrAgreeProperty() {
//		return mgrAgreeProperty;
//	}
//
//	public String getGateAgreeProperty() {
//		return gateAgreeProperty;
//	}

}
