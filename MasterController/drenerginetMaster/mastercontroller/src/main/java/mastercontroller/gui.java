import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class gui {



JPanel pnFrame;
JLabel lbVmName;
JLabel lbVmProperties;
JLabel lbVmIP;
JLabel lbVmStatus;
JLabel lbVmMigrationLabel;
JComboBox cmbAvailableVMList;
JButton btConfirmMigration;
JList lsVmList;

pnFrame = new JPanel();
pnFrame.setBorder( BorderFactory.createTitledBorder( "VM Manager" ) );
GridBagLayout gbFrame = new GridBagLayout();
GridBagConstraints gbcFrame = new GridBagConstraints();
pnFrame.setLayout( gbFrame );

lbVmName = new JLabel( "VM Name:"  );
gbcFrame.gridx = 1;
gbcFrame.gridy = 1;
gbcFrame.gridwidth = 2;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 1;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( lbVmName, gbcFrame );
pnFrame.add( lbVmName );

lbVmProperties = new JLabel( "VM Properties"  );
gbcFrame.gridx = 1;
gbcFrame.gridy = 0;
gbcFrame.gridwidth = 2;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 1;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( lbVmProperties, gbcFrame );
pnFrame.add( lbVmProperties );

lbVmIP = new JLabel( "VM IP:"  );
gbcFrame.gridx = 1;
gbcFrame.gridy = 2;
gbcFrame.gridwidth = 2;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 1;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( lbVmIP, gbcFrame );
pnFrame.add( lbVmIP );

lbVmStatus = new JLabel( "VM Status"  );
gbcFrame.gridx = 1;
gbcFrame.gridy = 3;
gbcFrame.gridwidth = 2;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 1;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( lbVmStatus, gbcFrame );
pnFrame.add( lbVmStatus );

lbVmMigrationLabel = new JLabel( "Migration To:"  );
gbcFrame.gridx = 1;
gbcFrame.gridy = 4;
gbcFrame.gridwidth = 1;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 1;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( lbVmMigrationLabel, gbcFrame );
pnFrame.add( lbVmMigrationLabel );

String []dataAvailableVMList = { "Chocolate", "Ice Cream", "Apple Pie" };
cmbAvailableVMList = new JComboBox( dataAvailableVMList );
gbcFrame.gridx = 2;
gbcFrame.gridy = 4;
gbcFrame.gridwidth = 1;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 0;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( cmbAvailableVMList, gbcFrame );
pnFrame.add( cmbAvailableVMList );

btConfirmMigration = new JButton( "Migrate"  );
gbcFrame.gridx = 1;
gbcFrame.gridy = 5;
gbcFrame.gridwidth = 2;
gbcFrame.gridheight = 1;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 0;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( btConfirmMigration, gbcFrame );
pnFrame.add( btConfirmMigration );

String []dataVmList = { "Chocolate", "Ice Cream", "Apple Pie" };
lsVmList = new JList( dataVmList );
JScrollPane scpVmList = new JScrollPane( lsVmList );
gbcFrame.gridx = 0;
gbcFrame.gridy = 0;
gbcFrame.gridwidth = 1;
gbcFrame.gridheight = 6;
gbcFrame.fill = GridBagConstraints.BOTH;
gbcFrame.weightx = 1;
gbcFrame.weighty = 1;
gbcFrame.anchor = GridBagConstraints.NORTH;
gbFrame.setConstraints( scpVmList, gbcFrame );
pnFrame.add( scpVmList );
}
