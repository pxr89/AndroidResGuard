package com.pxr.guard.bean.table;

import com.pxr.guard.bean.ResValue;

/**
 * This is a reference to a unique entry (a ResTable_entry structure) in a
 * resource table. The value is structured as: 0xpptteeee, where pp is the
 * package index, tt is the type index in that package, and eeee is the entry
 * index in that type. The package and type values start at 1 for the first
 * item, to help catch cases where they have not been supplied.
 */
public class ResTableMap {
	// The resource identifier defining this mapping's name. For attribute
	// resources, 'name' can be one of the following special resource types
	// to supply meta-data about the attribute; for all other resource types
	// it must be an attribute resource.
	public ResTableRef name;
	public ResValue value;

	public int getByteSize() {
		// 4+8
		return name.getByteSize() + value.getByteSize();
	}

	@Override
	public String toString() {
		return "(name:" +name+"  value:"+value+")";
	}

	public static ResTableMap parseResTableMap(byte[] src, int offset) {
		ResTableMap ref = new ResTableMap();
		ref.name = ResTableRef.parseResTableRef(src, offset);
		offset += ref.name.getByteSize();
		ref.value = ResValue.parseResValue(src, offset);
		return ref;
	}
}
