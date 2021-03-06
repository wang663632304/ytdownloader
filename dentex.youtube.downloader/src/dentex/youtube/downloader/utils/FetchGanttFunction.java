package dentex.youtube.downloader.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

public class FetchGanttFunction {
	
	String DEBUG_TAG = "FetchGanttFunction";
	
	public String doFetch(boolean fallingback, String url) {
        try {
            return downloadUrl(fallingback, url);
        } catch (IOException e) {
        	Log.e(DEBUG_TAG, "doFetch: " + e.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> doFetch: ", e.getMessage(), e);
            return null;
        } catch (RuntimeException re) {
        	Log.e(DEBUG_TAG, "doFetch: " + re.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> doFetch: ", re.getMessage(), re);
	    	return null;
        }
    }

    private String downloadUrl(boolean fallingback, String myurl) throws IOException {
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpget = new HttpGet(myurl); 
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();    
    	String responseBody = httpclient.execute(httpget, responseHandler);
    	httpclient.getConnectionManager().shutdown();
    	if (!fallingback) {
        	return fetchDecipheringFunction(responseBody);
    	} else {
    		return responseBody;
    	}
	}

    private String fetchDecipheringFunction(String content) {
    	String function = null;
    	Pattern pattern = Pattern.compile("function decryptSignature.*?return sig", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
        	function = matcher.group() + "; }";
        }
        function = function.replace("\n", " ");
        function = function.replace("\t", " ");
        function = function.replace("\r", " ");
        function = function.replace("&#39;", "'");

        if (function == null) {
        	Log.e(DEBUG_TAG, "gantt's function: fetching error");
        } else {
        	//Log.d(DEBUG_TAG, ShareActivity.ganttFunction);
        	Utils.logger("d", "gantt's function: successfully fetched", DEBUG_TAG);
        }
    	return function;
    }
}
