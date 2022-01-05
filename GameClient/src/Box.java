import java.awt.Color;

import javax.swing.JLabel;
public class Box {
	
	private String status; //Empty CurrentFall AlreadySet
	private char type; //'O' 'L' 'J' 'I' 'Z' 'S' 'T' 'V' '-' '.' ' '
	public JLabel labelbox;
	
	public Box() {
		this.labelbox = new JLabel();
		setBox(' ', null, "Empty");
	}
	
	public void setBox(char type, Color color, String status) {
		this.type = type;
		labelbox.setBackground(color);
		setStatus(status);
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public char getType() {
		return type;
	}
	
	public String getStatus() {
		return status;
	}
	
}
