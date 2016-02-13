package rocks.wallenius.joop.parser;

/* 
* Lexer.java
* Converts a stream of characters into a stream string tokens
* 
* v1.1 
* 25/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.io.*;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

public class Lexer {
	private File file;
	private String input;
	private ArrayList<String> tokens;
	
	//Constructors
	public Lexer(String f) {
		tokens = new ArrayList<String>();
		
		input = f;
		
		tokens = tokenize(input);
		
	}	
	
	//Method to tokenize input string. It stores both token and its index spearated by ::
	public static ArrayList<String> tokenize(String stringInput) {
		
		boolean inToken = false;
		boolean inComment = false;
		String tokenString = "";
		ArrayList<String> tokens = new ArrayList<String>();	
		int newlinescount = 0;
		
		//loop input string and start tokenization
		for(int i = 0; i < stringInput.length(); i++) {
			
			//extract char
			char currentChar = stringInput.charAt(i);
		
			//is character whitespace?
			if(Character.isWhitespace(currentChar)) {	
				
				if((currentChar == '\n')) {		
					newlinescount++;
				}
				continue;
			}

			//is character a comment?
			else if(currentChar == '/') {

				//increment i
				i++;
		
				//extract next char
				currentChar = stringInput.charAt(i);
				
				//is it a single line comment?
				if(currentChar == '/') {

					//in a comment
					inComment = true;
					
					//continue until new line
					while(inComment == true) {
						
						//increment i
						i++;
						
						//extract next char
						currentChar = stringInput.charAt(i);
						
						//check if current character is new line
						if(( currentChar == '\n' ) || ( currentChar == '\r' )) {
							inComment = false;						
						}
					}
				}
				
				//is it a block comment?
				if(currentChar == '*') {

					//in a comment
					inComment = true;
					
					//continue until next '*/'
					while(inComment == true) {
						
						//increment i
						i++;
						
						//extract next char
						currentChar = stringInput.charAt(i);
						
						//if currentChar is star, check if the next is a slash which means end of comment
						if(currentChar == '*') {
							
							i++;
							currentChar = stringInput.charAt(i);

							//if currentChar is a slash, block comment is finished. Jump out of loop.
							if(currentChar == '/') {		
								inComment = false;
							}
							else {
								i--;
							}
						}
					}
				}
			}
			
			//is character reserved character like brackets, braces, plus, minus etc? 
			else if(currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == ';' || currentChar == '=' || currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == ',' || currentChar == '%' || currentChar == '>' || currentChar == '<' || currentChar == '!') {

				//in a token
				inToken = true;
				
				//add char to tokenString
				tokenString += currentChar;
				
				//token extracted
				inToken = false;
				
				//add token to tokens arraylist
				tokens.add(tokenString+"::"+(i-(tokenString.length() + newlinescount)));
				
				//empty tokenString
				tokenString = "";
			}			
			
			//is character a letter, underscore, dollarsign etc...? for example identifier, data type
			else if(Character.isLetter(currentChar) || (currentChar == '_') || (currentChar == '$') || (currentChar == '[') || (currentChar == ']')) {

				//lexer is in a token
				inToken = true;
				
				//add first character of token into tokenString
				tokenString += currentChar;
				
				//extract the token
				while(inToken) {
					
					//increase index
					i++;
					
					//take currentChar
					currentChar = stringInput.charAt(i);
					
					//if character is whitespace, terminator, brackets, braces etc. then break out
					if(Character.isWhitespace(currentChar) || currentChar == ';' || currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == '=' || currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '%') {
						
						//add token to tokens arraylist
						tokens.add(tokenString+"::"+(i-(tokenString.length() + newlinescount)));
						
						//empty tokenString
						tokenString = "";
						
						//extracted token
						inToken = false;
					}
					
					//if currentChar is valid character add it to tokenString
					else if(Character.isLetter(currentChar) || currentChar == '_' || currentChar == '$' || currentChar == '.' || currentChar == ',' || currentChar == '*' || currentChar == '[' || currentChar == ']') {
						tokenString += currentChar;
					}
					
					else if(Character.isDigit(currentChar)) {
						tokenString += currentChar;
					}
					
					//if other character, it is invalid
					else {
						System.out.println("1. Error in token extraction. It might be due to an invalid char. currentChar value: " + currentChar);
						System.exit(0);
					}
					
				}
				//need to decrement i here. otherwise, if break out due to bracket, braces etc. 
				//it could be missed and not read as token
				i--;
			}
			
			//if character is a digit
			else if(Character.isDigit(currentChar)) {

				//lexer is in a token
				inToken = true;
				
				//add first character of token into tokenString
				tokenString += currentChar;
				
				//extract the token
				while(inToken) {
					
					//increase index
					i++;
					
					//take currentChar
					currentChar = stringInput.charAt(i);
					
					//if character is whitespace, terminator, brackets, braces etc. then break out
					if(Character.isWhitespace(currentChar) || currentChar == ';' || currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == '=' || currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '%' || currentChar == ',') {
						//add token to tokens arraylist
						tokens.add(tokenString+"::"+(i-(tokenString.length() + newlinescount)));
						
						
						//empty tokenString
						tokenString = "";
						
						//extracted token
						inToken = false;
					}
					
					//if currentChar is valid character add it to tokenString
					else if(Character.isDigit(currentChar) || currentChar == '.' || currentChar == 'f') {
						tokenString += currentChar;
					}
					
					//if other character, it is invalid
					else {
						//shouldn't be reachable
						System.out.println("2. Error in token extraction. It might be due to an invalid char. currentChar value: " + currentChar);
						System.exit(0);
					}
				}
				//need to decrement i here. otherwise, if break out due to bracket, braces etc. 
				//it could be missed and not read as token
				i--;
			}
			
			//is character a string?
			else if(currentChar == '"') {

				//lexer is in a token
				inToken = true;
				
				//add first character of token into tokenString
				tokenString += currentChar;
				
				//extract the token
				while(inToken) {
					
					//increment i
					i++;
					
					//extract next char
					currentChar = stringInput.charAt(i);
					
					//if char is another " then token is extracted
					if(currentChar == '"') {
						tokenString += currentChar;
						tokens.add(tokenString+"::"+(i-(tokenString.length() + newlinescount)));
						tokenString = "";
						inToken = false;
						break;			
					}
					tokenString += currentChar;
				}
			}
			
			//is character a char?
			else if(currentChar == '\'') {

				//extract the three characters representing the char eg 'c'
				inToken = true;
				tokenString += currentChar;
				i++;
				currentChar = stringInput.charAt(i);
				tokenString += currentChar;
				i++;
				currentChar = stringInput.charAt(i);
				tokenString += currentChar;
				tokens.add(tokenString+"::"+(i-(tokenString.length() + newlinescount)));
				tokenString = "";
				inToken = false;
			}
			
			//otherwise character is invalid!
			else {
				System.out.println("3. Error. Invalid character in lexer stream. currentChar value is: " + currentChar);
				System.exit(0);
			}	
		}
		
		//return arraylist of extracted tokens
		return tokens;	
	}
	
	public ArrayList<String> getTokens() {
		return tokens;
	}
}
