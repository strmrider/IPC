package interfaces;
import models.Event;
import models.Process;


public interface ServiceHandlers {
	
	public Event getEvent(String event);
	public void addEvent(Event event);
	public void deleteEvent(String event);
	
	public Process getProcess(long id);
	public void deleteProcess(long id);

}
