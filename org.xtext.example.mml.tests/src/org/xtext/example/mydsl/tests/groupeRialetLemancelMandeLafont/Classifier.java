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

public class Classifier {
	
	private static String fileName = "output_LAFONT_LEMANCEL_MANDE_RIALET\\scores.txt";
	
	public static void reset() throws IOException {
		File f = new File(fileName);
		f.createNewFile();
		new PrintWriter(fileName).close();
	}
	
	public static void addScores(List<String> results) throws IOException {
		for (String score : results) {
			Writer w = new BufferedWriter(new FileWriter(fileName, true));
			w.append(score + "\n");
			w.close();
		}
	}
	
	public static void calculateScore() {
		List<String[]> tmp = new ArrayList<String[]>();
		String[][] scores = new String[1][4];
		String[] score; 
		try {  
			File file = new File(fileName);    
			FileReader fr = new FileReader(file);   
			BufferedReader br = new BufferedReader(fr);  
			String line;  
			while ((line=br.readLine()) != null) {  
				score = new String[4];
				score[0] = line.split("___")[0];
				score[1] = line.split("___")[1];
				score[2] = line.split("___")[2];
				score[3] = line.split("___")[3];
				tmp.add(score);
			}  
			fr.close();
			scores = (String[][]) tmp.toArray(scores);
			java.util.Arrays.sort(scores, new java.util.Comparator<String[]>() {
				public int compare(String[] a, String[] b) {
					return Double.compare(Double.parseDouble(b[2]), Double.parseDouble(a[2]));
				}
			});
			if(scores.length > 0) {				
				printResultsByScores(scores);
			}
			java.util.Arrays.sort(scores, new java.util.Comparator<String[]>() {
				public int compare(String[] a, String[] b) {
					return Double.compare(Double.parseDouble(a[3]), Double.parseDouble(b[3]));
				}
			});
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
		System.out.println("|---|---------------|-------------------------|--------------------|------|");
		for(String[] s1 : scores) {
			System.out.println("|"+configureString(""+i++,3)+"|"+configureString(s1[0],15)+"|"+configureString(s1[1],25)+"|"+configureString(s1[2],20)+"|"+configureString(s1[3],6)+"|");
			System.out.println("|---|---------------|-------------------------|--------------------|------|");
		}
	}
	
	public static void printResultsByElapsedTime(String[][] scores) {
		int i = 0;
		System.out.println("CLASSEMENT PAR TEMPS ECOULE (millisecondes)----------");
		System.out.println("|---|---------------|-------------------------|--------------------|------|");
		for(String[] s1 : scores) {
			System.out.println("|"+configureString(""+i++,3)+"|"+configureString(s1[0],15)+"|"+configureString(s1[1],25)+"|"+configureString(s1[2],20)+"|"+configureString(s1[3],6)+"|");
			System.out.println("|---|---------------|-------------------------|--------------------|------|");
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
	
}