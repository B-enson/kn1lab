import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Die "Klasse" Sender liest einen String von der Konsole und zerlegt ihn in einzelne Worte. Jedes Wort wird in ein
 * einzelnes {@link Packet} verpackt und an das Medium verschickt. Erst nach dem Erhalt eines entsprechenden
 * ACKs wird das nächste {@link Packet} verschickt. Erhält der Sender nach einem Timeout von einer Sekunde kein ACK,
 * überträgt er das {@link Packet} erneut.
 */
public class Sender {
    /**
     * Hauptmethode, erzeugt Instanz des {@link Sender} und führt {@link #send()} aus.
     * @param args Argumente, werden nicht verwendet.
     */
    public static void main(String[] args) {
        Sender sender = new Sender();
        try {
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erzeugt neuen Socket. Liest Text von Konsole ein und zerlegt diesen. Packt einzelne Worte in {@link Packet}
     * und schickt diese an Medium. Nutzt {@link SocketTimeoutException}, um eine Sekunde auf ACK zu
     * warten und das {@link Packet} ggf. nochmals zu versenden.
     * @throws IOException Wird geworfen falls Sockets nicht erzeugt werden können.
     */
    private void send() throws IOException {


   	    //Text einlesen und in Worte zerlegen
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();
        String[] splitInput = input.split(" ");
        byte[][] barry = new byte[splitInput.length][];
        for(int i = 0; i < splitInput.length; i++){
            barry[i] = splitInput[i].getBytes();
        }
        // Socket erzeugen auf Port 9998 und Timeout auf eine Sekunde setzen
        DatagramSocket clientSocket = new DatagramSocket(9998);
        clientSocket.setSoTimeout(1000);

        // Iteration über den Konsolentext
        int seq = 0;
        int ack = 0;

        String[] complete = new String[barry.length];
        int i = 0; //index for building complete output string
        for(byte[] bob : barry) {

            while (true) {

                // Paket an Port 9997 senden
                // create new packet
                Packet packetOut = new Packet(seq, ack, false, bob);

                // serialize Packet for sending
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                ObjectOutputStream o = new ObjectOutputStream(b);
                o.writeObject(packetOut);
                byte[] buf = b.toByteArray();

                DatagramPacket p = new DatagramPacket(buf,buf.length,InetAddress.getLocalHost(),9997);

                //send packet
                clientSocket.send(p);


                try {
                    byte[] buffer = new byte[256];
                    DatagramPacket rcvPacketRaw = new DatagramPacket(buffer, buffer.length);
                    clientSocket.receive(rcvPacketRaw);

                    // deserialize Packet
                    ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(rcvPacketRaw.getData()));
                    Packet packetIn = (Packet) is.readObject();

                    //correct ack for sent package
                    if(packetIn.isAckFlag() && (packetIn.getAckNum() == seq + packetIn.getPayload().length)) {
                        String word = new String((packetIn.getPayload()), StandardCharsets.UTF_8);
                        System.out.println(word);
                        complete[i] = (word);
                        i++;
                        seq += bob.length;
                        break;
                    }
                    //... no correct package received -> send again
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    System.out.println("Receive timed out, retrying...");
                }


            }

        }
        
        // Wenn alle Packete versendet und von der Gegenseite bestätigt sind, Programm beenden

        //output of complete message string
        System.out.print("Complete Message: ");
        for(String w : complete){
            System.out.print(w + " ");
        }

        clientSocket.close();
        
        if(System.getProperty("os.name").equals("Linux")) {
            clientSocket.disconnect();
        }

        System.exit(0);






    }

}

