package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.DataInput;

public class RDataImput {

	DataInput mml;
	
	public RDataImput(DataInput mml) {
		// TODO Auto-generated constructor stub
		this.mml = mml;
	}
	
	public String compile() {
		String filelocation = mml.getFilelocation();
		RCSVParsingConfiguration parsingInstruction = new RCSVParsingConfiguration(mml.getParsingInstruction());
		
		String result = "";
		result += filelocation;
		if (!parsingInstruction.isEmpty())
			result += parsingInstruction.compile();
		
		return result;
	}
}
