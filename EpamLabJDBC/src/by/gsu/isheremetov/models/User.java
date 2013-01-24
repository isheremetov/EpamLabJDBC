package by.gsu.isheremetov.models;

public class User {
	public final static String USERROLE_ADMIN = "Admin";
	public final static String USERROLE_USER = "User";

	private int id;
	private String login;
	private String password;
	private String username;
	private String role;
	private Boolean active;

	public User(int id, String login, String password, String username,
			String role, Boolean active) {
		setId(id);
		setLogin(login);
		setPassword(password);
		setUsername(username);
		setRole(role);
		setActive(active);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
