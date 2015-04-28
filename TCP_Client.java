
import java.io.*;
import java.net.*;
import java.util.*;

class TCP_Client
{
  Boolean conected;
  String ipServer;
  InetAddress serverAdd;
  Socket clientSocket;
  int controlPort;
  int dataPort;

  public TCP_Client(int control, int data) {
    controlPort = control;
    dataPort = data;
  }


  public void open(String ip,int port) throws Exception
  {
    System.out.println("TCP Connected to "+ ip +"...");
    serverAdd = InetAddress.getByName(ip);
    clientSocket = new Socket(ip,port);
    ipServer = ip;
    send("open\n");
    String fromServer = receive();
    char pass[];
    if (fromServer.equals("220")) {
      String input =  System.console().readLine("> Ingrese Usuario : ");
      send(input+"\n");        

      fromServer = receive();
      if (fromServer.equals("331")) {
        pass =  System.console().readPassword("> Ingrese Password : ");
        send(String.valueOf(pass)+"\n");
        fromServer = receive();
        if (fromServer.equals("230")) {
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
      // help(1);
      return;
    }
    send("cd "+dir+"\n");
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
      // help(1);
      System.out.println("en ls no conectado");
      return;
    }
    send("ls\n");
    String ans = receive();
    ans = ans.replace("&&&","\n");
    ans = ans.substring(0,ans.length()-1);
    System.out.println(ans+"\b\b\b");
    
  }

  public void quit() throws Exception {
    System.out.println("TCP Close Connection.");
    clientSocket.close();
  }

  public void send(String s) throws Exception 
  { 
    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    outToServer.writeBytes(s);
    outToServer.flush();
  }

  public String receive() throws Exception 
  {   
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));
      String res = inFromServer.readLine(); // if connection closes on server end, this throws java.net.SocketException 
      return res.trim();
  }
  
   public static void main(String args[])throws Exception 
  {
    int controlPort = 2121;
    int dataPort = 2020;
    Socket clientSocket;
    TCP_Client client = new TCP_Client(controlPort,dataPort);
    String fromServer;
    String msg,input;
    String params[];
    while(true)
    {
      input =  System.console().readLine("> ");
      params = input.split(" ");
      // client.send(input+'\n',client.clientSocket);
      // fromServer = client.receive(client.clientSocket);
      // System.out.println("FROM SERVER: " + fromServer);
      
      if(params[0].equals("open"))
      { 
        client.open(params[1],controlPort);
      }
      else if (params[0].equals("cd")) 
      {
        if (params.length == 1) 
        {
          // client.help(0);
          continue;
        }
        client.cd(params[1]);
        continue;
      }
      else if (params[0].equals("ls")) 
      {
        client.ls();
        continue;
      } 
      else if(params[0].equals("quit"))
      {
        client.quit();
        break;
      }

    }        
  }

}


