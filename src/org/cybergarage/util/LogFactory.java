package org.cybergarage.util;



public class LogFactory {
	private static final String TAG = "all_share";
	private static Log log = null;

	public static Log createLog() {
		if (log == null) {
    		log = new Log();
		}
		
		log.setTag(TAG);
		return log;
	}
	
	public static Log createLog(String tag) {
		if (log == null) {
			log = new Log();
		}
		
		if (tag == null || tag.length() < 1) {
    		log.setTag(TAG);
		} else {
			log.setTag(tag);
		}
		return log;
	}
	
	public static Log createNewLog(String tag){
		Log log = new Log();
		log.setTag(tag);
		return log;
	}
}