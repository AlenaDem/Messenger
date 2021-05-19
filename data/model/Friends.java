package data.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Dao;
import data.Database;

public class Friends extends Dao{
	static public String TABLE_NAME = "friends";
	private int id;
	protected int user_id;
	protected int contact_id;
	public Friends(int id, int user_id,int contact_id) {
		
		super(id);
		this.id = id;
		this.user_id = user_id;
		this.contact_id = contact_id;
	}
	public  Friends() {
		super();
	}
	public void setid(int id) {
		this.id = id;
	}
	public int getid() {
		return id;
	}
	public void setcontact_id(int contact_id) {
		this.contact_id = contact_id;
	}
	public int getcontact_id() {
		return contact_id;
	}
	public void setuser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getuser_id() {
		return user_id;
	}
	@Override
	protected String table() {
		return "friends";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET  user_id='%d' contact_id='%d' WHERE id='%d';".formatted(table(), this.user_id, this.contact_id, this.id);
		}
		return "INSERT INTO %s (user_id, contact_id) VALUES ('%d', '%d');".formatted(table(), this.user_id, this.contact_id);
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.user_id = rs.getInt("user_id");
			this.contact_id = rs.getInt("contact_id");
			return true;
		} catch (SQLException e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}
	public static ArrayList<Friends> selectAll() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
			var res = new ArrayList<Friends>();
			while (rs.next()) {
				var p = new Friends(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("contact_id"));
				res.add(p);
			}
			s.close();
			return res;
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return new ArrayList<Friends>();
	}
}