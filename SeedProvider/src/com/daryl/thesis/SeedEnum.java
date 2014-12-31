package com.daryl.thesis;

public enum SeedEnum {
	instance;
	private String seed;

	private SeedEnum() {

	}
	
	public String getSeed()
	{
		return seed;
	}
	
	public void setSeed(String newSeed)
	{
		seed = newSeed;
	}

}
