import java.io.*;
import java.net.*;

class UDP_Server{

  Boolean conected;
  String clientIp;
  InetAddress clientAdd;
  DatagramSocket serverSocket;
  int clientP;
  int controlP;
  int dataP;
  String dir;

  public UDP_Server () throws Exception 
  {
  	serverSocket = new DatagramSocket(21);
  	dir = ".";
  }

  public void run() throws Exception {
  	while(true) {
  		String answer = receive();
  		if (answer.equals("open")) {
  			open();
  		}
  		else if (answer.equals("ls")) {
  			ls();
  		}
  		else if (answer.equals("cd")) {
  			cd(receive());
  		}
  		else if (answer.equals("get")) {
  			get(receive());
  		}
      else if (answer.equals("put")) {
        System.out.println("put aca");
      }
    }
  }

	public void open() throws Exception {
		send("220");
		String answer = receive();
		if (answer.equals("admin")) {
			send("331");
			answer = receive();
			if (answer.equals("password")) {
				send("230");
			}
			else {
				send("530");
			}
		}
		else {
			send("530");
		}
  }

  public void cd(String dir) throws Exception {
    if (dir.startsWith("/")) {
    	File f = new File(dir);
			if (f.exists() && f.isDirectory()) {
			  this.dir = dir;
			  send("250");
			  return;
			}
			send("550");
    }
    else {
    	File f = new File(this.dir + "/" + dir);
			if (f.exists() && f.isDirectory()) {
				if (this.dir.equals(".")) {
					this.dir = "";
				}
			  this.dir = this.dir + dir;
			  send("250");
			  return;
			}
			send("550");
    }
  }

  public void ls() throws Exception {
    System.out.println("UDP ls");
    String list = " ";
    File dir = new File(this.dir);
    for (String d : dir.list()) {
    	File f = new File(this.dir+"/"+d);
    	if (f.isFile()) {
    		d = "file   " + d;
    	}
    	else {
    		d = "dir    " + d;
    	}
			send(d);
		}
		send("226");
  }

  public void get(String fname) throws Exception {
    System.out.println("UDP get "+ fname +"...");
    send("150");
    sendFile(fname);
  }

  public void put(String fname) throws Exception 
  {
    System.out.println("UDP put "+ fname +"...");
     
  }
  public void quit() throws Exception {
    System.out.println("UDP Closing session.");
  }

  public void send(String s) throws Exception {
    byte[] sendData = new byte[1024];
  	sendData = s.getBytes();
	  DatagramPacket sendPacket =
	  new DatagramPacket(sendData, sendData.length, clientAdd, clientP);
	  serverSocket.send(sendPacket);
    System.out.println(">>" + s);
  }

  public String receive() throws Exception 
  { 
    byte[] receiveData = new byte[1024];
  	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	  serverSocket.receive(receivePacket);
	  clientAdd = receivePacket.getAddress();
	  clientP = receivePacket.getPort();
	  String answer = new String( receivePacket.getData());
    System.out.println("< " + answer);
	  return answer.trim();
  }

  public void sendFile(String fname) throws Exception 
  { 
  	System.out.println(fname);
    File file = new File(fname);
    if (file.isFile()) {
      try {
        DataInputStream diStream = new DataInputStream(new FileInputStream(file));
        long len = (int) file.length();
        byte[] fileBytes = new byte[(int) len];
        int read = 0;
        int numRead = 0;
        while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
          read = read + numRead;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = outputStream.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(fileBytes, fileBytes.length, clientAdd, 20);
        DatagramSocket socket = new DatagramSocket(20);
        socket.send(sendPacket);
        System.out.println("File sent");
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(e);
      }
    } else {
      System.out.println("path specified is not pointing to a file");
    }
  }

  public static void main(String args[])throws Exception 
  {
    UDP_Server server = new UDP_Server();
    server.run();
  }

}