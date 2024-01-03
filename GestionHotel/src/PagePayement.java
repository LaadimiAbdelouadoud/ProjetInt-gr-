import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PagePayement extends JFrame{

    private JTextField tfNomPorteur;
    private JTextField tfNumCarte;
    private JPasswordField pfCodeVerification;
    private JButton btnPayer;
    private JButton btnRetour;
    private JButton btnReinitialiser;
    private JLabel lbMessage;
    private JPanel JPPayement;
    int NumChambre;
    String dateDebutSejour;
    String dateFinSejour;
    String dateReservation;
    double PrixReservation;
    int IDClient;

    public PagePayement(int IDClient, int NumChambre, String dateReservation, String dateDebutSejour, String dateFinSejour, double PrixReservation) {

        this.IDClient=IDClient;
        this.NumChambre=NumChambre;
        this.dateReservation=dateReservation;
        this.dateDebutSejour=dateDebutSejour;
        this.dateFinSejour=dateFinSejour;
        this.PrixReservation=PrixReservation;

        setTitle("Page Payement");
        setContentPane(JPPayement);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        btnPayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String NomPorteur = tfNomPorteur.getText();
                String NumCarte = tfNumCarte.getText();
                String CodeVerification = new String(pfCodeVerification.getPassword()).trim();

                int NumCompte = CompteExiste(NomPorteur,NumCarte,CodeVerification);
                if(Payer(NumCompte,PrixReservation)){
                    Reservation reservation = new Reservation(IDClient, NumChambre, dateReservation, dateDebutSejour, dateFinSejour, "EnCours", PrixReservation, Integer.parseInt(NumCarte));
                    reservation.CreationReservation();

                    String Nom = Client.RechercheClient_ID(IDClient)[1];
                    String Prenom = Client.RechercheClient_ID(IDClient)[2];

                    generateReceipt(Nom, Prenom, dateReservation, dateDebutSejour, dateFinSejour, PrixReservation);
                }

            }
        });
        btnReinitialiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfNomPorteur.setText("");
                tfNumCarte.setText("");
                pfCodeVerification.setText("");
            }
        });
        btnRetour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PageClient(IDClient);
                dispose();
            }
        });
    }

    public boolean Payer(int numCarte, double prix) {

        // Si le compte n'est pas trouvé, informer que le paiement a échoué
        if(numCarte == -1){
            lbMessage.setForeground(Color.RED);
            lbMessage.setText("Compte non trouvé. Paiement échoué.");
            return false;
        }

        File inputFile = new File("comptesBancaires.txt");
        File tempFile = new File("comptesBancaires_temp.txt");
        boolean resultat = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Diviser la ligne pour avoir accès à ses données
                String[] data = line.split(",");

                // Vérifier si les données sont valides
                if (data.length == 4 && Integer.parseInt(data[0].trim()) == numCarte) {
                    double solde = Double.parseDouble(data[3].trim());

                    if (solde >= prix) {
                        // Payer
                        solde -= prix;

                        // Mise à jour du solde dans la ligne
                        data[3] = String.valueOf(solde);

                        // Écrire la ligne mise à jour dans le fichier temporaire
                        writer.write(String.join(",", data) + "\n");

                        // Informer que le paiement a réussi
                        lbMessage.setForeground(Color.GREEN);
                        lbMessage.setText("Payement reussi");


                        resultat = true;
                    } else {
                        // Informer que le solde n'est pas suffisant
                        lbMessage.setForeground(Color.RED);
                        lbMessage.setText("Solde insuffisant pour effectuer le paiement.");
                        resultat = false;
                    }
                }else{
                    // Écrire la ligne inchangée dans le fichier temporaire
                    writer.write(line + "\n");
                }
            }
            // Fermeture des readers et writers
            reader.close();
            writer.close();

            // Supprimer le fichier
            if (!inputFile.delete()) {
                System.out.println("Error.");
            }

            // Renommer le fichier temporaire
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }


        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    public static int CompteExiste(String NomPorteur, String NumCarte, String CodeVerification) {
        try (BufferedReader reader = new BufferedReader(new FileReader("comptesBancaires.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Diviser la ligne pour avoir acces a ses donnees
                String[] data = line.split(",");

                // Verifier si les donnees sont valables
                if (data.length == 4 &&
                        data[0].equals(NumCarte) &&
                        data[1].equals(NomPorteur) &&
                        data[2].equals(CodeVerification) )
                {
                    return Integer.parseInt(data[0]);  // Numero Du compte
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;  // Compte non valable
    }

    private void generateReceipt(String nomClient, String prenomClient, String dateReservation,
                                 String dateDebutSejour, String dateFinSejour, double prixReservation) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("receipt.pdf"));
            document.open();

            // Add content to the PDF
           /* document.add(new Paragraph("Reçu de paiement"));
            document.add(new Paragraph("--------------------"));
            document.add(new Paragraph("Nom du client: " + nomClient));
            document.add(new Paragraph("Prénom du client: " + prenomClient));
            document.add(new Paragraph("Date de réservation: " + dateReservation));
            document.add(new Paragraph("Date de début de séjour: " + dateDebutSejour));
            document.add(new Paragraph("Date de fin de séjour: " + dateFinSejour));
            document.add(new Paragraph("Montant payé: $" + prixReservation));
            document.add(new Paragraph("--------------------"));
            document.add(new Paragraph("Merci pour votre paiement !"));*/


            com.itextpdf.text.Font fontTitre = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 24, com.itextpdf.text.Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph titre = new Paragraph("Facture de réservation", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);

            // Ligne de séparation
            document.add(new Paragraph("---------------------------------------------------------------------------------------------------------------------------------"));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            com.itextpdf.text.Image image = Image.getInstance("imageRiad.jpg");
            image.scaleToFit(700, 200);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            // Détails de la facture
            com.itextpdf.text.Font fontDetails = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);

            // Créer une table avec deux colonnes
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(80);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Ajouter les détails de réservation dans la table
            table.addCell(createCell("Nom du client :", fontDetails));
            table.addCell(createCell(nomClient + " " + prenomClient, fontDetails));
            table.addCell(createCell("Date de début du séjour :", fontDetails));
            table.addCell(createCell(dateDebutSejour, fontDetails));
            table.addCell(createCell("Date de fin du séjour :", fontDetails));
            table.addCell(createCell(dateFinSejour, fontDetails));
            table.addCell(createCell("Numéro de chambre réservé :", fontDetails));
            table.addCell(createCell(String.valueOf(NumChambre), fontDetails));
            table.addCell(createCell("Montant payé en MAD :", fontDetails));
            table.addCell(createCell(String.valueOf(prixReservation), fontDetails));

            // Ajouter la table au document
            document.add(table);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);



            // Message de remerciement
            // Message de remerciement
            Paragraph thankYouParagraph = new Paragraph("Merci de nous avoir choisi!",fontTitre);
            thankYouParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYouParagraph);



            document.close();
            JOptionPane.showMessageDialog(null, "Facture téléchargée avec succès!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du téléchargement de la facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(8f);
        return cell;
    }





}
