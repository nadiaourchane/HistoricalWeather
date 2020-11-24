import java.io.*;
import java.text.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoricalWeather {
    static ArrayList<String> ValidCities = new ArrayList<String>(Arrays.asList("mia", "jnu", "bos"));
    static ArrayList<String> Name = new ArrayList<String>();
    static ArrayList<String> Date = new ArrayList<String>();
    static ArrayList<String> Prcp = new ArrayList<String>();
    static ArrayList<String> Snow = new ArrayList<String>();
    static ArrayList<String> Tmax = new ArrayList<String>();
    static ArrayList<String> Tmin = new ArrayList<String>();
    // LINE_LENGTH must account for extra comma in "Name" column
    static int LINE_LENGTH = 20;
    static int NAME_INDEX = 1;
    static int DATE_INDEX = 6;
    static int PRCP_INDEX = 10;
    static int SNOW_INDEX = 11;
    static int TMAX_INDEX = 14;
    static int TMIN_INDEX = 15;

    public static void main(String[] args) {
        String dirName = System.getProperty("user.dir");
        String file = dirName + "/../noaa_historical_weather_10yr.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Read each line of CSV file into appropriate Array Lists
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                Name.add(splitLine[NAME_INDEX]);
                Date.add(splitLine[DATE_INDEX]);
                Prcp.add(splitLine[PRCP_INDEX]);
                Snow.add(splitLine[SNOW_INDEX]);
                Tmax.add(splitLine[TMAX_INDEX]);
                Tmin.add(splitLine[TMIN_INDEX]);
            }

            // Depending on how many arguments are entered, run a specific program
            if (args.length < 1) {
                throw new Exception("No function name provided. Please provide 'days-of-precip' or 'max-temp-delta'.");
            }
            String functionName = args[0];
            String city = (args.length > 1) ? convertCity(args[1]) : null;

            if (!ValidCities.contains(city)) {
                throw new Exception("Invalid city provided. Please provide 'mia', 'jnu', or 'bos'.");
            }

            if (functionName.equals("days-of-precip")) {
                float days_of_precip = daysOfPrecip(city);
                System.out.print("Days of precipitation in " + city + " = " + days_of_precip);
            }
            else if (functionName.equals("max-temp-delta")) {
                int year = (args[2] == null) ? 0 : Integer.parseInt(args[2]);
                int month = (args[3] == null) ? 0 : Integer.parseInt(args[3]);
                String max_temp_delta = maxTempDelta(city, year, month);
                System.out.print(max_temp_delta);
            }
            else {
                throw new Exception("Unexpected function name entered: " + functionName);
            }
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public static float daysOfPrecip(String city) {
        float days_of_precip = 0.0f;
        int totalPrcpDays = 0;
        int totalYears = 1;

        // Save first logged date in date format so total number of years can be tracked
        Calendar firstLoggedDate = convertToCalendar(Date.get(1));
        int year = firstLoggedDate.get(Calendar.YEAR);

        int currYear = 0;
        for (int i = 1; i < Name.size(); i++) {
            // Check if we are looking at the city requested
            if (Name.get(i).toLowerCase().contains(city)) {
                // Convert date format to Calendar to get Year
                Calendar currDate = convertToCalendar(Date.get(i));
                currYear = currDate.get(Calendar.YEAR);
                
                // If the current year (in Date ArrayList) does not equal the logged date,
                // then account for a new year and reset the currYear
                if (currYear != year) {
                    totalYears++;
                    year = currYear;
                }

                // Check if Prcp has value and if it's greater than 0 count the day for
                // both Prcp and Snow.  If Prcp < 0, check if it snowed
                if (!Prcp.get(i).isEmpty() && Float.parseFloat(Prcp.get(i)) > 0)
                    totalPrcpDays++;
                else if (!Snow.get(i).isEmpty() && Float.parseFloat(Snow.get(i)) > 0)
                    totalPrcpDays++;
            }
        }

        // Calculate the average precipitation days
        days_of_precip = totalPrcpDays / totalYears;
        return days_of_precip;
    }

    public static String maxTempDelta(String city, int year, int month) {
        Date maxDeltaDate = new Date();
        float deltaMax = 0.0f;

        // Initialize currYear and currMonth in case user does not input year and/or month
        int currYear = 0;
        int currMonth = 0;

        // i starts at 1 to account for Headers/Column names in Array List
        for (int i = 1; i < Name.size(); i++) {
            // Check if we are looking at the city requested
            if (Name.get(i).toLowerCase().contains(city)) {
                // Convert date format to Calendar to get Year
                Calendar currDate = convertToCalendar(Date.get(i));
                
                // If user enters a year (doesn't equal 0), set the currYear to the current Date Year in the Array List
                if (year != 0)
                    currYear = currDate.get(Calendar.YEAR);
                // If user enters a month (doesn't equal 0), set the currMonth to the current Date Month in the Array List
                if (month != 0)
                    currMonth = currDate.get(Calendar.MONTH) + 1;
                
                // If user does not enter a year or month, year and month will equal 0 and still execute code
                if (currYear == year && currMonth == month) {
                    // Reset currDelta (max and min temperature difference) each time
                    float currDelta = 0.0f;

                    // Ensure that temperaure values are not empty and calcualte difference if they are not
                    if (!Tmax.get(i).isEmpty() && !Tmin.get(i).isEmpty())
                        currDelta = Float.parseFloat(Tmax.get(i)) - Float.parseFloat(Tmin.get(i));

                    // If the temp change is larger than the stored MAX temp change, then save it as the new max
                    if (currDelta > deltaMax) {
                        deltaMax = currDelta;

                        try { maxDeltaDate = new SimpleDateFormat("MM/dd/yyyy").parse(Date.get(i)); }
                        catch (ParseException e) { System.out.print(e.getMessage()); }
                    }
                }
            }
        }

        // Return expected values in a string
        return "Highest temperature change in " + city + " = " + String.valueOf(deltaMax) + " celcius on " + maxDeltaDate.toString();
    }

    public static Calendar convertToCalendar(String currDate) {
        Date datei = new Date();
        try { datei = new SimpleDateFormat("MM/dd/yyyy").parse(currDate); }
        catch (ParseException e) { System.out.print(e.getMessage()); }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datei);

        return calendar;
    }

    public static String convertCity(String city) {
        // If "JNU" is input city, change city variable so searching can be done using .contains()
        city = city.toLowerCase();
        if (city.equals("jnu"))
            city = "jun";

        return city;
    }
}