import java.io.*;

public class CompteBancaire {
    int NumCarte;
    String NomPorteur;
    String CodeVerification;
    double Solde;

    // Constructeur
    public CompteBancaire(String NomPorteur, String NumCarte, String CodeVerification, double Solde) {
        this.NumCarte = MaxNumCarte()+1;
        this.NomPorteur = NomPorteur;
        this.CodeVerification = CodeVerification;
        this.Solde = Solde;
    }

    public static int MaxNumCarte() {
        int highestNumCarte = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("comptesBancaires"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] CompteData = currentLine.split(",");
                int currentNumCarte = Integer.parseInt(CompteData[0].trim());

                if (currentNumCarte > highestNumCarte) {
                    highestNumCarte = currentNumCarte;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return highestNumCarte;
    }

    public void CreationCompteBancaire() {
        try (BufferedWriter writerAll = new BufferedWriter(new FileWriter("comptesBancaires.txt", true))) {

            // Format: "NomPorteur, NumCarte, DateExpiration, CodeVerification, Solde"
            String reservationData = String.format("%d,%s,%s,%.2f\n",
                    NumCarte, NomPorteur, CodeVerification, Solde);

            // ecrire dans "comptesBancaires.txt" file
            writerAll.write(reservationData);
            System.out.println("Reservation cree et les informations sont stockes dans reservations.txt.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ajouterMontant(int numCarte, double montant) {
        try {
            File inputFile = new File("comptesBancaires.txt");
            File tempFile = new File("comptesBancaires_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Split the line to extract NumCarte
                String[] compteData = currentLine.split(",");
                int currentNumCarte = Integer.parseInt(compteData[0].trim());

                // Check if it's the correct account (based on NumCarte)
                if (currentNumCarte == numCarte) {
                    // Update Solde by adding the specified amount
                    double currentSolde = Double.parseDouble(compteData[3].trim());
                    compteData[3] = String.valueOf(currentSolde + montant);
                }

                // Write the modified line to the temporary file
                writer.write(String.join(",", compteData) + "\n");
            }

            // Close readers and writers
            reader.close();
            writer.close();

            // Delete the original file
            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }

            // Rename the temporary file
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }

            System.out.println("Amount added to the account with NumCarte " + numCarte);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
