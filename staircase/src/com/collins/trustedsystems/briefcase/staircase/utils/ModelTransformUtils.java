package com.collins.trustedsystems.briefcase.staircase.utils;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Feature;
import org.osate.aadl2.Port;
import org.osate.aadl2.Subcomponent;

public class ModelTransformUtils {

	/**
	 * Returns all the in data ports in the specified component implementation
	 * @param ci - component implementation
	 * @return list of in port names
	 */
	public static List<String> getInports(ComponentImplementation ci) {
		List<String> inports = new ArrayList<>();
		// Get component implementation out ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof Port && ((Port) f).isOut()) {
				inports.add(f.getName());
			}
		}

		// Get subcomponent in ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof Port && ((Port) f).isIn()) {
					inports.add(s.getName() + "." + f.getName());
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
		List<String> outports = new ArrayList<>();
		// Get component implementation in ports
		for (Feature f : ci.getAllFeatures()) {
			if (f instanceof Port && ((Port) f).isIn()) {
				outports.add(f.getName());
			}
		}

		// Get subcomponent out ports
		for (Subcomponent s : ci.getOwnedSubcomponents()) {
			for (Feature f : s.getAllFeatures()) {
				if (f instanceof Port && ((Port) f).isOut()) {
					outports.add(s.getName() + "." + f.getName());
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
	public static Port getPort(ComponentImplementation ci, String portName) {
		String[] parts = portName.split("\\.");
		if (parts.length == 1) {
			for (Feature f : ci.getAllFeatures()) {
				if (f.getName().equalsIgnoreCase(portName)) {
					if (f instanceof Port) {
						return (Port) f;
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
								return (Port) f;
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
		String[] parts = portName.split("\\.");
		if (parts.length == 2) {
			for (Subcomponent s : ci.getOwnedSubcomponents()) {
				if (s.getName().equalsIgnoreCase(parts[0])) {
					return s;
				}
			}
		}
		return null;
	}

}
