/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primus;

import com.models.KeyWordModel;
import com.models.UrlModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLHandshakeException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Windows
 */
@WebServlet(name = "Report", urlPatterns = {"/Report"})
public class Report extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    Crawler crawler = new Crawler();

    DB db;
    DBKeywords dbKeywords;
    String keyword;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code.*/

            try {
                db = new DB();
                List<UrlModel> urls = db.getUrls();

                dbKeywords = new DBKeywords();
                List<KeyWordModel> keywords = dbKeywords.getKeywords();
                
                for (int k = 0; k < keywords.size(); k++) {   // loop through keywords from the keyword table and use them in the search
                    
                     keyword = keywords.get(k).getName();
                     
                    for (int j = 0; j < urls.size(); j++) {                                // loop through the urls from the database and crawl for the keyword
                        Elements linksFromRoot = crawler.fetchLinks(urls.get(j).getUrl());

                        System.out.println("Fetching links from each link");

                        // convert Elements to text
                        for (Element webpage : linksFromRoot) {
                            String pageUrl = String.format("%s ", webpage.attr("abs:href"));// fetch url of each link and convert it into a string
                            String pageText = String.format("%s ", webpage.text());   // fetch the text from each webpage

                            try {
                                //Document doc;
                                crawler.doc = Jsoup.connect(pageUrl)
                                        .ignoreContentType(true)
                                        .get();
                                Elements body = crawler.doc.select("body");  // select the text of the body of each webpage
                                String bodyContent = body.text();
                                String keywordContent = null;
                                int indexOfKeyword = 0;
                                int beginStop = 0;
                                int endStop = 0;
                                // look through bodyContent and take only the portion of the body that contains that text

                                if (!bodyContent.contains(keyword)) {
                                    // if the keyword is not contained then it should parse @param pageUrl and check if it exist inside the link
                                    // if it does not open this link and crawl through all the links to find it and display the content
                                    searchThroughUrl(pageUrl);
                                } else {
                                    indexOfKeyword = bodyContent.indexOf(keyword);

                                    // locating the first full stop before indexOfKeyword
                                    for (int i = indexOfKeyword - 1; i > 0; i--) {
                                        char charAtIndex = bodyContent.charAt(i);
                                        int isFullStop = Character.compare('.', charAtIndex);

                                        if (isFullStop == 0) {
                                            beginStop = i;
                                            // locating the first full stop before indexOfKeyword
                                            break;
                                        }
                                    }

                                    //locating the first full stop after indexOfKeyword
                                    for (int i = indexOfKeyword; i < bodyContent.length(); i++) {
                                        char charAtIndex = bodyContent.charAt(i);
                                        int isFullStop = Character.compare('.', charAtIndex);

                                        if (isFullStop == 0) {
                                            endStop = i;
                                            break;
                                        }
                                    }
                                    keywordContent = bodyContent.substring(beginStop, endStop);

                                    out.println(pageUrl);
                                    out.println(keywordContent);
                                    //out.println(crawler.fetchData(pageUrl));

                                }

                            } catch (SSLHandshakeException ex) {
                                System.out.println("There is a SSLHandshakeException");
                            } catch (UnsupportedMimeTypeException ex) {
                                System.out.println("There is an UnsupportedMimeTypeException so set the ignoreContentType of the doc to true");
                            } catch (HttpStatusException ex) {
                                System.out.println("There is a HttpStatusException possibly no internet connection");
                            } catch (SocketTimeoutException ex) {
                                System.out.println("There is a SocketTimeoutException possibly taking longer for the server to respond, internet is slow or connection turned off");

                            } catch (StringIndexOutOfBoundsException ex) {
                                System.out.println("There is a StringIndexOutOfBoundsException");
                            } catch (ConnectException ex) {
                                System.out.println("There is a ConnectException");
                            } catch (IllegalStateException ex) {
                                System.out.println("There is a IllegalStateException");
                            } catch (MalformedURLException ex) {
                                System.out.println("There is a MalformedURLException");
                            } catch (UnknownHostException ex) {
                                System.out.println("There is an UnknownHostException possibly internet connection was turned off");
                            }
                        }

                        for (int i = 0; i < urls.size(); i++) {
                            out.print("No connection to database so the return is null  ");
                            out.println(urls.get(i).getName()); // from the database so later
                            out.print(urls.get(i).getUrl());

                            break;
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void searchThroughUrl(String url) throws IOException {
        try {
            db = new DB();

            Elements linksFromRoot = crawler.fetchLinks(url);

            System.out.println("Fetching links from each link");

            // convert Elements to text
            for (Element webpage : linksFromRoot) {
                String pageUrl = String.format("%s ", webpage.attr("abs:href"));// fetch url of each link and convert it into a string
                String pageText = String.format("%s ", webpage.text());   // fetch the text from each webpage

                try {
                    //Document doc;
                    crawler.doc = Jsoup.connect(pageUrl)
                            .ignoreContentType(true)
                            .get();
                    Elements body = crawler.doc.select("body");  // select the text of the body of each webpage
                    String bodyContent = body.text();
                    String keywordContent = "";
                    int indexOfKeyword = 0;
                    int beginStop = 0;
                    int endStop = 0;

                    if (!bodyContent.contains(keyword)) {
                        // if the keyword is not contained then it should parse @param pageUrl and check if it exist inside the link
                        // if it does not open this link and crawl through all the links to find it and display the content
                        // searchThroughUrl(pageUrl);
                    } else {
                        indexOfKeyword = bodyContent.indexOf(keyword);

                        // locating the first full stop before indexOfKeyword
                        for (int i = indexOfKeyword - 1; i > 0; i--) {
                            char charAtIndex = bodyContent.charAt(i);
                            int isFullStop = Character.compare('.', charAtIndex);

                            if (isFullStop == 0) {
                                beginStop = i;
                                // locating the first full stop before indexOfKeyword
                                break;
                            }
                        }

                        //locating the first full stop after indexOfKeyword
                        for (int i = indexOfKeyword; i < bodyContent.length(); i++) {
                            char charAtIndex = bodyContent.charAt(i);
                            int isFullStop = Character.compare('.', charAtIndex);

                            if (isFullStop == 0) {
                                endStop = i;
                                break;
                            }
                        }
                        keywordContent = bodyContent.substring(beginStop, endStop);

                        System.out.println(pageUrl);
                        System.out.println(keywordContent);
                        //System.out.println(crawler.fetchData(pageUrl));
                    }

                } catch (SSLHandshakeException ex) {
                    System.out.println("There is a SSLHandshakeException");
                } catch (UnsupportedMimeTypeException ex) {
                    System.out.println("There is an UnsupportedMimeTypeException");
                } catch (HttpStatusException ex) {
                    System.out.println("There is a HttpStatusException possibly no internet connection");
                } catch (SocketTimeoutException ex) {
                    System.out.println("There is a SocketTimeoutException possibly taking longer for the server to respond");

                } catch (StringIndexOutOfBoundsException ex) {
                    System.out.println("There is a StringIndexOutOfBoundsException");
                } catch (ConnectException ex) {
                    System.out.println("There is a ConnectException");
                } catch (IllegalStateException ex) {
                    System.out.println("There is a IllegalStateException");
                } catch (UnknownHostException ex) {
                    System.out.println("There is an UnknownHostException");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
