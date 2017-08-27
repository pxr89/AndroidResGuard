package com.pxr.guard.bean.dex;
/**
 * dex文件的bean类
 * @author panxianrong
 */
public class Dex {
	
	public DexHeader header;
	public DexStrings strings;
	
	
	public static Dex parseDex(byte[] src,int offset){
		Dex dex = new Dex();
		dex.header = DexHeader.parseDexHeader(src, 0);
		dex.strings = DexStrings.parseDexStrings(dex.header, src);
		return dex;
	}

}
