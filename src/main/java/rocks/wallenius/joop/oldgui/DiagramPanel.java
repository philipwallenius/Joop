package rocks.wallenius.joop.oldgui;

/* 
* DiagramPanel.java
* Abstract class which allows the drawing of classes and objects onto class and object diagrams
* 
* v1.0 
* 12/02/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

import javax.swing.*;
import java.awt.*;

public abstract class DiagramPanel extends JPanel {
	
	/*
	 * Constructors
	 */
	public DiagramPanel() {
		setBackground(Color.white);
	}
	
	/*
	 * Abstract method responsible for drawing classes
	 */
	public abstract void paintComponent(Graphics g);
	
	/*
	 * Method clears offscreen bitmap
	 */
	protected void clear(Graphics g) {
		super.paintComponent(g);
	}
	
}
