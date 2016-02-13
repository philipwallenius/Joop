package rocks.wallenius.joop.parser;

/* 
* Parser.java
* Reads a stream of string tokens into a stream of token objects
* 
* v1.1 
* 25/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.util.ArrayList;
import java.util.HashSet;

public class Parser {
	
	private ArrayList<Token> tokens;
	private ArrayList<String> stringTokens;
	Lexer lexer;
	
	/*
	 * Constructors
	 */
	public Parser(Lexer lexer) { 
		this.lexer = lexer;
		
		//Create ArrayList and get String tokens from the lexer
		stringTokens = new ArrayList<String>();
		stringTokens = lexer.getTokens();
		
		//Create Token objects from string tokens and put in ArrayList tokens
		tokens = createTokens(stringTokens);	
	}	
	
	/*
	 * Method to create Token objects from an ArrayList of string tokens
	 */
	public static ArrayList<Token> createTokens(ArrayList<String> stringTokens) {
		ArrayList<Token> extractedTokens = new ArrayList<Token>();
		String className = "";
		
		//categorize token
		for(int i = 0; i < stringTokens.size(); i++) {
			
			//split token and its index
			String[] splitToken = stringTokens.get(i).split("::");
			
			//get next token
			String token = splitToken[0];
			
			//is token a modifier?
			if(isModifier(token)) {
				extractedTokens.add(new Token(token, "modifier"));
			}
			
			//is token a datatype?
			else if(isDataType(token)) {
				extractedTokens.add(new Token(token, "datatype"));
			}
			
			//is token a identifier?
			else if(isIdentifier(token)) {
				
				i++;
				
				String[] temp = stringTokens.get(i).split("::");
				String next = temp[0];
				
				//check which type of identifier (method or field)
				if(next.equals("(")) {
					extractedTokens.add(new Token(token, "midentifier"));
				}
				else {
					extractedTokens.add(new Token(token, "fidentifier"));
				}

				i--;
			}
			//is token any reserved word or symbol?
			else if(token.equals("class")) {
				extractedTokens.add(new Token(token, "class"));
			}
			else if(token.equals("(")) {
				extractedTokens.add(new Token(token, "leftbracket"));
			}
			else if(token.equals(")")) {
				extractedTokens.add(new Token(token, "rightbracket"));
			}
			else if(token.equals("{")) {
				extractedTokens.add(new Token(token, "leftcurlybracket"));
			}
			else if(token.equals("}")) {
				extractedTokens.add(new Token(token, "rightcurlybracket"));
			}
			else if(token.equals(";")) {
				extractedTokens.add(new Token(token, "semicolon"));
			}
			else if(token.equals(",")) {
				extractedTokens.add(new Token(token, "comma"));
			}
			else if(token.equals("=")) {
				extractedTokens.add(new Token(token, "equals"));
			}
			//otherwise check if its a classname otherwise categorize as "other"
			else {
				if(Character.isUpperCase(token.charAt(0))) {
					if(className.equals("")) {
						extractedTokens.add(new Token(token, "classname"));
						className = token;
					}
					else if(className.equals(token)) {
						extractedTokens.add(new Token(token, "classname"));
					}
					else {
						extractedTokens.add(new Token(token, "other"));
					}
				}
				else {
					extractedTokens.add(new Token(token, "other"));
				}
			}
		}
		
		return extractedTokens;
	}
	
	/*
	 * Method to check if string is a modifier
	 */
	public static boolean isModifier(String t) {
		
		//Create TreeSet with all possible modifiers
		HashSet<String> modifiers = new HashSet<String>();
		modifiers.add("abstract");
		modifiers.add("final");
		modifiers.add("native");
		modifiers.add("none(package)");
		modifiers.add("private");
		modifiers.add("protected");
		modifiers.add("public");
		modifiers.add("strictfp");
		modifiers.add("static");
		modifiers.add("synchronized");
		modifiers.add("transient");
		modifiers.add("volatile");
		
		//Test if string is a modifier
		return modifiers.contains(t);
	}
	
	/*
	 * Method to check if string is a data type
	 */
	public static boolean isDataType(String t) {
		
		//Create TreeSet of possible datatypes
		HashSet<String> dataTypes = new HashSet<String>();
		dataTypes.add("byte");
		dataTypes.add("short");
		dataTypes.add("int");
		dataTypes.add("long");
		dataTypes.add("float");
		dataTypes.add("double");
		dataTypes.add("char");
		dataTypes.add("String");
		dataTypes.add("boolean");
		dataTypes.add("void");
		
		//Test if String is a simple data type
		if(dataTypes.contains(t)) {
			return true;
		}
		//Test if String is a complex data type
		else if(t.contains("")) {
//			System.out.println("t="+t);
//			int lastDotIndex = t.lastIndexOf("");
//			System.out.println("lastDotIndex="+lastDotIndex);
//			String temp = t.substring(lastDotIndex);
//			String temp = t.substring(lastDotIndex+1);
			//complex if contains dot and class name begins with big letter
//			return Character.isUpperCase(temp.charAt(0));
			return Character.isUpperCase(t.charAt(0));
		}
		else {
			return false;
		}
	}
	
	/*
	 * Method to check if string is a variable name
	 */
	public static boolean isIdentifier(String t) {
		
		//if String is a reserved word, return false
		if(isReservedWord(t)) {
			return false;
		}
		
		//if first character is lowercase, return true
		else //if string is not a reserved word but does not start with lowercase, return false
			return Character.isLowerCase(t.charAt(0)) || t.charAt(0) == '_' || t.charAt(0) == '$';
	}
	
	/*
	 * Method to check if string is a reserved word
	 */
	public static boolean isReservedWord(String t) {
		
		//Create HashSet with reserved words
		HashSet<String> reservedWords = new HashSet<String>();
		reservedWords.add("abstract");
		reservedWords.add("assert");
		reservedWords.add("boolean");
		reservedWords.add("break");
		reservedWords.add("byte");
		reservedWords.add("case");
		reservedWords.add("catch");
		reservedWords.add("char");
		reservedWords.add("class");
		reservedWords.add("const");
		reservedWords.add("continue");
		reservedWords.add("default");
		reservedWords.add("do");
		reservedWords.add("double");
		reservedWords.add("else");
		reservedWords.add("enum");
		reservedWords.add("extends");
		reservedWords.add("false");
		reservedWords.add("final");
		reservedWords.add("finally");
		reservedWords.add("float");
		reservedWords.add("for");
		reservedWords.add("goto");
		reservedWords.add("if");
		reservedWords.add("implements");
		reservedWords.add("import");
		reservedWords.add("instanceof");
		reservedWords.add("int");
		reservedWords.add("interface");
		reservedWords.add("long");
		reservedWords.add("native");
		reservedWords.add("new");
		reservedWords.add("null");
		reservedWords.add("package");
		reservedWords.add("private");
		reservedWords.add("protected");
		reservedWords.add("public");
		reservedWords.add("return");
		reservedWords.add("short");
		reservedWords.add("static");
		reservedWords.add("strictfp");
		reservedWords.add("super");
		reservedWords.add("switch");
		reservedWords.add("synchronized");
		reservedWords.add("this");
		reservedWords.add("throw");
		reservedWords.add("throws");
		reservedWords.add("transient");
		reservedWords.add("true");
		reservedWords.add("try");
		reservedWords.add("void");
		reservedWords.add("volatile");
		reservedWords.add("while");
		
		//Test if String is a reserved word
		return (reservedWords.contains(t));
	}
	
	public ArrayList<Token> getTokens() { return tokens; }

}
