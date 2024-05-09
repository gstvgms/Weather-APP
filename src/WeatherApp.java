import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

// retrieve weather data from API
// data from the external API and return it
// display this data to the user
public class WeatherApp {
    // fetch weather data for given location
    public static JSObject getWeatherData(String location) {
        // fetch weather data from the API
        // return the data
        JSONArray locationData = getLocationData(location);
        return null;
    }

    // retrieves geographic coordinates for a given location
    private static JSONArray getLocationData(String location) {
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
}
