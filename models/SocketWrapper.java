package models;
import java.net.*;
import java.io.*;

import exceptions.SocketException;

public class SocketWrapper {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	
	public SocketWrapper(Socket socket)
	{
		try {
			this.socket = socket;
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] receive() throws SocketException
	{
		int size;
		try {
			size = input.readInt();
			byte[] dataBuffer = new byte[size];
			input.read(dataBuffer);
			return dataBuffer;
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketException("Failed to read data from socket stream");
		}
	}
	
	public void send(byte[] data) throws SocketException
	{
		try {
			output.writeInt(data.length);
			output.write(data);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketException("Failed to write data to socket stream");
		}
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public void sendID(long id) throws SocketException
	{
		try {
			output.writeLong(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SocketException("Failed to send id");
		}
	}
}
