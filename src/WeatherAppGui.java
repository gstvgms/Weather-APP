import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
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
        JTextField searchBar = new JTextField();

        // set the location and size of the search bar
        searchBar.setBounds(15, 15, 351, 45);

        // change the font size of the search bar
        searchBar.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchBar);

        // search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change the cursor to a hand when hovering over the button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        add(searchButton);

        // weather icon
        JLabel weatherIcon = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherIcon.setBounds(0,125,450,217);
        add(weatherIcon);

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
        JLabel humidityIcon = new JLabel(loadImage("src/assets/humidity.png"));
        humidityIcon.setBounds(15, 500, 74, 66);
        add(humidityIcon);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b><br> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed icon
        JLabel windspeedIcon = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedIcon.setBounds(220, 500, 74, 66);
        add(windspeedIcon);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b><br> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

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
