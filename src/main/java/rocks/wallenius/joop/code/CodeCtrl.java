package rocks.wallenius.joop.code;

/* 
* CodeCtrl.java
* Controller for the Code entity object.
* 
* v1.1 
* 13/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.swing.text.BadLocationException;
import rocks.wallenius.joop.gui.MainCtrl;
import javax.swing.JOptionPane;

public class CodeCtrl {
	
	private MainCtrl mainCtrl;
	private Code code;
	private CodePanel codePanel;
	
	/*
	 * Constructor
	 */
	public CodeCtrl(Code code) {
		mainCtrl = null;
		this.code = code;
		codePanel = new CodePanel(this, code);
		codePanel.addObserver(code);
	}
	
	/*
	 * This method creates a new Code object
	 */
	public void createCode(File file) {
		
		String className = file.getName();
		className = className.substring(0, className.length() - 5);
		
		//Template code for new code objects
		String initCode = "public class " + className + "{\n\n    //variable declarations\n\n    //default constructor\n    public " + className + "(){\n\n    }\n\n    //method declarations\n\n}";
		
		//Set code model state
		code.setName(className);
		code.setCode(initCode);
		code.setFile(file);
			
		//Write template code to new file
		try {
			code.saveToFile();
		}
		catch(FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		}
		catch(IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
		
		//check if class has been loaded previously and reset if so
		if(mainCtrl.isClassLoaded()) {
			mainCtrl.reset();
		}
	}
	
	/*
	 * This method loads code from an existing .java file
	 */
	public void loadCode(File file) {
		String fileName = file.getName();
		fileName = file.getName().substring(0, fileName.length()-5);
		code.setName(fileName);
		code.setFile(file);
		
		try {
			code.loadFromFile();
		}
		catch(FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		}
		catch(IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
		//check if class has been loaded previously
		if(mainCtrl.isClassLoaded()) {
			mainCtrl.reset();
		}		
		
	}
	
	/*
	 * This method saves the code to the .java file
	 */
	public void saveCode() {
		if(code.getFile() != null) {
			try {
				code.setCode(codePanel.getText());
				code.saveToFile();
				mainCtrl.getMainWindow().getToolMenu().setBtnSaveClassEnabled(false);
				mainCtrl.getMainWindow().getToolMenu().setBtnCompileEnabled(true);
			}
			catch(FileNotFoundException e) {
				System.out.println("FileNotFoundException: " + e.getMessage());
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
			catch(BadLocationException e) {
				System.out.println("BadLocationException: " + e.getMessage());
			}
		}
		
		//check if class has been loaded previously and reset if so
		if(mainCtrl.isClassLoaded()) {
			mainCtrl.reset();
		}
		
	}
	
	/*
	 * This method compiles the code to create a .class object and then invokes a ClassCtrl method
	 */
	public int compileCode() {
		
		//check if code is saved before compiling
		if(!code.isSaved()) {
			
			int ans = JOptionPane.showConfirmDialog(null, "Do you want to save the changes made to class?", "Save Class", JOptionPane.INFORMATION_MESSAGE);
			
			if(ans == JOptionPane.YES_OPTION) {
				//save code and continue compiling
				saveCode();
			}
			else if(ans == JOptionPane.NO_OPTION) {
				//dont save but continue compiling
			}
			else{
				//cancel compilation
				return 0;
			}
		}
		
		//compile file
		if(code.getFile() != null) {
			
			String fileName = code.getFile().getName();
			
			//Get absolute path to the java file
			String dir = code.getFile().getAbsolutePath(); 
			dir = dir.substring(0, (dir.length()-fileName.length()));
			
			Runtime run = Runtime.getRuntime();		
			
			//Get last modified time of the class file
			//to compare after compilation if it has been
			//modified successfully or if some error occurred.
			
			//get the file's .class file path
			File path = code.getFile();
			String classFilePath = path.toString();
			int indexOfJava = classFilePath.indexOf(".java");
			classFilePath = classFilePath.substring(0, indexOfJava) + ".class";
			
			//get the time the class file was last modified
			File file = new File(classFilePath);
			Date preCompileDate = new Date(file.lastModified());
			
			//set busy cursor while loading
			getMainCtrl().getMainWindow().setLoading(true);
			
			//compile class
			try {
				
				//this method might have some overhead, causing ClassNotFound error in LoadedClass.java
				//User's java path must be defined
                run.exec(new String[] { "javac", dir + fileName });

				//delay due to compilation overlay
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ie) {
					System.out.println("InterruptedException: " + ie.getMessage());
				}
				
				
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
			
			//check that the compilation succeeded.	
			//get last modified date and compare to previous if it has changed, then compilation succeeded
			Date postCompileDate = new Date(file.lastModified());
			
			//set finished loading i.e. default
			getMainCtrl().getMainWindow().setLoading(false);
			
			if(preCompileDate.getTime() == postCompileDate.getTime()) {
				JOptionPane.showMessageDialog(null, "Error: The class could not be compiled. Please check for syntax errors and try again.", "Compilation Failed", JOptionPane.ERROR_MESSAGE);
				return 0;
			}
			else {
				//Call the ClassCtrl to create a LoadedClass object and draw it on class diagram
				mainCtrl.getClassCtrl().createLoadedClass(dir, code.getName());
				mainCtrl.getMainWindow().getToolMenu().setBtnSaveClassEnabled(false);
				code.setCompiled(true);
				
				//Color fields, constructors and methods
				codePanel.colorSyntax();
			}
			return 1;
		}
		return 0;
	}
	
	/*
	 * This method passes the main controller
	 */
	public void setMainCtrl(MainCtrl mainCtrl) {
		this.mainCtrl = mainCtrl;
	}
	
	/*
	 * Method returns CodePanel view
	 */
	public CodePanel getCodePanel() { return codePanel; }
	public Code getCode() { return code; }
	public MainCtrl getMainCtrl() { return mainCtrl; }
}
