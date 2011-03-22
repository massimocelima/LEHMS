package com.lehms.ui.clinical.device;

public class HexConverter {
	private static final String[] digits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	public static byte[] decode(final String hexString) {
		if (hexString.length() == 0) {
			final byte[] nullByte = {0};
			return nullByte;
		}
		
		final byte[] result = new byte[(int)Math.ceil(hexString.length() / 2.0f)];
		int index = 0;
		
		final char[] chars = hexString.toUpperCase().toCharArray();
		
		for (int i = 0; i < chars.length; i+=2) {
			result[index] = (byte)((HexConverter.toByte(chars[i]) << 4) | HexConverter.toByte(chars[i+1])); 
			index++;
		}
		
		return result;
	}
	
	public static String encode(final byte item) {
		return HexConverter.toHex(item);
	}
	
	public static String encode(final byte[] byteArray) {
		final StringBuffer buffer = new StringBuffer();

		for (final byte item : byteArray) {
			buffer.append(HexConverter.toHex(item));
		}

		return buffer.toString();
	}
	
	private static byte toByte(final char item) {
		if ((item >= '0') && (item <= '9')) {
			return (byte)(item - '0');  
		}
		
		return (byte)(item - 'A' + 10);
	}

	private static String toHex(final byte item) {
		int value = item;

		value &= 0x000000ff;

		return HexConverter.digits[(byte) ((value & 0xf0) >> 4)] + HexConverter.digits[(byte) (value & 0x0f)];
	}
}
