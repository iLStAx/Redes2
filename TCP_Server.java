import java.io.*;
import java.net.*;
import java.io.ByteArrayOutputStream;

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
  String dirAux;
  int buffer_size;

  public TCP_Server () throws Exception 
  {
    serverSocket = new ServerSocket(2121);
    clientSocket = serverSocket.accept();
    dir = ".";
    dirAux = System.getProperty("user.dir");
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
    String dirAbs[] = dirAux.split("/");
    String aux[] = new String[dirAbs.length-1];
    String dirFinal = "";
    if(dir.equals(".."))
    {
      for(int i =0;i < dirAbs.length-1;i++)
      {
        aux[i] = dirAbs[i];
      }
      for(int i =0;i < dirAbs.length-1;i++)
      { 
        if(i == dirAbs.length-2)
        {
          dirFinal += aux[i];
          dirAux = dirFinal;
        }
        else
        {
          dirFinal += aux[i]+"/";
          dirAux = dirFinal;
        }
      }
    }
    else
    {
      dirAux += "/"+dir;
    }

    if (dir.startsWith("/")) 
    {
      File f = new File(dir);
      if (f.exists() && f.isDirectory()) 
      {
        this.dir = dir;
        send("250 "+dirAux+"\n");
        // send("250 "+dirAbs2[dirAbs2.length-1]+"/"+this.dir+"\n");
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
        this.dir = this.dir  +dir +"/";
        send("250 "+dirAux+"\n");
        // send("250 "+dirAbs2[dirAbs2.length-1]+"/"+this.dir+"\n");
        return;
      }
      send("550\n");
    }
  }

  public void ls() throws Exception {
    String list = " ";
    String final2 ="";
    String dirAbs = System.getProperty("user.dir");
    String dirAbs2[] = dirAbs.split("/");
    String empty = "empty dirr\n";
    File dir = new File(this.dir);
    // System.out.println(dirAbs2[dirAbs2.length-1]);
    send(dirAux+"\n");
    if(dir.list().length > 0)
    {
      for (String d : dir.list()) 
      {
        File f = new File(this.dir+"/"+d);
        if (f.isFile()) {
          d = "file   " + d + "saltodelinea";
        }
        else {
          d = "dir    " + d +"saltodelinea";
        } 
        final2 =final2 + d;
      }
      send(final2+"\n");
    }
    send(empty);
  }

  private void saveFile(String fileName) throws Exception {  
    
    InputStream is = null;
    FileOutputStream fos = null;
    try {
        byte[] mybytearray = new byte[1024];
        is = clientSocket.getInputStream();
        fos = new FileOutputStream("hola/"+fileName);

        int count;
        while ((count = is.read(mybytearray)) >= 0) {
            fos.write(mybytearray, 0, count);
        }
    } finally {
            fos.close();
    }

  
  } 


    

  public Boolean isConnected() throws Exception
  {
      return clientSocket.isConnected();
  }
  
  public void quit() throws Exception {
    System.out.println("TCP Connection Close.");
    send("quit\n");
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
    while(server.clientSocket != null)
    { 
      fromClient = server.receive();
      output = fromClient.split(" ");

      if(output[0].equals("open"))
      { 
        System.out.println("> Client : " + fromClient);
        server.open();
      }
      else if(output[0].equals("cd"))
      { 
        System.out.println("> Client : " + fromClient);
        server.cd(output[1]);
      }
      else if(output[0].equals("ls"))
      { 
        System.out.println("> Client : " + fromClient);
        server.ls();
      }
      else if(output[0].equals("put"))
      { 
        System.out.println("> Client : " + fromClient);
        server.saveFile(output[1]);

      }
      else if(output[0].equals("quit"))
      { 
        System.out.println("> Client : " + fromClient);
        server.quit();
        break;
      }
      
    }
   
  }

}