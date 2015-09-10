package com.corejava;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Serialization - case 1 - super class A, subclass B, now, A implements Serializable, then B again need not to be a Serializable
 * 
 * case 2 - B implements Serializable, where as A is NOT, then, while
 * deSerialization process, it will call default constructor of A to set default
 * values of A.
 * 
 * read more on - how default constructor call
 * 
 * why serialVersionUID? it is a long value which will be generated by JVM base,
 * depends on class meta data, if we try to change/tamper any structure of a
 * class then it will get change. here, while deSerialization process, JVM class
 * loader will look into it as a key to load a respective class. if the key not
 * found, then it will load all class loader to load all classes and find the
 * respective one.
 * 
 * -------http://howtodoinjava.com/2014/07/25/how-deserialization-process-happen-in-java------------
 * 
 * To be very precise, this stream of bytes (or say serialized data) has all the
 * information about the instance which was serialized by serialization process.
 * This information includes class�s meta data, type information of instance
 * fields and values of instance fields as well. This same information is needed
 * when object is re-constructed back to a new object instance. While
 * deSerializing an object, the JVM reads its class meta-data from the stream of
 * bytes which specifies whether the class of an object implements either
 * �Serializable� or �Externalizable� interface. 
 * NOTE : Please note that for the deSerialization to happen seamlessly, 
 * the bytecode of a class, whose object is being deSerialized, must be present 
 * within the JVM performing deSerialization. Otherwise, the �ClassNotFoundException� is thrown.
 * 
 * public class SimpleProgram { public static void main(String[] args) {
 * System.out.println("Hello World!"); } }
 * 
 * Byte code:
 * 
 * public class SimpleProgram extends java.lang.Object{ public SimpleProgram();
 * Code: 0: aload_0 1: invokespecial #1; //Method java/lang/Object."":()V 4:
 * return
 * 
 * public static void main(java.lang.String[]); Code: 0: getstatic #2; //Field
 * java/lang/System.out:Ljava/io/PrintStream; 3: ldc #3; //String Hello World!
 * 5: invokevirtual #4; //Method
 * java/io/PrintStream.println:(Ljava/lang/String;)V 8: return }
 * 
 * 
 * It actually invokes the constructor of super most class and in above case it
 * is Object.java. And once the constructor of super most class (i.e. Object in
 * this case) has been called, rest of the code does specific instructions
 * written in code.
 * 
 * Matching to above concept i.e. constructor of super most class, we have
 * similar concept in deserialization. In deserialization process, it is
 * required that all the parent classes of instance should be Serializable; and
 * if any super class in hirarchy is not Serializable then it must have a
 * default constructor. Now it make sense. So, while deserialization the super
 * most class is searched first until any non-serializable class is found. If
 * all super classes are serializable then JVM end up reaching Object class
 * itself and create an instance of Object class first. If in between searching
 * the super classes, any class is found non-serializable then it�s default
 * constructor will be used to allocate an instance in memory.
 * 
 * If any super class of instance to be deSerialized is nonSerializable and
 * also does not have a default constructor then the �NotSerializableException�
 * is thrown by JVM. Also, before continuing with the object reconstruction, the
 * JVM checks to see if the serialVersionUID mentioned in the byte stream
 * matches the serialVersionUID of the class of that object. If it does not
 * match then the 'InvalidClassException' is thrown.
 * 
 * So till now we got the instance located in memory using one of superclass�s
 * default constructor. Note that after this no constructor will be called for
 * any class. After executing super class constructor, JVM read the byte stream
 * and use instance�s meta data to set type information and other meta
 * information of instance.
 * 
 * After the blank instance is created, JVM first set it�s static fields and
 * then invokes the default readObject() method, if it�s not overridden,
 * otherwise overridden method will be called. internally which is responsible
 * for setting the values from byte stream to blank instance.
 * 
 * 
 * After the readObject() method is completed, the deserialization process is
 * done and you are ready to work with new deSerialized instance.
 * 
 * @author Thanooj Kalathuru
 *
 */
class SerializableIns /*implements Serializable*/ {

	private static final long serialVersionUID = 3553360521727114582L;
	
	protected int id;
	protected String name;

	public SerializableIns() {
		System.out.println("inside superClass SerializableIns");
	}

	public int getId() {
		return id;
	}

	public SerializableIns(int id, String name) {
		super();
		System.out.println("SerializableIns.SerializableIns(int id, String name)");
		this.id = id;
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SerializableIns [id=" + id + ", name=" + name + "]";
	}

}

class Addr implements Serializable{

	private static final long serialVersionUID = 2284031518816266320L;

	private Integer dno;
	private String streetName;
	private String city;
	
	
	public Addr() {
	}
	
	public Addr(Integer dno, String streetName, String city) {
		super();
		this.dno = dno;
		this.streetName = streetName;
		this.city = city;
	}

	public Integer getDno() {
		return dno;
	}

	public void setDno(Integer dno) {
		this.dno = dno;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "Addr [dno=" + dno + ", streetName=" + streetName + ", city=" + city + "]";
	}
}

public class SerializableEmp extends SerializableIns implements Serializable {

	private static final long serialVersionUID = -7571902186047402412L;
	
	private Integer eId;
	
	private Addr addr;
	/**
	 * 'transient' field will NOT get participated in both SE and DE-SE, including field name and value
	 */
	private transient String eName;
	/**
	 *  'transient static final' field will NOT get participated in SE-DE, but we will get back the values , as they are class level fields. 
	 */
	private transient static final String sfeName = "sfeName";

	public SerializableEmp() {
		super(123, "ram");
		System.out.println("SerializableEmp subClass  of SerializableIns  default constructor");
	}
	public SerializableEmp(Integer eId, String eName, Addr addr) {
		super(eId, eName);
		System.out.println("SerializableEmp subClass  of SerializableIns - SerializableEmp(String eId, String eName)");
		this.eId = eId;
		this.eName = eName;
		this.addr = addr;
	}

	public void seteId(Integer eId) {
		this.eId = eId;
	}

	public String geteName() {
		return eName;
	}

	public void seteName(String eName) {
		this.eName = eName;
	}

	public Integer geteId() {
		return eId;
	}

	public static String getSfename() {
		return sfeName;
	}
 
	@Override
	public String toString() {
		return "SerializableEmp [eId=" + eId + ", addr=" + addr + ", eName=" + eName + "]";
	}
	
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		getSerialized();
		getDeSerialized();
	}
	
	public Addr getAddr() {
		return addr;
	}
	public void setAddr(Addr addr) {
		this.addr = addr;
	}

	private static void getSerialized() throws IOException {
		SerializableIns emp = new SerializableEmp(1, "sriram", new Addr(35, "BTM", "Bangalore"));
		System.out.println("Before Serializable :: "+emp);
		FileOutputStream faos = new FileOutputStream("/Emp.ser");
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(faos);
		objectOutputStream.writeObject(emp);
		faos.close();
		objectOutputStream.close();
	}
	
	private static void getDeSerialized() throws IOException, ClassNotFoundException {
		FileInputStream fais = new FileInputStream("/Emp.ser");
		ObjectInputStream objectInputStream = new ObjectInputStream(fais);
		SerializableEmp emp = (SerializableEmp) objectInputStream.readObject();
		System.out.println("After deSerializable :: "+emp);
		fais.close();
	}

}
