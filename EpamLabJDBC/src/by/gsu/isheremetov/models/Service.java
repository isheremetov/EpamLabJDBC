package by.gsu.isheremetov.models;

public class Service {
	private int id;
	private String name;
	private int price;

	public Service(int id, String name, int price) {
		setId(id);
		setName(name);
		setPrice(price);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}
