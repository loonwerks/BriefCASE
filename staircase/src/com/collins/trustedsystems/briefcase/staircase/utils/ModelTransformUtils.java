package com.collins.trustedsystems.briefcase.staircase.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ConnectionEnd;
import org.osate.aadl2.DataClassifier;
import org.osate.aadl2.DataImplementation;
import org.osate.aadl2.DataType;
import org.osate.aadl2.DirectedFeature;
import org.osate.aadl2.Feature;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.ModelUnit;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;

public class ModelTransformUtils {

	/**
	 * Returns all the in data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of in port names
	 */
	public static List<String> getInports(ComponentImplementation ci) {
		final List<String> inports = new ArrayList<>();
		// Get component implementation out ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof DirectedFeature && ((DirectedFeature) f).isOut()) {
				if (f instanceof Port) {
					inports.add(f.getName());
				} else if (f instanceof FeatureGroup) {
					inports.add(f.getName());
					// TODO: support nested feature groups
					for (Feature p : ((FeatureGroup) f).getFeatureGroupType().getAllFeatures()) {
						if (p instanceof Port && ((Port) p).isIn()) {
							inports.add(f.getName() + "[" + p.getName() + "]");
						}
					}
				}
			}
		}

		// Get subcomponent in ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof DirectedFeature && ((DirectedFeature) f).isIn()) {
					if (f instanceof Port) {
						inports.add(s.getName() + "." + f.getName());
					} else if (f instanceof FeatureGroup) {
						inports.add(s.getName() + "." + f.getName());
						// TODO: support nested feature groups
						for (Feature p : ((FeatureGroup) f).getFeatureGroupType().getAllFeatures()) {
							if (p instanceof Port && ((Port) p).isIn()) {
								inports.add(s.getName() + "." + f.getName() + "[" + p.getName() + "]");
							}
						}
					}
				}
			}
		}
		return inports;
	}

	/**
	 * Returns all the out data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of out port names
	 */
	public static List<String> getOutports(ComponentImplementation ci) {
		final List<String> outports = new ArrayList<>();
		// Get component implementation in ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof DirectedFeature && ((DirectedFeature) f).isIn()) {
				if (f instanceof Port) {
					outports.add(f.getName());
				} else if (f instanceof FeatureGroup) {
					outports.add(f.getName());
					// TODO: support nested feature groups
					for (Feature p : ((FeatureGroup) f).getFeatureGroupType().getAllFeatures()) {
						if (p instanceof Port && ((Port) p).isIn()) {
							outports.add(f.getName() + "[" + p.getName() + "]");
						}
					}
				}
			}
		}

		// Get subcomponent out ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof DirectedFeature && ((DirectedFeature) f).isOut()) {
					if (f instanceof Port) {
						outports.add(s.getName() + "." + f.getName());
					} else if (f instanceof FeatureGroup) {
						outports.add(s.getName() + "." + f.getName());
						// TODO: support nested feature groups
						for (Feature p : ((FeatureGroup) f).getFeatureGroupType().getAllFeatures()) {
							if (p instanceof Port && ((Port) p).isIn()) {
								outports.add(s.getName() + "." + f.getName() + "[" + p.getName() + "]");
							}
						}
					}
				}
			}
		}
		return outports;
	}

	/**
	 * Returns the port of the specified subcomponent port name
	 * in the specified component implementation
	 * @param ci - component implementation
	 * @param portName - <subcomponent> . <feature name>
	 * @return
	 */
	public static ConnectionEnd getPort(ComponentImplementation ci, String portName) {
		String featureName = null;
		if (portName.contains("[")) {
			featureName = portName.substring(portName.indexOf('[') + 1, portName.length() - 1);
			portName = portName.substring(0, portName.indexOf('['));
		}
		final String[] parts = portName.split("\\.");
		if (parts.length == 1) {
			for (Feature f : ci.getAllFeatures()) {
				if (f.getName().equalsIgnoreCase(portName)) {
					if (f instanceof Port) {
						return f;
					} else if (f instanceof FeatureGroup) {
						if (featureName == null) {
							return f;
						} else {
							for (Feature p : ((FeatureGroup) f).getFeatureGroupType().getOwnedFeatures()) {
								if (p.getName().equalsIgnoreCase(featureName)) {
									return p;
								}
							}
							return null;
						}
					} else {
						return null;
					}
				}
			}
		} else if (parts.length > 1) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					for (Feature f : s.getAllFeatures()) {
						if (f.getName().equalsIgnoreCase(parts[1])) {
							if (f instanceof Port) {
								return f;
							} else if (f instanceof FeatureGroup) {
								if (featureName == null) {
									return f;
								} else {
									for (Feature p : ((FeatureGroup) f).getFeatureGroupType().getOwnedFeatures()) {
										if (p.getName().equalsIgnoreCase(featureName)) {
											return p;
										}
									}
									return null;
								}
							} else {
								return null;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static Subcomponent getSubcomponent(ComponentImplementation ci, String portName) {
		final String[] parts = portName.split("\\.");
		if (parts.length == 2) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					return s;
				}
			}
		}
		return null;
	}

	public static boolean isValidName(String name) {
		return name.matches("([a-z]|[A-Z])((_)?([a-z]|[A-Z]|[0-9]))*");
	}

	/**
	 * Builds an identifier using the specified base name that doesn't conflict with identifiers in the specified element list.
	 * @param baseIdentifier - Name
	 * @param startWithBase - If true, if the baseIdentifier is the only one of its kind,
	 * it will be returned as 'baseIdentifier' rather than 'baseIdentifier1'
	 * @param elements - Collection of names which cannot match base name
	 * @return An identifier that is unique in the specified list
	 */
	public static String getUniqueName(String baseIdentifier, boolean startWithBase,
			final Collection<? extends NamedElement> elements) {

		// Sort names list alphabetically
		final TreeSet<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		elements.forEach(n -> {
			if (n.getName() != null) {
				names.add(n.getName());
			}
		});

		// Resolve naming conflicts
		String newIdentifier = baseIdentifier + (startWithBase ? "" : "1");
		boolean done = false;
		int num = getLastInt(baseIdentifier);
		if (num > 0) {
			// If the baseIdentifier already has a number at the end, start with it
			baseIdentifier = baseIdentifier.substring(0, baseIdentifier.length() - Integer.toString(num).length());
		} else if (num == 0 && !startWithBase) {
			num = 1;
		}

		do {
			if (names.contains(newIdentifier)) {
				num++;
				newIdentifier = baseIdentifier + num;
			} else {
				done = true;
			}
		} while (!done);

		return newIdentifier;
	}

	/**
	 * Adds the containing package of the specified object to the specified package section's with clause if it isn't already present
	 * @param obj - Object whose containing package needs to be imported
	 * @param pkgSection - Package Section
	 */
	public static void importContainingPackage(EObject obj, PackageSection pkgSection) {

		final AadlPackage pkg = AadlUtil.getContainingPackage(obj);
		if (pkg == null) {
			return;
		}
		// Don't add the with clause if it is for the current package
		final AadlPackage thisPkg = AadlUtil.getContainingPackage(pkgSection);
		if (thisPkg == null || pkg.getName().equalsIgnoreCase(thisPkg.getName())) {
			return;
		}
		boolean pkgFound = false;
		for (ModelUnit mu : pkgSection.getImportedUnits()) {
			if (mu.getName().equalsIgnoreCase(pkg.getName())) {
				pkgFound = true;
				break;
			}
		}
		if (!pkgFound) {
			pkgSection.getImportedUnits().add(pkg);
		}
	}

	/**
	 * Returns a list of data types and data implementations for each
	 * package visible to obj
	 * @param obj
	 * @return
	 */
	public static Map<String, List<String>> getTypes(EObject obj) {
		Map<String, List<String>> types = new HashMap<>();
		if (obj == null) {
			return null;
		}

		// Look in current package
		final PackageSection pkgSection = AadlUtil.getContainingPackageSection(obj);
		if (pkgSection == null) {
			return null;
		}
		List<String> packageTypes = new ArrayList<>();
		for (Classifier c : pkgSection.getOwnedClassifiers()) {
			if (c instanceof DataType || c instanceof DataImplementation) {
				packageTypes.add(c.getName());
			}
		}
		if (!packageTypes.isEmpty()) {
			types.put(AadlUtil.getContainingPackage(pkgSection).getName(), packageTypes);
		}
		// Look in referenced packages specified in with clause
		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			packageTypes = new ArrayList<>();
			if (modelUnit instanceof AadlPackage) {
				final AadlPackage aadlPkg = (AadlPackage) modelUnit;
				for (Classifier c : aadlPkg.getOwnedPublicSection().getOwnedClassifiers()) {
					if (c instanceof DataType || c instanceof DataImplementation) {
						packageTypes.add(c.getName());
					}
				}
				if (!packageTypes.isEmpty()) {
					types.put(aadlPkg.getName(), packageTypes);
				}
			}
		}

		return types;
	}

	/**
	 * Returns a list of data types and data implementations for each
	 * package visible to obj
	 * @param obj
	 * @return
	 */
	public static Map<String, List<DataClassifier>> getVisibleDataClassifiers(EObject obj) {
		if (obj == null) {
			return null;
		}
		final Map<String, List<DataClassifier>> types = new HashMap<>();

		// Look in current package
		final PackageSection pkgSection = AadlUtil.getContainingPackageSection(obj);
		if (pkgSection == null) {
			return null;
		}

		List<DataClassifier> classifiers = new ArrayList<>();
		for (Classifier c : pkgSection.getOwnedClassifiers()) {
			if (c instanceof DataClassifier) {
				classifiers.add((DataClassifier) c);
			}
		}
		if (!classifiers.isEmpty()) {
			types.put(AadlUtil.getContainingPackage(pkgSection).getName(), classifiers);
		}

		// Look in referenced packages specified in with clause
		for (ModelUnit modelUnit : pkgSection.getImportedUnits()) {
			if (modelUnit instanceof AadlPackage) {
				final AadlPackage aadlPkg = (AadlPackage) modelUnit;
				classifiers.clear();
				if (aadlPkg.getOwnedPublicSection() != null) {
					for (Classifier c : aadlPkg.getOwnedPublicSection().getOwnedClassifiers()) {
						if (c instanceof DataClassifier) {
							classifiers.add((DataClassifier) c);
						}
					}
				}
				if (!classifiers.isEmpty()) {
					types.put(aadlPkg.getName(), classifiers);
				}
			}
		}

		return types;
	}

	/**
	 * Helper function for getUniqueName() for extracting a number at the end of a string
	 * @param name - String
	 * @return An integer
	 */
	private static int getLastInt(String name) {

		int offset = name.length();
		for (int i = name.length() - 1; i >= 0; i--) {
			char c = name.charAt(i);
			if (Character.isDigit(c)) {
				offset--;
			} else {
				if (offset == name.length()) {
					// No int at the end
					return 0;
				}
				return Integer.parseInt(name.substring(offset));
			}
		}
		return Integer.parseInt(name.substring(offset));
	}

}
