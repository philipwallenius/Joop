package rocks.wallenius.joop.code;

/* 
* CodePanel.java
* The view for the Code entity. Acts as observer of the Code subject. 
* 
* v1.1 
* 26/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import rocks.wallenius.joop.mvc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rocks.wallenius.joop.parser.*;

public class CodePanel extends JScrollPane implements Observer, KeyListener, Observable {
	
	private JPanel containerPanel;
	private JTextPane jtp;
	private CodeCtrl codeCtrl;
	private Code code;
	private ArrayList<Observer> observers;
	private boolean changed;
	private Font font = new Font("courier", Font.PLAIN, 18);
	private MutableAttributeSet attrs;
	private StyledDocument doc;
	
	//colors
	public final static Color COLOR_CLASSNAME = new Color(255, 0, 0);
	public final static Color COLOR_ATTRIBUTES = new Color(3, 134, 3);
	public final static Color COLOR_CONSTRUCTORS = new Color(0, 126, 255);
	public final static Color COLOR_METHODS = new Color(0, 0, 255);
	
	//Parsed fields, constructors and methods
	private ArrayList<String> fields;	
	private ArrayList<String> constructors;
	private ArrayList<String> methods; 
	
	private ExecutionSimulator executionSimulator;
	
	/*
	 * Constructor
	 */
	public CodePanel(CodeCtrl codeCtrl, Code code) {
		
		this.codeCtrl = codeCtrl;
		this.code = code;
		code.addObserver(this);
		changed = false;
		
		observers = new ArrayList<Observer>();
		
		//Create JPanel to hold JTextPane to disable line breaks
		containerPanel = new JPanel(new BorderLayout());
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Create a JTextPane and set up the initial font styles
		jtp = new JTextPane();
		attrs = jtp.getInputAttributes();
		StyleConstants.setFontFamily(attrs, font.getFamily());
        StyleConstants.setFontSize(attrs, font.getSize());
        StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
        StyleConstants.setBold(attrs, true);
        StyleConstants.setForeground(attrs, Color.black);   

        //get document object and set document initial style
        doc = jtp.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);

		//Add JTextPane to JPanel and disable it
		containerPanel.add(jtp);
		setEnabled(false);
		
		//Add JPanel to JScrollPane
		getViewport().add(containerPanel);
		
		//KeyListener to highlight keywords
		jtp.addKeyListener(this);
		
		fields = new ArrayList<String>();
		constructors = new ArrayList<String>();
		methods = new ArrayList<String>();
		
		executionSimulator = new ExecutionSimulator(this, jtp);
		
	}
	
	/*
	 * This method colors extracted fields/constructors/methods with specified color
	 */
	public void applyColor(ArrayList<String> extracted, Color color) {
		
		String text = jtp.getText();
		Lexer lexer = new Lexer(text);
		
		//lexerTokens contains string token and its index position.
		ArrayList<String> lexerTokens = lexer.getTokens();
		
		//Loop through extracted fields/constructors/methods and color them in the code
		for(int x = 0; x < extracted.size(); x++) {
			
			//split the extracted field/constructor/method into tokens
			List<String> matches = new ArrayList<String>();
			
			//regex finds spaces and words inside double or single quotes (i.e. it preservers spaces inside strings)
			Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
			Matcher matcher = regex.matcher(extracted.get(x));
			while (matcher.find()) {
				matches.add(matcher.group());
			} 
			
			//convert from array list to an array
			String[] extractedTokens = new String[matches.size()];
			for(int i = 0; i < matches.size(); i++) {
				extractedTokens[i] = matches.get(i);
			}
				
			//start and end index positions for fields int he code
			int start = 0;
			int end = 0;
				
			//Loop through lexer tokens, compare to extracted fields/constructors/methods and find corresponding fields.
			//Then extract the index.
			//Lexer tokens holds a token string and its index position separated by "::".
			for(int i = 0; i < lexerTokens.size(); i++) {
				
				//lexerToken Array contains the token at 0 and its index at 1
				//split the lexertoken to access each them
				String[] lexerToken = lexerTokens.get(i).split("::");
				
				//k holds the current string of the extracted field/constructor/method
				int k = 0;
				
				//extractedToken holds an extracted field/constructor/method
				String extractedToken = extractedTokens[k];
				
				//compare lexer token to field/constructor/method token
				if(lexerToken[0].equals(extractedToken)) {
					
					//if match, this is the start position to color
					start = Integer.parseInt(lexerToken[1]);
					
					i++;
					k++;
					lexerToken = lexerTokens.get(i).split("::");
					extractedToken = extractedTokens[k];
					
					//while the following tokens match and as long as there are token strings in extractedToken left
					//continue until reach end of field/constructor/method
					while(lexerToken[0].equals(extractedToken) && ((k+1) < extractedTokens.length)) {
						i++;
						k++;
						
						lexerToken = lexerTokens.get(i).split("::");
						extractedToken = extractedTokens[k];
						
					}
					
					//when looped through the intermediate strings
					//if enough matches, color the identified field/constructor/method
					if(k == (extractedTokens.length - 1)) {
						
						//get the ending index 
						end = Integer.parseInt(lexerToken[1]);
						
						//for some reason must do this to synch text index
						//if left out, the coloring does not cover whole field/constructor/method
						end += 2;
						
						//set color and color the given field/constructor/method
						StyleConstants.setForeground(attrs, color);
						doc.setCharacterAttributes(start, end, attrs, false);
						
						//After coloring, must color the rest of the code black to prevent bug
						StyleConstants.setForeground(attrs, Color.black);
						doc.setCharacterAttributes(end, doc.getLength(), attrs, false);
					}
				}	
			}
			
			//reset start and end indexes
			start = 0;
			end = 0;
		}		
		
	}
	
	/*
	 * This method finds and returns the indexes of string parameter. returns both indexes as 0 if not found.
	 */
	public int[] getIndexes(String input) {

		System.out.println("input="+input);

		int[] indexes = new int[2];
		
		//initialize indexes to 0
		indexes[0] = 0;
		indexes[1] = 0;
		
		String text = jtp.getText();
		Lexer lexer = new Lexer(text);
		
		//lexerTokens contains string token and its index position.
		ArrayList<String> lexerTokens = lexer.getTokens();

		//split the extracted field/constructor/method into tokens
		List<String> matches = new ArrayList<String>();
		
		//regex finds spaces and words inside double or single quotes (i.e. it preservers spaces inside strings)
		Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
		Matcher matcher = regex.matcher(input);
	
		while (matcher.find()) {
			matches.add(matcher.group());
		} 
				
		//convert from array list to an array
		String[] extractedTokens = new String[matches.size()];
		for(int i = 0; i < matches.size(); i++) {
			extractedTokens[i] = matches.get(i);
		}

		System.out.println("extractedTokens length="+extractedTokens.length);


		//start and end index positions for fields int he code
		int start = 0;
		int end = 0;
		
		//Loop through lexer tokens, compare to extracted fields and find corresponding fields.
		//Then extract the index.
		//Lexer tokens holds a token string and its index position separated by "::".
		for(int i = 0; i < lexerTokens.size(); i++) {

			//lexerToken Array contains the token at 0 and its index at 1
			//split the lexertoken to access each them
			String[] lexerToken = lexerTokens.get(i).split("::");
			
			//k holds the current string of the extracted field
			int k = 0;
			
			//extractedToken holds an extracted field
			String extractedToken = extractedTokens[k];

			//compare lexer token to field token
			if(lexerToken[0].equals(extractedToken)) {

				//if match, this is the start position to color
				start = Integer.parseInt(lexerToken[1]);
				
				i++;
				k++;
				lexerToken = lexerTokens.get(i).split("::");
				extractedToken = extractedTokens[k];
				
				//while the following tokens match and as long as there are token strings in extractedToken left
				//continue until reach end of field
				while(lexerToken[0].equals(extractedToken) && ((k+1) < extractedTokens.length)) {
					i++;
					k++;
					
					lexerToken = lexerTokens.get(i).split("::");
					extractedToken = extractedTokens[k];						
				}
				
				//when looped through the intermediate strings
				//if enough matches, color the identified field
				if(k == (extractedTokens.length - 1)) {
					
					//get the ending index 
					end = Integer.parseInt(lexerToken[1]);
					
					//for some reason must do this to synch text index
					//if left out, the coloring does not cover whole field
					end += 2;
					
					indexes[0] = start;
					indexes[1] = end;
					return indexes;
				}
			}	
		}
		
		return indexes;
	}
	
	/*
	 * This method extracts fields, constructors and methods from class into ArrayLists
	 */
	public void parseClass() {
		
		//empty array lists first to prevent duplicates of methods
		if(!fields.isEmpty()) {
			fields.clear();
		}
		if(!constructors.isEmpty()) {
			constructors.clear();
		}
		if(!methods.isEmpty()) {
			methods.clear();
		}
		
		String text = jtp.getText();
		
		Lexer lexer = new Lexer(text);
		Parser parser = new Parser(lexer);
		ArrayList<Token> tokens = parser.getTokens();
		StringBuilder textString = new StringBuilder();
		
		/*
		 * Fields: Find all attributes in the code
		 */
		//Loop through tokens
		for(int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			
			//Token object holds type and string value. Compare the token type to find attribute.
			//Extract modifiers, datatype, identifier and any values until semicolon
			if(t.getType().equals("modifier")) {
				textString.append(t.getToken() + " ");
				i++;
				t = tokens.get(i);
				if(t.getType().equals("modifier")) {
					textString.append(t.getToken() + " ");
					i++;
					t = tokens.get(i);					
				}
				if(t.getType().equals("modifier")) {
					textString.append(t.getToken() + " ");
					i++;
					t = tokens.get(i);
				}
			}
			if(t.getType().equals("datatype")) {
				textString.append(t.getToken() + " ");
				i++;
				t = tokens.get(i);
				if(t.getType().equals("fidentifier")) {
					textString.append(t.getToken() + " ");
					i++;
					t = tokens.get(i);
					if(t.getType().equals("semicolon") || t.getType().equals("equals")) {
						if(t.getType().equals("semicolon")) {
							textString.append(t.getToken() + " ");
							fields.add(textString.toString());
						}
						else {
							
							textString.append(t.getToken() + " ");
							i++;
							t = tokens.get(i);
							
							while(!t.getType().equals("semicolon")) {
								textString.append(t.getToken() + " ");
								i++;
								t = tokens.get(i);
							}
							
							textString.append(";");
							
							//add extracted field to fields array list
							fields.add(textString.toString());	
						}
					}
				}
			}
			//empty string builder
			textString.delete(0, textString.length());	
		}
		
		/*
		 * Constructors: Find all constructors in the code
		 */
		//Same as for fields. Loop through tokens and extract all constructors
		for(int i = 0; i < tokens.size(); i++) {
			
			Token t = tokens.get(i);
			if(t.getType().equals("modifier")) {
				textString.append(t.getToken());
				i++;
				t = tokens.get(i);
			}
			if(t.getType().equals("classname")) {
				textString.append(" " + t.getToken());
				i++;
				t = tokens.get(i);
				if(t.getType().equals("leftbracket")) {
					textString.append(" " + t.getToken());
					i++;
					t = tokens.get(i);
					while(!t.getType().equals("leftcurlybracket")) {			
						textString.append(" " + t.getToken());
						i++;
						t = tokens.get(i);
					}
					//open curly brackets count (ocb_count)
					int ocb_count = 1;
					while(ocb_count > 0) {
						textString.append(" " + t.getToken());
						i++;
						t = tokens.get(i);
						if(t.getType().equals("leftcurlybracket")) {
							ocb_count++;
						}
						if(t.getType().equals("rightcurlybracket")) {
							ocb_count--;
						}
					}	
					textString.append(" }");
					
					//Add extracted constructor to array list
					constructors.add(textString.toString());
				}
			}
			
			//empty string builder
			textString.delete(0, textString.length());	
		}
		
		/*
		 * Methods: Find all methods in the code
		 */
		//Loop through tokens and extract all methods
		for(int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			if(t.getType().equals("modifier")) {
				textString.append(t.getToken());
				i++;
				t = tokens.get(i);
				if(t.getType().equals("modifier")) {
					textString.append(" " + t.getToken());
					i++;
					t = tokens.get(i);
				}
				if(t.getType().equals("modifier")) {
					textString.append(" " + t.getToken());
					i++;
					t = tokens.get(i);
				}
			}
			if(t.getType().equals("datatype")) {
				textString.append(" " + t.getToken());
				i++;
				t = tokens.get(i);
				
				if(t.getType().equals("midentifier")) {
					
					textString.append(" " + t.getToken());
					i++;
					t = tokens.get(i);
					if(t.getType().equals("leftbracket")) {
						textString.append(" " + t.getToken());
						i++;
						t = tokens.get(i);
						while(!t.getType().equals("leftcurlybracket")) {			
							textString.append(" " + t.getToken());
							i++;
							t = tokens.get(i);
						}
						//open curly brackets count (ocb_count)
						int ocb_count = 1;
						while(ocb_count > 0) {
							textString.append(" " + t.getToken());
							i++;
							t = tokens.get(i);
							if(t.getType().equals("leftcurlybracket")) {
								ocb_count++;
							}
							if(t.getType().equals("rightcurlybracket")) {
								ocb_count--;
							}
						}	
						textString.append(" }");
						//Add extracted method to array list
						methods.add(textString.toString());
					}
				}
			}
			//empty string builder
			textString.delete(0, textString.length());	
		}
	}
	
	/*
	 * This method colors the fields, constructors and methods of the class source code
	 */
	public void colorSyntax() {
		
		String text = jtp.getText();
		Lexer lexer = new Lexer(text);
		Parser parser = new Parser(lexer);
		ArrayList<Token> tokens = parser.getTokens();
		
		//Remove all previous colors
		StyleConstants.setForeground(attrs, Color.black);
		doc.setCharacterAttributes(0, doc.getLength(), attrs, false);
		
		//Loop through tokens and find class name. Find the index of the class name and color it.
		for(int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			if(t.getType().equals("classname")) {
				String className = t.getToken();
				int start = text.indexOf(className);
				int end = start + className.length();
				StyleConstants.setForeground(attrs, COLOR_CLASSNAME);
				doc.setCharacterAttributes(start, end, attrs, false);
				StyleConstants.setForeground(attrs, Color.black);
				doc.setCharacterAttributes(end, doc.getLength(), attrs, false);
				
			}
		}

		//color fields, constructors and methods
		applyColor(fields, COLOR_ATTRIBUTES);
		applyColor(constructors, COLOR_CONSTRUCTORS);
		applyColor(methods, COLOR_METHODS);
	}
	
	/*
	 * This method animates execution of a method
	 */
	public void animateMethodExecution(String method, String methodName, int objectIndex, int methodIndex, Object[] arguments) {
		
		String foundMethod = searchExtracted(methods, method, methodName);
		
		int[] indexes = getIndexes(foundMethod);
		
		//pass indexes to execution simulator and simulate the execution of the method
		if(!(indexes[0] == 0 && indexes[1] == 0)) {
			executionSimulator.simulateMethodExecution(indexes[0], indexes[1], objectIndex, methodIndex, arguments);
		}
	}
	
	/*
	 * This method animates execution of a constructor
	 */
	public void animateConstructorExecution(String constructor, String constructorName, String instanceName, Class[] parameters, Object[] arguments) {
		
		String foundConstructor = searchExtracted(constructors, constructor, constructorName);
		
		int[] indexes = getIndexes(foundConstructor);
		
		//pass indexes to execution simulator and simulate the execution of the method
		if(!(indexes[0] == 0 && indexes[1] == 0)) {
			executionSimulator.simulateConstructorExecution(indexes[0], indexes[1], instanceName, parameters, arguments);
		}
	}
	
	
	/*
	 * This method highlights the given method in the code
	 */
	public void highlightMethod(String method, String methodName) {
		
		String foundMethod = searchExtracted(methods, method, methodName);
		
		int[] indexes = getIndexes(foundMethod);
		
		//scroll to method
		scrollToIndex(indexes[0], indexes[1]);
		
		//pass indexes to execution simulator and simulate the execution of the method
		if(!(indexes[0] == 0 && indexes[1] == 0)) {
			executionSimulator.highlight(indexes[0], indexes[1]);
		}		
	}
	
	public void highlightConstructor(String constructor, String constructorName) {
		System.out.println("constructor="+constructor);
		System.out.println("constructorName="+constructorName);
		String foundConstructor = searchExtracted(constructors, constructor, constructorName);
		
		int[] indexes = getIndexes(foundConstructor);
		
		//scroll to constructor in view
		scrollToIndex(indexes[0], indexes[1]);
		
		//pass indexes to execution simulator and simulate the execution of the constructor
		if(!(indexes[0] == 0 && indexes[1] == 0)) {
			executionSimulator.highlight(indexes[0], indexes[1]);
		}				
	}
	
	/*
	 * This method scrolls to method/constructor at given indexes
	 */
	public void scrollToIndex(int startIndex, int endIndex) {
		try {
			//get view port 
			JViewport viewport = getViewport();
			
			//get rectangles of method
			Rectangle startRect = jtp.modelToView(startIndex);
			Rectangle endRect = jtp.modelToView(endIndex);
			
			//make a new rectangle which the view will be moved to
			Rectangle textRect = new Rectangle(new Point(0, startRect.y), new Dimension(startRect.width, (endRect.y - startRect.y)));
			Point pt = viewport.getViewPosition();

			if(textRect.y < pt.y) {
				textRect.setLocation(0, textRect.y-(pt.y+100));
			}
			else {
				textRect.setLocation(0, textRect.y-(pt.y-100));
			}
			
			//scroll to the new view
			viewport.scrollRectToVisible(textRect);	
		}
		catch(BadLocationException ble) {
			System.out.println("BadLocationException: " + ble.getMessage());
		}
	}
	
	/*
	 * This method searches the constructors or methods arrayList for a method/constructor and returns it if found
	 */
	public String searchExtracted(ArrayList<String> stack, String extracted, String extractedName) {
		
		String extractedString = "";
		
		int lBracketIndex = extracted.indexOf("(", 0);
		int rBracketIndex = extracted.indexOf(")", 0);
		
		String methodSignature = extracted.substring(lBracketIndex + 1, rBracketIndex);
		String[] parameterTypes = null;
		
		//if method signature has no parameters
		if(methodSignature.length() == 0) {
			//do nothing, no parameters
		}
		//if method signature has many parameters
		else if(methodSignature.contains(",")) {
			parameterTypes = methodSignature.split(",");
		}
		//if method signature has only one parameter
		else {
			parameterTypes = new String[1];
			parameterTypes[0] = methodSignature;
		}
		
		//Replace java.lang.String with String
		if(parameterTypes != null) {
			for(int i = 0; i < parameterTypes.length; i++) {
				parameterTypes[i] = parameterTypes[i].replace("java.lang.String", "String");
			}
		}
		System.out.println("stack size="+stack.size());
		
		//find indexes of given method
		for(int i = 0; i < stack.size(); i++) {

			//get method
			extractedString = stack.get(i);
			System.out.println("extractedString="+extractedString);
			int firstLeftBracket = extractedString.indexOf("(", 0);
			int firstRightBracket = extractedString.indexOf(")", 0);
			
			String charsBeforeSignature = extractedString.substring(0, firstLeftBracket);
			
			//match method name first
			if(charsBeforeSignature.contains(extractedName)) {
				
				//method match. check if parameters same
				String signature = extractedString.substring(firstLeftBracket + 1, firstRightBracket).trim();
				
				//if method signature has no parameters
				String[] parameters = null;
				
				if(signature.length() == 0) {
					//do nothing, no parameters, parameters array is null!
				}
				//if method signature has many parameters
				else if(signature.contains(",")) {
					parameters = signature.split(",");
				}
				//if method signature has only one parameter
				else {
					parameters = new String[1];
					parameters[0] = signature;
				}									
				
				if(parameters != null && parameters.length > 0) {
					String[] temp = new String[parameters.length];
					//filter out parameter types
					for(int y = 0; y < parameters.length; y++) {
						parameters[y] = parameters[y].trim();
						String[] paramToken = parameters[y].split(" ");
						temp[y] = paramToken[0];
					}
					parameters = temp; 
				}
				
				
				//Check if both do not have any parameters
				if(parameters == null && parameterTypes == null) {
					
					//METHOD MATCHES
					return extractedString;
				}
				if(parameters != null && parameterTypes != null) {
					//Check that they have equal number of parameters
					if(parameters.length == parameterTypes.length) {
						
						//compare each parameter type and see if same
						for(int z = 0; z < parameters.length; z++) {
							
							if(parameters[z].equals(parameterTypes[z])) {	
								
								//Check if all parameters compared already. if yes, then they match
								if(z < parameters.length) {	
									//METHOD MATCHES
									return extractedString;
								}
							}
							else {
								//parameters do not match
								break;
							}
						}
					
					}
				}
				else {
					continue;
				}
				continue;
			}		
		}
		return "";
	}

	/*
	 * This method highlights text between specified start and end indexes
	 */
	public void highlightText(int start, int end) {
		Highlighter h = jtp.getHighlighter();
	    
		//remove previous highlights
		h.removeAllHighlights();
	    
	    try {
	    	
	    	h.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
	    	
	    } 
	    catch(BadLocationException ble) {
	    	System.out.println("BadLocationException: " + ble.getMessage());
		}
	}	
	
	/*
	 * Mutators
	 */
	public void setText(String text) throws BadLocationException {
		doc.remove(0, doc.getLength());
		doc.insertString(0, text, attrs);
		
		//extract the fields, constructors and methods after the text has been inserted
		parseClass();
	}
	
	public void setEnabled(boolean b) {
		if(b == true) {
			jtp.setEditable(true);
			jtp.setBackground(Color.white);
		}
		if(b == false) {
			jtp.setEditable(false);
			jtp.setBackground(new Color(238,238,238));			
		}
	}
	
	/*
	 * This methods returns the code in the CodePanel
	 */
	public String getText() throws BadLocationException {
		return doc.getText(0, doc.getLength());
	}
	
	public CodeCtrl getCodeCtrl() { return codeCtrl; }
	
	/*
	 * This method updates the JTextPanel content and it's called whenever 
	 * the code field changes in the Code entity (such as when creating or opening a .java file)
	 */
	public void update()  {
		try {
			//Enable the JTextPanel and get the code from the Code object
			setEnabled(true);
			
			setText(code.getCode());
		}
		catch(BadLocationException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * This method handles key presses for the CodePanel
	 */
	public void keyPressed(KeyEvent e) {
		//notify observers (esp ToolMenu) that the code has been changed to enable save icon
		setChanged(true);
		notifyObservers();
	}
	
	public void keyReleased(KeyEvent e) {
		
	}
	
	public void keyTyped(KeyEvent e) {
		
	}

	/*
	 * Observer methods to add, delete and notify observers
	 * 
	 */
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void deleteObserver(Observer o) {
		int i = observers.indexOf(o);
		if (i >= 0 ) {
			observers.remove(i);
		}
	}
	
	public void notifyObservers() {
		if(changed) {
			for(int i = 0; i < observers.size(); i++) {
				Observer observer = observers.get(i);
				observer.update();
			}
			setChanged(false);
		}
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
