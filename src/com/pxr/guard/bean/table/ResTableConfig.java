package com.pxr.guard.bean.table;

import com.pxr.guard.utils.ByteUtils;

public class ResTableConfig {
	public int size;

	public int getByteSize() {
		return 0x38 + 4;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nResTableConfig:\n").append("---config start---\n").append("size:").append(size)
				.append("\n").append("mcc:").append(mcc).append("mnc:").append(mnc).append("imsi:").append(imsi)
				.append("\n")

				.append("language:").append(new String(language)).append("country:").append(new String(country))
				.append("locale:").append(locale).append("\n")

				.append("orientation:").append(orientation & 0xff).append("touchscreen:").append(touchscreen & 0xff)
				.append("density:").append(density).append("screenType:").append(screenType).append("\n")

				.append("keyboard:").append(keyboard & 0xff).append("navigation:").append(navigation & 0xff)
				.append("inputFlags:").append(inputFlags & 0xff).append("inputPad0:").append(inputPad0 & 0xff)
				.append("input:").append(input).append("\n")

				.append("screenWidth:").append(screenWidth).append("screenHeight:").append(screenHeight)
				.append("screenSize:").append(screenSize).append("\n")

				.append("sdkVersion:").append(sdkVersion).append("minorVersion:").append(minorVersion)
				.append("version:").append(version).append("\n")

				.append("screenLayout:").append(screenLayout & 0xff).append("uiMode:").append(uiMode & 0xff)
				.append("smallestScreenWidthDp:").append(smallestScreenWidthDp).append("screenConfig:")
				.append(screenConfig).append("\n")

				.append("screenWidthDp:").append(screenWidthDp).append("screenHeightDp:").append(screenHeightDp)
				.append("screenSizeDp:").append(screenSizeDp).append("\n").append("---config end---");
		return stringBuilder.toString();
	}

	public static ResTableConfig parseResTableConfig(byte[] src, int offset) {
		ResTableConfig config = new ResTableConfig();
		config.size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.mcc = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.mnc = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.imsi = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.language = ByteUtils.copyByte(src, offset, 2);
		offset += 2;
		config.country = ByteUtils.copyByte(src, offset, 2);
		offset += 2;
		config.locale = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.orientation = src[offset++];
		config.touchscreen = src[offset++];
		config.density = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.screenType = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.keyboard = src[offset++];
		config.navigation = src[offset++];
		config.inputFlags = src[offset++];
		config.inputPad0 = src[offset++];
		config.input = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.screenWidth = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.screenHeight = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.screenSize = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.sdkVersion = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.minorVersion = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.version = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.screenLayout = src[offset++];
		config.uiMode = src[offset++];
		config.smallestScreenWidthDp = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.screenConfig = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		config.screenWidthDp = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.screenHeightDp = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		config.screenSizeDp = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		return config;
	}

	/**
	 * union { struct { // Mobile country code (from SIM). 0 means "any".
	 * uint16_t mcc; // Mobile network code (from SIM). 0 means "any". uint16_t
	 * mnc; }; uint32_t imsi; }; +6 = 10
	 */
	public short mcc;
	public short mnc;
	public int imsi;

	/**
	 * union { struct { // \0\0 means "any". Otherwise, en, fr, etc. char
	 * language[2];
	 * 
	 * // \0\0 means "any". Otherwise, US, CA, etc. char country[2]; }; uint32_t
	 * locale; }; + 8 =18
	 */
	public byte[] language = new byte[2];
	public byte[] country = new byte[2];
	public int locale;

	/**
	 * union { struct { uint8_t orientation; uint8_t touchscreen; uint16_t
	 * density; }; uint32_t screenType; }; +8 =26
	 */
	public byte orientation;
	public byte touchscreen;
	public short density;
	public int screenType;

	/**
	 * union { struct { uint8_t keyboard; uint8_t navigation; uint8_t
	 * inputFlags; uint8_t inputPad0; }; uint32_t input; }; +8 = 34
	 */
	public byte keyboard;
	public byte navigation;
	public byte inputFlags;
	public byte inputPad0;
	public int input;

	/**
	 * union { struct { uint16_t screenWidth; uint16_t screenHeight; }; uint32_t
	 * screenSize; }; + 8 = 42
	 */
	public short screenWidth;
	public short screenHeight;
	public int screenSize;

	/**
	 * union { struct { uint16_t sdkVersion; // For now minorVersion must always
	 * be 0!!! Its meaning // is currently undefined. uint16_t minorVersion; };
	 * uint32_t version; }; + 8=50
	 */
	public short sdkVersion;
	public short minorVersion;
	public int version;
	/**
	 * union { struct { uint8_t screenLayout; uint8_t uiMode; uint16_t
	 * smallestScreenWidthDp; }; uint32_t screenConfig; };
	 * 
	 * union { struct { uint16_t screenWidthDp; uint16_t screenHeightDp; };
	 * uint32_t screenSizeDp; }; + 8+8 = 56
	 */

	public byte screenLayout;
	public byte uiMode;
	public short smallestScreenWidthDp;
	public int screenConfig;

	public short screenWidthDp;
	public short screenHeightDp;
	public int screenSizeDp;

}
