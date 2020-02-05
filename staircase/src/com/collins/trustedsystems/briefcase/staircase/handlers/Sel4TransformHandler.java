package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.Context;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.Realization;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubprogramAccess;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.SystemSubcomponent;
import org.osate.aadl2.SystemType;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.ThreadGroupSubcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;

public class Sel4TransformHandler extends AadlHandler {

	static final String PORT_CONNECTION_IMPL_NAME = "c";
	static final String ACCESS_CONNECTION_IMPL_NAME = "a";

	@Override
	protected void runCommand(URI uri) {

		// Selection should be a system implementation
		// containing at least one process
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof SystemImplementation)) {
			Dialog.showError("seL4 Transform",
					"A system implementation containing at least one process must be selected to perform the seL4 transform.");
			return;
		}

		final SystemImplementation selectedSystem = (SystemImplementation) eObj;

		// Each process subcomponent containing a thread group and/or multiple thread subcomponents
		// becomes a system subcomponent
		// Each process thread becomes a process containing the thread
		// Each process thread group becomes a system
		// Re-wire everything appropriately
		// Change processor bindings
		// Set processor OS property to seL4

		transformSystem(selectedSystem);

	}

	/**
	 * Transforms a System into a System containing transformed subcomponents
	 * @param systemImpl - System implementation to be transformed
	 */
	public void transformSystem(SystemImplementation systemImpl) {

		// Transform contained systems
		// This will ensure that any process or thread group descendants will get transformed
		for (SystemSubcomponent ss : systemImpl.getOwnedSystemSubcomponents()) {
			transformSystem((SystemImplementation) ss.getComponentImplementation());
		}

		// Transform contained processes
		final Map<String, SystemImplementation> transformedSubs = new HashMap<>();
		for (ProcessSubcomponent ps : systemImpl.getOwnedProcessSubcomponents()) {
			SystemImplementation sysImpl = transformProcess(ps);
			if (sysImpl != null) {
				transformedSubs.put(ps.getName(), sysImpl);
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

						// TODO: Modify property associations that reference or apply to process subcomponent
						// TODO: make sure property is supported
						for (PropertyAssociation pa : systemImpl.getOwnedPropertyAssociations()) {
							for (ContainedNamedElement cne : pa.getAppliesTos()) {

							}
						}

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
	 * @return Resulting transformed System Implementation
	 */
	@SuppressWarnings("unchecked")
	public SystemImplementation transformProcess(ProcessSubcomponent process) {

		final ProcessImplementation processImpl = (ProcessImplementation) process.getComponentImplementation();

		// If the process only contains a single thread, no transform is necessary
		// Instead check for SEL4 property?
		if (processImpl.getOwnedThreadSubcomponents().size() <= 1
				&& processImpl.getOwnedThreadGroupSubcomponents().isEmpty()) {
			return null;
		}

		// Transform thread subcomponents
		final Map<String, ComponentImplementation> transformedSubs = new HashMap<>();
		for (ThreadSubcomponent ts : processImpl.getOwnedThreadSubcomponents()) {
			ProcessImplementation procImpl = transformThread(ts);
			if (procImpl != null) {
				transformedSubs.put(ts.getName(), procImpl);
			}
		}

		// Transform thread group subcomponents
		for (ThreadGroupSubcomponent tgs : processImpl.getOwnedThreadGroupSubcomponents()) {
			SystemImplementation sysImpl = transformThreadGroup(tgs);
			if (sysImpl != null) {
				transformedSubs.put(tgs.getName(), sysImpl);
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
	 * @return Resulting transformed System Implementation
	 */
	@SuppressWarnings("unchecked")
	public SystemImplementation transformThreadGroup(ThreadGroupSubcomponent threadGroup) {

		final ThreadGroupImplementation tgImpl = (ThreadGroupImplementation) threadGroup.getComponentImplementation();

		// Make sure this thread group hasn't already been transformed
		PackageSection ps = AadlUtil.getContainingPackageSection(tgImpl);
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c instanceof SystemImplementation && c.getName().equalsIgnoreCase(tgImpl.getName() + "_seL4")) {
				return (SystemImplementation) c;
			}
		}

		// Transform thread subcomponents
		final Map<String, ComponentImplementation> transformedSubs = new HashMap<>();
		for (ThreadSubcomponent ts : tgImpl.getOwnedThreadSubcomponents()) {
			ComponentImplementation procImpl = transformThread(ts);
			if (procImpl != null) {
				transformedSubs.put(ts.getName(), procImpl);
			}
		}

		// Transform thread group subcomponents
		for (ThreadGroupSubcomponent tgs : tgImpl.getOwnedThreadGroupSubcomponents()) {
			ComponentImplementation sysImpl = transformThreadGroup(tgs);
			if (sysImpl != null) {
				transformedSubs.put(tgs.getName(), sysImpl);
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
			if (c instanceof ProcessImplementation && c.getName().equalsIgnoreCase(threadImpl.getName() + "_seL4")) {
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
			return (ProcessImplementation) transformImplList.get(0);
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
	 * @param transformedSub - Map of compImpl's subcomponent names to transformed component implementation
	 * @return Resulting transformed component implementation
	 */
	public ComponentImplementation transform(ComponentImplementation compImpl,
			Map<String, ComponentImplementation> transformedSubs) {

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
		transformType.setName(compImpl.getTypeName() + "_seL4");

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

			Subcomponent sub = ComponentCreateHelper.createOwnedSubcomponent(transformImpl, compImpl.getCategory());
			sub.setName(compImpl.getTypeName());
			ComponentCreateHelper.setSubcomponentType(sub, compImpl);

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

			Iterator<Map.Entry<String, ComponentImplementation>> subIterator = transformedSubs.entrySet().iterator();
			while (subIterator.hasNext()) {
				Map.Entry<String, ComponentImplementation> mapElement = subIterator.next();
				Subcomponent sub = ComponentCreateHelper.createOwnedSubcomponent(transformImpl,
						mapElement.getValue().getCategory());
				// Give it the same name as the original subcomponent
				sub.setName(mapElement.getKey());
				ComponentCreateHelper.setSubcomponentType(sub, mapElement.getValue());
				// TODO: Copy property associations of original subcomponent

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

		return transformImpl;

	}

	private void copyPropertyAssociations(NamedElement from, NamedElement to) {
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

}
