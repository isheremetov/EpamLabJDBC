package by.gsu.isheremetov.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import by.gsu.isheremetov.controllers.DatabaseConnector;
import by.gsu.isheremetov.gui.tableModels.BoolTableModel;
import by.gsu.isheremetov.models.Service;
import by.gsu.isheremetov.models.Subscribe;
import by.gsu.isheremetov.models.User;
import by.gsu.isheremetov.models.containers.Container;
import by.gsu.isheremetov.models.containers.SubscribesContainer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserEditServices extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6275496232883029623L;
	private final JPanel contentPanel = new JPanel();
	private JTable tableServices;
	private Container<Service> serviceContainer;
	private SubscribesContainer subscribesContainer;

	private void updateSubscribes(int userID) throws SQLException {
		for (int i = 0; i < tableServices.getRowCount(); i++) {
			if ((boolean) tableServices.getValueAt(i, 2) && !subscribesContainer.contains(serviceContainer.get(i))) {
				DatabaseConnector.executeUpdate("INSERT INTO subscribes (userid, serviceid) VALUES ("
					+ userID + "," + serviceContainer.get(i).getId() + ")");
			} else if ( !(boolean) tableServices.getValueAt(i, 2) && subscribesContainer.contains(serviceContainer.get(i))) { 
				DatabaseConnector.executeUpdate("UPDATE subscribes SET active = 0 where userid = " + userID + " and serviceid = " + serviceContainer.get(i).getId());
			} 
		}
	}

	private void loadData(int userID) throws SQLException {
		serviceContainer.clear();
		subscribesContainer.clear();
		ResultSet rs = DatabaseConnector.executeQuery("select * from services");
		while (rs.next()) {
			serviceContainer.add(new Service(rs.getInt("id"), rs
					.getString("name"), rs.getInt("price")));
		}
		rs = DatabaseConnector.executeQuery("select * from subscribes where userid = "
				+ userID + " and active > 0");
		while (rs.next()) {
			subscribesContainer.add(new Subscribe(rs.getInt("id"), rs
					.getInt("userid"), rs.getInt("serviceid"), rs.getInt("active")));
		}
		rs.close();
	}

	/**
	 * Create the dialog.
	 */
	public UserEditServices(final User user) {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		serviceContainer = new Container<Service>();
		subscribesContainer = new SubscribesContainer();
		try {
			loadData(user.getId());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка!",
					JOptionPane.ERROR_MESSAGE);
		}
		String[] header = { "Услуга", "Цена", "Подписка" };
		ArrayList<Vector<Object>> rows = new ArrayList<Vector<Object>>();
		for (int i = 0; i < serviceContainer.size(); i++) {
			Vector<Object> row = new Vector<Object>();
			row.add(serviceContainer.get(i).getName());
			row.add(serviceContainer.get(i).getPrice());
			row.add(subscribesContainer.contains(serviceContainer.get(i)));
			rows.add(row);
		}
		Object rowData[][] = new Object[rows.size()][3];
		for (int i = 0; i < rows.size(); i++) {
			rowData[i][0] = rows.get(i).get(0);
			rowData[i][1] = rows.get(i).get(1);
			rowData[i][2] = rows.get(i).get(2);
		}

		BoolTableModel tableModel = new BoolTableModel(header, rowData);

		tableServices = new JTable();
		tableServices.setModel(tableModel);

		JScrollPane scrollPane = new JScrollPane(tableServices);
		contentPanel.add(scrollPane);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						try {
							updateSubscribes(user.getId());
							loadData(user.getId());
							ApplicationWindow.loadSubscribedServices();
							ApplicationWindow.updateListModel();
							JOptionPane.showMessageDialog(null, "Подписки успешно обновлены.",
									"Успех.", JOptionPane.INFORMATION_MESSAGE);
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
				JButton cancelButton = new JButton("Cancel");
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
