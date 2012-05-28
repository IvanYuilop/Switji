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

package org.jinglenodes.jingle.description;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;

import java.util.List;

public class Description extends BaseElement {

    private static final String NAME = "description";
    private static final String MEDIA = "media";
    public static final String NAMESPACE = "urn:xmpp:jingle:apps:rtp:1";

    public Description(String media) {
        super(NAME, Namespace.get(NAMESPACE));
        this.addAttribute(MEDIA, media);
    }

    public void addPayload(final Payload payload) {
        this.add(payload);
    }

    public void addPayload(List<Payload> payloads) {
        for (Payload payload : payloads) {
            this.add(payload);
        }
    }

    public List<Payload> getPayloads() {
        return (List<Payload>) this.elements();
    }

    public String getMedia() {
        return this.attributeValue(MEDIA);
    }

    public static Description fromElement(Element element) {
        final List<Payload> payloadList = Payload.fromElement(element);
        final Description description = new Description(element.attributeValue("media"));
        description.addPayload(payloadList);
        return description;
    }
}

