import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Application {
	//cs-leave and cs-enter - static
	public int numOfNodes;  
	public int inter_request_delay;
	public int cs_exec_time;
	public int numOfRequests, requestSentCount = 0; 
	public int curNodeId;
	ArrayList<Node> nodes = new ArrayList<>();
	HashMap<Integer, Node> nodeInfo = new HashMap<>();
	HashMap<Integer,Socket> channels;
	ArrayList<Integer> neighbors;
	HashMap<Integer, ArrayList<Integer>> quorum;
	public HashMap<Integer, ObjectOutputStream> oStream;
	public boolean active;
	public boolean isLocked;
	public int lockedNodeId;
	HashMap<Integer, Boolean> checkLockGranted;
	//HashMap<Integer, Boolean> checkFailedMsgRcvd;
	public boolean hasReceivedFailed;
	public int scalar;
	public boolean hasEnteredCS;
	public boolean isInquireSent;
	Queue<Integer> inqMessages;
	int[] csEnterVector;
	int[] csTestVector;
	int csCounter = 0;
	boolean res;
	
	PriorityQueue<RequestMessage> requestQueue= new PriorityQueue<>(100, new RequestComparator());
	
	//Constructor to initialize all variables
	public Application() {
		active=false;
		neighbors = new ArrayList<>();
		nodes = new ArrayList<Node>();
		nodeInfo = new HashMap<Integer,Node>();
		channels = new HashMap<Integer,Socket>();
		oStream = new HashMap<Integer,ObjectOutputStream>();
		checkLockGranted = new HashMap<Integer,Boolean>();
		//checkFailedMsgRcvd = new HashMap<Integer,Boolean>();
		inqMessages = new LinkedList<Integer>();
		res = true;
	}
	
	//Initialize again before taking another snapshot
	void initialize(Application appObject){

		//Set<Integer> keys = appObject.channels.keySet();
		
		//for(Integer e: appObject.neighbors){
		//	//appObject.RxdMarker.put(e,false);
		//}
		for(int i=0;i<appObject.nodes.size();i++){
			appObject.nodeInfo.put(appObject.nodes.get(i).nodeId, appObject.nodes.get(i));
		}
		for(Integer i : appObject.quorum.get(appObject.curNodeId)){
			appObject.neighbors.add(i);
			appObject.checkLockGranted.put(i, false);
		}
		Util.numberofNodes = appObject.numOfNodes;
		csEnterVector = new int[appObject.numOfNodes];
		csTestVector = new int[appObject.numOfNodes];
	}
}
class RequestComparator implements Comparator<RequestMessage>{

	@Override
	public int compare(RequestMessage r1, RequestMessage r2) {
		// TODO Auto-generated method stub
		if (r1.timestamp < r2.timestamp) {
			System.out.println(r1.timestamp + " < " + r2.timestamp);
			return -1;
		}
		else if ((r1.timestamp == r2.timestamp) && (r1.nodeId < r2.nodeId)) {
			System.out.println(r1.timestamp + " = " + r2.timestamp + " and " + r1.nodeId + " < " + r2.nodeId);
			return -1;
		}
		else if (r1.timestamp > r2.timestamp){
			System.out.println(r1.timestamp + " > " + r2.timestamp);
			return 1;
		}
		else if ((r1.timestamp == r2.timestamp) && (r1.nodeId > r2.nodeId)) {
			System.out.println(r1.timestamp + " = " + r2.timestamp + " and " + r1.nodeId + " > " + r2.nodeId);
			return 1;
		}
		else {
			System.out.println("Something wrong with compare function");
			return 0;
		}
	}
}
