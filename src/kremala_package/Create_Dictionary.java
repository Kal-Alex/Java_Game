package kremala_package;

import java.io.IOException;
import java.util.*;
//import org.json.JSONException;
/**
 * The class that gets a description of a json text and creates dictionary 
 * with words of the string that has length>5 and are not repeated in the dictionary
 * @author Alexandros Kalaitzis
 */
public class Create_Dictionary {
	public static double letters6=0;
	public static double letters7_9=0;
	public static double letters10=0;
	public static double freqletters6=0.0;
	public static double freqletters7_9=0.0;
	public static double freqletters10=0.0;

	public static void main(String[] args) throws IOException {	
	}
	public static ArrayList<String> dictionary = new ArrayList<String>();
	public ArrayList<String> getDic() {
	      return dictionary;
	  }
	public void setDic(ArrayList<String> dictionary) {
	      this.dictionary = dictionary;
	  }
	public static int Dict(String description) {
		String[] words = description.split("[ \\r\\n\\r\\n.1234567890,-:;_+=/'?!@#$%^&*()\\[\\]]");
		
		for (int k = 0; k < words.length; k++) {
			words[k] = words[k].replace("\\r", "").replace("\\n",""); 
			words[k] = words[k].replaceAll("\"", "");
			words[k] = words[k].replace("\\", "");
		}
		for (int k = 0; k < words.length; k++) {
			words[k]=words[k].toUpperCase(); 
		}
		
		for (int i = 0; i < words.length; i++) {
			boolean flag=true;
			for (int j = 0; j < i; j++) {
				if (words[j].equals(words[i])) {flag=false;}
			}	
			if (words[i].length()>5 && flag==true)	{
				String s = words[i];
				dictionary.add(s);
			}	
		}


		if (dictionary.size()<20) {
			return 1;
		}
		else {
			double times=0.0;
			for (int l = 0; l < dictionary.size(); l++) {
				if (dictionary.get(l).length()>=9) times++;
			}
			if (times/dictionary.size()<0.2) {
				return 2;
			}
			else {
				System.out.println("Dictionary: "+ dictionary); 
				return 0;
			}
		}
	}
}
