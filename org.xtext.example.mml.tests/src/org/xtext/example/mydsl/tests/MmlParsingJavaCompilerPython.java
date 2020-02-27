package org.xtext.example.mydsl.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.mml.*;

import com.google.common.io.Files;

public class MmlParsingJavaCompilerPython extends Compiler {
	
	
	public List<String> compileDataInput(MMLModel model, MLAlgorithm al, int numAlgo) throws Exception {
		
		super.init();
		
		List<String> results = new ArrayList<String>();
		
		DataInput dataInput = model.getInput();
		String fileLocation = dataInput.getFilelocation();
		
		/* Construction imports */
		imports = addInstruction(imports, "import pandas as pd"); 
		imports = addInstruction(imports, "from sklearn.model_selection import train_test_split\r");
		imports = addInstruction(imports, "from sklearn import tree\r");
		imports = addInstruction(imports, "import os");

		/* Separator */
		String csv_separator = getParsingInstruction(dataInput);
		
		/* csvReading */
		csvReading = addInstruction(csvReading, "mml_data = pd.read_csv(os.path.abspath(os.path.dirname(__file__))+'\\" + fileLocation + "', sep=" + mkValueInSingleQuote(csv_separator) + ",encoding='utf-8')");	
		
		/* Formula */
		this.formulaTreatment(model);
		
		/* Algorithm */		
		this.algorithmTreatment(al);
		
		/* Stratification and metrics */
		this.stratificationMetricsTreatment(model);
		
		/* Construction program */
		program = addInstruction(program, imports);
		program = addInstruction(program, csvReading);
		program = addInstruction(program, x);
		program = addInstruction(program, y);
		program = addInstruction(program, algorithm);
		program = addInstruction(program, stratificationAndMetrique);
		program = addInstruction(program, printScores);
		
		/* Generation program */
		Files.write(program.getBytes(), new File("output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_" + numAlgo + ".py"));

		/* Execution program */
		try {
            System.out.println("**********");
            results = runProcess("python output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_" + numAlgo + ".py", results, "scikit-learn");
            System.out.println("**********");
        } catch (Exception e) {
            e.printStackTrace();
        }
		return results;
    }

	/* Formula treatment */
	public void formulaTreatment(MMLModel model) {
	
		super.formulaTreatment(model);
		
		if (f == null) {
			y = "Y = mml_data.iloc[:,-1]\n";
			x = "X = mml_data.iloc[:, :-1]\n";
		}
		else {
			//Predictives
			FormulaItem fi = f.getPredictive();
			String colName = fi.getColName() + "";
			int colIndex = fi.getColumn();
			String column = "";
			
			if (!colName.equals("null") && colName.length() > 0) {
				column = "\"" + colName + "\"";
			}
			else {
				column = "mml_data.columns[" + (colIndex-1) + "]";			
			}
			y = addInstruction(y, "Y = mml_data[" + column + "]");
			
			//Predictors
			XFormula preds = f.getPredictors();
			if (preds instanceof AllVariables) {
				x = addInstruction(x, "X = mml_data.drop(columns=["+column+ "])");
			}
			else if (preds instanceof PredictorVariables) {
				FormulaItem[] predictors = (FormulaItem[]) ((PredictorVariables) preds).getVars().toArray();
				String predictorsCol = "";
				FormulaItem item;
				for(int i = 0; i < predictors.length; i++) {
					item = predictors[i];
					colName = item.getColName() + "";
					colIndex = item.getColumn();
					column = "";
					if (!colName.equals("null") && colName.length() > 0) {
						column = colName;
					}
					else {
						column = "mml_data.columns["+(colIndex-1)+"]";
					}
					predictorsCol += column + ",";
				}
				if (predictorsCol.length() > 0) {
					predictorsCol = predictorsCol.substring(0, predictorsCol.length() - 1);
				}
				x = addInstruction(x, "X = mml_data[[" + predictorsCol + "]]");
			}
		}
	}
	
	/* Algorithm treatment */
	public void algorithmTreatment(MLAlgorithm al) {
		
		if (al instanceof DT) {
			DT defAlg = (DT) al;
			int maxDepth = defAlg.getMax_depth();
			if(maxDepth == 0)
				algorithm = addInstruction(algorithm, "clf = tree.DecisionTreeClassifier()");
			else
				algorithm = addInstruction(algorithm, "clf = tree.DecisionTreeClassifier(max_depth="+maxDepth+")");
		}
		else if (al instanceof SVM) {
			SVM defAlg = (SVM) al;
			
			/* Si absence de gamma dans le programme, on aura null comme valeur */
			String gamma = (defAlg.getGamma() == null) ? "'auto'" : defAlg.getGamma();
			
			/* Si absence de c dans le programme, on aura null comme valeur */
			float c = (float) ((defAlg.getC() == null) ? 1.0 : Float.valueOf(defAlg.getC()));
			
			/* Si absence de kernel dans le programme, on aura de base "linear" */
			String kernel = "";
			if (defAlg.isKernelSpecified()) {
				kernel = defAlg.getKernel().getLiteral();
				switch (kernel) {
					case "radial" 		: kernel = "'rbf'"; 		break;
					case "polynomial" 	: kernel = "'poly'"; 		break;
					case "linear" 		: kernel = "'linear'"; 		break;
				}	
			}
			else {
				kernel = "'linear'";
			}
			
			/* Si absence de classification dans le programme, on aura de base "C-classification" */
			String classification = "";
			if (defAlg.isClassificationSpecified()) {
				
				classification = defAlg.getSvmclassification().getLiteral();
				
				switch (classification) {
				
					case "C-classification" :
						imports 	= addInstruction(imports, "from sklearn.svm import SVC");
						algorithm	= addInstruction(algorithm, "clf = SVC(C=" + c + ","
									+ "gamma=" + gamma + "," 
									+ "kernel=" + kernel + ")");
						break;
						
					case "nu-classification" :
						imports 	= addInstruction(imports, "from sklearn.svm import NuSVC");
						algorithm	= addInstruction(algorithm, "clf = NuSVC("
									+ "gamma=" + gamma + ","
									+ "kernel=" + kernel + ")");
						break;
						
					case "one-classification" : 
						imports 	= addInstruction(imports, "from sklearn.svm import LinearSVC");
						algorithm	= addInstruction(algorithm, "clf = LinearSVC(C=" + c + ")");	
						break;
				}
			}
			else {
				imports 	= addInstruction(imports, "from sklearn.svm import SVC");
				algorithm	= addInstruction(algorithm, "clf = SVC(C=" + c + ","
							+ "gamma=" + gamma + "," 
							+ "kernel=" + kernel + ")");
			}
		}
		else if (al instanceof RandomForest) {
			imports = addInstruction(imports, "from sklearn.ensemble import RandomForestClassifier");
			algorithm = addInstruction(algorithm, "clf = RandomForestClassifier(n_estimators=100)");
		}
		else if (al instanceof LogisticRegression) {
			imports = addInstruction(imports, "from sklearn.linear_model import LogisticRegression");
			algorithm = addInstruction(algorithm, "clf = LogisticRegression(solver='lbfgs', multi_class='auto')");
		}
	}
	
	/* Stratification and Metrics treatment */
	public void stratificationMetricsTreatment(MMLModel model) {
		
		Validation validation = model.getValidation();
		StratificationMethod strat = validation.getStratification();
		EList<ValidationMetric> metric = validation.getMetric();
		ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
		
		int number; 
		boolean accuracy = false;
		String metrique = "";
		
		if (strat instanceof CrossValidation) {
			number = strat.getNumber();
			metriques = "scoring = [";
			for (int i = 0 ; i < metriquesArray.length ; i++) {
				metrique = metriquesArray[i].getLiteral().toLowerCase();
				if ((metrique.equals("accuracy") || metrique.equals("macro_accuracy")) && accuracy) {
					if (i == metriquesArray.length - 1) {
						metriques += "]\n";
					}
					continue;
				}
				if (metrique.equals("accuracy") || metrique.equals("macro_accuracy")) {
					accuracy = true;
				}
				if (metrique.contains("macro")) {
					metrique = metrique.substring(6, metrique.length());
					if (!metrique.equals("accuracy")) {
						metrique += "_macro";
					}
				}
				else if (!metrique.equals("accuracy") && !metrique.equals("balanced_accuracy")) {
						metrique += "_micro";
				}
				if (i != metriquesArray.length - 1) {
					metriques += "'" + metrique + "',";
				}
				else {
					metriques+= "'"+metrique + "'] \n";
				}
				
				printScores += "print('" + metrique + "___'+str(sum(scores['test_" + metrique + "']) / len(scores['test_" + metrique + "'])))\n";
			}
			
			imports = addInstruction(imports, "from sklearn.model_selection import cross_validate");
			stratificationAndMetrique = addInstruction(stratificationAndMetrique, metriques + "scores = cross_validate(clf,X, Y, scoring=scoring,cv=" + number + ")");
		} 
		else if (strat instanceof TrainingTest) {
			metrique = "";
			number = strat.getNumber();
			stratificationAndMetrique = addInstruction(stratificationAndMetrique, "test_size = 0." + number + "\nX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=test_size)\nclf.fit(X_train, y_train)");
			for (int i = 0 ; i < metriquesArray.length ; i++) {
				metrique = metriquesArray[i].getLiteral();
				if (metrique == "accuracy") {
					imports = addInstruction(imports, "from sklearn.metrics import accuracy_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "accuracy = accuracy_score(y_test, clf.predict(X_test))\nprint('accuracy___'+str(accuracy))");
				}
				else if (metrique == "balanced_accuracy") {
					imports = addInstruction(imports, "from sklearn.metrics import balanced_accuracy_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "accuracy = balanced_accuracy_score(y_test, clf.predict(X_test))\nprint('balanced accuracy___'+str(accuracy))");
				}
				else if (metrique == "recall") {
					imports = addInstruction(imports, "from sklearn.metrics import recall_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "recall = recall_score(y_test, clf.predict(X_test),average='micro')\nprint('recall___'+str(recall))");
				}
				else if (metrique == "macro_recall") {
					imports = addInstruction(imports, "from sklearn.metrics import recall_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "recall = recall_score(y_test, clf.predict(X_test),average='macro')\nprint('macro recall___'+str(recall))");
				}
				else if (metrique == "precision") {
					imports = addInstruction(imports, "from sklearn.metrics import precision_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "precision = precision_score(y_test, clf.predict(X_test),average='micro')\nprint('precision___'+str(precision))");
				}
				else if (metrique == "macro_precision") {
					imports = addInstruction(imports, "from sklearn.metrics import precision_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "precision = precision_score(y_test, clf.predict(X_test),average='macro')\nprint('macro precision___'+str(precision))");
				}
				else if(metrique == "F1") {
					imports = addInstruction(imports, "from sklearn.metrics import f1_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "F1 = f1_score(y_test, clf.predict(X_test),average='micro')\nprint('F1___'+str(F1))");
				}
				else if (metrique == "macro_F1") {
					imports = addInstruction(imports, "from sklearn.metrics import f1_score");
					stratificationAndMetrique = addInstruction(stratificationAndMetrique, "F1 = f1_score(y_test, clf.predict(X_test),average='macro')\nprint('macro F1___'+str(F1))");
				}
			}
		}
	}
}