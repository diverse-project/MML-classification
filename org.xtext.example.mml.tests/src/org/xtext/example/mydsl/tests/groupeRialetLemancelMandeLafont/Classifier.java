package org.xtext.example.mydsl.tests.groupeRialetLemancelMandeLafont;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Classifier {

	private static String fileNameScores = "output_LAFONT_LEMANCEL_MANDE_RIALET\\scores.txt";
	private static String fileNameCsvByScores = "output_LAFONT_LEMANCEL_MANDE_RIALET\\scores.csv";
	private static String fileNameCsvByTime = "output_LAFONT_LEMANCEL_MANDE_RIALET\\times.csv";
	
	public static void reset() throws IOException {
		File f = new File(fileNameScores);
		f.createNewFile();
		new PrintWriter(fileNameScores).close();
	}
	
	public static void addScores(List<String> results) throws IOException {
		for (String score : results) {
			Writer w = new BufferedWriter(new FileWriter(fileNameScores, true));
			w.append(score + "\n");
			w.close();
		}
	}
	
	public static void calculateScore() {
		List<String[]> tmp = new ArrayList<String[]>(), resultsCsvByScore= new ArrayList<String[]>(), resultsCsvByTime= new ArrayList<String[]>();
		String[][] scores = new String[1][5];
		String[] score; 
		try {  
			File file = new File(fileNameScores);    
			FileReader fr = new FileReader(file);   
			BufferedReader br = new BufferedReader(fr);  
			String line;  
			while ((line=br.readLine()) != null) {  
				score = new String[5];
				score[0] = line.split("___")[0];
				score[1] = line.split("___")[1];
				score[2] = line.split("___")[2];
				score[3] = line.split("___")[3];
				score[4] = line.split("___")[4];
				tmp.add(score);
			}  
			fr.close();
			scores = (String[][]) tmp.toArray(scores);
			java.util.Arrays.sort(scores, new java.util.Comparator<String[]>() {
				public int compare(String[] a, String[] b) {
					return Double.compare(Double.parseDouble(b[3]), Double.parseDouble(a[3]));
				}
			});
			for(String[] s : scores) {
				resultsCsvByScore.add(s);
			}
			convertArrayToCsv(resultsCsvByScore,fileNameCsvByScores);
			if(scores.length > 0) {				
				printResultsByScores(scores);
			}
			java.util.Arrays.sort(scores, new java.util.Comparator<String[]>() {
				public int compare(String[] a, String[] b) {
					return Double.compare(Double.parseDouble(a[4]), Double.parseDouble(b[4]));
				}
			});

			for(String[] s : scores) {
				resultsCsvByTime.add(s);
			}
			convertArrayToCsv(resultsCsvByTime,fileNameCsvByTime);
			if(scores.length > 0) {				
				printResultsByElapsedTime(scores);
			}
		} catch(IOException e) {  
			e.printStackTrace();  
		}  
	}
	
	public static void printResultsByScores(String[][] scores) {
		int i = 0;
		System.out.println("CLASSEMENT PAR SCORE----------");
		System.out.println("|---|---------------|--------------------|-------------------------|--------------------|------|");
		for(String[] s1 : scores) {
			System.out.println("|"+configureString(""+i++,3)+"|"+configureString(s1[0],15)+"|"+configureString(s1[1],20)+"|"+configureString(s1[2],25)+"|"+configureString(s1[3],20)+"|"+configureString(s1[4],6)+"|");
			System.out.println("|---|---------------|--------------------|-------------------------|--------------------|------|");
		}
	}
	
	public static void printResultsByElapsedTime(String[][] scores) {
		int i = 0;
		System.out.println("CLASSEMENT PAR TEMPS ECOULE (millisecondes)----------");
		System.out.println("|---|---------------|--------------------|-------------------------|--------------------|------|");
		for(String[] s1 : scores) {
			System.out.println("|"+configureString(""+i++,3)+"|"+configureString(s1[0],15)+"|"+configureString(s1[1],20)+"|"+configureString(s1[2],25)+"|"+configureString(s1[3],20)+"|"+configureString(s1[4],6)+"|");
			System.out.println("|---|---------------|--------------------|-------------------------|--------------------|------|");
		}
	}
	
	public static String configureString(String str, int len) {
		int diff = 0;
		if(str.length() < len) {
			diff = len-str.length();
			for(int i = 0; i < diff; i++) {
				str+=" ";
			}
		}
		return str;
	}
	
	public static String convertToCSV(String[] data) {
	    return Stream.of(data)
	      .map(Classifier::escapeSpecialCharacters)
	      .collect(Collectors.joining(","));
	}
	
	public static String escapeSpecialCharacters(String data) {
	    String escapedData = data.replaceAll("\\R", " ");
	    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}
	
	public static void convertArrayToCsv(List<String[]> results, String fileName) throws IOException {
	    File csvOutputFile = new File(fileName);
	    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
	    	csvOutputFile.createNewFile();
	        results.stream()
	          .map(Classifier::convertToCSV)
	          .forEach(pw::println);
	    }
	}
	
}