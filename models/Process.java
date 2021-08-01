package models;

import java.net.Socket;
import java.util.ArrayList;

import exceptions.SocketException;

public class Process {
	private long id;
	SocketWrapper socket;
	ArrayList<String> subscriptions; 
	boolean isListening;
	
	public Process(Socket socket, long id)
	{
		this.id = id;
		this.socket = new SocketWrapper(socket);
		subscriptions = new ArrayList<String>();
	}
	
	public byte[] receive() throws SocketException
	{
		return socket.receive();
	}
	
	public void send(byte[] data) throws SocketException
	{
		socket.send(data);
	}
	
	public long getID()
	{
		return id;
	}
}
