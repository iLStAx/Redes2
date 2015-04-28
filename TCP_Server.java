import java.io.*;
import java.net.*;

class TCP_Server{

  Boolean conected;
  String clientIp;
  InetAddress clientAddress;
  ServerSocket serverSocket;
  Socket clientSocket;
  int clientPort;
  int controlPort;
  int dataPort;
  String dir;


  public TCP_Server () throws Exception 
  {
    serverSocket = new ServerSocket(2121);
    dir = ".";
  }




 
  public void send(String s,Socket socket) throws Exception {

    DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
    outToClient.writeBytes(s);   

  }

  public String receive(Socket socket) throws Exception 
  { 
    String fromClient;  
    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    fromClient = inFromClient.readLine();
    return fromClient;
  }

  public static void main(String args[])throws Exception 
  { 
    TCP_Server server = new TCP_Server();
    Socket clientSocket = server.serverSocket.accept ();
    String capitalizedSentence;
    String fromClient;
    String output[];
    while(true)
    { 
      // BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      // DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
      // fromClient = inFromClient.readLine();
      fromClient = server.receive(clientSocket);
      output = fromClient.split(" ");
      if(output[0].equals("open"))
      {
        server.send("open\n",clientSocket);  
      }
      else if(output[0].equals("quit"))
      { 
        server.send("quit",clientSocket);
        clientSocket.close ();
        server.serverSocket.close ();
        break;
      }
      // System.out.println("Received: " + fromClient);
      // server.send("Chupalo\n",clientSocket);
      // capitalizedSentence = "Chupalo"+'\n';
      // outToClient.writeBytes(capitalizedSentence);
      // fromClient = server.receive(clientSocket);
      // output = fromClient.split(" ");
      // System.out.println("Received: " + fromClient);

      // // fromClient = in.readLine ();
      // if (output[0].equals("open")) 
      // {
      //   capitalizedSentence = "220"+'\n';
      //   server.send("pene",clientSocket);
      //   System.out.println(output[1]);        
      // }
      // else if (output[0].equals("ls")) {
      //   capitalizedSentence = "ls 220"+'\n';
      //   server.send(capitalizedSentence,clientSocket);
      // }
      // else if (output[0].equals("cd")) {
      //   capitalizedSentence = "cd 220"+'\n';
      //   server.send(capitalizedSentence,clientSocket);
      // }
    }
   
  }

}