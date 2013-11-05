package gov.nist.toolkit.dst.cmd;

import gov.nist.toolkit.dst.cmd.language.Interpreter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class CmdInput  
implements ActionListener, KeyListener {
	JTextField textField;
	
	public CmdInput(JTextField textField) {
		this.textField = textField;
//		textField = new JTextField(50);
		textField.addActionListener(this);
		textField.addKeyListener(this);
	}
	
	public JTextField getComponent() {
		return textField;
	}

	// Captures ENTER/CR
	@Override
	public void actionPerformed(ActionEvent e) {
		textField.setText("");
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	// Captures regular keystrokes
	@Override
	public void keyReleased(KeyEvent arg0) {
		String text = textField.getText(); 
		if (text.length() == 0)
			return;
		char last = text.charAt(text.length()-1);
		if (last == ' ') {
			text = text.trim();
			int start = text.lastIndexOf(' ');
			String word;
			if (start == -1) {
				word = text;
			} else {
				word = text.substring(start).trim();
			}
			System.out.println("word is " + word);
			Interpreter.get().newWord(word);
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

}
