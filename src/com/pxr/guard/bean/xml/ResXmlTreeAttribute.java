package com.pxr.guard.bean.xml;

import com.pxr.guard.bean.ResValue;
import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.utils.ByteUtils;

public class ResXmlTreeAttribute {

	public ResStringPoolRef ns;
	public ResStringPoolRef name;
	public ResStringPoolRef rawValue;
	public ResValue typedValue; // 8

	public int getByteSize() {
		return ns.getByteSize() + name.getByteSize() + rawValue.getByteSize() + typedValue.getByteSize();
	}

	public static ResXmlTreeAttribute parseAttribute(byte[] src, int offset) {
		ResXmlTreeAttribute data = new ResXmlTreeAttribute();
		data.ns = ResStringPoolRef.parseResStringPoolRef(src, offset);
		offset += data.ns.getByteSize();
		data.name = ResStringPoolRef.parseResStringPoolRef(src, offset);
		offset += data.name.getByteSize();
		data.rawValue = ResStringPoolRef.parseResStringPoolRef(src, offset);
		offset += data.rawValue.getByteSize();
		data.typedValue = ResValue.parseResValue(src, offset);
		return data;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("ResXmlTreeAttribute:");
		stringBuilder.append(" |ns:").append(ns).append(" |name:").append(name).append(" |rawValue:").append(rawValue)
				.append(" |typedValue:").append(typedValue);
		return stringBuilder.toString();
	}

	public byte[] toBytes() {
		return ByteUtils.mergeBytes(ns.toBytes(), name.toBytes(), rawValue.toBytes(), typedValue.toBytes());
	}
	
	public ResXmlTreeAttribute(){
		
	}

	public ResXmlTreeAttribute(ResStringPoolRef ns, ResStringPoolRef name, ResStringPoolRef rawValue,
			ResValue typedValue) {
		super();
		this.ns = ns;
		this.name = name;
		this.rawValue = rawValue;
		this.typedValue = typedValue;
	}
}
