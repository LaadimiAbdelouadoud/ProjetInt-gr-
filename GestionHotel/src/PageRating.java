import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PageRating extends JFrame{
    private JTable tableRating;
    private JButton btnNoter;
    private JComboBox cbRating;
    private JButton btnRetour;
    private JLabel lbMessage;
    private JPanel JPRating;
    int ID;

    public PageRating(int ID) {

        this.ID = ID;

        setTitle("Rating");
        setContentPane(JPRating);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        createTable();
        setVisible(true);

        List<String[]> reservations = Reservation.ReservationsTermineeUnrated_IdClient(ID);
        RemplirTable(reservations);

        btnNoter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbMessage.setText("");
                int selectedRow = tableRating.getSelectedRow();

                if (selectedRow != -1){
                    int NumReservation = Integer.parseInt(tableRating.getValueAt(selectedRow, 0).toString());
                    int NumChambre = Integer.parseInt(tableRating.getValueAt(selectedRow, 1).toString());
                    int note = Integer.parseInt(cbRating.getSelectedItem().toString());

                    Chambre.AjouterRatingEtMettreAJour(NumChambre,note);
                    Reservation.UpdateStatusToRated(NumReservation);

                    List<String[]> reservations = Reservation.ReservationsTermineeUnrated_IdClient(ID);
                    RemplirTable(reservations);

                    lbMessage.setForeground(Color.GREEN);
                    lbMessage.setText("Votre Note("+note+") a été enregistrée. Merci pour votre FeedBack!");

                }else {
                    lbMessage.setForeground(Color.RED);
                    lbMessage.setText("Veuillez sélectionner une réservation à supprimer.");
                }

            }
        });
        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageClient(ID);
                dispose();

            }
        });
    }

    private void createTable() {
        tableRating.setModel(new DefaultTableModel(null, new String[]{"NumReservation","NumeroChambre", "DateReservation", "DateDebutSejour",
                "DateFinSejour", "Prix"}));
    }

    private void RemplirTable(List<String[]> reservations) {
        DefaultTableModel model = (DefaultTableModel) tableRating.getModel();
        model.setRowCount(0); // suppression de tout les lignes

        for (String[] reservation : reservations) {
            // extraction des donnees desirables
            String[] rowData = {
                    reservation[0], // "NumReservation"
                    reservation[2], // "NumeroChambre"
                    reservation[3], // "DateReservation"
                    reservation[4], // "DateDebutSejour"
                    reservation[5], // "DateFinSejour"
                    reservation[7], // "Prix"
            };
            model.addRow(rowData);
        }


    }
}
