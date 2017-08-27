package com.pxr.guard.bean;

import com.pxr.guard.bean.string.ResStringPool;
import com.pxr.guard.bean.table.ResTableHeader;
import com.pxr.guard.bean.table.ResTablePackage;
import com.pxr.guard.utils.ByteUtils;

public class ARSCRes {
	public ResTableHeader header;
	public ResStringPool resStringPool;
	public ResTablePackage tablePackage;
	
	
	//TODO TestCode
	public static ResStringPool strPool;

	public static ARSCRes parseRes(byte[] srcByte) {
		ARSCRes data = new ARSCRes();
		int offset = 0;
		data.header = ResTableHeader.parseTableHeader(srcByte, offset);
		offset += data.header.getByteSize();
		data.resStringPool = ResStringPool.parseResStringPool(srcByte, offset);
		offset += data.resStringPool.getByteSize();
		
		//TODO testcode
		strPool = data.resStringPool;
		data.tablePackage = ResTablePackage.parseResTablePackage(srcByte, offset);
		return data;
	}
	
	/**
	 * 转换成byte 写入文件
	 * @return
	 */
	public byte[] toBytes(){
		byte[] afterHeader = ByteUtils.mergeBytes(resStringPool.toBytes(), tablePackage.toBytes());
		header.header.size = header.getByteSize() + afterHeader.length;
		return ByteUtils.mergeBytes(header.toBytes(), afterHeader);
	}
}
