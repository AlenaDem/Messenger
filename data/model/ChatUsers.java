package data.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Dao;
import data.Database;

public class ChatUsers extends Dao{
	static public String TABLE_NAME = "chat_users";
	private int id;
	protected int chatId;
	protected int userId;
	private boolean isBlocked;
	private String title;
	public ChatUsers(int id, int chatId, int userId, boolean isBlocked, String title) {
		super(id);
		this.id = id;
		this.chatId = chatId;
		this.userId = userId;
		this.isBlocked = isBlocked;
		this.title = title;
	}
	public  ChatUsers() {
		super();
	}
	public void setid(int id) {
		this.id = id;
	}
	public int getid() {
		return id;
	}
	public void setchatId(int chatId) {
		this.chatId = chatId;
	}
	public int getchatId() {
		return chatId;
	}
	public void setuserId(int userId) {
		this.userId = userId;
	}
	public int getuserId() {
		return userId;
	}
	public void setisBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	public boolean getisBlocked() {
		return isBlocked;
	}
	public void settitle(String title) {
		this.title = title;
	}
	public String gettitle() {
		return title;
	}
	@Override
	protected String table() {
		return "chat_users";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET chat_id='%d' user_id='%d' is_blocked='%b' title='%s' WHERE id='%d';".formatted(table(), this.chatId, this.userId, this.isBlocked, this.title, this.id);
		}
		return "INSERT INTO %s (chat_id, user_id, is_blocked, title) VALUES ('%d', '%d', '%b', '%s');".formatted(table(), this.chatId, this.userId, this.isBlocked, this.title);
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.chatId = rs.getInt("chat_id");
			this.userId = rs.getInt("user_id");
			this.isBlocked = rs.getBoolean("is_blocked");
			this.title = rs.getString("title");
			return true;
		} catch (SQLException e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}
	public static ArrayList<ChatUsers> selectAll() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
			var res = new ArrayList<ChatUsers>();
			while (rs.next()) {
				var p = new ChatUsers(rs.getInt("id"), rs.getInt("chat_id"), rs.getInt("user_id"), rs.getBoolean("is_blocked"), rs.getString("title"));
				res.add(p);
			}
			s.close();
			return res;
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return new ArrayList<ChatUsers>();
	}
}

