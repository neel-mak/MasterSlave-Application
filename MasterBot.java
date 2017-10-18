import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class MasterBot extends Thread{
	
	private ServerSocket ss; 
MasterBot(int port)
{
	try {
		 ss = new ServerSocket(port);  // Attempts to create a server socket bound to the specified port.
		 
	} catch (IOException e) {
		
		e.printStackTrace();
		System.exit(-1);   // if it throws exception then it will exit from the code 
	}
	
}
public void run(){    // run method for every threads ( multiple slaves)
	while(true)
	{
		try{  
			 Socket st= ss.accept(); /* Waits for an incoming client. 
			 This method blocks until either a client connects to the server on the specified port.*/
			 _list.add(st);   // Add to the list which is maintaining the info about all connected slaves.
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
}

static ArrayList<Socket> _list = new ArrayList<Socket>();  //list for all slaves

public static void main(String args[])
{        
		try {
			 int PORTNUMBER; // port number from user
			 if(args.length<2)// if it is not as "-p port_number"  
			 {
				 System.err.println("-p command with Port number should be provided for MasterBot.");
				 System.exit(-1);
			 } 
			 PORTNUMBER=Integer.parseInt(args[1]);
			 Thread t = new MasterBot(PORTNUMBER); // creates thread
			 t.start();	// start the thread and it will run parallel 
			 while(true)
	         {
				System.out.print(">");
				String line; // to store connect and disconnect cmds
				String[] array;
				String[] dis_array;
				int connection=1; // to store number of connections for target host
				BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
				line = br1.readLine();
				DateFormat df=new SimpleDateFormat("YYYY/MM/dd"); // this is for specified date format
			    Date date=new Date();
			    String regDate=df.format(date);
				if (line.equals("list")) // to display list
				{
					System.out.println();
					System.out.println("HostName          "+"IP Address  "+"PORT "+"Date  ");
		         	for (int i=0; i < _list.size(); i++ )
		         	{
		         		System.out.print(" "+_list.get(i).getRemoteSocketAddress()); // Host Name
		         		System.out.print(" "+_list.get(i).getLocalAddress()); // IP address
		         		System.out.print(" "+_list.get(i).getLocalPort()); // Port
		         		System.out.print(" "+regDate);
		         		System.out.println();
		         	}
				}
				if(line.equals("connect")) // compare the string whether its about connect cmd or not
				{
					array=line.split("\\s+"); // array for verifying the arguments with connect cmd
					if(array.length<4)// not as specified 
					{
						System.out.println("Arguments should be legal to connect with target host.");
						System.exit(-1);
					}
					if(array.length==5) // if no. of connection is already given
					{
						connection=Integer.parseInt(array[4]);
					}
					else
					{
						connection=1;
					}
					int port_=Integer.parseInt(array[3]);
					String host_name=array[1];
					String target=array[2];
					//calling method for making connection to target_host.
					//connection_targethost(host_name,port_,target,connection);
					
				}
				if(line.equals("disconnect"))
				{
					dis_array=line.split("\\s+");
					if(dis_array.length<3)
					{
						System.out.println("Arguments should be legal to disconnect with target host.");
						System.exit(-1);
					}
					if(dis_array.length==4)
					{
						int Port;
						Port=Integer.parseInt(dis_array[3]);
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
