/**
 */
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annex Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject#getJsonAnnexMembers <em>Json Annex Members</em>}</li>
 * </ul>
 *
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexObject()
 * @model
 * @generated
 */
public interface JsonAnnexObject extends JsonAnnexElement
{
  /**
   * Returns the value of the '<em><b>Json Annex Members</b></em>' containment reference list.
   * The list contents are of type {@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Json Annex Members</em>' containment reference list.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexObject_JsonAnnexMembers()
   * @model containment="true"
   * @generated
   */
  EList<JsonAnnexMember> getJsonAnnexMembers();

} // JsonAnnexObject
