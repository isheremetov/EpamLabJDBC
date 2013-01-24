package by.gsu.isheremetov.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;

import by.gsu.isheremetov.controllers.DatabaseConnector;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2602458533329722364L;
	private final JPanel contentPanel = new JPanel();
	private JLabel lblLogin;
	private JLabel lblPassword;
	private JTextField textFieldLogin;
	private JTextField textFieldPassword;

	/**
	 * Create the dialog.
	 */
	public LoginDialog(JFrame owner) {
		super(owner);
		setTitle("Авторизация");
		setModal(true);
		setBounds(100, 100, 230, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblLogin = new JLabel("Логин");
		}
		{
			lblPassword = new JLabel("Пароль");
		}
		textFieldLogin = new JTextField();
		textFieldLogin.setColumns(10);
		textFieldPassword = new JTextField();
		textFieldPassword.setColumns(10);
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
														.addComponent(lblLogin)
														.addComponent(
																lblPassword))
										.addGroup(
												gl_contentPanel
														.createParallelGroup(
																Alignment.LEADING,
																false)
														.addGroup(
																gl_contentPanel
																		.createSequentialGroup()
																		.addGap(18)
																		.addComponent(
																				textFieldLogin,
																				GroupLayout.DEFAULT_SIZE,
																				122,
																				Short.MAX_VALUE))
														.addGroup(
																gl_contentPanel
																		.createSequentialGroup()
																		.addGap(18)
																		.addComponent(
																				textFieldPassword)))
										.addGap(236)));
		gl_contentPanel
				.setVerticalGroup(gl_contentPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPanel
										.createSequentialGroup()
										.addGap(5)
										.addGroup(
												gl_contentPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(lblLogin)
														.addComponent(
																textFieldLogin,
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
																lblPassword)
														.addComponent(
																textFieldPassword,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(184, Short.MAX_VALUE)));
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Войти");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						// JDBC
						try {
							ResultSet rs = DatabaseConnector
									.executeQuery("SELECT count(*) as 'count' FROM users WHERE login = '"
											+ textFieldLogin.getText()
											+ "' AND password = '"
											+ textFieldPassword.getText() + "'");
							if (rs.next()) {
								if (rs.getInt("count") > 0) {
									rs = DatabaseConnector
											.executeQuery("SELECT id, login, password, username, role, active FROM users WHERE login = '"
													+ textFieldLogin.getText()
													+ "' AND password = '"
													+ textFieldPassword
															.getText() + "'");
									if (rs.next()) {
										ApplicationWindow.setUser(
												rs.getInt("id"),
												rs.getString("login"),
												rs.getString("password"),
												rs.getString("username"),
												rs.getString("role"),
												rs.getBoolean("active"));
										//
										if (rs.getBoolean("active")) {
											ApplicationWindow
													.setAuthorized(true);
										} else {
											JOptionPane
													.showMessageDialog(
															null,
															"Вы заблокированы.",
															"Информация.",
															JOptionPane.INFORMATION_MESSAGE);
											ApplicationWindow
													.setAuthorized(false);
										}
										//
										dispose();
									} else {
										JOptionPane
												.showMessageDialog(
														null,
														"Не удалось создать пользователя.",
														"Ошибка!",
														JOptionPane.ERROR_MESSAGE);
									}
								} else {
									// показать сообщение
									// такой пользователь не найден
									JOptionPane.showMessageDialog(null,
											"Пользователь не найден.",
											"Ошибка!",
											JOptionPane.ERROR_MESSAGE);
								}
								rs.close();
							}
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
						ApplicationWindow.setAuthorized(false);
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
