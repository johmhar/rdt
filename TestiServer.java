import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class TestiServer {
    private static DatagramSocket soketti = null;

    public static void main(String[] args) {
        try {
            // soketti = new DatagramSocket(6666);
            // soketti = new VirtuaaliSoketti(6666);
            // soketti = new RDTLayer2_0(6666);
            // soketti = new RDTLayer2_2(6666);
            soketti = new RDTLayer3_0(6666);
            boolean listening = true;
             while(listening) {
                byte[] rec = new byte[256];
                DatagramPacket paketti = new DatagramPacket(rec, rec.length);
                soketti.receive(paketti);
                System.out.printf("--- TestiSov [" + paketti.getLength() + "]:");
                // System.out.println(new String(rec, 0, paketti.getLength() - 1));
                System.out.println(new String(rec, 0, paketti.getLength()));
                System.out.println();
             }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
