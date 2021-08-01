package models;

import java.util.ArrayList;

public class Event {
	String id;
	int mode;
	long creator;
	ArrayList<Long> subscribers;
	
	public Event(String id, int mode, long creator)
	{
		this.id = id;
		this.mode = mode;
		this.creator = creator;
		subscribers = new ArrayList<Long>();
	}
	
	public void add_subscription(long id)
	{
		subscribers.add(id);
	}
	
	public void remove_subscription(long id)
	{
		subscribers.remove(id);
	}
	
	public ArrayList<Long> getSubs()
	{
		return subscribers;
	}
	
	public String getID()
	{
		return id;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public long getCreator()
	{
		return creator;
	}
	
	public int subs()
	{
		return subscribers.size();
	}
}
