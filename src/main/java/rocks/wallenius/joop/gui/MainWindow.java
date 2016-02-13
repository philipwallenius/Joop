package rocks.wallenius.joop.gui;

/* 
* MainFrame.java
* Creates the GUI of the main application window of the JOOP Learning System
* 
* v1.0 
* 12/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.*;  

import javax.swing.*; 
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import rocks.wallenius.joop.code.*;
import rocks.wallenius.joop.loadedclass.*;
import rocks.wallenius.joop.createdobject.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.reflect.*;
import java.util.regex.Pattern;

public class MainWindow extends JFrame {
	
	/*
	 * Declare components of GUI
	 */
	private JPanel contentPanel, workPanel, viewsPanel;
	
	//Main program icon
	private ImageIcon programIcon;
	
	//Border used in GUI
	private Border loweredEtched;
	
	//Menus
	private MainMenu mainMenu;
	private ToolMenu toolMenu;
	
	//JTextPanel which holds the code
	private CodeCtrl codeCtrl;
	private CodePanel codePanel;
	
	//ClassDiagram
	private ClassCtrl classCtrl;
	private ClassDiagram classDiagram;
	private JScrollPane classScrollPane;
	
	//ObjectDiagram
	private ObjectCtrl objectCtrl;
	private ObjectDiagram objectDiagram;
	private JScrollPane objectScrollPane;

	private JPopupMenu classPopupMenu, objectPopupMenu;
	private Console console;
	private About about;
	
	/*
	 * Constructors
	 */
	public MainWindow(CodeCtrl codeCtrl, CodePanel codePanel, ClassCtrl classCtrl, ClassDiagram classDiagram, ObjectCtrl objectCtrl, ObjectDiagram objectDiagram) {
		
		super("JOOP Learning System");
		
		this.codeCtrl = codeCtrl;
		this.codePanel = codePanel;
		this.classCtrl = classCtrl;
		this.classDiagram = classDiagram;
		this.objectCtrl = objectCtrl;
		this.objectDiagram = objectDiagram;
		
		//initialize console window
		console = new Console();
		about = new About();
		
		//Create etched border (used on class and object diagrams)
		loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);	
		
		//Main program icon
		programIcon = new ImageIcon("img\\icon9.png");
		
		//Instantiate content JPanel which holds all the other panels
		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		//Class popup menu right-click
		classPopupMenu = new JPopupMenu();
		
	    //Object popup menu right-click
	    objectPopupMenu = new JPopupMenu();
	    
	    //Create MouseListener and add listeners to components
	    MouseListener popupListener = new PopupMenuListener();
	    classDiagram.addMouseListener(popupListener);
	    objectDiagram.addMouseListener(popupListener);
	    
		//Create top main menu
		mainMenu = new MainMenu(this, codeCtrl);
		setJMenuBar(mainMenu);
		
		//Create top tool menu
		toolMenu = new ToolMenu(codeCtrl);

		//Instantiate work area JPanel
		workPanel = new JPanel(new GridLayout(1,2));
		
		//Instantiate code and views area JPanels
		viewsPanel = new JPanel(new GridLayout(2,1));
		
		//classDiagram.setBorder(loweredEtched);
		//objectDiagram.setBorder(loweredEtched);
		
		//class diagram
		objectDiagram.setPreferredSize(new Dimension(600, 250));
		classDiagram.setPreferredSize(new Dimension(600, 250));
		classScrollPane = new JScrollPane(classDiagram);
		objectScrollPane = new JScrollPane(objectDiagram);
		
		//Add class and objects views to viewsPanel JPanel
		viewsPanel.add(classScrollPane);
		viewsPanel.add(objectScrollPane);
		
		//Add JPanels to JFrame
		contentPanel.add(workPanel);
		contentPanel.add(toolMenu, BorderLayout.NORTH);
		workPanel.add(codePanel, BorderLayout.WEST);
		workPanel.add(viewsPanel, BorderLayout.EAST);
		
		//add content panel to JFrame
		add(contentPanel);
		
		//set JFrame icon
		setIconImage(programIcon.getImage());
	}
	
	public ToolMenu getToolMenu() { return toolMenu; }
	public CodePanel getCodePanel() { return codePanel; }
	public Console getConsole() { return console; }
	
	/*
	 * This method brings up the console window
	 */
	public void showConsole() {
		console.setVisible(true);
	}
	
	/*
	 * This method brings up the about window
	 */
	public void showAbout() {
		about.setVisible(true);
	}	
	
	/*
	 * This method sets window status as loading
	 */
	public void setLoading(boolean b) {
		if(b == true) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		if(b == false) {
			setCursor(Cursor.getDefaultCursor());
		}
	}
	
	/*
	 * This method highlights and object with specified index
	 */
	public void highlightObject(int index) {
    	ObjectArrayList<CreatedObject> createdObjects = objectCtrl.getCreatedObjects();

		CreatedObject co = (CreatedObject)createdObjects.get(index);
		removeObjectHighlights();
		co.setHighlighted(true);
		objectDiagram.repaint();
		
	}
	
	/*
	 * This method removes all object highlights
	 */
	public void removeObjectHighlights() {
    	//remove object highlights every time class diagram is clicked
    	ObjectArrayList<CreatedObject> createdObjects = objectCtrl.getCreatedObjects();
		for(int i = 0; i < createdObjects.size(); i++) {
			CreatedObject co = (CreatedObject)createdObjects.get(i);
			co.setHighlighted(false);
			objectDiagram.repaint();
		} 			
	}
	
	/*
	 * Mouse event handler for popup menu
	 */
	public class PopupMenuListener extends MouseAdapter implements ActionListener {
	    public void mousePressed(MouseEvent e) {
	    	
	    	//remove objects highlights when objectdiagram is clicked
	    	if(e.getSource() == objectDiagram) {
	    		removeObjectHighlights();
	    	}
	    	
	    	//show popup 
	    	showPopup(e);

	    }

	    public void mouseReleased(MouseEvent e) {
	    	showPopup(e);
	    }

	    private void showPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	        	if(e.getSource() == classDiagram) {
		        	//if mouse pointer is inside rectangle, remove all components and add all constructors to popup menu
		        	if(classDiagram.isInsideRect(e.getX(), e.getY())) {
		        		
		        		classPopupMenu.removeAll();
			        	
		        		String[] constructors = classCtrl.getLoadedClass().getConstructorNames();
		        		

			        	for(int i = 0; i < constructors.length; i++) {	
			        		JMenuItem item = new JMenuItem(constructors[i]);
			        		String index = Integer.toString(i);
			        		item.setActionCommand("invokeConstructor:" + index);
			        		item.addActionListener(this);
			        		classPopupMenu.add(item);
			        	}
			        	
			        	//show menu at coordinates of mouse pointer
			            classPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		        	}	        		
	        	}
	        	else if(e.getSource() == objectDiagram) {
	        		
	        		ObjectArrayList<CreatedObject> createdObjects = objectCtrl.getCreatedObjects();
	        		
	        		for(int i = 0; i < createdObjects.size(); i++) {
	        			CreatedObject co = (CreatedObject)createdObjects.get(i);
	        			
	        			if(co.contains(e.getX(), e.getY())) {
			        		objectPopupMenu.removeAll();
				        	
			        		String[] methods = co.getMethodNames();

				        	for(int x = 0; x < methods.length; x++) {
				        		JMenuItem item = new JMenuItem(methods[x]);
				        		String index = Integer.toString(x);
				        		item.setActionCommand("invokeMethod:" + index + ":" + i);
				        		item.addActionListener(this);
				        		objectPopupMenu.add(item);
				        	}
				        	
				        	//show menu at coordinates of mouse pointer
				            objectPopupMenu.show(e.getComponent(), e.getX(), e.getY());	        				
	        			}
	        		}
	        	}
	        }
	    }
	    
		/*
		 * Event handler for jmenuitems etc.
		 */
		public void actionPerformed(ActionEvent e) {
			
			//get parameters from constructor chosen and create object
			String[] command = e.getActionCommand().split(":");
			
			if(command[0].equals("invokeConstructor")) {
				//get constructors and find the one with specified index
				Constructor[] allConstructors = classCtrl.getLoadedClass().getLoadedClass().getConstructors();
				int indexOfConstructor = Integer.parseInt(command[1]);
				Constructor constructor = allConstructors[indexOfConstructor];
				
				//highlight constructor in CodePanel
				codePanel.highlightConstructor(constructor.toString(), constructor.getName());
				
				//get instance name and parameters for constructor and request arguments from user
				Class[] parameters = constructor.getParameterTypes();			
				Object[] arguments = new Object[parameters.length];
				
				
				String instanceName = JOptionPane.showInputDialog(null, "Instance reference: ", "Invoke Constructor", JOptionPane.QUESTION_MESSAGE);			
				
				//check if null (ie cancel was pressed)
				if(instanceName == null) {
					return;
				}
				
				//validate the chosen reference to ensure its not taken and its proper syntax
				boolean validated = false;
				while(!validated) {
					if(objectCtrl.instanceNameExists(instanceName)) {
						instanceName = JOptionPane.showInputDialog(null, "The reference \"" + instanceName + "\" already points to an existing object. Please try another reference name: ", "Invoke Constructor", JOptionPane.ERROR_MESSAGE);
					}
					else if(!Pattern.matches("[a-zA-Z0-9$_]+", instanceName) || !Character.isLowerCase(instanceName.charAt(0)) || Character.isDigit(instanceName.charAt(0))) {
						instanceName = JOptionPane.showInputDialog(null, "The reference is invalid. Please try another reference name: ", "Invoke Constructor", JOptionPane.ERROR_MESSAGE);
					}
					else if(instanceName.length() > 100) {
						instanceName = JOptionPane.showInputDialog(null, "The reference exceeds the allowed length of 100 characters. Please try another reference name: ", "Invoke Constructor", JOptionPane.ERROR_MESSAGE);
					}
					else {
						validated = true;
					}
					
				}	
				
				//prompt for all parameters
				for(int i = 0; i < parameters.length; i++) {
					
					String type = parameters[i].getSimpleName();
					String arg = JOptionPane.showInputDialog(null, "Value for the " + getNumberName(i+1) + " " + type + " parameter: " , "Invoke Constructor", JOptionPane.QUESTION_MESSAGE);
					
					//if cancel is pressed, return
					if(arg == null) {
						return;
					}
					
					//cast argument and validate it so its of correct type
					Object castArg = castArgument(arg, type);
					while(castArg == null) {
						arg = JOptionPane.showInputDialog(null, "Error. The argument supplied is invalid. Please provide another value for the " + getNumberName(i+1) + " " + type + " parameter: " , "Invoke Constructor", JOptionPane.ERROR_MESSAGE);
						if(arg == null) {
							return;
						}
						castArg = castArgument(arg, type);
					}
					
					//add to list of arguments
					arguments[i] = castArg;
					
				}

				objectCtrl.simulateConstructorExecution(instanceName, parameters, arguments);

			}
			if(command[0].equals("invokeMethod")) {
				
				Method[] allMethods = classCtrl.getLoadedClass().getLoadedClass().getMethods();
				int indexOfMethod = Integer.parseInt(command[1]);
				
				Method method = allMethods[indexOfMethod];
				
				//highlight method in CodePanel
				codePanel.highlightMethod(method.toString(), method.getName());
				
				//get instance name and parameters for constructor and request arguments from user
				Class[] parameters = method.getParameterTypes();			
				Object[] arguments = new Object[parameters.length];		
				
				for(int i = 0; i < parameters.length; i++) {
					
					String type = parameters[i].getSimpleName();
					
					String arg = JOptionPane.showInputDialog(null, "Value for the " + getNumberName(i+1) + " " + type + " parameter for " + method.toString() + ": " , "Invoke Method", JOptionPane.QUESTION_MESSAGE);
					
					//if cancel is pressed, return
					if(arg == null) {
						return;
					}
					
					//cast argument and validate it so its of correct type
					Object castArg = castArgument(arg, type);
					while(castArg == null) {
						arg = JOptionPane.showInputDialog(null, "Error. The argument supplied is invalid. Please provide another value for the " + getNumberName(i+1) + " " + type + " parameter for " + method.toString() + ": " , "Invoke Method", JOptionPane.ERROR_MESSAGE);
						if(arg == null) {
							return;
						}
						castArg = castArgument(arg, type);
					}
					
					//add to list of arguments
					arguments[i] = castArg;
					
				}
					
				//invoke method by passing object index, method index and arguments
				objectCtrl.simulateMethodExecution(Integer.parseInt(command[2]), Integer.parseInt(command[1]), arguments);
			}
		}
		
		/*
		 * This method returns the cast value of string input
		 */
		public Object castArgument(String arg, String type) {
			try {
				//Add arguments based on their type
				if(type.equals("int")) {
					return new Integer(Integer.parseInt(arg));
				}
				else if(type.equals("double")) {
					return new Double(arg);
				}
				else if(type.equals("float")) {
					return new Float(arg);
				}
				else if(type.equals("char")) {
					return new Character(arg.charAt(0));
				}
				else if(type.equals("byte")) {
					return new Byte(arg);
				}
				else if(type.equals("short")) {
					return new Short(arg);
				}
				else if(type.equals("long")) {
					return new Long(arg);
				}
				else if(type.equals("boolean")) {
					return new Boolean(arg);
				}
				else {
					return arg;
				}
			}
			catch(IllegalArgumentException iae) {
				System.out.println("IllegalArgumentException: " + iae.getMessage());
				return null;
			}	
		}
		
		/*
		 * This method returns the sequential string name for a number
		 */
		public String getNumberName(int number) {
			switch(number) {
				case 1 : return "first";
				case 2: return "second";
				case 3: return "third";
				case 4: return "fourth";
				case 5: return "fifth";
				case 6: return "sixth";
				case 7: return "seventh";
				case 8: return "eigth";
				case 9: return "ninth";
				case 10: return "tenth";
				default: return Integer.toString(number) + "";
			}
		
		}
	}
}


