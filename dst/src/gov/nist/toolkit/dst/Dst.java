package gov.nist.toolkit.dst;

import gov.nist.toolkit.dst.cmd.CmdInput;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Dst implements UiRuntime {
	public static boolean RIGHT_TO_LEFT = false;
	static final Color foregroundColor = Color.WHITE;
	static final Color backgroundColor = Color.BLACK;
	static JTextField cmdInputField;
	static Font font = new Font("Courier New", Font.BOLD, 14);
	static JTextArea selectArea;
	static JTextArea topArea;
	
	public static void addComponentsToPane(Container pane) {

		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(
					java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		}

		//		JButton button = new JButton("Button 1 (PAGE_START)");
		topArea = new JTextArea(/*height*/10, /*width*/20);
		topArea.setBorder(BorderFactory.createLineBorder(foregroundColor));
		topArea.setForeground(foregroundColor);
		topArea.setBackground(backgroundColor);
		topArea.setFont(font);
		topArea.setEditable(false);
		pane.add(topArea, BorderLayout.PAGE_START);

		//Make the center component big, since that's the
		//typical usage of BorderLayout.
		JTextArea leftArea = new JTextArea();
		leftArea.setPreferredSize(new Dimension(200, 100));
		leftArea.setBorder(BorderFactory.createLineBorder(foregroundColor));
		leftArea.setForeground(foregroundColor);
		leftArea.setBackground(backgroundColor);
		leftArea.setFont(font);
		leftArea.setEditable(false);
		pane.add(leftArea, BorderLayout.CENTER);

		selectArea = new JTextArea(/*height*/20, /*width*/20);
		selectArea.setBorder(BorderFactory.createLineBorder(foregroundColor));
		selectArea.setForeground(foregroundColor);
		selectArea.setBackground(backgroundColor);
		selectArea.setFont(font);
		selectArea.setEditable(false);
		pane.add(selectArea, BorderLayout.LINE_START);

		cmdInputField = new JTextField(40);
		new CmdInput(cmdInputField);
		cmdInputField.setForeground(foregroundColor);
		cmdInputField.setBackground(backgroundColor);
		cmdInputField.setFont(font);
		pane.add(cmdInputField, BorderLayout.PAGE_END);

		JTextArea rightArea = new JTextArea(/*height*/20, /*width*/75);
		rightArea.setBorder(BorderFactory.createLineBorder(foregroundColor));
		rightArea.setForeground(foregroundColor);
		rightArea.setBackground(backgroundColor);
		rightArea.setFont(font);
		rightArea.setEditable(false);
		pane.add(rightArea, BorderLayout.LINE_END);
	}


	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {

		//Create and set up the window.
		JFrame frame = new JFrame("BorderLayoutDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Set up the content pane.
		addComponentsToPane(frame.getContentPane());
		//Use the content pane's default BorderLayout. No need for
		//setLayout(new BorderLayout());
		//Display the window.
		frame.pack();
		frame.setVisible(true);
		cmdInputField.requestFocus();

	}

	public static void main(String[] args) {
		/* Use an appropriate Look and Feel */
		try {
			new Config(new Dst());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* Turn off metal's use bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}


	@Override
	public void displaySelection(String info) {
		selectArea.setText(info);
	}
	
	public void displayTop(String info) {
		topArea.setText(info);
	}

	public void displayStack(String info) {
		cmdInputField.setText(info);
	}
}

