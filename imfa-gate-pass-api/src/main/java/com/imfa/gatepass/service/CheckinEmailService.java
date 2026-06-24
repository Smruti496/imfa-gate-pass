package com.imfa.gatepass.service;

import com.imfa.gatepass.model.GatePass;
import com.imfa.gatepass.repository.GatePassRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.checkin.email.enabled", havingValue = "true")
public class CheckinEmailService {

    private final JavaMailSender mailSender;
    private final GatePassRepository repo;
    private final GatePassPdfService pdfService;

    @Value("${app.checkin.email.from}")
    private String fromAddress;

    public void notify(UUID passId, String passNo, List<String> emails, String pdfUrl) {
        if (emails == null || emails.isEmpty()) return;
        try {
            GatePass pass = repo.findById(passId).orElse(null);
            if (pass == null) {
                log.warn("Email skipped — pass not found: {}", passId);
                return;
            }
            byte[] pdfBytes = pdfService.generate(pass);
            String html = buildHtml(pass, pdfUrl);
            for (String toEmail : emails) {
                try {
                    MimeMessage msg = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                    helper.setFrom(fromAddress);
                    helper.setTo(toEmail);
                    helper.setSubject("IMFA Gate Pass — Check-In Confirmation | " + passNo);
                    helper.setText(html, true);
                    helper.addAttachment(passNo + ".pdf", new ByteArrayResource(pdfBytes), "application/pdf");
                    mailSender.send(msg);
                    log.info("Check-in email sent for pass {} to {}", passNo, toEmail);
                } catch (Exception e) {
                    log.warn("Check-in email failed for pass {} to {}: {}", passNo, toEmail, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Check-in email failed for pass {}: {}", passNo, e.getMessage());
        }
    }

    private String buildHtml(GatePass pass, String pdfUrl) {
        String status = pass.getStatus() != null ? pass.getStatus().toUpperCase() : "—";
        String statusColor = switch (status.toLowerCase()) {
            case "onsite"  -> "#16a34a";
            case "cleared" -> "#1d4ed8";
            default        -> "#b45309";
        };

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#f1f5f9;font-family:Helvetica,Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f1f5f9;padding:32px 0;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.08);">

                    <!-- Header -->
                    <tr>
                      <td style="background:#0f172a;padding:24px 32px;">
                        <p style="margin:0;color:#ffffff;font-size:22px;font-weight:700;letter-spacing:.5px;">
                          IMFA <span style="font-weight:300;color:#94a3b8;">GATE PASS</span>
                        </p>
                        <p style="margin:4px 0 0;color:#93c5fd;font-size:11px;">Indian Metals &amp; Ferro Alloys Ltd.</p>
                      </td>
                    </tr>

                    <!-- Pass number + status -->
                    <tr>
                      <td style="background:#1e293b;padding:12px 32px;display:flex;align-items:center;justify-content:space-between;">
                        <table width="100%%"><tr>
                          <td style="color:#f59e0b;font-size:13px;font-weight:700;">%s</td>
                          <td align="right">
                            <span style="background:%s;color:#fff;font-size:11px;font-weight:700;padding:4px 12px;border-radius:12px;">%s</span>
                          </td>
                        </tr></table>
                      </td>
                    </tr>

                    <!-- Visitor name band -->
                    <tr>
                      <td style="background:#f8fafc;border-left:4px solid #f59e0b;padding:16px 32px;">
                        <p style="margin:0;font-size:20px;font-weight:700;color:#0f172a;">%s</p>
                        <p style="margin:4px 0 0;font-size:12px;color:#64748b;">%s</p>
                      </td>
                    </tr>

                    <!-- Details -->
                    <tr><td style="padding:24px 32px;">
                      <table width="100%%" cellpadding="0" cellspacing="0">
                        %s
                      </table>
                    </td></tr>

                    <!-- CTA -->
                    <tr>
                      <td style="padding:0 32px 28px;" align="center">
                        <a href="%s" style="display:inline-block;background:#0f172a;color:#fff;text-decoration:none;padding:12px 28px;border-radius:6px;font-size:13px;font-weight:600;">
                          View / Download Gate Pass PDF
                        </a>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:16px 32px;">
                        <p style="margin:0;font-size:11px;color:#94a3b8;text-align:center;">
                          This is a system-generated email. Gate pass is valid only for the stated date and purpose.
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                pass.getPassNo() != null ? pass.getPassNo() : "—",
                statusColor,
                status,
                pass.getVisitorName() != null ? htmlEsc(pass.getVisitorName()) : "—",
                buildSubLine(pass),
                buildRows(pass),
                pdfUrl
            );
    }

    private String buildSubLine(GatePass pass) {
        StringBuilder sb = new StringBuilder();
        if (pass.getCompanyName() != null) sb.append(htmlEsc(pass.getCompanyName()));
        if (pass.getWhomToVisit() != null && !pass.getWhomToVisit().isBlank()
                && !pass.getWhomToVisit().equalsIgnoreCase(pass.getCompanyName()))
            sb.append("  &nbsp;·&nbsp;  ").append(htmlEsc(pass.getWhomToVisit()));
        return sb.toString();
    }

    private String buildRows(GatePass pass) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, "Purpose",        pass.getPurpose());
        appendRow(sb, "ID Type",        pass.getPhotoIdType());
        appendRow(sb, "Gender",         pass.getGender());
        appendRow(sb, "Location",       pass.getLocation());
        appendRow(sb, "Gate",           pass.getGate());
        appendRow(sb, "Visit Date",     pass.getVisitDate());
        appendRow(sb, "Visit Time",     pass.getVisitTime());
        if (pass.getCheckInTime() != null)
            appendRow(sb, "Check-in Time", pass.getCheckInTime());
        return sb.toString();
    }

    private void appendRow(StringBuilder sb, String label, String value) {
        sb.append("""
            <tr>
              <td style="padding:7px 10px;background:#f8fafc;border-left:3px solid #f59e0b;
                         font-size:11px;font-weight:700;color:#64748b;width:38%%;">%s</td>
              <td style="padding:7px 12px;border-bottom:1px solid #e2e8f0;
                         font-size:12px;color:#0f172a;">%s</td>
            </tr>
            """.formatted(label.toUpperCase(), value != null && !value.isBlank() ? htmlEsc(value) : "—"));
    }

    private String htmlEsc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
