package rocks.wallenius.joop.gui;

/* 
* MainCtrl.java
* The main controller is mainly used to access other controllers
* 
* v1.0 
* 25/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import rocks.wallenius.joop.code.*;
import rocks.wallenius.joop.loadedclass.*;
import rocks.wallenius.joop.createdobject.*;

public class MainCtrl {
	
	//holds singleton instance
	private static MainCtrl mainCtrl;
	
	private CodeCtrl codeCtrl;
	private ClassCtrl classCtrl;
	private ObjectCtrl objectCtrl;
	private MainWindow mainWindow;
	
	//private constructor
	private MainCtrl() {		
		mainCtrl = null;
		codeCtrl = null;
		classCtrl = null;
		objectCtrl = null;
	}
	
	/*
	 * Accessors
	 */
	public CodeCtrl getCodeCtrl() {
		return codeCtrl;
	}
	
	public ClassCtrl getClassCtrl() {
		return classCtrl;
	}
	
	public ObjectCtrl getObjectCtrl() {
		return objectCtrl;
	}
	
	public MainWindow getMainWindow() { return mainWindow; }
	
	//Singleton accessor method
	public static MainCtrl getInstance() {
		
		if(mainCtrl == null) {
			mainCtrl = new MainCtrl();
		}
		
		return mainCtrl;
	}
	/*
	 * Mutators
	 */
	public void setCodeCtrl(CodeCtrl codeCtrl) {
		this.codeCtrl = codeCtrl;
	}
	
	public void setClassCtrl(ClassCtrl classCtrl) {
		this.classCtrl = classCtrl;
	}
	
	public void setObjectCtrl(ObjectCtrl objectCtrl) {
		this.objectCtrl = objectCtrl;
	}
	
	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	/*
	 * This method checks if any method has been loaded previously
	 */
	
	public boolean isClassLoaded() {
		return classCtrl.isClassLoaded();
	}
	
	/*
	 * This method resets any loaded class (removes from class diagram, object diagram etc)
	 */
	public void reset() {
		classCtrl.resetLoadedClass();
		objectCtrl.resetCreatedObjects();
	}
}
