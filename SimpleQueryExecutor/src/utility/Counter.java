package utility;
public enum Counter {
	instance;
	int i = 0;
	
	private Counter() {

	}
	
	public int get()
	{
		return i;
	}
	
	public void increment()
	{
		++i;
	}
	
	
}
