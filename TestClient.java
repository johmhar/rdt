import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;


public class TestClient {
    private static RDTLayer3_0 soketti = null;
    private static int port = 6663;

    public static void main(String[] args) {
        if (args.length > 0) {
            // Port number given
            port = Integer.parseInt(args[0]);
        }
        try {
            soketti = new RDTLayer3_0();

            Scanner input = new Scanner(System.in);

            while (true) {
              String in = input.nextLine();
              if (in.equals("QUIT")) {
                soketti.close();
                input.close();
                System.exit(1);
              }
              soketti.rdtSend(in, port);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

