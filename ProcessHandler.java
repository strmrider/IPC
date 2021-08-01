import interfaces.ServiceHandlers;
import java.util.Iterator;
import exceptions.SocketException;
import models.Event;
import models.Process;

public class ProcessHandler implements Runnable {
	private ServiceHandlers serviceListener;
	private Process process;
	
	public ProcessHandler(Process process, ServiceHandlers listener) throws SocketException
	{
		serviceListener = listener;
		this.process = process;
	}
	
	@Override
	public void run(){
		try {
			listen();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	private void listen() throws SocketException
	{
		byte[] request;
		Action action;
		while (true)
		{
			try {
				request = process.receive();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			action = Action.fromByte(request[0]);
			request = Util.subArray(request, 1, request.length-1);
			switch (action)
			{
			case CREATE_EVENT:
				createEvent(request);
				break;
			case DISSOLVE_EVENT:
				dissolveEvent(request);
				break;
			case SUBSCRIBE:
			case UNSUBSCRIBE:
				subscription(action, request);
				break;
			case EMIT:
				emit(request);
				break;
			case CONTACT_PROCESS:
				contactProcess(request);
				break;
			default:
				break;
			}
		}
	}
	
	// Done
	private void createEvent(byte[] request)
	{
		int mode = Util.bytesToInt(Util.subArray(request, 0, 3));
		String eventName = Util.bytesToString(Util.subArray(request, 4, request.length-1));
		Event event = new Event(eventName, mode, process.getID());
		if (mode == 0)
			event.add_subscription(process.getID());
		serviceListener.addEvent(event);
	}
	
	private void dissolveEvent(byte[] request){
		String eventName = Util.bytesToString(request);
		Event event = serviceListener.getEvent(eventName);
		if (event != null)
		{
			if (event.getMode() == 1 && event.getCreator() == process.getID())
				serviceListener.deleteEvent(eventName);
		}
	}
	
	private void subscription(Action action, byte[] request) throws SocketException
	{
		String eventName = Util.bytesToString(request);
		Event event = serviceListener.getEvent(eventName);
		if (event != null)
		{
			if (action == Action.SUBSCRIBE)
			{
				event.add_subscription(process.getID());
				if (event.getMode() == 0)
				{
					byte[] pack = Util.buildPack(Action.OPEN_EVENT, event.getID().getBytes());
					process.send(pack);
				}
			}
			else if(action == Action.UNSUBSCRIBE)
			{
				event.remove_subscription(process.getID());
				if (event.getMode() == 0 && event.subs() == 0)
					serviceListener.deleteEvent(eventName);
			}
		}
	}
	
	private void emit(byte[] request) throws SocketException{
		int idLen = Util.bytesToInt(Util.subArray(request, 0, 3));
		String id = Util.bytesToString(Util.subArray(request, 4, 3+idLen));
		byte[] data = Util.subArray(request, 4+idLen,request.length-1);
		Event event = serviceListener.getEvent(id);
		if (event != null)
		{
			if (event.getMode() == 0 || 
					(event.getMode() == 1 && event.getCreator() == process.getID()))
			{
				byte[] pack = 
						Util.buildPack(Action.EMIT, Util.intToBytes(id.length()), id.getBytes(), data);
				long sub;
				Iterator<Long> iter = event.getSubs().iterator();
			      while (iter.hasNext()) {
			         sub = (long) iter.next();
			         if (sub != process.getID())
			        	 serviceListener.getProcess(sub).send(pack);
			      }
			}
		}
	}

	private void contactProcess(byte[] request) throws SocketException
	{
		int id = Util.bytesToInt(Util.subArray(request, 0, 3));
		byte[] data = Util.subArray(request, 4, request.length);
		Process targetProcess = serviceListener.getProcess(id);
		if (targetProcess != null)
			targetProcess.send(Util.buildPack(Action.CONTACT_PROCESS, data));
	}
}
