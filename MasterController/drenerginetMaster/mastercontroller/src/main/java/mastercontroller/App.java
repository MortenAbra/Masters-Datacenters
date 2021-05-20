package mastercontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mastercontroller.Models.Guest;
import mastercontroller.Models.Record;
import mastercontroller.Models.Workload;
import mastercontroller.Services.EnergiDataFetcher;
import mastercontroller.Services.EnergiDataFetcher.OperationStage;
import mastercontroller.Services.GuestManager;
import mastercontroller.Services.VMManager;

public class App {

	private VMManager manager;
	public GuestManager guestManager;
	private WorkloadManager wm;
	private ScheduledExecutorService es;
	private JFrame frmVmManager;
	private JTextField migrationThresholdTextField;
	private JTextField migrationThresholdPercentTextField;
	private JPanel vmListPanel;
	private JPanel panel;
	private JPanel panel_2;
	private JPanel vmPropertiesPanel;
	private JPanel vmMigrationThresholdPanel;
	private JPanel panel_1;
	private JList vmList;
	private JCheckBox vmAutoMigrationSwitch;
	private JComboBox availableVMsComboBox;
	private JButton vmMigrationBtn;
	private JButton addWorkloadButton;
	private JButton migrationThresholdSetButton;
	private JLabel migrationThresholdLabel;
	private JLabel migrationThresholdPercentLabel;
	private JLabel lblNewLabel_1;
	private JLabel vmNameLabel;
	private JLabel vmNameResult;
	private JLabel vmIPLabel;
	private JLabel vmIPResult;
	private JLabel vmStatusLabel;
	private JLabel vmStatusResult;
	private JLabel vmMigrationLabel;
	private JLabel migrationStatusLabel;
	private JLabel migrationDummyLabel;
	private JLabel lblNewLabel;
	private DefaultListModel listModel;
	private Workload selectedWorkload;

	private String initialThresholdPercent = "5";
	private boolean initDone = false;

	private EnergiDataFetcher edf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				App window = new App();
				window.frmVmManager.setVisible(true);
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws IOException
	 */
	public App() {
		es = Executors.newScheduledThreadPool(10);
		this.wm = new WorkloadManager();
		this.manager = new VMManager(wm);
		guestManager = new GuestManager();
		guestManager.initialize(manager);

		setupWorkloads();
		setupEnergiDataLoop();
		initialize();
	}

	public void checkListModelDuplicates(VMManager manager, boolean done) {
		if (done && listModel != null) {
			// Add workloads that are not in the listModel
			for (Workload workload : manager.getWorkloads()) {
				if (!listModel.contains(workload)) {
					listModel.addElement(workload);
				}
			}

			for (int i = 0; i < listModel.size(); i++) {
				if (!manager.getWorkloads().contains(listModel.get(i))) {
					listModel.removeElementAt(i);
				}
			}
			vmList.setModel(listModel);
		}
	}

	private void setupWorkloads() {
		int delay = 0;
		int period = 5;
		AtomicInteger workloadIteration = new AtomicInteger(0);
		TimerTask updateWorkloadsTask = new TimerTask() {
			@Override
			public void run() {
				workloadIteration.incrementAndGet();

				guestManager.getWorkloadsFromGuests(manager);

				checkListModelDuplicates(manager, initDone);

			}
		};
		es.scheduleAtFixedRate(updateWorkloadsTask, delay, period, TimeUnit.SECONDS);
		try {
			Thread.sleep(delay + period);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setupEnergiDataLoop() {
		int energiPeriod = 10;
		AtomicInteger energiIteration = new AtomicInteger(0);
		TimerTask energiDataTask = new TimerTask() {
			@Override
			public void run() {
				if (initDone) {
					energiIteration.incrementAndGet();
					initializeEnergiDataService();
				}
			}
		};
		es.scheduleAtFixedRate(energiDataTask, 0, energiPeriod, TimeUnit.SECONDS);
		try {
			Thread.sleep(energiPeriod);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initialize EnergiDataService
	 */
	private void initializeEnergiDataService() {
		int days = 10;
		double thresholdPercentage = 100;
		// Checks if the expected number is a number
		thresholdPercentage = Double.parseDouble(migrationThresholdPercentTextField.getText());

		edf = new EnergiDataFetcher(days, thresholdPercentage);
		Record currentRecord = edf.getCurrentRecord();
		System.out.println("Current Price = " + currentRecord.getSpotPriceDKK());
		System.out.println("Current Time = " + currentRecord.getHourDK());

		OperationStage operation = edf.getOperationStage();
		migrationStatusLabel.setText(operation.name());

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVmManager = new JFrame();
		frmVmManager.setPreferredSize(new Dimension(675, 300));
		frmVmManager.setVisible(true);
		frmVmManager.setSize(new Dimension(675, 300));
		frmVmManager.setTitle("Workload Manager");
		frmVmManager.setBounds(100, 100, 675, 300);
		frmVmManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		vmAutoMigrationSwitch = new JCheckBox();

		vmListPanel = new JPanel();
		frmVmManager.getContentPane().add(vmListPanel, BorderLayout.WEST);
		GridBagLayout gbl_vmListPanel = new GridBagLayout();
		gbl_vmListPanel.columnWidths = new int[] { 56, 0 };
		gbl_vmListPanel.rowHeights = new int[] { 0, 0 };
		gbl_vmListPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_vmListPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		vmListPanel.setLayout(gbl_vmListPanel);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Workload List", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		vmListPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 56, 58, 0 };
		gbl_panel.rowHeights = new int[] { 188, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		listModel = new DefaultListModel<>();
		for (Workload workload : manager.getWorkloads()) {
			listModel.addElement(workload);

		}

		vmList = new JList(listModel);
		vmList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vmList.setSelectedIndex(0);
		vmList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (vmList.getSelectedIndex() != -1) {
						Workload object = (Workload) vmList.getSelectedValue();
						if (object instanceof Workload) {
							selectedWorkload = object;
							vmNameResult.setText(object.getWl_name());
							vmIPResult.setText(object.getWl_ip());
							vmStatusResult.setText(String.valueOf(object.isWl_status()));
							vmAutoMigrationSwitch.setSelected(object.isWl_autoMigration());
						}
					}
				}
			}

		});

		addWorkloadButton = new JButton("Add workload");
		addWorkloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane workloadOptionPane = new JOptionPane("Add new workload with JSON: ");
				JTextArea workloadJsonTextArea = new JTextArea();
				workloadJsonTextArea.setLineWrap(true);
				workloadJsonTextArea.setColumns(15);
				workloadJsonTextArea.setRows(10);
				JScrollPane workloadScrollPane = new JScrollPane(workloadJsonTextArea);
				JOptionPane.showMessageDialog(null, workloadScrollPane, "New Workload", JOptionPane.DEFAULT_OPTION);
				if (JOptionPane.YES_OPTION == 0) {
					String newWorkloadString = workloadJsonTextArea.getText();

					JOptionPane guestOptionPane = new JOptionPane("Add workload to Guest");
					JComboBox guestMigrationComboBox = new JComboBox();
					DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel<Object>(
							guestManager.getGuestList().toArray());
					guestMigrationComboBox.setModel(comboBoxModel);
					JOptionPane.showMessageDialog(null, guestMigrationComboBox, "Pick Guest",
							JOptionPane.DEFAULT_OPTION);
					if (JOptionPane.YES_OPTION == 0) {
						manager.HTTPMigrate((Guest) guestMigrationComboBox.getSelectedItem(), newWorkloadString,
								"/workloads");
					} else {
						System.out.println("Migration pane closed!");
					}
				} else {
					System.out.println("New workload optionpane closed!");
				}
			}

		});

		GridBagConstraints gbc_addWorkloadButton = new GridBagConstraints();
		gbc_addWorkloadButton.gridwidth = 2;
		gbc_addWorkloadButton.gridx = 0;
		gbc_addWorkloadButton.gridy = 1;
		panel.add(addWorkloadButton, gbc_addWorkloadButton);

		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridwidth = 2;
		gbc_textArea.insets = new Insets(0, 0, 0, 5);
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 0;
		panel.add(vmList, gbc_textArea);

		vmMigrationThresholdPanel = new JPanel();
		frmVmManager.getContentPane().add(vmMigrationThresholdPanel, BorderLayout.EAST);
		GridBagLayout gbl_vmMigrationThresholdPanel = new GridBagLayout();
		gbl_vmMigrationThresholdPanel.columnWidths = new int[] { 88, 46 };
		gbl_vmMigrationThresholdPanel.rowHeights = new int[] { 104 };
		gbl_vmMigrationThresholdPanel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_vmMigrationThresholdPanel.rowWeights = new double[] { 1.0 };
		vmMigrationThresholdPanel.setLayout(gbl_vmMigrationThresholdPanel);

		panel_2 = new JPanel();
		panel_2.setBorder(
				new TitledBorder(null, "Workload Settings", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.anchor = GridBagConstraints.WEST;
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.VERTICAL;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		vmMigrationThresholdPanel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 116, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		lblNewLabel_1 = new JLabel("Workload Auto Migration:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		GridBagConstraints gbc_vmAutoMigrationSwitch = new GridBagConstraints();
		gbc_vmAutoMigrationSwitch.insets = new Insets(0, 0, 5, 0);
		gbc_vmAutoMigrationSwitch.gridx = 0;
		gbc_vmAutoMigrationSwitch.gridy = 1;
		panel_2.add(vmAutoMigrationSwitch, gbc_vmAutoMigrationSwitch);

		migrationThresholdLabel = new JLabel("Threshold:");
		GridBagConstraints gbc_migrationThresholdLabel = new GridBagConstraints();
		gbc_migrationThresholdLabel.insets = new Insets(0, 0, 5, 0);
		gbc_migrationThresholdLabel.gridx = 0;
		gbc_migrationThresholdLabel.gridy = 2;
		panel_2.add(migrationThresholdLabel, gbc_migrationThresholdLabel);

		migrationThresholdTextField = new JTextField();
		GridBagConstraints gbc_migrationThresholdTextField = new GridBagConstraints();
		gbc_migrationThresholdTextField.insets = new Insets(0, 0, 5, 0);
		gbc_migrationThresholdTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_migrationThresholdTextField.gridx = 0;
		gbc_migrationThresholdTextField.gridy = 3;
		panel_2.add(migrationThresholdTextField, gbc_migrationThresholdTextField);
		migrationThresholdTextField.setColumns(10);

		migrationThresholdPercentLabel = new JLabel("Threshold %:");
		GridBagConstraints gbc_migrationThresholdPercentLabel = new GridBagConstraints();
		gbc_migrationThresholdPercentLabel.insets = new Insets(0, 0, 5, 0);
		gbc_migrationThresholdPercentLabel.gridx = 0;
		gbc_migrationThresholdPercentLabel.gridy = 4;
		panel_2.add(migrationThresholdPercentLabel, gbc_migrationThresholdPercentLabel);

		migrationThresholdPercentTextField = new JTextField();
		GridBagConstraints gbc_migrationThresholdPercentTextField = new GridBagConstraints();
		gbc_migrationThresholdPercentTextField.insets = new Insets(0, 0, 5, 0);
		gbc_migrationThresholdPercentTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_migrationThresholdPercentTextField.gridx = 0;
		gbc_migrationThresholdPercentTextField.gridy = 5;
		panel_2.add(migrationThresholdPercentTextField, gbc_migrationThresholdPercentTextField);
		migrationThresholdPercentTextField.setColumns(10);
		migrationThresholdPercentTextField.setText(initialThresholdPercent);
		migrationThresholdPercentTextField.setHorizontalAlignment(JTextField.CENTER);

		migrationThresholdSetButton = new JButton("Set Threshold");
		GridBagConstraints gbc_migrationThresholdSetButton = new GridBagConstraints();
		gbc_migrationThresholdSetButton.gridx = 0;
		gbc_migrationThresholdSetButton.gridy = 6;
		panel_2.add(migrationThresholdSetButton, gbc_migrationThresholdSetButton);

		vmPropertiesPanel = new JPanel();
		frmVmManager.getContentPane().add(vmPropertiesPanel, BorderLayout.CENTER);
		GridBagLayout gbl_vmPropertiesPanel = new GridBagLayout();
		gbl_vmPropertiesPanel.columnWidths = new int[] { 174, 0 };
		gbl_vmPropertiesPanel.rowHeights = new int[] { 0, 0 };
		gbl_vmPropertiesPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_vmPropertiesPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		vmPropertiesPanel.setLayout(gbl_vmPropertiesPanel);

		panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(null, "Workload Properties", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		vmPropertiesPanel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 51, 52, 33, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		vmNameLabel = new JLabel("Workload Name:");
		GridBagConstraints gbc_vmNameLabel = new GridBagConstraints();
		gbc_vmNameLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_vmNameLabel.gridx = 0;
		gbc_vmNameLabel.gridy = 0;
		panel_1.add(vmNameLabel, gbc_vmNameLabel);

		vmNameResult = new JLabel("");
		GridBagConstraints gbc_vmNameResult = new GridBagConstraints();
		gbc_vmNameResult.insets = new Insets(0, 0, 5, 5);
		gbc_vmNameResult.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmNameResult.gridx = 1;
		gbc_vmNameResult.gridy = 0;
		panel_1.add(vmNameResult, gbc_vmNameResult);

		vmIPLabel = new JLabel("IP:");
		GridBagConstraints gbc_vmIPLabel = new GridBagConstraints();
		gbc_vmIPLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmIPLabel.insets = new Insets(0, 0, 5, 5);
		gbc_vmIPLabel.gridx = 0;
		gbc_vmIPLabel.gridy = 1;
		panel_1.add(vmIPLabel, gbc_vmIPLabel);

		vmIPResult = new JLabel("");
		GridBagConstraints gbc_vmIPResult = new GridBagConstraints();
		gbc_vmIPResult.insets = new Insets(0, 0, 5, 5);
		gbc_vmIPResult.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmIPResult.gridx = 1;
		gbc_vmIPResult.gridy = 1;
		panel_1.add(vmIPResult, gbc_vmIPResult);

		vmStatusLabel = new JLabel("Status:");
		GridBagConstraints gbc_vmStatusLabel = new GridBagConstraints();
		gbc_vmStatusLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmStatusLabel.insets = new Insets(0, 0, 5, 5);
		gbc_vmStatusLabel.gridx = 0;
		gbc_vmStatusLabel.gridy = 2;
		panel_1.add(vmStatusLabel, gbc_vmStatusLabel);

		vmStatusResult = new JLabel("");
		GridBagConstraints gbc_vmStatusResult = new GridBagConstraints();
		gbc_vmStatusResult.insets = new Insets(0, 0, 5, 5);
		gbc_vmStatusResult.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmStatusResult.gridx = 1;
		gbc_vmStatusResult.gridy = 2;
		panel_1.add(vmStatusResult, gbc_vmStatusResult);

		vmMigrationLabel = new JLabel("Migrate To:");
		GridBagConstraints gbc_vmMigrationLabel = new GridBagConstraints();
		gbc_vmMigrationLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmMigrationLabel.insets = new Insets(0, 0, 5, 5);
		gbc_vmMigrationLabel.gridx = 0;
		gbc_vmMigrationLabel.gridy = 3;
		panel_1.add(vmMigrationLabel, gbc_vmMigrationLabel);

		availableVMsComboBox = new JComboBox<>();
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel<Object>(guestManager.getGuestList().toArray());
		availableVMsComboBox.setModel(comboBoxModel);

		GridBagConstraints gbc_availableVMsComboBox = new GridBagConstraints();
		gbc_availableVMsComboBox.fill = GridBagConstraints.BOTH;
		gbc_availableVMsComboBox.gridwidth = 2;
		gbc_availableVMsComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_availableVMsComboBox.gridx = 1;
		gbc_availableVMsComboBox.gridy = 3;
		panel_1.add(availableVMsComboBox, gbc_availableVMsComboBox);
		availableVMsComboBox.setMaximumRowCount(5);

		vmMigrationBtn = new JButton("Migrate Workload");
		vmMigrationBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Guest guest = (Guest) comboBoxModel.getSelectedItem();
				for (Guest g : guestManager.getGuestList()) {
					if (guest.getIp() == g.getIp()) {
						if (selectedWorkload != null && g.isOnline()) {
							System.out.println("Libvirt: " + g.getLibvirtURI());
							manager.migrateWorkload(selectedWorkload, g, guestManager);
							listModel.removeElement(vmList.getSelectedValue());
							vmList.setModel(listModel);
						} else {
							System.out.println("Workload is not selected or guest is not online!");
						}
					}
				}

			}
		});
		GridBagConstraints gbc_vmMigrationBtn = new GridBagConstraints();
		gbc_vmMigrationBtn.gridwidth = 3;
		gbc_vmMigrationBtn.insets = new Insets(0, 0, 0, 0);
		gbc_vmMigrationBtn.fill = GridBagConstraints.HORIZONTAL;
		gbc_vmMigrationBtn.gridx = 0;
		gbc_vmMigrationBtn.gridy = 4;
		panel_1.add(vmMigrationBtn, gbc_vmMigrationBtn);

		lblNewLabel = new JLabel("Migration status:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(10, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		gbc_lblNewLabel.weighty = 0;
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		vmPropertiesPanel.add(lblNewLabel, gbc_lblNewLabel);

		migrationStatusLabel = new JLabel("Ready");
		GridBagConstraints gbc_migrationStatusLabel = new GridBagConstraints();
		gbc_migrationStatusLabel.insets = new Insets(0, 0, 5, 0);
		gbc_migrationStatusLabel.gridx = 0;
		gbc_migrationStatusLabel.gridy = 2;
		gbc_migrationStatusLabel.weighty = 1;
		gbc_migrationStatusLabel.anchor = GridBagConstraints.NORTH;
		vmPropertiesPanel.add(migrationStatusLabel, gbc_migrationStatusLabel);

		initDone = true;
	}

	public void setToggleState(int state) {
		vmAutoMigrationSwitch.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				int currentState = e.getStateChange();
				if (currentState == e.SELECTED) {
					currentState = state;
				} else {
					System.out.println("eeeehh");
					currentState = state;
				}
			}

		});
	}
}

/*
 * 
 * public App() { manager = new VMManager(); generator = new
 * ComponentGenerator(); try { manager.addJSONWorkloadToList(); } catch
 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
 * addComponenets(generator); String[] test =
 * generator.fillVMList(manager.getWorkloadList()); }
 * 
 */