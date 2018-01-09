//---------------------------------------------------------------------------
//	I N T E R M E C  T E C H N O L O G I E S  C O R P O R A T I O N
//	5 5 0  S E C O N D  S T R E E T
//	C E D A R  R A P I D S,  I O W A   U S A
//---------------------------------------------------------------------------
//	COPYRIGHT 2004 INTERMEC TECHNOLOGIES CORPORATION.
//	UNPUBLISHED - ALL RIGHTS RESERVED UNDER THE COPYRIGHT LAWS.
//	PROPRIETARY AND CONFIDENTIAL INFORMATION. DISTRIBUTION, USE
//	AND DISCLOSURE RESTRICTED BY INTERMEC TECHNOLOGIES CORPORATION.
//---------------------------------------------------------------------------

//---------------------------------------------------------------------------
//	FILE: SerialReaderConnection.java
//
//	DESC: IReaderConnection implementation for serial I/O (RS-232).
//---------------------------------------------------------------------------

package com.intermec.datacollection.rfid;

import javax.comm.*;

import java.io.*;

//import java.util.*;  //required for "Enumeration"

/**
 * An implementation of the IReaderConnection interface for serial I/O (i.e.,
 * RS-232).
 */
class SerialReaderConnection implements IReaderConnection {
	private SerialPort serialPort;

	private OutputStream outputStream;

	private InputStream inputStream;

	private String commPortName;

	int baud = 115200;

	int dataBits = SerialPort.DATABITS_8;

	int stopBits = SerialPort.STOPBITS_1;

	int parity = SerialPort.PARITY_NONE;

	/**
	 * @param commPortName
	 *            (e.g., COM1, COM2, etc.)
	 */
	public SerialReaderConnection(String commPortName) {
		this.commPortName = commPortName;
	}

	/**
	 * @param commPortName
	 *            (e.g., COM1, COM2, etc.)
	 */
	public SerialReaderConnection(String commPortName, int baud) {
		this.commPortName = commPortName;
		this.baud = baud;
	}

	/**
	 * Establish the serial I/O hardware parameters. Default values are: <br>
	 * <br>
	 * BAUD = 115,200 DATA BITS = 8 STOP BITS = 1 PARITY = NONE <br>
	 * <br>
	 * 
	 * @param baud
	 * @param dataBits
	 * @param stopBits
	 * @param parity
	 */
	public void setRS232parameters(int baud, int dataBits, int stopBits,
			int parity) {
		this.baud = baud;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
	}

	/**
	 * Open the serial I/O connection. Scan through all available serial ports
	 * and find a match with the comm name provided by the called.
	 * 
	 * @param commPortName
	 *            (e.g., COM1, COM2, etc.)
	 * @param port
	 *            not used. Regarded for the ReaderConnection interface which
	 *            also supports TCP.
	 * @throws BRIException
	 *             Serial conditions can cause the open to fail: serial driver
	 *             (e.g., win32comm.dll) can't be found, serial properties file
	 *             can't be found (e.g., javax.comm.properties), the comm port
	 *             is already opened by another program, the comm port doesn't
	 *             exist on the target device, etc.
	 */
	public void open() throws BRIException {
		CommPortIdentifier portId;

		// Display existing Comm Port names (used for debugging)
		// Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		// while (portList.hasMoreElements()) {
		// portId = (CommPortIdentifier)portList.nextElement();
		// if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		// System.out.println("Port="+portId.getName());
		// }
		// }

		try {
			portId = CommPortIdentifier.getPortIdentifier(commPortName);
			try {
				serialPort = (SerialPort) portId.open("BasicReader", 2000);
			} catch (PortInUseException ex) {
				// ex.printStackTrace();
				throw new BRIException(BRIException.BEX_PORT_IN_USE);
			}
			try {
				outputStream = serialPort.getOutputStream();
				inputStream = serialPort.getInputStream();
			} catch (IOException ex) {
				// ex.printStackTrace();
				throw new BRIException(BRIException.BEX_IO_EXCEPTION);
			}
			try {
				serialPort
						.setSerialPortParams(baud, dataBits, stopBits, parity);
			} catch (UnsupportedCommOperationException ex) {
				// ex.printStackTrace();
				throw new BRIException(
						BRIException.BEX_UNSUPPORTED_COMM_OPERATION);
			}
		} catch (NoSuchPortException ex) {
			// ex.printStackTrace();
			throw new BRIException(BRIException.BEX_NO_SUCH_PORT);
		}
	}

	/**
	 * Send data bytes out over the serial interface.
	 * 
	 * @param sendBytes
	 *            Byte array of data to send. The length of the array
	 *            establishes the number of bytes to send (i.e., the entire
	 *            array is sent).
	 * @throws BRIException
	 *             if the write fails for any reason
	 */
	public void send(byte[] sendBytes) throws BRIException {
		try {
			outputStream.write(sendBytes);
		} catch (IOException ex) {
			// ex.printStackTrace();
			throw new BRIException(BRIException.BEX_IO_EXCEPTION);
		}
	}

	/**
	 * Send data bytes out over the serial interface. Force a delay between each
	 * character. This is sometimes required to communication with the RFID
	 * reader module when ECHO=ON.
	 * 
	 * @param sendBytes
	 *            Byte array of data to send. The length of the array
	 *            establishes the number of bytes to send (i.e., the entire
	 *            array is sent).
	 * @throws BRIException
	 *             if the write fails for any reason
	 */
	public void sendWithDelay(byte[] sendBytes) throws BRIException {
		for (int i = 0; i < sendBytes.length; i++) {
			try {
				outputStream.write(sendBytes[i]);
				outputStream.flush();
			} catch (IOException e) {
				throw new BRIException(BRIException.BEX_IO_EXCEPTION);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Receive data bytes from the serial interface. This is a 'blocked' call.
	 * The read will hang until data is read.
	 * 
	 * @param recvBytes
	 *            Byte array wherein to copy the received data.
	 * @param offset
	 *            Offset into the byte array from where the data is to be
	 *            copied.
	 * @return number of bytes read
	 * @throws BRIException
	 *             if the data read overflows the buffer provided or if the
	 *             serial connection becomes invalid.
	 */
	public int receive(byte[] recvBytes, int offset) throws BRIException {
		try {
			int numBytes = inputStream.read(recvBytes, offset, recvBytes.length
					- offset);
			return numBytes;
		} catch (IOException ex) {
			// ex.printStackTrace();
			throw new BRIException(BRIException.BEX_IO_EXCEPTION);
		}
	}

	/**
	 * Close the serial connection which includes both the input and output data
	 * streams.
	 * 
	 * @throws BRIException
	 *             if the close fails for any reason
	 */
	public void close() throws BRIException {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException ex) {
			// ex.printStackTrace();
			throw new BRIException(BRIException.BEX_IO_EXCEPTION);
		}
		serialPort.close();
	}
}
