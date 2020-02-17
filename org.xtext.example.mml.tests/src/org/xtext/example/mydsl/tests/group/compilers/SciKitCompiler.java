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
		
		//Si une variable cible est défini 
		if (formula.getPredictive() != null) {
			rFormulaPart += splitingDataSet(formula.getPredictive(), formula.getPredictors());
		}
		// Si une variable cible n'est pas définis par l'utilisateur on choisi la derniere colonne 
		else {
			if(formula.getPredictors() instanceof AllVariables) {
				//TODO : deplacer ce traitement dans la fonction splitingDataSet
				rFormulaPart += "y = df.iloc[:,-1]\n";
				rFormulaPart += "X = df.drop(df.columns[-1],axis=1)\n";
			}
			else if (formula.getPredictors() instanceof PredictorVariables) {
				PredictorVariables predictors = (PredictorVariables) formula.getPredictors();
				FormulaItem predictive = predictors.getVars().get(predictors.getVars().size()-1);
				predictors.getVars().remove(predictors.getVars().size()-1);
				
				rFormulaPart += splitingDataSet(predictive, predictors);
			}
		}
		
		return rFormulaPart;
	}
	
	private static String splitingDataSet (FormulaItem predictive, XFormula predictors) {
		
		String rFormulaPart ="";
		
		if(predictive.getColName() != null) {
			rFormulaPart += "y = df[\""+predictive.getColName()+"\"]\n";
		}
		else{
			rFormulaPart += "y = df.iloc[:,"+predictive.getColumn()+"]\n";					
		}
		
		//Si tout le fichier est selectionner, couper la variable cible de l'ensemble
		if (predictors instanceof AllVariables) {
			//Si la vaiable cible est donné en nom de colonne
			if(predictive.getColName() != null) {
				rFormulaPart += "X = df.drop(columns=[\""+predictive.getColName()+"\"])\n";
			}
			// colonne cible donner par position int
			else{
				rFormulaPart += "X = df.drop(df.columns["+predictive.getColumn()+"],axis=1)\n";					
			}
			
		}
		//Si j'ai des colonnes spécifique pour l'ensemble des predictors
		else if(predictors instanceof PredictorVariables) {
			List<String> predictorWithColumnName = new ArrayList<>();
			List<Integer>  predictorWithColumnIndex = new ArrayList<>();
			
			//TODO : verifier si la list de predictors est vide 
			for(FormulaItem current : ((PredictorVariables) predictors).getVars()) {
				if(current.getColName() != null) {
					predictorWithColumnName.add(current.getColName());
				}
				else {
					predictorWithColumnIndex.add(current.getColumn());
				}
			}
			
			if(!predictorWithColumnName.isEmpty()) {
				rFormulaPart += "withColumName = df[[";
				for(String colname : predictorWithColumnName) {
					rFormulaPart += "\""+colname+"\",";
				}					
				rFormulaPart += "]]";
				rFormulaPart += "\n";
			}
			
			if(!predictorWithColumnIndex.isEmpty()) {
				rFormulaPart += "withColumIndex = df.iloc[:,[";
				for(Integer col : predictorWithColumnIndex) {
					rFormulaPart += col+",";
				}
				rFormulaPart += "]]";
				rFormulaPart += "\n";
			}
			
			if(!predictorWithColumnName.isEmpty() && !predictorWithColumnIndex.isEmpty()) {
				rFormulaPart += "X = pd.concat([withColumName,withColumIndex],axis = 1)\n";
			}
			else if(!predictorWithColumnName.isEmpty()) {
				rFormulaPart += "X = withColumName\n";
			}
			else if(!predictorWithColumnIndex.isEmpty()) {
				rFormulaPart += "X = withColumIndex\n";
			}
			
		}
		return rFormulaPart;
	}

}
