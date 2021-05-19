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
	protected int chatId;
	protected int creatorId;
	private String messageBody;
	private String link;
	protected int imageId;
	protected int fileId;
	private Date time;
	public Message(int id, int chatId, int creatorId, String messageBody, String link, int imageId, int fileId, Date time) {
		super(id);
		this.id = id;
		this.chatId = chatId;
		this.creatorId = creatorId;
		this.messageBody = messageBody;
		this.link = link;
		this.imageId = imageId;
		this.fileId = fileId;
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
	public void setchatId(int chatId) {
		this.chatId = chatId;
	}
	public int getchatId() {
		return chatId;
	}
	public void setcreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public int getcreatorId() {
		return creatorId;
	}
	public void setmessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	public String getmessageBody() {
		return messageBody;
	}
	public void setlink(String link) {
		this.link = link;
	}
	public String getlink() {
		return link;
	}
	public void setimageId(int imageId) {
		this.imageId = imageId;
	}
	public int getimageId() {
		return imageId;
	}
	public void setfileId(int fileId) {
		this.fileId = fileId;
	}
	public int getfileId() {
		return fileId;
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
			return "UPDATE %s SET  chat_id='%d' creator_id='%d' message_body='%s' link='%s' image_id='%d' file_id ='%d'".formatted(table(), this.chatId, this.creatorId, this.messageBody, this.link, this.imageId, this.fileId) + "time=" + gettime() + " WHERE id=" + getid() +";";
		}
		return "INSERT INTO %s (chat_id, creator_id, message_body, link, image_id, file_id, time) VALUES ('%d', '%d', '%s', '%s', '%d', '%d',".formatted(table(), this.chatId, this.creatorId, this.messageBody, this.link, this.imageId, this.fileId) + gettime() + ");";
	}

	@Override
	protected boolean processSelectResult(ResultSet rs) {
		try {
			this.chatId = rs.getInt("chat_id");
			this.creatorId= rs.getInt("creator_id");
			this.messageBody = rs.getString("message_body");
			this.link = rs.getString("link");
			this.imageId = rs.getInt("image_id");
			this.fileId = rs.getInt("file_id");
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
