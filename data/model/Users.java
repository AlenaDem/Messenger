package data.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Dao;
import data.Database;

public class Users extends Dao{
	static public String TABLE_NAME = "users";
	private int id;
	private String nickname;
	private String hash_password;
	private String email;
	private boolean is_admin;
	private boolean message_notification;
	private boolean friend_request_notification;
	private boolean chat_invitation_notification;
	protected int profile_photo_id;
	public Users(int id, String nickname, String hash_password, String email, boolean is_admin, boolean message_notification, boolean friend_request_notification, boolean chat_invitation_notification, int profile_photo_id) {
		super(id);
		this.id = id;
		this.nickname = nickname;
		this.hash_password = hash_password;
		this.email = email;
		this.is_admin = is_admin;
		this.message_notification = message_notification;
		this.friend_request_notification = friend_request_notification;
		this.chat_invitation_notification = chat_invitation_notification;
		this.profile_photo_id = profile_photo_id;
	}
	public  Users() {
		super();
	}
	public void setid(int id) {
		this.id = id;
	}
	public int getid() {
		return id;
	}
	public void setnickname(String nickname) {
		this.nickname = nickname;;
	}
	public String getnickname() {
		return nickname;
	}
	public void sethash_password(String hash_password) {
		this.hash_password = hash_password;
	}
	public String gethash_password() {
		return hash_password;
	}
	public void setemail(String email) {
		this.email = email;
	}
	public String getemail() {
		return email;
	}
	public void setis_admin(boolean is_admin) {
		this.is_admin = is_admin;
	}
	public boolean getis_admin() {
		return is_admin;
	}
	public void setmessage_notification(boolean message_notification) {
		this.message_notification = message_notification;
	}
	public boolean getmessage_notification() {
		return message_notification;
	}
	public void setfriend_request_notification(boolean friend_request_notification) {
		this.friend_request_notification = friend_request_notification;
	}
	public boolean getfriend_request_notification() {
		return friend_request_notification;
	}
	public void setchat_invitation_notification(boolean chat_invitation_notification) {
		this.chat_invitation_notification = chat_invitation_notification;
	}
	public boolean getchat_invitation_notification() {
		return chat_invitation_notification;
	}
	public void setprofile_photo_id(int profile_photo_id) {
		this.profile_photo_id = profile_photo_id;
	}
	public int getprofile_photo_id() {
		return profile_photo_id;
	}
	@Override
	protected String table() {
		return "users";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET nickname='%s' hash_password='%s' email='%s' is_admin='%b' message_notification='%b' friend_request_notification='%b' chat_invitation_notification='%b' profile_photo_id='%d' WHERE id=%d;".formatted(table(), this.nickname, this.hash_password, this.email, this.is_admin, this.message_notification, this.friend_request_notification, this.chat_invitation_notification, this.profile_photo_id, this.id);
		}
		return "INSERT INTO %s (nickname, hash_password, email, is_admin, message_notification, friend_request_notification, chat_invitation_notification, profile_photo_id) VALUES ('%s', '%s', '%s', '%b', '%b', '%b', '%b','%d');".formatted(table(), this.nickname, this.hash_password, this.email, this.is_admin, this.message_notification, this.friend_request_notification, this.chat_invitation_notification, this.profile_photo_id);
	}
	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.nickname= rs.getString("nickname");
			this.hash_password = rs.getString("hash_password");
			this.email = rs.getString("email");
			this.is_admin = rs.getBoolean("is_admin");
			this.message_notification = rs.getBoolean("message_notification");
			this.friend_request_notification = rs.getBoolean("friend_request_notification");
			this.chat_invitation_notification = rs.getBoolean("chat_invitation_notification");
			this.profile_photo_id = rs.getInt("profile_photo_id");
			return true;
		} catch (SQLException e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}

	public static ArrayList<Users> selectAll() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
			var res = new ArrayList<Users>();
			while (rs.next()) {
				var p = new Users(rs.getInt("id"), rs.getString("nickname"), rs.getString("hash_password"), rs.getString("email"), rs.getBoolean("is_admin"), rs.getBoolean("message_notification"), rs.getBoolean("friend_request_notification"), rs.getBoolean("chat_invitation_notification"), rs.getInt("profile_photo_id"));
				res.add(p);
			}
			s.close();
			return res;
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return new ArrayList<Users>();
	}
}
