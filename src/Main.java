
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.MatteBorder;
import javax.swing.JRootPane;

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
    int numPanes = 65;
	
	JTextArea logText;
	
	/* Windows */
	JWindow northWindow;
	JWindow eastWindow;
	JWindow westWindow;
	JWindow southWindow;
	JPanel eastPanel;
	JFrame frame;
	
	JButton startButton;
	JButton endButton;
	JButton markButton;
	JButton saveButton;
	JButton addAnchorButton;
	JButton headingXPlus;
	
	CoordinateRecorder recorder;
	
	public Main() {
		frame = new JFrame();
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				exitProgram();
			}
		});
		frame.setVisible(true);
		initComponents();
	}
	
	public void exitProgram() {
		System.out.println("You closed it!");
		northWindow.dispose();
		southWindow.dispose();
		westWindow.dispose();
		eastWindow.dispose();
		frame.dispose();
		System.exit(0);
	}
	
	public void initComponents() {
		startButton = new JButton("Start");
		markButton = new JButton("Mark");
		endButton = new JButton("End");
		saveButton = new JButton("Save to File...");
		headingXPlus = new JButton("Heading X+");
		JButton exitButton = new JButton("Exit");
		
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = new Double(screenSize.getHeight()).intValue();
		screenWidth = new Double(screenSize.getWidth()).intValue();
		startHeight = (screenHeight - 600)/2;
		windowWidth = (screenWidth-1024)/2;
		Integer southWindowHeight = ((screenHeight-600)/2) - 40;
		startSouthWindow = screenHeight - ((screenHeight - 600)/2);
		Integer westWindowStart = screenWidth - windowWidth;
				
		northWindow = new JWindow(frame);
		JPanel northLeftPanel = new JPanel();
		JPanel northCenterPanel = new JPanel();
		JPanel northRightPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		eastPanel = new JPanel();
        
		JScrollPane eastScroll = new JScrollPane(eastPanel);
		JLabel centerLabel = new JLabel("AIONAV Tracking");
		centerLabel.setFont(new Font(centerLabel.getFont().toString(), Font.PLAIN, 16));
		JLabel loggingLabel = new JLabel("Logging");
		JLabel anchorLabel = new JLabel("Anchor Points");
		northLeftPanel.setLayout(new BorderLayout());
		northCenterPanel.setLayout(new BorderLayout());
		loggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loggingLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		northLeftPanel.add(loggingLabel, BorderLayout.CENTER);
		northLeftPanel.setPreferredSize(new Dimension(windowWidth, southWindowHeight+40));
		northLeftPanel.setMaximumSize(new Dimension(windowWidth, southWindowHeight+40));
		
		centerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centerLabel.setVerticalAlignment(SwingConstants.CENTER);
		northCenterPanel.add(centerLabel);
		northCenterPanel.setPreferredSize(new Dimension(1024, southWindowHeight));
		northCenterPanel.setMinimumSize(new Dimension(1024, southWindowHeight));
		
		anchorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		anchorLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		addAnchorButton = new JButton("Add");
		northRightPanel.setLayout(new BorderLayout());
		northRightPanel.add(anchorLabel, BorderLayout.CENTER);
		northRightPanel.add(addAnchorButton, BorderLayout.SOUTH);
		northRightPanel.setPreferredSize(new Dimension(windowWidth, southWindowHeight+40));
		northRightPanel.setMaximumSize(new Dimension(windowWidth, southWindowHeight+40));
		
		System.out.println("Window width: " + windowWidth);
		northWindow.add(northLeftPanel, BorderLayout.LINE_START);
		northWindow.add(northCenterPanel, BorderLayout.CENTER);
		northWindow.add(northRightPanel, BorderLayout.LINE_END);
		
		northWindow.pack();
		
		northWindow.setBounds(0, 0, screenWidth, southWindowHeight + 40);
		northWindow.setVisible(true);
		
		/* East Window set up */
		westWindow = new JWindow(frame);
		westWindow.setBounds(0, startHeight, windowWidth, 600);
		westWindow.setVisible(true);
		
		/* West Window set up */
		eastWindow = new JWindow(frame);
		eastWindow.setBounds(westWindowStart, startHeight, windowWidth, 600);
		System.out.println("West window start" + westWindowStart);
		System.out.println("West window width " + windowWidth);
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		JButton test = new JButton("Test");
        logText = new JTextArea();
        recorder = new CoordinateRecorder(logText);
        AnchorPane firstPane = new AnchorPane((char)numPanes, recorder);
        firstPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		eastPanel.add(firstPane);
		//eastPanel.add(test);
		eastWindow.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		eastWindow.add(eastScroll);
		eastWindow.setVisible(true);
		//eastWindow.pack();
		
		/* South window set up */
		southWindow = new JWindow(frame);
		southWindow.setBounds(0, startSouthWindow, screenWidth, southWindowHeight);
		bottomPanel.add(startButton);
		bottomPanel.add(markButton);
		bottomPanel.add(endButton);
		bottomPanel.add(saveButton);
		bottomPanel.add(headingXPlus);
		bottomPanel.add(exitButton);
		
		southWindow.add(bottomPanel);
		System.out.println("South Window Start: " + startSouthWindow);
		southWindow.setVisible(true);
		
		/* Add components to east window */
		JScrollPane logScroller = new JScrollPane(logText);
		logScroller.setPreferredSize(new Dimension(windowWidth, 600));
		westWindow.add(logScroller, BorderLayout.CENTER);
		
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});
		
		markButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				markButtonActionPerformed(evt);
			}
		});
		
		addAnchorButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addAnchorButtonActionPerformed(evt);
			}
		});
		
		headingXPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				headingXPlusActionPerformed(evt);
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitProgram();
			}
		});
		
		markButton.setEnabled(false);
		endButton.setEnabled(false);
		saveButton.setEnabled(false);
		
	}
	
	/* Listen for start button click */
	private void startButtonActionPerformed(ActionEvent evt) {
		recorder.start();
		startButton.setEnabled(false);
		markButton.setEnabled(true);
		endButton.setEnabled(true);
		saveButton.setEnabled(true);
	}
	
	/* Listen for mark button click */
	private void markButtonActionPerformed(ActionEvent evt) {
		recorder.mark=true;
		recorder.getSegInfo();
	}
	
	/* Listen for add anchor button click */
	private void addAnchorButtonActionPerformed(ActionEvent evt) {
                numPanes++;
                System.out.println("Add new anchor point");
                AnchorPane newAnchorPane = new AnchorPane((char)numPanes, recorder);
                newAnchorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		eastPanel.add(newAnchorPane);
                eastWindow.revalidate();
	}
	
	/* Listen for heading X Plus button click */
	private void headingXPlusActionPerformed(ActionEvent evt) {
		recorder.heading();
	}
	
	public static void main (String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (UnsupportedLookAndFeelException e) {
					
				} catch (IllegalAccessException illegalE) {
					
				} catch (InstantiationException instantiationE) {
					
				} catch (ClassNotFoundException classE) {
					
				}
				new Main();
			}
		});

	}
}
