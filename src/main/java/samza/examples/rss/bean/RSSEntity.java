/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package samza.examples.rss.bean;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.util.List;

@XmlRootElement(name="channel")
public class RSSEntity {
    @XmlElement(name ="item")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "RSSEntity{" +
                "items=" + items +
                '}';
    }

    public static void main(String[] args) throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(RSSEntity.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();
        String content = "<?xml version=\"1.0\" encoding=\"gb2312\"?><rss version=\"2.0\">"+
                "<channel>"
                +" <ttl>5</ttl>"
                +"<item>"
                +"<title><![CDATA[China pledges to participate in Ghana and sub-regional ...]]></title>"
                +"<source><![CDATA[新华网]]></source>"
                +"\t</item>"
                +"<item>"
                +"<title><![CDATA[China pledges to participate in Ghana and sub-regional ...]]></title>"
                +"<source><![CDATA[新华网]]></source>"
                +"\t</item>"
                +"</channel>";
        RSSEntity entity = (RSSEntity)um.unmarshal(new ByteArrayInputStream(content.getBytes()));
        System.out.println(entity.toString());
    }
}


class Item{
    @XmlElement
    private String title;
    @XmlElement
    private String source;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
