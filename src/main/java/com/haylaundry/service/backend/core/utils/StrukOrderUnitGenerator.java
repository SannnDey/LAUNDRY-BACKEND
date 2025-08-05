package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.DetailOrderUnitResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.BaseColor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class StrukOrderUnitGenerator {

    public static byte[] generateStrukPesananSatuan(
            String noFaktur,
            String namaCustomer,
            String tanggalMasuk,
            String statusBayar,
            String statusOrder,
            Map<String, List<Item>> kategoriItemMap,
            String totalHarga
    ) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Rectangle pageSize = new Rectangle(226, 567); // 80mm x 200mm
        Document document = new Document(pageSize, 3, 3, 10, 10);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Font setup
            BaseColor darkBlue = new BaseColor(0, 51, 102);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
            Font normalBoldFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
            Font normalBoldFont1 = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, darkBlue);
            Font footerBoldFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, darkBlue);

            // Logo
            InputStream logoStream = StrukOrderGenerator.class.getResourceAsStream("/logo1.jpg");
            if (logoStream != null) {
                Image logo = Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            }

            Paragraph title = new Paragraph("HAY LAUNDRY", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            addSeparatorLine(document);

            Paragraph subTitle = new Paragraph("NOTA LAUNDRY", headerFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            subTitle.setSpacingBefore(4);
            subTitle.setSpacingAfter(4);
            document.add(subTitle);

            // Info pelanggan & order
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{4, 6});
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            infoTable.addCell(createAlignedCell("NO FAKTUR", normalBoldFont, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(noFaktur, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("TANGGAL MASUK", normalBoldFont, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(tanggalMasuk, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("NAMA PELANGGAN", normalBoldFont, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell(namaCustomer, normalFont, Element.ALIGN_RIGHT));

            infoTable.addCell(createAlignedCell("KASIR", normalBoldFont, Element.ALIGN_LEFT));
            infoTable.addCell(createAlignedCell("HAY LAUNDRY", normalFont, Element.ALIGN_RIGHT));

            document.add(infoTable);

            addSeparatorLine(document);

            PdfPTable itemTable = new PdfPTable(3);
            itemTable.setWidthPercentage(100);
            itemTable.setWidths(new float[]{5f, 1.5f, 3f});
            itemTable.setSplitLate(false);

            itemTable.addCell(createHeaderCell("Item", normalBoldFont));
            itemTable.addCell(createHeaderCell("Qty", normalBoldFont));
            itemTable.addCell(createHeaderCell("Harga", normalBoldFont));

            for (Map.Entry<String, List<Item>> entry : kategoriItemMap.entrySet()) {
                List<Item> items = entry.getValue();

                for (Item item : items) {
                    itemTable.addCell(new PdfPCell(new Phrase(item.getNamaItem(), normalFont)));

                    PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQty()), normalFont));
                    qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    itemTable.addCell(qtyCell);

                    PdfPCell hargaCell = new PdfPCell(new Phrase("Rp. " + item.getHarga(), normalFont));
                    hargaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemTable.addCell(hargaCell);
                }
            }

            document.add(itemTable);

            addSeparatorLine(document);

            // Total harga
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{7, 3});
            totalTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", normalBoldFont1));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalLabelCell.setPaddingBottom(5);

            PdfPCell totalValueCell = new PdfPCell(new Phrase("Rp. " + totalHarga, normalBoldFont1));
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setPaddingBottom(5);

            totalTable.addCell(totalLabelCell);
            totalTable.addCell(totalValueCell);
            document.add(totalTable);

            addSeparatorLine(document);
            document.add(Chunk.NEWLINE);

            Paragraph statusOrderPar = new Paragraph("STATUS ORDER : " + statusOrder.toUpperCase(), normalBoldFont);
            statusOrderPar.setAlignment(Element.ALIGN_CENTER);
            document.add(statusOrderPar);

            Paragraph statusBayarPar = new Paragraph("STATUS BAYAR : " + statusBayar.toUpperCase(), normalBoldFont);
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
            footerAddress.setSpacingBefore(3);
            document.add(footerAddress);

            // Kontak (tidak diubah)
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

    public static class Item {
        private String namaItem;
        private int qty;
        private String harga;

        public Item(String namaItem, int qty, String harga) {
            this.namaItem = namaItem;
            this.qty = qty;
            this.harga = harga;
        }

        public String getNamaItem() { return namaItem; }
        public int getQty() { return qty; }
        public String getHarga() { return harga; }
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

    private static PdfPCell createHeaderCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private static void addSeparatorLine(Document document) throws DocumentException {
        com.itextpdf.text.pdf.draw.LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.BLACK);
        line.setPercentage(100);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }
}
