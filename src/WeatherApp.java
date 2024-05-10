import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retrieve weather data from API
// data from the external API and return it
// display this data to the user
public class WeatherApp {
    // fetch weather data for given location
    public static JSONObject getWeatherData(String location) {
        // fetch weather data from the API
        // return the data
        JSONArray locationData = getLocationData(location);

        // extract the latitude and longitude from the location data
        JSONObject locationObj = (JSONObject) locationData.get(0);
        double latitude = (double) locationObj.get("latitude");
        double longitude = (double) locationObj.get("longitude");

        // build the API URL with the latitude and longitude
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FSao_Paulo";

        try {
            // call the API and get the response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check if the response is valid
            // 200 is the status code for a successful response
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }
            // store resulting json data

            StringBuilder resultJson = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());
            while (sc.hasNext()) {
                // read and store the response
                resultJson.append(sc.nextLine());
            }

            // close scanner
            sc.close();

            // close connection
            conn.disconnect();

            // parse the JSON string into a JSON obj
            JSONParser parser = new JSONParser();
            JSONObject resultObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data from the API response
            JSONObject hourlyData = (JSONObject) resultObj.get("hourly");

            JSONArray time = (JSONArray) hourlyData.get(0);
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourlyData.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weatherCode = (JSONArray) hourlyData.get("weather_code");
            String weatherCondition = (String) convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourlyData.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windSpeed = (JSONArray) hourlyData.get("wind_speed_10m");
            double windspeed = (double) windSpeed.get(index);

            // build the weather json data
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weatherCondition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // retrieves geographic coordinates for a given location
    public static JSONArray getLocationData(String location) {
        // replace any whitespace with a plus sign
        location = location.replaceAll(" ", "+");

        // build API URL with location
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                location + "&count=10&language=pt&format=json";

        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check if the response is valid
            // 200 is the status code for a successful response
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // store the API results
                StringBuilder response = new StringBuilder();
                Scanner sc = new Scanner(conn.getInputStream());

                // read and store the response
                while (sc.hasNext()) {
                    response.append(sc.nextLine());
                }

                // close scanner
                sc.close();

                // close connection
                conn.disconnect();

                // parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(String.valueOf(response));

                // get the list of location data the API returned from the location name
                JSONArray locationData = (JSONArray) obj.get("results");
                return locationData;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // attempt to connect to the API
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // connect to the API
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // could not connect to the API
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        if (timeList == null) {
            return 0;
        }
        // get the current time
        String currentTime = getCurrentTime();

        // iterate through the time list
        for(int i = 0; i < timeList.size(); i++) {
            // get the time at the current index
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        // get the current time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be in the same format as the API response
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        // return the current time
        return formattedDateTime;
    }

    private static String convertWeatherCode(long weathercode) {
        // convert the weather code to a weather condition
        String weatherCondition = "";
        if(weathercode == 0L) {
            weatherCondition = "Clear sky";
        } else if(weathercode <= 3L && weathercode > 0L) {
            weatherCondition = "Cloudy";
        }else if(weathercode >= 41L && weathercode <= 67L
                    || weathercode >= 80L && weathercode <= 99L) {
            //rain
            weatherCondition = "Rain";
        }else if (weathercode >= 71L && weathercode <= 77L) {
            //snow
            weatherCondition = "Snow";
    }
        return weatherCondition;
    }
}
