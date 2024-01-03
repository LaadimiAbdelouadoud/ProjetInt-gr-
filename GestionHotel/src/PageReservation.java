import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PageReservation extends JFrame{
    private JSpinner spDAnnee;
    private JSpinner spDMois;
    private JSpinner spDJour;
    private JSpinner spFAnnee;
    private JSpinner spFMois;
    private JSpinner spFJour;
    private JButton btnVoirOffres;
    private JTable tableChambres;
    private JPanel jpReserver;
    private JLabel lbMessageDSejour;
    private JLabel lbMessageFSejour;
    private JLabel lbMessageCapacite;
    private JLabel lbMessageQualite;
    private JButton btnRetour;
    private JButton btnReserver;
    private JLabel lbMessage;
    private JComboBox cbCapMin;
    private JComboBox cbCapMax;
    private JComboBox cbQualMin;
    private JComboBox cbQualMax;
    int IDClient;

    public PageReservation(int IDClient) {

        this.IDClient=IDClient;

        setTitle("Se connecter");
        setContentPane(jpReserver);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        createTable();
        setVisible(true);


        btnVoirOffres.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                lbMessageDSejour.setText("");
                lbMessageFSejour.setText("");
                lbMessageCapacite.setText("");
                lbMessageQualite.setText("");

                // Convertir les valeurs de dates en strings sous format "yyyy-MM-dd"
                String dateDebutSejour = String.format("%04d-%02d-%02d",
                        (int) spDAnnee.getValue(), (int) spDMois.getValue(), (int) spDJour.getValue());
                String dateFinSejour = String.format("%04d-%02d-%02d",
                        (int) spFAnnee.getValue(), (int) spFMois.getValue(), (int) spFJour.getValue());

                LocalDate startDate = LocalDate.parse(dateDebutSejour, DateTimeFormatter.ISO_DATE);
                LocalDate endDate = LocalDate.parse(dateFinSejour, DateTimeFormatter.ISO_DATE);

                new DateSysteme();

                //convertir DateProgramme qui est de type Date en LocalDate
                LocalDate localDateProgramme = DateSysteme.DateProgramme.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                boolean isStartDateValid = DateValide((int)spDAnnee.getValue(), (int)spDMois.getValue(), (int)spDJour.getValue()) && localDateProgramme.isBefore(startDate);
                boolean isEndDateValid = DateValide((int)spFAnnee.getValue(), (int)spFMois.getValue(), (int)spFJour.getValue());
                boolean isCapaciteValid = Integer.parseInt(cbCapMin.getSelectedItem().toString()) <= Integer.parseInt(cbCapMax.getSelectedItem().toString());
                boolean isQualiteValid = Integer.parseInt(cbQualMin.getSelectedItem().toString()) <= Integer.parseInt(cbQualMax.getSelectedItem().toString());

                if (isStartDateValid && isEndDateValid && isCapaciteValid && isQualiteValid) {

                    if (startDate.isBefore(endDate)) {
                        // si la Date de debut est avant la date de depart on continue
                        List<String[]> filteredChambres = Chambre.ChambresFiltrees(
                                Integer.parseInt(cbQualMin.getSelectedItem().toString()), Integer.parseInt(cbQualMax.getSelectedItem().toString()),
                                Integer.parseInt(cbCapMin.getSelectedItem().toString()), Integer.parseInt(cbCapMax.getSelectedItem().toString())
                        );

                        // Stocker le resulta des ChambresDisponibles
                        List<String[]> availableChambres = Chambre.ChambresDisponibles(filteredChambres, dateDebutSejour, dateFinSejour);

                        // RemplirTable avec availableChambres
                        RemplirTable(availableChambres,dateDebutSejour,dateFinSejour);
                    } else {
                        // gerer le cas ou la date de fin de sejour est avant la date de debut de sejour
                        lbMessageFSejour.setForeground(Color.RED);
                        lbMessageFSejour.setText("date de fin de sejour avant ou egale a la date de debut de sejour");
                    }
                } else {
                    // gerer les erreurs de validation
                    if(!isStartDateValid){
                        lbMessageDSejour.setForeground(Color.RED);
                        lbMessageDSejour.setText("Date non valide");
                    } if (!isEndDateValid) {
                        lbMessageFSejour.setForeground(Color.RED);
                        lbMessageFSejour.setText("Date non valide");
                    } if (!isCapaciteValid) {
                        lbMessageCapacite.setForeground(Color.RED);
                        lbMessageCapacite.setText("Capacite min supperieur a capacite max");
                    } if (!isQualiteValid) {
                        lbMessageQualite.setForeground(Color.RED);
                        lbMessageQualite.setText("Qualite min supperieur a Qualite max");
                    }
                }

            }
        });
        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageClient(IDClient);
                dispose();
            }
        });

        btnReserver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableChambres.getSelectedRow();
                lbMessage.setText("");

                if (selectedRow != -1) { // verifier si la ligne selectionnee est non nulle
                    // recupererer "NumReservation" depuis la ligne selectionnee
                    int NumChambre = Integer.parseInt(tableChambres.getValueAt(selectedRow, 0).toString());
                    double PrixReservation = Double.parseDouble(tableChambres.getValueAt(selectedRow, 4).toString());

                    String dateDebutSejour = String.format("%04d-%02d-%02d",
                            (int) spDAnnee.getValue(), (int) spDMois.getValue(), (int) spDJour.getValue());
                    String dateFinSejour = String.format("%04d-%02d-%02d",
                            (int) spFAnnee.getValue(), (int) spFMois.getValue(), (int) spFJour.getValue());
                    String dateReservation = DateSysteme.getFormattedProgramDate();

                    new PagePayement(IDClient, NumChambre, dateReservation, dateDebutSejour, dateFinSejour, PrixReservation);
                    dispose();

                    lbMessage.setForeground(Color.GREEN);
                    lbMessage.setText("Reservation cree avec succes");
                }else {
                    lbMessage.setForeground(Color.RED);
                    lbMessage.setText("Veuillez sélectionner une chambre à reserver.");
                }
            }
        });
    }

    private void createTable() {
        tableChambres.setModel(new DefaultTableModel(null, new String[]{"NumeroChambre", "Capacite", "Qualite",
                "Rating","Prix"}));
    }

    //verifie si une date est valide
    public static boolean DateValide(int year, int month, int day) {
        try {
            // Construct a LocalDate object from the provided year, month, and day
            LocalDate date = LocalDate.of(year, month, day);

            // Ensure that the constructed date matches the input values
            return date.getYear() == year && date.getMonthValue() == month && date.getDayOfMonth() == day;
        } catch (IllegalArgumentException | DateTimeException e) {
            // Catch exceptions that may occur during date validation
            return false;
        }
    }

    private void RemplirTable(List<String[]> chambres ,String DateDebutSejour, String DateFinSejour) {
        DefaultTableModel model = (DefaultTableModel) tableChambres.getModel();
        model.setRowCount(0); // suppression de tout les lignes

        for (String[] chambre : chambres) {
                // extraction des donnees desirables
            String[] rowData = {
                    chambre[0], // "NumeroChambre"
                    chambre[1], // "Capacite"
                    chambre[2], // "Qualite"
                    chambre[3], // "Rating"
                    String.valueOf(Reservation.CalculerPrix(Integer.parseInt(chambre[0]),DateDebutSejour,DateFinSejour,IDClient)) //Prix
            };
            model.addRow(rowData);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PageReservation(1);
        });
    }
}
