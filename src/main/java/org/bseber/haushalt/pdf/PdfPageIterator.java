package org.bseber.haushalt.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PdfPageIterator implements Iterator<PdfPage> {

    private final PDDocument document;
    private int currentPage;

    public PdfPageIterator(PDDocument document) {
        this.document = document;
        this.currentPage = 0;
    }

    @Override
    public boolean hasNext() {
        return currentPage < document.getNumberOfPages();
    }

    @Override
    public PdfPage next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        currentPage++;

        try {
            return new PdfPage(extractLines(currentPage, document));
        } catch (IOException e) {
            throw new IllegalStateException("error while reading PDF", e);
        }
    }

    private static List<String> extractLines(int page, PDDocument document) throws IOException {
        final List<String> lines = new ArrayList<>();

        final PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setStartPage(page);
        textStripper.setEndPage(page);

        try (BufferedReader reader = new BufferedReader(new StringReader(textStripper.getText(document)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }
}
