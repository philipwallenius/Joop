package rocks.wallenius.joop.oldgui;

/* 
* MainMenu.java
* The main top menu component of the GUI with text buttons (File, Edit, etc...)
* 
* v1.0 
* 12/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import rocks.wallenius.joop.code.CodeCtrl;

public class MainMenu extends JMenuBar implements ActionListener {
	
	private JMenu fileMenu, aboutMenu;
	private JMenuItem itemNewClass, itemOpenClass, itemSaveClass, itemExit, itemAbout;
	private MainWindow mainWindow;
	private final static String dirPath = "usergenerated";
	private CodeCtrl codeCtrl;
	private final JFileChooser fileChooser;
	private FileNameExtensionFilter filter;
	
	//Main program icon
	private ImageIcon programIcon;
	
	public MainMenu(MainWindow mainWindow, CodeCtrl codeCtrl) {
		this.mainWindow = mainWindow;
		this.codeCtrl = codeCtrl;
		
		//Main program icon
		programIcon = new ImageIcon("img\\icon9.png");
		
		fileChooser = new JFileChooser();
		filter = new FileNameExtensionFilter("Java Source Files", "java");
		fileChooser.setFileFilter(filter);
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
		fileChooser.setCurrentDirectory(dir); 
		
		//File menu
		fileMenu = new JMenu("File");
		itemNewClass = new JMenuItem("New Class");
		itemOpenClass = new JMenuItem("Open Class");
		itemSaveClass = new JMenuItem("Save Class");
		itemExit = new JMenuItem("Exit");
		
		aboutMenu = new JMenu("About");
		itemAbout = new JMenuItem("About");

		//Add menu items to fileMenu
		fileMenu.add(itemNewClass);
		fileMenu.add(itemOpenClass);
		fileMenu.add(itemSaveClass);
		fileMenu.add(itemExit);
		
		//Add menu items to aboutMenu
		aboutMenu.add(itemAbout);	
		
		//Set action commands for buttons
		itemNewClass.setActionCommand("NEW_CLASS");
		itemOpenClass.setActionCommand("LOAD_CLASS");
		itemSaveClass.setActionCommand("SAVE_CLASS");
		itemExit.setActionCommand("EXIT");
		itemAbout.setActionCommand("ABOUT");
		
		//Add action listeners to buttons
		itemNewClass.addActionListener(this);
		itemOpenClass.addActionListener(this);
		itemSaveClass.addActionListener(this);
		itemExit.addActionListener(this);
		itemAbout.addActionListener(this);
		
		//Add menu components to menu bar
		add(fileMenu);	
		add(aboutMenu);
	}
	
	/*
	 * This method handles actions for the MainMenu
	 */
	public void actionPerformed(ActionEvent ae) {
    	
		String command = ae.getActionCommand();
    	
    	//Action handler for new class button
		if(command.equals("NEW_CLASS")) {
			File file = null;
			
			//User chooses file
			int val = fileChooser.showSaveDialog(this);		
			if(val == JFileChooser.APPROVE_OPTION) {
				
				file = fileChooser.getSelectedFile();
				String fileName = file.getName();
				
				//Check if file name ends with .java. If not, add it.
				if(fileName.length() < 6 || !fileName.substring(fileName.length()-5).equals(".java")) {
					File renamedFile = new File(file.getAbsolutePath() + ".java");
					file = renamedFile;
				}
				
				//Create code object and enable compile button
    			codeCtrl.createCode(file);
    			mainWindow.getToolMenu().setBtnCompileEnabled(true);
			} 
		}
		
		//Action handler for load class button
		if(command.equals("LOAD_CLASS")) {
			File file;
			int val = fileChooser.showOpenDialog(this);
			if (val == JFileChooser.APPROVE_OPTION) {
	        	file = fileChooser.getSelectedFile();
			} else {
				return;
			}
			if(file != null) {
				codeCtrl.loadCode(file);
			}
			else {
		    	throw new IllegalArgumentException("filename is null");
			}
			mainWindow.getToolMenu().setBtnCompileEnabled(true);
		}
		
		//Action handler for save class button
		if(command.equals("SAVE_CLASS")) {
			codeCtrl.saveCode();
		}
		
    	//Action handler for new class button
		if(command.equals("EXIT")) {
			if(!codeCtrl.getCode().isSaved()) {
				int ans = JOptionPane.showConfirmDialog(null, "Do you want to save the changes made to class before exit?", "Save Class", JOptionPane.INFORMATION_MESSAGE);
				
				if(ans == JOptionPane.YES_OPTION) {
					//save code
					codeCtrl.saveCode();
				}
				else if(ans == JOptionPane.NO_OPTION) {
					//dont save
				}
				else{
					//cancel compilation
					return;
				}
			}
			System.exit(0);
		}
		
		if(command.equals("ABOUT")) {
			mainWindow.showAbout();
		}

	}
}
