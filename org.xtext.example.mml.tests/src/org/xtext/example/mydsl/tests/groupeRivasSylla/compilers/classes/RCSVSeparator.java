package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.CSVSeparator;

public class RCSVSeparator {

	CSVSeparator mml;
	
	public RCSVSeparator(CSVSeparator sep) {
		// TODO Auto-generated constructor stub
		this.mml = sep;
	}
	
	public String compile() {
		switch (mml.toString()) {
			case "," : break;
			case ";" : break;
		};
		return "";
	}

}
