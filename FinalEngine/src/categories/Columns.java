package categories;
/**
 * @author daryl Enum for columns returned using cassandra-jdbc driver
 * 
 */
public enum Columns {
	instrument, date, time, recordType, price, volume, undisclosedVolume, value, qualifiers, transId, bidId, askId, bidAsk, entryTime, oldPrice, oldVolume, buyerBrokerId, sellerBrokerId;
}
