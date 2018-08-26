import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class CriticalSectionHandler extends Thread {
	Application appObject;
	PrintWriter pw;
	public CriticalSectionHandler(Application appObject) {
		this.appObject = appObject;
		String file = "output-" + appObject.curNodeId + ".out";
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		this.pw.write("Number of Requests : " + appObject.numOfRequests + "\n");
		pw.flush();
		for(appObject.csCounter = 0; appObject.csCounter < appObject.numOfRequests; appObject.csCounter++) {
			new SendRequestThread(appObject).start();
			try {
				synchronized (appObject) {
					enterCS();
					this.pw.write(appObject.csCounter + "\n");
					pw.flush();
					System.out.println("Node " + appObject.curNodeId + ": entered CS------------------------------------------------------ " + appObject.csCounter);
					System.out.println();
					leaveCS();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.pw.close();
	}
	
	public void enterCS() throws InterruptedException {
		synchronized (appObject) {
			//Check boolean hashmap if all elements are true
			while(!Util.areAllTrue(appObject.checkLockGranted)) {
				System.out.println("Node " + appObject.curNodeId + ": Waiting: " + Util.areAllTrue(appObject.checkLockGranted));
				appObject.wait();
			}
			appObject.hasEnteredCS = true;
			//Keep blocking till received grant from all msgs
			appObject.csEnterVector[appObject.curNodeId]++;
			for (int i = 0; i < appObject.csEnterVector.length; i++) {
				appObject.csTestVector[i] = appObject.csEnterVector[i];
			}
			Thread.sleep(Util.somenumber(appObject.cs_exec_time)); //Enters critical section for cs_exec_time
		}
	}
	public void leaveCS() throws InterruptedException {
		int[] temp = null;
		synchronized (appObject) {
			appObject.hasEnteredCS = false;
			appObject.hasReceivedFailed = false;
			appObject.inqMessages.clear();
			appObject.csEnterVector[appObject.curNodeId]++;
			for (Integer curNeighbor : appObject.neighbors) {
				appObject.checkLockGranted.put(curNeighbor, false);
				ReleaseMessage rm = new ReleaseMessage(appObject.curNodeId);
				System.out.println("Node " + appObject.curNodeId + ": Sending release to " + curNeighbor);
				rm.sendMessage(rm, appObject.oStream.get(curNeighbor));
			}
			temp = appObject.csEnterVector;
			for (int i = 0; i < appObject.csEnterVector.length; i++) {
				if (i!=appObject.curNodeId && appObject.csTestVector[i] != appObject.csEnterVector[i]) {
					appObject.res = false;
				}
			}
			//this.pw.write(Arrays.toString(appObject.csEnterVector) + "\n");
		}
		//System.out.println("Exiting CS Node " + appObject.curNodeId + Arrays.toString(temp) + " " + "DME :: " + appObject.res);
		Thread.sleep(Util.somenumber(appObject.inter_request_delay));
	}
	
}
