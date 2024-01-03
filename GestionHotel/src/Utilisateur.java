import java.io.*;

public class Utilisateur {
    int ID;
    String Nom;
    String Prenom;
    String Email;
    String MotDePasse;
    String Type;

    // Constructeur
    public Utilisateur(String Nom, String Prenom, String Email, String MotDePasse, String Type) {
        this.ID = MaxID()+1;
        this.Nom = Nom;
        this.Prenom = Prenom;
        this.Email = Email;
        this.MotDePasse = MotDePasse;
        this.Type = Type;
    }

    //Methode permettant de stocker les donnees d'un utilisateur dans le fichier
    public void CreationCompte() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("utilisateurs.txt", true))) {
            // Format: "ID,Nom,Prenom,Email,Type"
            String DonneesUtilisateur = String.format("%d,%s,%s,%s,%s,%s\n", ID, Nom, Prenom, Email, MotDePasse, Type);
            writer.write(DonneesUtilisateur);
            System.out.println("Utilisateur created and information stored in file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Methode qui permet de calculer le nombre d'utilisateurs
    public static int NombreUtilisateurs() {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("utilisateurs.txt"))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    //methode qui retourne le plus grand ID
    public static int MaxID() {
        int highestID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("utilisateurs.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] userData = currentLine.split(",");
                int currentID = Integer.parseInt(userData[0].trim());

                if (currentID > highestID) {
                    highestID = currentID;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return highestID;
    }

    //Methode qui permet de supprimer un Utilisateur ayant un certaint ID du fichier
    public static void SupprimerUtilisateur_ID(int userID) {
        try {
            File inputFile = new File("utilisateurs.txt");
            File tempFile = new File("utilisateurs_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            //Parcourir le fichier ligne par ligne
            while ((currentLine = reader.readLine()) != null) {
                // Diviser la ligne pour retrouver les atttributs de L'Utilisateur
                String[] userData = currentLine.split(",");
                int currentID = Integer.parseInt(userData[0].trim());

                // Verifier si C'est l'ID qu'on cherche puis supprimer
                if (currentID != userID) {
                    writer.write(currentLine + "\n");
                }
            }

            // fermer les  readers et writers
            reader.close();
            writer.close();

            //supprimer le ficher originel
            if (!inputFile.delete()) {
                System.out.println("Erreur.");
                return;
            }

            // Renommer le ficher temporaire comme fichier originel
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Erreur.");
            }

            System.out.println("l'Utilisateur d'ID " + userID + " a bien ete suprime.");
        } catch (IOException e) {
            //exception pour les erreurs d'E/S
            e.printStackTrace();
        }
    }

    //Verifie si un Email figure deja dans le fichier
    public static boolean EmailEstUnique(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader("utilisateurs.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] userData = currentLine.split(",");
                String storedEmail = userData[3].trim(); // l'email se trouve dans l'index 3

                if (email.equals(storedEmail)) {
                    // l'Email se trouve deja, non unique
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // l'Email est unique
        return true;
    }



}
