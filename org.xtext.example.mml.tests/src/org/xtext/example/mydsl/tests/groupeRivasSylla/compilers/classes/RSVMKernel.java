package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes;

import org.xtext.example.mydsl.mml.SVMKernel;

public class RSVMKernel {
	
	SVMKernel kernel;
	public RSVMKernel(SVMKernel kernel) {
		// TODO Auto-generated constructor stub
		this.kernel = kernel;
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.kernel == null;
	}

	public String compile() {
		// TODO Auto-generated method stub
		String result = "";
		switch (kernel.toString()) {
			case "linear" : break;
			case "polynomial" : break;
			case "radial" : break;
		};
		return result;
	}

}