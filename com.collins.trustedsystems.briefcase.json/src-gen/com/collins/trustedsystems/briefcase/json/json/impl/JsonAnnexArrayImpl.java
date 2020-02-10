/**
 */
package com.collins.trustedsystems.briefcase.json.json.impl;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement;
import com.collins.trustedsystems.briefcase.json.json.JsonPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annex Array</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexArrayImpl#getJsonAnnexElements <em>Json Annex Elements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JsonAnnexArrayImpl extends JsonAnnexElementImpl implements JsonAnnexArray
{
  /**
   * The cached value of the '{@link #getJsonAnnexElements() <em>Json Annex Elements</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJsonAnnexElements()
   * @generated
   * @ordered
   */
  protected EList<JsonAnnexElement> jsonAnnexElements;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JsonAnnexArrayImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return JsonPackage.Literals.JSON_ANNEX_ARRAY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<JsonAnnexElement> getJsonAnnexElements()
  {
    if (jsonAnnexElements == null)
    {
      jsonAnnexElements = new EObjectContainmentEList<JsonAnnexElement>(JsonAnnexElement.class, this, JsonPackage.JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS);
    }
    return jsonAnnexElements;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case JsonPackage.JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS:
        return ((InternalEList<?>)getJsonAnnexElements()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case JsonPackage.JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS:
        return getJsonAnnexElements();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case JsonPackage.JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS:
        getJsonAnnexElements().clear();
        getJsonAnnexElements().addAll((Collection<? extends JsonAnnexElement>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case JsonPackage.JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS:
        getJsonAnnexElements().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case JsonPackage.JSON_ANNEX_ARRAY__JSON_ANNEX_ELEMENTS:
        return jsonAnnexElements != null && !jsonAnnexElements.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //JsonAnnexArrayImpl
