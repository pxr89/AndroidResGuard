package com.pxr.guard.bean.xml;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.utils.ByteUtils;

public class ResXmlTreeAttrExt {
	public ResStringPoolRef ns;
	public ResStringPoolRef name;
	public short attributeStart;
	public short attributeSize; // ResXmlTreeAttrExt的size
	public short attributeCount;

	// Index (1-based) of the "id" attribute. 0 if none.
	public short idIndex;
	public short classIndex;
	public short styleIndex;

	public ResXmlTreeAttrExt() {
	}

	public ResXmlTreeAttrExt(ResStringPoolRef ns, ResStringPoolRef name, short attributeStart, short attributeSize,
			short attributeCount) {
		this.ns = ns;
		this.name = name;
		this.attributeStart = attributeStart;
		this.attributeSize = attributeSize;
		this.beforeCount = ByteUtils.mergeBytes(ns.toBytes(), name.toBytes(), ByteUtils.short2Byte(attributeStart),
				ByteUtils.short2Byte(attributeSize));

		this.attributeCount = attributeCount;

		this.idIndex = 0;
		this.classIndex = 0;
		this.styleIndex = 0;
		this.afterCount = ByteUtils.mergeBytes(ByteUtils.short2Byte(idIndex), ByteUtils.short2Byte(classIndex),
				ByteUtils.short2Byte(styleIndex));
	}

	public ResXmlTreeAttrExt(ResStringPoolRef ns, ResStringPoolRef name, short attributeStart, short attributeSize,
			short attributeCount, short idIndex, short classIndex, short styleIndex) {
		this.ns = ns;
		this.name = name;
		this.attributeStart = attributeStart;
		this.attributeSize = attributeSize;
		this.beforeCount = ByteUtils.mergeBytes(ns.toBytes(), name.toBytes(), ByteUtils.short2Byte(attributeStart),
				ByteUtils.short2Byte(attributeSize));

		this.attributeCount = attributeCount;

		this.idIndex = idIndex;
		this.classIndex = classIndex;
		this.styleIndex = styleIndex;
		this.afterCount = ByteUtils.mergeBytes(ByteUtils.short2Byte(idIndex), ByteUtils.short2Byte(classIndex),
				ByteUtils.short2Byte(styleIndex));
	}

	public int getByteSize() {
		return ns.getByteSize() + name.getByteSize() + 6 * 2;
	}

	public static ResXmlTreeAttrExt parseResXmlTreeAttrExt(byte[] src, int offset) {
		int initialOffset = offset;
		ResXmlTreeAttrExt data = new ResXmlTreeAttrExt();
		data.ns = ResStringPoolRef.parseResStringPoolRef(src, offset);
		offset += data.ns.getByteSize();
		data.name = ResStringPoolRef.parseResStringPoolRef(src, offset);
		offset += data.name.getByteSize();

		data.attributeStart = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.attributeSize = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;

		data.beforeCount = ByteUtils.copyByte(src, initialOffset, 4 + 4 + 2 + 2);

		data.attributeCount = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;

		data.afterCount = ByteUtils.copyByte(src, offset, 2 + 2 + 2);

		data.idIndex = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.classIndex = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.styleIndex = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		return data;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("ResXmlTreeAttrExt");
		stringBuilder.append(" |ns:").append(ns).append(" |name:").append(name).append(" |attributeStart:")
				.append(attributeStart).append(" |attributeSize:").append(attributeSize).append(" |attributeCount:")
				.append(attributeCount).append(" |idIndex:").append(idIndex).append(" |classIndex:").append(classIndex)
				.append(" |styleIndex:").append(styleIndex);
		return stringBuilder.toString();
	}

	private byte[] beforeCount;
	private byte[] afterCount;

	public byte[] toBytes() {
		return ByteUtils.mergeBytes(beforeCount, ByteUtils.short2Byte(attributeCount), afterCount);
	}

	public void addAttrCount(int count) {
		attributeCount += count;
	}

}
