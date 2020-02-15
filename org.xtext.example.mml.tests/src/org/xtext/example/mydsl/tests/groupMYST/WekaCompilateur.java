package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
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
	String wikaImport = "import wekaexamples.helper as helper "+"\r\n"+ "from weka.core.converters import Loader"+"\r\n";
	String loader = "loader = Loader(classname=\"weka.core.converters.CSVLoader\")"+"\r\n";
	String csvReading = "mml_data = loader.load_file(helper.get_data_dir() + " + separator +" + "+ 
	mkValueInSingleQuote(dataInput.getFilelocation())+")\r\n";
	
	String predictivestr = "";
	if (predictive.getColName() != null && predictive.getColName()!= "") {
		//TODO
		predictivestr = ""+"\r\n";
	}else {
		//dernière colonne predictive.getColumn()
		//TODO
		predictivestr = ""+"\r\n";
	}
	
	String predictorstr ="";
	
	String algostr ="";
	if(algo instanceof SVM) {
		  //TODO
		  SVM svm = (SVM)algo;
		  algostr = "algo = "+"\r\n";
	}else if(algo instanceof DT) {
		 DT dt = (DT)algo;
			int max_depth = dt.getMax_depth();
			if(max_depth != 0) {
				 algostr = "algo = "+"\r\n";
			}else {
				 algostr = "algo = "+"\r\n";
			}
			 
	}else if(algo instanceof RandomForest ) {
		  //TODO
		  algostr = "algo = "+"\r\n";
	}else if(algo instanceof LogisticRegression) {
		  //TODO
		  algostr = "algo = "+"\r\n";
	}
	

	int num;
	String val = "";
	switch(validation.getStratification().toString()) {
	  case "CrossValidation":
		  //TODO
		  num = validation.getStratification().getNumber();
		  val = ""+"\r\n";
		  
	    break;
	  case "TrainingTest":
		  //TODO
		  num = validation.getStratification().getNumber();
		  val = ""+"\r\n";
	    break;
	}
	
	String metric ="";
	String affiche ="";
	for (ValidationMetric laMetric : metrics) {
	switch(laMetric.getName()) {
	  case "balanced_accuracy":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
	    break;
	  case "recall":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
	    break;
	  case "precision":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "F1":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "accuracy":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_recall":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_precision":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_F1":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	  case "macro_accuracy":
		  //TODO
		   metric +=""+"\r\n";
		   affiche  +=""+"\r\n";
		    break;
	}

	}
	
	String wekaCode = wikaImport + loader + csvReading + predictivestr +predictorstr + algostr + val + metric +  affiche  ;
	Long date = new Date().getTime();
	Files.write(wekaCode.getBytes(), new File("mml_weka_"+date+".py"));
		
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