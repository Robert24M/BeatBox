import javax.sound.midi.*;

public class MiniMiniMusicApp {
    public static void main(String[] args) {

        MiniMiniMusicApp mini = new MiniMiniMusicApp();
        mini.play();
    }

    public void play() {

        try {
            Sequencer player = MidiSystem.getSequencer();
            player.open();

            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            ShortMessage first = new ShortMessage();
            first.setMessage(144, 1, 20, 100);
            MidiEvent noteOn = new MidiEvent(first, 1);
            track.add(noteOn);

            ShortMessage second = new ShortMessage();
            second.setMessage(128, 1, 20, 100);
            MidiEvent noteOff = new MidiEvent(second, 16);
            track.add(noteOff);

            player.setSequence(seq);

            player.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
        }

    }
}