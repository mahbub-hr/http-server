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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tanvir
 */
public class HttpServer {

    static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
       
        
        String logfilename="log.text";
        File logfile=new File(logfilename);
        if(!logfile.exists())
        {
            logfile.createNewFile();
            
        }
        OutputStream logos=new FileOutputStream(logfile);
        PrintWriter logwriter=new PrintWriter(logos);
        logwriter.println("Time                          Client IP        METHOD  HTTP VERSION         REQUESTED FILE           FOUND ?        STATUS              BROWSER            OS        Hardware");
        logwriter.println();
        logwriter.println();
        logwriter.flush();
        ServerSocket serverConnect = new ServerSocket(PORT);
       System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
        
        while(true)
        {
            Socket s=serverConnect.accept();
            System.out.println("Start");
            WorkerThread wt=new WorkerThread(s,logwriter);
            Thread t=new Thread(wt);
            t.start();
          //  System.out.println("hhh");
        }
      
        
    }
    
    
}
class WorkerThread implements Runnable
{
    Socket s;
    BufferedReader in;
    PrintWriter pr;
    OutputStream os;
    String postfile="h.html";
    PrintWriter logwriter; //   to write log file
    Util ob;
    WorkerThread(Socket s,PrintWriter logwriter)
    {
        this.logwriter=logwriter;
        try {
            this.s=s;
            os=s.getOutputStream();
            pr = new PrintWriter(os);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
        } catch (IOException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        ob=new Util( postfile, pr, os, logwriter);
    }
    
   
    @Override
    public void run() {
        String status200="200 OK";
        String status404="404 File Not Found";
        String httpversion="";
        String ops="";
        String browser="";
        String hardware="";
        try {
           
          
            
            String input=in.readLine();
            String input1,nstr;
            String filename;
            File file;
           
            if(input!=null){
            if(input.trim().contains("GET"))/////////// 200 ok found
            {
               // System.out.println(input);
                input1=input.substring(5,input.length()-8).trim();
                httpversion=input.substring(input.length()-8,input.length());
               // System.out.println(httpversion);
                while((nstr=in.readLine())!=null)
                {
                   // System.out.println(nstr);
                    if(nstr.length()==0)
                    {
                        break;
                    }
                    else if(nstr.contains("User-Agent:"))
                    {
                        ops=ob.getOs(nstr.toLowerCase());
                        browser=ob.getBrowser(nstr.toLowerCase());
                        hardware=ob.getHardware(nstr.toLowerCase());
                    }
                }
                if(input1.isEmpty())
                {
                    filename="index.html";
                }
                else
                {
                    filename=input1.trim();
                }
               // System.out.println("----->>>"+input1);
                file = new File(filename.replaceAll("%20"," "));
                
                if(file.exists())
                {
                      if(s.getInetAddress().toString().contains("/0:0:0:0:0:0:0:1"))
                      {
                          logwriter.println(new Date()+"  "+s.getInetAddress()+"   GET"+"     "+httpversion+"             "+file.getName()+"                  "+"Yes"+"             200"+"                "+browser+"         "+ops+"     "+hardware);
                    
                      }
                      else
                       logwriter.println(new Date()+"  "+s.getInetAddress()+"     GET"+"     "+httpversion+"             "+file.getName()+"                 "+"Yes"+"             200"+"                "+browser+"         "+ops+"     "+hardware);
                    
                    
                    logwriter.flush();
                    ob.sendfile(file,httpversion+" "+status200,"");
                   
                    
                }
                else  /////////////////////////////// 404 not found
                {
                    if(file.getName().equalsIgnoreCase("favicon.ico")==false)
                    {
                    if(s.getInetAddress().toString().contains("/0:0:0:0:0:0:0:1"))
                      {
                          logwriter.println(new Date()+"      "+s.getInetAddress()+" GET"+"     "+httpversion+"             "+file.getName()+"               "+"NOT"+"             404"+"                "+browser+"         "+ops+"     "+hardware);
                    
                      }
                      else
                   logwriter.println(new Date()+"  "+s.getInetAddress()+"     GET"+"     "+httpversion+"             "+file.getName()+"               "+"   NOT"+"             404"+"                "+browser+"         "+ops+"     "+hardware);
                    
                   logwriter.flush();
                   filename="notfound.html";
                   file=new File(filename); 
                  ob.sendfile(file,httpversion+" "+status404,"");
                    }
                }
                
                
            }
            else if(input.trim().contains("POST"))
            {
                String str;
                int contentlength = 0;
                //System.out.println(input);
                input1=input.substring(6,input.length()-8);
               httpversion=input.substring(input.length()-8,input.length());
               //System.out.println(httpversion);
                 while((str=in.readLine()).length()!=0&&str!=null)
                  {                      
                           // System.out.println(str); 
                            if(str.contains("Content-Length"))
                             {
                               contentlength=Integer.parseInt(str.substring("Content-Length: ".length()));
                               //System.out.println(contentlength);
                             }
                             else if(str.contains("User-Agent:"))
                             {
                                ops=ob.getOs(str.toLowerCase());
                               browser=ob.getBrowser(str.toLowerCase());
                                hardware=ob.getHardware(str.toLowerCase());
                             }
                  }
                StringBuilder sb=new StringBuilder();
                int i=0;
                while(i<contentlength)
                 {
                    sb.append((char)in.read());                                         
                    i++;
                  }
                //System.out.println(sb.toString());
                if(s.getInetAddress().toString().contains("/0:0:0:0:0:0:0:1"))
                      {
                            logwriter.println(new Date()+"      "+s.getInetAddress()+" POST"+"    "+httpversion+"                                                                   "+browser+"          "+ops+"     "+hardware);    
               
                      }
                      else
                logwriter.println(new Date()+"  "+s.getInetAddress()+"    POST"+"    "+httpversion+"                                                                     "+browser+"          "+ops+"     "+hardware);    
                   logwriter.flush();
                ob.sendfile(new File(input1),httpversion+" "+status200,sb.toString());
            }
            else{
                
                //System.out.println("\n\n\n................None...................\n\n\n");
            }
            }
              
        } catch (IOException ex) {
             
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        try {  
            s.close();
            System.out.println("Finish");
        } catch (IOException ex) {
            Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
           
        
        
    }
    
    
    
    
}
