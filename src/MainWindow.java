
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class MainWindow extends JFrame {
	
	JButton addAnchorButton;
	JButton startButton;
	JButton markButton;
	JButton endButton;
	JButton saveButton;
	
	JTextArea logText;
	JLabel distanceLabel;
	
	CoordinateRecorder recorder;
	
	public MainWindow() {
		//CoordinateRecorder recorder = new CoordinateRecorder();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		initComponents();
	}
	
	public void initComponents() {
		
		logText = new JTextArea(30, 50);
		JScrollPane logScroller = new JScrollPane(logText);
		distanceLabel = new JLabel("Distance from start to end: ");
		distanceLabel.setMaximumSize(new Dimension(1000000, 22));
		recorder = new CoordinateRecorder(logText, distanceLabel);
		Font font = new Font("Arial", Font.PLAIN, 12);
		addAnchorButton = new JButton("Add Anchor");
		startButton = new JButton("Start");
		markButton = new JButton("Mark");
		endButton = new JButton("End");
		saveButton = new JButton("Save To File...");
		JPanel anchorSidePanel = new JPanel();
		
		Container pane = this.getContentPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel anchorTitlePanel = new JPanel();
		JPanel anchorButtonPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JPanel distancePanel = new JPanel();
		distanceLabel.setMaximumSize(new Dimension(1000000, 22));
		distancePanel.setLayout(new FlowLayout());
		
		/* Set up ActionListener for buttons on the bottom panel */
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});
		
		markButton.setEnabled(false);
		markButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				markButtonActionPerformed(evt);
			}
		}); 
		
		endButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		bottomPanel.add(startButton);
		bottomPanel.add(markButton);
		bottomPanel.add(endButton);
		bottomPanel.add(saveButton);
		bottomPanel.setLayout(new FlowLayout());
		
		System.out.println(bottomPanel.getWidth());
		anchorButtonPanel.setLayout(new FlowLayout());
		anchorTitlePanel.setLayout(new FlowLayout());
		logText.setFont(font);
		mainPanel.add(logScroller);
		distancePanel.add(distanceLabel);
		mainPanel.add(distancePanel);
		JScrollPane scroller = new JScrollPane(anchorSidePanel); 
		scroller.setBorder(null);
		anchorSidePanel.setLayout(new BoxLayout(anchorSidePanel, BoxLayout.Y_AXIS));
		//anchorSidePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pane.add(mainPanel, BorderLayout.CENTER);
		pane.add(scroller, BorderLayout.EAST);
		anchorTitlePanel.setMaximumSize(new Dimension(15000, 24));
		anchorTitlePanel.setMinimumSize(new Dimension(150, 24));
		anchorSidePanel.setOpaque(true);
		//anchorSidePanel.setBackground(Color.BLACK);
		pane.add(bottomPanel, BorderLayout.SOUTH);
		anchorButtonPanel.setMaximumSize(new Dimension(15000, 33));
		anchorButtonPanel.setMinimumSize(new Dimension(150, 33));
		
		anchorTitlePanel.add(new JLabel("Anchor Points"));
		anchorSidePanel.add(anchorTitlePanel);
		anchorSidePanel.add(anchorButtonPanel);
		anchorButtonPanel.add(addAnchorButton);
		anchorSidePanel.add(new AnchorPanel(recorder));

		addAnchorButton.addActionListener(new AnchorActionListener(anchorSidePanel));
		pack();
		System.out.println(anchorSidePanel.getHeight());
		System.out.println(bottomPanel.getWidth());
		System.out.println(anchorTitlePanel.getHeight());
		System.out.println(anchorButtonPanel.getHeight());
	}
	
	/* 
	 * Implements and ActionListener to handle the creation
	 * of more anchor points when necessary.
	 */
	
	private class AnchorActionListener implements ActionListener {
		
		JPanel anchorSidePanel;
		
		public AnchorActionListener(JPanel _anchorSidePanel) {
			anchorSidePanel = _anchorSidePanel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			AnchorPanel anchorPanel = new AnchorPanel(recorder);
			anchorSidePanel.add(anchorPanel);
			MainWindow.this.revalidate();
		}
	}
	
	private void startButtonActionPerformed(ActionEvent evt) {
		System.out.println("Button clicked");
		recorder.start();
		markButton.setEnabled(true);
		endButton.setEnabled(true);
		startButton.setEnabled(false);
	}
	
	private void markButtonActionPerformed(ActionEvent evt) {
		recorder.mark=true;
		recorder.getSegInfo();
	}
	
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (UnsupportedLookAndFeelException e) {
					
				} catch (IllegalAccessException illegalE) {
					
				} catch (InstantiationException instantiationE) {
					
				} catch (ClassNotFoundException classE) {
					
				}
				new MainWindow().setVisible(true);
			}
		});
	}
}
