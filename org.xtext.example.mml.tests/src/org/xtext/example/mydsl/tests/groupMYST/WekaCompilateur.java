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
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class WekaCompilateur implements Compilateur {
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;
	private List<ValidationMetric> metrics;
	
	
	public void execute() throws IOException{
	String wekaImport = "import wekaexamples.helper as helper "+"\r\n"+ "from weka.core.converters import Loader"+"\r\n";
	String loader = "loader = Loader(classname=\"weka.core.converters.CSVLoader\")"+"\r\n";
	String csvReading = "mml_data = loader.load_file(helper.get_data_dir() + " + separator +" + "+ 
	mkValueInSingleQuote(dataInput.getFilelocation())+")\r\n";
	
	String predictivestr = "";
	if (predictive != null) {
		//TODO
		predictivestr = "pred_output = PredictionOutput(classname=\"weka.classifiers.evaluation.output.prediction.CSV\", options=[\"-p\", \"1\"])"+"\r\n";
	}else {
		//dernière colonne predictive.getColumn()
		//TODO
		predictivestr = ""+"\r\n";
	}
	//TODO
	String predictorstr ="";
	
	int num;
	 String NameAlgo = "";
	String algostr ="";
	if(algo instanceof SVM) {
//	    Y a po !!
		 NameAlgo = "SVM";
	    SVM svm = (SVM)algo;
		algostr = "algo = "+"\r\n";
	}else if(algo instanceof DT) {
		 DT dt = (DT)algo;
//			Classifier(classname="weka.classifiers.trees.J48")
//		 	classifier.set_property("confidenceFactor", typeconv.double_to_float(0.3))
//		    classifier.build_classifier(iris_data)
		 NameAlgo = "Decision Tree";
			 
	}else if(algo instanceof RandomForest ) {
//			classifier = Classifier(classname="weka.classifiers.trees.RandomForest")
//			evaluation = Evaluation(diabetes_data)
//			evaluation.crossvalidate_model(classifier, diabetes_data, 10, Random(42))
		  num = validation.getStratification().getNumber();
		  algostr = "classifier = Classifier(classname=\"weka.classifiers.trees.RandomForest\")"+"\r\n";
		  algostr += "evaluation = Evaluation("+ dataInput +")"+"\r\n";
		  algostr += "evaluation.crossvalidate_model(classifier, "+ dataInput +", "+ num +", Random(42))"+"\r\n";
		  NameAlgo = "Random Forest";
	}else if(algo instanceof LogisticRegression) {
		  //TODO
		  algostr = "algo = "+"\r\n";
		  // y a pas
		  NameAlgo = "Logistic Regression";
	}
	

	String val = "";

	if (validation.getStratification() instanceof CrossValidation) {

//		  classifier = Classifier(classname="weka.classifiers.bayes.NaiveBayes")
//		  evaluation = Evaluation(diabetes_data)
//		  evaluation.crossvalidate_model(classifier, diabetes_data, 10, Random(42), output=pred_output)

		  num = validation.getStratification().getNumber();
		  algostr = "classifier = Classifier(classname=\"weka.classifiers.bayes.NaiveBayes\")"+"\r\n";
		  algostr += "evaluation = Evaluation("+ dataInput +")"+"\r\n";
		  algostr += "evaluation.crossvalidate_model(classifier, "+ dataInput +", "+ num +", Random(42))"+"\r\n";
		  val = ""+"\r\n";
	}else if (validation.getStratification() instanceof TrainingTest) {
		  //TODO
		  //y a pas
		  num = validation.getStratification().getNumber();
		  val = ""+"\r\n";
	   
	}
	
	String metric ="";
	String affiche ="";
	boolean writeInFile = false;
	for (ValidationMetric laMetric : metrics) {
	switch(laMetric.getLiteral()) {
	  case "balanced_accuracy":
		  //TODO
		  //y a pas
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
	    break;
	  case "recall":
		  //TODO
		  //print("recall: " + str(evaluation.recall(1)))
		   metric +="str(evaluation.recall(1)"+"\r\n";
		   affiche  +=""+"\r\n";
		   writeInFile = true;
	    break;
	  case "precision":
		  //TODO
		  //print("precision: " + str(evaluation.precision(1)))
		   metric +="str(evaluation.precision(1)"+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "F1":
		  //TODO
		  //y a pas
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "accuracy":
		  //TODO
		  //y a pas
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_recall":
		  //TODO
		  // y a pas
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_precision":
		  //TODO
		  //y a pas
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_F1":
		  //TODO
		  //(pas sur de moi) print("unweightedMacroFmeasure: " + str(evaluation.unweighted_macro_f_measure)) ?
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_accuracy":
		  //TODO
		  //y a pas
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	}

	}
	
	String pandasCode = wekaImport + csvReading + predictorstr + predictivestr + algostr + val + metric + affiche;
	Long date = new Date().getTime();
	Files.write(pandasCode.getBytes(), new File("mml_Weka_"+date+".py"));

	long debut = System.currentTimeMillis();
	Process p = Runtime.getRuntime().exec("python mml_Weka_"+date+".py");
	long fin = System.currentTimeMillis()-debut;
	BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
	if (writeInFile) {
		File myFile = new File("recall.csv");
		FileOutputStream oFile = new FileOutputStream(myFile, true);
		oFile.write((dataInput.getFilelocation() +";"+NameAlgo+";ScikitLearn;"+fin+";"+in.read()+"\n").getBytes());
		oFile.close();
		}
	}
	public void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator, FormulaItem predictive, XFormula predictore,  String fileResult) {
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