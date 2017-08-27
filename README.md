# AndroidResGuard

###使用步骤

	1.将需要混淆的app放入src_apks文件目录下  
	2.因为有些资源文件是通过getIdentifier方法获取的，这些资源文件不能混淆，所以需要在app工程中全局查找getIdentifier方法，将这个方法前两个参数以key:value的形式写入config目录下的except_config文件  
		注意：有些lib库中也会使用getIdentifier方法：比如友盟分享库等，也需要注意；  
		umenglib库已经做了特殊处理，包含umeng的都不混淆了  
	3.config目录下的签名配置 sign_cinfig文件 
	
		destpath:/Users/panxianrong/Desktop // apk的输出路径
		keystorefilepath:config/test.jks	// 签名文件的路径
		storepass:123456
		key:test
		keypass:123456
	4.运行即可 
	
###后续功能-Activity的混淆	

	
###TODO2:
	ResStringPool中的style未做解析(如果有使用Stringstyle的，请勿用此工具加密)

