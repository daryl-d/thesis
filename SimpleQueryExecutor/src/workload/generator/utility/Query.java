package workload.generator.utility;
import java.io.Serializable;


public class Query implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String security;
	public String date;
	public String lowerTime;
	public String upperTime;
	
	public Query(String security, String date, String lower, String upper)
	{
		this.security = security;
		this.date = date;
		this.lowerTime = lower;
		this.upperTime = upper;
	}
	
	public String getSecurity() {
		return security;
	}
	public void setSecurity(String security) {
		this.security = security;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLowerTime() {
		return lowerTime;
	}
	public void setLowerTime(String lowerTime) {
		this.lowerTime = lowerTime;
	}
	public String getUpperTime() {
		return upperTime;
	}
	public void setUpperTime(String upperTime) {
		this.upperTime = upperTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString()
	{
		return security + "," + date + "," +lowerTime +"," + upperTime;
	}
	

}
