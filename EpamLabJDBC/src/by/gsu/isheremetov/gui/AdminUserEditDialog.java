package by.gsu.isheremetov.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

import by.gsu.isheremetov.controllers.DatabaseConnector;
import by.gsu.isheremetov.gui.tableModels.BoolTableModel;
import by.gsu.isheremetov.models.Service;
import by.gsu.isheremetov.models.Subscribe;
import by.gsu.isheremetov.models.User;
import by.gsu.isheremetov.models.containers.Container;
import by.gsu.isheremetov.models.containers.SubscribesContainer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class AdminUserEditDialog extends JDialog {

	private static final long serialVersionUID = -6511307404180574652L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldLogin;
	private JTextField textFieldPassword;
	private JTextField textFieldUsername;
	private JTable tableServices;
	private Container<Service> serviceContainer;
	private SubscribesContainer subscribesContainer;

	private void updateUserByAdmin(int userID, String rights, Boolean active)
			throws SQLException {
		int isActive = active ? 1 : 0;
		String query = "UPDATE users SET login = '" + textFieldLogin.getText()
				+ "', password = '" + textFieldPassword.getText()
				+ "', username = '" + textFieldUsername.getText()
				+ "', role = '" + rights + "', active = " + isActive
				+ " WHERE id = " + userID;
		DatabaseConnector.executeUpdate(query);
		for (int i = 0; i < tableServices.getRowCount(); i++) {
			if ((boolean) tableServices.getValueAt(i, 2)
					&& !subscribesContainer.contains(serviceContainer.get(i))) {
				DatabaseConnector
						.executeUpdate("INSERT INTO subscribes (userid, serviceid) VALUES ("
								+ userID
								+ ","
								+ serviceContainer.get(i).getId() + ")");
			} else if (!(boolean) tableServices.getValueAt(i, 2)
					&& subscribesContainer.contains(serviceContainer.get(i))) {
				DatabaseConnector
						.executeUpdate("UPDATE subscribes SET active = 0 where userid = "
								+ userID
								+ " and serviceid = "
								+ serviceContainer.get(i).getId());
			}
		}
	}

	private void createUserByAdmin(String rights, Boolean active)
			throws SQLException {
		int isActive = active ? 1 : 0;
		String query = "INSERT INTO users (login, password, username, role, active) VALUES('"
				+ textFieldLogin.getText()
				+ "', '"
				+ textFieldPassword.getText()
				+ "', '"
				+ textFieldUsername.getText()
				+ "', '"
				+ rights
				+ "', "
				+ isActive + ")";
		DatabaseConnector.executeUpdate(query);
		ResultSet rs = DatabaseConnector
				.executeQuery("SELECT id FROM users WHERE login = '"
						+ textFieldLogin.getText() + "' AND password = '"
						+ textFieldPassword.getText() + "'");
		int userID = 0;
		if (rs.next()) {
			userID = rs.getInt("id");
		} else {
			throw new SQLException("Пользователю не добавлены услуги.");
		}
		for (int i = 0; i < tableServices.getRowCount(); i++) {
			if ((boolean) tableServices.getValueAt(i, 2)) {
				DatabaseConnector
						.executeUpdate("INSERT INTO subscribes (userid, serviceid) VALUES ("
								+ userID
								+ ","
								+ serviceContainer.get(i).getId() + ")");
			}
		}
		rs.close();
	}

	private void loadServices() throws SQLException {
		serviceContainer.clear();
		ResultSet rs = DatabaseConnector.executeQuery("select * from services");
		while (rs.next()) {
			serviceContainer.add(new Service(rs.getInt("id"), rs
					.getString("name"), rs.getInt("price")));
		}
		rs.close();
	}

	private void loadData(int userID) throws SQLException {
		serviceContainer.clear();
		subscribesContainer.clear();
		ResultSet rs = DatabaseConnector.executeQuery("select * from services");
		while (rs.next()) {
			serviceContainer.add(new Service(rs.getInt("id"), rs
					.getString("name"), rs.getInt("price")));
		}
		rs = DatabaseConnector
				.executeQuery("select * from subscribes where userid = "
						+ userID + " and active > 0");
		while (rs.next()) {
			subscribesContainer.add(new Subscribe(rs.getInt("id"), rs
					.getInt("userid"), rs.getInt("serviceid"), rs
					.getInt("active")));
		}
		rs.close();
	}

	public AdminUserEditDialog(final Boolean edit, final User user) {
		setModal(true);
		// выбираем режим - создание пользователя или редактирование
		if (edit) {
			setTitle("Редактирование пользователя");
		} else {
			setTitle("Создание пользователя");
		}
		// создаем контейнеры
		serviceContainer = new Container<Service>();
		subscribesContainer = new SubscribesContainer();
		// забиваем данные в контейнеры
		try {
			if (edit) {
				loadData(user.getId());
			} else {
				loadServices();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка!",
					JOptionPane.ERROR_MESSAGE);
		}
		// всякая шляпа
		setBounds(100, 100, 570, 215);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblLogin = new JLabel("Логин");
		JLabel lblPassword = new JLabel("Пароль");
		JLabel lblName = new JLabel("Имя");
		JLabel lblRights = new JLabel("Права");

		textFieldLogin = new JTextField();
		textFieldLogin.setColumns(10);

		textFieldPassword = new JTextField();
		textFieldPassword.setColumns(10);

		textFieldUsername = new JTextField();
		textFieldUsername.setColumns(10);

		String[] rights = { User.USERROLE_ADMIN, User.USERROLE_USER };
		final JComboBox<String> comboBoxRights = new JComboBox<String>();
		comboBoxRights.setModel(new DefaultComboBoxModel<String>(rights));

		final JCheckBox checkBoxActive = new JCheckBox("Активен");

		JLabel label = new JLabel("Оформленные подписки");

		if (user != null && edit) {
			textFieldLogin.setText(user.getLogin());
			textFieldPassword.setText(user.getPassword());
			textFieldUsername.setText(user.getUsername());
			checkBoxActive.setSelected(user.getActive());
			comboBoxRights.setSelectedItem(user.getRole());
		}

		JPanel panel = new JPanel();

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel
				.setHorizontalGroup(gl_contentPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_contentPanel
																		.createSequentialGroup()
																		.addComponent(
																				lblLogin)
																		.addGap(18)
																		.addComponent(
																				textFieldLogin,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_contentPanel
																		.createParallelGroup(
																				Alignment.LEADING,
																				false)
																		.addGroup(
																				gl_contentPanel
																						.createSequentialGroup()
																						.addComponent(
																								lblRights)
																						.addPreferredGap(
																								ComponentPlacement.RELATED,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								comboBoxRights,
																								GroupLayout.PREFERRED_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.PREFERRED_SIZE))
																		.addGroup(
																				gl_contentPanel
																						.createSequentialGroup()
																						.addGroup(
																								gl_contentPanel
																										.createParallelGroup(
																												Alignment.LEADING)
																										.addComponent(
																												lblPassword)
																										.addComponent(
																												lblName))
																						.addPreferredGap(
																								ComponentPlacement.UNRELATED)
																						.addGroup(
																								gl_contentPanel
																										.createParallelGroup(
																												Alignment.LEADING)
																										.addComponent(
																												textFieldUsername,
																												GroupLayout.PREFERRED_SIZE,
																												GroupLayout.DEFAULT_SIZE,
																												GroupLayout.PREFERRED_SIZE)
																										.addComponent(
																												textFieldPassword,
																												GroupLayout.PREFERRED_SIZE,
																												GroupLayout.DEFAULT_SIZE,
																												GroupLayout.PREFERRED_SIZE))))
														.addComponent(
																checkBoxActive))
										.addGap(18)
										.addGroup(
												gl_contentPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(label)
														.addComponent(
																panel,
																GroupLayout.PREFERRED_SIZE,
																380,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		gl_contentPanel
				.setVerticalGroup(gl_contentPanel
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								Alignment.LEADING,
								gl_contentPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(lblLogin)
														.addComponent(
																textFieldLogin,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(label))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPanel
														.createParallelGroup(
																Alignment.TRAILING)
														.addGroup(
																gl_contentPanel
																		.createSequentialGroup()
																		.addGroup(
																				gl_contentPanel
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblPassword)
																						.addComponent(
																								textFieldPassword,
																								GroupLayout.PREFERRED_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				gl_contentPanel
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblName)
																						.addComponent(
																								textFieldUsername,
																								GroupLayout.PREFERRED_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				gl_contentPanel
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblRights)
																						.addComponent(
																								comboBoxRights,
																								GroupLayout.PREFERRED_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				checkBoxActive))
														.addComponent(
																panel,
																GroupLayout.DEFAULT_SIZE,
																97,
																Short.MAX_VALUE))
										.addGap(182)));
		// создаем и заполняем таблицу

		String[] header = { "Услуга", "Цена", "Подписка" };
		ArrayList<Vector<Object>> rows = new ArrayList<Vector<Object>>();
		for (int i = 0; i < serviceContainer.size(); i++) {
			Vector<Object> row = new Vector<Object>();
			row.add(serviceContainer.get(i).getName());
			row.add(serviceContainer.get(i).getPrice());
			if (edit) {
				row.add(subscribesContainer.contains(serviceContainer.get(i)));
			} else {
				row.add(Boolean.FALSE);
			}
			rows.add(row);
		}
		Object rowData[][] = new Object[rows.size()][3];
		for (int i = 0; i < rows.size(); i++) {
			rowData[i][0] = rows.get(i).get(0);
			rowData[i][1] = rows.get(i).get(1);
			rowData[i][2] = rows.get(i).get(2);
		}

		BoolTableModel tableModel = new BoolTableModel(header, rowData);

		panel.setLayout(null);
		tableServices = new JTable();
		tableServices.setModel(tableModel);

		JScrollPane scrollPane = new JScrollPane(tableServices);
		scrollPane.setBounds(0, 5, 380, 92);
		panel.add(scrollPane);
		// таблица создана
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						// jdbc
						try {
							if (edit) {
								updateUserByAdmin(user.getId(), comboBoxRights
										.getSelectedItem().toString(),
										checkBoxActive.isSelected());
							} else {
								createUserByAdmin(comboBoxRights
										.getSelectedItem().toString(),
										checkBoxActive.isSelected());
							}
							ApplicationWindow.loadUsers();
							if (edit) {
								loadData(user.getId());
							}
							ApplicationWindow.updateListModel();
							JOptionPane.showMessageDialog(null,
									"Данные успешно обновлены.", "Успех.",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(),
									"Ошибка!", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Отмена");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
