package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.CSVParsingConfiguration;

public class RCSVParsingConfiguration {
	
	CSVParsingConfiguration mml;

	public RCSVParsingConfiguration(CSVParsingConfiguration parsingInstruction) {
		// TODO Auto-generated constructor stub
		this.mml = parsingInstruction;
	}

	public String compile() {
		RCSVSeparator sep = new RCSVSeparator(mml.getSep());
		return sep.compile();
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.mml == null;
	}
}
