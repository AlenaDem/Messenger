package com.Messenger.Controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Messenger.Settings;
import com.Messenger.Repo.ChatMessageRepository;
import com.Messenger.Repo.UserRepository;

@Controller
public class FileLoadController 
{
	@Autowired ChatMessageRepository msgRepo;
    
    @RequestMapping("files/{msgId}")
    public void getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("msgId") Long msgId) 
    {
    	if (!msgRepo.existsById(msgId))
    		return;
    	
    	var msg = msgRepo.findById(msgId).get();
    	try {
    		response.addHeader("Content-Disposition", "attachment; filename=" + msg.getAttachedFile());
    		response.getOutputStream().write(msg.getFile());
    		response.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Ошибка! Не удалось загрузить запрошенный файл.");
		}
    }
    
    @RequestMapping("files/")
    public void noFile(HttpServletRequest request, HttpServletResponse response) 
    {
    	try {
			response.getWriter().append("No file");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}