package org.xtext.example.mydsl.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.management.loading.MLetMBean;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.RFormula;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.StratificationMethod;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.mml.XFormula;
import org.xtext.example.mydsl.mml.impl.MLAlgorithmImpl;
import org.xtext.example.mydsl.services.MmlGrammarAccess.MLAlgorithmElements;

import com.google.inject.Inject;


@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingR {

	@Inject
	ParseHelper<MMLModel> parseHelper;

	@Test
	public void compileDataInput() throws Exception {
		HashMap<String, MMLModel> map = (HashMap<String, MMLModel>) loadModel();
		for (String fileName : map.keySet()) {
			MMLModel model = map.get(fileName);
			List<MLChoiceAlgorithm> algos = model.getAlgorithms();
			for (MLChoiceAlgorithm alg : algos) {
				if (alg.getFramework() == FrameworkLang.R) {
					StringBuilder rFileContent = new StringBuilder();
					DataInput data = model.getInput();
					String fileLocation = data.getFilelocation();
					CSVParsingConfiguration csvConf = data.getParsingInstruction();
					String csvSeparator = (csvConf.getSep() != null ? csvConf.getSep().getLiteral() : ";");
					
					// Installation et import des librairies
					rFileContent.append("install.packages(\"caret\")").append(System.lineSeparator());
					rFileContent.append("install.packages(\"e1071\")").append(System.lineSeparator());
					if (alg.getAlgorithm() instanceof DT) {
						rFileContent.append("install.packages(\"rpart\")").append(System.lineSeparator());
						rFileContent.append("library(rpart)").append(System.lineSeparator());
					} else if (alg.getAlgorithm() instanceof LogisticRegression) {
						rFileContent.append("install.packages(\"nnet\")").append(System.lineSeparator());
						rFileContent.append("library(nnet)").append(System.lineSeparator());
					} else if (alg.getAlgorithm() instanceof RandomForest) {
						rFileContent.append("install.packages(\"randomForest\")").append(System.lineSeparator());
						rFileContent.append("library(randomForest)").append(System.lineSeparator());
					} else if (alg.getAlgorithm() instanceof SVM) {
						
					}
					rFileContent.append("library(caret)").append(System.lineSeparator());
					rFileContent.append("library(e1071)").append(System.lineSeparator());
					
					// Lecture du CSV
					rFileContent.append("data <- read.csv(\"" + fileLocation + "\", header=TRUE, sep=\"" + csvSeparator + "\")").append(System.lineSeparator());
					// Mélange les lignes pour éviter les fichiers triés
					rFileContent.append("shuffleIndex <- sample(1:nrow(data))").append(System.lineSeparator());
					rFileContent.append("data <- data[shuffleIndex,]").append(System.lineSeparator());
					
					// Gestion de la colonne à prédire
					FormulaItem predictive = (model.getFormula() != null ? model.getFormula().getPredictive() : null);
					XFormula predictors = (model.getFormula() != null ? model.getFormula().getPredictors() : null);
					String toPredict = (predictive != null ? predictive.getColName() : "");
					if (toPredict.length() == 0) {
						BufferedReader reader = new BufferedReader(new FileReader("src" + File.separator + "test" + File.separator + "resources" + File.separator + fileLocation));
						String columnNames = reader.readLine();
						toPredict = columnNames.split(csvSeparator)[columnNames.split(csvSeparator).length - 1].replace("\"", "");
						reader.close();
					}
					
					// Récupération des paramètres utiles pour entrainer le modèle
					Validation validation = model.getValidation();
					StratificationMethod stratMethod = validation.getStratification();
					validation.getMetric();
					validation.getStratification();
					int trControlValue = 0;
					float sampleSize = 0.8f;
					if (stratMethod instanceof CrossValidation) {
						trControlValue = validation.getStratification().getNumber();
					} else if (stratMethod instanceof TrainingTest) {
						sampleSize = 1 - validation.getStratification().getNumber() / 100;
					}
					
					// Création des jeux de données
//					> trainSample <- 1: (0.8 * nrow(data))
//					> dataTrain <- data[trainSample, ]
//					> dataTest <- data[-trainSample, ]
					rFileContent.append("trainSample <- 1: (" + sampleSize + " * nrow(data))").append(System.lineSeparator());
					rFileContent.append("dataTrain <- data[trainSample, ]").append(System.lineSeparator());
					rFileContent.append("dataTest <- data[-trainSample, ]").append(System.lineSeparator());
					
					// Entrainement du modèle et prédiction sur le jeu de test (sans la colonne à prédire)
					if (alg.getAlgorithm() instanceof DT) {
//						DecisionTree https://www.guru99.com/r-decision-trees.html
//						> model <- rpart(toPredict~., data=dataTrain, method='class')
//						> prediction <- predict(model, dataTest, type='class')
						if (trControlValue != 0) {
							rFileContent.append("trControl <- trainControl(method=\"cv\", number=" + trControlValue + ")").append(System.lineSeparator());
							rFileContent.append("model <- train(" + toPredict + "~., data=dataTrain, method=\"rpart\", trControl=trControl, metric=\"Accuracy\")").append(System.lineSeparator());
						} else {
							rFileContent.append("model <- rpart(\" + toPredict + \"~., data=dataTrain, method='class')").append(System.lineSeparator());
						}
						rFileContent.append("prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1]" + (trControlValue != 0 ? "" : ", type='class'") + ")").append(System.lineSeparator());
					} else if (alg.getAlgorithm() instanceof LogisticRegression) {
//						LogisticRegression https://www.r-bloggers.com/how-to-perform-a-logistic-regression-in-r/
//										 & https://stats.stackexchange.com/questions/175782/how-to-perform-a-logistic-regression-for-more-than-2-response-classes-in-r
//						> model <- glm(formula=toPredict~., family=binomial(link="logit"), data=dataTrain)
//						> model <- multinom(toPredict~., data=dataTrain))
//						> prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1] [, type="response"])
						rFileContent.append("classNb <- length(dataTest$" + toPredict + "~.)").append(System.lineSeparator());
						// Les ifelse ayant un comportement imprévu pour l'attribution des variables, il est nécessaire de passer par les ifs standards
						if (trControlValue != 0) {
							rFileContent.append("trControl <- trainControl(method=\"cv\", number=" + trControlValue + ")").append(System.lineSeparator());
							rFileContent.append("model <- ").append(System.lineSeparator());
							rFileContent.append("if (classNb <= 2) {\r\n" + 
									"    model <- train(" + toPredict + "~., data=dataTrain, method=\"glm\", family=binomial, trControl=trControl)\r\n" + 
									"} else {\r\n" + 
									"    model <- train(" + toPredict + "~., data=dataTrain, method=\"multinom\", trControl=trControl)\r\n" + 
									"}")
							.append(System.lineSeparator());
						} else {
							rFileContent.append("if (classNb <= 2) {\r\n" + 
									"    model <- glm(formula=variety~., family=binomial, data=dataTrain)\r\n" + 
									"} else {\r\n" + 
									"    model <- multinom(variety~., data=dataTrain)\r\n" + 
									"}")
							.append(System.lineSeparator());
						}
						rFileContent.append("if (classNb <= 2) {\r\n" + 
								"    prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1], type=\"response\")\r\n" + 
								"} else {\r\n" + 
								"    prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1])\r\n" + 
								"}")
						.append(System.lineSeparator());
					} else if (alg.getAlgorithm() instanceof RandomForest) {
//						RandomForest https://www.guru99.com/r-random-forest-tutorial.html
//						> trControl <- trainControl(method="cv", number=INT)
//						> model <- train(toPredict~, data=dataTrain, method="rf", trControl=trControl)
						if (trControlValue != 0) {
							rFileContent.append("trControl <- trainControl(method=\"cv\", number=" + trControlValue + ")").append(System.lineSeparator());
						}
						rFileContent.append("model <- train(" + toPredict + "~., data=dataTrain, method=\"rf\", " + (trControlValue != 0 ? "trControl=trControl, " : "") + "metric=\"Accuracy\")").append(System.lineSeparator());
						rFileContent.append("prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1])").append(System.lineSeparator());
					} else if (alg.getAlgorithm() instanceof SVM) {
//						SVM
//						> model <- svm(toPredict~., data=dataTrain, kernel="linear", scale=FALSE, type="C-classification")
//						>
						SVM algSVM = (SVM) alg.getAlgorithm();
						String gamma = (algSVM.getGamma() != null ? algSVM.getGamma() : "");
						String cost = (algSVM.getC() != null ? algSVM.getGamma() : "");
						String kernel = (algSVM.isKernelSpecified() ? algSVM.getKernel().getName() : "");
						String classification = (algSVM.isClassificationSpecified() ? algSVM.getSvmclassification().getLiteral() : "");
						rFileContent.append("model <- svm(" + toPredict + "~., data=dataTrain"
								+ (classification.length() > 0 ? ", type=\"" + classification + "\"" : "")
								+ (kernel.length() > 0 ? ", kernel=\"" + kernel + "\"" : "")
								+ (gamma.length() > 0 ? ", gamma=" + gamma : "")
								+ (cost.length() > 0 ? ", cost=" + cost : "")
								+ (trControlValue != 0 ? ", cross=" + trControlValue : "")
								+ ")")
						.append(System.lineSeparator());
						rFileContent.append("prediction <- predict(model, dataTest[, 1:ncol(dataTest) - 1])").append(System.lineSeparator());
					}
					
					// Calcul des résultats (validation)
//					> inPredictTest <- dataTest[,ncol(dataTest)]
//					> matrix <- confusionMatrix(prediction, inPredictTest, mode="everything")
//					> classRep <- matrix$byClass
//					> paste(mean(classRep[TRUE,c("NOM_VARIABLE")]))
//					   - Sensitivity
//					   - Specificity
//					   - Pos Pred Value
//					   - Neg Pred Value
//					   - Precision
//					   - Recall
//					   - F1
//					   - Prevalence
//					   - Detection Rate
//					   - Detection Prevalence
//					   - Balanced Accuracy
					// Si LogisticRegression binomiale ? (testé mais pas sur du vrai binomial)
//					if (alg.getAlgorithm() instanceof LogisticRegression) {
//						rFileContent.append("matrix <- table(dataTest$" + toPredict + ", predict > 0.5)");
//						rFileContent.append("ACCURACY <- sum(diag(matrix)) / sum(matrix)");
//						rFileContent.append("print(contrasts(dataTest$" + toPredict + "))");
//					} else {
					List<ValidationMetric> valMetCol = validation.getMetric();
					rFileContent.append("inPredictTest <- dataTest[,ncol(dataTest)]").append(System.lineSeparator());
					rFileContent.append("matrix <- confusionMatrix(prediction, inPredictTest, mode=\"everything\")").append(System.lineSeparator());
					rFileContent.append("classRep <- matrix$byClass").append(System.lineSeparator());
					for (ValidationMetric valMet : valMetCol) {
						if (valMet.getName().contains("MACRO")) {
							rFileContent.append(valMet.getName() + " <- paste(mean(classRep[TRUE,c(\"");
							switch (valMet.getName()) {
								case "MACRO_RECALL":
									rFileContent.append("Recall");
									break;
								case "MACRO_PRECISION":
									rFileContent.append("Precision");
									break;
								case "MACRO_F1":
									rFileContent.append("F1");
									break;
								case "MACRO_ACCURACY":
									rFileContent.append("Balanced Accuracy");
									break;
							}
							rFileContent.append(")]))");
						} else if (valMet.getName() == "ACCURACY") {
							rFileContent.append("ACCURACY <- matrix$overall[\"Accuracy\"]").append(System.lineSeparator());
						} else {
							rFileContent.append(valMet.getName() + " <- classRep[TRUE,c(\"");
							switch (valMet.getName()) {
								case "BALANCED_ACCURACY":
									rFileContent.append("Balanced Accuracy");
									break;
								case "RECALL":
									rFileContent.append("Recall");
									break;
								case "PRECISION":
									rFileContent.append("Precision");
									break;
								case "F1":
									rFileContent.append("F1");
									break;
							}
							rFileContent.append("\")]").append(System.lineSeparator());
						}
						rFileContent.append("message(\"" + valMet.getName() + " :\")").append(System.lineSeparator());
						rFileContent.append("print(" + valMet.getName() + ")").append(System.lineSeparator());
					}
//					}
					saveFileContent(fileName, alg.getAlgorithm().getClass().getInterfaces()[0].getSimpleName(), rFileContent);
				}
			}
//			saveFileContent(fileName, alg.getAlgorithm().getClass().getInterfaces()[0].getSimpleName(), rFileContent);
//			saveFileContent(fileName, lastAlgName, rFileContent);
//			saveFileContent(rFileContent);
			
//			Process proc = Runtime.getRuntime().exec("D:\\Programmes\\R\\bin\\Rscript.exe --vanilla --slave D:\\Donnees\\Cours\\MIAGE\\M2\\IDM\\Projet\\MML-classification\\org.xtext.example.mml.tests\\src\\test\\resources\\results\\final.r");
//			BufferedReader stdInput = new BufferedReader(new 
//				     InputStreamReader(proc.getInputStream()));
//			BufferedReader stdError = new BufferedReader(new 
//				     InputStreamReader(proc.getErrorStream()));
//			// Read the output from the command
//			System.out.println("Here is the standard output of the command:\n");
//			String s = null;
//			while ((s = stdInput.readLine()) != null) {
//			    System.out.println(s);
//			}
//			// Read any errors from the attempted command
//			System.out.println("Here is the standard error of the command (if any):\n");
//			while ((s = stdError.readLine()) != null) {
//			    System.out.println(s);
//			}
		}
	}
	
	private Map<String, MMLModel> loadModel() throws Exception {
		HashMap<String, MMLModel> map = new HashMap<>();
		for (int i = 1; i<11;i++) {
			File f = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "test" + i + ".mml");
			MMLModel result = parseHelper.parse(FileUtils.readFileToString(f, Charset.defaultCharset()));
			map.put(f.getName(), result);
		}
		return map;
	}
	
	private void saveFileContent(String fileName, String algName, StringBuilder sb) {
		try {
			Path newFile = Paths.get("src" + File.separator + "test" + File.separator + "resources" + File.separator + "results" + File.separator + fileName + "." + algName + ".r");
			Files.write(newFile, sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		sb = new StringBuilder();
	}

}
