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
		String[][] scores = new String[1][3];
		String[] score; 
		try {  
			File file = new File(fileName);    
			FileReader fr = new FileReader(file);   
			BufferedReader br = new BufferedReader(fr);  
			String line;  
			while ((line=br.readLine()) != null) {  
				score = new String[3];
				score[0] = line.split("___")[0];
				score[1] = line.split("___")[1];
				score[2] = line.split("___")[2];
				tmp.add(score);
			}  
			fr.close();
			scores = (String[][]) tmp.toArray(scores);
			java.util.Arrays.sort(scores, new java.util.Comparator<String[]>() {
				public int compare(String[] a, String[] b) {
					return Double.compare(Double.parseDouble(a[2]), Double.parseDouble(b[2]));
				}
			});
			for(String[] s1 : scores) {
				System.out.println(s1[0]+"  "+s1[1]+"  "+s1[2]);
			}
		} catch(IOException e) {  
			e.printStackTrace();  
		}  
	}
	
}