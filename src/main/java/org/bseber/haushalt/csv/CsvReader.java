package org.bseber.haushalt.csv;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class CsvReader {

    public List<CsvRow> readRows(File file, String delimiter) throws IOException {
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream
                .map(line -> line.split(delimiter))
                .map(Arrays::asList)
                .map(CsvRow::new)
                .toList();
        }
    }
}
