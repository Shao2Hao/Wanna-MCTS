package m.pay.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import m.system.RuntimeData;

import com.github.wxpay.sdk.WXPayConfig;

public class WxPayConfig implements WXPayConfig{

    private byte[] certData;
    private String key;
    private String appID;
    private String mchID;

    public WxPayConfig(String appID,String key,String mchID,String certPath) {
    	this.appID=appID;
    	this.mchID=mchID;
    	this.key=key;
    	String path=new StringBuffer(null!=RuntimeData.getClassPath()?RuntimeData.getClassPath():"").append(certPath).toString();
        File file = new File(path);
		try {
			InputStream certStream = new FileInputStream(file);
	        this.certData = new byte[(int) file.length()];
	        certStream.read(this.certData);
	        certStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }
	public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
	}

	public String getKey() {
		return key;
	}

	public String getAppID() {
		return appID;
	}

	public String getMchID() {
		return mchID;
	}

}
