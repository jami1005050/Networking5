//import ClientPackage.FileSegment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThreadHandler implements Runnable {
        public final static int FILE_SIZE = 6022386;
	private Socket connection = null;
	private int counter = 0;
	ServerClass serverClass;
	public String clientID;
        //for file rcv required variables and buffers 
        private FileOutputStream fos=null;
        private ObjectInputStream ois=null;
        private ObjectOutputStream oos=null;
        private byte [] mybytearray=new byte[FILE_SIZE];
        public File rcvFileName ;
        private int bytesRead;
        private int current=0;
        private ArrayList<FileSegment> fss=null;
       // public serverRcvSegment rcvSegment;        
         
         
	public ThreadHandler(Socket connection, ServerClass server)
        {
            this.connection = connection;
	    serverClass = server;
            try 
            {
                getStream();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Thread Handler started with client " + connection.getPort());
	}

	public void run()
        {
            //System.out.println("Text clientHandler thread started");
            try 
            {
               clientID=String.valueOf(ois.readInt());
               serverClass.serverframe.root_directoryName+="\\"+clientID;
               new File(serverClass.serverframe.root_directoryName).mkdirs();
               oos.writeObject(serverClass.serverframe.fileExtension);
               oos.flush();
               oos.writeObject(serverClass.serverframe.fromTO);
               oos.flush();
               oos.writeInt(serverClass.serverframe.file_size);
               oos.flush();
               oos.writeObject(serverClass.serverframe.fileNamesAry);
               oos.flush();
               if(ois.readInt()==1){
                   System.out.println("Constraints send successful\n");
               }
               
                while(true){
                     fss=new ArrayList<FileSegment>();
                    String fname=ois.readUTF();
                    rcvFile(fname);
                    /*oos.writeInt(1);
                    oos.flush();
                    int i=ois.readInt();*/
                    
                    System.out.println("one file received with ");
                }
            }
            catch (IOException ex)
            {
                Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception ex)
            {
                System.err.println(ex);
            }
            finally
            {
                try 
                {
                    oos .flush();
                    ois.close();
                    oos.close();
                } 
                catch (IOException ex)
                {
                    Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
             }
	}
        public int initial=0;
        public boolean ack=false;
       
        @SuppressWarnings("empty-statement")
        public void rcvFile(String fname) throws IOException
        {
            //rcvFileName= new File (this.serverClass.serverframe.getmakeDir());
            //InputStream is = connection.getInputStream();
            //fos = new FileOutputStream(rcvFileName);
           // System.out.println("inside rcv function\n");
            while(true){
                try {
                    Object object=ois.readObject();
                    if(object instanceof FileSegment)
                    {
                        FileSegment fs=(FileSegment)object;
                        System.out.println(fs.toString());
                        fss.add(fs);
                        oos.writeObject("true");
                        oos.flush();
                        System.out.println("Make Acknowledgement true\n");
                        serverClass.serverframe.SetFrameMessage("Make Acknowledgement true\n");
                    }
                    else if(object instanceof String)
                    {
                        String str=(String)object;
                        rcvFileName= new File (serverClass.serverframe.root_directoryName+"\\"
                                +fname);
                        fos = new FileOutputStream(rcvFileName);
                        if(str.compareTo("End")==0)
                        {
                            for(int i=0;i<fss.size();i++)
                            {
                                fos.write(fss.get(i).data);
                            }
                            fos.close();
                            serverClass.serverframe.SetFrameMessage("File Tranfer Complete");
                            return;
                        }
                    }              
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
       }       
        public void getStream() throws IOException
        {            
            oos = new ObjectOutputStream(this.connection.getOutputStream());
            ois = new ObjectInputStream(this.connection.getInputStream());

        }
}
