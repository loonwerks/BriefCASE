/**
 */
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annex Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getKey <em>Key</em>}</li>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getJsonAnnexElement <em>Json Annex Element</em>}</li>
 * </ul>
 *
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexMember()
 * @model
 * @generated
 */
public interface JsonAnnexMember extends EObject
{
  /**
   * Returns the value of the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Key</em>' containment reference.
   * @see #setKey(JsonAnnexString)
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexMember_Key()
   * @model containment="true"
   * @generated
   */
  JsonAnnexString getKey();

  /**
   * Sets the value of the '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getKey <em>Key</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Key</em>' containment reference.
   * @see #getKey()
   * @generated
   */
  void setKey(JsonAnnexString value);

  /**
   * Returns the value of the '<em><b>Json Annex Element</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Json Annex Element</em>' containment reference.
   * @see #setJsonAnnexElement(JsonAnnexElement)
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexMember_JsonAnnexElement()
   * @model containment="true"
   * @generated
   */
  JsonAnnexElement getJsonAnnexElement();

  /**
   * Sets the value of the '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember#getJsonAnnexElement <em>Json Annex Element</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Json Annex Element</em>' containment reference.
   * @see #getJsonAnnexElement()
   * @generated
   */
  void setJsonAnnexElement(JsonAnnexElement value);

} // JsonAnnexMember
