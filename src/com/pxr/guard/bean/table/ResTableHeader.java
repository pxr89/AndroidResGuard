package com.pxr.guard.bean.table;

import com.pxr.guard.constant.Constants;
import com.pxr.guard.utils.ByteUtils;

/**
 * Header for a resource table. Its data contains a series of additional chunks:
 * * A ResStringPool_header containing all table values. This string pool
 * contains all of the string values in the entire resource table (not the names
 * of entries or type identifiers however). * One or more ResTable_package
 * chunks.
 *
 * Specific entries within a resource table can be uniquely identified with a
 * single integer as defined by the ResTable_ref structure.
 */
public class ResTableHeader {
	public ResChunkHeader header;
	public int packageCount;

	@Override
	public String toString() {
		return "ResTableHeader:\n" + Constants.TAB + "header:" + header + "\n" + Constants.TAB + "packageCount:"
				+ packageCount;
	}

	public int getByteSize() {
		return header.getByteSize() + 4;
	}

	public static ResTableHeader parseTableHeader(byte[] srcByte, int offset) {
		ResTableHeader tableHeader = new ResTableHeader();
		tableHeader.header = ResChunkHeader.parseHeader(srcByte, offset);
		tableHeader.packageCount = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, tableHeader.header.getByteSize(), 4));
		return tableHeader;
	}
	
	public byte[] toBytes(){
		return ByteUtils.mergeBytes(header.toBytes(),ByteUtils.int2Byte(packageCount));
	}
}
