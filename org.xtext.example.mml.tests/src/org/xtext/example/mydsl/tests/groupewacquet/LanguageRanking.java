package org.xtext.example.mydsl.tests.groupewacquet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class LanguageRanking {

	@Inject
	static ParseHelper<MMLModel> parseHelper;
	
	public static void main(String[] args) throws IOException {
		StringBuilder resultsFile = new StringBuilder(); 
		resultsFile.append("Data;Algorithme;Framework;Temps d'exécution (secondes);Accuracy;Recall;F1").append(System.lineSeparator());
		// Récupération du répertoire contenant les scripts et parcours des scripts existants
		File scriptsFolder = new File("src" + File.separator + MmlParsingR.class.getPackage().getName().replace(".", File.separator) + File.separator + "resources" + File.separator + "results");
		for (File f : scriptsFolder.listFiles()) {
			if (f.isFile()) {
				System.out.println("Traitement du fichier '" + f.getName() + "'");
				// Récupération du nom du jeu de données
				String dataName = "";
				String algorithm = f.getName().split("\\.")[2];
				try {
					File mmlFile = new File(f.getParentFile().getParent() + File.separator + f.getName().substring(0, f.getName().indexOf(".")) + ".mml");
					String fileContent = FileUtils.readFileToString(mmlFile, Charset.defaultCharset());
					Pattern pattern = Pattern.compile("\"(.*?)\"");
					Matcher matcher = pattern.matcher(fileContent);
					if (matcher.find()) {
						String dataPath = matcher.group(1);
						dataName = (dataPath.split("/").length > 1 ? dataPath.split("/")[dataPath.split("/").length - 1] : dataPath);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Récupération du langage
				String extension = f.getName().split("\\.")[f.getName().split("\\.").length - 1].toLowerCase();
				// Attribution de la commande à exécuter
				String execCommands = "";
				if (extension.equals("r")) {
					execCommands = "Rscript.exe --slave " + f.getAbsolutePath();
				} else if (extension.equals("py")) {
					execCommands = "python " + f.getAbsolutePath();
				} else if (extension.equals("java")) {
					// TODO : implémenter runner java
				}
				// Récupération du temps d'exécution
				long beginning = System.currentTimeMillis();
				Process proc = Runtime.getRuntime().exec(execCommands);
				try {
					proc.waitFor();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				long end = System.currentTimeMillis();
				double execTime = (double) (end - beginning) / 1000;
				// Gestion du répertoire contenant les fichiers de résultat
				String resultFolderName = "";
				if (extension.equals("r")) {
					resultFolderName = "rfiles";
				} else if (extension.equals("py")) {
					resultFolderName = "pythonfiles";
				} else if (extension.equals("java")) {
					resultFolderName = "javafiles";
				}
				Map<String, String> mesureMap = new HashMap<>();
				mesureMap.put("Accuracy", "");
				mesureMap.put("Recall", "");
				mesureMap.put("F1", "");
				try {
					File resultFile = new File(scriptsFolder.getAbsolutePath() + File.separator + resultFolderName + File.separator + f.getName().substring(0, f.getName().length() - extension.length()) + "txt");
					if (resultFile.exists()) {
						BufferedReader bf = new BufferedReader(new FileReader(resultFile));
						String resultLine = bf.readLine();
						bf.close();
						String mesure = resultLine.split("=")[0].trim();
						String result = resultLine.split("=")[1].trim();
						mesure = reformatName(mesure);
						if (mesure.length() > 0) {
							mesureMap.put(mesure, result);
						}
						String framework = getFramework(extension);
						resultsFile.append(dataName + ";" + algorithm + ";" + framework + ";" + execTime + ";" + mesureMap.get("Accuracy") + ";" + mesureMap.get("Recall") + ";" + mesureMap.get("F1"));
						resultsFile.append(System.lineSeparator());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		Path csvFile = Paths.get("src" + File.separator + LanguageRanking.class.getPackage().getName().replace(".", File.separator) + File.separator + "resources" + File.separator + "classement.csv");
		Files.write(csvFile, resultsFile.toString().getBytes());
	}
	
	private static String reformatName(String mesure) {
		switch (mesure) {
			case "accuracy_score":
			case "ACCURACY":
				return "Accuracy";
			case "f1_score":
				return "F1";
			case "recall_score":
			case "RECALL":
				return "Recall";
			case "precision_score":
			case "PRECISION":
				return "Precision";
			default:
				return "";
		}
	}
	
	private static String getFramework(String extension) {
		if (extension.equals("r")) {
			return "R";
		} else if (extension.equals("py")) {
			return "scikit-learn";
		} else {
			return "Weka (Java)";
		}
	}
	
}
