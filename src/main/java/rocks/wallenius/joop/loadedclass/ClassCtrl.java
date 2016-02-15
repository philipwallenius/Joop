package rocks.wallenius.joop.loadedclass;

/* 
* ClassCtrl.java
* View for the LoadedClass entity and class diagram
* 
* v1.0
* 23/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import rocks.wallenius.joop.gui.MainCtrl;
import rocks.wallenius.joop.gui.ClassDiagram;

public class ClassCtrl {
	
	private MainCtrl mainCtrl;
	private LoadedClass loadedClass;
	private ClassDiagram classDiagram;
	
	/*
	 * Constructor
	 */
	public ClassCtrl(LoadedClass loadedClass) {
		mainCtrl = null;
		this.loadedClass = loadedClass;
		classDiagram = new ClassDiagram(this, loadedClass);
	}
	
	/*
	 * Accessors
	 */
	public ClassDiagram getClassDiagram() { return classDiagram; }
	public boolean isClassLoaded() { return loadedClass.isLoaded(); }
	public LoadedClass getLoadedClass() { return loadedClass; }
	public MainCtrl getMainCtrl() { return mainCtrl; }
	
	/*
	 * Mutators
	 */
	public void setMainCtrl(MainCtrl mainCtrl) {
		this.mainCtrl = mainCtrl;
	}
	
	/*
	 * This method initializes a loadedClass
	 */
	public void createLoadedClass(String dir, String className) {
		try {
			loadedClass.init(dir, className);
		}
		catch(ClassNotFoundException e) {
			System.out.println("ClassNotFoundException: " + e.getMessage());
		}
	}
	
	/*
	 * This method resets the loaded class
	 */
	public void resetLoadedClass() { 
		loadedClass.reset();
	}
}
