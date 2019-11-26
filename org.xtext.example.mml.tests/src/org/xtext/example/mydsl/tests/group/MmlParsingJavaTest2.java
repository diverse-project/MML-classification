package org.xtext.example.mydsl.tests.group;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.*;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest2 {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	@Test
	public void loadModel() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"foo.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "recall\n"
				+ "");
		Assertions.assertNotNull(result);
		EList<Resource.Diagnostic> errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
		Assertions.assertEquals("foo.csv", result.getInput().getFilelocation());			
		
	}		
	
	@Test
	public void compileDataInput() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"foo2.csv\" separator ;\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "recall\n"
				+ "");
		DataInput dataInput = result.getInput();
		String fileLocation = dataInput.getFilelocation();
	
		
		String pythonImport = "import pandas as pd\n"; 
		String DEFAULT_COLUMN_SEPARATOR = ","; // by default
		String csv_separator = DEFAULT_COLUMN_SEPARATOR;
		CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
		if (parsingInstruction != null) {			
			System.err.println("parsing instruction..." + parsingInstruction);
			csv_separator = parsingInstruction.getSep().toString();
		}
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep=" + mkValueInSingleQuote(csv_separator) + ")";
		String body = "";
		
		//Training 
		StratificationMethod stratification= result.getValidation().getStratification();
		if(stratification instanceof CrossValidation) {
			body+="";
		}
		else if(stratification instanceof TrainingTest) {
			int test_size = 1 - stratification.getNumber()/100;
			body += "test_size = "+test_size+"\n";
		}
		
		
		// Import Algo pyton
		MLChoiceAlgorithm framealgo = result.getAlgorithm();
		FrameworkLang framework = framealgo.getFramework();
		MLAlgorithm algo = framealgo.getAlgorithm();
		if(algo instanceof DT) {
			pythonImport+= "from sklearn.model_selection import train_test_split\n"
					+"from sklearn import tree\n"
					+"from sklearn.metrics import accuracy_score\n";
			int maxd = ((DT) algo).getMax_depth();
			
			body += "clf = ";
			body += (maxd !=0 ) ?
					"tree.DecisionTreeClassifier(0, " + maxd + ")\n":
					"tree.DecisionTreeClassifier()\n";
		}
		
		String pandasCode = pythonImport +body + csvReading;
		
		pandasCode += "\nprint (mml_data)\n"; 
		
		Files.write(pandasCode.getBytes(), new File("mml.py"));
		// end of Python generation
		
		
		/*
		 * Calling generated Python script (basic solution through systems call)
		 * we assume that "python" is in the path
		 */
		Process p = Runtime.getRuntime().exec("python mml.py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line; 
		while ((line = in.readLine()) != null) {
			System.out.println(line);
	    }

		
		
	}

	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}


}