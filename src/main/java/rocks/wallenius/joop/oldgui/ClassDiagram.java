package rocks.wallenius.joop.oldgui;

/* 
* ClassDiagram.java
* Draws a class on the class diagram based on a LoadedClass object
* 
* v1.0 
* 12/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.font.*;

import rocks.wallenius.joop.code.CodePanel;
import rocks.wallenius.joop.loadedclass.*;
import rocks.wallenius.joop.mvc.*;
import java.lang.reflect.*;
import javax.swing.JLabel;

public class ClassDiagram extends DiagramPanel implements Observer {
	
	private ClassCtrl classCtrl;
	private LoadedClass loadedClass;
	
	private Rectangle2D.Float classRect;	
	
	private Field[] fields;
	private Constructor[] constructors;
	private Method[] methods;
	
	private Font font = new Font("Helvetica",Font.BOLD, 26);
	private Font fontSmall = new Font("Helvetica",Font.BOLD, 14);
	
	//offsets etc
	private final static int RECTANGLE_INIT_X_OFFSET = 185;
	private final static int RECTANGLE_INIT_Y_OFFSET = 30;
	private final static int RECTANGLE_INIT_WIDTH = 300;
	private final static int RECTANGLE_INIT_HEIGHT = 200;
	private final static int TEXT_INIT_X_OFFSET = 10;
	private final static int TEXT_INIT_Y_OFFSET = 20;
	private final static int INTER_TEXT_Y_SPACING = 20;
	private final static int INTER_LINE_DIVIDER_Y_OFFSET = 50;
	
	private JLabel classDiagramLabel;
	
	/*
	 * Constructor
	 */
	public ClassDiagram(ClassCtrl classCtrl, LoadedClass loadedClass) {
		super();
		this.classCtrl = classCtrl;
		this.loadedClass = loadedClass;
		loadedClass.addObserver(this);
		
		fields = null;
		constructors = null;
		methods = null;
		
	}
	
	/*
	 * This method draws the class diagram
	 */
	public void paintComponent(Graphics g) {
		
		//Clear offscreen bitmap
		clear(g);
		
		//Cast Graphics context to Graphics2D
		Graphics2D g2d = (Graphics2D)g;		
		
		//Configure graphics context to use anti aliasing for smoother graphics
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//paint label
		int w = getWidth()-30;
		int h = getHeight()/3;
		String label = "CLASS DIAGRAM";
		for(int i = 0; i < label.length(); i++) {
			char c = label.charAt(i);
			g2d.drawString(Character.toString(c), w, h);
			h += 10;
		}

		if(classCtrl.getMainCtrl().getCodeCtrl().getCode().isCreated()) {		
			double divider_x1; 
			double divider_x2; 
			double divider_y; 
			float className_x;
			float className_y;
			float className_width;
			float className_height;
			
			//get class name
			String className = classCtrl.getMainCtrl().getCodeCtrl().getCode().getName();
			
			if(loadedClass.getLoadedClass() == null) {
		        //Create the classRectangle and draw it
		        classRect = new Rectangle2D.Float(RECTANGLE_INIT_X_OFFSET, RECTANGLE_INIT_Y_OFFSET, RECTANGLE_INIT_WIDTH, RECTANGLE_INIT_HEIGHT);

		        //create diagonal lines texture 
		        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		        TexturePaint texturePaint = new TexturePaint(bufferedImage, new Rectangle(0, 0, 10, 10));
		        Graphics2D texture = bufferedImage.createGraphics();
		       
		        //background of texture 
		        texture.setColor(new Color(243, 248, 250));
		        texture.fillRect(0, 0, 10, 10);
		        
		        //foreground of texture
		        texture.setColor(new Color(200, 200, 200));
		        texture.drawLine(0, 0, 10, 10);
		        
		        //set paint to texture
		        g2d.setPaint(texturePaint);
		        
		        
				g2d.fill(classRect);
				g2d.setPaint(Color.black);
		        g2d.draw(classRect);
		        
		        divider_x1 = classRect.getX();
		        divider_x2 = (classRect.getX() + classRect.getWidth()) - 1;
		        divider_y = classRect.getY() + INTER_LINE_DIVIDER_Y_OFFSET;
				g2d.draw(new Line2D.Double(divider_x1, divider_y, divider_x2, divider_y));

				//Configure font and calculate where to place fonts inside classRectangle
				FontRenderContext frc = g2d.getFontRenderContext();
				g2d.setFont(font);
		        className_width = (float)font.getStringBounds(className, frc).getWidth();
		        LineMetrics lm = font.getLineMetrics(className, frc);
		        className_height = lm.getAscent() + lm.getDescent();					

				//set font and draw class name inside classRectangle    
		        className_x = classRect.x + (classRect.width - className_width)/2;
		        className_y = classRect.y + className_height;
		        g2d.setPaint(CodePanel.COLOR_CLASSNAME);
		        g2d.drawString(className, className_x, className_y);
		        
		        repaint();
			}
			else {
				//Get data from loadedClass
				fields = loadedClass.getFields();
				constructors = loadedClass.getConstructors();
				methods = loadedClass.getMethods(); 
				
				int classRectHeight = RECTANGLE_INIT_HEIGHT;
				
		        //calulate how high the classRectangle should be to fit all fields and methods inside
		        if((fields.length + methods.length + constructors.length) > 2) {
		        	classRectHeight = 110 + (fields.length + methods.length + constructors.length) * 20; 
		        }
		        
		        //check if rectangle height exceeds jpanel height. change jpanel size if so..
		        if((RECTANGLE_INIT_X_OFFSET + classRectHeight) > getHeight()) {
					setPreferredSize(new Dimension(getWidth(), classRectHeight + 100));
					revalidate();
		        }
		        
		        //Create the classRectangle and draw it
		        classRect = new Rectangle2D.Float(RECTANGLE_INIT_X_OFFSET, RECTANGLE_INIT_Y_OFFSET, RECTANGLE_INIT_WIDTH, classRectHeight);
		        g2d.setPaint(Color.white);
				g2d.fill(classRect);
				g2d.setPaint(Color.black);
		        g2d.draw(classRect);		
		        
		        //Draw dividing line inside rectangle
		        divider_x1 = classRect.getX();
		        divider_x2 = (classRect.getX() + classRect.getWidth()) - 1;
		        divider_y = classRect.getY() + INTER_LINE_DIVIDER_Y_OFFSET;
				g2d.draw(new Line2D.Double(divider_x1, divider_y, divider_x2, divider_y));
	
				//Configure font and calculate where to place fonts inside classRectangle
				FontRenderContext frc = g2d.getFontRenderContext();
				g2d.setFont(font);
		        className_width = (float)font.getStringBounds(className, frc).getWidth();
		        LineMetrics lm = font.getLineMetrics(className, frc);
		        className_height = lm.getAscent() + lm.getDescent();			
				
				//set font and draw class name inside classRectangle    
		        className_x = classRect.x + (classRect.width - className_width)/2;
		        className_y = classRect.y + className_height;
		        g2d.setPaint(CodePanel.COLOR_CLASSNAME);
		        g2d.drawString(className, className_x, className_y);
				
				//draw fields inside classRectangle
				g2d.setFont(fontSmall);
				
				//set fields' colors
				g2d.setPaint(CodePanel.COLOR_ATTRIBUTES);
				
				float cur_field_x = classRect.x + TEXT_INIT_X_OFFSET;
				float cur_field_y = (float)divider_y + TEXT_INIT_Y_OFFSET;
				
				for(int x = 0; x < fields.length; x++) {
					
					Field field = fields[x];
					
					String fieldModifiers = Modifier.toString(field.getModifiers());
					
					//convert modifiers from name to symbol, ex public -> +
					if(fieldModifiers != "") {
						fieldModifiers = changeModifiers(fieldModifiers);
					}
					else {
						fieldModifiers = " ";
					}
					
					String fieldType = field.getType().getName();
					
					//if the data type is an array, get the correct UML type 
					if(fieldType.charAt(0) == '[') {
						fieldType = getArrayUMLType(fieldType);
					}
					
					//convert String symbol
					if(fieldType.equals("java.lang.String")) {
						fieldType = "String";
					}
					
					String fieldName = field.getName();
					
					String fieldString = fieldModifiers + fieldName + " : " + fieldType;
					
					g2d.drawString(fieldString, cur_field_x, cur_field_y);
					cur_field_y += INTER_TEXT_Y_SPACING;
					divider_y += INTER_TEXT_Y_SPACING;
				}
				
		        //Draw dividing line between fields and constructors inside rectangle
				divider_y += INTER_TEXT_Y_SPACING;
				g2d.setPaint(Color.black);
				g2d.draw(new Line2D.Double(divider_x1, divider_y, divider_x2, divider_y));			
				
				//set constructors' color
				g2d.setPaint(CodePanel.COLOR_CONSTRUCTORS);
				
				//draw constructors inside classRectangle
				float cur_constructor_x = cur_field_x;
				float cur_constructor_y = (float)divider_y + INTER_TEXT_Y_SPACING;
				
				for(int x = 0; x < constructors.length; x++) {
					Constructor constructor = constructors[x];
					
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
					
					
					String constructorString = constructorModifiers + constructorName + "(" + constructorParameters + ")";
					
					g2d.drawString(constructorString, cur_constructor_x, cur_constructor_y);
					cur_constructor_y += INTER_TEXT_Y_SPACING;
					divider_y += INTER_TEXT_Y_SPACING;
				}
				
				//draw methods inside classRectangle
				float cur_method_x = cur_constructor_x;
				float cur_method_y = cur_constructor_y + INTER_TEXT_Y_SPACING;
				
				//change color to method's color
				g2d.setPaint(CodePanel.COLOR_METHODS);
				
				for(int x = 0; x < methods.length; x++) {
					Method method = methods[x];
					String methodString = "";
					
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
					
					methodString = methodModifiers + methodName + "(" + methodParameters + ") : " + returnType;
					
					g2d.drawString(methodString, cur_method_x, cur_method_y);
					cur_method_y += INTER_TEXT_Y_SPACING;
					divider_y += INTER_TEXT_Y_SPACING;
				}
			}
		}
		
	}

	
	/*
	 * Tests if x and y coordinates are inside rectangle
	 */
	public boolean isInsideRect(double mouse_x, double mouse_y) {
		
		//get position of class rectangle
		double x1 = classRect.getX();
		double x2 = x1 + classRect.getWidth();
		double y1 = classRect.getY();
		double y2 = y1 + classRect.getHeight();

		return (mouse_x >= x1 && mouse_x <= x2) && (mouse_y >= y1 && mouse_y <= y2);

	}
	
	/*
	 * This method adds a code object and repaints so that it gets drawn on the class diagram
	 */
	public void update() {
		repaint();
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
	
	/*
	 * This method returns the UML display label for an Array of any type
	 */
	public String getArrayUMLType(String fieldType) {
		char type = fieldType.charAt(1);
		
		switch(type) {
		case 'I' : return "int[]";
		case 'D' : return "double[]";
		case 'F' : return "float[]";
		case 'B' : return "byte[]";
		case 'S' : return "short[]";
		case 'C' : return "char[]";
		case 'J' : return "long[]";
		case 'Z' : return "boolean[]";
		}
		//[Ljava.lang.String
		if(type == 'L') {
			int lastIndex = fieldType.lastIndexOf("");
			String className = fieldType.substring(lastIndex + 1);
			className = className.substring(0, className.length()-1);
			return className + "[]";
		}
		return "";
	}
}
