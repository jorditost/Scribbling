package edu.tamu.recognition.paleo;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Requester{
	
	Socket requestSocket;

	public BufferedReader vlub;
	public String message;
	public LinkedList<String> messageList;
	public int port;
	
	Requester(){
		
		messageList = new LinkedList<String>();
	}
	
	void run()
	{

		try{
			requestSocket = new Socket("172.16.22.182", port);
			//System.out.println("Connected");
			vlub = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			
			if((message = vlub.readLine()) !=null) {
		
				System.out.println("server>" + message);
				messageList.add(message);
			}

			vlub.close();
			requestSocket.close();
		}
		catch(Exception classNot){
			System.err.println("data received in unknown format");
		}

	}

}




//void sendMessage(String msg)
//{
//	try{
//		out.writeObject(msg);
//		out.flush();
//		System.out.println("client>" + msg);
//	}
//	catch(IOException ioException){
//		ioException.printStackTrace();
//	}
//}