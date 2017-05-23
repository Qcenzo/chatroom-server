package com.qcenzo.apps.chatroom;

import java.util.HashMap;

import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.IServiceCapableConnection;

public class Application extends ApplicationAdapter  
{
	private static HashMap<String, User> map;
	private static SqlConnection sqlc; 
	
	public boolean start(IScope app)   
	{
		map = new HashMap<String, User>();
		sqlc = new SqlConnection();   
		return super.start(app); 
	}
	 
	public boolean connect(IConnection conn, IScope app, Object[] params)
	{
		User u = new User(conn, params);
		String uname = u.name();
		if (map.containsKey(uname)) 
		{
			if (u.priority() <= map.get(uname).priority())
			{
				rejectClient();
				return false;
			}
			IServiceCapableConnection c = (IServiceCapableConnection) map.remove(uname).conn;
			c.invoke("quit");
		}
		map.put(uname, u); 
		return super.connect(conn, app, params);   
	}
	
	public boolean join(IClient client, IScope app)
	{
		broadcastVisitorList();
		broadcastVideoList();
		return super.join(client, app);
	}
	
	public void disconnect(IConnection conn, IScope app)
	{
		updateVideoList();
		
		for (String uname:map.keySet())
			if (map.get(uname).conn.equals(conn))
			{
				map.remove(uname);
				break;
			}
		broadcastVisitorList();
		
		super.disconnect(conn, app);
	}
	
	public void groupChat(String message)
	{
		broadcast("receiveGroupChat", new String[]{message});
	}
	
	public void privateChat(String from, String to, String message)
	{
		if (map.containsKey(to)) 
		{
			IServiceCapableConnection c = (IServiceCapableConnection) map.get(to).conn;
			if (c != null)
				c.invoke("receivePrivateChat", new String[]{from, message});
		}
	}
	
	public void insertVideoList(String name, String creator, String type)
	{
		sqlc.insert(name, creator, type);
		broadcastVideoList();  
	}
	
	public void updateVideoList()
	{
		IConnection conn = Red5.getConnectionLocal();
		for (String uname:map.keySet())
		{
			if (map.get(uname).conn == conn)
			{
				if (sqlc.update(uname))
					broadcastVideoList();
				break;
			}
		}
	}
	
	private void broadcastVisitorList()
	{ 
		Object[] params = new Object[map.size() * 2];
		Object[] item;
		int i = 0;
		for (String uname:map.keySet())
		{
			item = map.get(uname).params;
			params[i++] = item[0];
			params[i++] = item[1];
		}
		broadcast("receiveVisitorList", params);
	}
	
	private void broadcastVideoList() 
	{
		broadcast("receiveVideoList", sqlc.data()); 
	}
	
	private void broadcast(String function, Object[] params)
	{
		IServiceCapableConnection c;
		for (String uname:map.keySet())
		{
			c = (IServiceCapableConnection)map.get(uname).conn;
			if (c != null)
				c.invoke(function, params);
		}
	}   
}
