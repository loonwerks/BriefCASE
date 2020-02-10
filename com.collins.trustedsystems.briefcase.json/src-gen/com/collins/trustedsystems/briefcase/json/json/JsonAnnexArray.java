/**
 */
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annex Array</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray#getJsonAnnexElements <em>Json Annex Elements</em>}</li>
 * </ul>
 *
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexArray()
 * @model
 * @generated
 */
public interface JsonAnnexArray extends JsonAnnexElement
{
  /**
   * Returns the value of the '<em><b>Json Annex Elements</b></em>' containment reference list.
   * The list contents are of type {@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Json Annex Elements</em>' containment reference list.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexArray_JsonAnnexElements()
   * @model containment="true"
   * @generated
   */
  EList<JsonAnnexElement> getJsonAnnexElements();

} // JsonAnnexArray
