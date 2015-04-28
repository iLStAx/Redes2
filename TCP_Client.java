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

  public TCP_Client(int control, int data, Socket socket) {
    controlPort = control;
    dataPort = data;
    clientSocket = socket;
  }


  public void send(String s,Socket socket) throws Exception 
  { 
    DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
    outToServer.writeBytes(s);
  }

  public String receive(Socket socket) throws Exception 
  {   
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader (socket.getInputStream()));
      String res = inFromServer.readLine(); // if connection closes on server end, this throws java.net.SocketException 
      return res;
  }
  
   public static void main(String args[])throws Exception 
  {
    int controlPort = 2121;
    int dataPort = 2020;
    Socket clientSocket = new Socket("127.0.0.1",controlPort);
    TCP_Client client = new TCP_Client(controlPort,dataPort,clientSocket);
    String fromServer;
    // BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
    // DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    // BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    String msg,input;
    String params[];
    while(true)
    {
      input =  System.console().readLine("> ");
      params = input.split(" ");
      client.send(input+'\n',clientSocket);
      // outToServer.writeBytes(input + '\n');
      fromServer = client.receive(clientSocket);
      System.out.println("FROM SERVER: " + fromServer);
      if(params[0].equals("open"))
      {  
          System.out.println(params[1]);
      }
      else if(fromServer.equals("quit"))
      {
        clientSocket.close();
        client.clientSocket.close();
        break;
      }

    }
    // do
    // {   
    //   input =  System.console().readLine("> ");
    //   String params[] = input.split(" ");
    //   if (params[0].equals("open"))
    //   {
    //     client.send(input);
    //   }
    //   if((msg = client.receive(clientSocket)) !=null)
    //   {
    //     msg = client.receive(clientSocket);
    //     System.console().readLine("> " + msg);    
    //   }


    // }while(!input.equals("quit"));
    // clientSocket.close ();
        
  }

}

