package org.bseber.haushalt.transactions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TransactionCsvReader {

    boolean supports(File file);

    List<NewTransaction> readCsvFile(File file) throws IOException;
}
