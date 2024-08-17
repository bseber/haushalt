package org.bseber.haushalt.transactions.importer;

import org.bseber.haushalt.transactions.NewTransaction;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionFileReaderDelegate {

    private final List<TransactionFileReader> fileReaders;

    TransactionFileReaderDelegate(List<TransactionFileReader> fileReaders) {
        this.fileReaders = fileReaders;
    }

    public List<NewTransaction> readFile(File file) throws IOException {
        final List<NewTransaction> newTransactions = new ArrayList<>();
        for (TransactionFileReader fileReader : fileReaders) {
            if (fileReader.supports(file)) {
                newTransactions.addAll(fileReader.readFile(file));
            }
        }
        return newTransactions;
    }
}
