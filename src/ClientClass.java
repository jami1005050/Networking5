

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;



@SuppressWarnings("serial")
public class ClientClass  implements Runnable
{
	private DataInputStream dIS;
        private DataOutputStream dOS;//for sending clientId
	public String serverIp;
	public Socket clientSocket;
        //public String filedirname;
	Socket sock = null;
        public JFrameClient clientFrame;
        private int port;
        private int currentClientId ; 
        public boolean  boolConnected=false;
        public boolean fileSendStatus=false;
        //public FileSenderClass fileSender;
        public  File  myfile;
        public File [] fileAry;
       // public FileInputStream fis=null;
        public ObjectOutputStream oos=null;
        public ObjectInputStream ois=null;
        public boolean  acknowledge=false;
        
        
        //constructor
	public ClientClass(JFrameClient jfc,String iP,int pNo,int cId) //JFrameClient Constructore silo
        {
            clientFrame=jfc;
            this.serverIp = iP;
            this.port=pNo;
            this.currentClientId=cId;//getting client id from frame
            connectToServer();
            
	}	
	public void run(){
            //runClient();
                
           
	}
	public void runClient() {
        while (true) {
            try {
                String s = (String) ois.readObject();
                System.out.println("ackk is " + s);
                displayMessage("ackk is " + s);
                        //System.out.println("checking readObject from runClient\n");
                //displayMessage("checking readObject from runClient\n");
                if (s.equals("true")) {
                    acknowledge = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientClass.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
        public String [] ext;
        public  String [] fromTO;
        public int filesize;
        public String [] fileNameAry;
	private void connectToServer() {
            try {
                displayMessage("Attempting connection\n");
                clientSocket = new Socket(serverIp, port);
                getStream();
                oos.writeInt(currentClientId);
                oos.flush();
                displayMessage("Connected to" + serverIp);
                ext=(String [])ois.readObject();
                fromTO=(String [])ois.readObject();
                
                filesize=ois.readInt();
                fileNameAry=(String [])ois.readObject();
                for(String s:fromTO)if(s!=null)System.out.println("Got:"+s);
                oos.writeInt(1);
                oos.flush();
                this.boolConnected=true;
                //sendClientIdToServer();
            } catch (IOException ex) {
                Logger.getLogger(ClientClass.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
        /*public void sendClientIdToServer() throws IOException
        {
            dOS=new DataOutputStream(clientSocket.getOutputStream());
            dOS.write(this.currentClientId);
            this.displayMessage("Client Id sending is Successfull To server");
            displayMessage("id sending successful to server from sendClientIdToServer()\n");
            System.out.println("id sending successful to server from sendClientIdToServer()\n");
        }*/
	public void displayMessage(final String messagetoDisplay) {
           
            clientFrame.SetFrameMessage(messagetoDisplay);
            //System.out.println(messagetoDisplay);
	}
        public void getStream() 
        {
            try {
                System.out.println("Getting Socket Streams");
                //this.myfile=new File(this.filedirname);
                oos=new ObjectOutputStream(clientSocket.getOutputStream());
                ois=new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ClientClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //for sending file by segment
        
        public void sendFile(File  file) throws IOException
        {
            if(!checkFormat(file.getName(),ext)){
                JOptionPane.showMessageDialog(null, "file format not supported");
                return;
            }
            if(!checkRange(clientFrame.studentId.getText().trim(),fromTO)){
                JOptionPane.showMessageDialog(null, "ID not allowed");
                return;
            }
//            if(file.length()>filesize*1000){
//                JOptionPane.showMessageDialog(null, "File too big");
//                return;
//            }
//            if(!checkRange(file.getName(),fileNameAry)){
//                JOptionPane.showMessageDialog(null, "Name Not Matched");
//                return;
//            }
            oos.writeUTF(file.getName());
            oos.flush();
            System.out.println("Inside SendFIle:"+file.getAbsolutePath());
            FileSegment arr[]= FileSegment.makeSegments(file.getAbsolutePath());
            System.out.println("Segment length:"+arr.length);
            for(int i=0;i<arr.length;i++)
            {
                
                if(i==0)
                {
                    oos.writeObject(arr[i]);
                    oos.flush();
                    System.out.println("send first segment \n");
                }
                else
                {
                    while(!acknowledge)
                    {
//                        System.out.println("Its A trap try to get out of it\n");
                        break;
                    }
                    oos.writeObject(arr[i]);
                    oos.flush();
                    acknowledge=false;
                    System.out.println("Send "+i+" th segment\n");
                }
            }
            oos.flush();
            oos.writeObject("End"); 
            oos.flush();
            arr=null;
            System.out.println("Done.");
            this.displayMessage("Done :)");
      }
        //public jframeServer.serverConfiguration jsc;
      
        private boolean checkFormat(String fileName,String [] l)
        {
            if(l[0]==null)return true;
            String ext=fileName.substring(fileName.lastIndexOf('.')+1);
            for(String retValue: l)
            {
                if(retValue==null)return false;
                System.out.println("Compairing "+retValue+" with "+ext);
                if(retValue.compareToIgnoreCase(ext)==0)
                {
                    
                    return true;
                
                }
            }
            return false;
        }
        private boolean checkRange(String fileName,String [] l)
        {
            //String ext=fileName.substring(fileName.lastIndexOf('.')+1);
            if(l[0]==null)return true;
            for(String retValue: l)
            {
                if(retValue==null)return false;
                 System.out.println("Compairing "+retValue+" with "+fileName);
                if(retValue.compareToIgnoreCase(fileName)==0)return true;
            }
            return false;
        }
      
}





	