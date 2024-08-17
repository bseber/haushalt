package org.bseber.haushalt.transactions.importer;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.pdf.PdfPage;
import org.bseber.haushalt.pdf.PdfPageIterator;
import org.bseber.haushalt.transactions.NewTransaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransactionPdfReader implements TransactionFileReader {

    public record AccountOwner(String name, IBAN iban) {}

    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".pdf") && isPdfSupported(file);
    }

    /**
     * Called in {@linkplain #supports(File)} when file is a PDF.
     *
     * <p>
     * This method returns {@code true} by default.<br />
     * Override this to implement optional bank specifics.
     *
     * @param file file to check
     * @return {@code true} (default) when the file is supported and {@link NewTransaction}s can be extracted, {@code false} otherwise
     */
    protected boolean isPdfSupported(File file) {
        return true;
    }

    @Override
    public List<NewTransaction> readFile(File file) throws IOException {

        final List<NewTransaction> pdfTransactions = new ArrayList<>();

        AccountOwner accountOwner = null;

        try (PDDocument document = Loader.loadPDF(file)) {
            final PdfPageIterator pageIterator = new PdfPageIterator(document);
            while (pageIterator.hasNext()) {
                final PdfPage page = pageIterator.next();
                if (accountOwner == null) {
                    accountOwner = getAccountOwner(page);
                }
                pdfTransactions.addAll(readPage(page, accountOwner));
            }
        }

        return pdfTransactions;
    }

    protected abstract List<NewTransaction> readPage(PdfPage page, AccountOwner accountOwner);

    protected abstract AccountOwner getAccountOwner(PdfPage page);
}
