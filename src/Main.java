import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Parse through config.txt file
		Application appObject = ReadConfigFile.readConfigFile(args[1]);
		// Get the node number of the current Node
		appObject.curNodeId = Integer.parseInt(args[0]);
		//Get the configuration file name from command line
		String configFileName = args[1];

		// Transfer the collection of nodes from ArrayList to hash map nodes
		appObject.initialize(appObject);
		//Create a server socket 
		TCPServer server = new TCPServer(appObject);
		
		//Create channels and keep it till the end
		new TCPClient(appObject);

		//Initialize all data structures
		//appObject.initialize(appObject);
		
	    new CriticalSectionHandler(appObject).start();

		server.listenforinput(); //Listen for client connections
		
	}
}
