import java.io.Serializable;

public class Data implements Serializable{
	private static final long serialVersionUID = 1L;
	public int ID;
	public String code;
	public String username;
	public int userNum;
	public String chatMsg;
	public boolean isDead;
	
	public String [] userList = new String[4];
	
	public char [][] blockStatus = new char [10][20];
	public boolean [] itemStatus = new boolean[2];
	public int attacklines;
	public int item;
	public int emoticon;
	
	public Data(String username, String code) {
		this.username = username;
		this.code = code;
	}

}
