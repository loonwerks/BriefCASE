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
package com.collins.trustedsystems.briefcase.json.json;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage
 * @generated
 */
public interface JsonFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  JsonFactory eINSTANCE = com.collins.trustedsystems.briefcase.json.json.impl.JsonFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Annex Library</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Library</em>'.
   * @generated
   */
  JsonAnnexLibrary createJsonAnnexLibrary();

  /**
   * Returns a new object of class '<em>Annex Subclause</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Subclause</em>'.
   * @generated
   */
  JsonAnnexSubclause createJsonAnnexSubclause();

  /**
   * Returns a new object of class '<em>Annex Element</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Element</em>'.
   * @generated
   */
  JsonAnnexElement createJsonAnnexElement();

  /**
   * Returns a new object of class '<em>Annex Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Object</em>'.
   * @generated
   */
  JsonAnnexObject createJsonAnnexObject();

  /**
   * Returns a new object of class '<em>Annex Array</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Array</em>'.
   * @generated
   */
  JsonAnnexArray createJsonAnnexArray();

  /**
   * Returns a new object of class '<em>Annex Member</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Member</em>'.
   * @generated
   */
  JsonAnnexMember createJsonAnnexMember();

  /**
   * Returns a new object of class '<em>Annex String</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex String</em>'.
   * @generated
   */
  JsonAnnexString createJsonAnnexString();

  /**
   * Returns a new object of class '<em>Annex Number</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Number</em>'.
   * @generated
   */
  JsonAnnexNumber createJsonAnnexNumber();

  /**
   * Returns a new object of class '<em>Annex Boolean</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Boolean</em>'.
   * @generated
   */
  JsonAnnexBoolean createJsonAnnexBoolean();

  /**
   * Returns a new object of class '<em>Annex Null</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Null</em>'.
   * @generated
   */
  JsonAnnexNull createJsonAnnexNull();

  /**
   * Returns a new object of class '<em>Annex Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Integer</em>'.
   * @generated
   */
  JsonAnnexInteger createJsonAnnexInteger();

  /**
   * Returns a new object of class '<em>Annex Real</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex Real</em>'.
   * @generated
   */
  JsonAnnexReal createJsonAnnexReal();

  /**
   * Returns a new object of class '<em>Annex True</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex True</em>'.
   * @generated
   */
  JsonAnnexTrue createJsonAnnexTrue();

  /**
   * Returns a new object of class '<em>Annex False</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Annex False</em>'.
   * @generated
   */
  JsonAnnexFalse createJsonAnnexFalse();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  JsonPackage getJsonPackage();

} //JsonFactory
