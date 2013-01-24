package by.gsu.isheremetov.models;

import java.util.Date;

public class Bill {
	private int id;
	private int subscribeID;
	private int payed;
	private Date date;

	public Bill(int id, int subscribeID, int payed, Date date) {
		setId(id);
		setSubscribeID(subscribeID);
		setPayed(payed);
		setDate(date);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubscribeID() {
		return subscribeID;
	}

	public void setSubscribeID(int subscribeID) {
		this.subscribeID = subscribeID;
	}

	public int getPayed() {
		return payed;
	}

	public void setPayed(int payed) {
		this.payed = payed;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
