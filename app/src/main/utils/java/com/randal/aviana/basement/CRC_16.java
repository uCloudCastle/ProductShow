package com.randal.aviana.basement;

public class CRC_16 {
	public static int alex_crc16(byte[] buf, int len) {
		int i, j;
		int c, crc = 0xFFFF;
		for (i = 0; i < len; i++) {
			c = buf[i] & 0x00FF;
			crc ^= c;
			for (j = 0; j < 8; j++) {
				if ((crc & 0x0001) != 0) {
					crc >>= 1;
					crc ^= 0xA001;
				} else
					crc >>= 1;
			}
		}
		return crc;
	}
}