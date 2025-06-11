/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003-2004
*
*	File: Format.java
*
*	Revision;
*
*	01/12/04
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.server.object;

import java.io.*;

public interface Format
{
	FormatObject createObject(File file);
	boolean equals(File file);
	String getMimeType();
	String getMediaClass();
}



