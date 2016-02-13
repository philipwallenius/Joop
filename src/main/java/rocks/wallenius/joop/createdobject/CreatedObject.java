package rocks.wallenius.joop.createdobject;

/* 
* CreatedObject.java
* Entity which holds a object's reference, fields and methods etc.
* 
* v1.0 
* 01/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.util.ArrayList;
import rocks.wallenius.joop.mvc.Observable;
import rocks.wallenius.joop.mvc.Observer;
import java.lang.reflect.*;

public class CreatedObject implements Observable {

	private ArrayList<Observer> observers;
	private String reference;
	private Object object;
	private boolean changed;
	private boolean highlighted;
	private Field[] fields;
	private Method[] methods;
	
	private int x, y, width, height;
	private final static int CIRCLE_INIT_HEIGHT = 170;
	private final static int CIRCLE_INIT_WIDTH = 170;
	
	/*
	 * Constructor
	 */
	public CreatedObject(String reference, Object object) {
		observers = new ArrayList<Observer>();
		this.reference = reference;
		this.object = object;
		
		//drawing attributes of circle shape
		x = 0;
		y = 0;
		height = CIRCLE_INIT_HEIGHT;
		width = CIRCLE_INIT_WIDTH;
		
		changed = false;
		highlighted = true;
		extractFields();
		extractMethods();
	}
	
	/*
	 * Accessors
	 */
	public String getReference() { return reference; }
	public Object getCreatedObject() { return object; }
	public Field[] getFields() { return fields; }
	public Method[] getMethods() { return methods; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getHeight() { return height; }
	public int getWidth() { return width; }
	public boolean isHighlighted() { return highlighted; }
	
	/*
	 * Mutators
	 */
	public void setReference(String reference) { this.reference = reference; }
	public void setObject(Object object) { this.object = object; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setHeight(int height) { this.height = height; }
	public void setWidth(int width) { this.width = width; }
	public void setHighlighted(boolean b) { highlighted = b; } 

	/*
	 * This method extracts all fields from an object
	 */
	public void extractFields() {
		fields = object.getClass().getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
		}
	}
	
	/*
	 * This method extracts all methods from an object
	 */
	public void extractMethods() {
		methods = object.getClass().getDeclaredMethods();
		for(int i = 0; i < methods.length; i++) {
			methods[i].setAccessible(true);
		}
	}
	
	/*
	 * This methods checks if coordinates are inside the object
	 */
	public boolean contains(int click_x, int click_y) {
		
		//Check if distance from center of circle to click x/y is less or equals to radius
		//using pythagoras theorem
		int radius = width / 2;
		int cent_x = x + (width/2);
		int cent_y = y + (height/2);

		double distance = Math.pow((cent_x - click_x), 2) + (Math.pow((cent_y - click_y), 2));
		double radius_sq = Math.pow(radius, 2);
		
		return distance <= radius_sq;
	}
	
	/*
	 * This method returns an array of all methods of this object
	 */
	public String[] getMethodNames() {
		
		String[] m = new String[methods.length];
		
		for(int i = 0; i < m.length; i++) {
			Method method = methods[i];
			
			//get modifiers of method and replace them with symbols
			String methodModifiers = Modifier.toString(method.getModifiers());				
			if(methodModifiers != "") {
				methodModifiers = changeModifiers(methodModifiers);
			}
			
			String returnType = method.getReturnType().toString();
			
			//if package name inside, strip it out
			if(returnType.contains("")) {
				int dotindex = returnType.lastIndexOf('.');
				returnType = returnType.substring(dotindex + 1);
			}				
			
			String methodName = method.getName();

			//get parameters of method
			String methodParameters = "";
			Class[] parameterTypes = method.getParameterTypes();
			
			if(parameterTypes.length != 0) {
				
				//format each parameter
				for(int n = 0; n < parameterTypes.length; n++) {
					String parameter = parameterTypes[n].getName();
					
					//if package name inside, strip it out
					if(parameter.contains("")) {
						int dotindex = parameter.lastIndexOf('.');
						parameter = parameter.substring(dotindex + 1);
					}
					
					//add parameter to string
					methodParameters += parameter;
					
					//add comma if not last parameter
					if(n != (parameterTypes.length-1)) {
						methodParameters += ", ";
					}
				}
			}				
			
			m[i] = methodModifiers + methodName + "(" + methodParameters + ") : " + returnType;
						
		}
		return m;
	}
	
	/*
	 * This method resets the loaded class (for example if a class is changed, created or loaded)
	 */
	public void reset() {
		reference = "";
		object = null;
		changed = false;
		fields = null;
		methods = null;
		setChanged(true);
		notifyObservers();
	}

	/*
	 * This method replaces modifiers with chars -, +, #
	 */
	public String changeModifiers(String mods) {
		mods = mods.replace("private","-");
		mods = mods.replace("public","+");
		mods = mods.replace("protected","#");
		return mods;
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
