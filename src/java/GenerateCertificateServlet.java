import org.apache.pdfbox.pdmodel.*;
import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/GenerateCertificateServlet")
public class GenerateCertificateServlet extends HttpServlet {

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/OnlineLearningDB?zeroDateTimeBehavior=CONVERT_TO_NULL";
        String user = "root";
        String password = "12345";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);

            // Query to get user details and completed course
            String sql = "SELECT u.name, c.course_name, c.completion_date FROM users u " + "JOIN courses c ON u.id = c.user_id WHERE u.email = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                String userName = rs.getString("name");
                String courseName = rs.getString("course_name");
                String completionDate = rs.getString("completion_date");

                // Set response headers for PDF download
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=Certificate.pdf");

                // Create a PDF document
                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage();
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        // Title
                        contentStream.beginText();
                        contentStream.newLineAtOffset(150, 700);
                        contentStream.showText("Certificate of Completion");
                        contentStream.endText();

                        // User Details
                        contentStream.beginText();
                        contentStream.newLineAtOffset(100, 650);
                        contentStream.showText("This is to certify that");
                        contentStream.endText();

                        contentStream.beginText();
                        contentStream.newLineAtOffset(200, 620);
                        contentStream.showText(userName);
                        contentStream.endText();

                        contentStream.beginText();
                        contentStream.newLineAtOffset(100, 580);
                        contentStream.showText("has successfully completed the course");
                        contentStream.endText();

                        contentStream.beginText();
                        contentStream.newLineAtOffset(200, 550);
                        contentStream.showText(courseName);
                        contentStream.endText();

                        contentStream.beginText();
                        contentStream.newLineAtOffset(100, 510);
                        contentStream.showText("Date of Completion: " + completionDate);
                        contentStream.endText();
                    }

                    document.save(response.getOutputStream());
                }
            } else {
                response.getWriter().println("No certificate found for this email.");
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error generating certificate.");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}