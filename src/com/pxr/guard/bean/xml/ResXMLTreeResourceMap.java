package com.pxr.guard.bean.xml;

import com.pxr.guard.bean.table.ResChunkHeader;
import com.pxr.guard.utils.ByteUtils;

//RES_XML_RESOURCE_MAP_TYPE = 0x0180;
public class ResXMLTreeResourceMap {
	public ResChunkHeader header;
	public int[] resIds;
	
	public static ResXMLTreeResourceMap parseXMLResMap(byte[] src, int offset){
		ResXMLTreeResourceMap map = new ResXMLTreeResourceMap();
		int iOffset = offset;
		map.header = ResChunkHeader.parseHeader(src, offset);
		offset += map.header.getByteSize();
		int size = (map.header.size - map.header.headerSize)/4;
		map.resIds = new int[size];
		for(int i = 0;i<size;i++){
			map.resIds[i] = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
			offset +=4;
		}
		map.mapBytes = ByteUtils.copyByteBetween(src, iOffset, offset);
		return map;
	}
	
	private byte[] mapBytes;
	public int getByteSize(){
		return header.size;
	}
	
	public byte[] toBytes(){
		return mapBytes;
	}

}
