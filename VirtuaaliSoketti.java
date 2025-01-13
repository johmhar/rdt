import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class VirtuaaliSoketti extends DatagramSocket {
    
    private static double p_drop = 0.0;
    private static double p_delay = 0.0;
    private static double p_err = 0.0;

    public VirtuaaliSoketti() throws SocketException {
        super();
    }
    
    public VirtuaaliSoketti(int portti) throws SocketException {
        super(portti);
        p_drop = 0.0;
        p_delay = 0.5;
        p_err = 0.0;
    }

    public void receive(DatagramPacket paketti) throws IOException {
        while(true) {
            Random randGen = new Random();
            super.receive(paketti);
            // System.out.printf("VS: pituus %d", paketti.getLength()).println();
            if (randGen.nextDouble() <= p_drop) {
                System.out.println("... VS layer: Dropped packet");
            } else {
                if (randGen.nextDouble() <= p_delay) {
                    System.out.println("... VS layer: Delaying packet");
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if(randGen.nextDouble() <= p_err) {
                    System.out.println("... VS layer: Creating error to packet");
                    int place = randGen.nextInt(8); // 0-8
                    byte[] bytedata = paketti.getData();
                    int length = paketti.getLength();
                    int index = randGen.nextInt(length-1); // 0-pituus-1
                    // System.out.printf("index %d, place %d", index, place).println();
                    // System.out.printf("bytedata[index] %02X", bytedata[index]).println();
                    // System.out.println("Bytedata[0] " + Integer.toBinaryString(bytedata[0]));
                    // System.out.println("Bytedata[1] " + Integer.toBinaryString(bytedata[1]));
                    bytedata[index] = (byte) (bytedata[index] ^ (1 << place));
                    paketti.setData(bytedata, 0, length);
                } 
                return;
            }
        }
    }
}
