package com.thesis.daryl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;





public enum SaveData {
	instance;
	private static final String CATALINA_HOME = "CATALINA_HOME";
	private File f = new File(System.getenv(CATALINA_HOME) + "/data-log-"+ System.currentTimeMillis() + ".txt");
	private BufferedWriter bw;
	
	private SaveData() {
		try
		{
			//System.out.println(f.getAbsolutePath());
			bw = new BufferedWriter(new FileWriter(f));
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run()
				{
					SaveData.instance.close();
				}
			});
			
		}
		catch(Exception ex)
		{
			System.exit(0);
		}
	}

	public void addPoint(String l) {
		try
		{
			Date date = new Date();
			bw.append(String.valueOf(l) + "#" + new Timestamp(date.getTime()) + "\n");
			bw.flush();
		}
		catch(Exception ex)
		{
			
		}
	}

	public void close() {
		if(bw != null)
		{
			try
			{
				bw.flush();
				bw.close();
			}
			catch(Exception ex)
			{
				
			}

		}
	}

}
