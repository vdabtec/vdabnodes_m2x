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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.lcrc.af.AnalysisData;
import com.lcrc.af.AnalysisDataDef;
import com.lcrc.af.AnalysisEvent;
import com.lcrc.af.AnalysisObject;
import com.lcrc.af.constants.HTTPMethodType;
import com.lcrc.af.constants.OutputEventType;
import com.lcrc.af.util.StringUtility;

import vdab.api.node.HTTPService_A;
import vdab.core.dataencode.JsonUtility;
import vdab.core.nodes.http.HTTPRequestRunner;
import vdab.core.nodes.http.HTTPResponseHandler_I;

public class M2XService extends HTTPService_A implements HTTPResponseHandler_I{
	private static final String API_ENDPOINT = "http://api-m2x.att.com"; 
	private static final String API_HTTPS_ENDPOINT = "https://api-m2x.att.com"; 
	private static final String API_VERSION = "/v2"; 
	private static final String API_DEVICEPATH = "/devices/";
	
	private String c_DeviceID;
	private String c_ApiKey;
	private String c_Stream;
	private Boolean c_UseHTTPS = Boolean.FALSE;
	private String c_AvailableStreams;
	public M2XService(){
		 set_OutputType(OutputEventType.BOOLEAN); // Always outputs a boolean succeeded or failed.
	}
	
	public String get_DeviceID(){
		return c_DeviceID;
	}
	public void set_DeviceID(String id){
		String oldId = c_DeviceID;
		c_DeviceID = id;
		if (oldId == null || id != oldId){
				String infoURL = buildStreamInfoURL();
			if (infoURL != null)
				new HTTPRequestRunner(this, 1, infoURL, this);
		} 
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
	public AnalysisDataDef def_Stream(AnalysisDataDef theDataDef){
		if (c_AvailableStreams != null){
			String [] streams = c_AvailableStreams.split(",");
			if(streams.length > 0)
				theDataDef.setAllPickValues(streams);
		}
		return theDataDef;
	}
	public Boolean get_UseHTTPS(){
		return c_UseHTTPS;
	}
	public void set_UseHTTPS(Boolean use){
		c_UseHTTPS = use;
	}

	public String get_AvailableStreams(){
		return c_AvailableStreams;
		
	}
	
	public void _init(){
		super._init();
		// Force to use post for M2X
		/*String infoURL = buildStreamInfoURL();
		if (infoURL != null)
			new HTTPRequestRunner(this, 1, infoURL, this);
		*///"http://api-m2x.att.com/v2/devices/cacdfce9db48384737835aa1f8589b41/streams?pretty=true"
		set_HTTPMethod(Integer.valueOf(HTTPMethodType.POST));
	}
	
	private String buildStreamInfoURL() {

		if (c_DeviceID  == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(API_ENDPOINT);	
		sb.append(API_VERSION);	
		sb.append(API_DEVICEPATH);		
		sb.append(c_DeviceID);
		sb.append("/streams");
		return sb.toString();
	}
	// for HTTPService_A --------------------------
	@Override
	public String buildCompleteURL(AnalysisEvent ev) {
		StringBuilder sb = new StringBuilder();
		if (c_UseHTTPS.booleanValue())
			sb.append(API_HTTPS_ENDPOINT);
		else
			sb.append(API_ENDPOINT);	
		sb.append(API_VERSION);	
		sb.append(API_DEVICEPATH);		
		sb.append(c_DeviceID);
		sb.append("/updates");
		return sb.toString();
	}
	@Override
	public String buildPost(AnalysisEvent inEvent){		
		AnalysisData ad = this.getSelectedData(inEvent);
		StringBuilder sb = new StringBuilder();
		sb.append("\n { \"values\": ");
		if( c_Stream != null && ad.isSimple() ) {
			sb.append(M2XDataUtility.convertAnalysisDataToM2XJsonSettingStream(c_Stream, ad, inEvent.getTimestamp().longValue()));
		}
		else if( c_Stream != null && !ad.isSimple() ) {
			setWarning("The event has to be simple in order for Stream to be successfully selected.");
		}
		else {
			
			sb.append(M2XDataUtility.convertAnalysisDataToM2XJson(ad, inEvent.getTimestamp().longValue()));
		}
		sb.append("\n }");
		logTrace("JSON="+sb.toString());
		return sb.toString();
	//	return "{ \"values\": { \"temperature\": [ { \"timestamp\": \"2018-06-09T12:00:00Z\", \"value\": 112 } ] } } ";
	}
	@Override
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
			serviceFailed(inEvent, 3);
		}
	}
	@Override
	public void setupHTTPConnection(HttpURLConnection con)  {
		con.setRequestProperty("X-M2X-KEY", c_ApiKey);
		con.setRequestProperty("Content-Type", "application/json");	
	}
	// for HTTPResponseHandler_I -------------------------------------------
	@Override
	public void processRunnerResponse(int reqCode, String retMsg, InputStream inS)  {
			BufferedReader in = new BufferedReader(new InputStreamReader(inS));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			try {
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			} 
			catch (IOException e) {}
			if (response.length() > 0 ){
				List<String> streamList = StringUtility.locateAllBetween(response.toString(), "\"display_name\":\"", "\",");
				c_AvailableStreams  = StringUtility.getDelimitedStrings(streamList,",");
			}
	}
	@Override
	public void setHTTPRequestRunnerConnection(int reqCode, HttpURLConnection connection) {
		connection.setRequestProperty("X-M2X-KEY", c_ApiKey);
		connection.setRequestProperty("Content-Type", "application/json");	
	}

}
