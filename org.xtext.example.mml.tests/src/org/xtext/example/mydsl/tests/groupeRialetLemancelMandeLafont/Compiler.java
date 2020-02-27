package org.xtext.example.mydsl.tests.groupeRialetLemancelMandeLafont;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	
	public static List<String> printLines(String cmd, InputStream ins, String framework, String algo) throws Exception {
    	String line = null;
    	BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    	List<String> results = new ArrayList<String>();
    	while ((line = in.readLine()) != null) {
    		System.out.println(cmd + " " + line);
    		if (line.contains("___")) {
    			if(framework == "R") {
    				line = line.substring(4);
    			}
    			line = line.replace("\"","").toLowerCase();
    			try {
    			    double d = Double.parseDouble(line.split("___")[1]);
    			    results.add(framework + "___"+algo+"___" + line);
    			} catch (NumberFormatException e) {
    			    
    			}
    		}
	    }
    	return results;
    }
	
    public static List<String> runProcess(String command, String algo, String framework) throws Exception {
	    Process pro = Runtime.getRuntime().exec(command);
	    List<String> results = printLines(command + " stdout:", pro.getInputStream(), framework,algo);
    	printLines(command + " stderr:", pro.getErrorStream(), framework,algo);
	    pro.waitFor();
	    System.out.println(command + " exitValue() " + pro.exitValue());
	    return results;
    }
    
	public String mkValueInSingleQuote(String val) {
		return "'" + val + "'";
	}

}