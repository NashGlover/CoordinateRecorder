
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnchorPanel extends JPanel {
	
	JTextField xTextField, yTextField, zTextField;
	JButton setButton, hereButton;
	CoordinateRecorder recorder;
	Coordinate coordinate;
	
	public AnchorPanel(CoordinateRecorder _recorder) {
		
		recorder = _recorder;
		Dimension textFieldSize = new Dimension(50, 21);
		setSize(new Dimension(1000, 10));
		xTextField = new JTextField();
		yTextField = new JTextField();
		zTextField = new JTextField();
		setButton = new JButton("Set");
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setButtonActionPerformed(evt);
			}
		});
		
		hereButton = new JButton("Here");
		hereButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				hereButtonActionPerformed(evt);
			}
		});
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		//this.setLayout(new FlowLayout());
		//setAlignmentX(Component.LEFT_ALIGNMENT);
		
		xTextField.setText("x");
		yTextField.setText("y");
		zTextField.setText("z");
		
		xTextField.setMinimumSize(textFieldSize);
		xTextField.setPreferredSize(textFieldSize);
		xTextField.setSize(textFieldSize);
		xTextField.setMaximumSize(textFieldSize);
		yTextField.setMinimumSize(textFieldSize);
		yTextField.setPreferredSize(textFieldSize);
		yTextField.setMaximumSize(textFieldSize);
		yTextField.setMinimumSize(textFieldSize);
		zTextField.setMinimumSize(textFieldSize);
		zTextField.setPreferredSize(textFieldSize);
		zTextField.setMaximumSize(textFieldSize);
		zTextField.setMinimumSize(textFieldSize);
		
		add(xTextField);
		add(yTextField);
		add(zTextField);
		add(setButton);
		add(hereButton);
	}
	
	public void setButtonActionPerformed(ActionEvent evt) {
		coordinate = new Coordinate(Double.parseDouble(xTextField.getText()), Double.parseDouble(yTextField.getText()), Double.parseDouble(zTextField.getText()));
		xTextField.setEnabled(false);
		yTextField.setEnabled(false);
		zTextField.setEnabled(false);
		
		System.out.println("Button pressed");
	}
	
	public void hereButtonActionPerformed(ActionEvent evt) {
		recorder.setAnchor(coordinate.getX(), coordinate.getY(), coordinate.getZ());
	}
}
