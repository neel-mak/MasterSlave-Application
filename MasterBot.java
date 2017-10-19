import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.text.*;

class socketConnection
{
	String 	regDate;
	String 	targetHost;
	String 	targetName;
	int	targetPort;
	Socket 	targetSock;
	socketConnection()
	{
		Date date = new Date();
		SimpleDateFormat dm = new SimpleDateFormat("yyyy-MM-dd");
		String format = dm.format(date);
		regDate=format;
	}
}

class SocketThread extends Thread{
	  int PORTNUMBER;
	  socketConnection socketList;
	  SocketThread(int port)
	  {
	    PORTNUMBER = port;
	  }
	  public void run()
	  {
		  try
		  {
		   	ServerSocket ss =  new ServerSocket(PORTNUMBER);
		    while(true)
		    {
		    	Socket clientSocket = ss.accept();
		    	socketList=new socketConnection();
		    	socketList.targetHost=clientSocket.getInetAddress().getHostAddress();
		    	socketList.targetName=clientSocket.getInetAddress().getHostName();
			   	socketList.targetPort=clientSocket.getPort();
				socketList.targetSock=clientSocket;
			    MasterBot.connectionList.add(socketList);
		    }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
	  }
}

public class MasterBot {
  public static String Cmd;
  public static ArrayList<socketConnection> connectionList;
  public static void main(String[] args) throws Exception
  {
    connectionList = new ArrayList<socketConnection>();
	if (args.length<2)
	{
		System.err.println("Please provide legal arguments.");
		System.exit(-1);
	}
	try
	{
		String port="";
		if (args[0].equals("-p"))
		{
			port=args[1];
		}
		else
		{
			System.err.println("Port number is compulsory for server.");
			System.exit(-1);
		}
		SocketThread st = new SocketThread(Integer.parseInt(port));
		st.start();
		while(true)
		{
			System.out.print(">");
			BufferedReader Input = new BufferedReader(new InputStreamReader(System.in));
			Cmd=Input.readLine();
			if(Cmd.equals("list"))
			{
				for(int i=0;i<connectionList.size(); i++)
				{
  					System.out.println(connectionList.get(i).targetName+" "+connectionList.get(i).targetHost + " "+ connectionList.get(i).targetPort + " "+ connectionList.get(i).regDate);
				}
			}
			String[] array = Cmd.split(" ");
			if(array[0].equals("connect"))
			{
				if(array.length<4)
				{
					
					System.out.println("connect requires atleast 3 arguments");
					System.exit(-1);
				}
				else
				{
					int numConn;
					if(array.length>4)
					{
						numConn= Integer.parseInt(array[4]);
					}
					else
						numConn=1;
					Iterator<socketConnection> i = connectionList.iterator();
					if(array[1].equals("all"))
					{
						while(i.hasNext())
						{
							socketConnection sc=i.next();
							if(sc.targetHost.equals(array[1]))
							{
								try
								{
							   	 	PrintWriter pw = new PrintWriter(new OutputStreamWriter(sc.targetSock.getOutputStream()));
							   		pw.println("connect "+ array[2] + " "+ array[3] + " "+numConn +" "+ array[5]);
		  							pw.flush();
								}
						    	catch (Exception e)
						    	{
						    		e.printStackTrace();
								}
							 }
						}
					}
					else if(array[1]!="all")
					{
						while(i.hasNext())
						{
							socketConnection sc=i.next();
							if(sc.targetName.equals(array[1]))
							{
								try
								{
									PrintWriter pw = new PrintWriter(new OutputStreamWriter(sc.targetSock.getOutputStream()));
									pw.println("connect "+ array[2] + " "+ array[3] + " "+numConn +" "+ array[5]);
	  								pw.flush();
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
				     }
					 else
					 {
						while (i.hasNext())
						{
						   	socketConnection sc=i.next();
							try
							{
								PrintWriter pw=new PrintWriter(new OutputStreamWriter(sc.targetSock.getOutputStream()));
								pw.println("connect "+ array[2] + " "+ array[3] + " "+numConn +" "+ array[5]);
	  							pw.flush();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					 }
				}
			}
			if(array[0].equals("disconnect"))
			{
				if(array.length<3)
				{
					System.out.println("For disconnect command there should be minimum 2 arguments");
				}
				else
				{
					if(array[1] != null && array[2]!= null)
					{
						int disPort=0;
						if(array.length>3)
						{
							disPort= Integer.parseInt(array[3]);
						}
						Iterator<socketConnection> i = connectionList.iterator();
						int flag=1;
						if(array[1].equals("all"))
						{
							flag=3;
						}
						else
						{
							flag=2;
						}
						if(flag==1)
						{
							while(i.hasNext())
							{
							   	socketConnection sc=i.next();
								if(sc.targetHost.equals(array[1]))
								{
									try
									{
										PrintWriter pw=new PrintWriter(new OutputStreamWriter(sc.targetSock.getOutputStream()));
										pw.println("disconnect "+ array[2] + " "+ disPort);
										pw.flush();
									}
									catch(Exception e)
									{
										System.err.println("Error connecting while sending command to socket with "+ sc.targetHost+" " + sc.targetPort);
									}
								}
							}
						}
						if(flag==2)
						{
							while(i.hasNext())
							{
								socketConnection sc=i.next();
								if(sc.targetName.equals(array[1]))
								{
									try
									{
										PrintWriter pw=new PrintWriter(new OutputStreamWriter(sc.targetSock.getOutputStream()));
										pw.println("disconnect "+ array[2] + " "+ disPort);
										pw.flush();
									}
									catch(Exception e)
									{
										System.err.println("Error connecting while sending command to socket with "+ sc.targetHost+" " + sc.targetPort);
									}
								}
							}
						}
						if(flag==3)
						{
							while(i.hasNext())
							{
							   	socketConnection sc=i.next();
								try
								{
									PrintWriter pw=new PrintWriter(new OutputStreamWriter(sc.targetSock.getOutputStream()));
									pw.println("disconnect "+ array[2] + " "+ disPort);
  									pw.flush();
								}
								catch(Exception e)
								{
									System.err.println("Error connecting while sending command to socket with "+ sc.targetHost+" " + sc.targetPort);
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
			}
		}
	}
	catch (Exception e)
	{
      	e.printStackTrace();
      	System.exit(-1);
	}
  }
}
