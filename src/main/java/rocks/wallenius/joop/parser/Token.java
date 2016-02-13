package rocks.wallenius.joop.parser;

/* 
* Token.java
* Holds a token and its type 
* 
* v1.0 
* 08/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

public class Token {
	
	private String token, type;
	
	/*
	 * Constructors
	 */
	public Token(String token, String type) {
		this.token = token;
		this.type = type;
	}
	
	/*
	 * Accessors
	 */
	public String getToken() { return token; }
	
	public String getType() { return type; }
	
	/*
	 * Mutators
	 */
	public void setToken(String to) {
		token = to;
	}
	
	public void setType(String ty) {
		type = ty;
	}
	
	public String toString() {
		return ("Token: " + token + "\nType: " + type);
	}
	
}
	
