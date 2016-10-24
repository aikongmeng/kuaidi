package com.kuaibao.skuaidi.api;

import android.content.Context;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebServiceHelper {
	private static WebServiceHelper webServiceHelper;

	public static WebServiceHelper getInstance() {
		if (webServiceHelper == null) {
			webServiceHelper = new WebServiceHelper();
		}
		return webServiceHelper;
	}

	public String getPart(Context context, String targetNameSpace,
			String Method, String WSDL, String soapAction, StringBuffer request)
			throws Exception {
		SoapObject soapObject = new SoapObject(targetNameSpace, Method);
		soapObject.addProperty("Request", request.toString());

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);

		HttpTransportSE ht = new HttpTransportSE(WSDL);
		ht.debug = true;
		ht.call(soapAction, envelope);
		String result = envelope.getResponse().toString();
		
		return result;
	}

}
