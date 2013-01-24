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
import by.gsu.isheremetov.models.Bill;
import by.gsu.isheremetov.models.Service;
import by.gsu.isheremetov.models.User;
import by.gsu.isheremetov.models.containers.Container;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BillsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -58401160952490098L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private Container<Bill> billContainer;
	private Container<Service> serviceContainer;

	private void loadData(int userID) throws SQLException {
		billContainer.clear();
		ResultSet rs = DatabaseConnector
				.executeQuery("select bills.id, bills.subscribeid, bills.payed, bills.date, services.id, services.name, services.price"
						+ " from bills, subscribes, services where bills.subscribeid = subscribes.id and subscribes.userid = "
						+ userID + " and subscribes.serviceid = services.id");
		while (rs.next()) {
			billContainer.add(new Bill(rs.getInt(1), rs.getInt(2),
					rs.getInt(3), rs.getDate(4)));
			serviceContainer.add(new Service(rs.getInt(5), rs.getString(6), rs
					.getInt(7)));
		}
		rs.close();
	}

	/**
	 * Create the dialog.
	 */
	public BillsDialog(User user) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		contentPanel.add(scrollPane);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		billContainer = new Container<Bill>();
		serviceContainer = new Container<Service>();
		try {
			loadData(user.getId());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Ошибка!",
					JOptionPane.ERROR_MESSAGE);
		}
		//
		String[] header = { "Услуга", "Цена", "Оплата" };
		ArrayList<Vector<Object>> rows = new ArrayList<Vector<Object>>();
		for (int i = 0; i < serviceContainer.size(); i++) {
			Vector<Object> row = new Vector<Object>();
			row.add(serviceContainer.get(i).getName());
			row.add(serviceContainer.get(i).getPrice());
			row.add(billContainer.get(i).getPayed() > 0 ? true : false);
			rows.add(row);
		}
		Object rowData[][] = new Object[rows.size()][3];
		for (int i = 0; i < rows.size(); i++) {
			rowData[i][0] = rows.get(i).get(0);
			rowData[i][1] = rows.get(i).get(1);
			rowData[i][2] = rows.get(i).get(2);
		}
		BoolTableModel tableModel = new BoolTableModel(header, rowData);
		table.setModel(tableModel);
		//
		{
			JButton okButton = new JButton("OK");
			okButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try {
						for (int i = 0; i < table.getRowCount(); i++) {
							if ((boolean) table.getValueAt(i, 2)) {
								DatabaseConnector.executeUpdate("UPDATE bills SET payed = 1 WHERE id = "
										+ serviceContainer.get(i).getId());
							}
						}
						JOptionPane.showMessageDialog(null, "Данные успешно обновлены.",
								"Успех", JOptionPane.INFORMATION_MESSAGE);
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
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
