import java.awt.Color;
import javax.swing.JLabel;

public class SmallBox {
	public char type;
	public JLabel labelbox;
	
	public SmallBox() {
		this.labelbox = new JLabel();
		setBox(' ', null);
	}
	
	public void setBox(char type, Color color) {
		this.type = type;
		labelbox.setBackground(color);
	}
}
