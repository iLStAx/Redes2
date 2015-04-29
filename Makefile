JCC = javac
JFLAGS = -g

default: Client.class UDP_Server.class UDP_Client.class TCP_Client.class TCP_Server.class TCPClient.class 

Client.class: Client.java
	$(JCC) $(JFLAGS) Client.java

UDP_Server.class: UDP_Server.java
	$(JCC) $(JFLAGS) UDP_Server.java

UDP_Client.class: UDP_Client.java
	$(JCC) $(JFLAGS) UDP_Client.java

TCP_Server.class: TCP_Server.java
	$(JCC) $(JFLAGS) TCP_Server.java

TCP_Client.class: TCP_Client.java
	$(JCC) $(JFLAGS) TCP_Client.java

TCPClient.class: TCPClient.java
	$(JCC) $(JFLAGS) TCPClient.java


clean: 
	$(RM) *.class