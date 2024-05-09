import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // display the gui
                new WeatherAppGui().setVisible(true);
            }
        });
    }
}
