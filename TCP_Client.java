import java.io.*;
import java.net.*;
import java.util.*;


class TCP_Client
{
  Boolean conected;
  String ipServer;
  InetAddress serverAdd;
  Socket clientSocket;
  int serverPort;
  int dataPort;
  int buffer_size;
  String currentDir;

  public TCP_Client(int control, int data) {
    serverPort = control;
    dataPort = data;
    currentDir = ".";
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
      String input =  System.console().readLine("> User : ");
      send("USER "+input+"\n");        

      fromServer = receive();
      if (fromServer.equals("331")) {
        pass =  System.console().readPassword("> Password : ");
        send("PASS "+String.valueOf(pass)+"\n");
        fromServer = receive();
        if (fromServer.equals("230")) {
          System.out.println("230 Login OK");
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

    send("CWD "+dir+"\n");
    String ans = receive();
    if (ans.split(" ")[0].equals("250")) {
      currentDir = ans.split(" ")[1];
      System.out.println("250 "+ ans.split(" ")[1] + " is the new work directory.");
    }
    else {
      System.out.println(dir + " not found.");
    }

  }
  public void ls() throws Exception {

    send("LIST\n");
    String ans = receive();    
    System.out.println("150 Show List From " + ans);
    ans = receive();
    ans = ans.replace("saltodelinea","\n");
    ans = ans.substring(0,ans.length()-1);
    System.out.println(ans+"\b\b\b"); 
    System.out.println("226 List Complete"); 
  }

  public void quit() throws Exception {
      System.out.println("TCP Connection Close.");
      send("QUIT\n");
      String res = receive();
      if(res.equals("quit"))
        clientSocket.close(); 
  }


  public void get(String fname) throws Exception{
    
        send("RETR "+fname+"\n");
        
          DataInputStream transferIn = null;
          File file = null;
          DataOutputStream fileOut = null;
          try{
            transferIn = new DataInputStream(clientSocket.getInputStream());
            file = new File(fname);
            fileOut = new DataOutputStream(new FileOutputStream(file));
          }
          catch(Exception e){
          }
          try{
            byte b = 0;
            while((b = transferIn.readByte()) != -1)
            {
              fileOut.write(b);
            }
            transferIn.close();
            fileOut.close();
          }
          catch(Exception e){
          } 

  }

  public void put(String fileName) throws Exception
  { 
    send("STOR "+fileName+"\n"); 
    DataOutputStream transferOut = null;
    File file = null;
    DataInputStream fileIn = null;
    try{
      file = new File(currentDir+"/"+fileName);
      fileIn = new DataInputStream(new FileInputStream(file));
    }
    catch(IOException e){
      
    }
    try{
      transferOut = new DataOutputStream(clientSocket.getOutputStream());
      byte b = 0;
      while((b = fileIn.readByte()) != -1)
      {
        transferOut.write(b);
      }

      fileIn.close();
      transferOut.close();
    }
     catch(IOException e){
      
    }
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
    
  
    // File myFile = new File(currentDir+"/"+fileName);
    // FileInputStream fis = null;
    // OutputStream os = null;
    // while (true) 
    // {
    //     try {
    //     byte[] mybytearray = new byte[1024];
    //     fis = new FileInputStream(myFile);
    //     os = clientSocket.getOutputStream();

    //     int count;
    //     while ((count = fis.read(mybytearray)) >= 0) {
    //         os.write(mybytearray, 0, count);

    //     }
    //     System.out.println("Sending " + fileName + "(" + Integer.toString((int)myFile.length()) + " bytes)");
    //     os.flush();
    //     break;
    //     } finally{
    //       fis.close();
    //     }

    // }




  public static void main(String args[])throws Exception 
  {
    int serverPort = 2121;
    int dataPort = 2020;
    TCP_Client client = new TCP_Client(serverPort,dataPort);
    String fromServer;
    String msg,input;
    String params[];
    while(true)
    {
      input =  System.console().readLine("> ");
      params = input.split(" ");
  
      if(params[0].equals("open"))
      { 
        client.open(params[1],serverPort);
      }
      else if (params[0].equals("cd")) 
      {
        client.cd(params[1]);
        continue;
      }
      else if (params[0].equals("ls")) 
      {
        client.ls();
        continue;
      } 
      else if(params[0].equals("put"))
      { 
        client.put(params[1]);
          continue;
      }
      else if(params[0].equals("get"))
      { 
        client.get(params[1]);
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


