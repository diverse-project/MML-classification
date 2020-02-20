package org.xtext.example.mydsl.tests;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.*;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaCompilerJava {
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
	
	public void compileDataInput(MMLModel model, MLAlgorithm al, int numAlgo) throws Exception {
		String fileLocation = model.getInput().getFilelocation();
		String javaImport = "package output_LAFONT_LEMANCEL_MANDE_RIALET;\n";
		javaImport+= "import java.io.File;\n"; 
		javaImport+="import java.util.Random;\n";
		javaImport+="import weka.classifiers.functions.Logistic;\n"; 
		javaImport+="import weka.classifiers.trees.J48;\n";
		javaImport+="import weka.classifiers.trees.RandomForest;\n";
		javaImport+="import weka.classifiers.Classifier;\n";
		javaImport+="import weka.classifiers.Evaluation;\n"; 
		javaImport+="import weka.core.converters.CSVLoader;\n";
		javaImport+="import weka.core.Instances;\n"; 
		javaImport+="public class Mml_"+numAlgo +" {\n";
		javaImport+="\tpublic static void main(String[] args) throws Exception {\n";
		
		String csvReading = "\t\tCSVLoader loader = new CSVLoader();\n";
		csvReading += "\t\tloader.setSource(new File(System.getProperty(\"user.dir\")+\"\\\\output_LAFONT_LEMANCEL_MANDE_RIALET\\\\"+fileLocation+"\"));\n";	
		
		String dataSetCreation = "\t\tInstances data = loader.getDataSet();\n";
		dataSetCreation += "\t\tdata.randomize(new Random());\n\n";
		dataSetCreation +="\t\tdata.setClassIndex(data.numAttributes()-1);\n";
		
		StratificationMethod strat = model.getValidation().getStratification();
		String strategy = "";
		int number = strat.getNumber();
		if (strat instanceof TrainingTest) {
			strategy = "tt";
			dataSetCreation +="\t\tint trainSize = (int) Math.round(data.numInstances() * 0."+number+");\n";
			dataSetCreation +="\t\tint testSize = data.numInstances() - trainSize;\n";
			dataSetCreation += "\t\tInstances train = new Instances(data, 0, trainSize);\n";
			dataSetCreation += "\t\tInstances test = new Instances(data, trainSize, testSize);\n";
		}else if(strat instanceof CrossValidation) {
			strategy = "cv";
		}
		
		//Algorithm		
		String algorithm = "", dataSetName = algorithm == "tt"?"train":"data";
		boolean stop = false;
		if (al instanceof DT) {
			algorithm = "\t\tClassifier cls = new J48();\n"; 
			algorithm +="\t\tcls.buildClassifier("+dataSetName+");\n";
		}
		else if (al instanceof SVM) {
			stop = true;
			algorithm = "\t\tSystem.out.println(\"Impossible d'utiliser SVM avec Weka\");\n";
		}
		else if (al instanceof RandomForest) {
			algorithm = "\t\tClassifier cls = new RandomForest();\n";
			algorithm+=	"\t\tcls.buildClassifier("+dataSetName+");\n";
		}
		else if (al instanceof LogisticRegression) {
			algorithm = "\t\tClassifier cls = new Logistic();\n"; 
			algorithm+= "\t\tcls.buildClassifier("+dataSetName+");\n";
		}
		
		String stratification = "",metriques = "";
		if(!stop) {
			EList<ValidationMetric> metric = model.getValidation().getMetric();
			ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
			
			//Stratification
			if(strategy == "tt") {
				stratification = "\t\tEvaluation eval = new Evaluation(train);\n"; 
				stratification += "\t\teval.evaluateModel(cls, test);\n";
				
			}else if(strategy == "cv") {
				stratification = "\t\tEvaluation eval = new Evaluation(data);\n"; 
				stratification += "\t\teval.crossValidateModel(cls, data, "+number+", new Random(1));\n";
			}
			
			//Metriques
			for(int i = 0; i < metriquesArray.length; i++) {
				if(metriquesArray[i] == ValidationMetric.ACCURACY) {
					metriques += "\t\tSystem.out.println(\"Accuracy = \"+eval.pctCorrect());\n";			
				}
				else if(metriquesArray[i] == ValidationMetric.RECALL) {
					metriques +="\t\tSystem.out.println(\"Recall = \"+eval.weightedRecall()*100);\n";
				}
				else if(metriquesArray[i]==ValidationMetric.F1) {
					metriques +="\t\tSystem.out.println(\"F1 = \"+eval.weightedFMeasure()*100);\n";
				}
				else if(metriquesArray[i]==ValidationMetric.PRECISION) {
					metriques +="\t\tSystem.out.println(\"Precision =\"+eval.weightedPrecision()*100);\n";
				}else {
					metriques +="\t\tSystem.out.println(\"La métrique "+metriquesArray[i].getLiteral()+" n'est pas supportée avec Weka.\");\n";
				}
			}
		}
		
		
		//build python code
		String javaCode = addJavaText("", javaImport);
		javaCode = addJavaText(javaCode, csvReading);
		javaCode = addJavaText(javaCode, dataSetCreation);
		javaCode = addJavaText(javaCode, algorithm);
		javaCode = addJavaText(javaCode, stratification);
		javaCode = addJavaText(javaCode, metriques);
		javaCode = addJavaText(javaCode, "\t}\n}");
		
		Files.write(javaCode.getBytes(), new File("output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_"+numAlgo+".java"));
		// end of Java generation
		
		
		
		try {
            System.out.println("**********");
            runProcess("javac -cp output_LAFONT_LEMANCEL_MANDE_RIALET/weka.jar output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_"+numAlgo+".java");
            System.out.println("**********");
            //runProcess("java -cp .:output_LAFONT_LEMANCEL_MANDE_RIALET/weka.jar output_LAFONT_LEMANCEL_MANDE_RIALET.Mml_"+numAlgo);
            runProcess("java -cp .;output_LAFONT_LEMANCEL_MANDE_RIALET/weka.jar output_LAFONT_LEMANCEL_MANDE_RIALET.Mml_"+numAlgo);
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
	    printLines(command + " stderr:", pro.getErrorStream());
	    pro.waitFor();
	    System.out.println(command + " exitValue() " + pro.exitValue());
    }

	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}
	
	private String addJavaText(String javaCode, String toAdd) {
		return javaCode + toAdd;
	}
}
