import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BeatBoxGUI {
    public static void main(String[] args) {

    }

    private JFrame frame;
    private JPanel panel;
    private static Map<String, Integer> instruments;

    static {
        instruments = new HashMap<>();
        instruments.put("Bass Drum", 35);
        instruments.put("Closed Hi-Hat", 42);
        instruments.put("Open Hi-Hat", 46);
        instruments.put("Acoustic Snare", 38);
        instruments.put("Crash Cymbal", 49);
        instruments.put("Hand Clap", 39);
        instruments.put("High Tom", 50);
        instruments.put("Hi Bongo", 60);
        instruments.put("Maracas", 70);
        instruments.put("Whistle", 72);
        instruments.put("Low Conga", 64);
        instruments.put("Cowbell", 56);
        instruments.put("Vibraslap", 58);
        instruments.put("Low-mid Tom", 47);
        instruments.put("High Agogo", 67);
        instruments.put("Open Hi Conga", 63);

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("instruments.txt"))) {
            for(String instrument : instruments.keySet()) {
                bufferedWriter.write(instrument + "," + instruments.get(instrument) + "\n");
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
