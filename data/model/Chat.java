package data.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Dao;
import data.Database;

public class Chat extends Dao{
	static public String TABLE_NAME = "Chat";
	private int id;
	private int type;
	protected int creatorId;
	private String title;
	public Chat(int id, int type, int creatorId, String title) {
		super(id);
		this.id = id;
		this.type = type;
		this.creatorId = creatorId;
		this.title = title;
	}
	public  Chat() {
		super();
	}
	public void setid(int id) {
		this.id = id;
	}
	public int getid() {
		return id;
	}
	public void setnickname(int type) {
		this.type = type;
	}
	public int gettype() {
		return type;
	}
	public void setcreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public int getcreator() {
		return creatorId;
	}
	public void setemail(String title) {
		this.title = title;
	}
	public String gettitle() {
		return title;
	}
	@Override
	protected String table() {
		return "chat";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET type='%d' creator_id='%d' title='%s' WHERE id=%d;".formatted(table(), this.type, this.creatorId, this.title, this.id);
		}
		return "INSERT INTO %s (type, creator_id, title,) VALUES ('%d', '%d', '%s');".formatted(table(), this.type, this.creatorId, this.title);
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.type = rs.getInt("type");
			this.creatorId = rs.getInt("creator_id");
			this.title = rs.getString("title");
			return true;
		} catch (SQLException e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}
	public static ArrayList<Chat> selectAll() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
			var res = new ArrayList<Chat>();
			while (rs.next()) {
				var p = new Chat(rs.getInt("id"), rs.getInt("type"), rs.getInt("creator_id"), rs.getString("title"));
				res.add(p);
			}
			s.close();
			return res;
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return new ArrayList<Chat>();
	}
}
