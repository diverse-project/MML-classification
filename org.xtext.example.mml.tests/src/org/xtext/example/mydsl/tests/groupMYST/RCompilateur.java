package org.xtext.example.mydsl.tests.groupMYST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.SVMClassification;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;

import com.google.common.io.Files;

public class RCompilateur implements Compilateur{
	private MLAlgorithm algo;
	private DataInput dataInput;
	private Validation validation;
	private String separator;
	private FormulaItem predictive;
	private XFormula predictore;
	private String fileResult;


	private List<ValidationMetric> metrics;

	public void execute() throws IOException {
		String rImport = "install.packages(\"rpart\")\r\n";
		String columnToPredictName = "";
		rImport += "install.packages(\"caret\")\r\n";
		rImport += "library(rpart)\r\n";
		rImport += "library(caret)\r\n";
		String csvReading = "data <- read.csv(\""+dataInput.getFilelocation()+"\", header = TRUE, sep = \""+separator+"\")\r\nattach(data)\r\n";

        BufferedReader br = new BufferedReader(new FileReader(dataInput.getFilelocation()));
        String header = br.readLine();
        if (header != null) {
            String[] columns = header.split(separator);
            columnToPredictName = columns[columns.length-1].trim();
        }
		
		String predictivestr = "";
		if (predictive!= null ) {
			predictivestr = "X <- data$"+predictive.getColName()+"\r\n";
		}else {
			//dernière colonne predictive.getColumn()
			predictivestr = "X <- subset(data, select=-" + columnToPredictName + ")\r\n";
			predictivestr = predictivestr.replace("\"", "");
		}
		
		String predictorstr ="Y <- " + columnToPredictName + "\r\n";
		predictorstr = predictorstr.replace("\"", "");

		String NameAlgo ="";
		String algostr ="";
		String trainingModel = "";
		if(algo instanceof SVM) {
			SVM svm = (SVM)algo;
			NameAlgo = "SVM";
			String kernel = svm.getKernel().getName();
			String cost = svm.getC();
			String gamma = svm.getGamma();

			if (svm.isClassificationSpecified()) {
				algostr += "install.packages(\"e1071\")\r\n";
				algostr += "library(e1071)\r\n";
				SVMClassification classification = svm.getSvmclassification();
				switch (classification.getLiteral()) {
				case "C-classification":
					algostr += "model <- svm(Y ~ ., data = X, kernel = \""+ kernel +"\", cost = "+ cost +", gamma = "+ gamma +", scale = FALSE, type =\"C-classification\")\r\n";
					break;
				case "nu-classification":
					algostr += "model <- svm(Y ~ ., data = X, kernel = \""+ kernel +"\", cost = "+ cost +", gamma = "+ gamma +", scale = FALSE, type =\"nu-classification\")\r\n";
					break;	
				case "one-classification":
					//TODO : impossible d'afficher des stats avec confusionMatrix pour ce svm
					algostr += "model <- svm(Y ~ ., data = X, kernel = \""+ kernel +"\", cost = "+ cost +", gamma = "+ gamma +", scale = FALSE, type =\"one-classification\")\r\n";
					break;
				}
			}
		}else if(algo instanceof DT) {
			trainingModel += "rpart";
			NameAlgo = "DecisionTree";
		}else if(algo instanceof RandomForest ) {
			algostr += "install.packages(\"randomForest\")\r\n";
			algostr += "library(randomForest)\r\n";
			trainingModel += "rf";
			NameAlgo = "RandomForest";
		}else if(algo instanceof LogisticRegression) {
			algostr += "install.packages(\"caTools\")\r\n";
			algostr += "library(caTools)\r\n";
			trainingModel += "LogitBoost";
			NameAlgo = "LogisticRegression";
		}


		int num;
		String val = "";
		if (validation.getStratification() instanceof CrossValidation) {
			val += "set.seed(123)\r\n";
			String tmpAlgo = "sample <- createDataPartition(data$" + columnToPredictName + ", p = .8, list = FALSE)\r\n";
			val += "train <- data[sample, ]\r\n";
			val += "test  <- data[-sample, ]\r\n";
			if (trainingModel != "") {
				num = validation.getStratification().getNumber();
				val += "train_control <- trainControl(method=\"cv\", number="+num+")\r\n";
				tmpAlgo = "model <- train(" + columnToPredictName;
				tmpAlgo = tmpAlgo.replace("\"", "");
				val += tmpAlgo;
				val += "~ ., data=train, trControl=train_control, method=\""+trainingModel+"\")\r\n";
			}
			val += "X <- test[,1:ncol(test)-1]\r\n";
			val += "Y <-test[,ncol(test)]\r\n";
			val += "predictions <- predict(model, X)\r\n";
		}else if(validation.getStratification() instanceof TrainingTest) {
			num = validation.getStratification().getNumber();
			val += "set.seed(123)\r\n";
			String tmpAlgo = "sample <- createDataPartition(data$"+columnToPredictName+", p = " + (1-(num*0.01)) + ", list = FALSE)\r\n";
			tmpAlgo = tmpAlgo.replace("\"", "");
			val += tmpAlgo;
			val += "train <- data[sample, ]\r\n";
			val += "test  <- data[-sample, ]\r\n";
			if (trainingModel != "") {
				tmpAlgo = "model <- train(" + columnToPredictName;
				tmpAlgo = tmpAlgo.replace("\"", "");
				val += tmpAlgo;
				val += "~ ., data=train, method=\""+trainingModel+"\")\r\n";
			}
			val += "X <- test[,1:ncol(test)-1]\r\n";
			val += "Y <-test[,ncol(test)]\r\n";
			val += "predictions <- predict(model, X)\r\n";
		}

		String metric ="";
		String affiche  ="";
		boolean writeInFile = false;
		if (!metrics.isEmpty()) {
			metric += "cm <- confusionMatrix(predictions, Y, mode=\"prec_recall\")\r\n";
			metric += "byClass <- cm$byClass\r\n";
			for (ValidationMetric laMetric : metrics) {
				switch(laMetric.getLiteral()) {
				case "balanced_accuracy":
					metric +="balanceA <- paste(mean(byClass[TRUE,c(\"Balanced Accuracy\")]))"+"\r\n";
					affiche  +="print(paste(\"BalancedAccuracy:\", balanceA))"+"\r\n";
					writeInFile = true;
					break;
				case "recall":
					metric +="recall <- paste(mean(byClass[TRUE,c(\"Recall\")]))\r\n";
					affiche  +="print(paste(\"Recall:\", recall))\r\n";
					writeInFile = true;
					break;
				case "precision":
					metric +="precision <- paste(mean(byClass[TRUE,c(\"Precision\")]))"+"\r\n";
					affiche  +="print(paste(\"Precision:\", precision))"+"\r\n";
					writeInFile = true;
					break;
				case "F1":
					metric +="F1 <- paste(mean(byClass[TRUE,c(\"F1\")]))\r\n";
					affiche  +="print(paste(\"F1:\", F1))"+"\r\n";
					writeInFile = true;
					break;
				case "accuracy":
					metric +="accuracy <- cm$overall[['Accuracy']]"+"\r\n";
					affiche  +="print(paste(\"Accuracy:\", accuracy))"+"\r\n";
					writeInFile = true;
					break;
				case "macro_recall":
					metric +="macroRecall <- paste(mean(byClass[TRUE,c(\"Recall\")]))\r\n";
					affiche  +="print(paste(\"MacroRecall:\", macroRecall))\r\n";
					writeInFile = true;
					break;
				case "macro_precision":
					metric +="macroPrecision <- paste(mean(byClass[TRUE,c(\"Precision\")]))"+"\r\n";
					affiche  +="print(paste(\"MacroPrecision:\", macroPrecision))"+"\r\n";
					writeInFile = true;
					break;
				case "macro_F1":
					metric +="macroF1 <- paste(mean(byClass[TRUE,c(\"F1\")]))"+"\r\n";
					affiche  +="print(paste(\"MacroF1:\", macroF1))"+"\r\n";
					writeInFile = true;
					break;
				case "macro_accuracy":
					metric +="macroAccuracy <- paste(mean(byClass[TRUE,c(\"Balanced Accuracy\")]))"+"\r\n";
					affiche  +="print(paste(\"MacroAccuracy:\", macroAccuracy))"+"\r\n";
					writeInFile = true;
					break;
				}
	
			}
		}
		
		String rCode = rImport + csvReading + predictivestr +predictorstr  + algostr + val + metric +affiche;
		Long date = new Date().getTime();
		File rFile = new File("mml_R"+date+".R");
		Files.write(rCode.getBytes(), rFile);		
		String rFilePath = rFile.getAbsolutePath();
		long debut = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec("Rscript.exe --vanilla --quiet --slave "+rFilePath+"");
		System.out.println();
		long fin = System.currentTimeMillis()-debut;
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line; 
		String consoleOutput = "";
		while ((line = in.readLine()) != null) {
			consoleOutput += line;
		}
		
		BufferedReader inError = new BufferedReader(new InputStreamReader(p.getErrorStream()));		
	
		while ((line = inError.readLine()) != null) {
			System.out.println(line);
		}
			
		if (writeInFile) {
			// Gets metrics from R output
			String metricsString = "", balancedAccuracy = "", recall = "", precision = "", F1 = "", accuracy = "", macroRecall = "", macroPrecision = "", macroF1 = "", macroAccuracy = "";
			for (ValidationMetric laMetric : metrics) {
				switch(laMetric.getLiteral()) {
				case "balanced_accuracy":
					balancedAccuracy = this.getMetricFromOutput("BalancedAccuracy", consoleOutput);
					metricsString += balancedAccuracy + ";";
					break;
				case "recall":
					recall = this.getMetricFromOutput("Recall", consoleOutput);
					metricsString += recall + ";";
					break;
				case "precision":
					precision = this.getMetricFromOutput("Precision", consoleOutput);
					metricsString += precision + ";";
					break;
				case "F1":
					F1 = this.getMetricFromOutput("F1", consoleOutput);
					metricsString += F1 + ";";
					break;
				case "accuracy":
					accuracy = this.getMetricFromOutput("Accuracy", consoleOutput);
					metricsString += accuracy + ";";
					break;
				case "macro_recall":
					macroRecall = this.getMetricFromOutput("MacroRecall", consoleOutput);
					metricsString += macroRecall + ";";
					break;
				case "macro_precision":
					macroPrecision = this.getMetricFromOutput("MacroPrecision", consoleOutput);
					metricsString += macroPrecision + ";";
					break;
				case "macro_F1":
					macroF1 = this.getMetricFromOutput("MacroF1", consoleOutput);
					metricsString += macroF1 + ";";
					break;
				case "macro_accuracy":
					macroAccuracy = this.getMetricFromOutput("MacroAccuracy", consoleOutput);
					metricsString += macroAccuracy + ";";
					break;
				}
			}
			
			File myFile = new File("recall.csv");
			FileOutputStream oFile = new FileOutputStream(myFile, true);
			oFile.write((dataInput.getFilelocation() +";"+NameAlgo+";R;"+fin+";"+metricsString+"\n").getBytes());
			oFile.close();
			}

	}
	public void configure(MLAlgorithm algo, DataInput dataInput, Validation validation, String separator,  FormulaItem predictive, XFormula predictore, String fileResult) {
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
	
	private String getMetricFromOutput(String metricName, String output) {
		String metric ="";
		Pattern metricPattern = Pattern.compile("\""+metricName+":(.*?)\"");
		Matcher matcher = metricPattern.matcher(output);
		while(matcher.find()) metric += matcher.group(1);
		return metric.trim();
	}
	
}
