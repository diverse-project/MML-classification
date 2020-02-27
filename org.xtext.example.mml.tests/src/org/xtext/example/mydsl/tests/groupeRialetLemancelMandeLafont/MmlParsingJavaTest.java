package org.xtext.example.mydsl.tests.groupeRialetLemancelMandeLafont;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	String data = "iris.csv";
	//String data = "new-thyroid.csv";
	
	private static boolean setUpIsDone = false;
	
	@Test
	public void loadModel() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "recall\n"
				+ "");
		Assertions.assertNotNull(result);
		EList<Resource.Diagnostic> errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
		Assertions.assertEquals("foo.csv", result.getInput().getFilelocation());			
		
	}
	
	public void setUp() throws IOException {
	    if (setUpIsDone) {
	        return;
	    }
	    Classifier.reset();
	    setUpIsDone = true;
	}
	
	@Test
	/* Random Forest en CrossValidation - RF */
	public void RandomForest1Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en CrossValidation - RF */
	public void RandomForest1Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en CrossValidation - RF */
	public void RandomForest1R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en CrossValidation - RandomForest */
	public void RandomForest2Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RandomForest\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en CrossValidation - RandomForest */
	public void RandomForest2Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RandomForest\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en CrossValidation - RandomForest */
	public void RandomForest2R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm RandomForest\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en TrainingTest - RF */
	public void RandomForest3Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en TrainingTest - RF */
	public void RandomForest3Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Random Forest en TrainingTest - RF */
	public void RandomForest3R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}

	
	@Test
	/* Decision Tree en TrainingTest - DT */
	public void decisionTree1Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en TrainingTest - DT */
	public void decisionTree1Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm DT \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en TrainingTest - DT */
	public void decisionTree1R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm DT \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en TrainingTest - DecisionTree */
	public void decisionTree2Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DecisionTree \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en TrainingTest - DecisionTree */
	public void decisionTree2Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm DecisionTree \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en TrainingTest - DecisionTree */
	public void decisionTree2R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm DecisionTree \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en CrossValidation */
	public void decisionTree3Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en CrossValidation */
	public void decisionTree3Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm DT \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree en CrossValidation */
	public void decisionTree3R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm DT \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en CrossValidation */
	public void decisionTree4Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT 10 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en CrossValidation */
	public void decisionTree4Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm DT 10 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en CrossValidation */
	public void decisionTree4R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm DT 10 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en TrainingTest */
	public void decisionTree5Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT 4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en TrainingTest */
	public void decisionTree5Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm DT 4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Decision Tree avec parametre max depth en TrainingTest */
	public void decisionTree5R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm DT 4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}

	
	@Test
	/* Logistic Regression en CrossValidation */
	public void logisticRegression1Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm LogisticRegression\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* Logistic Regression en CrossValidation */
	public void logisticRegression1Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm LogisticRegression\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* Logistic Regression en CrossValidation */
	public void logisticRegression1R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm LogisticRegression\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* Logistic Regression en Training test */
	public void logisticRegression2Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm LogisticRegression\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* Logistic Regression en Training test */
	public void logisticRegression2Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm LogisticRegression\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* Logistic Regression en Training test */
	public void logisticRegression2R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm LogisticRegression\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}

	
	@Test
	/* SVM sans parametres en CrossValidation */
	public void SVM1Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn \n"
				+ "algorithm SVM \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM sans parametres en CrossValidation */
	public void SVM1Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka \n"
				+ "algorithm SVM \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM sans parametres en CrossValidation */
	public void SVM1R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R \n"
				+ "algorithm SVM \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM sans parametres en TrainingTest */
	public void SVM2Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn \n"
				+ "algorithm SVM \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM sans parametres en TrainingTest */
	public void SVM2Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka \n"
				+ "algorithm SVM \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM sans parametres en TrainingTest */
	public void SVM2R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R \n"
				+ "algorithm SVM \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
		
		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification C */
	public void SVM3Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM classification C-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification C */
	public void SVM3Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM classification C-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification C */
	public void SVM3R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM classification C-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}

	@Test
	/* SVM avec classification nu */
	/* Non supporté par R */
	public void SVM4Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM classification nu-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	@Test
	/* SVM avec classification nu */
	/* Non supporté par R */
	public void SVM4Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM classification nu-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	@Test
	/* SVM avec classification nu */
	/* Non supporté par R */
	public void SVM4R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM classification nu-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification one */
	/* Non supporté par R */
	public void SVM5Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM classification one-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification one */
	/* Non supporté par R */
	public void SVM5Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM classification one-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec classification one */
	/* Non supporté par R */
	public void SVM5R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM classification one-classification \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel linear */
	public void SVM6Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM kernel=linear \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel linear */
	public void SVM6Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM kernel=linear \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel linear */
	public void SVM6R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM kernel=linear \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel radial */
	public void SVM7Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM kernel=radial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel radial */
	public void SVM7Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM kernel=radial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel radial */
	public void SVM7R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM kernel=radial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel polynomial */
	public void SVM8Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM kernel=polynomial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel polynomial */
	public void SVM8Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM kernel=polynomial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec kernel polynomial */
	public void SVM8R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM kernel=polynomial \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec C */
	public void SVM9Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM C=0.8 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec C */
	public void SVM9Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM C=0.8 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec C */
	public void SVM9R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM C=0.8 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec gamma */
	public void SVM10Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm SVM gamma=1.5 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec gamma */
	public void SVM10Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework Weka\n"
				+ "algorithm SVM gamma=1.5 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* SVM avec gamma */
	public void SVM10R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"" + data + "\"\n"
				+ "mlframework R\n"
				+ "algorithm SVM gamma=1.5 \n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator , */
	public void Separator1Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\" separator ,\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator , */
	public void Separator1Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\" separator ,\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator , */
	public void Separator1R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\" separator ,\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator ; */
	public void Separator2Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris2.csv\" separator ;\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator ; */
	public void Separator2Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris2.csv\" separator ;\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + ajout separator ; */
	public void Separator2R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris2.csv\" separator ;\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula1Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula1Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula1R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula2Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula2Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula2R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "formula 5 ~ 1+2+3 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula3Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "formula 1 ~ 2+3+4+5 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula3Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "formula 1 ~ 2+3+4+5 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula3R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "formula 1 ~ 2+3+4+5 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula4Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula4Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula4R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+3+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula5Python() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+\"petal.length\"+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula5Weka() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework Weka\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+\"petal.length\"+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@Test
	/* Algo fonctionnant + formula personnalisée */
	public void Formula5R() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "formula \"variety\" ~ 1+2+\"petal.length\"+4 \n"
				+ "TrainingTest { percentageTraining 65 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	

		compileDataInput(model);
	}
	
	@AfterAll 
	public static void deleteOutputFile() {
        Classifier.calculateScore();
	}
	
	private void compileDataInput(MMLModel model) throws Exception {
		setUp();
		MLChoiceAlgorithm[] algos = (MLChoiceAlgorithm[]) model.getAlgorithms().toArray();
		List<String> results = new ArrayList<String>(),resultsWithTime = new ArrayList<String>();
		Instant start = Instant.now();
		
		for(int i = 0; i < algos.length; i++) {
			MLAlgorithm al = (MLAlgorithm) algos[i].getAlgorithm();
			FrameworkLang framework = algos[i].getFramework();
			if(framework.getLiteral() == "scikit-learn") {
				MmlParsingJavaCompilerPython compiler = new MmlParsingJavaCompilerPython();
				results = compiler.compileDataInput(model,al,i+1);
			}else if(framework.getLiteral() == "R") {
				MmlParsingJavaCompilerR compiler = new MmlParsingJavaCompilerR();
				results = compiler.compileDataInput(model,al,i+1);
			}else if(framework.getLiteral() == "Weka") {
				MmlParsingJavaCompilerJava compiler = new MmlParsingJavaCompilerJava();
				results = compiler.compileDataInput(model,al,i+1);
			}
		}
		
		Boolean executionReussie = !results.isEmpty();
		assertTrue(executionReussie);
		
	    Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();  
		for(String result : results) {
			resultsWithTime.add(result+"___"+timeElapsed);
		}
		Classifier.addScores(resultsWithTime);
		
	}
	
}