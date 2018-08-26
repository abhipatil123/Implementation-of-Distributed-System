import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadConfigFile {

	public static Application readConfigFile(String name) throws IOException{
		Application mapFile = new Application();
		int node_count = 0,next = 0;
		// Keeps track of current node
		int curNode = 0;
		
		String fileName = System.getProperty("user.dir") + "/" + name;
		
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			while((line = br.readLine()) != null) {
				if(line.length() == 0 || line.startsWith("#"))
					continue;
				// Ignore comments and consider only those lines which are not comments
				String[] config_input;
				if(line.contains("#")){
					String[] config_input_comment = line.split("#.*$"); //Ignore text after # symbol
					config_input = config_input_comment[0].split("\\s+");
				}
				else {
					config_input = line.split("\\s+");
				}

				if(next == 0 && config_input.length == 4){
					mapFile.numOfNodes = Integer.parseInt(config_input[0]);
					mapFile.inter_request_delay = Integer.parseInt(config_input[1]);
					mapFile.cs_exec_time = Integer.parseInt(config_input[2]);
					mapFile.numOfRequests = Integer.parseInt(config_input[3]);
					mapFile.quorum = new HashMap<>();
					next++;
				}
				else if(next == 1 && node_count < mapFile.numOfNodes)
				{							
					mapFile.nodes.add(new Node(Integer.parseInt(config_input[0]),config_input[1],Integer.parseInt(config_input[2])));
					node_count++;
					if(node_count == mapFile.numOfNodes){
						next = 2;
					}
				}
				else if(next == 2) {
					ArrayList<Integer> members = new ArrayList<Integer>();
					//members.add(curNode);
					for(String i : config_input){
						members.add(Integer.parseInt(i));
					}
					mapFile.quorum.put(curNode, members);
					curNode++;
				}
			}
			br.close();  
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");                  
		}
		return mapFile;
	}

	public static void main(String[] args) throws IOException {
		Application m = ReadConfigFile.readConfigFile("config.txt");
		
		for(Node n : m.nodes) {
			System.out.println(n.host + " " + n.nodeId + " " + n.port);
		}
		System.out.println(m.numOfNodes);
		System.out.println(m.inter_request_delay);
		System.out.println(m.cs_exec_time);
		System.out.println(m.numOfRequests);
		
		System.out.println(m.quorum);

	}
}

