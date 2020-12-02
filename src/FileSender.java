
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking5.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jami
 */
public class FileSender implements Runnable
{
    public ClientClass cc;
    public  File [] fary;
    public FileSender(ClientClass c,File [] fAry)
    {
        cc=c;
        fary=fAry;
    }
    
    public void run()
    {
        try {
            for(File myfile:fary)
            {
                cc.sendFile(myfile);
                /*this.cc.ois.readInt();
                this.cc.oos.writeInt(1);
                this.cc.oos.flush();*/
            }
        } catch (IOException ex) {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
