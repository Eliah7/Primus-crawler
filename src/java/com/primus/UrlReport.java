/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primus;

import com.models.UrlModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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
@WebServlet(name = "UrlReport", urlPatterns = {"/UrlReport"})
public class UrlReport extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    Crawler crawler, crawler2;
    DB db;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            db = new DB();
            crawler = new Crawler();
            List<UrlModel> urls = db.getUrls();
            

            Elements linksFromRoot = crawler.fetchLinks("http://millardayo.com/");

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

                    if (!bodyContent.contains("Diamond")) {    //Diamond is a keyword
                        // if the keyword is not contained then it should parse @param pageUrl and check if it exist inside the link
                        // if it does not open this link and crawl through all the links to find it and display the content
                        //searchThroughUrl(pageUrl);
                    } else {
                       // out.println(pageUrl);
                        
                        //out.println(crawler.fetchData(pageUrl));
                    }

                    for(int i = 0;i<urls.size();i++){
                                out.println(urls.get(i).getUrl());
                                out.println(urls.get(i).getName());
                                out.println(urls.get(i).getPublisher_id());
                                out.println();
                                break;
                            }
                } catch (SSLHandshakeException ex) {
                    System.out.println("There is a SSLHandshakeException");
                } catch (UnsupportedMimeTypeException ex) {
                    System.out.println("There is an UnsupportedMimeTypeException so set the ignoreContentType of the doc to true");
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
                } catch (MalformedURLException ex) {
                    System.out.println("There is a MalformedURLException");
                }
            }

            for (int i = 0; i < urls.size(); i++) {
                out.print("No connection to database so the return is null  ");
                out.println(urls.get(i).getName()); // from the database so later
                out.print(urls.get(i).getUrl());

                break;
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
