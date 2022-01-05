import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class TetrisLoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private TetrisFrame parent; 
	
	private JTextField textUserId;
	private JTextField textIpAddress;
	private JTextField textPortNumber;
	
	private ImageIcon backgroundImgIcon = new ImageIcon("image/background1.png");
	private ImageIcon tetrisTextImgIcon = new ImageIcon("image/TetrisText1.png");
	private Image backgroundImg = backgroundImgIcon.getImage();
	private Image tetrisTextImg = tetrisTextImgIcon.getImage();
	private LineBorder border = new LineBorder(Color.GRAY);

	public TetrisLoginPanel(TetrisFrame parent) {
		this.parent = parent;
		
		tetrisTextImg = tetrisTextImg.getScaledInstance(200, 65, Image.SCALE_SMOOTH);
		tetrisTextImgIcon = new ImageIcon(tetrisTextImg);
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JLabel labelTitle = new JLabel(tetrisTextImgIcon);
		labelTitle.setBounds(100, -5, 260, 90);
		this.add(labelTitle);
		
		JLabel labelBase = new JLabel();
		labelBase.setBounds(100, 75, 260, 140);
		labelBase.setBackground(Color.WHITE);
		labelBase.setOpaque(true);
		this.add(labelBase);
		
		JLabel labelUserId = new JLabel("USER ID");
		labelUserId.setBounds(110, 85, 100, 25);
		labelUserId.setBackground(Color.PINK);
		labelUserId.setOpaque(true);
		labelUserId.setBorder(border);
		this.add(labelUserId);
		
		JLabel labelIpAdr = new JLabel("IP ADDRESS");
		labelIpAdr.setBounds(110, 120, 100, 25);
		labelIpAdr.setBackground(Color.PINK);
		labelIpAdr.setOpaque(true);
		labelIpAdr.setBorder(border);
		this.add(labelIpAdr);
		
		JLabel labelPortNum = new JLabel("PORT NUMBER");
		labelPortNum.setBounds(110, 155, 100, 25);
		labelPortNum.setBackground(Color.PINK);
		labelPortNum.setOpaque(true);
		labelPortNum.setBorder(border);
		this.add(labelPortNum);
		
		textUserId = new JTextField();
		textUserId.setBounds(220, 85, 130, 25);
		this.add(textUserId);
		
		textIpAddress = new JTextField("127.0.0.1");
		textIpAddress.setBounds(220, 120, 130, 25);
		this.add(textIpAddress);
		
		textPortNumber = new JTextField("30000");
		textPortNumber.setBounds(220, 155, 130, 25);
		this.add(textPortNumber);
		
		JButton buttonLogin = new JButton("LOGIN");
		buttonLogin.setBounds(280, 185, 69, 25);
		buttonLogin.setBackground(Color.PINK);
		this.add(buttonLogin);
		
		this.setComponentZOrder(labelUserId, 0);
		this.setComponentZOrder(labelIpAdr, 1);
		this.setComponentZOrder(labelPortNum, 2);
		this.setComponentZOrder(textUserId, 3);
		this.setComponentZOrder(textIpAddress, 4);
		this.setComponentZOrder(textPortNumber, 5);
		this.setComponentZOrder(buttonLogin, 6);
		this.setComponentZOrder(labelBase, 7);
		
		buttonLogin.addActionListener(new MyActionListener());
		textUserId.addActionListener(new MyActionListener());
		textIpAddress.addActionListener(new MyActionListener());
		textPortNumber.addActionListener(new MyActionListener());
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
	}
	
	//부모에게 포트번호 ip주소 전달
	class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String userId = textUserId.getText().trim();
			String ipAddr = textIpAddress.getText().trim();
			String portNum = textPortNumber.getText().trim();
			if(!userId.equals("") && !ipAddr.equals("") && !portNum.equals("")) {
				setVisible(false);
				parent.loginAndGoToTitle(userId, ipAddr, portNum);
			}
		}	
	}

}
