import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class IOTServer {
	public static void main(String[] args) {
		System.out.println("IOT SERVER STARTED"); // Just to know if the server is up
		try (ServerSocket serverSocket = new ServerSocket(12345);) { // Server Socket on port 12345
			try {
				while (true) {
					Socket socket = serverSocket.accept(); // Accept Client's requests
					DataInputStream is = new DataInputStream(socket.getInputStream()); // Standard input
					OutputStream os = socket.getOutputStream(); // Standard output
					// A simple output to know when a client makes a request
					System.out.printf("Connection accepted from: %s\r\n", socket.getInetAddress());

					// Launch new connection thread to handle the client's request
					IOTConnection connection = new IOTConnection(is, os);
					connection.start();
				}
			} catch (IOException ioe) {
				System.err.println("ERROR REPORTED -> socket IOException: " + ioe.getMessage());
				ioe.printStackTrace();
			}
		} catch (IOException ioe) {
			System.err.println("ERROR REPORTED -> ServerSocket IOException: " + ioe.getMessage());
			ioe.printStackTrace();
		}
	}
}
/*
 * Thing to be implemented (a part from specifications)
 * 	- Device ID check: when a device is connected, thread must check if it's already registered and send the proper ACK message. Now it's just implemented device registration
 *
 */