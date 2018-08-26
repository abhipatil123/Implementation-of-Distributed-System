// Object that will have <Identifier> <Hostname> <Port> read from config file stored
public class Node {
	int nodeId;
	String host;
	int port;
	public Node(int nodeId, String host, int port) {
		this.nodeId = nodeId;
		this.host = host + ".utdallas.edu";
		this.port = port;
	}
}