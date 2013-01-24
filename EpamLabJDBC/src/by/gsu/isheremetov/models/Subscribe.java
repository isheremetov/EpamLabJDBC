package by.gsu.isheremetov.models;

public class Subscribe {
	private int id;
	private int userID;
	private int serviceID;
	private int active;

	public Subscribe(int id, int userID, int serviceID, int active) {
		setId(id);
		setUserID(userID);
		setServiceID(serviceID);
		setActive(active);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getServiceID() {
		return serviceID;
	}

	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
}
