package org.xtext.example.mydsl.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;

import com.google.common.io.Files;
import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingPythonTest {

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
			if (algo.getFramework() == FrameworkLang.SCIKIT)
			{
				DataInput dataInput = result.getInput();
				String fileLocation = dataInput.getFilelocation();
				String pythonImport = "import pandas as pd\r\n" + 
						"from sklearn.model_selection import train_test_split\r\n" + 
						"from sklearn import tree\r\n" + 
						"from sklearn.metrics import accuracy_sco\r\n"; 
				String DEFAULT_COLUMN_SEPARATOR = ","; // by default
				String csv_separator = DEFAULT_COLUMN_SEPARATOR;
				CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
				if (parsingInstruction != null) {			
					System.err.println("parsing instruction..." + parsingInstruction);
					csv_separator = parsingInstruction.getSep().toString();
				}
				String csvReading = "df = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep=" + mkValueInSingleQuote(csv_separator) + ")";						
				//			result.getFormula()
				//			String spliting = "X = df.drop(columns=[\"variety\"])";
				String pandasCode = pythonImport + csvReading;

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
		}
	}

	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}


}