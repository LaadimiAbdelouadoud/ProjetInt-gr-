import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Chambre {
    int Numero;
    int Capacite;
    int Qualite; //1-5*
    double Rating;
    String ListeRatings; //x1-x2-....-xn avec xi entre 0 et 5



    // Constructeur
    public Chambre( int Capacite, int Qualite) {
        this.Numero = MaxNumChambre()+1;
        this.Capacite = Capacite;
        this.Qualite = Qualite;
        this.Rating = 0;
        this.ListeRatings = "0";
    }

    // Methode pour stocker la chambre dans le fichier
    public void EnregistrerChambre() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("chambres.txt", true))) {
            // Format: "Numero,Capacite,Qualite,Ratings,Status"
            String chambreData = String.format("%d,%d,%d,%.2f,%s\n", Numero, Capacite, Qualite, Rating, ListeRatings);
            writer.write(chambreData);
            System.out.println("Chambre data stored in file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> ToutChambres() {
        List<String[]> allChambreData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("chambres.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] chambreData = currentLine.split(",");
                allChambreData.add(chambreData);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return allChambreData;
    }

    //methode permettant de trouver les donnees d'une chambre grace a son numero
    public static String[] ChercherChambre_Numero(int chambreNumero) {
        try (BufferedReader reader = new BufferedReader(new FileReader("chambres.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] chambreData = currentLine.split(",");
                int currentNumero = Integer.parseInt(chambreData[0].trim());

                if (currentNumero == chambreNumero) {
                    return chambreData;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return null; // Return null if the room with the specified number is not found
    }

    // Methode por calculer le nombre Chambres
    public static int NombreChambres() {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("chambres.txt"))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static int MaxNumChambre() {
        int highestNum = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("chambres.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] userData = currentLine.split(",");
                int currentNum = Integer.parseInt(userData[0].trim());

                if (currentNum > highestNum) {
                    highestNum = currentNum;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return highestNum;
    }

    // Method to remove a Chambre with a certain Numero from the file
    public static void SupprimerChambre_Numero(int chambreNumero) {
        try {
            File inputFile = new File("chambres.txt");
            File tempFile = new File("chambres_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            // Traverse the file line by line
            while ((currentLine = reader.readLine()) != null) {
                // Split the line to retrieve the attributes of the Chambre
                String[] chambreData = currentLine.split(",");
                int currentNumero = Integer.parseInt(chambreData[0].trim());

                // Check if it's the Numero we are looking for, then remove
                if (currentNumero != chambreNumero) {
                    writer.write(currentLine + "\n");
                }
            }

            // Close readers and writers
            reader.close();
            writer.close();

            // Delete the original file
            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }

            // Rename the temporary file to the original file
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }

            System.out.println("Chambre with Numero " + chambreNumero + " has been successfully removed.");
        } catch (IOException e) {
            // Exception for I/O errors
            e.printStackTrace();
        }
    }

    //methode qui retourne des chambres filtrees en fonction de leurs qualite et capacite
    public static List<String[]> ChambresFiltrees(int qualiteMin, int qualiteMax, int capaciteMin, int capaciteMax) {
        List<String[]> filteredChambreData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("chambres.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] chambreData = currentLine.split(",");
                float qualite = Float.parseFloat(chambreData[2].trim());
                int capacite = Integer.parseInt(chambreData[1].trim());

                // Check if Chambre data satisfies the filtering criteria
                if (qualite >= qualiteMin && qualite <= qualiteMax &&
                        capacite >= capaciteMin && capacite <= capaciteMax) {
                    filteredChambreData.add(chambreData);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return filteredChambreData;
    }


    public static List<String[]> ChambresDisponibles(List<String[]> chambresData, String dateDebutSejour, String dateFinSejour) {
        List<String[]> availableChambres = new ArrayList<>();

        for (String[] chambreData : chambresData) {
            if (EstDisponible(chambreData, dateDebutSejour, dateFinSejour)) {
                availableChambres.add(chambreData);
            }
        }

        return availableChambres;
    }

    private static boolean EstDisponible(String[] chambreData, String dateDebutSejour, String dateFinSejour) {
        try {
            int chambreNumero = Integer.parseInt(chambreData[0].trim());

            // Read reservations from reservation.txt
            List<String> reservations = LireReservations();

            // Check reservations for the specific chambre
            for (String reservation : reservations) {
                String[] reservationData = reservation.split(",");
                int reservationChambreNumero = Integer.parseInt(reservationData[1].trim());
                String reservationStatus = reservationData[6].trim();

                // Verifier si la reservation appartient a notre chambre chambre
                if (reservationChambreNumero == chambreNumero && reservationStatus.equals("EnCours")) {
                    String reservationDateDebut = reservationData[4].trim();
                    String reservationDateFin = reservationData[5].trim();

                    // verifier s'il ya cheuvauchement
                    if (CheuvauchementDates(reservationDateDebut, reservationDateFin, dateDebutSejour, dateFinSejour)) {
                        System.out.println("Chambre " + chambreNumero + " is not available for the specified date range.");
                        return false; // Chambre is not available
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        System.out.println("Chambre is available for the specified date range.");
        return true; // Chambre disponible
    }

    private static boolean CheuvauchementDates(String start1, String end1, String start2, String end2) {
        return !(end1.compareTo(start2) < 0 || start1.compareTo(end2) > 0);
    }

    private static List<String> LireReservations() {
        List<String> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reservations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    //FacteurQalite
     static double FacteurQalite(int Qualite) {
        switch (Qualite) {
            case 1:
                return 0.8;
            case 2:
                return 1.0;
            case 3:
                return 1.2;
            case 4:
                return 1.5;
            case 5:
                return 2.0;
            default:
                return 1.0;
        }
    }

    // FacteurCapacite
     static double FacteurCapacite(int capacite) {
        if (capacite <= 2) {
            return 1.0;
        } else if (capacite <= 4) {
            return 1.2;
        } else {
            return 1.5;
        }
    }

    // Method pour ajouter un rating, mettre-a-jour la ListeRatings, et recalculer la moyenne du Rating
    public static void AjouterRatingEtMettreAJour(int NumChambre, int newRating) {
        // lire tout les donnees des chambres depuis le fichier
        List<String[]> allChambreData = ToutChambres();

        for (String[] chambreData : allChambreData) {
            int currentNumero = Integer.parseInt(chambreData[0].trim());

            if (currentNumero == NumChambre) {
                // ajouter le nouveau Rating a la liste des ratings
                String currentListeRatings = chambreData[4].trim();
                String updatedListeRatings = currentListeRatings.isEmpty()
                        ? Integer.toString(newRating)
                        : currentListeRatings + "-" + newRating;

                // mettre a jour ListeRatings
                chambreData[4] = updatedListeRatings;

                // calculer la moyenne des ratings
                double averageRating = CalculerMoyenneRating(updatedListeRatings);

                // mettre a jour le Rating
                chambreData[3] = Double.toString(averageRating);

                // mettre a jour la chambre en question dans le fichier
                MettreAJourChambre(chambreData);
            }
        }

        System.out.println("Rating added and updated for Chambre with Numero " + NumChambre);
    }


    // Methode pour calculer la moyenne depuis ListeRatings
    private static double CalculerMoyenneRating(String listeRatings) {
        String[] ratingsArray = listeRatings.split("-");
        int sum = 0;

        if (ratingsArray.length > 1) {
            for (String rating : ratingsArray) {
                sum += Integer.parseInt(rating);
            }

            return (double) sum / (ratingsArray.length-1);
        } else {
            return 0.0;
        }
    }


    // Method to update the chambre data in the file
    private static void MettreAJourChambre(String[] updatedChambreData) {
        try {
            File inputFile = new File("chambres.txt");
            File tempFile = new File("chambres_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            // Traverse the file line by line
            while ((currentLine = reader.readLine()) != null) {
                // Split the line to retrieve the attributes of the Chambre
                String[] chambreData = currentLine.split(",");
                int currentNumero = Integer.parseInt(chambreData[0].trim());

                // Check if it's the Numero we are looking for, then update
                if (currentNumero == Integer.parseInt(updatedChambreData[0])) {
                    writer.write(String.join(",", updatedChambreData) + "\n");
                } else {
                    writer.write(currentLine + "\n");
                }
            }

            // Close readers and writers
            reader.close();
            writer.close();

            // Delete the original file
            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }

            // Rename the temporary file to the original file
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }
        } catch (IOException e) {
            // Exception for I/O errors
            e.printStackTrace();
        }
    }

}
