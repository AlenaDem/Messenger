package data.model;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import data.Dao;
import data.Database;
public class Message extends Dao{
	static public String TABLE_NAME = "message";
	private int id;
	protected int chat_id;;
	protected int creator_id;
	private String message_body;
	private String link;
	protected int image_id;
	protected int file_id;
	private Date time;
	public Message(int id, int chat_id, int creator_id, String message_body, String link, int image_id, int file_id, Date time) {
		super(id);
		this.id = id;
		this.chat_id = chat_id;
		this.creator_id = creator_id;
		this.message_body = message_body;
		this.link = link;
		this.image_id = image_id;
		this.file_id = file_id;
		this.time = time;
	}
	public  Message() {
		super();
	}
	public void setid(int id) {
		this.id = id;
	}
	public int getid() {
		return id;
	}
	public void setchat_id(int chat_id) {
		this.chat_id = chat_id;
	}
	public int getchat_id() {
		return chat_id;
	}
	public void setcreator_id(int creator_id) {
		this.creator_id = creator_id;
	}
	public int getcreator_id() {
		return creator_id;
	}
	public void setmessage_body(String message_body) {
		this.message_body = message_body;
	}
	public String getmessage_body() {
		return message_body;
	}
	public void setlink(String link) {
		this.link = link;
	}
	public String getlink() {
		return link;
	}
	public void setimage_id(int image_id) {
		this.image_id = image_id;
	}
	public int getimage_id() {
		return image_id;
	}
	public void setfile_id(int file_id) {
		this.file_id = file_id;
	}
	public int getfile_id() {
		return file_id;
	}
	public void settime(Date time) {
		this.time = time;
	}
	public Date gettime() {
		return time;
	}
	@Override
	protected String table() {
		return "message";
	}
	@Override
	protected String createSaveQuery() {
		if (this.id != 0) {
			return "UPDATE %s SET  chat_id='%d' creator_id='%d' message_body='%s' link='%s' image_id='%d' file_id ='%d'".formatted(table(), this.chat_id, this.creator_id, this.message_body, this.link, this.image_id, this.file_id) + "time=" + gettime() + " WHERE id=" + getid() +";";
		}
		return "INSERT INTO %s (chat_id, creator_id, message_body, link, image_id, file_id, time) VALUES ('%d', '%d', '%s', '%s', '%d', '%d',".formatted(table(), this.chat_id, this.creator_id, this.message_body, this.link, this.image_id, this.file_id) + gettime() + ");";
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.chat_id = rs.getInt("chat_id");
			this.creator_id= rs.getInt("creator_id");
			this.message_body = rs.getString("message_body");
			this.link = rs.getString("link");
			this.image_id = rs.getInt("image_id");
			this.file_id = rs.getInt("file_id");
			this.time = rs.getTimestamp("time");
			return true;
		} catch (SQLException e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}
	public static ArrayList<Message> selectAll() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
			var res = new ArrayList<Message>();
			while (rs.next()) {
				var p = new Message(rs.getInt("id"), rs.getInt("chat_id"), rs.getInt("creator_id"), rs.getString("message_body"), rs.getString("link"), rs.getInt("image_id"), rs.getInt("file_id"), rs.getTimestamp("time"));
				res.add(p);
			}
			s.close();
			return res;
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return new ArrayList<Message>();
	}
}
