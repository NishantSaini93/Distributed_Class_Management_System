package Servers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import RecordManagement.Record;
import TestCasesAndCheckers.GenerateID;
import TestCasesAndCheckers.LogEvent;


@WebService(endpointInterface="Servers.IDCMSInterface",portName="LVLServicePort",serviceName="LVLService", targetNamespace = "http://Servers/")
public class LVLServer implements IDCMSInterface, Runnable {
	int sPort = 0;
	HashMapRecord objectofrecord;
	int ListeningportUDP = 0;
	LogEvent message1=null;
	String managerID = "";
	static ExecutorService exSer = Executors.newFixedThreadPool(15);
	
	public LVLServer() {}
	protected LVLServer(int s1Port, int UDP_Port1) {
		super();
		sPort = s1Port;
		objectofrecord = new HashMapRecord();
		ListeningportUDP = UDP_Port1;
		message1= new LogEvent("LVL");
		objectofrecord.createRecord(new Record("LVL1017","SR99999", "Ashish", "Sharma", "Distributed Systems,Software Design Methodologies", true, 
				"01/01/2018"));
		objectofrecord.createRecord(new Record("LVL1018","TR99998", "Pradeep", "Verma", "Concordia", "123456", 
				"C#", "LVL"));
	}



	/**
	 * This method creates a new record in LVL Server
	 */
	public boolean createTRecord(String managerID, String firstName, String lastName, String address, String phone, String specialization,
			String location) {
		String teacherID = GenerateID.getInstance().generateNewID("TR");
		Record tRecord = new Record(managerID, teacherID, firstName, lastName, address, phone, specialization, 
				location);
		if (!objectofrecord.createRecord(tRecord))
		{
			message1.setMessage(managerID + " failed to create " + tRecord.toString("TR"));
			return false;
		} 
		else{
			message1.setMessage(managerID + " created " + tRecord.toString("TR"));
			return true;
		}
	}

	public boolean signIn(String managerID){
		if(managerID.substring(0, 3).equalsIgnoreCase("LVL")){
			this.managerID = managerID;		
			message1.setMessage("Manager " +managerID +" signed in. ");
			return true;
		}
		return false;
	}

	//change here
	public String getRecordCounts() {
		message1.setMessage("Manager with manager ID:" +managerID + " executed the query for getcount.");
		String outpt = "LVL Records:"+ objectofrecord.fetchRecordCount();
	
		
		try {
			byte[] m1 = "getRecCount".getBytes();
			byte[] m2 = new byte[1000];
			byte[] m3 = new byte[1000];
			
			Future<String> mtlserverrepl = exSer.submit(new Callable<String>() {

				public String call() throws Exception {
					InetAddress mtlgettinghost = InetAddress.getByName("localhost");
					DatagramPacket mtlrequestingdata = new DatagramPacket(m1, m1.length, mtlgettinghost, 9213);
					DatagramSocket mtlsktdata = new DatagramSocket();
					mtlsktdata.send(mtlrequestingdata);
					DatagramPacket mtlmessageof = new DatagramPacket(m2, m2.length);
					mtlsktdata.receive(mtlmessageof);
					String mtlresponse1 = new String(mtlmessageof.getData());
					String str = mtlresponse1.trim();
					mtlsktdata.close();
					return str;
				}
				
				
			});
			
			Future<String> ddoserverrepl = exSer.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					InetAddress ddogettinghost = InetAddress.getByName("localhost");
					DatagramPacket ddorequestingdata = new DatagramPacket(m1, m1.length, ddogettinghost, 9215);
					DatagramSocket ddosktdata = new DatagramSocket();
					ddosktdata.send(ddorequestingdata);
					DatagramPacket ddomessageof = new DatagramPacket(m3, m3.length);
					ddosktdata.receive(ddomessageof);
					String ddoresponse1= new String(ddomessageof.getData());
					ddosktdata.close();
					String str2 = ddoresponse1.trim();
					return str2;
				}
			});
			
			outpt = outpt + " " + ddoserverrepl.get() + " " +mtlserverrepl.get();
		}catch(Exception e){
			e.printStackTrace();
		}
		return outpt;
	}

	public void signOut()
	{
		this.managerID = "";
		message1.setMessage("Manager " + managerID +" has signed out. ");
	}


	public boolean createSRecord(String managerID, String firstName, String lastName, String coursesRegistered, boolean status,
			String statusDate)  {
		String studentID = GenerateID.getInstance().generateNewID("SR");
		Record sRecord = new Record(managerID, studentID, firstName, lastName, coursesRegistered, status, 
				statusDate);

		if (!objectofrecord.createRecord(sRecord))

		{
			message1.setMessage(managerID +" failed to create " + sRecord.toString("SR"));
			return false;
		}

		else{
			message1.setMessage(managerID +" created " + sRecord.toString("SR"));
			return true;

		}		
	}

	/**
	 * This method edits an existing record in LVL Server
	 */
	public boolean editRecord(String managerID, String recordID, String fieldName, String newValue) {

		boolean editResult = objectofrecord.editRecord(recordID, fieldName, newValue);
		if (!editResult) {
			message1.setMessage(managerID + " failed to edit RecordID:- " + recordID);

		}
		else{
			message1.setMessage(managerID + " edited RecordID :- " + recordID + " changed (" + fieldName + ") to (" + newValue + ")");
		}
		return editResult;
	}



	public static void startLVLServer() {

		LVLServer LVLServer = new LVLServer(8759, 9214);
		Endpoint LVLEP = Endpoint.publish("http://localhost:8080/LVL",LVLServer);
		System.out.println("LVL server started: " + LVLEP.isPublished());
		new Thread(LVLServer).start();
	}

	@Override
	public boolean transferRecord(String managerID, String recordID, String remoteCenterServerName) {
		Record alreadypresentR = objectofrecord.fetchRecordByID(recordID);
		if (alreadypresentR == null) {
			return false;
		} else {
			System.out.println("Existing record(Found during transfer) " + alreadypresentR.toString(alreadypresentR.getRecordID().substring(0, 2)));
			if (remoteCenterServerName.equalsIgnoreCase(managerID.substring(0, 3)))
				return false;

			if (remoteCenterServerName.equalsIgnoreCase("MTL")) {
				System.out.println("Record is being transferred to  MTL");
				if (this.UDPhandlingClient(9213, alreadypresentR).startsWith("true")) {
					message1.setMessage("Record has moved to MTL with ID :- " + alreadypresentR.getRecordID());
					System.out.println("There is successful transfer of Record with ID:-" + alreadypresentR.getRecordID()+"to MTL server");
					return this.Recordtobedeleted(managerID, recordID);
				} else {
					message1.setMessage("The transfer of Record with ID:-" + alreadypresentR.getRecordID() + " is unsuccesful.");
					System.out.println("The transferring of Record with ID:- " + alreadypresentR.getRecordID() + " to MTL server is unsuccessful.");
					return false;
				}
			}
			if (remoteCenterServerName.equalsIgnoreCase("DDO")) {
				System.out.println("Record is being transferred to DDO");
				if (this.UDPhandlingClient(9215, alreadypresentR).startsWith("true")) {
					message1.setMessage("Record has moved to DDO with ID :- " + alreadypresentR.getRecordID());
					System.out.println("There is successful transfer of Record with ID:- " + alreadypresentR.getRecordID()+" to DDO server");
					return this.Recordtobedeleted(managerID, recordID);
				}
				else {
					message1.setMessage("The transfer of Record with ID:- "+ alreadypresentR.getRecordID() +" is unsuccesful.");
					System.out.println("The transferring of Record with ID:- " + alreadypresentR.getRecordID() + " to DDO server is unsuccessful.");
					return false;
				}
			}
			return false;
		}
	}


	public boolean Recordtobedeleted(String Idofmanager, String IdofRecord) {
		if (objectofrecord.deletefromhashmap(IdofRecord)) {
			message1.setMessage(Idofmanager + "Deleting of Record with RecordID:- " + IdofRecord + " is successful");
			return true;
		} else {
			message1.setMessage(
					Idofmanager + "Deleting of Record with RecordID:- " + IdofRecord + " is unsuccessful");
			return false;
		}
	}

	public boolean addRecordtootherplace(Record getrecord) {
		if (this.objectofrecord.Recordaddition(getrecord)) {
			message1.setMessage("Record with RecordID:- " + getrecord.recordID+" is successfully added");
			return true;
		} else {
			message1.setMessage("Unable to add record with RecordID:- " + getrecord.recordID);
			return false;
		}
	}


	public String printRecords() {
		String stringrec = "";
		synchronized(objectofrecord) {
			for (ArrayList<Record> variablerefrence : objectofrecord.getRecordInfoTable().values()) {
				for (Record objrecord : variablerefrence) {
					stringrec += objrecord.toString(objrecord.getRecordID().substring(0, 2));
				}
			}
			return stringrec;}
	}


	public void run() {
		DatagramSocket datatransferSocket = null;
		try {
			datatransferSocket = new DatagramSocket(this.ListeningportUDP);
			DatagramPacket datareply = null;
			byte[] dataofbf = new byte[65536];
			while (true) {

				DatagramPacket datarequesting = new DatagramPacket(dataofbf, dataofbf.length);
				datatransferSocket.receive(datarequesting);

				byte[] streamdatarequest = datarequesting.getData();
				if (convert(streamdatarequest).toString().contains("getRecCount")) {
					String numberofc = "LVL: - " + objectofrecord.fetchRecordCount();
					datareply = new DatagramPacket(numberofc.getBytes(), numberofc.getBytes().length, datarequesting.getAddress(),
							datarequesting.getPort());
					datatransferSocket.send(datareply);

				} else {

					ByteArrayInputStream Streamofdata = new ByteArrayInputStream(streamdatarequest);
					ObjectInputStream outputdatastream = new ObjectInputStream(Streamofdata);
					Record insertingtherecord = (Record) outputdatastream.readObject();
					String instatus = null;
					if (this.addRecordtootherplace(insertingtherecord)) {
						instatus = "true";
						datareply = new DatagramPacket(instatus.getBytes(), instatus.getBytes().length,
								datarequesting.getAddress(), datarequesting.getPort());
						datatransferSocket.send(datareply);

					} else {
						instatus = "false";
						datareply = new DatagramPacket(instatus.getBytes(), instatus.getBytes().length,
								datarequesting.getAddress(), datarequesting.getPort());
						datatransferSocket.send(datareply);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	public String UDPhandlingClient(int pusedUDP, Record recToTransfer) {
		System.out.println("Port with Port Number:- "+ pusedUDP+" received a service request");
		String backmessage = null;
		DatagramSocket datatransferobj = null;
		byte[] args;

		try {
			InetAddress ahost = InetAddress.getByName("localhost");
			datatransferobj = new DatagramSocket();
			if (recToTransfer == null) {
				args = "getRecCount".getBytes(StandardCharsets.UTF_8);
				ByteArrayOutputStream Streamgoingout = new ByteArrayOutputStream();
				Streamgoingout.write(args);
				DatagramPacket request = new DatagramPacket(Streamgoingout.toByteArray(),
						Streamgoingout.toByteArray().length, ahost, pusedUDP);
				datatransferobj.send(request);

				byte[] gatheringdata = new byte[65536];
				DatagramPacket responseofdatagather = new DatagramPacket(gatheringdata, gatheringdata.length);
				datatransferobj.receive(responseofdatagather);
				byte[] receivingdataresponse = responseofdatagather.getData();
				backmessage = new String(receivingdataresponse);
			} 
			else {
				ByteArrayOutputStream Sgoingoutin = new ByteArrayOutputStream();
				ObjectOutputStream streamofobject = new ObjectOutputStream(Sgoingoutin);
				streamofobject.writeObject(recToTransfer);
				byte[] datastore = Sgoingoutin.toByteArray();

				DatagramPacket requestingfordata = new DatagramPacket(Sgoingoutin.toByteArray(), Sgoingoutin.toByteArray().length,
						ahost, pusedUDP);
				datatransferobj.send(requestingfordata);

				DatagramPacket receivedataf = new DatagramPacket(datastore, datastore.length);
				datatransferobj.receive(receivedataf);
				byte[] receiveddatasave = receivedataf.getData();
				backmessage = new String(receiveddatasave);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return backmessage;
	}
	
	private static StringBuffer convert(byte[] datainbyte) {
		if(datainbyte==null)
		return null;
		StringBuffer strbob=new StringBuffer();
		int index=0;
		while(datainbyte[index] !=0) {
			
			strbob.append((char) datainbyte[index]);
			index++;
		}
		return strbob;
	}

}
