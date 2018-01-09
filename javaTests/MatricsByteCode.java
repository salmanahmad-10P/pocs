//Readers at DC  10.1.32.11 DCOUT ar400
// and 10.1.32.12 //DCIN ar400
package com.dodshow.RFIDGlobalSolutions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.*;
import java.io.*;
import java.net.*;

class MatricsByteCode extends Thread {
	byte READ_COMMAND = 0x22; //

	int ANTENNAPARM_NODE = 1;

	int ANTENNAPARM_INCANT0 = 4;

	int ANTENNAPARM_INCANT1 = 5;

	int ANTENNAPARM_INCANT2 = 6;

	int ANTENNAPARM_INCANT3 = 7;

	int ANTENNAPARM_POWER = 8;

	int ANTENNAPARM_ENVIRONMENT = 9;

	int ANTENNAPARM_COMBINED = 10;

	int RECEIVE_PACKET_STATUS = 4;

	int PACKET_SOF = 0;

	int PACKET_NODEADDRESS = 1;

	int PACKET_LENGTH = 2;

	int PACKET_COMMAND = 3;

	int PACKET_DATA = 4; // upto 64 bytes

	int TAG_PACKET_NUMTAGS = 6;

	int TAG_ANTENNA = 5;

	int VirtualReaderEnabled = 1;

	int CommandPending = 0; // This gets set to reflect a command other than

	// read

	int CommandParms[] = { 0, 0, 0, 0, 0, 0, 0, 0 };

	int ConnectionFailed = 0;

	int ThreadShutdown = 0;

	private XMLConfiguration configuration = null;

	private Translator translator;

	private static Log log = LogFactory.getLog(MatricsByteCode.class);

	RfidReader ParentReader;

	Socket MSocket;

	String Host;

	int Port;

	private InputStream inStream = null;

	private OutputStream outStream = null;

	byte Command10[] = { 1, 4, READ_COMMAND };

	byte NewReqRead[] = { 0x01, 0x04, 0x06, READ_COMMAND, (byte) 0xa0, 0, 0 };

	byte SetAntennaParms[] = {
			0x01,
			0x04,
			0x29,
			0x15,
			0x00, // AM0
			0x00, // AM1
			0x00, // AM2
			0x00, // AM3

			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00 };

	byte SetAntennaParmsResp[] = { 0x01, 0x04, 0x06, 0x15, 0x00 };

	byte GetReaderParameters[] = { 0x01, 0x00, 0x06, 0x16, (byte) 0xa0, 0x00,
			0x00 };

	int RespGetReaderParameters0[] = { 0x01, 0x00, 0x26, 0x16, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	int RespGetReaderParameters1[] = { 0x01, 0x00, 0x26, 0x16, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	int RespGetReaderParameters2[] = { 0x01, 0x00, 0x26, 0x16, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	int RespGetReaderParameters3[] = { 0x01, 0x00, 0x26, 0x16, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	int AntennaPower[] = { 255, 255, 255, 255, 255, 255, 255, 255 };

	int AntennaEnvironment[] = { 1, 1, 1, 1 }; // 3,3,3,3};

	byte CurrentAntenna = 0;

	byte ActiveAntennas[] = { 1, 1, 1, 0 };

	byte CombinedAntennas[] = { 0, 0, 0, 0 }; // 1,1,1,1};

	byte ReaderID = 4;

	int recdata[] = new int[512];

	int rindex;

	int map[] = { 8, 9, 10, 11, 5, 6, 7, 0, 1, 2, 3, 4 };

	short omatrics_table[] = { (short) 0x0000, (short) 0x1189, (short) 0x2312,
			(short) 0x329B, (short) 0x4624, (short) 0x57AD, (short) 0x6536,
			(short) 0x74BF, (short) 0x8C48, (short) 0x9DC1, (short) 0xAF5A,
			(short) 0xBED3, (short) 0xCA6C, (short) 0xDBE5, (short) 0xE97E,
			(short) 0xF8F7, (short) 0x1081, (short) 0x0108, (short) 0x3393,
			(short) 0x221A, (short) 0x56A5, (short) 0x472C, (short) 0x75B7,
			(short) 0x643E, (short) 0x9CC9, (short) 0x8D40, (short) 0xBFDB,
			(short) 0xAE52, (short) 0xDAED, (short) 0xCB64, (short) 0xF9FF,
			(short) 0xE876, (short) 0x2102, (short) 0x308B, (short) 0x0210,
			(short) 0x1399, (short) 0x6726, (short) 0x76AF, (short) 0x4434,
			(short) 0x55BD, (short) 0xAD4A, (short) 0xBCC3, (short) 0x8E58,
			(short) 0x9FD1, (short) 0xEB6E, (short) 0xFAE7, (short) 0xC87C,
			(short) 0xD9F5, (short) 0x3183, (short) 0x200A, (short) 0x1291,
			(short) 0x0318, (short) 0x77A7, (short) 0x662E, (short) 0x54B5,
			(short) 0x453C, (short) 0xBDCB, (short) 0xAC42, (short) 0x9ED9,
			(short) 0x8F50, (short) 0xFBEF, (short) 0xEA66, (short) 0xD8FD,
			(short) 0xC974, (short) 0x4204, (short) 0x538D, (short) 0x6116,
			(short) 0x709F, (short) 0x0420, (short) 0x15A9, (short) 0x2732,
			(short) 0x36BB, (short) 0xCE4C, (short) 0xDFC5, (short) 0xED5E,
			(short) 0xFCD7, (short) 0x8868, (short) 0x99E1, (short) 0xAB7A,
			(short) 0xBAF3, (short) 0x5285, (short) 0x430C, (short) 0x7197,
			(short) 0x601E, (short) 0x14A1, (short) 0x0528, (short) 0x37B3,
			(short) 0x263A, (short) 0xDECD, (short) 0xCF44, (short) 0xFDDF,
			(short) 0xEC56, (short) 0x98E9, (short) 0x8960, (short) 0xBBFB,
			(short) 0xAA72, (short) 0x6306, (short) 0x728F, (short) 0x4014,
			(short) 0x519D, (short) 0x2522, (short) 0x34AB, (short) 0x0630,
			(short) 0x17B9, (short) 0xEF4E, (short) 0xFEC7, (short) 0xCC5C,
			(short) 0xDDD5, (short) 0xA96A, (short) 0xB8E3, (short) 0x8A78,
			(short) 0x9BF1, (short) 0x7387, (short) 0x620E, (short) 0x5095,
			(short) 0x411C, (short) 0x35A3, (short) 0x242A, (short) 0x16B1,
			(short) 0x0738, (short) 0xFFCF, (short) 0xEE46, (short) 0xDCDD,
			(short) 0xCD54, (short) 0xB9EB, (short) 0xA862, (short) 0x9AF9,
			(short) 0x8B70, (short) 0x8408, (short) 0x9581, (short) 0xA71A,
			(short) 0xB693, (short) 0xC22C, (short) 0xD3A5, (short) 0xE13E,
			(short) 0xF0B7, (short) 0x0840, (short) 0x19C9, (short) 0x2B52,
			(short) 0x3ADB, (short) 0x4E64, (short) 0x5FED, (short) 0x6D76,
			(short) 0x7CFF, (short) 0x9489, (short) 0x8500, (short) 0xB79B,
			(short) 0xA612, (short) 0xD2AD, (short) 0xC324, (short) 0xF1BF,
			(short) 0xE036, (short) 0x18C1, (short) 0x0948, (short) 0x3BD3,
			(short) 0x2A5A, (short) 0x5EE5, (short) 0x4F6C, (short) 0x7DF7,
			(short) 0x6C7E, (short) 0xA50A, (short) 0xB483, (short) 0x8618,
			(short) 0x9791, (short) 0xE32E, (short) 0xF2A7, (short) 0xC03C,
			(short) 0xD1B5, (short) 0x2942, (short) 0x38CB, (short) 0x0A50,
			(short) 0x1BD9, (short) 0x6F66, (short) 0x7EEF, (short) 0x4C74,
			(short) 0x5DFD, (short) 0xB58B, (short) 0xA402, (short) 0x9699,
			(short) 0x8710, (short) 0xF3AF, (short) 0xE226, (short) 0xD0BD,
			(short) 0xC134, (short) 0x39C3, (short) 0x284A, (short) 0x1AD1,
			(short) 0x0B58, (short) 0x7FE7, (short) 0x6E6E, (short) 0x5CF5,
			(short) 0x4D7C, (short) 0xC60C, (short) 0xD785, (short) 0xE51E,
			(short) 0xF497, (short) 0x8028, (short) 0x91A1, (short) 0xA33A,
			(short) 0xB2B3, (short) 0x4A44, (short) 0x5BCD, (short) 0x6956,
			(short) 0x78DF, (short) 0x0C60, (short) 0x1DE9, (short) 0x2F72,
			(short) 0x3EFB, (short) 0xD68D, (short) 0xC704, (short) 0xF59F,
			(short) 0xE416, (short) 0x90A9, (short) 0x8120, (short) 0xB3BB,
			(short) 0xA232, (short) 0x5AC5, (short) 0x4B4C, (short) 0x79D7,
			(short) 0x685E, (short) 0x1CE1, (short) 0x0D68, (short) 0x3FF3,
			(short) 0x2E7A, (short) 0xE70E, (short) 0xF687, (short) 0xC41C,
			(short) 0xD595, (short) 0xA12A, (short) 0xB0A3, (short) 0x8238,
			(short) 0x93B1, (short) 0x6B46, (short) 0x7ACF, (short) 0x4854,
			(short) 0x59DD, (short) 0x2D62, (short) 0x3CEB, (short) 0x0E70,
			(short) 0x1FF9, (short) 0xF78F, (short) 0xE606, (short) 0xD49D,
			(short) 0xC514, (short) 0xB1AB, (short) 0xA022, (short) 0x92B9,
			(short) 0x8330, (short) 0x7BC7, (short) 0x6A4E, (short) 0x58D5,
			(short) 0x495C, (short) 0x3DE3, (short) 0x2C6A, (short) 0x1EF1,
			(short) 0x0F78, (short) 0x5818, (short) 0x0088, (short) 0x5788,
			(short) 0x0088, (short) 0x56C8, (short) 0x0088, (short) 0x5868,
			(short) 0x0088 };

	// -------------------------------------------------------------------------------------

	private short oMATRICS_CRC(short crc, short len, byte buf[]) {
		short tempcrc;
		short tseed = crc;
		short nseed = 0;
		for (int dx = 1; dx < len; dx++) {
			tempcrc = tseed;
			tseed = (short) (tseed ^ (byte) buf[dx]);
			tseed &= 0xff; // (short) (tseed & (byte) 0xff);
			tseed = omatrics_table[tseed];
			nseed = tempcrc;
			nseed = (short) ((nseed >> 8) & 0xff);
			tseed = (short) (tseed ^ nseed);
			nseed = tseed;
		}
		nseed = (short) (nseed ^ (short) 0xffff);
		return nseed;
	}

	int matrics_table[] = { (int) 0x0000, (int) 0x1189, (int) 0x2312,
			(int) 0x329B, (int) 0x4624, (int) 0x57AD, (int) 0x6536,
			(int) 0x74BF, (int) 0x8C48, (int) 0x9DC1, (int) 0xAF5A,
			(int) 0xBED3, (int) 0xCA6C, (int) 0xDBE5, (int) 0xE97E,
			(int) 0xF8F7, (int) 0x1081, (int) 0x0108, (int) 0x3393,
			(int) 0x221A, (int) 0x56A5, (int) 0x472C, (int) 0x75B7,
			(int) 0x643E, (int) 0x9CC9, (int) 0x8D40, (int) 0xBFDB,
			(int) 0xAE52, (int) 0xDAED, (int) 0xCB64, (int) 0xF9FF,
			(int) 0xE876, (int) 0x2102, (int) 0x308B, (int) 0x0210,
			(int) 0x1399, (int) 0x6726, (int) 0x76AF, (int) 0x4434,
			(int) 0x55BD, (int) 0xAD4A, (int) 0xBCC3, (int) 0x8E58,
			(int) 0x9FD1, (int) 0xEB6E, (int) 0xFAE7, (int) 0xC87C,
			(int) 0xD9F5, (int) 0x3183, (int) 0x200A, (int) 0x1291,
			(int) 0x0318, (int) 0x77A7, (int) 0x662E, (int) 0x54B5,
			(int) 0x453C, (int) 0xBDCB, (int) 0xAC42, (int) 0x9ED9,
			(int) 0x8F50, (int) 0xFBEF, (int) 0xEA66, (int) 0xD8FD,
			(int) 0xC974, (int) 0x4204, (int) 0x538D, (int) 0x6116,
			(int) 0x709F, (int) 0x0420, (int) 0x15A9, (int) 0x2732,
			(int) 0x36BB, (int) 0xCE4C, (int) 0xDFC5, (int) 0xED5E,
			(int) 0xFCD7, (int) 0x8868, (int) 0x99E1, (int) 0xAB7A,
			(int) 0xBAF3, (int) 0x5285, (int) 0x430C, (int) 0x7197,
			(int) 0x601E, (int) 0x14A1, (int) 0x0528, (int) 0x37B3,
			(int) 0x263A, (int) 0xDECD, (int) 0xCF44, (int) 0xFDDF,
			(int) 0xEC56, (int) 0x98E9, (int) 0x8960, (int) 0xBBFB,
			(int) 0xAA72, (int) 0x6306, (int) 0x728F, (int) 0x4014,
			(int) 0x519D, (int) 0x2522, (int) 0x34AB, (int) 0x0630,
			(int) 0x17B9, (int) 0xEF4E, (int) 0xFEC7, (int) 0xCC5C,
			(int) 0xDDD5, (int) 0xA96A, (int) 0xB8E3, (int) 0x8A78,
			(int) 0x9BF1, (int) 0x7387, (int) 0x620E, (int) 0x5095,
			(int) 0x411C, (int) 0x35A3, (int) 0x242A, (int) 0x16B1,
			(int) 0x0738, (int) 0xFFCF, (int) 0xEE46, (int) 0xDCDD,
			(int) 0xCD54, (int) 0xB9EB, (int) 0xA862, (int) 0x9AF9,
			(int) 0x8B70, (int) 0x8408, (int) 0x9581, (int) 0xA71A,
			(int) 0xB693, (int) 0xC22C, (int) 0xD3A5, (int) 0xE13E,
			(int) 0xF0B7, (int) 0x0840, (int) 0x19C9, (int) 0x2B52,
			(int) 0x3ADB, (int) 0x4E64, (int) 0x5FED, (int) 0x6D76,
			(int) 0x7CFF, (int) 0x9489, (int) 0x8500, (int) 0xB79B,
			(int) 0xA612, (int) 0xD2AD, (int) 0xC324, (int) 0xF1BF,
			(int) 0xE036, (int) 0x18C1, (int) 0x0948, (int) 0x3BD3,
			(int) 0x2A5A, (int) 0x5EE5, (int) 0x4F6C, (int) 0x7DF7,
			(int) 0x6C7E, (int) 0xA50A, (int) 0xB483, (int) 0x8618,
			(int) 0x9791, (int) 0xE32E, (int) 0xF2A7, (int) 0xC03C,
			(int) 0xD1B5, (int) 0x2942, (int) 0x38CB, (int) 0x0A50,
			(int) 0x1BD9, (int) 0x6F66, (int) 0x7EEF, (int) 0x4C74,
			(int) 0x5DFD, (int) 0xB58B, (int) 0xA402, (int) 0x9699,
			(int) 0x8710, (int) 0xF3AF, (int) 0xE226, (int) 0xD0BD,
			(int) 0xC134, (int) 0x39C3, (int) 0x284A, (int) 0x1AD1,
			(int) 0x0B58, (int) 0x7FE7, (int) 0x6E6E, (int) 0x5CF5,
			(int) 0x4D7C, (int) 0xC60C, (int) 0xD785, (int) 0xE51E,
			(int) 0xF497, (int) 0x8028, (int) 0x91A1, (int) 0xA33A,
			(int) 0xB2B3, (int) 0x4A44, (int) 0x5BCD, (int) 0x6956,
			(int) 0x78DF, (int) 0x0C60, (int) 0x1DE9, (int) 0x2F72,
			(int) 0x3EFB, (int) 0xD68D, (int) 0xC704, (int) 0xF59F,
			(int) 0xE416, (int) 0x90A9, (int) 0x8120, (int) 0xB3BB,
			(int) 0xA232, (int) 0x5AC5, (int) 0x4B4C, (int) 0x79D7,
			(int) 0x685E, (int) 0x1CE1, (int) 0x0D68, (int) 0x3FF3,
			(int) 0x2E7A, (int) 0xE70E, (int) 0xF687, (int) 0xC41C,
			(int) 0xD595, (int) 0xA12A, (int) 0xB0A3, (int) 0x8238,
			(int) 0x93B1, (int) 0x6B46, (int) 0x7ACF, (int) 0x4854,
			(int) 0x59DD, (int) 0x2D62, (int) 0x3CEB, (int) 0x0E70,
			(int) 0x1FF9, (int) 0xF78F, (int) 0xE606, (int) 0xD49D,
			(int) 0xC514, (int) 0xB1AB, (int) 0xA022, (int) 0x92B9,
			(int) 0x8330, (int) 0x7BC7, (int) 0x6A4E, (int) 0x58D5,
			(int) 0x495C, (int) 0x3DE3, (int) 0x2C6A, (int) 0x1EF1,
			(int) 0x0F78, (int) 0x5818, (int) 0x0088, (int) 0x5788,
			(int) 0x0088, (int) 0x56C8, (int) 0x0088, (int) 0x5868,
			(int) 0x0088 };

	// -------------------------------------------------------------------------------------

	private int MATRICS_CRC(int crc, int len, byte nbuf[]) {
		int buf[] = new int[256];
		for (int i = 0; i < len + 2; i++) {
			buf[i] = nbuf[i];
			if (buf[i] < 0)
				buf[i] = (buf[i] | 0x80) & 0xff;
		}

		int tempcrc;
		int tseed = crc;
		int nseed = 0;
		for (int dx = 1; dx < len; dx++) {
			tempcrc = tseed;
			tseed = (tseed ^ buf[dx]);
			tseed &= 0xff; // (int) (tseed & (byte) 0xff);
			tseed = matrics_table[tseed];
			nseed = tempcrc;
			nseed = ((nseed >> 8) & 0xff);
			tseed = (tseed ^ nseed);
			nseed = tseed;
		}
		nseed = (nseed ^ 0xffff);
		return nseed;
	}

	// -------------------------------------------------------------------------------------

	MatricsByteCode(XMLConfiguration configuration, RfidReader preader) {
		this.configuration = configuration;
		this.translator = preader.translator;
		this.ParentReader = preader;
		this.Host = preader.IPAddress;
		this.Port = preader.Port;

		this.VirtualReaderEnabled = preader.VirtualState;
	}

	// -------------------------------------------------------------------------------------

	public int ReadPacket() {
		int PacketReadInProgress;
		rindex = 0;
		int retval;
		int totalpktlen = 0;
		int PacketLength = -1;
		int pktstatus = 1;
		byte tempbytes[] = new byte[32];

		while ((pktstatus != 0) && (pktstatus != 0x40)
				&& (ConnectionFailed == 0)) {
			PacketReadInProgress = 1;
			rindex = 0;

			while ((PacketReadInProgress == 1) && (ConnectionFailed == 0)) {
				try {
					retval = inStream.read(tempbytes, 0, 1);
				} catch (IOException e) {
					ConnectionFailed = 1;
					log.debug("\n\r\n\rSKTERR 200 : Host:" + Host
							+ " Read a byte failed timeout?" + e);
					return 0;
				}

				if (retval == -1) // end of stream??? Weird??!
				{
					ConnectionFailed = 1;
					log
							.debug("End of stream received in ReadPacket, failing connection.");
					return 0;
				}

				if (retval == 1) {
					recdata[rindex] = tempbytes[0];
					if (recdata[rindex] < 0)
						recdata[rindex] = (recdata[rindex] & 0xff) | 0x80;

					if (rindex == 2) // lenth byte of 01 04 len cmd status
					{
						PacketLength = recdata[rindex];
					}
					if (rindex == 4) {
						pktstatus = recdata[rindex];
					}
					if (PacketLength != -1) {
						if (rindex >= PacketLength) {
							PacketReadInProgress = 0;
							totalpktlen = rindex;
						}
					}// if PacketLength !=-1
					rindex++;
				}// if retval==1
			}// while PacketReadInProgress
			ParseATagPacket(recdata);
		}// while pktstatus NOT done
		return totalpktlen;
	}

	// -------------------------------------------------------------------------------------

	public int ReadAntennaPacket() {
		int PacketReadInProgress;
		rindex = 0;
		int retval = 0;
		int totalpktlen = 0;
		int PacketLength = -1;
		int pktstatus = 1;
		byte tempbytes[] = new byte[32];

		while ((pktstatus != 0) && (pktstatus != 0x40)
				&& (ConnectionFailed == 0)) {
			PacketReadInProgress = 1;
			rindex = 0;

			// log.info("Read Antennas...About to read a byte...");
			while ((PacketReadInProgress == 1) && (ConnectionFailed == 0)) {
				try {
					retval = inStream.read(tempbytes, 0, 1);
				} catch (IOException e) {
					ConnectionFailed = 1;
					log.debug("\n\r\n\rSKTERR 200 : Host:" + Host
							+ " ReadByte in antennapacket Failed" + e);
				}

				if (retval == -1) // end of stream??? Weird??!
				{
					ConnectionFailed = 1;
					log
							.debug("End of stream received in ReadAntennaPacket, failing connection.");
					return 0;
				}

				if (retval == 1) {
					// log.info("READ a antenna byte");
					recdata[rindex] = tempbytes[0];
					if (recdata[rindex] < 0)
						recdata[rindex] = (recdata[rindex] & 0xff) | 0x80;

					if (rindex == 2) // lenth byte of 01 04 len cmd status
					{
						PacketLength = recdata[rindex];
						// log.info("ReaderAntennaPacket Length="+PacketLength);
					}

					if (rindex == 4) // status
					{
						pktstatus = recdata[rindex];
						// log.info("ReadAntenna Packet Status="+pktstatus);
					}

					if (PacketLength != -1) {
						if (rindex >= PacketLength) {
							PacketReadInProgress = 0;
							totalpktlen = rindex;
						}
					}// if PacketLength !=-1
					rindex++;
				}// if retval==1

			}// while PacketReadInProgress
		}// while pktstatus NOT done
		// log.info("Done ReadAntennaPacket len="+totalpktlen);
		return totalpktlen;
	}

	// -------------------------------------------------------------------------------------

	private int ParseATagPacket(int pkt[]) {
		int tag[][] = new int[256][12];
		int tagtype[] = new int[256];
		int mtag[] = new int[16];

		if (pkt[PACKET_COMMAND] != READ_COMMAND) {
			log.debug("\n\r\n\rMalformed packet Host:" + Host + " expected "
					+ READ_COMMAND + ", got = " + pkt[PACKET_COMMAND]);
			return 0;
		}
		int tagcount = pkt[TAG_PACKET_NUMTAGS];

		int PktStatus = pkt[RECEIVE_PACKET_STATUS];

		int skipadding = 0;

		if ((PktStatus == 0x80) || (PktStatus == 0xc0)) {
			int ErrCode = pkt[RECEIVE_PACKET_STATUS + 1];

			log.debug("\n\r\n\rERROR HOST: " + Host + " STATUS = " + PktStatus
					+ " ERRCODE=" + ErrCode);
			if (ErrCode == 0xF3) {
				ActiveAntennas[CurrentAntenna] = 0;
				log.info("\n\r\n\rANTENNA " + CurrentAntenna
						+ " Error DISABLED! ERROR RECEIVED! ANTENNA # "
						+ CurrentAntenna);
			}
			return 0;
			// skipadding=1;
		}
		if (tagcount == 0)
			return 0; // No tags? return.

		int toff = 7; // offset to 1st tag
		int tnum = 0;
		if (skipadding == 0) {
			while (tnum < tagcount) {
				// log.info("TAGFORMATBYTE: "+pkt[toff]);
				if (READ_COMMAND == 0x10) {
					if (pkt[toff] == 0x0c) {
						for (int o = 0; o < 8; o++)
							tag[tnum][o] = pkt[toff + o + 1];
						tagtype[tnum] = 0x0c;
						toff += 9;
					} else {
						for (int o = 0; o < 12; o++)
							mtag[o] = pkt[toff + o + 1];

						if (pkt[PACKET_COMMAND] == READ_COMMAND) {
							for (int o = 0; o < 12; o++)
								tag[tnum][map[o]] = mtag[o];
						} else {
							for (int o = 0; o < 12; o++)
								tag[tnum][o] = mtag[o];
						}
						tagtype[tnum] = pkt[toff];
						toff += 13;
					}
				}// if read command 0x10
				else // cmd 22?
				{
					if ((pkt[toff] & 0xf0) == 0) // ==0x0c)
					{
						for (int o = 0; o < 8; o++)
							tag[tnum][o] = pkt[toff + o + 1];
						tagtype[tnum] = 0x0c;
						toff += 9;
					} else {
						for (int o = 0; o < 12; o++)
							mtag[o] = pkt[toff + o + 1];

						if (pkt[PACKET_COMMAND] == READ_COMMAND) {
							for (int o = 0; o < 12; o++)
								tag[tnum][o] = mtag[o];
							// tag[tnum][map[o]] = mtag[o];
						} else {
							for (int o = 0; o < 12; o++)
								tag[tnum][o] = mtag[o];
						}
						tagtype[tnum] = pkt[toff];
						toff += 13;
					}
				}
				tnum++;
			} // while

			for (int t = 0; t < tagcount; t++) {
				int tsize = 8;
				if (tagtype[t] != 0x0c)
					tsize = 12;
				int antseenat = 0xa + CurrentAntenna;
				antseenat = antseenat << 4;
				translator.GlobalTagCounter++;
				EATag ntag = new EATag(tag[t], tsize, antseenat, ParentReader);
				if (ParentReader.AddTag(ntag) == 0)
					ntag = null;
				ParentReader.LastTimeReaderRead = new Date();
			} // for
		}// if skipadding
		return 1;
	}

	// -------------------------------------------------------------------------------------

	public void run() {
		log
				.info("\n\r--------------------------------------------------------------------");
		log.info("MatricsByteCode Thread Host: " + Host + " starting...");
		if (VirtualReaderEnabled == 1) // Create Virtual Tags
		{
			int tmptag[] = { 0xf0, 0xe0, 0xd0, 0xc0, 0xb0, 0xa0, 0x90, 0x80,
					0x70, 0x60, 0x50, 0x00 };

			tmptag[10] = RfidReader.GlobalReaderList.indexOf(ParentReader); // make
			// this
			// the
			// reader
			// index
			// note
			// 255
			// problem
			// unlikely.
			while (VirtualReaderEnabled == 1) {
				if (ParentReader.ReaderEnabled == true) {
					tmptag[11]++;
					tmptag[11] &= 15;
					int ant = (int) (Math.random() * 4);

					EATag ntag = new EATag(tmptag, 12, ant, ParentReader);
					if (ParentReader.AddTag(ntag) == 0)
						ntag = null;

					try {
						Thread.sleep(ParentReader.ReadDelay);
					} catch (InterruptedException ie) {
						log
								.debug("\n\r\n\rRead delay thread interrupted 0: Host:"
										+ Host + "  : " + ie);
					}
				}// if parentreaderenabled
				else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						log
								.debug("\n\r\n\rRead delay thread interrupted 1: Host:"
										+ Host + " : " + ie);
					}
				} // else
			} // while virtual reader = 1
		} // if virutalreader=1
		else {
			while (ThreadShutdown == 0) {
				try {
					log.info("\n\r\n\rRETRYING CONNECTION...Host:" + Host
							+ " Port:" + Port);
					ConnectionFailed = 0;
					MSocket = new Socket(Host, Port);
					inStream = MSocket.getInputStream();
					outStream = MSocket.getOutputStream();
					MSocket.setSoTimeout(2000); // block only for 2 seconds...

					while (ConnectionFailed == 0) {
						if ((translator.ElectricEyeState == 0)
								|| (translator.ElectricEyeBypass == 1)) // No
						// box
						// blocking
						// eye?
						{
							if (ParentReader.ReaderEnabled == true) {
								switch (CommandPending) {
								case 0:
									// log.info("Sending Read to Host: "+Host);
									GetNextAntenna();
									int curant = (CurrentAntenna << 4) + 0xa0;

									NewReqRead[4] = (byte) curant; // ((curant<<4)+(byte)0xa0);
									int csum = MATRICS_CRC((int) 0xBEEF,
											(int) 5, NewReqRead);
									NewReqRead[5] = (byte) (csum & (short) 0xff);
									NewReqRead[6] = (byte) (csum >> 8);
									translator.GlobalReadRequestCounter++;
									outStream.write(NewReqRead, 0, 7);
									ReadPacket();
									break;

								case 1: {
									CmdSetAntennaParametersTCP(CommandParms[0],
											CommandParms[1], CommandParms[2]);
									CommandPending = 0; // Reset back to read
									// command
								}
									break;

								case 2: {
									CmdGetAntennaParametersTCP();
									CommandPending = 0;
								}
								}// switch

								if (ParentReader.ReadDelay != 0) {
									try {
										Thread.sleep(ParentReader.ReadDelay);
									} catch (InterruptedException ie) {
										log.debug(ie);
									}
								}// if readdelay
							} // if reader enabled
							else {
								try {
									Thread.sleep(500);
									// log.debug("ThreadSleeping: Host:"+Host+"
									// Port:"+Port);
								} catch (InterruptedException ie) {
									log
											.debug("ThreadSleep exception? In readerenabled=0 : Host:"
													+ Host + " :  " + ie);
								}
							} // else reader NOT enabled
						}// if electric eye NOT blocked
						else {
							try {
								Thread.sleep(25); // sleep a bit if paused,
								// not too long though,
								// startup time is critical!
							} catch (InterruptedException ie) {
							} // ignore if sleep is interrupted for some
							// reason dont care
						}

					}// while connection NOT failed
					log.debug("CONNECTION FAILED WHile loop exited Host: "
							+ Host + " ConnectionFailed=" + ConnectionFailed);
				}// try
				catch (IOException e) {
					ConnectionFailed = 1;
					log.debug("Connectionfailed...Host:" + Host + " : " + e);
					try {
						if (MSocket != null)
							MSocket.close();
					} catch (IOException ee) {
						MSocket = null;
						inStream = null;
						outStream = null;
					}
					;
					log.debug("\n\r\n\rSKTERR 100: Host:" + Host
							+ " Socket disconnected?? Sleeping 2 seconds : "
							+ e);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException inte) {
						log
								.debug("SKTERR 999: Thread Sleep 2000 exception Host:+"
										+ Host + " : " + inte);
					}
				}// trycatch
			}// while threadshutdown
			log.debug("ERR: Host:" + Host + " Thread Shutdown set!");
		} // else
		log.debug("Err? : Host:" + Host + " thread is exiting!");
	}// run

	// -------------------------------------------------------------------------------------

	private byte GetNextAntenna() {
		int ctr = 0;
		while (ctr < 4) {
			CurrentAntenna++;
			CurrentAntenna &= 3;
			if (ActiveAntennas[CurrentAntenna] == 1)
				break;
			ctr++;
		}
		return CurrentAntenna;
	}

	// -------------------------------------------------------------------------------------

	int CmdSetAntennaParametersTCP(int antennatoset, int antpower, int antenv) {
		// log.info("Host: "+Host+"SetAntennaParameters : Ant: "+antennatoset+"
		// Power: "+antpower+" Env:"+antenv);
		for (int a = 0; a < 4; a++) {
			int recnum = 0;
			SetAntennaParms[1] = ReaderID;
			SetAntennaParms[ANTENNAPARM_COMBINED] = CombinedAntennas[a]; // ]=1;

			SetAntennaParms[ANTENNAPARM_INCANT0] = 0;
			SetAntennaParms[ANTENNAPARM_INCANT1] = 0;
			SetAntennaParms[ANTENNAPARM_INCANT2] = 0;
			SetAntennaParms[ANTENNAPARM_INCANT3] = 0;

			SetAntennaParms[ANTENNAPARM_INCANT0 + a] = 1;

			SetAntennaParms[ANTENNAPARM_POWER] = (byte) AntennaPower[a]; // antpower;
			SetAntennaParms[ANTENNAPARM_ENVIRONMENT] = (byte) AntennaEnvironment[a]; // antenv;

			int csum = MATRICS_CRC((int) 0xBEEF, (int) 40, SetAntennaParms);

			SetAntennaParms[40] = (byte) (csum & (short) 0xff);
			SetAntennaParms[41] = (byte) (csum >> 8);

			try {
				// log.info("CmdSetAntennaParm before write");
				outStream.write(SetAntennaParms, 0, 42);
				// log.info("CmdAfterWrite");
				int completed = 1;

				while ((completed != 0) && (completed != 0x40)
						&& (ConnectionFailed == 0)) {
					// log.debug("Cmd Before REad Pkt");
					ReadAntennaPacket();
					// log.debug("Cmd After Read Pkt");
					int stat = recdata[RECEIVE_PACKET_STATUS];
					completed = stat;
					int skipadding = 0;

					if ((stat == 0x80) || (stat == 0xc0)) {
						log.debug("\n\r\n\rError Host: " + Host
								+ " in setup antennas response packet!");
					}
				}
			} catch (IOException e) {
				log.debug("Set Anteanna Parms got a ioexception: Host: " + Host
						+ " :" + e);
			}
		} // for a
		// mr->PendingCommand=0x80;
		// log.debug("Finished CmdSetAntennaParams");
		return 1;
	}

	// -------------------------------------------------------------------------------------

	int CmdGetAntennaParametersTCP() {
		for (int a = 0; a < 4; a++) {

			GetReaderParameters[1] = ReaderID;
			GetReaderParameters[4] = (byte) (0xa0 + (a << 4));

			int csum = MATRICS_CRC((int) 0xBEEF, (int) 5, GetReaderParameters);

			GetReaderParameters[5] = (byte) (csum & (short) 0xff);
			GetReaderParameters[6] = (byte) (csum >> 8);

			try {
				outStream.write(GetReaderParameters, 0, 7);

				int completed = 1;

				while ((completed != 0) && (completed != 0x40)
						&& (ConnectionFailed == 0)) {
					ReadGetAntennaPacket(a);
					int stat = recdata[RECEIVE_PACKET_STATUS];
					completed = stat;

					if ((stat == 0x80) || (stat == 0xc0)) {
						log.debug("\n\r\n\rError Host:" + Host
								+ "  in setup antennas response packet!");
					}
				}
			} catch (IOException e) {
			}
		} // for a
		// mr->PendingCommand=0x80;
		return 1;
	}

	// -------------------------------------------------------------------------------------

	public int ReadGetAntennaPacket(int ant) {
		int PacketReadInProgress;
		rindex = 0;
		int retval = 0;
		int totalpktlen = 0;
		int PacketLength = -1;
		int pktstatus = 1;
		byte tempbytes[] = new byte[32];

		while ((pktstatus != 0) && (pktstatus != 0x40)
				&& (ConnectionFailed == 0)) {
			PacketReadInProgress = 1;
			rindex = 0;

			while ((PacketReadInProgress == 1) && (ConnectionFailed == 0)) {
				try {
					retval = inStream.read(tempbytes, 0, 1);
				} catch (IOException e) {
					ConnectionFailed = 1;
					log.debug("\n\r\n\rSKTERR Host:" + Host
							+ " 200 ReadByte in antennapacket Failed" + e);
				}

				if (retval == -1) // end of stream??? Weird??!
				{
					ConnectionFailed = 1;
					log
							.debug("End of stream received in ReadGetAntennaPacket, failing connection.");
					return 0;
				}

				if (retval == 1) {
					recdata[rindex] = tempbytes[0];
					if (recdata[rindex] < 0)
						recdata[rindex] = (recdata[rindex] & 0xff) | 0x80;

					if (rindex == 2) // lenth byte of 01 04 len cmd status
					{
						PacketLength = recdata[rindex];
					}

					if (rindex == 4) // status
					{
						pktstatus = recdata[rindex];
					}

					if (PacketLength != -1) {
						if (rindex >= PacketLength) {
							PacketReadInProgress = 0;
							totalpktlen = rindex;
						}
					}// if PacketLength !=-1
					rindex++;
				}// if retval==1
			}// while PacketReadInProgress
		}// while pktstatus NOT done

		for (int i = 0; i < rindex; i++) {
			switch (ant) {
			case 0:
				RespGetReaderParameters0[i] = recdata[i];
				break;
			case 1:
				RespGetReaderParameters1[i] = recdata[i];
				break;
			case 2:
				RespGetReaderParameters2[i] = recdata[i];
				break;
			case 3:
				RespGetReaderParameters3[i] = recdata[i];
				break;

			}

		}

		return totalpktlen;
	}

}
