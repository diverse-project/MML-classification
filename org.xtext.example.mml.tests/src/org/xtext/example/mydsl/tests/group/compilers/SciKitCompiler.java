package org.xtext.example.mydsl.tests.group.compilers;

import java.io.File;
import java.io.IOException;

import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.RFormula;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.StratificationMethod;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class SciKitCompiler {

	// private static StringBuilder imports = new StringBuilder();

	public static boolean compiler(MLAlgorithm algorithm, MMLModel model, String filename) {

		// Variable principal pour la creation du code final
		String importTexte = "";
		String body = "";
		String codeFinalTexte = "";

		// Import de bibliotheque python pandas pour gerer l'importation de fichier
		String pythonImport = "import pandas as pd\n";
		importTexte += pythonImport;
		importTexte += genImportPackageCode(algorithm, model.getFormula(), model.getValidation());

		body += "\n" + genDataInputTraitement(model.getInput());

		body += "\n" + genBodypartForPredictiveRFormula(model.getFormula());

		body += "\n" + genBodypartForAlgorithm(algorithm);

		body += "\n" + genBodypartForValidation(model.getValidation());

		codeFinalTexte = importTexte + body;

		try {
			Files.write(codeFinalTexte.getBytes(), new File(filename + ".py"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}

	private static String genDataInputTraitement(DataInput dataInput) {
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

	private static String genImportPackageCode(MLAlgorithm algo, RFormula formula, Validation validation) {
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

	private static String genBodypartForAlgorithm(MLAlgorithm algo) {
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

	private static String genBodypartForValidation(Validation validation) {
		String validationPart = "\n";

		// Training
		StratificationMethod stratification = validation.getStratification();
		if (stratification instanceof CrossValidation) {
			validationPart += "";
		} else if (stratification instanceof TrainingTest) {
			int test_size = 1 - stratification.getNumber() / 100;
			validationPart += "test_size = " + test_size + "\n";

		}

		return validationPart;
	}

	private static String genBodypartForPredictiveRFormula(RFormula formula) {
		String rFormulaPart = "";

		if (formula.getPredictive() != null) {

		} else {

		}

		return rFormulaPart;
	}

}
