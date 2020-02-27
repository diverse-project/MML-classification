package org.xtext.example.mydsl.tests.groupeRialetLemancelMandeLafont;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.RFormula;

public class Compiler {
	
	RFormula f;
	String x;
	String y;
	String formula;
	String csvReading;
	String algorithm;
	String imports;
	String stratification;
	String metriques;
	String stratificationAndMetrique;
	String program;
	String prediction;
	String printScores;
	
	
	public void init() {
		f = null;
		x = "";
		y = "";
		formula = "";
		csvReading = "";
		algorithm = "";
		imports = "";
		stratification = "";
		metriques = "";
		stratificationAndMetrique = "";
		program = "";
		prediction = "";
		printScores = "";
	}
	
	public String getParsingInstruction(DataInput dataInput) {
		String csv_separator = ",";
		CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
		
		if (parsingInstruction != null) {			
			csv_separator = parsingInstruction.getSep().toString();
		}
		
		return csv_separator;
	}
	
	public String addInstruction(String existant, String add) {
		if (existant == null) {
			return add + "\n";
		}
		else {
			return existant + add + "\n";
		}
	}

	public void formulaTreatment(MMLModel model) {
		f = model.getFormula();
		x = "";
		y = "";
		formula = "";
	}
	
	public static List<String> printLines(String cmd, InputStream ins, List<String> results, String framework) throws Exception {
    	String line = null;
    	BufferedReader in = new BufferedReader(
	    new InputStreamReader(ins));
    	while ((line = in.readLine()) != null) {
    		System.out.println(cmd + " " + line);
    		if (line.contains("___")) {
    			results.add(framework + "___" + line);
    		}
	    }
    	return results;
    }
	
    public static List<String> runProcess(String command, List<String> results, String framework) throws Exception {
	    Process pro = Runtime.getRuntime().exec(command);
	    printLines(command + " stdout:", pro.getInputStream(), results, framework);
    	//printLines(command + " stderr:", pro.getErrorStream());
	    pro.waitFor();
	    System.out.println(command + " exitValue() " + pro.exitValue());
	    return results;
    }
    
	public String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}

}