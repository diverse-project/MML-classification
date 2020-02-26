package org.xtext.example.mydsl.tests;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.*;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaCompilerR {
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
		/*MMLModel model = getModel("prog1.mml");
		Assertions.assertNotNull(model);
		EList<Resource.Diagnostic> errors = model.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
		Assertions.assertEquals("iris.csv", result.getInput().getFilelocation());	
		Assertions.assertEquals("scikit-learn", result.getAlgorithm().getFramework().toString());
		Assertions.assertEquals(DTImpl.class, result.getAlgorithm().getAlgorithm().getClass());	
		System.out.println(result.getAlgorithm().getAlgorithm().getClass());*/
	}
	
	@Test
	public Boolean compileDataInput(MMLModel model, MLAlgorithm al, int numAlgo) throws Exception {
		DataInput dataInput = model.getInput();
		String fileLocation = dataInput.getFilelocation();
		
		/*String installPackages = "install.packages(\"caret\",repos=\"http://cran.irsn.fr/\")\n";
		installPackages += "install.packages(\"readr\",repos=\"http://cran.irsn.fr/\")\n";
		installPackages += "install.packages(\"randomForest\",repos=\"http://cran.irsn.fr/\")\n";
		installPackages += "install.packages(\"LogicReg\",repos=\"http://cran.irsn.fr/\")\n";
		installPackages += "install.packages(\"e1071\",repos=\"http://cran.irsn.fr/\")\n";
		installPackages += "install.packages(\"party\",repos=\"http://cran.irsn.fr/\")\n";*/
		
		String installPackages = "";


		String DEFAULT_COLUMN_SEPARATOR = ","; // by default
		String csv_separator = DEFAULT_COLUMN_SEPARATOR;
		CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
		
		if (parsingInstruction != null) {			
			System.err.println("parsing instruction..." + parsingInstruction);
			csv_separator = parsingInstruction.getSep().toString();
		}
		
		String importPackages = "library(readr)\n";
		importPackages += "library(caret)\n";
		importPackages += "library(randomForest)\n";
		importPackages += "library(LogicReg)\n";
		importPackages += "library(e1071)\n";
		importPackages += "library(party)\n";
		
		String readCsv = "";
		if (csv_separator == ";") {
			readCsv = "data <- read.csv2(\"output_LAFONT_LEMANCEL_MANDE_RIALET/" + fileLocation + "\")\n";
		}
		else {
			readCsv = "data <- read.csv(\"output_LAFONT_LEMANCEL_MANDE_RIALET/" + fileLocation + "\")\n";
		}
		
		
		//Formula
		String predictive = "";
		String predictors = "";
		String formula = "";
		String colName = "";
		int colIndex = 0;
		RFormula f = model.getFormula();
		
		if (f == null) {
			/* On récupere le nom de la derniere colonne */
			predictive = "predictive <- names(data[dim(data)[2]])\n";
			predictors = "predictors <- \".\"\n";
		}
		else {
			/* Traitement predictive */
			if (f.getPredictive().getColName() != null) {
				predictive = "predictive <- \"" + f.getPredictive().getColName() + "\"\n";
			}
			else if (f.getPredictive().getColumn() != 0) {
				predictive = "predictive <- names(data[" + (f.getPredictive().getColumn()) + "])\n";
			}
			
			/* Traitement predictors */
			if (f.getPredictors() instanceof AllVariables) {
				predictors = "predictors <- .\n";
			}
			else if (f.getPredictors() instanceof PredictorVariables) {
				FormulaItem[] arrayPredictors = (FormulaItem[]) ((PredictorVariables) f.getPredictors()).getVars().toArray();
				FormulaItem item;
				predictors = "predictors <- c()\n";
				for(int i = 0; i < arrayPredictors.length; i++) {
					item = arrayPredictors[i];
					colName = item.getColName();
					colIndex = item.getColumn();
					if(colName != null && colName.length() > 0 ) {
						predictors += "predictors <- c(predictors, \"" + colName + "\")\n";
					}
					else {
						predictors += "predictors <- c(predictors, names(data[" + colIndex + "]))\n";
					}
				}
			}
		}
		
		formula = "formula <- reformulate(termlabels = predictors, response = predictive)\n";
		

		String methode = "";
		String ecrireAlgo = "";
		Boolean nonSupporte = false;
		
		if (al instanceof DT) {
			DT defAlg = (DT) al;
			int maxDepth = defAlg.getMax_depth();
			
			methode = "ctree2";
			
			if(maxDepth == 0) {
				ecrireAlgo += "model <- train(formula, data=data_train,method=\"" + methode + "\",trControl=fitControl)";
			}
			else {
				ecrireAlgo += "grid <- expand.grid(maxdepth=" + maxDepth + ",mincriterion=1)\n";
				ecrireAlgo += "model <- train(formula, data=data_train,method=\"" + methode + "\",trControl=fitControl,tuneGrid=grid)";
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
					case "polynomial" 	: kernel = "'polynomial'"; 		break;
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
						ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=\"" + classification + "\"";
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
			}
			else {
				ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=\"C-classification\"";
				if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
				ecrireAlgo += ", kernel=" + kernel;
				ecrireAlgo += ", cost=" + c;
				ecrireAlgo += ")";
			}
			
		}
		else if (al instanceof RandomForest) {
			ecrireAlgo += "data_train[[predictive]] <- as.character(data_train[[predictive]])\n";
			ecrireAlgo += "data_train[[predictive]] <- as.factor(data_train[[predictive]])\n";
			ecrireAlgo += "model <- randomForest(formula, data=data_train, na.action = na.omit)";
		}
		else if (al instanceof LogisticRegression) {
			//methode = "logreg";
			//ecrireAlgo += "model <- train(formula, data=data_train,method=\"" + methode + "\",trControl=fitControl)";
			ecrireAlgo += "data_train[[predictive]] <- as.character(data_train[[predictive]])\n";
			ecrireAlgo += "data_train[[predictive]] <- as.factor(data_train[[predictive]])\n";
			ecrireAlgo += "model <- glm(formula, family = binomial(logit), data=data_train)";
		}
		ecrireAlgo += "\n";
		
		
		Validation validation = model.getValidation();
		StratificationMethod strat = validation.getStratification();
		EList<ValidationMetric> metric = validation.getMetric();
		
		String stratification = "";
		int number; 
		
		if (strat instanceof CrossValidation) {
			number = strat.getNumber();
			
			stratification += "fitControl <- trainControl(method=\"cv\", number=" + number + ")\n";
			stratification += "data_train <- data\n";
			stratification += "data_test <- data\n";
		} 
		else if (strat instanceof TrainingTest) {
			
			number = 100 - strat.getNumber();
			stratification = "split=0." + number + "\n";
			stratification += "trainIndex <- createDataPartition(data[[predictive]], p=split, list=FALSE)\n";
			stratification += "data_train <- data[ trainIndex,]\n";
			stratification += "data_test <- data[-trainIndex,]\n";
			stratification += "fitControl <- trainControl(method=\"none\")\n";
		}
		
		String prediction = "";
		prediction += "pred <- predict(model,newdata=data_test)\n";
		prediction += "u <- union(pred, data_test[[predictive]])\n";
		prediction += "t <- table(factor(pred, u), factor(data_test[[predictive]], u))\n";
		prediction += "mat <- confusionMatrix(t)\n";
		
		ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
		String metrique = "";
		String metriques = "";
		for(int i = 0; i < metriquesArray.length; i++) {
			metrique = metriquesArray[i].getLiteral();
			if(metrique == "accuracy") {
				metriques += "print(\"Accuracy\")\n";
				metriques +="print(as.double(mat$overall[\"Accuracy\"]))\n";
			}else if(metrique == "balanced_accuracy") {
				metriques += "print(\"Balanced Accuracy\")\n";
				metriques += "if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,\"Balanced Accuracy\"])) } ";
				metriques += "else { print(mean(mat$byClass[\"Balanced Accuracy\"])) }\n";
			}else if(metrique == "recall") {
				metriques += "print(\"Recall\")\n";
				metriques += "if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,\"Recall\"],na.rm=TRUE)) } ";
				metriques += "else { print(mean(mat$byClass[\"Recall\"],na.rm=TRUE)) }\n";
			}else if(metrique == "macro_recall") {
				metriques += "print(\"Macro Recall non supporté\")\n";
			}else if(metrique == "precision") {
				metriques += "print(\"Precision\")\n";
				metriques += "if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,\"Precision\"],na.rm=TRUE)) } ";
				metriques += "else { print(mean(mat$byClass[\"Precision\"],na.rm=TRUE)) }\n";
			}else if(metrique == "macro_precision") {
				metriques += "print(\"Macro Precision non supporté\")\n";
			}else if(metrique == "F1") {
				metriques += "print(\"F1\")\n";
				metriques += "if (!is.null(dim(mat$byClass)[1])) { print(mean(mat$byClass[,\"F1\"],na.rm=TRUE)) } ";
				metriques += "else { print(mean(mat$byClass[\"F1\"],na.rm=TRUE)) }\n";
			}else if(metrique == "macro_F1") {
				metriques += "print(\"Macro F1 non supporté\")\n";
			}
			else if(metrique == "macro_accuracy") {
				metriques += "print(\"Macro Accuracy non supporté\")\n";
			}
		}
		
		String writeProgram = "";
		if (nonSupporte) {
			writeProgram = "print(\"Les classifications nu et one de SVM ne sont pas supportées\")";
		} 
		else {
			writeProgram = addProgramText("", installPackages);
			writeProgram += addProgramText("", importPackages);
			writeProgram += addProgramText("", readCsv);
			writeProgram += addProgramText("", predictive);
			writeProgram += addProgramText("", predictors);
			writeProgram += addProgramText("", formula);
			writeProgram += addProgramText("", stratification);
			writeProgram += addProgramText("", ecrireAlgo);
			writeProgram += addProgramText("", prediction);
			writeProgram += addProgramText("", metriques);
		}
	

		
		Files.write(writeProgram.getBytes(), new File("output_LAFONT_LEMANCEL_MANDE_RIALET/mml_"+numAlgo+".r"));

		Boolean executionReussie = false;
		try {
            System.out.println("**********");
            //runProcess("cd output_LAFONT_LEMANCEL_MANDE_RIALET");
            executionReussie = runProcess("Rscript output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_"+numAlgo+".r");
            System.out.println("**********");
            //runProcess("java -cp src com/journaldev/files/Test Hi Pankaj");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return executionReussie;
		
		
	}
	
	 private static void printLines(String cmd, InputStream ins) throws Exception {
	    	String line = null;
	    	BufferedReader in = new BufferedReader(
		    new InputStreamReader(ins));
	    	while ((line = in.readLine()) != null) {
	    		System.out.println(cmd + " " + line);
		    }
		  }
		
	    private static boolean runProcess(String command) throws Exception {
		    Process pro = Runtime.getRuntime().exec(command);
		    printLines(command + " stdout:", pro.getInputStream());
		    //printLines(command + " stderr:", pro.getErrorStream());
		    pro.waitFor();
		    System.out.println(command + " exitValue() " + pro.exitValue());
		    return pro.exitValue() == 0;
	    }
	
	private String addProgramText(String programText, String toAdd) {
		return programText + toAdd;
	}
}
