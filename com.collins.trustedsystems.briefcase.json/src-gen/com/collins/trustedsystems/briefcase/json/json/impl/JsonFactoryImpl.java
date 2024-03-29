/**
 * Copyright (c) 2021, Collins Aerospace.
 * Developed with the sponsorship of Defense Advanced Research Projects Agency (DARPA).
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this data, 
 * including any software or models in source or binary form, as well as any drawings, specifications, 
 * and documentation (collectively "the Data"), to deal in the Data without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Data.
 * 
 * THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
 * 
 * Generated by Xtext version 2.25.0.
 */
package com.collins.trustedsystems.briefcase.json.json.impl;

import com.collins.trustedsystems.briefcase.json.json.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JsonFactoryImpl extends EFactoryImpl implements JsonFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static JsonFactory init()
  {
    try
    {
      JsonFactory theJsonFactory = (JsonFactory)EPackage.Registry.INSTANCE.getEFactory(JsonPackage.eNS_URI);
      if (theJsonFactory != null)
      {
        return theJsonFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new JsonFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JsonFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case JsonPackage.JSON_ANNEX_LIBRARY: return createJsonAnnexLibrary();
      case JsonPackage.JSON_ANNEX_SUBCLAUSE: return createJsonAnnexSubclause();
      case JsonPackage.JSON_ANNEX_ELEMENT: return createJsonAnnexElement();
      case JsonPackage.JSON_ANNEX_OBJECT: return createJsonAnnexObject();
      case JsonPackage.JSON_ANNEX_ARRAY: return createJsonAnnexArray();
      case JsonPackage.JSON_ANNEX_MEMBER: return createJsonAnnexMember();
      case JsonPackage.JSON_ANNEX_STRING: return createJsonAnnexString();
      case JsonPackage.JSON_ANNEX_NUMBER: return createJsonAnnexNumber();
      case JsonPackage.JSON_ANNEX_BOOLEAN: return createJsonAnnexBoolean();
      case JsonPackage.JSON_ANNEX_NULL: return createJsonAnnexNull();
      case JsonPackage.JSON_ANNEX_INTEGER: return createJsonAnnexInteger();
      case JsonPackage.JSON_ANNEX_REAL: return createJsonAnnexReal();
      case JsonPackage.JSON_ANNEX_TRUE: return createJsonAnnexTrue();
      case JsonPackage.JSON_ANNEX_FALSE: return createJsonAnnexFalse();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexLibrary createJsonAnnexLibrary()
  {
    JsonAnnexLibraryImpl jsonAnnexLibrary = new JsonAnnexLibraryImpl();
    return jsonAnnexLibrary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexSubclause createJsonAnnexSubclause()
  {
    JsonAnnexSubclauseImpl jsonAnnexSubclause = new JsonAnnexSubclauseImpl();
    return jsonAnnexSubclause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexElement createJsonAnnexElement()
  {
    JsonAnnexElementImpl jsonAnnexElement = new JsonAnnexElementImpl();
    return jsonAnnexElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexObject createJsonAnnexObject()
  {
    JsonAnnexObjectImpl jsonAnnexObject = new JsonAnnexObjectImpl();
    return jsonAnnexObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexArray createJsonAnnexArray()
  {
    JsonAnnexArrayImpl jsonAnnexArray = new JsonAnnexArrayImpl();
    return jsonAnnexArray;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexMember createJsonAnnexMember()
  {
    JsonAnnexMemberImpl jsonAnnexMember = new JsonAnnexMemberImpl();
    return jsonAnnexMember;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexString createJsonAnnexString()
  {
    JsonAnnexStringImpl jsonAnnexString = new JsonAnnexStringImpl();
    return jsonAnnexString;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexNumber createJsonAnnexNumber()
  {
    JsonAnnexNumberImpl jsonAnnexNumber = new JsonAnnexNumberImpl();
    return jsonAnnexNumber;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexBoolean createJsonAnnexBoolean()
  {
    JsonAnnexBooleanImpl jsonAnnexBoolean = new JsonAnnexBooleanImpl();
    return jsonAnnexBoolean;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexNull createJsonAnnexNull()
  {
    JsonAnnexNullImpl jsonAnnexNull = new JsonAnnexNullImpl();
    return jsonAnnexNull;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexInteger createJsonAnnexInteger()
  {
    JsonAnnexIntegerImpl jsonAnnexInteger = new JsonAnnexIntegerImpl();
    return jsonAnnexInteger;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexReal createJsonAnnexReal()
  {
    JsonAnnexRealImpl jsonAnnexReal = new JsonAnnexRealImpl();
    return jsonAnnexReal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexTrue createJsonAnnexTrue()
  {
    JsonAnnexTrueImpl jsonAnnexTrue = new JsonAnnexTrueImpl();
    return jsonAnnexTrue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonAnnexFalse createJsonAnnexFalse()
  {
    JsonAnnexFalseImpl jsonAnnexFalse = new JsonAnnexFalseImpl();
    return jsonAnnexFalse;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public JsonPackage getJsonPackage()
  {
    return (JsonPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static JsonPackage getPackage()
  {
    return JsonPackage.eINSTANCE;
  }

} //JsonFactoryImpl
