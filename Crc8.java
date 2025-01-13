public final class Crc8 {
    private Crc8() {
    }

    public static byte calculateCRC8(byte[] bytedata, int length, boolean isCheck) {
        // System.out.printf("bytedata[0] 0x%02X", bytedata[0]).println(); // 61	01100001	a
        // System.out.println("Bytedata[0] " + Integer.toBinaryString(bytedata[0]));
        // System.out.println("Bytedata[1] " + Integer.toBinaryString(bytedata[1]));
        // System.out.println("Bytedata[2] " + Integer.toBinaryString(bytedata[2]));
        int maski [] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
        byte rekisteri = 0;

        // go through all bytes
        for (int i =0; i < length; i ++) {
            // move each bit of byte to register MSB first
            for (int j=7; j >=0; j--) {
                // Pos 3
                byte x = (byte) (((rekisteri & 0x80) >>> 7) ^ ((rekisteri & 0x02) >>> 1)); 
                // Pos 2
                byte y = (byte) (((rekisteri & 0x80) >>> 7) ^ (rekisteri & 0x01));
                // Pos 1
                byte z = (byte) (((rekisteri & 0x80) >>> 7) ^ ((bytedata[i] & maski[j]) >>> j));
                // Siirto takaisin
                rekisteri = (byte) (rekisteri << 1);
                rekisteri = (byte) ((rekisteri & ~0x04) ^ (x << 2));
                rekisteri = (byte) ((rekisteri & ~0x02) ^ (y << 1));
                rekisteri = (byte) ((rekisteri & ~0x01) ^ z);
                // System.out.println("rekisteri lopuksi " + Integer.toBinaryString(rekisteri));
            }
        }
        if (!isCheck){
            // Nollat mukaan (vain generointiin)
            for (int j=7; j >=0; j--) {
                // move each bit of byte to register MSB first
                // Pos 3
                byte x = (byte) (((rekisteri & 0x80) >>> 7) ^ ((rekisteri & 0x02) >>> 1)); 
                // Pos 2
                byte y = (byte) (((rekisteri & 0x80) >>> 7) ^ (rekisteri & 0x01));
                // Pos 1
                byte z = (byte) (((rekisteri & 0x80) >>> 7) ^ 0);
                // Siirto takaisin
                rekisteri = (byte) (rekisteri << 1);
                rekisteri = (byte) ((rekisteri & ~0x04) ^ (x << 2));
                rekisteri = (byte) ((rekisteri & ~0x02) ^ (y << 1));
                rekisteri = (byte) ((rekisteri & ~0x01) ^ z);
                // System.out.println("rekisteri lopuksi " + Integer.toBinaryString(rekisteri));
            }
        }
        return (byte) (rekisteri & 0xFF);
    }
}
