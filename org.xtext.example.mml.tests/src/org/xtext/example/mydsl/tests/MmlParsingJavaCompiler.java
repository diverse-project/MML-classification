package org.xtext.example.mydsl.tests;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.*;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaCompiler {
	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	public MMLModel getModel(String fileName) throws Exception {
		File file = new File("prog1.mml"); 
		BufferedReader br = new BufferedReader(new FileReader(file));
		String prog = "", line;
		while ((line = br.readLine()) != null) {
			prog += line;
		}
		br.close();
		System.out.println(prog);
		return parseHelper.parse(prog);
	}
	
	@Test
	public void loadModel1() throws Exception {	
		MMLModel model = getModel("prog1.mml");
		Assertions.assertNotNull(model);
		EList<Resource.Diagnostic> errors = model.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
		/*Assertions.assertEquals("iris.csv", result.getInput().getFilelocation());	
		Assertions.assertEquals("scikit-learn", result.getAlgorithm().getFramework().toString());
		Assertions.assertEquals(DTImpl.class, result.getAlgorithm().getAlgorithm().getClass());	
		System.out.println(result.getAlgorithm().getAlgorithm().getClass());*/
	}
	
	@Test
	public void compileDataInput() throws Exception {
		MMLModel model = getModel("prog1.mml");
		DataInput dataInput = model.getInput();
		String fileLocation = dataInput.getFilelocation();
		String pythonImport = "import pandas as pd\n"; 
		String pythonImport2 = 	"from sklearn.model_selection import train_test_split\r\n" + 
								"from sklearn import tree\r\n";
		pythonImport+=pythonImport2;
		String DEFAULT_COLUMN_SEPARATOR = ","; // by default
		String csv_separator = DEFAULT_COLUMN_SEPARATOR;
		CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
		
		if (parsingInstruction != null) {			
			System.err.println("parsing instruction..." + parsingInstruction);
			csv_separator = parsingInstruction.getSep().toString();
		}
		
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep=" + mkValueInSingleQuote(csv_separator) + ")\n";							
		String x ="", y="";
		
		//Formula
		RFormula f = model.getFormula();
		//Predictives
		FormulaItem fi = f.getPredictive();
		String colName = fi.getColName();
		int colIndex = fi.getColumn();
		String column = "";
		if(colName.length() > 0 )
			column = colName;
		else
			column = colIndex+"";
		y = "Y = mml_data[\""+column+"\"]\n";
		
		//Predictors
		XFormula preds = f.getPredictors();
		if(preds instanceof AllVariables)
			x = "X = mml_data.drop(columns=[\""+column+ "\"])\n";
		else if(preds instanceof PredictorVariables) {
			FormulaItem[] predictors = (FormulaItem[]) ((PredictorVariables) preds).getVars().toArray();
			String predictorsCol = "";
			FormulaItem item;
			for(int i = 0; i < predictors.length; i++) {
				item = predictors[i];
				colName = item.getColName();
				colIndex = item.getColumn();
				column = "";
				if(colName.length() > 0 )
					column = colName;
				else
					column = colIndex+"";
				predictorsCol+="'"+column+"',";
			}
			if(predictorsCol.length() > 0) {
				predictorsCol =  predictorsCol.substring(0, predictorsCol.length()-1);
			}
			x = "X = mml_data[["+predictorsCol+"]]\n";
		}
		
		
		//Algorithm
		MLAlgorithm al = model.getAlgorithm().getAlgorithm();
		
		String algorithm = "";
		if (al instanceof DT) {
			DT defAlg = (DT) al;
			int maxDepth = defAlg.getMax_depth();
			if(maxDepth == 0)
				algorithm = "clf = tree.DecisionTreeClassifier()\n";
			else
				algorithm = "clf = tree.DecisionTreeClassifier(max_depth="+maxDepth+")\n";
		}
		else if (al instanceof SVM) {
SVM defAlg = (SVM) al;
			
			/* Si absence de gamma dans le programme, on aura null comme valeur */
			String gamma = (defAlg.getGamma() == null) ? "'auto'" : defAlg.getGamma();
			
			/* Si absence de c dans le programme, on aura null comme valeur */
			float c = (float) ((defAlg.getC() == null) ? 1.0 : Float.valueOf(defAlg.getC()));
			
			/* Si absence de kernel dans le programme, on aura de base "linear" */
			String kernel = defAlg.getKernel().getLiteral();
			switch (kernel) {
				case "radial" 		: kernel = "'rbf'"; 		break;
				case "polynomial" 	: kernel = "'poly'"; 		break;
				case "linear" 		: kernel = "'linear'"; 		break;
			}	
			
			/* Si absence de classification dans le programme, on aura de base "C-classification" */
			String classification = defAlg.getSvmclassification().getLiteral();
			
			String importSVC = "";
			String ecrireAlgo = "";
			
			switch (classification) {
			
				case "C-classification" :
					importSVC 	= "from sklearn.svm import SVC \n";
					ecrireAlgo	= "clf = SVC(gamma=" + gamma + ")\n";
					ecrireAlgo 	+= "clf.fit(X, y)\n";
					ecrireAlgo	+= "SVC(C=" + c + ","
								+ "gamma=" + gamma + "," 
								+ "kernel=" + kernel + ")\n";
					ecrireAlgo	+= "clf.predict()\n";
					break;
					
				case "nu-classification" :
					importSVC 	= "from sklearn.svm import NuSVC \n";
					ecrireAlgo	="clf = NuSVC(gamma=" + gamma + ")\n";
					ecrireAlgo 	+= "clf.fit(X, y)\n";
					ecrireAlgo	+= "NuSVC("
								+ "gamma=" + gamma + ","
								+ "kernel=" + kernel + ")\n";
					ecrireAlgo	+= "clf.predict()\n";	
					break;
					
				case "one-classification" : 
					importSVC 	= "from sklearn.svm import LinearSVC \n";
					ecrireAlgo	="clf = LinearSVC(C=" + c + ")\n";
					ecrireAlgo 	+= "clf.fit(X, y)\n";
					ecrireAlgo	+= "";
					ecrireAlgo	+= "clf.predict()\n";	
					break;
			}
			pythonImport+=importSVC;
			algorithm+=ecrireAlgo;
		}
		else if (al instanceof RandomForest) {
			RandomForest defAlg = (RandomForest) al;
			algorithm = "clf = RandomForestClassifier()";
		}
		else if (al instanceof LogisticRegression) {
			LogisticRegression defAlg = (LogisticRegression) al;
			
			// Traitement LR
		}
		
		
		Validation validation = model.getValidation();
		StratificationMethod strat = validation.getStratification();
		EList<ValidationMetric> metric = validation.getMetric();
		ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
		String stratificationAndMetrique = "";
		int number; 
		String metriques = "";
		String printScores = "";
		
		if (strat instanceof CrossValidation) {
			number = strat.getNumber();
			metriques = "scoring = [";
			for(int i = 0; i < metriquesArray.length; i++) {
				if(i != metriquesArray.length-1)
					metriques+= "'"+metriquesArray[i].getLiteral() + "',";
				else
					metriques+= "'"+metriquesArray[i].getLiteral() + "'] \n";
				printScores+="print('"+metriquesArray[i].getLiteral()+" : ')\n";
				printScores+="print(scores['test_"+metriquesArray[i].getLiteral()+"'])\n";
			}
			stratificationAndMetrique = metriques+"scores = cross_validate(clf,X, Y, scoring=scoring,cv="+number+")\n";
		} 
		else if (strat instanceof TrainingTest) {
			String metrique = "";
			number = strat.getNumber();
			stratificationAndMetrique = "test_size = 0."+number+"\nX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=test_size)\nclf.fit(X_train, y_train)\n";
			for(int i = 0; i < metriquesArray.length; i++) {
				metrique = metriquesArray[i].getLiteral();
				if(metrique == "accuracy") {
					pythonImport += "from sklearn.metrics import accuracy_score\n";
					stratificationAndMetrique+="accuracy = accuracy_score(y_test, clf.predict(X_test))\nprint('accuracy :')\nprint(accuracy)\n";
				}else if(metrique == "recall") {
					pythonImport += "from sklearn.metrics import recall_score\n";
					stratificationAndMetrique+="recall = recall_score(y_test, clf.predict(X_test),average='micro')\nprint('recall :')\nprint(recall)\n";
				}else if(metrique == "precision") {
					pythonImport += "from sklearn.metrics import precision_score\n";
					stratificationAndMetrique+="precision = precision_score(y_test, clf.predict(X_test),average='micro')\nprint('precision :')\nprint(precision)\n";
				}else if(metrique == "F1") {
					pythonImport += "from sklearn.metrics import f1_score\n";
					stratificationAndMetrique+="F1 = f1_score(y_test, clf.predict(X_test),average='micro')\nprint('F1 :')\nprint(F1)\n";
				}
			}
		}
		
		//build python code
		String pythonCode = addPythonText("", pythonImport);
		pythonCode = addPythonText(pythonCode, csvReading);
		pythonCode = addPythonText(pythonCode, x);
		pythonCode = addPythonText(pythonCode, y);
		pythonCode = addPythonText(pythonCode, algorithm);
		pythonCode = addPythonText(pythonCode, stratificationAndMetrique);
		pythonCode = addPythonText(pythonCode, printScores);
		
		Files.write(pythonCode.getBytes(), new File("mml.py"));
		// end of Python generation
		
		
		/*
		 * Calling generated Python script (basic solution through systems call)
		 * we assume that "python" is in the path
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
	
	private String addPythonText(String pythonCode, String toAdd) {
		return pythonCode + toAdd;
	}
}
