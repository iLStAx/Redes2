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
    clientSocket = serverSocket.accept();
    dir = ".";
  }


  public void open() throws Exception {
    send("220\n");
    String fromClient = receive();
    if (fromClient.equals("admin")) {
      send("331\n");
      fromClient = receive();
      if (fromClient.equals("password")) {
        send("230\n");
      } 
      else {
        send("530\n");
      }
    }
    else {
      send("530\n");
    }
  }
  
  public void cd(String dir) throws Exception {
    if (dir.startsWith("/")) 
    {
      File f = new File(dir);
      if (f.exists() && f.isDirectory()) 
      {
        this.dir = dir;
        send("250\n");
        return;
      }
      send("550\n");
    }
    else {
      File f = new File(this.dir + "/" + dir);
      if (f.exists() && f.isDirectory()) {
        if (this.dir.equals(".")) {
          this.dir = "";
        }
        this.dir = this.dir + dir;
        send("250\n");
        return;
      }
      send("550\n");
    }
  }

  public void ls() throws Exception {
    System.out.println("TCP ls");
    String list = " ";
    String final2 ="";
    File dir = new File(this.dir);
    for (String d : dir.list()) 
    {
      File f = new File(this.dir+"/"+d);
      if (f.isFile()) {
        d = "file   " + d + "&&&";
      }
      else {
        d = "dir    " + d +"&&&";
      } 
      final2 =final2 + d;
    }

    send(final2+"\n");
  }

  
  public void quit() throws Exception {
    System.out.println("TCP Terminando sesión.");
    clientSocket.close();
    serverSocket.close();
  }

  public void send(String s) throws Exception {

    DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
    outToClient.writeBytes(s); 
    outToClient.flush();  

  }

  public String receive() throws Exception 
  { 
    String fromClient;  
    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    fromClient = inFromClient.readLine();
    return fromClient.trim();
  }

  public static void main(String args[])throws Exception 
  { 
    TCP_Server server = new TCP_Server();
    String capitalizedSentence;
    String fromClient;
    String output[];
    while(true)
    { 
      // BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      // DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
      // fromClient = inFromClient.readLine();
      fromClient = server.receive();
      output = fromClient.split(" ");
      // System.out.println("FROM CLIENT: " + fromClient);

      if(output[0].equals("open"))
      { 
        System.out.println("> Cient : " + fromClient);
        server.open();
      }
      else if(output[0].equals("cd"))
      { 
        System.out.println("> Cient : " + fromClient);
        server.cd(output[1]);
      }
      else if(output[0].equals("ls"))
      { 
        System.out.println("> Cient : " + fromClient);
        server.ls();
      }
      else if(output[0].equals("quit"))
      { 
        server.send("quit");
        server.quit();
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