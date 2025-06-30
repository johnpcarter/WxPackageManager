package wx.packages.manager._priv;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ServiceThread;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import com.softwareag.is.dynamicvariables.DynamicVariablesEncryptor;
import com.softwareag.util.IDataMap;
import com.wm.util.JournalLogger;
// --- <<IS-END-IMPORTS>> ---

public final class tools

{
	// ---( internal utility methods )---

	final static tools _instance = new tools();

	static tools _newInstance() { return new tools(); }

	static tools _cast(Object o) { return (tools)o; }

	// ---( server methods )---




	public static final void addToArray (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(addToArray)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:0:required value
		// [i] - field:0:required @name
		// [i] - field:0:required *body
		// [i] record:1:optional inArray
		// [i] field:0:optional keyName
		// [i] field:0:optional bodyName
		// [o] record:1:required outArray
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		IData value = IDataUtil.getIData( pipelineCursor, "value" );
		
		String name = null;
		String body = null;
		
		if (value != null) {
			IDataCursor valueCursor = value.getCursor();
			name = IDataUtil.getString(valueCursor, "@name");
			body = IDataUtil.getString(valueCursor, "*body");
			valueCursor.destroy();
		}
		
		IData[]	inArray = IDataUtil.getIDataArray(pipelineCursor, "inArray");
		
		String keyName = IDataUtil.getString(pipelineCursor, "keyName");
		String bodyName = IDataUtil.getString(pipelineCursor, "bodyName");
		
		// process
		
		IData[] outArray = inArray;
		
		if (name != null) {
			
			outArray = new IData[inArray != null ? inArray.length + 1 : 1];
			
			if (inArray != null) {
				for (int i = 0; i < inArray.length; i++) {
					outArray[i] = inArray[i];
				}
			}
			
			IData doc = IDataFactory.create();
			IDataCursor docCursor = doc.getCursor();
			
			if (keyName != null) 
				IDataUtil.put(docCursor, keyName, name);
			else 
				IDataUtil.put(docCursor, "name", name);
			
			if (bodyName != null)
				IDataUtil.put(docCursor, bodyName, body);
			else
				IDataUtil.put(docCursor, "body", body);
			
			docCursor.destroy();
			
			outArray[outArray.length-1] = doc;
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "outArray", outArray);
		pipelineCursor.destroy();
		
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void base64Decode (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(base64Decode)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required encodedData
		// [o] field:0:required decodedData
		IDataCursor cur = pipeline.getCursor();
		String input = IDataUtil.getString(cur, "encodedData");
		String decodedData = new String(Base64.getDecoder().decode(input)).replaceAll("\n", "");
		System.out.println("decoded: " + "'" + decodedData + "'");
		cur.insertAfter("decodedData", decodedData);
		// --- <<IS-END>> ---

                
	}



	public static final void countPackage (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(countPackage)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required registry
		// [i] field:0:required packageName
		// [o] field:0:required newCount
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String registry = IDataUtil.getString(pipelineCursor, "registry");
		String packageName = IDataUtil.getString(pipelineCursor, "packageName");
		
		// process
		
		String key = (registry != null ? registry : "default") + "-" + packageName;
		int newCount = 1;
		
		synchronized (_packageCounter) {
			if (_packageCounter.get(key) == null) {
				_packageCounter.put(key, 1);
			} else {
				newCount = _packageCounter.get(key) + 1;
				_packageCounter.put(key,  newCount);
			}
		}
		
		 
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "newCount", "" + newCount);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void decrypt (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(decrypt)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required encryptedText
		// [o] field:0:required text
		IDataCursor c = pipeline.getCursor();
		String data = IDataUtil.getString(c, "encryptedText");
		
		// process
		
		String text = null;
		try {
			text = DynamicVariablesEncryptor.instance().decryptData(data);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		// pipeline out
		
		if (text != null)
			IDataUtil.put(c, "text", text);
			
		// --- <<IS-END>> ---

                
	}



	public static final void defaultRegistry (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(defaultRegistry)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] field:0:required registry
		IDataCursor c = pipeline.getCursor();
		IDataUtil.put(c, "registry", _defaultRegistry);
		c.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getEnvironmentVar (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getEnvironmentVar)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		IDataCursor pipelineCursor = pipeline.getCursor();
		String envVarName = IDataUtil.getString(pipelineCursor, "envVarName" );
		pipelineCursor.destroy();
		String envVarValue = System.getenv(envVarName);
		
		IDataUtil.put(pipelineCursor, "envVarValue", envVarValue);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void getTaskId (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getTaskId)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] field:0:required taskId
		IDataCursor c = pipeline.getCursor();
		IDataUtil.put(c, "taskId", _taskId);
		c.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getTenantIdFromSession (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getTenantIdFromSession)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] field:0:required tenantId
		String tenantID = InvokeState.getCurrentState().getTenantID();
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "tenantId", tenantID);
		IDataUtil.put(pipelineCursor, "currState", InvokeState.getCurrentState().toString());
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void hackForExtractClaims (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(hackForExtractClaims)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] object:0:required claimsDetails
		// [i] field:0:required ref
		// [o] field:0:required value
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		Object claims = IDataUtil.get(pipelineCursor, "claimsDetails");
		String ref = IDataUtil.getString(pipelineCursor, "ref");
		
		// process
		
		String value = null;
		
		if (claims instanceof IData) {
			 IDataCursor c = ((IData) claims).getCursor();
			 value = IDataUtil.getString(c, ref);
			 c.destroy();
		} else {
			IData[] test = (IData[]) claims;
			for (int i=0; i<test.length; i++) {
				
				//IDataCursor dc = test[i].getCursor();
			 	//System.out.println("" + i + " - " + test[i].toString());
			 	IDataMap dm = new IDataMap(test[i]);
			 	if (dm.get("name").equals(ref)) {
			 		value = (String) dm.get("value");
			 	}
			}
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "value", value);
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void invokeAsync (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(invokeAsync)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required service
		// [i] record:0:required pipeline
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String service = IDataUtil.getString(pipelineCursor, "service");
		
		IData p = IDataUtil.getIData(pipelineCursor, "pipeline");
		
		/*if (p == null) {
			p = IDataFactory.create();
		}*/
		
		int index = service.indexOf(":");
		String ifc = "";
		
		if (index != -1) {
			ifc = service.substring(0, index);
			service = service.substring(index+1);
		}
		
		Service.doThreadInvoke(ifc, service, p);
		
		// pipeline out
			
		// --- <<IS-END>> ---

                
	}



	public static final void isInList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(isInList)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required trustedTags
		// [i] - field:0:required tag
		// [i] field:0:required tag
		// [o] object:0:required exists
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String tag = IDataUtil.getString(pipelineCursor, "tag");
		IData[]	trustedTags = IDataUtil.getIDataArray(pipelineCursor, "trustedTags");
		
		// process
		
		boolean found = false;
		
		if (trustedTags != null) {
			
			for (int i = 0; i < trustedTags.length; i++) {
				
				if (trustedTags[i] != null) {
					IDataCursor trustedTagsCursor = trustedTags[i].getCursor();
					String t = IDataUtil.getString(trustedTagsCursor, "tag");
					trustedTagsCursor.destroy();
				
					if (t != null && tag.equals(t)) {
						found = true;
						break;
					}
				}
			}
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "exists", found);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void logException (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logException)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] object:0:required exception
		// [i] object:1:required stacktrace
		IDataCursor cur = null;
		try {
		cur = pipeline.getCursor();
		Object e = IDataUtil.get(cur, "exception");
		if (!(e instanceof Throwable)) {
		return;
		}
		Exception exception = (Exception) e;
		Object stackTraceElements = IDataUtil.get(cur, "stacktrace");
		StackTraceElement[] stackTraceElementsArray = null;
		if (stackTraceElements.getClass().isArray()
		&& StackTraceElement.class.equals(stackTraceElements.getClass().getComponentType())) {
		stackTraceElementsArray = (StackTraceElement[]) stackTraceElements;
		}
		
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
		exception.printStackTrace(pw);
		JournalLogger.logError(JournalLogger.LOG_EXCEPTION, JournalLogger.ERROR,
			String.format("Exception [%s] occurred, message: [%s], %s", exception.getClass().toString(),
					exception.getMessage(), sw.toString()));
		
		} catch (IOException ioe) {
		throw new ServiceException("Unable to log exception");
		}
		} finally {
		if (cur != null) {
		cur.destroy();
		}
		}
		// --- <<IS-END>> ---

                
	}



	public static final void packageCount (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(packageCount)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required registry
		// [i] field:0:required packageName
		// [o] field:0:required count
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String registry = IDataUtil.getString(pipelineCursor, "registry");
		String packageName = IDataUtil.getString(pipelineCursor, "packageName");
		
		// process
		
		String key = (registry != null ? registry : "public") + "-" + packageName;
		int count = 0;
		
		if (_packageCounter.get(key) != null) {
		count = _packageCounter.get(key);
		}
		 
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "count", "" + count);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void packageCounts (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(packageCounts)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] record:1:required counts
		// [o] - field:0:required registry
		// [o] - field:0:required packageName
		// [o] - object:0:required count
		ArrayList<IData> counts = new ArrayList<IData>();
		
		for (String key : _packageCounter.keySet()) {
			String registry = key.substring(0, key.indexOf("-"));
			String packageName = key.substring(key.indexOf("-")+1);
			
			IData count = IDataFactory.create();
			IDataCursor cc = count.getCursor();
			IDataUtil.put(cc, "registry", registry);
			IDataUtil.put(cc,  "packageName", packageName);
			IDataUtil.put(cc,  "count", _packageCounter.get(key));
			cc.destroy();
			
			counts.add(count);		
		}
		
		_packageCounter.clear();
		
		// pipeline out
		
		IDataCursor c = pipeline.getCursor();
		IDataUtil.put(c, "counts", counts.toArray(new IData[counts.size()]));
		c.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void padDaysInDownloadHistory (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(padDaysInDownloadHistory)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required results
		// [i] - object:0:required TRACK_DATE
		// [i] - object:0:required DOWNLOAD_COUNT
		// [i] field:0:optional maxColumns
		// [i] field:0:optional maxPaddingInterval
		// [o] record:1:required paddedResults
		// [o] - field:0:required label
		// [o] - object:0:required value
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();	
		IData[] results = IDataUtil.getIDataArray(pipelineCursor, "results");
		String maxColumns = IDataUtil.getString(pipelineCursor, "maxColumns");
		String maxPad = IDataUtil.getString(pipelineCursor, "maxPadInterval");
		
		pipelineCursor.destroy();
		
		// process
		
		int maxCols = -1;
		int maxInterval = 5;
		
		try { maxCols = Integer.parseInt(maxColumns); } catch(Exception e) {}
		try { maxInterval = Integer.parseInt(maxPad); } catch(Exception e) {}
		
		ArrayList<IData> paddedResults = new ArrayList<IData>();
		
		for(int i = 0; i < results.length; i++) {
		
			IDataCursor c = results[i].getCursor();
			Date date = (Date) IDataUtil.get(c, "TRACK_DATE");
			int v = IDataUtil.getInt(c, "DOWNLOAD_COUNT", 0);
			c.destroy();
			
			paddedResults.add(makeValue(date, v));
			
			if (i+1 < results.length) {
				c = results[i+1].getCursor();
				Date nextDate = (Date) IDataUtil.get(c, "TRACK_DATE");
				c.destroy();
				
				
				int numDays = Period.between(convertDateToLocalDate(date), convertDateToLocalDate(nextDate)).getDays();
				boolean dotdot = false;
						
				if (numDays > maxInterval) {
					numDays = maxInterval;
					dotdot = true;
				}
				
				for (int z = 0; z < numDays-1; z++) {
					date = Date.from(date.toInstant().plus(1, ChronoUnit.DAYS));
					
					if (z == numDays-2 && dotdot) {
						paddedResults.add(makeValue("...", 0));
					} else {
						paddedResults.add(makeValue(date, 0));
					}
				}
			}			
		}
		
		// output
		
		if (maxCols != -1 && paddedResults.size() > maxCols) {
			IDataUtil.put(pipelineCursor, "paddedResults", paddedResults.subList(paddedResults.size() - maxCols, paddedResults.size()).toArray(new IData[paddedResults.size()]));
		} else {
			IDataUtil.put(pipelineCursor, "paddedResults", paddedResults.toArray(new IData[paddedResults.size()]));
		}
		
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void recordTaskId (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(recordTaskId)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required taskId
		IDataCursor c = pipeline.getCursor();
		_taskId = IDataUtil.getString(c, "taskId");
		c.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void retrieveOidcClaimsFromSession (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(retrieveOidcClaimsFromSession)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] record:0:required idToken
		// [o] record:0:required uiToken
		IData idToken = InvokeState.getCurrentIdTokenClaims();
		IData uiToken = InvokeState.getCurrentUserInfoTokenClaims();
		String ibmEmail = null;
		String ibmUniqueSecurityName = null;
		if(idToken != null) {
		IDataCursor idTokenCursor = idToken.getCursor();
		ibmEmail = IDataUtil.getString(idTokenCursor, "email");
		ibmUniqueSecurityName = IDataUtil.getString(idTokenCursor, "uniqueSecurityName");
		}
		
		IDataCursor cur = pipeline.getCursor();
		cur.insertAfter("idToken", idToken);
		cur.insertAfter("uiToken", uiToken);
		cur.insertAfter("ibmEmail", ibmEmail);
		cur.insertAfter("ibmUniqueSecurityName", ibmUniqueSecurityName);
		// --- <<IS-END>> ---

                
	}



	public static final void setDefaultRegistry (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setDefaultRegistry)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required registry
		IDataCursor c = pipeline.getCursor();
		String defaultRegistry = IDataUtil.getString(c, "registry");
		c.destroy();
		
		if (defaultRegistry != null && defaultRegistry.length() > 0) {
			_defaultRegistry = defaultRegistry;
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void splitUrl (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(splitUrl)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required url
		// [o] field:0:required owner
		// [o] field:0:required repo
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		String url = IDataUtil.getString(pipelineCursor, "url" );
		pipelineCursor.destroy();
		
		// process
		
		String owner = null;
		String repo = null;
		
		if (url != null) {
		
		String[] parts = splitUri(url);
		owner = parts[0];
		repo = parts[1];
		
		try {
		URL urlParsed = new URL(url);
		String githubApiHost = "api.github.com";
		String repoHost = urlParsed.getHost();
		if (!(repoHost.contains("github.com"))) {
		githubApiHost = String.format("%s/api/v3", repoHost);
		}
		
		JournalLogger.log(4,90,3,"[WPM]", "Selected Github REST API host: " + githubApiHost);
		
		// pipeline out
		IDataUtil.put(pipelineCursor, "githubApiHost", githubApiHost);
		IDataUtil.put(pipelineCursor, "repoHost", repoHost);
		} catch (MalformedURLException e) {
		IData input = IDataFactory.create();
		IDataCursor inputCursor = input.getCursor();
		IDataUtil.put(inputCursor, "exception", e);
		IDataUtil.put(inputCursor, "stacktrace", e.getStackTrace());
		inputCursor.destroy();
		logException(input);
		
		throw new ServiceException("Unable to construct GitHub API URL.");
		}
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "owner", owner);
		IDataUtil.put(pipelineCursor, "repo", repo);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void splitUsers (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(splitUsers)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required currentUsers
		// [i] - field:0:required USER
		// [i] field:1:required newUsers
		// [o] field:1:required usersToAdd
		// [o] field:1:required usersToRemove
		// pipeline in
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		IData[]	currentUsers = IDataUtil.getIDataArray(pipelineCursor, "currentUsers");
		String[] newUsers = IDataUtil.getStringArray(pipelineCursor, "newUsers");
		
		// process
		
		ArrayList<String> usersToAdd = new ArrayList<String>();
		ArrayList<String> usersToRemove = new ArrayList<String>();
		
		if ( currentUsers != null)
		{
			for (int i = 0; i < currentUsers.length; i++ )
			{
				String user = grabUser(currentUsers[i]);
				
				boolean found = false;
				for(int z = 0; z < newUsers.length; z++) {
					if (newUsers[z].equals(user)) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					usersToRemove.add(user);
				}
			}
			
			for(int i = 0; i < newUsers.length; i++) {
				boolean found = false;
				for(int z = 0; z < currentUsers.length; z++) {
		
					String user = grabUser(currentUsers[z]);
		
					if (newUsers[i].equals(user)) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					usersToAdd.add(newUsers[i]);
				}
			}
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "usersToAdd", usersToAdd.toArray(new String[usersToAdd.size()]));
		IDataUtil.put(pipelineCursor, "usersToRemove", usersToRemove.toArray(new String[usersToRemove.size()]));
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void truncateStringToNewLine (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(truncateStringToNewLine)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required inString
		// [o] field:0:required outString
		// pipeline in
		IDataCursor pipelineCursor = pipeline.getCursor();
		String inString = IDataUtil.getString(pipelineCursor, "inString");
		
		// process
		
		String outString = inString;
		
		if (outString.indexOf("\n") != -1) {
			outString = outString.substring(0, outString.indexOf("\n"));
		}
		
		// pipeline out
		
		IDataUtil.put(pipelineCursor, "outString", outString);
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static String _taskId = null;
	
	private static String _defaultRegistry = "public";
	
	private static HashMap<String, Integer> _packageCounter = new HashMap<String, Integer>();
	
	public static IData makeValue(Date date, int v) {
	
		return makeValue(formatDate(date), v);
	}
	
	public static IData makeValue(String label, int v) {
		
		IData value = IDataFactory.create();
		
		IDataCursor paddedResultsCursor = value.getCursor();
		IDataUtil.put(paddedResultsCursor, "label", label);
		IDataUtil.put(paddedResultsCursor, "value", v);
		paddedResultsCursor.destroy();
		
		return value;
	}
	
	
	public static LocalDate convertDateToLocalDate(Date date) {
	
		return Instant.ofEpochMilli(date.getTime())
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	
	public static final String TIMESTAMP_PATTERN = "dd-MMM";
	public static final DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
	
	public static String formatDate(Date date) {
		
		return FOMATTER.format(convertDateToLocalDate(date));
	}
	
	public static String[] splitUri(String urlString) {
	
		// https://github.com/SoftwareAG/wm-is-client.git		
	    URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
	    	throw new RuntimeException(String.format("%s is not a valid URL.",urlString));
		}	
	    
	    String path = url.getPath(); 
	    
	    String[] split = path.replaceFirst("/", "").split("/");
	    String malformedGithubUrlMessage = String.format("\"%s\" is not a valid GitHub repository URL. A valid GitHub URL is in the format https://<hostname>/<owner>/<repository>.git", url);
	    if (split.length != 2) {
	    	throw new RuntimeException(malformedGithubUrlMessage);
	    }
	    
	    if (!(split[1].endsWith(".git"))) {
	    	throw new RuntimeException(malformedGithubUrlMessage);
	    }
	    
		String repo = split[1].replace(".git", "");
		String owner = split[0];
					
		if (owner.indexOf(":") != -1) {
			owner = owner.substring(owner.indexOf(":")+1);
		}
		
		String[] out = new String[2];
		out[0] = owner;
		out[1] = repo;
		
		return out;
	}
	
	private static String grabUser(IData currentUser) {
		
		IDataCursor currentUsersCursor = currentUser.getCursor();
		String user = IDataUtil.getString(currentUsersCursor, "USER");
		currentUsersCursor.destroy();
		
		return user;
	}
	
	
	private static String findPath(String basePath, String file) {
	    
		File[] files = new File(basePath).listFiles();
		String foundPath;
	
		for (int i = 0; i < files.length; i++) {
						
			if (file.equals(files[i].getName())) {
				return files[i].getAbsolutePath();
			} else if (files[i].isDirectory()) {
				foundPath = findPath(files[i].getAbsolutePath(), file);
				
				if (foundPath != null) {
					return foundPath;
				}
			}
		}
		
		return null;
	}
	
	private static void deleteFiles(File dirPath) {
	      File filesList[] = dirPath.listFiles();
	      for(File file : filesList) {
	         if(file.isDirectory()) {
	        	 deleteFiles(file); 
	         }
	         
	         file.delete();
	      }
	   }
		
		
		
		
		
		
		
		
		
		
	// --- <<IS-END-SHARED>> ---
}

