package org.bseber.haushalt.transactions.importer;

import org.bseber.haushalt.transactions.NewTransaction;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TransactionFileReader {

    boolean supports(File file);

    List<NewTransaction> readFile(File file) throws IOException;
}
