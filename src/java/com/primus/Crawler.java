/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primus;

import java.io.IOException;
import static java.lang.System.out;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Windows
 */

/* works perfectly*/
public class Crawler {

    DB db;
    Document doc;

    public Crawler() {
        

    }

    //receives a url string and connects to the file and parses the document received
    public String fetchData(String url) {
        // parse into each link and print a passage from that webpage
        String content = null;
        try {
            doc = Jsoup.connect(url).get();
            String title = doc.title();
            System.out.println(title);

            Elements body = doc.select("body");         // links 

            content = body.text();

        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }

    public Elements fetchLinks(String url) {  // fetch links from the root web page and return the list of links in that root web page 
        Elements links = null;
        try {
            doc = Jsoup.connect(url).get();
            String title = doc.title();
            System.out.println(title);

            links = doc.select("a[href]");         // links 
            Elements media = doc.select("[src]");           // images
            Elements imports = doc.select("link[href]");        // other pointers

            // print the collected links from the root webpage
            print("\n There are %d Links ", links.size());
            for (Element link : links) {
                print(" **** a : <%s> (%s)", link.attr("abs:href"), trim(link.text(), 35));
            }

        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return links;
    }

    private void print(String msg, Object... args) {
        //To change body of generated methods, choose Tools | Templates.
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width - 1) + ".";

        } else {
            return s;
        }
    }
}
