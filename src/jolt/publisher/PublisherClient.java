package jolt.publisher;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PublisherClient {

 public static void main(String[] args) {
	
		try {
			Socket connection = new Socket ("localhost", 1602);
			PrintWriter writer = new PrintWriter (new OutputStreamWriter(connection.getOutputStream())); 
			
			writer.println("PUBLISH | sports | Mbappe scores a hat trick");
			
			writer.flush();
		
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
	

	}

}

