import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    private JTextField searchTextField;

    public WeatherAppGui() {
        // setup our gui and add a tittle

        super("Weather App");

        // configure gui to end the program when the window is closed

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of the window
        setSize(450,650);

        // center the window
        setLocationRelativeTo(null);

        // disable resizing
        setLayout(null);

        // prevent any resizing
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        // search bar
        searchTextField = new JTextField();

        // set the location and size of the search bar
        searchTextField.setBounds(15, 15, 351, 45);

        // change the font size of the search bar
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        System.out.println(searchTextField);
        // add placeholder text to the search bar
        addPlaceholder();

        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(searchTextField.getText().equals("Search for a locatio")  || searchTextField.getText().isEmpty()){
                    removePlaceholder();
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(searchTextField.getText().equals("Search for a location")){
                    System.out.println("oi");
                    removePlaceholder();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events
            }
        });



        // weather icon
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        // temperature text
        JLabel temperatureText = new JLabel("10º C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition text
        JLabel weatherConditionText = new JLabel("Cloudy");
        weatherConditionText.setBounds(0, 405, 450, 36);
        weatherConditionText.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionText.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionText);

        // humidity icon
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b><br> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed icon
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b><br> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                // validate input - remove whitespace to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // update gui

                // update weather image
                assert weatherData != null;
                String weatherCondition = (String) weatherData.get("weatherCondition");
                // depending on the condition, we will update the weather image that corresponds with the condition
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionText.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

    private boolean isUpdating = false;
    // placeholder for the search bar
    private void addPlaceholder() {
        SwingUtilities.invokeLater(() -> {
            if (searchTextField == null) {
                return;
            }
            if(searchTextField.getText().isEmpty()){
                searchTextField.setText("Search for a location");
                searchTextField.setForeground(Color.GRAY);
            }
        });
    }

    private void removePlaceholder() {
        SwingUtilities.invokeLater(() -> {
            if (!searchTextField.getText().equals("Search for a location")) {
                searchTextField.setText("");
                searchTextField.setForeground(Color.BLACK);
            }
        });
    }

    // use this method to load images in the gui
    private ImageIcon loadImage(String sourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(sourcePath));
            return new ImageIcon(image);
        }catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Failed to load image");
        return null;
    }
}

    // creating a list of favorite locations



