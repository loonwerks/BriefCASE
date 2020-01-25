package com.collins.trustedsystems.briefcase.staircase.handlers;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.SystemImplementation;
import org.osate.ui.dialogs.Dialog;

public class Sel4TransformHandler extends AadlHandler {

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
		if (selectedSystem.getOwnedProcessSubcomponents().isEmpty()) {
			Dialog.showError("seL4 Transform",
					"A system implementation containing at least one process must be selected to perform the seL4 transform.");
			return;
		}

		// Each process subcomponent becomes a system subcomponent

		// Each process thread becomes a process containing the thread

		// Re-wire everything appropriately

		// Change processor bindings

		// Set processor OS property to seL4

	}

}
