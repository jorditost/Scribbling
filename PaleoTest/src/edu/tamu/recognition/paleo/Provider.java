package edu.tamu.recognition.paleo;

import java.io.*;
import java.net.*;


public class Provider{
	
	ServerSocket providerSocket;
	Socket connection = null;
	OutputStreamWriter out;
	ObjectInputStream in;
	String message;
	
	Provider(){}
	
	void run(String message)
	{
		try{
			//1. creating a server socket
			providerSocket = new ServerSocket(5000, 10);
			//2. Wait for connection
			System.out.println("Waiting for connection to send");
			connection = providerSocket.accept();
			
			
			out = new OutputStreamWriter(connection.getOutputStream());
			sendMessage(message);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				//in.close();
				out.close();
				providerSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			out.write(msg);
			out.flush();
			System.out.println("receive>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
}
