package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Realization;

public abstract class AadlHandler extends AbstractHandler {

	abstract protected void runCommand(URI uri);

	static final String OUTLINE_VIEW_PART_ID = "org.eclipse.ui.views.ContentOutline";
	protected ExecutionEvent executionEvent;
	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) {

		this.executionEvent = event;
		window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (window == null) {
			return null;
		}

		// Save changes
		if (!saveChanges(true)) {
			return null;
		}

		// Get the current selection
		ISelection selection = null;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(OUTLINE_VIEW_PART_ID);
		if (viewPart == null) {
			selection = HandlerUtil.getCurrentSelection(event);
		} else {
			IViewSite viewSite = viewPart.getViewSite();
			ISelectionProvider selectionProvider = viewSite.getSelectionProvider();
			selection = selectionProvider.getSelection();
		}

		// TODO: Handle same functionality in the Graphical Editor?
		URI uri = getSelectionURI(selection);
		if (uri == null) {
			return null;
		}

		// Run the command in the handler
		runCommand(uri);

		return null;
	}

	/**
	 * Returns the component EObject corresponding to its URI.
	 * @param uri - Component URI
	 * @return EObject
	 */
	protected EObject getEObject(URI uri) {
		XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
		return xtextEditor.getDocument().readOnly(resource -> {
			return resource.getResourceSet().getEObject(uri, true);
		});
	}

	/**
	 * Gets the URI for the current selection in the editor
	 * @param currentSelection - Selected model object
	 * @return A URI representing the selected model object
	 */
//	@SuppressWarnings("restriction")
	private URI getSelectionURI(ISelection currentSelection) {

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection iss = (IStructuredSelection) currentSelection;
			if (iss.size() == 1) {
				final Object obj = iss.getFirstElement();
//				if (obj instanceof DiagramElement) {
//					final DiagramElement diagramElement = (DiagramElement) obj;
//					EObject eObj = (EObject) diagramElement.getBusinessObject();
//					return EcoreUtil.getURI(eObj);
//				} else {
				return ((EObjectNode) obj).getEObjectURI();
//				}
			}
		} else if (currentSelection instanceof TextSelection) {
			// Selection may be stale, get latest from editor
			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();
			return xtextEditor.getDocument().readOnly(resource -> {
				EObject e = new EObjectAtOffsetHelper().resolveContainedElementAt(resource, ts.getOffset());
				if (e instanceof Realization) {
					e = e.eContainer();
				}
				return EcoreUtil.getURI(e);
			});
		}
		return null;
	}

	protected boolean saveChanges(boolean prompt) {

		IEditorPart[] dirtyEditors = window.getActivePage().getDirtyEditors();

		if (dirtyEditors.length == 0) {
			return true;
		}

		if (prompt && !MessageDialog.openConfirm(window.getShell(), "Save editors", "Save editors and continue?")) {
			return false;
		} else {
			NullProgressMonitor monitor = new NullProgressMonitor();
			for (IEditorPart e : dirtyEditors) {
				e.doSave(monitor);
			}
			return true;
		}
	}


	/**
	 * Returns the index of a component with the specified name in the specified element list.
	 * TODO: What if the name isn't in the list?
	 * @param compName - Component name
	 * @param elements - Collection of elements
	 * @return An identifier that is unique in the specified list
	 */
	protected static int getIndex(String compName, final Collection<? extends NamedElement> elements) {
		int idx = 0;

		for (NamedElement e : elements) {
			if (e.getName().equalsIgnoreCase(compName)) {
				break;
			} else {
				idx++;
			}
		}
		return idx;
	}


//	/**
//	 * Adds the containing package of the specified named element to the specified package section's with clause if it isn't already present
//	 * @param namedElement - Named element whose containing package needs to be imported
//	 * @param pkgSection - Package Section
//	 */
//	protected void importContainingPackage(NamedElement namedElement, PackageSection pkgSection) {
//
//		AadlPackage pkg = AadlUtil.getContainingPackage(namedElement);
//		if (pkg == null) {
//			return;
//		}
//		// Don't add the with clause if it is for the current package
//		AadlPackage thisPkg = AadlUtil.getContainingPackage(pkgSection);
//		if (thisPkg == null || pkg.getName().equalsIgnoreCase(thisPkg.getName())) {
//			return;
//		}
//		boolean pkgFound = false;
//		for (ModelUnit mu : pkgSection.getImportedUnits()) {
//			if (mu.getName().equalsIgnoreCase(pkg.getName())) {
//				pkgFound = true;
//				break;
//			}
//		}
//		if (!pkgFound) {
//			pkgSection.getImportedUnits().add(pkg);
//		}
//	}

}
