package org.xtext.example.mydsl.tests.group;

import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

import javax.swing.Box.Filler;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.LogisticRegression;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SVM;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;
import org.xtext.example.mydsl.tests.group.compilers.SciKitCompiler;

import com.google.common.io.Files;
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
		StringBuffer imports = new StringBuffer();

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

	private String pythonReadDataset(DataInput input) {
		String location = input.getFilelocation();
		String csvSep;
		CSVParsingConfiguration parsingConfig = input.getParsingInstruction();
		if (Objects.isNull(parsingConfig)) {
			csvSep = ",";
		} else {
			csvSep = parsingConfig.getSep().toString();
		}

		return "df = pd.read_csv(\"" + location + "\", sep=\"" + csvSep + "\")";
	}
}
