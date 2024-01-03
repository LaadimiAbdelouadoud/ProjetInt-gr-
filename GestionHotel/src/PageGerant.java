import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageGerant extends JFrame{
    private JButton BtnChambres;
    private JButton btnReservations;
    private JPanel JPEspaceGerant;

    public PageGerant() {

        setTitle("Espace Gerant");
        setContentPane(JPEspaceGerant);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        BtnChambres.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageChambres();
                dispose();
            }
        });
        btnReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageVoirReservations();
                dispose();
            }
        });
    }
}
