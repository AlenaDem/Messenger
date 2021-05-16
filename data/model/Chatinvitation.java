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
	protected int send_user_id;
	protected int received_users_id;
	public Chatinvitation(int id, int send_user_id, int received_users_id) {
		super(id);
		this.id = id;
		this.send_user_id = send_user_id;
		this.received_users_id = received_users_id;
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
	public void setsend_user_id(int send_user_id) {
		this.send_user_id = send_user_id;
	}
	public int getsend_user_id() {
		return send_user_id;
	}
	public void setreceived_users_id(int received_users_id) {
		this.received_users_id = received_users_id;
	}
	public int getreceived_users_id() {
		return received_users_id;
	}
	@Override
	protected String table() {
		return "chat_invitation";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET  send_user_id='%d' received_users_id='%d' WHERE id='%d';".formatted(table(), this.send_user_id, this.received_users_id, this.id);
		}
		return "INSERT INTO %s (send_user_id, received_users_id) VALUES ('%d', '%d');".formatted(table(), this.send_user_id, this.received_users_id);
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.send_user_id = rs.getInt("send_user_id");
			this.received_users_id = rs.getInt("received_users_id");
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
