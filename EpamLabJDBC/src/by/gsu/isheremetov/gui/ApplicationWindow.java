package by.gsu.isheremetov.gui;

import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;

import by.gsu.isheremetov.controllers.DatabaseConnector;
import by.gsu.isheremetov.gui.LoginDialog;
import by.gsu.isheremetov.gui.AdminUserEditDialog;
import by.gsu.isheremetov.models.Service;
import by.gsu.isheremetov.models.User;
import by.gsu.isheremetov.models.containers.Container;

import javax.swing.DefaultListModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ApplicationWindow {

	private JFrame frame;
	private static Boolean authorized;
	private static User user;
	private static Container<User> userContainer;
	private static Container<Service> userServices;
	private static DefaultListModel<String> listModel;
	private static JList<String> list;

	public static DefaultListModel<String> getListModel() {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		if (user.getRole().equals(User.USERROLE_ADMIN)) {
			for (int i = 0; i < userContainer.size(); i++) {
				listModel.addElement(userContainer.get(i).getUsername());
			}
		} else {
			for (int i = 0; i < userServices.size(); i++) {
				listModel.addElement(userServices.get(i).getName());
			}
		}
		return listModel;
	}

	public static void updateListModel() {
		// кривая процедурщина!
		// надо событие
		listModel = getListModel();
		list.setModel(listModel);
	}

	private void makeBills() throws SQLException {
		DatabaseConnector.execute("call makeBills();");
	}

	public static void loadUsers() throws SQLException {
		userContainer.clear();
		ResultSet rs = DatabaseConnector.executeQuery("select * from users");
		while (rs.next()) {
			userContainer.add(new User(rs.getInt("id"), rs.getString("login"),
					rs.getString("password"), rs.getString("username"), rs
							.getString("role"), rs.getBoolean("active")));
		}
		rs.close();
	}

	public static void loadSubscribedServices() throws SQLException {
		userServices.clear();
		ResultSet rs = DatabaseConnector
				.executeQuery("select * from services, subscribes where services.id = subscribes.serviceid and subscribes.userid = "
						+ user.getId() + " and subscribes.active > 0");
		while (rs.next()) {
			userServices.add(new Service(rs.getInt("id"), rs.getString("name"),
					rs.getInt("price")));
		}
		rs.close();
	}

	public static void setAuthorized(Boolean authorized) {
		ApplicationWindow.authorized = authorized;
	}

	public static void setUser(int id, String login, String password,
			String username, String role, Boolean active) {
		user = new User(id, login, password, username, role, active);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DatabaseConnector.getInstance();
					ApplicationWindow window = new ApplicationWindow();
					if (!authorized) {
						window.frame.dispose();
						DatabaseConnector.dispose();
					} else {
						window.frame.setVisible(true);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"Ошибка!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		authorized = false;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// задаем внешний вид такой же, как у системы
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка!",
					JOptionPane.ERROR_MESSAGE);
		}

		// выбрасываем диалог авторизации
		LoginDialog loginDialog = new LoginDialog(frame);
		loginDialog.setVisible(true);

		// создаем фрейм
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// если авторизация не прошла - закрыть приложение
		if (authorized) {
			JLabel lblUserName = new JLabel("Приветствие");
			lblUserName.setText("Здравствуйте, " + user.getUsername()
					+ ". Вы имеете права " + user.getRole() + ".");
			JLabel lblListHeader = new JLabel();

			// создание коллекции с пользователями либо с услугами, в
			// зависимости от прав
			userContainer = new Container<User>();
			userServices = new Container<Service>();
			try {
				if (user.getRole().equals(User.USERROLE_ADMIN)) {
					lblListHeader.setText("Список пользователей:");
					loadUsers();
				} else {
					lblListHeader.setText("Активные услуги:");
					loadSubscribedServices();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка!",
						JOptionPane.ERROR_MESSAGE);
			}

			// Забиваем в список либо пользователей, либо подписанные услуги
			listModel = getListModel();
			list = new JList<String>(listModel);
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			list.setLayoutOrientation(JList.VERTICAL_WRAP);
			list.setSelectedIndex(0);

			// меню
			JMenuBar menuBar = new JMenuBar();
			frame.setJMenuBar(menuBar);
			JMenu menuFile = new JMenu("Файл");
			JMenu menuEdit = new JMenu("Пользователь");
			menuBar.add(menuFile);
			menuBar.add(menuEdit);
			// файл - выход
			JMenuItem menuItemExit = new JMenuItem("Выход");
			menuItemExit.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
					for (Frame fr : Frame.getFrames()) {
						fr.dispose();
					}
					try {
						DatabaseConnector.dispose();
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Ошибка!", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			menuFile.add(menuItemExit);
			// если админ - редактировать пользователя
			if (user.getRole().equals(User.USERROLE_ADMIN)) {
				JMenuItem menuItemEditUser = new JMenuItem("Редактировать");
				menuItemEditUser.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						AdminUserEditDialog adminUEDialog = new AdminUserEditDialog(
								true,
								userContainer.get(list.getSelectedIndex()));
						adminUEDialog.setVisible(true);
					}
				});
				//
				JMenuItem menuItemAddUser = new JMenuItem("Добавить");
				menuItemAddUser.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						AdminUserEditDialog adminUEDialog = new AdminUserEditDialog(
								false, null);
						adminUEDialog.setVisible(true);
					}
				});
				//
				JMenuItem menuItemAdminBills = new JMenuItem("Счета");
				menuItemAdminBills.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						if (list.getSelectedIndex() >= 0) {
							BillsDialog BillsDialog = new BillsDialog(
									userContainer.get(list.getSelectedIndex()));
							BillsDialog.setVisible(true);
						} else {
							JOptionPane.showMessageDialog(null,
									"Не выбран пользователь.", "Ошибка.",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				});
				//
				JMenuItem menuItemBills = new JMenuItem("Выставить счета");
				menuItemBills.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						try {
							makeBills();
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(),
									"Ошибка!", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				//
				menuEdit.add(menuItemEditUser);
				menuEdit.add(menuItemAddUser);
				menuEdit.add(menuItemAdminBills);
				menuEdit.add(menuItemBills);
			} else {
				JMenuItem menuServicesUser = new JMenuItem("Услуги");
				menuServicesUser.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						UserEditServices ueServices = new UserEditServices(user);
						ueServices.setVisible(true);
					}
				});
				//
				JMenuItem menuItemBills = new JMenuItem("Счета");
				menuItemBills.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						BillsDialog BillsDialog = new BillsDialog(user);
						BillsDialog.setVisible(true);
					}
				});
				//
				menuEdit.add(menuItemBills);
				menuEdit.add(menuServicesUser);
			}
			// /меню

			// формируем вывод
			GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
			groupLayout
					.setHorizontalGroup(groupLayout
							.createParallelGroup(Alignment.LEADING)
							.addGroup(
									groupLayout
											.createSequentialGroup()
											.addContainerGap()
											.addGroup(
													groupLayout
															.createParallelGroup(
																	Alignment.LEADING)
															.addComponent(
																	lblListHeader)
															.addComponent(
																	lblUserName,
																	GroupLayout.DEFAULT_SIZE,
																	422,
																	Short.MAX_VALUE)
															.addComponent(
																	list,
																	GroupLayout.PREFERRED_SIZE,
																	179,
																	GroupLayout.PREFERRED_SIZE))
											.addContainerGap()));
			groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
					Alignment.LEADING).addGroup(
					groupLayout
							.createSequentialGroup()
							.addComponent(lblUserName)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblListHeader)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(list, GroupLayout.DEFAULT_SIZE, 208,
									Short.MAX_VALUE).addContainerGap()));
			frame.getContentPane().setLayout(groupLayout);
		}
	}
}
