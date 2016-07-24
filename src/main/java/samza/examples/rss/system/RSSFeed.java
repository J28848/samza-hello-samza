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
package samza.examples.rss.system;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by J28848 on 7/24/16.
 */
public class RSSFeed {

    private Logger logger = Logger.getLogger(RSSFeed.class);
    private String url;
    private String keyword;


    public RSSFeed(String url,String keyword){
        this.url = url;
        this.keyword = keyword;
    }

    /**
     * download the rss content and push to kafka
     * @param listener
     */
    public void start(RSSFeedListener listener){
        logger.debug("RSSFeed system start to work!");
        if(listener != null){
            String content = download();
            RSSFeedEvent event = new RSSFeedEvent(content);
            listener.onEvent(event);
        }
    }
    private String download(){
        BufferedReader in = null;
        String content = null;
        try{
            this.keyword = java.net.URLEncoder.encode(this.keyword,"utf-8");
            this.url = this.url.replaceAll("\\{KEYWORD\\}",this.keyword);//replace the keyword with the config values.
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = httpClient.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"gb2312"));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            content = sb.toString();

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return content;
        }
    }


    public void stop(){
        logger.debug("RSSFeed system stop!");
    }
    public static interface RSSFeedListener{
        public void onEvent(RSSFeedEvent event);
    }
    public static class RSSFeedEvent{

        private String content;
        public RSSFeedEvent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }
    }

    public static void main(String[] args) {
        RSSFeed feed = new RSSFeed("http://news.baidu.com/ns?word=title%3A{KEYWORD}&tn=newsrss&sr=0&cl=2&rn=20&ct=0","北京");
        feed.start(new RSSFeedListener() {
            @Override
            public void onEvent(RSSFeedEvent event) {
                System.out.println(event.getContent());
            }
        });
        feed.stop();
    }
}
