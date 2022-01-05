import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//�α��� ������ ���� ���̵�, ip�ּ�, ��Ʈ��ȣ�� �Է��ؾ� �Ѵ�
public class TetrisFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	// JPanel�� ������Ʈ���� �����ϱ� ���� ����
	private TetrisGamePanel gamePanel;
	private TetrisWatingRoomPanel waitingPanel;
	
	private int [] alreadySet = {0, 0, 0, 0, 0, 0, 0, 0, 0}; //���� ���� ��ȣ�� �������� 1�� ������ 2��° ��Ұ� 1�� set
	private boolean keyReady = true; //Ű �̺�Ʈ�� ������ �ֱ� ���� ����
	private int [] randomNumberArray = new int[5]; 
	private volatile int [][] fallBlocks = new int[4][2]; //���� �������� �ִ� ������ ��ġ ����
	private int fallBlockLength; //�������� ����� ũ�� 3ĭ¥�� �Ǵ� 4ĭ¥��
	private int currentBlockNumber; //���� ��� ���� - �������� ���°�
	private int centerX; //ȸ���߽� x��
	private int centerY; //ȸ���߽� y��
	
	private static final int UP = 0; 
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	private static final int SPACE = 4;
	
	private CreateThread cthread;
	private boolean isDead = false; //������ ����������� �����̴� �����带 �����
	private boolean isFirst = true; 
	private boolean gameRunning = false; //������ ���� �Ǿ�����?
	private int ID; //�������� �����ϴ� ���� ID
	private String userId; //user name
	private String ipAddr;
	private String portNum;
	private char [] blockType = {'O', 'L', 'J', 'I', 'Z', 'S', 'T', 'V', '-'};
	
	private ImageIcon Emo1ImgIcon = new ImageIcon("image/emoticon1.png");
	private Image Emo1Img = Emo1ImgIcon.getImage();
	private ImageIcon Emo2ImgIcon = new ImageIcon("image/emoticon2.png");
	private Image Emo2Img = Emo2ImgIcon.getImage();
	private ImageIcon Emo3ImgIcon = new ImageIcon("image/emoticon3.png");
	private Image Emo3Img = Emo3ImgIcon.getImage();
	private ImageIcon Item1ImgIcon = new ImageIcon("image/item1.png");
	private Image Item1Img = Item1ImgIcon.getImage();
	private ImageIcon Item2ImgIcon = new ImageIcon("image/item2.png");
	private Image Item2Img = Item2ImgIcon.getImage();
	private ImageIcon Item3ImgIcon = new ImageIcon("image/item3.png");
	private Image Item3Img = Item3ImgIcon.getImage();
	private ImageIcon Item2ImgIcon2;
	private ImageIcon Item3ImgIcon2;
	private ImageIcon connectingImgIcon = new ImageIcon("image/connecting.png");
	private Image connectingImg = connectingImgIcon.getImage();
	private ImageIcon disconnectImgIcon = new ImageIcon("image/disconnect.png");
	private Image disconnectImg = disconnectImgIcon.getImage();
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private String [] playerList = new String [4]; //�÷��̾���� �̸����� ����
	private String rival; //���ع��� ���¿� ���
	private char[][] rivalStatus = new char[10][20]; //���� ��� ���¸� ����
	private int countAttackLine = 0; //������ ���� ��
	private int countAttackFromRival = 0; //���� ���� ���� ��
	private int attackCount = 0; //
	private int removeLine = 0; //������ �� �� - ������ ���Ž� ����/�����ۻ���
	private int currentItem = 0; //���� ��밡�� �������� �������� ������ 0
	private boolean spinable = true; //false�� ȸ�� �Ұ�
	private int speed = 1000; //�������� �ӵ�
	private int rank = 4; //����
	private int readyPlayers = 0; //�غ�� �÷��̾��� ��
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TetrisFrame frame = new TetrisFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private TetrisFrame() {
		setTitle("TETRIS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 300, 476, 291);
		setContentPane(new TetrisLoginPanel(this));
		this.setResizable(false);
		resizeIcon();
	}
	
	// �������� �α��� �õ�
	public void loginAndGoToTitle(String userId, String ipAddr, String portNum) {
		this.userId = userId;
		this.ipAddr = ipAddr;
		this.portNum = portNum;
		waitingPanel = new TetrisWatingRoomPanel(this);
		
		try {
			socket = new Socket(ipAddr, Integer.parseInt(portNum));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			Data obcm = new Data(userId, "100");
			WriteData(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}	
	}
	
	public synchronized void WriteData(Data data) {
		try {
		    oos.writeObject(data.code);
		    oos.reset();
		    oos.writeObject(data.username);
		    oos.reset();
		    if (data.code.equals("202")) {
			    oos.writeObject(data.chatMsg);
		    }
		    else if (data.code.equals("401")) {
			    oos.writeObject(data.blockStatus);
			    oos.reset();
		    	oos.writeObject(data.itemStatus);
		    }
		    else if (data.code.equals("402")) {
			    oos.writeObject(data.attacklines);  
		    }
		    else if (data.code.equals("403")) {
			    oos.writeObject(data.item);
		    }
		    else if (data.code.equals("404")) {
			    oos.writeObject(data.emoticon);
		    }
		    oos.reset();
		    oos.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
			try {
				oos.close();
				socket.close();
				ois.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public Data ReadData() {
		Object obj = null;
		Data data = new Data("", "");
		try {
			obj = ois.readObject();
			data.code = (String) obj;
			obj = ois.readObject();
			data.username = (String) obj;
			
			// 101�� �ڵ�� �α��ο� ���� �ߴٴ� �ǹ��̸� ������ �α��� �����ÿ��� ���� - ��������Ʈ�� �� �������� ������Ʈ
			if (data.code.equals("101")) {
				obj = ois.readObject();
				data.userList = (String[]) obj;
			}
			// ������ ���� �ڽ��� ���� ID �ޱ�
			else if (data.code.equals("103")) {
				obj = ois.readObject();
				data.ID = (int) obj;
			}
			//ä�� �޽���
			else if (data.code.equals("202")) {
				obj = ois.readObject();
				data.chatMsg = (String) obj;
			}
			//������ ��� ���¿� ������ ���°� ���޵�
			else if (data.code.equals("401")) {
				obj = ois.readObject();
				data.blockStatus = (char[][]) obj;
				obj = ois.readObject();
				data.itemStatus = (boolean[]) obj;
			}
			//���濡�� ���ݹ��� ���μ�
			else if (data.code.equals("402")) {
				obj = ois.readObject();
				data.attacklines = (int) obj;
			}
			//���� ������ ��ȣ
			else if (data.code.equals("403")) {
				obj = ois.readObject();
				data.item = (int) obj;
			}
			//���� �̸�Ƽ�� ��ȣ
			else if (data.code.equals("404")) {
				obj = ois.readObject();
				data.emoticon = (int) obj;
			}//���� �α׾ƿ��� ���� - �� ���������� ��������Ʈ ������Ʈ��
			else if (data.code.equals("600")) {
				obj = ois.readObject();
				data.userList = (String[]) obj;
			}
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			try {
				oos.close();
				socket.close();
				ois.close();
				socket = null;
				return null;
			} catch (IOException e1) {
				e1.printStackTrace();
				try {
					oos.close();
					socket.close();
					ois.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				socket = null;
				return null;
			}
		}
		return data;
	}
	
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				synchronized(this) {
					Data data = ReadData();
					if (data==null)
						break;
					if (socket == null)
						break;
					switch (data.code) {
					case "101":
						//��������Ʈ ������Ʈ
						int count = 0;
						setContentPane(waitingPanel);
						for(int i=0; i<data.userList.length; i++) {
							if(data.userList[i]!=null && !data.userList[i].equals(userId)) {
								playerList[count++] = data.userList[i];
							}
						}
						rank = count + 1;
						WriteData(new Data(userId, "103"));		
						userlist();
						break;
					case "102":
						//�α��� ���� ����
						System.exit(0);
						break;
					case "103":
						//���̵� �������� ���� ����
						ID = data.ID;
						moveRoomPanel((ID%4));
						waitingPanel.IDBox.setText(Integer.toString(ID));
						break;
					case "200":
						//���� �������� �غ���·� �ٲ�
						readyPlayers++;
						for(int i=0; i<3; i++) {
							if(playerList[i]!=null && playerList[i].equals(data.username))
								waitingPanel.setReady(i+1);
						}
						break;
					case "201":
						//���� ������ ����
						readyPlayers--;
						for(int i=0; i<3; i++) {
							if(playerList[i]!=null && playerList[i].equals(data.username))
								waitingPanel.setNotReady(i+1);
						}
						break;
					case "202":
						//ä�� ���
						waitingPanel.textAreaChat.append("[" + data.username + "] " + data.chatMsg + "\n");
						waitingPanel.textAreaChat.setCaretPosition(waitingPanel.textAreaChat.getText().length());
						break;
					case "300":
						//���� ����
						gameStart();
						break;
					case "401":
						//���� ���� ������Ʈ
						if (!gameRunning) break;
						rival = data.username;
						for(int i=0; i<10; i++) {
							for(int j=0; j<20; j++) {
								rivalStatus[i][j] = data.blockStatus[i][j];
							}
						}
						updateRivalStatus(data.itemStatus[0], data.itemStatus[1]);
						break;
					case "402":
						//�Ʒ� ���� �߰�
						if (!gameRunning) break;
						if(!data.username.equals(userId))
							countAttackFromRival += data.attacklines;
						break;
					case "403":
						//���濡�� ������
						if (!gameRunning) break;
						if(data.item == 1) countAttackFromRival += 2;
						new ItemFromRival(data.item).start();
						attackCount++;
						break;
					case "404":
						//��� �̸�Ƽ�� �ڽ� ����
						if (!gameRunning) break;
						if(!data.username.equals(userId)) {
							for(int i=0; i<3; i++) 
								if(playerList[i]!=null && playerList[i].equals(data.username))
									showEmoticon(i, data.emoticon);		
						}
						break;
					case "405":
						//������ ���� �޽��� ��ũ�� ���
						if (!gameRunning) break;
						if(!isDead) {
							rank--;
							for(int i = 0; i<3; i++) {
								if(playerList[i]!=null && playerList[i].equals(data.username)) {
									rivalDead(i);
								}
							}
						}
						if(rank == 1) {
							isDead = true;
							WriteData(new Data(userId, "500"));
							gameEnd();
						}
						break;
					case "500":
						//���� ���� �޽���
						if (!gameRunning) break;
						gameEnd();
						break;
					case "600":
						//������ �α׾ƿ� �޽��� ��������Ʈ ������Ʈ��
						if (gameRunning) {
							for(int i=0; i<3; i++) {
								if(playerList[i]!=null && playerList[i].equals(data.username))
									gamePanel.networkStatusBox[i].setIcon(disconnectImgIcon);
							}
							rank--;
							if(rank == 1) {
								isDead = true;
								WriteData(new Data(userId, "500"));
								gameEnd();
							}
						}
						else {
							for(int i=0; i<playerList.length; i++) {
								playerList[i] = null;
							}
							count = 0;
							for(int i=0; i<data.userList.length; i++) {
								if(data.userList[i]!=null && !data.userList[i].equals(userId)) {
									playerList[count++] = data.userList[i];
								}
							}
							readyPlayers = 0;
							for(int i=0; i<4; i++) {
								waitingPanel.labelStatus[i].setIcon(null);
							}
							waitingPanel.notReady();
							rank = count + 1;
							WriteData(new Data(userId, "103"));
							userlist();	
						}
						break;
					}
				}
			}
		}
	}
	
	//1�ʸ��� �ڽ� ���¸� ���濡�� ������ ������
	class SendStatus extends Thread {
		public void run() {
			while(true) {
				if(isDead || !gameRunning) return;
				if(isFirst == true) {
					try {
						sleep(((ID%4)+1)*200);
					} catch(InterruptedException e) { return; }
					isFirst = false;
				}
				if(isDead || !gameRunning) return;
				
				sendStatusToRival();
				
				if(isDead || !gameRunning) return;
				try {
					sleep(1000);
				} catch(InterruptedException e) { return; }
				if(isDead || !gameRunning) return;
			}			
		}
	}
	
	//���ع��� ������ ���� ������
	class ItemFromRival extends Thread {
		int n;
		public ItemFromRival(int n) {
			this.n = n;
		}
		public void run() {
			if(n == 1) {
				gamePanel.attackFromRival.setIcon(Item1ImgIcon);
			}
			else if(n == 2) {
				speed = 200;
				gamePanel.attackFromRival.setIcon(Item2ImgIcon);
			}
			else if(n == 3) {
				spinable = false;
				gamePanel.attackFromRival.setIcon(Item3ImgIcon);
			}
			
			if(isDead || !gameRunning) return;
			try {
				sleep(5000);
			} catch(InterruptedException e) { return; }
			if(isDead || !gameRunning) return;
			
			attackCount--;
			if(attackCount > 0) return;
			
			if(n == 2) {
				speed = 1000;
			}
			else if(n == 3) {
				spinable = true;
			}
			gamePanel.attackFromRival.setIcon(null);
		}
	}
	
	public void enterWaitingRoom(String title, String passWord) {
		setContentPane(new TetrisWatingRoomPanel(this));
	}
	
	public void moveRoomPanel(int n) {
		switch(n) {
		case 0: setBounds(400, 300, 476, 291); break;
		case 1: setBounds(876, 300, 476, 291); break;
		case 2: setBounds(400, 591, 476, 291); break;
		case 3: setBounds(876, 591, 476, 291); break;
		default: setBounds(300, 300, 476, 291); break;
		}
	}
	
	//������ ũ�� ������
	public void resizeIcon() {
		Emo1Img = Emo1Img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Emo1ImgIcon = new ImageIcon(Emo1Img);
		Emo2Img = Emo2Img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Emo2ImgIcon = new ImageIcon(Emo2Img);
		Emo3Img = Emo3Img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Emo3ImgIcon = new ImageIcon(Emo3Img);
		Item1Img = Item1Img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Item1ImgIcon = new ImageIcon(Item1Img);
		Item2Img = Item2Img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Item2ImgIcon = new ImageIcon(Item2Img);
		Item3Img = Item3Img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		Item3ImgIcon = new ImageIcon(Item3Img);
		Item2Img = Item2Img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		Item3Img = Item3Img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		Item2ImgIcon2 = new ImageIcon(Item2Img);
		Item3ImgIcon2 = new ImageIcon(Item3Img);
		connectingImg = connectingImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		connectingImgIcon = new ImageIcon(connectingImg);
		disconnectImg = disconnectImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		disconnectImgIcon = new ImageIcon(disconnectImg);
	}
	
	public Color getColor(char type) {
		switch(type) {
			case 'O' : return new Color(255, 255, 0);
			case 'L' : return new Color(255, 116, 0);
			case 'J' : return new Color(0, 0, 255);
			case 'I' : return new Color(0, 255, 255);
			case 'Z' : return new Color(255, 0, 0);
			case 'S' : return new Color(0, 255, 0);
			case 'T' : return new Color(255, 0, 255);
			case 'V' : return new Color(176, 0, 255);
			case '-' : return new Color(58, 146, 98);
			case '.' : return new Color(128, 128, 128);
			default : return null;
		}
	}	
	
	public ImageIcon getEmoticon(int type) {
		switch(type) {
			case 1: return Emo1ImgIcon;
			case 2: return Emo2ImgIcon;
			case 3: return Emo3ImgIcon;
			default: return null;
		}
	}
	
	public ImageIcon getItemIcon(int type) {
		switch(type) {
			case 1: return Item1ImgIcon;
			case 2: return Item2ImgIcon;
			case 3: return Item3ImgIcon;
			default: return null;
		}
	}
	//��������Ʈ GUI ������Ʈ
	public void userlist() {
		waitingPanel.labelUsers[0].setText(userId);
		for(int i=0; i<3; i++) {
			waitingPanel.labelUsers[i+1].setText(playerList[i]);
		}
		if(playerList[0] == null) waitingPanel.buttonStart.setEnabled(true);
	}
	
	//������ ������ ���� ������Ʈ �ϱ�
	public void updateRivalStatus(boolean item2, boolean item3) {
		int num = -1;
		for(int i=0; i<3; i++) {
			if(rival.equals(playerList[i])) num = i;
		}
		if(num > -1) {
			for(int i=0; i<10; i++) {
				for(int j=0; j<20; j++) {
					gamePanel.drawRivalBlock(num, i, j, rivalStatus[i][j], getColor(rivalStatus[i][j]));
				}
			}
		}
		if(item2) gamePanel.rivalItemBox[num][0].setIcon(Item2ImgIcon2);
		else gamePanel.rivalItemBox[num][0].setIcon(null);
		if(item3) gamePanel.rivalItemBox[num][1].setIcon(Item3ImgIcon2);
		else gamePanel.rivalItemBox[num][1].setIcon(null);
		
		gamePanel.repaint();
	}
	
	//�ڽ� Ȥ�� ������ �̸�Ƽ�� ������Ʈ
	public void showEmoticon(int player, int type) {
		if(player == -1) {
			gamePanel.myEmoticon.setIcon(getEmoticon(type));
		}
		else {
			gamePanel.rivalEmoticon[player].setIcon(getEmoticon(type));
		}
		gamePanel.repaint();
	}
	
	//���� ���� ó��
	public void rivalDead(int n) {
		for(int i=0; i<10; i++) {
			for(int j=0; j<20; j++) {
				if(gamePanel.rivalBox[n][i][j].type != ' ')
					gamePanel.rivalBox[n][i][j].setBox('.', getColor('.'));
			}
		}
	}
	
	//ä�� ������
	public void sendChat(String chatMsg) {
		Data data = new Data(userId, "202");
		data.chatMsg = chatMsg;
		WriteData(data);
	}
	
	//�غ� �Ǿ����� ������ �Լ�
	public void sendReady() {
		readyPlayers++;
		if(readyPlayers == rank) WriteData(new Data(userId, "300"));
		else WriteData(new Data(userId, "200"));
	}
	
	//�غ���� �Ǿ����� ������ �Լ�
	public void sendNotReady() {
		WriteData(new Data(userId, "201"));
		readyPlayers--;
	}
	
	//��ο��� ���ӽ��� �޽��� ������ - ���������� �غ� ��ư ������
	public void sendGameStart() {
		WriteData(new Data(userId, "300"));
	}

	//�ڽ��� ��� ���� ������
	public synchronized void sendStatusToRival() {
		Data data = new Data(userId, "401");
		for(int i=0; i<10; i++) {
			for(int j=0; j<20; j++) {
				data.blockStatus[i][j] = gamePanel.box[i][j].getType();
			}
		}
		if(speed == 1000) data.itemStatus[0] = false;
		else data.itemStatus[0] = true;
		
		if(spinable) data.itemStatus[1] = false;
		else data.itemStatus[1] = true;
		WriteData(data);
	}
	
	//������ ������
	public void sendItem(int n) {
		Data data = new Data(userId, "403");
		data.item = n;
		WriteData(data);
	}
	
	// �̸�Ƽ�� ������
	public void sendEmoticon(int n) {
		Data data = new Data(userId, "404");
		data.emoticon = n;
		WriteData(data);
		
		showEmoticon(-1, n);
	}
	
	//�α׾ƿ� �޽��� ������ ����
	public void gameExit() {
		WriteData(new Data(userId, "600"));
		System.exit(0);
	}
	
	//���� ���� - JFrameũ�⸦ �� ũ�� �ٲٰ� ��Ŀ���� �༭ Ű�Է� �ϵ��� ��
	//�̸����� �ڽ��� ���� ���� ���� �迭
	//���� ������� ��뿡�� �ڽ� ���� ������ ������ ����
	public void gameStart() {
		switch((ID%4)) {
		case 0: setBounds(100, 0, 953, 583); break;
		case 1: setBounds(950, 0, 953, 583); break;
		case 2: setBounds(100, 450, 953, 583); break;
		case 3: setBounds(950, 450, 953, 583); break;
		default: setBounds(100, 0, 953, 583); break;
		}
		waitingPanel = null;
		gamePanel = new TetrisGamePanel(this);
		setContentPane(gamePanel);
		Random random = new Random();
		random.nextInt(9);
		for(int i = 0; i<randomNumberArray.length; i++) {
			randomNumberArray[i] = random.nextInt(9);
			gamePanel.drawhintBox(i, blockType[randomNumberArray[i]], getColor(blockType[randomNumberArray[i]]));
		}
		for(int i=0; i<3; i++) {
			if(playerList[i] != null) {
				gamePanel.networkStatusBox[i].setIcon(connectingImgIcon);
				gamePanel.nameBox[i].setText(playerList[i]);
			}
		}
		gameRunning = true;
		cthread = new CreateThread();
		cthread.start();
		new SendStatus().start();
		gamePanel.setFocusable(true);
		gamePanel.requestFocus();
		gamePanel.addKeyListener(new MyKeyListener());
	}
	
	//������ ��ó��
	public void gameEnd() {
		gameRunning = false;
		JOptionPane.showMessageDialog(this, "������ "+Integer.toString(rank)+"�� �Դϴ�.", " Message", JOptionPane.INFORMATION_MESSAGE);
		WriteData(new Data(userId, "600"));
		System.exit(0);
	}
	
	//���� ���� üũ�ϴ� �Լ� ���� ���� �� ���濡�� ������ �����ϰ� ���� ���� ���Ž� �������� �� �� �ְԵȴ�
	public void checkLine() {
		for(int i=0; i<10; i++) {
			if(gamePanel.box[i][19].getStatus().equals("AlreadySet")) {
				isDead = true;
				WriteData(new Data(userId, "405"));
				for(int j=0; j<10; j++) {
					for(int k=0; k<23; k++) {
						if(gamePanel.box[j][k].getStatus().equals("AlreadySet")) {
							gamePanel.drawBlock(j, k, '.', getColor('.'), "AlreadySet");
						}
					}
				}
				return;
			}
		}
		
		for(int i=0; i<20; i++) {
			for(int j=0; j<10; j++) {
				if(!gamePanel.box[j][i].getStatus().equals("AlreadySet")) break;
				if(j==9) {
					clearLine(i--);
					countAttackLine++;
					removeLine++;
				}
			}
		}
		
		if(removeLine >= 2) {
			if(currentItem == 0) setItem();
			removeLine -= 2;
		}
		
		if(countAttackLine >= 2) {
			Data data = new Data(userId, "402");
			data.attacklines = countAttackLine -1;
			countAttackLine = 0;
			WriteData(data);
		}
		gamePanel.repaint();
	}
	
	//���� ���� �Լ�
	public void clearLine(int line) {
		for(int i=line; i<19; i++) {
			for(int j=0; j<10; j++) {
				gamePanel.drawBlock(j, i, gamePanel.box[j][i+1].getType(), 
						getColor(gamePanel.box[j][i+1].getType()), 
						gamePanel.box[j][i+1].getStatus());
			}
		}
		for(int i=0; i<10; i++) {
			gamePanel.drawBlock(i, 19, ' ', null, "Empty");
		}
		
	}
	
	//�������� ��� �Լ�
	public void setItem() {
		Random r = new Random();
		r.nextInt(3);
		currentItem = r.nextInt(3)+1;
		gamePanel.itemBox.setIcon(getItemIcon(currentItem));
	}
	
	//���濡�� ���� �޴� �Լ� ���� ���� ��ŭ ������ ���� �ö󰣴�
	public void attackFromRival(int lines) {
		for(int i=19; i>=lines; i--) {
			for(int j=0; j<10; j++) {
				if(!gamePanel.box[j][i-lines].getStatus().equals("CurrentFall") || !gamePanel.box[j][i].getStatus().equals("CurrentFall"))
					gamePanel.drawBlock(j, i, 
						gamePanel.box[j][i-lines].getType(), 
						getColor(gamePanel.box[j][i-lines].getType()),
						gamePanel.box[j][i-lines].getStatus());
			}
		}
		
		for(int i=0; i<lines; i++) {
			Random r = new Random();
			int n = r.nextInt(10);
			for(int j=0; j<10; j++) {
				if(n!=j)
					gamePanel.drawBlock(j, i, '.', getColor('.'), "AlreadySet");
				else
					gamePanel.drawBlock(j, i, ' ', null, "Empty");
			}
		}
			
	}
	
	//��������Ű, �����̽��� - �����̱�
	//ZXCŰ - �̸�Ƽ�� ������
	//ShiftŰ - ������ ���
	class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (isDead) return;
			if(!gameRunning) return;
			
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				if(keyReady) new MoveThread(UP).start();
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if(keyReady) new MoveThread(DOWN).start();
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if(keyReady) new MoveThread(LEFT).start();
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if(keyReady) new MoveThread(RIGHT).start();
			}
			else if (e.getKeyCode() == KeyEvent.VK_SPACE && spinable==true) {
				if(keyReady) new MoveThread(SPACE).start();
			}
		}
		public void keyReleased(KeyEvent e) {
			if(!gameRunning) return;
			
			if (e.getKeyCode() == KeyEvent.VK_Z) {
				sendEmoticon(1);
			}
			else if (e.getKeyCode() == KeyEvent.VK_X) {
				sendEmoticon(2);
			}
			else if (e.getKeyCode() == KeyEvent.VK_C) {
				sendEmoticon(3);
			}
			else if (e.getKeyCode() == KeyEvent.VK_SHIFT && !isDead) {
				if(currentItem != 0) {
					sendItem(currentItem);
					gamePanel.itemBox.setIcon(null);
					currentItem = 0;
				}
			}
		}
	}
	
	//�����̴� ������ Ű�Է� ��Ÿ���� ���� ������� ����
	class MoveThread extends Thread {
		int key;
		
		public MoveThread(int key) {
			this.key=key;
		}
		
		public void run() {
			if(!gameRunning) return;
			keyReady = false;
			synchronized(this) {
				moveBlock();
			}
			try {
				sleep(50);
			} catch(InterruptedException e) { return; }
			keyReady = true;
		}
		
		public void moveBlock() {	
			fallBlockLength = 0;
			for(int i = 0; i < 23; i++) {
				for(int j = 0; j < 10; j++) {
					if(gamePanel.getBlockStatus(j, i).equals("CurrentFall")) {
						fallBlocks[fallBlockLength][0] = j;
						fallBlocks[fallBlockLength][1] = i;
						fallBlockLength++;
					}
				}
			}
				
			if(key == UP) {
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1],' ', null, "Empty");
				}
				while(true) {
					boolean flag = false;
					for(int i=0; i<fallBlockLength; i++) {
						if (fallBlocks[i][1] == 0) {
							flag = true;
							break;
						}
						if(gamePanel.getBlockStatus(fallBlocks[i][0], fallBlocks[i][1]-1).equals("AlreadySet")) {
							flag = true;
							break;
						}
					}
					if(flag) break;
					for(int j=0; j<fallBlockLength; j++) {
						fallBlocks[j][1]--;
					}
					centerY--;
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1], blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]), "CurrentFall");
				}
				checkLine();
			}
			
			if(key == DOWN) {
				for(int i=0; i<fallBlockLength; i++) {
					if (fallBlocks[i][1] == 0) {
						for(int k = 0; k < fallBlockLength; k++) {
							gamePanel.box[fallBlocks[k][0]][fallBlocks[k][1]].setStatus("AlreadySet");
						}
						return;
					}
					if(gamePanel.getBlockStatus(fallBlocks[i][0], fallBlocks[i][1]-1).equals("AlreadySet")) {
						for(int k = 0; k < fallBlockLength; k++) {
							gamePanel.box[fallBlocks[k][0]][fallBlocks[k][1]].setStatus("AlreadySet");
						}
						return;
					}
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1],' ', null, "Empty");
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1]-1, blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]), "CurrentFall");
				}
				centerY--;
			}
			
			if(key == LEFT) {
				for(int i=0; i<fallBlockLength; i++) {
					if (fallBlocks[i][0]-1 < 0) return;
					if(gamePanel.getBlockStatus(fallBlocks[i][0]-1, fallBlocks[i][1]).equals("AlreadySet")) return;
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1],' ', null, "Empty");
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0]-1, fallBlocks[k][1], blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]), "CurrentFall");
				}
				centerX--;
			}
			
			else if(key == RIGHT) {
				for(int i=0; i<fallBlockLength; i++) {
					if (fallBlocks[i][0]+1 > 9) return;
					if(gamePanel.getBlockStatus(fallBlocks[i][0]+1, fallBlocks[i][1]).equals("AlreadySet")) return;
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1],' ', null, "Empty");
				}
				for(int k = 0; k < fallBlockLength; k++) {
					gamePanel.drawBlock(fallBlocks[k][0]+1, fallBlocks[k][1], blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]), "CurrentFall");
				}
				centerX++;
			}
			
			else if(key == SPACE) {
				int f = gamePanel.rotateBlock(centerX, centerY, blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]));
				if(f == 0) {
					centerX++;
				}
				else if(f == 1) {
					centerX--;
				}
				else if(f == 2) {
					centerY++;
				}
				else if(f == 3) {
					centerX += 2;
				}
				else if(f == 4) {
					centerX -= 2;
				}
				else if(f == 5) {
					centerX += 3;
				}
				else if(f == 6) {
					centerX -= 3;
				}
			}
			gamePanel.repaint();
		}
	}
	
	//�������� ����� �����ϰ� ����Ʈ���� ������ �ٴۿ� �������� �ݺ��� ����ǰ� �ٽ� �����ϰ� ����Ʈ��
	class CreateThread extends Thread {
		int count = 0;
		Random random;
		
		public CreateThread() {
			random = new Random();
		}
		
		public void run() {
			while(true) {
				if(!gameRunning) return;
				if(isDead) break;
				int tmp;
					
				if(count == 7) {
					for(int i=0; i<9; i++) alreadySet[i] = 0;
					count = 0;
				}				
				while(true) {
					tmp = random.nextInt(9);
					if(alreadySet[tmp] == 0) {
						alreadySet[tmp] = 1;
						count++;
						break;
					}
				}
				currentBlockNumber = randomNumberArray[0];
				for(int i=0;i<randomNumberArray.length - 1;i++) {
					randomNumberArray[i] = randomNumberArray[i+1];
				}
				randomNumberArray[randomNumberArray.length-1] = tmp;
				
				for(int i=0;i<randomNumberArray.length;i++) {
					gamePanel.drawhintBox(i, blockType[randomNumberArray[i]], getColor(blockType[randomNumberArray[i]]));	
				}			
				
				createBlock();
				fallBlock();
			}
		}
		
		public void createBlock() {
			centerX = 4; 
			centerY = 20;
			if(currentBlockNumber == 0 || currentBlockNumber == 7) centerY = 19;
		
			gamePanel.drawEntireBlock(centerX, centerY, blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]), "CurrentFall");
		}
				
		public void fallBlock() {
			boolean flag = false;
			if(isDead || !gameRunning) return;
			while(true) {
				try {			
					sleep(speed);
				} catch(InterruptedException e) { return; }
				if(isDead || !gameRunning) return;
				synchronized(this) {
					fallBlockLength = 0;
				
					for(int i = 0; i < 23; i++) {
						for(int j = 0; j < 10; j++) {
							if(gamePanel.getBlockStatus(j, i).equals("CurrentFall")) {
								fallBlocks[fallBlockLength][0] = j;
								fallBlocks[fallBlockLength][1] = i;
								fallBlockLength++;
							}
						}
					}
					if (fallBlockLength <= 0) {
						checkLine();
						break;
					}
				
					for(int i = 0; i < fallBlockLength; i++) {
						if (fallBlocks[i][1] == 0) {
							for(int k = 0; k < fallBlockLength; k++) {
								gamePanel.box[fallBlocks[k][0]][fallBlocks[k][1]].setStatus("AlreadySet");
							}
							flag = true;
							break;
						}
						else if(gamePanel.getBlockStatus(fallBlocks[i][0], fallBlocks[i][1]-1).equals("AlreadySet")) {
							for(int k = 0; k < fallBlockLength; k++) {
								gamePanel.box[fallBlocks[k][0]][fallBlocks[k][1]].setStatus("AlreadySet");
							}
							flag = true;
							break;
						}
					}
					if(flag == true) {
						checkLine();
						break;
					}
				
					for(int k = 0; k < fallBlockLength; k++) {
						gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1],' ', null, "Empty");
					}
					for(int k = 0; k < fallBlockLength; k++) {
						gamePanel.drawBlock(fallBlocks[k][0], fallBlocks[k][1]-1, blockType[currentBlockNumber], getColor(blockType[currentBlockNumber]), "CurrentFall");
					}
					centerY--;
					
					gamePanel.repaint();
					
				}
			}
			if(countAttackFromRival > 0) {
				attackFromRival(countAttackFromRival);
				countAttackFromRival = 0;
			}
			
		}
		
	}
	
}


