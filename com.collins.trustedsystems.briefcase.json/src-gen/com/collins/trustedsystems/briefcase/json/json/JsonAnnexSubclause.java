/**
 */
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.ecore.EObject;

import org.osate.aadl2.AnnexSubclause;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annex Subclause</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause#getJsonAnnexElement <em>Json Annex Element</em>}</li>
 * </ul>
 *
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexSubclause()
 * @model
 * @generated
 */
public interface JsonAnnexSubclause extends EObject, AnnexSubclause
{
  /**
   * Returns the value of the '<em><b>Json Annex Element</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Json Annex Element</em>' containment reference.
   * @see #setJsonAnnexElement(JsonAnnexElement)
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexSubclause_JsonAnnexElement()
   * @model containment="true"
   * @generated
   */
  JsonAnnexElement getJsonAnnexElement();

  /**
   * Sets the value of the '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause#getJsonAnnexElement <em>Json Annex Element</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Json Annex Element</em>' containment reference.
   * @see #getJsonAnnexElement()
   * @generated
   */
  void setJsonAnnexElement(JsonAnnexElement value);

} // JsonAnnexSubclause
