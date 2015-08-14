import java.util.ArrayList;  
import java.util.Date;  
import java.util.List;  
  
import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;  


public class Runner {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		SynthManager s = new SynthManager();
		s.start();
		Thread.sleep(1000);
		
		
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
  
        PcapIf device = alldevs.get(12); // We know we have atleast 1 device  
        System.out  
            .printf("\nChoosing '%s' on your behalf:\n",  
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
  
//                System.out.printf("Received packet at %s caplen=%-4d len=%-4d\n",  
//                    new Date(packet.getCaptureHeader().timestampInMillis()),   
//                    packet.getCaptureHeader().caplen(),  // Length actually captured  
//                    packet.getCaptureHeader().wirelen()
//                    );  
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
		//test(s);
		
	}
	
	public static void test(SynthManager s) throws InterruptedException
	{
		s.playSound(new int[] {100, 100, 100, 100});
		
		for(int i = 0; i < 2550; i++)
		{
			s.playSound(new int[] {100, 100, i, i});
			//Thread.sleep(100);
		}
		s.canFinish = true;
		s.join();
	}
	
	

}
