
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Server extends JFrame{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket;
	private Socket client_socket;
	private Vector UserVec = new Vector();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server server = new Server();
					server.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); 
				txtPortNumber.setEnabled(false); 
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { 
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept();
					AppendText("새로운 참가자 from " + client_socket);
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user);
					new_user.start();
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
				}
			}
		}
	}

	public void AppendText(String str) {
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(Data data) {
		textArea.append("code = " + data.code + "\n");
		textArea.append("id = " + data.username + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	class UserService extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;

		public UserService(Socket client_socket) {
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public synchronized void Login(Data data) {
			AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteAll(data);
		}
		
		//방이 꽉차있거나 같은 user Id가 있다면 실패로 처리
		public synchronized void LoginFailed() {
			WriteMe(new Data(UserName, "102"));
			UserVec.removeElement(this);
			this.client_socket = null;
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		public synchronized void Logout() {
			UserVec.removeElement(this);
			Data data = new Data(UserName, "600");
			int count = 0;
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					data.userList[count++] = user.UserName;
			}
			WriteAll(data);
			this.client_socket = null;
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		public synchronized void WriteMe(Data data) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user == this)
					user.WriteData(data);
			}
		}
		
		public synchronized void WriteAll(Data data) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteData(data);
			}
		}

		public synchronized void WriteOthers(Data data) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					user.WriteData(data);
			}
		}

		public void WriteData(Data data) {
			try {
			    oos.writeObject(data.code);
			    oos.reset();
			    oos.writeObject(data.username);
			    oos.reset();
			    if (data.code.equals("101")) {
			    	oos.writeObject(data.userList);
			    }
			    else if (data.code.equals("103")) {
			    	oos.writeObject(data.ID);
			    }
			    else if (data.code.equals("202")) {
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
			    else if (data.code.equals("600")) {
				    oos.writeObject(data.userList);
			    }
			    oos.reset();
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Logout();
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
				
				if (data.code.equals("202")) {
					obj = ois.readObject();
					data.chatMsg = (String) obj;
				}
				else if (data.code.equals("401")) {
					obj = ois.readObject();
					data.blockStatus = (char[][]) obj;
					obj = ois.readObject();
					data.itemStatus = (boolean[]) obj;
				}
				else if (data.code.equals("402")) {
					obj = ois.readObject();
					data.attacklines = (int) obj;
				}
				else if (data.code.equals("403")) {
					obj = ois.readObject();
					data.item = (int) obj;
				}
				else if (data.code.equals("404")) {
					obj = ois.readObject();
					data.emoticon = (int) obj;
				}
			} catch (ClassNotFoundException e) {
				Logout();
				return null;
			} catch (IOException e) {
				Logout();
				return null;
			}
			return data;
		}
		
		public void run() {
			while (true) { 
				Data data = null; 
				if (client_socket == null)
					break;
				data = ReadData();
				if (data==null)
					break;
				if (data.code.length()==0)
					break;
				AppendObject(data);
				if (data.code.matches("100")) {
					UserName = data.username;
					int count = 0;
					if(user_vc.size() > 4) {
						LoginFailed();
						break;
					}
					data.userList[count++] = UserName;
					for (int i = 0; i < user_vc.size(); i++) {
						UserService user = (UserService) user_vc.elementAt(i);
						if (user != this) {
							if(UserName.equals(user.UserName)) {
								LoginFailed();
								break;
							}
							data.userList[count++] = user.UserName;
						}
					}
					data.ID = user_vc.size();
					data.code = "101";
					Login(data);
				}
				else if(data.code.matches("103")) {
					for (int i = 0; i < user_vc.size(); i++) {
						UserService user = (UserService) user_vc.elementAt(i);
						if (user == this) {
							data.ID = i;
							WriteMe(data);
						}
					}
				}
				else if (data.code.matches("200") || data.code.matches("201") || data.code.matches("401")
						|| data.code.matches("402") || data.code.matches("403")
						|| data.code.matches("404") || data.code.matches("405")) {
					WriteOthers(data);
				}
				else if (data.code.matches("202") || data.code.matches("300")) {
					WriteAll(data);
				}
				else if (data.code.matches("500")) WriteOthers(data);
				else if (data.code.matches("600")) {
					Logout();
					break;
				}
			}
		}
	}
}
