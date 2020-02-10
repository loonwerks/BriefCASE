/**
 */
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.osate.aadl2.Aadl2Package;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.collins.trustedsystems.briefcase.json.json.JsonFactory
 * @model kind="package"
 * @generated
 */
public interface JsonPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "json";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.collins.com/trustedsystems/briefcase/json/Json";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "json";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  JsonPackage eINSTANCE = com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl.init();

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexLibraryImpl <em>Annex Library</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexLibraryImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexLibrary()
   * @generated
   */
  int JSON_ANNEX_LIBRARY = 0;

  /**
   * The feature id for the '<em><b>Owned Element</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY__OWNED_ELEMENT = Aadl2Package.ANNEX_LIBRARY__OWNED_ELEMENT;

  /**
   * The feature id for the '<em><b>Owned Comment</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY__OWNED_COMMENT = Aadl2Package.ANNEX_LIBRARY__OWNED_COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY__NAME = Aadl2Package.ANNEX_LIBRARY__NAME;

  /**
   * The feature id for the '<em><b>Qualified Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY__QUALIFIED_NAME = Aadl2Package.ANNEX_LIBRARY__QUALIFIED_NAME;

  /**
   * The feature id for the '<em><b>Owned Property Association</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY__OWNED_PROPERTY_ASSOCIATION = Aadl2Package.ANNEX_LIBRARY__OWNED_PROPERTY_ASSOCIATION;

  /**
   * The feature id for the '<em><b>Json Annex Element</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT = Aadl2Package.ANNEX_LIBRARY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex Library</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_LIBRARY_FEATURE_COUNT = Aadl2Package.ANNEX_LIBRARY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexSubclauseImpl <em>Annex Subclause</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexSubclauseImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexSubclause()
   * @generated
   */
  int JSON_ANNEX_SUBCLAUSE = 1;

  /**
   * The feature id for the '<em><b>Owned Element</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__OWNED_ELEMENT = Aadl2Package.ANNEX_SUBCLAUSE__OWNED_ELEMENT;

  /**
   * The feature id for the '<em><b>Owned Comment</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__OWNED_COMMENT = Aadl2Package.ANNEX_SUBCLAUSE__OWNED_COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__NAME = Aadl2Package.ANNEX_SUBCLAUSE__NAME;

  /**
   * The feature id for the '<em><b>Qualified Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__QUALIFIED_NAME = Aadl2Package.ANNEX_SUBCLAUSE__QUALIFIED_NAME;

  /**
   * The feature id for the '<em><b>Owned Property Association</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__OWNED_PROPERTY_ASSOCIATION = Aadl2Package.ANNEX_SUBCLAUSE__OWNED_PROPERTY_ASSOCIATION;

  /**
   * The feature id for the '<em><b>In Mode</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__IN_MODE = Aadl2Package.ANNEX_SUBCLAUSE__IN_MODE;

  /**
   * The feature id for the '<em><b>Json Annex Element</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE__JSON_ANNEX_ELEMENT = Aadl2Package.ANNEX_SUBCLAUSE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex Subclause</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_SUBCLAUSE_FEATURE_COUNT = Aadl2Package.ANNEX_SUBCLAUSE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexElementImpl <em>Annex Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexElementImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexElement()
   * @generated
   */
  int JSON_ANNEX_ELEMENT = 2;

  /**
   * The number of structural features of the '<em>Annex Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_ELEMENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexObjectImpl <em>Annex Object</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexObjectImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexObject()
   * @generated
   */
  int JSON_ANNEX_OBJECT = 3;

  /**
   * The feature id for the '<em><b>Json Annex Members</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex Object</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_OBJECT_FEATURE_COUNT = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexArrayImpl <em>Annex Array</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexArrayImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexArray()
   * @generated
   */
  int JSON_ANNEX_ARRAY = 4;

  /**
   * The feature id for the '<em><b>Json Annex Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex Array</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_ARRAY_FEATURE_COUNT = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexMemberImpl <em>Annex Member</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexMemberImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexMember()
   * @generated
   */
  int JSON_ANNEX_MEMBER = 5;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_MEMBER__KEY = 0;

  /**
   * The feature id for the '<em><b>Json Annex Element</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT = 1;

  /**
   * The number of structural features of the '<em>Annex Member</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_MEMBER_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexStringImpl <em>Annex String</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexStringImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexString()
   * @generated
   */
  int JSON_ANNEX_STRING = 6;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_STRING__VALUE = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex String</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_STRING_FEATURE_COUNT = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNumberImpl <em>Annex Number</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNumberImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexNumber()
   * @generated
   */
  int JSON_ANNEX_NUMBER = 7;

  /**
   * The number of structural features of the '<em>Annex Number</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_NUMBER_FEATURE_COUNT = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexBooleanImpl <em>Annex Boolean</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexBooleanImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexBoolean()
   * @generated
   */
  int JSON_ANNEX_BOOLEAN = 8;

  /**
   * The number of structural features of the '<em>Annex Boolean</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_BOOLEAN_FEATURE_COUNT = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNullImpl <em>Annex Null</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNullImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexNull()
   * @generated
   */
  int JSON_ANNEX_NULL = 9;

  /**
   * The number of structural features of the '<em>Annex Null</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_NULL_FEATURE_COUNT = JSON_ANNEX_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexIntegerImpl <em>Annex Integer</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexIntegerImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexInteger()
   * @generated
   */
  int JSON_ANNEX_INTEGER = 10;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_INTEGER__VALUE = JSON_ANNEX_NUMBER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex Integer</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_INTEGER_FEATURE_COUNT = JSON_ANNEX_NUMBER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexRealImpl <em>Annex Real</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexRealImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexReal()
   * @generated
   */
  int JSON_ANNEX_REAL = 11;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_REAL__VALUE = JSON_ANNEX_NUMBER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Annex Real</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_REAL_FEATURE_COUNT = JSON_ANNEX_NUMBER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexTrueImpl <em>Annex True</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexTrueImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexTrue()
   * @generated
   */
  int JSON_ANNEX_TRUE = 12;

  /**
   * The number of structural features of the '<em>Annex True</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_TRUE_FEATURE_COUNT = JSON_ANNEX_BOOLEAN_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexFalseImpl <em>Annex False</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexFalseImpl
   * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexFalse()
   * @generated
   */
  int JSON_ANNEX_FALSE = 13;

  /**
   * The number of structural features of the '<em>Annex False</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JSON_ANNEX_FALSE_FEATURE_COUNT = JSON_ANNEX_BOOLEAN_FEATURE_COUNT + 0;


  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary <em>Annex Library</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Library</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary
   * @generated
   */
  EClass getJsonAnnexLibrary();

  /**
   * Returns the meta object for the containment reference '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary#getJsonAnnexElement <em>Json Annex Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Json Annex Element</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary#getJsonAnnexElement()
   * @see #getJsonAnnexLibrary()
   * @generated
   */
  EReference getJsonAnnexLibrary_JsonAnnexElement();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause <em>Annex Subclause</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Subclause</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause
   * @generated
   */
  EClass getJsonAnnexSubclause();

  /**
   * Returns the meta object for the containment reference '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause#getJsonAnnexElement <em>Json Annex Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Json Annex Element</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause#getJsonAnnexElement()
   * @see #getJsonAnnexSubclause()
   * @generated
   */
  EReference getJsonAnnexSubclause_JsonAnnexElement();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement <em>Annex Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Element</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement
   * @generated
   */
  EClass getJsonAnnexElement();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject <em>Annex Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Object</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject
   * @generated
   */
  EClass getJsonAnnexObject();

  /**
   * Returns the meta object for the containment reference list '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject#getJsonAnnexMembers <em>Json Annex Members</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Json Annex Members</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject#getJsonAnnexMembers()
   * @see #getJsonAnnexObject()
   * @generated
   */
  EReference getJsonAnnexObject_JsonAnnexMembers();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray <em>Annex Array</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Array</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray
   * @generated
   */
  EClass getJsonAnnexArray();

  /**
   * Returns the meta object for the containment reference list '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray#getJsonAnnexElements <em>Json Annex Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Json Annex Elements</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray#getJsonAnnexElements()
   * @see #getJsonAnnexArray()
   * @generated
   */
  EReference getJsonAnnexArray_JsonAnnexElements();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember <em>Annex Member</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Member</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember
   * @generated
   */
  EClass getJsonAnnexMember();

  /**
   * Returns the meta object for the containment reference '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Key</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getKey()
   * @see #getJsonAnnexMember()
   * @generated
   */
  EReference getJsonAnnexMember_Key();

  /**
   * Returns the meta object for the containment reference '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getJsonAnnexElement <em>Json Annex Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Json Annex Element</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getJsonAnnexElement()
   * @see #getJsonAnnexMember()
   * @generated
   */
  EReference getJsonAnnexMember_JsonAnnexElement();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexString <em>Annex String</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex String</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexString
   * @generated
   */
  EClass getJsonAnnexString();

  /**
   * Returns the meta object for the attribute '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexString#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexString#getValue()
   * @see #getJsonAnnexString()
   * @generated
   */
  EAttribute getJsonAnnexString_Value();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexNumber <em>Annex Number</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Number</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexNumber
   * @generated
   */
  EClass getJsonAnnexNumber();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexBoolean <em>Annex Boolean</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Boolean</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexBoolean
   * @generated
   */
  EClass getJsonAnnexBoolean();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexNull <em>Annex Null</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Null</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexNull
   * @generated
   */
  EClass getJsonAnnexNull();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger <em>Annex Integer</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Integer</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger
   * @generated
   */
  EClass getJsonAnnexInteger();

  /**
   * Returns the meta object for the attribute '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger#getValue()
   * @see #getJsonAnnexInteger()
   * @generated
   */
  EAttribute getJsonAnnexInteger_Value();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal <em>Annex Real</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex Real</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal
   * @generated
   */
  EClass getJsonAnnexReal();

  /**
   * Returns the meta object for the attribute '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal#getValue()
   * @see #getJsonAnnexReal()
   * @generated
   */
  EAttribute getJsonAnnexReal_Value();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexTrue <em>Annex True</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex True</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexTrue
   * @generated
   */
  EClass getJsonAnnexTrue();

  /**
   * Returns the meta object for class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexFalse <em>Annex False</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annex False</em>'.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexFalse
   * @generated
   */
  EClass getJsonAnnexFalse();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  JsonFactory getJsonFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexLibraryImpl <em>Annex Library</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexLibraryImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexLibrary()
     * @generated
     */
    EClass JSON_ANNEX_LIBRARY = eINSTANCE.getJsonAnnexLibrary();

    /**
     * The meta object literal for the '<em><b>Json Annex Element</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT = eINSTANCE.getJsonAnnexLibrary_JsonAnnexElement();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexSubclauseImpl <em>Annex Subclause</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexSubclauseImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexSubclause()
     * @generated
     */
    EClass JSON_ANNEX_SUBCLAUSE = eINSTANCE.getJsonAnnexSubclause();

    /**
     * The meta object literal for the '<em><b>Json Annex Element</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference JSON_ANNEX_SUBCLAUSE__JSON_ANNEX_ELEMENT = eINSTANCE.getJsonAnnexSubclause_JsonAnnexElement();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexElementImpl <em>Annex Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexElementImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexElement()
     * @generated
     */
    EClass JSON_ANNEX_ELEMENT = eINSTANCE.getJsonAnnexElement();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexObjectImpl <em>Annex Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexObjectImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexObject()
     * @generated
     */
    EClass JSON_ANNEX_OBJECT = eINSTANCE.getJsonAnnexObject();

    /**
     * The meta object literal for the '<em><b>Json Annex Members</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS = eINSTANCE.getJsonAnnexObject_JsonAnnexMembers();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexArrayImpl <em>Annex Array</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexArrayImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexArray()
     * @generated
     */
    EClass JSON_ANNEX_ARRAY = eINSTANCE.getJsonAnnexArray();

    /**
     * The meta object literal for the '<em><b>Json Annex Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS = eINSTANCE.getJsonAnnexArray_JsonAnnexElements();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexMemberImpl <em>Annex Member</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexMemberImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexMember()
     * @generated
     */
    EClass JSON_ANNEX_MEMBER = eINSTANCE.getJsonAnnexMember();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference JSON_ANNEX_MEMBER__KEY = eINSTANCE.getJsonAnnexMember_Key();

    /**
     * The meta object literal for the '<em><b>Json Annex Element</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT = eINSTANCE.getJsonAnnexMember_JsonAnnexElement();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexStringImpl <em>Annex String</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexStringImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexString()
     * @generated
     */
    EClass JSON_ANNEX_STRING = eINSTANCE.getJsonAnnexString();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute JSON_ANNEX_STRING__VALUE = eINSTANCE.getJsonAnnexString_Value();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNumberImpl <em>Annex Number</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNumberImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexNumber()
     * @generated
     */
    EClass JSON_ANNEX_NUMBER = eINSTANCE.getJsonAnnexNumber();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexBooleanImpl <em>Annex Boolean</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexBooleanImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexBoolean()
     * @generated
     */
    EClass JSON_ANNEX_BOOLEAN = eINSTANCE.getJsonAnnexBoolean();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNullImpl <em>Annex Null</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexNullImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexNull()
     * @generated
     */
    EClass JSON_ANNEX_NULL = eINSTANCE.getJsonAnnexNull();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexIntegerImpl <em>Annex Integer</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexIntegerImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexInteger()
     * @generated
     */
    EClass JSON_ANNEX_INTEGER = eINSTANCE.getJsonAnnexInteger();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute JSON_ANNEX_INTEGER__VALUE = eINSTANCE.getJsonAnnexInteger_Value();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexRealImpl <em>Annex Real</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexRealImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexReal()
     * @generated
     */
    EClass JSON_ANNEX_REAL = eINSTANCE.getJsonAnnexReal();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute JSON_ANNEX_REAL__VALUE = eINSTANCE.getJsonAnnexReal_Value();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexTrueImpl <em>Annex True</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexTrueImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexTrue()
     * @generated
     */
    EClass JSON_ANNEX_TRUE = eINSTANCE.getJsonAnnexTrue();

    /**
     * The meta object literal for the '{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexFalseImpl <em>Annex False</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexFalseImpl
     * @see com.collins.trustedsystems.briefcase.json.json.impl.JsonPackageImpl#getJsonAnnexFalse()
     * @generated
     */
    EClass JSON_ANNEX_FALSE = eINSTANCE.getJsonAnnexFalse();

  }

} //JsonPackage
