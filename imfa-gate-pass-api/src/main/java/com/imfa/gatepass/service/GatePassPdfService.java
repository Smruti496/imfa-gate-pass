package com.imfa.gatepass.service;

import com.imfa.gatepass.model.GatePass;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
public class GatePassPdfService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter DISPLAY_DT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private static final Color HEADER_BG  = new Color(30, 41, 59);
    private static final Color LABEL_BG   = new Color(241, 245, 249);
    private static final Color BORDER_CLR = new Color(203, 213, 225);
    private static final Color WHITE      = Color.WHITE;

    public byte[] generate(GatePass pass) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font headerFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, WHITE);
            Font subFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(148, 163, 184));
            Font labelFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,  new Color(71, 85, 105));
            Font valueFont   = FontFactory.getFont(FontFactory.HELVETICA,      10, new Color(15, 23, 42));
            Font footerFont  = FontFactory.getFont(FontFactory.HELVETICA,      8,  new Color(148, 163, 184));

            // --- Header bar ---
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{3f, 1f});

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBackgroundColor(HEADER_BG);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setPadding(14);
            Paragraph titlePara = new Paragraph("IMFA GATE PASS", headerFont);
            titlePara.add(Chunk.NEWLINE);
            titlePara.add(new Chunk(pass.getPassNo() != null ? pass.getPassNo() : "", subFont));
            titleCell.addElement(titlePara);
            header.addCell(titleCell);

            // Photo cell
            PdfPCell photoCell = new PdfPCell();
            photoCell.setBackgroundColor(HEADER_BG);
            photoCell.setBorder(Rectangle.NO_BORDER);
            photoCell.setPadding(10);
            photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            if (pass.getPhoto() != null && !pass.getPhoto().isBlank()) {
                try {
                    String b64 = pass.getPhoto().contains(",")
                        ? pass.getPhoto().substring(pass.getPhoto().indexOf(',') + 1)
                        : pass.getPhoto();
                    byte[] imgBytes = Base64.getDecoder().decode(b64);
                    Image photo = Image.getInstance(imgBytes);
                    photo.scaleToFit(80, 90);
                    photoCell.addElement(photo);
                } catch (Exception e) {
                    log.warn("PDF photo decode failed for pass {}: {}", pass.getPassNo(), e.getMessage());
                    Font noPhotoFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(148, 163, 184));
                    photoCell.addElement(new Paragraph("No photo", noPhotoFont));
                }
            } else {
                Font noPhotoFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(148, 163, 184));
                photoCell.addElement(new Paragraph("No photo", noPhotoFont));
            }
            header.addCell(photoCell);
            doc.add(header);

            doc.add(new Paragraph(" "));

            // --- Details table ---
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.6f});
            table.setSpacingBefore(4f);

            addRow(table, "Visitor Name",  pass.getVisitorName(),  labelFont, valueFont);
            addRow(table, "Company",       pass.getCompanyName(),  labelFont, valueFont);
            addRow(table, "Whom to Visit", pass.getWhomToVisit(),  labelFont, valueFont);
            addRow(table, "Purpose",       pass.getPurpose(),      labelFont, valueFont);
            addRow(table, "ID Type",       pass.getPhotoIdType(),  labelFont, valueFont);
            addRow(table, "Gender",        pass.getGender(),       labelFont, valueFont);
            addRow(table, "Location",      pass.getLocation(),     labelFont, valueFont);
            addRow(table, "Gate",          pass.getGate(),         labelFont, valueFont);
            addRow(table, "Visit Date",    pass.getVisitDate(),    labelFont, valueFont);
            addRow(table, "Visit Time",    pass.getVisitTime(),    labelFont, valueFont);
            addRow(table, "Status",        formatStatus(pass.getStatus()), labelFont, valueFont);
            if (pass.getCheckInTime() != null)
                addRow(table, "Check-in Time", pass.getCheckInTime(), labelFont, valueFont);

            doc.add(table);

            // --- Footer ---
            doc.add(new Paragraph(" "));
            String generated = "Generated: " + LocalDateTime.now(IST).format(DISPLAY_DT) + " IST";
            Paragraph footer = new Paragraph(generated, footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            doc.add(footer);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed for pass " + pass.getPassNo(), e);
        }
    }

    private void addRow(PdfPTable table, String label, String value,
                        Font labelFont, Font valueFont) {
        PdfPCell lc = new PdfPCell(new Phrase(label, labelFont));
        lc.setBackgroundColor(LABEL_BG);
        lc.setBorderColor(BORDER_CLR);
        lc.setPadding(7);

        PdfPCell vc = new PdfPCell(new Phrase(value != null ? value : "—", valueFont));
        vc.setBackgroundColor(WHITE);
        vc.setBorderColor(BORDER_CLR);
        vc.setPadding(7);

        table.addCell(lc);
        table.addCell(vc);
    }

    private String formatStatus(String status) {
        if (status == null) return "—";
        return switch (status.toLowerCase()) {
            case "pending" -> "Pending";
            case "onsite"  -> "On Site";
            case "cleared" -> "Cleared";
            default        -> status;
        };
    }
}
