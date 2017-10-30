package com.zpstudio.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */
public class ParseXmlService
{
	private static final String TAG = "ParseXmlService";
	private static final String REGEX = ";;";
	private static final String REPLACEMENT = "&";
	public HashMap<String, String> parseXml(InputStream inStream) throws Exception
	{

		String tmp = inStreamToString(inStream);
		tmp = tmp.replaceAll(REPLACEMENT, REGEX);
		Log.w(TAG , tmp);
		InputStream sbs = new StringBufferInputStream(tmp);
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		// ʵ����һ���ĵ�����������
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ͨ���ĵ�������������ȡһ���ĵ�������
		DocumentBuilder builder = factory.newDocumentBuilder();
		// ͨ���ĵ�ͨ���ĵ�����������һ���ĵ�ʵ��
		Document document = builder.parse(sbs);
		//��ȡXML�ļ����ڵ�
		Element root = document.getDocumentElement();
		//��������ӽڵ�
		NodeList childNodes = root.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++)
		{
			//�����ӽڵ�
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element) childNode;
				String value = childElement.getFirstChild().getNodeValue();
				value = value.replaceAll(REGEX, REPLACEMENT);
				hashMap.put(childElement.getNodeName() , value);
//				//�汾��
//				if ("version".equals(childElement.getNodeName()))
//				{
//					hashMap.put("version",childElement.getFirstChild().getNodeValue());
//				}
//				//�������
//				else if (("name".equals(childElement.getNodeName())))
//				{
//					hashMap.put("name",childElement.getFirstChild().getNodeValue());
//				}
//				//���ص�ַ
//				else if (("url".equals(childElement.getNodeName())))
//				{
//					hashMap.put("url",childElement.getFirstChild().getNodeValue());
//				}
			}
		}
		return hashMap;
	}
	
	private String inStreamToString(InputStream inStream) {
		StringBuffer res = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream));
			;
			while ((line = reader.readLine()) != null) {
				res.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.toString();
	}

	
}
