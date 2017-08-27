package com.pxr.guard.bean.dex;

import com.pxr.guard.utils.ByteUtils;

public class DexHeader {

	public byte[] magic; // 8
	public byte[] checksum; // 4
	public byte[] signature; // 20
	public int file_size;

	public int header_size;
	public int endian_tag;
	public int link_size;
	public int link_off;
	
	public int map_off;
	public int string_ids_size;
	public int string_ids_off;
	public int type_ids_size;
	
	public int type_ids_off;
	public int proto_ids_size;
	public int proto_ids_off;
	public int field_ids_size;
	
	public int field_ids_off;
	public int method_ids_size;
	public int method_ids_off;
	public int class_defs_size;
	
	public int class_defs_off;
	public int data_size;
	public int data_off;

	public byte[] headerOther; // 记录header_size-data_off的byte数
	
	public int getByteLength(){
		System.out.println(header_size);
		return 8+4+20 
				+4 * 4 * 5;
	}
	
	

	public static DexHeader parseDexHeader(byte[] src, int offset) {
		DexHeader header = new DexHeader();
		header.magic = ByteUtils.copyByte(src, offset, 8);
		offset += 8;
		header.checksum = ByteUtils.copyByte(src, offset, 4);
		offset += 4;
		header.signature = ByteUtils.copyByte(src, offset, 20);
		offset += 20;
		header.file_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		int i = offset;
		header.header_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.endian_tag = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.link_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.link_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.map_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.string_ids_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.string_ids_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.type_ids_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.type_ids_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.proto_ids_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.proto_ids_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.field_ids_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.field_ids_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.method_ids_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.method_ids_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.class_defs_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.class_defs_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.data_size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		header.data_off = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		header.headerOther = ByteUtils.copyByteBetween(src, i, offset);

//		if (header.headerOther.length == (header.header_size - 36))
//			System.out.println("header finish");

		return header;
	}

	public byte[] toBytes() {
		return ByteUtils.mergeBytes(magic, checksum, signature, 
				ByteUtils.int2Byte(file_size), headerOther);
	}

}
