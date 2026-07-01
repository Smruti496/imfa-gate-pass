package com.imfa.gatepass.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.imfa.gatepass.model.GatePass;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

@Slf4j
@Service
public class GatePassPdfService {

    @Value("${app.base-url}")
    private String baseUrl;

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter DISPLAY_DT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    // Palette
    private static final Color NAVY        = new Color(15,  23,  42);
    private static final Color NAVY_MID    = new Color(30,  41,  59);
    private static final Color ACCENT      = new Color(245, 158, 11);   // amber
    private static final Color BORDER_LITE = new Color(226, 232, 240);
    private static final Color MUTED       = new Color(100, 116, 139);
    private static final Color WHITE       = Color.WHITE;
    private static final Color VISITOR_BG  = new Color(248, 250, 252);
    private static final Color SEAL_GREEN  = new Color(22,  163,  74);
    private static final Color SEAL_BG     = new Color(240, 253, 244);
    private static final Color SEAL_BORDER = new Color(134, 239, 172);

    public byte[] generate(GatePass pass) {
        String passUrl  = baseUrl + "/api/gate-passes/" + pass.getId() + "/pdf";
        String issuedAt = LocalDateTime.now(IST).format(DISPLAY_DT) + " IST";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            // ── Fonts ──────────────────────────────────────────────────────────
            Font fBrand    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   22, WHITE);
            Font fBrandSub = FontFactory.getFont(FontFactory.HELVETICA,         9, new Color(148, 163, 184));
            Font fPassNo   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   11, ACCENT);
            Font fStatus   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   10, WHITE);
            Font fVisitor  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   18, NAVY);
            Font fCompany  = FontFactory.getFont(FontFactory.HELVETICA,        10, MUTED);
            Font fLabel    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    8, MUTED);
            Font fValue    = FontFactory.getFont(FontFactory.HELVETICA,         9, NAVY);
            Font fSealHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   11, SEAL_GREEN);
            Font fSealBody = FontFactory.getFont(FontFactory.HELVETICA,         8, new Color(21, 128, 61));
            Font fNote     = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, MUTED);

            // ── 1. HEADER ──────────────────────────────────────────────────────
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{3.2f, 1f});

            // Left: brand + pass no + status
            PdfPCell brandCell = new PdfPCell();
            brandCell.setBackgroundColor(NAVY);
            brandCell.setBorder(Rectangle.NO_BORDER);
            brandCell.setPaddingLeft(18); brandCell.setPaddingRight(12);
            brandCell.setPaddingTop(18);  brandCell.setPaddingBottom(14);

            Paragraph brandLine = new Paragraph();
            brandLine.add(new Chunk("JSW  ", fBrand));
            brandLine.add(new Chunk("GATE PASS",
                FontFactory.getFont(FontFactory.HELVETICA, 22, new Color(148, 163, 184))));
            brandCell.addElement(brandLine);

            Paragraph subLine = new Paragraph("JSW Ltd.", fBrandSub);
            subLine.setSpacingBefore(2);
            brandCell.addElement(subLine);

            // Separator
            Paragraph sep = new Paragraph(" ");
            sep.setSpacingAfter(4);
            brandCell.addElement(sep);

            // Pass number + status dot inline
            Color[] sc = statusColors(pass.getStatus());
            String statusLabel = formatStatus(pass.getStatus());
            Paragraph passLine = new Paragraph();
            passLine.add(new Chunk(pass.getPassNo() != null ? pass.getPassNo() : "—", fPassNo));
            passLine.add(new Chunk("   ●  ",
                FontFactory.getFont(FontFactory.ZAPFDINGBATS, 8, sc[1])));
            passLine.add(new Chunk(statusLabel,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, sc[1])));
            brandCell.addElement(passLine);
            header.addCell(brandCell);

            // Right: photo
            PdfPCell photoCell = new PdfPCell();
            photoCell.setBackgroundColor(NAVY_MID);
            photoCell.setBorder(Rectangle.NO_BORDER);
            photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            photoCell.setPadding(8);
            photoCell.addElement(buildPhotoElement(pass));
            header.addCell(photoCell);
            doc.add(header);

            // ── 2. VISITOR NAME BAND ──────────────────────────────────────────
            PdfPTable nameBand = new PdfPTable(1);
            nameBand.setWidthPercentage(100);

            PdfPCell nameCell = new PdfPCell();
            nameCell.setBackgroundColor(VISITOR_BG);
            nameCell.setBorderWidthTop(0);
            nameCell.setBorderWidthLeft(4f);
            nameCell.setBorderColorLeft(ACCENT);
            nameCell.setBorderWidthRight(0);
            nameCell.setBorderWidthBottom(1f);
            nameCell.setBorderColorBottom(BORDER_LITE);
            nameCell.setPaddingLeft(14); nameCell.setPaddingRight(14);
            nameCell.setPaddingTop(12);  nameCell.setPaddingBottom(10);

            Paragraph namePara = new Paragraph(
                pass.getVisitorName() != null ? pass.getVisitorName() : "—", fVisitor);
            nameCell.addElement(namePara);

            String companySub = "";
            if (pass.getCompanyName() != null) companySub += pass.getCompanyName();
            if (pass.getWhomToVisit() != null && !pass.getWhomToVisit().isBlank()
                    && !pass.getWhomToVisit().equalsIgnoreCase(pass.getCompanyName()))
                companySub += "   ·   " + pass.getWhomToVisit();
            if (!companySub.isBlank()) {
                Paragraph compPara = new Paragraph(companySub, fCompany);
                compPara.setSpacingBefore(2);
                nameCell.addElement(compPara);
            }
            nameBand.addCell(nameCell);
            doc.add(nameBand);

            // ── 3. DETAILS ────────────────────────────────────────────────────
            doc.add(spacer(8));
            PdfPTable details = new PdfPTable(2);
            details.setWidthPercentage(100);
            details.setWidths(new float[]{1f, 2f});

            addDetail(details, "PURPOSE",        pass.getPurpose());
            addDetail(details, "ID TYPE",        pass.getPhotoIdType());
            addDetail(details, "GENDER",         pass.getGender());
            addDetail(details, "LOCATION",       pass.getLocation());
            addDetail(details, "GATE",           pass.getGate());
            addDetail(details, "VISIT DATE",     pass.getVisitDate());
            addDetail(details, "VISIT TIME",     pass.getVisitTime());
            if (pass.getCheckInTime() != null)
                addDetail(details, "CHECK-IN TIME", pass.getCheckInTime());

            // Label/value fonts passed inline via row index for alternating shade
            doc.add(details);

            // ── 4. FOOTER: QR + SEAL ─────────────────────────────────────────
            doc.add(spacer(14));
            PdfPTable footer = new PdfPTable(2);
            footer.setWidthPercentage(100);
            footer.setWidths(new float[]{1f, 2.2f});

            // QR cell — inner 2-row table so alignment works correctly
            PdfPTable qrInner = new PdfPTable(1);
            qrInner.setWidthPercentage(100);

            try {
                byte[] qrBytes = generateQrCode(passUrl, 120);
                Image qrImg = Image.getInstance(qrBytes);
                qrImg.scaleAbsolute(95, 95);
                qrImg.setAlignment(Image.MIDDLE);

                PdfPCell qrImgCell = new PdfPCell();
                qrImgCell.setBorder(Rectangle.NO_BORDER);
                qrImgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qrImgCell.setPaddingTop(10); qrImgCell.setPaddingBottom(4);
                qrImgCell.addElement(qrImg);
                qrInner.addCell(qrImgCell);
            } catch (Exception e) {
                log.warn("QR generation failed: {}", e.getMessage());
                PdfPCell errCell = new PdfPCell(new Phrase("QR unavailable", fNote));
                errCell.setBorder(Rectangle.NO_BORDER);
                errCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qrInner.addCell(errCell);
            }

            PdfPCell scanLblCell = new PdfPCell(new Phrase("Scan to verify", fNote));
            scanLblCell.setBorder(Rectangle.NO_BORDER);
            scanLblCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            scanLblCell.setPaddingBottom(10);
            qrInner.addCell(scanLblCell);

            PdfPCell qrCell = new PdfPCell();
            qrCell.setBackgroundColor(new Color(248, 250, 252));
            qrCell.setBorderColor(BORDER_LITE);
            qrCell.setPadding(0);
            qrCell.addElement(qrInner);
            footer.addCell(qrCell);

            // Seal cell
            PdfPCell sealCell = new PdfPCell();
            sealCell.setBackgroundColor(SEAL_BG);
            sealCell.setBorderColor(SEAL_BORDER);
            sealCell.setPaddingLeft(18); sealCell.setPaddingRight(14);
            sealCell.setPaddingTop(14);  sealCell.setPaddingBottom(14);
            sealCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph sealTitle = new Paragraph("✓  DIGITALLY ISSUED", fSealHead);
            sealTitle.setSpacingAfter(8);
            sealCell.addElement(sealTitle);

            sealCell.addElement(sealLine("Issued by",    "Security Department, JSW Ltd.", fSealBody));
            sealCell.addElement(sealLine("Date & Time",  issuedAt,                         fSealBody));
            sealCell.addElement(sealLine("Document",     pass.getPassNo() != null ? pass.getPassNo() : "—", fSealBody));

            Paragraph note = new Paragraph(
                "This pass is system-generated and digitally verified.\nValid only for the stated date and purpose.",
                fNote);
            note.setSpacingBefore(10);
            sealCell.addElement(note);
            footer.addCell(sealCell);
            doc.add(footer);

            // ── 5. DIAGONAL WATERMARK ─────────────────────────────────────────
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.saveState();
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.035f);
            canvas.setGState(gs);
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, false);
            canvas.setColorFill(NAVY);
            canvas.beginText();
            canvas.setFontAndSize(bf, 80);
            canvas.showTextAligned(Element.ALIGN_CENTER, "JSW",
                PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2, 35);
            canvas.endText();
            canvas.restoreState();

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed for pass " + pass.getPassNo(), e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Element buildPhotoElement(GatePass pass) {
        Font noPhoto = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(148, 163, 184));
        if (pass.getPhoto() == null || pass.getPhoto().isBlank())
            return new Paragraph("No\nphoto", noPhoto);
        try {
            String b64 = pass.getPhoto().contains(",")
                ? pass.getPhoto().substring(pass.getPhoto().indexOf(',') + 1)
                : pass.getPhoto();
            Image img = Image.getInstance(Base64.getDecoder().decode(b64));
            img.scaleToFit(82, 96);
            return img;
        } catch (Exception e) {
            log.warn("Photo decode failed for {}: {}", pass.getPassNo(), e.getMessage());
            return new Paragraph("No\nphoto", noPhoto);
        }
    }

    private void addDetail(PdfPTable table, String label, String value) {
        Font fL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, MUTED);
        Font fV = FontFactory.getFont(FontFactory.HELVETICA,      9, NAVY);

        PdfPCell lc = new PdfPCell(new Phrase(label, fL));
        lc.setBorderWidthTop(0); lc.setBorderWidthRight(0);
        lc.setBorderWidthLeft(3f); lc.setBorderColorLeft(ACCENT);
        lc.setBorderWidthBottom(1f); lc.setBorderColorBottom(BORDER_LITE);
        lc.setPaddingLeft(10); lc.setPaddingTop(7);
        lc.setPaddingBottom(7); lc.setPaddingRight(8);
        lc.setBackgroundColor(new Color(248, 250, 252));

        PdfPCell vc = new PdfPCell(new Phrase(value != null && !value.isBlank() ? value : "—", fV));
        vc.setBorderWidthTop(0); vc.setBorderWidthLeft(0); vc.setBorderWidthRight(0);
        vc.setBorderWidthBottom(1f); vc.setBorderColorBottom(BORDER_LITE);
        vc.setPaddingLeft(12); vc.setPaddingTop(7);
        vc.setPaddingBottom(7); vc.setPaddingRight(8);
        vc.setBackgroundColor(WHITE);

        table.addCell(lc);
        table.addCell(vc);
    }

    private Paragraph sealLine(String key, String val,  Font font) {
        Font keyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(21, 128, 61));
        Paragraph p = new Paragraph();
        p.add(new Chunk(key + ":  ", keyFont));
        p.add(new Chunk(val, font));
        p.setSpacingBefore(3);
        return p;
    }

    private Paragraph spacer(float pts) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(pts);
        return p;
    }

    private String formatStatus(String status) {
        if (status == null) return "UNKNOWN";
        return switch (status.toLowerCase()) {
            case "pending" -> "PENDING";
            case "onsite"  -> "ON SITE";
            case "cleared" -> "CLEARED";
            default        -> status.toUpperCase();
        };
    }

    private Color[] statusColors(String status) {
        if (status == null) return new Color[]{MUTED, WHITE};
        return switch (status.toLowerCase()) {
            case "pending" -> new Color[]{new Color(180, 83, 9),  new Color(254, 243, 199)};
            case "onsite"  -> new Color[]{new Color(21,  128, 61), new Color(220, 252, 231)};
            case "cleared" -> new Color[]{new Color(29,  78,  216), new Color(219, 234, 254)};
            default        -> new Color[]{MUTED, WHITE};
        };
    }

    private byte[] generateQrCode(String content, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage img = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }
}
