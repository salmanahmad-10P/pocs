/*
 * Copyright 2005 Wavechain Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.wavechain.utilities;

import	java.net.*;
import	java.io.*;

import	org.apache.log4j.Logger;
import	javax.ejb.SessionBean;
import	javax.ejb.SessionContext;
import	javax.ejb.TimedObject;
import  javax.ejb.Timer;
import  javax.ejb.TimerHandle;
import  javax.ejb.TimerService;

public class SocketUtil implements SessionBean, TimedObject
{
	public static final String NO_ROUTE_TO_HOST_EXCEPTION = "java.net.NoRouteToHostException";
	public static final String UNKNOWN_HOST_EXCEPTION = "java.net.UnknownHostException";
	public static final String CONNECTION_REFUSED = "java.net.ConnectException:ConnectionRefused";
	public static final String OPERATION_TIMED_OUT = "java.net.ConnectException:OperationTimedOut";
	Socket aSocket = null;
	String connectionAddress = null;
	int portId = 0;
	String connectionException = null;
	private Logger log = null;
	private SessionContext sc = null;
	private TimerHandle timerHandle = null;

	public void ejbActivate() {
        }

        public void ejbPassivate() {
        }

        public void ejbRemove() {
        }

        public void ejbCreate()
        {
                log = Logger.getLogger(getClass());
        }

        public void setSessionContext(SessionContext ctx)
        {       sc = ctx;       }
	

	public SocketUtil()
	{
		log = Logger.getLogger("SocketUtil");
	}

	/* The container calls this method after expiration of the timer. Implementation of this
        method is required by the javax.ejb.TimedObject interface */
	public void ejbTimeout(Timer timer)
	{
		try
		{
			aSocket = new Socket(getConnectionAddress(), getPortId());
		}
                catch (java.net.NoRouteToHostException x)
                {
                	log.error("No route to host "+getConnectionAddress());
			setConnectionException(SocketUtil.NO_ROUTE_TO_HOST_EXCEPTION);
                }
                catch(java.net.UnknownHostException x)
                {
                	log.error("UnknownHostException to "+getConnectionAddress());
			setConnectionException(SocketUtil.CONNECTION_REFUSED);
                }
                catch (java.net.ConnectException x)
                {
			log.error("ConnectException to "+getConnectionAddress()+" = "+x);
			setConnectionException(SocketUtil.UNKNOWN_HOST_EXCEPTION);
		}
		catch(Exception x)
		{	x.printStackTrace();	}
	}

	/* Pass a null Socket along with connectionAddress and portId
	returns a String connectionException (null if no exception was thrown)
	socket will be aquired if possible (client can use this via pass by reference)

	9 Sept 05:  JA Bride
	*/
	public String connect(Socket aSocket, String address, int port) throws Exception
	{
		setConnectionAddress(address);
		setPortId(port);
		TimerService tService = sc.getTimerService();
                Timer timer = tService.createTimer(0L, address);

		long beginTime = System.currentTimeMillis();
		while((aSocket == null) && (getConnectionException() == null) && (System.currentTimeMillis() < (beginTime + 10000)))
		{}
		if(aSocket == null && getConnectionException() == null)
		{
			setConnectionException(SocketUtil.OPERATION_TIMED_OUT);
		}
		return getConnectionException();
	}

	private String getConnectionAddress()
	{	return connectionAddress;	}
	private void setConnectionAddress(String x)
	{	connectionAddress = x;	}
	private int getPortId()
	{	return portId;	}
	private void setPortId(int x)
	{	portId = x;	}
	private String getConnectionException()
	{	return connectionException;	}
	private void setConnectionException(String x)
	{	connectionException = x;	}
}
