import java.io.*;
import java.net.*;

public class IOTConnection extends Thread {
	// Class variables
	DataInputStream is;
	OutputStream os;

	public IOTConnection(DataInputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public void run() {
		try {
			// Read first 4 bytes to know the total length of Client's message
			byte[] msgLength = new byte[4]; // Standard for all protocol specified messages
			for (int i = 0; i < 4; i++) {
				msgLength[i] = is.readByte();
			}
			int code = (int) is.readByte(); // read message code
			int idLength = (int) is.readByte(); // read id length
			StringBuilder idBuilder = new StringBuilder();
			for (int i = 0; i < idLength; i++) { // read length
				idBuilder.append((char) is.readByte());
			}

			// Execute ACK message for client method
			sendAcknowledge(idBuilder.toString(), 3, 0);
		} catch (IOException ioe) {
			System.err.println("ERROR REPORTED -> IOTConnection IOException: " + ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	// Method to send ACK message
	private void sendAcknowledge(String id, int msgCode, int status) throws IOException {
		byte[] totalLength = new byte[4];
		int idLength = id.length();
		totalLength[0] = (byte) (4 + 1 + 1 + 1 + idLength);

		byte[] msg = new byte[totalLength[0]];
		for (int i = 0; i < 4; i++) {
			msg[i] = totalLength[i];
		}
		msg[4] = (byte) msgCode;
		msg[5] = (byte) status;
		msg[6] = (byte) idLength;
		int cur = 7;
		byte[] idBytes = id.getBytes();
		while (cur < (4 + 1 + 1 + 1 + idLength)) {
			msg[cur] = idBytes[cur - 7];
			cur++;
		}

		os.write(msg);
	}
}
