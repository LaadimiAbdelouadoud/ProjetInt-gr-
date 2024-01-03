import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reservation {
    int NumReservation; //Identifiant unique de la reservation
    int IdClient; //L'ID du client qui a reserve
    int NumeroChambre; //le Numero de la chambre reservee
    String DateReservation; //La date du jour de reservation
    String DateDebutSejour; //La date du debut du sejour
    String DateFinSejour; //La date de fin du sejour
    //format de date "yyyy-MM-dd"
    String StatusReservation; //EnCours, Annulee ou Terminee
    double PrixReservation;
    int NumCartePayement;
    String StatusRating;
    static int delaisAnnulation=5;
    static double[] FluctuationsPrixMensuels ={0.8, 0.8, 0.8 ,1 ,1 ,1.5 ,2 ,2 ,1.5 ,1 ,0.8 ,1};
    static double PrixDeBaseJournalier =300;



    Reservation(int IdClient, int NumeroChambre, String DateReservation, String DateDebutSejour, String DateFinSejour, String StatusReservation, Double PrixReservation, int NumCartePayement){
        this.NumReservation = MaxNumReservation()+1;
        this.IdClient = IdClient;
        this.NumeroChambre = NumeroChambre;
        this.DateReservation = DateReservation;
        this.DateDebutSejour = DateDebutSejour;
        this.DateFinSejour = DateFinSejour;
        this.PrixReservation = PrixReservation;
        this.StatusReservation = StatusReservation;
        this.NumCartePayement = NumCartePayement;
        this.StatusRating = "Unrated";
    }

    public static int NombreReservations() {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public void CreationReservation() {
        try (BufferedWriter writerAll = new BufferedWriter(new FileWriter("reservations.txt", true))) {

            // Format: "IdClient,NumeroChambre,DateReservation,DateDebutSejour,DateFinSejour,StatusReservation,NumReservation"
            String reservationData = String.format("%d,%d,%d,%s,%s,%s,%s,%.2f,%d,%s\n",
                     NumReservation, IdClient, NumeroChambre, DateReservation, DateDebutSejour, DateFinSejour, StatusReservation,PrixReservation,NumCartePayement,StatusRating);

            // Write to the "reservation.txt" file
            writerAll.write(reservationData);
            System.out.println("Reservation cree et les informations sont stockes dans reservations.txt.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Methode qui permet d'avoir les reservations terminees et pas encore notees
    public static List<String[]> ReservationsTermineeUnrated_IdClient(int IdClient) {
        List<String[]> termineeUnratedReservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] reservationData = currentLine.split(",");
                String statusReservation = reservationData[6].trim(); // Indice 6 correspond a StatusReservation
                String statusRating = reservationData[9].trim(); // Indice 9 correspond a StatusRating
                int ID = Integer.parseInt(reservationData[1].trim()); // Indice 1 correspond a IdClient

                if (statusReservation.equals("Terminee") && statusRating.equals("Unrated") && ID==IdClient) {
                    termineeUnratedReservations.add(reservationData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return termineeUnratedReservations;
    }

    public static void UpdateStatusToRated(int numReservation) {
        try {
            File inputFile = new File("reservations.txt");
            File tempFile = new File("reservations_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // diviser la ligne pour extraire NumReservation
                String[] reservationData = currentLine.split(",");
                int currentNumReservation = Integer.parseInt(reservationData[0].trim()); // NumReservation est a l'index 0

                // Verifier si c'est le bon NumReservation
                if (currentNumReservation == numReservation) {
                    // mettre a jour StatusReservation a "Rated"
                    reservationData[9] = "Rated"; // StatusReservation est a l'index 6
                }

                writer.write(String.join(",", reservationData) + "\n");
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

            System.out.println("StatusReservation for reservation ID " + numReservation + " changed to 'Rated' in reservations.txt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int MaxNumReservation() {
        int highestNumReservation = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] ReservationData = currentLine.split(",");
                int currentNumReservation = Integer.parseInt(ReservationData[0].trim());

                if (currentNumReservation > highestNumReservation) {
                    highestNumReservation = currentNumReservation;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return highestNumReservation;
    }

    public static void AnnulerReservation_NumReservation(int NumReservation) {
        try {
            File inputFile = new File("reservations.txt");
            File tempFile = new File("reservations_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Diviser la ligne pour extraire le NumReservation
                String[] reservationData = currentLine.split(",");
                int currentID = Integer.parseInt(reservationData[0].trim()); // le NumReservation est à l'index 0

                // verifier le NumReservation
                if (currentID == NumReservation) {
                    // Modifier StatusReservation à "Annulee"
                    reservationData[6] = "Annulee"; // le StatusReservation est à l'index 6
                    //rembourser
                    CompteBancaire.ajouterMontant(Integer.parseInt(reservationData[8]),Double.parseDouble(reservationData[7]));
                }

                // Écrire la ligne modifiée dans le fichier temporaire
                writer.write(String.join(",", reservationData) + "\n");
            }

            // Fermeture des readers et writers
            reader.close();
            writer.close();

            // Supprimer le fichier
            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }

            // Renommer le fichier temporaire
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }

            System.out.println("Reservation d'ID " + NumReservation + " est annulee dans reservations.txt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //methode qui verifie si les reservations enCours le sont toujours
    public static void verifierReservationsEnCours() {
        try {
            File inputFile = new File("reservations.txt");
            File tempFile = new File("reservations_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Diviser la ligne pour extraire les données de réservation
                String[] reservationData = currentLine.split(",");

                // Vérifier si le statut est "EnCours"
                if (reservationData[6].equals("EnCours")) { // Index où se trouve le statut dans le tableau est 6
                    // Extraire la date de fin de séjour
                    String dateFinSejourString = reservationData[5]; // Indice 4 contient DateFinSejour

                    // Vérifier si la date de fin de séjour est avant la date du système
                    if (isDateBeforeSystemDate(dateFinSejourString)) {
                        // Changer le statut à "Terminee"
                        reservationData[6] = "Terminee";

                        //Incrementer les points de fidelite du Client correspondant
                        int IDClient = Integer.parseInt(reservationData[1]);
                        Client.IncrementerPointsDeFidelite(IDClient);
                    }
                }

                // Écrire la ligne modifiée dans le fichier temporaire
                writer.write(String.join(",", reservationData) + "\n");
            }

            // Fermeture des readers et writers
            reader.close();
            writer.close();

            // Supprimer le fichier d'origine
            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }

            // Renommer le fichier temporaire
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }

            System.out.println("Vérification des réservations en cours effectuée.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isDateBeforeSystemDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = dateFormat.parse(date);
            Date systemDate = DateSysteme.DateProgramme;

            return endDate.before(systemDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void AnnulerReservation_NumChambre(int NumChambre) {
        try {
            File inputFile = new File("reservations.txt");
            File tempFile = new File("reservations_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Diviser la ligne pour extraire le NumReservation
                String[] reservationData = currentLine.split(",");
                int currentNumChambre = Integer.parseInt(reservationData[2].trim()); // le currentNumChambre est à l'index 2

                // verifier le NumReservation
                if (currentNumChambre == NumChambre) {
                    // Modifier StatusReservation à "Annulee"
                    reservationData[6] = "Annulee"; // le StatusReservation est à l'index 6
                    //rembourser
                    CompteBancaire.ajouterMontant(Integer.parseInt(reservationData[8]),Double.parseDouble(reservationData[7]));
                }

                // Écrire la ligne modifiée dans le fichier temporaire
                writer.write(String.join(",", reservationData) + "\n");
            }

            // Fermeture des readers et writers
            reader.close();
            writer.close();

            // Supprimer le fichier
            if (!inputFile.delete()) {
                System.out.println("Error.");
                return;
            }

            // Renommer le fichier temporaire
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error.");
            }

            System.out.println("Reservations de numero de chambre " + NumChambre + " sont annulees dans reservations.txt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //verifier si une reservation peut etre annulee
    public static boolean PeutEtreAnnulee(String DateDebutSejour) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(DateDebutSejour);

            Calendar programDateCalendar = Calendar.getInstance();
            programDateCalendar.setTime(DateSysteme.DateProgramme);

            // Calculer la difference en days entre DateDebutSejour et programDate
            long daysDifference = (startDate.getTime() - programDateCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);

            // verifier si la difference est superieure a 5 jours
            return daysDifference >delaisAnnulation ;
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // en cas d'erreur retourner false
        }
    }

    //methode calculant le nombre de jours de l'interval de sejour correspondants a chaque mois
    public static int[] NbreJoursDeChaqueMois(String DateDebutSejour, String DateFinSejour) {
        int[] daysInMonths = new int[12];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date startDate = dateFormat.parse(DateDebutSejour);
            Date endDate = dateFormat.parse(DateFinSejour);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
                int month = calendar.get(Calendar.MONTH);
                daysInMonths[month]++;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daysInMonths;
    }



    public static void ModifierReservation(int reservationID, String newDateDebutSejour, String newDateFinSejour) {
        try {

            // Mettre a jour reservations.txt
            updateFile("reservations.txt", reservationID, newDateDebutSejour, newDateFinSejour);

            System.out.println("Reservation with ID " + reservationID + " modified in both files.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> ToutReservations() {
        List<String[]> allReservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] reservationData = currentLine.split(",");
                allReservations.add(reservationData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allReservations;
    }

    // Methode pour mettre a jour un fichier specifique
    private static void updateFile(String fileName, int reservationID, String newDateDebutSejour, String newDateFinSejour)
            throws IOException {
        File inputFile = new File(fileName);
        File tempFile = new File(fileName.replace(".txt", "_temp.txt"));

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> modifiedReservations = new ArrayList<>();

        while ((currentLine = reader.readLine()) != null) {
            // Diviser la ligne pour extraire le NumReservation
            String[] reservationData = currentLine.split(",");
            int currentID = Integer.parseInt(reservationData[0].trim()); // le NumReservation est a l'index 0

            // verifier si c'est le bon NumReservation
            if (currentID == reservationID) {
                // modifier DateDebutSejour and DateFinSejour
                reservationData[4] = newDateDebutSejour;
                reservationData[5] = newDateFinSejour;
                //concatener les cases du tableau avec "," entre elles
                modifiedReservations.add(String.join(",", reservationData));
            } else {
                modifiedReservations.add(currentLine);
            }
        }

        // ecrire les modifications dans le fichier
        for (String modifiedReservation : modifiedReservations) {
            writer.write(modifiedReservation + System.getProperty("line.separator"));
        }

        // Fermeture des readers et writers
        reader.close();
        writer.close();

        // Renomer le fichier temporaire
        tempFile.renameTo(inputFile);
    }

    public static List<String[]> RechercheReservation_IdClient(int IdClientCible) {
        List<String[]> matchingReservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] reservationData = currentLine.split(",");
                int idClient = Integer.parseInt(reservationData[1].trim()); // IDClient est a l'index 1

                if (idClient == IdClientCible) {
                    matchingReservations.add(reservationData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchingReservations;
    }

    public static List<String[]> RechercheReservation_NumReservation(int NumReservationCible) {
        List<String[]> matchingReservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                String[] reservationData = currentLine.split(",");
                int idClient = Integer.parseInt(reservationData[0].trim()); // NumReservation est a l'index 0

                if (idClient == NumReservationCible) {
                    matchingReservations.add(reservationData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchingReservations;
    }

    public static double CalculerPrix(int NumChambre,String DateDebutSejour,String DateFinSejour,int IDClient){

        String[] DonneesChambre = Chambre.ChercherChambre_Numero(NumChambre);

        double FacteurCapacite = Chambre.FacteurQalite(Integer.parseInt(DonneesChambre[1]));
        double FacteurQalite = Chambre.FacteurQalite(Integer.parseInt(DonneesChambre[2]));

        double PrixJournalier = FacteurCapacite*FacteurQalite*PrixDeBaseJournalier;

        int[] NbreJoursDeChaqueMois= NbreJoursDeChaqueMois(DateDebutSejour,DateFinSejour);


        double Tarif = 0;

        for (int i = 0; i < 12; i++) {
            Tarif +=(PrixJournalier*NbreJoursDeChaqueMois[i]*FluctuationsPrixMensuels[i]);
        }
        int PointsDeFidelite = Client.getPointsDeFidelite_ID(IDClient);

        return Tarif*(1-(PointsDeFidelite/100.0));
    }
}
