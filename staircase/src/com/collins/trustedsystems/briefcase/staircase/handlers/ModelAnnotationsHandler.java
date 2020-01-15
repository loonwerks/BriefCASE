package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.Subcomponent;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.ModelAnnotationsDialog;
import com.collins.trustedsystems.briefcase.staircase.utils.CaseUtils;

public class ModelAnnotationsHandler extends AadlHandler {

	public enum CIA {
		HIGH, MEDIUM, LOW, NULL
	};

	public enum COMP_TYPE {
		FILTER, ATTESTATION, MONITOR, ROUTER, ISOLATOR, COMM_DRIVER, NULL
	};

	public enum COMM_MODALITY {
		RF, WIFI, WIRED_ETHERNET, SERIAL, BT, NULL
	};

	public enum BOUNDARY {
		TRUSTED, PHYSICAL, NULL
	}

	private CIA confidentiality = CIA.NULL;
	private CIA integrity = CIA.NULL;
	private CIA availability = CIA.NULL;
	private COMP_TYPE compType = COMP_TYPE.NULL;
	private COMM_MODALITY commModality = COMM_MODALITY.NULL;
	private Set<BOUNDARY> boundary = new HashSet<>();

	@Override
	protected void runCommand(URI uri) {
		// Check that it is a component type
		final EObject eObj = getEObject(uri);
		ComponentType component = null;
		if (eObj instanceof Subcomponent) {
			component = ((Subcomponent) eObj).getComponentType();
		} else if (eObj instanceof ComponentImplementation) {
			component = ((ComponentImplementation) eObj).getType();
		} else if (eObj instanceof ComponentType) {
			component = (ComponentType) eObj;
		} else {
			Dialog.showError("Model Annotations", "A component must be selected to annotate.");
			return;
		}

		// Get the current CASE annotations for the selected component
		getCurrentAnnotations(component);

		ModelAnnotationsDialog wizard = new ModelAnnotationsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.setComponentAnnotations(component, confidentiality, integrity, availability, compType, commModality,
				boundary);
		wizard.create();
		if (wizard.open() == Window.OK) {
			confidentiality = wizard.getConfidentiality();
			integrity = wizard.getIntegrity();
			availability = wizard.getAvailability();
			compType = wizard.getCompType();
			commModality = wizard.getCommModality();
			boundary = wizard.getBoundary();
		} else {
			return;
		}

		setAnnotations(component);

	}

	private void setAnnotations(ComponentType component) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {

				// Remove existing CASE properties
				boolean addConfidentiality = (confidentiality != CIA.NULL);
				boolean addIntegrity = (integrity != CIA.NULL);
				boolean addAvailability = (availability != CIA.NULL);
				boolean addCompType = (compType != COMP_TYPE.NULL);
				boolean addCommModality = (commModality != COMM_MODALITY.NULL);
				boolean addBoundary = !boundary.isEmpty();
				Iterator<PropertyAssociation> prop = component.getOwnedPropertyAssociations().iterator();
				while (prop.hasNext()) {
					String propName = prop.next().getProperty().getName();
					if (propName.equalsIgnoreCase("CONFIDENTIALITY")) {
						prop.remove();
					} else if (propName.equalsIgnoreCase("INTEGRITY")) {
						prop.remove();
					} else if (propName.equalsIgnoreCase("AVAILABILITY")) {
						prop.remove();
					} else if (propName.equalsIgnoreCase("COMP_TYPE")) {
						prop.remove();
					} else if (propName.equalsIgnoreCase("COMM_MODALITY")) {
						prop.remove();
					} else if (propName.equalsIgnoreCase("BOUNDARY")) {
						prop.remove();
					}
				}

				// Update CASE properties
				if (addConfidentiality || addIntegrity || addAvailability || addCompType || addCommModality
						|| addBoundary) {

					final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
					PackageSection pkgSection = null;
					// Figure out if the selected connection is in the public or private section
					EObject eObj = component.eContainer();
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
						Dialog.showError("Model Annotations", "No public or private package sections found.");
						return;
					}

					// Import CASE_Properties file
					if (!CaseUtils.addCasePropertyImport(pkgSection)) {
						return;
					}

					if (addConfidentiality) {
						if (!CaseUtils.addCasePropertyAssociation("CONFIDENTIALITY", confidentiality.toString(),
								component)) {
							Dialog.showError("Model Annotations",
									"Unable to set the CONFIDENTIALITY property for " + component.getName() + ".");
//							return;
						}
					}
					if (addIntegrity) {
						if (!CaseUtils.addCasePropertyAssociation("INTEGRITY", integrity.toString(), component)) {
							Dialog.showError("Model Annotations",
									"Unable to set the INTEGRITY property for " + component.getName() + ".");
//							return;
						}
					}
					if (addAvailability) {
						if (!CaseUtils.addCasePropertyAssociation("AVAILABILITY", availability.toString(), component)) {
							Dialog.showError("Model Annotations",
									"Unable to set the AVAILABILITY property for " + component.getName() + ".");
//							return;
						}
					}
					if (addCompType) {
						if (!CaseUtils.addCasePropertyAssociation("COMP_TYPE", compType.toString(), component)) {
							Dialog.showError("Model Annotations",
									"Unable to set the COMP_TYPE property for " + component.getName() + ".");
//							return;
						}
					}
					if (addCommModality) {
						if (!CaseUtils.addCasePropertyAssociation("COMM_MODALITY", commModality.toString(),
								component)) {
							Dialog.showError("Model Annotations",
									"Unable to set the COMM_MODALITY property for " + component.getName() + ".");
//							return;
						}
					}
					if (addBoundary) {
						String bString = "";
						for (BOUNDARY b : boundary) {
							bString = bString + b.toString() + ",";
						}
						bString = bString.substring(0, bString.length() - 1);
						if (!CaseUtils.addCasePropertyAssociation("BOUNDARY", bString, component)) {
							Dialog.showError("Model Annotations",
									"Unable to set the BOUNDARY property for " + component.getName() + ".");
//							return;
						}
					}
				}
			}
		});
	}


	private void reset() {
		confidentiality = CIA.NULL;
		integrity = CIA.NULL;
		availability = CIA.NULL;
		compType = COMP_TYPE.NULL;
		commModality = COMM_MODALITY.NULL;
		boundary = new HashSet<>();
	}

	private void getCurrentAnnotations(ComponentType component) {

		// Reset to default values
		reset();

		for (PropertyAssociation propertyAssociation : component.getOwnedPropertyAssociations()) {
			Property property = propertyAssociation.getProperty();
			if (property.getName().equalsIgnoreCase("CONFIDENTIALITY")) {
				ModalPropertyValue val = propertyAssociation.getOwnedValues().get(0);
				NamedValue namedVal = (NamedValue) val.getOwnedValue();
				EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
				confidentiality = CIA.valueOf(enumLiteral.getName());
			} else if (property.getName().equalsIgnoreCase("INTEGRITY")) {
				ModalPropertyValue val = propertyAssociation.getOwnedValues().get(0);
				NamedValue namedVal = (NamedValue) val.getOwnedValue();
				EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
				integrity = CIA.valueOf(enumLiteral.getName());
			} else if (property.getName().equalsIgnoreCase("AVAILABILITY")) {
				ModalPropertyValue val = propertyAssociation.getOwnedValues().get(0);
				NamedValue namedVal = (NamedValue) val.getOwnedValue();
				EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
				availability = CIA.valueOf(enumLiteral.getName());
			} else if (property.getName().equalsIgnoreCase("COMP_TYPE")) {
				ModalPropertyValue val = propertyAssociation.getOwnedValues().get(0);
				NamedValue namedVal = (NamedValue) val.getOwnedValue();
				EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
				compType = COMP_TYPE.valueOf(enumLiteral.getName());
			} else if (property.getName().equalsIgnoreCase("COMM_MODALITY")) {
				ModalPropertyValue val = propertyAssociation.getOwnedValues().get(0);
				NamedValue namedVal = (NamedValue) val.getOwnedValue();
				EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
				commModality = COMM_MODALITY.valueOf(enumLiteral.getName());
			} else if (property.getName().equalsIgnoreCase("BOUNDARY")) {
				ModalPropertyValue val = propertyAssociation.getOwnedValues().get(0);
				ListValue listVal = (ListValue) val.getOwnedValue();
				for (PropertyExpression propertyExpression : listVal.getOwnedListElements()) {
					NamedValue namedVal = (NamedValue) propertyExpression;
					EnumerationLiteral enumLiteral = (EnumerationLiteral) namedVal.getNamedValue();
					boundary.add(BOUNDARY.valueOf(enumLiteral.getName()));
				}
			}
		}

	}

}
