package kremala_package;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path; 
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
//import org.json.JSONException;


public class Character_Select extends Hangman{
	public static  Map<Integer,ArrayList<Character>> PL = new HashMap<Integer,ArrayList<Character>>();
	public static Map<Integer,ArrayList<Double>> Freq  = new HashMap<Integer,ArrayList<Double>>();
	public static int points; 
	public static int wrong_guesses;
	public static char[] hangman_spaces;
	public static char letter;
	public static int position;
	public static ArrayList<String> possible_words;
	public static ArrayList<String> new_possible_words = new ArrayList<String>();
	public static int rights = 0;
	public static int success_percent = 0;

	
	public static void main(String[] args) throws IOException {
		
	//Áí ôï ðñïçãïõìåíï ðáé÷íéäé å÷åé ïëïêëçñùèåé ìå åðéôõ÷éá, áðïôõ÷éá, solution äåí áöçíïõìå íá îáíáôñåîåé ôï ðáé÷íéäé
	BufferedReader end_of_game = new BufferedReader(new FileReader("Current_Game\\games_information.txt"));     
	if (end_of_game.readLine() == null) {
		System.out.println("START NEW GAME!!!");
		end_of_game.close();
		return;
	}
	end_of_game.close();
	Hangman myBook = new Hangman();
	PL = myBook.getPL();
	Freq = myBook.getFreq();
	String string_PW = Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(1);
	char[] PW = string_PW.toCharArray();
	hangman_spaces = Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(2).toCharArray();
	points = Integer.parseInt(Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(3));
	wrong_guesses = Integer.parseInt(Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(4));
	
	//ÄÉÁÂÁÆÏÕÌÅ ÃÑÁÌÁÌÔÁ ÊÁÉ ÈÅÓÅÉÓ ÔÏÕÓ ÁÐÏ args
	letter = args[0].charAt(0);
	position = Integer.parseInt(args[1]);
	if (letter==PW[position]) {
		System.out.println("GOOD GUESS!!!");
		System.out.println("Lives: "+(6-wrong_guesses));
		System.out.print("Missing Word: ");
		for (int l = 0; l < hangman_spaces.length; l+=2) {
			if (l==2*position) {
				hangman_spaces[l]= letter;
				System.out.print(letter+" ");
			}
			else {
				System.out.print(hangman_spaces[l]+" ");
			}
		}

		//check winner
		boolean flag=true;
		for (int l = 0; l < hangman_spaces.length; l+=2) {
			if (hangman_spaces[l]=='_') flag=false;
			}
		if (flag==true) {
			System.out.println("");
			System.out.println("Winner!!!");
			//êáèáñéæïõìå game information
			FileWriter fw = new FileWriter("Current_Game\\games_information.txt", false);
	    	PrintWriter pw = new PrintWriter(fw, false);
	    	pw.flush();
	    	pw.close();
	    	//Âáæïõìå Detail
	    	for (int l = 0; l < hangman_spaces.length; l+=2) {
				if (hangman_spaces[l]!= '_') rights++;
			}
	    	FileWriter fw2 =  new FileWriter("Current_Game\\Details.txt", true);
		    PrintWriter pw2 = new PrintWriter(fw2);
		    pw2.println(string_PW+", Attempts: "+(rights+wrong_guesses)+", Winner: Player");
	    	pw2.close();
			return;
		}
		//Ãñáöïõìå óôçí 3ç ãñáììç ôïõ gameinformation ôï hangman spaces
	    Path path = Paths.get("Current_Game\\games_information.txt");
	    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
	    lines.set(2, String.valueOf(hangman_spaces));
	    Files.write(path, lines, StandardCharsets.UTF_8);
	    
		possible_words = Possibilities(myBook);
		new_possible_words = Correct_Select(letter, possible_words);
		new_possible_words = Possibilities(myBook);
	    
		//Åêôõðùíïõìå óõíïëéêïõò ðïíôïõò-ðéèáíá ãñáììáôá-ðéèáíïôçôåò
		System.out.println("");
		System.out.println("Total points:"+ points);
		System.out.println("Possible Letters: "+PL);
		}
	else { 
		System.out.println("WRONG GUESS!!!");
		wrong_guesses++;
		int as = 6-wrong_guesses;
		System.out.println("Lives: "+(as));
	    if (wrong_guesses>=6) {
	    	System.out.println("GAME OVER!!!");
	    	System.out.println("The missing word was: "+string_PW);
	    	for (int l = 0; l < hangman_spaces.length; l+=2) {
				if (hangman_spaces[l]!= '_') rights++;
			}
			success_percent = (rights*100)/(rights+wrong_guesses);	
			System.out.println("Success percent: "+success_percent+"%");
			//êáèáñéæïõìå game information
			FileWriter fw = new FileWriter("Current_Game\\games_information.txt", false);
	    	PrintWriter pw = new PrintWriter(fw, false);
	    	pw.flush();
	    	pw.close();
	    	//Âáæïõìå Detail
	    	FileWriter fw2 =  new FileWriter("Current_Game\\Details.txt", true);
		    PrintWriter pw2 = new PrintWriter(fw2);
		    pw2.println(string_PW+", Attempts: "+(rights+wrong_guesses)+", Winner: Computer");
	    	pw2.close();
		    return;
	    		}
		//Ãñáöïõìå óôçí 5ç ãñáììç ôïõ gameinformation ôï total wrong guesses
	    Path path = Paths.get("Current_Game\\games_information.txt");
	    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
	    lines.set(4, String.valueOf(wrong_guesses));
	    Files.write(path, lines, StandardCharsets.UTF_8);
	    
		System.out.print("Missing Word: ");
		for (int l = 0; l < hangman_spaces.length; l+=2) {
			System.out.print(hangman_spaces[l]+" ");
			}
		
		possible_words = Possibilities(myBook);
		new_possible_words = Wrong_Select(letter, possible_words);
		new_possible_words = Possibilities(myBook);
		
	    //Åêôõðùíïõìå óõíïëéêïõò ðïíôïõò-ðéèáíá ãñáììáôá-ðéèáíïôçôåò
	  	System.out.println("");
		System.out.println("Total points:"+ points);
	  	System.out.println("Possible Letters: "+PL);
		}
	
    	//Åêôõðùíïõìå ðïóïóôï óùóôùí ãñáììáôùí ðïõ åðéëåîáìå
		for (int l = 0; l < hangman_spaces.length; l+=2) {
			if (hangman_spaces[l]!= '_') rights++;
		}
		success_percent = (rights*100)/(rights+wrong_guesses);	
		System.out.println("Success percent: "+success_percent+"%");
		
		possible_words=new_possible_words;
		}		
	

    //Emfanizei piuana grammata gia kathe keno kai tis pithanotites tous
	public static ArrayList<String>  Correct_Select(char letter, ArrayList<String> possible_words) throws IOException {
		int points_won=0;
		for (int i = 0; i < PL.get(position).size(); i++) { 
			if (letter==PL.get(position).get(i) && Freq.get(position).get(i)>=0.6 && Freq.get(position).get(i)<1) {
				points_won=5;
				points+=points_won;}
			else if (letter==PL.get(position).get(i) && Freq.get(position).get(i)>=0.4 && Freq.get(position).get(i)<1) {
				points_won=10;
				points+=points_won;}
			else if (letter==PL.get(position).get(i) && Freq.get(position).get(i)>=0.25 && Freq.get(position).get(i)<1) {
				points_won=15;
				points+=points_won;}
			else if (letter==PL.get(position).get(i) && Freq.get(position).get(i)<0.25) {
				points_won=30;
				points+=points_won;}
			else if(letter==PL.get(position).get(i)) {
				points_won=0;
				points+=points_won;}
			}
		ArrayList<String> temp_possible_words = new ArrayList<String>();
		for (int w = 0; w < possible_words.size(); w++) {
			char[] char_possible_words= possible_words.get(w).toCharArray();
			if (char_possible_words[position]==letter) {
				temp_possible_words.add(possible_words.get(w));
			}
		}
		//Åíçìåñùíïõìå ôï áñ÷åéï ìå ôéò ðéèáíåò ëåîåéò
		FileWriter fw =  new FileWriter("Current_Game\\games_information.txt", true);
	    PrintWriter pw = new PrintWriter(fw);
	    pw.println("Points won: "+points_won);
	    pw.println(temp_possible_words);
	    pw.close();
	    
	    //Ãñáöïõìå óôçí 4ç ãñáììç ôïõ gameinformation ôï total points
	    Path path = Paths.get("Current_Game\\games_information.txt");
	    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
	    lines.set(3, String.valueOf(points));
	    Files.write(path, lines, StandardCharsets.UTF_8);
	    
		return temp_possible_words;
	}

	public static ArrayList<String>  Wrong_Select(char letter, ArrayList<String> possible_words) throws IOException {
		int points_lose=0;
		if (points>15) {
			points_lose=15;
			points=points-points_lose;
		}
		else points=0;
		
		ArrayList<String> temp_possible_words = new ArrayList<String>();
		for (int w = 0; w < possible_words.size(); w++) {
			char[] char_possible_words= possible_words.get(w).toCharArray();
			if (char_possible_words[position]!=letter) {
				temp_possible_words.add(possible_words.get(w));
			}
		}

		FileWriter fw =  new FileWriter("Current_Game\\games_information.txt", true);
	    PrintWriter pw = new PrintWriter(fw);
	    pw.println("Points lose: "+points_lose);
	    pw.println(temp_possible_words);
	    pw.close();
	    
	    //Ãñáöïõìå óôçí 4ç ãñáììç ôïõ gameinformation ôï total points
	    Path path = Paths.get("Current_Game\\games_information.txt");
	    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
	    lines.set(3, String.valueOf(points));
	    Files.write(path, lines, StandardCharsets.UTF_8);
	    
		return temp_possible_words;
	}
}
