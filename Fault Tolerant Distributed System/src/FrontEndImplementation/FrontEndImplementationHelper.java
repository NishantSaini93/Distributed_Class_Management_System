package FrontEndImplementation;


/**
* FrontEndImplementation/FrontEndImplementationHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from idl
* Thursday, July 26, 2018 5:58:19 o'clock PM EDT
*/

abstract public class FrontEndImplementationHelper
{
  private static String  _id = "IDL:FrontEndImplementation/FrontEndImplementation:1.0";

  public static void insert (org.omg.CORBA.Any a, FrontEndImplementation that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static FrontEndImplementation extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (FrontEndImplementationHelper.id (), "FrontEndImplementation");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static FrontEndImplementation read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_FrontEndImplementationStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, FrontEndImplementation value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static FrontEndImplementation narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof FrontEndImplementation)
      return (FrontEndImplementation)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _FrontEndImplementationStub stub = new _FrontEndImplementationStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static FrontEndImplementation unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof FrontEndImplementation)
      return (FrontEndImplementation)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _FrontEndImplementationStub stub = new _FrontEndImplementationStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}