
import java.io.*;
import java.net.*;

public class SlaveBot 
{
    public static void main(String args[])
    {
	try {
        int port_number;
        if(args.length<4)// check whether arguments are as expected or not 
        {
         System.err.println("PortNumber and HostName should be provided for client.");
       	 System.exit(-1);
        }
        
        			String hostname=args[1];// store the host name
        			port_number=Integer.parseInt(args[3]); //store the port_number 
        			Socket slave=new Socket(hostname,port_number); //It attempts to connect to the specified server at the specified port.
        			System.out.println("Slave is connected.");
        	
        
        } catch (Exception ex) {
            
        }
    }
}

