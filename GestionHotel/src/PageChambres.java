import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PageChambres extends JFrame{
    private JTable tableChambres;
    private JButton btnAjouter;
    private JButton btnSupprimer;
    private JButton btnRetour;
    private JPanel JPChambres;
    private JLabel lbMessage;


    public PageChambres() {

        setTitle("Espace Chambres");
        setContentPane(JPChambres);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        createTable();
        setVisible(true);

        List<String[]> Chambres = Chambre.ToutChambres();
        RemplirTable(Chambres);



        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageGerant();
                dispose();
            }
        });
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lbMessage.setText("");
                int selectedRow = tableChambres.getSelectedRow();

                if (selectedRow != -1) { // verifier si la ligne selectionnee est non nulle
                    // recupererer "NumReservation" depuis la ligne selectionnee
                    int numChambreToDelete = Integer.parseInt(tableChambres.getValueAt(selectedRow, 0).toString());


                    // appeler SupprimerReservation_NumReservation
                    Reservation.AnnulerReservation_NumChambre(numChambreToDelete);
                    Chambre.SupprimerChambre_Numero(numChambreToDelete);

                    // mettre a jour la table
                    List<String[]> updatedChambres = Chambre.ToutChambres();
                    RemplirTable(updatedChambres);

                    lbMessage.setForeground(Color.GREEN);
                    lbMessage.setText("Suppression avec succes et remboursement des reservations en relation");
                } else {
                    lbMessage.setForeground(Color.RED);
                    lbMessage.setText("Veuillez sélectionner une réservation à supprimer.");
                }
            }
        });
        btnAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageCreationChambres();
                dispose();
            }
        });
    }

    private void createTable(){
        tableChambres.setModel(new DefaultTableModel(null, new String[]{"Numero","Capacite", "Qualite","ListeNotes", "Rating"}));
    }

    private void RemplirTable(List<String[]> chambres) {
        DefaultTableModel model = (DefaultTableModel) tableChambres.getModel();
        model.setRowCount(0); // suppression de tout les lignes

        for (String[] chambre : chambres) {
            // extraction des donnees desirables
            String[] rowData = {
                    chambre[0], // "NumeroChambre"
                    chambre[1], // "Capacite"
                    chambre[2], // "Qualite"
                    chambre[4], // "ListeNotes"
                    chambre[3] // "Rating"
            };
            model.addRow(rowData);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PageChambres();
        });
    }
}
