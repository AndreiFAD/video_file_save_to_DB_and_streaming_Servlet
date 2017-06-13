/*
 * Copyright (C) 2017 Fekete András Demeter
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.postgresql.jdbc.PgConnection;
/**
 *
 * @author Fekete András Demeter
 */
@WebServlet(urlPatterns = {"/radar_from_idokep_hu"})
public class weather_video extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try (ServletOutputStream out = response.getOutputStream()) {
            try {   
                   String url = "jdbc:postgresql://0.0.0.0:5432/database_name";
                   String username = "username";
                   String password = "password";
                   String DATABASE_DRIVER = "org.postgresql.Driver";
                   String sqlresults = "";
                   System.out.println("Connecting database...");

                   try {
                            Class.forName(DATABASE_DRIVER);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(ex.getMessage());
                        }
                   
                   PgConnection conn = null;
                   try {
                            conn = (PgConnection) DriverManager.getConnection(url,username,password);
                        } catch (SQLException ex) {
                             Logger.getLogger(ex.getMessage());
                        }
                   System.out.println("Database connection is active");
                   Statement stmt = conn.createStatement();
                   ResultSet rs ;
                   response.setIntHeader("Refresh", 12); 
                   try{
                            rs = stmt.executeQuery("select file_data from weather_video where id=(select max(id) from weather_video);");
                            while (rs.next()) {
                                try {
                                        byte[] fileBytes = (rs.getBytes("file_data"));
                                        InputStream in = new ByteArrayInputStream(fileBytes);
                                        String mimeType = "video/mp4 .mp4 ";
                                        byte[] bytes = new byte[4096];
                                        int bytesRead;
                                        response.setIntHeader("Refresh", 12);
                                        String responseRange = String.format("bytes %d-%d/%d", 1, 2, fileBytes.length);
                                        response.setHeader("Content-Range", responseRange);
                                        response.setDateHeader("Last-Modified", new Date().getTime());
                                        response.setHeader("Accept-Ranges", "bytes");
                                        response.setContentType(mimeType);
                                        while ((bytesRead = in.read(bytes)) != -1) {
                                            out.write(bytes, 0, bytesRead);
                                        }
                                        in.close();
                                        out.close();
                                    } catch (SQLException e) {
                                        System.out.println(e.getMessage());
                                        request.getRequestDispatcher("/weather_video").forward(request, response);
                                    }
                            }
                        } catch (SQLException e) {
                              conn.close();
                              System.out.println(e.getMessage());
                              request.getRequestDispatcher("/weather_video").forward(request, response);
                        }
                   conn.close();   
           } catch (SQLException ex) {
                request.getRequestDispatcher("/weather_video").forward(request, response);
           }

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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(weather_video.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(weather_video.class.getName()).log(Level.SEVERE, null, ex);
        }
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
