/* 
* About.java
* This class extends JFrame. It is responsible for showing the About info.
* 
* v1.0
* 09/05/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

package rocks.wallenius.joop.gui;
import javax.swing.*;

public class About extends JFrame {
	
	//Main program icon
	private ImageIcon programIcon;
	private JTextArea txtConsole;
	private JScrollPane scrollPane;
	
	public About() {
		
		setLayout(null);
		
		txtConsole = new JTextArea("The JOOP Learning System version 1.0\n\nSoftware Engineering Project\nby Philip Wallenius, 09031635");
		txtConsole.setEditable(false);
		scrollPane = new JScrollPane(txtConsole);
		scrollPane.setBounds(3, 3, 500, 100);
		
		add(scrollPane);
		
		setTitle("About");
		programIcon = new ImageIcon("img\\icon9.png");
		setSize(523,143);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		setIconImage(programIcon.getImage());
	}
	
	public void add(String txt) {
		txtConsole.append(txt);
	}
}
