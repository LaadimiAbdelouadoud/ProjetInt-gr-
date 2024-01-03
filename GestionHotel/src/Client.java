import java.io.*;

public class Client extends Utilisateur {

    private int PointsDeFidelite;

    public Client(String Nom, String Prenom, String Email, String MotDePasse, String Type) {
        super(Nom, Prenom, Email, MotDePasse, Type);
        this.PointsDeFidelite = 0;
    }

    @Override
    public void CreationCompte() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("utilisateurs.txt", true));
             BufferedWriter clientWriter = new BufferedWriter(new FileWriter("Clients.txt", true))) {

            // Format: "ID,Nom,Prenom,Email,Type"
            String DonneesUtilisateur = String.format("%d,%s,%s,%s,%s,%s\n", ID, Nom, Prenom, Email, MotDePasse, "Client");
            String DonneesClient = String.format("%d,%s,%s,%s,%s,%d\n", ID, Nom, Prenom, Email, MotDePasse, PointsDeFidelite);

            // Write to main file
            writer.write(DonneesUtilisateur);

            // If the Type is "Client", also write to Clients.txt
            if (Type.equals("Client")) {
                clientWriter.write(DonneesClient);
            }

            System.out.println("Compte client cree et informations sauvegardees.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //trouver les donnees d'un client par ID
    public static String[] RechercheClient_ID(int targetID) {
        String[] clientData = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("Clients.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                int userID = Integer.parseInt(userData[0].trim()); //l'ID se trouve a l'index 0

                if (userID == targetID) {
                    //si c'est le bon client
                    clientData = userData;
                    break;
                }
            }
        } catch (IOException e) {
            // Handle the exception (log or print an error message)
            e.printStackTrace();
        }

        return clientData;
    }

    public static void IncrementerPointsDeFidelite(int clientID) {
        try {
            File inputFile = new File("Clients.txt");
            File tempFile = new File("Clients_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] clientData = currentLine.split(",");
                int currentID = Integer.parseInt(clientData[0].trim());

                if (currentID == clientID) {
                    // Incrementer PointsDeFidelite, en etant sure qu'ils ne depassent pas 10
                    int currentPoints = Integer.parseInt(clientData[5].trim());
                    int newPoints = Math.min(currentPoints + 1, 10);
                    clientData[5] = String.valueOf(newPoints);
                }


                writer.write(String.join(",", clientData) + "\n");
            }


            reader.close();
            writer.close();


            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }


            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }

            System.out.println("PointsDeFidelite updated for client with ID " + clientID + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getPointsDeFidelite_ID(int targetID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Clients.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                int userID = Integer.parseInt(userData[0].trim()); // ID est a l'index 0

                if (userID == targetID) {

                    return Integer.parseInt(userData[5].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return -1;
    }
}
