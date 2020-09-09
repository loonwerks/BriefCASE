package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SystemImplementation;

public class SubcomponentSelector {

	CheckboxTreeViewer viewer = null;

	public class SubcomponentSelectorContentProvider implements ITreeContentProvider {

		Map<Subcomponent, Subcomponent> parentMap = new HashMap<>();

		public SubcomponentSelectorContentProvider(Map<Subcomponent, Subcomponent> parentMap) {
			this.parentMap = parentMap;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public boolean hasChildren(Object element) {
			Subcomponent sub = (Subcomponent) element;
			if (sub.getClassifier() instanceof SystemImplementation) {
				return sub.getComponentImplementation().getOwnedSubcomponents().size() > 0;
			}
			return false;
		}

		@Override
		public Object getParent(Object element) {
			Subcomponent sub = (Subcomponent) element;
			return parentMap.get(sub);
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (Subcomponent[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			Subcomponent sub = (Subcomponent) parentElement;
			if (sub.getClassifier() instanceof SystemImplementation) {
				ComponentImplementation compImpl = sub.getComponentImplementation();
				return compImpl.getOwnedSubcomponents().toArray();
			}
			return null;
		}
	}

	public SubcomponentSelector(Composite parent, Subcomponent sub) {

		// Create structure to map a subcomponent to its parent
		Map<Subcomponent, Subcomponent> parentMap = new HashMap<>();
		populateParentMap(sub, parentMap);

		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.heightHint = 100;

		Composite baseComposite = new Composite(parent, SWT.BORDER);
		baseComposite.setLayoutData(gridData);
		baseComposite.setLayout(new GridLayout(1, true));

		viewer = new CheckboxTreeViewer(baseComposite,
				SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new SubcomponentSelectorContentProvider(parentMap));
		viewer.getTree().setHeaderVisible(false);
		viewer.getTree().setLinesVisible(false);
		viewer.getTree().setLayoutData(gridData);
		viewer.addDoubleClickListener(event -> {
			IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
			Object selectedNode = thisSelection.getFirstElement();
			if (viewer.getChecked(selectedNode)) {
				viewer.setSubtreeChecked(selectedNode, true);
			} else {
				viewer.setSubtreeChecked(selectedNode, false);
			}
		});

		baseComposite.setBackground(viewer.getTree().getBackground());

		TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
		viewerColumn.getColumn().setWidth(300);
		viewerColumn.getColumn().setText("");
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Subcomponent sub = (Subcomponent) element;
				if (sub != null) {
					return sub.getName();
				}
				return "";
			}
		});
		Subcomponent[] subs = { sub };
		viewer.setInput(subs);
		viewer.expandAll();

	}

	public List<String> getContents() {
		List<String> contents = new ArrayList<>();
		Subcomponent root = (Subcomponent) viewer.getTree().getItem(0).getData();
		getSelectedSubcomponents(root, contents);
		return contents;
	}

	private void populateParentMap(Subcomponent sub, Map<Subcomponent, Subcomponent> parentMap) {
		if (sub.getClassifier() instanceof SystemImplementation) {
			for (Subcomponent child : sub.getComponentImplementation().getOwnedSubcomponents()) {
				parentMap.put(child, sub);
				populateParentMap(child, parentMap);
			}
		}
	}

	private void getSelectedSubcomponents(Subcomponent sub, List<String> selectedSubs) {
		SubcomponentSelectorContentProvider contentProvider = (SubcomponentSelectorContentProvider) viewer
				.getContentProvider();

		if (viewer.getChecked(sub)) {
			selectedSubs.add(getQualifiedSubcomponentName(sub));
		}
		for (Object obj : contentProvider.getChildren(sub)) {
			getSelectedSubcomponents((Subcomponent) obj, selectedSubs);
		}
	}

	private String getQualifiedSubcomponentName(Subcomponent sub) {
		String name = sub.getName();
		SubcomponentSelectorContentProvider contentProvider = (SubcomponentSelectorContentProvider) viewer
				.getContentProvider();
		Subcomponent current = sub;
		while (contentProvider.getParent(current) != null) {
			current = (Subcomponent) contentProvider.getParent(current);
			name = current.getName() + "." + name;
		}

		return name;
	}

}
