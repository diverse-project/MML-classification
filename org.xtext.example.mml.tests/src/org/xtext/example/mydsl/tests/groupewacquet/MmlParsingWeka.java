package org.xtext.example.mydsl.tests.groupewacquet;

import org.xtext.example.mydsl.mml.*;
import org.xtext.example.mydsl.mml.DataInput;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MmlParsingWeka {

    @Inject
    ParseHelper<MMLModel> parseHelper;

    public List<MMLModel> loadModel() throws Exception {
        ArrayList<MMLModel> list = new ArrayList<MMLModel>();
        for (int i = 1; i<11;i++) {
            MMLModel result = parseHelper.parse(
                    FileUtils.readFileToString(
                            new File(
                                    "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test"+i+".mml")
                            , Charset.defaultCharset()));
            list.add(result);
        }
        return list;
    }

    @Test
    public void compileDataInput() throws Exception {
        ArrayList<MMLModel> list = (ArrayList<MMLModel>) loadModel();
        for (MMLModel result : list)
        {
            MLChoiceAlgorithm algo = result.getAlgorithm();
            System.out.println();
            if (algo.getFramework() == FrameworkLang.JAVA_WEKA)
            {
            	//Gestion des imports
                String javaWekaImport = "import java.io.*;\r\n" +
                        "import java.util.*;\r\n" +
                        "import weka.core.*;\r\n" +
                        "import weka.classifiers.*;\r\n" +
                        "import weka.core.converters.ConverterUtils.DataSource\r\n";
                //Fin de gestion des imports
				DataInput dataInput = result.getInput();
				String fileLocation = dataInput.getFilelocation();
				
				//Gestion du CSV
                String csvReading = "DataSource source = new DataSource("+ mkValueInSingleQuote(fileLocation) +");\n" + 
                					"Instances data = source.getDataSet();"+
                					"if (data.classIndex() == -1)\n" + 
                					"data.setClassIndex(data.numAttributes() - 1);";
                //Fin gestion du CSV
				//Validation
				String validation = "";
				Boolean isCrossValidation = false;
				if(result.getValidation().getStratification() instanceof CrossValidation)
				{
					validation = ""+((CrossValidation)result.getValidation().getStratification()).getNumber();
					isCrossValidation = true;
				}
				else //TrainingTest
				{
					validation = ""+((TrainingTest)result.getValidation().getStratification()).getNumber();
				}
				//fin validation
				//Itération en fonction de l'algorithme demandé
				MLAlgorithm algorithm = algo.getAlgorithm();
				String iteration = "";
				//String splitting ="";
				if(algorithm instanceof DT) {
						iteration+= "Instances testingDataSet = data;"+
									"Classifier classifier = new J48();"+
									"classifier.buildClassifier(trainingDataSet);"+
									"Evaluation eval = new Evaluation(testingDataSet);"+
									"eval.evaluateModel(classifier, testingDataSet)"+
									"System.out.println("+mkValueInSingleQuote("DecisionTreeEvaluation")+")"+
									"System.out.println(mkValueInSingleQuote(eval.toSummaryString()))"+
									"System.out.println("+mkValueInSingleQuote(" the expression for the input data as per algorithm is ")+")"+
									"System.out.println(classifier);"+
									"System.out.println(eval.toMatrixString());"+
									"System.out.println(eval.toClassDetailsString());";
						
				}else if (algorithm instanceof LogisticRegression) {
					iteration+="Instances testingDataSet = data;"+
							"Classifier classifier = new weka.classifiers.functions.Logistic();"+
							"forest.buildClassifier(testingDataSet);"+
							"Evaluation eval = new Evaluation(testingDataSet)"+
							"eval.evaluateModel(classifier, testingDataSet);"+
							"System.out.println("+mkValueInSingleQuote("LogisticRegression Evaluation")+")"+
							"System.out.println(mkValueInSingleQuote(eval.toSummaryString()))"+
							"System.out.println("+mkValueInSingleQuote(" the expression for the input data as per algorithm is ")+")"+
							"System.out.println(classifier);"+
							"System.out.println(eval.toMatrixString());"+
							"System.out.println(eval.toClassDetailsString());";
				}else if(algorithm instanceof  RandomForest) {
					iteration+="Instances testingDataSet = data;"+
								"RandomForest forest = new RandomForest();"+
								"forest.setNumTrees(10);"+
								"forest.buildClassifier(testingDataSet);"+
								"Evaluation eval = new Evaluation(testingDataSet)"+
								"eval.evaluateModel(forest, testingDataSet);"+
								"System.out.println("+mkValueInSingleQuote("RandomForestEvaluation")+")"+
								"System.out.println(mkValueInSingleQuote(eval.toSummaryString()))"+
								"System.out.println("+mkValueInSingleQuote(" the expression for the input data as per algorithm is ")+")"+
								"System.out.println(forest);"+
								"System.out.println(eval.toMatrixString());"+
								"System.out.println(eval.toClassDetailsString());";+
								"double value = classifier.classifyInstance(testingDataSet);"+
								"System.out.println(value);";
				}else {//algorithm instanceof SVM
					iteration+="!!! Algorith SVM not availlable !!!";
				}
				//Fin itération
                
                
                
                
                
                String wekaCode = javaWekaImport + csvReading + iteration;

                Files.write(wekaCode.getBytes(), new File("mmlWeka.java"));
                // end of Weka generation

                //Lancement
                Process p = Runtime.getRuntime().exec("java mmlWeka.java");
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
    }

    private String mkValueInSingleQuote(String val) {
        return "'" + val + "'";
    }
}
