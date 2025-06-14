package com.haylaundry.service.backend.core.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PdfToImageConverter {

    public static byte[] convertPdfToImage(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(new ByteArrayInputStream(pdfBytes)))) {
            PDFRenderer renderer = new PDFRenderer(document);

            // Render first page at high DPI
            BufferedImage originalImage = renderer.renderImageWithDPI(0, 200);

            // Resize to 384x203 pixels (±58mm x 30mm)
            int dpi = 200;
            int targetWidth = mmToPixels(50, dpi);
            int targetHeight = mmToPixels(170, dpi);

            Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", output);

            return output.toByteArray();
        }
    }

    private static int mmToPixels(double mm, int dpi) {
        return (int) ((mm / 25.4) * dpi);
    }


}
