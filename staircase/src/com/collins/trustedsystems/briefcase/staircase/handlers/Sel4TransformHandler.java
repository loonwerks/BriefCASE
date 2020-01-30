package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.Access;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.ConnectedElement;
import org.osate.aadl2.Connection;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
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
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.utils.ModifyUtils;
import com.collins.trustedsystems.briefcase.util.Filesystem;

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

		// Search for nested process subcomponents
		List<ProcessSubcomponent> procSubs = new ArrayList<>();
		getProcessDescendants(selectedSystem, procSubs);
		if (procSubs.isEmpty()) {
			Dialog.showError("seL4 Transform",
					"A system implementation containing at least one process must be selected to perform the seL4 transform.");
			return;
		}

		// Each process subcomponent containing a thread group and/or multiple thread subcomponents
		// becomes a system subcomponent
		// Each process thread becomes a process containing the thread
		// Each process thread group becomes a system
		// Re-wire everything appropriately
		// Change processor bindings
		// Set processor OS property to seL4

		// TODO: Transform systems that contain process descendants

		// Transform contained processes
		procSubs.forEach(p -> transformProcess(p));
		final Map<String, URI> sysUris = new HashMap<>();
		for (ProcessSubcomponent ps : procSubs) {
			URI sysUri = transformProcess(ps);
			if (sysUri != null) {
				sysUris.put(ps.getName(), sysUri);
			}
		}

		XtextEditor editor = ModifyUtils.getEditor(Filesystem.getFile(selectedSystem.eResource().getURI()));
		if (editor == null) {
			throw new RuntimeException(
					"Could not transform process " + selectedSystem.getQualifiedName() + " for seL4.");
		}

		editor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			SystemImplementation sysImpl = (SystemImplementation) resource
					.getEObject(EcoreUtil.getURI(selectedSystem).fragment());
			PackageSection pkgSection = (PackageSection) sysImpl.eContainer();

			// Add transformed processes

			return null;
		});

	}

	public URI transformProcess(ProcessSubcomponent process) {

		// If the process only contains a single thread, no transform is necessary
		final ProcessImplementation processImpl = (ProcessImplementation) process.getComponentImplementation();
		// Instead check for SEL4 property?
		if (processImpl.getOwnedThreadSubcomponents().size() <= 1
				&& processImpl.getOwnedThreadGroupSubcomponents().isEmpty()) {
			return EcoreUtil.getURI(processImpl.getType());
		}

		// Make sure this process hasn't already been transformed
		PackageSection ps = (PackageSection) processImpl.eContainer();
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c.getName().equalsIgnoreCase(processImpl.getType().getName() + "_Sys")) {
				return EcoreUtil.getURI(c);
			}
		}

		// Transform thread subcomponents
		final Map<String, URI> procUris = new HashMap<>();
		for (ThreadSubcomponent ts : processImpl.getOwnedThreadSubcomponents()) {
			URI uri = transformThread(ts);
			if (uri != null) {
				procUris.put(ts.getName(), uri);
			}
		}

		// Transform thread group subcomponents
		final Map<String, URI> sysUris = new HashMap<>();
		for (ThreadGroupSubcomponent tgs : processImpl.getOwnedThreadGroupSubcomponents()) {
			URI uri = transformThreadGroup(tgs);
			if (uri != null) {
				sysUris.put(tgs.getName(), uri);
			}
		}

		XtextEditor editor = ModifyUtils.getEditor(Filesystem.getFile(processImpl.eResource().getURI()));
		if (editor == null) {
			throw new RuntimeException("Could not transform process " + process.getQualifiedName() + " for seL4.");
		}

		URI sysUri = editor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			ProcessImplementation procImpl = (ProcessImplementation) resource
					.getEObject(EcoreUtil.getURI(processImpl).fragment());
			PackageSection pkgSection = (PackageSection) procImpl.eContainer();

			// Create corresponding system type
			SystemType sysType = (SystemType) pkgSection.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemType());
			// Give it a name
			sysType.setName(procImpl.getTypeName() + "_Sys");
			// Give it the same features
			Map<Feature, Feature> features = new HashMap<>();
			for (Feature f : procImpl.getType().getOwnedFeatures()) {
				if (f instanceof DataPort) {
					DataPort p = sysType.createOwnedDataPort();
					p = EcoreUtil.copy((DataPort) f);
					features.put(f, p);
				} else if (f instanceof EventDataPort) {
					EventDataPort p = sysType.createOwnedEventDataPort();
					p = EcoreUtil.copy((EventDataPort) f);
					features.put(f, p);
				} else if (f instanceof EventPort) {
					EventPort p = sysType.createOwnedEventPort();
					p = EcoreUtil.copy((EventPort) f);
					features.put(f, p);
				} else if (f instanceof DataAccess) {
					DataAccess a = sysType.createOwnedDataAccess();
					a = EcoreUtil.copy((DataAccess) f);
					features.put(f, a);
				} else if (f instanceof SubprogramAccess) {
					SubprogramAccess a = sysType.createOwnedSubprogramAccess();
					a = EcoreUtil.copy((SubprogramAccess) f);
					features.put(f, a);
				}
			}
			// Give it the same property associations
			for (PropertyAssociation pa : procImpl.getType().getOwnedPropertyAssociations()) {
				PropertyAssociation prop = sysType.createOwnedPropertyAssociation();
				prop = EcoreUtil.copy(pa);
			}
			// TODO: Add SEL4 property association?

			// Create corresponding system implementation
			SystemImplementation sysImpl = (SystemImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemImplementation());
			// Give it a name
			sysImpl.setName(sysType.getName() + ".Impl");
			// Give it a realization
			final Realization r = sysImpl.createOwnedRealization();
			r.setImplemented(sysType);
			// Add transformed thread subcomponent(s),
			// which will be a seL4 process containing a single thread
			Iterator<Map.Entry<String, URI>> iterator = procUris.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, URI> uri = iterator.next();
				ProcessImplementation pImpl = (ProcessImplementation) resource.getEObject(uri.getValue().fragment());
				ProcessSubcomponent pSub = sysImpl.createOwnedProcessSubcomponent();
				// Give it the same name as the original thread subcomponent
				pSub.setName(uri.getKey());
				pSub.setProcessSubcomponentType(pImpl.getType());
			}
			// Add transformed thread group subcomponent(s),
			// which will be a seL4 system containing 1 or more seL4 processes
			iterator = sysUris.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, URI> uri = iterator.next();
				SystemImplementation sImpl = (SystemImplementation) resource.getEObject(uri.getValue().fragment());
				SystemSubcomponent sSub = sysImpl.createOwnedSystemSubcomponent();
				// Give it the same name as the original thread group subcomponent
				sSub.setName(uri.getKey());
				sSub.setSystemSubcomponentType(sImpl.getType());
			}

			// Add connections
			for (Connection c : procImpl.getOwnedConnections()) {
				Feature sysFeature = null;
				Feature subsysFeature = null;
				Subcomponent sub = null;
				if (c.getSource().getContext() == null) {
					sysFeature = features.get(c.getSource().getConnectionEnd());
					String subsysFeatureName = c.getDestination().getConnectionEnd().getName();
					Subcomponent procSub = (Subcomponent) c.getDestination().getContext();
					if (procSub instanceof ThreadSubcomponent) {
						sub = (Subcomponent) resource.getEObject(procUris.get(procSub.getName()).fragment());
					} else {
						sub = (Subcomponent) resource.getEObject(sysUris.get(procSub.getName()).fragment());
					}
					ComponentType subsysType = sub.getComponentType();
					for (Feature compFeature : subsysType.getOwnedFeatures()) {
						if (compFeature.getName().equalsIgnoreCase(subsysFeatureName)) {
							subsysFeature = compFeature;
							break;
						}
					}
				} else {
					sysFeature = features.get(c.getDestination().getConnectionEnd());
					String subsysFeatureName = c.getSource().getConnectionEnd().getName();
					Subcomponent procSub = (Subcomponent) c.getSource().getContext();
					if (procSub instanceof ThreadSubcomponent) {
						sub = (Subcomponent) resource.getEObject(procUris.get(procSub.getName()).fragment());
					} else {
						sub = (Subcomponent) resource.getEObject(sysUris.get(procSub.getName()).fragment());
					}
					ComponentType subsysType = sub.getComponentType();
					for (Feature compFeature : subsysType.getOwnedFeatures()) {
						if (compFeature.getName().equalsIgnoreCase(subsysFeatureName)) {
							subsysFeature = compFeature;
							break;
						}
					}
				}

				if (c instanceof PortConnection) {
					PortConnection pc = sysImpl.createOwnedPortConnection();
					pc.setName(getUniqueName(PORT_CONNECTION_IMPL_NAME, false, sysImpl.getOwnedPortConnections()));
					pc.setBidirectional(c.isBidirectional());
					ConnectedElement src = pc.createSource();
					ConnectedElement dst = pc.createDestination();
					if (c.getSource().getContext() == null) {
						src.setContext(null);
						src.setConnectionEnd(sysFeature);
						dst.setContext(sub);
						dst.setConnectionEnd(subsysFeature);
					} else {
						src.setContext(sub);
						src.setConnectionEnd(subsysFeature);
						dst.setContext(null);
						dst.setConnectionEnd(sysFeature);
					}
				} else if (c instanceof AccessConnection) {
					AccessConnection ac = sysImpl.createOwnedAccessConnection();
					ac.setName(getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false, sysImpl.getOwnedAccessConnections()));
					ConnectedElement src = ac.createSource();
					src.setContext(null);
					src.setConnectionEnd(sysFeature);
					ConnectedElement dst = ac.createDestination();
					dst.setContext(sub);
					dst.setConnectionEnd(subsysFeature);
				}
			}

			// Give it the same property associations
			for (PropertyAssociation pa : procImpl.getOwnedPropertyAssociations()) {
				PropertyAssociation prop = sysImpl.createOwnedPropertyAssociation();
				prop = EcoreUtil.copy(pa);
//							prop.setProperty(pa.getProperty());
//							for (ModalPropertyValue mpv : pa.getOwnedValues()) {
//								ModalPropertyValue val = prop.createOwnedValue();
//								val.setOwnedValue(mpv.getOwnedValue());
//							}
			}

			return EcoreUtil.getURI(sysImpl);

		});

		ModifyUtils.closeEditor(editor, true);

		return sysUri;

	}

	/**
	 * Transforms a Thread Group into a System containing transformed Threads
	 * @param threadGroup - Thread Group subcomponent to be transformed
	 * @return URI of the resulting System Type
	 */
	public URI transformThreadGroup(ThreadGroupSubcomponent threadGroup) {

		final ThreadGroupImplementation tgImpl = (ThreadGroupImplementation) threadGroup.getComponentImplementation();

		// Make sure this thread group hasn't already been transformed
		PackageSection ps = (PackageSection) tgImpl.eContainer();
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c.getName().equalsIgnoreCase(tgImpl.getType().getName() + "_Sys")) {
				return EcoreUtil.getURI(c);
			}
		}

		// Transform thread subcomponents
		final Map<String, URI> procUris = new HashMap<>();
		for (ThreadSubcomponent ts : tgImpl.getOwnedThreadSubcomponents()) {
			URI uri = transformThread(ts);
			if (uri != null) {
				procUris.put(ts.getName(), uri);
			}
		}

		// Transform thread group subcomponents
		final Map<String, URI> sysUris = new HashMap<>();
		for (ThreadGroupSubcomponent tgs : tgImpl.getOwnedThreadGroupSubcomponents()) {
			URI uri = transformThreadGroup(tgs);
			if (uri != null) {
				sysUris.put(tgs.getName(), uri);
			}
		}

		XtextEditor editor = ModifyUtils.getEditor(Filesystem.getFile(tgImpl.eResource().getURI()));
		if (editor == null) {
			throw new RuntimeException(
					"Could not transform thread group " + threadGroup.getQualifiedName() + " for seL4.");
		}

		URI sysUri = editor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			ThreadGroupImplementation thrGrpImpl = (ThreadGroupImplementation) resource
					.getEObject(EcoreUtil.getURI(tgImpl).fragment());
			PackageSection pkgSection = (PackageSection) thrGrpImpl.eContainer();

			// Create corresponding system type
			SystemType sysType = (SystemType) pkgSection.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemType());
			// Give it a name
			sysType.setName(thrGrpImpl.getTypeName() + "_Sys");
			// Give it the same features
			Map<Feature, Feature> features = new HashMap<>();
			for (Feature f : thrGrpImpl.getType().getOwnedFeatures()) {
				if (f instanceof DataPort) {
					DataPort p = sysType.createOwnedDataPort();
					p = EcoreUtil.copy((DataPort) f);
					features.put(f, p);
				} else if (f instanceof EventDataPort) {
					EventDataPort p = sysType.createOwnedEventDataPort();
					p = EcoreUtil.copy((EventDataPort) f);
					features.put(f, p);
				} else if (f instanceof EventPort) {
					EventPort p = sysType.createOwnedEventPort();
					p = EcoreUtil.copy((EventPort) f);
					features.put(f, p);
				} else if (f instanceof DataAccess) {
					DataAccess a = sysType.createOwnedDataAccess();
					a = EcoreUtil.copy((DataAccess) f);
					features.put(f, a);
				} else if (f instanceof SubprogramAccess) {
					SubprogramAccess a = sysType.createOwnedSubprogramAccess();
					a = EcoreUtil.copy((SubprogramAccess) f);
					features.put(f, a);
				}
			}
			// Give it the same property associations
			for (PropertyAssociation pa : thrGrpImpl.getType().getOwnedPropertyAssociations()) {
				PropertyAssociation prop = sysType.createOwnedPropertyAssociation();
				prop = EcoreUtil.copy(pa);
//				prop.setProperty(pa.getProperty());
//				for (ModalPropertyValue mpv : pa.getOwnedValues()) {
//					ModalPropertyValue val = prop.createOwnedValue();
//					val.setOwnedValue(mpv.getOwnedValue());
//				}
			}
			// TODO: Add SEL4 property association?

			// Create corresponding system implementation
			SystemImplementation sysImpl = (SystemImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getSystemImplementation());
			// Give it a name
			sysImpl.setName(sysType.getName() + ".Impl");
			// Give it a realization
			final Realization r = sysImpl.createOwnedRealization();
			r.setImplemented(sysType);
			// Add transformed thread subcomponent(s),
			// which will be a seL4 process containing a single thread
			Iterator<Map.Entry<String, URI>> iterator = procUris.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, URI> uri = iterator.next();
				ProcessImplementation procImpl = (ProcessImplementation) resource.getEObject(uri.getValue().fragment());
				ProcessSubcomponent pSub = sysImpl.createOwnedProcessSubcomponent();
				// Give it the same name as the original thread subcomponent
				pSub.setName(uri.getKey());
				pSub.setProcessSubcomponentType(procImpl.getType());
			}
			// Add transformed thread group subcomponent(s),
			// which will be a seL4 system containing 1 or more seL4 processes
			iterator = sysUris.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, URI> uri = iterator.next();
				SystemImplementation sImpl = (SystemImplementation) resource.getEObject(uri.getValue().fragment());
				SystemSubcomponent sSub = sysImpl.createOwnedSystemSubcomponent();
				// Give it the same name as the original thread group subcomponent
				sSub.setName(uri.getKey());
				sSub.setSystemSubcomponentType(sImpl.getType());
			}

			// Add connections
			for (Connection c : thrGrpImpl.getOwnedConnections()) {
				Feature sysFeature = null;
				Feature subsysFeature = null;
				Subcomponent sub = null;
				if (c.getSource().getContext() == null) {
					sysFeature = features.get(c.getSource().getConnectionEnd());
					String subsysFeatureName = c.getDestination().getConnectionEnd().getName();
					Subcomponent thrGrpSub = (Subcomponent) c.getDestination().getContext();
					if (thrGrpSub instanceof ThreadSubcomponent) {
						sub = (Subcomponent) resource.getEObject(procUris.get(thrGrpSub.getName()).fragment());
					} else {
						sub = (Subcomponent) resource.getEObject(sysUris.get(thrGrpSub.getName()).fragment());
					}
					ComponentType subsysType = sub.getComponentType();
					for (Feature compFeature : subsysType.getOwnedFeatures()) {
						if (compFeature.getName().equalsIgnoreCase(subsysFeatureName)) {
							subsysFeature = compFeature;
							break;
						}
					}
				} else {
					sysFeature = features.get(c.getDestination().getConnectionEnd());
					String subsysFeatureName = c.getSource().getConnectionEnd().getName();
					Subcomponent thrGrpSub = (Subcomponent) c.getSource().getContext();
					if (thrGrpSub instanceof ThreadSubcomponent) {
						sub = (Subcomponent) resource.getEObject(procUris.get(thrGrpSub.getName()).fragment());
					} else {
						sub = (Subcomponent) resource.getEObject(sysUris.get(thrGrpSub.getName()).fragment());
					}
					ComponentType subsysType = sub.getComponentType();
					for (Feature compFeature : subsysType.getOwnedFeatures()) {
						if (compFeature.getName().equalsIgnoreCase(subsysFeatureName)) {
							subsysFeature = compFeature;
							break;
						}
					}
				}

				if (c instanceof PortConnection) {
					PortConnection pc = sysImpl.createOwnedPortConnection();
					pc.setName(getUniqueName(PORT_CONNECTION_IMPL_NAME, false, sysImpl.getOwnedPortConnections()));
					pc.setBidirectional(c.isBidirectional());
					ConnectedElement src = pc.createSource();
					ConnectedElement dst = pc.createDestination();
					if (c.getSource().getContext() == null) {
						src.setContext(null);
						src.setConnectionEnd(sysFeature);
						dst.setContext(sub);
						dst.setConnectionEnd(subsysFeature);
					} else {
						src.setContext(sub);
						src.setConnectionEnd(subsysFeature);
						dst.setContext(null);
						dst.setConnectionEnd(sysFeature);
					}
				} else if (c instanceof AccessConnection) {
					AccessConnection ac = sysImpl.createOwnedAccessConnection();
					ac.setName(getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false, sysImpl.getOwnedAccessConnections()));
					ConnectedElement src = ac.createSource();
					src.setContext(null);
					src.setConnectionEnd(sysFeature);
					ConnectedElement dst = ac.createDestination();
					dst.setContext(sub);
					dst.setConnectionEnd(subsysFeature);
				}
			}

			// Give it the same property associations
			for (PropertyAssociation pa : thrGrpImpl.getOwnedPropertyAssociations()) {
				PropertyAssociation prop = sysImpl.createOwnedPropertyAssociation();
				prop = EcoreUtil.copy(pa);
//				prop.setProperty(pa.getProperty());
//				for (ModalPropertyValue mpv : pa.getOwnedValues()) {
//					ModalPropertyValue val = prop.createOwnedValue();
//					val.setOwnedValue(mpv.getOwnedValue());
//				}
			}

			return EcoreUtil.getURI(sysImpl);
		});

		ModifyUtils.closeEditor(editor, true);

		return sysUri;

	}

	/**
	 * Transforms a Thread into a Process containing a Thread
	 * @param thread - Thread subcomponent to be transformed
	 * @return URI of the resulting Process
	 */
	public URI transformThread(ThreadSubcomponent thread) {

		final ThreadImplementation thrImpl = (ThreadImplementation) thread.getComponentImplementation();

		// Make sure this thread hasn't already been transformed
		PackageSection ps = (PackageSection) thrImpl.eContainer();
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c.getName().equalsIgnoreCase(thread.getComponentType().getName() + "_Proc")) {
				return EcoreUtil.getURI(c);
			}
		}

		XtextEditor editor = ModifyUtils.getEditor(Filesystem.getFile(thrImpl.eResource().getURI()));
		if (editor == null) {
			throw new RuntimeException("Could not transform thread " + thread.getQualifiedName() + " for seL4.");
		}

		URI procUri = editor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			ThreadImplementation threadImpl = (ThreadImplementation) resource
					.getEObject(EcoreUtil.getURI(thrImpl).fragment());
			PackageSection pkgSection = (PackageSection) threadImpl.eContainer();

			// Create corresponding process type
			ProcessType procType = (ProcessType) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getProcessType());
			// Give it a name
			procType.setName(threadImpl.getTypeName() + "_Proc");
			// Give it the same features
			Map<Feature, Feature> features = new HashMap<>();
			for (Feature f : threadImpl.getType().getOwnedFeatures()) {
				if (f instanceof DataPort) {
					DataPort p = procType.createOwnedDataPort();
					p = EcoreUtil.copy((DataPort) f);
//					p.setDataFeatureClassifier(((DataPort) f).getDataFeatureClassifier());
//					p.setName(f.getName());
//					p.setDirection(((DataPort) f).getDirection());
//					p.setRefined(f.getRefined());
//					for (PropertyAssociation pa : f.getOwnedPropertyAssociations()) {
//						PropertyAssociation prop = p.createOwnedPropertyAssociation();
//						prop = EcoreUtil.copy(pa);
//					}
					features.put(p, f);
				} else if (f instanceof EventDataPort) {
					EventDataPort p = procType.createOwnedEventDataPort();
					p = EcoreUtil.copy((EventDataPort) f);
//					p.setDataFeatureClassifier(((EventDataPort) f).getDataFeatureClassifier());
//					p.setName(f.getName());
//					p.setDirection(((EventDataPort) f).getDirection());
//					p.setRefined(f.getRefined());
//					for (PropertyAssociation pa : f.getOwnedPropertyAssociations()) {
//						PropertyAssociation prop = p.createOwnedPropertyAssociation();
//						prop = EcoreUtil.copy(pa);
//					}
					features.put(p, f);
				} else if (f instanceof EventPort) {
					EventPort p = procType.createOwnedEventPort();
					p = EcoreUtil.copy((EventPort) f);
//					p.setName(f.getName());
//					p.setDirection(((EventPort) f).getDirection());
//					p.setRefined(f.getRefined());
//					for (PropertyAssociation pa : f.getOwnedPropertyAssociations()) {
//						PropertyAssociation prop = p.createOwnedPropertyAssociation();
//						prop = EcoreUtil.copy(pa);
//					}
					features.put(p, f);
				} else if (f instanceof DataAccess) {
					DataAccess a = procType.createOwnedDataAccess();
					a = EcoreUtil.copy((DataAccess) f);
//					a.setDataFeatureClassifier(((DataAccess) f).getDataFeatureClassifier());
//					a.setKind(((DataAccess) f).getKind());
//					a.setName(f.getName());
//					a.setRefined(f.getRefined());
//					for (PropertyAssociation pa : f.getOwnedPropertyAssociations()) {
//						PropertyAssociation prop = a.createOwnedPropertyAssociation();
//						prop = EcoreUtil.copy(pa);
//					}
					features.put(a, f);
				} else if (f instanceof SubprogramAccess) {
					SubprogramAccess a = procType.createOwnedSubprogramAccess();
					a = EcoreUtil.copy((SubprogramAccess) f);
//					a.setSubprogramFeatureClassifier(((SubprogramAccess) f).getSubprogramFeatureClassifier());
//					a.setKind(((SubprogramAccess) f).getKind());
//					a.setName(f.getName());
//					a.setRefined(f.getRefined());
//					for (PropertyAssociation pa : f.getOwnedPropertyAssociations()) {
//						PropertyAssociation prop = a.createOwnedPropertyAssociation();
//						prop = EcoreUtil.copy(pa);
//					}
					features.put(a, f);
				}
			}
			// Give it the same property associations
			for (PropertyAssociation pa : threadImpl.getType().getOwnedPropertyAssociations()) {
				PropertyAssociation prop = procType.createOwnedPropertyAssociation();
				prop = EcoreUtil.copy(pa);
//				prop.setProperty(pa.getProperty());
//				for (ModalPropertyValue mpv : pa.getOwnedValues()) {
//					ModalPropertyValue val = prop.createOwnedValue();
//					val.setOwnedValue(mpv.getOwnedValue());
//				}
			}
			// TODO: Add SEL4 property association?

			// Create corresponding process implementation
			ProcessImplementation procImpl = (ProcessImplementation) pkgSection
					.createOwnedClassifier(Aadl2Package.eINSTANCE.getProcessImplementation());
			// Give it a name
			procImpl.setName(procType.getName() + ".Impl");
			// Give it a realization
			final Realization r = procImpl.createOwnedRealization();
			r.setImplemented(procType);
			// Add thread subcomponent
			ThreadSubcomponent tSub = procImpl.createOwnedThreadSubcomponent();
			tSub.setName(threadImpl.getTypeName());
			tSub.setThreadSubcomponentType(threadImpl.getType());

			// Add connections
			Iterator<Map.Entry<Feature, Feature>> iterator = features.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Feature, Feature> f = iterator.next();
				Feature procFeature = f.getKey();
				Feature thrFeature = f.getValue();
				if (procFeature instanceof Port) {
					PortConnection pc = procImpl.createOwnedPortConnection();
					pc.setName(getUniqueName(PORT_CONNECTION_IMPL_NAME, false, procImpl.getOwnedPortConnections()));
					pc.setBidirectional(((Port) procFeature).getDirection() == DirectionType.IN_OUT);
					ConnectedElement src = pc.createSource();
					ConnectedElement dst = pc.createDestination();
					if (((Port) thrFeature).isIn()) {
						src.setContext(null);
						src.setConnectionEnd(procFeature);
						dst.setContext(tSub);
						dst.setConnectionEnd(thrFeature);
					} else {
						src.setContext(tSub);
						src.setConnectionEnd(thrFeature);
						dst.setContext(null);
						dst.setConnectionEnd(procFeature);
					}
				} else if (procFeature instanceof Access) {
					AccessConnection ac = procImpl.createOwnedAccessConnection();
					ac.setName(getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false, procImpl.getOwnedAccessConnections()));
					ConnectedElement src = ac.createSource();
					ConnectedElement dst = ac.createDestination();
					src.setContext(null);
					src.setConnectionEnd(procFeature);
					dst.setContext(tSub);
					dst.setConnectionEnd(thrFeature);
				}
			}

			// Give it the same property associations
			for (PropertyAssociation pa : threadImpl.getOwnedPropertyAssociations()) {
				PropertyAssociation prop = procImpl.createOwnedPropertyAssociation();
				prop = EcoreUtil.copy(pa);
//				prop.setProperty(pa.getProperty());
//				for (ModalPropertyValue mpv : pa.getOwnedValues()) {
//					ModalPropertyValue val = prop.createOwnedValue();
//					val.setOwnedValue(mpv.getOwnedValue());
//				}
			}

			return EcoreUtil.getURI(procImpl);
		});

		ModifyUtils.closeEditor(editor, true);

		return procUri;

	}

	private void getProcessDescendants(ComponentImplementation compImpl, List<ProcessSubcomponent> procSubs) {

		for (Subcomponent sub : compImpl.getOwnedSubcomponents()) {
			if (sub instanceof ProcessSubcomponent) {
				procSubs.add((ProcessSubcomponent) sub);
				continue;
			}
			getProcessDescendants(sub.getComponentImplementation(), procSubs);
		}
	}

}
