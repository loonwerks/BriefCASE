package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
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
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DirectedFeature;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.MemoryProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;
import org.osate.xtext.aadl2.properties.util.TimingProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.ProxyTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddProxyClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class ProxyTransformHandler extends AadlHandler {

	public static final String HIGH_PROXY_COMP_TYPE_NAME = "HighProxy";
	public static final String LOW_PROXY_COMP_TYPE_NAME = "LowProxy";
	public static final String HIGH_PROXY_SUBCOMP_NAME = "HighProxy";
	public static final String LOW_PROXY_SUBCOMP_NAME = "LowProxy";
//	public static final String PROXIED_COMP_TYPE_NAME = "CASE_Software";
//	public static final String PROXIED_SUBCOMP_NAME = "SW";
	static final String CONNECTION_IMPL_NAME = "c";

	private String highProxyComponentName;
	private String highProxySubcomponentName;
	private Map<String, List<String>> highProxyPortNames;
	private String highProxyDispatchProtocol;
	private String highProxyPeriod;
	private String highProxyStackSize;
	private String lowProxyComponentName;
	private String lowProxySubcomponentName;
	private Map<String, List<String>> lowProxyPortNames;
	private String lowProxyDispatchProtocol;
	private String lowProxyPeriod;
	private String lowProxyStackSize;
//	private boolean addProxiedComponent;
//	private String proxiedComponentName;
//	private String proxiedSubcomponentName;
//	private String proxiedCompDispatchProtocol;
//	private String proxiedCompPeriod;
	private String proxyRequirement;
	private boolean isSel4Process = false;

	@Override
	protected String getJobName() {
		return "Proxy transform";
	}

	@Override
	protected void runCommand(EObject eObj) {

		// Check if it is a component implementation
		if (!(eObj instanceof SystemImplementation || eObj instanceof ProcessImplementation)) {
			Dialog.showError("Add Proxy", "A system or process implementation must be selected to add a proxy.");
			return;
		}
		final ComponentImplementation compImpl = (ComponentImplementation) eObj;

		// Open Proxy Transform wizard to get proxy info
		final ProxyTransformDialog wizard = new ProxyTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create(compImpl);
		if (wizard.open() == Window.OK) {
			highProxyComponentName = wizard.getHighProxyComponentName();
			if (highProxyComponentName.isEmpty()) {
				highProxyComponentName = HIGH_PROXY_COMP_TYPE_NAME;
			}
			highProxySubcomponentName = wizard.getHighProxySubcomponentName();
			if (highProxySubcomponentName.isEmpty()) {
				highProxySubcomponentName = HIGH_PROXY_SUBCOMP_NAME;
			}
			highProxyPortNames = wizard.getHighProxyPortNames();
			highProxyDispatchProtocol = wizard.getHighProxyDispatchProtocol();
			highProxyPeriod = wizard.getHighProxyPeriod();
			highProxyStackSize = wizard.getHighProxyStackSize();
			lowProxyComponentName = wizard.getLowProxyComponentName();
			if (lowProxyComponentName.isEmpty()) {
				lowProxyComponentName = LOW_PROXY_COMP_TYPE_NAME;
			}
			lowProxySubcomponentName = wizard.getLowProxySubcomponentName();
			if (lowProxySubcomponentName.isEmpty()) {
				lowProxySubcomponentName = LOW_PROXY_SUBCOMP_NAME;
			}
			lowProxyPortNames = wizard.getLowProxyPortNames();
			lowProxyDispatchProtocol = wizard.getLowProxyDispatchProtocol();
			lowProxyPeriod = wizard.getLowProxyPeriod();
			lowProxyStackSize = wizard.getLowProxyStackSize();
//			addProxiedComponent = wizard.getAddProxiedComponent();
//			proxiedComponentName = wizard.getProxiedComponentName();
//			if (proxiedComponentName.isEmpty()) {
//				proxiedComponentName = PROXIED_COMP_TYPE_NAME;
//			}
//			proxiedSubcomponentName = wizard.getProxiedSubcomponentName();
//			if (proxiedSubcomponentName.isEmpty()) {
//				proxiedSubcomponentName = PROXIED_SUBCOMP_NAME;
//			}
//			proxiedCompDispatchProtocol = wizard.getProxiedCompDispatchProtocol();
//			proxiedCompPeriod = wizard.getProxiedCompPeriod();
			proxyRequirement = wizard.getRequirement();
			isSel4Process = wizard.createThread();
		} else {
			return;
		}

		// Insert the proxy components
		if (insertProxy(compImpl)) {
			BriefcaseNotifier.notify("StairCASE - Proxy", "Proxy added to model.");

			// Format and save
			format(true);

//			// Save
//			saveChanges(false);
		} else {
			BriefcaseNotifier.printError("Proxy transform failed.");
		}

		return;

	}

	/**
	 * Inserts a proxy component into the model.
	 * @param uri - The URI of the selected component implementation that will contain the proxy
	 */
	private boolean insertProxy(ComponentImplementation containingImpl) {

		if (containingImpl == null) {
			Dialog.showError("Proxy Transform", "Invalid component implementation.");
			return false;
		}

		final Resource aadlResource = containingImpl.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			AddProxyClaim claim = null;

			@Override
			protected void doExecute() {
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

				ComponentCategory compCategory = containingImpl.getCategory();
				if (containingImpl instanceof SystemImplementation) {
					compCategory = ComponentCategory.PROCESS;
				} else if (containingImpl instanceof ProcessImplementation) {
					compCategory = ComponentCategory.THREAD;
				}
//				// If the component type is a process, we will need to put a single thread inside.
//				// Per convention, we will attach all properties and contracts to the thread.
//				// For this model transformation, we will create the thread first, then wrap it in a process
//				// component, using the same mechanism we use for the seL4 transformation
//				final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
//				if (isProcess) {
//					compCategory = ComponentCategory.THREAD;
//				}

				// Add High-side Proxy
				final Map<ConnectionEnd, List<ConnectionEnd>> highProxyPorts = new HashMap<>();
				final Subcomponent highProxySubcomp = createProxyComponent(containingImpl, compCategory,
						highProxyComponentName, highProxySubcomponentName, highProxyPortNames,
						highProxyDispatchProtocol, highProxyPeriod, highProxyStackSize, highProxyPorts);

				// Add Low-side Proxy
				final Map<ConnectionEnd, List<ConnectionEnd>> lowProxyPorts = new HashMap<>();
				final Subcomponent lowProxySubcomp = createProxyComponent(containingImpl, compCategory,
						lowProxyComponentName, lowProxySubcomponentName, lowProxyPortNames, lowProxyDispatchProtocol,
						lowProxyPeriod, lowProxyStackSize, lowProxyPorts);

//				// Add proxied component, if needed
//				if (addProxiedComponent) {
				//
//					// proxied component type
//					final ComponentType proxiedCompType = (ComponentType) pkgSection
//							.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
				//
//					// Give it a unique name
//					proxiedCompType.setName(ModelTransformUtils.getUniqueName(proxiedComponentName, true,
//							pkgSection.getOwnedClassifiers()));
				//
//					// TODO: Create proxy ports
				//
//					// Move to top of file
//					pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
				//
//					// Create proxied component implementation
//					final ComponentImplementation proxiedCompImpl = (ComponentImplementation) pkgSection
//							.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
//					proxiedCompImpl.setName(proxiedCompType.getName() + ".Impl");
//					final Realization r = proxiedCompImpl.createOwnedRealization();
//					r.setImplemented(proxiedCompType);
				//
//					// Move below component type
//					pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
				//
//					// Dispatch protocol property
//					if (!proxiedCompDispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
//						final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(proxiedCompImpl,
//								ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
//						final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
//						dispatchProtocolLit.setName(proxiedCompDispatchProtocol);
//						final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
//						nv.setNamedValue(dispatchProtocolLit);
//						proxiedCompImpl.setPropertyValue(dispatchProtocolProp, nv);
//					}
//					// Period
//					if (!proxiedCompPeriod.isEmpty() && compCategory == ComponentCategory.THREAD) {
//						final Property periodProp = GetProperties.lookupPropertyDefinition(proxiedCompImpl,
//								TimingProperties._NAME, TimingProperties.PERIOD);
//						final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
//						final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
//						unit.setName(proxiedCompPeriod.replaceAll("[\\d]", "").trim());
//						periodLit.setBase(0);
//						periodLit.setValue(Long.parseLong(proxiedCompPeriod.replaceAll("[\\D]", "").trim()));
//						periodLit.setUnit(unit);
//						proxiedCompImpl.setPropertyValue(periodProp, periodLit);
//					}
				//
//					// Insert monitor subcomponent in containing component implementation
//					final Subcomponent proxiedSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
//							compCategory);
				//
//					// Give it a unique name
//					proxiedSubcomp.setName(ModelTransformUtils.getUniqueName(proxiedSubcomponentName, true,
//							containingImpl.getOwnedSubcomponents()));
				//
//					ComponentCreateHelper.setSubcomponentType(proxiedSubcomp, proxiedCompImpl);
//				}

				// High-side connections
				for (Map.Entry<ConnectionEnd, List<ConnectionEnd>> portEntry : highProxyPorts.entrySet()) {
					final Connection conn = ComponentCreateHelper.createOwnedConnection(containingImpl,
							portEntry.getKey());
					conn.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
							containingImpl.getAllConnections()));
					conn.setBidirectional(false);
					final ConnectedElement src = conn.createSource();
					String srcPortName = "";
					for (Map.Entry<String, List<String>> highPortNames : highProxyPortNames.entrySet()) {
						if (highPortNames.getValue().get(0).equalsIgnoreCase(portEntry.getValue().get(0).getName())) {
							srcPortName = highPortNames.getKey();
						}
					}
					src.setContext(ModelTransformUtils.getSubcomponent(containingImpl, srcPortName));
					src.setConnectionEnd(portEntry.getKey());
					final ConnectedElement dst = conn.createDestination();
					dst.setContext(highProxySubcomp);
					dst.setConnectionEnd(portEntry.getValue().get(0));

				}

				// Low-side connections
				for (Map.Entry<ConnectionEnd, List<ConnectionEnd>> portEntry : lowProxyPorts.entrySet()) {
					final Connection conn = ComponentCreateHelper.createOwnedConnection(containingImpl,
							portEntry.getKey());
					conn.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
							containingImpl.getAllConnections()));
					conn.setBidirectional(false);
					final ConnectedElement src = conn.createSource();
					src.setContext(lowProxySubcomp);
					src.setConnectionEnd(portEntry.getValue().get(1));
					final ConnectedElement dst = conn.createDestination();
					String dstPortName = "";
					for (Map.Entry<String, List<String>> lowPortNames : lowProxyPortNames.entrySet()) {
						if (lowPortNames.getValue().get(1).equalsIgnoreCase(portEntry.getValue().get(1).getName())) {
							dstPortName = lowPortNames.getKey();
						}
					}
					dst.setContext(ModelTransformUtils.getSubcomponent(containingImpl, dstPortName));
					dst.setConnectionEnd(portEntry.getKey());
				}
			}

			@Override
			public Collection<AddProxyClaim> getResult() {
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
			return false;
		} finally {
			domain.dispose();
		}

		@SuppressWarnings("unchecked")
		final List<AddProxyClaim> claim = (List<AddProxyClaim>) cmd.getResult();
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(proxyRequirement, claim.get(0));
		}

		return true;

//		// Get the active xtext editor so we can make modifications
//		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		final AddProxyClaim claim = xtextEditor.getDocument().modify(resource -> {
//
//			// Retrieve the model object to modify
//			final ComponentImplementation containingImpl = (ComponentImplementation) resource.getEObject(uri.fragment());
//
//			PackageSection pkgSection = AadlUtil.getContainingPackageSection(containingImpl);
//			if (pkgSection == null) {
//				// Something went wrong
//				Dialog.showError("Proxy Transform", "No public or private package sections found.");
//				return null;
//			}
//
//			// Import CASE_Properties file
//			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
//				return null;
//			}
//
//			ComponentCategory compCategory = containingImpl.getCategory();
//			if (containingImpl instanceof SystemImplementation) {
//				compCategory = ComponentCategory.PROCESS;
//			} else if (containingImpl instanceof ProcessImplementation) {
//				compCategory = ComponentCategory.THREAD;
//			}
////			// If the component type is a process, we will need to put a single thread inside.
////			// Per convention, we will attach all properties and contracts to the thread.
////			// For this model transformation, we will create the thread first, then wrap it in a process
////			// component, using the same mechanism we use for the seL4 transformation
////			final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
////			if (isProcess) {
////				compCategory = ComponentCategory.THREAD;
////			}
//
//			// Add High-side Proxy
//			final Map<ConnectionEnd, List<ConnectionEnd>> highProxyPorts = new HashMap<>();
//			final Subcomponent highProxySubcomp = createProxyComponent(containingImpl,
//					compCategory, highProxyComponentName, highProxySubcomponentName, highProxyPortNames,
//					highProxyDispatchProtocol, highProxyPeriod, highProxyPorts);
//
//			// Add Low-side Proxy
//			final Map<ConnectionEnd, List<ConnectionEnd>> lowProxyPorts = new HashMap<>();
//			final Subcomponent lowProxySubcomp = createProxyComponent(containingImpl,
//					compCategory, lowProxyComponentName, lowProxySubcomponentName, lowProxyPortNames,
//					lowProxyDispatchProtocol, lowProxyPeriod, lowProxyPorts);
//
//
////			// Add proxied component, if needed
////			if (addProxiedComponent) {
////
////				// proxied component type
////				final ComponentType proxiedCompType = (ComponentType) pkgSection
////						.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));
////
////				// Give it a unique name
////				proxiedCompType.setName(ModelTransformUtils.getUniqueName(proxiedComponentName, true,
////						pkgSection.getOwnedClassifiers()));
////
////				// TODO: Create proxy ports
////
////				// Move to top of file
////				pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);
////
////				// Create proxied component implementation
////				final ComponentImplementation proxiedCompImpl = (ComponentImplementation) pkgSection
////						.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
////				proxiedCompImpl.setName(proxiedCompType.getName() + ".Impl");
////				final Realization r = proxiedCompImpl.createOwnedRealization();
////				r.setImplemented(proxiedCompType);
////
////				// Move below component type
////				pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);
////
////				// Dispatch protocol property
////				if (!proxiedCompDispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
////					final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(proxiedCompImpl,
////							ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
////					final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
////					dispatchProtocolLit.setName(proxiedCompDispatchProtocol);
////					final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
////					nv.setNamedValue(dispatchProtocolLit);
////					proxiedCompImpl.setPropertyValue(dispatchProtocolProp, nv);
////				}
////				// Period
////				if (!proxiedCompPeriod.isEmpty() && compCategory == ComponentCategory.THREAD) {
////					final Property periodProp = GetProperties.lookupPropertyDefinition(proxiedCompImpl,
////							TimingProperties._NAME, TimingProperties.PERIOD);
////					final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
////					final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
////					unit.setName(proxiedCompPeriod.replaceAll("[\\d]", "").trim());
////					periodLit.setBase(0);
////					periodLit.setValue(Long.parseLong(proxiedCompPeriod.replaceAll("[\\D]", "").trim()));
////					periodLit.setUnit(unit);
////					proxiedCompImpl.setPropertyValue(periodProp, periodLit);
////				}
////
////				// Insert monitor subcomponent in containing component implementation
////				final Subcomponent proxiedSubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl,
////						compCategory);
////
////				// Give it a unique name
////				proxiedSubcomp.setName(ModelTransformUtils.getUniqueName(proxiedSubcomponentName, true,
////						containingImpl.getOwnedSubcomponents()));
////
////				ComponentCreateHelper.setSubcomponentType(proxiedSubcomp, proxiedCompImpl);
////			}
//
//			// High-side connections
//			for (Map.Entry<ConnectionEnd, List<ConnectionEnd>> portEntry : highProxyPorts.entrySet()) {
//				final Connection conn = ComponentCreateHelper.createOwnedConnection(containingImpl, portEntry.getKey());
//				conn.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
//						containingImpl.getAllConnections()));
//				conn.setBidirectional(false);
//				final ConnectedElement src = conn.createSource();
//				String srcPortName = "";
//				for (Map.Entry<String, List<String>> highPortNames : highProxyPortNames.entrySet()) {
//					if (highPortNames.getValue().get(0).equalsIgnoreCase(portEntry.getValue().get(0).getName())) {
//						srcPortName = highPortNames.getKey();
//					}
//				}
//				src.setContext(ModelTransformUtils.getSubcomponent(containingImpl, srcPortName));
//				src.setConnectionEnd(portEntry.getKey());
//				final ConnectedElement dst = conn.createDestination();
//				dst.setContext(highProxySubcomp);
//				dst.setConnectionEnd(portEntry.getValue().get(0));
//
//			}
//
//			// Low-side connections
//			for (Map.Entry<ConnectionEnd, List<ConnectionEnd>> portEntry : lowProxyPorts.entrySet()) {
//				final Connection conn = ComponentCreateHelper.createOwnedConnection(containingImpl, portEntry.getKey());
//				conn.setName(ModelTransformUtils.getUniqueName(CONNECTION_IMPL_NAME, false,
//						containingImpl.getAllConnections()));
//				conn.setBidirectional(false);
//				final ConnectedElement src = conn.createSource();
//				src.setContext(lowProxySubcomp);
//				src.setConnectionEnd(portEntry.getValue().get(1));
//				final ConnectedElement dst = conn.createDestination();
//				String dstPortName = "";
//				for (Map.Entry<String, List<String>> lowPortNames : lowProxyPortNames.entrySet()) {
//					if (lowPortNames.getValue().get(1).equalsIgnoreCase(portEntry.getValue().get(1).getName())) {
//						dstPortName = lowPortNames.getKey();
//					}
//				}
//				dst.setContext(ModelTransformUtils.getSubcomponent(containingImpl, dstPortName));
//				dst.setConnectionEnd(portEntry.getKey());
//			}
//
////			if (isProcess) {
////
////				// TODO: Wrap thread component in a process
////
////				// TODO: Bind process to processor
////			}
//
//			// Add add_monitor claims to resolute prove statement, if applicable
////			if (!proxyRequirement.isEmpty()) {
////				final CyberRequirement req = RequirementsManager.getInstance().getRequirement(proxyRequirement);
////				return new AddProxyClaim(req.getContext(), highProxySubcomp, lowProxySubcomp);
////			}
//
//			return null;
//		});
//
//		if (claim != null) {
//			RequirementsManager.getInstance().modifyRequirement(proxyRequirement, claim);
//		}
	}

	private Subcomponent createProxyComponent(ComponentImplementation containingImpl,
			ComponentCategory compCategory,
			String componentName, String subcomponentName, Map<String, List<String>> proxyPortNames,
			String dispatchProtocol, String period, String stackSize,
			Map<ConnectionEnd, List<ConnectionEnd>> proxyPorts) {

		// Proxy component type
		PackageSection pkgSection = AadlUtil.getContainingPackageSection(containingImpl);
		final ComponentType proxyType = (ComponentType) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getTypeClass(compCategory));

		// Give it a unique name
		proxyType.setName(ModelTransformUtils.getUniqueName(componentName, true, pkgSection.getOwnedClassifiers()));

		// Create high-side proxy ports
//		final Map<ConnectionEnd, List<ConnectionEnd>> proxyPorts = new HashMap<>();
		DataSubcomponentType dataFeatureClassifier = null;
		for (Map.Entry<String, List<String>> namesEntry : proxyPortNames.entrySet()) {
			ConnectionEnd portIn = null;
			ConnectionEnd portOut = null;
			ConnectionEnd srcPort = ModelTransformUtils.getPort(containingImpl, namesEntry.getKey());
			// If source port is not found for some reason, make it an event data port
			if (srcPort == null) {
				portIn = ComponentCreateHelper.createOwnedEventDataPort(proxyType);
				portOut = ComponentCreateHelper.createOwnedEventDataPort(proxyType);
			}
			if (srcPort instanceof EventDataPort) {
				portIn = ComponentCreateHelper.createOwnedEventDataPort(proxyType);
				dataFeatureClassifier = ((EventDataPort) srcPort).getDataFeatureClassifier();
				((EventDataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
				for (ArrayDimension dim : ((EventDataPort) srcPort).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((EventDataPort) portIn).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
				portOut = ComponentCreateHelper.createOwnedEventDataPort(proxyType);
				dataFeatureClassifier = ((EventDataPort) srcPort).getDataFeatureClassifier();
				((EventDataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				for (ArrayDimension dim : ((EventDataPort) srcPort).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((EventDataPort) portOut).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (srcPort instanceof DataPort) {
				portIn = ComponentCreateHelper.createOwnedDataPort(proxyType);
				dataFeatureClassifier = ((DataPort) srcPort).getDataFeatureClassifier();
				((DataPort) portIn).setDataFeatureClassifier(dataFeatureClassifier);
				for (ArrayDimension dim : ((DataPort) srcPort).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((DataPort) portIn).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
				portOut = ComponentCreateHelper.createOwnedDataPort(proxyType);
				dataFeatureClassifier = ((DataPort) srcPort).getDataFeatureClassifier();
				((DataPort) portOut).setDataFeatureClassifier(dataFeatureClassifier);
				for (ArrayDimension dim : ((DataPort) srcPort).getArrayDimensions()) {
					final ArrayDimension arrayDimension = ((DataPort) portOut).createArrayDimension();
					arrayDimension.setSize(dim.getSize());
				}
			} else if (srcPort instanceof EventPort) {
				portIn = ComponentCreateHelper.createOwnedEventPort(proxyType);
				portOut = ComponentCreateHelper.createOwnedEventPort(proxyType);
			} else if (srcPort instanceof FeatureGroup) {
				portIn = proxyType.createOwnedFeatureGroup();
				((FeatureGroup) portIn).setFeatureType(((FeatureGroup) srcPort).getFeatureGroupType());
				portOut = proxyType.createOwnedFeatureGroup();
				((FeatureGroup) portOut).setFeatureType(((FeatureGroup) srcPort).getFeatureGroupType());
			}
			((DirectedFeature) portIn).setIn(true);
			((DirectedFeature) portOut).setOut(true);
			portIn.setName(namesEntry.getValue().get(0));
			portOut.setName(namesEntry.getValue().get(1));
			List<ConnectionEnd> portPairs = new ArrayList<>();
			portPairs.add(portIn);
			portPairs.add(portOut);
			proxyPorts.put(srcPort, portPairs);

		}

		// Mitigate type property
//		CasePropertyUtils.setMitigationType(highProxyType, MITIGATION_TYPE.PROXY);

		// Move to top of file
		pkgSection.getOwnedClassifiers().move(0, pkgSection.getOwnedClassifiers().size() - 1);


		// Create proxy implementation
		final ComponentImplementation proxyImpl = (ComponentImplementation) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getImplClass(compCategory));
		proxyImpl.setName(proxyType.getName() + ".Impl");
		final Realization r = proxyImpl.createOwnedRealization();
		r.setImplemented(proxyType);

		// Move below component type
		pkgSection.getOwnedClassifiers().move(1, pkgSection.getOwnedClassifiers().size() - 1);

		// Dispatch protocol property
		if (!dispatchProtocol.isEmpty() && compCategory == ComponentCategory.THREAD) {
			final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(proxyImpl,
					ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
			final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
			dispatchProtocolLit.setName(dispatchProtocol);
			final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
			nv.setNamedValue(dispatchProtocolLit);
			proxyImpl.setPropertyValue(dispatchProtocolProp, nv);
		}
		// Period
		if (!period.isEmpty() && compCategory == ComponentCategory.THREAD) {
			final Property periodProp = GetProperties.lookupPropertyDefinition(proxyImpl, TimingProperties._NAME,
					TimingProperties.PERIOD);
			final IntegerLiteral periodLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
			final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
			unit.setName(period.replaceAll("[\\d]", "").trim());
			periodLit.setBase(0);
			periodLit.setValue(Long.parseLong(period.replaceAll("[\\D]", "").trim()));
			periodLit.setUnit(unit);
			proxyImpl.setPropertyValue(periodProp, periodLit);
		}
		// Stack Size
		if (!stackSize.isEmpty()) {
			final Property stackSizeProp = GetProperties.lookupPropertyDefinition(proxyImpl, MemoryProperties._NAME,
					MemoryProperties.STACK_SIZE);
			final IntegerLiteral stackSizeLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
			final UnitLiteral unit = Aadl2Factory.eINSTANCE.createUnitLiteral();
			unit.setName(stackSize.replaceAll("[\\d]", "").trim());
			stackSizeLit.setBase(0);
			stackSizeLit.setValue(Long.parseLong(stackSize.replaceAll("[\\D]", "").trim()));
			stackSizeLit.setUnit(unit);
			proxyImpl.setPropertyValue(stackSizeProp, stackSizeLit);
		}

		// Insert monitor subcomponent in containing component implementation
		final Subcomponent proxySubcomp = ComponentCreateHelper.createOwnedSubcomponent(containingImpl, compCategory);

		// Give it a unique name
		proxySubcomp.setName(
				ModelTransformUtils.getUniqueName(subcomponentName, true, containingImpl.getAllSubcomponents()));

		ComponentCreateHelper.setSubcomponentType(proxySubcomp, proxyImpl);

		return proxySubcomp;

	}

}
