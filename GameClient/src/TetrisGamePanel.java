import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// 게임시작시 교체 되는 패널임
public class TetrisGamePanel extends JPanel{
	private static final long serialVersionUID = 1L;

	private TetrisFrame parent;
	private LineBorder border = new LineBorder(Color.WHITE);
	private LineBorder border2 = new LineBorder(Color.LIGHT_GRAY, 1);
	
	public Box [][] box = new Box[10][23]; //자신 플레이 판
	public SmallBox [][][] rivalBox = new SmallBox[3][10][20]; //상대방 판
	public SmallBox [][][] hintBox = new SmallBox[5][4][5]; //미리보기 박스
	
	public JLabel itemBox; //나의 아이템 박스
	public JLabel [] networkStatusBox = new JLabel[3]; //상대방 연결상태 박스
	public JLabel [] nameBox = new JLabel[3]; //상대방 이름 박스
	public JLabel [][] rivalItemBox = new JLabel[3][2]; //상대방이 받은 아이템 박스
	public JLabel attackFromRival; //방해받은 아이템 박스
	
	public JLabel myEmoticon; //내 이모티콘
	public JLabel [] rivalEmoticon = new JLabel[3]; //상대방 이모티콘
	
	// 상자들의 기본 배경색
	public Color defaultColor1 = new Color(250, 210, 250);
	public Color defaultColor2 = new Color(250, 205, 185);
	public Color defaultColor3 = new Color(250, 250, 210);
	public Color defaultColor4 = new Color(210, 250, 220);
	public Color defaultColor5 = new Color(210, 210, 250);
	
	public TetrisGamePanel(TetrisFrame parent) {
		this.parent = parent;
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);	
		createBox();
	}
	
	// 기본 틀을 그리는 함수
	// 자신의 게임판, 힌트판, 아이템상자, 이모티콘 상자, 상대방의 게임판, 상대방의 상태판, 상대방의 이모티콘 등
	public void createBox() {
		for(int i=0; i<10; i++) {
			for(int j=0; j<23; j++) {
				box[i][j] = new Box();
				if(j>19) box[i][j].labelbox.setBounds(60 + 20*i, 478 - 20*j, 20, 20);
				else box[i][j].labelbox.setBounds(60 + 20*i, 480 - 20*j, 20, 20);
				box[i][j].labelbox.setBackground(defaultColor1);
				box[i][j].labelbox.setOpaque(true);
				if(j>19) box[i][j].labelbox.setBorder(border2);	
				else box[i][j].labelbox.setBorder(border);
				this.add(box[i][j].labelbox);
			}
		}
		
		for(int i=0; i<3; i++) {
			for(int j=0; j<10; j++) {
				for(int k=0; k<20; k++) {
					rivalBox[i][j][k] = new SmallBox();
					rivalBox[i][j][k].labelbox.setBounds(200*i + 400 + 10*j, 260 - 10*k, 10, 10);
					if(i == 0) {
						rivalBox[i][j][k].labelbox.setBackground(defaultColor3);
					}
					else if(i == 1) {
						rivalBox[i][j][k].labelbox.setBackground(defaultColor4);
					}
					else {
						rivalBox[i][j][k].labelbox.setBackground(defaultColor5);
					}
					rivalBox[i][j][k].labelbox.setOpaque(true);
					rivalBox[i][j][k].labelbox.setBorder(border);
					this.add(rivalBox[i][j][k].labelbox);
				}			
			}
		}
			
		for(int i=0; i<5; i++) {
			for(int j=0; j<4; j++) {
				for(int k=0; k<5; k++) {
					hintBox[i][j][k] = new SmallBox();
					hintBox[i][j][k].labelbox.setBounds(270 + 10*j, 140 - 10*k + i*50, 10, 10);
					hintBox[i][j][k].labelbox.setBackground(defaultColor2);
					hintBox[i][j][k].labelbox.setOpaque(true);
					hintBox[i][j][k].labelbox.setBorder(border);
					this.add(hintBox[i][j][k].labelbox);
				}
			}
		}
		
		JTextArea explainBox = new JTextArea();
		explainBox.setBounds(400, 390, 300, 110);
		explainBox.setBackground(defaultColor2);
		explainBox.setOpaque(true);
		explainBox.setBorder(border);
		explainBox.setText(" ← → : 좌우 이동\n ↑ : 하드드랍\n ↓ : 소프트드랍\n SPACE : 회전\n SHIFT : 아이템 사용\n Z X C : 이모티콘");
		explainBox.setEditable(false);
		explainBox.setFocusable(false);
		this.add(explainBox);
		
		JLabel textLabel3 = new JLabel("내 아이템");
		textLabel3.setBounds(0, 20, 60, 20);
		textLabel3.setHorizontalAlignment(JLabel.CENTER);
		this.add(textLabel3);
		
		itemBox = new JLabel();
		itemBox.setBounds(10, 40, 40, 40);
		itemBox.setBackground(defaultColor1);
		itemBox.setOpaque(true);
		itemBox.setBorder(border);
		this.add(itemBox);
		
		JLabel textLabel2 = new JLabel("받은 공격");
		textLabel2.setBounds(260, 380, 60, 20);
		textLabel2.setHorizontalAlignment(JLabel.CENTER);
		this.add(textLabel2);
		
		attackFromRival = new JLabel();
		attackFromRival.setBounds(270, 400, 40, 40);
		attackFromRival.setBackground(defaultColor2);
		attackFromRival.setOpaque(true);
		attackFromRival.setBorder(border);
		this.add(attackFromRival);
		
		JLabel textLabel1 = new JLabel("이모티콘");
		textLabel1.setBounds(260, 440, 60, 20);
		textLabel1.setHorizontalAlignment(JLabel.CENTER);
		this.add(textLabel1);
		
		myEmoticon = new JLabel();
		myEmoticon.setBounds(270, 460, 40, 40);
		myEmoticon.setBackground(defaultColor2);
		myEmoticon.setOpaque(true);
		myEmoticon.setBorder(border);
		this.add(myEmoticon);
		
		for(int i=0; i<3; i++) {
			networkStatusBox[i] = new JLabel();
			networkStatusBox[i].setBounds(390 + 200*i, 40, 20, 20);
			if(i == 0) {
				networkStatusBox[i].setBackground(defaultColor3);
			}
			else if(i == 1) {
				networkStatusBox[i].setBackground(defaultColor4);
			}
			else {
				networkStatusBox[i].setBackground(defaultColor5);
			}
			networkStatusBox[i].setOpaque(true);
			networkStatusBox[i].setHorizontalAlignment(JLabel.CENTER);
			networkStatusBox[i].setBorder(border);
			this.add(networkStatusBox[i]);
		}
		
		for(int i=0; i<3; i++) {
			nameBox[i] = new JLabel();
			nameBox[i].setBounds(410 + 200*i, 40, 60, 20);
			if(i == 0) {
				nameBox[i].setBackground(defaultColor3);
			}
			else if(i == 1) {
				nameBox[i].setBackground(defaultColor4);
			}
			else {
				nameBox[i].setBackground(defaultColor5);
			}
			nameBox[i].setOpaque(true);
			nameBox[i].setHorizontalAlignment(JLabel.CENTER);
			nameBox[i].setBorder(border);
			this.add(nameBox[i]);
		}
		
		for(int i=0; i<3; i++) {
			for(int j=0; j<2; j++) {
				rivalItemBox[i][j] = new JLabel();
				rivalItemBox[i][j].setBounds(470 + 200*i + 20*j, 40, 20, 20);
				if(i == 0) {
					rivalItemBox[i][j].setBackground(defaultColor3);
				}
				else if(i == 1) {
					rivalItemBox[i][j].setBackground(defaultColor4);
				}
				else {
					rivalItemBox[i][j].setBackground(defaultColor5);
				}
				rivalItemBox[i][j].setOpaque(true);
				rivalItemBox[i][j].setBorder(border);
				this.add(rivalItemBox[i][j]);
			}
		}
		
		for(int i=0; i<3; i++) {
			rivalEmoticon[i] = new JLabel();
			rivalEmoticon[i].setBounds(460 + 200*i, 280, 40, 40);
			if(i == 0) {
				rivalEmoticon[i].setBackground(defaultColor3);
			}
			else if(i == 1) {
				rivalEmoticon[i].setBackground(defaultColor4);
			}
			else {
				rivalEmoticon[i].setBackground(defaultColor5);
			}
			rivalEmoticon[i].setOpaque(true);
			rivalEmoticon[i].setBorder(border);
			this.add(rivalEmoticon[i]);
		}
	}
	
	// 블록 한 칸을 변경하는 함수 color값으로 null이 넘어오면 빈 블록이 되며 배경색으로 변경한다
	public void drawBlock(int x, int y, char type, Color color, String status) {
		if(color == null) color = defaultColor1;
		box[x][y].setBox(type, color, status);
	}
	
	// 상대방의 블록을 그리는 함수 n-상대방 번호
	public void drawRivalBlock(int n, int x, int y, char type, Color color) {
		if(color == null) {
			if(n == 0) color = defaultColor3;
			else if(n == 1) color = defaultColor4;
			else color = defaultColor5;
		}
		rivalBox[n][x][y].setBox(type, color);
	}
	
	// 블록을 회전 시킨 후 바뀐 회전축을 리턴하는 함수
	public int rotateBlock(int x, int y, char type, Color color) {
		
		int flag = -1;
		if(type == 'O') {
			return flag;
		}
		
		else if(type == 'I') {
			if(x==-2) {
				flag = 5;
				x+=3;
			}
			if(x==-1) {
				flag = 3;
				x+=2;
			}
			else if(x==0) {
				flag = 0;
				x+=1;
			}
			else if(x==8) {
				flag = 1;
				x=x-1;
			}
			else if(x==9) {
				flag = 4;
				x=x-2;
			}
			else if(x==10) {
				flag = 6;
				x=x-3;
			}
			else if(y<=1) {
				y++;
			}
			
			Box [] temp = new Box[16];
			temp[0] = box[x][y+1];
			temp[1] = box[x+1][y+1];
			temp[2] = box[x+1][y];
			temp[3] = box[x][y];
			
			temp[4] = box[x][y+2];
			temp[5] = box[x+1][y+2];
			temp[6] = box[x+2][y+2];
			temp[7] = box[x+2][y+1];
			temp[8] = box[x+2][y];
			temp[9] = box[x+2][y-1];
			temp[10] = box[x+1][y-1];
			temp[11] = box[x][y-1];
			temp[12] = box[x-1][y-1];
			temp[13] = box[x-1][y];
			temp[14] = box[x-1][y+1];
			temp[15] = box[x-1][y+2];
			
			String [] beforeStatus = new String[16];
			beforeStatus[0] = box[x][y].getStatus();
			beforeStatus[1] = box[x][y+1].getStatus();
			beforeStatus[2] = box[x+1][y+1].getStatus();
			beforeStatus[3] = box[x+1][y].getStatus();
			
			beforeStatus[4] = box[x-1][y].getStatus();
			beforeStatus[5] = box[x-1][y+1].getStatus();
			beforeStatus[6] = box[x-1][y+2].getStatus();
			beforeStatus[7] = box[x][y+2].getStatus();
			beforeStatus[8] = box[x+1][y+2].getStatus();
			beforeStatus[9] = box[x+2][y+2].getStatus();
			beforeStatus[10] = box[x+2][y+1].getStatus();
			beforeStatus[11] = box[x+2][y].getStatus();
			beforeStatus[12] = box[x+2][y-1].getStatus();
			beforeStatus[13] = box[x+1][y-1].getStatus();
			beforeStatus[14] = box[x][y-1].getStatus();
			beforeStatus[15] = box[x-1][y-1].getStatus();
			
			char [] beforeType = new char[16];
			beforeType[0] = box[x][y].getType();
			beforeType[1] = box[x][y+1].getType();
			beforeType[2] = box[x+1][y+1].getType();
			beforeType[3] = box[x+1][y].getType();
			
			beforeType[4] = box[x-1][y].getType();
			beforeType[5] = box[x-1][y+1].getType();
			beforeType[6] = box[x-1][y+2].getType();
			beforeType[7] = box[x][y+2].getType();
			beforeType[8] = box[x+1][y+2].getType();
			beforeType[9] = box[x+2][y+2].getType();
			beforeType[10] = box[x+2][y+1].getType();
			beforeType[11] = box[x+2][y].getType();
			beforeType[12] = box[x+2][y-1].getType();
			beforeType[13] = box[x+1][y-1].getType();
			beforeType[14] = box[x][y-1].getType();
			beforeType[15] = box[x-1][y-1].getType();
			
			String [] afterStatus = new String[16];
			afterStatus[0] = box[x][y+1].getStatus();
			afterStatus[1] = box[x+1][y+1].getStatus();
			afterStatus[2] = box[x+1][y].getStatus();
			afterStatus[3] = box[x][y].getStatus();
			
			afterStatus[4] = box[x][y+2].getStatus();
			afterStatus[5] = box[x+1][y+2].getStatus();
			afterStatus[6] = box[x+2][y+2].getStatus();
			afterStatus[7] = box[x+2][y+1].getStatus();
			afterStatus[8] = box[x+2][y].getStatus();
			afterStatus[9] = box[x+2][y-1].getStatus();
			afterStatus[10] = box[x+1][y-1].getStatus();
			afterStatus[11] = box[x][y-1].getStatus();
			afterStatus[12] = box[x-1][y-1].getStatus();
			afterStatus[13] = box[x-1][y].getStatus();
			afterStatus[14] = box[x-1][y+1].getStatus();
			afterStatus[15] = box[x-1][y+2].getStatus();
			
			char [] afterType = new char[16];
			afterType[0] = box[x][y+1].getType();
			afterType[1] = box[x+1][y+1].getType();
			afterType[2] = box[x+1][y].getType();
			afterType[3] = box[x][y].getType();
			
			afterType[4] = box[x][y+2].getType();
			afterType[5] = box[x+1][y+2].getType();
			afterType[6] = box[x+2][y+2].getType();
			afterType[7] = box[x+2][y+1].getType();
			afterType[8] = box[x+2][y].getType();
			afterType[9] = box[x+2][y-1].getType();
			afterType[10] = box[x+1][y-1].getType();
			afterType[11] = box[x][y-1].getType();
			afterType[12] = box[x-1][y-1].getType();
			afterType[13] = box[x-1][y].getType();
			afterType[14] = box[x-1][y+1].getType();
			afterType[15] = box[x-1][y+2].getType();
			
			for(int i=0; i<16; i++) {
				if(beforeStatus[i].equals("AlreadySet")) {
					return flag;
				}
			}
			
			for(int i=0; i<16; i++) {
				if(!beforeStatus[i].equals("AlreadySet")) {
					if(beforeType[i] == ' ') {
						temp[i].setBox(beforeType[i], defaultColor1, beforeStatus[i]);
					}
					else
						temp[i].setBox(beforeType[i], color, beforeStatus[i]);
				}
			}
			
			return flag;
		}
		
		else if(type == 'V') {
			Box [] temp = new Box[4];
			temp[0] = box[x][y+1];
			temp[1] = box[x+1][y+1];
			temp[2] = box[x+1][y];
			temp[3] = box[x][y];
			
			String [] beforeStatus = new String[4];
			beforeStatus[0] = box[x][y].getStatus();
			beforeStatus[1] = box[x][y+1].getStatus();
			beforeStatus[2] = box[x+1][y+1].getStatus();
			beforeStatus[3] = box[x+1][y].getStatus();
			
			char [] beforeType = new char[4];
			beforeType[0] = box[x][y].getType();
			beforeType[1] = box[x][y+1].getType();
			beforeType[2] = box[x+1][y+1].getType();
			beforeType[3] = box[x+1][y].getType();
			
			String [] afterStatus = new String[8];
			afterStatus[0] = box[x][y+1].getStatus();
			afterStatus[1] = box[x+1][y+1].getStatus();
			afterStatus[2] = box[x+1][y].getStatus();
			afterStatus[3] = box[x][y].getStatus();
			
			char [] afterType = new char[8];
			afterType[0] = box[x][y+1].getType();
			afterType[1] = box[x+1][y+1].getType();
			afterType[2] = box[x+1][y].getType();
			afterType[3] = box[x][y].getType();
			
			for(int i=0; i<4; i++) {
				if(beforeStatus[i].equals("AlreadySet")) {
					return flag;
				}
			}
			
			for(int i=0; i<4; i++) {
				if(!beforeStatus[i].equals("AlreadySet")) {
					if(beforeType[i] == ' ')
						temp[i].setBox(beforeType[i], defaultColor1, beforeStatus[i]);
					else
						temp[i].setBox(beforeType[i], color, beforeStatus[i]);
				}
			}		
			return flag;
		}
		
		else {
			if(x<=0) {
				flag = 0;
				x++;
			}
			else if(x>=9) {
				flag = 1;
				x--;
			}
			else if(y<=1) {
				flag = 2;
				y++;
			}
			
			Box [] temp = new Box[8];
			temp[0] = box[x+1][y+1];
			temp[1] = box[x+1][y];
			temp[2] = box[x+1][y-1];
			temp[3] = box[x][y-1];
			temp[4] = box[x-1][y-1];
			temp[5] = box[x-1][y];
			temp[6] = box[x-1][y+1];
			temp[7] = box[x][y+1];
			
			String [] beforeStatus = new String[8];
			beforeStatus[0] = box[x-1][y+1].getStatus();
			beforeStatus[1] = box[x][y+1].getStatus();
			beforeStatus[2] = box[x+1][y+1].getStatus();
			beforeStatus[3] = box[x+1][y].getStatus();
			beforeStatus[4] = box[x+1][y-1].getStatus();
			beforeStatus[5] = box[x][y-1].getStatus();
			beforeStatus[6] = box[x-1][y-1].getStatus();
			beforeStatus[7] = box[x-1][y].getStatus();
			
			char [] beforeType = new char[8];
			beforeType[0] = box[x-1][y+1].getType();
			beforeType[1] = box[x][y+1].getType();
			beforeType[2] = box[x+1][y+1].getType();
			beforeType[3] = box[x+1][y].getType();
			beforeType[4] = box[x+1][y-1].getType();
			beforeType[5] = box[x][y-1].getType();
			beforeType[6] = box[x-1][y-1].getType();
			beforeType[7] = box[x-1][y].getType();
			
			String [] afterStatus = new String[8];
			afterStatus[0] = box[x+1][y+1].getStatus();
			afterStatus[1] = box[x+1][y].getStatus();
			afterStatus[2] = box[x+1][y-1].getStatus();
			afterStatus[3] = box[x][y-1].getStatus();
			afterStatus[4] = box[x-1][y-1].getStatus();
			afterStatus[5] = box[x-1][y].getStatus();
			afterStatus[6] = box[x-1][y+1].getStatus();
			afterStatus[7] = box[x][y+1].getStatus();
			
			char [] afterType = new char[8];
			afterType[0] = box[x+1][y+1].getType();
			afterType[1] = box[x+1][y].getType();
			afterType[2] = box[x+1][y-1].getType();
			afterType[3] = box[x][y-1].getType();
			afterType[4] = box[x-1][y-1].getType();
			afterType[5] = box[x-1][y].getType();
			afterType[6] = box[x-1][y+1].getType();
			afterType[7] = box[x][y+1].getType();
			
			for(int i=0; i<8; i++) {
				if(beforeStatus[i].equals("AlreadySet")) {
					return -1;
				}
			}
			
			for(int i=0; i<8; i++) {
				if(!beforeStatus[i].equals("AlreadySet")) {
					if(beforeType[i] == ' ')
						temp[i].setBox(beforeType[i], defaultColor1, beforeStatus[i]);
					else
						temp[i].setBox(beforeType[i], color, beforeStatus[i]);
				}
			}
			return flag;
		}		
	}
	
	// 블록 생성시 그리는 함수 - 여러칸을 그린다는 뜻
	public void drawEntireBlock(int x, int y, char type, Color color, String status) {
		if(type == 'O') {
			box[x+1][y+1].setBox(type, color, status);
			box[x][y+1].setBox(type, color, status);
			box[x+1][y].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
		}
		else if(type == 'L') {
			box[x][y+1].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x][y-1].setBox(type, color, status);
			box[x+1][y-1].setBox(type, color, status);
		}
		else if(type == 'J') {
			box[x][y-1].setBox(type, color, status);
			box[x][y+1].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x+1][y+1].setBox(type, color, status);
		}
		else if(type == 'I') {
			box[x][y-1].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x][y+1].setBox(type, color, status);
			box[x][y+2].setBox(type, color, status);
		}
		else if(type == 'Z') {
			box[x+1][y+1].setBox(type, color, status);
			box[x+1][y].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x][y-1].setBox(type, color, status);
			
		}
		else if(type == 'S') {
			box[x+1][y-1].setBox(type, color, status);
			box[x+1][y].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x][y+1].setBox(type, color, status);
		}
		else if(type == 'T') {
			box[x][y-1].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x][y+1].setBox(type, color, status);
			box[x+1][y].setBox(type, color, status);
		}
		else if(type == 'V') {
			box[x][y+1].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x+1][y].setBox(type, color, status);
		}
		else if(type == '-') {
			box[x][y-1].setBox(type, color, status);
			box[x][y].setBox(type, color, status);
			box[x][y+1].setBox(type, color, status);
		}
	}
	
	// 미리보기 블록 그리는 함수
	public void drawhintBox(int n, char type, Color color) {
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				hintBox[n][i][j].setBox(' ', defaultColor2);
			}
		}
		if(type == 'O') {
			hintBox[n][1][1].setBox(type, color);
			hintBox[n][2][1].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][2][2].setBox(type, color);
		}
		else if(type == 'L') {
			hintBox[n][1][3].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][2][1].setBox(type, color);
			hintBox[n][1][1].setBox(type, color);
		}
		else if(type == 'J') {
			hintBox[n][2][3].setBox(type, color);
			hintBox[n][1][3].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][1][1].setBox(type, color);
		}
		else if(type == 'I') {
			hintBox[n][2][0].setBox(type, color);
			hintBox[n][2][1].setBox(type, color);
			hintBox[n][2][2].setBox(type, color);
			hintBox[n][2][3].setBox(type, color);
		}
		else if(type == 'Z') {
			hintBox[n][2][3].setBox(type, color);
			hintBox[n][2][2].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][1][1].setBox(type, color);
		}
		else if(type == 'S') {
			hintBox[n][1][3].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][2][2].setBox(type, color);
			hintBox[n][2][1].setBox(type, color);
		}
		else if(type == 'T') {
			hintBox[n][1][3].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][2][2].setBox(type, color);
			hintBox[n][1][1].setBox(type, color);
		}
		else if(type == 'V') {
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][2][1].setBox(type, color);
			hintBox[n][1][1].setBox(type, color);
		}
		else if(type == '-') {
			hintBox[n][1][3].setBox(type, color);
			hintBox[n][1][2].setBox(type, color);
			hintBox[n][1][1].setBox(type, color);
		}
		repaint();
	}
	
	// 블록의 상태 가져오는 함수
	public String getBlockStatus(int x, int y) {
		return box[x][y].getStatus();
	}
	
}
