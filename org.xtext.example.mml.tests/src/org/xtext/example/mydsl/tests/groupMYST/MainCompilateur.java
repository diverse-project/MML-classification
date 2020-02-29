package org.xtext.example.mydsl.tests.groupMYST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.XFormula;
import org.xtext.example.mydsl.mml.impl.FormulaItemImpl;

public class MainCompilateur {
	
	private final String DEFAULT_COLUMN_SEPARATOR = ","; // by default
	
	private MMLModel mmlModel;
	private DataInput dataInput;
	private CSVParsingConfiguration parsingInstruction ;
	private List<MLChoiceAlgorithm> AlgosList ;
	private Validation validation;
	private FormulaItem predictive;
	private XFormula predictor;
	private Compilateur compilateur;
	private File fileResultat;
	private String pathFile; 
	
	public MainCompilateur(MMLModel mmlModel, String PathFile) throws IOException {
		this.mmlModel = mmlModel;
		
		//Récupère les informations globales
		if (mmlModel != null) {
		this.dataInput = mmlModel.getInput();
		this.parsingInstruction = dataInput.getParsingInstruction();
		this.AlgosList = mmlModel.getAlgorithms();
		this.validation = mmlModel.getValidation();
		if( mmlModel.getFormula() != null) {
		if( mmlModel.getFormula().getPredictive() != null) {
			this.predictive = mmlModel.getFormula().getPredictive();
		}else {
		
			this.predictive = null;
		}
		if(mmlModel.getFormula().getPredictors() != null) {
				this.predictor = mmlModel.getFormula().getPredictors() ;
		}else {
			
			this.predictive = null;
		}
		}
		
		this.pathFile = pathFile;
		}
		
	}
	
	public void run() {
		for (MLChoiceAlgorithm alogs : AlgosList) 
		{ 
		
		switch(alogs.getFramework().getLiteral()) {
		  case "R":
		    compilateur = new RCompilateur();
		    break;
		  case "Weka":
		     compilateur = new WekaCompilateur();
		    break;
		  case "xgboost":
			  compilateur = new XgboostCompilateur();		 
			    break;
		  case "scikit-learn":
			  compilateur = new ScikitLearnCompilateur();		 
			    break;
		}
		compilateur.configure(alogs.getAlgorithm(),dataInput,validation, getCSVSeparator(),  predictive, predictor,pathFile );
		try {
			compilateur.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	

	private String getCSVSeparator() {
		if(parsingInstruction.getSep() == null) {
			return parsingInstruction.getSep().getName();
		}
		return DEFAULT_COLUMN_SEPARATOR;
	}
	

	
}
