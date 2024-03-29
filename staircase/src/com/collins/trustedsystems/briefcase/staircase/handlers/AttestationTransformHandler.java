package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.SaveOptions;
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
import org.osate.aadl2.Element;
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
import org.osate.aadl2.RangeValue;
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
import org.osate.xtext.aadl2.properties.util.MemoryProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.attestation.AttestationAccess;
import com.collins.trustedsystems.briefcase.preferences.BriefcasePreferenceConstants;
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
import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.agree.NamedSpecStatement;
import com.rockwellcollins.atc.agree.agree.SpecStatement;

public class AttestationTransformHandler extends AadlHandler {

//	static final String AM_REQUEST_MSG_NAME = "CASE_AttestationRequestMsg";
//	static final String AM_RESPONSE_MSG_NAME = "CASE_AttestationResponseMsg";
	static final String AM_REQUEST_MSG_IMPL_NAME = "CASE_AttestationRequestMsg.Impl";
	static final String AM_RESPONSE_MSG_IMPL_NAME = "CASE_AttestationResponseMsg.Impl";
	public static final String AM_COMP_TYPE_NAME = "AttestationManager";
	public static final String AG_COMP_TYPE_NAME = "AttestationGate";
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
	private String attestationManagerExecutionTime;
	private Integer attestationManagerDomain;
	private String attestationManagerStackSize;
	private String attestationGateDispatchProtocol;
	private String attestationGatePeriod;
	private String attestationGateExecutionTime;
	private Integer attestationGateDomain;
	private String attestationGateStackSize;
	private String requestMessageDataType;
	private String responseMessageDataType;
	private long cacheTimeout;
	private String idListDataType;
	private Integer maxTrustedIds;
	private String sourceField;
	private Map<String, List<String>> attestationGatePortNames;
	private PortCategory attestationManagerLogPortType;
	private PortCategory attestationGateLogPortType;
	private String attestationRequirement;
	private boolean useKUImplementation;
//	private String attestationManagerAgreeProperty;
	private boolean isSel4Process = false;

	@Override
	protected String getJobName() {
		return "Attestation transform";
	}

	@Override
	protected void runCommand(EObject eObj) {

		// Check if selection is a subcomponent
		Subcomponent selectedSubcomponent = null;
		if (eObj instanceof Subcomponent) {
			selectedSubcomponent = (Subcomponent) eObj;
			// Check if selected subcomponent is a comm driver
			if (!CasePropertyUtils.isCommDriver(selectedSubcomponent.getClassifier())) {
				Dialog.showError("Attestation Transform",
						"A communication driver subcomponent must be selected to add attestation.");
				return;
			}
		} else if (eObj instanceof ComponentImplementation) {
			boolean containsCommDrivers = false;
			for (Subcomponent sub : ((ComponentImplementation) eObj).getOwnedSubcomponents()) {
				if (CasePropertyUtils.isCommDriver(sub.getClassifier())) {
					containsCommDrivers = true;
					break;
				}
			}
			if (!containsCommDrivers) {
				Dialog.showError("Attestation Transform",
						"The selected component implementation must contain at least one communication driver to add attestation.");
				return;
			}
		} else {
			Dialog.showError("Attestation Transform",
					"A communication driver subcomponent, or component implementation containing a communication driver, must be selected to add attestation.");
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


		// Open wizard to enter filter info
		final AttestationTransformDialog wizard = new AttestationTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create(eObj);
		if (wizard.open() == Window.OK) {
			if (selectedSubcomponent == null && eObj instanceof ComponentImplementation) {
				final String commDriver = wizard.getCommDriver();
				for (Subcomponent sub : ((ComponentImplementation) eObj).getOwnedSubcomponents()) {
					if (sub.getName().equalsIgnoreCase(commDriver)) {
						selectedSubcomponent = sub;
						break;
					}
				}
				if (selectedSubcomponent == null) {
					Dialog.showError("Attestation Transform", "Unable to determine communication driver.");
					return;
				}
			}

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
			attestationManagerExecutionTime = wizard.getMgrExecutionTime();
			attestationManagerDomain = wizard.getMgrDomain();
			attestationGateDispatchProtocol = wizard.getGateDispatchProtocol();
			attestationGatePeriod = wizard.getGatePeriod();
			attestationGateExecutionTime = wizard.getGateExecutionTime();
			attestationGateDomain = wizard.getGateDomain();
			attestationManagerStackSize = wizard.getMgrStackSize();
			attestationGateStackSize = wizard.getGateStackSize();
			requestMessageDataType = wizard.getRequestMessageDataType();
			responseMessageDataType = wizard.getResponseMessageDataType();
			cacheTimeout = wizard.getCacheTimeout();
			idListDataType = wizard.getIdListDataType();
			maxTrustedIds = wizard.getMaxTrustedIds();
			sourceField = wizard.getSourceField();
			attestationGatePortNames = wizard.getGatePortNames();
			attestationManagerLogPortType = wizard.getMgrLogPortType();
			attestationGateLogPortType = wizard.getGateLogPortType();
			isSel4Process = wizard.createThread();
			useKUImplementation = wizard.useKUImplementation();
			attestationRequirement = wizard.getRequirement();
		} else {
			return;
		}

		// Insert the attestation manager
		if (insertAttestationComponents(selectedSubcomponent)) {

			// Add attestation implementation and compile cakeml, if requested
			if (useKUImplementation) {
//				BriefcaseNotifier.printInfo("Inserting Attestation Manager implementation code");
				if (!AttestationAccess.createSourceDirectory((Element)eObj)) {
					Dialog.showWarning("Attestation Transform",
							"Attestation components were added to the model, but the KU implementation source code could not be copied into the project");
				}
				
//				BriefcaseNotifier.printInfo("Compiling Attestation Manager implementation code");
//				if (!AttestationAccess.compileCakeSource((Element)eObj)) {
//					Dialog.showWarning("Attestation Transform",
//							"Attestation components were added to the model, but the KU implementation source code could not be compiled");
//				}
			}

			// Format and save
			format(true);

			BriefcaseNotifier.notify("BriefCASE - Attestation Transform", "Attestation added to model.");
		} else {
			BriefcaseNotifier.printError("Attestation transform failed.");
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
	private boolean insertAttestationComponents(Subcomponent commDriver) {

		if (commDriver == null) {
			Dialog.showError("Attestation Transform", "Invalid communication driver.");
			return false;
		}

		final Resource aadlResource = commDriver.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			AddAttestationClaim claim = null;

			@Override
			protected void doExecute() {

				// Get containing component implementation
				final ComponentImplementation ci = commDriver.getContainingComponentImpl();

				PackageSection pkgSection = AadlUtil.getContainingPackageSection(commDriver);
				if (pkgSection == null) {
					// Something went wrong
					BriefcaseNotifier.printError("No public or private package sections found.");
					throw new RuntimeException();
				}

				// Import CASE_Properties file
				if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
					BriefcaseNotifier.printError("Could not import CASE_Properties property set.");
					throw new RuntimeException();
				}

				ComponentCategory compCategory = commDriver.getCategory();
				if (compCategory == ComponentCategory.THREAD_GROUP) {
					compCategory = ComponentCategory.THREAD;
				}

				// Create new comm driver component that copies the selected one

				final ComponentType commDriverType = EcoreUtil.copy(commDriver.getComponentType());
				pkgSection.getOwnedClassifiers().add(commDriverType);

				// Give it a unique name
				String commDriverName = commDriver.getComponentType().getName().replace("_seL4", "") + "_Attestation";
				commDriverName = ModelTransformUtils.getUniqueName(commDriverName, true,
						pkgSection.getOwnedClassifiers()) + (isSel4Process ? "_seL4" : "");
				commDriverType.setName(commDriverName);
//				commDriverType.setExtended(commDriver.getComponentType());

				// Make sure data classifier package is in with clause
				for (Feature f : commDriverType.getOwnedFeatures()) {
					if (f instanceof EventDataPort) {
						ModelTransformUtils.importContainingPackage(((EventDataPort) f).getDataFeatureClassifier(),
								pkgSection);
					} else if (f instanceof DataPort) {
						ModelTransformUtils.importContainingPackage(((DataPort) f).getDataFeatureClassifier(),
								pkgSection);
					}
				}

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
				final EventDataPort commReq = ComponentCreateHelper.createOwnedEventDataPort(commDriverType);
				final EventDataPort commRes = ComponentCreateHelper.createOwnedEventDataPort(commDriverType);
				commReq.setDataFeatureClassifier(requestMsgImpl);
				commRes.setDataFeatureClassifier(responseMsgImpl);
				commReq.setName(AM_PORT_ATTESTATION_REQUEST_NAME);
				commReq.setIn(true);
				commRes.setName(AM_PORT_ATTESTATION_RESPONSE_NAME);
				commRes.setOut(true);

				makeAgreeNamesUnique(commDriverType.getOwnedAnnexSubclauses());

				// Put just above it's containing implementation
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// Create extended comm driver implementation
				final ComponentImplementation commDriverImpl = EcoreUtil.copy(commDriver.getComponentImplementation());
				pkgSection.getOwnedClassifiers().add(commDriverImpl);
				commDriverImpl.setName(commDriverType.getName() + ".Impl");
				commDriverImpl.getOwnedRealization().setImplemented(commDriverType);

				// Add it to proper place (below extended comm driver type)
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// If comm driver is an seL4 process,
				// it's thread subcomponent will also need to be copied
				if (isSel4Process) {

					String subName = ((ProcessImplementation) commDriverImpl).getOwnedThreadSubcomponents()
							.get(0)
							.getName();
					((ProcessImplementation) commDriverImpl).getOwnedThreadSubcomponents().clear();
					((ProcessImplementation) commDriverImpl).getOwnedPortConnections().clear();

					ThreadSubcomponent threadSub = Sel4TransformHandler
							.insertThreadInSel4Process((ProcessImplementation) commDriverImpl, null, null, null, null);

					threadSub.setName(subName);

					ThreadType commThreadType = (ThreadType) ((ProcessImplementation) commDriver
							.getComponentImplementation()).getOwnedThreadSubcomponents().get(0).getComponentType();
					ThreadImplementation commThreadImpl = (ThreadImplementation) ((ProcessImplementation) commDriver
							.getComponentImplementation()).getOwnedThreadSubcomponents()
									.get(0)
									.getComponentImplementation();

					// Copy properties
					threadSub.getComponentType().getOwnedPropertyAssociations().clear();
					Sel4TransformHandler.copyPropertyAssociations(commThreadType, threadSub.getComponentType());
					threadSub.getComponentImplementation().getOwnedPropertyAssociations().clear();
					Sel4TransformHandler.copyPropertyAssociations(commThreadImpl,
							threadSub.getComponentImplementation());

					// Copy AGREE and make names unique
					for (AnnexSubclause annexSubclause : commThreadType.getOwnedAnnexSubclauses()) {
						threadSub.getComponentType().getOwnedAnnexSubclauses().add(EcoreUtil.copy(annexSubclause));
					}
					makeAgreeNamesUnique(threadSub.getComponentType().getOwnedAnnexSubclauses());
					for (AnnexSubclause annexSubclause : commThreadImpl.getOwnedAnnexSubclauses()) {
						threadSub.getComponentImplementation()
								.getOwnedAnnexSubclauses()
								.add(EcoreUtil.copy(annexSubclause));
					}
					makeAgreeNamesUnique(threadSub.getComponentImplementation().getOwnedAnnexSubclauses());

				}

				// Create Attestation Manager component type
				final ComponentType attestationManagerType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
				// Give it a unique name
				final String amName = ModelTransformUtils.getUniqueName(attestationManagerComponentName, true,
						pkgSection.getOwnedClassifiers()) + (isSel4Process ? "_seL4" : "");
				attestationManagerType.setName(amName);

				// Add the ports for communicating attestation requests/responses with the Comm Driver
				final EventDataPort amReq = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
				final EventDataPort amRes = ComponentCreateHelper.createOwnedEventDataPort(attestationManagerType);
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
				// CASE_Properties::Attesting Property
				CasePropertyUtils.setMitigationType(attestationManagerType, MITIGATION_TYPE.ATTESTATION);

				// Move attestation manager to top of file
//				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
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
					pkgSection.getOwnedClassifiers()
							.move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// CASE_Properties::CACHE_TIMEOUT property
				if (cacheTimeout > 0) {
					CasePropertyUtils.setCacheTimeout(attestationManagerImpl, cacheTimeout);
				}

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

				// Execution Time
				if (compCategory == ComponentCategory.THREAD && !attestationManagerExecutionTime.isEmpty()) {
					final Property executionTimeProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
							TimingProperties._NAME, TimingProperties.COMPUTE_EXECUTION_TIME);
					final String[] parts = attestationManagerExecutionTime.split("\\.\\.");
					final UnitLiteral unitMin = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unitMin.setName(parts[0].replaceAll("[\\d]", "").trim());
					final IntegerLiteral executionTimeMin = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					executionTimeMin.setBase(0);
					executionTimeMin.setValue(Long.parseLong(parts[0].replaceAll("[\\D]", "").trim()));
					executionTimeMin.setUnit(unitMin);
					final UnitLiteral unitMax = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unitMax.setName(parts[1].replaceAll("[\\d]", "").trim());
					final IntegerLiteral executionTimeMax = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					executionTimeMax.setBase(0);
					executionTimeMax.setValue(Long.parseLong(parts[1].replaceAll("[\\D]", "").trim()));
					executionTimeMax.setUnit(unitMax);
					final RangeValue executionTimeRange = Aadl2Factory.eINSTANCE.createRangeValue();
					executionTimeRange.setMinimum(executionTimeMin);
					executionTimeRange.setMaximum(executionTimeMax);
					attestationManagerImpl.setPropertyValue(executionTimeProp, executionTimeRange);
				}

				// Domain
				if (compCategory == ComponentCategory.PROCESS && attestationManagerDomain != null) {
					if (CaseUtils.importCaseSchedulingPackage(pkgSection)) {
						final Property domainProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
								CaseUtils.CASE_SCHEDULING_NAME, "Domain");
						final IntegerLiteral domainLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
						domainLit.setBase(0);
						domainLit.setValue(attestationManagerDomain);
						attestationManagerImpl.setPropertyValue(domainProp, domainLit);
					}
				}

				// Stack Size
				if (compCategory == ComponentCategory.THREAD && !attestationManagerStackSize.isEmpty()) {
					final Property stackSizeProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
							MemoryProperties._NAME, MemoryProperties.STACK_SIZE);
					final IntegerLiteral stackSizeLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unit.setName(attestationManagerStackSize.replaceAll("[\\d]", "").trim());
					stackSizeLit.setBase(0);
					stackSizeLit.setValue(Long.parseLong(attestationManagerStackSize.replaceAll("[\\D]", "").trim()));
					stackSizeLit.setUnit(unit);
					attestationManagerImpl.setPropertyValue(stackSizeProp, stackSizeLit);
				}

				// Language and Source Text
				if (useKUImplementation) {
					CasePropertyUtils.setCompLanguage(attestationManagerImpl, "CakeML");

					final Property sourceTextProp = GetProperties.lookupPropertyDefinition(attestationManagerImpl,
							ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);

					String componentSourceFolderName = Platform.getPreferencesService()
							.getString("com.collins.trustedsystems.briefcase",
									BriefcasePreferenceConstants.COMPONENT_SOURCE_FOLDER, "", null);
					String kuFolderName = Platform.getPreferencesService()
							.getString("com.collins.trustedsystems.briefcase",
									BriefcasePreferenceConstants.KU_IMPL_FOLDER, "", null);
					String attestationImplPath = componentSourceFolderName + "/" + kuFolderName + "/build/";
					// TODO: don't hardcode attestation impl path
					StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
					sourceTextLit.setValue(attestationImplPath + "heli_am.S");
					final List<StringLiteral> listVal = new ArrayList<>();
					listVal.add(sourceTextLit);
//					sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
//					sourceTextLit.setValue(attestationImplPath + "apps" + File.separator + "case-tool-assessment"
//							+ File.separator + "libheli_am_c.a");
//					listVal.add(sourceTextLit);
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
				attestationManagerSubcomp.setName(ModelTransformUtils.getUniqueName(attestationManagerSubcomponentName,
						true, ci.getAllSubcomponents()));
				// Assign thread implementation
				ComponentCreateHelper.setSubcomponentType(attestationManagerSubcomp, attestationManagerImpl);

				// Add the Attestation Gate type
				final ComponentType attestationGateType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
				// Give it a unique name
				final String agName = ModelTransformUtils.getUniqueName(attestationGateComponentName, true,
						pkgSection.getOwnedClassifiers());
				attestationGateType.setName(agName + (isSel4Process ? "_seL4" : ""));

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
							((EventDataPort) connEndIn)
									.setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
							for (ArrayDimension dim : ((EventDataPort) commPort).getArrayDimensions()) {
								final ArrayDimension arrayDimension = ((EventDataPort) connEndIn)
										.createArrayDimension();
								arrayDimension.setSize(dim.getSize());
							}
							connEndOut = ComponentCreateHelper.createOwnedEventDataPort(attestationGateType);
							((EventDataPort) connEndOut)
									.setDataFeatureClassifier((DataSubcomponentType) featureClassifier);
							for (ArrayDimension dim : ((EventDataPort) commPort).getArrayDimensions()) {
								final ArrayDimension arrayDimension = ((EventDataPort) connEndOut)
										.createArrayDimension();
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
									.getConnectionEnd()).getFeatureGroupType();
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
				// CASE_Properties:Gating Property
				CasePropertyUtils.setMitigationType(attestationGateType, MITIGATION_TYPE.GATE);

				// Move attestation gate to top of file
//				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// Create Attestation Gate implementation
				final ComponentImplementation attestationGateImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
				attestationGateImpl.setName(attestationGateType.getName() + ".Impl");
				final Realization gateRealization = attestationGateImpl.createOwnedRealization();
				gateRealization.setImplemented(attestationGateType);

				// Add it to proper place (just below component type)
//				pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(ci.getTypeName(), pkgSection.getOwnedClassifiers()),
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

				// Execution Time
				if (compCategory == ComponentCategory.THREAD && !attestationGateExecutionTime.isEmpty()) {
					final Property executionTimeProp = GetProperties.lookupPropertyDefinition(attestationGateImpl,
							TimingProperties._NAME, TimingProperties.COMPUTE_EXECUTION_TIME);
					final String[] parts = attestationGateExecutionTime.split("\\.\\.");
					final UnitLiteral unitMin = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unitMin.setName(parts[0].replaceAll("[\\d]", "").trim());
					final IntegerLiteral executionTimeMin = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					executionTimeMin.setBase(0);
					executionTimeMin.setValue(Long.parseLong(parts[0].replaceAll("[\\D]", "").trim()));
					executionTimeMin.setUnit(unitMin);
					final UnitLiteral unitMax = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unitMax.setName(parts[1].replaceAll("[\\d]", "").trim());
					final IntegerLiteral executionTimeMax = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					executionTimeMax.setBase(0);
					executionTimeMax.setValue(Long.parseLong(parts[1].replaceAll("[\\D]", "").trim()));
					executionTimeMax.setUnit(unitMax);
					final RangeValue executionTimeRange = Aadl2Factory.eINSTANCE.createRangeValue();
					executionTimeRange.setMinimum(executionTimeMin);
					executionTimeRange.setMaximum(executionTimeMax);
					attestationGateImpl.setPropertyValue(executionTimeProp, executionTimeRange);
				}

				// Domain
				if (compCategory == ComponentCategory.PROCESS && attestationGateDomain != null) {
					if (CaseUtils.importCaseSchedulingPackage(pkgSection)) {
						final Property domainProp = GetProperties.lookupPropertyDefinition(attestationGateImpl,
								CaseUtils.CASE_SCHEDULING_NAME, "Domain");
						final IntegerLiteral domainLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
						domainLit.setBase(0);
						domainLit.setValue(attestationGateDomain);
						attestationGateImpl.setPropertyValue(domainProp, domainLit);
					}
				}

				// Stack size
				if (compCategory == ComponentCategory.THREAD && !attestationGateStackSize.isEmpty()) {
					final Property stackSizeProp = GetProperties.lookupPropertyDefinition(attestationGateImpl,
							MemoryProperties._NAME, MemoryProperties.STACK_SIZE);
					final IntegerLiteral stackSizeLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unit.setName(attestationGateStackSize.replaceAll("[\\d]", "").trim());
					stackSizeLit.setBase(0);
					stackSizeLit.setValue(Long.parseLong(attestationGateStackSize.replaceAll("[\\D]", "").trim()));
					stackSizeLit.setUnit(unit);
					attestationGateImpl.setPropertyValue(stackSizeProp, stackSizeLit);
				}

				// Insert attestation gate in component implementation
				final Subcomponent attestationGateSubcomp = ComponentCreateHelper.createOwnedSubcomponent(ci,
						compCategory);

				// Give it a unique name
				attestationGateSubcomp.setName(ModelTransformUtils.getUniqueName(attestationGateSubcomponentName, true,
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
//					ci.getOwnedConnections().move(connIdx + idxOffset, ci.getOwnedConnections().size() - 1);

				}

				// Add AGREE statements
				final StringBuilder agreeClauses = new StringBuilder();
				if (maxTrustedIds != null && maxTrustedIds > 0 && !idListDataType.isBlank() && !sourceField.isBlank()) {

					// CASE_Properties::Component_Spec property
					String attestationGatePropId = "";
					for (String commPortName : attestationGatePortNames.keySet()) {
						if (!attestationGatePropId.isEmpty()) {
							attestationGatePropId += ",";
						}
						if (attestationGatePortNames.get(commPortName).isEmpty()) {
							attestationGatePropId += agName + "_" + commPortName + "_out";
						} else {
							attestationGatePropId += agName + "_"
									+ attestationGatePortNames.get(commPortName).get(1);
						}
					}
					if (!attestationGatePropId.isEmpty()) {
						CasePropertyUtils.setCompSpec(attestationGateType, attestationGatePropId);
					}

					agreeClauses.append("{**" + System.lineSeparator());

					final String default_trustedid_list = idListDataType + " { value = [| "
							+ String.join(", ", Collections.nCopies(maxTrustedIds, "0")) + " |] }";
					agreeClauses.append("eq trusted_ids : " + idListDataType
							+ " = (if event(TrustedIds) then TrustedIds else " + default_trustedid_list
							+ ") -> (if event(TrustedIds) then TrustedIds else pre(trusted_ids));"
							+ System.lineSeparator());

					final String[] sourceMsgParts = sourceField.split("\\.");
					final String sourceMsgType = sourceMsgParts.length > 1 ? sourceMsgParts[0] + "." + sourceMsgParts[1]
							: sourceMsgParts[0];
					final String sourceMsgField = sourceMsgParts.length > 1
							? "." + String.join(".", Arrays.copyOfRange(sourceMsgParts, 2, sourceMsgParts.length))
							: "";
					agreeClauses.append("fun IS_TRUSTED(command : " + sourceMsgType + ", ids : " + idListDataType
							+ ") : bool = exists i in ids.value, command" + sourceMsgField + " = i;"
							+ System.lineSeparator());

					for (String commPortName : attestationGatePortNames.keySet()) {
						String portNameIn = "";
						String portNameOut = "";
						if (attestationGatePortNames.get(commPortName).isEmpty()) {
							portNameIn = commPortName + "_in";
							portNameOut = commPortName + "_out";
						} else {
							portNameIn = attestationGatePortNames.get(commPortName).get(0);
							portNameOut = attestationGatePortNames.get(commPortName).get(1);
						}
						agreeClauses.append("guarantee " + agName + "_" + portNameOut
								+ " \"The gate shall output only data from trusted sources\" : if event(" + portNameIn
								+ ") and IS_TRUSTED(" + portNameIn + ", trusted_ids) then event(" + portNameOut
								+ ") and " + portNameOut + " = " + portNameIn + " else not event(" + portNameOut + ");"
								+ System.lineSeparator());
					}

					agreeClauses.append("**}");

					if (!isSel4Process) {
						final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
								.createOwnedAnnexSubclause(attestationGateType);
						annexSubclauseImpl.setName("agree");
						annexSubclauseImpl.setSourceText(agreeClauses.toString());
					}
				}

				if (isSel4Process) {

					Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) attestationManagerImpl,
							attestationManagerDispatchProtocol, attestationManagerExecutionTime,
							attestationManagerStackSize, null);

					// Create 'lift contract' statement in process implementation.
					// This will be done even if an AGREE property hasn't been specified yet.
					// That way if one is manually added later, everything will still work.
					final DefaultAnnexSubclause annexSubclause = ComponentCreateHelper
							.createOwnedAnnexSubclause(attestationGateImpl);
					annexSubclause.setName("agree");
					annexSubclause.setSourceText(
							"{**" + System.lineSeparator() + "lift contract;" + System.lineSeparator() + "**}");

					Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) attestationGateImpl,
							attestationGateDispatchProtocol, attestationGateExecutionTime, attestationGateStackSize,
							agreeClauses.toString());

				}

				// Add add_attestation claims to resolute prove statement, if applicable
				if (!attestationRequirement.isEmpty()) {
					final CyberRequirement req = RequirementsManager.getInstance()
							.getRequirement(attestationRequirement);
					claim = new AddAttestationClaim(req.getContext(), commDriver, attestationManagerSubcomp,
							attestationGateSubcomp);
				}
				return;

			}

			@Override
			public Collection<AddAttestationClaim> getResult() {
				if (claim == null) {
					return null;
				} else {
					return Collections.singletonList(claim);
				}
			}

		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		@SuppressWarnings("unchecked")
		final List<AddAttestationClaim> claim = (List<AddAttestationClaim>) cmd.getResult();
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(attestationRequirement, claim.get(0));
		}

		return true;

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

}
