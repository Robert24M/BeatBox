import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeatBox implements Serializable {
    public static void main(String[] args) {
        BeatBox beatBoxGUI = new BeatBox();
        for (String instrument : instruments.keySet()) {
            System.out.println(instrument + " " + instruments.get(instrument));
        }
        beatBoxGUI.buildGui();
    }

    private JFrame frame;
    private JPanel panel;
    List<JCheckBox> checkBoxList;
    private transient Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private static final Map<String, Integer> instruments;

    static {
        instruments = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("instruments.txt"))) {
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                String[] data = input.split(",");
                String name = data[0];
                int id = Integer.parseInt(data[1]);
                instruments.put(name, id);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void buildGui() {
        frame = new JFrame("Cyber BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel background = new JPanel(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxList = new ArrayList<>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(action -> buildTrackAndStart());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(action -> sequencer.stop());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(action -> {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        });
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo down");
        downTempo.addActionListener(action -> {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        });
        buttonBox.add(downTempo);
        JTextField nameToSave = new JFormattedTextField();

        JButton save = new JButton("Save");
        buttonBox.add(save);

//        Temporary implementation
//        final boolean[] firstPress = {true};
        save.addActionListener(action -> {
            String result = (String) JOptionPane.showInputDialog(
                    frame,
                    "Enter a name",
                    "Save",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""
            );
            if (result != null && result.length() > 0) {
                try (ObjectOutputStream outputStream = new ObjectOutputStream(
                        new BufferedOutputStream(Files.newOutputStream(Paths.get(result + ".ser"))))) {
                    outputStream.writeObject(this);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        instruments.keySet().forEach(instrument -> nameBox.add(new Label(instrument)));

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        panel = new JPanel(grid);
        background.add(BorderLayout.CENTER, panel);

        for (int i = 0; i < 256; i++) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(false);
            checkBoxList.add(checkBox);
            panel.add(checkBox);
        }

        setUpMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }

    private void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            System.out.println(e.getMessage());
        }
    }

    private void buildTrackAndStart() {
        int[] trackList;
        int count = 0;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (Integer value : instruments.values()) {
            trackList = new int[16];

            int key = value;

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkBoxList.get(j + 16 * count);
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            count++;
            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }

        track.add(makeEvent(192, 9, 1, 0, 15));

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (InvalidMidiDataException e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeTracks(int[] trackList) {
        for (int i = 0; i < trackList.length; i++) {
            int key = trackList[i];

            if (key != 0) {
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i + 1));
            }
        }
    }

    private MidiEvent makeEvent(int cmd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(cmd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (InvalidMidiDataException e) {
            System.out.println(e.getMessage());
        }
        return event;
    }
}
