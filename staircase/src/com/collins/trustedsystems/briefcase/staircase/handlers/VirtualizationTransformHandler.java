package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessorSubcomponent;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.Realization;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SystemSubcomponent;
import org.osate.aadl2.VirtualProcessorImplementation;
import org.osate.aadl2.VirtualProcessorSubcomponent;
import org.osate.aadl2.VirtualProcessorType;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.DeploymentProperties;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import com.collins.trustedsystems.briefcase.staircase.dialogs.VirtualizationTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddVirtualizationClaim;
import com.collins.trustedsystems.briefcase.staircase.requirements.RequirementsManager;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils.MITIGATION_TYPE;
import com.collins.trustedsystems.briefcase.staircase.utils.ComponentCreateHelper;
import com.collins.trustedsystems.briefcase.staircase.utils.ModelTransformUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class VirtualizationTransformHandler extends AadlHandler {

	public static final String VIRTUAL_PROCESSOR_COMP_TYPE_NAME = "CASE_VirtualMachine";
	public static final String VIRTUAL_PROCESSOR_SUBCOMP_NAME = "VirtualMachine";
	static final String CONNECTION_IMPL_NAME = "c";

	private String virtualProcessorComponentName;
	private String virtualProcessorSubcomponentName;
	private String virtualMachineOS;
	private List<String> virtualizationComponents;
	private String virtualizationRequirement;

	private Map<String, Set<String>> explicitProcessorBindings = new HashMap<>();
	private Map<String, Set<String>> implicitProcessorBindings = new HashMap<>();
	private Set<String> boundProcessors = new HashSet<>();
	private Map<String, Subcomponent> virtualizationSubNameMap = new HashMap<>();

	@Override
	protected void runCommand(URI uri) {

		// ASSUMPTIONS:
		// Selected subcomponent or it's subcomponents are bound to a processor
		// Selected subcomponent is in same component implementation as processor it is bound to
		// The selected component or subcomponents are bound to the same processors
		// (in other words, processor bindings are the same for all selected components)

		// Get the current selection
		EObject eObj = getEObject(uri);
		Subcomponent selectedSub = null;
		if (eObj instanceof SystemSubcomponent || eObj instanceof ProcessSubcomponent) {
			selectedSub = (Subcomponent) eObj;
		} else {
			Dialog.showError("Virtualization Transform",
					"A system or process implementation subcomponent must be selected in order to add virtualization.");
			return;
		}

		// Make sure subcomponent refers to a component implementation
		if (selectedSub.getComponentImplementation() == null) {
			Dialog.showError("Virtualization Transform", "Selected subcomponent must be a component implementation.");
			return;
		}

		// Selected subcomponent must only contain software subcomponents
		if (!isSoftwareComponent(selectedSub)) {
			Dialog.showError("Virtualization Transform",
					"Selected component must only contain software subcomponents (system, process, thread group, thread).");
			return;
		}

		// Check if this subcomponent is bound to a processor
		// ASSUMPTION: processor binding will be specified in model for selected subcomponent, or its subcomponents
		explicitProcessorBindings = new HashMap<>();
		getExplicitProcessorBindings(selectedSub);
		implicitProcessorBindings = new HashMap<>();
		getImplicitProcessorBindings(selectedSub, "");
		// TODO: Remove this restriction and present user with list of processors and virtual processors to choose from
		// TODO: May need to look at Available_Processor_Bindings property to do this
		if (explicitProcessorBindings.isEmpty() && implicitProcessorBindings.isEmpty()) {
			Dialog.showError("Virtualization Transform",
					"The selected component (or at least one of its subcomponents) must be bound to a processor or virtual processor.");
			return;
		}

		// Open wizard to enter virtualization info
		final VirtualizationTransformDialog wizard = new VirtualizationTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		wizard.create(selectedSub);
		if (wizard.open() == Window.OK) {
			virtualProcessorComponentName = wizard.getVirtualProcessorComponentName();
			if (virtualProcessorComponentName.isEmpty()) {
				virtualProcessorComponentName = VIRTUAL_PROCESSOR_COMP_TYPE_NAME;
			}
			virtualProcessorSubcomponentName = wizard.getVirtualProcessorSubcomponentName();
			if (virtualProcessorSubcomponentName == "") {
				virtualProcessorSubcomponentName = VIRTUAL_PROCESSOR_SUBCOMP_NAME;
			}
			virtualMachineOS = wizard.getVirtualMachineOS();
			virtualizationComponents = wizard.getVirtualizationComponents();
			virtualizationRequirement = wizard.getRequirement();
		} else {
			return;
		}

		// Get the subcomponent objects corresponding to the selected name
		virtualizationSubNameMap = new HashMap<>();
		for (String vComp : virtualizationComponents) {
			Subcomponent currentSub = selectedSub;
			for (String subName : vComp.split("\\.")) {
				for (Subcomponent sub : currentSub.getComponentImplementation().getOwnedSubcomponents()) {
					if (sub.getName().equalsIgnoreCase(subName)) {
						currentSub = sub;
						break;
					}
				}
			}
			virtualizationSubNameMap.put(vComp, currentSub);
		}

		// Check that all of the selected subcomponents are bound to a processor
		// If a subcomponent isn't bound to a processor, but it's parent is, then the subcomponent
		// has the same binding as its parent
		boundProcessors = new HashSet<>();
		final Set<String> unboundSubcomponents = new HashSet<>();
		for (String vComp : virtualizationComponents) {
			if (!explicitProcessorBindings.containsKey(vComp) && implicitProcessorBindings.get(vComp) == null) {
				unboundSubcomponents.add(vComp);
			} else if (explicitProcessorBindings.containsKey(vComp)) {
				boundProcessors.addAll(explicitProcessorBindings.get(vComp));
			} else if (implicitProcessorBindings.containsKey(vComp)) {
				boundProcessors.addAll(implicitProcessorBindings.get(vComp));
			}
		}

		if (boundProcessors.size() > 1) {
			Dialog.showError("Virtualization Transform",
					"All virtualization components must be bound to the same processor.");
			return;
		}

		if (!unboundSubcomponents.isEmpty()) {
			String errMsg = "The following subcomponents must already be bound to a processor in order to be virtualized:\n\n\t";
			for (String s : unboundSubcomponents) {
				errMsg += s + "\n\t";
			}
			Dialog.showError("Virtualization Transform", errMsg);
			return;
		}

		// Insert the virtual processor type and implementation components
		// into the same package as the selected subcomponent's containing implementation.
		// Note that this could be a different package than the bound processor(s).
		insertVirtualProcessor(EcoreUtil.getURI(selectedSub));

		BriefcaseNotifier.notify("StairCASE - Virtualization Transform", "Virtual machine added to model.");

		return;

	}

	/**
	 * Inserts the virtual processor type and implementation components
	 * into the same package as the selected subcomponent.
	 * @param selectedComponentURI - URI of the selected subcomponent
	 */
	private void insertVirtualProcessor(URI selectedComponentURI) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor editor = EditorUtils.getActiveXtextEditor();
		AddVirtualizationClaim claim = null;

		if (editor != null) {
			claim = editor.getDocument().modify(resource -> {

				final Subcomponent selectedSub = (Subcomponent) resource.getEObject(selectedComponentURI.fragment());
				final ComponentImplementation containingImpl = selectedSub.getContainingComponentImpl();
				PackageSection pkgSection = AadlUtil.getContainingPackageSection(containingImpl);
				if (pkgSection == null) {
					// Something went wrong
					Dialog.showError("Virtualization Transform", "No public or private package sections found.");
					return null;
				}

				// Import CASE_Properties file
				if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
					return null;
				}

				// Create virtual processor component type
				final VirtualProcessorType vpType = (VirtualProcessorType) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getVirtualProcessorType());
				// Give it a unique name
				vpType.setName(ModelTransformUtils.getUniqueName(virtualProcessorComponentName, true,
						pkgSection.getOwnedClassifiers()));

				// Put in the right place in the package (before the selected subcomponent's containing implementation)
				pkgSection.getOwnedClassifiers().move(
						getIndex(selectedSub.getContainingComponentImpl().getTypeName(),
								pkgSection.getOwnedClassifiers()),
						pkgSection.getOwnedClassifiers().size() - 1);

				// CASE::COMP_TYPE Property
//				if (!CasePropertyUtils.setCompType(vpType, "VIRTUAL_MACHINE")) {
				if (!CasePropertyUtils.setMitigationType(vpType, MITIGATION_TYPE.VIRTUALIZATION)) {
//					return;
				}

				// Create virtual processor component implementation
				final VirtualProcessorImplementation vpImpl = (VirtualProcessorImplementation) pkgSection
						.createOwnedClassifier(Aadl2Package.eINSTANCE.getVirtualProcessorImplementation());
				vpImpl.setName(vpType.getName() + ".Impl");
				final Realization rVpImpl = vpImpl.createOwnedRealization();
				rVpImpl.setImplemented(vpType);
				// Put in the right place in the package (after the virtual processor component type)
				pkgSection.getOwnedClassifiers().move(getIndex(vpType.getName(), pkgSection.getOwnedClassifiers()) + 1,
						pkgSection.getOwnedClassifiers().size() - 1);

				// CASE::OS Property
				if (!virtualMachineOS.isEmpty()) {
					if (!CasePropertyUtils.setOs(vpImpl, virtualMachineOS)) {
//						return;
					}
				}

				// Create virtual processor subcomponent
				final VirtualProcessorSubcomponent vpSub = (VirtualProcessorSubcomponent) ComponentCreateHelper
						.createOwnedSubcomponent(containingImpl,
						ComponentCategory.VIRTUAL_PROCESSOR);

				// Give it a unique name
				vpSub.setName(ModelTransformUtils.getUniqueName(virtualProcessorSubcomponentName, true,
						containingImpl.getAllSubcomponents()));

				// Set subcomponent type
				vpSub.setVirtualProcessorSubcomponentType(vpImpl);

				// Bind the virtual processor to the processor(s)
				// If a binding to a processor already exists, add virtual processor to Applies To
				// Remove virtualized components from this processor binding
//				// Add non-virtualized components to binding if their parents are virtualized
				boolean propertyAssociationFound = false;
//				final List<String> nonVmBoundChildren = new ArrayList<>();
				final Iterator<PropertyAssociation> paIterator = containingImpl.getOwnedPropertyAssociations()
						.iterator();
				while (paIterator.hasNext()) {
					final PropertyAssociation pa = paIterator.next();
					final Property prop = pa.getProperty();
					if (prop == null || prop.getName() == null
							|| !prop.getName()
							.equalsIgnoreCase(DeploymentProperties.ACTUAL_PROCESSOR_BINDING)) {
						continue;
					}
					// Find bindings to processors (there could be multiple)
					for (ModalPropertyValue val : pa.getOwnedValues()) {
						final ListValue listVal = (ListValue) val.getOwnedValue();
						for (PropertyExpression propExpr : listVal.getOwnedListElements()) {
							if (propExpr instanceof ReferenceValue) {
								final ReferenceValue refVal = (ReferenceValue) propExpr;
								// Check if the referenced processor was bound to a virtualized component
								final String processorRef = refVal.getPath().getNamedElement().getName();
								if (boundProcessors.contains(processorRef)) {

									// Add virtual processor subcomponent to Applies To
									final ContainedNamedElement cne = pa.createAppliesTo();
									final ContainmentPathElement cpe = cne.createPath();
									cpe.setNamedElement(vpSub);

									// Remove virtualized components from this binding
									final Iterator<ContainedNamedElement> i = pa.getAppliesTos().iterator();
									while (i.hasNext()) {

										final ContainedNamedElement containedNamedElement = i.next();
										ContainmentPathElement containmentPathElement = containedNamedElement.getPath();
										String appliesTo = containmentPathElement.getNamedElement().getName();

										while (containmentPathElement.getPath() != null) {
											containmentPathElement = containmentPathElement.getPath();
											appliesTo += "." + containmentPathElement.getNamedElement().getName();
										}

										if (virtualizationComponents.contains(appliesTo)) {
											i.remove();
										}

									}

//									// If any subcomponents with implicit processor bindings to be virtualized
//									// have children that are not to be virtualized, create explicit processor bindings for them
//									for (String s : virtualizationComponents) {
//										final Set<String> processors = implicitProcessorBindings.get(s);
//										if (processors != null && processors.contains(processorRef)) {
//											final Subcomponent parent = virtualizationSubNameMap.get(s);
//											for (Subcomponent child : parent.getComponentImplementation()
//													.getOwnedSubcomponents()) {
//												if (!virtualizationSubNameMap.containsValue(child)) {
//													final String childQualName = s + "." + child.getName();
//													final ContainedNamedElement ne = createContainedNamedElement(
//															selectedSub,
//															childQualName);
//													pa.getAppliesTos().add(ne);
//													nonVmBoundChildren.add(childQualName);
//												}
//											}
//										}
//									}

									propertyAssociationFound = true;
								}
							}
						}
					}
					// If the processor binding property association does not apply to any subcomponent,
					// remove it
					if (pa.getAppliesTos().isEmpty()) {
						paIterator.remove();
					}
				}

				// If a binding to any processor doesn't exist, create one and add virtual processor
				if (!propertyAssociationFound) {
					final PropertyAssociation pa = containingImpl.createOwnedPropertyAssociation();
					final Property prop = GetProperties.lookupPropertyDefinition(containingImpl,
							DeploymentProperties._NAME,
							DeploymentProperties.ACTUAL_PROCESSOR_BINDING);
					pa.setProperty(prop);
					final ModalPropertyValue mpv = pa.createOwnedValue();
					final ListValue lv = (ListValue) mpv.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());
					for (String s : boundProcessors) {
						for (Subcomponent sub : containingImpl.getOwnedSubcomponents()) {
							if (sub.getName().equalsIgnoreCase(s)) {
								final ReferenceValue rv = (ReferenceValue) lv
										.createOwnedListElement(Aadl2Package.eINSTANCE.getReferenceValue());
								final ContainmentPathElement cpe = rv.createPath();
								cpe.setNamedElement(sub);
								break;
							}
						}
					}
					final ContainedNamedElement cne = pa.createAppliesTo();
					final ContainmentPathElement cpe = cne.createPath();
					cpe.setNamedElement(vpSub);
				}

				// Bind the selected subcomponent(s) to the virtual processor
				final PropertyAssociation pa = containingImpl.createOwnedPropertyAssociation();
				final Property prop = GetProperties.lookupPropertyDefinition(containingImpl, DeploymentProperties._NAME,
						DeploymentProperties.ACTUAL_PROCESSOR_BINDING);
				pa.setProperty(prop);
				final ModalPropertyValue mpv = pa.createOwnedValue();
				final ListValue lv = (ListValue) mpv.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());
				final ReferenceValue rv = (ReferenceValue) lv
						.createOwnedListElement(Aadl2Package.eINSTANCE.getReferenceValue());
				final ContainmentPathElement cpe = rv.createPath();
				cpe.setNamedElement(vpSub);
				for (String virtualComp : virtualizationComponents) {
					final ContainedNamedElement cne = createContainedNamedElement(selectedSub, virtualComp);
					pa.getAppliesTos().add(cne);
				}

				// Add HAMR::Component_Type property to virtualization components
//				for (String virtualComp : virtualizationComponents) {
//					final ContainedNamedElement cne = createContainedNamedElement(selectedSub, virtualComp);
//					pa.getAppliesTos().add(cne);
//				}

				// Add add_virtualization claims to resolute prove statement, if applicable
				if (!virtualizationRequirement.isEmpty()) {
//					return new AddVirtualizationClaim(virtualizationComponents, nonVmBoundChildren, vpSub);
					return new AddVirtualizationClaim(virtualizationComponents, vpSub);
				}

				return null;
			});
		}

		// Add add_virtualization claims to resolute prove statement, if applicable
		if (claim != null) {
			RequirementsManager.getInstance().modifyRequirement(virtualizationRequirement, claim);
		}

	}

	/**
	 * Gets the processor bindings that are explicitly declared in the containing
	 * component implementation of the specified subcomponent.
	 * Only processor bindings for specified subcomponent and its descendants will be returned
	 * @param rootSub - Root subcomponent
	 * @return Map of subcomponent name to processor name
	 */
	private void getExplicitProcessorBindings(Subcomponent rootSub) {

		// Get processor bindings for all components within containing component implementation of selected subcomponent
		final ComponentImplementation ci = rootSub.getContainingComponentImpl();

		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			final Property prop = pa.getProperty();
			if (prop != null && prop.getName() != null) {
				if (pa.getProperty().getName().equalsIgnoreCase(DeploymentProperties.ACTUAL_PROCESSOR_BINDING)) {
					for (ContainedNamedElement cne : pa.getAppliesTos()) {
						ContainmentPathElement cpe = cne.getPath();
						String appliesTo = cpe.getNamedElement().getName();
						Subcomponent boundSub = rootSub;
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
							final ListValue listVal = (ListValue) val.getOwnedValue();
							for (PropertyExpression propExpr : listVal.getOwnedListElements()) {
								if (propExpr instanceof ReferenceValue) {
									final ReferenceValue refVal = (ReferenceValue) propExpr;

									if (refVal.getPath().getNamedElement() instanceof ProcessorSubcomponent || refVal
											.getPath().getNamedElement() instanceof VirtualProcessorSubcomponent) {

										if (explicitProcessorBindings.get(appliesTo) == null) {
											explicitProcessorBindings.put(appliesTo, new HashSet<String>(
													Arrays.asList(refVal.getPath().getNamedElement().getName())));
										} else {
											explicitProcessorBindings.get(appliesTo)
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
	}

	private void getImplicitProcessorBindings(Subcomponent rootSub, String parentName) {

		final String qualifiedName = (parentName.isEmpty() ? "" : parentName + ".") + rootSub.getName();

		// If the specified subcomponent isn't explicitly bound to a processor,
		// check its parents binding
		if (explicitProcessorBindings.get(qualifiedName) == null) {
			if (explicitProcessorBindings.containsKey(parentName)) {
				implicitProcessorBindings.put(qualifiedName, explicitProcessorBindings.get(parentName));
			} else if (implicitProcessorBindings.containsKey(parentName)) {
				implicitProcessorBindings.put(qualifiedName, implicitProcessorBindings.get(parentName));
			}
		}
		for (Subcomponent sub : rootSub.getComponentImplementation().getOwnedSubcomponents()) {
			getImplicitProcessorBindings(sub, qualifiedName);
		}
	}

	private ContainedNamedElement createContainedNamedElement(Subcomponent rootSub, String qualifiedName) {

		final ContainedNamedElement cne = Aadl2Factory.eINSTANCE.createContainedNamedElement();
		ContainmentPathElement cpe = cne.createPath();
		Subcomponent currentSub = rootSub;

		for (String subName : qualifiedName.split("\\.")) {

			if (subName.equalsIgnoreCase(rootSub.getName())) {
				cpe = cne.createPath();
			} else {
				for (Subcomponent sub : currentSub.getComponentImplementation().getOwnedSubcomponents()) {
					if (sub.getName().equalsIgnoreCase(subName)) {
						currentSub = sub;
						break;
					}
				}
				cpe = cpe.createPath();
			}
			cpe.setNamedElement(currentSub);
		}
		return cne;
	}

	private boolean isSoftwareComponent(Subcomponent sub) {
		if (sub.getComponentImplementation().getCategory() == ComponentCategory.THREAD) {
			return true;
		} else if (sub.getComponentImplementation().getCategory() == ComponentCategory.SYSTEM
				|| sub.getComponentImplementation().getCategory() == ComponentCategory.PROCESS
				|| sub.getComponentImplementation().getCategory() == ComponentCategory.THREAD_GROUP) {
			if (sub.getComponentImplementation() == null) {
				return true;
			}
			for (Subcomponent s : sub.getComponentImplementation().getOwnedSubcomponents()) {
				if (!isSoftwareComponent(s)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

}
