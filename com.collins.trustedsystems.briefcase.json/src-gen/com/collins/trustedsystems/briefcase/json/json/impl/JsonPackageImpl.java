/**
 */
package com.collins.trustedsystems.briefcase.json.json.impl;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexBoolean;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexFalse;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexNull;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexNumber;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexString;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexTrue;
import com.collins.trustedsystems.briefcase.json.json.JsonFactory;
import com.collins.trustedsystems.briefcase.json.json.JsonPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.osate.aadl2.Aadl2Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JsonPackageImpl extends EPackageImpl implements JsonPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexLibraryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexSubclauseEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexElementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexObjectEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexArrayEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexMemberEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexStringEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexNumberEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexBooleanEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexNullEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexIntegerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexRealEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexTrueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass jsonAnnexFalseEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private JsonPackageImpl()
  {
    super(eNS_URI, JsonFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link JsonPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static JsonPackage init()
  {
    if (isInited) return (JsonPackage)EPackage.Registry.INSTANCE.getEPackage(JsonPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredJsonPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    JsonPackageImpl theJsonPackage = registeredJsonPackage instanceof JsonPackageImpl ? (JsonPackageImpl)registeredJsonPackage : new JsonPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();
    Aadl2Package.eINSTANCE.eClass();

    // Create package meta-data objects
    theJsonPackage.createPackageContents();

    // Initialize created meta-data
    theJsonPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theJsonPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(JsonPackage.eNS_URI, theJsonPackage);
    return theJsonPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexLibrary()
  {
    return jsonAnnexLibraryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getJsonAnnexLibrary_JsonAnnexElement()
  {
    return (EReference)jsonAnnexLibraryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexSubclause()
  {
    return jsonAnnexSubclauseEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getJsonAnnexSubclause_JsonAnnexElement()
  {
    return (EReference)jsonAnnexSubclauseEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexElement()
  {
    return jsonAnnexElementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexObject()
  {
    return jsonAnnexObjectEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getJsonAnnexObject_JsonAnnexMembers()
  {
    return (EReference)jsonAnnexObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexArray()
  {
    return jsonAnnexArrayEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getJsonAnnexArray_JsonAnnexElements()
  {
    return (EReference)jsonAnnexArrayEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexMember()
  {
    return jsonAnnexMemberEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getJsonAnnexMember_Key()
  {
    return (EReference)jsonAnnexMemberEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getJsonAnnexMember_JsonAnnexElement()
  {
    return (EReference)jsonAnnexMemberEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexString()
  {
    return jsonAnnexStringEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getJsonAnnexString_Value()
  {
    return (EAttribute)jsonAnnexStringEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexNumber()
  {
    return jsonAnnexNumberEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexBoolean()
  {
    return jsonAnnexBooleanEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexNull()
  {
    return jsonAnnexNullEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexInteger()
  {
    return jsonAnnexIntegerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getJsonAnnexInteger_Value()
  {
    return (EAttribute)jsonAnnexIntegerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexReal()
  {
    return jsonAnnexRealEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getJsonAnnexReal_Value()
  {
    return (EAttribute)jsonAnnexRealEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexTrue()
  {
    return jsonAnnexTrueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getJsonAnnexFalse()
  {
    return jsonAnnexFalseEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonFactory getJsonFactory()
  {
    return (JsonFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    jsonAnnexLibraryEClass = createEClass(JSON_ANNEX_LIBRARY);
    createEReference(jsonAnnexLibraryEClass, JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT);

    jsonAnnexSubclauseEClass = createEClass(JSON_ANNEX_SUBCLAUSE);
    createEReference(jsonAnnexSubclauseEClass, JSON_ANNEX_SUBCLAUSE__JSON_ANNEX_ELEMENT);

    jsonAnnexElementEClass = createEClass(JSON_ANNEX_ELEMENT);

    jsonAnnexObjectEClass = createEClass(JSON_ANNEX_OBJECT);
    createEReference(jsonAnnexObjectEClass, JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS);

    jsonAnnexArrayEClass = createEClass(JSON_ANNEX_ARRAY);
    createEReference(jsonAnnexArrayEClass, JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS);

    jsonAnnexMemberEClass = createEClass(JSON_ANNEX_MEMBER);
    createEReference(jsonAnnexMemberEClass, JSON_ANNEX_MEMBER__KEY);
    createEReference(jsonAnnexMemberEClass, JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT);

    jsonAnnexStringEClass = createEClass(JSON_ANNEX_STRING);
    createEAttribute(jsonAnnexStringEClass, JSON_ANNEX_STRING__VALUE);

    jsonAnnexNumberEClass = createEClass(JSON_ANNEX_NUMBER);

    jsonAnnexBooleanEClass = createEClass(JSON_ANNEX_BOOLEAN);

    jsonAnnexNullEClass = createEClass(JSON_ANNEX_NULL);

    jsonAnnexIntegerEClass = createEClass(JSON_ANNEX_INTEGER);
    createEAttribute(jsonAnnexIntegerEClass, JSON_ANNEX_INTEGER__VALUE);

    jsonAnnexRealEClass = createEClass(JSON_ANNEX_REAL);
    createEAttribute(jsonAnnexRealEClass, JSON_ANNEX_REAL__VALUE);

    jsonAnnexTrueEClass = createEClass(JSON_ANNEX_TRUE);

    jsonAnnexFalseEClass = createEClass(JSON_ANNEX_FALSE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    Aadl2Package theAadl2Package = (Aadl2Package)EPackage.Registry.INSTANCE.getEPackage(Aadl2Package.eNS_URI);
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    jsonAnnexLibraryEClass.getESuperTypes().add(theAadl2Package.getAnnexLibrary());
    jsonAnnexSubclauseEClass.getESuperTypes().add(theAadl2Package.getAnnexSubclause());
    jsonAnnexObjectEClass.getESuperTypes().add(this.getJsonAnnexElement());
    jsonAnnexArrayEClass.getESuperTypes().add(this.getJsonAnnexElement());
    jsonAnnexStringEClass.getESuperTypes().add(this.getJsonAnnexElement());
    jsonAnnexNumberEClass.getESuperTypes().add(this.getJsonAnnexElement());
    jsonAnnexBooleanEClass.getESuperTypes().add(this.getJsonAnnexElement());
    jsonAnnexNullEClass.getESuperTypes().add(this.getJsonAnnexElement());
    jsonAnnexIntegerEClass.getESuperTypes().add(this.getJsonAnnexNumber());
    jsonAnnexRealEClass.getESuperTypes().add(this.getJsonAnnexNumber());
    jsonAnnexTrueEClass.getESuperTypes().add(this.getJsonAnnexBoolean());
    jsonAnnexFalseEClass.getESuperTypes().add(this.getJsonAnnexBoolean());

    // Initialize classes and features; add operations and parameters
    initEClass(jsonAnnexLibraryEClass, JsonAnnexLibrary.class, "JsonAnnexLibrary", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getJsonAnnexLibrary_JsonAnnexElement(), this.getJsonAnnexElement(), null, "jsonAnnexElement", null, 0, 1, JsonAnnexLibrary.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexSubclauseEClass, JsonAnnexSubclause.class, "JsonAnnexSubclause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getJsonAnnexSubclause_JsonAnnexElement(), this.getJsonAnnexElement(), null, "jsonAnnexElement", null, 0, 1, JsonAnnexSubclause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexElementEClass, JsonAnnexElement.class, "JsonAnnexElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(jsonAnnexObjectEClass, JsonAnnexObject.class, "JsonAnnexObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getJsonAnnexObject_JsonAnnexMembers(), this.getJsonAnnexMember(), null, "jsonAnnexMembers", null, 0, -1, JsonAnnexObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexArrayEClass, JsonAnnexArray.class, "JsonAnnexArray", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getJsonAnnexArray_JsonAnnexElements(), this.getJsonAnnexElement(), null, "jsonAnnexElements", null, 0, -1, JsonAnnexArray.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexMemberEClass, JsonAnnexMember.class, "JsonAnnexMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getJsonAnnexMember_Key(), this.getJsonAnnexString(), null, "key", null, 0, 1, JsonAnnexMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getJsonAnnexMember_JsonAnnexElement(), this.getJsonAnnexElement(), null, "jsonAnnexElement", null, 0, 1, JsonAnnexMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexStringEClass, JsonAnnexString.class, "JsonAnnexString", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJsonAnnexString_Value(), theEcorePackage.getEString(), "value", null, 0, 1, JsonAnnexString.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexNumberEClass, JsonAnnexNumber.class, "JsonAnnexNumber", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(jsonAnnexBooleanEClass, JsonAnnexBoolean.class, "JsonAnnexBoolean", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(jsonAnnexNullEClass, JsonAnnexNull.class, "JsonAnnexNull", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(jsonAnnexIntegerEClass, JsonAnnexInteger.class, "JsonAnnexInteger", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJsonAnnexInteger_Value(), theAadl2Package.getInteger(), "value", null, 0, 1, JsonAnnexInteger.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexRealEClass, JsonAnnexReal.class, "JsonAnnexReal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJsonAnnexReal_Value(), theAadl2Package.getReal(), "value", null, 0, 1, JsonAnnexReal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(jsonAnnexTrueEClass, JsonAnnexTrue.class, "JsonAnnexTrue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(jsonAnnexFalseEClass, JsonAnnexFalse.class, "JsonAnnexFalse", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} //JsonPackageImpl
