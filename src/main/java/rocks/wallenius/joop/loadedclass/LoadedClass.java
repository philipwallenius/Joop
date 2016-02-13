package rocks.wallenius.joop.loadedclass;

/* 
* LoadedClass.java
* Entity which holds a class's class reference, fields, constructors and methods etc.
* 
* v1.0 
* 23/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.util.ArrayList;
import rocks.wallenius.joop.mvc.Observable;
import rocks.wallenius.joop.mvc.Observer;

import java.lang.reflect.*;

public class LoadedClass implements Observable {

	private ArrayList<Observer> observers;
	private String name;
	private Class loadedClass;
	private boolean changed, loaded;
	private Field[] fields;
	private Constructor[] constructors;
	private Method[] methods;
	

	
	/*
	 * Constructor
	 */
	public LoadedClass() {
		observers = new ArrayList<Observer>();
		name = "";
		loadedClass = null;
		changed = false;
		loaded = false;
		fields = null;
		constructors = null;
		methods = null;
		
	}
	
	/*
	 * Accessors
	 */
	public String getName() { return name; }
	public Class getLoadedClass() { return loadedClass; }
	public Field[] getFields() { return fields; }
	public Constructor[] getConstructors() { return constructors; }
	public Method[] getMethods() { return methods; }
	public boolean isLoaded() { return loaded; }
	
	/*
	 * Mutators
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public void setClass(Class loadedClass) {
		this.loadedClass = loadedClass;
	}
	
	/*
	 * This method initializes the LoadedClass object
	 */
	public void init(String dir, String name) throws ClassNotFoundException {
		this.name = name;
		loadClass(dir);
		
		extractFields();
		extractConstructors();
		extractMethods();
		
		setChanged(true);
		notifyObservers();
		setLoaded(true);
	}
	
	/*
	 * This method loads the dynamically created class
	 */
	public void loadClass(String dir) throws ClassNotFoundException {
		loadedClass = null;
		if(fields != null) {
			for(int i = 0; i < fields.length; i++) {
				fields[i] = null;
			}
			for(int i = 0; i < constructors.length; i++) {
				constructors[i] = null;
			}
			for(int i = 0; i < methods.length; i++) {
				methods[i] = null;
			}
		}
		
	    LoadedClassLoader classLoader = new LoadedClassLoader();
	    Class reloadedClass = classLoader.loadClass(dir, name);
	    setClass(reloadedClass);
	}
	
	/*
	 * This method extracts the fields from the class
	 */
	public void extractFields() {
		 fields = loadedClass.getDeclaredFields();
	}
	
	/*
	 * This method extracts the constructors from the class
	 */
	public void extractConstructors() {
		constructors = loadedClass.getConstructors();
	}
	
	/*
	 * This method extracts the methods from the class
	 */
	public void extractMethods() {
		methods = loadedClass.getDeclaredMethods();
	}
	
	/*
	 * This methods returns all constructors in String form
	 */
	public String[] getConstructorNames() {
		
		String[] c = new String[constructors.length];
		
		for(int i = 0; i < c.length; i++) {
    		Constructor constructor = constructors[i];
			
			//get modifiers of constructor and replace them with symbols
			String constructorModifiers = Modifier.toString(constructor.getModifiers());				
			if(constructorModifiers != "") {
				constructorModifiers = changeModifiers(constructorModifiers);
			}
			
			//get constructor's name and strip package name from it
			String constructorName = constructor.getName();
			int dotpos = constructorName.lastIndexOf('.');
			constructorName = constructorName.substring(dotpos + 1);
			
			//get parameters of constructor
			String constructorParameters = "";
			Class[] parameterTypes = constructor.getParameterTypes();
			
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
					constructorParameters += parameter;
					
					//add comma if not last parameter
					if(n != (parameterTypes.length-1)) {
						constructorParameters += ", ";
					}
				}
			}
			
			c[i] = constructorModifiers + constructorName + "(" + constructorParameters + ")";
		}
		
		return c;
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
	
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	
	/*
	 * This method resets the loaded class (for example if a loaded class is changed)
	 */
	public void reset() {
		name = "";
		loadedClass = null;
		changed = false;
		for(int i = 0; i < fields.length; i++) {
			fields[i] = null;
		}
		fields = null;
		for(int i = 0; i < constructors.length; i++) {
			constructors[i] = null;
		}
		constructors = null;
		for(int i = 0; i < methods.length; i++) {
			methods[i] = null;
		}
		methods = null;
		
		setChanged(true);
		notifyObservers();
		setLoaded(false);
	}
	
	/*
	 * This method replaces modifiers with chars -, +, #
	 */
	public String changeModifiers(String mods) {
		mods = mods.replace("private","-");
		mods = mods.replace("public","+");
		mods = mods.replace("protected","#");
		
		if(mods.split(" ").length > 1) {
			mods += " ";
		}
		
		return mods;
	}
}
