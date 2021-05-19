package data.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Dao;
import data.Database;

public class Chatinvitation extends Dao{
	static public String TABLE_NAME = "chat_invitation";
	private int id;
	protected int sendUserId;
	protected int receivedUsersId;
	public Chatinvitation(int id, int sendUserId, int receivedUsersId) {
		super(id);
		this.id = id;
		this.sendUserId = sendUserId;
		this.receivedUsersId = receivedUsersId;
	}
	public Chatinvitation() {
		super();
	}
	public void setid(int id) {
		this.id = id;
	}
	public int getid() {
		return id;
	}
	public void setsendUserId(int sendUserId) {
		this.sendUserId = sendUserId;
	}
	public int getsendUserId() {
		return sendUserId;
	}
	public void setreceivedUsersId(int receivedUsersId) {
		this.receivedUsersId = receivedUsersId;
	}
	public int getreceivedUsersId() {
		return receivedUsersId;
	}
	@Override
	protected String table() {
		return "chat_invitation";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET  send_user_id='%d' received_users_id='%d' WHERE id='%d';".formatted(table(), this.sendUserId, this.receivedUsersId, this.id);
		}
		return "INSERT INTO %s (send_user_id, received_users_id) VALUES ('%d', '%d');".formatted(table(), this.sendUserId, this.receivedUsersId);
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.sendUserId = rs.getInt("send_user_id");
			this.receivedUsersId = rs.getInt("received_users_id");
			return true;
		} catch (SQLException e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}
	public static ArrayList<Chatinvitation> selectAll() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
			var res = new ArrayList<Chatinvitation>();
			while (rs.next()) {
				var p = new Chatinvitation(rs.getInt("id"), rs.getInt("send_user_id"), rs.getInt("received_users_id"));
				res.add(p);
			}
			s.close();
			return res;
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return new ArrayList<Chatinvitation>();
	}
}
