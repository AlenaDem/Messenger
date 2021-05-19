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
	private String hashPassword;
	private String email;
	private boolean isAdmin;
	private boolean messageNotification;
	private boolean friendRequestNotification;
	private boolean chatInvitationNotification;
	protected int profilePhotoId;
	public Users(int id, String nickname, String hashPassword, String email, boolean isAdmin, boolean messageNotification, boolean friendRequestNotification, boolean chatInvitationNotification, int profilePhotoId) {
		super(id);
		this.id = id;
		this.nickname = nickname;
		this.hashPassword = hashPassword;
		this.email = email;
		this.isAdmin = isAdmin;
		this.messageNotification = messageNotification;
		this.friendRequestNotification = friendRequestNotification;
		this.chatInvitationNotification = chatInvitationNotification;
		this.profilePhotoId = profilePhotoId;
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
	public void sethashPassword(String hashPassword) {
		this.hashPassword = hashPassword;
	}
	public String gethashPassword() {
		return hashPassword;
	}
	public void setemail(String email) {
		this.email = email;
	}
	public String getemail() {
		return email;
	}
	public void setisAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public boolean getisAdmin() {
		return isAdmin;
	}
	public void setmessageNotification(boolean messageNotification) {
		this.messageNotification = messageNotification;
	}
	public boolean getmessageNotification() {
		return messageNotification;
	}
	public void setfriendRequestNotification(boolean friendRequestNotification) {
		this.friendRequestNotification = friendRequestNotification;
	}
	public boolean getfriendRequestNotification() {
		return friendRequestNotification;
	}
	public void setchatInvitationNotification(boolean chatInvitationNotification) {
		this.chatInvitationNotification = chatInvitationNotification;
	}
	public boolean getchatInvitationNotification() {
		return chatInvitationNotification;
	}
	public void setprofilePhotoId(int profilePhotoId) {
		this.profilePhotoId = profilePhotoId;
	}
	public int getprofilePhotoId() {
		return profilePhotoId;
	}
	@Override
	protected String table() {
		return "users";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET nickname='%s' hash_password='%s' email='%s' is_admin='%b' message_notification='%b' friend_request_notification='%b' chat_invitation_notification='%b' profile_photo_id='%d' WHERE id=%d;".formatted(table(), this.nickname, this.hashPassword, this.email, this.isAdmin, this.messageNotification, this.friendRequestNotification, this.chatInvitationNotification, this.profilePhotoId, this.id);
		}
		return "INSERT INTO %s (nickname, hash_password, email, is_admin, message_notification, friend_request_notification, chat_invitation_notification, profile_photo_id) VALUES ('%s', '%s', '%s', '%b', '%b', '%b', '%b','%d');".formatted(table(), this.nickname, this.hashPassword, this.email, this.isAdmin, this.messageNotification, this.friendRequestNotification, this.chatInvitationNotification, this.profilePhotoId);
	}
	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.nickname= rs.getString("nickname");
			this.hashPassword = rs.getString("hash_password");
			this.email = rs.getString("email");
			this.isAdmin = rs.getBoolean("is_admin");
			this.messageNotification = rs.getBoolean("message_notification");
			this.friendRequestNotification = rs.getBoolean("friend_request_notification");
			this.chatInvitationNotification = rs.getBoolean("chat_invitation_notification");
			this.profilePhotoId = rs.getInt("profile_photo_id");
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
