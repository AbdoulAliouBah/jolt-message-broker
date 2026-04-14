package jolt.publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SuscriberClient {

	public static void main(String[] args) {
	
		try {
			Socket connection = new Socket ("localhost", 1602);
			PrintWriter writer = new PrintWriter (new OutputStreamWriter(connection.getOutputStream())); 
			
			writer.println("SUBSCRIBE | sports");
			System.out.println("Connected to broker!");
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String incoming = reader.readLine();
			System.out.println("Received: " + incoming);
		
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
	

	}

}
