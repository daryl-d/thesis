package com.daryl.thesis.loader.helper;
import java.util.Scanner;

public class Tick {
	String Instrument;
	int Date;
	int Time;
	String Record_Type;
	double Price;
	int Volume;
	int Undisclosed_Volume;
	double Value;
	String Qualifiers;
	long Trans_ID;
	long Bid_ID;
	long Ask_ID;
	String Bid_Ask;
	String Entry_Time;
	double Old_Price;
	int Old_Volume;
	long Buyer_Broker_ID;
	long Seller_Broker_ID;

	public Tick() {

	}

	/**
	 * Instrument Date Time Record Type Price Volume Undisclosed Volume Value
	 * Qualifiers Trans ID Bid ID Ask ID Bid/Ask Entry Time Old Price Old Volume
	 * Buyer Broker ID Seller Broker ID
	 */
	public Tick(Scanner s, String line) {

		int counter = 0;

		while (s.hasNext()) {
			switch (counter) {
			case 0:
				Instrument = s.next();
				break;
			case 1:
					Date = s.nextInt();
				break;
			case 2:
				try
				{
				String token = s.next();
				int hour = Integer.parseInt(token.substring(0, 2));
				int min = Integer.parseInt(token.substring(3, 5));
				int sec = Integer.parseInt(token.substring(6, 8));
				int millis = Integer.parseInt(token.substring(9, 12));
				Time = ((hour * 60 + min) * 60 + sec) * 1000 + millis;
				}catch(Exception ex)
				{
				//	System.out.println( "ex: " + line );
				}
				break;
			case 3:
				Record_Type = s.next();
				break;
			case 4:
				try
				{
					Price = s.nextDouble();	
				}
				catch( Exception ex )
				{
					Price = 0.0;
				}
				
				break;
			case 5:
				try
				{
					Volume = s.nextInt();	
				} 
				catch( Exception ex )
				{
					Volume = 0;
				}
				
				break;
			case 6:
				try
				{
					Undisclosed_Volume = s.nextInt();	
				} 
				catch(Exception ex )
				{
					Undisclosed_Volume = 0;
				}
				
				break;
			case 7:
				try
				{
					Value = s.nextDouble();
				} catch( Exception ex )
				{
					Value = 0.0;
				}
				break;
			case 8:
				Qualifiers = s.next();
				break;
			case 9:
				try
				{
			    	Trans_ID = s.nextLong();
				}
				catch( Exception ex )
				{
					Trans_ID = 0;
				}
				
				break;
			case 10:
				try
				{
					Bid_ID = s.nextLong();
				}
				catch( Exception ex )
				{
					Bid_ID = 0;
				}
				break;
			case 11:
				try 
				{
					Ask_ID = s.nextLong();
				} 
				catch ( Exception ex ) 
				{
					Ask_ID = 0;
				}
				break;
			case 12:
				Bid_Ask = s.next();
				break;
			case 13:
				Entry_Time = s.next();
				break;
			case 14:
				try
				{
					Old_Price = s.nextDouble();
				}catch( Exception ex )
				{
					Old_Price = 0.0;
				}
				break;
			case 15:
				try
				{
					Old_Volume = s.nextInt();
				}
				catch( Exception ex )
				{
					Old_Volume = 0;
				}
				break;
			case 16:
				try 
				{
					Buyer_Broker_ID = s.nextLong();
				} 
				catch (Exception ex) 
				{
					Buyer_Broker_ID = 0;
				}

				break;
			case 17:
				try 
				{
					Seller_Broker_ID = s.nextLong();
				} catch ( Exception ex ) {
					Seller_Broker_ID = 0;
				}
				break;
			default:
				s.nextLine();
			}

			++counter;
		}
	}

	public String getInstrument() {
		return Instrument;
	}

	public void setInstrument(String instrument) {
		Instrument = instrument;
	}

	public int getDate() {
		return Date;
	}

	public void setDate(int date) {
		Date = date;
	}

	public int getTime() {
		return Time;
	}

	public void setTime(int time) {
		Time = time;
	}

	public String getRecord_Type() {
		return Record_Type;
	}

	public void setRecord_Type(String record_Type) {
		Record_Type = record_Type;
	}

	public double getPrice() {
		return Price;
	}

	public void setPrice(double price) {
		Price = price;
	}

	public int getVolume() {
		return Volume;
	}

	public void setVolume(int volume) {
		Volume = volume;
	}

	public int getUndisclosed_Volume() {
		return Undisclosed_Volume;
	}

	public void setUndisclosed_Volume(int undisclosed_Volume) {
		Undisclosed_Volume = undisclosed_Volume;
	}

	public double getValue() {
		return Value;
	}

	public void setValue(double value) {
		Value = value;
	}

	public String getQualifiers() {
		return Qualifiers;
	}

	public void setQualifiers(String qualifiers) {
		Qualifiers = qualifiers;
	}

	public long getTrans_ID() {
		return Trans_ID;
	}

	public void setTrans_ID(long trans_ID) {
		Trans_ID = trans_ID;
	}

	public long getBid_ID() {
		return Bid_ID;
	}

	public void setBid_ID(long bid_ID) {
		Bid_ID = bid_ID;
	}

	public long getAsk_ID() {
		return Ask_ID;
	}

	public void setAsk_ID(long ask_ID) {
		Ask_ID = ask_ID;
	}

	public String getBid_Ask() {
		return Bid_Ask;
	}

	public void setBid_Ask(String bid_Ask) {
		Bid_Ask = bid_Ask;
	}

	public String getEntry_Time() {
		return Entry_Time;
	}

	public void setEntry_Time(String entry_Time) {
		Entry_Time = entry_Time;
	}

	public double getOld_Price() {
		return Old_Price;
	}

	public void setOld_Price(double old_Price) {
		Old_Price = old_Price;
	}

	public int getOld_Volume() {
		return Old_Volume;
	}

	public void setOld_Volume(int old_Volume) {
		Old_Volume = old_Volume;
	}

	public long getBuyer_Broker_ID() {
		return Buyer_Broker_ID;
	}

	public void setBuyer_Broker_ID(long buyer_Broker_ID) {
		Buyer_Broker_ID = buyer_Broker_ID;
	}

	public long getSeller_Broker_ID() {
		return Seller_Broker_ID;
	}

	public void setSeller_Broker_ID(long seller_Broker_ID) {
		Seller_Broker_ID = seller_Broker_ID;
	}

}
