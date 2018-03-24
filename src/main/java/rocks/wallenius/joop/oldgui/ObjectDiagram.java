package rocks.wallenius.joop.oldgui;

/* 
* ObjectDiagram.java
* Responsible for drawing of objects on the object diagram
* 
* v1.0 
* 01/03/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.*;

import rocks.wallenius.joop.mvc.*;
import rocks.wallenius.joop.createdobject.*;

import java.lang.reflect.*;
import javax.swing.JLabel;

public class ObjectDiagram extends DiagramPanel implements Observer {
	 
	//Constants used in drawing object circles
	private final static int TEXT_INIT_Y_OFFSET = 50;
	private final static int INTER_CIRCLE_GAP = 10;
	private final static int INTER_LINE_SPACING = 20;

	private final static int CIRCLE_INIT_X_OFFSET = 10;
	private final static int CIRCLE_INIT_Y_OFFSET = 100;
	
	//Constants used in drawing reference rectangles
	private final static double REFERENCE_INIT_X_OFFSET = 10;
	private final static double REFERENCE_INIT_Y_OFFSET = 30;
	private final static double REFERENCE_INIT_WIDTH = 80;
	private final static double REFERENCE_INIT_HEIGHT = 40;
	
	private ObjectCtrl objectCtrl;
	private ObjectArrayList<CreatedObject> createdObjects;
	private Ellipse2D.Double circle;
	private Rectangle2D.Double rectangle;
	private GradientPaint gradient = new GradientPaint(150, 150, new Color(192, 255, 175), 150, 280, new Color(151, 255, 140), false); // true means to repeat pattern	
	private Font font = new Font("Helvetica",Font.BOLD, 14);
	private Font fontSmall = new Font("Helvetica",Font.BOLD, 14);
	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 0);
	
	private JLabel objectDiagramLabel;
	
	/*
	 * Constructor
	 */
	public ObjectDiagram(ObjectCtrl objectCtrl, ObjectArrayList<CreatedObject> createdObjects) {
		super();	
		this.objectCtrl = objectCtrl;
		this.createdObjects = createdObjects;
		createdObjects.addObserver(this);
	}
	
	/*
	 * This method paints objects on JPanel canvas
	 */
	public void paintComponent(Graphics g) {
		
		clear(g);
		Graphics2D g2d = (Graphics2D)g;

		//Configure graphics context to use anti aliasing for smoother graphics
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);		
		
		//paint label
		int w = getWidth()-30;
		int h = getHeight()/3;
		String label = "OBJECT DIAGRAM";
		for(int i = 0; i < label.length(); i++) {
			char c = label.charAt(i);
			g2d.drawString(Character.toString(c), w, h);
			h += 10;
		}
		
		//draw objects only if objects have been created
		if(!createdObjects.isEmpty()) {
			
			//set init circle x and y offset values
			int curCircleX = CIRCLE_INIT_X_OFFSET;
			int curCircleY = CIRCLE_INIT_Y_OFFSET;
			
			//set init rectangle x and y offset values, height and width..
			double curRectangleX = REFERENCE_INIT_X_OFFSET;
			double curRectangleY = REFERENCE_INIT_Y_OFFSET;
			double curRectangleWidth = REFERENCE_INIT_WIDTH;
			double curRectangleHeight = REFERENCE_INIT_HEIGHT;
			
			//draw all circles inside createdObjects array list
			for(int i = 0; i < createdObjects.size(); i++) {
				
				CreatedObject c = (CreatedObject)createdObjects.get(i);		
				
				String objectName = c.getReference();
				int curCircleHeight = c.getHeight();
				int curCircleWidth = c.getWidth();
				
		        //check if circle x and width exceeds jpanel total width. change jpanel size if so, to fit all circles..
				if((curCircleX + curCircleWidth) > getWidth()) {
					setPreferredSize(new Dimension(getWidth() + curCircleWidth, getHeight()));
					revalidate();
		        }
				
				/*
				 * draw circle
				 */
				circle = new Ellipse2D.Double(curCircleX, curCircleY, curCircleWidth, curCircleHeight);
				g2d.setPaint(gradient);
				g2d.fill(circle);
				
				//if highlighted, paint red, otherwise black
				if(c.isHighlighted()) {
					g2d.setPaint(HIGHLIGHT_COLOR);
					g2d.setStroke(new BasicStroke(3));
				}
				else {
					g2d.setPaint(Color.black);
					g2d.setStroke(new BasicStroke(1));
				}
				g2d.draw(circle);
				
				//set default color and stroke width
				g2d.setStroke(new BasicStroke(1));
				g2d.setPaint(Color.black);
				
				/*
				 * DRAW REFERENCE TO OBJECT
				 * 
				 */
				rectangle = new Rectangle2D.Double(curRectangleX, curRectangleY, curRectangleWidth, curRectangleHeight);
				
				g2d.setPaint(gradient);
				g2d.fill(rectangle);
				
				//if highlighted, paint red, otherwise black
				if(c.isHighlighted()) {
					g2d.setPaint(HIGHLIGHT_COLOR);
					g2d.setStroke(new BasicStroke(3));
				}
				else {
					g2d.setPaint(Color.black);
					g2d.setStroke(new BasicStroke(1));
				}
				g2d.draw(rectangle);
				
				//return to default color and stroke
				g2d.setStroke(new BasicStroke(1));
				g2d.setPaint(Color.black);
				
				//draw object name inside reference rectangle 
				FontRenderContext frc = g2d.getFontRenderContext();    
		        LineMetrics lineMetrics = font.getLineMetrics(objectName, frc);
		        
		        float nameWidth = (float)font.getStringBounds(objectName, frc).getWidth();
		        float nameHeight = lineMetrics.getAscent() + lineMetrics.getDescent();	
		        
		        float nameX = (float)(rectangle.x + (rectangle.width - nameWidth)/2);
		        float nameY = (float)(rectangle.y + (rectangle.height + nameHeight)/2 - lineMetrics.getDescent());
		        
		        g2d.setFont(font);
		        g2d.drawString(objectName, nameX, nameY);
				
				curRectangleX += curCircleWidth + INTER_CIRCLE_GAP;
				
				/*
				 * Draw line pointing from reference towards circle
				 */
				double arrow_x1 = rectangle.x + (rectangle.width/2);
				double arrow_y1 = rectangle.y + rectangle.height;
				
				//where on circle the arrow should point to
				double degrees = 250;
				
				//calculate point on circumference of circle and draw the arrow to point there
				double arrow_x2 = circle.getCenterX() + ((circle.width / 2) * Math.cos(degrees * Math.PI / 180));
				double arrow_y2 = circle.getCenterY() + ((circle.width / 2) * Math.sin(degrees * Math.PI / 180));			
				g2d.draw(new Line2D.Double(arrow_x1, arrow_y1, arrow_x2, arrow_y2));
				
				//draw arrow head (not dynamic adjusting)
				g2d.draw(new Line2D.Double(arrow_x2, arrow_y2, arrow_x2-8, arrow_y2-7));
				g2d.draw(new Line2D.Double(arrow_x2, arrow_y2, arrow_x2+1, arrow_y2-11));
				
				
				
				/*
				 * Draw variables and their values inside circle
				 */
		        g2d.setFont(fontSmall);
		        
		        Field[] fields = c.getFields();
				int numFields = fields.length;
				
				float currFieldX = (float)circle.x + (curCircleWidth/6);
				float currFieldY = curCircleY + (curCircleHeight/4);
				
				for(int x = 0; x < numFields; x++) {
					
					String fieldName = fields[x].getName();
					String fieldValue = "";
					
					try {
						//get field value
						Object fieldValueObject = fields[x].get(c.getCreatedObject());
						
						//Determine which data type value is and cast it appropriately
						if(fieldValueObject != null) {
							if(fieldValueObject.getClass().isArray()) {
								fieldValue = "[ ]";
							}
							else if(fieldValueObject.getClass().equals(String.class)) {
								fieldValue = (String)fieldValueObject;
								fieldValue = "\"" + fieldValue + "\"";
							}
							else if(fieldValueObject.getClass().equals(Integer.class)) {
								fieldValue = Integer.toString((Integer)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Double.class)) {
								fieldValue = Double.toString((Double)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Float.class)) {
								fieldValue = Float.toString((Float)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Character.class)) {
								fieldValue = Character.toString((Character)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Byte.class)) {
								fieldValue = Byte.toString((Byte)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Short.class)) {
								fieldValue = Short.toString((Short)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Long.class)) {
								fieldValue = Long.toString((Long)fieldValueObject);
							}
							else if(fieldValueObject.getClass().equals(Boolean.class)) {
								fieldValue = Boolean.toString((Boolean)fieldValueObject);
							}
							else {
								System.out.println("Error. Invalid field value type in ObjectDiagram class.");
							}
						}
					}
					catch(IllegalAccessException e) {
						System.out.println("IllegalAccessException. Error: " + e.getMessage());
					}
					
					//draw the string
					g2d.drawString(fieldName + " == " + fieldValue, currFieldX, currFieldY);
					currFieldY += INTER_LINE_SPACING;
				}
				
				//set current x and y position of each object
				c.setX(curCircleX);
				c.setY(curCircleY);
				curCircleX += curCircleWidth + INTER_CIRCLE_GAP;
			}
		}
	}
	
	/*
	 * Update invoked from ObjectCtrl repaints the object diagram
	 */
	public void update() {
		repaint();
	}
}
