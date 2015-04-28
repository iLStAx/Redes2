import java.io.*;
import java.net.*;
import java.util.*;

class UDP_Client
{
  Boolean conected;
  String ipServer;
  InetAddress serverAdd;
  DatagramSocket clientSocket;
  int controlP;
  int dataP;

  public UDP_Client(int control, int data) {
    conected = false;
    controlP = control;
    dataP = data;
  }

  public void open(String ip) throws Exception
  {

    System.out.println("UDP Connected to "+ ip +"...");
    serverAdd = InetAddress.getByName(ip);
    ipServer = ip;
    clientSocket = new DatagramSocket();
    send("open");
    String answer = receive();

    if (answer.equals("220")) {
      String input =  System.console().readLine("Ingrese Usuario > ");
      send(input);
      answer = receive();
      if (answer.equals("331")) {
        input =  System.console().readLine("Ingrese Password > ");
        send(input);
        answer = receive();
        if (answer.equals("230")) {
          System.out.println("Login OK");
          conected = true;
        }
        else {
          System.out.println("Login Error");    
        }
      }
      else {
        System.out.println("Login Error");    
      }
    }
  }
  public void cd(String dir) throws Exception {
    if (!conected) {
      help(1);
      return;
    }
    send("cd");
    send(dir);
    String ans = receive();
    if (ans.equals("250")) {
      System.out.println(dir + " es el nuevo directorio de trabajo.");
    }
    else {
      System.out.println(dir + " no encontrado.");
    }

  }
  public void ls() throws Exception {
    if (!conected) {
      help(1);
      return;
    }
    send("ls");
    String ans = receive();
    while (!ans.equals("226")) {
      System.out.println(ans);
      ans = receive();
    }
  }
  public void get(String fname) throws Exception {
    System.out.println("UDP get "+ fname +"...");
    if (!conected) {
      help(1);
      return;
    }
    send("get");
    send(fname);
    String ans = receive();
    if (ans.equals("150")) {
      receiveFile(fname);
    }
  }
  public void put(String fname) throws Exception {
    System.out.println("UDP put "+ fname +"...");
    send("put");
    // if (!conected) {
    //   help(1);
    //   return;
    // }
    // send("put");
    // String ans = receive();
    // if (ans.equals("150")) {

    // }
    // fname.startsWith("./");
    // byte b[]=new byte[1024];
    // FileInputStream f=new FileInputStream(fname);
    // DatagramSocket dsoc=new DatagramSocket(2121);
    // int i=0;
    // while(f.available()!=0)
    // {
    //             b[i]=(byte)f.read();
    //             i++;
    // }                     
    // f.close();
    // DatagramPacket data = new DatagramPacket(b,i,UDP_ClinipServer,2020);
    // dsoc.send(data);
   
  }

  public void quit() throws Exception {
    System.out.println("UDP Terminando sesión.");
    clientSocket.close();
  }

  public void help(int n) {
    if (n == 0) {
      System.out.println("UDP_Client Options:");
      System.out.println("    - open  <ip address>");
      System.out.println("          Open connection with <ip address>.\n");
      System.out.println("    - cd <dir>");
      System.out.println("          Change dir.\n");
      System.out.println("    - ls");
      System.out.println("          Files to current dir.\n");
      System.out.println("    - get  <file>");
      System.out.println("          Pull <file> from server.\n");
      System.out.println("    - put  <file>");
      System.out.println("          Upload <file> to server.\n");
      System.out.println("    - quit");
      System.out.println("          Close session.\n");
    }
    else if (n == 1) {
      System.out.println("Error 1:");
      System.out.println("First need to open connection.");
    }
  }

  public void send(String s)  throws Exception 
  { 
    byte[] sendData = new byte[1024];
    sendData = s.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAdd, controlP);
    clientSocket.send(sendPacket);
  }

  public String receive()  throws Exception 
  {
    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String answer = new String(receivePacket.getData());
    return answer.trim();
  }

  public void receiveFile(String file) throws Exception 
  {
    file = "rec" + file;
    int b = Integer.parseInt(receive());
    byte[] receiveData = new byte[b];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);

    if (!new File(file).exists()) 
    {
        new File(file).mkdirs();
    }
    File dstFile = new File(file);

    FileOutputStream fileOutputStream = null;
    try 
    {
        fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(receiveData);
        fileOutputStream.flush();
        fileOutputStream.close();
        System.out.println("Output file : " + file + " is successfully saved ");
    }
    catch (FileNotFoundException e) 
    {
      e.printStackTrace();
    } 
    catch (IOException e) 
    {
        e.printStackTrace();
    }
  }

   public static void main(String args[])throws Exception 
  {
    int controlP = 21;
    int dataP = 20;

    UDP_Client client;
    client = new UDP_Client(controlP,dataP);
    while (true) 
    {
      String input =  System.console().readLine("> ");
      String params[] = input.split(" ");
      if (params[0].equals("open"))
      {
        if (params.length == 1) 
        {
          client.help(0);
          continue;
        }
        client.open(params[1]);
      }
      else if (params[0].equals("cd")) 
      {
        if (params.length == 1) 
        {
          client.help(0);
          continue;
        }
        client.cd(params[1]);
      }
      else if (params[0].equals("ls")) 
      {
        client.ls();
        continue;
      } 
      else if (params[0].equals("get")) 
      {
        if (params.length == 1) 
        {
          client.help(0);
          continue;
        }
        client.get(params[1]);
      } 
      else if (params[0].equals("put")) 
      {
        if (params.length == 1) 
        {
          client.help(0);
          continue;
        }
        client.put(params[1]);
      }
      else if (params[0].equals("quit")) 
      {
        client.quit();
        break;
      } 
      else 
      {
        client.help(0);
      }
    }
  }

}

