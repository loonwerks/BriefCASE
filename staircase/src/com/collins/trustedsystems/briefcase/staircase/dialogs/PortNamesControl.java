package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class PortNamesControl {

	private TableViewer tblPorts;

	public enum PortNameItems {
		INSTANCE;

		private List<PortNameItem> portNames;

		private PortNameItems() {
			portNames = new ArrayList<PortNameItem>();
		}

		public List<PortNameItem> getPortNames() {
			return portNames;
		}

	}

	public enum PortDirection {
		INPUT, OUTPUT;
	}

	public PortNamesControl(Composite parent, String keyName, List<String> keyPorts, List<String> inputPorts,
			List<String> outputPorts, String targetComponentName) {

		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;

		Composite baseComposite = new Composite(parent, SWT.BORDER);
		baseComposite.setLayoutData(gridData);
		baseComposite.setLayout(new GridLayout(1, true));

		Composite tableComposite = new Composite(baseComposite, SWT.NONE);
		gridData.heightHint = 120;
		tableComposite.setLayoutData(gridData);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		tblPorts = new TableViewer(tableComposite, SWT.NO_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn colKeyName = createTableViewerColumn(keyName, 100);
		TableViewerColumn colInputPortName = createTableViewerColumn(targetComponentName + " Input Port Name", 100);
		TableViewerColumn colOutputPortName = createTableViewerColumn(targetComponentName + " Output Port Name", 100);

		// Add the key port column
		colKeyName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PortNameItem p = (PortNameItem) element;
				return p.getKeyPortName();
			}
		});

		// add input port name column
		colInputPortName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((PortNameItem) cell.getElement()).getInputPortName());
			}
		});
		colInputPortName.setEditingSupport(new PortNameEditingSupport(tblPorts, PortDirection.INPUT));

		// add output port name column
		colOutputPortName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((PortNameItem) cell.getElement()).getOutputPortName());
			}
		});
		colOutputPortName.setEditingSupport(new PortNameEditingSupport(tblPorts, PortDirection.OUTPUT));

		// make lines and header visible
		final Table table = tblPorts.getTable();
		table.setHeaderVisible(true);
		table.setHeaderBackground(new Color(Display.getDefault(), 220, 220, 220));
		table.setLinesVisible(true);

		tblPorts.setContentProvider(new ArrayContentProvider());

		// Initialize
		PortNameItems.INSTANCE.getPortNames().clear();
		for (int i = 0; i < keyPorts.size(); ++i) {
			PortNameItems.INSTANCE.getPortNames()
					.add(new PortNameItem(keyPorts.get(i), inputPorts.get(i), outputPorts.get(i)));
		}
		tblPorts.setInput(PortNameItems.INSTANCE.getPortNames());

		// Layout the viewer
		tableColumnLayout.setColumnData(colKeyName.getColumn(), new ColumnWeightData(33, 100, true));
		tableColumnLayout.setColumnData(colInputPortName.getColumn(), new ColumnWeightData(33, 100, true));
		tableColumnLayout.setColumnData(colOutputPortName.getColumn(), new ColumnWeightData(33, 100, true));

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

	public Map<String, List<String>> getContents() {
		Map<String, List<String>> contents = new HashMap<>();
		PortNameItems.INSTANCE.getPortNames().forEach(
				p -> contents.put(p.getKeyPortName(),
						new ArrayList<String>(Arrays.asList(p.getInputPortName(), p.getOutputPortName()))));
		return contents;
	}

	public class PortNameItem {

		private String keyPortName = "";
		private String inputPortName = "";
		private String outputPortName = "";
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public PortNameItem() {

		}

		public PortNameItem(String keyPortName, String inputPortName, String outputPortName) {
			this.keyPortName = keyPortName;
			this.inputPortName = inputPortName;
			this.outputPortName = outputPortName;
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}

		public String getKeyPortName() {
			return keyPortName;
		}

		public String getInputPortName() {
			return inputPortName;
		}

		public String getOutputPortName() {
			return outputPortName;
		}

		public void setKeyPortName(String portName) {
			propertyChangeSupport.firePropertyChange("keyPortName", this.keyPortName, this.keyPortName = portName);
		}

		public void setInputPortName(String portName) {
			propertyChangeSupport.firePropertyChange("inputPortName", this.inputPortName,
					this.inputPortName = portName);
		}

		public void setOutputPortName(String portName) {
			propertyChangeSupport.firePropertyChange("outputPortName", this.outputPortName,
					this.outputPortName = portName);
		}

		@Override
		public String toString() {
			return keyPortName + " : " + inputPortName + " : " + outputPortName;
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
				return ((PortNameItem) element).getInputPortName();
			} else {
				return ((PortNameItem) element).getOutputPortName();
			}
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (portDirection == PortDirection.INPUT) {
				((PortNameItem) element).setInputPortName(String.valueOf(value));
			} else {
				((PortNameItem) element).setOutputPortName(String.valueOf(value));
			}
			viewer.update(element, null);
		}
	}

}
