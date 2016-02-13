package rocks.wallenius.joop.gui;

/* 
* ToolMenu.java
* The tool menu component of the GUI with icon buttons
* 
* v1.0 
* 12/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.FlowLayout;
import java.awt.Insets;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import rocks.wallenius.joop.code.CodeCtrl;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import rocks.wallenius.joop.mvc.Observer;

public class ToolMenu extends JPanel implements ActionListener, Observer {
	
	private CodeCtrl codeCtrl;
	
	//Icon buttons used in GUI
	private JButton btnNewClass, btnLoadClass, btnSaveClass, btnCompileClass, btnConsole;
	private ImageIcon iconNew, iconLoad, iconSave, iconCompile, iconConsole;
	private final JFileChooser fileChooser;
	private FileNameExtensionFilter filter;
	
	private final static String dirPath = "usergenerated";
	
	public ToolMenu(CodeCtrl codeCtrl) {
		
		this.codeCtrl = codeCtrl;
		codeCtrl.getCodePanel().addObserver(this);
		
		fileChooser = new JFileChooser();
		filter = new FileNameExtensionFilter("Java Source Files", "java");
		fileChooser.setFileFilter(filter);
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
		fileChooser.setCurrentDirectory(dir); 
		
		//Load icon images for buttons (author: Yingjunjiu. Src: http://www.iconfinder.com/search/3/?q=iconset%3Aarzo)
		try {
			iconNew = new ImageIcon(ImageIO.read(this.getClass().getResource("/img/btn_addclass.png")));
			iconLoad = new ImageIcon(ImageIO.read(this.getClass().getResource("/img/btn_loadclass.png")));
			iconSave = new ImageIcon(ImageIO.read(this.getClass().getResource("/img/btn_saveclass.png")));
			iconCompile = new ImageIcon(ImageIO.read(this.getClass().getResource("/img/btn_compileclass.png")));
			iconConsole = new ImageIcon(ImageIO.read(this.getClass().getResource("/img/btn_console.png")));
		} catch(IOException e) {
			e.printStackTrace();
		}

		//Initialize buttons
		btnNewClass = new JButton(iconNew);
		btnLoadClass = new JButton(iconLoad);
		btnSaveClass = new JButton(iconSave);
		btnCompileClass = new JButton(iconCompile);
		btnConsole = new JButton(iconConsole);
		
		//Remove border from buttons
		btnNewClass.setBorderPainted(false);
		btnLoadClass.setBorderPainted(false);
		btnSaveClass.setBorderPainted(false);
		btnCompileClass.setBorderPainted(false);
		btnConsole.setBorderPainted(false);
		
		//Remove default background from buttons
		btnNewClass.setContentAreaFilled(false);
		btnLoadClass.setContentAreaFilled(false);
		btnSaveClass.setContentAreaFilled(false);
		btnCompileClass.setContentAreaFilled(false);
		btnConsole.setContentAreaFilled(false);
		
		//Remove margin/padding from within buttons
		btnNewClass.setMargin(new Insets(0,0,0,0));
		btnLoadClass.setMargin(new Insets(0,0,0,0));
		btnSaveClass.setMargin(new Insets(0,0,0,0));
		btnCompileClass.setMargin(new Insets(0,0,0,0));
		btnConsole.setMargin(new Insets(0,0,0,0));
		
		//Set action commands for buttons
		btnNewClass.setActionCommand("NEW_CLASS");
		btnLoadClass.setActionCommand("LOAD_CLASS");
		btnSaveClass.setActionCommand("SAVE_CLASS");
		btnCompileClass.setActionCommand("COMPILE_CLASS");
		btnConsole.setActionCommand("CONSOLE");
		
		//Set tooltips for buttons
		btnNewClass.setToolTipText("New");
		btnLoadClass.setToolTipText("Open");
		btnSaveClass.setToolTipText("Save");
		btnCompileClass.setToolTipText("Compile");
		btnConsole.setToolTipText("Console");
		
		//Add action listeners to buttons
		btnNewClass.addActionListener(this);
		btnLoadClass.addActionListener(this);
		btnSaveClass.addActionListener(this);
		btnCompileClass.addActionListener(this);
		btnConsole.addActionListener(this);
		
		//Add buttons to JPanel
		add(btnNewClass);
		add(btnLoadClass);
		add(btnSaveClass);
		add(btnConsole);
		add(btnCompileClass);
		
		//Disable save button until opened
		btnSaveClass.setEnabled(false);
		btnCompileClass.setEnabled(false);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	/*
	 * This method handles actions for the ToolMenu
	 */
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
        if (o instanceof JButton) {
        	requestFocus();
        	
        	String command = e.getActionCommand();
        	
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
        			btnCompileClass.setEnabled(true);
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
    			btnCompileClass.setEnabled(true);
    		}
    		
    		//Action handler for save class button
    		if(command.equals("SAVE_CLASS")) {
    			codeCtrl.saveCode();
    			btnSaveClass.setEnabled(false);
    		}
    		
    		//Action handler for compile class button
    		if(command.equals("COMPILE_CLASS")){
    			int r = codeCtrl.compileCode();
    			if(r == 1) {
    				btnCompileClass.setEnabled(false);
    			}
    		}
    		
    		//Action handler for console button
    		if(command.equals("CONSOLE")) {
    			codeCtrl.getMainCtrl().getMainWindow().showConsole();
    		}
        }
		
	}
	
	/*
	 * This method updates the save button when text in codePanel is changed
	 */
	public void update() {
		btnSaveClass.setEnabled(true);
		btnCompileClass.setEnabled(true);
	}
	
	public void setBtnSaveClassEnabled(boolean enabled) {
		btnSaveClass.setEnabled(enabled);
	}
	
	public void setBtnCompileEnabled(boolean enabled) {
		btnCompileClass.setEnabled(enabled);
	}
}
