package org.cybergarage.util;

import java.io.*;
import org.cybergarage.util.Log;

public class Log {
	public class LogLevel {
		public int VERBOSE = 1;
		public int DEBUG = 2;
		public int WARN = 3;
		public int INFO = 4;
		public int ERROR = 5;
	};

	private String tag = "CommonLog";
	public static boolean isDebug = true;
	LogLevel Log = new LogLevel();
	public static int logLevel = 1;

	public Log() { }
	
	public Log(String tag) {
		this.tag = tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        
        if (sts == null) {
            return null;
        }

        for (StackTraceElement st:sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }

            return "["+Thread.currentThread().getId()+": "+st.getFileName()+":"+st.getLineNumber()+"]";
        }
        
        return null;
	}

	public void info(Object str) {
	    if (logLevel <= Log.INFO) {	        
	        String name = getFunctionName();
	        String ls=(name==null?str.toString():(name+" - "+str));
	        System.out.println(tag + ls);
	    }
	}
	
	public void i(Object str) {
		if (isDebug) {
			info(str);
		}
	}
	
	public void verbose(Object str) {
        if (logLevel <= Log.VERBOSE) {
            String name = getFunctionName();
            String ls=(name==null?str.toString():(name+" - "+str));
            System.out.println(tag + ls);
        }
	}
	
	public void v(Object str) {
		if (isDebug) {
			verbose(str);
		}
    }
	
	public void warn(Object str) {
	    if (logLevel <= Log.WARN) {
            String name = getFunctionName();
            String ls=(name==null?str.toString():(name+" - "+str));
            System.out.println(tag + ls);
	    }
	}
	
	public void w(Object str) {
		if (isDebug) {
			warn(str);
		}
    }
	
	public void error(Object str) {
        if (logLevel <= Log.ERROR) {            
            String name = getFunctionName();
            String ls=(name==null?str.toString():(name+" - "+str));
            System.out.println(tag + ls);
        }
	}
	
	public void error(Exception ex) {
	    if (logLevel <= Log.ERROR) {
	        StringBuffer sb = new StringBuffer();
	        String name = getFunctionName();
	        StackTraceElement[] sts = ex.getStackTrace();

	        if (name != null) {
                sb.append(name+" - "+ex+"\r\n");
            } else {
                sb.append(ex+"\r\n");
            }
	        
	        if (sts != null && sts.length > 0) {
	            for (StackTraceElement st:sts) {
	                if (st != null) {
	                    sb.append("[ "+st.getFileName()+":"+st.getLineNumber()+" ]\r\n");
	                }
	            }
	        }

	        System.out.println(tag + sb.toString());
	    }
	}
	
    public void e(Object str) {
    	if (isDebug) {
    		error(str);
    	}
    }

    public void e(Exception ex) {
    	if (isDebug) {
    		error(ex);
    	}
    }
	
	public void debug(Object str) {
        if (logLevel <= Log.DEBUG) {
            String name = getFunctionName();
            String ls = (name == null?str.toString():(name+" - "+str));
            System.out.println(tag + ls);
        }
	}
	
	public void d(Object str) {
		if (isDebug) {
			debug(str);
		}
    }
}