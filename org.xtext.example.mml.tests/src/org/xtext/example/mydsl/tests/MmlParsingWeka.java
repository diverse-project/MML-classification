package org.xtext.example.mydsl.tests;

import org.xtext.example.mydsl.mml.*;
import org.xtext.example.mydsl.mml.DataInput;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MmlParsingWeka {

    @Inject
    ParseHelper<MMLModel> parseHelper;

    public List<MMLModel> loadModel() throws Exception {
        ArrayList<MMLModel> list = new ArrayList<MMLModel>();
        for (int i = 1; i<11;i++) {
            MMLModel result = parseHelper.parse(
                    FileUtils.readFileToString(
                            new File(
                                    "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test"+i+".mml")
                            , Charset.defaultCharset()));
            list.add(result);
        }
        return list;
    }

    @Test
    public void compileDataInput() throws Exception {
        ArrayList<MMLModel> list = (ArrayList<MMLModel>) loadModel();
        for (MMLModel result : list)
        {
            MLChoiceAlgorithm algo = result.getAlgorithm();
            System.out.println();
            if (algo.getFramework() == FrameworkLang.JAVA_WEKA)
            {
                DataInput dataInput = result.getInput();
                String fileLocation = dataInput.getFilelocation();
                String javaWekaImport = "import java.io.*;\r\n" +
                        "import java.util.*;\r\n" +
                        "import weka.core.*;\r\n" +
                        "import weka.classifiers.*;\r\n";

                String DEFAULT_COLUMN_SEPARATOR = ","; // by default
                String csv_separator = DEFAULT_COLUMN_SEPARATOR;
                CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
                if (parsingInstruction != null) {
                    System.err.println("parsing instruction..." + parsingInstruction);
                    csv_separator = parsingInstruction.getSep().toString(); //TODO Que faire de ça ?
                }
                String csvReading = "CSVLoader loader = new CSVLoader();\r\n" +
                        "loader.setSource(" + mkValueInSingleQuote(fileLocation) + ");\r\n" +
                        "Instances data = loader.getDataSet();\r\n";

                String wekaCode = javaWekaImport + csvReading;

                Files.write(wekaCode.getBytes(), new File("mmlWeka.java"));
                // end of Weka generation


                /*
                 * Calling generated Python script (basic solution through systems call)
                 * we assume that "python" is in the path
                 */
                Process p = Runtime.getRuntime().exec("java mmlWeka.java");
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
    }

    private String mkValueInSingleQuote(String val) {
        return "'" + val + "'";
    }
}
