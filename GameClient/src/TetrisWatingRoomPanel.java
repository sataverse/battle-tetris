import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

//대기방 패널 로그인성공시 panel 교체
public class TetrisWatingRoomPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private String [] users = new String[4];
	public JLabel [] labelUsers = new JLabel[4];
	public JLabel [] labelStatus = new JLabel[4];
	public JLabel [] labelKick = new JLabel[4];
	public JTextArea textAreaChat;
	public JButton buttonStart;
	private int userNum = 0;
	private boolean isReady = false;

	private TetrisFrame parent;
	private LineBorder border = new LineBorder(Color.GRAY);
	public JLabel IDBox;
	
	private ImageIcon backgroundImgIcon = new ImageIcon("image/background2.png");
	private ImageIcon backImgIcon = new ImageIcon("image/back.png");
	private ImageIcon controllerImgIcon = new ImageIcon("image/controller.png");
	private ImageIcon readyImgIcon = new ImageIcon("image/ready.png");
	private ImageIcon notReadyImgIcon = new ImageIcon("image/kick.png");
	private ImageIcon chatImgIcon = new ImageIcon("image/chat.png");
	
	private Image backgroundImg = backgroundImgIcon.getImage();
	private Image backImg = backImgIcon.getImage();
	private Image controllerImg = controllerImgIcon.getImage();
	private Image readyImg = readyImgIcon.getImage();
	private Image notReadyImg= notReadyImgIcon.getImage();
	private Image chatImg = chatImgIcon.getImage();
	

	public TetrisWatingRoomPanel(TetrisFrame parent) {
		this.parent = parent;
		
		backImg = backImg.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		backImgIcon = new ImageIcon(backImg);
		controllerImg = controllerImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		controllerImgIcon = new ImageIcon(controllerImg);
		readyImg = readyImg.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		readyImgIcon = new ImageIcon(readyImg);
		notReadyImg = notReadyImg.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		notReadyImgIcon = new ImageIcon(notReadyImg);
		chatImg = chatImg.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		chatImgIcon = new ImageIcon(chatImg);
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		IDBox = new JLabel();
		IDBox.setBounds(10, 10, 20, 20);
		IDBox.setBackground(Color.WHITE);
		IDBox.setOpaque(true);
		this.add(IDBox);
		
		users[0] = "USER1";
		addUser(users[0]);
		users[1] = new String("USER2");
		addUser(users[1]);
		users[2] = new String("USER3");
		addUser(users[2]);
		users[3] = new String("USER4");
		addUser(users[3]);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(230, 20, 200, 180);
		this.add(scrollPane);
		
		textAreaChat = new JTextArea();
		textAreaChat.setEditable(false);
		scrollPane.setViewportView(textAreaChat);
		
		JLabel textChatIcon = new JLabel(chatImgIcon);
		textChatIcon.setBounds(230, 205, 25, 25);
		textChatIcon.setBackground(Color.WHITE);
		textChatIcon.setOpaque(true);
		textChatIcon.setBorder(border);
		this.add(textChatIcon);
		
		JTextField textChat = new JTextField("");
		textChat.setBounds(255, 205, 175, 25);
		this.add(textChat);
		
		JButton buttonBack = new JButton(backImgIcon);
		buttonBack.setBackground(Color.WHITE);
		buttonBack.setOpaque(true);
		buttonBack.setBounds(30, 175, 50, 50);
		this.add(buttonBack);
		
		buttonStart = new JButton();
		buttonStart.setIcon(readyImgIcon);
		buttonStart.setBackground(Color.WHITE);
		buttonStart.setOpaque(true);
		buttonStart.setBounds(100, 175, 50, 50);
		this.add(buttonStart);
		
		textChat.addActionListener(new ChatActionListener());
		buttonBack.addActionListener(new BackActionListener());
		buttonStart.addActionListener(new StartActionListener());
	}
	
	// 유저리스트 및 준비상태에 대한 JLabel들
	public void addUser(String name) {
		labelUsers[userNum] = new JLabel();
		labelUsers[userNum].setBounds(30, 20 + 30*userNum, 90, 30);
		labelUsers[userNum].setBackground(Color.WHITE);
		labelUsers[userNum].setOpaque(true);
		labelUsers[userNum].setBorder(border);
		add(labelUsers[userNum]);
		
		labelStatus[userNum] = new JLabel();
		labelStatus[userNum].setBounds(120, 20 + 30*userNum, 30, 30);
		labelStatus[userNum].setBackground(Color.WHITE);
		labelStatus[userNum].setOpaque(true);
		labelStatus[userNum].setBorder(border);
		add(labelStatus[userNum]);
		
		userNum++;
	}
	
	// 버튼 이미지를 준비 이미지로 바꿈
	public void notReady() {
		isReady = false;
		buttonStart.setIcon(readyImgIcon);
	}
	
	// 상대방 준비 상태를 set
	public void setReady(int n) {
		labelStatus[n].setIcon(controllerImgIcon);
	}
	
	// 상대방 준비 상태를 끔
	public void setNotReady(int n) {
		labelStatus[n].setIcon(null);
	}
	
	// TextField에 엔터키가 입력시 서버에게 내용 전달
	class ChatActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JTextField tf = (JTextField)e.getSource();
			if(!tf.getText().equals(""))
				parent.sendChat(tf.getText());
			tf.setText("");
		}	
	}
	
	// 게임 나가기 버튼
	class BackActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			parent.gameExit();
		}	
	}
	
	// 준비 버튼
	// 토글형식 버튼 준비, 준비 취소시 서버에게 전달
	class StartActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton b = (JButton)e.getSource();
			if(isReady) {
				b.setIcon(readyImgIcon);
				labelStatus[0].setIcon(null);
				isReady = false;
				parent.sendNotReady();
			}
			else {
				b.setIcon(notReadyImgIcon);
				labelStatus[0].setIcon(controllerImgIcon);
				isReady = true;
				parent.sendReady();
			}
		}	
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
	}

}
