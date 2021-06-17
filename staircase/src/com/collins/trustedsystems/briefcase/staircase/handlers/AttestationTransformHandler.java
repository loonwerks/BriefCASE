package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.DirectedFeature;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.FeatureGroupType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.Realization;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.attestation.AttestationAccess;
import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
import com.collins.trustedsystems.briefcase.staircase.Activator;
import com.collins.trustedsystems.briefcase.staircase.dialogs.AttestationTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddAttestationClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.collins.trustedsystems.briefcase.util.TraverseProject;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;

public class AttestationTransformHandler extends AadlHandler {

//	static final String AM_REQUEST_MSG_NAME = "CASE_AttestationRequestMsg";
//	static final String AM_RESPONSE_MSG_NAME = "CASE_AttestationResponseMsg";
	static final String AM_REQUEST_MSG_IMPL_NAME = "CASE_AttestationRequestMsg.Impl";
	static final String AM_RESPONSE_MSG_IMPL_NAME = "CASE_AttestationResponseMsg.Impl";
	public static final String AM_COMP_TYPE_NAME = "CASE_AttestationManager";
	public static final String AG_COMP_TYPE_NAME = "CASE_AttestationGate";
	public static final String LOG_PORT_NAME = "LogMessage";
	public static final String AM_PORT_ATTESTATION_REQUEST_NAME = "AttestationRequest";
	public static final String AM_PORT_ATTESTATION_RESPONSE_NAME = "AttestationResponse";
	public static final String AM_PORT_TRUSTED_IDS_NAME = "TrustedIds";
//	static final String AM_PORT_ATTESTATION_REQUEST_NAME_EXTERNAL = "comm_request";
//	static final String AM_PORT_ATTESTATION_RESPONSE_NAME_EXTERNAL = "comm_response";
	public static final String AM_SUBCOMP_NAME = "AttestationManager";
	public static final String AG_SUBCOMP_NAME = "AttestationGate";
	static final String CONNECTION_IMPL_NAME = "c";

	private String attestationManagerComponentName;
	private String attestationGateComponentName;
	private String attestationManagerSubcomponentName;
	private String attestationGateSubcomponentName;
	private String attestationManagerDispatchProtocol;
	private String attestationManagerPeriod;
	private String attestationGateDispatchProtocol;
	private String attestationGatePeriod;
	private String requestMessageDataType;
	private String responseMessageDataType;
	private long cacheTimeout;
//	private long cacheSize;
	private String idListDataType;
	private Map<String, List<String>> attestationGatePortNames;
	private PortCategory attestationManagerLogPortType;
	private PortCategory attestationGateLogPortType;
	private String attestationRequirement;
	private boolean useKUImplementation;
//	private String attestationManagerAgreeProperty;
//	private String attestationGateAgreeProperty;
	private boolean isSel4Process = false;

	@Override
	protected void runCommand(URI uri) {

		// Check if selection is a subcomponent
		final EObject eObj = getEObject(uri);
		Subcomponent selectedSubcomponent = null;
		if (eObj instanceof Subcomponent) {
			selectedSubcomponent = (Subcomponent) eObj;
		} else {
			Dialog.showError("Attestation Transform",
					"A communication driver subcomponent must be selected to add attestation.");
			return;
		}

		// Check if selected subcomponent is a comm driver
		if (!CasePropertyUtils.isCommDriver(selectedSubcomponent.getClassifier())) {
			Dialog.showError("Attestation Transform",
					"A communication driver subcomponent must be selected to add attestation.");
			return;
		}

//		// Check if the selected subcomponent already has an attestation manager connected
//		// If there is, ask the user if they would like to associate the attestation manager with another requirement
//		final Subcomponent attestationManager = getAttestationManager(selectedSubcomponent);
//		Subcomponent attestationGate = null;
//		if (attestationManager != null) {
//			if (!Dialog.askQuestion("Attestation Transform", selectedSubcomponent.getName()
//					+ " already has associated attestation components. Would you like to associate them with a new requirement?")) {
//				return;
//			}
//			attestationGate = getAttestationGate(attestationManager);
//		}

//		// Check that attestation components aren't being added to a thread in a seL4 process
//		if (selectedSubcomponent.getContainingComponentImpl() instanceof ProcessImplementation
//				&& selectedSubcomponent.getContainingComponentImpl().getTypeName().endsWith("_seL4")) {
//			Dialog.showError("Attestation Transform", "An seL4 process cannot contain multiple components.");
//			return;
//		}

//		isSel4Process = selectedSubcomponent.getComponentImplementation() instanceof ProcessImplementation
//				&& selectedSubcomponent.getContainingComponentImpl().getTypeName().endsWith("_seL4");

//		if (isSel4Process && ((ProcessImplementation) selectedSubcomponent.getComponentImplementation())
//				.getOwnedThreadSubcomponents().size() != 1) {
//			Dialog.showError("Add Attestation",
//					"Selected comm driver is tagged as an seL4 process, but does not contain a single thread subcomponent.");
//			return;
//		}

		// Open wizard to enter filter info
		final AttestationTransformDialog wizard = new AttestationTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		wizard.create(selectedSubcomponent, attestationManager, attestationGate);
		wizard.create(selectedSubcomponent);
		if (wizard.open() == Window.OK) {
			attestationManagerComponentName = wizard.getAttestationManagerComponentName();
			if (attestationManagerComponentName.isEmpty()) {
				attestationManagerComponentName = AM_COMP_TYPE_NAME;
			}
			attestationGateComponentName = wizard.getAttestationGateComponentName();
			if (attestationGateComponentName.isEmpty()) {
				attestationGateComponentName = AG_COMP_TYPE_NAME;
			}
			attestationManagerSubcomponentName = wizard.getAttestationManagerSubcomponentName();
			if (attestationManagerSubcomponentName.isEmpty()) {
				attestationManagerSubcomponentName = AM_SUBCOMP_NAME;
			}
			attestationGateSubcomponentName = wizard.getAttestationGateSubcomponentName();
			if (attestationGateSubcomponentName.isEmpty()) {
				attestationGateSubcomponentName = AG_SUBCOMP_NAME;
			}
			attestationManagerDispatchProtocol = wizard.getMgrDispatchProtocol();
			attestationManagerPeriod = wizard.getMgrPeriod();
			attestationGateDispatchProtocol = wizard.getGateDispatchProtocol();
			attestationGatePeriod = wizard.getGatePeriod();
			requestMessageDataType = wizard.getRequestMessageDataType();
			responseMessageDataType = wizard.getResponseMessageDataType();
			cacheTimeout = wizard.getCacheTimeout();
//			cacheSize = wizard.getCacheSize();
			idListDataType = wizard.getIdListDataType();
			attestationGatePortNames = wizard.getGatePortNames();
			attestationManagerLogPortType = wizard.getMgrLogPortType();
			attestationGateLogPortType = wizard.getGateLogPortType();
			isSel4Process = wizard.createThread();
			useKUImplementation = wizard.useKUImplementation();
			attestationRequirement = wizard.getRequirement();
//			attestationManagerAgreeProperty = wizard.getMgrAgreeProperty();
//			attestationGateAgreeProperty = wizard.getGateAgreeProperty();
		} else {
			return;
		}

		// Insert the attestation manager
//		if (attestationManager != null) {
//			associateNewRequirement(EcoreUtil.getURI(selectedSubcomponent), EcoreUtil.getURI(attestationManager),
//					EcoreUtil.getURI(attestationGate));
//			BriefcaseNotifier.notify("StairCASE - Attestation Transform",
//					"New requirement associated with Attestation Manager.");
//		} else {
		insertAttestationComponents(uri);
		BriefcaseNotifier.notify("StairCASE - Attestation Transform", "Attestation added to model.");
//		}

		// Save
		saveChanges(false);

		// Add attestation implementation, if requested
		if (useKUImplementation) {
			if (!AttestationAccess.createSourceDirectory()) {
				Dialog.showWarning("Attestation Transform",
						"Attestation components were added to the model, but the KU implementation source code could not be copied into the project");
			}
		}

		return;

	}

//	private void associateNewRequirement(URI commDriverUri, URI attestationManagerUri, URI attestationGateUri) {
//		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		final AddAttestationClaim claim = xtextEditor.getDocument().modify(resource -> {
//
//			final Subcomponent commDriver = (Subcomponent) resource.getEObject(commDriverUri.fragment());
//			final Subcomponent attestationManager = (Subcomponent) resource
//					.getEObject(attestationManagerUri.fragment());
//			final Subcomponent attestationGate = (Subcomponent) resource.getEObject(attestationGateUri.fragment());
//
//			// Add add_attestation claims to resolute prove statement, if applicable
//			if (!attestationRequirement.isEmpty()) {
//				final CyberRequirement req = RequirementsManager.getInstance().getRequirement(attestationRequirement);
//				return new AddAttestationClaim(req.getContext(), commDriver, attestationManager,
//						attestationGate);
//			}
//
//			return null;
//		});
//
//		if (claim != null) {
//			RequirementsManager.getInstance().modifyRequirement(attestationRequirement, claim);
//		}
//	}

	/**
	 * Inserts an attestation manager and gate component into the model.  The attestation manager is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private void insertAttestationComponents(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		final AddAttestationClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			final Subcomponent commDriver = (Subcomponent) resource.getEObject(uri.fragment());
			// Get containing component implementation
			final ComponentImplementation ci = commDriver.getContainingComponentImpl();

			PackageSection pkgSection = AadlUtil.getContainingPackageSection(commDriver);
			if (pkgSection == null) {
				// Something went wrong
				Dialog.showError("Attestation Transform", "No public or private package sections found.");
				return null;
			}

			// Import CASE_Properties file
			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}

			ComponentCategory compCategory = commDriver.getCategory();
			if (compCategory == ComponentCategory.THREAD_GROUP) {
				compCategory = ComponentCategory.THREAD;
			}
//			// If the component type is a process, we will need to put a single thread inside.
//			// Per convention, we will attach all properties and contracts to the thread.
//			// For this model transformation, we will create the thread first, then wrap it in a process
//			// component, using the same mechanism we use for the seL4 transformation
//			final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
//			if (isProcess) {
//				compCategory = ComponentCategory.THREAD;
//			}

			// Create new comm driver component that copies the selected one

//			final ComponentType commDriverType = (ComponentType) pkgSection
//					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
			final ComponentType commDriverType = EcoreUtil.copy(commDriver.getComponentType());
			pkgSection.getOwnedClassifiers().add(commDriverType);

			// Give it a unique name
//			commDriverType.setName(
//					ModelTransformUtils.getUniqueName(commDriver.getComponentType().getName() + "_Attestation", true,
//					pkgSection.getOwnedClassifiers()));
			String commDriverName = commDriver.getComponentType().getName().replace("_seL4", "") + "_Attestation";
			commDriverName = ModelTransformUtils.getUniqueName(commDriverName, true, pkgSection.getOwnedClassifiers())
					+ (isSel4Process ? "_seL4" : "");
			commDriverType.setName(commDriverName);
//			commDriverType.setExtended(commDriver.getComponentType());

			// Make sure data classifier package is in with clause
			for (Feature f : commDriverType.getOwnedFeatures()) {
				if (f instanceof EventDataPort) {
					ModelTransformUtils.importContainingPackage(((EventDataPort) f).getDataFeatureClassifier(),
							pkgSection);
				} else if (f instanceof DataPort) {
					ModelTransformUtils.importContainingPackage(((DataPort) f).getDataFeatureClassifier(), pkgSection);
				}
			}
//			// Copy the features
//			// Copy feature properties
//			for (Feature f : commDriver.getComponentType().getOwnedFeatures()) {
//				commDriverType.getOwnedFeatures().add(EcoreUtil.copy(f));
//				// Make sure data classifier package is in with clause
//				ModelTransformUtils.importContainingPackage(p.getDataFeatureClassifier(), pkgSection);
//				if (f instanceof EventDataPort) {
//					final EventDataPort p = ComponentCreateHelper.createOwnedEventDataPort(commDriverType);
//					p.setName(f.getName());
//					p.setDataFeatureClassifier(((EventDataPort) f).getDataFeatureClassifier());
//					for (ArrayDimension dim : ((EventDataPort) f).getArrayDimensions()) {
//						ArrayDimension arrayDimension = p.createArrayDimension();
//						arrayDimension.setSize(dim.getSize());
//					}
//					// Make sure data classifier package is in with clause
//					ModelTransformUtils.importContainingPackage(p.getDataFeatureClassifier(), pkgSection);
//					p.setIn(((EventDataPort) f).isIn());
//					p.setOut(((EventDataPort) f).isOut());
//				} else if (f instanceof DataPort) {
//					final DataPort p = ComponentCreateHelper.createOwnedDataPort(commDriverType);
//					p.setName(f.getName());
//					p.setDataFeatureClassifier(((DataPort) f).getDataFeatureClassifier());
//					for (ArrayDimension dim : ((DataPort) f).getArrayDimensions()) {
//						ArrayDimension arrayDimension = p.createArrayDimension();
//						arrayDimension.setSize(dim.getSize());
//					}
//					// Make sure data classifier package is in with clause
//					ModelTransformUtils.importContainingPackage(p.getDataFeatureClassifier(), pkgSection);
//					p.setIn(((DataPort) f).isIn());
//					p.setOut(((DataPort) f).isOut());
//				} else if (f instanceof EventPort) {
//					final EventPort p = ComponentCreateHelper.createOwnedEventPort(commDriverType);
//					p.setName(f.getName());
//					p.setIn(((EventPort) f).isIn());
//					p.setOut(((EventPort) f).isOut());
//				} else if (f instanceof FeatureGroup) {
//					final FeatureGroup fg = commDriverType.createOwnedFeatureGroup();
//					fg.setName(f.getName());
//					fg.setIn(((FeatureGroup) f).isIn());
//					fg.setOut(((FeatureGroup) f).isOut());
//					fg.setFeatureType(((FeatureGroup) f).getFeatureGroupType());
//				}
//			}

			// Get the request and response message types from the CASE_Model_Transformations package
//			DataImplementation requestMsgImpl = null;
//			DataImplementation responseMsgImpl = null;
//			final AadlPackage caseModelTransformationsPkg = CaseUtils.getCaseModelTransformationsPackage();
//			for (Classifier classifier : caseModelTransformationsPkg.getOwnedPublicSection().getOwnedClassifiers()) {
//				if (classifier.getName().equalsIgnoreCase(AM_REQUEST_MSG_IMPL_NAME)) {
//					requestMsgImpl = (DataImplementation) classifier;
//				} else if (classifier.getName().equalsIgnoreCase(AM_RESPONSE_MSG_IMPL_NAME)) {
//					responseMsgImpl = (DataImplementation) classifier;
//				}
//			}
			DataSubcomponentType requestMsgImpl = null;
			if (!requestMessageDataType.isEmpty()) {
				requestMsgImpl = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getDataSubcomponentType(),
						requestMessageDataType);
				if (requestMsgImpl == null) {
					// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
					for (Classifier c : pkgSection.getOwnedClassifiers()) {
						if (c.getQualifiedName().equalsIgnoreCase(requestMessageDataType)
								&& c instanceof DataSubcomponentType) {
							requestMsgImpl = (DataSubcomponentType) c;
							break;
						}
					}
				} else {
					ModelTransformUtils.importContainingPackage(requestMsgImpl, pkgSection);
				}
			}

			DataSubcomponentType responseMsgImpl = null;
			if (!responseMessageDataType.isEmpty()) {
				responseMsgImpl = Aadl2GlobalScopeUtil.get(ci, Aadl2Package.eINSTANCE.getDataSubcomponentType(),
						responseMessageDataType);
				if (responseMsgImpl == null) {
					// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
					for (Classifier c : pkgSection.getOwnedClassifiers()) {
						if (c.getQualifiedName().equalsIgnoreCase(responseMessageDataType)
								&& c instanceof DataSubcomponentType) {
							responseMsgImpl = (DataSubcomponentType) c;
							break;
						}
					}
				} else {
					ModelTransformUtils.importContainingPackage(responseMsgImpl, pkgSection);
				}
			}

			if (requestMsgImpl == null || responseMsgImpl == null) {
				final AadlPackage caseModelTransformationsPkg = CaseUtils.getCaseModelTransformationsPackage();
				for (Classifier classifier : caseModelTransformationsPkg.getOwnedPublicSection()
						.getOwnedClassifiers()) {
					if (requestMsgImpl == null && classifier.getName().equalsIgnoreCase(AM_REQUEST_MSG_IMPL_NAME)) {
						requestMsgImpl = (DataSubcomponentType) classifier;
					} else if (responseMsgImpl == null
							&& classifier.getName().equalsIgnoreCase(AM_RESPONSE_MSG_IMPL_NAME)) {
						responseMsgImpl = (DataSubcomponentType) classifier;
					}
				}
				// Import CASE_Model_Transformations file, if necessary
				if (requestMsgImpl != null || responseMsgImpl != null) {
					CaseUtils.addCaseModelTransformationsImport(pkgSection, true);
				}
			}

			// Create attestation request and response ports for communicating with the attestation manager
			final EventDataPort commReq = ComponentCreateHelper
					.createOwnedEventDataPort(commDriverType);
			final EventDataPort commRes = ComponentCreateHelper
					.createOwnedEventDataPort(commDriverType);
			commReq.setDataFeatureClassifier(requestMsgImpl);
			commRes.setDataFeatureClassifier(responseMsgImpl);
			commReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
			commReq.setIn(true);
			commRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
			commRes.setOut(true);

//			// Create attestation request and response ports for communicating with the message sender
//			final EventDataPort commReqEx = ComponentCreateHelper
//					.createOwnedEventDataPort(commDriverType);
//			final EventDataPort commResEx = ComponentCreateHelper
//					.createOwnedEventDataPort(commDriverType);
//			commReqEx.setDataFeatureClassifier(requestMsgImpl);
//			commResEx.setDataFeatureClassifier(responseMsgImpl);
//			commReqEx.setName(AM_PORT_ATTESTATION_REQUEST_NAME_EXTERNAL);
//			commReqEx.setOut(true);
//			commResEx.setName(AM_PORT_ATTESTATION_RESPONSE_NAME_EXTERNAL);
//			commResEx.setIn(true);

//			// Copy the properties
//			copyPropertyAssociations(commDriver.getComponentType(), commDriverType);

			// Copy the annexes
//			Iterator<AnnexSubclause> annexIterator = commDriverType.getOwnedAnnexSubclauses().iterator();
//			while (annexIterator.hasNext()) {
//				AnnexSubclause annex = annexIterator.next();
//				if (annex.getName().equalsIgnoreCase("agree")) {
//					annexIterator.remove();
//					break;
//				}
//			}
//			commDriverType.getOwnedAnnexSubclauses().clear();
//			for (AnnexSubclause annexSubclause : commDriver.getComponentType().getOwnedAnnexSubclauses()) {

			makeAgreeNamesUnique(commDriverType.getOwnedAnnexSubclauses());
//			Iterator<AnnexSubclause> annexIterator = commDriverType.getOwnedAnnexSubclauses().iterator();
//			while (annexIterator.hasNext()) {
//				AnnexSubclause annexSubclause = annexIterator.next();
////				final DefaultAnnexSubclause defaultAnnexSubclause = EcoreUtil.copy(commDriverType.createOwnedAnnexSubclause());
////				defaultAnnexSubclause.setName(annexSubclause.getName());
//
//				if (annexSubclause.getName().equalsIgnoreCase("agree")) {
//
//					// Make sure statement IDs are unique
////					final List<String> specStatements = new ArrayList<>();
//					final AgreeContractSubclause agreeContractSubclause = (AgreeContractSubclause) ((DefaultAnnexSubclause) annexSubclause)
//							.getParsedAnnexSubclause();
////					final AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
////					String specs = unparser.unparseContract((AgreeContract) agreeContractSubclause.getContract(), "");
//					final AgreeContract agreeContract = (AgreeContract) agreeContractSubclause.getContract();
//					for (SpecStatement spec : agreeContract.getSpecs()) {
//						if (spec instanceof NamedSpecStatement) {
//							final NamedSpecStatement namedSpecStatement = (NamedSpecStatement) spec;
//							if (namedSpecStatement.getName() != null && !namedSpecStatement.getName().isEmpty()) {
//								namedSpecStatement.setName(namedSpecStatement.getName() + "_Attestation");
//							}
//						}
//					}
////					for (String spec : specs.split(";")) {
////						String specType = "";
////						if (spec.trim().toLowerCase().startsWith("guarantee")) {
////							specType = "guarantee ";
////						} else if (spec.trim().toLowerCase().startsWith("assume")) {
////							specType = "assume ";
////						} else if (spec.trim().toLowerCase().startsWith("lemma")) {
////							specType = "lemma ";
////						} else if (spec.trim().toLowerCase().startsWith("assert")) {
////							specType = "assert ";
////						}
////						if (!specType.isEmpty()) {
////							String newSpec = "";
////							for (String line : spec.trim().concat(";").split(System.lineSeparator())) {
////								newSpec += line.trim() + " ";
////							}
////
////							String expr = newSpec.substring(newSpec.lastIndexOf("\"") + 1, newSpec.lastIndexOf(";"))
////									.trim();
////							// get rid of : delimiter
////							expr = expr.substring(1).trim();
////							String desc = newSpec.substring(newSpec.indexOf("\""), newSpec.lastIndexOf("\"") + 1)
////									.trim();
////							String id = newSpec
////									.substring(newSpec.toLowerCase().indexOf(specType) + specType.length(),
////											newSpec.indexOf("\""))
////									.trim();
////
////							newSpec = specType;
////							// If spec has an ID, append a suffix to maintain ID uniqueness
////							if (!id.isEmpty()) {
////								newSpec += id + "_Attestation ";
////							}
////							newSpec += desc + " : " + expr + ";";
////
////							specStatements.add(newSpec);
////						} else if (!spec.trim().isEmpty()) {
////							specStatements.add(spec.trim() + ";");
////						}
////					}
////					String agreeClauses = "{**" + System.lineSeparator();
////					for (String s : specStatements) {
////						agreeClauses += s + System.lineSeparator();
////					}
////					agreeClauses += "**}";
////
////					defaultAnnexSubclause.setSourceText(agreeClauses);
//
//					break;
////				} else {
////					defaultAnnexSubclause.setSourceText(((DefaultAnnexSubclause) annexSubclause).getSourceText());
//
//				}
//			}


			// Put just above it's containing implementation
			try {
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// Create extended comm driver implementation
//			final ComponentImplementation commDriverImpl = (ComponentImplementation) pkgSection
//					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			final ComponentImplementation commDriverImpl = EcoreUtil.copy(commDriver.getComponentImplementation());
			pkgSection.getOwnedClassifiers().add(commDriverImpl);
			commDriverImpl.setName(commDriverType.getName() + ".Impl");
			commDriverImpl.getOwnedRealization().setImplemented(commDriverType);
//			final Realization commRealization = commDriverImpl.createOwnedRealization();
//			commRealization.setImplemented(commDriverType);

//			// Copy the subcomponents
//			for (Subcomponent oldSubcomponent : commDriver.getComponentImplementation().getOwnedSubcomponents()) {
//				final Subcomponent newSubcomponent = ComponentCreateHelper.createOwnedSubcomponent(commDriverImpl,
//						oldSubcomponent.getCategory());
//				newSubcomponent.setName(oldSubcomponent.getName());
//				copyPropertyAssociations(oldSubcomponent, newSubcomponent);
//			}
//			// Copy the connections

//			// Copy the properties
//			copyPropertyAssociations(commDriver.getComponentImplementation(), commDriverImpl);

//			// Copy the annexes
//			for (AnnexSubclause annexSubclause : commDriver.getComponentImplementation().getOwnedAnnexSubclauses()) {
//				final DefaultAnnexSubclause defaultAnnexSubclause = commDriverImpl.createOwnedAnnexSubclause();
//				defaultAnnexSubclause.setName(annexSubclause.getName());
//				defaultAnnexSubclause.setSourceText(((DefaultAnnexSubclause) annexSubclause).getSourceText());
//			}

			// Add it to proper place (below extended comm driver type)
			try {
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// If comm driver is an seL4 process,
			// it's thread subcomponent will also need to be copied
			if (isSel4Process) {

				String subName = ((ProcessImplementation) commDriverImpl).getOwnedThreadSubcomponents().get(0)
						.getName();
				((ProcessImplementation) commDriverImpl).getOwnedThreadSubcomponents().clear();
				((ProcessImplementation) commDriverImpl).getOwnedPortConnections().clear();

				ThreadSubcomponent threadSub = Sel4TransformHandler
						.insertThreadInSel4Process((ProcessImplementation) commDriverImpl, null, null);

				threadSub.setName(subName);

				ThreadType commThreadType = (ThreadType) ((ProcessImplementation) commDriver
						.getComponentImplementation())
						.getOwnedThreadSubcomponents().get(0).getComponentType();
				ThreadImplementation commThreadImpl = (ThreadImplementation) ((ProcessImplementation) commDriver
						.getComponentImplementation())
						.getOwnedThreadSubcomponents().get(0).getComponentImplementation();

				// Copy properties
				threadSub.getComponentType().getOwnedPropertyAssociations().clear();
				Sel4TransformHandler.copyPropertyAssociations(commThreadType, threadSub.getComponentType());
				threadSub.getComponentImplementation().getOwnedPropertyAssociations().clear();
				Sel4TransformHandler.copyPropertyAssociations(commThreadImpl, threadSub.getComponentImplementation());

				// Copy AGREE and make names unique
				for (AnnexSubclause annexSubclause : commThreadType.getOwnedAnnexSubclauses()) {
					threadSub.getComponentType().getOwnedAnnexSubclauses().add(EcoreUtil.copy(annexSubclause));
				}
				makeAgreeNamesUnique(threadSub.getComponentType().getOwnedAnnexSubclauses());
				for (AnnexSubclause annexSubclause : commThreadImpl.getOwnedAnnexSubclauses()) {
					threadSub.getComponentImplementation().getOwnedAnnexSubclauses()
							.add(EcoreUtil.copy(annexSubclause));
				}
				makeAgreeNamesUnique(threadSub.getComponentImplementation().getOwnedAnnexSubclauses());

//				ThreadSubcomponent sub = ((ProcessImplementation) commDriver.getComponentImplementation())
//						.getOwnedThreadSubcomponents().get(0);
//				// Copy type
//				ThreadType commThreadType = (ThreadType) EcoreUtil.copy(sub.getComponentType());
//				commThreadType.setName(commThreadType.getName() + "_Attestation");
////				// Create attestation request and response ports for communicating with the attestation manager
////				final EventDataPort commThreadReq = ComponentCreateHelper
////						.createOwnedEventDataPort(commThreadType);
////				final EventDataPort commThreadRes = ComponentCreateHelper
////						.createOwnedEventDataPort(commThreadType);
////				commReq.setDataFeatureClassifier(requestMsgImpl);
////				commRes.setDataFeatureClassifier(responseMsgImpl);
////				commReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
////				commReq.setIn(true);
////				commRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
////				commRes.setOut(true);
//				// Make sure AGREE names are unique
//				makeAgreeNamesUnique(commThreadType.getOwnedAnnexSubclauses());
//				pkgSection.getOwnedClassifiers().add(commThreadType);
//				// Copy impl
//				ThreadImplementation commThreadImpl = (ThreadImplementation) EcoreUtil
//						.copy(sub.getComponentImplementation());
//				commThreadImpl.setName(commThreadType.getName() + ".Impl");
//				pkgSection.getOwnedClassifiers().add(commThreadImpl);
//				// Set thread subcomponent in copied process to this one
//				((ThreadSubcomponent) commDriverImpl.getOwnedSubcomponents().get(0))
//						.setThreadSubcomponentType(commThreadImpl);
			}

			// Create Attestation Manager component type
			final ComponentType attestationManagerType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
			// Give it a unique name
			final String amName = ModelTransformUtils.getUniqueName(attestationManagerComponentName, true,
					pkgSection.getOwnedClassifiers()) + (isSel4Process ? "_seL4" : "");
			attestationManagerType.setName(amName);

			// Add the ports for communicating attestation requests/responses with the Comm Driver
			final EventDataPort amReq = ComponentCreateHelper
					.createOwnedEventDataPort(attestationManagerType);
			final EventDataPort amRes = ComponentCreateHelper
					.createOwnedEventDataPort(attestationManagerType);
			// Set data feature classifier
			amReq.setDataFeatureClassifier(requestMsgImpl);
			amRes.setDataFeatureClassifier(responseMsgImpl);
			amReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
			amReq.setOut(true);
			amRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
			amRes.setIn(true);

			// Create Trusted ID list port
			final EventDataPort amId = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
			DataSubcomponentType idDataFeatureClassifier = null;
			if (!idListDataType.isEmpty()) {
				idDataFeatureClassifier = Aadl2GlobalScopeUtil.get(ci,
						Aadl2Package.eINSTANCE.getDataSubcomponentType(), idListDataType);
				if (idDataFeatureClassifier == null) {
					// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
					for (Classifier c : pkgSection.getOwnedClassifiers()) {
						if (c.getQualifiedName().equalsIgnoreCase(idListDataType)
								&& c instanceof DataSubcomponentType) {
							idDataFeatureClassifier = (DataSubcomponentType) c;
							break;
						}
					}
				} else {
					ModelTransformUtils.importContainingPackage(idDataFeatureClassifier, pkgSection);
				}
				if (idDataFeatureClassifier != null) {
					amId.setDataFeatureClassifier(idDataFeatureClassifier);
				}
			}
			amId.setName(AM_PORT_TRUSTED_IDS_NAME);
			amId.setOut(true);

			// Create log port, if necessary
			if (attestationManagerLogPortType != null) {
				Port logPort = null;
				if (attestationManagerLogPortType == PortCategory.EVENT) {
					logPort = ComponentCreateHelper.createOwnedEventPort(attestationManagerType);
				} else if (attestationManagerLogPortType == PortCategory.DATA) {
					logPort = ComponentCreateHelper.createOwnedDataPort(attestationManagerType);
				} else {
					logPort = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
				}
				logPort.setOut(true);
				logPort.setName(LOG_PORT_NAME);
			}


			// Add Attestation Manager properties
			// CASE_Properties::Component_Type Property
			if (!CasePropertyUtils.setMitigationType(attestationManagerType, MITIGATION_TYPE.ATTESTATION)) {
//				return;
			}

//			// CASE_Properties::Component_Spec property
//			// Parse the ID from the Attestation Gate AGREE property
//			String attestationManagerPropId = "";
//			try {
//				attestationManagerPropId = attestationManagerAgreeProperty.substring(
//						attestationManagerAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
//						attestationManagerAgreeProperty.indexOf("\"")).trim();
//			} catch (IndexOutOfBoundsException e) {
//				if (!attestationManagerAgreeProperty.isEmpty()) {
//					// agree property is malformed, so leave blank
//					Dialog.showWarning("Add Attestation", "Attestation Manager AGREE statement is malformed.");
//				}
//			}
//			if (!attestationManagerPropId.isEmpty()) {
//				if (!CasePropertyUtils.setCompSpec(attestationManagerType, attestationManagerPropId)) {
////					return;
//				}
//			}


			// Move attestation manager to top of file
//			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
			try {
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// Create Attestation Manager implementation
			final ComponentImplementation attestationManagerImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			attestationManagerImpl.setName(amName + ".Impl");
			final Realization r = attestationManagerImpl.createOwnedRealization();
			r.setImplemented(attestationManagerType);

			// Add it to proper place (just below component type)
			try {
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// CASE_Properties::CACHE_TIMEOUT property
			if (cacheTimeout > 0) {
				if (!CasePropertyUtils.setCacheTimeout(attestationManagerImpl, cacheTimeout)) {
//					return;
				}
			}

//			// CASE_Properties::CACHE_SIZE property
//			if (cacheSize > 0) {
//				if (!CasePropertyUtils.setCacheSize(attestationManagerImpl, cacheSize)) {
////					return;
//				}
//			}

			// Dispatch protocol property
			if (compCategory == ComponentCategory.THREAD && !attestationManagerDispatchProtocol.isEmpty()) {
				final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(attestationManagerDispatchProtocol);
				final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				attestationManagerImpl.setPropertyValue(dispatchProtocolProp, nv);
			}

			// Period
			if (!attestationManagerPeriod.isEmpty()) {
				final Property periodProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
						TimingProperties._NAME, TimingProperties.PERIOD);
				final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
				final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
				unit.setName(attestationManagerPeriod.replaceAll("[\\d]", "").trim());
				periodLit.setBase(0);
				periodLit.setValue(Long.parseLong(attestationManagerPeriod.replaceAll("[\\D]", "").trim()));
				periodLit.setUnit(unit);
				attestationManagerImpl.setPropertyValue(periodProp, periodLit);
			}

			// Language and Source Text
			if (useKUImplementation) {
				CasePropertyUtils.setCompLanguage(attestationManagerImpl, "CakeML");

				final Property sourceTextProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
						ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);
				final StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();

				IProject project = TraverseProject.getCurrentProject();
				String attestationImplPath = project.getLocation() + File.separator + Activator.getDefault()
						.getPreferenceStore().getString(BriefcasePreferenceConstants.KU_IMPL_FOLDER) + File.separator;
				// TODO: Add file name(s) to impl path
				sourceTextLit.setValue(attestationImplPath);
				final List<StringLiteral> listVal = new ArrayList<>();
				listVal.add(sourceTextLit);
				attestationManagerImpl.setPropertyValue(sourceTextProp, listVal);
			}

			// Replace the comm driver with the extended comm driver
			for (Subcomponent sub : ci.getOwnedSubcomponents()) {
				if (sub.getName().equalsIgnoreCase(commDriver.getName())) {
					ComponentCreateHelper.setSubcomponentType(sub, commDriverImpl);
					break;
				}
			}

			// Insert attestation manager in component implementation
			final Subcomponent attestationManagerSubcomp = ComponentCreateHelper.createOwnedSubcomponent(ci,
					compCategory);

			// Give it a unique name
			attestationManagerSubcomp
					.setName(ModelTransformUtils.getUniqueName(attestationManagerSubcomponentName, true,
							ci.getAllSubcomponents()));
			// Assign thread implementation
			ComponentCreateHelper.setSubcomponentType(attestationManagerSubcomp, attestationManagerImpl);

			// Add the Attestation Gate type
			final ComponentType attestationGateType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
			// Give it a unique name
			final String agName = ModelTransformUtils.getUniqueName(attestationGateComponentName, true,
					pkgSection.getOwnedClassifiers()) + (isSel4Process ? "_seL4" : "");
			attestationGateType.setName(agName);

			// Create Attestation Gate ports
			// To do this we need to look at the current connections of the comm driver in the implementation
			// However, we do not want to consider the outports that lead to the comm bus
			// We also only want to consider a connection once (in the case of fan out)
			// Also, save the port names, for use later
			final List<String> agPortNames = new ArrayList<>();
			for (Connection conn : ci.getOwnedConnections()) {
				if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {

					// Ignore if the connection destination is an attestation manager
					if (conn.getDestination().getContext() instanceof Subcomponent) {
						if (CasePropertyUtils.hasMitigationType(
								((Subcomponent) conn.getDestination().getContext()).getClassifier(),
								MITIGATION_TYPE.ATTESTATION)) {
							continue;
						}
					}

					final ConnectionEnd commPort = conn.getSource().getConnectionEnd();
					if (agPortNames.contains(commPort.getName())) {
						continue;
					}
					ConnectionEnd connEndIn = null;
					ConnectionEnd connEndOut = null;
					NamedElement featureClassifier = null;
					if (commPort instanceof EventDataPort) {
						connEndIn = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
						featureClassifier = ((EventDataPort) commPort).getDataFeatureClassifier();
						((EventDataPort) connEndIn).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
						for (ArrayDimension dim : ((EventDataPort) commPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventDataPort) connEndIn).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
						connEndOut = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
						((EventDataPort) connEndOut).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
						for (ArrayDimension dim : ((EventDataPort) commPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventDataPort) connEndOut).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (commPort instanceof DataPort) {
						connEndIn = ComponentCreateHelper.createOwnedDataPort(attestationGateType);
						featureClassifier = ((DataPort) commPort).getDataFeatureClassifier();
						((DataPort) connEndIn).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
						for (ArrayDimension dim : ((DataPort) commPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((DataPort) connEndIn).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
						connEndOut = ComponentCreateHelper.createOwnedDataPort(attestationGateType);
						((DataPort) connEndOut).setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
						for (ArrayDimension dim : ((DataPort) commPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((DataPort) connEndOut).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (commPort instanceof EventPort) {
						connEndIn = ComponentCreateHelper.createOwnedEventPort(attestationGateType);
						for (ArrayDimension dim : ((EventPort) commPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventPort) connEndIn).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
						connEndOut = ComponentCreateHelper.createOwnedEventPort(attestationGateType);
						for (ArrayDimension dim : ((EventPort) commPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventPort) connEndOut).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (commPort instanceof FeatureGroup) {
						final FeatureGroupType featureClassifierIn = ((FeatureGroup) conn.getDestination()
								.getConnectionEnd())
								.getFeatureGroupType();
						connEndIn = attestationGateType.createOwnedFeatureGroup();
						((FeatureGroup) connEndIn).setFeatureType(featureClassifierIn);
						ModelTransformUtils.importContainingPackage(featureClassifierIn, pkgSection);
						featureClassifier = ((FeatureGroup) commPort).getFeatureGroupType();
						connEndOut = attestationGateType.createOwnedFeatureGroup();
						((FeatureGroup) connEndOut).setFeatureType((FeatureGroupType) featureClassifier);
					}

					((DirectedFeature) connEndIn).setIn(true);
					((DirectedFeature) connEndOut).setOut(true);

					final List<String> names = attestationGatePortNames.get(commPort.getName());
					if (names != null) {
						connEndIn.setName(names.get(0));
						connEndOut.setName(names.get(1));
					} else {
						connEndIn.setName(commPort.getName() + "_in");
						connEndOut.setName(commPort.getName() + "_out");
					}

					agPortNames.add(commPort.getName());

					// The data subcomponent type could be in a different package.
					// Make sure to include it in the with clause
					ModelTransformUtils.importContainingPackage(featureClassifier, pkgSection);

				}
			}
			// Create Trusted ID List port
			final EventDataPort agId = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
			if (!idListDataType.isEmpty() && idDataFeatureClassifier != null) {
				agId.setDataFeatureClassifier(idDataFeatureClassifier);
			}
			agId.setName(AM_PORT_TRUSTED_IDS_NAME);
			agId.setIn(true);

			// Create log port, if necessary
			if (attestationGateLogPortType != null) {
				Port logPort = null;
				if (attestationGateLogPortType == PortCategory.EVENT) {
					logPort = ComponentCreateHelper.createOwnedEventPort(attestationGateType);
				} else if (attestationGateLogPortType == PortCategory.DATA) {
					logPort = ComponentCreateHelper.createOwnedDataPort(attestationGateType);
				} else {
					logPort = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
				}
				logPort.setOut(true);
				logPort.setName(LOG_PORT_NAME);
			}

			// Add Attestation Gate properties
			// CASE_Properties::Component_Type Property
			if (!CasePropertyUtils.setMitigationType(attestationGateType, MITIGATION_TYPE.GATE)) {
//				return;
			}

//			// CASE_Properties::Component_Spec property
//			String attestationGatePropId = "";
//			for (String commPortName : attestationGatePortNames.keySet()) {
//				if (!attestationGatePropId.isEmpty()) {
//					attestationGatePropId += ",";
//				}
//				if (attestationGatePortNames.get(commPortName).isEmpty()) {
//					attestationGatePropId += attestationGateType.getName() + "_" + commPortName + "_out";
//				} else {
//					attestationGatePropId += attestationGateType.getName() + "_"
//							+ attestationGatePortNames.get(commPortName).get(1);
//				}
//			}
//			if (!attestationGatePropId.isEmpty()) {
//				if (!CasePropertyUtils.setCompSpec(attestationGateType, attestationGatePropId)) {
////					return;
//				}
//			}

			// Move attestation gate to top of file
//			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
			try {
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// Create Attestation Gate implementation
			final ComponentImplementation attestationGateImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			attestationGateImpl.setName(agName + ".Impl");
			final Realization gateRealization = attestationGateImpl.createOwnedRealization();
			gateRealization.setImplemented(attestationGateType);

			// Add it to proper place (just below component type)
//			pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
			try {
				pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);
			} catch (Exception e) {

			}

			// Dispatch protocol property
			if (compCategory == ComponentCategory.THREAD && !attestationGateDispatchProtocol.isEmpty()) {
				final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(attestationGateImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(attestationGateDispatchProtocol);
				final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				attestationGateImpl.setPropertyValue(dispatchProtocolProp, nv);
			}

			// Period
			if (!attestationGatePeriod.isEmpty()) {
				final Property periodProp = GetProperties.lookupPropertyDefinition(attestationGateImpl,
						TimingProperties._NAME, TimingProperties.PERIOD);
				final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
				final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
				unit.setName(attestationGatePeriod.replaceAll("[\\d]", "").trim());
				periodLit.setBase(0);
				periodLit.setValue(Long.parseLong(attestationGatePeriod.replaceAll("[\\D]", "").trim()));
				periodLit.setUnit(unit);
				attestationGateImpl.setPropertyValue(periodProp, periodLit);
			}

			// Insert attestation gate in component implementation
			final Subcomponent attestationGateSubcomp = ComponentCreateHelper.createOwnedSubcomponent(ci, compCategory);

			// Give it a unique name
			attestationGateSubcomp.setName(
					ModelTransformUtils.getUniqueName(attestationGateSubcomponentName, true,
							ci.getAllSubcomponents()));
			// Assign implementation
			ComponentCreateHelper.setSubcomponentType(attestationGateSubcomp, attestationGateImpl);

			// TODO: Handle feature group connections
			final List<PortConnection> newPortConns = new ArrayList<>();
			// Create new connections between comm driver / attestation gate / destination components
			int connIdx = -1;
			for (int i = 0; i < ci.getOwnedPortConnections().size(); i++) {
				final PortConnection conn = ci.getOwnedPortConnections().get(i);
				// Ignore bus connections (destination context not null)
				if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {

					// Ignore if the connection destination is an attestation manager
					if (conn.getDestination().getContext() instanceof Subcomponent) {
						if (CasePropertyUtils.hasMitigationType(
								((Subcomponent) conn.getDestination().getContext()).getClassifier(),
								MITIGATION_TYPE.ATTESTATION)) {
							continue;
						}
					}

					// In order to put new connections in right place, keep track of
					// the first relevant comm driver connection name
					if (connIdx < 0) {
						connIdx = i;
					}

					// Rewire existing connection sources to be the attestation gate
					final String featureName = conn.getSource().getConnectionEnd().getName();
					final ConnectionEnd connEnd = conn.getSource().getConnectionEnd();
					conn.getSource().setContext(attestationGateSubcomp);
					final List<String> featureNames = attestationGatePortNames.getOrDefault(featureName,
							new ArrayList<>(Arrays.asList(featureName + "_in", featureName + "_out")));
					for (Feature feature : attestationGateType.getOwnedFeatures()) {
						if (feature.getName().equalsIgnoreCase(featureNames.get(1))) {
							conn.getSource().setConnectionEnd(feature);
							break;
						}
					}

					// Create connections from comm driver to attestation manager
					// Don't create extra connections if a comm driver feature is fan out
					boolean connExists = false;
					for (PortConnection c : newPortConns) {
						if (c.getSource().getConnectionEnd().getName().equalsIgnoreCase(featureName)) {
							connExists = true;
							break;
						}
					}
					if (connExists) {
						continue;
					}

//					PortConnection connOut = null;
//					if (conn instanceof PortConnection) {
//						connOut = Aadl2Factory.eINSTANCE.createPortConnection();
//					} else if (conn instanceof FeatureGroupConnection) {
//						connOut = Aadl2Factory.eINSTANCE.createFeatureGroupConnection();
//					} else {
//						// TODO: Handle this
//						continue;
//					}
					final PortConnection connOut = Aadl2Factory.eINSTANCE.createPortConnection();
					connOut.setBidirectional(false);
					final ConnectedElement connSrc = connOut.createSource();
					connSrc.setContext(commDriver);
					connSrc.setConnectionEnd(connEnd);

					final ConnectedElement connDst = connOut.createDestination();
					connDst.setContext(attestationGateSubcomp);
					for (Feature feature : attestationGateType.getOwnedFeatures()) {
						if (feature.getName().equalsIgnoreCase(featureNames.get(0))) {
							connDst.setConnectionEnd(feature);
							break;
						}
					}

					newPortConns.add(connOut);
				}
			}

			// Create trusted id list connection between Attestation Manager and Attestation Gate
			final PortConnection portConnId = Aadl2Factory.eINSTANCE.createPortConnection();
			portConnId.setBidirectional(false);
			final ConnectedElement idSrc = portConnId.createSource();
			idSrc.setContext(attestationManagerSubcomp);
			idSrc.setConnectionEnd(amId);
			final ConnectedElement idDst = portConnId.createDestination();
			idDst.setContext(attestationGateSubcomp);
			idDst.setConnectionEnd(agId);
			newPortConns.add(portConnId);

			// Create attestation request / response connections between comm driver and attestation manager
			final PortConnection portConnReq = Aadl2Factory.eINSTANCE.createPortConnection();
			portConnReq.setBidirectional(false);
			final ConnectedElement reqSrc = portConnReq.createSource();
			reqSrc.setContext(attestationManagerSubcomp);
			reqSrc.setConnectionEnd(amReq);
			final ConnectedElement reqDst = portConnReq.createDestination();
			reqDst.setContext(commDriver);
			reqDst.setConnectionEnd(commReq);
			newPortConns.add(portConnReq);

			final PortConnection portConnRes = Aadl2Factory.eINSTANCE.createPortConnection();
			portConnRes.setBidirectional(false);
			final ConnectedElement resSrc = portConnRes.createSource();
			resSrc.setContext(commDriver);
			resSrc.setConnectionEnd(amRes);
			final ConnectedElement resDst = portConnRes.createDestination();
			resDst.setContext(attestationManagerSubcomp);
			resDst.setConnectionEnd(commRes);
			newPortConns.add(portConnRes);

			int idxOffset = 0;
			for (PortConnection newConn : newPortConns) {
				// Make sure each new connection has a unique name
				newConn.setName(
						ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false, ci.getAllConnections()));
				ci.getOwnedPortConnections().add(newConn);
				// Move to right place
				try {
					ci.getOwnedPortConnections().move(connIdx + idxOffset, ci.getOwnedPortConnections().size() - 1);
					idxOffset++;
				} catch (Exception e) {

				}
				// TODO: THIS DOES NOT WORK. If a PortConnection is added, the size of OwnedConnections is not incremented
//				ci.getOwnedConnections().move(connIdx + idxOffset, ci.getOwnedConnections().size() - 1);

			}

//			// Add AGREE statements
//			StringBuilder agreeClauses = new StringBuilder();
//			agreeClauses.append("{**" + System.lineSeparator());
//
//			agreeClauses.append("fun IS_TRUSTED(srcid : " + idListDataType + ") : bool = (exists id in "
//					+ AM_PORT_TRUSTED_IDS_NAME + ", (id = srcid));" + System.lineSeparator());
//			agreeClauses.append("eq selection : int =" + System.lineSeparator());
//			// TODO: Feature groups
//			for (Feature f : attestationGateType.getOwnedFeatures()) {
//				if (f instanceof EventPort || f instanceof EventDataPort) {
//					if (((Port)f).isIn() && !f.getName().equalsIgnoreCase(AM_PORT_TRUSTED_IDS_NAME)) {
//						agreeClauses.append("if event(" + f.getName() + ") and IS_TRUSTED()");
//					}
//				} else if (f instanceof DataPort) {
//
//				}
//			}
//
//			agreeClauses.append("**}");
//
//			final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
//					.createOwnedAnnexSubclause(attestationGateType);
//			annexSubclauseImpl.setName("agree");
//			annexSubclauseImpl.setSourceText(agreeClauses.toString());


			if (isSel4Process) {

				Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) attestationManagerImpl,
						attestationManagerDispatchProtocol, null);

				Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) attestationGateImpl,
						attestationGateDispatchProtocol, null);

			}

			// Add add_attestation claims to resolute prove statement, if applicable
			if (!attestationRequirement.isEmpty()) {
				final CyberRequirement req = RequirementsManager.getInstance().getRequirement(attestationRequirement);
				return new AddAttestationClaim(req.getContext(), commDriver, attestationManagerSubcomp,
						attestationGateSubcomp);
			}
			return null;
		});

		// Add add_attestation claims to resolute prove statement, if applicable
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(attestationRequirement, claim);
		}

	}

	private void makeAgreeNamesUnique(List<AnnexSubclause> annexSubclauses) {
		Iterator<AnnexSubclause> annexIterator = annexSubclauses.iterator();
		while (annexIterator.hasNext()) {
			AnnexSubclause annexSubclause = annexIterator.next();

			if (annexSubclause.getName().equalsIgnoreCase("agree")) {

				// Make sure statement IDs are unique
				final AgreeContractSubclause agreeContractSubclause = (AgreeContractSubclause) ((DefaultAnnexSubclause) annexSubclause)
						.getParsedAnnexSubclause();
				final AgreeContract agreeContract = (AgreeContract) agreeContractSubclause.getContract();
				for (SpecStatement spec : agreeContract.getSpecs()) {
					if (spec instanceof NamedSpecStatement) {
						final NamedSpecStatement namedSpecStatement = (NamedSpecStatement) spec;
						if (namedSpecStatement.getName() != null && !namedSpecStatement.getName().isEmpty()) {
							namedSpecStatement.setName(namedSpecStatement.getName() + "_Attestation");
						}
					}
				}
				break;
			}
		}
	}


//	private Subcomponent getAttestationManager(Subcomponent comp) {
//
//		final ComponentImplementation ci = comp.getContainingComponentImpl();
//
//		// Look at each connection in the subcomponent's containing implementation
//		for (Connection conn : ci.getAllConnections()) {
//
//			// Get the source component of the connection
//			NamedElement ne = conn.getAllSrcContextComponent();
//			// If source component is the specified subcomponent, get the destination component of the connection
//			if (ne instanceof Subcomponent && ne.getName().equalsIgnoreCase(comp.getName())) {
//				ne = conn.getAllDstContextComponent();
//				Subcomponent dst = null;
//				if (ne instanceof Subcomponent) {
//					dst = (Subcomponent) ne;
//					// Check if it's an attestation manager
//					if (CasePropertyUtils.hasMitigationType(dst.getClassifier(), MITIGATION_TYPE.ATTESTATION)) {
//						return dst;
//					}
//				}
//			}
//
//		}
//
//		return null;
//	}

//	private Subcomponent getAttestationGate(Subcomponent attestationManager) {
//
//		final ComponentImplementation ci = attestationManager.getContainingComponentImpl();
//
//		// Look at each connection in the attestation manager's containing implementation
//		for (Connection conn : ci.getAllConnections()) {
//			// Get the source component of the connection
//			NamedElement ne = conn.getAllSrcContextComponent();
//			if (ne instanceof Subcomponent && ne.getName().equalsIgnoreCase(attestationManager.getName())) {
//				ne = conn.getAllDstContextComponent();
//				Subcomponent dst = null;
//				if (ne instanceof Subcomponent) {
//					dst = (Subcomponent) ne;
//					// Check if it's a switch
//					if (CasePropertyUtils.hasMitigationType(dst.getClassifier(), MITIGATION_TYPE.GATE)) {
//						return dst;
//					}
//				}
//			}
//		}
//
//		return null;
//	}

//	/**
//	 * Copies property associations from one AADL named element to another.
//	 * Only valid property associations accepted by the 'to' element will be copied
//	 * @param from
//	 * @param to
//	 */
//	private void copyPropertyAssociations(NamedElement from, NamedElement to) {
//		if (to == null || from == null) {
//			return;
//		}
//		for (PropertyAssociation pa : from.getOwnedPropertyAssociations()) {
//			final PropertyAssociation propAssoc = EcoreUtil.copy(pa);
//			final Property prop = propAssoc.getProperty();
//			if (to.acceptsProperty(prop)) {
//				// TODO: Make sure property package is in with clause?
//				to.getOwnedPropertyAssociations().add(propAssoc);
//			} else {
//				// TODO: log exception
//			}
//		}
//	}

}
