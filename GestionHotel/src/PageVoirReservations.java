import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PageVoirReservations extends JFrame{
    private JTable tableReservations;
    private JPanel JPVoirReservations;
    private JCheckBox cbEnCours;
    private JCheckBox cbAnnulees;
    private JCheckBox cbTerminees;
    private JButton btnRetour;
    private JButton btnRafraichir;

    PageVoirReservations(){

        cbEnCours.setSelected(true);
        cbAnnulees.setSelected(true);
        cbTerminees.setSelected(true);

        setTitle("Espace Chambres");
        setContentPane(JPVoirReservations);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        createTable();
        setVisible(true);

        List<String[]> reservations = Reservation.ToutReservations();
        RemplirTable(reservations);

        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageGerant();
                dispose();
            }
        });

        btnRafraichir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String[]> reservations = Reservation.ToutReservations();
                RemplirTable(reservations);
            }
        });
    }

    private void createTable() {
        tableReservations.setModel(new DefaultTableModel(null, new String[]{"NumReservation","NumeroChambre", "DateReservation", "DateDebutSejour",
                "DateFinSejour", "Prix", "StatusReservation"}));
    }

    private void RemplirTable(List<String[]> reservations) {
        DefaultTableModel model = (DefaultTableModel) tableReservations.getModel();
        model.setRowCount(0); // suppression de tout les lignes

        for (String[] reservation : reservations) {
            String Status = reservation[6];

            if(     (cbAnnulees.isSelected() && Status.equals("Annulee") )||
                    (cbEnCours.isSelected() && Status.equals("EnCours") )||
                    (cbTerminees.isSelected() && Status.equals("Terminee") )
            ) {
                // extraction des donnees desirables
                String[] rowData = {
                        reservation[0], // "NumReservation"
                        reservation[2], // "NumeroChambre"
                        reservation[3], // "DateReservation"
                        reservation[4], // "DateDebutSejour"
                        reservation[5], // "DateFinSejour"
                        reservation[7], // "Prix"
                        reservation[6]  // "StatusReservation"
                };
                model.addRow(rowData);
            }

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PageVoirReservations();
        });
    }
}
