
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jami
 */
public class FileSegment implements Serializable{
    public long startingByte;
    public String fileName;
    public long sizeOfSegment;
    public byte [] data;
    
    public FileSegment(String fn,long sb,long sos,byte[] dt)
    {
        this.data=dt;
        fileName=fn;
        startingByte=sb;
        sizeOfSegment=sos;
    }
    
    public String toString()
    {
        return "File Name:"+fileName+"\nStarting byte: "+startingByte+"\nSize: "+sizeOfSegment+"\n";
    }

    public static FileSegment[] makeSegments(String filePath)
    {
        FileInputStream fis;
        byte[] fullData=new byte[(int)new File(filePath).length()];
        try
        {
            fis = new FileInputStream(new File(filePath));
            fis.read(fullData);
        } 
        catch (Exception ex) 
        {
           ex.printStackTrace();
        }
        int size =fullData.length/512+1;
        FileSegment[] fss=new FileSegment[size];
        for(int i=0;i<size;i++)
        {
            int ffi=i*512;
            
            
            if(i==size-1)
            {    
                int fli=fullData.length;
                byte[] fsData=Arrays.copyOfRange(fullData,ffi,fli);
                fss[i]=new FileSegment(filePath,ffi,fsData.length, fsData);                
                
            }
            else
            {   
                int fli=ffi+512;
                byte[] fsData=Arrays.copyOfRange(fullData,ffi,fli);
                fss[i]=new FileSegment(filePath,ffi,512, fsData); // segment size is give as 512
            }
        }
        return fss;
        
    }
    
    
   /* public static void main(String args[])
    {
        FileSegment arr[]=FileSegment.makeSegments("C:\\Users\\jami\\Desktop\\file.l");
        for(int i=0;i<arr.length;i++)
            System.err.println(arr[i].toString());
    }*/
}
