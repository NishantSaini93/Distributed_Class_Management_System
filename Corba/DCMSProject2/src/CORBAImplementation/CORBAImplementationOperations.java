package CORBAImplementation;


/**
* CORBAImplementation/CORBAImplementationOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from CORBAImplementation.idl
* Friday, June 15, 2018 4:13:15 o'clock PM EDT
*/

public interface CORBAImplementationOperations 
{
  boolean signIn (String mgID);
  void signOut ();
  boolean createTRecord (String managerID, String fName, String lName, String address, String phone, String specialization, String location);
  String getRecordCounts ();
  boolean editRecord (String managerID, String recordID, String fieldName, String newValue);
  boolean createSRecord (String managerID, String fName, String lName, String coursesRegistered, boolean status, String statusDate);
  boolean transferRecord (String managerID, String recordID, String remoteCenterServerName);
  String printRecords ();
} // interface CORBAImplementationOperations