import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
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
        assert locationData != null;
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
            assert conn != null;
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
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data from the API response
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = (String) convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windSpeed = (JSONArray) hourly.get("wind_speed_10m");
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
    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace with a plus sign
        locationName = locationName.replaceAll(" ", "+");

        // build API URL with location
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=pt&format=json";
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
                StringBuilder resultJson = new StringBuilder();
                InputStream stream = conn.getInputStream();
                if (stream != null) {
                    Scanner sc = new Scanner(conn.getInputStream());

                    // read and store the response
                    while (sc.hasNext()) {
                        resultJson.append(sc.nextLine());
                    }
                    // close scanner
                    sc.close();
                }
                // close connection
                conn.disconnect();

                // parse the JSON string into a JSON resultJsonObj
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API returned from the location name
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
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

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // return the index
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

    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            // clear
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}