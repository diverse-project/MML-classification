package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.DT;

public class RDT {

	DT mml;

	public RDT(DT mml) {
		// TODO Auto-generated constructor stub
		this.mml = mml;
	}

	public String compile() {
		// TODO Auto-generated method stub
		String result = "";
		if (this.mml.getMax_depth() != 0)
			result += mml.getMax_depth();
		return result;
	}

}
