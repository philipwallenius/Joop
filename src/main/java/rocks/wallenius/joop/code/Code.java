package rocks.wallenius.joop.code;

/* 
* Code.java
* Entity which holds a class's name, code and file. Acts as subject for CodePanel observer.  
* 
* v1.1 
* 13/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import org.apache.commons.io.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import rocks.wallenius.joop.mvc.Observable;
import rocks.wallenius.joop.mvc.Observer;

public class Code implements Observable, Observer {
	
	private ArrayList<Observer> observers;
	private String name, code;
	private File file;
	private boolean changed, saved, compiled, created;
	
	/*
	 * Constructor
	 */
	public Code() {
		observers = new ArrayList<Observer>();
		name = "";
		code = "";
		file = null;
		changed = false;	
		saved = true;
		compiled = false;
		created = false;
	}
	
	/*
	 * Accessors
	 */
	public String getName() { return name; }
	public String getCode() { return code; }
	public File getFile() { return file; }
	public boolean isSaved() { return saved; } 
	public boolean isChanged() { return changed; }
	public boolean isCreated() { return created; }
	public boolean isCompiled() { return compiled; }
	
	/*
	 * Mutators
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCode(String code) { 
		this.code = code;
		setCreated(true);
		
		//notify observers that the code has been changed
		setChanged(true);
		notifyObservers();
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setSaved(boolean saved) {
		this.saved = saved;
		setCompiled(false);
	}
	
	public void setCreated(boolean created) {
		this.created = created;
	}
	
	public void setCompiled(boolean compiled) {
		this.compiled = compiled;
	}
	
	/*
	 * Method to save the code to the .java source file
	 */
	public void saveToFile() throws IOException {
		
		//Create a BufferedWriter and save code to the file
		BufferedWriter bf = null;
		
		try {
			bf = new BufferedWriter(new FileWriter(file));		
			bf.write(code);
		}
		finally {
			//close buffered writer
			if(bf != null) {
				bf.flush();
				bf.close();
				saved = true;
			}
		}
	}
	
	/*
	 * Method to load code form the .java source file
	 */
	public void loadFromFile() throws IOException {
		
		//FileUtils is a String manipulation package from the Apache Commons project. URL: http://commons.apache.org/
		String src = FileUtils.readFileToString(file);
		setCode(src);
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
	
	public void update() {
		saved = false;
	}
}