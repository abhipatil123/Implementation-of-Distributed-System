import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{

	ServerSocket listener = null;
	Socket socket = null;
	int serverPort;
	private Application appObject;
	
	public TCPServer(Application appObject) {
		
		this.appObject = appObject; //Global appObject
		// port number on which this node should listen 
		serverPort = appObject.nodes.get(appObject.curNodeId).port;
		try {
			listener = new ServerSocket(serverPort);
		} 
		catch(BindException e) {
			System.out.println("Node " + appObject.curNodeId + " : " + e.getMessage() + ", Port : " + serverPort);
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void listenforinput(){
		//Listen and accept for any client connections
		try {
			while (true) {
				ObjectInputStream ois = null;
				try {
					socket = listener.accept();
					ois = new ObjectInputStream(socket.getInputStream());
				} catch (IOException e1) {
					System.out.println("Connection Broken");
					System.exit(1);
				}
				// For every client request start a new thread 
				new ReceiveThread(ois,appObject).start();
			}
		}
		finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}