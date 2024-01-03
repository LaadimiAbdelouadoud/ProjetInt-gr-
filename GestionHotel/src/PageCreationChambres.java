import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageCreationChambres extends JFrame{
    private JComboBox cbCapacite;
    private JButton btnRetour;
    private JButton atouterButton;
    private JPanel JPAjouterChambre;
    private JComboBox cbQualite;
    private JLabel lbMessage;

    public PageCreationChambres() {

        setTitle("Ajouter une Chambre");
        setContentPane(JPAjouterChambre);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        atouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int Capacite = Integer.parseInt(cbCapacite.getSelectedItem().toString());
                int Qualite = Integer.parseInt(cbQualite.getSelectedItem().toString());

                if(Capacite!=0 && Qualite!=0){
                    Chambre NouvelleChambre = new Chambre(Capacite, Qualite);
                    NouvelleChambre.EnregistrerChambre();

                    lbMessage.setForeground(Color.GREEN);
                    lbMessage.setText("Chambre cree avec succes");
                }else{
                    lbMessage.setForeground(Color.RED);
                    lbMessage.setText("les attributs ne doivent pas etre nulls");
                }
            }
        });

        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageChambres();
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PageCreationChambres();
        });
    }
}
