import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Iterator;

//Read object data sent by neighboring clients
public class ReceiveThread extends Thread {
	ObjectInputStream ois;
	Application appObject;
	Util mutexObject;
	public ReceiveThread(ObjectInputStream ois,Application appObject) {
		this.ois = ois;
		this.appObject = appObject;
		this.mutexObject = new Util();
	}

	public void run() {
		while(true){
			try {
				StreamMessage msg = (StreamMessage) ois.readObject();
				// Synchronizing appObject so that multiple threads access appObject in a synchronized way
				synchronized(appObject){
					for (int i = 0; i < msg.vector.length; i++) {
						if(msg.vector[i] >= appObject.csEnterVector[i]) {
							appObject.csEnterVector[i] = msg.vector[i];
						}
					}
					if (msg instanceof RequestMessage) {
						appObject.scalar = Math.max(appObject.scalar, ((RequestMessage) msg).timestamp) + 1;
						
						int requesting_nodeId = ((RequestMessage) msg).nodeId;
						System.out.print("Node " + appObject.curNodeId + " Request Queue : ");
						Util.printReqQueue(appObject.requestQueue);
						System.out.print("Node " + appObject.curNodeId + ": received request from " + requesting_nodeId + "timestamp : " + appObject.scalar);
						System.out.println(" : Is Locked? " + appObject.isLocked);
						
						if(!appObject.isLocked) { // if not locked, send a lock msg to the requesting node
							//System.out.println("Requesting node " + requesting_nodeId + ": Is Locked? " + appObject.isLocked);
							
							appObject.lockedNodeId = requesting_nodeId;
							LockMessage lm = new LockMessage(appObject.curNodeId);
							//System.out.println("OOS: "+ appObject.oStream.get(appObject.lockedNodeId));
							lm.sendMessage(lm, appObject.oStream.get(appObject.lockedNodeId)); 
							System.out.println("Node " + appObject.curNodeId + ": send lock to " + appObject.lockedNodeId);
							appObject.isLocked = true;
						}
						else // if quorum member is locked to some other node
						{
							appObject.requestQueue.add((RequestMessage) msg); //Add to request queue only if request is not satisfied
							RequestMessage head = appObject.requestQueue.peek();
							System.out.println("Node " + appObject.curNodeId + ": priority queue " + head.nodeId);
							if (head.equals((RequestMessage) msg)) { 
								//send an inquire msg to that node if timestamp is top of priority queue
								if(!appObject.isInquireSent) {
									appObject.isInquireSent = true;
									InquireMessage im = new InquireMessage(appObject.curNodeId);
									System.out.println("Node " + appObject.curNodeId + " : send inquire message to locked node Id " + appObject.lockedNodeId);
									im.sendMessage(im, appObject.oStream.get(appObject.lockedNodeId));
								}
							}
							else {
								//send failed message if not top of priority queue
								FailedMessage fm = new FailedMessage(appObject.curNodeId);
								System.out.println("Node " + appObject.curNodeId + ": send Failed masg to " + requesting_nodeId);
								fm.sendMessage(fm, appObject.oStream.get(requesting_nodeId));
							}
						}
					}
					else if (msg instanceof LockMessage) {
						int lockedNodeId = ((LockMessage) msg).nodeId;
						System.out.println("Node " + appObject.curNodeId + ": received lock from " + lockedNodeId);
						//Make boolean array of that nodeId true
						appObject.checkLockGranted.put(lockedNodeId, true);
						System.out.println("Node " + appObject.curNodeId + "Check Lock Granted " + appObject.checkLockGranted);
						if(Util.areAllTrue(appObject.checkLockGranted)){
							appObject.notify();
						}
					}
					else if (msg instanceof ReleaseMessage) {
						int released_node = ((ReleaseMessage) msg).nodeId;
						System.out.println("Node " + appObject.curNodeId + ": received release from " + released_node);
						System.out.print("Node " + appObject.curNodeId + " Request Queue before removing: ");
						Util.printReqQueue(appObject.requestQueue);
						appObject.isLocked = false;
						appObject.isInquireSent = false;
						
//						if(appObject.isInquireSent) {
//							System.out.println("Node " + appObject.curNodeId + ": isInquireSent is true");
//							appObject.isInquireSent = false;
//							//appObject.requestQueue.poll();
//							//Remove node id from which release msg has received
//							//appObject.requestQueue.remove((RequestMessage)msg);
//							for (RequestMessage i : appObject.requestQueue) {
//								if (i.nodeId == released_node) {  
//									appObject.requestQueue.remove(i);
//									break;
//								}
//							}
//						}
						//else { //TODO check if request queue is empty
							appObject.requestQueue.poll();
					//	}
						System.out.print("Node " + appObject.curNodeId + " Request Queue after removing : ");
						Util.printReqQueue(appObject.requestQueue);
						//Should send lock msg
						//to next requested node id in priority queue
						if(!appObject.requestQueue.isEmpty())
						{
							appObject.lockedNodeId = appObject.requestQueue.peek().nodeId;;
							LockMessage lm = new LockMessage(appObject.curNodeId);
							System.out.println("Node " + appObject.curNodeId + ": send lock to " + appObject.lockedNodeId);
							lm.sendMessage(lm, appObject.oStream.get(appObject.lockedNodeId));
							appObject.isLocked = true;
							
						}
						
					}
					else if (msg instanceof YieldMessage) {
						appObject.isInquireSent = false;
						int yielded_node = ((YieldMessage) msg).nodeId;
						System.out.println("Node " + appObject.curNodeId + ": received yield from " + yielded_node);
						// needs to send a grant message to the top request in the queue
						if (appObject.requestQueue.peek() != null) {
							appObject.lockedNodeId = appObject.requestQueue.peek().nodeId;
							LockMessage lm = new LockMessage(appObject.curNodeId);
							System.out.println("Node " + appObject.curNodeId + ": send lock to " + appObject.lockedNodeId);
							lm.sendMessage(lm, appObject.oStream.get(appObject.lockedNodeId));
							appObject.isLocked = true;
						}
						else {
							appObject.isLocked = false;
						}
					}
					else if (msg instanceof InquireMessage) {
						int inquiring_nodeId = ((InquireMessage) msg).nodeId;
						System.out.println("Node " + appObject.curNodeId + ": received inquire from " + inquiring_nodeId);
						//send yield when hasReceivedFailed is true and it is not in CS (hasEnteredCS is false)
						if (appObject.hasReceivedFailed && appObject.hasEnteredCS == false) {
							YieldMessage ym = new YieldMessage(appObject.curNodeId);
							System.out.println("Node " + appObject.curNodeId + ": send yield to " + inquiring_nodeId);
							ym.sendMessage(ym, appObject.oStream.get(inquiring_nodeId));
						}
						else if(appObject.hasReceivedFailed == false && appObject.hasEnteredCS == false ){
							System.out.println("Node " + appObject.curNodeId + " : buffer inquire msg from " + inquiring_nodeId);
							//Buffer the inquiring nodeId
							appObject.inqMessages.add(inquiring_nodeId);
							System.out.println("Inquire Queue : " + appObject.inqMessages);
						}
						else if (appObject.hasEnteredCS == true){
							//Has entered critical section
							//Do-Nothing
							System.out.println("Node " + appObject.curNodeId + ": Igonore Inquire Msg since Node has entered CS");
						}
						else {
							System.out.println("Node " + appObject.curNodeId + "something wrong after receiving Inquire message");
						}
					}
					else if (msg instanceof FailedMessage) {
						int failedNodeId = ((FailedMessage) msg).nodeId;
						System.out.println("Node " + appObject.curNodeId + ": received failed msg from " + failedNodeId);
						//Store the failed msgs in an hashmap
						//appObject.checkFailedMsgRcvd.put(failedNodeId, true);
						appObject.hasReceivedFailed = true;
						//Check if inquire msgs queue is not empty
						//Send yield msg to the inquiring nodeid
						//Since the curNode has received a failed msg
						while(!appObject.inqMessages.isEmpty()) {
							int inquiring_nodeId = appObject.inqMessages.poll(); 
							YieldMessage ym = new YieldMessage(appObject.curNodeId);
							System.out.println("Node " + appObject.curNodeId + ": send yield to " + inquiring_nodeId);
							ym.sendMessage(ym, appObject.oStream.get(inquiring_nodeId));
						}
					}
					else {
						System.out.println("received none");
					}
				}
			}
			catch(StreamCorruptedException e) {
				e.printStackTrace();
				System.exit(2);
			}
			catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(2);
			}
		}
	}
}
