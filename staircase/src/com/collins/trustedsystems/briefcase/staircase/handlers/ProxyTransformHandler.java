package com.collins.trustedsystems.briefcase.staircase.handlers;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.PrivatePackageSection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.SystemImplementation;
import org.osate.ui.dialogs.Dialog;

import com.collins.trustedsystems.briefcase.staircase.dialogs.ProxyTransformDialog;
import com.collins.trustedsystems.briefcase.staircase.requirements.AddProxyClaim;
import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class ProxyTransformHandler extends AadlHandler {

	public static final String HIGH_PROXY_COMP_TYPE_NAME = "CASE_HighProxy";
	public static final String LOW_PROXY_COMP_TYPE_NAME = "CASE_LowProxy";
	public static final String HIGH_PROXY_SUBCOMP_NAME = "HighProxy";
	public static final String LOW_PROXY_SUBCOMP_NAME = "HighProxy";
	static final String CONNECTION_IMPL_NAME = "c";

	private String highProxyComponentName;
	private String highProxySubcomponentName;
	private String lowProxyComponentName;
	private String lowProxySubcomponentName;
	private String dispatchProtocol;
	private String period;
	private String proxyRequirement;

	@Override
	protected void runCommand(URI uri) {

		// Check if it is a component implementation
		final EObject eObj = getEObject(uri);
		if (!(eObj instanceof SystemImplementation || eObj instanceof ProcessImplementation)) {
			Dialog.showError("Add Proxy", "A system or process implementation must be selected to add a proxy.");
			return;
		}
		final ComponentImplementation compImpl = (ComponentImplementation) eObj;

		// Open Proxy Transform wizard to get proxy info
		final ProxyTransformDialog wizard = new ProxyTransformDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		wizard.create(compImpl);
		if (wizard.open() == Window.OK) {
			highProxyComponentName = wizard.getHighProxyComponentName();
			if (highProxyComponentName.isEmpty()) {
				highProxyComponentName = HIGH_PROXY_COMP_TYPE_NAME;
			}
			highProxySubcomponentName = wizard.getHighProxySubcomponentName();
			if (highProxySubcomponentName.isEmpty()) {
				highProxySubcomponentName = HIGH_PROXY_SUBCOMP_NAME;
			}
			lowProxyComponentName = wizard.getLowProxyComponentName();
			if (lowProxyComponentName.isEmpty()) {
				lowProxyComponentName = LOW_PROXY_COMP_TYPE_NAME;
			}
			lowProxySubcomponentName = wizard.getLowProxySubcomponentName();
			if (lowProxySubcomponentName.isEmpty()) {
				lowProxySubcomponentName = LOW_PROXY_SUBCOMP_NAME;
			}
			dispatchProtocol = wizard.getDispatchProtocol();
			period = wizard.getPeriod();
			proxyRequirement = wizard.getRequirement();
		} else {
			return;
		}

		// Insert the proxy components
		insertProxy(uri);
		BriefcaseNotifier.notify("StairCASE - Proxy", "Proxy added to model.");

		// Save
		saveChanges(false);

		return;

	}

	/**
	 * Inserts a proxy component into the model.
	 * @param uri - The URI of the selected component implementation that will contain the proxy
	 */
	private void insertProxy(URI uri) {

		// Get the active xtext editor so we can make modifications
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();

		final AddProxyClaim claim = xtextEditor.getDocument().modify(resource -> {

			// Retrieve the model object to modify
			final ComponentImplementation compImpl = (ComponentImplementation) resource.getEObject(uri.fragment());

			final AadlPackage aadlPkg = (AadlPackage) resource.getContents().get(0);
			PackageSection pkgSection = null;
			// Figure out if the comm driver's containing implementation is in the public or private section
			EObject eObj = compImpl.eContainer();
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
				Dialog.showError("Proxy Transform", "No public or private package sections found.");
				return null;
			}

			// Import CASE_Properties file
			if (!CasePropertyUtils.addCasePropertyImport(pkgSection)) {
				return null;
			}
//			// Import CASE_Model_Transformations file
//			if (!CaseUtils.addCaseModelTransformationsImport(pkgSection, true)) {
//				return null;
//			}

//			ComponentCategory compCategory = compImpl.getCategory();
//			// If the component type is a process, we will need to put a single thread inside.
//			// Per convention, we will attach all properties and contracts to the thread.
//			// For this model transformation, we will create the thread first, then wrap it in a process
//			// component, using the same mechanism we use for the seL4 transformation
//			final boolean isProcess = (compCategory == ComponentCategory.PROCESS);
//			if (isProcess) {
//				compCategory = ComponentCategory.THREAD;
//			}

			return null;
		});
	}

}
