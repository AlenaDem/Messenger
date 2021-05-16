package data.model;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.Dao;
import data.Database;

public class File extends Dao{
static public String TABLE_NAME = "file";
private int id;
private String FileName;
private Blob data;

public int getFileId() {
	return  id;
}
public String getFileName() {
	return FileName;
}
public Blob getdata() {
	return data;
}
public void setFileId(int id) {
	this.id =  id;
}
public void setFileName(String FileName) {
	this.FileName = FileName;
}
public void setdata(Blob data) {
	this.data = data;
}
public File(int id, String FileName, Blob data) {
	super(id);
	this.id =  id;
	this.FileName = FileName;
	this.data = data;
}
@Override
protected String table() {
	return "file";
}
//Далее ставлю для blob %d возможно нужно изменить!!!
@Override
protected String createSaveQuery() {
	if (this.id != 0) {
		return "UPDATE %s SET FileName='%s' data='%d' WHERE id='%d';".formatted(table(),  this.FileName, this.data, this.id);
	}
	return "INSERT INTO %s (FileName, data) VALUES ('%s', '%d');".formatted(table(), this.FileName, this.data);
}

@Override
protected boolean processSelectResult(ResultSet rs) {
	try {
		this.FileName = rs.getString("FileName");
		this.data = rs.getBlob("data");
		return true;
	} catch (SQLException e) {
		System.out.println("Ошибка при выборе данных из БД.");
	}
	return false;
}
public static ArrayList<File> selectAll() {
	try {
		Connection conn = Database.connection();
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM %s".formatted(TABLE_NAME));
		var res = new ArrayList<File>();
		while (rs.next()) {
			var p = new File(rs.getInt("id"), rs.getString("FileName"), rs.getBlob("data"));
			res.add(p);
		}
		s.close();
		return res;
		
	} catch (Exception e) {
		System.out.println("Ошибка при выборе данных из БД.");
	}
	return new ArrayList<File>();
}
}