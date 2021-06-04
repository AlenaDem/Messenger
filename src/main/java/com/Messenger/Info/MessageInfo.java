package com.Messenger.Info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.Messenger.Models.ChatMessage;

public class MessageInfo {
	public Long id;
	public Long chat_id;
	public String time;
	public String username;
	public String text;
	public String type;
	public String filename;
	public boolean deletable;
	
	public MessageInfo(ChatMessage msg) {
		id = msg.getId();
		chat_id = msg.getChat().getId();
		username = msg.getUser().getUsername();
		text = msg.getText();
		filename = msg.getAttachedFile();
		
		var format = MessageInfo.isToday(msg.getDate()) ? "HH:mm" : "HH:mm dd-MM-yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);  
		time = dateFormat.format(msg.getDate());
		
		var msgType = msg.getType();
		if(filename == null || filename.isEmpty())
			type = "text";
		else if (msgType.contains("image"))
			type = "image";
		else
			type = "file";
	}
	
	public static boolean isToday(Date date) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    return dateFormat.format(date).equals(dateFormat.format(new Date()));
	}
}
