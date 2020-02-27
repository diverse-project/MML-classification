package org.xtext.example.mydsl.tests;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.*;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaCompiler {
	
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
	public void compileDataInput() throws Exception {
		MMLModel model = parseHelper.parse("datainput \"iris.csv\"\n"
				+ "mlframework R\n"
				+ "algorithm RF\n"
				+ "CrossValidation { numRepetitionCross 10 }\n"
				+ "balanced_accuracy recall precision F1 accuracy macro_recall macro_precision macro_F1 macro_accuracy\n" 
				+ "");	
	
		List<String[]> values = new ArrayList<String[]>();
		
		//Algorithm
		MLChoiceAlgorithm[] algos = (MLChoiceAlgorithm[]) model.getAlgorithms().toArray();
		
		for(int i = 0; i < algos.length; i++) {
			MLAlgorithm al = (MLAlgorithm) algos[i].getAlgorithm();
			FrameworkLang framework = algos[i].getFramework();
			if(framework.getLiteral() == "scikit-learn") {
				MmlParsingJavaCompilerPython compiler = new MmlParsingJavaCompilerPython();
				compiler.compileDataInput(model,al,i+1);
			}else if(framework.getLiteral() == "R") {
				MmlParsingJavaCompilerR compiler = new MmlParsingJavaCompilerR();
				compiler.compileDataInput(model,al,i+1);
			}else if(framework.getLiteral() == "Weka") {
				MmlParsingJavaCompilerJava compiler = new MmlParsingJavaCompilerJava();
				compiler.compileDataInput(model,al,i+1);
			}
		}		
	}
	
}