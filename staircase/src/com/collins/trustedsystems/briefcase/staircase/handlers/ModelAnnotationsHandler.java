package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.SaveOptions;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertyValue;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.ModelAnnotationsDialog;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class ModelAnnotationsHandler extends AadlHandler {

	private Map<Property, PropertyExpression> annotations;

	@Override
	protected void runCommand(EObject eObj) {
		// Check that it is a component type
		if (!(eObj instanceof NamedElement)) {
			Dialog.showError("Model Annotations", "Select an AADL Named Element to annotate");
			return;
		}
		final NamedElement element = (NamedElement) eObj;

		final ModelAnnotationsDialog wizard = new ModelAnnotationsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create(element);
		if (wizard.open() == Window.OK) {
			annotations = wizard.getAnnotations();
		} else {
			return;
		}

		if (setAnnotations(element)) {
			BriefcaseNotifier.notify("Model Annotations", "Model annotations complete.");
			format(true);
		} else {
			BriefcaseNotifier.printError("Model annotation failed");
		}

	}

	private boolean setAnnotations(NamedElement element) {

		final Resource aadlResource = element.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				for (Map.Entry<Property, PropertyExpression> entry : annotations.entrySet()) {
					if (element.acceptsProperty(entry.getKey())) {
						if (entry.getValue() instanceof PropertyValue) {
							element.setPropertyValue(entry.getKey(), (PropertyValue) entry.getValue());
						}
					}
				}
			}
		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
