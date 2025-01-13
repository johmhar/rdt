import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class RDTLayer2_0  extends VirtuaaliSoketti {

    public RDTLayer2_0() throws SocketException {
        super();
    }

    public RDTLayer2_0(int portti) throws SocketException{
        super(portti);
    }

    public boolean isCRC8Valid(byte[] bytedata, int length) {
        byte result = Crc8.calculateCRC8(bytedata, length, true);
        return result == 0;
    }

        public void receive(DatagramPacket paketti) throws IOException {
        while(true) {
            super.receive(paketti);
            byte[] bytedata = paketti.getData();
            int length = paketti.getLength();
            boolean result = isCRC8Valid(bytedata, length);

            // Send ACK/NACK
            String resp = result ? "ACK" : "NAK";
            byte[] respdata = resp.getBytes();
            byte crc8 = Crc8.calculateCRC8(respdata, respdata.length, false);

            System.out.println("*** RDT layer: " + resp);

            // Append crc8 byte to the end of the byte array (utilize ByteArrayOutputStream)
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(respdata);
            output.write(crc8);
            byte[] out = output.toByteArray();

            DatagramPacket resppaketti = new DatagramPacket(out, out.length, paketti.getAddress(), paketti.getPort());
            super.send(resppaketti);

            if (result) {
                // Forward packet to above
                return;
            }
        }
    }
    
}
