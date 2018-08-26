import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


@SuppressWarnings("serial")
public class StreamMessage implements Serializable {
	int[] vector;
	public StreamMessage() {
		// TODO Auto-generated constructor stub
		this.vector = new int[Util.numberofNodes];
	}
	public void sendMessage(StreamMessage msgType, ObjectOutputStream oos) {
		try {
			oos.writeObject(msgType);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

@SuppressWarnings("serial")
// Sends string message and vector timestamp
class RequestMessage extends StreamMessage implements Serializable{
	public RequestMessage(int nodeId) {
		super();
		this.nodeId = nodeId;
		//this.vector = new int[Util.numberofNodes];
	}
	public RequestMessage() {
		// TODO Auto-generated constructor stub
	}
	int nodeId;
	int timestamp;
	//int[] vector;
}
// Sends marker string and nodeId
@SuppressWarnings("serial")
class LockMessage extends StreamMessage implements Serializable{
	public LockMessage(int nodeId) {
		super();
		this.nodeId = nodeId;
		//this.vector = new int[Util.numberofNodes];
	}
	public LockMessage() {
		// TODO Auto-generated constructor stub
	}
	int nodeId;
	//int[] vector;
}

@SuppressWarnings("serial")
class ReleaseMessage extends StreamMessage implements Serializable{
	public ReleaseMessage(int nodeId) {
		super();
		this.nodeId = nodeId;
		//this.vector = new int[Util.numberofNodes];
	}
	public ReleaseMessage() {
		// TODO Auto-generated constructor stub
	}
	int nodeId;
	//int[] vector;
}
// State message is sent to converge cast tree,
// It should have the process state and all its incoming channel states 
@SuppressWarnings("serial")
class FailedMessage extends StreamMessage implements Serializable{
	public FailedMessage(int nodeId) {
		super();
		this.nodeId = nodeId;
		//this.vector = new int[Util.numberofNodes];
	}
	public FailedMessage() {
		// TODO Auto-generated constructor stub
	}
	int nodeId;
	//int[] vector;
}

// Send Finish messages to all nodes to when termination is detected
@SuppressWarnings("serial")
class InquireMessage extends StreamMessage implements Serializable{
	public InquireMessage(int nodeId) {
		super();
		this.nodeId = nodeId;
		//this.vector = new int[Util.numberofNodes];
	}
	public InquireMessage() {
		// TODO Auto-generated constructor stub
	}
	int nodeId;
	//int[] vector;
}

@SuppressWarnings("serial")
class YieldMessage extends StreamMessage implements Serializable{
	public YieldMessage(int nodeId) {
		super();
		this.nodeId = nodeId;
		//this.vector = new int[Util.numberofNodes];
	}
	public YieldMessage() {
		// TODO Auto-generated constructor stub
	}
	int nodeId;
	//int[] vector;
}

