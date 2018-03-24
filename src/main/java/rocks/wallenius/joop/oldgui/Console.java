/* 
* Console.java
* This class extends JFrame. It is responsible for showing all the output to the console.
* 
* v1.0
* 09/05/2012 
* 
* Author: Philip Wallenius, UNN-id: 09031635
*/ 

package rocks.wallenius.joop.oldgui;
import javax.swing.*;

public class Console extends JFrame {
	
	//Main program icon
	private ImageIcon programIcon;
	private JTextArea txtConsole;
	private JScrollPane scrollPane;
	
	public Console() {
		
		setLayout(null);
		
		txtConsole = new JTextArea();
		txtConsole.setEditable(false);
		scrollPane = new JScrollPane(txtConsole);
		scrollPane.setBounds(3, 3, 500, 300);
		
		add(scrollPane);
		
		setTitle("Console");
		programIcon = new ImageIcon("img\\icon9.png");
		setSize(523,343);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		setIconImage(programIcon.getImage());
	}
	
	public void add(String txt) {
		txtConsole.append(txt);
	}
}
