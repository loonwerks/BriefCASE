/**
 */
package com.collins.trustedsystems.briefcase.json.json.util;

import com.collins.trustedsystems.briefcase.json.json.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Element;
import org.osate.aadl2.ModalElement;
import org.osate.aadl2.NamedElement;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage
 * @generated
 */
public class JsonSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static JsonPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JsonSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = JsonPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case JsonPackage.JSON_ANNEX_LIBRARY:
      {
        JsonAnnexLibrary jsonAnnexLibrary = (JsonAnnexLibrary)theEObject;
        T result = caseJsonAnnexLibrary(jsonAnnexLibrary);
        if (result == null) result = caseAnnexLibrary(jsonAnnexLibrary);
        if (result == null) result = caseNamedElement(jsonAnnexLibrary);
        if (result == null) result = caseElement(jsonAnnexLibrary);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_SUBCLAUSE:
      {
        JsonAnnexSubclause jsonAnnexSubclause = (JsonAnnexSubclause)theEObject;
        T result = caseJsonAnnexSubclause(jsonAnnexSubclause);
        if (result == null) result = caseAnnexSubclause(jsonAnnexSubclause);
        if (result == null) result = caseModalElement(jsonAnnexSubclause);
        if (result == null) result = caseNamedElement(jsonAnnexSubclause);
        if (result == null) result = caseElement(jsonAnnexSubclause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_ELEMENT:
      {
        JsonAnnexElement jsonAnnexElement = (JsonAnnexElement)theEObject;
        T result = caseJsonAnnexElement(jsonAnnexElement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_OBJECT:
      {
        JsonAnnexObject jsonAnnexObject = (JsonAnnexObject)theEObject;
        T result = caseJsonAnnexObject(jsonAnnexObject);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexObject);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_ARRAY:
      {
        JsonAnnexArray jsonAnnexArray = (JsonAnnexArray)theEObject;
        T result = caseJsonAnnexArray(jsonAnnexArray);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexArray);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_MEMBER:
      {
        JsonAnnexMember jsonAnnexMember = (JsonAnnexMember)theEObject;
        T result = caseJsonAnnexMember(jsonAnnexMember);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_STRING:
      {
        JsonAnnexString jsonAnnexString = (JsonAnnexString)theEObject;
        T result = caseJsonAnnexString(jsonAnnexString);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexString);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_NUMBER:
      {
        JsonAnnexNumber jsonAnnexNumber = (JsonAnnexNumber)theEObject;
        T result = caseJsonAnnexNumber(jsonAnnexNumber);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexNumber);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_BOOLEAN:
      {
        JsonAnnexBoolean jsonAnnexBoolean = (JsonAnnexBoolean)theEObject;
        T result = caseJsonAnnexBoolean(jsonAnnexBoolean);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexBoolean);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_NULL:
      {
        JsonAnnexNull jsonAnnexNull = (JsonAnnexNull)theEObject;
        T result = caseJsonAnnexNull(jsonAnnexNull);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexNull);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_INTEGER:
      {
        JsonAnnexInteger jsonAnnexInteger = (JsonAnnexInteger)theEObject;
        T result = caseJsonAnnexInteger(jsonAnnexInteger);
        if (result == null) result = caseJsonAnnexNumber(jsonAnnexInteger);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexInteger);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_REAL:
      {
        JsonAnnexReal jsonAnnexReal = (JsonAnnexReal)theEObject;
        T result = caseJsonAnnexReal(jsonAnnexReal);
        if (result == null) result = caseJsonAnnexNumber(jsonAnnexReal);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexReal);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_TRUE:
      {
        JsonAnnexTrue jsonAnnexTrue = (JsonAnnexTrue)theEObject;
        T result = caseJsonAnnexTrue(jsonAnnexTrue);
        if (result == null) result = caseJsonAnnexBoolean(jsonAnnexTrue);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexTrue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case JsonPackage.JSON_ANNEX_FALSE:
      {
        JsonAnnexFalse jsonAnnexFalse = (JsonAnnexFalse)theEObject;
        T result = caseJsonAnnexFalse(jsonAnnexFalse);
        if (result == null) result = caseJsonAnnexBoolean(jsonAnnexFalse);
        if (result == null) result = caseJsonAnnexElement(jsonAnnexFalse);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Library</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Library</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexLibrary(JsonAnnexLibrary object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Subclause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Subclause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexSubclause(JsonAnnexSubclause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexElement(JsonAnnexElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexObject(JsonAnnexObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Array</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Array</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexArray(JsonAnnexArray object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Member</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Member</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexMember(JsonAnnexMember object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex String</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex String</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexString(JsonAnnexString object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Number</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Number</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexNumber(JsonAnnexNumber object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Boolean</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Boolean</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexBoolean(JsonAnnexBoolean object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Null</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Null</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexNull(JsonAnnexNull object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Integer</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Integer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexInteger(JsonAnnexInteger object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Real</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Real</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexReal(JsonAnnexReal object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex True</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex True</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexTrue(JsonAnnexTrue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex False</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex False</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJsonAnnexFalse(JsonAnnexFalse object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseElement(Element object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Named Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Named Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNamedElement(NamedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Library</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Library</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAnnexLibrary(AnnexLibrary object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Modal Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Modal Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModalElement(ModalElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Annex Subclause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Annex Subclause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAnnexSubclause(AnnexSubclause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //JsonSwitch
