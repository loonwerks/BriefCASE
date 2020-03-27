package com.collins.trustedsystems.briefcase.staircase.utils;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.AbstractNamedValue;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListType;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.modelsupport.scoping.Aadl2GlobalScopeUtil;
import org.osate.aadl2.properties.PropertyDoesNotApplyToHolderException;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.Activator;

public class CasePropertyUtils {

	public static final String CASE_RESOURCE_PATH = Activator.PLUGIN_ID + "/resources/";
	public static final String CASE_PROPSET_NAME = "CASE_Properties";
	public static final String CASE_PROPSET_FILE = "CASE_Properties.aadl";

	public static final String COMP_TYPE = "Component_Type";
	public static final String COMP_IMPL = "Component_Impl";
	public static final String COMP_SPEC = "Component_Spec";

	public static final String CACHE_TIMEOUT = "Cache_Timeout";
	public static final String CACHE_SIZE = "Cache_Size";

	public static final String COMM_MODALITY = "Comm_Modality";

	public static final String OS = "OS";

	/**
	 * Adds the CASE_Properties file to the list of imported model units via the 'with' statement
	 * to the specified package section
	 * @param pkgSection - The package section (public or private) to add the imported file to
	 * @return the property set
	 */
	public static boolean addCasePropertyImport(PackageSection pkgSection) throws Exception {

		// First check if CASE Property file has already been imported in the model
		PropertySet casePropSet = null;
		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			if (modelUnit instanceof PropertySet) {
				if (modelUnit.getName().equalsIgnoreCase(CASE_PROPSET_NAME)) {
					casePropSet = (PropertySet) modelUnit;
					break;
				}
			}
		}

		if (casePropSet == null) {
			// Try importing the resource
			casePropSet = getCasePropertySet();
			if (casePropSet == null) {
				Dialog.showError("Could not import " + CASE_PROPSET_NAME,
						"Property set " + CASE_PROPSET_NAME + " could not be found.");
				return false;
			}
			// Add as "importedUnit" to package section
			pkgSection.getImportedUnits().add(casePropSet);
		}

		return true;
	}

	/**
	 * Gets the CASE property set from either the current resource, or
	 * the specified file, provided as an OSATE plugin.
	 * @return PropertySet
	 */
	public static PropertySet getCasePropertySet() {

		PropertySet propSet = null;

		final String pathName = CASE_RESOURCE_PATH + CASE_PROPSET_FILE;
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource r = resourceSet.getResource(URI.createPlatformPluginURI(pathName, true), true);
		final EObject eObj = r.getContents().get(0);
		if (eObj instanceof PropertySet) {
			propSet = (PropertySet) eObj;
		}

		return propSet;
	}

	/**
	 * Adds a name/value property association to the provided thread component.
	 * The property association must have a type that is defined in the specified property set.
	 * @param propName - Property name
	 * @param propVal - Property value
	 * @param threadType - Thread that will contain the property association
	 * @param casePropSet - PropertySet that defines the property
	 * @return A boolean indicating success
	 */
	public static boolean addCasePropertyAssociation(String propName, String propVal, Classifier classifier) {
		return addPropertyAssociation(propName, propVal, classifier, getCasePropertySet());
	}

	/**
	 * Adds a name/value property association to the provided thread component.
	 * The property association must have a type that is defined in the specified property set.
	 * @param propName - Property name
	 * @param propVal - Property value
	 * @param threadType - Thread that will contain the property association
	 * @param casePropSet - PropertySet that defines the property
	 * @return A boolean indicating success
	 */
	public static boolean addPropertyAssociation(String propName, String propVal, Classifier classifier,
			PropertySet propSet) {

		PropertyAssociation propAssocImpl = null;
		Property prop = null;

		// Check if the property is already present in the component.
		// If so, we don't need to create a new property association, just overwrite the existing one
		for (PropertyAssociation propAssoc : classifier.getOwnedPropertyAssociations()) {
			if (propAssoc.getProperty().getName().equalsIgnoreCase(propName)) {
				propAssocImpl = propAssoc;
				break;
			}
		}

		if (propAssocImpl == null) {

			// Property is not already present in the component. Need to create a new property association
			propAssocImpl = classifier.createOwnedPropertyAssociation();

			// Find the property in the specified property set
			for (Property p : propSet.getOwnedProperties()) {
				if (p.getName().equalsIgnoreCase(propName)) {
					prop = p;
					break;
				}
			}

			// Make sure the property type was found
			if (prop != null) {
				propAssocImpl.setProperty(prop);
			} else {
				Dialog.showError(propSet.getName() + " Properties",
						propName + " property not found in " + propSet.getName() + " property set.");
				return false;
			}

		} else {
			// Property is already present in the component.
			prop = propAssocImpl.getProperty();
			// Clear the current value. We write the new value below.
			propAssocImpl.getOwnedValues().clear();
		}

		// Add property value
		final ModalPropertyValue modalPropVal = propAssocImpl.createOwnedValue();

		// Figure out what type the property value is
		if (prop.getOwnedPropertyType() instanceof EnumerationType) {
			final NamedValue namedVal = (NamedValue) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getNamedValue());
			final EnumerationLiteral enumLiteralCompType = (EnumerationLiteral) EcoreUtil
					.create(Aadl2Package.eINSTANCE.getEnumerationLiteral());
			enumLiteralCompType.setName(propVal);
			namedVal.setNamedValue(enumLiteralCompType);
		} else if (prop.getOwnedPropertyType() instanceof AadlBoolean) {
			final BooleanLiteral boolVal = (BooleanLiteral) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getBooleanLiteral());
			boolVal.setValue(Boolean.parseBoolean(propVal));
		} else if (prop.getOwnedPropertyType() instanceof AadlInteger) {
			final IntegerLiteral intVal = (IntegerLiteral) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getIntegerLiteral());
			try {
				intVal.setValue(Long.parseLong(propVal));
			} catch (NumberFormatException e) {
				Dialog.showError(CASE_PROPSET_NAME + " Properties", "Value for " + propName + " must be a number.");
				return false;
			}
		} else if (prop.getOwnedPropertyType() instanceof AadlString) {
			final StringLiteral stringVal = (StringLiteral) modalPropVal
					.createOwnedValue(Aadl2Package.eINSTANCE.getStringLiteral());
			stringVal.setValue(propVal);
		} else if (prop.getOwnedPropertyType() instanceof ListType) {
			final ListValue listVal = (ListValue) modalPropVal.createOwnedValue(Aadl2Package.eINSTANCE.getListValue());

			String[] elements = propVal.split(",");

			// TODO: This property could be a list of enums, strings, numbers, lists, etc
			// TODO: This really needs to be set up in a recursive manner
			// Figure out which
			ListType listType = (ListType) prop.getOwnedPropertyType();
			PropertyType listSubType = listType.getOwnedElementType();
			if (listSubType instanceof AadlString) {
				for (String element : elements) {
					StringLiteral stringVal = (StringLiteral) listVal
							.createOwnedListElement(Aadl2Package.eINSTANCE.getStringLiteral());
					stringVal.setValue(element);
				}
			} else if (listSubType instanceof AadlInteger) {

			} else if (listSubType instanceof AadlBoolean) {

			} else if (listSubType instanceof EnumerationType) {
				for (String element : elements) {
					NamedValue namedVal = (NamedValue) listVal
							.createOwnedListElement(Aadl2Package.eINSTANCE.getNamedValue());
					EnumerationLiteral enumLiteral = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
					enumLiteral.setName(element);
					namedVal.setNamedValue(enumLiteral);
				}
			} else {

			}

		} else {
			// TODO: Add other property types
			// Couldn't figure it out
			Dialog.showError(CASE_PROPSET_NAME + " Properties",
					"Could not determine property type of " + propName + ".");
			return false;
		}

		return true;
	}

	public static boolean setCompType(Classifier comp, String compType) {
		try {
			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + COMP_TYPE);
			EnumerationLiteral enumLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
			enumLit.setName(compType);
			NamedValue namedVal = Aadl2Factory.eINSTANCE.createNamedValue();
			namedVal.setNamedValue(enumLit);
			comp.setPropertyValue(prop, namedVal);
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return true;
	}

	public static boolean setCompImpl(Classifier comp, String compImpl) {
		try {
			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + COMP_IMPL);
			StringLiteral strLit = Aadl2Factory.eINSTANCE.createStringLiteral();
			strLit.setValue(compImpl);
			comp.setPropertyValue(prop, strLit);
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return true;
	}

	public static boolean setCompSpec(Classifier comp, String compSpec) {
		try {
			String[] elements = compSpec.split(",");
			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + COMP_SPEC);
			ListValue listVal = Aadl2Factory.eINSTANCE.createListValue();
//			List<? extends PropertyExpression> listVal = new ArrayList<>();
			for (String s : elements) {
				StringLiteral strLit = (StringLiteral) listVal
						.createOwnedListElement(Aadl2Package.eINSTANCE.getStringLiteral());
				strLit.setValue(s);
//				StringLiteral strLit = Aadl2Factory.eINSTANCE.createStringLiteral();
//				strLit.setValue(s);
//				listVal.add(strLit);
			}

			comp.setPropertyValue(prop, listVal.getOwnedListElements());
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return true;
	}

	public static boolean setCacheSize(Classifier comp, long cacheSize) {
		try {
			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + CACHE_SIZE);
			IntegerLiteral intLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
			intLit.setValue(cacheSize);
			comp.setPropertyValue(prop, intLit);
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return true;
	}

	public static boolean setCacheTimeout(Classifier comp, long cacheTimeout) {
		try {
			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + CACHE_TIMEOUT);
			IntegerLiteral intLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
			intLit.setValue(cacheTimeout);
			comp.setPropertyValue(prop, intLit);
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return true;
	}

	public static boolean setOs(Classifier comp, String os) {
		try {
			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + OS);
			StringLiteral strLit = Aadl2Factory.eINSTANCE.createStringLiteral();
			strLit.setValue(os);
			comp.setPropertyValue(prop, strLit);
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return true;
	}

	public static boolean isCompType(Classifier comp, String compType) {

		try {

			Property prop = Aadl2GlobalScopeUtil.get(comp, Aadl2Package.eINSTANCE.getProperty(),
					CASE_PROPSET_NAME + "::" + COMP_TYPE);
			List<? extends PropertyExpression> propVal = comp.getPropertyValueList(prop);

			if (propVal != null) {
				for (PropertyExpression expr : propVal) {
					if (expr instanceof NamedValue) {
						NamedValue namedVal = (NamedValue) expr;
						AbstractNamedValue absVal = namedVal.getNamedValue();
						if (absVal instanceof EnumerationLiteral) {
							EnumerationLiteral enVal = (EnumerationLiteral) absVal;
							if (enVal.getName().equalsIgnoreCase(compType)) {
								return true;
							}
						}
					}
				}
			}
		} catch (PropertyDoesNotApplyToHolderException e) {
			return false;
		}
		return false;
	}

}