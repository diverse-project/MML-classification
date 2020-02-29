package org.xtext.example.mydsl.tests.groupWolouAnoh;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;
import org.xtext.example.mydsl.tests.groupWolouAnoh.compilers.SciKitCompiler;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MMLFileParsingTest {

	@Inject
	ParseHelper<MMLModel> parser;
	
	static String resultFile = "runtime_data.csv";
	static FileWriter  fw;
	
	@BeforeAll
	public static void openReportFile() throws IOException {
		fw = new FileWriter(resultFile, true);
	}
	
	@AfterAll
	public static void closeFile() throws IOException {
		fw.close();
	}

	@Test
	void example1Test() throws Exception {
		parseFile("example01");
	}

	@Test
	void example2Test() throws Exception {
		parseFile("example02");
	}

	@Test
	void example3Test() throws Exception {
		parseFile("example03");
	}

	@Test
	void example4Test() throws Exception {
		parseFile("example04");
	}

	@Test
	void example5Test() throws Exception {
		parseFile("example05");
	}

	@Test
	void example6Test() throws Exception {
		parseFile("example06");
	}

	@Test
	void example7Test() throws Exception {
		parseFile("example07");
	}

	@Test
	void example8Test() throws Exception {
		parseFile("example08");
	}

	@Test
	void example9Test() throws Exception {
		parseFile("example09");
	}

	@Test
	void example10Test() throws Exception {
		parseFile("example10");
	}
	
	private void parseFile(String filename) throws Exception {
		MMLModel model = parser.parse(readMMLFile(filename));

		Objects.requireNonNull(model.getAlgorithms(), "MLChoiceAlgorithm is not provided.");

		for (MLChoiceAlgorithm algorithm : model.getAlgorithms()) {
			algorithmFactory(algorithm, model, filename);

			String file = filename.concat("_").concat(algorithm.getFramework().getLiteral()).concat("_")
					.concat(algorithm.getAlgorithm().getClass().getSimpleName()).concat(".py");
			assertTrue(new File(file).exists());

			runScript(file, model, algorithm.getAlgorithm(), algorithm.getFramework());
		}
	}

	private void runScript(String file, MMLModel model, MLAlgorithm algo, FrameworkLang frame) throws IOException {
		Process p = Runtime.getRuntime().exec("python " + file);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String dataset = model.getInput().getFilelocation();
		String algorithm = algo.getClass().getSimpleName().replace("Impl", "");
		String line;
		while ((line = in.readLine()) != null) {
			fw.write(String.format("%s,%s,%s,%s\n", dataset, algorithm, frame.getLiteral(), line));
		}
	}

	private void algorithmFactory(MLChoiceAlgorithm choiceAlgorithm, MMLModel model, String filename) {
		FrameworkLang framework = choiceAlgorithm.getFramework();
		MLAlgorithm algorithm = choiceAlgorithm.getAlgorithm();

		switch (framework) {
		case SCIKIT:
			SciKitCompiler.compile(framework, algorithm, model, filename);
			break;

		case R:

			break;

		case JAVA_WEKA:

			break;
		}
	}

	public String readMMLFile(String filename) {
		try {
			InputStream in = getClass().getResourceAsStream(
					"/org/xtext/example/mydsl/tests/groupWolouAnoh/examples/" + normalizeFilename(filename) + ".mml");

			StringBuffer buf = new StringBuffer();
			Scanner sc = new Scanner(in);
			while (sc.hasNext()) {
				buf.append(sc.nextLine());
				buf.append("\n");
			}
			sc.close();

			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String normalizeFilename(String filename) {
		return filename.trim().replace(".mml", "");
	}
}