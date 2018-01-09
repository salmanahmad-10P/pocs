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

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;

import org.apache.log4j.Logger;


/**
 * This is a util class for helper functions encountered in dealing with any
 * kind of writers.
 */
public class IOUtil {
	public  static  final String  FILETYPE_TAGIDMAP_PROCESSING = "RFID_TAG_Processing";

	static Logger log = null;
	static boolean useWindowsIOHack = false;
	static {
		log = Logger.getLogger("IOUtil");
		try {
			String windowsIOHack = System.getProperty("WINDOWS_IO_HACK");
			if(!StringUtil.isEmpty(windowsIOHack)) {
				if(Boolean.parseBoolean(windowsIOHack)) {
					useWindowsIOHack = true;
					log.info("static()  WILL USE WINDOWS IO HACK");
				}
			}
		} catch(Exception x) {
			x.printStackTrace();
		}
	}

	public static boolean isReachable(String cAddress) throws java.io.IOException {
		if(!useWindowsIOHack) {
			InetAddress remoteAddress = InetAddress.getByName(cAddress);
			return remoteAddress.isReachable(null, 0, 4000);
		}else {
			Socket s = null;
			try {
				s = new Socket(cAddress, 80);
				return s.isConnected();
			} finally {
				if(s != null)
					s.close();
			}
		}
	}

	/**
	 * @author JA Bride 13 March 2001
	 */
	public static PrintWriter createPrintWriter(String filePath, String fileName) throws java.io.IOException
	{
		// log.debug("IOUtil.createPrintWriter starting");
		FileOutputStream fos = null;
		try
		{
			File file = new File(filePath, fileName);
			fos = new FileOutputStream(file);
			return new PrintWriter(fos);
		}
		catch (Exception x)
	 	{
			x.printStackTrace();
			return null;
		}
	}

	public static FileOutputStream createOutputStream(String filePath, String fileName) throws java.io.IOException
	{
		log.debug("IOUtil.createOutputStream starting filepath = '" + filePath + "' name = '" + fileName + "'");
		FileOutputStream fos = null;
		File file = new File(filePath, fileName);
		fos = new FileOutputStream(file);
		return fos;
	}

	/**
	 * Creates a directory as specified.
	 * 
	 * @param the
	 *            directory path and name
	 * @author Peter Manta
	 */
	private static void createDirectory(String dir) 
	{
		log.debug("IOUtil.createDirectory Starting");
		File f = new File(dir);
		f.mkdirs();
	}

	/**
        createDirectoryPath()

        @param  String  One of the file type constants declared in this class:  Note: will be parent directory
        @param  String  The level2Dir --> optional directory branch off of parent directory
        @param  String  The level3Dir --> optional directory branch off of leve2Dir directory
        @return String  The directory path
        */
        public static String createDirectoryPath(String fileType, String level2Dir, String level3Dir) throws Exception
        {
		String GE_HOME = System.getProperty("GE_HOME");
		if(StringUtil.isEmpty(GE_HOME))
		{
			log.error("$GE_HOME variable not set!!  Please set in the command line that initiated this process.  In the meantime ... setting $GE_HOME to /opt/globaledge/");
			GE_HOME = "/opt/globaledge/";
		}
                if (StringUtil.isEmpty(fileType))
                {
                        log.error("createDirectoryPath() fileType is set to null!!");
                        throw new Exception("Could not resolve the path. File not uploaded.");
                }
                //get the base directory path for files
                StringBuffer dirPath = new StringBuffer(GE_HOME);

                if (!(dirPath.toString()).endsWith(File.separator))
                {
                        dirPath.append(File.separator);
                }

                dirPath.append(fileType);
                dirPath.append(File.separator);

                if(!StringUtil.isEmpty(level2Dir))
                {
                        dirPath.append(level2Dir);
                        dirPath.append(File.separator);
                }

                //get the level3Dir and append
                if(!StringUtil.isEmpty(level3Dir))
                {
                        dirPath.append(level3Dir);
                        dirPath.append(File.separator);
                }

                String strPath = new String(dirPath);

                File f = new File(strPath);
                if (!f.isDirectory())
		{
                        f.mkdirs();
                }
                log.debug("IOUtil.createDirectorypath path = " + strPath);
                return strPath;
        }

	public static void dumpReaderOutput(BufferedReader readerIO) throws Exception {
		String line = null;
		while(!StringUtil.isEmpty(line = readerIO.readLine())) {
                	log.debug(line);
                }
	}

	public static String printByteArrayToHex(byte[] byteArray, int length) {
		//log.info("length = "+length);
                StringBuffer buffer = new StringBuffer();
                for (int z = 0; z < length; z++) {
                        buffer.append(Integer.toHexString(byteArray[z]));
                }
                return buffer.toString();
        }

}
