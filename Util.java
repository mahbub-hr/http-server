/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tanvir
 */
public class Util {
    
    
    String postfile;
     PrintWriter pr;
     OutputStream os;
     PrintWriter logwriter;
    Util(String postfile,PrintWriter pr,OutputStream os,PrintWriter logwriter)
    {
        this.postfile=postfile;
        this.pr=pr;
        this.os=os;
        this.logwriter=logwriter;
    }
    String getHardware(String info)
    {
        if(info.contains("mobile"))
        {
            return "Mobile";
        }
        else if(info.contains("tablet"))
        {
            
            return "Tablet";
            
        }
        else
        {
            return "computer";
        }
        
        
      
    }
    String getBrowser(String info)
    {
        if(info.contains("opera")||info.contains("opr"))
        {
            
            return "Opera Mini";
        }
        else if(info.contains("edg"))
        {
            return "Mecrosoft Edge";
        }
        else if(info.contains("samsungbrowser"))
        {
            return "SamsungBrowser";
        }
        else if(info.contains("ucbrowser"))
        {
            return "Ucbrowser";
        }
        else if(info.contains("chrome"))
        {
            return "Chrome";
        }
        else if(info.contains("firefox"))
        {
            return "Firefox";
        }
        
        
        else{
            
            return "Unknown";
        }
        
        
    }
    String getOs(String info)
    {
        if(info.contains("android"))
        {
            return "Android";
        }
       else if(info.contains("iphone"))
        {
            return"iphone";
        }
        else if(info.contains("ipad"))
        {
            return"ipad";
        }
        else if(info.contains("windows"))
        {
            
            
            return "Windows";
        }
        
        else if(info.contains("macintosh"))
        {
            return "Mac Os X";
        }
        else if(info.contains("x11"))
        {
            
            return "Linux";
        }
       
        else
        {
            return "Unknown";
        }
    }
   
    String getContenttype(File file)
    {
        String filename=file.getName();
        String type=filename.substring(filename.lastIndexOf(".")+1);
        
        if(type!=null)
        {
            switch(type){
                
                case "html":
                    return "text/html";
                case "htm":
                    return "text/html";
                case "jpg":
                    return "image/pjpeg";
                case "jpeg":
                 return "image/jpeg";
                case "png":
                  return "image/png";
                case "gif":
                  return "image/gif";
                case "pdf":
                  return "application/pdf";
                  case "tif":
                  return "image/tiff";
                  case "tiff":
                  return "image/tiff";
                case "mp4":
                  return "video/mp4";
                default:
                      return "text/plain";
                
            }
            
        }
           System.out.println("");
            return "text/plain";
      
        
    }
    String getString(String postdata)
    {
        String data ="";
        int i=0,len=postdata.length();
        String hex="";
        
        while(i<len)
        {
            hex="";
            if(postdata.charAt(i)=='%')
            {
                hex+=postdata.charAt(i+1);
                hex+=postdata.charAt(i+2);
                //System.out.println("---------->"+hex);
               data+="&#"+String.valueOf(Integer.parseInt(hex, 16))+";";
                i=i+2;
            }
            else if(postdata.charAt(i)=='+')
            {
                data+="&nbsp;";
            }
            else
            {
                data+=postdata.charAt(i);
            }
          
            
            i++;
        }
        
        return data;
    }
    void sendpostdata(String postdata) throws IOException
    {
        String str;
        File f=new File(postfile);
        BufferedReader lbr = new BufferedReader(new FileReader(f));
        while((str=lbr.readLine())!=null)
        {
            if(str.contains("HTTP REQUEST TYPE->"))
            {
                pr.println(str.replace("</h2>","POST METHOD")+"</h2>");
                 
            }
            else if(str.contains("Post->"))
            {
                 pr.println(str.replace("</h2>", postdata)+"</h2>");
                // System.out.println("\n"+str+postdata);
            }
            else{
            pr.println(str);
            }
            
        }
        pr.flush();
        
    }
    void sendgetdata(File file) throws IOException
    {
       
            String str;
            BufferedReader lbr = null;
          
                lbr = new BufferedReader(new FileReader(file));
           
            while((str=lbr.readLine())!=null)
            {
                if(str.contains("HTTP REQUEST TYPE->"))
                {
                    pr.println(str.replace("</h2>", "GET METHOD")+"</h2>");
                    //System.out.println("\n"+str);
                }
                else{
                    pr.println(str);
                }
                
            }
            pr.flush();
            lbr.close();
     
        
    }
   
    void sendfile(File file,String status,String postdata)
    {
        if(!postdata.isEmpty())
         { 
                pr.println(status);
	        pr.println("Server: Java HTTP ");
                pr.println("Date: " + new Date());
                pr.println("Content-type: " + "text/html");
         
                pr.println(); // blank line between headers and content, very important !
                pr.flush(); // flus
            try {
                if(postdata.trim().endsWith("="))
                {
                    sendpostdata("");
                }
                else{
                    sendpostdata( getString(postdata.split("=")[1]));
                }
            } catch (IOException ex) {
                Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
             
             return;
         }
        else if(file.getName().trim().equalsIgnoreCase(postfile))
        {
               // System.out.println("---------------hhhhhhhhhhhhhhh--------");
                pr.println(status);
	        pr.println("Server: Java HTTP ");
                pr.println("Date: " + new Date());
                pr.println("Content-type: " + "text/html");
         
                pr.println(); // blank line between headers and content, very important !
                pr.flush(); // flus
            try {
                sendgetdata(file);
            } catch (IOException ex) {
                Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
                return;
            
        }
       // System.out.println("kkk1");
        FileInputStream fis;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
                                                           
                                                            
         byte[] contents;
         long fileLength = file.length();
                                                            
         String contenttype=getContenttype(file);
         pr.println(status);
        
	 pr.println("Server: Java HTTP ");
         pr.println("Date: " + new Date());
         pr.println("Content-type: " + contenttype);
         
	 pr.println("Content-length: " + fileLength);
	 pr.println(); // blank line between headers and content, very important !
         pr.flush(); // flus
         long current = 0;
                                                            
                                                            //long start = System.nanoTime();
         while(current!=fileLength){
            try {
                int size = 10000;
                if(fileLength - current >= size)
                    current += size;
                else{
                    size = (int)(fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                bis.read(contents, 0, size);
                os.write(contents);
                //System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
            } catch (IOException ex) {
                Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
           }
        try {
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("File sent successfully!");
        
    }

  
}
