import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateSysteme {
    static Date DateProgramme;

    // Method pour initialiser la programDate a la date actuelle
    DateSysteme(){
    }

    public static void setCurrentDate() {
            DateProgramme = new Date();
    }

    // Method pour additionner des jours, mois, ou annees a programDate
    public static void modifierDate(int days, int months, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateProgramme);

        // modifier jours, mois, ou annees
        calendar.add(Calendar.DAY_OF_MONTH, days);
        calendar.add(Calendar.MONTH, months);
        calendar.add(Calendar.YEAR, years);

        // mettre a jour programDate
            DateProgramme = calendar.getTime();
    }

    // Method mettre la date sous le format de "yyyy-MM-dd"
    public static String getFormattedProgramDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(DateProgramme);
    }


}
