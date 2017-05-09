package com.qcenzo.apps.chatroom;

import org.red5.server.api.IConnection;

public class User
{
	public IConnection conn;
	public Object[] params;
	
	public User(IConnection conn, Object[] params)
	{
		this.conn = conn;
		this.params = params;
	}
	
	public String name()
	{
		return (String) params[0];
	}
}
