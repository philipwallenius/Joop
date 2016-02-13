package rocks.wallenius.joop.loadedclass;

/* 
* LoadedClassLoader.java
* Custom ClassLoader which loads a class based on url to dir and name.
* This class enables classes to be dynamically reloaded during runtime.
* 
* v1.0 
* 20/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LoadedClassLoader extends ClassLoader {
    
	/*
	 * Constructor
	 */
	public LoadedClassLoader(){
		
		//initialize parent class loader
		super(LoadedClassLoader.class.getClassLoader());
    }
    
	/*
	 * This method loads a class based on given URL path and name
	 */
    public Class<?> loadClass(String dir, String className) {
    	URL[] urls = null;

    	try {
    		//Create url to class file
			URL url = new URL("file:" + dir);
			System.out.println("dir="+dir+", URL="+url.getPath());
			urls = new URL[] { url };
	    } catch (MalformedURLException e) {
	    	System.out.println("MalformedURLException: " + e.getMessage());
	    }
	    
	    //Create a new URLClassLoader with url to directory
	    ClassLoader classLoader = new URLClassLoader(urls);

	    //Load the class and return it
	    Class loadedClass = null;
	    try {
	    	loadedClass = classLoader.loadClass(className);
	    } catch (ClassNotFoundException e) {
	    	System.out.println("ClassNotFoundException: " + e.getMessage());
	    }
	    return loadedClass;
    }
}