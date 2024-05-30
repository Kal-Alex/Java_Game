package kremala_package;

import java.util.Random;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONException;

public class Hangman extends JsonReader {
	public static void main(String[] args) throws IOException, JSONException {
		Hangman myBook = new Hangman();
		myBook.setOLD(args[0]);
		myBook.getOLD();
		BufferedReader reader = new BufferedReader(new FileReader("medialab\\hangman_"+myBook.getOLD()+".txt"));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		int max = lines-1;
		Random random = new Random();
		int rand = random.nextInt(max);
		//Åðéëåãåôáé ôõ÷áéá ìéá ëåîç
		myBook.setPW(Files.readAllLines(Paths.get("medialab\\hangman_"+myBook.getOLD()+".txt")).get(rand).toCharArray());		
		ArrayList<String> all_words = new ArrayList<String>();
		for (int i = 0; i < lines; i++) {
			String temp_word = Files.readAllLines(Paths.get("medialab\\hangman_"+myBook.getOLD()+".txt")).get(i);
			all_words.add(temp_word);}
		File file = new File("Current_Game\\games_information.txt"); 
	    FileWriter fw =  new FileWriter(file);
	    PrintWriter pw = new PrintWriter(fw);
	    pw.println(myBook.getOLD());
	    pw.println(myBook.getPW());
	    for (int l = 0; l < myBook.getPW().length; l++) {
	    	 pw.print("_ ");
		}
	    pw.println();
	    pw.println(0); //4ç ãñáììç total points
	    pw.println(0); //5ç ãñáììç total wrong guesses
	    pw.println(all_words);
	    pw.close();

		Possibilities(myBook);
	}

	public static Map<Integer,ArrayList<Character>> possible_letters_map = new HashMap<Integer,ArrayList<Character>>();
	public Map<Integer,ArrayList<Character>> getPL() {
	      return possible_letters_map ;}
	public void setPL(Map<Integer,ArrayList<Character>> possible_letters_map ) {
	      this.possible_letters_map  = possible_letters_map ;}
	
	public static Map<Integer,ArrayList<Double>> frequencies_map  = new HashMap<Integer,ArrayList<Double>>();
	public Map<Integer,ArrayList<Double>> getFreq() {
	      return frequencies_map ;}
	public void setFreq(Map<Integer,ArrayList<Double>>  frequencies_map ) {
	      this.frequencies_map  =  frequencies_map ;}

	public char[] players_word;	
	public char[] getPW() {
	      return players_word;}
	public void setPW(char[]  players_word) {
	      this.players_word =  players_word;}
	
	public static ArrayList<String> Possibilities(Hangman myBook) throws IOException {
		String OLD = Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(0);
		BufferedReader reader = new BufferedReader(new FileReader("medialab\\hangman_"+OLD+".txt"));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		int max = lines-1;
		Random random = new Random();
		int rand = random.nextInt(max);
		
		//ÂÑÉÓÊÏÕÌÅ ÔÉÓ ÐÉÈÁÍÅÓ ËÅÎÅÉÓ
		ArrayList<String> possible_words= new ArrayList<String>();
		int j=0;
		String players_word = Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(1);
		
		FileInputStream fs= new FileInputStream("Current_Game\\games_information.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		int info_lines = 0;
		while (br.readLine() != null) info_lines++;
		fs.close();
		br.close();
	    String[] all_Words = Files.readAllLines(Paths.get("Current_Game\\games_information.txt")).get(info_lines-1).split(", ");
	    for (int k = 0; k < all_Words.length; k++) {
			all_Words[k] = all_Words[k].replaceAll("\\[", "").replaceAll("\\]","");} 
		
	    for (int i = 0; i < all_Words.length; i++) {
			String temp_word = all_Words[i];
			if (players_word.length()==temp_word.length()) {	
				possible_words.add(temp_word);}
				j++;}	
		ArrayList<ArrayList<Character>> duplicate_letters = new ArrayList<ArrayList<Character>>();
		for (int l = 0; l < players_word.length(); l++) {
			Map<String,ArrayList<Character>> arraynames = new HashMap<String,ArrayList<Character>>();
			String query = String.format("possible_letters_%o",l);
			arraynames.put(query, new ArrayList<Character>());
			for (int w = 0; w < possible_words.size(); w++) {
				char[] char_possible_words= possible_words.get(w).toCharArray();
				arraynames.get(query).add(char_possible_words[l]);
			}
			duplicate_letters.add(sortArrayElementsByFrequency(arraynames.get(query)));	
		}
		
		//ÊÑÁÔÁÌÅ ÔÁ ÐÉÈÁÍÁ ÃÑÁÌÌÁÔÁ ÃÉÁ ÊÁÈÅ ÈÅÓÇ ÌÅ ÐÉÈÁÍÏÔÉÊÇ ÓÅÉÑÁ
		double  double_size = possible_words.size();
		for (int u = 0; u < players_word.length(); u++) {
			int freq=1;
			possible_letters_map.put(u, new ArrayList<Character>());
			frequencies_map.put(u, new ArrayList<Double>());
			for (int y = 0; y < possible_words.size()-1; y++) {
				if (duplicate_letters.get(u).get(y) != duplicate_letters.get(u).get(y+1)) {
					possible_letters_map.get(u).add(duplicate_letters.get(u).get(y));
					frequencies_map.get(u).add(freq/double_size);
					freq=1;
				}
				else freq += 1;
			}
			if(possible_words.size()>0) {
			possible_letters_map.get(u).add(duplicate_letters.get(u).get(possible_words.size()-1));
			frequencies_map.get(u).add(freq/double_size);
			}
		}	
		myBook.setPL(possible_letters_map);	
		myBook.setFreq(frequencies_map);
		
	    if(info_lines==6) { 
			System.out.println("Total words of the dictionary: "+lines);
			for (int l = 0; l < all_Words.length; l++) {
				if (all_Words[l].length()==6) letters6++;
				else if (all_Words[l].length()<10) letters7_9++;
				else if (all_Words[l].length()>=10) letters10++;
			}
			freqletters6=letters6*100/all_Words.length;
			freqletters7_9=letters7_9*100/all_Words.length;
			freqletters10=letters10*100/all_Words.length;
			System.out.println("Words with 6 letters: " + freqletters6 + "%");
			System.out.println("Words with >=7 and <=9 letters: " + freqletters7_9 + "%");
			System.out.println("Words with >=10 letters: " + freqletters10 + "%");
			
	    	System.out.println("Lives: 6");
		    System.out.print("Missing Word: ");
			for (int l = 0; l < myBook.getPW().length; l++) {
				System.out.print("_ ");
			}
			System.out.println("");
			System.out.println("Total points:"+ 0);
			System.out.println("Possible Letters: "+myBook.getPL());
			FileWriter fw =  new FileWriter("Current_Game\\games_information.txt", true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(possible_words);
			pw.close();
	    }
		return possible_words;
	}
	
	//ÂÏÇÈÇÔÉÊÇ ÓÕÍÁÑÔÇÓÇ ÃÉÁ ÓÏÑÔÁÑÉÓÌÁ ÌÅ ÂÁÓÇ ÓÕ×ÍÏÔÇÔÁ ÃÑÁÌÌÁÔÏÓ
	private static ArrayList<Character> sortArrayElementsByFrequency(ArrayList<Character> inputArray) throws IOException{ 
        Map<Character, Integer> elementCountMap = new LinkedHashMap<>();
        for (int i = 0; i < inputArray.size(); i++) {
            if (elementCountMap.containsKey(inputArray.get(i))){
                elementCountMap.put(inputArray.get(i), elementCountMap.get(inputArray.get(i))+1);
            }
            else{
                elementCountMap.put(inputArray.get(i), 1);
            }
        }
        ArrayList<Character> sortedElements = new ArrayList<>();
        elementCountMap.entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .forEach(entry -> { 
                            for(int i = 1; i <= entry.getValue(); i++) 
                                sortedElements.add(entry.getKey());
                                });
        return sortedElements;
    }
}
