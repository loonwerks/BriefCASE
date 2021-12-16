package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlReal;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.NumberType;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.RealLiteral;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.UnitsType;

import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class ModelAnnotationsDialog extends TitleAreaDialog {

	private Map<Property, PropertyExpression> annotations = new HashMap<>();
	private NamedElement element = null;

	private final static String NO_VALUE_SELECTED = "<No value selected>";

	public ModelAnnotationsDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Annotate Model");
		setMessage("Annotate " + element.getName() + " with CASE-specific properties.", IMessageProvider.NONE);
	}

	public void create(NamedElement element) {
		if (element == null) {
			return;
		}
		this.element = element;
		create();
	}


//	@Override
//	protected Point getInitialSize() {
//		final Point size = super.getInitialSize();
////		size.x -= convertWidthInCharsToPixels(6);
//		size.y += convertHeightInCharsToPixels(1);
//
//		return size;
//	}

	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite area = (Composite) super.createDialogArea(parent);
		area.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		TabFolder folder = new TabFolder(area, SWT.NONE);
		createTab(folder, CasePropertyUtils.getCasePropertySet());
//		createTab(folder, CasePropertyUtils.getCaseSchedulingPropertySet());
//		createTab(folder, CasePropertyUtils.getHamrPropertySet());
		folder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		return area;
	}

	private void createTab(TabFolder folder, PropertySet propertySet) {

		if (propertySet == null) {
			return;
		}

		final TabItem tab = new TabItem(folder, SWT.NONE);
		tab.setText(propertySet.getName());

		final ScrolledComposite scroller = new ScrolledComposite(folder, SWT.BORDER | SWT.V_SCROLL);

		Composite container = new Composite(scroller, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);


		for (Property prop : propertySet.getOwnedProperties()) {
			if (element.acceptsProperty(prop)) {
				createPropertyField(container, prop);
			}
		}
		scroller.setContent(container);
		scroller.setExpandVertical(true);
		scroller.setExpandHorizontal(true);
		scroller.setMinSize(folder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scroller.setShowFocusedControl(true);

		tab.setControl(scroller);
	}

	private void createPropertyField(Composite container, Property prop) {

		PropertyType propType = prop.getOwnedPropertyType();
		if (propType == null) {
			propType = prop.getReferencedPropertyType();
		}
		if (propType == null) {
			BriefcaseNotifier.printWarning("Unable to determine property type for " + prop.getName());
			return;
		}

		Label lblProperty = new Label(container, SWT.NONE);
		lblProperty.setText(prop.getName());

		PropertyExpression propExpr = null;
		try {
			propExpr = element.getSimplePropertyValue(prop);
		} catch (Exception e) {

		}

		if (propType instanceof AadlBoolean) {
			createBooleanField(container, prop, propExpr);
		} else if (propType instanceof AadlString) {
			createStringField(container, prop, propExpr);
		} else if (propType instanceof EnumerationType) {
			createEnumField(container, prop, propExpr);
		} else if (propType instanceof UnitsType) {
			createUnitsField(container, prop, propExpr);
		} else if (propType instanceof AadlReal) {
			createRealField(container, prop, propExpr);
		} else if (propType instanceof AadlInteger) {
			createIntegerField(container, prop, propExpr);
//		} else if (propType instanceof RangeType) {
//
//		} else if (propType instanceof ClassifierType) {
//
//		} else if (propType instanceof ReferenceType) {
//
//		} else if (propType instanceof RecordType) {
//
//		} else if (propType instanceof ListType) {
//			createListField(container, prop, propExpr);
		} else {
			lblProperty = new Label(container, SWT.NONE);
			lblProperty.setText("Unsupported type - modify in model");
		}
	}

	private void createBooleanField(Composite container, Property prop, PropertyExpression propExpr) {

		final String initialValue = (propExpr == null ? NO_VALUE_SELECTED
				: (((BooleanLiteral) propExpr).getValue() ? "True" : "False"));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Combo combo = new Combo(container, SWT.BORDER);
		combo.setLayoutData(dataInfoField);
		combo.add(NO_VALUE_SELECTED);
		combo.add("True");
		combo.add("False");
		combo.setText(initialValue);
		combo.addFocusListener(new FocusListener() {

			private String priorValue = "";

			@Override
			public void focusGained(FocusEvent e) {
				priorValue = combo.getText();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (combo.getText().contentEquals(priorValue)) {
					return;
				} else if (combo.getText().contentEquals(initialValue)) {
					annotations.remove(prop);
				} else if (combo.getText().equals(NO_VALUE_SELECTED)) {
					annotations.put(prop, null);
				} else {
					BooleanLiteral boolLit = Aadl2Factory.eINSTANCE.createBooleanLiteral();
					boolLit.setValue(Boolean.parseBoolean(combo.getText()));
					annotations.put(prop, boolLit);
				}
			}

		});
//		combo.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (combo.getText().equals(NO_VALUE_SELECTED)) {
//					annotations.put(prop, null);
//				} else {
//					BooleanLiteral boolLit = Aadl2Factory.eINSTANCE.createBooleanLiteral();
//					boolLit.setValue(Boolean.parseBoolean(combo.getText()));
//					annotations.put(prop, boolLit);
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//
//		});

	}

	private void createStringField(Composite container, Property prop, PropertyExpression propExpr) {

		final String initialValue = (propExpr == null ? "" : ((StringLiteral) propExpr).getValue());

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(dataInfoField);
		text.setText(initialValue);
		text.addFocusListener(new FocusListener() {

			private String priorValue = "";

			@Override
			public void focusGained(FocusEvent e) {
				priorValue = text.getText();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().contentEquals(priorValue)) {
					return;
				} else if (text.getText().contentEquals(initialValue)) {
					annotations.remove(prop);
				} else if (text.getText().isEmpty()) {
					annotations.put(prop, null);
				} else {
					StringLiteral stringLit = Aadl2Factory.eINSTANCE.createStringLiteral();
					stringLit.setValue(text.getText());
					annotations.put(prop, stringLit);
				}
			}

		});

	}

	private void createEnumField(Composite container, Property prop, PropertyExpression propExpr) {

		final String initialValue = (propExpr == null ? NO_VALUE_SELECTED
				: ((EnumerationLiteral) ((NamedValue) propExpr).getNamedValue()).getName());

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Combo combo = new Combo(container, SWT.BORDER);
		combo.setLayoutData(dataInfoField);
		combo.add(NO_VALUE_SELECTED);
		for (EnumerationLiteral literal : ((EnumerationType) prop.getOwnedPropertyType()).getOwnedLiterals()) {
			combo.add(literal.getName());
		}
		combo.setText(initialValue);
		combo.addFocusListener(new FocusListener() {

			private String priorValue = "";

			@Override
			public void focusGained(FocusEvent e) {
				priorValue = combo.getText();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (combo.getText().contentEquals(priorValue)) {
					return;
				} else if (combo.getText().contentEquals(initialValue)) {
					annotations.remove(prop);
				} else if (combo.getText().equals(NO_VALUE_SELECTED)) {
					annotations.put(prop, null);
				} else {
					final EnumerationLiteral enumLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
					enumLit.setName(combo.getText());
					final NamedValue namedVal = Aadl2Factory.eINSTANCE.createNamedValue();
					namedVal.setNamedValue(enumLit);
					annotations.put(prop, namedVal);
				}
			}

		});
//		combo.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (combo.getText().equals(NO_VALUE_SELECTED)) {
//					annotations.put(prop, null);
//				} else {
//					EnumerationLiteral enumLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
//					enumLit.setName(combo.getText());
//					annotations.put(prop, enumLit);
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//
//		});

	}

	private void createUnitsField(Composite container, Property prop, PropertyExpression propExpr) {

		final String initialValue = (propExpr == null ? NO_VALUE_SELECTED
				: ((UnitLiteral) ((NamedValue) propExpr).getNamedValue()).getName());

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Combo combo = new Combo(container, SWT.BORDER);
		combo.setLayoutData(dataInfoField);
		combo.add(NO_VALUE_SELECTED);
		for (EnumerationLiteral literal : ((UnitsType) prop.getOwnedPropertyType()).getOwnedLiterals()) {
			combo.add(literal.getName());
		}
		combo.setText(initialValue);
		combo.addFocusListener(new FocusListener() {

			private String priorValue = "";

			@Override
			public void focusGained(FocusEvent e) {
				priorValue = combo.getText();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (combo.getText().contentEquals(priorValue)) {
					return;
				} else if (combo.getText().contentEquals(initialValue)) {
					annotations.remove(prop);
				} else if (combo.getText().equals(NO_VALUE_SELECTED)) {
					annotations.put(prop, null);
				} else {
					final EnumerationLiteral enumLit = Aadl2Factory.eINSTANCE.createEnumerationLiteral();
					enumLit.setName(combo.getText());
					final NamedValue namedVal = Aadl2Factory.eINSTANCE.createNamedValue();
					namedVal.setNamedValue(enumLit);
					annotations.put(prop, namedVal);
				}
			}

		});
//		combo.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (combo.getText().equals(NO_VALUE_SELECTED)) {
//					annotations.put(prop, null);
//				} else {
//
//					annotations.put(prop, combo.getText());
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//
//		});

	}

	private void createRealField(Composite container, Property prop, PropertyExpression propExpr) {

		final UnitLiteral unitLit = (propExpr == null ? null : ((RealLiteral) propExpr).getUnit());
		final String initialValue = (propExpr == null ? ""
				: ((RealLiteral) propExpr).getValue()
						+ (unitLit != null ? " " + unitLit.getName() : ""));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(dataInfoField);
		text.setText(initialValue);
		text.addFocusListener(new FocusListener() {

			private String priorValue = "";

			@Override
			public void focusGained(FocusEvent e) {
				priorValue = text.getText();
			}

			@Override
			public void focusLost(FocusEvent e) {

				if (text.getText().contentEquals(priorValue)) {
					return;
				} else if (text.getText().contentEquals(initialValue)) {
					annotations.remove(prop);
				} else if (text.getText().isEmpty()) {
					annotations.put(prop, null);
				} else {
					RealLiteral realLit = Aadl2Factory.eINSTANCE.createRealLiteral();
					// TODO: parse units
					realLit.setValue(text.getText());
					annotations.put(prop, realLit);
				}
			}

		});

	}

	private void createIntegerField(Composite container, Property prop, PropertyExpression propExpr) {

		final UnitLiteral unitLit = (propExpr == null ? null : ((IntegerLiteral) propExpr).getUnit());
		final String initialValue = (propExpr == null ? ""
				: ((IntegerLiteral) propExpr).getValue()
						+ (unitLit != null ? " " + unitLit.getName() : ""));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(dataInfoField);
		text.setText(initialValue);
		text.addFocusListener(new FocusListener() {

			private String priorValue = "";

			@Override
			public void focusGained(FocusEvent e) {
				priorValue = text.getText();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().contentEquals(priorValue)) {
					return;
				} else if (text.getText().contentEquals(initialValue)) {
					annotations.remove(prop);
				} else if (text.getText().isEmpty()) {
					annotations.put(prop, null);
				} else {
					IntegerLiteral intLit = Aadl2Factory.eINSTANCE.createIntegerLiteral();
					// TODO: parse units
					intLit.setValue(text.getText());
					annotations.put(prop, intLit);
				}
			}

		});

	}

//	public void createListField(Composite container, Property prop, PropertyExpression propExpr) {
//
//		final List<String> initialValues = new ArrayList<>();
//		final ListValue listValue = (ListValue) propExpr;
//		if (listValue != null) {
//			for (PropertyExpression pe : listValue.getOwnedListElements()) {
//				if (pe instanceof StringLiteral) {
//					initialValues.add(((StringLiteral) pe).getValue());
//				} else if (pe instanceof RealLiteral) {
//					final UnitLiteral unitLit = ((RealLiteral) pe).getUnit();
//					initialValues
//							.add(((RealLiteral) pe).getValue() + (unitLit == null ? "" : " + " + unitLit.getName()));
//				} else if (pe instanceof IntegerLiteral) {
//					final UnitLiteral unitLit = ((IntegerLiteral) pe).getUnit();
//					initialValues
//							.add(((IntegerLiteral) pe).getValue() + (unitLit == null ? "" : " + " + unitLit.getName()));
//				} else if (pe instanceof BooleanLiteral) {
//					initialValues.add(((BooleanLiteral) pe).getValue() ? "True" : "False");
//				} else {
//					initialValues.add(pe.toString());
//				}
//			}
//		}
//
//		final GridData dataInfoField = new GridData();
//		dataInfoField.grabExcessHorizontalSpace = true;
//		dataInfoField.horizontalAlignment = SWT.FILL;
//		dataInfoField.grabExcessVerticalSpace = false;
//
//		final ListControl list = new ListControl(container, initialValues);
//
//
//	}

//	private String printValue(PropertyExpression expr) {
//		if (expr == null) {
//			return "";
//		}
//		if (expr instanceof BooleanLiteral) {
//			return (((BooleanLiteral) expr).getValue() ? "True" : "False");
//		} else if (expr instanceof IntegerLiteral) {
//			final UnitLiteral unitLit = ((IntegerLiteral) expr).getUnit();
//			return ((IntegerLiteral) expr).getValue() + (unitLit != null ? " " + unitLit.getName() : "");
//		} else if (expr instanceof RealLiteral) {
//			final UnitLiteral unitLit = ((RealLiteral) expr).getUnit();
//			return ((RealLiteral) expr).getValue() + (unitLit != null ? " " + unitLit.getName() : "");
//		} else if (expr instanceof StringLiteral) {
//			return ((StringLiteral) expr).getValue();
//		} else if (expr instanceof NamedValue) {
//			if (((NamedValue) expr).getNamedValue() instanceof EnumerationLiteral) {
//				return ((EnumerationLiteral) ((NamedValue) expr).getNamedValue()).getName();
//			}
//
//		} else if (expr instanceof ListValue) {
//			String value = "[";
//			final ListValue listValue = (ListValue) expr;
//			for (int i = 0; i < listValue.getOwnedListElements().size(); ++i) {
//				value += printValue(listValue.getOwnedListElements().get(i));
//				if (i < listValue.getOwnedListElements().size() - 1) {
//					value += ",";
//				}
//			}
//			return value + "]";
//		}
//		return "";
//	}


	@Override
	protected void okPressed() {
		super.okPressed();
		for (Map.Entry<Property, PropertyExpression> entry : annotations.entrySet()) {
			// TODO: Check for missing units
			if (entry.getKey().getPropertyType() instanceof NumberType) {
				NumberType numberType = (NumberType) entry.getKey().getPropertyType();
				if (numberType.getOwnedUnitsType() != null) {

				}
			}
		}
	}

	public Map<Property, PropertyExpression> getAnnotations() {
		return annotations;
	}

}
