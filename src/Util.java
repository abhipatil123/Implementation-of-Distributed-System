import java.util.HashMap;
import java.util.PriorityQueue;

public class Util {
	public static int numberofNodes;
	public static boolean areAllTrue(HashMap<Integer, Boolean> checkLockGranted)
	{
		for (Boolean value : checkLockGranted.values()) {
		    if(!value) {return false;}
		}
		return true;
	}
	
	public static int somenumber(double val){
		return (int)(-val * Math.log(Math.random()));
	}
	
	public static void printReqQueue(PriorityQueue<RequestMessage> requestQueue) {
		for (RequestMessage i : requestQueue) {							
			System.out.print(i.nodeId + " (" + i.timestamp + ") " +  ", ");
		}
		System.out.println();
	}
}
