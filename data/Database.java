package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
	private static Connection conn;
	
	public static Connection connection() throws ClassNotFoundException, SQLException {
		if (conn == null) {
		   Class.forName("com.mysql.cj.jdbc.Driver");
		   String url = "jdbc:mysql:C:\\Users\\142\\eclipse-workspace\\rgr\\messenger.db";
		   conn = DriverManager.getConnection(url);
		   createDB();
		}
		return conn;
    }
	
	public static void createDB() throws ClassNotFoundException, SQLException {
		Statement statmt = conn.createStatement();
		statmt.execute("CREATE TABLE if not exists image(id INT NOT NULL AUTO_INCREMENT, FileName VARCHAR(50), data BLOB, PRIMARY KEY(id));");
		
		statmt.execute("CREATE TABLE if not exists file(id INT NOT NULL AUTO_INCREMENT, FileName VARCHAR(50), data BLOB, PRIMARY KEY(id));");
		
		statmt.execute("CREATE TABLE if not exists users(id INT NOT NULL AUTO_INCREMENT, nickname VARCHAR(30), hash_password TEXT, email VARCHAR(256), is_admin BOOL, message_notification BOOL, friend_request_notification BOOL, chat_invitation_notification BOOL, profile_photo_id INT NOT NULL, PRIMARY KEY(id), FOREIGN KEY (profile_photo_id) REFERENCES image (id), CONSTRAINT uniqueconsraint UNIQUE (nickname, email));");
		
		statmt.execute("CREATE TABLE if not exists chat(id INT NOT NULL AUTO_INCREMENT, type INT, creator_id INT NOT NULL, title VARCHAR(40), PRIMARY KEY(id), FOREIGN KEY (creator_id) REFERENCES users (id));");
		
		statmt.execute("CREATE TABLE if not exists message(id INT NOT NULL AUTO_INCREMENT, chat_id INT NOT NULL, creator_id INT NOT NULL, message_body TEXT, link TEXT, image_id INT, file_id INT, time DATETIME DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(id),  FOREIGN KEY (creator_id) REFERENCES users (id), FOREIGN KEY (chat_id) REFERENCES chat (id), FOREIGN KEY (creator_id) REFERENCES users (id), FOREIGN KEY (image_id) REFERENCES image (id), FOREIGN KEY (file_id) REFERENCES file (id));");
		
		statmt.execute("CREATE TABLE if not exists chat_users(id INT NOT NULL AUTO_INCREMENT, chat_id INT NOT NULL, users_id INT NOT NULL, is_blocked BOOL, title VARCHAR(40), PRIMARY KEY(id), FOREIGN KEY (chat_id) REFERENCES chat (id), FOREIGN KEY (users_id) REFERENCES users (id));");
		
		statmt.execute("CREATE TABLE if not exists friends(id INT NOT NULL AUTO_INCREMENT, users_id INT NOT NULL, contact_id INT NOT NULL, PRIMARY KEY(id), FOREIGN KEY (users_id) REFERENCES users (id), FOREIGN KEY (contact_id) REFERENCES users (id));");
		
		statmt.execute("CREATE TABLE if not exists friend_requests(id INT NOT NULL AUTO_INCREMENT, send_user_id INT NOT NULL, received_users_id INT NOT NULL, PRIMARY KEY(id), FOREIGN KEY (send_user_id) REFERENCES users (id), FOREIGN KEY (received_users_id) REFERENCES users (id));");
		
		statmt.execute("CREATE TABLE if not exists chat_invitation(id INT NOT NULL AUTO_INCREMENT, send_user_id INT NOT NULL, received_users_id INT NOT NULL, PRIMARY KEY(id), FOREIGN KEY (send_user_id) REFERENCES users (id), FOREIGN KEY (received_users_id) REFERENCES users (id));");
		
		System.out.println("База данных создана");
	}
	
}
