
public enum Action {
	NONE (-1),
	REGISTER (0),
	LISTEN_ON (1),
	LISTEN_OFF (2),
	CREATE_EVENT (3),
	DISSOLVE_EVENT (4),
	SUBSCRIBE (5),
	UNSUBSCRIBE (6),
	EMIT (7),
	OPEN_EVENT (8),
	CONTACT_PROCESS (9),
	SINGLE_EMISSION (10),
	MULTI_EMISSION (11);
	
	private int value;
	private Action(int value)
	{
		this.value = value;
	}
	
	public static Action fromByte(byte action)
	{
		int toInt = action;
		for (Action a: Action.values())
		{
			if (a.value == toInt) {
                return a;
            }
		}
		return NONE;
	}
	
	public static byte toByte(Action action)
	{
		for (Action a: Action.values())
		{
			if (a == action) {
                return (byte)a.value;
            }
		}
		return -1;
	}
}
