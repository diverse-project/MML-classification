package org.xtext.example.mydsl.tests.groupeRialetLemancelMandeLafont;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.mml.*;

import com.google.common.io.Files;

public class MmlParsingJavaCompilerR extends Compiler {

	List<String> results = new ArrayList<String>();
	Boolean nonSupporte = false;
	
	public List<String> compileDataInput(MMLModel model, MLAlgorithm al, int numAlgo) throws Exception {
		
		super.init();
		
		DataInput dataInput = model.getInput();
		String fileLocation = dataInput.getFilelocation();
		
		/* Un executable est present dans output_LAFONT_LEMANCEL_MANDE_RIALET/executable/ pour installer
		 * les packages R avant de pouvoir executer les programmes MML avec R
		 * Il doit etre execute avec RStudio
		 */

		
		/* Construction imports */
		imports = addInstruction(imports, "library(readr)");
		imports = addInstruction(imports, "library(caret)");
		imports = addInstruction(imports, "library(randomForest)");
		imports = addInstruction(imports, "library(LogicReg)");
		imports = addInstruction(imports, "library(e1071)");
		imports = addInstruction(imports, "library(party)");
		
		/* Separator */
		String csv_separator = getParsingInstruction(dataInput);
		
		/* csvReading */
		if (csv_separator == ";") {
			csvReading = addInstruction(csvReading, "data <- read.csv2(\"output_LAFONT_LEMANCEL_MANDE_RIALET/" + fileLocation + "\")");
		}
		else {
			csvReading = addInstruction(csvReading, "data <- read.csv(\"output_LAFONT_LEMANCEL_MANDE_RIALET/" + fileLocation + "\")");
		}
		
		/* Formula */
		this.formulaTreatment(model);
		
		/* Algorithm */
		this.algorithmTreatment(al);
		
		/* Stratification and metrics */
		this.stratificationAndMetrics(model);
		
		/* Construction program */
		if (nonSupporte) {
			program = "print(\"Les classifications nu et one de SVM ne sont pas supportées\")";
		} 
		else {
			program = addInstruction(program, imports);
			program = addInstruction(program, csvReading);
			program = addInstruction(program, x);
			program = addInstruction(program, y);
			program = addInstruction(program, formula);
			program = addInstruction(program, stratification);
			program = addInstruction(program, algorithm);
			program = addInstruction(program, prediction);
			program = addInstruction(program, metriques);
		}
	
		/* Generation program */
		Files.write(program.getBytes(), new File("output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_" + numAlgo + ".r"));

		/* Execution program */
		try {
            System.out.println("**********");
            results = runProcess("Rscript output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_" + numAlgo + ".r", "R");
            System.out.println("**********");
        } catch (Exception e) {
            e.printStackTrace();
        }
		return results;
	}
	
	/* Formula treatment */
	public void formulaTreatment(MMLModel model) {
		
		super.formulaTreatment(model);
		
		//Formula
		String colName = "";
		int colIndex = 0;
		
		if (f == null) {
			/* On récupere le nom de la derniere colonne */
			x = addInstruction(x, "x <- names(data[dim(data)[2]])");
			y = addInstruction(y, "y <- \".\"");
		}
		else {
			/* Traitement predictive */
			if (f.getPredictive().getColName() != null) {
				x = addInstruction(x, "x <- \"" + f.getPredictive().getColName() + "\"");
			}
			else if (f.getPredictive().getColumn() != 0) {
				x = addInstruction(x, "x <- names(data[" + f.getPredictive().getColumn() + "])");
			}
			
			/* Traitement predictors */
			if (f.getPredictors() instanceof AllVariables) {
				y = addInstruction(y, "y <- .");
			}
			else if (f.getPredictors() instanceof PredictorVariables) {
				FormulaItem[] predictors = (FormulaItem[]) ((PredictorVariables) f.getPredictors()).getVars().toArray();
				FormulaItem item;
				y = addInstruction(y, "y <- c()");
				for(int i = 0; i < predictors.length; i++) {
					item = predictors[i];
					colName = item.getColName();
					colIndex = item.getColumn();
					if(colName != null && colName.length() > 0 ) {
						y = addInstruction(y, "y <- c(y, \"" + colName + "\")");
					}
					else {
						y = addInstruction(y, "y <- c(y, names(data[" + colIndex + "]))");
					}
				}
			}
		}
		
		formula = addInstruction(formula, "formula <- reformulate(termlabels = y, response = x)");
	}
	
	/* Algorithm treatment */
	public void algorithmTreatment(MLAlgorithm al) {
		
		if (al instanceof DT) {
			DT defAlg = (DT) al;
			int maxDepth = defAlg.getMax_depth();
			
			if(maxDepth == 0) {
				algorithm = addInstruction(algorithm, "model <- train(formula, data=data_train,method=\"ctree2\",trControl=fitControl)");
			}
			else {
				algorithm = addInstruction(algorithm, "grid <- expand.grid(maxdepth=" + maxDepth + ",mincriterion=1)");
				algorithm = addInstruction(algorithm, "model <- train(formula, data=data_train, method=\"ctree2\", trControl=fitControl, tuneGrid=grid)");
			}
			
		}
		else if (al instanceof SVM) {
			SVM defAlg = (SVM) al;
			
			/* Si absence de gamma dans le programme, on aura null comme valeur */
			String gamma = (defAlg.getGamma() == null) ? null : defAlg.getGamma();
			
			/* Si absence de c dans le programme, on aura null comme valeur */
			float c = (float) ((defAlg.getC() == null) ? 1.0 : Float.valueOf(defAlg.getC()));
			
			String kernel = "";
			if (defAlg.isKernelSpecified()) {
				kernel = defAlg.getKernel().getLiteral();
				switch (kernel) {
					case "radial" 		: kernel = "'radial'"; 		break;
					case "polynomial" 	: kernel = "'polynomial'"; 	break;
					case "linear" 		: kernel = "'linear'"; 		break;
				}	
			}
			else {
				kernel = "'linear'";
			}
			
			/* Si absence de classification dans le programme, on aura de base "C-classification" */
			String classification = "";
			String ecrireAlgo = "";
			if (defAlg.isClassificationSpecified()) {
				classification = defAlg.getSvmclassification().getLiteral();
				
				
				switch (classification) {
				
					case "C-classification" :
						ecrireAlgo = "model <- svm(formula=formula, data=data_train, type=\"" + classification + "\"";
						if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
						ecrireAlgo += ", kernel=" + kernel;
						ecrireAlgo += ", cost=" + c;
						ecrireAlgo += ")";
						break;
						
					case "nu-classification" :
						/*ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=\"" + classification + "\"";
						if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
						ecrireAlgo += ", kernel=" + kernel;
						ecrireAlgo += ", cost=" + c;
						ecrireAlgo += ", nu=" + c;
						ecrireAlgo += ")";*/
						nonSupporte = true;
						break;
						
					case "one-classification" : 
						/*ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=\"" + classification + "\"";
						if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
						ecrireAlgo += ", kernel=" + kernel;
						ecrireAlgo += ", cost=" + c;
						ecrireAlgo += ", nu=" + c;
						ecrireAlgo += ")";*/
						nonSupporte = true;
						break;
				}
				algorithm = addInstruction(algorithm, ecrireAlgo);
			}
			else {
				ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=\"C-classification\"";
				if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
				ecrireAlgo += ", kernel=" + kernel;
				ecrireAlgo += ", cost=" + c;
				ecrireAlgo += ")";
				
				algorithm = addInstruction(algorithm, ecrireAlgo);
			}
		}
		else if (al instanceof RandomForest) {
			algorithm = addInstruction(algorithm, "data_train[[x]] <- as.character(data_train[[x]])");
			algorithm = addInstruction(algorithm, "data_train[[x]] <- as.factor(data_train[[x]])");
			algorithm = addInstruction(algorithm, "model <- randomForest(formula, data=data_train, na.action = na.omit)");
		}
		else if (al instanceof LogisticRegression) {
			algorithm = addInstruction(algorithm, "data_train[[x]] <- as.character(data_train[[x]])");
			algorithm = addInstruction(algorithm, "data_train[[x]] <- as.factor(data_train[[x]])");
			algorithm = addInstruction(algorithm, "model <- glm(formula, family = binomial(logit), data=data_train)");
		}
	}
	
	/* Stratification and Metrics treatment */
	public void stratificationAndMetrics(MMLModel model) {
		
		Validation validation = model.getValidation();
		StratificationMethod strat = validation.getStratification();
		EList<ValidationMetric> metric = validation.getMetric();
		
		int number; 
		
		if (strat instanceof CrossValidation) {
			number = strat.getNumber();
			
			stratification = addInstruction(stratification, "fitControl <- trainControl(method=\"cv\", number=" + number + ")");
			stratification = addInstruction(stratification, "data_train <- data");
			stratification = addInstruction(stratification, "data_test <- data");
		} 
		else if (strat instanceof TrainingTest) {
			number = 100 - strat.getNumber();
			
			stratification = addInstruction(stratification, "split=0." + number);
			stratification = addInstruction(stratification, "trainIndex <- createDataPartition(data[[x]], p=split, list=FALSE)");
			stratification = addInstruction(stratification, "data_train <- data[ trainIndex,]");
			stratification = addInstruction(stratification, "data_test <- data[-trainIndex,]");
			stratification = addInstruction(stratification, "fitControl <- trainControl(method=\"none\")");
		}
		
		prediction = addInstruction(prediction, "pred <- predict(model,newdata=data_test)");
		prediction = addInstruction(prediction, "u <- union(pred, data_test[[x]])");
		prediction = addInstruction(prediction, "t <- table(factor(pred, u), factor(data_test[[x]], u))");
		prediction = addInstruction(prediction, "mat <- confusionMatrix(t)");
		
		ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
		String metrique = "";
		for (int i = 0 ; i < metriquesArray.length ; i++) {
			metrique = metriquesArray[i].getLiteral();
			if (metrique == "accuracy") {
				metriques = addInstruction(metriques, "print(paste(\"Accuracy___\",as.double(mat$overall[\"Accuracy\"]),sep=\"\"))");
			}
			else if (metrique == "balanced_accuracy") {
				metriques = addInstruction(metriques, "if (!is.null(dim(mat$byClass)[1])) { print(paste(\"Balanced Accuracy___\",mean(mat$byClass[,\"Balanced Accuracy\"]),sep=\"\")) } "
					      + "else { print(paste(\"Balanced Accuracy___\",mean(mat$byClass[\"Balanced Accuracy\"]),sep=\"\")) }");
			}
			else if (metrique == "recall") {
				metriques = addInstruction(metriques, "if (!is.null(dim(mat$byClass)[1])) { print(paste(\"Recall___\",mean(mat$byClass[,\"Recall\"],na.rm=TRUE),sep=\"\")) } "
				          + "else { print(paste(\"Recall___\",mean(mat$byClass[\"Recall\"],na.rm=TRUE),sep=\"\")) }");
			}
			else if (metrique == "macro_recall") {
				metriques = addInstruction(metriques, "print(\"Macro Recall non supporté\")");
			}
			else if (metrique == "precision") {
				metriques = addInstruction(metriques, "if (!is.null(dim(mat$byClass)[1])) { print(paste(\"Precision___\",mean(mat$byClass[,\"Precision\"],na.rm=TRUE),sep=\"\")) } "
				          + "else { print(paste(\"Precision\",mean(mat$byClass[\"Precision\"],na.rm=TRUE),sep=\"\")) }");
			}
			else if (metrique == "macro_precision") {
				metriques = addInstruction(metriques, "print(\"Macro Precision non supporté\")");
			}
			else if (metrique == "F1") {
				metriques = addInstruction(metriques, "if (!is.null(dim(mat$byClass)[1])) { print(paste(\"F1___\",mean(mat$byClass[,\"F1\"],na.rm=TRUE),sep=\"\")) } "
				          + "else { print(paste(\"F1___\",mean(mat$byClass[\"F1\"],na.rm=TRUE),sep=\"\")) }");
			}
			else if (metrique == "macro_F1") {
				metriques = addInstruction(metriques, "print(\"Macro F1 non supporté\")");
			}
			else if (metrique == "macro_accuracy") {
				metriques = addInstruction(metriques, "print(\"Macro Accuracy non supporté\")");
			}
		}
	}
}