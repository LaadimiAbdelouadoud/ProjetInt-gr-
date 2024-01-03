import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PageAuthentification extends JFrame {
    private JLabel JlbTitre;
    private JTextField tfEmail;
    private JPasswordField pfMotdepasse;
    private JButton btnValider;
    private JButton btnReinitialiser;
    private JLabel lbMessage;
    private JPanel jpSeConnecter;
    private JButton btnRetour;
    static int ID;
    static String Type;

    public PageAuthentification() {
        setTitle("Se connecter");
        setContentPane(jpSeConnecter);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);


        btnValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SeConnecter();
            }
        });
        btnReinitialiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfEmail.setText("");
                pfMotdepasse.setText("");
                lbMessage.setText("");
            }
        });
        btnRetour.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new PageAccueil();
                    dispose();
                }
        });
    }

    private void SeConnecter() {
        String email = tfEmail.getText().trim();
        String password = new String(pfMotdepasse.getPassword()).trim();

        if (VerificationDonnees(email, password)) {
            lbMessage.setForeground(Color.GREEN);
            lbMessage.setText("Connection avec succes");
            if(Type.equals("Client")){
                new PageClient(ID);
                dispose();
            }else {
                new PageGerant();
                dispose();
            }
            //JOptionPane.showMessageDialog(this, "Connexion réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE)
        } else {
            lbMessage.setForeground(Color.RED);
            lbMessage.setText("Mail ou motdepasse incorrect");
            //JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean VerificationDonnees(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("utilisateurs.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] userData = currentLine.split(",");
                String storedEmail = userData[3].trim(); // Le mail se trouve dans l'index 3
                String storedPassword = userData[4].trim(); // Le mot de passe de trouves dans l'index 4

                if (email.equals(storedEmail) && password.equals(storedPassword)) {
                    ID = Integer.parseInt(userData[0].trim());
                    Type = userData[5].trim();
                    return true; // vrai si les donnees sont justes
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // faux si les donnees sont fausses
    }
}
