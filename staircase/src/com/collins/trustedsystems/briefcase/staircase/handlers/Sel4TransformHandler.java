package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.Access;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentCategory;
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
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
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
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubprogramAccess;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.SystemSubcomponent;
import org.osate.aadl2.SystemType;
import org.osate.aadl2.ThreadGroupImplementation;
import org.osate.aadl2.ThreadGroupSubcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ThreadProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.Sel4TransformDialog;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class Sel4TransformHandler extends AadlHandler {

	static final String PORT_CONNECTION_IMPL_NAME = "c";
	static final String ACCESS_CONNECTION_IMPL_NAME = "a";
	static final String SEL4 = "seL4";

	private String sel4Subcomponent;
	private String sel4Requirement;

	@Override
	protected void runCommand(URI uri) {

		// Selection must be a system or process subcomponent
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof SystemSubcomponent || eObj instanceof ProcessSubcomponent)) {
			Dialog.showError("seL4 Transform",
					"A system or process subcomponent must be selected to perform the seL4 transform.");
			return;
		}
		final Subcomponent selectedSubcomponent = (Subcomponent) eObj;

		// Selection must be a software component -- can only be (and contain) system, process, thread group, thread components
		if (!isSoftwareSubcomponent(selectedSubcomponent)) {
			Dialog.showError("seL4 Transform",
					"Selected subcomponent must be a software component (only consist of system, process, thread group, and thread component implementations) to perform the seL4 transform.");
			return;
		}

		final SystemImplementation si = (SystemImplementation) selectedSubcomponent.getContainingComponentImpl();

		// Open wizard to enter seL4 transform info
		final Sel4TransformDialog wizard = new Sel4TransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		if (selectedSubcomponent.getComponentType().getCategory() != ComponentCategory.SYSTEM
				|| !isSoftwareSubcomponent(selectedSubcomponent)
				|| selectedSubcomponent.getComponentImplementation().getOwnedSubcomponents().size() <= 1) {
			sel4Subcomponent = selectedSubcomponent.getName();
		} else {

			wizard.create(selectedSubcomponent);
			if (wizard.open() == Window.OK) {
				sel4Subcomponent = wizard.getSelectedSubcomponent();
				sel4Requirement = wizard.getRequirement();
			} else {
				return;
			}
		}

//		SystemInstance systemInstance = null;
//		try {
//			systemInstance = InstantiateModel.buildInstanceModelFile(si);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (systemInstance == null) {
//			Dialog.showError("seL4 Transform", "Unable to instantiate model.");
//			return;
//		}

//		final Subcomponent sub = getSubcomponentFromPath(si, sel4Subcomponent);
		Subcomponent sub = null;
		final List<Subcomponent> subs = getSubcomponentsFromPath(si, sel4Subcomponent);
//		if (sub == null) {
		if (subs.isEmpty()) {
			Dialog.showError("seL4 Transform", "Selected subcomponent " + sel4Subcomponent + " cannot be found.");
			return;
		} else {
			sub = subs.get(subs.size() - 1);
		}

		// Check that selected subcomponent is bound to a processor
		// (could be specified on subcomponent or containing component impl)
//		final ContainmentPathElement processor = getProcessor(sub);
		ContainmentPathElement proc = null;
		final ListIterator<Subcomponent> subIterator = subs.listIterator(subs.size());
		while (subIterator.hasPrevious()) {
			proc = getProcessor(subIterator.previous());
			if (proc != null) {
				break;
			}
		}
		final ContainmentPathElement processor = proc;
		if (processor == null) {
			Dialog.showError("seL4 Transform", "Selected subcomponent " + sel4Subcomponent
					+ " must already be bound to a processor in order to perform the seL4 transform.");
			return;
		}

		// Set OS property on containing component impl for processor
		final Resource aadlResource = si.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {
			@Override
			protected void doExecute() {
				setOsProperty(si, processor);
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);
			// We're done: Save the model
			aadlResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RollbackException e) {
			e.printStackTrace();
		}

		// Each process subcomponent containing a thread group and/or multiple thread subcomponents
		// becomes a system subcomponent
		// Each thread becomes a process containing the thread
		// Each thread group becomes a system
		// Re-wire everything appropriately
		if (sub instanceof ProcessSubcomponent) {
			final Map<String, SystemImplementation> transformedSubs = new HashMap<>();
			final SystemImplementation systemImpl = transformProcess((ProcessSubcomponent) sub);
			if (systemImpl != null) {
				transformedSubs.put(sub.getName(), systemImpl);
			}
			updateSystemImplementation((SystemImplementation) sub.getContainingComponentImpl(), transformedSubs);
		} else {
			transformSystem((SystemSubcomponent) sub);
		}

//		// Add sel4_transform claims to resolute prove statement, if applicable
//		if (!sel4Requirement.isEmpty()) {
//			final CyberRequirement req = RequirementsManager.getInstance().getRequirement(sel4Requirement);
//			Sel4TransformClaim claim = new Sel4TransformClaim(req.getContext(),
//					si, sel4Subcomponent);
//			RequirementsManager.getInstance().modifyRequirement(sel4Requirement, claim);
//		}

		BriefcaseNotifier.notify("BriefCASE - seL4 Transform", "Model transformed for seL4 build.");

		// Format and save
//		format(true);

//		saveChanges(false);

	}

	/**
	 * Transforms a System into a System containing transformed subcomponents
	 * @param system - Subcomponent instantiation of a system to be transformed
	 */
	public void transformSystem(SystemSubcomponent system) {

		final SystemImplementation systemImpl = (SystemImplementation) system.getComponentImplementation();

		// Transform contained systems
		// This will ensure that any process or thread group descendants will get transformed
		for (SystemSubcomponent ss : systemImpl.getOwnedSystemSubcomponents()) {
			transformSystem(ss);
		}

		// Transform contained processes
		final Map<String, SystemImplementation> transformedSubs = new HashMap<>();
		for (ProcessSubcomponent ps : systemImpl.getOwnedProcessSubcomponents()) {
			final SystemImplementation sysImpl = transformProcess(ps);
			if (sysImpl != null) {
				transformedSubs.put(ps.getName(), sysImpl);
			}
		}

		updateSystemImplementation(systemImpl, transformedSubs);

		return;
	}

	private void updateSystemImplementation(SystemImplementation systemImpl,
			Map<String, SystemImplementation> transformedSubs) {

		final Resource aadlResource = systemImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				// Unlike other component types, we will just replace the original process subcomponent
				// with the transformed system component
				// We won't create a new system implementation
				final Iterator<ProcessSubcomponent> subIterator = systemImpl.getOwnedProcessSubcomponents().iterator();
				while (subIterator.hasNext()) {
					final ProcessSubcomponent pSub = subIterator.next();
					final SystemImplementation transformedImpl = transformedSubs.get(pSub.getName());
					if (transformedImpl != null) {
						// Create transformed subcomponent
						final SystemSubcomponent sSub = systemImpl.createOwnedSystemSubcomponent();
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
						final Iterator<Connection> connIterator = systemImpl.getOwnedConnections().iterator();
						while (connIterator.hasNext()) {
							final Connection conn = connIterator.next();
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
	public SystemImplementation transformProcess(ProcessSubcomponent process) {

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
		for (ThreadSubcomponent ts : processImpl.getOwnedThreadSubcomponents()) {
			final ProcessImplementation procImpl = transformThread(ts);
			if (procImpl != null) {
				transformedSubs.put(ts, procImpl);
			}
		}

		// Transform thread group subcomponents
		for (ThreadGroupSubcomponent tgs : processImpl.getOwnedThreadGroupSubcomponents()) {
			final SystemImplementation sysImpl = transformThreadGroup(tgs);
			if (sysImpl != null) {
				transformedSubs.put(tgs, sysImpl);
			}
		}

		final Resource aadlResource = processImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

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

		@SuppressWarnings("unchecked")
		final List<ComponentImplementation> transformImplList = (List<ComponentImplementation>) cmd.getResult();
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
	public SystemImplementation transformThreadGroup(ThreadGroupSubcomponent threadGroup) {

		final ThreadGroupImplementation tgImpl = (ThreadGroupImplementation) threadGroup.getComponentImplementation();

		// Make sure this thread group hasn't already been transformed
		final PackageSection ps = AadlUtil.getContainingPackageSection(tgImpl);
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c instanceof SystemImplementation
					&& c.getName().equalsIgnoreCase(tgImpl.getTypeName() + "_" + SEL4 + ".Impl")) {
				return (SystemImplementation) c;
			}
		}

		// Transform thread subcomponents
		final Map<Subcomponent, ComponentImplementation> transformedSubs = new HashMap<>();
		for (ThreadSubcomponent ts : tgImpl.getOwnedThreadSubcomponents()) {
			final ComponentImplementation procImpl = transformThread(ts);
			if (procImpl != null) {
				transformedSubs.put(ts, procImpl);
			}
		}

		// Transform thread group subcomponents
		for (ThreadGroupSubcomponent tgs : tgImpl.getOwnedThreadGroupSubcomponents()) {
			final ComponentImplementation sysImpl = transformThreadGroup(tgs);
			if (sysImpl != null) {
				transformedSubs.put(tgs, sysImpl);
			}
		}

		final Resource aadlResource = tgImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

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

		@SuppressWarnings("unchecked")
		final List<ComponentImplementation> transformImplList = (List<ComponentImplementation>) cmd.getResult();
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
		final PackageSection ps = AadlUtil.getContainingPackageSection(threadImpl);
		for (Classifier c : ps.getOwnedClassifiers()) {
			// Instead check for SEL4 property?
			if (c instanceof ProcessImplementation
					&& c.getName().equalsIgnoreCase(threadImpl.getTypeName() + "_" + SEL4 + ".Impl")) {
				return (ProcessImplementation) c;
			}
		}

		final Resource aadlResource = threadImpl.eResource();
		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

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

		final List<ComponentImplementation> transformImplList = (List<ComponentImplementation>) cmd.getResult();
		if (transformImplList.get(0) instanceof ProcessImplementation) {
			final ProcessImplementation processImpl = (ProcessImplementation) transformImplList.get(0);
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
	public static ComponentImplementation transform(ComponentImplementation compImpl,
			Map<Subcomponent, ComponentImplementation> transformedSubs) {

		final PackageSection pkgSection = AadlUtil.getContainingPackageSection(compImpl);

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
		final Map<Feature, Feature> features = new HashMap<>();
		for (Feature compFeature : compImpl.getType().getOwnedFeatures()) {
			if (compFeature instanceof DataPort) {
				final DataPort transformFeature = EcoreUtil.copy((DataPort) compFeature);
				ComponentCreateHelper.createOwnedDataPort(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof EventDataPort) {
				final EventDataPort transformFeature = EcoreUtil.copy((EventDataPort) compFeature);
				ComponentCreateHelper.createOwnedEventDataPort(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof EventPort) {
				final EventPort transformFeature = EcoreUtil.copy((EventPort) compFeature);
				ComponentCreateHelper.createOwnedEventPort(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof DataAccess) {
				final DataAccess transformFeature = EcoreUtil.copy((DataAccess) compFeature);
				ComponentCreateHelper.createOwnedDataAccess(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			} else if (compFeature instanceof SubprogramAccess) {
				final SubprogramAccess transformFeature = EcoreUtil.copy((SubprogramAccess) compFeature);
				ComponentCreateHelper.createOwnedSubprogramAccess(transformType, transformFeature);
				features.put(compFeature, transformFeature);
			}
		}
		// Give it the same property associations
		copyPropertyAssociations(compImpl.getType(), transformType);
		// TODO: Add SEL4 property association?

//		// Give it the same AGREE clauses (for systems, processes, thread groups) or lift AGREE clauses (for threads)
//		// if an AGREE annex is present.
		boolean liftContract = false;
		for (AnnexSubclause annexSubclause : compImpl.getType().getOwnedAnnexSubclauses()) {
			if (annexSubclause.getName().equalsIgnoreCase("agree")) {
				if (transformType instanceof ProcessType) {
					// Add lift contract to process implementation
					liftContract = true;
				} else {
					// Copy AGREE annex
					// Need to give unique IDs

					final DefaultAnnexSubclause defaultAnnexSubclause = transformType.createOwnedAnnexSubclause();
					defaultAnnexSubclause.setName("agree");
					final List<String> specStatements = new ArrayList<>();

//					AgreeContractSubclause agreeContractSubclause = (AgreeContractSubclause) ((DefaultAnnexSubclause) annexSubclause)
//							.getParsedAnnexSubclause();
//					AgreeAnnexUnparser unparser = new AgreeAnnexUnparser();
//					String specs = unparser.unparseContract((AgreeContract) agreeContractSubclause.getContract(), "");
					final String specs = ((DefaultAnnexSubclause) annexSubclause).getSourceText().replace("{**", "")
							.replace("**}", "");
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
							final String desc = newSpec.substring(newSpec.indexOf("\""), newSpec.lastIndexOf("\"") + 1)
									.trim();
							final String id = newSpec
									.substring(newSpec.toLowerCase().indexOf(specType) + specType.length(),
									newSpec.indexOf("\"")).trim();

							newSpec = specType;
							// If spec has an ID, append a suffix to maintain ID uniqueness
							if (!id.isEmpty()) {
								newSpec += id + "_" + SEL4 + " ";
							}
							newSpec += desc + " : " + expr + ";";

							specStatements.add(newSpec);
						} else if (!spec.trim().isEmpty()) {
							specStatements.add(spec.trim() + ";");
						}
					}

					String agreeClauses = "{**" + System.lineSeparator();
					for (String s : specStatements) {
						agreeClauses += s + System.lineSeparator();
					}
					agreeClauses += "**}";
					defaultAnnexSubclause.setSourceText(agreeClauses);

//					DefaultAnnexSubclause defaultAnnexSubclause = (DefaultAnnexSubclause) EcoreUtil
//							.copy(annexSubclause);
//					transformType.getOwnedAnnexSubclauses().add(defaultAnnexSubclause);

				}
			}
		}

//		// Put it in the correct place (after component implementation of original component)
//		try {
//			pkgSection.getOwnedClassifiers().move(getIndex(compImpl.getName(), pkgSection.getOwnedClassifiers()) + 1,
//					pkgSection.getOwnedClassifiers().size() - 1);
//		} catch (Exception e) {
//
//		}

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

			final Subcomponent sub = ComponentCreateHelper.createOwnedSubcomponent(transformImpl,
					compImpl.getCategory());
			sub.setName(compImpl.getTypeName());
			ComponentCreateHelper.setSubcomponentType(sub, compImpl);
			// Thread subcomponent property associations get set by caller

			// Add connections
			final Iterator<Map.Entry<Feature, Feature>> iterator = features.entrySet().iterator();
			while (iterator.hasNext()) {
				final Map.Entry<Feature, Feature> f = iterator.next();
				final Feature compFeature = f.getKey();
				final Feature transformFeature = f.getValue();
				if (transformFeature instanceof Port) {
					final PortConnection pc = transformImpl.createOwnedPortConnection();
					pc.setName(
							ModelTransformUtils.getUniqueName(PORT_CONNECTION_IMPL_NAME, false,
									transformImpl.getOwnedPortConnections()));
					pc.setBidirectional(((Port) transformFeature).getDirection() == DirectionType.IN_OUT);
					final ConnectedElement src = pc.createSource();
					final ConnectedElement dst = pc.createDestination();
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
					final AccessConnection ac = transformImpl.createOwnedAccessConnection();
					ac.setName(ModelTransformUtils.getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false,
							transformImpl.getOwnedAccessConnections()));
					final ConnectedElement src = ac.createSource();
					final ConnectedElement dst = ac.createDestination();
					src.setContext(null);
					src.setConnectionEnd(transformFeature);
					dst.setContext(sub);
					dst.setConnectionEnd(compFeature);
				}

			}

		} else {

			// For transformed components other than threads

			final Iterator<Map.Entry<Subcomponent, ComponentImplementation>> subIterator = transformedSubs.entrySet()
					.iterator();
			while (subIterator.hasNext()) {
				final Map.Entry<Subcomponent, ComponentImplementation> mapElement = subIterator.next();
				final Subcomponent sub = ComponentCreateHelper.createOwnedSubcomponent(transformImpl,
						mapElement.getValue().getCategory());
				// Give it the same name as the original subcomponent
				sub.setName(mapElement.getKey().getName());
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
					final Context cSrcContext = c.getSource().getContext();
					final Context cDstContext = c.getDestination().getContext();
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
					final PortConnection pc = transformImpl.createOwnedPortConnection();
//					pc.setName(
//							ModelTransformUtils.getUniqueName(PORT_CONNECTION_IMPL_NAME, false,
//									transformImpl.getOwnedPortConnections()));
					pc.setName(c.getName());
					pc.setBidirectional(c.isBidirectional());
					final ConnectedElement src = pc.createSource();
					final ConnectedElement dst = pc.createDestination();
					src.setContext(srcContext);
					src.setConnectionEnd(srcConnEnd);
					dst.setContext(dstContext);
					dst.setConnectionEnd(dstConnEnd);
					// Give the connection the same property associations
					copyPropertyAssociations(c, pc);
				} else if (c instanceof AccessConnection) {
					final AccessConnection ac = transformImpl.createOwnedAccessConnection();
//					ac.setName(ModelTransformUtils.getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false,
//							transformImpl.getOwnedAccessConnections()));
					ac.setName(c.getName());
					final ConnectedElement src = ac.createSource();
					final ConnectedElement dst = ac.createDestination();
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

		// Give it the same AGREE clauses (for systems, processes, threadgroups) or lift AGREE clauses (for threads)
		// if an AGREE annex is present.
		if (liftContract) {
			// Add lift contract
			final String agreeClause = "{**" + System.lineSeparator() + "lift contract;" + System.lineSeparator()
					+ "**}";
			final DefaultAnnexSubclause defaultAnnexSubclause = ComponentCreateHelper
					.createOwnedAnnexSubclause(transformImpl);
			defaultAnnexSubclause.setName("agree");
			defaultAnnexSubclause.setSourceText(agreeClause);
		}

//		// Put it in the correct place (after transformed component type)
//		try {
//			pkgSection.getOwnedClassifiers().move(
//					getIndex(transformType.getName(), pkgSection.getOwnedClassifiers()) + 1,
//					pkgSection.getOwnedClassifiers().size() - 1);
//		} catch (Exception e) {
//
//		}

		return transformImpl;

	}

	/**
	 * Inserts a single thread into a designated seL4 process
	 */
	public static ThreadSubcomponent insertThreadInSel4Process(ProcessImplementation processImpl,
			String dispatchProtocol, String agreeClauses) {

		final PackageSection pkgSection = (PackageSection) processImpl.eContainer();

		// Create thread type
		final ThreadType threadType = (ThreadType) pkgSection
				.createOwnedClassifier(ComponentCreateHelper.getTypeClass(ComponentCategory.THREAD));
		threadType.setName(processImpl.getTypeName().replace("_seL4", ""));

		// Give it the same features
		final Map<Feature, Feature> features = new HashMap<>();
		for (Feature processFeature : processImpl.getType().getOwnedFeatures()) {
			if (processFeature instanceof DataPort) {
				final DataPort threadFeature = EcoreUtil.copy((DataPort) processFeature);
				ComponentCreateHelper.createOwnedDataPort(threadType, threadFeature);
				features.put(processFeature, threadFeature);
			} else if (processFeature instanceof EventDataPort) {
				final EventDataPort threadFeature = EcoreUtil.copy((EventDataPort) processFeature);
				ComponentCreateHelper.createOwnedEventDataPort(threadType, threadFeature);
				features.put(processFeature, threadFeature);
			} else if (processFeature instanceof EventPort) {
				final EventPort threadFeature = EcoreUtil.copy((EventPort) processFeature);
				ComponentCreateHelper.createOwnedEventPort(threadType, threadFeature);
				features.put(processFeature, threadFeature);
			} else if (processFeature instanceof DataAccess) {
				final DataAccess threadFeature = EcoreUtil.copy((DataAccess) processFeature);
				ComponentCreateHelper.createOwnedDataAccess(threadType, threadFeature);
				features.put(processFeature, threadFeature);
			} else if (processFeature instanceof SubprogramAccess) {
				final SubprogramAccess threadFeature = EcoreUtil.copy((SubprogramAccess) processFeature);
				ComponentCreateHelper.createOwnedSubprogramAccess(threadType, threadFeature);
				features.put(processFeature, threadFeature);
			}
		}
		// Give it the same property associations
		copyPropertyAssociations(processImpl.getType(), threadType);

		// Put thread component above process components
		try {
			pkgSection.getOwnedClassifiers().move(getIndex(processImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
				pkgSection.getOwnedClassifiers().size() - 1);
		} catch (Exception e) {

		}

		// Create corresponding implementation
		ThreadImplementation threadImpl = (ThreadImplementation) pkgSection
				.createOwnedClassifier(Aadl2Package.eINSTANCE.getThreadImplementation());

		// Give it a name
		threadImpl.setName(threadType.getName() + ".Impl");
		// Give it a realization
		final Realization threadRealization = threadImpl.createOwnedRealization();
		threadRealization.setImplemented(threadType);

		// Add thread-specific property associations

		// Dispatch protocol
		if (dispatchProtocol != null && !dispatchProtocol.isEmpty()) {
			final Property dispatchProtocolProp = GetProperties.lookupPropertyDefinition(threadImpl,
					ThreadProperties._NAME, ThreadProperties.DISPATCH_PROTOCOL);
			final EnumerationLiteral dispatchProtocolLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
			dispatchProtocolLit.setName(dispatchProtocol);
			final NamedValue nv = Aadl2Factory.eINSTANCE.createNamedValue();
			nv.setNamedValue(dispatchProtocolLit);
			threadImpl.setPropertyValue(dispatchProtocolProp, nv);
		}

		// Put thread component above process components
		try {
			pkgSection.getOwnedClassifiers().move(getIndex(processImpl.getTypeName(), pkgSection.getOwnedClassifiers()),
				pkgSection.getOwnedClassifiers().size() - 1);
		} catch (Exception e) {

		}

		// Add subcomponent
		final ThreadSubcomponent sub = processImpl.createOwnedThreadSubcomponent();
		sub.setName(processImpl.getTypeName().replace("_seL4", ""));
		sub.setThreadSubcomponentType(threadImpl);

		// Add connections
		final Iterator<Map.Entry<Feature, Feature>> iterator = features.entrySet().iterator();
		while (iterator.hasNext()) {
			final Map.Entry<Feature, Feature> f = iterator.next();
			final Feature processFeature = f.getKey();
			final Feature threadFeature = f.getValue();
			if (threadFeature instanceof Port) {
				final PortConnection pc = processImpl.createOwnedPortConnection();
				pc.setName(ModelTransformUtils.getUniqueName(PORT_CONNECTION_IMPL_NAME, false,
						processImpl.getOwnedPortConnections()));
				pc.setBidirectional(((Port) threadFeature).getDirection() == DirectionType.IN_OUT);
				final ConnectedElement src = pc.createSource();
				final ConnectedElement dst = pc.createDestination();
				if (((Port) processFeature).isIn()) {
					src.setContext(null);
					src.setConnectionEnd(threadFeature);
					dst.setContext(sub);
					dst.setConnectionEnd(processFeature);
				} else {
					src.setContext(sub);
					src.setConnectionEnd(processFeature);
					dst.setContext(null);
					dst.setConnectionEnd(threadFeature);
				}
			} else if (threadFeature instanceof Access) {
				final AccessConnection ac = processImpl.createOwnedAccessConnection();
				ac.setName(ModelTransformUtils.getUniqueName(ACCESS_CONNECTION_IMPL_NAME, false,
						processImpl.getOwnedAccessConnections()));
				final ConnectedElement src = ac.createSource();
				final ConnectedElement dst = ac.createDestination();
				src.setContext(null);
				src.setConnectionEnd(threadFeature);
				dst.setContext(sub);
				dst.setConnectionEnd(processFeature);
			}
		}

		// Give it the same property associations
		copyPropertyAssociations(processImpl, threadImpl);

		// Add AGREE clauses
		if (agreeClauses != null && !agreeClauses.isEmpty()) {
			final DefaultAnnexSubclause defaultAnnexSubclause = ComponentCreateHelper
					.createOwnedAnnexSubclause(threadType);
			defaultAnnexSubclause.setName("agree");
			defaultAnnexSubclause.setSourceText(agreeClauses);
		}

		return sub;
	}

	/**
	 * Copies property associations from one AADL named element to another.
	 * Only valid property associations accepted by the 'to' element will be copied
	 * @param from
	 * @param to
	 */
	public static void copyPropertyAssociations(NamedElement from, NamedElement to) {
		if (to == null || from == null) {
			return;
		}
		for (PropertyAssociation pa : from.getOwnedPropertyAssociations()) {
			final PropertyAssociation propAssoc = EcoreUtil.copy(pa);
			final Property prop = propAssoc.getProperty();
			if (to.acceptsProperty(prop)) {
				to.getOwnedPropertyAssociations().add(propAssoc);
			} else {
				// TODO: log exception
			}
		}
	}

	private ContainmentPathElement getProcessor(Subcomponent selectedSubcomponent) {

		// Check containing component implementation properties
		for (PropertyAssociation pa : selectedSubcomponent.getContainingComponentImpl()
				.getOwnedPropertyAssociations()) {
			if (pa.getProperty().getName().equalsIgnoreCase("Actual_Processor_Binding")) {
				for (ContainedNamedElement cne : pa.getAppliesTos()) {
					final ContainmentPathElement cpe = cne.getPath();
					final Subcomponent boundSub = (Subcomponent) cpe.getNamedElement();
					if (!boundSub.equals(selectedSubcomponent)) {
						continue;
					}

					// Get the processor this subcomponent is bound to
					for (ModalPropertyValue val : pa.getOwnedValues()) {
						final ListValue listVal = (ListValue) val.getOwnedValue();
						for (PropertyExpression propExpr : listVal.getOwnedListElements()) {
							if (propExpr instanceof ReferenceValue) {
								final ReferenceValue refVal = (ReferenceValue) propExpr;
								return refVal.getPath();
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void setOsProperty(SystemImplementation systemImpl, ContainmentPathElement cpe) {

		boolean osSet = false;
		final Property prop = Aadl2GlobalScopeUtil.get(systemImpl, Aadl2Package.eINSTANCE.getProperty(),
				CasePropertyUtils.CASE_PROPSET_NAME + "::" + CasePropertyUtils.OS);

		// Check if processor is a subcomponent of systemImpl, and remove the OS property if it has it
		if (cpe.getPath() == null && cpe.getNamedElement() instanceof ProcessorSubcomponent) {
			final ProcessorSubcomponent proc = (ProcessorSubcomponent) cpe.getNamedElement();
			final Iterator<PropertyAssociation> paIterator = proc.getOwnedPropertyAssociations().iterator();
			while (paIterator.hasNext()) {
				final PropertyAssociation pa = paIterator.next();
				if (pa.getProperty().equals(prop)) {
					final ModalPropertyValue val = pa.getOwnedValues().get(0);
					final NamedValue namedVal = (NamedValue) val.getOwnedValue();
					final EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
					if (enumLiteral.getName().equalsIgnoreCase("seL4")) {
						osSet = true;
					} else {
						paIterator.remove();
					}
					break;
				}
			}
		}

		// Check containing component implementation properties
		final Iterator<PropertyAssociation> paIterator = systemImpl.getOwnedPropertyAssociations().iterator();
		while (paIterator.hasNext()) {
			final PropertyAssociation pa = paIterator.next();
			if (pa.getProperty().equals(prop)) {

				final ModalPropertyValue val = pa.getOwnedValues().get(0);
				final NamedValue namedVal = (NamedValue) val.getOwnedValue();
				final EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
				final boolean isSel4 = enumLiteral.getName().equalsIgnoreCase("seL4");

				// Check if it applies to the processor
				final Iterator<ContainedNamedElement> atIterator = pa.getAppliesTos().iterator();
				while (atIterator.hasNext()) {
					final ContainedNamedElement cne = atIterator.next();
					if (equalPaths(cne.getPath(), cpe)) {
						// If it's seL4, add the processor if it's not already there
						// If it's not seL4, remove the processor if it's there
						if (isSel4) {
							osSet = true;
						} else {
							atIterator.remove();
						}
						break;
					}
				}
				if (isSel4 && !osSet) {
					final ContainedNamedElement cne = pa.createAppliesTo();
					cne.setPath(copyPath(cpe));
					osSet = true;
				}
				if (pa.getAppliesTos().isEmpty()) {
					paIterator.remove();
				}
			}
		}
		// If the OS of the processor hasn't been set, add the property association
		if (!osSet) {

			final PropertyAssociation pa = Aadl2Factory.eINSTANCE.createPropertyAssociation();
			final ContainedNamedElement cne = pa.createAppliesTo();
			cne.setPath(copyPath(cpe));
			pa.setProperty(prop);
			final ModalPropertyValue mpv = pa.createOwnedValue();
			final EnumerationLiteral enumLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
			enumLit.setName("seL4");
			final NamedValue namedVal = (NamedValue) mpv.createOwnedValue(Aadl2Package.eINSTANCE.getNamedValue());
			namedVal.setNamedValue(enumLit);
			systemImpl.getOwnedPropertyAssociations().add(pa);

			// Import CASE_Properties file
			try {
				CasePropertyUtils.addCasePropertyImport(AadlUtil.getContainingPackageSection(systemImpl));
			} catch (Exception e) {
//				return null;
			}
		}

	}

	private boolean equalPaths(ContainmentPathElement cpe1, ContainmentPathElement cpe2) {

		while (cpe1 != null && cpe2 != null) {
			if (cpe1.getNamedElement().equals(cpe2.getNamedElement())) {
				cpe1 = cpe1.getPath();
				cpe2 = cpe2.getPath();
			} else {
				return false;
			}
		}
		if (cpe1 != null || cpe2 != null) {
			return false;
		}
		return true;
	}

	private ContainmentPathElement copyPath(ContainmentPathElement cpe) {
		final ContainedNamedElement cne = Aadl2Factory.eINSTANCE.createContainedNamedElement();
		ContainmentPathElement copy = cne.createPath();
		while (cpe != null) {
			copy.setNamedElement(cpe.getNamedElement());
			cpe = cpe.getPath();
			if (cpe != null) {
				copy = copy.createPath();
			}
		}
		return cne.getPath();
	}

	public static boolean isSoftwareSubcomponent(Subcomponent sub) {
		if (sub instanceof SystemSubcomponent) {
			final SystemImplementation systemImpl = (SystemImplementation) sub.getComponentImplementation();
			if (systemImpl != null) {
				for (Subcomponent systemSub : systemImpl.getOwnedSubcomponents()) {
					if (!isSoftwareSubcomponent(systemSub)) {
						return false;
					}
				}
				return true;
			}
		} else if (sub instanceof ProcessSubcomponent) {
			final ProcessImplementation processImpl = (ProcessImplementation) sub.getComponentImplementation();
			if (processImpl != null) {
				for (Subcomponent processSub : processImpl.getOwnedSubcomponents()) {
					if (!isSoftwareSubcomponent(processSub)) {
						return false;
					}
				}
				return true;
			}
		} else if (sub instanceof ThreadGroupSubcomponent) {
			final ThreadGroupImplementation threadGroupImpl = (ThreadGroupImplementation) sub
					.getComponentImplementation();
			if (threadGroupImpl != null) {
				for (Subcomponent threadGroupSub : threadGroupImpl.getOwnedSubcomponents()) {
					if (!isSoftwareSubcomponent(threadGroupSub)) {
						return false;
					}
				}
				return true;
			}
		} else if (sub instanceof ThreadSubcomponent) {
			final ThreadImplementation threadImpl = (ThreadImplementation) sub.getComponentImplementation();
			if (threadImpl != null) {
				return true;
			}
		}
		return false;
	}


//	private Subcomponent getSubcomponentFromPath(ComponentImplementation rootImpl, String qualifiedName) {
//
//		final String[] subs = qualifiedName.split("\\.");
//		Subcomponent currentSub = null;
//
//		for (Subcomponent s : rootImpl.getOwnedSubcomponents()) {
//			if (s.getName().equalsIgnoreCase(subs[0])) {
//				currentSub = s;
//				break;
//			}
//		}
//		if (currentSub == null) {
//			return null;
//		}
//
//		for (int i = 1; i < subs.length; ++i) {
//
//			boolean subFound = false;
//			for (Subcomponent sub : currentSub.getComponentImplementation().getOwnedSubcomponents()) {
//
//				if (sub.getName().equalsIgnoreCase(subs[i])) {
//					currentSub = sub;
//					subFound = true;
//					break;
//				}
//			}
//			if (!subFound) {
//				return null;
//			}
//
//		}
//
//		return currentSub;
//	}

	private List<Subcomponent> getSubcomponentsFromPath(ComponentImplementation rootImpl, String qualifiedName) {
		final List<Subcomponent> subcomponents = new ArrayList<>();
		final String[] subs = qualifiedName.split("\\.");
		Subcomponent currentSub = null;

		for (Subcomponent s : rootImpl.getOwnedSubcomponents()) {
			if (s.getName().equalsIgnoreCase(subs[0])) {
				currentSub = s;
				subcomponents.add(s);
				break;
			}
		}
		if (currentSub == null) {
			return null;
		}

		for (int i = 1; i < subs.length; ++i) {

			boolean subFound = false;
			for (Subcomponent sub : currentSub.getComponentImplementation().getOwnedSubcomponents()) {

				if (sub.getName().equalsIgnoreCase(subs[i])) {
					currentSub = sub;
					subcomponents.add(sub);
					subFound = true;
					break;
				}
			}
			if (!subFound) {
				return null;
			}

		}

		return subcomponents;
	}

}
