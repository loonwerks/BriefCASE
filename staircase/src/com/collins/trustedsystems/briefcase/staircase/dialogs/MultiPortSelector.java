package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class MultiPortSelector {

	private TableViewer tblPorts;
	private List<String> connectionEnds;
	private String basePortName = "port";

	private static final String NO_PORT_SELECTED = "<No port selected>";

	public enum PortConnectionItems {
		INSTANCE;

		private List<PortConnectionItem> ports;

		private PortConnectionItems() {
			ports = new ArrayList<PortConnectionItem>();
		}

		public List<PortConnectionItem> getPorts() {
			return ports;
		}

	}

	public MultiPortSelector(Composite parent, List<String> ports, String basePortName) {

		this.connectionEnds = ports;
		if (basePortName != null && !basePortName.isEmpty()) {
			this.basePortName = basePortName;
		}

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Composite baseComposite = new Composite(parent, SWT.BORDER);
		baseComposite.setLayoutData(gridData);
		baseComposite.setLayout(new GridLayout(1, true));

		Composite tableComposite = new Composite(baseComposite, SWT.NONE);
		gridData.heightHint = 120;
		tableComposite.setLayoutData(gridData);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		tblPorts = new TableViewer(tableComposite, SWT.NO_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// add port name column
		TableViewerColumn colPortName = createTableViewerColumn("Port Name", 100, 0);
		colPortName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((PortConnectionItem) cell.getElement()).getPortName());
			}
		});
		colPortName.setEditingSupport(new PortNameEditingSupport(tblPorts));

		// Add the connection end column
		TableViewerColumn colConnectionEnd = createTableViewerColumn("Connection End", 100, 1);
		colConnectionEnd.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PortConnectionItem p = (PortConnectionItem) element;
				return p.getConnectionEnd();
			}
		});
		colConnectionEnd.setEditingSupport(new ConnectionEndEditingSupport(tblPorts));

		// make lines visible and header invisible
		final Table table = tblPorts.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
//		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true);
//		gd_table.heightHint = 60;
//		table.setLayoutData(gd_table);

		tblPorts.setContentProvider(new ArrayContentProvider());

		// Initialize
		PortConnectionItems.INSTANCE.getPorts().clear();
		PortConnectionItems.INSTANCE.getPorts().add(new PortConnectionItem(this.basePortName + "_1", NO_PORT_SELECTED));
		tblPorts.setInput(PortConnectionItems.INSTANCE.getPorts());

		// Layout the viewer
		tableColumnLayout.setColumnData(colPortName.getColumn(), new ColumnWeightData(30, 100, true));
		tableColumnLayout.setColumnData(colConnectionEnd.getColumn(), new ColumnWeightData(70, 100, true));

		// Add / Remove buttons
		Composite btnComposite = new Composite(baseComposite, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		btnComposite.setLayout(new GridLayout(2, true));

		Button btnAdd = new Button(btnComposite, SWT.PUSH);
		btnAdd.setText("Add port");
		btnAdd.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				int i = tblPorts.getTable().getItemCount() + 1;
				PortConnectionItems.INSTANCE.getPorts()
						.add(new PortConnectionItem(this.basePortName + "_" + i, NO_PORT_SELECTED));
				tblPorts.refresh();
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		Button btnRemove = new Button(btnComposite, SWT.PUSH);
		btnRemove.setText("Remove port");
		btnRemove.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection && PortConnectionItems.INSTANCE.getPorts().size() > 1) {
				IStructuredSelection selection = tblPorts.getStructuredSelection();
				@SuppressWarnings("unchecked")
				Iterator<PortConnectionItem> iterator = selection.iterator();
				while (iterator.hasNext()) {
					PortConnectionItem port = iterator.next();
					PortConnectionItems.INSTANCE.getPorts().remove(port);
				}
				tblPorts.refresh();
			}
		});
		btnRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tblPorts, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;

	}

	public Map<String, String> getContents() {
		Map<String, String> contents = new HashMap<>();
		PortConnectionItems.INSTANCE.getPorts().forEach(p -> contents.put(p.getPortName(), p.getConnectionEnd()));
		return contents;
	}

	public class PortConnectionItem {

		private String portName = "";
		private String connectionEnd = "";
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public PortConnectionItem() {

		}

		public PortConnectionItem(String portName, String connectionEnd) {
			this.portName = portName;
			this.connectionEnd = connectionEnd;
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}

		public String getPortName() {
			return portName;
		}

		public String getConnectionEnd() {
			return connectionEnd;
		}

		public void setPortName(String portName) {
			propertyChangeSupport.firePropertyChange("portName", this.portName, this.portName = portName);
		}

		public void setConnectionEnd(String connectionEnd) {
			propertyChangeSupport.firePropertyChange("connectionEnd", this.connectionEnd,
					this.connectionEnd = connectionEnd);
		}

		@Override
		public String toString() {
			return portName + " : " + connectionEnd;
		}

	}

	public class PortNameEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public PortNameEditingSupport(TableViewer viewer) {
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
			return ((PortConnectionItem) element).getPortName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			((PortConnectionItem) element).setPortName(String.valueOf(value));
			viewer.update(element, null);
		}
	}

	public class ConnectionEndEditingSupport extends EditingSupport {

		private final TableViewer viewer;

		public ConnectionEndEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new ComboBoxCellEditor(viewer.getTable(), connectionEnds.toArray(new String[0]));
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			PortConnectionItem p = (PortConnectionItem) element;
			int idx = connectionEnds.indexOf(p.getConnectionEnd());
			if (idx < 0) {
				idx = 0;
			}
			return idx;
		}

		@Override
		protected void setValue(Object element, Object value) {
			PortConnectionItem p = (PortConnectionItem) element;
			int idx = (Integer) value;
			if (idx < 0) {
				idx = 0;
			}
			p.setConnectionEnd(connectionEnds.get(idx));
			viewer.update(element, null);
		}
	}
}