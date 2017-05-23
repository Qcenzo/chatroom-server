package com.qcenzo.apps.chatroom.servlets;

import java.io.FileOutputStream;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class MediaUploadServlet extends HttpServlet
{
	ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			request.setCharacterEncoding("UTF-8");
			List<FileItem> fls = upload.parseRequest(request);
			for (FileItem f:fls)
			{
				if (f.getName() != null)
				{
					FileOutputStream fs = new FileOutputStream(getServletContext().getRealPath(getServletConfig().getInitParameter("path") 
							+ "\\" + f.getName()));  
					fs.write(f.get());  
					fs.close();  
					break;
				}
			}
		} 
		catch (Throwable e)
		{
			e.printStackTrace();
		}  
	}
}
