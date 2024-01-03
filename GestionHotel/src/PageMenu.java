import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageMenu extends JFrame{
    private JLabel lbDate;
    private JButton btnAjouterJour;
    private JButton btnSoustraireJour;
    private JButton btnAjouterMois;
    private JButton btnSoustraireMois;
    private JButton btnAjouterAn;
    private JButton btnSoustraireAn;
    private JPanel JPMenu;
    private JButton btnExemplePageClient;
    private JButton btnPageGerant;
    private JButton btnAccueil;

    public PageMenu() {

        setTitle("Acceuil Client");
        setContentPane(JPMenu);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        new DateSysteme();
        DateSysteme.setCurrentDate();
        lbDate.setText(DateSysteme.getFormattedProgramDate());
        Reservation.verifierReservationsEnCours();


        btnAjouterJour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateSysteme.modifierDate(1,0,0);
                lbDate.setText(DateSysteme.getFormattedProgramDate());
                Reservation.verifierReservationsEnCours();
            }
        });
        btnSoustraireJour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateSysteme.modifierDate(-1,0,0);
                lbDate.setText(DateSysteme.getFormattedProgramDate());
                Reservation.verifierReservationsEnCours();
            }
        });


        btnAjouterMois.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateSysteme.modifierDate(0,1,0);
                lbDate.setText(DateSysteme.getFormattedProgramDate());
                Reservation.verifierReservationsEnCours();
            }
        });
        btnSoustraireMois.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateSysteme.modifierDate(0,-1,0);
                lbDate.setText(DateSysteme.getFormattedProgramDate());
                Reservation.verifierReservationsEnCours();
            }
        });


        btnAjouterAn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateSysteme.modifierDate(0,0,1);
                lbDate.setText(DateSysteme.getFormattedProgramDate());
                Reservation.verifierReservationsEnCours();
            }
        });
        btnSoustraireAn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateSysteme.modifierDate(0,0,-1);
                lbDate.setText(DateSysteme.getFormattedProgramDate());
                Reservation.verifierReservationsEnCours();
            }
        });//verification fin sejour

        btnExemplePageClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageClient(1);
            }
        });
        btnPageGerant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageGerant();
            }
        });
        btnAccueil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageAccueil();
            }
        });
    }

}
