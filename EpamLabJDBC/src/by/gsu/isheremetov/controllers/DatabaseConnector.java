package by.gsu.isheremetov.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnector {
	private static DatabaseConnector instance;
	private static Statement st;
	private static Connection connection;

	private DatabaseConnector() throws SQLException, FileNotFoundException,
			IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream("MySQL.properties"));
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		String url = "jdbc:mysql://" + properties.getProperty("server") + "/"
				+ properties.getProperty("database");
		connection = DriverManager.getConnection(url,
				properties.getProperty("user"),
				properties.getProperty("password"));
		st = connection.createStatement();
	}

	public static DatabaseConnector getInstance() throws SQLException,
			FileNotFoundException, IOException {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}

	public static ResultSet executeQuery(String query) throws SQLException {
		return st.executeQuery(query);
	}

	public static boolean execute(String query) throws SQLException {
		return st.execute(query);
	}

	public static int executeUpdate(String query) throws SQLException {
		return st.executeUpdate(query);
	}

	public static void dispose() throws SQLException {
		connection.close();
		st.close();
	}

}
