

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerClass implements Runnable
{
	public ServerSocket server;
	private int counter=0 ;
        private DataInputStream dis;//to rcv the client id
	private Socket connection;
        public File myfile;
        public jframeServer serverframe;
        private int port;       
        public int currentClientId;
        
	public ServerClass(jframeServer s,int p) 
        {
            this.serverframe=s;
            //this.counter =0;
            this.port=p;
	}
	public void run(){
            //System.out.println("into the server thread");
	    runServer();
	}
	public void runServer()
	{
            try{
                server = new ServerSocket(port, 100);
		while (true) {
                    try {
                        waitForConnection();
                    } catch (Exception eofException) {
                        displayMessage("\nServer terminated connection");
                    } finally {
                        
			counter++;
                    }
		}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
            System.out.println("server closed\n");
	}

	private void waitForConnection() throws IOException {
		displayMessage("Waiting for connection\n");
		
                    
                    connection = server.accept();          
                   // this.currentClientId=getClientId();                   
                    Thread threadHandler = new Thread(new ThreadHandler(connection,this));
                    threadHandler.start();
                    displayMessage("Connection " + (counter++) + " received from: "
					+ connection.getInetAddress().getHostName() + "\n");
                   
	}

	public void displayMessage(String messageToDisplay) {
            this.serverframe.SetFrameMessage(messageToDisplay);
            
	}       
        
       /* public int getClientId() throws IOException
        {
            dis=new DataInputStream(connection.getInputStream());
            Integer i=new Integer(dis.read());
            int value=i.intValue();
            System.out.println("The Received Client Id is "+value+"\n");
            return value;
        }*/
}
