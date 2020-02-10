/**
 */
package com.collins.trustedsystems.briefcase.json.json.impl;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject;
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
 * An implementation of the model object '<em><b>Annex Object</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexObjectImpl#getJsonAnnexMembers <em>Json Annex Members</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JsonAnnexObjectImpl extends JsonAnnexElementImpl implements JsonAnnexObject
{
  /**
   * The cached value of the '{@link #getJsonAnnexMembers() <em>Json Annex Members</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJsonAnnexMembers()
   * @generated
   * @ordered
   */
  protected EList<JsonAnnexMember> jsonAnnexMembers;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JsonAnnexObjectImpl()
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
    return JsonPackage.Literals.JSON_ANNEX_OBJECT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<JsonAnnexMember> getJsonAnnexMembers()
  {
    if (jsonAnnexMembers == null)
    {
      jsonAnnexMembers = new EObjectContainmentEList<JsonAnnexMember>(JsonAnnexMember.class, this, JsonPackage.JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS);
    }
    return jsonAnnexMembers;
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
      case JsonPackage.JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS:
        return ((InternalEList<?>)getJsonAnnexMembers()).basicRemove(otherEnd, msgs);
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
      case JsonPackage.JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS:
        return getJsonAnnexMembers();
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
      case JsonPackage.JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS:
        getJsonAnnexMembers().clear();
        getJsonAnnexMembers().addAll((Collection<? extends JsonAnnexMember>)newValue);
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
      case JsonPackage.JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS:
        getJsonAnnexMembers().clear();
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
      case JsonPackage.JSON_ANNEX_OBJECT__JSON_ANNEX_MEMBERS:
        return jsonAnnexMembers != null && !jsonAnnexMembers.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //JsonAnnexObjectImpl
