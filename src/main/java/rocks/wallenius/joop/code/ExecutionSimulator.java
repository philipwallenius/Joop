package rocks.wallenius.joop.code;

/* 
* ExecutionSimulator.java
* This class is resonsible for highlighting code in the CodePanel class. It is also responsible for animating
* the simulation of code execution by using Timer and TimerTask objects. 
* 
* v1.0
* 02/04/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

public class ExecutionSimulator implements HighlightPainter {
	
	private JTextComponent component;
	private final static Color INIT_COLOR = new Color(205, 205, 205);
	private final static int INCREMENT_INDEX = 25;
	
	private int startIndex;
	private int endIndex;
	
	private AnimationTask animationTask;
	private CodePanel codePanel;
	private Color highlightColor;
	
	/*
	 * Constructor
	 */
	public ExecutionSimulator(CodePanel codePanel, JTextComponent component) {	
		this.codePanel = codePanel;
		this.component = component;
		highlightColor = INIT_COLOR;
		try
		{
			component.getHighlighter().addHighlight(0, 0, this);
		}
		catch(BadLocationException ble) {
			System.out.println("BadLocationException: " + ble.getMessage());
		}
		
		startIndex = 0;
		endIndex = 0;
	}	
	
	/*
	 * This method paints the highlight. 
	 */
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		
		if(!(startIndex == 0 && endIndex == 0)) {
			try {
				Rectangle r = component.modelToView(startIndex);
				g.setColor(highlightColor);
				g.fillRect(0, r.y, c.getWidth(), r.height);
			}
			catch(BadLocationException ble) { 
				System.out.println("BadLocationException: " + ble.getMessage());
			}			
		}
	}
	
	/*
	 * This method simulates the execution of code by animating row-by-row highlighting 
	 * between given start and end indexes.
	 */
	public void simulateMethodExecution(int start, int end, int objectIndex, int methodIndex, Object[] arguments) {
		animationTask = new AnimationTask(this, component, start, end, objectIndex, methodIndex, arguments);
		animationTask.execute();
	}
	
	/*
	 * This method simulates the execution of code by animating row-by-row highlighting 
	 * between given start and end indexes.
	 */
	public void simulateConstructorExecution(int start, int end, String instanceName, Class[] parameters, Object[] arguments) {
		animationTask = new AnimationTask(this, component, start, end, instanceName, parameters, arguments);
		animationTask.execute();
	}	
	
	/*
	 * This method highlights methods
	 */
	public void highlight(int start, int end) {
		setColor(INIT_COLOR);
		startIndex = start;
		endIndex = end;
		component.repaint();
	}
	
	/*
	 * This method resets all previous highlights
	 */
	public void resetHighlights() {
		setColor(new Color(255, 255, 255));
		
		startIndex = 0;
		endIndex = 0;	
		
		component.repaint();
	}
	
	public CodePanel getCodePanel() { return codePanel; }
	public Color getColor() { return highlightColor; }
	
	/*
	 * Mutators
	 */
	public void setStartIndex(int i) { startIndex = i; }
	public void setEndIndex(int i) { endIndex = i; }
	public void setColor(Color c) { highlightColor = c; }
}
