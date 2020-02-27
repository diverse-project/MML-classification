package org.xtext.example.mydsl.tests.group;

import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;
import org.xtext.example.mydsl.tests.group.compilers.SciKitCompiler;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MMLFileParsingTest {

	@Inject
	ParseHelper<MMLModel> parser;

	@Test
	void example1Test() throws Exception {
		String filename = "example01";
		MMLModel model = parser.parse(readMMLFile(filename));

		model.getAlgorithms().forEach(algorithm -> {
			// call algorithm factory class
			algorithmFactory(algorithm, model, filename);
		});
	}

	private void algorithmFactory(MLChoiceAlgorithm choiceAlgorithm, MMLModel model, String filename) {
		FrameworkLang framework = choiceAlgorithm.getFramework();
		MLAlgorithm algorithm = choiceAlgorithm.getAlgorithm();

		switch (framework) {
		case SCIKIT:
			SciKitCompiler.compile(algorithm, model, filename);
			break;

		case R:

			break;

		case JAVA_WEKA:

			break;
		}
	}

	public StringBuffer readMMLFile(String filename) {
		try {
			InputStream in = getClass().getResourceAsStream(
					"/org/xtext/example/mydsl/tests/group/examples/" + normalizeFilename(filename) + ".mml");

			StringBuffer buf = new StringBuffer("");
			Scanner sc = new Scanner(in);
			while (sc.hasNext()) {
				buf.append(sc.next());
			}
			sc.close();

			return buf;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String normalizeFilename(String filename) {
		return filename.trim().replace(".mml", "");
	}
}