package org.xtext.example.mydsl.tests.groupeRivasSylla.compilers;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;
import org.xtext.example.mydsl.tests.groupeRivasSylla.compilers.classes.RMMLModel;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class EreCompiler {
	
	@Inject
	ParseHelper<MMLModel> parseHelper = new ParseHelper<MMLModel>();

	public MMLModel getMMLModel() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"foo2.csv\" separator ;\n" + "mlframework scikit-learn\n"
				+ "algorithm DT\n" + "TrainingTest { percentageTraining 70 }\n" + "recall\n" + "");
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EreCompiler compiler = new EreCompiler();
		MMLModel mml = compiler.getMMLModel();
		
		RMMLModel model = new RMMLModel(mml);
		
		String r = model.compile();
		System.out.println(r);
	}

}
