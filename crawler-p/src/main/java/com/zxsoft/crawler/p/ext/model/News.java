/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.zxsoft.crawler.p.ext.model;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author hu
 */
public class News {

    protected String url = null;
    protected String title = null;
    protected StringBuffer content = null;
    protected String time = null;

    protected Element contentElement = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        if (content == null) {
            content = new StringBuffer();
            if (contentElement != null) {
                Elements links = contentElement.getElementsByTag("p");
                if (links != null && links.size() > 0) {
                    for (Element ele : links) {
                        content.append( ele.text()).append( "\n");
                    }
                } else {
                    content = new StringBuffer(contentElement.text());
                }
            }
        }
        return content.toString();
    }


    public void setContent(StringBuffer content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "URL:\n" + url + "\nTITLE:\n" + title + "\nTIME:\n" + time + "\n" +
                "\n" + getContent() + "\nCONTENT(SOURCE):\n" + contentElement;
    }

    public Element getContentElement() {
        return contentElement;
    }

    public void setContentElement(Element contentElement) {
        this.contentElement = contentElement;
    }


}
