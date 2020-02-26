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
	public void compileDataInput(MMLModel model, MLAlgorithm al, int numAlgo) throws Exception {
		DataInput dataInput = model.getInput();
		String fileLocation = dataInput.getFilelocation();
		
		/*String installPackages = "install.packages(\"caret\")\n";
		installPackages += "install.packages(\"readr\")\n";
		installPackages += "install.packages(\"randomForest\")\n";
		installPackages += "install.packages(\"LogicReg\")\n";
		installPackages += "install.packages(\"e1071\")\n";
		installPackages += "install.packages(\"party\")\n";*/
		
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
		
		String readCsv = "data <- read_csv(\"output_LAFONT_LEMANCEL_MANDE_RIALET/" + fileLocation + "\")\n";
		
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
			if (!f.getPredictive().getColName().equalsIgnoreCase("")) {
				predictive = "predictive <- " + f.getPredictive().getColName() + "\n";
			}
			else if (f.getPredictive().getColumn() != 0) {
				/* On ajoute +1 car R ajoute une première colonne qui correspond à une PK */
				predictive = "predictive <- names(data[" + (f.getPredictive().getColumn() + 1) + "])\n";
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
					if(colName.length() > 0 ) {
						predictors += "predictors <- (predictors, " + colName + ")\n";
					}
					else {
						predictors += "predictors <- (predictors, names(data[" + colIndex + "])\n";
					}
				}
			}
		}
		
		formula = "formula <- reformulate(termlabels = predictors, response = predictive)\n";
		

		String methode = "";
		String parametres = "";
		String ecrireAlgo = "";
		
		if (al instanceof DT) {
			DT defAlg = (DT) al;
			int maxDepth = defAlg.getMax_depth();
			
			methode = "ctree2";
			
			if(maxDepth == 0) {
				parametres = "";
			}
			else {
				parametres = ",maxdepth=" + maxDepth;
			}
			
			ecrireAlgo += "model <- train(formula, data=data_train,method=\"" + methode + "\"" + parametres + ",trControl=fitControl)";
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
					case "radial" 		: kernel = "'radial basis'"; 		break;
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
						ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=" + classification;
						if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
						ecrireAlgo += ", kernel=" + kernel;
						ecrireAlgo += ", cost=" + c;
						ecrireAlgo += ")";
						break;
						
					case "nu-classification" :
						ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=" + classification;
						if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
						ecrireAlgo += ", kernel=" + kernel;
						ecrireAlgo += ", cost=" + c;
						ecrireAlgo += ", nu=" + c;
						ecrireAlgo += ")";
						break;
						
					case "one-classification" : 
						ecrireAlgo	= "model <- svm(formula=formula, data=data_train, type=" + classification;
						if (gamma != null) ecrireAlgo += ", gamma=" + gamma;
						ecrireAlgo += ", kernel=" + kernel;
						ecrireAlgo += ", cost=" + c;
						ecrireAlgo += ", nu=" + c;
						ecrireAlgo += ")";
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
			methode = "rf";
			ecrireAlgo += "model <- train(formula, data=data_train,method=\"" + methode + "\",trControl=fitControl)";
		}
		else if (al instanceof LogisticRegression) {
			methode = "logreg";
			ecrireAlgo += "model <- train(formula, data=data_train,method=\"" + methode + "\",trControl=fitControl)";
		
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
		prediction += "mat <- confusionMatrix(table(pred,data_test[[predictive]]))\n";
		
		ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
		String metrique = "";
		String metriques = "";
		for(int i = 0; i < metriquesArray.length; i++) {
			metrique = metriquesArray[i].getLiteral();
			if(metrique == "accuracy") {
				metriques += "print(\"Accuracy\")\n";
				metriques +="print(mat$overall[\"Accuracy\"])\n";
			}else if(metrique == "balanced_accuracy") {
				metriques += "print(\"Balanced Accuracy\")\n";
				metriques += "print(mat$byClass[,\"Balanced Accuracy\"])\n";
			}else if(metrique == "recall") {
				metriques += "print(\"Recall\")\n";
				metriques += "print(mat$byClass[,\"Recall\"])\n";
			}else if(metrique == "macro_recall") {
				metriques += "print(\"Macro Recall\")\n";
				metriques += "print(mean(mat$byClass[,\"Recall\"],na.rm=TRUE))\n";
			}else if(metrique == "precision") {
				metriques += "print(\"Precision\")\n";
				metriques += "print(mat$byClass[,\"Precision\"])\n";
			}else if(metrique == "macro_precision") {
				metriques += "print(\"Macro Precision\")\n";
				metriques += "print(mean(mat$byClass[,\"Precision\"],na.rm=TRUE))\n";
			}else if(metrique == "F1") {
				metriques += "print(\"F1\")\n";
				metriques += "print(mat$byClass[,\"F1\"])\n";
			}else if(metrique == "macro_F1") {
				metriques += "print(\"Macro F1\")\n";
				metriques += "print(mean(mat$byClass[,\"F1\"],na.rm=TRUE))\n";
			}
			else if(metrique == "macro_accuracy") {
				metriques += "print(\"Macro Accuracy non supporté\")\n";
			}
		}
		
		
		String writeProgram = addProgramText("", installPackages);
		writeProgram += addProgramText("", importPackages);
		writeProgram += addProgramText("", readCsv);
		writeProgram += addProgramText("", predictive);
		writeProgram += addProgramText("", predictors);
		writeProgram += addProgramText("", formula);
		writeProgram += addProgramText("", stratification);
		writeProgram += addProgramText("", ecrireAlgo);
		writeProgram += addProgramText("", prediction);
		writeProgram += addProgramText("", metriques);
	

		
		Files.write(writeProgram.getBytes(), new File("output_LAFONT_LEMANCEL_MANDE_RIALET/mml_"+numAlgo+".r"));

	
		try {
            System.out.println("**********");
            //runProcess("cd output_LAFONT_LEMANCEL_MANDE_RIALET");
            runProcess("Rscript output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_"+numAlgo+".r");
            System.out.println("**********");
            //runProcess("java -cp src com/journaldev/files/Test Hi Pankaj");
        } catch (Exception e) {
            e.printStackTrace();
        }
		

		
		
	}
	
	 private static void printLines(String cmd, InputStream ins) throws Exception {
	    	String line = null;
	    	BufferedReader in = new BufferedReader(
		    new InputStreamReader(ins));
	    	while ((line = in.readLine()) != null) {
	    		System.out.println(cmd + " " + line);
		    }
		  }
		
	    private static void runProcess(String command) throws Exception {
		    Process pro = Runtime.getRuntime().exec(command);
		    printLines(command + " stdout:", pro.getInputStream());
		    //printLines(command + " stderr:", pro.getErrorStream());
		    pro.waitFor();
		    System.out.println(command + " exitValue() " + pro.exitValue());
	    }
	
	private String addProgramText(String programText, String toAdd) {
		return programText + toAdd;
	}
}
