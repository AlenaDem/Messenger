package com.Messenger.Controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Messenger.Settings;

@Controller
public class FileDownloadController 
{
    @RequestMapping("files/{fileName}")
    public void downloadResource(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) 
    {
    	System.out.println("Request file:" + fileName);
        Path file = Paths.get(Settings.getFilesDir(), fileName);
        if (Files.exists(file)) 
        {
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try
            {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
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