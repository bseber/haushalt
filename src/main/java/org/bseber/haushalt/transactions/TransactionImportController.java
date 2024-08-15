package org.bseber.haushalt.transactions;

import jakarta.servlet.http.HttpServletRequest;
import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;

@Controller
@RequestMapping("/transactions/import")
class TransactionImportController {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private final TransactionService transactionService;
    private final List<TransactionCsvReader> csvReaders;

    TransactionImportController(TransactionService transactionService, List<TransactionCsvReader> csvReaders) {
        this.transactionService = transactionService;
        this.csvReaders = csvReaders;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        // TODO use nice UX for lists greater than X
        // default of 256 can be reached easily with importing transactions
        webDataBinder.setAutoGrowCollectionLimit(1000);
    }

    @GetMapping
    public String get(Model model) {
        return "transactions/import";
    }

    @GetMapping("/csv")
    public String get(HttpServletRequest request) {
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            return "transactions/import";
        } else {
            return "redirect:/transactions/import";
        }
    }

    @PostMapping("/csv")
    public String importCsv(@RequestParam("file") MultipartFile multipartFile, RedirectAttributes redirectAttributes) {

        final List<NewTransaction> transactions = readFile(multipartFile);
        final List<TransactionImportDto> dtos = transactions
            .stream()
            .map(TransactionImportController::toTransactionImportDto)
            .toList();

        redirectAttributes.addFlashAttribute("preview", new ImportPreviewDto(dtos));

        return "redirect:/transactions/import/csv";
    }

    @PostMapping("/csv/apply")
    public String applyImportedCsv(@ModelAttribute("preview") ImportPreviewDto dto, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            // TODO does this work with validation errors?
            redirectAttributes.addFlashAttribute("preview", dto);
            return "redirect:/transactions/import/csv";
        }

        final List<NewTransaction> transactions = dto.getTransactions().stream().map(TransactionImportController::toTransaction).toList();
        transactionService.addTransactions(transactions);

        redirectAttributes.addFlashAttribute("importSuccess", true);
        return "redirect:/transactions";
    }


    private List<NewTransaction> readFile(MultipartFile multipartFile) {
        File file = null;
        try {
            file = toFile(multipartFile);
            final TransactionCsvReader csvReader = getReader(file);
            return csvReader.readCsvFile(file);
        } catch (IOException e) {
            LOG.error("Could not read CSV file.", e);
            return List.of();
        } finally {
            deleteFile(file);
        }
    }

    private static NewTransaction toTransaction(TransactionImportDto dto) {
        return new NewTransaction(
            dto.getBookingDate(),
            Optional.ofNullable(dto.getValueDate()),
            dto.getProcedure(),
            Optional.empty(),
            dto.getPayer(),
            new IBAN(dto.getIban()),
            dto.getPayee(),
            dto.getRevenueType(),
            Money.ofEUR(dto.getAmount()),
            dto.getReference(),
            dto.getCustomerReference(),
            dto.getStatus()
        );
    }

    private static void deleteFile(File file) {
        if (file != null) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                LOG.error("Could not delete temporary csv import file.", e);
            }
        }
    }

    private static File toFile(MultipartFile multipartFile) throws IOException {
        final Path tempFilePath = Files.createTempFile("import-" + UUID.randomUUID(), ".csv");
        multipartFile.transferTo(tempFilePath);
        return tempFilePath.toFile();
    }

    private TransactionCsvReader getReader(File file) {
        return csvReaders.stream()
            .filter(reader -> reader.supports(file))
            .findFirst()
            // currently not expected since DKB is hard coded
            .orElseThrow(() -> new IllegalStateException("Could not find matching CsvReader for file."));
    }

    private static TransactionImportDto toTransactionImportDto(NewTransaction transaction) {
        return new TransactionImportDto(
            transaction.bookingDate(),
            transaction.valueDate().orElse(null),
            transaction.procedure(),
            transaction.payer(),
            transaction.payee(),
            transaction.reference(),
            transaction.revenueType(),
            transaction.ibanPayee().value(),
            transaction.amount().amount(),
            transaction.customerReference(),
            transaction.status()
        );
    }
}
