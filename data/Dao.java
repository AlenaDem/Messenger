package data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class Dao {
	protected int id = 0;
	
	public Dao() {
		this.id = 0;
	}
	
	public Dao(int id) {
		this.id = id;
	}
	
	public int id() {
		return this.id;
	}
	
    public void setId(int id) {
    	this.id = id;
    }
	
	protected abstract String table();
	protected abstract String createSaveQuery();
	protected abstract boolean processSelectResult(ResultSet rs);
	
	public boolean select() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM %s WHERE id = %d".formatted(table(), id));
			return processSelectResult(rs);
			
		} catch (Exception e) {
			System.out.println("Ошибка при выборе данных из БД.");
		}
		return false;
	}
	
	
	
	public boolean save() {
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			String query = createSaveQuery();
			s.executeUpdate(query);
			ResultSet rs = s.getGeneratedKeys();
			if (rs.next()){
			    this.id = rs.getInt(1);
			}
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Ошибка сохранения.");
		}
		return false;
	}
	
	public boolean remove() {
	    if (this.id == 0)
	        return false;
	    
		try {
			Connection conn = Database.connection();
			Statement s = conn.createStatement();
			s.executeUpdate("DELETE FROM %s WHERE id = %d".formatted(table(), id));
			this.id = 0;
			return true;
			
		} catch (Exception e) {
			System.out.println("Ошибка удаления.");
		}
		return false;
	}
}
