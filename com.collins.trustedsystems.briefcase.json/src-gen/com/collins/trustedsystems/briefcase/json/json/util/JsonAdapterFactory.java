/**
 */
package com.collins.trustedsystems.briefcase.json.json.util;

import com.collins.trustedsystems.briefcase.json.json.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Element;
import org.osate.aadl2.ModalElement;
import org.osate.aadl2.NamedElement;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.collins.trustedsystems.briefcase.json.json.JsonPackage
 * @generated
 */
public class JsonAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static JsonPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JsonAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = JsonPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected JsonSwitch<Adapter> modelSwitch =
    new JsonSwitch<Adapter>()
    {
      @Override
      public Adapter caseJsonAnnexLibrary(JsonAnnexLibrary object)
      {
        return createJsonAnnexLibraryAdapter();
      }
      @Override
      public Adapter caseJsonAnnexSubclause(JsonAnnexSubclause object)
      {
        return createJsonAnnexSubclauseAdapter();
      }
      @Override
      public Adapter caseJsonAnnexElement(JsonAnnexElement object)
      {
        return createJsonAnnexElementAdapter();
      }
      @Override
      public Adapter caseJsonAnnexObject(JsonAnnexObject object)
      {
        return createJsonAnnexObjectAdapter();
      }
      @Override
      public Adapter caseJsonAnnexArray(JsonAnnexArray object)
      {
        return createJsonAnnexArrayAdapter();
      }
      @Override
      public Adapter caseJsonAnnexMember(JsonAnnexMember object)
      {
        return createJsonAnnexMemberAdapter();
      }
      @Override
      public Adapter caseJsonAnnexString(JsonAnnexString object)
      {
        return createJsonAnnexStringAdapter();
      }
      @Override
      public Adapter caseJsonAnnexNumber(JsonAnnexNumber object)
      {
        return createJsonAnnexNumberAdapter();
      }
      @Override
      public Adapter caseJsonAnnexBoolean(JsonAnnexBoolean object)
      {
        return createJsonAnnexBooleanAdapter();
      }
      @Override
      public Adapter caseJsonAnnexNull(JsonAnnexNull object)
      {
        return createJsonAnnexNullAdapter();
      }
      @Override
      public Adapter caseJsonAnnexInteger(JsonAnnexInteger object)
      {
        return createJsonAnnexIntegerAdapter();
      }
      @Override
      public Adapter caseJsonAnnexReal(JsonAnnexReal object)
      {
        return createJsonAnnexRealAdapter();
      }
      @Override
      public Adapter caseJsonAnnexTrue(JsonAnnexTrue object)
      {
        return createJsonAnnexTrueAdapter();
      }
      @Override
      public Adapter caseJsonAnnexFalse(JsonAnnexFalse object)
      {
        return createJsonAnnexFalseAdapter();
      }
      @Override
      public Adapter caseElement(Element object)
      {
        return createElementAdapter();
      }
      @Override
      public Adapter caseNamedElement(NamedElement object)
      {
        return createNamedElementAdapter();
      }
      @Override
      public Adapter caseAnnexLibrary(AnnexLibrary object)
      {
        return createAnnexLibraryAdapter();
      }
      @Override
      public Adapter caseModalElement(ModalElement object)
      {
        return createModalElementAdapter();
      }
      @Override
      public Adapter caseAnnexSubclause(AnnexSubclause object)
      {
        return createAnnexSubclauseAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary <em>Annex Library</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexLibrary
   * @generated
   */
  public Adapter createJsonAnnexLibraryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause <em>Annex Subclause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexSubclause
   * @generated
   */
  public Adapter createJsonAnnexSubclauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement <em>Annex Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexElement
   * @generated
   */
  public Adapter createJsonAnnexElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject <em>Annex Object</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexObject
   * @generated
   */
  public Adapter createJsonAnnexObjectAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray <em>Annex Array</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexArray
   * @generated
   */
  public Adapter createJsonAnnexArrayAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember <em>Annex Member</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexMember
   * @generated
   */
  public Adapter createJsonAnnexMemberAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexString <em>Annex String</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexString
   * @generated
   */
  public Adapter createJsonAnnexStringAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexNumber <em>Annex Number</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexNumber
   * @generated
   */
  public Adapter createJsonAnnexNumberAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexBoolean <em>Annex Boolean</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexBoolean
   * @generated
   */
  public Adapter createJsonAnnexBooleanAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexNull <em>Annex Null</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexNull
   * @generated
   */
  public Adapter createJsonAnnexNullAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger <em>Annex Integer</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexInteger
   * @generated
   */
  public Adapter createJsonAnnexIntegerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal <em>Annex Real</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexReal
   * @generated
   */
  public Adapter createJsonAnnexRealAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexTrue <em>Annex True</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexTrue
   * @generated
   */
  public Adapter createJsonAnnexTrueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.collins.trustedsystems.briefcase.json.json.JsonAnnexFalse <em>Annex False</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.collins.trustedsystems.briefcase.json.json.JsonAnnexFalse
   * @generated
   */
  public Adapter createJsonAnnexFalseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.osate.aadl2.Element <em>Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.osate.aadl2.Element
   * @generated
   */
  public Adapter createElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.osate.aadl2.NamedElement <em>Named Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.osate.aadl2.NamedElement
   * @generated
   */
  public Adapter createNamedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.osate.aadl2.AnnexLibrary <em>Annex Library</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.osate.aadl2.AnnexLibrary
   * @generated
   */
  public Adapter createAnnexLibraryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.osate.aadl2.ModalElement <em>Modal Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.osate.aadl2.ModalElement
   * @generated
   */
  public Adapter createModalElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.osate.aadl2.AnnexSubclause <em>Annex Subclause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.osate.aadl2.AnnexSubclause
   * @generated
   */
  public Adapter createAnnexSubclauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //JsonAdapterFactory