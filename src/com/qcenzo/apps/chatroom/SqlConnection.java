package com.qcenzo.apps.chatroom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class SqlConnection 
{
	private Connection conn;
	private SimpleDateFormat fmt;
	private ArrayList<Object[]> cache;
	
	public SqlConnection()
	{
		try
		{ 
			Properties p = new Properties();
			p.load(getClass().getClassLoader().getResourceAsStream("../db.properties"));
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + p.getProperty("host") + ":" + p.getProperty("port") + "/" + p.getProperty("db"), 
					p.getProperty("user"), p.getProperty("pwd"));
			
			fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cache = new ArrayList<Object[]>();
			ResultSet rs = conn.createStatement().executeQuery("select * from play_list");
			int n = rs.getMetaData().getColumnCount();
			int i;
			Object[] row;
			Object o;
			while (rs.next())
			{
				i = 1;
				row = new Object[n];
				while (i <= n)
				{
					o = rs.getObject(i++);
					row[i - 2] = o instanceof Timestamp ? fmt.format(o) : o;
				}
				cache.add(row);
			}
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public void insert(String name, String creator, String type)
	{
		try
		{
			String time = fmt.format(new Date());
			cache.add(new Object[]{name, creator, time, type});

			conn.createStatement().execute("insert into play_list values('" + name + "','" + creator + "', '" + time + "','" + type + "')");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean update(String name)
	{
		try
		{
			int n = cache.size();
			for (int i = 0; i < cache.size(); i++)
				if (cache.get(i)[1].equals(name) && !cache.get(i)[3].equals("vod"))
					cache.remove(i--); 
			
			if (n == cache.size())
				return false;
			
			conn.createStatement().execute("delete from play_list where creator='" + name + "' and type<>'vod'");
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return true;
	}
	
	public Object[] data()
	{
		return cache.toArray();
	}
}
