import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MonthDaysExample {
    public static void main(String[] args) {
        // Example date range
        String startDateStr = "2023-01-07";
        String endDateStr = "2024-01-11";

        int[] daysInMonths = NbreJoursDeChaqueMois(startDateStr, endDateStr);

        // Print the result
        System.out.println("Number of days in each month:");
        for (int i = 0; i < daysInMonths.length; i++) {
            System.out.println(daysInMonths[i]);
        }
    }

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

    public static String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
}
