/**
 * generated by Xtext 2.19.0
 */
package org.xtext.example.mydsl.mml;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>MML Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.xtext.example.mydsl.mml.MMLModel#getInput <em>Input</em>}</li>
 *   <li>{@link org.xtext.example.mydsl.mml.MMLModel#getAlgorithms <em>Algorithms</em>}</li>
 *   <li>{@link org.xtext.example.mydsl.mml.MMLModel#getFormula <em>Formula</em>}</li>
 *   <li>{@link org.xtext.example.mydsl.mml.MMLModel#getValidation <em>Validation</em>}</li>
 * </ul>
 *
 * @see org.xtext.example.mydsl.mml.MmlPackage#getMMLModel()
 * @model
 * @generated
 */
public interface MMLModel extends EObject
{
  /**
   * Returns the value of the '<em><b>Input</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Input</em>' containment reference.
   * @see #setInput(DataInput)
   * @see org.xtext.example.mydsl.mml.MmlPackage#getMMLModel_Input()
   * @model containment="true"
   * @generated
   */
  DataInput getInput();

  /**
   * Sets the value of the '{@link org.xtext.example.mydsl.mml.MMLModel#getInput <em>Input</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Input</em>' containment reference.
   * @see #getInput()
   * @generated
   */
  void setInput(DataInput value);

  /**
   * Returns the value of the '<em><b>Algorithms</b></em>' containment reference list.
   * The list contents are of type {@link org.xtext.example.mydsl.mml.MLChoiceAlgorithm}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Algorithms</em>' containment reference list.
   * @see org.xtext.example.mydsl.mml.MmlPackage#getMMLModel_Algorithms()
   * @model containment="true"
   * @generated
   */
  EList<MLChoiceAlgorithm> getAlgorithms();

  /**
   * Returns the value of the '<em><b>Formula</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Formula</em>' containment reference.
   * @see #setFormula(RFormula)
   * @see org.xtext.example.mydsl.mml.MmlPackage#getMMLModel_Formula()
   * @model containment="true"
   * @generated
   */
  RFormula getFormula();

  /**
   * Sets the value of the '{@link org.xtext.example.mydsl.mml.MMLModel#getFormula <em>Formula</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Formula</em>' containment reference.
   * @see #getFormula()
   * @generated
   */
  void setFormula(RFormula value);

  /**
   * Returns the value of the '<em><b>Validation</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Validation</em>' containment reference.
   * @see #setValidation(Validation)
   * @see org.xtext.example.mydsl.mml.MmlPackage#getMMLModel_Validation()
   * @model containment="true"
   * @generated
   */
  Validation getValidation();

  /**
   * Sets the value of the '{@link org.xtext.example.mydsl.mml.MMLModel#getValidation <em>Validation</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Validation</em>' containment reference.
   * @see #getValidation()
   * @generated
   */
  void setValidation(Validation value);

} // MMLModel
