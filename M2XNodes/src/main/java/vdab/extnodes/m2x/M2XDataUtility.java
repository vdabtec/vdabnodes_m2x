package vdab.extnodes.m2x;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.eclipse.jetty.util.ajax.JSON;

import com.lcrc.af.AnalysisCompoundData;
import com.lcrc.af.AnalysisData;

public class M2XDataUtility {
	private final static String SPACES ="\n                                                           ";
	private static DateFormat DATEFORMAT = getM2XDateFormat();
	private static DateFormat getM2XDateFormat(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");	
		df.setTimeZone(TimeZone.getTimeZone("UTC"));	
		return df;
	}
	public static String convertAnalysisDataToM2XJson(AnalysisData ad, long ts) {
		StringBuilder sb = new StringBuilder("{");
		String timeStr = DATEFORMAT.format(new Date(ts));
		buildJSON(sb, timeStr, 0, ad);
		sb.append("\n}");	
		return sb.toString();
	}

	
	// AD to M2X JSON ------------------------------------------------------------
	private static void buildJSON(StringBuilder sb, String timeStr, int level, AnalysisData ad){
		level++;
		if (ad.isSimple()){
			addJSONForAD(sb, timeStr, level, ad.getLabel(), ad.getDataAsString());
		}
		else {
			addJSONStartForACD(sb, level, ad.getLabel());
			AnalysisData[] childAds = ad.getChildData();
			for (int n = 0; n < childAds.length; n++) {
				if (n > 0)
					sb.append(",");
				buildJSON(sb, timeStr, level, childAds[n]);
				
			}
			addJSONEndForACD(sb, level, ad.getLabel());
		}
	}
	
	private static void addIndent(StringBuilder sb, int level){
		sb.append(SPACES.substring(0,level*4+1));
	}
	private static void addJSONStartForACD(StringBuilder sb, int level, String tag){
		addIndent(sb, level);
		sb.append("\"");
		sb.append(tag);
		sb.append("\": {");
	}
	private static void addJSONEndForACD(StringBuilder sb, int level, String tag){
		addIndent(sb, level);
		sb.append("}");
	}
	private static void addJSONForAD(StringBuilder sb, String timeStr, int level, String tag, String value){
		addIndent(sb, level);

		sb.append(" \"");
		sb.append(tag);
		sb.append("\": [ { ");
		sb.append("\"timestamp\": \"");
		sb.append(timeStr).append("Z");
		sb.append("\" , \"value\": \"");
		sb.append(value);
		sb.append("\" } ]");
	}

}
