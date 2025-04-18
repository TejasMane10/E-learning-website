import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class CertificateServlet extends HttpServlet {

    /**
     *a
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String courseName = request.getParameter("courseName");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"certificate.pdf\"");
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Certificate of Completion");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("This certifies that " + userName);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 620);
                contentStream.showText("has successfully completed the course: " + courseName);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 590);
                contentStream.showText("Date of Completion: " + Date.valueOf(LocalDate.now()));
                contentStream.endText();
            }

            OutputStream out = response.getOutputStream();
            document.save(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}