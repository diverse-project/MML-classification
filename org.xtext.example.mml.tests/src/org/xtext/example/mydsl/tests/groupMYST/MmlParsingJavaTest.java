
package org.xtext.example.mydsl.tests.groupMYST;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.inject.Inject;

import junit.framework.Assert;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	public ArrayList<MMLModel> loadModel() throws Exception {
		ArrayList<MMLModel> listMmlFiles = new ArrayList<MMLModel>();
		for(int i = 1; i<11; i++) {
			MMLModel result = parseHelper.parse(FileUtils.readFileToString(new File("src" + File.separator + "org" + 
					File.separator + "xtext" + File.separator + "example" + File.separator + "mydsl" + File.separator + "tests" + 
					File.separator + "groupMYST" + File.separator + "mml" + i + ".mml")));//, Charset.defaultCharset()
			Assertions.assertNotNull(result);
			EList<Resource.Diagnostic> errors = result.eResource().getErrors();
			//Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");
			//Assertions.assertEquals("foo2.csv", result.getInput().getFilelocation());
			listMmlFiles.add(result);
		}
		return listMmlFiles;
	}		
	
	@Test
	public void compileDataInput() throws Exception {
		ArrayList<MMLModel> listMmlFiles = (ArrayList<MMLModel>) loadModel();
		int indexModel = 1;
	
		for (MMLModel result : listMmlFiles){

			/*
			 * Calling generated Python script (basic solution through systems call)
			 * we assume that "python" is in the path
			 */
			try {
				Process p = Runtime.getRuntime().exec("python mml"+indexModel+".py");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line; 
				int index = 0;
				while ((line = in.readLine()) != null) {
					System.out.println(line);
					assertEquals(false, line.isEmpty());
					switch (index) {
					case 0:
						assertEquals("    colonne 1     colonne 2", line);
						break;
					case 1:
						assertEquals("0  cellule 1 1  cellule 1 2", line);
						break;
					case 2:
						assertEquals("1  cellule 2 1  cellule 2 2", line);
						break;
					default:
						Assert.fail("Error while reading mml"+indexModel+" results");
						break;
					}
					index++;
				}
			}
			catch(IOException e){
				Assert.fail("Error during mml"+indexModel+" execution");
			}
		}
		indexModel++;
		
	}

	
	@Test
	public void createFileScikitLearn() throws IOException, Exception {
	MMLModel result = parseHelper.parse(FileUtils.readFileToString(new File("src" + File.separator + "org" + 
				File.separator + "xtext" + File.separator + "example" + File.separator + "mydsl" + File.separator + "tests" + 
				File.separator + "groupMYST" + File.separator + "recallScikitLearn.mml"), Charset.defaultCharset()));
	
		MainCompilateur compile = new MainCompilateur(result, "tst.csv");
	compile.run();
	}
	
	
	
	/*
	 * @Test public void createFileWeka() throws IOException, Exception { MMLModel
	 * result = parseHelper.parse(FileUtils.readFileToString(new File("src" +
	 * File.separator + "org" + File.separator + "xtext" + File.separator +
	 * "example" + File.separator + "mydsl" + File.separator + "tests" +
	 * File.separator + "groupMYST" + File.separator + "mml9-Weka.mml"),
	 * Charset.defaultCharset()));
	 * 
	 * MainCompilateur compile = new MainCompilateur(result, "tst.csv");
	 * compile.run();
	 * 
	 * }
	 */
	 
	 
	
	/*
	 * @Test public void createFileR() throws IOException, Exception { MMLModel
	 * result = parseHelper.parse(FileUtils.readFileToString(new File("src" +
	 * File.separator + "org" + File.separator + "xtext" + File.separator +
	 * "example" + File.separator + "mydsl" + File.separator + "tests" +
	 * File.separator + "groupMYST" + File.separator + "mml" + 2 + "-R.mml"),
	 * Charset.defaultCharset()));
	 * 
	 * MainCompilateur compile = new MainCompilateur(result, "bigfoo.csv");
	 * compile.run(); }
	 */
	
	private String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}

	}