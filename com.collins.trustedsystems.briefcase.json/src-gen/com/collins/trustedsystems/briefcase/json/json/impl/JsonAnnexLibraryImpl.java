/**
 */
package com.collins.trustedsystems.briefcase.json.json.impl;

import com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement;
import com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary;
import com.collins.trustedsystems.briefcase.json.json.JsonPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.osate.aadl2.impl.AnnexLibraryImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annex Library</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.collins.trustedsystems.briefcase.json.json.impl.JsonAnnexLibraryImpl#getJsonAnnexElement <em>Json Annex Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class JsonAnnexLibraryImpl extends AnnexLibraryImpl implements JsonAnnexLibrary
{
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
  protected JsonAnnexLibraryImpl()
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
    return JsonPackage.Literals.JSON_ANNEX_LIBRARY;
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT, oldJsonAnnexElement, newJsonAnnexElement);
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
        msgs = ((InternalEObject)jsonAnnexElement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT, null, msgs);
      if (newJsonAnnexElement != null)
        msgs = ((InternalEObject)newJsonAnnexElement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT, null, msgs);
      msgs = basicSetJsonAnnexElement(newJsonAnnexElement, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT, newJsonAnnexElement, newJsonAnnexElement));
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
      case JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT:
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
      case JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT:
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
      case JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT:
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
      case JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT:
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
      case JsonPackage.JSON_ANNEX_LIBRARY__JSON_ANNEX_ELEMENT:
        return jsonAnnexElement != null;
    }
    return super.eIsSet(featureID);
  }

} //JsonAnnexLibraryImpl
