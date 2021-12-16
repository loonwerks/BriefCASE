package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.SaveOptions;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.StringLiteral;
import org.osate.ui.dialogs.Dialog;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.ProgrammingProperties;

public class SelectImplementationHandler extends AadlHandler {

	private String legacyComponentImplementationLocation = "";

	@Override
	public void runCommand(EObject eObj) {

		// Make sure selection is a process or thread
		if (!(eObj instanceof ComponentImplementation)) {
			Dialog.showError("No software component implementation is selected",
					"A component implementation must be selected to assign an implementation source.");
			return;
		}

		// Get location of legacy source or binary
		final FileDialog dlgImplementationLocation = new FileDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dlgImplementationLocation.setText("Select Implementation");
		dlgImplementationLocation.open();
		legacyComponentImplementationLocation = dlgImplementationLocation.getFilterPath();
		if (!legacyComponentImplementationLocation.isEmpty()) {
			legacyComponentImplementationLocation += "/";
		}
		legacyComponentImplementationLocation += dlgImplementationLocation.getFileName();
		legacyComponentImplementationLocation = legacyComponentImplementationLocation.replace("\\", "/");

		// Add Legacy Component Properties
		addLegacyComponentImplementation((ComponentImplementation) eObj);

		return;
	}

	/**
	 * Adds the properties to the selected component that specify it
	 * is a legacy implementation.
	 * Also adds a resolute clause for checking that the legacy component
	 * verification was performed on the latest version of the implementation
	 * @param uri - The URI of the selected component
	 */
	private void addLegacyComponentImplementation(ComponentImplementation selectedComponent) {

		final Resource aadlResource = selectedComponent.eResource();
		final TransactionalEditingDomain domain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();

		// We execute this command on the command stack because otherwise, we will not
		// have write permissions on the editing domain.
		final Command cmd = new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				final Property sourceTextProp = GetProperties.lookupPropertyDefinition(selectedComponent,
						ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);
				final StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
				sourceTextLit.setValue(legacyComponentImplementationLocation);
				final List<StringLiteral> listVal = new ArrayList<>();
				listVal.add(sourceTextLit);
				selectedComponent.setPropertyValue(sourceTextProp, listVal);
			}

		};

		try {
			((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);

			// We're done: Save the model
			aadlResource.save(SaveOptions.newBuilder().format().getOptions().toOptionsMap());
		} catch (Exception e) {
			return;
		} finally {
			domain.dispose();
		}

//		// Get the active xtext editor so we can make modifications
//		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
//
//		xtextEditor.getDocument().modify(new IUnitOfWork.Void<XtextResource>() {
//
//			@Override
//			public void process(final XtextResource resource) throws Exception {
//
//				// Retrieve the model object to modify
//				final ComponentImplementation selectedComponent = (ComponentImplementation) resource
//						.getEObject(uri.fragment());
//				final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
//				PackageSection pkgSection = null;
//				// Figure out if the selected component is in the public or private section
//				EObject eObj = selectedComponent.eContainer();
//				while (eObj != null) {
//					if (eObj instanceof PublicPackageSection) {
//						pkgSection = aadlPkg.getOwnedPublicSection();
//						break;
//					} else if (eObj instanceof PrivatePackageSection) {
//						pkgSection = aadlPkg.getOwnedPrivateSection();
//						break;
//					} else {
//						eObj = eObj.eContainer();
//					}
//				}
//
//				if (pkgSection == null) {
//					// Something went wrong
//					Dialog.showError("No package section found", "No public or private package sections found.");
//					return;
//				}
//
//				final Property sourceTextProp = GetProperties.lookupPropertyDefinition(selectedComponent,
//						ProgrammingProperties._NAME, ProgrammingProperties.SOURCE_TEXT);
//				final StringLiteral sourceTextLit = Aadl2Factory.eINSTANCE.createStringLiteral();
//				sourceTextLit.setValue(legacyComponentImplementationLocation);
//				final List<StringLiteral> listVal = new ArrayList<>();
//				listVal.add(sourceTextLit);
//				selectedComponent.setPropertyValue(sourceTextProp, listVal);
//
//			}
//		});

		return;
	}

}
