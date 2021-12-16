package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.SaveOptions;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.ArraySize;
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
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.MemoryProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.MonitorTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddMonitorClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.CyberRequirement;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class MonitorTransformHandler extends AadlHandler {

	public static final String MONITOR_COMP_TYPE_NAME = "Monitor";
	public static final String MONITOR_OBSERVED_PORT_NAME = "Observed";
	public static final String MONITOR_GATE_PORT_NAME = "Output";
	public static final String MONITOR_ALERT_PORT_NAME = "Alert";
	public static final String MONITOR_RESET_PORT_NAME = "Reset";
	public static final String MONITOR_SUBCOMP_NAME = "Monitor";
	static final String CONNECTION_IMPL_NAME = "c";

	private String monitorComponentName;
	private String monitorSubcomponentName;
	private String observationPortName;
	private String observationGatePortName;
	private String dispatchProtocol;
	private String period;
	private String stackSize;
	private String resetPortName;
	private String resetPort;
	private boolean latched;
	private Map<String, String> referencePorts;
	private String alertPortName;
	private String alertPort;
	private PortCategory alertPortCategory;
	private String alertPortDataType;
	private boolean observationGate;
	private String monitorRequirement;
	private String monitorAgreeProperty;
	private boolean isSel4Process = false;

	@Override
	protected void runCommand(EObject eObj) {

		Connection selectedConnection = null;

		// Check if it is a connection
		if (eObj instanceof ComponentImplementation) {
			if (((ComponentImplementation) eObj).getOwnedConnections().isEmpty()) {
				Dialog.showError("Monitor Transform",
						"The selected component implementation must contain at least one connected subcomponent to add a monitor.");
				return;
			}
		} else if (!(eObj instanceof Connection)) {
			Dialog.showError("Monitor Transform",
					"A connection between two components, or a component implementation containing at least one connected subcomponent, must be selected to add a monitor.");
			return;
		} else {
			selectedConnection = (Connection) eObj;
		}

		// Open wizard to enter monitor info
		final MonitorTransformDialog wizard = new MonitorTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());


		wizard.create(eObj);
		if (wizard.open() == Window.OK) {
			if (eObj instanceof ComponentImplementation) {
				final String connection = wizard.getConnection();

				for (Connection conn : ((ComponentImplementation) eObj).getOwnedConnections()) {
					if (conn.getName().equalsIgnoreCase(connection)) {
						selectedConnection = conn;
						break;
					}
				}
				if (selectedConnection == null) {
					Dialog.showError("Monitor Transform", "Unable to determine connection to insert monitor.");
					return;
				}
			}
			monitorComponentName = wizard.getMonitorComponentName();
			if (monitorComponentName.isEmpty()) {
				monitorComponentName = MONITOR_COMP_TYPE_NAME;
			}
			monitorSubcomponentName = wizard.getMonitorSubcomponentName();
			if (monitorSubcomponentName.isEmpty()) {
				monitorSubcomponentName = MONITOR_SUBCOMP_NAME;
			}
			observationPortName = wizard.getObservationPortName();
			if (observationPortName.isEmpty()) {
				observationPortName = MONITOR_OBSERVED_PORT_NAME;
			}
			observationGatePortName = wizard.getObservationGatePortName();
			if (observationGatePortName.isEmpty()) {
				observationGatePortName = MONITOR_GATE_PORT_NAME;
			}
			resetPortName = wizard.getResetPortName();
			if (resetPortName.isEmpty()) {
				resetPortName = MONITOR_RESET_PORT_NAME;
			}
			resetPort = wizard.getResetPort();
			latched = wizard.getLatched();
			isSel4Process = wizard.createThread();
			dispatchProtocol = wizard.getDispatchProtocol();
			period = wizard.getPeriod();
			stackSize = wizard.getStackSize();
			referencePorts = wizard.getReferencePorts();
			alertPortName = wizard.getAlertPortName();
			if (alertPortName.isEmpty()) {
				alertPortName = MONITOR_ALERT_PORT_NAME;
			}
			alertPort = wizard.getAlertPort();
			alertPortCategory = wizard.getAlertPortCategory();
			alertPortDataType = wizard.getAlertPortDataType();
			observationGate = wizard.getObservationGate();
			monitorRequirement = wizard.getRequirement();
			monitorAgreeProperty = wizard.getAgreeProperty();
		} else {
			return;
		}

		// Insert the monitor component
		if (insertMonitor(selectedConnection)) {

			BriefcaseNotifier.notify("BriefCASE - Monitor Transform", "Monitor added to model.");

			// Format and save
			format(true);

//			// Save
//			saveChanges(false);
		} else {
			BriefcaseNotifier.printError("Monitor transform failed.");
		}

		return;

	}

	/**
	 * Inserts a monitor component into the model, including monitor type definition
	 * and implementation (including correct wiring for monitored signal).
	 * The monitor is inserted at the location of the selected connection
	 * @param uri - The URI of the selected connection
	 */
	private boolean insertMonitor(Connection selectedConnection) {

		if (selectedConnection == null) {
			Dialog.showError("Monitor Transform", "Invalid connection.");
			return false;
		}

		final Resource aadlResource = selectedConnection.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			AddMonitorClaim claim = null;

			@Override
			protected void doExecute() {
				final ComponentImplementation containingImpl = selectedConnection.getContainingComponentImpl();

				PackageSection pkgSection = AadlUtil.getContainingPackageSection(containingImpl);
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

				// Figure out component type by looking at the component type of the source or destination component
				// Note that the context could be null if the connection end is the containing component
				Subcomponent context = (Subcomponent) selectedConnection.getDestination().getContext();
				if (context == null) {
					context = (Subcomponent) selectedConnection.getSource().getContext();
				}
				ComponentCategory compCategory = context.getCategory();
				if (compCategory == ComponentCategory.THREAD_GROUP) {
					compCategory = ComponentCategory.THREAD;
				}

				final ComponentType monitorType = (ComponentType) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

				// Give it a unique name
				final String monitorName = ModelTransformUtils.getUniqueName(monitorComponentName, true,
						pkgSection.getOwnedClassifiers());
				monitorType.setName(monitorName + (isSel4Process ? "_seL4" : ""));

				// Create monitor observed port
				final ConnectionEnd portSrc = selectedConnection.getSource().getConnectionEnd();
				ConnectionEnd portObserved = null;
				NamedElement observedDataFeatureClassifier = null;
				if (portSrc instanceof EventDataPort) {
					observedDataFeatureClassifier = ((EventDataPort) portSrc).getDataFeatureClassifier();
					portObserved = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					((EventDataPort) portObserved)
							.setDataFeatureClassifier((DataSubcomponentType) observedDataFeatureClassifier);
					for (ArrayDimension dim : ((EventDataPort) portSrc).getArrayDimensions()) {
						final ArrayDimension arrayDimension = ((EventDataPort) portObserved).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (portSrc instanceof DataPort) {
					observedDataFeatureClassifier = ((DataPort) portSrc).getDataFeatureClassifier();
					portObserved = ComponentCreateHelper.createOwnedDataPort(monitorType);
					((DataPort) portObserved)
							.setDataFeatureClassifier((DataSubcomponentType) observedDataFeatureClassifier);
					for (ArrayDimension dim : ((DataPort) portSrc).getArrayDimensions()) {
						final ArrayDimension arrayDimension = ((DataPort) portObserved).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (portSrc instanceof EventPort) {
					portObserved = ComponentCreateHelper.createOwnedEventPort(monitorType);
				} else if (portSrc instanceof FeatureGroup) {
					portObserved = monitorType.createOwnedFeatureGroup();
					((FeatureGroup) portObserved).setFeatureType(((FeatureGroup) portSrc).getFeatureGroupType());
				} else {
					BriefcaseNotifier.printError("Could not determine the port type of the destination component.");
					return;
				}

				((DirectedFeature) portObserved).setIn(true);
				portObserved.setName(observationPortName);

				// Create reference ports
				final Map<ConnectionEnd, ConnectionEnd> monReferencePorts = new HashMap<>();
				DataSubcomponentType dataFeatureClassifier = null;
				for (Map.Entry<String, String> refEntry : referencePorts.entrySet()) {
					ConnectionEnd monRefPort = null;
					ConnectionEnd srcRefPort = ModelTransformUtils.getPort(containingImpl, refEntry.getValue());
					// If user didn't specify a source port, make it an event data port
					if (srcRefPort == null) {
						monRefPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					}
					if (srcRefPort instanceof EventDataPort) {
						monRefPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
						dataFeatureClassifier = ((EventDataPort) srcRefPort).getDataFeatureClassifier();
						((EventDataPort) monRefPort).setDataFeatureClassifier(dataFeatureClassifier);
						for (ArrayDimension dim : ((EventDataPort) srcRefPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventDataPort) monRefPort).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (srcRefPort instanceof DataPort) {
						monRefPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
						dataFeatureClassifier = ((DataPort) srcRefPort).getDataFeatureClassifier();
						((DataPort) monRefPort).setDataFeatureClassifier(dataFeatureClassifier);
						for (ArrayDimension dim : ((DataPort) srcRefPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((DataPort) monRefPort).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (srcRefPort instanceof EventPort) {
						monRefPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
					} else if (srcRefPort instanceof FeatureGroup) {
						monRefPort = monitorType.createOwnedFeatureGroup();
						((FeatureGroup) monRefPort).setFeatureType(((FeatureGroup) srcRefPort).getFeatureGroupType());
					}
					((DirectedFeature) monRefPort).setIn(true);
					monRefPort.setName(refEntry.getKey());
					if (srcRefPort != null) {
						monReferencePorts.put(srcRefPort, monRefPort);
					}
				}

				// Create monitor alert port
				Port monAlertPort = null;
				final ConnectionEnd dstAlertPort = ModelTransformUtils.getPort(containingImpl, alertPort);
				DataSubcomponentType alertDataFeatureClassifier = null;
				Long dimension = null;
				if (!alertPortDataType.isEmpty()) {
					String dataType = alertPortDataType;
					if (dataType.contains("[")) {
						dimension = Long
								.parseLong(dataType.substring(dataType.indexOf("[") + 1, dataType.length() - 1));
						dataType = dataType.substring(0, dataType.indexOf("["));
					}
					if (!dataType.isEmpty()) {
						alertDataFeatureClassifier = Aadl2GlobalScopeUtil.get(containingImpl,
								Aadl2Package.eINSTANCE.getDataSubcomponentType(), dataType);
						if (alertDataFeatureClassifier == null) {
							// Aadl2GlobalScopeUtil.get() doesn't seem to find elements in current package
							for (Classifier c : pkgSection.getOwnedClassifiers()) {
								if (c.getQualifiedName().equalsIgnoreCase(dataType)
										&& c instanceof DataSubcomponentType) {
									alertDataFeatureClassifier = (DataSubcomponentType) c;
									break;
								}
							}
						} else {
							ModelTransformUtils.importContainingPackage(alertDataFeatureClassifier, pkgSection);
						}
					}
				}
				// If user didn't specify an alert inport, make it an event data port with no type
				if (dstAlertPort == null) {
					if (alertPortCategory != null) {
						if (alertPortCategory == PortCategory.DATA) {
							monAlertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
							if (alertDataFeatureClassifier != null) {
								((DataPort) monAlertPort).setDataFeatureClassifier(alertDataFeatureClassifier);
							}
							if (dimension != null) {
								final ArrayDimension arrayDimension = ((DataPort) monAlertPort).createArrayDimension();
								final ArraySize arraySize = Aadl2Factory.eINSTANCE.createArraySize();
								arraySize.setSize(dimension.intValue());
								arrayDimension.setSize(arraySize);
							}
						} else if (alertPortCategory == PortCategory.EVENT) {
							monAlertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
							if (dimension != null) {
								final ArrayDimension arrayDimension = ((EventPort) monAlertPort).createArrayDimension();
								final ArraySize arraySize = Aadl2Factory.eINSTANCE.createArraySize();
								arraySize.setSize(dimension.intValue());
								arrayDimension.setSize(arraySize);
							}
						} else {
							monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
							if (alertDataFeatureClassifier != null) {
								((EventDataPort) monAlertPort).setDataFeatureClassifier(alertDataFeatureClassifier);
							}
							if (dimension != null) {
								final ArrayDimension arrayDimension = ((EventDataPort) monAlertPort)
										.createArrayDimension();
								final ArraySize arraySize = Aadl2Factory.eINSTANCE.createArraySize();
								arraySize.setSize(dimension.intValue());
								arrayDimension.setSize(arraySize);
							}
						}
					} else {
						monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
						if (alertDataFeatureClassifier != null) {
							((EventDataPort) monAlertPort).setDataFeatureClassifier(alertDataFeatureClassifier);
						}
						if (dimension != null) {
							final ArrayDimension arrayDimension = ((EventDataPort) monAlertPort).createArrayDimension();
							final ArraySize arraySize = Aadl2Factory.eINSTANCE.createArraySize();
							arraySize.setSize(dimension.intValue());
							arrayDimension.setSize(arraySize);
						}
					}
				} else if (dstAlertPort instanceof EventDataPort) {
					monAlertPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					dataFeatureClassifier = ((EventDataPort) dstAlertPort).getDataFeatureClassifier();
					((EventDataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((EventDataPort) dstAlertPort).getArrayDimensions()) {
						final ArrayDimension arrayDimension = ((EventDataPort) monAlertPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (dstAlertPort instanceof DataPort) {
					monAlertPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
					dataFeatureClassifier = ((DataPort) dstAlertPort).getDataFeatureClassifier();
					((DataPort) monAlertPort).setDataFeatureClassifier(dataFeatureClassifier);
					for (ArrayDimension dim : ((DataPort) dstAlertPort).getArrayDimensions()) {
						final ArrayDimension arrayDimension = ((DataPort) monAlertPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (dstAlertPort instanceof EventPort) {
					monAlertPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
					for (ArrayDimension dim : ((EventPort) dstAlertPort).getArrayDimensions()) {
						final ArrayDimension arrayDimension = ((EventPort) monAlertPort).createArrayDimension();
						arrayDimension.setSize(dim.getSize());
					}
				} else if (dstAlertPort instanceof FeatureGroup) {
					// TODO: Names of all feature group features should be provided so user can pick one

				}
				monAlertPort.setOut(true);
				monAlertPort.setName(alertPortName);

				// Create observation gate output port, if needed
				ConnectionEnd monGatePort = null;
				if (observationGate) {
					final ConnectionEnd dstGatePort = selectedConnection.getDestination().getConnectionEnd();
					// If user didn't specify a gate inport, make it an event data port
					if (dstGatePort == null) {
						monGatePort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					} else if (dstGatePort instanceof EventDataPort) {
						monGatePort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
						dataFeatureClassifier = ((EventDataPort) dstGatePort).getDataFeatureClassifier();
						((EventDataPort) monGatePort).setDataFeatureClassifier(dataFeatureClassifier);
						for (ArrayDimension dim : ((EventDataPort) dstGatePort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventDataPort) monGatePort).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (dstGatePort instanceof DataPort) {
						monGatePort = ComponentCreateHelper.createOwnedDataPort(monitorType);
						dataFeatureClassifier = ((DataPort) dstGatePort).getDataFeatureClassifier();
						((DataPort) monGatePort).setDataFeatureClassifier(dataFeatureClassifier);
						for (ArrayDimension dim : ((DataPort) dstGatePort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((DataPort) monGatePort).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (dstGatePort instanceof EventPort) {
						monGatePort = ComponentCreateHelper.createOwnedEventPort(monitorType);
					} else if (dstGatePort instanceof FeatureGroup) {
						monGatePort = monitorType.createOwnedFeatureGroup();
						((FeatureGroup) monGatePort).setFeatureType(((FeatureGroup) dstGatePort).getFeatureGroupType());
					}
					((DirectedFeature) monGatePort).setOut(true);
					monGatePort.setName(observationGatePortName);
				}

				// Create monitor reset port, if needed
				ConnectionEnd monResetPort = null;
				ConnectionEnd srcResetPort = null;
				if (resetPort != null) {
					srcResetPort = ModelTransformUtils.getPort(containingImpl, resetPort);
					// If user didn't specify a reset outport, make it an event data port
					if (srcResetPort == null) {
						monResetPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
					} else if (srcResetPort instanceof EventDataPort) {
						monResetPort = ComponentCreateHelper.createOwnedEventDataPort(monitorType);
						dataFeatureClassifier = ((EventDataPort) srcResetPort).getDataFeatureClassifier();
						((EventDataPort) monResetPort).setDataFeatureClassifier(dataFeatureClassifier);
						for (ArrayDimension dim : ((EventDataPort) srcResetPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((EventDataPort) monResetPort).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (srcResetPort instanceof DataPort) {
						monResetPort = ComponentCreateHelper.createOwnedDataPort(monitorType);
						dataFeatureClassifier = ((DataPort) srcResetPort).getDataFeatureClassifier();
						((DataPort) monResetPort).setDataFeatureClassifier(dataFeatureClassifier);
						for (ArrayDimension dim : ((DataPort) srcResetPort).getArrayDimensions()) {
							final ArrayDimension arrayDimension = ((DataPort) monResetPort).createArrayDimension();
							arrayDimension.setSize(dim.getSize());
						}
					} else if (srcResetPort instanceof EventPort) {
						monResetPort = ComponentCreateHelper.createOwnedEventPort(monitorType);
					} else if (srcResetPort instanceof FeatureGroup) {
						monResetPort = monitorType.createOwnedFeatureGroup();
						((FeatureGroup) monResetPort)
								.setFeatureType(((FeatureGroup) srcResetPort).getFeatureGroupType());
					}
					((DirectedFeature) monResetPort).setIn(true);
					monResetPort.setName(resetPortName);
				}

				// Add monitor properties
				// CASE_Properties::Component_Type Property
				CasePropertyUtils.setMitigationType(monitorType, MITIGATION_TYPE.MONITOR);

//				// CASE_Properties::Component_Spec property
//				final String monitorAlertPropId = monitorName + "_" + alertPortName;
//				String monitorGatePropId = "";
//				if (observationGate) {
//					monitorGatePropId += monitorName + "_" + observationGatePortName;
//				}
//				CasePropertyUtils.setCompSpec(monitorType,
//						monitorAlertPropId + (monitorGatePropId.isEmpty() ? "" : "," + monitorGatePropId));

//				// CASE_Properties::Monitor_Latched
//				CasePropertyUtils.setMonitorLatched(monitorType, latched);

				// Move to top of file
//				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(containingImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// Create monitor implementation
				final ComponentImplementation monitorImpl = (ComponentImplementation) pkgSection
						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
				monitorImpl.setName(monitorType.getName() + ".Impl");
				final Realization r = monitorImpl.createOwnedRealization();
				r.setImplemented(monitorType);

				// Move below component type
//				pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
				try {
					pkgSection.getOwnedClassifiers()
							.move(getIndex(containingImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
									pkgSection.getOwnedClassifiers().size() - 1);
				} catch (Exception e) {

				}

				// Dispatch protocol property
				if (compCategory == ComponentCategory.THREAD && !dispatchProtocol.isEmpty()) {
					final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(monitorImpl,
							ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
					final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
					dispatchProtocolLit.setName(dispatchProtocol);
					final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
					nv.setNamedValue(dispatchProtocolLit);
					monitorImpl.setPropertyValue(dispatchProtocolProp, nv);
				}

				// Period
				if (!period.isEmpty()) {
					final Property periodProp = GetProperties.lookupPropertyDefinition(monitorImpl,
							TimingProperties._NAME, TimingProperties.PERIOD);
					final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unit.setName(period.replaceAll("[\\d]", "").trim());
					periodLit.setBase(0);
					periodLit.setValue(Long.parseLong(period.replaceAll("[\\D]", "").trim()));
					periodLit.setUnit(unit);
					monitorImpl.setPropertyValue(periodProp, periodLit);
				}

				// Stack Size
				if (compCategory == ComponentCategory.THREAD && !stackSize.isEmpty()) {
					final Property stackSizeProp = GetProperties.lookupPropertyDefinition(monitorImpl,
							MemoryProperties._NAME, MemoryProperties.STACK_SIZE);
					final IntegerLiteral stackSizeLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
					unit.setName(stackSize.replaceAll("[\\d]", "").trim());
					stackSizeLit.setBase(0);
					stackSizeLit.setValue(Long.parseLong(stackSize.replaceAll("[\\D]", "").trim()));
					stackSizeLit.setUnit(unit);
					monitorImpl.setPropertyValue(stackSizeProp, stackSizeLit);
				}

				// Insert monitor subcomponent in containing component implementation
				final Subcomponent monitorSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
						compCategory);

				// Give it a unique name
				monitorSubcomp.setName(ModelTransformUtils.getUniqueName(monitorSubcomponentName, true,
						containingImpl.getAllSubcomponents()));

				ComponentCreateHelper.setSubcomponentType(monitorSubcomp, monitorImpl);

				// Create a connection from selected connection source to monitor observed input
				final Connection connObserved = ComponentCreateHelper.createOwnedConnection(containingImpl,
						selectedConnection.getSource().getConnectionEnd());
				// Give it a unique name
				connObserved.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
						containingImpl.getAllConnections()));
				connObserved.setBidirectional(false);
				final ConnectedElement monitorObservedSrc = connObserved.createSource();
				monitorObservedSrc.setContext(selectedConnection.getSource().getContext());
				monitorObservedSrc.setConnectionEnd(selectedConnection.getSource().getConnectionEnd());
				final ConnectedElement monitorObservedDst = connObserved.createDestination();
				monitorObservedDst.setContext(monitorSubcomp);
				monitorObservedDst.setConnectionEnd(portObserved);

				// Put portConnObserved in right place (after selected connection)
				String destName = selectedConnection.getName();
				try {
					containingImpl.getOwnedConnections()
							.move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
									containingImpl.getOwnedPortConnections().size() - 1);
				} catch (Exception e) {

				}

				// Change selected connection source to monitor gate output port, if needed
				if (observationGate) {
					selectedConnection.getSource().setConnectionEnd(monGatePort);
					selectedConnection.getSource().setContext(monitorSubcomp);
				}

				// Create Reference port connections, if provided
				if (!referencePorts.isEmpty()) {
					for (Map.Entry<ConnectionEnd, ConnectionEnd> portEntry : monReferencePorts.entrySet()) {
						final Connection connExpected = ComponentCreateHelper.createOwnedConnection(containingImpl,
								portEntry.getKey());
						connExpected.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
								containingImpl.getAllConnections()));
						connExpected.setBidirectional(false);
						final ConnectedElement monitorExpectedSrc = connExpected.createSource();
						monitorExpectedSrc.setContext(ModelTransformUtils.getSubcomponent(containingImpl,
								referencePorts.get(portEntry.getValue().getName())));
						monitorExpectedSrc.setConnectionEnd(portEntry.getKey());
						final ConnectedElement monitorExpectedDst = connExpected.createDestination();
						monitorExpectedDst.setContext(monitorSubcomp);
						monitorExpectedDst.setConnectionEnd(portEntry.getValue());
						// Put connExpected in right place (before connObserved)
						destName = connObserved.getName();
						try {
							containingImpl.getOwnedConnections()
									.move(getIndex(destName, containingImpl.getOwnedConnections()),
											containingImpl.getOwnedConnections().size() - 1);
						} catch (Exception e) {

						}
					}
				}

				// Create Alert connection, if provided
				Connection connAlert = null;
				if (!alertPort.isEmpty()) {
					connAlert = ComponentCreateHelper.createOwnedConnection(containingImpl, monAlertPort);
					connAlert.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
							containingImpl.getAllConnections()));
					connAlert.setBidirectional(false);
					final ConnectedElement monitorAlertSrc = connAlert.createSource();
					monitorAlertSrc.setContext(monitorSubcomp);
					monitorAlertSrc.setConnectionEnd(monAlertPort);
					final ConnectedElement monitorAlertDst = connAlert.createDestination();
					monitorAlertDst.setContext(ModelTransformUtils.getSubcomponent(containingImpl, alertPort));
					monitorAlertDst.setConnectionEnd(dstAlertPort);
					// Put connAlert in right place (after connObserved)
					destName = connObserved.getName();
					try {
						containingImpl.getOwnedConnections()
								.move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
										containingImpl.getOwnedConnections().size() - 1);
					} catch (Exception e) {

					}
				}

				// Create Reset connection, if provided
				if (resetPort != null && !resetPort.isEmpty()) {
					final Connection connReset = ComponentCreateHelper.createOwnedConnection(containingImpl,
							monResetPort);
					connReset.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
							containingImpl.getAllConnections()));
					connReset.setBidirectional(false);
					final ConnectedElement monitorResetSrc = connReset.createSource();
					monitorResetSrc.setContext(ModelTransformUtils.getSubcomponent(containingImpl, resetPort));
					monitorResetSrc.setConnectionEnd(srcResetPort);
					final ConnectedElement monitorResetDst = connReset.createDestination();
					monitorResetDst.setContext(monitorSubcomp);
					monitorResetDst.setConnectionEnd(monResetPort);
					// Put connExpected in right place (after observed)
					destName = connObserved.getName();
					try {
						containingImpl.getOwnedConnections()
								.move(getIndex(destName, containingImpl.getOwnedConnections()) + 1,
										containingImpl.getOwnedConnections().size() - 1);
					} catch (Exception e) {

					}
				}

				// AGREE
				final StringBuilder agreeClauses = new StringBuilder();
				if (!monitorAgreeProperty.isEmpty()) {

					// CASE_Properties::Component_Spec property
					final String monitorAlertPropId = monitorName + "_" + alertPortName;
					String monitorGatePropId = "";
					if (observationGate) {
						monitorGatePropId += monitorName + "_" + observationGatePortName;
					}
					CasePropertyUtils.setCompSpec(monitorType,
							monitorAlertPropId + (monitorGatePropId.isEmpty() ? "" : "," + monitorGatePropId));

//					monitorAgreeProperty = "false";
//				} else if (!monitorAgreeProperty.trim().endsWith(";")) {
					monitorAgreeProperty = monitorAgreeProperty.trim();
//				}

//				final String monitorPolicyName = monitorName + "_policy";

					String resetString = "";
					if (resetPort != null) {
						if (monResetPort instanceof EventPort || monResetPort instanceof EventDataPort) {
							resetString = "not event(" + resetPortName + ") and ";
						} else {
							resetString = "not " + resetPortName + " and ";
						}
					}

					agreeClauses.append("{**" + System.lineSeparator());

					agreeClauses.append(
							"const is_latched : bool = " + (latched ? "true" : "false") + ";" + System.lineSeparator());
//					agreeClauses
//							.append("property " + monitorPolicyName + " = " + monitorAgreeProperty + System.lineSeparator());

					agreeClauses.append("property alerted = (");
					if (portObserved instanceof EventPort || portObserved instanceof EventDataPort) {
						agreeClauses.append("event(" + observationPortName + ") and ");
					}
					agreeClauses.append("not " + monitorAgreeProperty + ") -> ((" + resetString
							+ "is_latched and pre(alerted)) or (");
					if (portObserved instanceof EventPort || portObserved instanceof EventDataPort) {
						agreeClauses.append("event(" + observationPortName + ") and ");
					}
					agreeClauses.append("not " + monitorAgreeProperty + "));" + System.lineSeparator());

					String alertExpr = "";
					if (monAlertPort instanceof EventPort || monAlertPort instanceof EventDataPort) {
						alertExpr = "event(" + alertPortName + ") <=> alerted";
					}
					if (alertExpr.isEmpty()) {
						alertExpr = "false";
						agreeClauses.append("-- GUARANTEE EXPRESSION INCOMPLETE" + System.lineSeparator());
					}
					agreeClauses.append("guarantee " + monitorAlertPropId
							+ " \"A violation of the monitor policy shall trigger an alert\" : " + alertExpr + ";"
							+ System.lineSeparator());

					if (observationGate) {
						String gateExpr = "";
						if (monGatePort instanceof EventPort || monGatePort instanceof EventDataPort) {
							gateExpr = "if event(" + observationPortName + ") and not alerted then event("
									+ observationGatePortName + ") and " + observationGatePortName + " = "
									+ observationPortName + " else not event(" + observationGatePortName + ")";
						} else {
							gateExpr = "false";
							agreeClauses.append("-- GUARANTEE EXPRESSION INCOMPLETE" + System.lineSeparator());
						}
						agreeClauses.append("guarantee " + monitorGatePropId
								+ " \"A violation of the monitor policy shall prevent propagation of the observed input.\" : "
								+ gateExpr + ";" + System.lineSeparator());

					}

					agreeClauses.append("**}");

					if (isSel4Process) {
						final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
								.createOwnedAnnexSubclause(monitorImpl);
						annexSubclauseImpl.setName("agree");
						annexSubclauseImpl.setSourceText(
								"{**" + System.lineSeparator() + "lift contract;" + System.lineSeparator() + "**}");
					} else {
						final DefaultAnnexSubclause annexSubclauseImpl = ComponentCreateHelper
								.createOwnedAnnexSubclause(monitorType);
						annexSubclauseImpl.setName("agree");
						annexSubclauseImpl.setSourceText(agreeClauses.toString());
					}
				}

				// Add thread if this is a seL4 process
				if (isSel4Process) {
					Sel4TransformHandler.insertThreadInSel4Process((ProcessImplementation) monitorImpl,
							dispatchProtocol, stackSize, agreeClauses.toString());
				}

				// Add add_monitor claims to resolute prove statement, if applicable
				if (!monitorRequirement.isEmpty()) {
					final CyberRequirement req = RequirementsManager.getInstance().getRequirement(monitorRequirement);
					if (observationGate) {
						String gateContextName = "";
						if (selectedConnection.getDestination() != null
								&& selectedConnection.getDestination().getContext() != null) {
							gateContextName = selectedConnection.getDestination().getContext().getName();
						}
						claim = new AddMonitorClaim(req.getContext(), monitorSubcomp, monAlertPort, gateContextName,
								observedDataFeatureClassifier);
					} else {
						claim = new AddMonitorClaim(req.getContext(), monitorSubcomp, monAlertPort);
					}
				}
			}

			@Override
			public Collection<AddMonitorClaim> getResult() {
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
		} finally {
			domain.dispose();
		}

		@SuppressWarnings("unchecked")
		final List<AddMonitorClaim> claim = (List<AddMonitorClaim>) cmd.getResult();
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(monitorRequirement, claim.get(0));
		}

		return true;

	}

}
