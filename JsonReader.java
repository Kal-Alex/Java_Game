package kremala_package;

import java.io.BufferedReader;
import java.io.*; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;
//import java.util.*;

/**
 * This class is used to read a json text from a url in order to create a valid dictionary
 * @author Alexandros Kalaitzis
 */
public class JsonReader extends Create_Dictionary {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException{
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }
  /**
   * String with the description of the json text
   */
  public String description;
  /**
   * Description setter method
   * @param description the description of a book
   */
  public void setD(String description) {
      this.description = description;
  }
  /**
   * Get the description of a book
   * @return the description in a string
   */
  public String getD() {
      return description;
  }
 /**
  * The Open Library Books API
  */
  public String Open_Library_ID;
  /**
   * Open Library ID of a book setter method
   * @param Open_Library_ID the book ID in openlibrary
   */
  public void setOLD(String Open_Library_ID) {
      this.Open_Library_ID = Open_Library_ID;
  }
  /**
   * Get the openlibrary ID of the book
   * @return the book ID in openlibrary
   */
  public String getOLD() {
      return Open_Library_ID;
  }
 
  /**
   * Call method Book that creates the Dictionary and throws Exceptions if the Dictionary is invalid
   * @param args args[0] is the Open Library ID
   * @throws IOException necessary when we read from json and write in a text
   * @throws JSONException when JSONObject["description"] not found
   */
  public static void main(String[] args) throws IOException, JSONException {
	  JsonReader myBook = new JsonReader();
	  myBook.setOLD(args[0]);
	   try {
		   Book(myBook);
	    }
	    catch (UndersizeException ex1) {
	    	System.err.print(ex1);
	    }
	    catch (UnbalancedException ex2) {
		    System.err.print(ex2);
		}
  }
  /**
   * Creates a dictionary from the string description
   * @param myBook JsonReader Object
   * @throws UndersizeException invalid dictionary with less than 20 words
   * @throws UnbalancedException invalid dictionary with less than 20% words with more than 9 letters
   * @throws IOException necessary when we read from json and write in a text
   * @throws JSONException when JSONObject["description"] not found
   */
  public static void Book(JsonReader myBook) throws UndersizeException, UnbalancedException, IOException, JSONException{
	String Open_Library_ID = myBook.getOLD();
    JSONObject json = readJsonFromUrl("https://openlibrary.org/works/"+Open_Library_ID+".json"); 
    myBook.setD(json.get("description").toString());
    
    if (Dict(myBook.getD())==0) {
        File file = new File("medialab\\hangman_"+Open_Library_ID+".txt"); 
        FileWriter fw =  new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
    	int Dicsize=myBook.getDic().size();
    	for (int i = 0; i < Dicsize-1; i++) {
    		pw.println(myBook.getDic().get(i));
    	}
    	pw.print(myBook.getDic().get(Dicsize-1));
    	pw.close();
    }
    else if (Dict(myBook.getD())==1){
    	throw new UndersizeException("Dictionary with <20 words");
    }
    else if (Dict(myBook.getD())==2){
    	throw new UnbalancedException("Dictionary with <20% words with >9 letters");
    }
  }
    /**
     * This class is used in order to throw exception if a dictionary has less than 20 words.
     * @author Alexandros Kalaitzis
     */
	public static class UndersizeException extends Exception {
		/**
		 *  Method for throwing a specific message
		 * @param message "Dictionary with less than 20 words"
		 */
	    public UndersizeException(String message) {
	        super(message);
	    }
	}
	/**
     * This class is used in order to throw exception if a dictionary has less than 20% words with more than 9 letters.
     * @author Alexandros Kalaitzis
	 */
	public static class UnbalancedException extends Exception {
		/**
		 *  Method for throwing a specific message
		 * @param message "Dictionary with less than 20% words with more than 9 letters"
		 */ 
	    public UnbalancedException(String message) {
	        super(message);
	    }
	}
}
