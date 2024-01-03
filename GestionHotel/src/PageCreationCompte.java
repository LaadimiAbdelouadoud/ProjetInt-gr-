import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageCreationCompte extends JFrame{
    private JTextField tfNom;
    private JTextField tfPrenom;
    private JTextField tfEmail;
    private JPasswordField pfMotdepasse;
    private JPasswordField pfConfirmerMotdepasse;
    private JRadioButton rbTypeClient;
    private JRadioButton rbTypeEmpoye;
    private JButton btnCreerCompte;
    private JButton btnReinitialiser;
    private JLabel lbMessageMail;
    private JPanel jpCreerCompte;
    private JLabel lbMessageMotdepasse;
    private JLabel lbMessage;
    private JButton btnRetour;


    public PageCreationCompte() {

        setTitle("Se connecter");
        setContentPane(jpCreerCompte);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rbTypeEmpoye);
        buttonGroup.add(rbTypeClient);


        btnCreerCompte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReinitialiserMessages();
                if(ToutChampsRemplis()){
                    if( Utilisateur.EmailEstUnique(tfEmail.getText().trim()) ){
                        String password = new String(pfMotdepasse.getPassword()).trim();
                        String confirmPassword = new String(pfConfirmerMotdepasse.getPassword()).trim();
                        String Type = "";

                        if(password.equals(confirmPassword)){
                            if(rbTypeEmpoye.isSelected()){
                                Type = "Employe";
                                Utilisateur user = new Utilisateur(tfNom.getText().trim(), tfPrenom.getText().trim(), tfEmail.getText().trim(), password, Type );
                                user.CreationCompte();
                            }
                            if(rbTypeClient.isSelected()){
                                Type = "Client";
                                Client user = new Client(tfNom.getText().trim(), tfPrenom.getText().trim(), tfEmail.getText().trim(), password, Type );
                                user.CreationCompte();
                            }
                            lbMessage.setForeground(Color.GREEN);
                            lbMessage.setText("Compte cree avec succes!");
                        }
                        else{
                            lbMessageMotdepasse.setForeground(Color.RED);
                            lbMessageMotdepasse.setText("Mots de passes non identiques");
                        }

                    }
                    else{lbMessageMail.setForeground(Color.RED);
                        lbMessageMail.setText("Email deja Utilise");}
                }
                else{
                    lbMessage.setForeground(Color.RED);
                    lbMessage.setText("Un ou plusieurs champs ne sont pas remplis");
                }

            }
        });
        btnReinitialiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                tfNom.setText("");
                tfPrenom.setText("");
                tfEmail.setText("");
                pfMotdepasse.setText("");
                pfConfirmerMotdepasse.setText("");
                lbMessage.setText("");
                buttonGroup.clearSelection();
                ReinitialiserMessages();
            }
        });
        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PageAccueil Acceuil = new PageAccueil();
                dispose();
            }
        });
    }

    private void ReinitialiserMessages(){
        lbMessageMail.setText("");
        lbMessageMotdepasse.setText("");
        lbMessage.setText("");
    }


    private boolean ToutChampsRemplis() {
        return !tfNom.getText().trim().isEmpty() &&
                !tfPrenom.getText().trim().isEmpty() &&
                !tfEmail.getText().trim().isEmpty() &&
                pfMotdepasse.getPassword().length > 0 &&
                pfConfirmerMotdepasse.getPassword().length > 0 &&
                (rbTypeEmpoye.isSelected() || rbTypeClient.isSelected());
    }
}
