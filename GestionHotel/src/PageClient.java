import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PageClient extends JFrame{
    private JTable tableReservation;
    private JButton btnAnnuler;
    private JButton btnAjouter;
    private JPanel jpAcceuilClient;
    private JLabel lbBienVenue;
    private JLabel lbMessage;
    private JCheckBox cbTerminees;
    private JCheckBox cbEnCours;
    private JCheckBox cbAnnulees;
    private JButton btnNoter;
    private JLabel lbDate;
    private JButton btnRafraichir;
    private JLabel lbPtsFidelite;
    int IDClient;


    PageClient(int IDClient){
        this.IDClient = IDClient;

        new DateSysteme();
        lbDate.setText(DateSysteme.getFormattedProgramDate());

        cbEnCours.setSelected(true);
        cbAnnulees.setSelected(true);
        cbTerminees.setSelected(true);

        setTitle("Acceuil Client");
        setContentPane(jpAcceuilClient);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        createTable();
        setVisible(true);

        String Bienvenue = "Bienvenue "+Client.RechercheClient_ID(IDClient)[1]+" "+Client.RechercheClient_ID(IDClient)[2];
        lbBienVenue.setText(Bienvenue);

        String ptsFidelite = String.valueOf(Client.getPointsDeFidelite_ID(IDClient));
        lbPtsFidelite.setText(ptsFidelite);

        List<String[]> reservations = Reservation.RechercheReservation_IdClient(IDClient);
        RemplirTable(reservations);


        btnAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbMessage.setText("");
                int selectedRow = tableReservation.getSelectedRow();

                if (selectedRow != -1) { // verifier si la ligne selectionnee est non nulle
                    // recupererer "NumReservation" depuis la ligne selectionnee
                    int numReservationToDelete = Integer.parseInt(tableReservation.getValueAt(selectedRow, 0).toString());
                    double Prix = Double.parseDouble(tableReservation.getValueAt(selectedRow, 5).toString());
                    String DateDebutSejour = tableReservation.getValueAt(selectedRow, 3).toString();

                    if(Reservation.PeutEtreAnnulee(DateDebutSejour)){
                        // appeler SupprimerReservation_NumReservation
                        Reservation.AnnulerReservation_NumReservation(numReservationToDelete);

                        // mettre a jour la table
                        List<String[]> updatedReservations = Reservation.RechercheReservation_IdClient(IDClient);
                        RemplirTable(updatedReservations);
                        lbMessage.setForeground(Color.GREEN);
                        lbMessage.setText("Annulation avec succes et remboursement d'une somme de "+Prix+".");
                    }else {
                        lbMessage.setForeground(Color.RED);
                        lbMessage.setText("Delais d'annulation depasse.");
                    }

                } else {
                    lbMessage.setForeground(Color.RED);
                    lbMessage.setText("Veuillez sélectionner une réservation à supprimer.");
                }
            }
        });

        btnAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageReservation(IDClient);
                dispose();
            }
        });
        cbEnCours.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String[]> updatedReservations = Reservation.RechercheReservation_IdClient(IDClient);
                RemplirTable(updatedReservations);
            }
        });
        cbAnnulees.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String[]> updatedReservations = Reservation.RechercheReservation_IdClient(IDClient);
                RemplirTable(updatedReservations);
            }
        });
        cbTerminees.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String[]> updatedReservations = Reservation.RechercheReservation_IdClient(IDClient);
                RemplirTable(updatedReservations);
            }
        });
        btnNoter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageRating(IDClient);
                dispose();
            }
        });
        btnRafraichir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageClient(IDClient);
                dispose();
            }
        });
    }

    private void createTable() {
        tableReservation.setModel(new DefaultTableModel(null, new String[]{"NumReservation","NumeroChambre", "DateReservation", "DateDebutSejour",
                "DateFinSejour", "Prix", "StatusReservation"}));
    }

    //methode qui remplit la table avec les donnees
    private void RemplirTable(List<String[]> reservations) {
        DefaultTableModel model = (DefaultTableModel) tableReservation.getModel();
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
}
