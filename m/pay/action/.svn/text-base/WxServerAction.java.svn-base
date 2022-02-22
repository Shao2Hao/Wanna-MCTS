package m.pay.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.swing.text.Document;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import m.common.action.Action;
import m.common.action.ActionMeta;
import m.pay.service.WxServerService;
import m.system.lang.HtmlBodyContent;
import m.system.util.JSONMessage;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;


@ActionMeta(name="payWxServer")
public class WxServerAction extends Action {
	private String url;
	public JSONMessage getWxConfig(){
		JSONMessage message;;
		try {
			message=getService(WxServerService.class).getWxConfig(url);
			message.push("status", 1);
		} catch (Exception e) {
			e.printStackTrace();
			message=new JSONMessage();
			message.push("status", 0);
			message.push("message", e.getMessage());
		}
		return message;
	}
	private String signature;
	private String echostr;
	private String timestamp;
	private String nonce;
	public HtmlBodyContent token() throws SAXException, IOException, ParserConfigurationException{
		String token = "qwer1234";
		 // 第一步:将token、timestamp、nonce三个参数进行字典序排序
		String[] parms = new String[] { token, timestamp, nonce };// 将需要字典序排列的字符串放到数组中
		Arrays.sort(parms);// 按照api要求进行字典序排序
		// 第二步:将三个参数字符串拼接成一个字符串进行sha1加密
		// 拼接字符串
		String parmsString = "";// 注意，此处不能=null。
		for (int i = 0; i < parms.length; i++) {
			parmsString += parms[i];
		}
		// sha1加密
		String mParms = null;// 加密后的结果
		MessageDigest digest = null;
		try {
			digest = java.security.MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		digest.update(parmsString.getBytes());
		byte messageDigest[] = digest.digest();
		// Create Hex String
		StringBuffer hexString = new StringBuffer();
		// 字节数组转换为 十六进制 数
		for (int i = 0; i < messageDigest.length; i++) {
			String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexString.append(0);
			}
			hexString.append(shaHex);
		}
		mParms = hexString.toString();// 加密结果

		/*
		 * api要求： 若确认此次GET请求来自微信服务器，请原样返回echostr参数内容， 则接入生效， 成为开发者成功，否则接入失败。
		 */
		// 第三步： 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信接入成功。
		// System.out.println(TAG + ":" + mParms + "---->" + signature);
		if (mParms.equals(signature)) {
			// System.out.println(TAG + ":" + mParms + "---->" + signature);
			return new HtmlBodyContent(echostr);
		} else {
			// 接入失败,不用回写
			// System.out.println(TAG + "接入失败");
		}
		
		
		
// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
// DocumentBuilder db = dbf.newDocumentBuilder();
// Document doc = (Document) db.parse(getRequest().getInputStream());
// System.out.println(doc);
        return new HtmlBodyContent("");
	}
	public static String getXmlString(Document doc){  
        TransformerFactory tf = TransformerFactory.newInstance();  
        try {  
            Transformer t = tf.newTransformer();  
            t.setOutputProperty(OutputKeys.ENCODING,"UTF-8");//解决中文问题，试过用GBK不行  
            t.setOutputProperty(OutputKeys.METHOD, "html");    
            t.setOutputProperty(OutputKeys.VERSION, "4.0");    
            t.setOutputProperty(OutputKeys.INDENT, "no");    
            ByteArrayOutputStream bos = new ByteArrayOutputStream();  
            t.transform(new DOMSource((Node) doc), new StreamResult(bos));  
            return bos.toString();  
        } catch (TransformerConfigurationException e) {  
            e.printStackTrace();  
        } catch (TransformerException e) {  
            e.printStackTrace();  
        }  
        return "";  
    } 
	
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getEchostr() {
		return echostr;
	}
	public void setEchostr(String echostr) {
		this.echostr = echostr;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
}
