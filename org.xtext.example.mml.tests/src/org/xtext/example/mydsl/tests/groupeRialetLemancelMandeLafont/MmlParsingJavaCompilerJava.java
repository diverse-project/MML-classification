package org.xtext.example.mydsl.tests.groupeRialetLemancelMandeLafont;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.mml.*;

import com.google.common.io.Files;

public class MmlParsingJavaCompilerJava extends Compiler {

	Pair<Boolean,ArrayList<String>> results;
	boolean stop = false;
	String strategy = "";
	StratificationMethod strat ;
	int number;
	String algoName;
	
	public Pair<Boolean,ArrayList<String>> compileDataInput(MMLModel model, MLAlgorithm al, int numAlgo) throws Exception {
		
		super.init();
		
		String fileLocation = model.getInput().getFilelocation();
		
		/* Construction imports */
		imports = addInstruction(imports, "package output_LAFONT_LEMANCEL_MANDE_RIALET;");
		imports = addInstruction(imports, "import java.io.File;"); 
		imports = addInstruction(imports, "import java.util.Random;");
		imports = addInstruction(imports, "import weka.classifiers.functions.Logistic;"); 
		imports = addInstruction(imports, "import weka.classifiers.trees.J48;");
		imports = addInstruction(imports, "import weka.classifiers.trees.RandomForest;");
		imports = addInstruction(imports, "import weka.classifiers.Classifier;");
		imports = addInstruction(imports, "import weka.classifiers.Evaluation;"); 
		imports = addInstruction(imports, "import weka.core.converters.CSVLoader;");
		imports = addInstruction(imports, "import weka.core.Instances;"); 
		imports = addInstruction(imports, "public class Mml_" + numAlgo + " {");
		imports = addInstruction(imports, "\tpublic static void main(String[] args) throws Exception {");
		
		/* csvReading */
		csvReading = addInstruction(csvReading, "\t\tCSVLoader loader = new CSVLoader();");
		csvReading = addInstruction(csvReading, "\t\tloader.setSource(new File(System.getProperty(\"user.dir\")+\"\\\\output_LAFONT_LEMANCEL_MANDE_RIALET\\\\" + fileLocation + "\"));");	
		
		/* Construction dataSet */
		String dataSetCreation = "\t\tInstances data = loader.getDataSet();\n";
		dataSetCreation += "\t\tdata.randomize(new Random());\n\n";
		dataSetCreation +="\t\tdata.setClassIndex(data.numAttributes()-1);\n";
		
		strat = model.getValidation().getStratification();
		number = strat.getNumber();
		
		if (strat instanceof TrainingTest) {
			strategy = "tt";
			dataSetCreation +="\t\tint trainSize = (int) Math.round(data.numInstances() * 0." + number + ");\n";
			dataSetCreation +="\t\tint testSize = data.numInstances() - trainSize;\n";
			dataSetCreation += "\t\tInstances train = new Instances(data, 0, trainSize);\n";
			dataSetCreation += "\t\tInstances test = new Instances(data, trainSize, testSize);\n";
		}else if(strat instanceof CrossValidation) {
			strategy = "cv";
		}
		
		/* Algorithm */
		this.algorithmTreatment(al);
		
		/* Stratification and Metrics */
		this.stratificationAndMetrics(model);
		
		/* Construction program */
		program = addInstruction(program, imports);
		program = addInstruction(program, csvReading);
		program = addInstruction(program, dataSetCreation);
		program = addInstruction(program, algorithm);
		program = addInstruction(program, stratification);
		program = addInstruction(program, metriques);
		program = addInstruction(program, "\t}\n}");
		
		/* Generation program */
		Files.write(program.getBytes(), new File("output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_" + numAlgo + ".java"));
		
		/* Execution program */
		try {
            System.out.println("**********");
            runProcess("javac -cp output_LAFONT_LEMANCEL_MANDE_RIALET/weka.jar output_LAFONT_LEMANCEL_MANDE_RIALET/Mml_" + numAlgo + ".java",algoName, "Weka");
            System.out.println("**********");
            results = runProcess("java -cp .;output_LAFONT_LEMANCEL_MANDE_RIALET/weka.jar output_LAFONT_LEMANCEL_MANDE_RIALET.Mml_" + numAlgo, algoName, "Weka");
        } catch (Exception e) {
            e.printStackTrace();
        }
		return results;
    }
	
	/* Algorithm treatment */
	public void algorithmTreatment(MLAlgorithm al) {

		String dataSetName = algorithm == "tt" ? "train" : "data";
		
		if (al instanceof DT) {
			algoName = "DT";
			algorithm = addInstruction(algorithm, "\t\tClassifier cls = new J48();"); 
			algorithm = addInstruction(algorithm, "\t\tcls.buildClassifier(" + dataSetName + ");");
		}
		else if (al instanceof SVM) {
			algoName = "SVM";
			stop = true;
			algorithm = addInstruction(algorithm, "\t\tSystem.out.println(\"Impossible d'utiliser SVM avec Weka\");");
		}
		else if (al instanceof RandomForest) {
			algoName = "RandomForest";
			algorithm = addInstruction(algorithm, "\t\tClassifier cls = new RandomForest();");
			algorithm =	addInstruction(algorithm, "\t\tcls.buildClassifier(" + dataSetName + ");");
		}
		else if (al instanceof LogisticRegression) {
			algoName = "LogisticRegression";
			algorithm = addInstruction(algorithm, "\t\tClassifier cls = new Logistic();"); 
			algorithm = addInstruction(algorithm, "\t\tcls.buildClassifier(" + dataSetName + ");");
		}
	}
	
	/* Stratification and Metrics treatment */
	public void stratificationAndMetrics(MMLModel model) {
		
		if (!stop) {
			EList<ValidationMetric> metric = model.getValidation().getMetric();
			ValidationMetric[] metriquesArray = (ValidationMetric[]) metric.toArray();
			
			//Stratification
			if (strategy == "tt") {
				stratification = addInstruction(stratification, "\t\tEvaluation eval = new Evaluation(train);"); 
				stratification = addInstruction(stratification, "\t\teval.evaluateModel(cls, test);");
				
			}
			else if (strategy == "cv") {
				stratification = addInstruction(stratification, "\t\tEvaluation eval = new Evaluation(data);"); 
				stratification = addInstruction(stratification, "\t\teval.crossValidateModel(cls, data, " + number + ", new Random(1));");
			}
			
			//Metriques
			for (int i = 0 ; i < metriquesArray.length ; i++) {
				if (metriquesArray[i] == ValidationMetric.ACCURACY) {
					metriques = addInstruction(metriques, "\t\tSystem.out.println(\"Accuracy___\"+eval.pctCorrect());");			
				}
				else if (metriquesArray[i] == ValidationMetric.RECALL) {
					metriques = addInstruction(metriques, "\t\tSystem.out.println(\"Recall___\"+eval.weightedRecall()*100);");
				}
				else if (metriquesArray[i]==ValidationMetric.F1) {
					metriques = addInstruction(metriques, "\t\tSystem.out.println(\"F1___\"+eval.weightedFMeasure()*100);");
				}
				else if (metriquesArray[i]==ValidationMetric.PRECISION) {
					metriques = addInstruction(metriques, "\t\tSystem.out.println(\"Precision___\"+eval.weightedPrecision()*100);");
				}
				else {
					metriques = addInstruction(metriques, "\t\tSystem.out.println(\"La métrique " + metriquesArray[i].getLiteral() + " n'est pas supportée avec Weka.\");");
				}
			}
		}
	}
}