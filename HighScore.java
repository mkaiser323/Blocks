
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class HighScore implements Comparable, Serializable{
	static String fileName = "high.score";
	String name;
	int score;
	
	//CONSTRUCTOR
	public HighScore(String name, int score){
		this.name = name;
		this.score = score;
	}

	//COMPARE_TO METHOD
	public int compareTo(Object o) {
		HighScore newScore = (HighScore)o;
		if (this.score > newScore.score){
			return -1;
		}
		if (this.score < newScore.score){
			return 1;
		}
		return 0;
	}
	
	//method that checks the new score against the five highest scores, returns true or false; 
	//the file will have a -1 at the last entry, and that's how this method will know to stop reading in new values
	public static boolean isHighScore(int score){
		//read in the first five scores
		try{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
			//fill up the array list with the top 5 scores
			ArrayList<HighScore> scoreList = new ArrayList<>();
			int nextScore, s = 0;
			String name;
			boolean endOfFile = false;
			boolean afterFirst = false;
			while(!endOfFile && s < 5 ){
				nextScore = inputStream.readInt();
				if(nextScore == -1){
					endOfFile = true;
					name = "";
				} else {
					name = inputStream.readUTF().trim();	
				}
				if(!afterFirst || nextScore != -1){
					scoreList.add(new HighScore(name, nextScore));
				}
				afterFirst = true;
				s++;
			}
			inputStream.close();
			//check new score against the current top five. return true when new score is greater than one of them:
			for(int i = 0; i < scoreList.size(); i++){
				if(score > scoreList.get(i).score){
					return true;
				}
			}
			
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/*
	 * method that takes the new high score and stores it in the highScores file 
	 * such that all scores are listed from least to greatest*/
	public static void storeScore(int newScore){
		//input window for new high score
		String nameString = JOptionPane.showInputDialog("Congratulations!  You got a high score!  Please enter your name:") + "";

		//if no name is entered, or if window is closed, put name as "Anonymous"
		if (nameString.equals("null") || nameString.equals("")){
			nameString = "Anonymous";
		}
		
		//store all HighScore objects in an arrayList in preparation for sorting
		ArrayList<HighScore> scoreList = new ArrayList<>();
		
		try{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
			int nextScore;
			String name;
			boolean endOfFile = false;
			do{	//read all entries from the file, storing them as HighScore objects until the end of file is reached
				nextScore = inputStream.readInt();
				if (nextScore == -1){
					endOfFile = true;
					name = "";
				} else {
					name = inputStream.readUTF().trim();
				}
				scoreList.add(new HighScore(name, nextScore));
			}while(!endOfFile);
			inputStream.close();

			//add the new entry
			scoreList.add(new HighScore(nameString, newScore));
			//sort them
			Collections.sort(scoreList);

		} catch(Exception e){
			System.out.println(e.getMessage());
		}

		//write them back to the file
		try{
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
			for(int i = 0; i < scoreList.size(); i++){
				outputStream.writeInt(scoreList.get(i).score);
				outputStream.writeUTF("" + scoreList.get(i).name);
			}
			outputStream.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	/*a method that checks whether a high scores file exists, 
	 * and if not, creates one and stores the number -1 to signify the end of the file*/
	public static void checkFile(){
		File file = new File(fileName);
		if(!file.exists()){
			//create a new binary file
			try{
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
				outputStream.writeInt(-1);
				outputStream.close();
			} catch(Exception e){
				
			}
		}

	}

}