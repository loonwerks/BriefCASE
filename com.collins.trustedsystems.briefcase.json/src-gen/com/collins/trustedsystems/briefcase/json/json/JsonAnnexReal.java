/**
 */
package com.collins.trustedsystems.briefcase.json.json;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annex Real</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexReal()
 * @model
 * @generated
 */
public interface JsonAnnexReal extends JsonAnnexNumber
{
  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(double)
   * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage#getJsonAnnexReal_Value()
   * @model dataType="org.osate.aadl2.Real"
   * @generated
   */
  double getValue();

  /**
   * Sets the value of the '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(double value);

} // JsonAnnexReal
