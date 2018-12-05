/**
 * CodeMirror.java
 *
 * Copyright (C) 2015 Sinnlabs LTD. All Rights Reserved.
 *
 * {{IS_RIGHT
 *   This program is distributed under LGPL Version 2.1 in the hope that
 *   it will be useful, but WITHOUT ANY WARRANTY.
 * }}IS_RIGHT
 */
package org.sinnlabs.zk.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Textbox;

/**
 * Class represents source editor component for ZK framework
 * @author peter.liverovsky
 *
 */
public class CodeMirror extends Textbox {

	/**
	 *
	 */
	private static final long serialVersionUID = -6278831344472300331L;

    public static final String TEXT_PLAIN = "text/plain";

	public static final String TEXT_HTML = "text/html";
	
	public static final String JAVA = "text/x-java";

	public static final String JAVASCRIPT = "text/javascript";

	public static final String C = "text/x-csrc";

	public static final String CPP = "text/x-c++src";

	public static final String OBJECTIVE_C = "text/x-objectivec";

	public static final String SCALA = "text/x-scala";

	public static final String KOTLIN = "text/x-kotlin";

	public static final String CEYLON = "text/x-ceylon";

	public static final String CSHARP = "text/x-csharp";

	public static final String JSON = "application/json";

	public static final String LD_JSON = "application/ld+json";

	public static final String TYPESCRIPT = "text/typescript";

	public static final String CSS = "text/css";

	public static final String SCSS = "text/x-scss";

	public static final String LESS = "text/x-less";

	public static final String XML = "application/xml";

	public static final String TIDDLY_WIKI = "text/x-tiddlywiki";

	public static final String TIKI_WIKI = "text/tiki";

	/**
	 * Based on codemirror\mode\meta.js
	 */
	private static final Map<String, Set<String>> modeExtensions = new HashMap<String, Set<String>>();

	private String _mode = TEXT_HTML; // as in CodeMirrorWidget.js

	private boolean _lineNumbers = false; // as in CodeMirrorWidget.js

	static {
	    Set<String> clikeExts = new HashSet<String>();
	    clikeExts.addAll(Arrays.asList(
	            /* C           */  "c", "h",
	            /* C++         */  "cpp", "c++", "cc", "cxx", "hpp", "h++", "hh", "hxx",
	            /* C#          */  "cs",
	            /* Java        */  "java",
	            /* Kotlin      */  "kt",
	            /* Objective C */  "m", "mm",
	            /* Scala       */  "scala",
	            /* Squirrel    */  "nut"
	    ));
	    modeExtensions.put("clike", clikeExts);


	    Set<String> cssExts = new HashSet<String>();
	    cssExts.addAll(Arrays.asList(
        	    /* Closure Stylesheets (GSS) */  "gss",
        	    /* CSS                       */  "css",
                /* LESS                      */  "less",
        	    /* SCSS                      */  "scss"
        ));
	    modeExtensions.put("css", cssExts);

	    Set<String> jsExts = new HashSet<String>();
	    jsExts.addAll(Arrays.asList(
	            /* JavaScript */  "js",
	            /* JSON       */  "json", "map",
	            /* JSON-LD    */  "jsonld",
	            /* TypeScript */  "ts"
	    ));
	    modeExtensions.put("javascript", jsExts);

	    Set<String> xmlExts = new HashSet<String>();
	    xmlExts.addAll(Arrays.asList(
	            /* XML */  "xml", "xsl", "xsd", "svg"
	    ));
	    modeExtensions.put("xml", xmlExts);


	    Set<String> htmlExts = new HashSet<String>();
        htmlExts.addAll(Arrays.asList(
                /* HTML */  "html", "htm"
        ));
        modeExtensions.put("htmlmixed", htmlExts);

        Set<String> tiddlywikiExts = new HashSet<String>();
        tiddlywikiExts.add("tiddlywiki");
        modeExtensions.put("tiddlywiki", tiddlywikiExts);

        Set<String> tikiExts = new HashSet<String>();
        tikiExts.addAll(Arrays.asList("tiki", "wiki"));
        modeExtensions.put("tiki", tikiExts);

	}

	public CodeMirror() {
		super();
		this.setZclass("z-div");
	}

	public CodeMirror(String value) {
		super(value);
		setZclass("z-div");
	}

	/**
	 * Returns syntax mode
	 * @return
	 */
	public String getMode() {
		return _mode;
	}

	/**
	 * Sets the syntax mode name, which either simply names the mode or is a MIME type associated with the mode.
	 * <br> File extension does NOT valid value for mode, use setSyntax() to heuristically map extension to codemirror's mode.
	 * @param name - Mode name
	 */
	public void setMode(String name) {
		_mode = name;
		smartUpdate("mode", _mode);
	}

	@Override
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		render(renderer, "mode", _mode);
		render(renderer, "lineNumbers", _lineNumbers);
	}

	private static String findModeByExtension(String extension) {
	    if (extension != null && !extension.isEmpty()) {
    	    for (Entry<String, Set<String>> i : modeExtensions.entrySet()) {
    	        Set<String> exts = i.getValue();
    	        if (exts.contains(extension)) return i.getKey();
    	    }
	    }
	    return TEXT_HTML;
	}

	/** Sets mode by file extension, for example: txt, html, ... */
	public void setSyntax(String extension) {
	    setMode(findModeByExtension(extension));
	}

    public boolean getLineNumbers() {
        return _lineNumbers;
    }

    public void setLineNumbers(boolean value) {
        _lineNumbers = value;
        smartUpdate("lineNumbers", _lineNumbers);
    }
}
