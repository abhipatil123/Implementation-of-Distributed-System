import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	
	//Each node acts as a client to all its neighboring nodes
	public TCPClient(Application appObject) {
		//Create client connection to every node not just quorums
		for(Integer i=0; i < appObject.numOfNodes; i++){
			String hostName = appObject.nodeInfo.get(i).host;
			int port = appObject.nodeInfo.get(i).port;
			InetAddress address = null;
			try {
				address = InetAddress.getByName(hostName);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}
			Socket client = null;
			try {
				client = new Socket(address,port);
			} catch (IOException e) {
				System.out.println("Connection Broken");
				e.printStackTrace();
				System.exit(1);
			}
			//Send client request to all neighboring nodes
			appObject.channels.put(i, client);
			//appObject.checkLockGranted.put(i, false);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(client.getOutputStream());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			appObject.oStream.put(i, oos);	
		}
	}
}
