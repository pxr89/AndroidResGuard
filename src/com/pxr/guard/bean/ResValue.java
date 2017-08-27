package com.pxr.guard.bean;

import com.pxr.guard.utils.ByteUtils;

/**
 * Representation of a value in a resource, supplying type information.
 */
public class ResValue {
	// Number of bytes in this structure.
	public short size;
	public byte res0;
	public byte dataType; // 如果是TYPE_STRING = 0x03, 那么去resStringPool找
	public int data; // 如果这个data在Res.resStringPool能找到内容，而且type和name都相同，那么就是一个文件资源

	public int getByteSize() {
		return 2 + 1 + 1 + 4;
	}

	public ResValue() {
	}
	public ResValue(byte type,int data) {
		size = 8;
		dataType = type;
		this.data = data;
	}

	/**
	 * same with ResValue(byte type,int data)
	 * @param size
	 * @param res0
	 * @param dataType
	 * @param data
	 */
	public ResValue(short size, byte res0, byte dataType, int data) {
		super();
		this.size = size;
		this.res0 = res0;
		this.dataType = dataType;
		this.data = data;
	}

	public static ResValue parseResValue(byte[] src, int offset) {
		ResValue data = new ResValue();
		data.size = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.res0 = src[offset++];
		data.dataType = src[offset++];
		data.data = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		return data;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("ResValue:\n");
		stringBuilder.append("|size:").append(size).append(" |res0:").append(res0 & 0xff).append(" |dataType:")
				.append(getTypeStr()).append(" |size:").append(size).append(" |data:").append(data)
				.append(":" + ARSCRes.strPool.getString(data));
		return stringBuilder.toString();
	}

	public byte[] toBytes() {
		return ByteUtils.mergeBytes(
				ByteUtils.short2Byte(size), 
				new byte[]{res0}, new byte[]{dataType},
				ByteUtils.int2Byte(data));

	}

	// dataType字段使用的常量 Type of the data value.
	public final static byte // The 'data' is either 0 or 1, specifying this
							// resource is either
	// undefined or empty, respectively.
	TYPE_NULL = 0x00,
			// The 'data' holds a ResTable_ref, a reference to another resource
			// table entry.
			TYPE_REFERENCE = 0x01,
			// The 'data' holds an attribute resource identifier.
			TYPE_ATTRIBUTE = 0x02,
			// The 'data' holds an index into the containing resource table's
			// global value string pool.
			TYPE_STRING = 0x03,
			// The 'data' holds a single-precision floating point number.
			TYPE_FLOAT = 0x04,
			// The 'data' holds a complex number encoding a dimension value,
			// such as "100in".
			TYPE_DIMENSION = 0x05,
			// The 'data' holds a complex number encoding a fraction of a
			// container.
			TYPE_FRACTION = 0x06,
			// The 'data' holds a dynamic ResTable_ref, which needs to be
			// resolved before it can be used like a TYPE_REFERENCE.
			TYPE_DYNAMIC_REFERENCE = 0x07,
			// Beginning of integer flavors...
			TYPE_FIRST_INT = 0x10,
			// The 'data' is a raw integer value of the form n..n.
			TYPE_INT_DEC = 0x10,
			// The 'data' is a raw integer value of the form 0xn..n.
			TYPE_INT_HEX = 0x11,
			// The 'data' is either 0 or 1, for input "false" or "true"
			// respectively.
			TYPE_INT_BOOLEAN = 0x12,
			// Beginning of color integer flavors...
			TYPE_FIRST_COLOR_INT = 0x1c,
			// The 'data' is a raw integer value of the form #aarrggbb.
			TYPE_INT_COLOR_ARGB8 = 0x1c,
			// The 'data' is a raw integer value of the form #rrggbb.
			TYPE_INT_COLOR_RGB8 = 0x1d,
			// The 'data' is a raw integer value of the form #argb.
			TYPE_INT_COLOR_ARGB4 = 0x1e,
			// The 'data' is a raw integer value of the form #rgb.
			TYPE_INT_COLOR_RGB4 = 0x1f,
			// ...end of integer flavors.
			TYPE_LAST_COLOR_INT = 0x1f,
			// ...end of integer flavors.
			TYPE_LAST_INT = 0x1f;

	public String getTypeStr() {
		switch (dataType) {
		case TYPE_NULL:
			return "TYPE_NULL";
		case TYPE_REFERENCE:
			return "TYPE_REFERENCE";
		case TYPE_ATTRIBUTE:
			return "TYPE_ATTRIBUTE";
		case TYPE_STRING:
			return "TYPE_STRING";
		case TYPE_FLOAT:
			return "TYPE_FLOAT";
		case TYPE_DIMENSION:
			return "TYPE_DIMENSION";
		case TYPE_FRACTION:
			return "TYPE_FRACTION";
		case TYPE_DYNAMIC_REFERENCE:
			return "TYPE_DYNAMIC_REFERENCE";
		case TYPE_FIRST_INT:
			return "TYPE_FIRST_INT";
		case TYPE_INT_HEX:
			return "TYPE_INT_HEX";
		case TYPE_INT_BOOLEAN:
			return "TYPE_INT_BOOLEAN";
		case TYPE_FIRST_COLOR_INT:
			return "TYPE_FIRST_COLOR_INT";
		case TYPE_INT_COLOR_RGB8:
			return "TYPE_INT_COLOR_RGB8";
		case TYPE_INT_COLOR_ARGB4:
			return "TYPE_INT_COLOR_ARGB4";
		case TYPE_INT_COLOR_RGB4:
			return "TYPE_INT_COLOR_RGB4";
		}
		return "";
	}

	public final static int COMPLEX_UNIT_SHIFT = 0, COMPLEX_UNIT_MASK = 0xf,

			// TYPE_DIMENSION: Value is raw pixels.
			COMPLEX_UNIT_PX = 0,
			// TYPE_DIMENSION: Value is Device Independent Pixels.
			COMPLEX_UNIT_DIP = 1,
			// TYPE_DIMENSION: Value is a Scaled device independent Pixels.
			COMPLEX_UNIT_SP = 2,
			// TYPE_DIMENSION: Value is in points.
			COMPLEX_UNIT_PT = 3,
			// TYPE_DIMENSION: Value is in inches.
			COMPLEX_UNIT_IN = 4,
			// TYPE_DIMENSION: Value is in millimeters.
			COMPLEX_UNIT_MM = 5,

			// TYPE_FRACTION: A basic fraction of the overall size.
			COMPLEX_UNIT_FRACTION = 0,
			// TYPE_FRACTION: A fraction of the parent size.
			COMPLEX_UNIT_FRACTION_PARENT = 1,

			// Where the radix information is, telling where the decimal place
			// appears in the mantissa. This give us 4 possible fixed point
			// representations as defined below.
			COMPLEX_RADIX_SHIFT = 4, COMPLEX_RADIX_MASK = 0x3,

			// The mantissa is an integral number -- i.e., 0xnnnnnn.0
			COMPLEX_RADIX_23p0 = 0,
			// The mantissa magnitude is 16 bits -- i.e, 0xnnnn.nn
			COMPLEX_RADIX_16p7 = 1,
			// The mantissa magnitude is 8 bits -- i.e, 0xnn.nnnn
			COMPLEX_RADIX_8p15 = 2,
			// The mantissa magnitude is 0 bits -- i.e, 0x0.nnnnnn
			COMPLEX_RADIX_0p23 = 3,

			// Where the actual value is. This gives us 23 bits of
			// precision. The top bit is the sign.
			COMPLEX_MANTISSA_SHIFT = 8, COMPLEX_MANTISSA_MASK = 0xffffff;
}
