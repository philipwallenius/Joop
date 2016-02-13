package rocks.wallenius.joop.code;

/* 
* AnimationTask.java
* This class extends SwingWorker class. It is responsible for animating the code execution simulation. It does this
* by incrementing the indexes  which the highlighting is based on. 
* 
* v1.1
* 02/04/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.Color;

import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

public class AnimationTask extends SwingWorker {

	private JTextComponent component;
	private int startIndex, endIndex;
	private final static int INCREMENT_INDEX = 1;
	private ExecutionSimulator es;
	private final static long INIT_DELAY = 30;
	
	private int objectIndex, methodIndex;
	private Object[] arguments;
	private String instanceName;
	private Class[] parameters;
	
	private int type;
	private final static int CONSTRUCTOREXECUTION = 1;
	private final static int METHODEXECUTION = 2;
	
	private final static Color EXECUTION_COLOR = new Color(42, 255, 52);
	private final static Color STOP_COLOR = new Color(205, 205, 205);
	
	/*
	 * Constructor for method execution animation
	 */
	public AnimationTask(ExecutionSimulator es, JTextComponent component, int startIndex, int endIndex, int objectIndex, int methodIndex, Object[] arguments) {
		this.es = es;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.component = component;
		
		this.objectIndex = objectIndex;
		this.methodIndex = methodIndex;
		this.arguments = arguments;
		type = METHODEXECUTION; 
		
		es.setColor(STOP_COLOR);
	}
	
	/*
	 * Constructor for constructor execution animation
	 */
	public AnimationTask(ExecutionSimulator es, JTextComponent component, int startIndex, int endIndex, String instanceName, Class[] parameters, Object[] arguments) {
		this.es = es;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.component = component;
		
		this.instanceName = instanceName;
		this.parameters = parameters;
		this.arguments = arguments;
		type = CONSTRUCTOREXECUTION; 
		
		es.setColor(STOP_COLOR);		
	}
	
	/*
	 * This method is executed each time the timer schedule goes off. It is reponsible for incrementing the indexes
	 * which the highlighting in ExecutionSimulator are based on and it also repaints the JTextPane in CodePanel. 
	 */
	public String doInBackground() {
 		
		boolean b = true;
		long currDelay = INIT_DELAY;
		
		
 		do {	
 			es.setColor(EXECUTION_COLOR);
 			
			if(startIndex == 0 && endIndex == 0) {
				component.repaint();
			}
			else {
				if(startIndex >= endIndex) {
					es.setColor(STOP_COLOR);
					currDelay = 1300;
					b = false;
				}
				
				if((startIndex + INCREMENT_INDEX) < endIndex) {
					startIndex += INCREMENT_INDEX;
				}
				else {
					int y = endIndex%startIndex;
					startIndex += y;
				}
				es.setStartIndex(startIndex);
				es.setEndIndex(endIndex);
			}
			
			component.repaint();
			try {
				Thread.currentThread().sleep(currDelay);
			}
			catch(InterruptedException ie) {
				System.out.println("InterruptedExecption: " + ie.getMessage());
			}	

 		}
		while(b == true);	
		return "";
	}
	
	/*
	 * This method is executed when doInBackground finishes
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	public void done() {
		try {
		    super.get();
		    es.resetHighlights();
		    
		    //if method execution, invoke the method
		    if(objectIndex >= 0 && methodIndex >= 0 && arguments != null && type == METHODEXECUTION) {
		    	es.getCodePanel().getCodeCtrl().getMainCtrl().getObjectCtrl().invokeMethod(objectIndex, methodIndex, arguments);
		    }
		    //if constructor execution, invoke the constructor
		    else if(instanceName != null && parameters != null && arguments != null && type == CONSTRUCTOREXECUTION) {
		    	es.getCodePanel().getCodeCtrl().getMainCtrl().getObjectCtrl().createObject(instanceName, parameters, arguments);		   
		    }
		    else {
		    	System.out.println("Error: Cannot invoke method nor constructor in AnimationTask.");
		    }
		   } catch (Throwable t) {
		    
		  }		
	}
}
