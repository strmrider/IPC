import interfaces.ServiceHandlers;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import exceptions.SocketException;
import models.Event;
import models.Process;


public class Service implements Runnable, ServiceHandlers {
	ServerSocket server;
	Hashtable<Long, Process> processes = new Hashtable<Long, Process>();
	Hashtable<String, Event> events = new Hashtable<String, Event>();

	public Service(int port) throws SocketException
	{
		try {
			server = new ServerSocket(port);  
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketException("Faile to run the server");
		}//establishes connection   
	}
	
	@Override
	public void run() {
		
		try {
			while (true)
			{
				Socket client = server.accept();
				handleProcess(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	private void handleProcess(Socket socket) throws SocketException
	{
		DataInputStream stream;
		try {
			stream = new DataInputStream(socket.getInputStream());
			long id = stream.readLong();
			System.out.println(id);
			Process process = new Process(socket, id);
			processes.put(id, process);
			ProcessHandler processHandler = new ProcessHandler(process, this);
			Thread thread = new Thread(processHandler);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Event getEvent(String event) {
		return events.get(event);
	}

	@Override
	public void addEvent(Event event) {
		events.put(event.getID(), event);
		
	}

	@Override
	public void deleteEvent(String event) {
		events.remove(event);
		
	}

	@Override
	public Process getProcess(long id) {
		return processes.get(id);
	}

	@Override
	public void deleteProcess(long id) {
		processes.remove(id);
	}
}
