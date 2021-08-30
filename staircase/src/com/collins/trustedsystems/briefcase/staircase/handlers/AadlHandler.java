package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
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
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Realization;
import org.osate.ui.dialogs.Dialog;

public abstract class AadlHandler extends AbstractHandler {

	abstract protected void runCommand(URI uri);

	static final String OUTLINE_VIEW_PART_ID = "org.eclipse.ui.views.ContentOutline";
	protected ExecutionEvent executionEvent;

	@Override
	public Object execute(ExecutionEvent event) {

		this.executionEvent = event;

		// Save changes
		if (!saveChanges(true)) {
			return null;
		}

		// Get the current selection
		ISelection selection = null;
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final IViewPart viewPart = page.findView(OUTLINE_VIEW_PART_ID);
		if (viewPart == null) {
			selection = HandlerUtil.getCurrentSelection(event);
		} else {
			final IViewSite viewSite = viewPart.getViewSite();
			final ISelectionProvider selectionProvider = viewSite.getSelectionProvider();
			selection = selectionProvider.getSelection();
		}

		// TODO: Handle same functionality in the Graphical Editor?
		final URI uri = getSelectionURI(selection);
		if (uri == null) {
			Dialog.showError("BriefCASE", "A model element must be selected to perform this action.");
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
		final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
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
			final XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			final TextSelection ts = (TextSelection) xtextEditor.getSelectionProvider().getSelection();
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

	protected static boolean saveChanges(boolean prompt) {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return false;
		}
		final IEditorPart[] dirtyEditors = window.getActivePage().getDirtyEditors();

		if (dirtyEditors.length == 0) {
			return true;
		}

		if (prompt && !MessageDialog.openConfirm(window.getShell(), "Save editors", "Save editors and continue?")) {
			return false;
		} else {
			for (IEditorPart e : dirtyEditors) {
//				final ITextOperationTarget fOperationTarget = e.getAdapter(ITextOperationTarget.class);
//				fOperationTarget.doOperation(ISourceViewer.FORMAT);
//				((SourceViewer) ((XtextEditor) e).getInternalSourceViewer())
//				((SourceViewer) EditorUtils.getActiveXtextEditor().getInternalSourceViewer())
//						.doOperation(ISourceViewer.FORMAT);
				e.doSave(new NullProgressMonitor());
			}
			return true;
		}
	}

	/**
	 * Formats files (Shift-Ctrl-F) since for some reason the xtext formatter is doing that
	 */
	public static void format(boolean saveAfterFormat) {
		WorkbenchJob refreshJob = new WorkbenchJob("Format") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {

				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null) {
					return null;
				}
				final IEditorPart[] dirtyEditors = window.getActivePage().getDirtyEditors();
				for (IEditorPart e : dirtyEditors) {
//					final ITextOperationTarget fOperationTarget = e.getAdapter(ITextOperationTarget.class);
//					fOperationTarget.doOperation(ISourceViewer.FORMAT);
					if (e instanceof XtextEditor) {
						((SourceViewer) ((XtextEditor) e).getInternalSourceViewer())
//					((SourceViewer) EditorUtils.getActiveXtextEditor().getInternalSourceViewer())
							.doOperation(ISourceViewer.FORMAT);
						e.doSave(new NullProgressMonitor());
					}
				}
//				((SourceViewer) EditorUtils.getActiveXtextEditor().getInternalSourceViewer())
//						.doOperation(ISourceViewer.FORMAT);
//				if (saveAfterFormat) {
//					saveChanges(false);
//				}
				return Status.OK_STATUS;
			}
		};
		refreshJob.schedule(500);
	}


	/**
	 * Returns the index of a component with the specified name in the specified element list.
	 * @param compName - Component name
	 * @param elements - Collection of elements
	 * @return An identifier that is unique in the specified list
	 * Throws exception if name is not in list
	 */
	protected static int getIndex(String compName, final Collection<? extends NamedElement> elements) {
		int idx = 0;
		boolean found = false;

		for (NamedElement e : elements) {
			if (e.getName().equalsIgnoreCase(compName)) {
				found = true;
				break;
			} else {
				++idx;
			}
		}
		if (!found) {
			throw new IndexOutOfBoundsException();
		}
		return idx;
	}

}
