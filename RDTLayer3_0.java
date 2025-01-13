import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class RDTLayer3_0  extends VirtuaaliSoketti {
    private byte lastSeq;
    private byte lastSentSeq;

    public RDTLayer3_0() throws SocketException {
        super();
        this.lastSeq = -1;
        this.lastSentSeq = 1;
        setSoTimeout(5000);
    }

    public RDTLayer3_0(int portti) throws SocketException{
        super(portti);
        this.lastSeq = -1;
        this.lastSentSeq = 1;
    }

    public boolean isCRC8Valid(byte[] bytedata, int length) {
        byte result = Crc8.calculateCRC8(bytedata, length, true);
        return result == 0;
    }

    public void rdtSend(String input, int port) throws IOException {
        this.lastSentSeq = (byte) (this.lastSentSeq == 1 ? 0 : 1);

        //
        // Create packet for sending
        //

        byte[] inData = input.getBytes();

        // 0. Combine array for send
        byte[] sendData = new byte[inData.length + 2]; // +1 for seq and +1 for CRC8
        // 1. Add seq
        sendData[0] = this.lastSentSeq;
        // 2. Add data
        System.arraycopy(inData, 0, sendData, 1, inData.length);
        // 3. Add crc8
        byte crc8 = Crc8.calculateCRC8(sendData, sendData.length - 1, false); // Subtract the length of CRC8 field
        sendData[sendData.length - 1] = crc8;

        // 4. Create packet
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), port);

        while(true) {

            // 5. Send below
            System.out.println("*** RDT layer: send " + input + " seq: " + this.lastSentSeq);
            super.send(sendPacket);

            //
            // Wait for ACK or timeout
            //

            try {

                System.out.println("*** Listening on port: " + super.getLocalPort());
                byte[] rec = new byte[256];
                DatagramPacket recPacket = new DatagramPacket(rec, rec.length);
                super.receive(recPacket);

                byte[] bytedata = recPacket.getData();
                int length = recPacket.getLength();
                boolean result = isCRC8Valid(bytedata, length);

                // Check the received seq number
                byte receivedSeq = bytedata[0];

                System.out.print("*** RDT layer - ACK [" + length + "]: ");
                System.out.print(new String(bytedata, 0, length));
                System.out.println(" " + receivedSeq);

                if (result == true && receivedSeq == this.lastSentSeq) {
                    // Valid packet
                    System.out.println("*** VALID! last seq: " + this.lastSentSeq +  " rec seq: " + receivedSeq);
                }
                else {
                    System.out.println("*** CORRPUT or wrong seq! last seq: " + this.lastSentSeq +  " rec seq: " + receivedSeq);
                    continue;
                }

                if (result) {
                    // Back to above
                    System.out.println("*** return... ");
                    return;
                }
            } catch (SocketTimeoutException e) {
                // Timer off, resend the packet
                System.out.println("*** TIMEOUT!");
                continue;
            }
        }
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
