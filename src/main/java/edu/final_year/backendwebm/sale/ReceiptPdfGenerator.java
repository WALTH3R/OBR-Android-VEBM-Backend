package edu.final_year.backendwebm.sale;

import edu.final_year.backendwebm.store.Store;
import edu.final_year.backendwebm.vault.NifEncryptionService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ReceiptPdfGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final NifEncryptionService nifEncryptionService;

    public byte[] generate(Sale sale) {
        PDFont regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float margin = 50f;
                float y = page.getMediaBox().getHeight() - margin;
                Store store = sale.getStore();

                y = writeLine(content, bold, 16, margin, y, store.getFiscalName());
                y = writeLine(content, regular, 10, margin, y, "NIF: " + nifEncryptionService.decrypt(store.getEncryptedNif()));
                y = writeLine(content, regular, 10, margin, y,
                        store.getCommune().getName() + ", " + store.getCommune().getProvince().getName());
                if (store.getPhone() != null) {
                    y = writeLine(content, regular, 10, margin, y, "Tel: " + store.getPhone());
                }

                y -= 10;
                y = writeLine(content, bold, 12, margin, y, "RECEIPT");
                y = writeLine(content, regular, 10, margin, y, "Sale: " + sale.getId());
                y = writeLine(content, regular, 10, margin, y, "Date: " + TIMESTAMP_FORMAT.format(sale.getCreatedAt()));

                if (sale.getClient() != null) {
                    y -= 5;
                    y = writeLine(content, bold, 10, margin, y, "Client");
                    y = writeLine(content, regular, 10, margin, y,
                            sale.getClient().getName() + " (" + sale.getClient().getClientType() + ")");
                    if (sale.getClient().getNif() != null) {
                        y = writeLine(content, regular, 10, margin, y, "NIF: " + sale.getClient().getNif());
                    }
                }

                y -= 10;
                y = writeLine(content, bold, 10, margin, y,
                        String.format("%-28s %6s %10s %10s", "Item", "Qty", "Unit", "Total"));
                y = writeLine(content, regular, 10, margin, y, "-".repeat(60));
                for (SaleItem item : sale.getItems()) {
                    y = writeLine(content, regular, 9, margin, y,
                            String.format("%-28s %6d %10s %10s",
                                    truncate(item.getProductName(), 28),
                                    item.getQuantity(),
                                    item.getUnitPrice().toPlainString(),
                                    item.getLineTotal().toPlainString()));
                }
                y = writeLine(content, regular, 10, margin, y, "-".repeat(60));

                y = writeLine(content, regular, 10, margin, y, "Subtotal: " + sale.getSubtotal().toPlainString());
                y = writeLine(content, regular, 10, margin, y, "TVA: " + sale.getTvaAmount().toPlainString());
                y = writeLine(content, bold, 11, margin, y,
                        "TOTAL: " + sale.getTotalAmount().toPlainString() + " " + store.getCurrency());

                y -= 10;
                y = writeLine(content, bold, 10, margin, y, "Payment");
                y = writeLine(content, regular, 10, margin, y, "Method: " + sale.getPaymentMethod());
                y = writeLine(content, regular, 10, margin, y, "Amount paid: " + sale.getPaymentAmount().toPlainString());
                if (sale.getPaymentReference() != null) {
                    y = writeLine(content, regular, 10, margin, y, "Reference: " + sale.getPaymentReference());
                }
                BigDecimal change = sale.getPaymentAmount().subtract(sale.getTotalAmount());
                if (change.signum() > 0) {
                    y = writeLine(content, regular, 10, margin, y, "Change due: " + change.toPlainString());
                }

                y -= 10;
                writeLine(content, regular, 8, margin, y,
                        sale.isObrSubmitted() ? "Submitted to OBR" : "Not yet submitted to OBR");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private float writeLine(PDPageContentStream content, PDFont font, float fontSize, float x, float y, String text)
            throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - (fontSize + 4);
    }

    private String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1) + "...";
    }
}
