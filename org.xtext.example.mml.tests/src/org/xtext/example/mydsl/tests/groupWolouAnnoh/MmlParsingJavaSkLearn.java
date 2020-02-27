package org.xtext.example.mydsl.tests.groupWolouAnnoh;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.*;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaSkLearn {

	@Inject
	ParseHelper<MMLModel> parseHelper;

	private Map<String, String> variablesTable = new HashMap<>();

	@Test
	public void loadModel() throws Exception {
		/*
		 * MMLModel result = parseHelper.parse("datainput \"foo.csv\"\n" +
		 * "mlframework scikit-learn\n" + "algorithm DT\n" +
		 * "TrainingTest { percentageTraining 70 }\n" + "recall\n" + "");
		 */

		MMLModel result = parseHelper.parse("datainput \"iris.csv\"separator;\r\n" + "mlframework scikit-learn\r\n"
				+ "algorithm DecisionTree\r\n" + "formula \"colName\"+1+\"colName\"\r\n"
				+ "TrainingTest{percentageTraining 70 }\r\n" + "accuracy balanced_accuracy F1 macro_F1" + ""
				);
		
		/*MMLModel result += parseHelper("datainput \"Filelocation.csv\"\r\n" + 
				"mlframework scikit-learn\r\n" + 
				"algorithm DecisionTree\r\n" + 
				"1\r\n" + 
				"CrossValidation{numRepetitionCross 1 }\r\n" + 
				"accuracy balanced_accuracy F1 macro_F1");*/

		RFormula formul = result.getFormula();
		// Assertions.assertEquals(formul.getPredictive().getColName(), "variety");
		Assertions.assertNull(formul.getPredictive());
		Assertions.assertFalse(formul.getPredictors() instanceof AllVariables);
		Assertions.assertTrue(formul.getPredictors() instanceof PredictorVariables);

		EList<FormulaItem> predictors = ((PredictorVariables) formul.getPredictors()).getVars();// liste des predictives

		Assertions.assertEquals(predictors.size(), 3);

		Assertions.assertNotNull(result);
		EList<Resource.Diagnostic> errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");
		Assertions.assertEquals("iris.csv", result.getInput().getFilelocation());
		
		EList<ValidationMetric> metric = result.getValidation().getMetric();
		System.out.println(metric.toString());
		
		for(ValidationMetric m : result.getValidation().getMetric()) {
			switch (m) {
			case BALANCED_ACCURACY:
				System.out.println(m);
				
				break;
			case RECALL:
				System.out.println(m.toString()+"\n");

				break;
			case PRECISION:
				System.out.println(m.toString()+"\n");

				break;
			case ACCURACY:
				System.out.println(m);

				break;

			default:
				break;
			}
		}

	}

	@Test
	public void compileDataInput() throws Exception {
		// Récuperation du Model MML
		MMLModel result = parseHelper.parse("datainput \"foo2.csv\" separator ;\n" + "mlframework scikit-learn\n"
				+ "algorithm DT\n" + "TrainingTest { percentageTraining 70 }\n" + "recall\n" + "");
		Assertions.assertNotNull(result);

		// Recuperation de la liste des Frameworks et algorithmes
		EList<MLChoiceAlgorithm> frameworkList = result.getAlgorithms();
		MLChoiceAlgorithm frameworkEnTraitement = frameworkList.get(0);

		// Variable principal pour la creation du code final
		String importTexte = "";
		String body = "";
		String codeFinalTexte = "";

		// Import de bibliotheque python pandas pour gerer l'importation de fichier
		String pythonImport = "import pandas as pd\n";
		importTexte += pythonImport;
		importTexte += genImportPackageCode(frameworkEnTraitement.getAlgorithm(), result.getFormula(),
				result.getValidation());

		body += "\n" + genDataInputTraitement(result.getInput());

		body += "\n" + genBodypartForPredictiveRFormula(result.getFormula());

		body += "\n" + genBodypartForAlgorithm(frameworkEnTraitement.getAlgorithm());

		body += "\n" + genBodypartForValidation(result.getValidation(), frameworkEnTraitement.getAlgorithm(), result);

		codeFinalTexte = importTexte + body;

		Files.write(codeFinalTexte.getBytes(), new File("mml.py"));
		// end of Python generation
		/*
		 * Calling generated Python script (basic solution through systems call) we
		 * assume that "python" is in the path
		 */
		Process p = Runtime.getRuntime().exec("python mml.py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);

		}

	}

	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}

	private String genDataInputTraitement(DataInput dataInput) {
		String datainputpart = "";
		// Recuperation de l'objet dataInput Possedant les infos sur fichier et les
		// instructions de parsingCSV
		String fileLocation = dataInput.getFilelocation();
		String DEFAULT_COLUMN_SEPARATOR = ","; // by default
		String csv_separator = DEFAULT_COLUMN_SEPARATOR;

		CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
		if (parsingInstruction != null) {
			System.err.println("parsing instruction..." + parsingInstruction);
			csv_separator = parsingInstruction.getSep().toString();
		}
		datainputpart += "mml_data = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep="
				+ mkValueInSingleQuote(csv_separator) + ")";

		return datainputpart;
	}

	private String genImportPackageCode(MLAlgorithm algo, RFormula formula, Validation validation) {
		String importCode = "";

		// Import pour l'algorithme
		String importAlgo = "";
		if (algo instanceof DT) {
			importAlgo = "from sklearn import tree\n";
		} else if (algo instanceof SVM) {
			importAlgo = "from sklearn import svm\n";
		} else if (algo instanceof RandomForest) {
			importAlgo = "from sklearn.ensemble import RandomForestClassifier\n";
		} else if (algo instanceof LogisticRegression) {
			importAlgo = "from sklearn.linear_model import LogisticRegression\n";
		}

		// Import pour la formula (Pour R uniquement)
		String importFormula = "";
		FormulaItem predictive = formula.getPredictive();

		XFormula predictors = formula.getPredictors();

		// Import pour la validation
		String importValidate = "";
		if (validation instanceof CrossValidation) {
			importValidate = "from sklearn.model_selection import cross_validate\n";
		} else if (validation instanceof TrainingTest) {
			importValidate = "from sklearn.model_selection import train_test_split\n";
		}

		importValidate += "from sklearn import metrics\n";

		// Import Code (Algo + Formula + Validate)
		importCode = importAlgo + "\n" + importFormula + "\n" + importValidate;

		return importCode;

	}

	private String genBodypartForAlgorithm(MLAlgorithm algo) {
		String algopart = "\n";

		if (algo instanceof DT) {
			int maxd = ((DT) algo).getMax_depth();

			algopart += "clf = ";
			algopart += (maxd != 0) ? "tree.DecisionTreeClassifier(max_depth = " + maxd + ")\n"
					: "tree.DecisionTreeClassifier()\n";
		} else if (algo instanceof SVM) {

		}

		return algopart;
	}

	private String genBodypartForValidation(Validation validation, MLAlgorithm algorithm, MMLModel model) {
		String validationPart = "\n";

		// Training
		StratificationMethod stratification = validation.getStratification();
		if (stratification instanceof CrossValidation) {
			if (algorithm instanceof DT) {
				validationPart += "clf = DecisionTreeClassifier()\n" + "cross_val_score(clf, X, y, cv="
						+ stratification.getNumber() + ")\n";
			}
			if (algorithm instanceof SVM) {

			}
			if (algorithm instanceof RandomForest) {

			}
			if (algorithm instanceof LogisticRegression) {

			}
			validationPart += "cross_validate()";
		} else if (stratification instanceof TrainingTest) {
			int test_size = 1 - stratification.getNumber() / 100;
			validationPart += "test_size = " + test_size + "\n";
			validationPart += "X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)\n";

		}

		return validationPart;
	}

	private String genBodypartForPredictiveRFormula(RFormula formula) {
		String rFormulaPart = "";

		if (formula.getPredictive() != null) {

		} else {

		}

		return rFormulaPart;
	}

}