# Reliability on top of UDP

## Virtual Socket
************************

1. Make an application for testing purposes, e.g. client sends data to server and server displays it on screen. It is enough for the application to send data only in one direction (and later acknowledgements in the other). Use the test application to test what you implement later.
   - TestiServeri.java

2. Make a virtual socket that randomly drops a packet (i.e. does not pass it to UDP socket or to the application)
   - VirtuaaliSoketti.java

3. Add to the virtual socket: randomly delays a packet, before passing it forward
   - VirtuaaliSoketti.java

4. Add to the virtual socket: randomly generates bit errors on the data of the packet, a single bit error is enough. See e.g. Bitin asettaminen Javalla.pptx or Bitin asettaminen Javalla.pdf
   - VirtuaaliSoketti.java


## Positive and Negative ACKs
*****************************

1. Add to your data packet some method for detecting at least a single bit error, e.g. CRC8, you can use possible built-in methods in programming languages. is described in Finnish Siirtorekisterin toteutus Javalla.pptx or Siirtorekisterin toteutus Javalla.pdf
   - Crc8.java

2. Reliable data transfer with positive and negative ACKs
   - RDTLayer2_0.java

3. Reliable data transfer with only positive ACKs
   - RDTLayer2_2.java

4. Reliable data transfer with only negative ACKs
   - RDTLayer3_1.java


## Reliable Transport protocol
******************************

1. Implement reliable data transfer on top of UDP that can handle packet loss, packet delay and single bit error (detect error and react accordingly).
   - RDTLayer3_0.java