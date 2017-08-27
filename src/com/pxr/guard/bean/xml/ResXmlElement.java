package com.pxr.guard.bean.xml;

public abstract class ResXmlElement {
	public static final int TYPE_START =0 ;
	public static final int TYPE_END =1 ;
	public abstract int getType();
	public abstract byte[] toBytes();
	public ResXmlTreeNode header;

}
