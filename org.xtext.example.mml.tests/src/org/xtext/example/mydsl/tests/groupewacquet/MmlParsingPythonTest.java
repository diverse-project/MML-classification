package org.xtext.example.mydsl.tests.groupewacquet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.ValidationMetric;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.common.io.Files;
import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingPythonTest {
	
	@Inject
	ParseHelper<MMLModel> parseHelper;

	private Map<String, MMLModel> loadModel() throws Exception {
		HashMap<String, MMLModel> map = new HashMap<>();
		for (int i = 1; i<11;i++) {
			File f = new File("src" + File.separator + MmlParsingR.class.getPackage().getName().replace(".", File.separator) + File.separator + "resources" + File.separator + "test" + i + ".mml");
			MMLModel result = parseHelper.parse(FileUtils.readFileToString(f, Charset.defaultCharset()));
			map.put(f.getName(), result);
		}
		return map;
	}	

	@Test
	public void compileDataInput() throws Exception {
		HashMap<String, MMLModel> map = (HashMap<String, MMLModel>) loadModel();
		int i = 1;
		for (String fileName : map.keySet()) {
			MMLModel result = map.get(fileName);
			System.out.println("► Début du traitement du fichier " + fileName);
			List<MLChoiceAlgorithm> algos = result.getAlgorithms();
			for(MLChoiceAlgorithm algo : algos) {
				if (algo.getFramework() == FrameworkLang.SCIKIT)
				{
					String algName = algo.getAlgorithm().getClass().getInterfaces()[0].getSimpleName();
					//imports
					String pythonImport = "import pandas as pd\r\n" + 
							"from sklearn.tree import DecisionTreeClassifier\r\n" + 
							"from sklearn.model_selection import train_test_split\r\n" + 
							"from sklearn.metrics import classification_report, confusion_matrix\r\n" + 
							"from sklearn.model_selection import GridSearchCV\r\n" + 
							"from sklearn.linear_model import LogisticRegression\r\n" + 
							"from sklearn.ensemble import RandomForestClassifier\r\n" + 
							"from sklearn.svm import LinearSVC\r\n" + 
							"from sklearn.svm import OneClassSVM\r\n" + 
							"from sklearn.svm import NuSVC\r\n" + 
							"from sklearn.metrics import accuracy_score\r\n" + 
							"from sklearn.metrics import balanced_accuracy_score\r\n" + 
							"from sklearn.metrics import f1_score\r\n" + 
							"from sklearn.metrics import precision_score\r\n" + 
							"from sklearn.metrics import recall_score\r\n";
					//fin imports
					DataInput dataInput = result.getInput();
					String fileLocation = dataInput.getFilelocation();
					//CSV SEPARATOR
					String DEFAULT_COLUMN_SEPARATOR = ","; // by default
					String csv_separator = DEFAULT_COLUMN_SEPARATOR;
					//					CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
					//					if (parsingInstruction != null) {			
					//						System.err.println("parsing instruction..." + parsingInstruction);
					//						csv_separator = parsingInstruction.getSep().toString();
					//					}
					//END CSV SEPARATOR
					//pr�paration des x et y
					String csvReading = "df = pd.read_csv("+mkValueInSingleQuote(fileLocation)+", sep="+mkValueInSingleQuote(csv_separator)+")\r\n" + 
							"features = list(df.columns[:4])\r\n" + 
							"X = df.drop('variety', axis=1)\r\n" + 
							"y = df['variety']\r\n";
					//fin pr�pa
					//Validation
					String validation = "";
					boolean isCrossValidation = false;
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
					//iteration en fonction de l'algo
					MLAlgorithm algorithm = algo.getAlgorithm();
					String clf = "";
					String spliting = "";
					if (algorithm instanceof DT)
					{
						DT dtAlgo = (DT)algorithm;
						int maxdepth = dtAlgo.getMax_depth();
						if (maxdepth == 0) maxdepth = 1;
						if(isCrossValidation)
						{
							spliting = "classifier = GridSearchCV(DecisionTreeClassifier(max_depth="+maxdepth+"), parameters, cv="+validation+")\r\n";
							//							clf = "parameters = {}\r\n" +
							//									"y_test = y;\r\n" + 
							//									 + 
							//									"classifier.fit(X=X, y=y)\r\n" + 
							//									"tree_model = classifier.best_estimator_\r\n" + 
							//									"y_pred = classifier.predict(X)";
						}
						else
						{
							spliting = "classifier = DecisionTreeClassifier(max_depth="+maxdepth+")"+
									"\r\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size="+validation+")\r\n";
							//							clf = "classifier = DecisionTreeClassifier(max_depth="+maxdepth+")"+
							//									"\r\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size="+validation+")\r\n" + 
							//									"classifier.fit(X_train, y_train)\r\n" + 
							//									"y_pred = classifier.predict(X_test)";
						}
					}
					else if (algorithm instanceof LogisticRegression)
					{
						if(isCrossValidation)
						{
							spliting = "classifier = GridSearchCV(LogisticRegression(), parameters, cv="+validation+")\r\n";
						}
						else
						{
							spliting = "classifier = LogisticRegression()"+
									"\r\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size="+validation+")\r\n";
						}
					}
					else if (algorithm instanceof RandomForest)
					{
						if(isCrossValidation)
						{
							spliting = "classifier = GridSearchCV(RandomForestClassifier(), parameters, cv="+validation+")\r\n";
						}
						else
						{
							spliting = "classifier = RandomForestClassifier()"+
									"\r\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size="+validation+")\r\n";
						}
					}
					else{ //SVM
						SVM svmAlgo = (SVM)algorithm;
						String diagtype = "LinearSVC";
						if (svmAlgo.isClassificationSpecified())
						{
							if(svmAlgo.getSvmclassification().getLiteral().equals("one-classification"))
							{
								diagtype="OneClassSVM"; //cross val impossible
							}
							else if (svmAlgo.getSvmclassification().getLiteral().equals("nu-classification"))
							{
								diagtype="NuSVC";
							}
						}
						String newdiag = diagtype+"("+(diagtype != "LinearSVC" ? (svmAlgo.getGamma() == null ? "":"gamma="+svmAlgo.getGamma()) : "") +
								(diagtype != "OneClassSVM" && diagtype != "NuSVC" ? (svmAlgo.getC() != null && svmAlgo.getGamma() != null ? "," : "") : "")+
								(diagtype != "OneClassSVM" && diagtype != "NuSVC" ? (svmAlgo.getC() == null ? "":"C="+svmAlgo.getC()+"") : "" )+
								((svmAlgo.getC() != null || svmAlgo.getGamma() != null) && svmAlgo.isKernelSpecified() ? "," : "")+
								(diagtype != "LinearSVC" ? (svmAlgo.isKernelSpecified() ? "kernel='"+svmAlgo.getKernel().getLiteral()+"'":"") : "") +
								")";
						if(isCrossValidation)
						{
							spliting = "classifier = GridSearchCV("+newdiag+", parameters, cv="+validation+")\r\n";
						}
						else
						{
							spliting = "classifier = "+newdiag+
									"\r\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size="+validation+")\r\n";
						}

					}


					if(isCrossValidation)
					{
						clf = "parameters = {}\r\n" +
								"y_test = y;\r\n" + 
								spliting+
								"classifier.fit(X=X, y=y)\r\n" + 
								"tree_model = classifier.best_estimator_\r\n" + 
								"y_pred = classifier.predict(X)\r\n";
					}
					else
					{
						clf = spliting+
								"classifier.fit(X_train, y_train)\r\n" + 
								"y_pred = classifier.predict(X_test)\r\n";
					}
					//affichage des metrics
					String metrics = "text_file = open(generated\"result" + fileName + "." + algName + ".txt\", \"w\")\r\n";
					for(ValidationMetric metric : result.getValidation().getMetric())
					{
						metrics = "text_file = open(\"src/" + MmlParsingR.class.getPackage().getName().replace(".", "/") + "/resources/results/pythonfiles/" + fileName + "." + algName + ".txt\", \"w\")\r\n";
						switch (metric) {
						case ACCURACY:
							metrics += "print(\"accuracy_score= \" + str(accuracy_score(y_test, y_pred)),flush=True)\r\n" 									;
							metrics += "text_file.write(\"accuracy_score= \" + str(accuracy_score(y_test, y_pred)))\r\n" 									;
							break;
						case BALANCED_ACCURACY:
							metrics += "print(\"balanced_accuracy_score= \" + str(balanced_accuracy_score(y_test, y_pred)),flush=True)\r\n"  									;
							metrics += "text_file.write(\"balanced_accuracy_score= \" + str(balanced_accuracy_score(y_test, y_pred)))\r\n"  									;
							break;
						case F1:
							metrics += "print(\"f1_score= \" + str(f1_score(y_test, y_pred, average=None)),flush=True)\r\n" ;
							metrics += "text_file.write(\"f1_score= \" + str(f1_score(y_test, y_pred, average=None)))\r\n" ;
							break;
						case MACRO_F1:
							metrics += "print(\"f1_score= \" + str(f1_score(y_test, y_pred, average='macro')),flush=True)\r\n" ;
							metrics += "text_file.write(\"f1_score= \" + str(f1_score(y_test, y_pred, average='macro')))\r\n" ;
							break;
						case MACRO_ACCURACY:
							metrics += "print(\"accuracy_score_macro= \" + str(accuracy_score(y_test, y_pred)),flush=True)\r\n"  ;
							metrics += "text_file.write(\"accuracy_score_macro= \" + str(accuracy_score(y_test, y_pred)))\r\n"  ;
							break;
						case MACRO_PRECISION:
							metrics += "print(\"precision_score_macro= \" + str(precision_score(y_test, y_pred, average='macro')),flush=True)\r\n" ;
							metrics += "text_file.write(\"precision_score_macro= \" + str(precision_score(y_test, y_pred, average='macro')))\r\n" ;
							break;
						case MACRO_RECALL:
							metrics += "print(\"recall_score_macro= \" + str(recall_score(y_test, y_pred, average='macro')),flush=True)\r\n" ;
							metrics += "text_file.write(\"recall_score_macro= \" + str(recall_score(y_test, y_pred, average='macro')))\r\n" ;
							break;
						case PRECISION:
							metrics += "print(\"precision_score= \" + str(precision_score(y_test, y_pred, average=None)),flush=True)\r\n" ;
							metrics += "text_file.write(\"precision_score= \" + str(precision_score(y_test, y_pred, average=None)))\r\n" ;
							break;
						case RECALL:
							metrics += "print(\"recall_score= \" + str(recall_score(y_test, y_pred, average=None)),flush=True)\r\n" ;							
							metrics += "text_file.write(\"recall_score= \" + str(recall_score(y_test, y_pred, average=None)))\r\n" ;							
							break;
						default:
							metrics = "print(classification_report(y_test, y_pred),flush=True)\r\n";
							metrics = "text_file.write(classification_report(y_test, y_pred))\r\n";
							break;
						}
						metrics += "text_file.close()\r\n";
					}
					String code = pythonImport + csvReading + clf + metrics;

					//					code += "\nprint (mml_data)\n"; 

					Files.write(code.getBytes(), new File("src" + File.separator + MmlParsingPythonTest.class.getPackage().getName().replace(".", File.separator) + File.separator + "resources" + File.separator + "results" + File.separator + fileName + "." + algName + ".py"));
					// end of Python generation


					/*
					 * Calling generated Python script (basic solution through systems call)
					 * we assume that "python" is in the path
					 */
					Process p = Runtime.getRuntime().exec("python src" + File.separator + MmlParsingPythonTest.class.getPackage().getName().replace(".", File.separator) + File.separator + "resources" + File.separator + "results" + File.separator + fileName + "." + algName + ".py");
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line; 
					// obligatoire pour laiser le temps au processus de se finir
					line = in.readLine(); //on ne lit qu'une ligne pour �viter le blocage du code
					//										while ((line = in.readLine()) != null) {
					//											System.out.println(line);
					//										}
					System.out.println("Les résultats se trouvent dans le dossier results");
				}
			}
			System.out.println("⬛ Fin du traitement fichier " + fileName);
			i++;
		}
	}

	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}


}