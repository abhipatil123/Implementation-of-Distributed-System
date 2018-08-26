
public class SendRequestThread extends Thread{
	Application appObject;
	public SendRequestThread(Application appObject) {
		this.appObject = appObject;
	}
	void sendRequestMessage() throws InterruptedException {
		appObject.scalar++;
		for(Integer curNeighbor : appObject.neighbors){
			synchronized(appObject){
				RequestMessage m = new RequestMessage(appObject.curNodeId); 
				m.timestamp = appObject.scalar;
				System.out.println("Node " + appObject.curNodeId + ": Sending request to " + curNeighbor + " with ts = " + appObject.scalar);
				m.sendMessage(m, appObject.oStream.get(curNeighbor));
			}
		}
		
	}
	public void run(){
		try {
				this.sendRequestMessage();	
		} catch (InterruptedException e) {
			System.out.println("Error in SendMessages");
			e.printStackTrace();
		}
	}
}
