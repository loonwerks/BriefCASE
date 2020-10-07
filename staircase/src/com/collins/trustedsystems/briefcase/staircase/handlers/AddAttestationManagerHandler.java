package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.List;

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
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.AddAttestationManagerDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddAttestationManagerClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.unparsing.AgreeAnnexUnparser;

public class AddAttestationManagerHandler extends AadlHandler {

	static final String AM_REQUEST_MSG_NAME = "CASE_AttestationRequestMsg";
	static final String AM_RESPONSE_MSG_NAME = "CASE_AttestationResponseMsg";
	static final String AM_REQUEST_MSG_IMPL_NAME = "CASE_AttestationRequestMsg.Impl";
	static final String AM_RESPONSE_MSG_IMPL_NAME = "CASE_AttestationResponseMsg.Impl";
	public static final String AM_COMP_TYPE_NAME = "CASE_AttestationManager";
	public static final String AG_COMP_TYPE_NAME = "CASE_AttestationGate";
	public static final String AM_LOG_PORT_NAME = "LogMessage";
	static final String AM_PORT_ATTESTATION_REQUEST_NAME = "AttestationRequest";
	static final String AM_PORT_ATTESTATION_RESPONSE_NAME = "AttestationResponse";
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
//	private String attestationManagerImplLanguage;
//	private String attestationGateImplLanguage;
	private String dispatchProtocol;
	private long cacheTimeout;
	private long cacheSize;
	private String idListDataType;
	private PortCategory logPortType;
	private String attestationRequirement;
	private boolean propagateGuarantees;
	private String attestationAgreeProperty;

	@Override
	protected void runCommand(URI uri) {

		// Check if selection is a subcomponent
		final EObject eObj = getEObject(uri);
		Subcomponent selectedSubcomponent = null;
		if (eObj instanceof Subcomponent) {
			selectedSubcomponent = (Subcomponent) eObj;
		} else {
			Dialog.showError("Add Attestation",
					"A communication driver subcomponent must be selected to add an attestation manager.");
			return;
		}

		// Check if selected subcomponent is a comm driver
		if (!CasePropertyUtils.isCompType(selectedSubcomponent.getClassifier(), "COMM_DRIVER")) {
			Dialog.showError("Add Attestation",
					"A communication driver subcomponent must be selected to add attestation.");
			return;
		}

		// Check if the selected subcomponent already has an attestation manager connected
		// If there is, ask the user if they would like to associate the attestation manager with another requirement
		Subcomponent attestationManager = getAttestationManager(selectedSubcomponent);
		Subcomponent attestationGate = null;
		if (attestationManager != null) {
			if (!Dialog.askQuestion("Add Attestation", selectedSubcomponent.getName()
					+ " already has associated attestation components. Would you like to associate them with a new requirement?")) {
				return;
			}
			attestationGate = getAttestationGate(attestationManager);
		}

		// Open wizard to enter filter info
		final AddAttestationManagerDialog wizard = new AddAttestationManagerDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		List<String> importedRequirements = new ArrayList<>();
		RequirementsManager.getInstance().getImportedRequirements().forEach(r -> importedRequirements.add(r.getId()));
		wizard.create(selectedSubcomponent, importedRequirements, attestationManager, attestationGate);
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
//			attestationManagerImplLanguage = wizard.getAttestationManagerImplLanguage();
//			attestationGateImplLanguage = wizard.getAttestationManagerImplLanguage();
			dispatchProtocol = wizard.getDispatchProtocol();
			cacheTimeout = wizard.getCacheTimeout();
			cacheSize = wizard.getCacheSize();
			idListDataType = wizard.getIdListDataType();
			logPortType = wizard.getLogPortType();
			attestationRequirement = wizard.getRequirement();
			propagateGuarantees = wizard.getPropagateGuarantees();
			attestationAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the attestation manager
		if (attestationManager != null) {
			associateNewRequirement(EcoreUtil.getURI(selectedSubcomponent), EcoreUtil.getURI(attestationManager),
					EcoreUtil.getURI(attestationGate));
			BriefcaseNotifier.notify("StairCASE - Attestation", "New requirement associated with Attestation Manager.");
		} else {
			insertAttestationManager(uri);
			BriefcaseNotifier.notify("StairCASE - Attestation", "Attestation added to model.");
		}

		// Save
		saveChanges(false);

		return;

	}

	private void associateNewRequirement(URI commDriverUri, URI attestationManagerUri, URI attestationGateUri) {
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		AddAttestationManagerClaim claim = xtextEditor.getDocument().modify(resource -> {

			Subcomponent commDriver = (Subcomponent) resource.getEObject(commDriverUri.fragment());
			Subcomponent attestationManager = (Subcomponent) resource.getEObject(attestationManagerUri.fragment());
			Subcomponent attestationGate = (Subcomponent) resource.getEObject(attestationGateUri.fragment());

			// Add add_attestation claims to resolute prove statement, if applicable
			if (!attestationRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(attestationRequirement);
				return new AddAttestationManagerClaim(req.getContext(), commDriver, attestationManager,
						attestationGate);
			}

			return null;
		});

		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(attestationRequirement, claim);
		}
	}

	/**
	 * Inserts an attestation manager component into the model.  The attestation manager is inserted at
	 * the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private void insertAttestationManager(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		AddAttestationManagerClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			Subcomponent commDriver = (Subcomponent) resource.getEObject(uri.fragment());
			// Get comm driver component type
			ComponentType selectedCommDriverType = commDriver.getComponentType();
			// Get containing component implementation
			ComponentImplementation ci = commDriver.getContainingComponentImpl();

			final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
			PackageSection pkgSection = null;
			// Figure out if the comm driver's containing implementation is in the public or private section
			EObject eObj = commDriver.eContainer();
			while (eObj != null) {
				if (eObj instanceof PublicPackageSection) {
					pkgSection = aadlPkg.getOwnedPublicSection();
					break;
				} else if (eObj instanceof PrivatePackageSection) {
					pkgSection = aadlPkg.getOwnedPrivateSection();
					break;
				} else {
					eObj = eObj.eContainer();
				}
			}

			if (pkgSection == null) {
				// Something went wrong
				Dialog.showError("Add Attestation", "No public or private package sections found.");
				return null;
			}

			// Import CASE_Properties file
			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}
			// Import CASE_Model_Transformations file
			if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
				return null;
			}

			ComponentCategory compCategory = commDriver.getCategory();
			// If the component type is a process, we will need to put a single thread inside.
			// Per convention, we will attach all properties and contracts to the thread.
			// For this model transformation, we will create the thread first, then wrap it in a process
			// component, using the same mechanism we use for the seL4 transformation
			final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
			if (isProcess) {
				compCategory = ComponentCategory.THREAD;
			}

			// Create new comm driver component that copies the selected one
			final ComponentType commDriverType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
			// Give it a unique name
			commDriverType.setName(
					ModelTransformUtils.getUniqueName(commDriver.getComponentType().getName() + "_Attestation", true,
					pkgSection.getOwnedClassifiers()));
//			commDriverType.setExtended(commDriver.getComponentType());

			// Copy the features
			for (Feature f : commDriver.getComponentType().getOwnedFeatures()) {
				if (f instanceof EventDataPort) {
					EventDataPort p = ComponentCreateHelper.createOwnedEventDataPort(commDriverType);
					p.setName(f.getName());
					p.setDataFeatureClassifier(((EventDataPort) f).getDataFeatureClassifier());
					// Make sure data classifier package is in with clause
					ModelTransformUtils.importContainingPackage(p.getDataFeatureClassifier(), pkgSection);
					p.setIn(((EventDataPort) f).isIn());
					p.setOut(((EventDataPort) f).isOut());
				} else if (f instanceof DataPort) {
					DataPort p = ComponentCreateHelper.createOwnedDataPort(commDriverType);
					p.setName(f.getName());
					p.setDataFeatureClassifier(((DataPort) f).getDataFeatureClassifier());
					// Make sure data classifier package is in with clause
					ModelTransformUtils.importContainingPackage(p.getDataFeatureClassifier(), pkgSection);
					p.setIn(((DataPort) f).isIn());
					p.setOut(((DataPort) f).isOut());
				} else if (f instanceof EventPort) {
					EventPort p = ComponentCreateHelper.createOwnedEventPort(commDriverType);
					p.setName(f.getName());
					p.setIn(((EventPort) f).isIn());
					p.setOut(((EventPort) f).isOut());
				}
			}

			// Get the request and response message types from the CASE_Model_Transformations package
			DataImplementation requestMsgImpl = null;
			DataImplementation responseMsgImpl = null;
			AadlPackage caseModelTransformationsPkg = CaseUtils.getCaseModelTransformationsPackage();
			for (Classifier classifier : caseModelTransformationsPkg.getOwnedPublicSection().getOwnedClassifiers()) {
				if (classifier.getName().equalsIgnoreCase(AM_REQUEST_MSG_IMPL_NAME)) {
					requestMsgImpl = (DataImplementation) classifier;
				} else if (classifier.getName().equalsIgnoreCase(AM_RESPONSE_MSG_IMPL_NAME)) {
					responseMsgImpl = (DataImplementation) classifier;
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

			// Copy the properties
			copyPropertyAssociations(commDriver.getComponentType(), commDriverType);

			// Copy the annexes
			for (AnnexSubclause annexSubclause : commDriver.getComponentType().getOwnedAnnexSubclauses()) {
				DefaultAnnexSubclause defaultAnnexSubclause = commDriverType.createOwnedAnnexSubclause();
				defaultAnnexSubclause.setName(annexSubclause.getName());
				if (annexSubclause.getName().equalsIgnoreCase("agree")) {

					// Make sure statement IDs are unique
					List<String> specStatements = new ArrayList<>();
					AgreeContractSubclause agreeContractSubclause = (AgreeContractSubclause) ((DefaultAnnexSubclause) annexSubclause)
							.getParsedAnnexSubclause();
					AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
					String specs = unparser.unparseContract((AgreeContract) agreeContractSubclause.getContract(), "");
					for (String spec : specs.split(";")) {
						String specType = "";
						if (spec.trim().toLowerCase().startsWith("guarantee")) {
							specType = "guarantee ";
						} else if (spec.trim().toLowerCase().startsWith("assume")) {
							specType = "assume ";
						} else if (spec.trim().toLowerCase().startsWith("lemma")) {
							specType = "lemma ";
						} else if (spec.trim().toLowerCase().startsWith("assert")) {
							specType = "assert ";
						}
						if (!specType.isEmpty()) {
							String newSpec = "";
							for (String line : spec.trim().concat(";").split(System.lineSeparator())) {
								newSpec += line.trim() + " ";
							}

							String expr = newSpec.substring(newSpec.lastIndexOf("\"") + 1, newSpec.lastIndexOf(";"))
									.trim();
							// get rid of : delimiter
							expr = expr.substring(1).trim();
							String desc = newSpec.substring(newSpec.indexOf("\""), newSpec.lastIndexOf("\"") + 1)
									.trim();
							String id = newSpec
									.substring(newSpec.toLowerCase().indexOf(specType) + specType.length(),
											newSpec.indexOf("\""))
									.trim();

							newSpec = specType;
							// If spec has an ID, append a suffix to maintain ID uniqueness
							if (!id.isEmpty()) {
								newSpec += id + "_Attestation ";
							}
							newSpec += desc + " : " + expr + ";";

							specStatements.add(newSpec);
						} else if (!spec.trim().isEmpty()) {
							specStatements.add(spec + ";");
						}
					}
					String agreeClauses = "{**" + System.lineSeparator();
					for (String s : specStatements) {
						agreeClauses += s + System.lineSeparator();
					}
					agreeClauses += "**}";
					defaultAnnexSubclause.setSourceText(agreeClauses);
				} else {
					defaultAnnexSubclause.setSourceText(((DefaultAnnexSubclause) annexSubclause).getSourceText());
				}
			}

			// Put just above it's containing implementation
			pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);

			// Create extended comm driver implementation
			final ComponentImplementation commDriverImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			commDriverImpl.setName(commDriverType.getName() + ".Impl");
//			commDriverImpl.setExtended(commDriver.getComponentImplementation());
			final Realization commRealization = commDriverImpl.createOwnedRealization();
			commRealization.setImplemented(commDriverType);

			// Copy the subcomponents
			for (Subcomponent oldSubcomponent : commDriver.getComponentImplementation().getOwnedSubcomponents()) {
				Subcomponent newSubcomponent = ComponentCreateHelper.createOwnedSubcomponent(commDriverImpl,
						oldSubcomponent.getCategory());
				newSubcomponent.setName(oldSubcomponent.getName());
				copyPropertyAssociations(oldSubcomponent, newSubcomponent);
			}
			// TODO: Copy the connections

			// Copy the properties
			copyPropertyAssociations(commDriver.getComponentImplementation(), commDriverImpl);

			// Copy the annexes
			for (AnnexSubclause annexSubclause : commDriver.getComponentImplementation().getOwnedAnnexSubclauses()) {
				DefaultAnnexSubclause defaultAnnexSubclause = commDriverImpl.createOwnedAnnexSubclause();
				defaultAnnexSubclause.setName(annexSubclause.getName());
				defaultAnnexSubclause.setSourceText(((DefaultAnnexSubclause) annexSubclause).getSourceText());
			}

			// Add it to proper place (below extended comm driver type)
			pkgSection.getOwnedClassifiers().move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
					pkgSection.getOwnedClassifiers().size() - 1);

			// Create Attestation Manager component type
			final ComponentType attestationManagerType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
			// Give it a unique name
			attestationManagerType.setName(
					ModelTransformUtils.getUniqueName(attestationManagerComponentName, true,
							pkgSection.getOwnedClassifiers()));

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
			if (!idListDataType.isEmpty()) {
				DataSubcomponentType dataFeatureClassifier = Aadl2GlobalScopeUtil.get(ci,
						Aadl2Package.eINSTANCE.getDataSubcomponentType(), idListDataType);
				if (dataFeatureClassifier == null) {
					// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
					for (Classifier c : pkgSection.getOwnedClassifiers()) {
						if (c.getQualifiedName().equalsIgnoreCase(idListDataType)
								&& c instanceof DataSubcomponentType) {
							dataFeatureClassifier = (DataSubcomponentType) c;
							break;
						}
					}
				}
				if (dataFeatureClassifier != null) {
					amId.setDataFeatureClassifier(dataFeatureClassifier);
				}
			}
			amId.setName(AM_PORT_TRUSTED_IDS_NAME);
			amId.setOut(true);

			// Create log port, if necessary
			if (logPortType != null) {
				Port logPort = null;
				if (logPortType == PortCategory.EVENT) {
					logPort = ComponentCreateHelper.createOwnedEventPort(attestationManagerType);
				} else if (logPortType == PortCategory.DATA) {
					logPort = ComponentCreateHelper.createOwnedDataPort(attestationManagerType);
				} else {
					logPort = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
				}
				logPort.setOut(true);
				logPort.setName(AM_LOG_PORT_NAME);
			}


			// Add Attestation Manager properties
			// CASE_Properties::Component_Type Property
			if (!CasePropertyUtils.setCompType(attestationManagerType, "ATTESTATION")) {
//				return;
			}

//			// CASE_Properties::COMP_SPEC property
//			// Parse the ID from the Attestation Manager AGREE property
//			String attestationPropId = "";
//			try {
//				attestationPropId = attestationAgreeProperty
//						.substring(attestationAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
//								attestationAgreeProperty.indexOf("\""))
//						.trim();
//			} catch (IndexOutOfBoundsException e) {
//				if (!attestationAgreeProperty.isEmpty()) {
//					// agree property is malformed, so leave blank
//					Dialog.showWarning("Add Attestation Manager", "Attestation Manager AGREE statement is malformed.");
//				}
//			}
//			if (!attestationPropId.isEmpty()) {
//				if (!CasePropertyUtils.setCompSpec(attestationManagerType, attestationPropId)) {
////					return;
//				}
//			}

			// Move attestation manager to top of file
			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

			// Create Attestation Manager implementation
			final ComponentImplementation attestationManagerImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			attestationManagerImpl.setName(attestationManagerType.getName() + ".Impl");
			final Realization r = attestationManagerImpl.createOwnedRealization();
			r.setImplemented(attestationManagerType);

			// Add it to proper place (just below component type)
			pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

//			// CASE_Properties::Component_Impl property
//			if (!attestationManagerImplLanguage.isEmpty()) {
//				if (!CasePropertyUtils.setCompImpl(attestationManagerImpl, attestationManagerImplLanguage)) {
////					return;
//				}
//			}

			// CASE_Properties::CACHE_TIMEOUT property
			if (cacheTimeout > 0) {
				if (!CasePropertyUtils.setCacheTimeout(attestationManagerImpl, cacheTimeout)) {
//					return;
				}
			}

			// CASE_Properties::CACHE_SIZE property
			if (cacheSize > 0) {
				if (!CasePropertyUtils.setCacheSize(attestationManagerImpl, cacheSize)) {
//					return;
				}
			}

			// Dispatch protocol property
			if (!dispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(dispatchProtocol);
				NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				attestationManagerImpl.setPropertyValue(dispatchProtocolProp, nv);
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
							ci.getOwnedSubcomponents()));
			// Assign thread implementation
			ComponentCreateHelper.setSubcomponentType(attestationManagerSubcomp, attestationManagerImpl);

			// Add the Attestation Gate type
			final ComponentType attestationGateType = (ComponentType) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
			// Give it a unique name
			attestationGateType.setName(
					ModelTransformUtils.getUniqueName(attestationGateComponentName, true,
							pkgSection.getOwnedClassifiers()));

			// Create Attestation Gate ports
			// To do this we need to look at the current connections of the comm driver in the implementation
			// However, we do not want to consider the outports that lead to the comm bus
			// We also only want to consider a connection once (in the case of fan out)
			// Also, save the port names, for use later
			List<String> agPortNames = new ArrayList<>();
			List<DataImplementation> agPortTypes = new ArrayList<>();
			for (PortConnection conn : ci.getOwnedPortConnections()) {
				if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {
					final Port commPort = (Port) conn.getSource().getConnectionEnd();
					if (agPortNames.contains(commPort.getName())) {
						continue;
					}
					Port portIn = null;
					Port portOut = null;
					DataSubcomponentType dataFeatureClassifier = null;
					if (commPort instanceof EventDataPort) {
						portIn = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
						dataFeatureClassifier = ((EventDataPort) commPort).getDataFeatureClassifier();
						((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
						portOut = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
						((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
					} else if (commPort instanceof DataPort) {
						portIn = ComponentCreateHelper.createOwnedDataPort(attestationGateType);
						dataFeatureClassifier = ((DataPort) commPort).getDataFeatureClassifier();
						((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
						portOut = ComponentCreateHelper.createOwnedDataPort(attestationGateType);
						((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
					}

					portIn.setIn(true);
					portIn.setName(commPort.getName() + "_in");

					portOut.setOut(true);
					portOut.setName(commPort.getName() + "_out");

					agPortNames.add(commPort.getName());
					agPortTypes.add((DataImplementation) dataFeatureClassifier);

					// The data subcomponent type could be in a different package.
					// Make sure to include it in the with clause
					importContainingPackage(dataFeatureClassifier, pkgSection);

				}
			}
			// Create Trusted ID List port
			final EventDataPort agId = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
			if (!idListDataType.isEmpty()) {
				DataSubcomponentType dataFeatureClassifier = Aadl2GlobalScopeUtil.get(ci,
						Aadl2Package.eINSTANCE.getDataSubcomponentType(), idListDataType);
				if (dataFeatureClassifier == null) {
					// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
					for (Classifier c : pkgSection.getOwnedClassifiers()) {
						if (c.getQualifiedName().equalsIgnoreCase(idListDataType)
								&& c instanceof DataSubcomponentType) {
							dataFeatureClassifier = (DataSubcomponentType) c;
							break;
						}
					}
				}
				if (dataFeatureClassifier != null) {
					agId.setDataFeatureClassifier(dataFeatureClassifier);
				}
			}
			agId.setName(AM_PORT_TRUSTED_IDS_NAME);
			agId.setIn(true);

			// Add Attestation Gate properties
			// CASE_Properties::Component_Type Property
			if (!CasePropertyUtils.setCompType(attestationGateType, "SWITCH")) {
//				return;
			}

			// CASE_Properties::Component_Spec property
			// Parse the ID from the Attestation Gate AGREE property
			String attestationPropId = "";
			try {
				attestationPropId = attestationAgreeProperty
						.substring(attestationAgreeProperty.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
								attestationAgreeProperty.indexOf("\""))
						.trim();
			} catch (IndexOutOfBoundsException e) {
				if (!attestationAgreeProperty.isEmpty()) {
					// agree property is malformed, so leave blank
					Dialog.showWarning("Add Attestation", "Attestation AGREE statement is malformed.");
				}
			}
			if (!attestationPropId.isEmpty()) {
				if (!CasePropertyUtils.setCompSpec(attestationGateType, attestationPropId)) {
//					return;
				}
			}

			// Move attestation gate to top of file
			pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);

			// Create Attestation Gate implementation
			final ComponentImplementation attestationGateImpl = (ComponentImplementation) pkgSection
					.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
			attestationGateImpl.setName(attestationGateType.getName() + ".Impl");
			final Realization gateRealization = attestationGateImpl.createOwnedRealization();
			gateRealization.setImplemented(attestationGateType);

			// Add it to proper place (just below component type)
			pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

//			// CASE_Properties::Component_Impl property
//			if (!attestationGateImplLanguage.isEmpty()) {
//				if (!CasePropertyUtils.setCompImpl(attestationGateImpl, attestationGateImplLanguage)) {
////					return;
//				}
//			}

			// Dispatch protocol property
			if (!dispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
				Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(attestationGateImpl,
						ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
				EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
				dispatchProtocolLit.setName(dispatchProtocol);
				NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
				nv.setNamedValue(dispatchProtocolLit);
				attestationGateImpl.setPropertyValue(dispatchProtocolProp, nv);
			}

			// Insert attestation gate in component implementation
			final Subcomponent attestationGateSubcomp = ComponentCreateHelper.createOwnedSubcomponent(ci, compCategory);

			// Give it a unique name
			attestationGateSubcomp.setName(
					ModelTransformUtils.getUniqueName(attestationGateSubcomponentName, true,
							ci.getOwnedSubcomponents()));
			// Assign implementation
			ComponentCreateHelper.setSubcomponentType(attestationGateSubcomp, attestationGateImpl);

			List<PortConnection> newPortConns = new ArrayList<>();
			// Create new connections between comm driver / attestation gate / destination components
			int connIdx = -1;
			for (int i = 0; i < ci.getOwnedPortConnections().size(); i++) {
				PortConnection conn = ci.getOwnedPortConnections().get(i);
				// Ignore bus connections (destination context not null)
				if (conn.getSource().getContext() == commDriver && conn.getDestination().getContext() != null) {

					// In order to put new connections in right place, keep track of
					// the first relevant comm driver connection name
					if (connIdx < 0) {
						connIdx = i;
					}

					// Rewire existing connection sources to be attestation gate
					String featureName = conn.getSource().getConnectionEnd().getName();
					ConnectionEnd connEnd = conn.getSource().getConnectionEnd();
					conn.getSource().setContext(attestationGateSubcomp);
					for (Feature feature : attestationGateType.getAllFeatures()) {
						if (feature.getName().equalsIgnoreCase(featureName + "_out")) {
							conn.getSource().setConnectionEnd(feature);
							break;
						}
					}

					// Create connections from comm driver to attestation manager
					// Don't create extra connections if a comm driver feature is fan out
					boolean connExists = false;
					for (PortConnection pc : newPortConns) {
						if (pc.getSource().getConnectionEnd().getName().equalsIgnoreCase(featureName)) {
							connExists = true;
							break;
						}
					}
					if (connExists) {
						continue;
					}

					final PortConnection portConnOut = Aadl2Factory.eINSTANCE.createPortConnection();
					portConnOut.setBidirectional(false);
					final ConnectedElement connSrc = portConnOut.createSource();
					connSrc.setContext(commDriver);
					connSrc.setConnectionEnd(connEnd);

					final ConnectedElement connDst = portConnOut.createDestination();
					connDst.setContext(attestationGateSubcomp);
					for (Feature feature : attestationGateType.getAllFeatures()) {
						if (feature.getName().equalsIgnoreCase(featureName + "_in")) {
							connDst.setConnectionEnd(feature);
							break;
						}
					}

					newPortConns.add(portConnOut);

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
			for (PortConnection newPortConn : newPortConns) {
				// Make sure each new connection has a unique name
				newPortConn.setName(
						ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false, ci.getOwnedPortConnections()));
				ci.getOwnedPortConnections().add(newPortConn);
				// Move to right place
				ci.getOwnedPortConnections().move(connIdx + idxOffset, ci.getOwnedPortConnections().size() - 1);
				idxOffset++;
			}

			// Propagate Agree Guarantees from comm driver, if there are any
			if (attestationAgreeProperty.length() > 0 || propagateGuarantees) {

				String agreeClauses = "{**" + System.lineSeparator();

				// Get guarantees from comm driver
				List<String> guarantees = new ArrayList<>();
				for (AnnexSubclause annexSubclause : selectedCommDriverType.getOwnedAnnexSubclauses()) {
					// Get the Agree annex
					if (annexSubclause.getName().equalsIgnoreCase("agree")) {
						DefaultAnnexSubclause annexSubclauseImpl = (DefaultAnnexSubclause) annexSubclause;
						// See if the agree annex contains guarantee statements
						AgreeContractSubclause agreeContract = (AgreeContractSubclause) annexSubclauseImpl
								.getParsedAnnexSubclause();
						AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
						// TODO: use unparser to unparse just guarantee statements
						String specs = unparser.unparseContract((AgreeContract) agreeContract.getContract(), "");
						int gCtr = 1;
						for (String spec : specs.split(";")) {
							if (spec.trim().toLowerCase().startsWith("guarantee")) {
								String guarantee = "";
								for (String line : spec.trim().concat(";").split(System.lineSeparator())) {
									guarantee += line.trim() + " ";
								}

								String expr = guarantee
										.substring(guarantee.lastIndexOf(":") + 1, guarantee.lastIndexOf(";")).trim();
								String desc = guarantee
										.substring(guarantee.indexOf("\""), guarantee.lastIndexOf("\"") + 1).trim();
								String id = guarantee.substring(
										guarantee.toLowerCase().indexOf("guarantee ") + "guarantee ".length(),
										guarantee.indexOf("\"")).trim();

								// If guarantee has an ID, append a suffix to maintain ID uniqueness
								if (!id.isEmpty()) {
									id += "_AttestationManager";
								} else {
									id = "Req" + gCtr++ + "_AttestationManager";
								}

								// Replace comm driver out port name with attestation manager out port name
								for (Feature feature : selectedCommDriverType.getOwnedFeatures()) {
									expr = expr.replace(feature.getName(), feature.getName() + "_out");
								}

								guarantee = "guarantee " + id + " " + desc + " : " + expr + ";";

								guarantees.add(guarantee);
							}
						}
						break;
					}
				}

				for (String guarantee : guarantees) {
					agreeClauses += guarantee + System.lineSeparator();
				}

				if (!attestationAgreeProperty.isEmpty()) {
					agreeClauses += attestationAgreeProperty + System.lineSeparator();
				}

				agreeClauses += "**}";

				// If agreeClauses is not an empty annex, print it
				if (attestationAgreeProperty.length() > 0 || guarantees.size() > 0) {
					final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
							.createOwnedAnnexSubclause(attestationGateType);
					annexSubclauseImpl.setName("agree");
					annexSubclauseImpl.setSourceText(agreeClauses);
				}
			}

			if (isProcess) {

				// TODO: Wrap thread component in a process

				// TODO: Bind process to processor
			}

			// Add add_attestation claims to resolute prove statement, if applicable
			if (!attestationRequirement.isEmpty()) {
				CyberRequirement req = RequirementsManager.getInstance().getRequirement(attestationRequirement);
				return new AddAttestationManagerClaim(req.getContext(), commDriver, attestationManagerSubcomp,
						attestationGateSubcomp);
			}
			return null;
		});

		// Add add_attestation claims to resolute prove statement, if applicable
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(attestationRequirement, claim);
		}

	}


	private Subcomponent getAttestationManager(Subcomponent comp) {

		ComponentImplementation ci = comp.getContainingComponentImpl();

		// Look at each connection in the subcomponent's containing implementation
		for (Connection conn : ci.getAllConnections()) {

			// Get the source component of the connection
			NamedElement ne = conn.getAllSrcContextComponent();
			// If source component is the specified subcomponent, get the destination component of the connection
			if (ne instanceof Subcomponent && ne.getName().equalsIgnoreCase(comp.getName())) {
				ne = conn.getAllDstContextComponent();
				Subcomponent dst = null;
				if (ne instanceof Subcomponent) {
					dst = (Subcomponent) ne;
					// Check if it's an attestation manager
					if (CasePropertyUtils.isCompType(dst.getClassifier(), "ATTESTATION")) {
						return dst;
					}
				}
			}

		}

		return null;
	}

	private Subcomponent getAttestationGate(Subcomponent attestationManager) {

		ComponentImplementation ci = attestationManager.getContainingComponentImpl();

		// Look at each connection in the attestation manager's containing implementation
		for (Connection conn : ci.getAllConnections()) {
			// Get the source component of the connection
			NamedElement ne = conn.getAllSrcContextComponent();
			if (ne instanceof Subcomponent && ne.getName().equalsIgnoreCase(attestationManager.getName())) {
				ne = conn.getAllDstContextComponent();
				Subcomponent dst = null;
				if (ne instanceof Subcomponent) {
					dst = (Subcomponent) ne;
					// Check if it's a switch
					if (CasePropertyUtils.isCompType(dst.getClassifier(), "SWITCH")) {
						return dst;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Copies property associations from one AADL named element to another.
	 * Only valid property associations accepted by the 'to' element will be copied
	 * @param from
	 * @param to
	 */
	private void copyPropertyAssociations(NamedElement from, NamedElement to) {
		if (to == null || from == null) {
			return;
		}
		for (PropertyAssociation pa : from.getOwnedPropertyAssociations()) {
			PropertyAssociation propAssoc = EcoreUtil.copy(pa);
			Property prop = propAssoc.getProperty();
			if (to.acceptsProperty(prop)) {
				// TODO: Make sure property package is in with clause?
				to.getOwnedPropertyAssociations().add(propAssoc);
			} else {
				// TODO: log exception
			}
		}
	}

}
