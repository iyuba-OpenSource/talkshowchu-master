/******************************************************************
*
*	MediaServer for CyberLink
*
*	Copyright (C) Satoshi Konno 2003-2004
*
*	File: FormatObject.java
*
*	Revision;
*
*	01/12/04
*		- first revision.
*
******************************************************************/

package org.cybergarage.upnp.std.av.server.object;

import org.cybergarage.xml.*;

public interface FormatObject
{
	AttributeList getAttributeList();
	String getTitle();
	String getCreator();
}



