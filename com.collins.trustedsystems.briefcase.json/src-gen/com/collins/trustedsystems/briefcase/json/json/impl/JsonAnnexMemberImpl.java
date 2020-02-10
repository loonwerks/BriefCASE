/**
 */
package com.collins.trustedsystems.briefcase.json.json.impl;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexString;
import com.collins.trustedsystems.briefcase.json.json.JsonPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annex Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexMemberImpl#getKey <em>Key</em>}</li>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexMemberImpl#getJsonAnnexElement <em>Json Annex Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JsonAnnexMemberImpl extends MinimalEObjectImpl.Container implements JsonAnnexMember
{
  /**
   * The cached value of the '{@link #getKey() <em>Key</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKey()
   * @generated
   * @ordered
   */
  protected JsonAnnexString key;

  /**
   * The cached value of the '{@link #getJsonAnnexElement() <em>Json Annex Element</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getJsonAnnexElement()
   * @generated
   * @ordered
   */
  protected JsonAnnexElement jsonAnnexElement;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JsonAnnexMemberImpl()
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
    return JsonPackage.Literals.JSON_ANNEX_MEMBER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexString getKey()
  {
    return key;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetKey(JsonAnnexString newKey, NotificationChain msgs)
  {
    JsonAnnexString oldKey = key;
    key = newKey;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonPackage.JSON_ANNEX_MEMBER__KEY, oldKey, newKey);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setKey(JsonAnnexString newKey)
  {
    if (newKey != key)
    {
      NotificationChain msgs = null;
      if (key != null)
        msgs = ((InternalEObject)key).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonPackage.JSON_ANNEX_MEMBER__KEY, null, msgs);
      if (newKey != null)
        msgs = ((InternalEObject)newKey).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonPackage.JSON_ANNEX_MEMBER__KEY, null, msgs);
      msgs = basicSetKey(newKey, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, JsonPackage.JSON_ANNEX_MEMBER__KEY, newKey, newKey));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexElement getJsonAnnexElement()
  {
    return jsonAnnexElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetJsonAnnexElement(JsonAnnexElement newJsonAnnexElement, NotificationChain msgs)
  {
    JsonAnnexElement oldJsonAnnexElement = jsonAnnexElement;
    jsonAnnexElement = newJsonAnnexElement;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT, oldJsonAnnexElement, newJsonAnnexElement);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJsonAnnexElement(JsonAnnexElement newJsonAnnexElement)
  {
    if (newJsonAnnexElement != jsonAnnexElement)
    {
      NotificationChain msgs = null;
      if (jsonAnnexElement != null)
        msgs = ((InternalEObject)jsonAnnexElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT, null, msgs);
      if (newJsonAnnexElement != null)
        msgs = ((InternalEObject)newJsonAnnexElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT, null, msgs);
      msgs = basicSetJsonAnnexElement(newJsonAnnexElement, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT, newJsonAnnexElement, newJsonAnnexElement));
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
      case JsonPackage.JSON_ANNEX_MEMBER__KEY:
        return basicSetKey(null, msgs);
      case JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT:
        return basicSetJsonAnnexElement(null, msgs);
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
      case JsonPackage.JSON_ANNEX_MEMBER__KEY:
        return getKey();
      case JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT:
        return getJsonAnnexElement();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case JsonPackage.JSON_ANNEX_MEMBER__KEY:
        setKey((JsonAnnexString)newValue);
        return;
      case JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT:
        setJsonAnnexElement((JsonAnnexElement)newValue);
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
      case JsonPackage.JSON_ANNEX_MEMBER__KEY:
        setKey((JsonAnnexString)null);
        return;
      case JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT:
        setJsonAnnexElement((JsonAnnexElement)null);
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
      case JsonPackage.JSON_ANNEX_MEMBER__KEY:
        return key != null;
      case JsonPackage.JSON_ANNEX_MEMBER__JSON_ANNEX_ELEMENT:
        return jsonAnnexElement != null;
    }
    return super.eIsSet(featureID);
  }

} //JsonAnnexMemberImpl
