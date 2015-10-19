package edu.tamu.recognition.paleo;
import java.io.*;
import java.net.*;

public class EchoClient {
	
    public static void run(String senderString) throws IOException {

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket("172.16.22.182", 7);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: taranis.");
            System.exit(1);
        }

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	//String userInput;

	//while ((userInput = stdIn.readLine()) != null) {
	    out.println(senderString);
	   // System.out.println("echo: " + in.readLine());
	//}

	out.close();
	in.close();
	stdIn.close();
	echoSocket.close();
    }
}
