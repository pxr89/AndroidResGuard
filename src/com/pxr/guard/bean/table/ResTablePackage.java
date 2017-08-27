package com.pxr.guard.bean.table;

import java.util.ArrayList;
import java.util.Arrays;

import com.pxr.guard.bean.ITableRes;
import com.pxr.guard.bean.ResType;
import com.pxr.guard.bean.string.ResStringPool;
import com.pxr.guard.constant.Constants;
import com.pxr.guard.utils.ByteUtils;

/**
 * A collection of resource data types within a package. Followed by one or more
 * ResTable_type and ResTable_typeSpec structures containing the entry values
 * for each resource type. struct ResTable_package { struct ResChunk_header
 * header;
 * 
 * // If this is a base package, its ID. Package IDs start // at 1
 * (corresponding to the value of the package bits in a // resource identifier).
 * 0 means this is not a base package. uint32_t id;
 * 
 * // Actual name of this package, \0-terminated. uint16_t name[128];
 * 
 * // Offset to a ResStringPool_header defining the resource // type symbol
 * table. If zero, this package is inheriting from // another base package
 * (overriding specific values in it). uint32_t typeStrings;
 * 
 * // Last index into typeStrings that is for public use by others. uint32_t
 * lastPublicType;
 * 
 * // Offset to a ResStringPool_header defining the resource // key symbol
 * table. If zero, this package is inheriting from // another base package
 * (overriding specific values in it). uint32_t keyStrings;
 * 
 * // Last index into keyStrings that is for public use by others. uint32_t
 * lastPublicKey;
 * 
 * uint32_t typeIdOffset; };
 */
public class ResTablePackage {

	// package的头部部分
	public ResChunkHeader header;
	public int id;
	public byte[] name = new byte[128 * 2]; // 128
	public int typeStrings;
	public int lastPublicType;
	public int keyStrings;
	public int lastPublicKey;
	public int typeIdOffset;

	// 
	public ResStringPool typeStringPool;
	public ResStringPool keyStringPool;

	public ArrayList<ITableRes> ress;
	public ArrayList<ResTableType> resTableTypes;

	public int getByteSize() {
		return header.size;
	}
	// 修改时涉及到的内容更改只有 keyStringPool 以及 header的长度
	public byte[] betweenHeaderAndKey;
	public byte[] afterKey;
	
	public byte[] toBytes(){
		byte[] afterHeader = ByteUtils.mergeBytes(ByteUtils.mergeBytes(betweenHeaderAndKey, keyStringPool.toBytes()),afterKey);
		header.size = header.getByteSize() + afterHeader.length;
		return ByteUtils.mergeBytes(header.toBytes(), afterHeader);
	}
	
	//TODO 解析的时候获取type和key会用到的 待优化
	public static ArrayList<String> typeStringLists;
	public static ArrayList<String> keyStringLists;

	public static ResTablePackage parseResTablePackage(byte[] srcByte, int offset) {
		final int initialOffset = offset;
		ResTablePackage tableP = new ResTablePackage();
		tableP.header = ResChunkHeader.parseHeader(srcByte, offset);
		offset += tableP.header.getByteSize();
		tableP.id = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		tableP.name = ByteUtils.copyByte(srcByte, offset, 128 * 2);
		offset += 128 * 2;
		tableP.typeStrings = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		tableP.lastPublicType = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		tableP.keyStrings = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		tableP.lastPublicKey = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		tableP.typeIdOffset = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));

		offset = initialOffset + tableP.typeStrings;
		tableP.typeStringPool = ResStringPool.parseResStringPool(srcByte, offset);
		typeStringLists = tableP.typeStringPool.stringPool;

		offset = initialOffset + tableP.keyStrings;
		int bStart = initialOffset+tableP.header.getByteSize();
		
		tableP.betweenHeaderAndKey = Arrays.copyOfRange(srcByte, bStart, offset);
		
		tableP.keyStringPool = ResStringPool.parseResStringPool(srcByte, offset);
		keyStringLists = tableP.keyStringPool.stringPool;

		// 解析剩下的类型数据
		offset += tableP.keyStringPool.getByteSize();
		
		tableP.afterKey = Arrays.copyOfRange(srcByte, offset, srcByte.length);
		
		tableP.ress = new ArrayList<>();
		tableP.resTableTypes = new ArrayList<>(); // modify use
		short type;
		ITableRes res = null;
		o:while (offset < srcByte.length) {
			// 获取类型
			type = ByteUtils.byte2Short(ByteUtils.copyByte(srcByte, offset, 2));
			switch (type) {
				case ResType.RES_TABLE_TYPE_SPEC_TYPE:
					res = ResTableTypeSpec.parseResTableTypeSpec(srcByte, offset);
					break;
				case ResType.RES_TABLE_TYPE_TYPE:
					res = ResTableType.parseResTableType(srcByte, offset);
					tableP.resTableTypes.add((ResTableType) res);
					break;
				default:
					System.out.println("未知类型：" + offset);
					break o;
			}
			if (res == null) {
				System.out.println("解析为空：offset：" + offset);
				break o;
			}
			offset += res.getByteSize();
			if(offset == srcByte.length){
				System.out.println("解析完成");
			}
			tableP.ress.add(res);
			res = null;
		}

		return tableP;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("\nResTablePackage:\n");
		s.append(Constants.TAB).append("id:").append(id).append(" |name:")
				.append(ByteUtils.filterStringNull(new String(name))).append(" |typeStrings:").append(typeStrings)
				.append(" |lastPublicType:").append(lastPublicType).append(" |keyStrings:").append(keyStrings)
				.append(" |lastPublicKey:").append(lastPublicKey).append(" |typeIdOffset:").append(typeIdOffset)
				.append("\n" + Constants.TAB).append("typeStringPool:").append(typeStringPool)
				.append("\n" + Constants.TAB).append("keyStringPool:").append(keyStringPool).append("\n");

		s.append("\nres start\n").append("------------------------------------------------------------------");
		ITableRes res = null;
		for (int i = 0; i < ress.size(); i++) {
			res = ress.get(i);
			s.append("\ntype:" + ResType.getStrType(res.getType())).append("\n").append(res).append("\n");
			s.append("**********************************");
		}
		s.append("------------------------------------------------------------------");
		return s.toString();
	}
	
	
	public static String getKeyString(int index){
		return keyStringLists.get(index);
	}

}
