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
		return "" + mml.getMax_depth();
	}

}
