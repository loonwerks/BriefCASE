package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.Access;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.Context;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.ProcessorSubcomponent;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.Realization;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubprogramAccess;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.SystemSubcomponent;
import org.osate.aadl2.SystemType;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.ThreadGroupSubcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.VirtualProcessorSubcomponent;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.properties.PropertyAcc;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;

public class Sel4TransformHandler extends AadlHandler {

	static final String PORT_CONNECTION_IMPL_NAME = "c";
	static final String ACCESS_CONNECTION_IMPL_NAME = "a";
	static final String SEL4 = "seL4";

	Map<String, Set<String>> processorBindings = null;
	Set<String> sel4Processors = null;

	@Override
	protected void runCommand(URI uri) {

		// Selection must be a system implementation
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof SystemImplementation)) {
			Dialog.showError("seL4 Transform",
					"A system implementation containing at least one processor-bound process must be selected to perform the seL4 transform.");
			return;
		}

		final SystemImplementation selectedSystem = (SystemImplementation) eObj;

		// Selected system implementation should contain at least one processor-bound process
		// Get processor bindings for all software subcomponent descendants of selected system implementation
		// Neither the processor or the process need to be immediate subcomponents of the selected system (they can be nested descendants)
		processorBindings = getProcessorBindings(selectedSystem);

		// Get the names of seL4 processors in the system
		sel4Processors = getSel4Processors(selectedSystem);

		boolean sel4Found = false;
		for (Map.Entry<String, Set<String>> entry : processorBindings.entrySet()) {
			for (String processor : entry.getValue()) {
				if (sel4Processors.contains(processor)) {
					sel4Found = true;
					break;
				}
			}
		}
		if (!sel4Found || processorBindings.isEmpty()) {
			Dialog.showError("seL4 Transform",
					"Selected system implementation must contain at least one software subcomponent that is bound to an seL4 processor to perform the seL4 transform.");
			return;
		}


		// Each process subcomponent containing a thread group and/or multiple thread subcomponents
		// becomes a system subcomponent
		// Each process thread becomes a process containing the thread
		// Each process thread group becomes a system
		// Re-wire everything appropriately
		// Change processor bindings
		// Set processor OS property to seL4


		transformSystem(selectedSystem, "");

	}

	/**
	 * Transforms a System into a System containing transformed subcomponents
	 * @param systemImpl - System implementation to be transformed
	 * @param qualifiedName - Qualified name of subcomponent representing this system (will be empty if it's the top-level system)
	 */
	public void transformSystem(SystemImplementation systemImpl, String qualifiedName) {

		// Transform contained systems
		// This will ensure that any process or thread group descendants will get transformed
		for (SystemSubcomponent ss : systemImpl.getOwnedSystemSubcomponents()) {
			String qName = qualifiedName + (qualifiedName.isEmpty() ? "" : ".") + ss.getName();
			transformSystem((SystemImplementation) ss.getComponentImplementation(), qName);
		}

		// Transform contained processes
		final Map<String, SystemImplementation> transformedSubs = new HashMap<>();
		for (ProcessSubcomponent ps : systemImpl.getOwnedProcessSubcomponents()) {
			String qName = qualifiedName + (qualifiedName.isEmpty() ? "" : ".") + ps.getName();
			if (isBoundToSel4Processor(qName)) {
				SystemImplementation sysImpl = transformProcess(ps, qName);
				if (sysImpl != null) {
					transformedSubs.put(ps.getName(), sysImpl);
				}
			} else {
				System.out.println("Sub " + qName + " not bound to seL4 processor");
			}
		}

		Resource aadlResource = systemImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				// Unlike other component types, we will just replace the original process subcomponent
				// with the transformed system component
				// We won't create a new system implementation
				Iterator<ProcessSubcomponent> subIterator = systemImpl.getOwnedProcessSubcomponents().iterator();
				while (subIterator.hasNext()) {
					ProcessSubcomponent pSub = subIterator.next();
					SystemImplementation transformedImpl = transformedSubs.get(pSub.getName());
					if (transformedImpl != null) {
						// Create transformed subcomponent
						SystemSubcomponent sSub = systemImpl.createOwnedSystemSubcomponent();
						sSub.setName(pSub.getName());
						sSub.setSystemSubcomponentType(transformedImpl);
						// Copy subcomponent properties
						copyPropertyAssociations(pSub, sSub);

//						// TODO: Modify property associations that reference or apply to process subcomponent
//						// TODO: make sure property is supported
//						for (PropertyAssociation pa : systemImpl.getOwnedPropertyAssociations()) {
//							for (ContainedNamedElement cne : pa.getAppliesTos()) {
//
//							}
//						}

						// Update connections
						Iterator<Connection> connIterator = systemImpl.getOwnedConnections().iterator();
						while (connIterator.hasNext()) {
							Connection conn = connIterator.next();
							if (conn.getSource().getContext() == pSub) {
								for (Feature f : sSub.getComponentType().getOwnedFeatures()) {
									if (conn.getSource().getConnectionEnd().getName().equalsIgnoreCase(f.getName())) {
										conn.getSource().setConnectionEnd(f);
										break;
									}
								}
								conn.getSource().setContext(sSub);
							}
							if (conn.getDestination().getContext() == pSub) {
								for (Feature f : sSub.getComponentType().getOwnedFeatures()) {
									if (conn.getDestination().getConnectionEnd().getName()
											.equalsIgnoreCase(f.getName())) {
										conn.getDestination().setConnectionEnd(f);
										break;
									}
								}
								conn.getDestination().setContext(sSub);
							}
						}

						// Delete process subcomponent
						subIterator.remove();
					}
				}
			}

		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		} catch (RollbackException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		}

		return;
	}

	/**
	 * Transforms a Process into a System containing transformed Threads
	 * @param process - Process subcomponent to be transformed
	 * @param qualifiedName - Qualified name of subcomponent
	 * @return Resulting transformed System Implementation
	 */
	@SuppressWarnings("unchecked")
	public SystemImplementation transformProcess(ProcessSubcomponent process, String qualifiedName) {

		final ProcessImplementation processImpl = (ProcessImplementation) process.getComponentImplementation();

		// TODO: If the process is bound to a virtual processor NOT running seL4, no transform is necessary

		// If the process only contains a single thread, no transform is necessary
		// Instead check for SEL4 property?
		if (processImpl.getOwnedThreadSubcomponents().size() <= 1
				&& processImpl.getOwnedThreadGroupSubcomponents().isEmpty()) {
			return null;
		}

		// Transform thread subcomponents
		final Map<Subcomponent, ComponentImplementation> transformedSubs = new HashMap<>();
//		final Map<String, ComponentImplementation> transformedSubs = new HashMap<>();
		for (ThreadSubcomponent ts : processImpl.getOwnedThreadSubcomponents()) {
			String qName = qualifiedName + "." + ts.getName();
			if (isBoundToSel4Processor(qName)) {
				ProcessImplementation procImpl = transformThread(ts);
				if (procImpl != null) {
					transformedSubs.put(ts, procImpl);
//					transformedSubs.put(ts.getName(), procImpl);
				}
			} else {
				System.out.println("Sub " + qName + " not bound to seL4 processor");
			}
		}

		// Transform thread group subcomponents
		for (ThreadGroupSubcomponent tgs : processImpl.getOwnedThreadGroupSubcomponents()) {
			String qName = qualifiedName + "." + tgs.getName();
			if (isBoundToSel4Processor(qName)) {
				SystemImplementation sysImpl = transformThreadGroup(tgs, qName);
				if (sysImpl != null) {
					transformedSubs.put(tgs, sysImpl);
//					transformedSubs.put(tgs.getName(), sysImpl);
				}
			} else {
				System.out.println("Sub " + qName + " not bound to seL4 processor");
			}
		}

//		System.out.println(
//				"Transforming Process " + process.getName() + " : " + process.getComponentImplementation().getName());
		Resource aadlResource = processImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		Command cmd = new RecordingCommand(domain) {

			public ComponentImplementation implementation;

			@Override
			protected void doExecute() {
				implementation = transform(processImpl, transformedSubs);
			}

			@Override
			public List<ComponentImplementation> getResult() {
				return Collections.singletonList(implementation);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		} catch (RollbackException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		}

		List<ComponentImplementation> transformImplList = (List<ComponentImplementation>) cmd.getResult();
		if (transformImplList.get(0) instanceof SystemImplementation) {
			return (SystemImplementation) transformImplList.get(0);
		} else {
			return null;
		}

	}

	/**
	 * Transforms a Thread Group into a System containing transformed Threads
	 * @param threadGroup - Thread Group subcomponent to be transformed
	 * @param qualifiedName - Qualified name of subcomponent
	 * @return Resulting transformed System Implementation
	 */
	@SuppressWarnings("unchecked")
	public SystemImplementation transformThreadGroup(ThreadGroupSubcomponent threadGroup, String qualifiedName) {

		final ThreadGroupImplementation tgImpl = (ThreadGroupImplementation) threadGroup.getComponentImplementation();

		// Make sure this thread group hasn't already been transformed
		PackageSection ps = AadlUtil.getContainingPackageSection(tgImpl);
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c instanceof SystemImplementation
					&& c.getName().equalsIgnoreCase(tgImpl.getTypeName() + "_" + SEL4 + ".Impl")) {
				return (SystemImplementation) c;
			}
		}

		// Transform thread subcomponents
		final Map<Subcomponent, ComponentImplementation> transformedSubs = new HashMap<>();
//		final Map<String, ComponentImplementation> transformedSubs = new HashMap<>();
		for (ThreadSubcomponent ts : tgImpl.getOwnedThreadSubcomponents()) {
			ComponentImplementation procImpl = transformThread(ts);
			if (procImpl != null) {
				transformedSubs.put(ts, procImpl);
//				transformedSubs.put(ts.getName(), procImpl);
			}
		}

		// Transform thread group subcomponents
		for (ThreadGroupSubcomponent tgs : tgImpl.getOwnedThreadGroupSubcomponents()) {
			String qName = qualifiedName + "." + tgs.getName();
			if (isBoundToSel4Processor(qName)) {
				ComponentImplementation sysImpl = transformThreadGroup(tgs, qName);
				if (sysImpl != null) {
					transformedSubs.put(tgs, sysImpl);
//					transformedSubs.put(tgs.getName(), sysImpl);
				}
			} else {
				System.out.println("Sub " + qName + " not bound to seL4 processor");
			}
		}

//		System.out.println("Transforming Thread Group " + threadGroup.getName() + " : "
//				+ threadGroup.getComponentImplementation().getName());
		Resource aadlResource = tgImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		Command cmd = new RecordingCommand(domain) {

			public ComponentImplementation implementation;

			@Override
			protected void doExecute() {
				implementation = transform(tgImpl, transformedSubs);
			}

			@Override
			public List<ComponentImplementation> getResult() {
				return Collections.singletonList(implementation);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		} catch (RollbackException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		}

		List<ComponentImplementation> transformImplList = (List<ComponentImplementation>) cmd.getResult();
		if (transformImplList.get(0) instanceof SystemImplementation) {
			return (SystemImplementation) transformImplList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Transforms a Thread into a Process containing a Thread
	 * @param thread - Thread subcomponent to be transformed
	 * @return Resulting transformed Process implementation
	 */
	@SuppressWarnings("unchecked")
	public ProcessImplementation transformThread(ThreadSubcomponent thread) {

		final ThreadImplementation threadImpl = (ThreadImplementation) thread.getComponentImplementation();

		// Make sure this thread hasn't already been transformed
		PackageSection ps = AadlUtil.getContainingPackageSection(threadImpl);
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c instanceof ProcessImplementation
					&& c.getName().equalsIgnoreCase(threadImpl.getTypeName() + "_" + SEL4 + ".Impl")) {
				return (ProcessImplementation) c;
			}
		}

//		System.out.println(
//				"Transforming Thread " + thread.getName() + " : " + thread.getComponentImplementation().getName());
		Resource aadlResource = threadImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		Command cmd = new RecordingCommand(domain) {

			public ComponentImplementation implementation;

			@Override
			protected void doExecute() {
				implementation = transform(threadImpl, null);
			}

			@Override
			public List<ComponentImplementation> getResult() {
				return Collections.singletonList(implementation);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		} catch (RollbackException e) {
			e.printStackTrace();
//			setErrorMessage(e.getMessage());
		}

		List<ComponentImplementation> transformImplList = (List<ComponentImplementation>) cmd.getResult();
		if (transformImplList.get(0) instanceof ProcessImplementation) {
			ProcessImplementation processImpl = (ProcessImplementation) transformImplList.get(0);
			// Copy property associations of original subcomponent (if they are accepted by new subcomponent type)
			copyPropertyAssociations(thread, processImpl.getOwnedThreadSubcomponents().get(0));
			return processImpl;
		} else {
			return null;
		}

	}

	/**
	 * Transforms a Component for seL4:
	 * Thread -> Process,
	 * Thread Group -> System,
	 * Process -> System
	 * @param compImpl - Component implementation to be transformed
	 * @param transformedSubs - Map of compImpl's subcomponent names to transformed component implementation
	 * @return Resulting transformed component implementation
	 */
	public ComponentImplementation transform(ComponentImplementation compImpl,
			Map<Subcomponent, ComponentImplementation> transformedSubs) {
//	public ComponentImplementation transform(ComponentImplementation compImpl,
//			Map<String, ComponentImplementation> transformedSubs) {

		PackageSection pkgSection = AadlUtil.getContainingPackageSection(compImpl);

		// Create component type for transform
		ComponentType transformType = null;
		if (compImpl instanceof ThreadImplementation) {
			transformType = (ProcessType) pkgSection.createOwnedClassifier(Aadl2Package.eINSTANCE.getProcessType());
		} else if (compImpl instanceof ThreadGroupImplementation) {
			transformType = (SystemType) pkgSection.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemType());
		} else if (compImpl instanceof ProcessImplementation) {
			transformType = (SystemType) pkgSection.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemType());
		} else if (compImpl instanceof SystemImplementation) {
			transformType = (SystemType) pkgSection.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemType());
		}

		// Give it a name
		transformType.setName(compImpl.getTypeName() + "_" + SEL4);

		// Give it the same features
		Map<Feature, Feature> features = new HashMap<>();
		for (Feature compFeature : compImpl.getType().getOwnedFeatures()) {
			if (compFeature instanceof DataPort) {
				DataPort transformFeature = EcoreUtil.copy((DataPort) compFeature);
				ComponentCreateHelper.createOwnedDataPort(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof EventDataPort) {
				EventDataPort transformFeature = EcoreUtil.copy((EventDataPort) compFeature);
				ComponentCreateHelper.createOwnedEventDataPort(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof EventPort) {
				EventPort transformFeature = EcoreUtil.copy((EventPort) compFeature);
				ComponentCreateHelper.createOwnedEventPort(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof DataAccess) {
				DataAccess transformFeature = EcoreUtil.copy((DataAccess) compFeature);
				ComponentCreateHelper.createOwnedDataAccess(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof SubprogramAccess) {
				SubprogramAccess transformFeature = EcoreUtil.copy((SubprogramAccess) compFeature);
				ComponentCreateHelper.createOwnedSubprogramAccess(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			}
		}
		// Give it the same property associations
		copyPropertyAssociations(compImpl.getType(), transformType);
		// TODO: Add SEL4 property association?

//		// Give it the same AGREE clauses (for systems, processes, threadgroups) or lift AGREE clauses (for threads)
//		// if an AGREE annex is present.
		boolean liftContract = false;
		for (AnnexSubclause annexSubclause : compImpl.getType().getOwnedAnnexSubclauses()) {
			if (annexSubclause instanceof DefaultAnnexSubclause && annexSubclause.getName().equalsIgnoreCase("agree")) {
				if (transformType instanceof ProcessType) {
					// Add lift contract to process implementation
					liftContract = true;
				} else {
					// Copy AGREE annex
					DefaultAnnexSubclause defaultAnnexSubclause = (DefaultAnnexSubclause) EcoreUtil
							.copy(annexSubclause);
					transformType.getOwnedAnnexSubclauses().add(defaultAnnexSubclause);
				}
			}
		}
//
//		// Put it in the correct place (after component implementation of original component)
//		pkgSection.getOwnedClassifiers().move(getIndex(compImpl.getName(), pkgSection.getOwnedClassifiers()) + 1,
//				pkgSection.getOwnedClassifiers().size() - 1);

		// Create corresponding implementation
		ComponentImplementation transformImpl = null;
		if (compImpl instanceof ThreadImplementation) {
			transformImpl = (ProcessImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getProcessImplementation());
		} else if (compImpl instanceof ThreadGroupImplementation) {
			transformImpl = (SystemImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemImplementation());
		} else if (compImpl instanceof ProcessImplementation) {
			transformImpl = (SystemImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemImplementation());
		} else if (compImpl instanceof SystemImplementation) {
			transformImpl = (SystemImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemImplementation());
		}

		// Give it a name
		transformImpl.setName(transformType.getName() + ".Impl");
		// Give it a realization
		final Realization r = transformImpl.createOwnedRealization();
		r.setImplemented(transformType);

		// Add subcomponent(s) and connections
		if (transformedSubs == null) {

			// For transformed threads

			Subcomponent sub = ComponentCreateHelper.createOwnedSubcomponent(transformImpl, compImpl.getCategory());
			sub.setName(compImpl.getTypeName());
			ComponentCreateHelper.setSubcomponentType(sub, compImpl);
			// Thread subcomponent property associations get set by caller

			// Add connections
			Iterator<Map.Entry<Feature, Feature>> iterator = features.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Feature, Feature> f = iterator.next();
				Feature compFeature = f.getKey();
				Feature transformFeature = f.getValue();
				if (transformFeature instanceof Port) {
					PortConnection pc = transformImpl.createOwnedPortConnection();
					pc.setName(
							getUniqueName(PORT_CONNECTION_IMPL_NAME, false, transformImpl.getOwnedPortConnections()));
					pc.setBidirectional(((Port) transformFeature).getDirection() == DirectionType.IN_OUT);
					ConnectedElement src = pc.createSource();
					ConnectedElement dst = pc.createDestination();
					if (((Port) compFeature).isIn()) {
						src.setContext(null);
						src.setConnectionEnd(transformFeature);
						dst.setContext(sub);
						dst.setConnectionEnd(compFeature);
					} else {
						src.setContext(sub);
						src.setConnectionEnd(compFeature);
						dst.setContext(null);
						dst.setConnectionEnd(transformFeature);
					}
				} else if (transformFeature instanceof Access) {
					AccessConnection ac = transformImpl.createOwnedAccessConnection();
					ac.setName(getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false,
							transformImpl.getOwnedAccessConnections()));
					ConnectedElement src = ac.createSource();
					ConnectedElement dst = ac.createDestination();
					src.setContext(null);
					src.setConnectionEnd(transformFeature);
					dst.setContext(sub);
					dst.setConnectionEnd(compFeature);
				}

			}

		} else {

			// For transformed components other than threads

			Iterator<Map.Entry<Subcomponent, ComponentImplementation>> subIterator = transformedSubs.entrySet()
					.iterator();
//			Iterator<Map.Entry<String, ComponentImplementation>> subIterator = transformedSubs.entrySet().iterator();
			while (subIterator.hasNext()) {
				Map.Entry<Subcomponent, ComponentImplementation> mapElement = subIterator.next();
//				Map.Entry<String, ComponentImplementation> mapElement = subIterator.next();
				Subcomponent sub = ComponentCreateHelper.createOwnedSubcomponent(transformImpl,
						mapElement.getValue().getCategory());
				// Give it the same name as the original subcomponent
				sub.setName(mapElement.getKey().getName());
//				sub.setName(mapElement.getKey());
				ComponentCreateHelper.setSubcomponentType(sub, mapElement.getValue());
				// Copy property associations of original subcomponent (if they are accepted by new subcomponent type)
				copyPropertyAssociations(mapElement.getKey(), sub);
			}

			// Add connections
			for (Connection c : compImpl.getOwnedConnections()) {
				Context srcContext = null;
				Context dstContext = null;
				ConnectionEnd srcConnEnd = null;
				ConnectionEnd dstConnEnd = null;

				// Get the source and destination subcomponents
				for (Subcomponent tSub : transformImpl.getOwnedSubcomponents()) {
					Context cSrcContext = c.getSource().getContext();
					Context cDstContext = c.getDestination().getContext();
					if (cSrcContext != null && tSub.getName().equalsIgnoreCase(cSrcContext.getName())) {
						srcContext = tSub;
					} else if (cDstContext != null && tSub.getName().equalsIgnoreCase(cDstContext.getName())) {
						dstContext = tSub;
					}
				}

				// Get the source and destination features
				if (srcContext == null) {
					srcConnEnd = features.get(c.getSource().getConnectionEnd());
				} else {
					for (Feature compFeature : ((Subcomponent) srcContext).getComponentType().getOwnedFeatures()) {
						if (compFeature.getName().equalsIgnoreCase(c.getSource().getConnectionEnd().getName())) {
							srcConnEnd = compFeature;
							break;
						}
					}
				}
				if (dstContext == null) {
					dstConnEnd = features.get(c.getDestination().getConnectionEnd());
				} else {
					for (Feature compFeature : ((Subcomponent) dstContext).getComponentType().getOwnedFeatures()) {
						if (compFeature.getName().equalsIgnoreCase(c.getDestination().getConnectionEnd().getName())) {
							dstConnEnd = compFeature;
							break;
						}
					}
				}

				if (c instanceof PortConnection) {
					PortConnection pc = transformImpl.createOwnedPortConnection();
					pc.setName(
							getUniqueName(PORT_CONNECTION_IMPL_NAME, false, transformImpl.getOwnedPortConnections()));
					pc.setBidirectional(c.isBidirectional());
					ConnectedElement src = pc.createSource();
					ConnectedElement dst = pc.createDestination();
					src.setContext(srcContext);
					src.setConnectionEnd(srcConnEnd);
					dst.setContext(dstContext);
					dst.setConnectionEnd(dstConnEnd);
					// Give the connection the same property associations
					copyPropertyAssociations(c, pc);
				} else if (c instanceof AccessConnection) {
					AccessConnection ac = transformImpl.createOwnedAccessConnection();
					ac.setName(getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false,
							transformImpl.getOwnedAccessConnections()));
					ConnectedElement src = ac.createSource();
					ConnectedElement dst = ac.createDestination();
					src.setContext(srcContext);
					src.setConnectionEnd(srcConnEnd);
					dst.setContext(dstContext);
					dst.setConnectionEnd(dstConnEnd);
					// Give the connection the same property associations
					copyPropertyAssociations(c, ac);
				}

			}

		}

		// Give it the same property associations
		copyPropertyAssociations(compImpl, transformImpl);

//		// Give it the same AGREE clauses (for systems, processes, threadgroups) or lift AGREE clauses (for threads)
//		// if an AGREE annex is present.
//		if (liftContract) {
//			// Add lift contract
//			String agreeClause = "{**" + System.lineSeparator() + "lift contract;" + System.lineSeparator() + "**}";
//			final DefaultAnnexSubclause defaultAnnexSubclause = ComponentCreateHelper
//					.createOwnedAnnexSubclause(transformImpl);
//			defaultAnnexSubclause.setName("agree");
//			defaultAnnexSubclause.setSourceText(agreeClause);
//		}
//
//		// Put it in the correct place (after transformed component type)
//		pkgSection.getOwnedClassifiers().move(getIndex(transformType.getName(), pkgSection.getOwnedClassifiers()) + 1,
//				pkgSection.getOwnedClassifiers().size() - 1);

		return transformImpl;

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
				to.getOwnedPropertyAssociations().add(propAssoc);
			} else {
				// TODO: log exception
			}
		}
	}

	private Map<String, Set<String>> getProcessorBindings(SystemImplementation selectedSystem) {

		Map<String, Set<String>> processorBindings = new HashMap<>();

		// Get explicit processor bindings
		getExplicitProcessorBindings(selectedSystem, processorBindings);

		// Get implicit processor bindings
		for (Subcomponent sub : selectedSystem.getOwnedSubcomponents()) {
			if (isSoftwareSubcomponent(sub)) {
				getImplicitProcessorBindings(sub, "", processorBindings);
			}
		}

		return processorBindings;
	}

	private void getExplicitProcessorBindings(SystemImplementation sysImpl,
			Map<String, Set<String>> processorBindings) {

		// Get processor bindings specified in nested system implementation properties
		for (SystemSubcomponent sub : sysImpl.getOwnedSystemSubcomponents()) {
			getExplicitProcessorBindings((SystemImplementation) sub.getComponentImplementation(), processorBindings);
		}

		for (PropertyAssociation pa : sysImpl.getOwnedPropertyAssociations()) {
			if (pa.getProperty().getName().equalsIgnoreCase("Actual_Processor_Binding")) {
				for (ContainedNamedElement cne : pa.getAppliesTos()) {
					ContainmentPathElement cpe = cne.getPath();
					String appliesTo = cpe.getNamedElement().getName();
					Subcomponent boundSub = (Subcomponent) cpe.getNamedElement();
					while (cpe.getPath() != null) {
						cpe = cpe.getPath();
						appliesTo += "." + cpe.getNamedElement().getName();

						for (Subcomponent sub : boundSub.getComponentImplementation().getOwnedSubcomponents()) {
							if (sub.getName().equalsIgnoreCase(cpe.getNamedElement().getName())) {
								boundSub = sub;
								break;
							}
						}

					}

					// Get the processor this subcomponent is bound to
					for (ModalPropertyValue val : pa.getOwnedValues()) {
						ListValue listVal = (ListValue) val.getOwnedValue();
						for (PropertyExpression propExpr : listVal.getOwnedListElements()) {
							if (propExpr instanceof ReferenceValue) {
								ReferenceValue refVal = (ReferenceValue) propExpr;

								// TODO: Handle case when processor is in a nested subsystem
								if (refVal.getPath().getNamedElement() instanceof ProcessorSubcomponent
										|| refVal.getPath().getNamedElement() instanceof VirtualProcessorSubcomponent) {

									if (processorBindings.get(appliesTo) == null) {
										processorBindings.put(appliesTo, new HashSet<String>(
												Arrays.asList(refVal.getPath().getNamedElement().getName())));
									} else {
										processorBindings.get(appliesTo)
												.add(refVal.getPath().getNamedElement().getName());
									}

								}
							}
						}
					}

				}
			}
		}

	}

	private void getImplicitProcessorBindings(Subcomponent rootSub, String parentName,
			Map<String, Set<String>> processorBindings) {

		String qualifiedName = (parentName.isEmpty() ? "" : parentName + ".") + rootSub.getName();

		// If the specified subcomponent isn't explicitly bound to a processor,
		// check its parents binding
		if (processorBindings.get(qualifiedName) == null) {
			if (processorBindings.containsKey(parentName)) {
				processorBindings.put(qualifiedName, processorBindings.get(parentName));
			}
		}
		for (Subcomponent sub : rootSub.getComponentImplementation().getOwnedSubcomponents()) {
			if (isSoftwareSubcomponent(sub)) {
				getImplicitProcessorBindings(sub, qualifiedName, processorBindings);
			}
		}

	}

	private Set<String> getSel4Processors(SystemImplementation sysImpl) {
		Set<String> processors = new HashSet<>();
		for (Subcomponent sub : sysImpl.getOwnedSubcomponents()) {
			if (sub instanceof ProcessorSubcomponent || sub instanceof VirtualProcessorSubcomponent) {
				Property prop = Aadl2GlobalScopeUtil.get(sysImpl, Aadl2Package.eINSTANCE.getProperty(),
						CaseUtils.CASE_PROPSET_NAME + "::OS");
				PropertyAcc propAcc = sub.getComponentImplementation().getPropertyValue(prop);
				PropertyAssociation pa = propAcc.first();
				if (pa != null) {
					for (ModalPropertyValue propVal : pa.getOwnedValues()) {
						PropertyExpression propExpr = propVal.getOwnedValue();
						if (propExpr instanceof StringLiteral) {
							StringLiteral stringLit = (StringLiteral) propExpr;
							if (stringLit.getValue().equalsIgnoreCase(SEL4)) {
								processors.add(sub.getName());
							}
						}
					}
				}
			}
		}
		return processors;
	}

	private boolean isSoftwareSubcomponent(Subcomponent sub) {
		if (sub instanceof SystemSubcomponent) {
			return true;
		} else if (sub instanceof ProcessSubcomponent) {
			return true;
		} else if (sub instanceof ThreadGroupSubcomponent) {
			return true;
		} else if (sub instanceof ThreadSubcomponent) {
			return true;
		}
		return false;
	}

	private boolean isBoundToSel4Processor(String subcomponentQualifiedName) {
		for (String processor : processorBindings.get(subcomponentQualifiedName)) {
			if (processor == null) {
				continue;
			}
			if (sel4Processors.contains(processor)) {
				return true;
			}
		}
		return false;
	}

}
