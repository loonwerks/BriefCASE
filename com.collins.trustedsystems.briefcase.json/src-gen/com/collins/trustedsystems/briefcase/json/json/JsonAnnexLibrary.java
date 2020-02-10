/**
 */
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.ecore.EObject;

import org.osate.aadl2.AnnexLibrary;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annex Library</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary#getJsonAnnexElement <em>Json Annex Element</em>}</li>
 * </ul>
 *
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexLibrary()
 * @model
 * @generated
 */
public interface JsonAnnexLibrary extends EObject, AnnexLibrary
{
  /**
   * Returns the value of the '<em><b>Json Annex Element</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Json Annex Element</em>' containment reference.
   * @see #setJsonAnnexElement(JsonAnnexElement)
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexLibrary_JsonAnnexElement()
   * @model containment="true"
   * @generated
   */
  JsonAnnexElement getJsonAnnexElement();

  /**
   * Sets the value of the '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary#getJsonAnnexElement <em>Json Annex Element</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Json Annex Element</em>' containment reference.
   * @see #getJsonAnnexElement()
   * @generated
   */
  void setJsonAnnexElement(JsonAnnexElement value);

} // JsonAnnexLibrary
