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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class PortSelectorControl {

	private TableViewer tblPorts;
	private List<String> connectionEnds;
	private List<PortConnectionItem> portConnectionItems = new ArrayList<PortConnectionItem>();

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

	public enum PortDirection {
		INPUT, OUTPUT;
	}

	/**
	 *
	 * @param parent - Parent container for port selector
	 * @param portDirection - Direction of connections
	 * @param ports - List of ports (<subcomponent>.<port>) to populate combo box
	 */
	public PortSelectorControl(Composite parent, PortDirection portDirection, List<String> ports) {

		this.connectionEnds = ports;

		final GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		final Composite baseComposite = new Composite(parent, SWT.BORDER);
		baseComposite.setLayoutData(gridData);
		baseComposite.setLayout(new GridLayout(1, true));

		final Composite tableComposite = new Composite(baseComposite, SWT.NONE);
		gridData.heightHint = 250;
		tableComposite.setLayoutData(gridData);
		final TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		tblPorts = new TableViewer(tableComposite, SWT.NO_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn colConnectionEnd = null;
		TableViewerColumn colInputPortName = null;
		TableViewerColumn colOutputPortName = null;

		if (portDirection == PortDirection.INPUT) {
			colConnectionEnd = createTableViewerColumn("Source Port", 100);
			colInputPortName = createTableViewerColumn("Input Port", 100);
			colOutputPortName = createTableViewerColumn("Output Port", 100);
		} else {
			colInputPortName = createTableViewerColumn("Input Port", 100);
			colOutputPortName = createTableViewerColumn("Output Port", 100);
			colConnectionEnd = createTableViewerColumn("Destination Port", 100);
		}

		// Add the connection end column
		colConnectionEnd.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PortConnectionItem p = (PortConnectionItem) element;
				return p.getConnectionEnd();
			}
		});
		colConnectionEnd.setEditingSupport(new ConnectionEndEditingSupport(tblPorts));

		// add input port name column
		colInputPortName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((PortConnectionItem) cell.getElement()).getInputPortName());
			}
		});
		colInputPortName.setEditingSupport(new PortNameEditingSupport(tblPorts, PortDirection.INPUT));

		// add output port name column
		colOutputPortName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((PortConnectionItem) cell.getElement()).getOutputPortName());
			}
		});
		colOutputPortName.setEditingSupport(new PortNameEditingSupport(tblPorts, PortDirection.OUTPUT));

		// make lines visible and header invisible
		final Table table = tblPorts.getTable();
		table.setHeaderVisible(true);
		table.setHeaderBackground(new Color(Display.getDefault(), 220, 220, 220));
		table.setLinesVisible(true);

		tblPorts.setContentProvider(new ArrayContentProvider());

		// Initialize
//		PortConnectionItems.INSTANCE.getPorts().clear();
		portConnectionItems.clear();
//		if (initPort != null) {
//			String iPort = initPort;
//			if (iPort.isEmpty()) {
//				iPort = NO_PORT_SELECTED;
//			}
//			PortConnectionItems.INSTANCE.getPorts().add(new PortConnectionItem("", "", NO_PORT_SELECTED));
//		}
//		tblPorts.setInput(PortConnectionItems.INSTANCE.getPorts());
		tblPorts.setInput(portConnectionItems);

		// Layout the viewer
		tableColumnLayout.setColumnData(colInputPortName.getColumn(), new ColumnWeightData(35, 100, true));
		tableColumnLayout.setColumnData(colOutputPortName.getColumn(), new ColumnWeightData(35, 100, true));
		tableColumnLayout.setColumnData(colConnectionEnd.getColumn(), new ColumnWeightData(30, 100, true));

		// Add / Remove buttons
		final Composite btnComposite = new Composite(baseComposite, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		btnComposite.setLayout(new GridLayout(2, true));

		final Button btnAdd = new Button(btnComposite, SWT.PUSH);
		btnAdd.setText("Add port");
		btnAdd.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
//				PortConnectionItems.INSTANCE.getPorts()
//						.add(new PortConnectionItem("", "", NO_PORT_SELECTED));
				portConnectionItems.add(new PortConnectionItem("", "", NO_PORT_SELECTED));
				tblPorts.refresh();
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		final Button btnRemove = new Button(btnComposite, SWT.PUSH);
		btnRemove.setText("Remove port");
		btnRemove.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				final IStructuredSelection selection = tblPorts.getStructuredSelection();
				@SuppressWarnings("unchecked")
				final Iterator<PortConnectionItem> iterator = selection.iterator();
				while (iterator.hasNext()) {
					PortConnectionItem port = iterator.next();
//					PortConnectionItems.INSTANCE.getPorts().remove(port);
					portConnectionItems.remove(port);
				}
				tblPorts.refresh();
			}
		});
		btnRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tblPorts, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;

	}

//	public Map<String, String> getContents() {
//		final Map<String, String> contents = new HashMap<>();
//		PortConnectionItems.INSTANCE.getPorts().forEach(p -> contents.put(p.getPortName(), p.getConnectionEnd()));
//		return contents;
//	}
	public Map<String, List<String>> getContents() {
		final Map<String, List<String>> contents = new HashMap<>();
//		for (PortConnectionItem item : PortConnectionItems.INSTANCE.getPorts()) {
		for (PortConnectionItem item : portConnectionItems) {
			final List<String> portNames = new ArrayList<>();
			portNames.add(item.getInputPortName());
			portNames.add(item.getOutputPortName());
			contents.put(item.getConnectionEnd(), portNames);
		}
		return contents;
	}

	public class PortConnectionItem {

		private String inputPortName = "";
		private String outputPortName = "";
		private String connectionEnd = "";
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public PortConnectionItem() {

		}

		public PortConnectionItem(String inputPortName, String outputPortName, String connectionEnd) {
			this.inputPortName = inputPortName;
			this.outputPortName = outputPortName;
			this.connectionEnd = connectionEnd;
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}

		public String getInputPortName() {
			return inputPortName;
		}

		public String getOutputPortName() {
			return outputPortName;
		}

		public String getConnectionEnd() {
			return connectionEnd;
		}

		public void setInputPortName(String inputPortName) {
			propertyChangeSupport.firePropertyChange("inputPortName", this.inputPortName,
					this.inputPortName = inputPortName);
		}

		public void setOutputPortName(String outputPortName) {
			propertyChangeSupport.firePropertyChange("outputPortName", this.outputPortName,
					this.outputPortName = outputPortName);
		}

		public void setConnectionEnd(String connectionEnd) {
			propertyChangeSupport.firePropertyChange("connectionEnd", this.connectionEnd,
					this.connectionEnd = connectionEnd);
		}

		@Override
		public String toString() {
			return connectionEnd + " : " + inputPortName + " : " + outputPortName;
		}

	}

	public class PortNameEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;
		private final PortDirection portDirection;

		public PortNameEditingSupport(TableViewer viewer, PortDirection portDirection) {
			super(viewer);
			this.viewer = viewer;
			this.editor = new TextCellEditor(viewer.getTable());
			this.portDirection = portDirection;
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
			if (portDirection == PortDirection.INPUT) {
				return ((PortConnectionItem) element).getInputPortName();
			} else {
				return ((PortConnectionItem) element).getOutputPortName();
			}
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (portDirection == PortDirection.INPUT) {
				((PortConnectionItem) element).setInputPortName(String.valueOf(value));
			} else {
				((PortConnectionItem) element).setOutputPortName(String.valueOf(value));
			}
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
			final PortConnectionItem p = (PortConnectionItem) element;
			int idx = connectionEnds.indexOf(p.getConnectionEnd());
			if (idx < 0) {
				idx = 0;
			}
			return idx;
		}

		@Override
		protected void setValue(Object element, Object value) {
			final PortConnectionItem p = (PortConnectionItem) element;
			int idx = (Integer) value;
			if (idx < 0) {
				idx = 0;
			}

			p.setInputPortName(connectionEnds.get(idx) + "_in");
			p.setOutputPortName(connectionEnds.get(idx) + "_out");
			p.setConnectionEnd(connectionEnds.get(idx));
			viewer.update(element, null);
		}
	}

}
