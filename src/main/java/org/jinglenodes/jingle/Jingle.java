/*
 * Copyright (C) 2011 - Jingle Nodes - Yuilop - Neppo
 *
 *   This file is part of Switji (http://jinglenodes.org)
 *
 *   Switji is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Switji is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MjSip; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Author(s):
 *   Benhur Langoni (bhlangonijr@gmail.com)
 *   Thiago Camargo (barata7@gmail.com)
 */

package org.jinglenodes.jingle;

import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;
import org.jinglenodes.jingle.content.Content;


public class Jingle extends BaseElement {

    public final static String SESSION_INITIATE = "session-initiate";
    public final static String SESSION_TERMINATE = "session-terminate";
    public final static String SESSION_ACCEPT = "session-accept";
    public final static String CONTENT_MODIFY = "content-modify";
    public final static String CONTENT_ADD = "content-add";
    public final static String SESSION_INFO = "session-info";
    public final static String TRANSPORT_INFO = "transport-info";

    public enum Action {
        session_initiate, session_terminate, session_accept, content_modify, content_add, session_info, transport_info;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public final static String NAME = "jingle";
    public static final Namespace Q_NAMESPACE = new Namespace("", "urn:xmpp:jingle:1");

    private final String SID = "sid";
    private final String INITIATOR = "initiator";
    private final String RESPONDER = "responder";
    private final String ACTION = "action";

    public static final String NAMESPACE = "urn:xmpp:jingle:1";

    private Content content;
    private Reason reason;
    private Info info;

    public Jingle(String sid, String initiator, String responder, String action) {
        super(NAME);
        this.addAttribute("xmlns", NAMESPACE);
        this.addAttribute(SID, sid);
        this.addAttribute(INITIATOR, initiator);
        this.addAttribute(RESPONDER, responder);
        this.addAttribute(ACTION, action);
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public String getSid() {
        return this.attributeValue(SID);
    }

    public String getInitiator() {
        return this.attributeValue(INITIATOR);
    }

    public String getResponder() {
        return this.attributeValue(RESPONDER);
    }

    public String getAction() {
        return this.attributeValue(ACTION);
    }

    public String toString() {
        return this.asXML();
    }

    public void setInitiator(String initiator) {
        this.addAttribute(INITIATOR, initiator);
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public void setResponder(String responder) {
        this.addAttribute(RESPONDER, responder);
    }

    public Jingle clone() {
        return new Jingle(this.getSid(), this.getInitiator(), this.getResponder(), this.getAction());
    }
}
