
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	
	File anchorFile;
	ArrayList<AnchorPane> anchorList = new ArrayList<AnchorPane>();
	
	GraphPlot plot;
	
	Integer screenHeight;
	Integer screenWidth;
	Integer startHeight;
	Integer windowWidth;
	Integer startSouthWindow;
    int numPanes = 64;
	
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
		final JButton showMapButton = new JButton("Hide Map");
		
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
		JButton loadAnchorButton = new JButton("Load Anchors");
		northRightPanel.setLayout(new BorderLayout());
		northRightPanel.add(anchorLabel, BorderLayout.CENTER);
		JPanel anchorButtonPanel = new JPanel();
		anchorButtonPanel.setLayout(new BorderLayout());
		anchorButtonPanel.add(addAnchorButton, BorderLayout.CENTER);
		anchorButtonPanel.add(loadAnchorButton, BorderLayout.SOUTH);
		
		loadAnchorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadAnchorPoints();
			}
		});
		
		northRightPanel.add(anchorButtonPanel, BorderLayout.SOUTH);
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
        plot = new GraphPlot();
        recorder = new CoordinateRecorder(logText, plot);
        numPanes++;
        AnchorPane firstPane = new AnchorPane((char)numPanes, recorder);
        firstPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        anchorList.add(firstPane);
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
		bottomPanel.add(showMapButton);
		bottomPanel.add(exitButton);
		
		southWindow.add(bottomPanel);
		System.out.println("South Window Start: " + startSouthWindow);
		southWindow.setVisible(true);
		
		/* Add components to east window */
		JScrollPane logScroller = new JScrollPane(logText);
		logScroller.setPreferredSize(new Dimension(windowWidth, 600));
		westWindow.add(logScroller, BorderLayout.CENTER);
		
		/* * * * * * * * * * * * * * * * * * * * *
		 * 
		 *  BUTTON LISTENERS FOR BOTTOM PANEL
		 * 
		 * * * * * * * * * * * * * * * * * * * * */
		
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
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				plot.saveChart("chart.png");
				//recorder.getAnchorlessPlot().saveChart("anchorlessChart.png");
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitProgram();
			}
		});
		
		showMapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (plot.getJFrame().getExtendedState() == Frame.ICONIFIED) {
					plot.getJFrame().setExtendedState(Frame.NORMAL);
					showMapButton.setText("Hide Map");
				}
				else {
					plot.getJFrame().setExtendedState(Frame.ICONIFIED);
					showMapButton.setText("Show Map");
				}
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
			addAnchor();
		/*   numPanes++;
                System.out.println("Add new anchor point");
                AnchorPane newAnchorPane = new AnchorPane((char)numPanes, recorder);
                newAnchorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
                eastPanel.add(newAnchorPane);
                anchorList.add(newAnchorPane);
                eastWindow.revalidate();*/
	}
	
	private void addAnchor() {
		numPanes++;
		AnchorPane newAnchorPane = new AnchorPane((char)numPanes, recorder);
		newAnchorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		eastPanel.add(newAnchorPane);
		anchorList.add(newAnchorPane);
		eastWindow.revalidate();
	}
	
	private void addAnchor(Coordinate _coordinate) {
		Coordinate inCoordinate = _coordinate;
		System.out.println("In add Anchor");
		numPanes++;
		AnchorPane newAnchorPane = new AnchorPane((char)numPanes, recorder, inCoordinate);
		newAnchorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		eastPanel.add(newAnchorPane);
		anchorList.add(newAnchorPane);
		eastWindow.revalidate();
	}
	
	/* Listen for heading X Plus button click */
	private void headingXPlusActionPerformed(ActionEvent evt) {
		recorder.heading();
	}
	
	public void loadAnchorPoints() {
		JFileChooser anchorFileChooser = new JFileChooser();
		int returnVal = anchorFileChooser.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			anchorFile = anchorFileChooser.getSelectedFile();
			System.out.println("Selected");
			
			for (AnchorPane existingPane : anchorList) {
				numPanes--;
				System.out.println("Found an anchor pane");
				eastPanel.remove(existingPane);
				eastPanel.revalidate();
				eastPanel.repaint();
			}
			
			/* Create the new panes from the text file */
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(anchorFile));
				String line;
				while ((line = br.readLine()) != null) {
					System.out.println("New Point");
					if (line.length() == 1) {
						Double x = Double.parseDouble(br.readLine());
						System.out.println("x: " + x);
						Double y = Double.parseDouble(br.readLine());
						Double z = Double.parseDouble(br.readLine());
						Coordinate coordinateForAnchor = new Coordinate(x, y, z);
						addAnchor(coordinateForAnchor);
					}
				}
			} catch(FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException ioE) {
				System.out.println(ioE);
			}
		}
		
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
