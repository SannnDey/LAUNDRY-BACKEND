package com.haylaundry.service.backend.core.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StrukOrderGenerator {

    public static byte[] generateStruk(
            String noFaktur,
            String namaCustomer,
            String qty,
            String harga,
            String tanggalMasuk,
            String statusBayar,
            String statusOrder,
            String jenisLayanan
    ) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Rectangle pageSize = new Rectangle(226, 567); // 80mm x 200mm
        Document document = new Document(pageSize, 3, 3, 10, 10);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            BaseColor darkBlue = new BaseColor(0, 51, 102);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            Font normalFont1 = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font normalFont2 = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font normalFont3 = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, darkBlue);
            Font footerBoldFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, darkBlue);

            // Logo
            InputStream logoStream = StrukOrderGenerator.class.getResourceAsStream("/logo1.jpg");
            if (logoStream != null) {
                Image logo = Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(90, 90);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            }

            Paragraph title = new Paragraph("HAY LAUNDRY", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            addSeparatorLine(document);

            Paragraph subTitle = new Paragraph("NOTA LAUNDRY", headerFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            subTitle.setSpacingBefore(5);
            document.add(subTitle);

            document.add(Chunk.NEWLINE);

            // Info pelanggan
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{4, 6});

            infoTable.addCell(createAlignedCell("NO FAKTUR", normalFont2, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(noFaktur, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("TANGGAL MASUK", normalFont2, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(tanggalMasuk, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("NAMA PELANGGAN", normalFont2, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(namaCustomer, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("KASIR", normalFont2, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell("Hay Laundry", normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("LAYANAN", normalFont2, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(jenisLayanan, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("QTY", normalFont2, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(qty, normalFont, Element.ALIGN_RIGHT));

            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            addSeparatorLine(document);

            // Total
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{7, 3});

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", normalFont3));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalLabelCell.setPaddingBottom(5);

            PdfPCell totalValueCell = new PdfPCell(new Phrase("Rp " + harga, normalFont3));
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setPaddingBottom(5);

            totalTable.addCell(totalLabelCell);
            totalTable.addCell(totalValueCell);

            document.add(totalTable);
            addSeparatorLine(document);
            document.add(Chunk.NEWLINE);

            // Status
            Paragraph statusOrderPar = new Paragraph("STATUS ORDER : " + statusOrder.toUpperCase(), normalFont1);
            statusOrderPar.setAlignment(Element.ALIGN_CENTER);
            document.add(statusOrderPar);

            Paragraph statusBayarPar = new Paragraph("STATUS BAYAR : " + statusBayar.toUpperCase(), normalFont1);
            statusBayarPar.setAlignment(Element.ALIGN_CENTER);
            document.add(statusBayarPar);

            document.add(Chunk.NEWLINE);
            addSeparatorLine(document);

            // Footer
            Paragraph footerTitle = new Paragraph("HAY LAUNDRY", footerBoldFont);
            footerTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(footerTitle);

            Paragraph footerAddress = new Paragraph(
                    "Visit Us At\nJl. Perkutut No.30, Tuban, Kec. Kuta, Kabupaten Badung, Bali 39061",
                    footerFont);
            footerAddress.setAlignment(Element.ALIGN_CENTER);
            footerAddress.setSpacingBefore(4);
            document.add(footerAddress);

            document.add(Chunk.NEWLINE);

            // Kontak
            InputStream phoneIconStream = StrukOrderGenerator.class.getResourceAsStream("/phone_icon.png");
            InputStream igIconStream = StrukOrderGenerator.class.getResourceAsStream("/instagram_icon.png");

            PdfPTable contactTable = new PdfPTable(2);
            contactTable.setWidthPercentage(40);
            contactTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            contactTable.setWidths(new float[]{1, 6});
            contactTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            if (phoneIconStream != null) {
                Image phoneIcon = Image.getInstance(phoneIconStream.readAllBytes());
                phoneIcon.scaleToFit(12, 12);
                PdfPCell phoneIconCell = new PdfPCell(phoneIcon, false);
                phoneIconCell.setBorder(Rectangle.NO_BORDER);
                phoneIconCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                contactTable.addCell(phoneIconCell);
            } else {
                contactTable.addCell(createCell("", footerFont));
            }
            contactTable.addCell(createAlignedCell("089670177373", footerFont, Element.ALIGN_LEFT));

            if (igIconStream != null) {
                Image igIcon = Image.getInstance(igIconStream.readAllBytes());
                igIcon.scaleToFit(12, 12);
                PdfPCell igIconCell = new PdfPCell(igIcon, false);
                igIconCell.setBorder(Rectangle.NO_BORDER);
                igIconCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                contactTable.addCell(igIconCell);
            } else {
                contactTable.addCell(createCell("", footerFont));
            }
            contactTable.addCell(createAlignedCell("@haylaundrybali", footerFont, Element.ALIGN_LEFT));

            document.add(contactTable);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private static PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        return cell;
    }

    private static PdfPCell createAlignedCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private static void addSeparatorLine(Document document) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.BLACK);
        line.setPercentage(100);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }
}