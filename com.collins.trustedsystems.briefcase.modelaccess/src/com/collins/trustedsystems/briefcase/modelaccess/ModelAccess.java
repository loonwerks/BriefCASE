package com.collins.trustedsystems.briefcase.modelaccess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Connection;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.ConnectionReference;
import org.osate.aadl2.instance.FeatureInstance;

import com.rockwellcollins.atc.resolute.analysis.execution.EvaluationContext;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteExternalFunctionLibrary;
import com.rockwellcollins.atc.resolute.analysis.execution.ResoluteFailException;
import com.rockwellcollins.atc.resolute.analysis.values.NamedElementValue;
import com.rockwellcollins.atc.resolute.analysis.values.ResoluteValue;
import com.rockwellcollins.atc.resolute.analysis.values.SetValue;

public class ModelAccess extends ResoluteExternalFunctionLibrary {

	private EvaluationContext context;

	@Override
	public ResoluteValue run(EvaluationContext context, String function, List<ResoluteValue> args) {

		this.context = context;

		switch (function.toLowerCase()) {

		case "getcomponent": {
			final ResoluteValue arg0 = args.get(0);
			assert (arg0.isString());
			return getComponent(arg0.getString());
		}
		case "getcomponentfrom": {
			final ResoluteValue arg0 = args.get(0);
			final ResoluteValue arg1 = args.get(1);
			assert (arg0.isNamedElement());
			assert (arg1.isString());
			return getComponentFrom(arg0.getNamedElement(), arg1.getString());
		}
		case "getport": {
			final ResoluteValue arg0 = args.get(0);
			assert (arg0.isString());
			return getPort(arg0.getString());
		}
		case "getportfrom": {
			final ResoluteValue arg0 = args.get(0);
			final ResoluteValue arg1 = args.get(1);
			assert (arg0.isNamedElement());
			assert (arg1.isString());
			return getPortFrom(arg0.getNamedElement(), arg1.getString());
		}
		case "getconnection": {
			final ResoluteValue arg0 = args.get(0);
			assert (arg0.isString());
			return getConnection(arg0.getString());
		}
		case "getconnectionfrom": {
			final ResoluteValue arg0 = args.get(0);
			final ResoluteValue arg1 = args.get(1);
			assert (arg0.isNamedElement());
			assert (arg1.isString());
			return getConnectionFrom(arg0.getNamedElement(), arg1.getString());
		}
		case "getcomponentset": {
			final ResoluteValue arg0 = args.get(0);
			assert (arg0.isString());
			return getComponentSet(arg0.getString());
		}
		case "getcomponentsetfrom": {
			final ResoluteValue arg0 = args.get(0);
			final ResoluteValue arg1 = args.get(1);
			assert (arg0.isNamedElement());
			assert (arg1.isString());
			return getComponentSetFrom(arg0.getNamedElement(), arg1.getString());
		}
		}

		throw new ResoluteFailException("Function " + function + " not part of ModelAccess library.",
				context.getThisInstance().getSubcomponent());
	}

	private ResoluteValue getComponent(String compName) {
		return getComponentFrom(context.getThisInstance(), compName);
	}

	private ResoluteValue getComponentFrom(NamedElement parent, String compName) {

		if (!(parent instanceof ComponentInstance)) {
			throw new ResoluteFailException(parent + " must be a component implementation.",
					context.getThisInstance().getSubcomponent());
		}

		if (compName.isEmpty()) {
			return new NamedElementValue(parent);
		}

		final ComponentInstance ci = (ComponentInstance) parent;
		compName = compName.replace(ci.getComponentClassifier().getQualifiedName() + ".", "");

		final ComponentInstance leaf = getLeafContainer(ci, compName);
		if (leaf != null) {
			for (ComponentInstance sub : leaf.getComponentInstances()) {
				if (sub.getName().equalsIgnoreCase(compName.substring(compName.lastIndexOf(".") + 1))) {
					return new NamedElementValue(sub);
				}
			}
		}
		throw new ResoluteFailException(
				"Could not find subcomponent " + ci.getComponentClassifier().getQualifiedName() + "." + compName,
				context.getThisInstance().getSubcomponent());
	}

	private ResoluteValue getComponentSet(String compNames) {
		return getComponentSetFrom(context.getThisInstance(), compNames);
	}

	private ResoluteValue getComponentSetFrom(NamedElement parent, String compNames) {
		if (!(parent instanceof ComponentInstance)) {
			throw new ResoluteFailException(parent + " must be a component implementation.",
					context.getThisInstance().getSubcomponent());
		}
		if (!compNames.startsWith("{") || !compNames.endsWith("}")) {
			throw new ResoluteFailException("Component names string is malformed.",
					context.getThisInstance().getSubcomponent());
		}
		final ComponentInstance ci = (ComponentInstance) parent;
		final String[] comps = compNames.replace("{", "").replace("}", "").split(",");
		final Set<NamedElementValue> setValue = new HashSet<>();
		for (String compName : comps) {
			compName = compName.replace(ci.getComponentClassifier().getQualifiedName() + ".", "");
			final ComponentInstance leaf = getLeafContainer((ComponentInstance) parent, compName);
			if (leaf != null) {
				boolean compFound = false;
				for (ComponentInstance sub : leaf.getComponentInstances()) {
					if (sub.getName().equalsIgnoreCase(compName.substring(compName.lastIndexOf(".") + 1))) {
						setValue.add(new NamedElementValue(sub));
						compFound = true;
						break;
					}
				}
				if (!compFound) {
					throw new ResoluteFailException(
							"Could not find subcomponent " + ci.getComponentClassifier().getQualifiedName() + "."
									+ compName,
							context.getThisInstance().getSubcomponent());
				}
			}
		}
		return new SetValue(setValue);
	}

	private ResoluteValue getPort(String portName) {
		return getPortFrom(context.getThisInstance(), portName);
	}

	private ResoluteValue getPortFrom(NamedElement parent, String portName) {
		if (!(parent instanceof ComponentInstance)) {
			throw new ResoluteFailException(parent + " must be a component implementation.",
					context.getThisInstance().getSubcomponent());
		}

		final ComponentInstance ci = (ComponentInstance) parent;
		portName = portName.replace(ci.getComponentClassifier().getQualifiedName() + ".", "");

		final ComponentInstance leaf = getLeafContainer(ci, portName);
		if (leaf != null) {
			for (FeatureInstance port : leaf.getFeatureInstances()) {
				if (port.getName().equalsIgnoreCase(portName.substring(portName.lastIndexOf(".") + 1))) {
					return new NamedElementValue(port);
				}
			}
		}
		throw new ResoluteFailException(
				"Could not find port " + ci.getComponentClassifier().getQualifiedName() + "." + portName,
				context.getThisInstance().getSubcomponent());
	}

	private ResoluteValue getConnection(String connName) {
		return getConnectionFrom(context.getThisInstance(), connName);
	}

	private ResoluteValue getConnectionFrom(NamedElement parent, String connName) {
		if (!(parent instanceof ComponentInstance)) {
			throw new ResoluteFailException(parent + " must be a component implementation.",
					context.getThisInstance().getSubcomponent());
		}

		final ComponentInstance ci = (ComponentInstance) parent;
		connName = connName.replace(ci.getComponentClassifier().getQualifiedName() + ".", "");

		final ComponentInstance leaf = getLeafContainer(ci, connName);
		if (leaf != null && leaf.getComponentClassifier() instanceof ComponentImplementation) {
			ComponentImplementation compImpl = (ComponentImplementation) leaf.getComponentClassifier();
			for (Connection conn : compImpl.getOwnedConnections()) {
				if (conn.getName().equalsIgnoreCase(connName.substring(connName.lastIndexOf(".") + 1))) {
					return new NamedElementValue(getConnectionInstance(leaf, conn));
				}
			}
		}
		throw new ResoluteFailException(
				"Could not find connection " + ci.getComponentClassifier().getQualifiedName() + "." + connName,
				context.getThisInstance().getSubcomponent());
	}

	private ConnectionInstance getConnectionInstance(ComponentInstance instance, NamedElement connection) {
		for (ConnectionInstance child : instance.getConnectionInstances()) {
			for (ConnectionReference ref : child.getConnectionReferences()) {
				if (ref.getConnection().equals(connection)) {
					return child;
				}
			}
		}
		throw new IllegalArgumentException("Unable to find connection " + connection.getName() + " in instance of "
				+ instance.getComponentClassifier().getName());
	}

	private ComponentInstance getLeafContainer(ComponentInstance parent, String elementName) {

		if (!elementName.contains(".")) {
			return parent;
		} else {
			for (ComponentInstance sub : parent.getComponentInstances()) {
				if (sub.getName().equalsIgnoreCase(elementName.substring(0, elementName.indexOf(".")))) {
					return getLeafContainer(sub, elementName.substring(elementName.indexOf(".") + 1));
				}
			}
		}
		return null;
	}

}
