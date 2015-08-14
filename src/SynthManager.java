import java.util.LinkedList;
import java.util.Queue;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

public class SynthManager extends Thread {
	
	private Synthesizer synth;
	private MidiChannel[] channels;
	public boolean runFlag = true;
	
	int durationMillis = 1000;
	int durationNano =  durationMillis*1000;
	
	LinkedList<Note> currentNotes = new LinkedList<Note>();
	
	//Note: the & 0xFF is due to Java's bytes being signed, where ip octets are unsigned
	//This operation should turn them into unsigned bytes with the correct value
	public void playSound(int octets[])
	{
		//There are supposed to be 16 channels
		int channel = (octets[0]& 0xFF + octets[2]& 0xFF) % 16;
		
		//128 different pitches, 0-127
		int pitch = (octets[1]& 0xFF + octets[3]& 0xFF) % 128;
		
		int volume = 80;
		
		try {
			//init the synth and the channels
			Note tmp = new Note();
			tmp.pitch = pitch;
			tmp.channel = channel;
			tmp.startTime = System.nanoTime();
			currentNotes.offer(tmp);
			
			channels[channel].noteOn(pitch, volume);
						
		} catch (Exception e) {
			System.out.print("[" + octets[0] + "," + octets[1] + "," + octets[2] + "," + octets[3] + "]");
			e.printStackTrace();
		}
	}
	
	
	public void run()
	{
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		while(runFlag)
		{
			long currTime = System.nanoTime();
			while(currentNotes.peek() != null && 
					currentNotes.peek().startTime + durationNano > currTime)
			{
				Note tmp = currentNotes.poll();
				channels[tmp.channel].noteOff(tmp.pitch);
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
