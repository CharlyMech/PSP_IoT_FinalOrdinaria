import java.io.*;
import java.net.*;

/**
 * This program runs with 3 arguments: serverIP, serverPOrt, deviceID
 */

public class IOTClient {
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("This program requires 3 arguments: serverIP, serverPOrt, deviceID");
			System.exit(-1);
		}
		//? I suppose here that the user does not want to make my program explode ... But should be confirmed input (ip regex, port is int, etc...)
		String serverIp = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String deviceID = args[2];
		try ( // Auto-closable elements
				Socket socket = new Socket(serverIp, serverPort); // connection socket
				DataInputStream is = new DataInputStream(socket.getInputStream()); // Standard input
				OutputStream os = socket.getOutputStream(); // Standard output
		) {
			byte[] totalLength = new byte[4]; // total message length
			int msgCode = 1; // message code
			int idLength = deviceID.length();
			totalLength[0] = (byte) (4 + 1 + 1 + idLength); // assign length to first element in byte[] totalLength

			byte[] msg = new byte[totalLength[0]]; // Create byte[] to store the whole message
			for(int i = 0; i< 4; i++) { // store total message length
				msg[i] = totalLength[i];
			}
			msg[4] = (byte) msgCode; // store message code
			msg[5] = (byte) idLength; // store id length
			int cur = 6;
			byte[] idBytes = deviceID.getBytes();
			while(cur < (4 + 1 + 1 + idLength)) { // store id
				msg[cur] = idBytes[cur-6];
				cur++;
			}

			os.write(msg); // send information to server

			// Execute method to read ACK from server
			getAcknowledge(is);
			//! This might be dangerous!
			// Since we're using TCP connection, this might be a point of failure since the server might not be up
		} catch (SocketException e) {
			System.err.println("Socket: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO: " + e.getMessage());
		}
	}

	// Method to read ACK from server and print it int terminal
	private static void getAcknowledge(DataInputStream is) throws IOException {
		// Read first 4 bytes to know the total length of Client's message
		byte[] msgLength = new byte[4]; // Standard for all protocol specified messages
		for (int i = 0; i < 4; i++) {
			msgLength[i] = is.readByte();
		}
		int code = (int) is.readByte(); // read message code
		int status = (int) is.readByte(); // read status message
		int idLength = (int) is.readByte(); // read id length
		StringBuilder idBuilder = new StringBuilder();
		for (int i = 0; i < idLength; i++) { // read length
			idBuilder.append((char) is.readByte());
		}

		System.out.printf("ACK from server: code %s - status %s - host id %s", code, status, idBuilder.toString());
	}
}
