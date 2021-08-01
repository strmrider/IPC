import interfaces.Handler;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import exceptions.ListenerException;
import exceptions.SocketException;
import models.SocketWrapper;


public class Listener implements Runnable{
	SocketWrapper socket;
	long id;
	Hashtable<String, Handler> handlers = new Hashtable<String, Handler>();
	ArrayList<String> adminEvents = new ArrayList<String>();
	ArrayList<String> emissions = new ArrayList<String>();
	Handler directConatctHandler = null;
	
	Listener(int port) throws SocketException
	{
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		id = Long.parseLong(processName.split("@")[0]);
		try {
			socket = new SocketWrapper(new Socket("127.0.0.1", port));
			socket.sendID(id);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SocketException("Failed to connect the service");
		}
	}
	
	public void listen(Handler handler)
	{
		directConatctHandler = handler;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private void listen()
	{
		byte[] response = null;
		Action action;
		while (true)
		{
			try {
				response = socket.receive();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			action = Action.fromByte(response[0]);
			response = Util.subArray(response, 1, response.length-1);
			switch (action)
			{
			case CONTACT_PROCESS:
				directConatctHandler.call(response);
				break;
			case EMIT:
				handleEmission(response);
				break;
			case OPEN_EVENT:
				byte[] eventBin = Util.subArray(response, 0, response.length-1);
				String event = Util.bytesToString(eventBin);
				emissions.add(event);
				break;
			default:
				break;
			}
		}
	}
	
	// handles income event emission
	private void handleEmission(byte[] response)
	{
		int eventIdLen = Util.bytesToInt(Util.subArray(response, 0, 3));
		byte[] eventArray = Util.subArray(response, 4, 3+eventIdLen);
		String eventId = Util.bytesToString(eventArray);
		byte[] data = Util.subArray(response, 4+eventIdLen, response.length-1);

		Handler handler = handlers.get(eventId);
		if (handler != null)
			handler.call(data);
	}
	
	// Done
	public void contactProcess(int process, byte[] data) throws SocketException, ListenerException
	{
		if (process!= id)
		{
			byte[] pack =  
					Util.buildPack(Action.CONTACT_PROCESS, Util.intToBytes((int)process), data);
			socket.send(pack);
		}
		else
			throw new ListenerException("Can't contact local process");
	}

	public void subscribe(String event, Handler handler) throws SocketException
	{
		// prevents from subscribing to own events
		for (String eventId: adminEvents)
		{
			if (eventId == event)
				return;

		}
		handlers.put(event, handler);
		socket.send(Util.buildPack(Action.SUBSCRIBE, event.getBytes()));
	}
	
	public void unsubscribe(String event) throws SocketException
	{
		int index = Util.existInList(event, emissions);
		Handler handler = handlers.get(event);
		if (handler != null && index > 0)
		{
			handlers.remove(event);
			emissions.remove(index);
		}
		socket.send(Util.buildPack(Action.UNSUBSCRIBE, event.getBytes()));
	}
	
	public void createEvent(String event, int type, Handler handler) throws SocketException
	{
		byte[] pack = Util.buildPack(Action.CREATE_EVENT, 
				Util.intToBytes(type), event.getBytes());
		socket.send(pack);
		// multi emission
		if (type == 0 && handler != null)
			handlers.put(event, handler);
		else if (type == 1)
			adminEvents.add(event);
	}
	
	public void dissolveEvent(String event) throws SocketException
	{
		int index = Util.existInList(event, adminEvents);
		if (index > -1)
		{
			socket.send(Util.buildPack(Action.DISSOLVE_EVENT, event.getBytes()));
			adminEvents.remove(index);
		}
	}
	
	public void emit(String event, byte[] data) throws SocketException
	{
		if (Util.existInList(event, emissions) > -1 || Util.existInList(event, adminEvents) > -1)
		{
			byte[] pack = Util.buildPack(Action.EMIT, 
					Util.intToBytes(event.length()), 
					event.getBytes(), 
					data);
			socket.send(pack);
		}
	}
	
	public ArrayList<String> getSubscriptions()
	{
		ArrayList<String> subs = new ArrayList<String>();
		Enumeration<String> keys = handlers.keys();
		while(keys.hasMoreElements())
			subs.add(keys.nextElement());
		
		return subs;
	}
	
	public long getID()
	{
		return id;
	}
	
	
	@Override
	public void run() {
		listen();
	}
}
