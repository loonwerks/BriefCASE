package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ListControl {

	private final static String NEW_LIST_ITEM = "New list item";

	private TableViewer tblItems;

	public enum ListItems {
		INSTANCE;

		private List<ListItem> items;

		private ListItems() {
			items = new ArrayList<ListItem>();
		}

		public List<ListItem> getItems() {
			return items;
		}

	}

	public ListControl(Composite parent, List<String> initialValues) {

		final GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		final Composite baseComposite = new Composite(parent, SWT.BORDER);
		baseComposite.setLayoutData(gridData);
		baseComposite.setLayout(new GridLayout(1, true));

		final Composite tableComposite = new Composite(baseComposite, SWT.NONE);
		gridData.heightHint = 120;
		tableComposite.setLayoutData(gridData);
		final TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		tblItems = new TableViewer(tableComposite, SWT.NO_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		final TableViewerColumn listColumn = new TableViewerColumn(tblItems, SWT.NONE);
		final TableColumn column = listColumn.getColumn();
		column.setWidth(100);
		column.setResizable(true);

		// add list column
		listColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((ListItem) cell.getElement()).getValue());
			}
		});
		listColumn.setEditingSupport(new ListEditingSupport(tblItems));

		// make lines visible and header invisible
		final Table table = tblItems.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		table.setItemCount(0);

		tblItems.setContentProvider(new ArrayContentProvider());

		// Initialize
		ListItems.INSTANCE.getItems().clear();
		if (initialValues != null) {
			for (String s : initialValues) {
				ListItems.INSTANCE.getItems().add(new ListItem(s));
			}
		}
		tblItems.setInput(ListItems.INSTANCE.getItems());

		// Layout the viewer
		tableColumnLayout.setColumnData(listColumn.getColumn(), new ColumnWeightData(55, 100, true));

		// Add / Remove buttons
		final Composite btnComposite = new Composite(baseComposite, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		btnComposite.setLayout(new GridLayout(2, true));

		final Button btnAdd = new Button(btnComposite, SWT.PUSH);
		btnAdd.setText("Add");
		btnAdd.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				ListItems.INSTANCE.getItems().add(new ListItem(NEW_LIST_ITEM));
				tblItems.refresh();
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		final Button btnRemove = new Button(btnComposite, SWT.PUSH);
		btnRemove.setText("Remove");
		btnRemove.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				final IStructuredSelection selection = tblItems.getStructuredSelection();
				@SuppressWarnings("unchecked")
				final Iterator<ListItem> iterator = selection.iterator();
				while (iterator.hasNext()) {
					ListItem item = iterator.next();
					ListItems.INSTANCE.getItems().remove(item);
				}
				tblItems.refresh();
			}
		});
		btnRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
	}

	public List<String> getContents() {
		final List<String> contents = new ArrayList<>();
		ListItems.INSTANCE.getItems().forEach(i -> contents.add(i.getValue()));
		return contents;
	}

	public void addFocusListener(FocusListener focusListener) {

	}

	public class ListItem {

		private String value = "";
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public ListItem() {

		}

		public ListItem(String value) {
			this.value = value;
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
		}

		@Override
		public String toString() {
			return value;
		}

	}

	public class ListEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public ListEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((ListItem) element).getValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			((ListItem) element).setValue(String.valueOf(value));
			viewer.update(element, null);
		}
	}

}
