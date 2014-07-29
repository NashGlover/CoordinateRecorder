
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Rectangle;

public class Main {
	
	Integer screenHeight;
	Integer screenWidth;
	Integer startHeight;
	Integer windowWidth;
	Integer startSouthWindow;
	
	JTextArea logText;
	
	public Main() {
		JFrame frame = new JFrame();
		frame.setVisible(true);
		initComponents();
	}
	
	public void initComponents() {
		
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = new Double(screenSize.getHeight()).intValue();
		screenWidth = new Double(screenSize.getWidth()).intValue();
		startHeight = (screenHeight - 600)/2;
		windowWidth = (screenWidth-1024)/2;
		Integer southWindowHeight = ((screenHeight-600)/2) - 40;
		startSouthWindow = screenHeight - ((screenHeight - 600)/2);
		Integer westWindowStart = screenWidth - windowWidth;
				
		JWindow eastWindow = new JWindow();
		eastWindow.setBounds(0, startHeight, windowWidth, 600);
		eastWindow.setVisible(true);
		
		JWindow westWindow = new JWindow();
		westWindow.setBounds(westWindowStart, startHeight, windowWidth, 600);
		westWindow.setVisible(true);
		
		JWindow southWindow = new JWindow();
		southWindow.setBounds(0, startSouthWindow, screenWidth, southWindowHeight);
		System.out.println("South Window Start: " + startSouthWindow);
		southWindow.setVisible(true);
		
		/* Add components to east window */
		logText = new JTextArea();
		logText.append("Nash Glover");
		JScrollPane logScroller = new JScrollPane(logText);
		logScroller.setPreferredSize(new Dimension(windowWidth, 600));
		eastWindow.add(logScroller, BorderLayout.CENTER);
		eastWindow.pack();
	}
	
	public static void main (String args[]) {
		Main main = new Main();
	}
}
