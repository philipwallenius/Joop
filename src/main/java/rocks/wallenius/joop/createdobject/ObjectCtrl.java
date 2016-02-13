package rocks.wallenius.joop.createdobject;

/* 
* ObjectCtrl.java
* Controller for the CreatedObject entity and object diagram 
* 
* v1.0
* 01/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import rocks.wallenius.joop.gui.MainCtrl;
import rocks.wallenius.joop.gui.ObjectDiagram;
import rocks.wallenius.joop.mvc.Observer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.*;
import javax.swing.JOptionPane;

public class ObjectCtrl {
	
	private MainCtrl mainCtrl;
	private ObjectArrayList<CreatedObject> createdObjects;
	private ObjectDiagram objectDiagram;
	
	/*
	 * Constructor
	 */
	public ObjectCtrl() {
		mainCtrl = null;
		createdObjects = new ObjectArrayList<CreatedObject>();
		objectDiagram = new ObjectDiagram(this, createdObjects);
	}
	
	/*
	 * Accessors
	 */
	public ObjectArrayList<CreatedObject> getCreatedObjects() { return createdObjects; }
	
	/*
	 * This method creates a new object using constructor with given parameters
	 */
	public void createObject(String reference, Class[] parameters, Object[] arguments) {	
		
		//if class is loaded, get current class and instantiate object
		if(mainCtrl.getClassCtrl().isClassLoaded()) {
			
			Class loadedClass = mainCtrl.getClassCtrl().getLoadedClass().getLoadedClass();
			
			try {
				//pipe output to be displayed in console
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			    PrintStream printStream = new PrintStream(out);
			    PrintStream default_out = System.out;
			    
			    //change output to printStream
			    System.setOut(printStream);	
				
				//get the class's constructor with specified parameters
				//and instantiate an object with it
				Constructor constructor = loadedClass.getConstructor(parameters);
				
				//when adding new objects, remove all previous highlights
				mainCtrl.getMainWindow().removeObjectHighlights();
				
				Object object = constructor.newInstance(arguments);
				CreatedObject createdObject = new CreatedObject(reference, object);
				createdObject.addObserver(objectDiagram);
				
				//add instantiated object to array list of created objects
				createdObjects.addCreatedObject(createdObject);
				
				//flush and change back to old output
			    System.out.flush();
			    System.setOut(default_out);
			    
			    //add output string to console
				mainCtrl.getMainWindow().getConsole().add(out.toString());
				

			}
			catch(NoSuchMethodException e) {
				System.out.println("Error. NoSuchMethodException: " + e.getMessage());
			}
			catch(IllegalAccessException e) {
				System.out.println("Error. IllegalAccessException: " + e.getMessage());
			}
			catch(InstantiationException e)  {
				System.out.println("Error. InstantiationException: " + e.getMessage());
			}
			catch(InvocationTargetException e) {
				System.out.println("Error. InvocationTargetException: " + e.getMessage());
			}
		}
		
	}
	
	/*
	 * This method invokes the animateMethodExecution in the CodePanel which animates the simulation of execution of a method
	 */
	public void simulateMethodExecution(int objectIndex, int methodIndex, Object[] arguments) {
		CreatedObject co = (CreatedObject)createdObjects.get(objectIndex);
		Method[] methods = co.getMethods();
		
		Method method = methods[methodIndex];
		
		//simulate execution by highlighting lines in CodePanel
		mainCtrl.getMainWindow().getCodePanel().animateMethodExecution(method.toString(), method.getName(), objectIndex, methodIndex, arguments);
	}
	
	/*
	 * This method invokes the animateConstructorExecution in the CodePanel which animates the simulation of execution of a method
	 */
	public void simulateConstructorExecution(String instanceName, Class[] parameters, Object[] arguments) {
		Class loadedClass = mainCtrl.getClassCtrl().getLoadedClass().getLoadedClass();
		try {
			Constructor constructor = loadedClass.getConstructor(parameters);
			
			mainCtrl.getMainWindow().getCodePanel().animateConstructorExecution(constructor.toString(), constructor.getName(), instanceName, parameters, arguments);
		}
		catch(NoSuchMethodException e) {
			System.out.println("Error. NoSuchMethodException: " + e.getMessage());
		}
		
	}
	
	/*
	 * This method invokes specified method
	 */
	public void invokeMethod(int objectIndex, int methodIndex, Object[] arguments) {
		
		CreatedObject co = (CreatedObject)createdObjects.get(objectIndex);
		Method[] methods = co.getMethods();
		
		Method method = methods[methodIndex];
		
		//simulate execution by highlighting lines in CodePanel
		//mainCtrl.getMainWindow().getCodePanel().animateMethodExecution(method.toString(), method.getName());
		
		//highlight changed object
		mainCtrl.getMainWindow().highlightObject(objectIndex);
		
		try {
			
			//pipe output to be displayed in console
			ByteArrayOutputStream out = new ByteArrayOutputStream();
		    PrintStream printStream = new PrintStream(out);
		    PrintStream default_out = System.out;
		    
		    //change output to printStream
		    System.setOut(printStream);
			
		    //invoke method
			Object returnValue = method.invoke(co.getCreatedObject(), arguments);
			if(returnValue != null) {
				JOptionPane.showMessageDialog(null, "The method returned:\n" + returnValue.toString(), "Returned Value", JOptionPane.INFORMATION_MESSAGE);
			}
		    
			//flush and change back to old output
		    System.out.flush();
		    System.setOut(default_out);
		    
		    //add output string to console
			mainCtrl.getMainWindow().getConsole().add(out.toString());
	
		}
		catch(InvocationTargetException e) {
			System.out.println("InvocationTargetException: " + e.getMessage());
		}
		catch(IllegalAccessException e) {
			System.out.println("IllegalAccessException: " + e.getMessage());
		}
		
		//notify observers
		co.setChanged(true);
		co.notifyObservers();
	}
	
	/*
	 * This method resets the objects
	 */
	public void resetCreatedObjects() {
		createdObjects.clear();
		objectDiagram.update();
	}
	
	/*
	 * This method passes the main controller
	 */
	public void setMainCtrl(MainCtrl mainCtrl) {
		this.mainCtrl = mainCtrl;
	}
	
	/*
	 * This method returns the ClassDiagram
	 */
	public ObjectDiagram getObjectDiagram() { return objectDiagram; }
	
	/*
	 * This method checks if a instance name already exists
	 */
	public boolean instanceNameExists(String instanceName) {
		for(int i = 0; i < createdObjects.size(); i++) {		
			CreatedObject co = (CreatedObject)createdObjects.get(i);
			String ref = co.getReference().trim();
			if(ref.equals(instanceName.trim())) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * This method removes an object
	 */
	public void removeObject(String instanceName) {
		for(int i = 0; i < createdObjects.size(); i++) {		
			CreatedObject co = (CreatedObject)createdObjects.get(i);
			String ref = co.getReference().trim();
			if(ref.equals(instanceName.trim())) {
				createdObjects.remove(i);
			}
		}		
	}
}
