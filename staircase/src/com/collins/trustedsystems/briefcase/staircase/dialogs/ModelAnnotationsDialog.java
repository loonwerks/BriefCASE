package com.collins.trustedsystems.briefcase.staircase.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.osate.aadl2.AadlBoolean;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.AadlReal;
import org.osate.aadl2.AadlString;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.ClassifierType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EnumerationType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListType;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PropertyType;
import org.osate.aadl2.RangeType;
import org.osate.aadl2.RealLiteral;
import org.osate.aadl2.RecordType;
import org.osate.aadl2.ReferenceType;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.UnitLiteral;
import org.osate.aadl2.UnitsType;

import com.collins.trustedsystems.briefcase.staircase.utils.CasePropertyUtils;
import com.collins.trustedsystems.briefcase.util.BriefcaseNotifier;

public class ModelAnnotationsDialog extends TitleAreaDialog {

	private Map<String, String> annotations = new HashMap<>();
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
//		super.create();
//		if (component == null) {
//			setTitle("Annotate Model");
//			setMessage("Annotate model with CASE-specific properties.", IMessageProvider.NONE);
//		} else {
//			setTitle("Annotate Model");
//			setMessage("Annotate " + component.getName() + " with CASE-specific properties.", IMessageProvider.NONE);
//		}
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


		final ScrolledComposite container = new ScrolledComposite(folder, SWT.BORDER | SWT.V_SCROLL);
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		final GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);


		final TabItem tab = new TabItem(folder, SWT.NONE);
		tab.setText(propertySet.getName());

		for (Property prop : propertySet.getOwnedProperties()) {
			if (element.acceptsProperty(prop)) {
				createPropertyField(container, prop);
			}
		}

		container.setExpandVertical(true);
		container.setExpandHorizontal(true);
//		container.setMinSize(tabArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		tab.setControl(container);
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
			createBooleanField(container, prop.getName(), propExpr);
		} else if (propType instanceof AadlString) {
			createStringField(container, prop.getName(), propExpr);
		} else if (propType instanceof EnumerationType) {
			createEnumField(container, prop, propExpr);
		} else if (propType instanceof UnitsType) {
			createUnitsField(container, prop, propExpr);
		} else if (propType instanceof AadlReal) {
			createRealField(container, prop, propExpr);
		} else if (propType instanceof AadlInteger) {
			createIntegerField(container, prop, propExpr);
		} else if (propType instanceof RangeType) {

		} else if (propType instanceof ClassifierType) {

		} else if (propType instanceof ReferenceType) {

		} else if (propType instanceof RecordType) {

		} else if (propType instanceof ListType) {

		} else {
			lblProperty = new Label(container, SWT.NONE);
			lblProperty.setText("Unsupported type");
		}
	}

	private void createBooleanField(Composite container, String propName, PropertyExpression propExpr) {

		final Group booleanGroup = new Group(container, SWT.SHADOW_NONE);
//		booleanGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		booleanGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Button btnTrue = new Button(booleanGroup, SWT.CHECK);
		btnTrue.setText("True");
		btnTrue.setSelection(false);

		final Button btnFalse = new Button(booleanGroup, SWT.CHECK);
		btnFalse.setText("False");
		btnFalse.setSelection(false);

		btnTrue.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				if (btnTrue.getSelection()) {
					btnFalse.setSelection(false);
					annotations.put(propName, "true");
				} else if (!btnFalse.getSelection()) {
					annotations.put(propName, "");
				}
			}
		});

		btnTrue.addListener(SWT.Selection, e -> {
			if (e.type == SWT.Selection) {
				if (btnFalse.getSelection()) {
					btnTrue.setSelection(false);
					annotations.put(propName, "true");
				} else if (!btnTrue.getSelection()) {
					annotations.put(propName, "");
				}
			}
		});

		if (propExpr != null) {
			if (((BooleanLiteral) propExpr).getValue()) {
				btnTrue.setSelection(true);
			} else {
				btnFalse.setSelection(true);
			}
		}

	}

	private void createStringField(Composite container, String propName, PropertyExpression propExpr) {

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
//		text.setLayoutData(dataInfoField);
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				annotations.put(propName, text.getText());
			}

		});

		if (propExpr != null) {
			text.setText(((StringLiteral) propExpr).getValue());
		}

	}

	private void createEnumField(Composite container, Property prop, PropertyExpression propExpr) {
		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Combo combo = new Combo(container, SWT.BORDER);
//		combo.setLayoutData(dataInfoField);
		combo.add(NO_VALUE_SELECTED);
		for (EnumerationLiteral literal : ((EnumerationType) prop.getOwnedPropertyType()).getOwnedLiterals()) {
			combo.add(literal.getName());
		}
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (combo.getText().equals(NO_VALUE_SELECTED)) {
					annotations.put(prop.getName(), "");
				} else {
					annotations.put(prop.getName(), combo.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		if (propExpr == null) {
			combo.setText(NO_VALUE_SELECTED);
		} else {
			combo.setText(((EnumerationLiteral) propExpr).getName());
		}
	}

	private void createUnitsField(Composite container, Property prop, PropertyExpression propExpr) {
		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = GridData.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Combo combo = new Combo(container, SWT.BORDER);
//		combo.setLayoutData(dataInfoField);
		combo.add(NO_VALUE_SELECTED);
		for (EnumerationLiteral literal : ((UnitsType) prop.getOwnedPropertyType()).getOwnedLiterals()) {
			combo.add(literal.getName());
		}
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (combo.getText().equals(NO_VALUE_SELECTED)) {
					annotations.put(prop.getName(), "");
				} else {
					annotations.put(prop.getName(), combo.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		if (propExpr == null) {
			combo.setText(NO_VALUE_SELECTED);
		} else {
			combo.setText(((UnitLiteral) propExpr).getName());
		}
	}

	private void createRealField(Composite container, Property prop, PropertyExpression propExpr) {

//		final Group numberGroup = new Group(container, SWT.SHADOW_NONE);
//		numberGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		numberGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
//		text.setLayoutData(dataInfoField);
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				annotations.put(prop.getName(), text.getText());
			}

		});

//		final Combo combo = new Combo(numberGroup, SWT.BORDER);
//		combo.setLayoutData(dataInfoField);
//		combo.add(NO_VALUE_SELECTED);
//
//		UnitsType ut = ((AadlReal) prop.getType()).getOwnedUnitsType();
//
//		RealLiteral realLiteral = (RealLiteral) propExpr;
//
//		for (EnumerationLiteral literal : ((UnitsType) prop.getOwnedPropertyType()).getOwnedLiterals()) {
//			combo.add(literal.getName());
//		}

		if (propExpr != null) {
			text.setText(((RealLiteral) propExpr).getValue() + "");
		}
	}

	private void createIntegerField(Composite container, Property prop, PropertyExpression propExpr) {

//		final Group numberGroup = new Group(container, SWT.SHADOW_NONE);
//		numberGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		numberGroup.setLayout(new RowLayout(SWT.HORIZONTAL));

		final GridData dataInfoField = new GridData();
		dataInfoField.grabExcessHorizontalSpace = true;
		dataInfoField.horizontalAlignment = SWT.FILL;
		dataInfoField.grabExcessVerticalSpace = false;
		final Text text = new Text(container, SWT.BORDER);
//		text.setLayoutData(dataInfoField);
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				annotations.put(prop.getName(), text.getText());
			}

		});

//		final Combo combo = new Combo(numberGroup, SWT.BORDER);
//		combo.setLayoutData(dataInfoField);
//		combo.add(NO_VALUE_SELECTED);

//		UnitsType ut = ((AadlInteger) prop.getType()).getOwnedUnitsType();
//
//		IntegerLiteral intLiteral = (IntegerLiteral) propExpr;
//
//		for (EnumerationLiteral literal : ((UnitsType) prop.getOwnedPropertyType()).getOwnedLiterals()) {
//			combo.add(literal.getName());
//		}

		if (propExpr != null) {
			text.setText(((IntegerLiteral) propExpr).getValue() + "");
		}
	}


	@Override
	protected void okPressed() {
		super.okPressed();
	}

}
