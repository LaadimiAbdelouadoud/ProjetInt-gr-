import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageAccueil extends JFrame{
    private JButton creezUnCompteButton;
    private JButton connectezVousButton;
    private JPanel JPAccueil;

    public PageAccueil() {

        setTitle("Accueil");
        setContentPane(JPAccueil);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        creezUnCompteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageCreationCompte();
                dispose();
            }
        });
        connectezVousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageAuthentification();
                dispose();
            }
        });
    }
}
