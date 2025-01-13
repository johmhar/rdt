import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;


public class RDTLayer2_2  extends VirtuaaliSoketti {
    private byte lastSeq;

    public RDTLayer2_2() throws SocketException {
        super();
        this.lastSeq = -1;
    }

    public RDTLayer2_2(int portti) throws SocketException{
        super(portti);
        this.lastSeq = -1;
    }

    public boolean isCRC8Valid(byte[] bytedata, int length) {
        byte result = Crc8.calculateCRC8(bytedata, length, true);
        return result == 0;
    }

    public void receive(DatagramPacket paketti) throws IOException {
        byte receivedSeq = 0;
        while(true) {
            super.receive(paketti);
            byte[] bytedata = paketti.getData();
            int length = paketti.getLength();
            boolean result = isCRC8Valid(bytedata, length);
            byte respSeq = 0;

            // Check the received seq number
            receivedSeq = bytedata[0];

            System.out.print("*** RDT layer [" + length + "]: ");
            System.out.print(new String(bytedata, 0, length));
            System.out.println(" " + receivedSeq);

            if (result == true && (this.lastSeq == -1 || receivedSeq != this.lastSeq)) {
                // Valid packet, resp with same seq
                System.out.println("*** VALID! last seq: " + this.lastSeq +  " rec seq: " + receivedSeq);
                respSeq = receivedSeq;
                this.lastSeq = receivedSeq;
            }
            else {
                result = false;
                respSeq = this.lastSeq;
            }

            //
            // Send ACK with seq
            //

            String resp = "ACK";
            byte[] respdata = resp.getBytes();

            // 0. Combine array for resp
            byte[] outputdata = new byte[respdata.length + 2]; // +1 for seq and +1 for CRC8
            // 1. Add seq
            outputdata[0] = respSeq;
            // 2. Add data
            System.arraycopy(respdata, 0, outputdata, 1, respdata.length);
            // 3. Add crc8
            byte crc8 = Crc8.calculateCRC8(outputdata, outputdata.length - 1, false); // Subtract the length of CRC8 field
            outputdata[outputdata.length - 1] = crc8;

            System.out.println("*** RDT layer: resp " + resp + " seq: " + respSeq);

            // 4. Send resp for RDT
            DatagramPacket resppaketti = new DatagramPacket(outputdata, outputdata.length, paketti.getAddress(), paketti.getPort());
            super.send(resppaketti);

            if (result) {
                // Forward packet to above
                return;
            }
        }
    }
}
