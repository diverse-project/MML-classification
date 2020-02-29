package org.xtext.example.mydsl.tests.groupMYST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.SVMClassification;
import org.xtext.example.mydsl.mml.SVMKernel;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class ScikitLearnCompilateur implements Compilateur {
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;
	private List<ValidationMetric> metrics;
	

	
	public void execute() throws IOException {
		
		String pythonImport = "import pandas as pd\r\n"; 
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(dataInput.getFilelocation()) + ", sep=" + 
		mkValueInSingleQuote(separator) + ")\r\n";	
		
		String predictivestr = "";
		if (predictive != null ) {
			predictivestr = "X = mml_data.drop(columns=[\""+predictive.getColName()+"\"])"+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X = mml_data.drop(columns=mml_data.columns[len(mml_data.columns)-1])"+"\r\n";
		}
		
		//TODO
		String predictorstr ="Y = mml_data[mml_data.columns[len(mml_data.columns)-1]] "+"\r\n";
		
		String NameAlgo= "";
		String algostr ="";
		if(algo instanceof SVM) {
			SVM svm = (SVM)algo;
			String c = svm.getC();
			String gamma = svm.getGamma();

			if(c == null) {
				c = "1.0";
			}
			if (gamma== null) {
				gamma = mkValueInSingleQuote("scale");
			}
			
			if(svm.isClassificationSpecified()) {
				SVMClassification classification = svm.getSvmclassification();
				NameAlgo = classification.getLiteral();
				switch(NameAlgo) {
					case "C-classification":
						pythonImport +="from sklearn.svm import SVC"+"\r\n";
						if(svm.isKernelSpecified()) {
						algostr = "algo = SVC(C="+c+", kernel="+mkValueInSingleQuote(svm.getKernel().getName())+",gamma="+mkValueInSingleQuote(gamma)+")"+"\r\n";
						}else {
							algostr = "algo = SVC(C="+c+",gamma="+gamma+")"+"\r\n";
						}
						break;
					case "nu-classification":
						pythonImport +="from sklearn.svm import NuSVC"+"\r\n";
						if(svm.isKernelSpecified()) {
						algostr = "algo = NuSVC(kernel="+mkValueInSingleQuote(svm.getKernel().getName())+",gamma="+mkValueInSingleQuote(gamma)+")"+"\r\n";
						}else {
							algostr = "algo = NuSVC(gamma="+gamma+")"+"\r\n";
						}
						break;
					case "one-classification":
						pythonImport +="from sklearn.svm import OneClassSVM"+"\r\n";
						if(svm.isKernelSpecified()) {
						algostr = "algo = OneClassSVM(kernel="+ mkValueInSingleQuote(svm.getKernel().getName())+",gamma="+mkValueInSingleQuote(gamma)+")"+"\r\n";
						}else {
							algostr = "algo = OneClassSVM(gamma="+gamma+")"+"\r\n";
							
						}
						break;
						}
			}
			
	
		}else if(algo instanceof DT) {
			 DT dt = (DT)algo;
				int max_depth = dt.getMax_depth();
			pythonImport += "from sklearn.tree import DecisionTreeClassifier"+"\r\n";
			NameAlgo = "Decision Tree";
			if(max_depth != 0) {
				algostr = "algo = DecisionTreeClassifier(max_depth="+max_depth+")"+"\r\n";
			}else {
				algostr = "algo = DecisionTreeClassifier()"+"\r\n";
			}
		}else if(algo instanceof RandomForest ) {
			pythonImport += "from sklearn.ensemble import RandomForestClassifier"+"\r\n";
			algostr = "algo = RandomForestClassifier()\r\n" ;
			NameAlgo = "Random Forest";
		}else if(algo instanceof LogisticRegression) {
			pythonImport += "from sklearn.linear_model import LogisticRegression"+"\r\n";
			algostr = "algo = LogisticRegression() "+"\r\n";
			NameAlgo = "Logistic Regression";
		}
		
		String val = "";
		double num;
		if (validation.getStratification() instanceof CrossValidation) {
		 
				 pythonImport += "from sklearn.model_selection import cross_val_predict"+"\r\n";
				 num = validation.getStratification().getNumber();
			  val = "y_pred = cross_val_predict(algo, X, Y, cv="+(int) num+")"+"\r\n";
		}else if (validation.getStratification() instanceof TrainingTest) {
			  pythonImport += "from sklearn.model_selection import train_test_split"+"\r\n";
			  num = (validation.getStratification().getNumber())*0.01;
			  val = "test_size =" +num+"\r\n" + 
			  		"X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=test_size)"+"\r\n";
			  val +="algo.fit(X_train,Y_train)"+"\r\n";
			  val += "y_pred = algo.predict(X_test)"+"\r\n";
			  val += "Y = Y_test"+"\r\n";

		}
		
		String affiche ="";
		String metric ="";
		boolean writeInFile = false;
		for (ValidationMetric laMetric : metrics) {
		switch(laMetric.getLiteral()) {
		  case "balanced_accuracy":
				pythonImport +="from sklearn.metrics import balanced_accuracy_score"+"\r\n";
			   metric +="balanceA = balanced_accuracy_score(Y, y_pred)"+"\r\n";
			   affiche  +="print(balanceA)";
		    break;
		  case "precision":
			  pythonImport +="from sklearn.metrics import precision_score"+"\r\n";
			  metric +="precision = precision_score(Y, y_pred, average='micro')"+"\r\n";
			   affiche  +="print(precision)";
		    break;
		  case "recall":
			  pythonImport +="from sklearn.metrics import recall_score"+"\r\n";
			  metric +="recall = recall_score(Y, y_pred,average='micro' )"+"\r\n";
			  affiche  +="print(recall)"+"\r\n";
			  writeInFile = true;
			    break;
		  case "F1":
			  pythonImport +="from sklearn.metrics import f1_score"+"\r\n";
			  metric +="f1 = f1_score(Y, y_pred, average='micro')"+"\r\n";
			   affiche  +="print(f1)";
			    break;
		  case "accuracy":
			pythonImport +="from sklearn.metrics import accuracy_score"+"\r\n";
			  metric +="accuracy = accuracy_score(Y, y_pred)"+"\r\n";
			   affiche  +="print(accuracy)";
			    break;
		  case "macro_recall":
			  pythonImport +="from sklearn.metrics import recall_score"+"\r\n";
			  metric +="recall = recall_score(Y, y_pred,average='macro' )"+"\r\n";
			  affiche  +="print(recall)";
			    break;
		  case "macro_precision":
			  pythonImport +="from sklearn.metrics import precision_score"+"\r\n";
			  metric +="precision = precision_score(Y, y_pred, average='macro')"+"\r\n";
			   affiche  +="print(precision)";
			    break;
		  case "macro_F1":
			  pythonImport +="from sklearn.metrics import f1_score"+"\r\n";
			  metric +="f1 = f1_score(Y, y_pred, average='macro')"+"\r\n";
			   affiche  +="print(f1)";
			    break;
		  case "macro_accuracy":
			  metric +=""+"\r\n";
			  affiche  +=""+"\r\n";
			    break;
		}

		}
		
		String pandasCode = pythonImport + csvReading + predictorstr + predictivestr + algostr + val + metric + affiche;
		Long date = new Date().getTime();
		Files.write(pandasCode.getBytes(), new File("mml_ScikitLearn_"+date+".py"));

		long debut = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec("C:\\Program Files (x86)\\Python38-32\\python.exe mml_ScikitLearn_"+date+".py");
		long fin = System.currentTimeMillis()-debut;
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line; 
	String recall ="";
		while ((line = in.readLine()) != null) {
			recall = line;
			System.out.println(recall);
		}
		BufferedReader inError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		
		
	
		while ((line = inError.readLine()) != null) {
			System.out.println(line);
		}
		
		
		if (writeInFile) {
		File myFile = new File("recall.csv");
		FileOutputStream oFile = new FileOutputStream(myFile, true);
		oFile.write((dataInput.getFilelocation() +";"+NameAlgo+";ScikitLearn;"+fin+";"+recall+"\n").getBytes());
		oFile.close();
		}

	}
	public void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator, 	FormulaItem predictive, XFormula predictore , String fileResult) {
		this.algo = algo;
		this.dataInput = dataInput;
		this.validation = validation;
		this.separator = separator;
		this.predictive = predictive;
		this.predictore = predictore;
		this.fileResult = fileResult;
		this.metrics = validation.getMetric();
	}
	
	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}
}
