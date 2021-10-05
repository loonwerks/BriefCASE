package com.collins.trustedsystems.briefcase.staircase.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Adapters;
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
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Realization;
import org.osate.ge.BusinessObjectSelection;
import org.osate.ui.dialogs.Dialog;

public abstract class AadlHandler extends AbstractHandler {

	abstract protected void runCommand(EObject eObj);

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

		final URI uri = getSelectionURI(selection);
		if (uri == null) {
			Dialog.showError("BriefCASE", "A model element must be selected to perform this action.");
			return null;
		}

		// Run the command in the handler
		runCommand(getEObject(uri));

		return null;
	}

	/**
	 * Returns the component EObject corresponding to its URI.
	 * @param uri - Component URI
	 * @return EObject
	 */
	protected EObject getEObject(URI uri) {

		final XtextResourceSet resourceSet = new XtextResourceSet();
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		return resourceSet.getEObject(uri, true);

	}


	/**
	 * Gets the URI for the current selection in the editor
	 * @param currentSelection - Selected model object
	 * @return A URI representing the selected model object
	 */
	private URI getSelectionURI(ISelection currentSelection) {

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection iss = (IStructuredSelection) currentSelection;

			if (iss.size() == 1 && iss.getFirstElement() instanceof EObjectNode) {
				final EObjectNode node = (EObjectNode) iss.getFirstElement();
				return node.getEObjectURI();
			} else {
				final BusinessObjectSelection bos = Adapters.adapt(currentSelection, BusinessObjectSelection.class);
				if (bos != null) {
					if (bos.boStream(EObject.class).count() == 1) {
						return bos.boStream(EObject.class).findFirst().map(e -> EcoreUtil.getURI(e)).orElse(null);
					}
				}
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

		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
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

				final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null) {
					return null;
				}
				for (IEditorReference ref : window.getActivePage().getEditorReferences()) {
					final IEditorPart editor = ref.getEditor(false);
					if (editor == null) {
						continue;
					} else if (editor instanceof XtextEditor) {
						((SourceViewer) ((XtextEditor) editor).getInternalSourceViewer())
							.doOperation(ISourceViewer.FORMAT);
//					} else if (editor instanceof AgeDiagramEditor) {
//						AgeDiagramEditor diagramEditor = (AgeDiagramEditor) editor;
//						AgeDiagram diagram = diagramEditor.getDiagram();
//						final List<DiagramElement> diagramElements = diagram.getAllDiagramNodes()
//								.filter(DiagramElement.class::isInstance)
//								.map(DiagramElement.class::cast)
//								.collect(Collectors.toList());
//						diagramEditor.selectDiagramElements(diagramElements);
//
//						ICommandService commandService = window.getActivePage()
//								.getActiveEditor()
//								.getSite()
//								.getService(ICommandService.class);
//						IHandlerService handlerService = window.getActivePage()
//								.getActiveEditor()
//								.getSite()
//								.getService(IHandlerService.class);
//						Command generateCmd = commandService.getCommand("org.osate.ge.showDefaultContents");
//						ExecutionEvent executionEvent = handlerService.createExecutionEvent(generateCmd, new Event());
//						((IEvaluationContext) executionEvent.getApplicationContext())
//								.addVariable(ISources.ACTIVE_EDITOR_NAME, editor);
//						try {
//							generateCmd.executeWithChecks(executionEvent);
//						} catch (Exception e) {
//
//						}
//
//						generateCmd = commandService.getCommand("org.osate.ge.layoutContents");
//						executionEvent = handlerService.createExecutionEvent(generateCmd, new Event());
//						((IEvaluationContext) executionEvent.getApplicationContext())
//								.addVariable(ISources.ACTIVE_EDITOR_NAME, editor);
//						try {
//							generateCmd.executeWithChecks(executionEvent);
//						} catch (Exception e) {
//
//						}
//
//						diagramEditor.clearSelection();
					}
					if (saveAfterFormat && editor.isDirty()) {
						editor.doSave(new NullProgressMonitor());
					}
				}

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

//	/**
//	 * Adds contents to the selected diagram elements. Adds all children which pass the specified filter.
//	 * @param event is the ExecutionEvent of the handler which provides the active editor.
//	 * @param filter is passed the parent diagram element and the business object of the potential child. If the filter returns true, then an element will be added for the business object.
//	 */
//	public static void showDefaultContents(final AgeDiagramEditor diagramEditor) {
//
//		if (!(diagramEditor instanceof AgeDiagramEditor)) {
//			throw new RuntimeException("Unexpected editor: " + diagramEditor);
//		}
//		final AgeDiagram diagram = diagramEditor.getDiagram();
//		if (diagram == null) {
//			throw new RuntimeException("Unable to retrieve diagram");
//		}
//		final List<DiagramElement> selectedDiagramElements = diagram.getAllDescendants()
//				.filter(d -> d instanceof DiagramElement)
//				.map(d -> (DiagramElement) d)
//				.collect(Collectors.toList());
//
//		final Bundle bundle = FrameworkUtil.getBundle(ShowDefaultContentsHandler.class);
//		final ExtensionRegistryService contentFilterProvider = Objects.requireNonNull(
//				EclipseContextFactory.getServiceContext(bundle.getBundleContext()).get(ExtensionRegistryService.class),
//				"Unable to retrieve extension registry");
//
//		final BiFunction<DiagramElement, Object, Boolean> filter = (parentElement, childBo) -> ContentFilterUtil
//				.passesAnyContentFilter(childBo,
//						DiagramTypeUtil.getApplicableDefaultContentFilters(diagram.getConfiguration().getDiagramType(),
//								parentElement.getBusinessObject(), contentFilterProvider));
//
//		final ExtensionRegistryService extService = Objects.requireNonNull(
//				Adapters.adapt(diagramEditor, ExtensionRegistryService.class), "Unable to retrieve extension service");
//		final AgeFeatureProvider featureProvider = Objects.requireNonNull(
//				(AgeFeatureProvider) diagramEditor.getDiagramTypeProvider().getFeatureProvider(),
//				"Unable to retrieve feature provider");
//		final ActionService actionService = Objects.requireNonNull(Adapters.adapt(diagramEditor, ActionService.class),
//				"Unable to retrieve action service");
//
//		final DiagramUpdater diagramUpdater = Objects.requireNonNull(featureProvider.getDiagramUpdater(),
//				"Unable to retrieve diagram updater");
//
//		final ReferenceBuilderService referenceBuilder = Objects.requireNonNull(
//				Adapters.adapt(diagramEditor, ReferenceBuilderService.class),
//				"Unable to retrieve reference builder service");
//
//
//		boolean childrenAdded = false;
//		final BusinessObjectProviderHelper bopHelper = new BusinessObjectProviderHelper(extService);
//		for (final DiagramElement selectedElement : selectedDiagramElements) {
//			for (final Object childBo : bopHelper.getChildBusinessObjects(selectedElement)) {
//				final RelativeBusinessObjectReference relativeReference = referenceBuilder
//						.getRelativeReference(childBo);
//
//				if (relativeReference != null && selectedElement.getByRelativeReference(relativeReference) == null) {
//					if (filter.apply(selectedElement, childBo)) {
//						diagramUpdater.addToNextUpdate(selectedElement, relativeReference, new FutureElementInfo());
//						childrenAdded = true;
//					}
//				}
//			}
//		}
//
//		if (childrenAdded) {
//			actionService.execute("Show Contents", ExecutionMode.NORMAL, () -> {
//				// Update the diagram
//				final IUpdateContext updateCtx = new UpdateContext(
//						diagramEditor.getGraphitiAgeDiagram().getGraphitiDiagram());
//				diagramEditor.getDiagramBehavior()
//						.executeFeature(featureProvider.getUpdateFeature(updateCtx), updateCtx);
//
//				return null;
//			});
//		}
//
//	}

}
