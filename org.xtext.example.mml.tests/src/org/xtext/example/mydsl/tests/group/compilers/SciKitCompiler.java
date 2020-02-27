package org.xtext.example.mydsl.tests.group.compilers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.mml.AllVariables;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.PredictorVariables;
import org.xtext.example.mydsl.mml.RFormula;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.StratificationMethod;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class SciKitCompiler {

	// private static StringBuilder imports = new StringBuilder();

	public static boolean compile(MLAlgorithm algorithm, MMLModel model, String filename) {

		// Variable principal pour la creation du code final
		String importTexte = "";
		String body = "";
		String codeFinalTexte = "";

		// Import de bibliotheque python pandas pour gerer l'importation de fichier

		importTexte += genImportPackageCode(algorithm, model.getFormula(), model.getValidation());

		body += "\n" + genDataInputTraitement(model.getInput());

		body += "\n" + genBodypartForPredictiveRFormula(model.getFormula());

		body += "\n" + genBodypartForAlgorithm(algorithm, model);

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

		// Import librairie Pandas
		importCode += "import pandas as pd\n";

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

	private static String genBodypartForAlgorithm(MLAlgorithm algo, MMLModel model) {
		String algopart = "\n";

		if (algo instanceof DT) {
			algopart += dtTraitement(algo, model);

		} else if (algo instanceof SVM) {
			algopart += svmTraitement(algo, model);
		} else if (algo instanceof RandomForest) {
			algopart += randomForestTraitement(algo, model);
		} else if (algo instanceof LogisticRegression) {
			algopart += logisticRegressionTraitement(algo, model);
		}

		return algopart;
	}

	/**
	 * Traitement logisticRegression
	 * @param algo
	 * @param model
	 * @return
	 */
	private static String logisticRegressionTraitement(MLAlgorithm algo, MMLModel model) {
		String algopart = "";

		// Définition de l'algorithme à utiliser pour le model
		algopart += "clf = LogisticRegression()\n";

		if (model.getValidation() instanceof TrainingTest) {
			algopart += "train_size = " + model.getValidation().getStratification().getNumber() + "/100 \n";
			algopart += "X_train, X_test, y_train, y_test = train_test_split(X, y, train_size=train_size)\n";

			// Creation du model avec le training set
			algopart += "clf.fit(X_train, y_train)\n";

			// Recuperation des resultats en fonction des metrics de validation definies
			algopart += train_test_accuracy_part(model.getValidation().getMetric());

		} else if (model.getValidation() instanceof CrossValidation) {

			// Set scoring setting for cross_validation operation
			algopart += "scoring_metrics = " + cross_validation_scoring_part(model.getValidation().getMetric()) + "\n";

			algopart += "\n" + "cv_results = cross_validation(clf, X, y, cv="
					+ model.getValidation().getStratification().getNumber() + "scoring = scoring_metrics)\n";

			// TODO:Recuperer les cv_results en fonction des metrics utilisés et retournés
			// le mean() sur l'indice du tableau concerné

		}

		return algopart;
	}

	/**
	 * Traitement randomForest
	 * @param algo
	 * @param model
	 * @return
	 */
	private static String randomForestTraitement(MLAlgorithm algo, MMLModel model) {
		String algopart = "";

		// Définition de l'algorithme à utiliser pour le model
		algopart += "clf = RandomForestClassifier()\n";

		if (model.getValidation() instanceof TrainingTest) {
			algopart += "train_size = " + model.getValidation().getStratification().getNumber() + "/100 \n";
			algopart += "X_train, X_test, y_train, y_test = train_test_split(X, y, train_size=train_size)\n";

			// Creation du model avec le training set
			algopart += "clf.fit(X_train, y_train)\n";

			// Recuperation des resultats en fonction des metrics de validation definies
			algopart += train_test_accuracy_part(model.getValidation().getMetric());

		} else if (model.getValidation() instanceof CrossValidation) {

			// Set scoring setting for cross_validation operation
			algopart += "scoring_metrics = " + cross_validation_scoring_part(model.getValidation().getMetric()) + "\n";

			algopart += "\n" + "cv_results = cross_validation(clf, X, y, cv="
					+ model.getValidation().getStratification().getNumber() + "scoring = scoring_metrics)\n";

			// TODO:Recuperer les cv_results en fonction des metrics utilisés et retournés
			// le mean() sur l'indice du tableau concerné

		}

		return algopart;
	}

	/**
	 * Decision tree algorithm traitement
	 * 
	 * @param algo
	 * @param model
	 * @return
	 */
	private static String dtTraitement(MLAlgorithm algo, MMLModel model) {

		String algopart = "";
		int maxd = ((DT) algo).getMax_depth();
		// Définition de l'algorithme à utiliser pour le model
		algopart += "clf = ";
		algopart += (maxd != 0) ? "tree.DecisionTreeClassifier(max_depth = " + maxd + ")\n"
				: "tree.DecisionTreeClassifier()\n";

		if (model.getValidation() instanceof TrainingTest) {
			algopart += "train_size = " + model.getValidation().getStratification().getNumber() + "/100 \n";
			algopart += "X_train, X_test, y_train, y_test = train_test_split(X, y, train_size=train_size)\n";

			// Creation du model avec le training set
			algopart += "clf.fit(X_train, y_train)\n";

			// Recuperation des resultats en fonction des metrics de validation definies
			algopart += train_test_accuracy_part(model.getValidation().getMetric());

		} else if (model.getValidation() instanceof CrossValidation) {

			// Set scoring setting for cross_validation operation
			algopart += "scoring_metrics = " + cross_validation_scoring_part(model.getValidation().getMetric()) + "\n";

			algopart += "\n" + "cv_results = cross_validation(clf, X, y, cv="
					+ model.getValidation().getStratification().getNumber() + "scoring = scoring_metrics)\n";

			// TODO:Recuperer les cv_results en fonction des metrics utilisés et retournés
			// le mean() sur l'indice du tableau concerné

		}

		return algopart;
	}

	/**
	 * Display the accuracy result for define validation metrics
	 * 
	 * @return
	 */
	private static String train_test_accuracy_part(EList<ValidationMetric> metrics) {
		String accuracy_result = "";
		for (ValidationMetric m : metrics) {
			switch (m) {
			case BALANCED_ACCURACY:
				accuracy_result+="\n"
						+"res_balanced_accuracy_score = metrics.balanced_accuracy_score(y_test,clf.predict(X_test))\n"
						+"print(res_balanced_accuracy_score)\n"
				;
				break;
			case RECALL:
				accuracy_result+="\n"
						+"res_recall_score = metrics.recall_score(y_test,clf.predict(X_test),average='micro')\n"
						+"print(res_recall_score)\n"
				;
				break;
			case PRECISION:
				accuracy_result+="\n"
						+"res_precision_score = metrics.precision_score(y_test,clf.predict(X_test),average='micro')\n"
						+"print(res_precision_score)\n"
				;
				break;
			case F1:
				accuracy_result+="\n"
						+"res_f1_score = metrics.f1_score(y_test,clf.predict(X_test),average='micro')\n"
						+"print(res_f1_score)\n"
				;
				break;
			case ACCURACY:
				accuracy_result+="\n"
						+"res_accuracy_score = metrics.accuracy_score(y_test,clf.predict(X_test))\n"
						+"print(res_accuracy_score)\n"
				;
				break;
			case MACRO_RECALL:
				accuracy_result+="\n"
						+"res_recall__macro_score = metrics.recall_score(y_test,clf.predict(X_test),average='macro')\n"
						+"print(res_recall__macro_score)\n"
				;
				break;
			case MACRO_PRECISION:
				accuracy_result+="\n"
						+"res_precision_macro_score = metrics.precision_score(y_test,clf.predict(X_test),average='macro')\n"
						+"print(res_precision_macro_score)\n"
				;
				break;
			case MACRO_F1:
				accuracy_result+="\n"
						+"res_f1_macro_score = metrics.f1_score(y_test,clf.predict(X_test),average='macro')\n"
						+"print(res_f1_macro_score)\n"
				;
				break;
			case MACRO_ACCURACY:
				accuracy_result+="\n"
						+"res_accuracy_macro_score = metrics.accuracy_score(y_test,clf.predict(X_test))\n"
						+"print(res_accuracy_macro_score)\n"
				;
				break;
			default:
				break;
			}
		}
		return accuracy_result;
	}

	/**
	 * 
	 * Set cross validation scoring setting for cross_validation operation Accuracy
	 * used if no metrics are defined
	 * 
	 * @param metrics
	 * @return
	 */
	private static String cross_validation_scoring_part(EList<ValidationMetric> metrics) {
		String scoringSet = "";

		// If no metrics are defined use accuracy for scoring
		if (metrics.isEmpty()) {
			scoringSet = "['accuracy']";

		} else {
			scoringSet = "[";
			for (ValidationMetric m : metrics) {
				switch (m) {
				case BALANCED_ACCURACY:
					scoringSet += "'balanced_accuracy',";
					break;
				case RECALL:
					scoringSet += "'recall_micro',";
					break;
				case PRECISION:
					scoringSet += "'precision_micro',";
					break;
				case F1:
					scoringSet += "'f1_micro',";
					break;
				case ACCURACY:
					scoringSet += "'accuracy',";
					break;
				case MACRO_RECALL:
					scoringSet += "'recall_macro',";
					break;
				case MACRO_PRECISION:
					scoringSet += "'precision_macro',";
					break;
				case MACRO_F1:
					scoringSet += "'f1_macro',";
					break;
				case MACRO_ACCURACY:
					scoringSet += "'accuracy',";
					break;
				default:
					break;
				}
			}
			scoringSet += "]";
		}

		return scoringSet;
	}

	/**
	 * Traitement algo SVM
	 * 
	 * @param algo
	 * @param model
	 * @return
	 */

	private static String svmTraitement(MLAlgorithm algo, MMLModel model) {
		return "";
	}

	private static String genBodypartForPredictiveRFormula(RFormula formula) {
		String rFormulaPart = "";

		// Si une variable cible est défini
		if (formula.getPredictive() != null) {
			rFormulaPart += splitingDataSet(formula.getPredictive(), formula.getPredictors());
		}
		// Si une variable cible n'est pas définis par l'utilisateur on choisi la
		// derniere colonne
		else {
			if (formula.getPredictors() instanceof AllVariables) {
				// TODO : deplacer ce traitement dans la fonction splitingDataSet
				rFormulaPart += "y = df.iloc[:,-1]\n";
				rFormulaPart += "X = df.drop(df.columns[-1],axis=1)\n";
			} else if (formula.getPredictors() instanceof PredictorVariables) {
				PredictorVariables predictors = (PredictorVariables) formula.getPredictors();
				FormulaItem predictive = predictors.getVars().get(predictors.getVars().size() - 1);
				predictors.getVars().remove(predictors.getVars().size() - 1);

				rFormulaPart += splitingDataSet(predictive, predictors);
			}
		}

		return rFormulaPart;
	}

	private static String splitingDataSet(FormulaItem predictive, XFormula predictors) {

		String rFormulaPart = "";

		if (predictive.getColName() != null) {
			rFormulaPart += "y = df[\"" + predictive.getColName() + "\"]\n";
		} else {
			rFormulaPart += "y = df.iloc[:," + predictive.getColumn() + "]\n";
		}

		// Si tout le fichier est selectionner, couper la variable cible de l'ensemble
		if (predictors instanceof AllVariables) {
			// Si la vaiable cible est donné en nom de colonne
			if (predictive.getColName() != null) {
				rFormulaPart += "X = df.drop(columns=[\"" + predictive.getColName() + "\"])\n";
			}
			// colonne cible donner par position int
			else {
				rFormulaPart += "X = df.drop(df.columns[" + predictive.getColumn() + "],axis=1)\n";
			}

		}
		// Si j'ai des colonnes spécifique pour l'ensemble des predictors
		else if (predictors instanceof PredictorVariables) {
			List<String> predictorWithColumnName = new ArrayList<>();
			List<Integer> predictorWithColumnIndex = new ArrayList<>();

			// TODO : verifier si la list de predictors est vide
			for (FormulaItem current : ((PredictorVariables) predictors).getVars()) {
				if (current.getColName() != null) {
					predictorWithColumnName.add(current.getColName());
				} else {
					predictorWithColumnIndex.add(current.getColumn());
				}
			}

			if (!predictorWithColumnName.isEmpty()) {
				rFormulaPart += "withColumName = df[[";
				for (String colname : predictorWithColumnName) {
					rFormulaPart += "\"" + colname + "\",";
				}
				rFormulaPart += "]]";
				rFormulaPart += "\n";
			}

			if (!predictorWithColumnIndex.isEmpty()) {
				rFormulaPart += "withColumIndex = df.iloc[:,[";
				for (Integer col : predictorWithColumnIndex) {
					rFormulaPart += col + ",";
				}
				rFormulaPart += "]]";
				rFormulaPart += "\n";
			}

			if (!predictorWithColumnName.isEmpty() && !predictorWithColumnIndex.isEmpty()) {
				rFormulaPart += "X = pd.concat([withColumName,withColumIndex],axis = 1)\n";
			} else if (!predictorWithColumnName.isEmpty()) {
				rFormulaPart += "X = withColumName\n";
			} else if (!predictorWithColumnIndex.isEmpty()) {
				rFormulaPart += "X = withColumIndex\n";
			}

		}
		return rFormulaPart;
	}

}