package com.pxr.guard.bean.table;

import com.pxr.guard.utils.ByteUtils;

/**
 *  This is a reference to a unique entry (a ResTable_entry structure)
 *  in a resource table.  The value is structured as: 0xpptteeee,
 *  where pp is the package index, tt is the type index in that
 *  package, and eeee is the entry index in that type.  The package
 *  and type values start at 1 for the first item, to help catch cases
 *  where they have not been supplied.
 */
public class ResTableRef {
	public int ident;

	public int getByteSize() {
		return 4;
	}

	@Override
	public String toString() {
		return ByteUtils.bytesToHexString(ByteUtils.int2Byte(ident));
	}
	
	public static ResTableRef parseResTableRef(byte[] src, int offset) {
		ResTableRef ref  = new ResTableRef();
		ref.ident = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		return ref;
	}
}
