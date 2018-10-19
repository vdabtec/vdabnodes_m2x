/*LICENSE*
 * Copyright (C) 2013 - 2018 MJA Technology LLC 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package vdab.extnodes.m2x;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import com.lcrc.af.AnalysisData;
import com.lcrc.af.AnalysisEvent;
import com.lcrc.af.constants.HTTPMethodType;

import vdab.api.node.HTTPService_A;
import vdab.core.dataencode.JsonUtility;
import vdab.core.nodes.http.ServiceHandler_HTTP;

public class M2XService extends HTTPService_A {
	private static final String API_ENDPOINT = "https://api-m2x.att.com"; 
	private static final String API_VERSION = "/v2"; 
	private static final String API_DEVICEPATH = "/devices/";
	
	private String c_DeviceID;
	private String c_ApiKey;
	private String c_Stream;
	
	public String get_DeviceID(){
		return c_DeviceID;
	}
	public void set_DeviceID(String id){
		c_DeviceID = id;
	}
	public String get_ApiKey(){
		return c_ApiKey;
	}
	public void set_ApiKey(String key){
		c_ApiKey = key;
	}
	public String get_Stream(){
		return c_Stream;
	}
	public void set_Stream(String stream){
		c_Stream = stream;
	}
	public void _init(){
		super._init();
		// Force to use post for M2X
		set_HTTPMethod(Integer.valueOf(HTTPMethodType.POST));
	}
	@Override
	public String buildCompleteURL(AnalysisEvent ev) {
		StringBuilder sb = new StringBuilder();
		sb.append(API_ENDPOINT).append(API_VERSION);
		sb.append(API_DEVICEPATH);		
		sb.append(c_DeviceID);
		sb.append("/updates");
		return sb.toString();
	}

	public String buildPost(AnalysisEvent inEvent){		
		AnalysisData ad = this.getSelectedData(inEvent);
		StringBuilder sb = new StringBuilder();
		sb.append("\n { \"values\": ");
		sb.append(M2XDataUtility.convertAnalysisDataToM2XJson(ad, inEvent.getTimestamp().longValue()));
		sb.append("\n }");
		logTrace("JSON="+sb.toString());
		return sb.toString();
	//	return "{ \"values\": { \"temperature\": [ { \"timestamp\": \"2018-06-09T12:00:00Z\", \"value\": 112 } ] } } ";
	}
	public void processReturnStream(AnalysisEvent inEvent, int retCode, InputStream is) {

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = in.readLine()) != null)
				sb.append(line);	
			AnalysisData ad = JsonUtility.convertJsonToAnalysisData("M2XResponse",sb.toString());
			serviceResponse(inEvent, new AnalysisEvent(this, ad));
		}
		catch (Exception e){
			this.serviceFailed(inEvent, 3);
		}
	}
	@Override
	public void setupHTTPConnection(HttpURLConnection con) throws Exception {
		con.setRequestProperty("X-M2X-KEY", c_ApiKey);
		con.setRequestProperty("Content-Type", "application/json");	
	}

}
