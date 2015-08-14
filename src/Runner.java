import java.util.ArrayList;  
import java.util.Date;  
import java.util.List;
import java.util.Scanner;

import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;  


public class Runner {

	public static void main(String[] args) throws InterruptedException {
		SynthManager s = new SynthManager();
		s.start();
		
		
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
        StringBuilder errbuf = new StringBuilder(); // For any error msgs  
  
 
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
            System.err.printf("Can't read list of devices, error is %s", errbuf  
                .toString());  
            return;  
        }  
  
        System.out.println("Network devices found:");  
  
        int i = 0;  
        for (PcapIf device : alldevs) {  
            String description =  
                (device.getDescription() != null) ? device.getDescription()  
                    : "No description available";  
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);  
        }  
  
        
        System.out.print("\nPlease choose one of the above (only the number): ");
        Scanner scan = new Scanner(System.in);
        int deviceNum = scan.nextInt();
        
        
        PcapIf device = alldevs.get(deviceNum); // We know we have atleast 1 device  
        System.out  
            .printf("\nChoosing '%s':\n",  
                (device.getDescription() != null) ? device.getDescription()  
                    : device.getName());  
  
 
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 10 * 1000;           // 10 seconds in millis  
        Pcap pcap =  
            Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
  
        if (pcap == null) {  
            System.err.printf("Error while opening device for capture: "  
                + errbuf.toString());  
            return;  
        }  
  
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {  
  
            public void nextPacket(PcapPacket packet, String user) {  
  
            	try
            	{
            		byte[] sourceIp = packet.getHeader(new Ip4()).source();
            		s.playSound(new int[] {sourceIp[0], sourceIp[1], sourceIp[2], sourceIp[3]});
            	} catch (NullPointerException e)
            	{
            		
            	}
            }  
        };  
   
        pcap.loop(-1, jpacketHandler, "");  
  
        pcap.close();  
		
	}

}
