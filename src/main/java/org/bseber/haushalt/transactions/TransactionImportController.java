package org.bseber.haushalt.transactions;

import jakarta.servlet.http.HttpServletRequest;
import org.bseber.haushalt.core.IBAN;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.transactions.importer.TransactionFileReaderDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
import static org.springframework.util.StringUtils.hasText;

@Controller
@RequestMapping("/transactions/import")
class TransactionImportController {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private final TransactionFileReaderDelegate transactionFileReader;
    private final TransactionService transactionService;

    TransactionImportController(TransactionFileReaderDelegate transactionFileReader, TransactionService transactionService) {
        this.transactionFileReader = transactionFileReader;
        this.transactionService = transactionService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        // TODO use nice UX for lists greater than X
        // default of 256 can be reached easily with importing transactions
        webDataBinder.setAutoGrowCollectionLimit(1000);
    }

    @GetMapping
    public String get() {
        return "transactions/import";
    }

    @PostMapping
    public String importFilePreview(@RequestParam("file") MultipartFile multipartFile, RedirectAttributes redirectAttributes) {

        final List<NewTransaction> transactions = readFile(multipartFile);
        final List<TransactionImportDto> dtos = transactions
            .stream()
            .map(TransactionImportController::toTransactionImportDto)
            .toList();

        redirectAttributes.addFlashAttribute("preview", new ImportPreviewDto(dtos));

        return "redirect:/transactions/import/preview";
    }

    @GetMapping("/preview")
    public String importPreview(HttpServletRequest request) {
        final Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap == null) {
            // user called url directly without using the import workflow
            // (or did a page reload on the preview page...)
            return "redirect:/transactions/import";
        } else {
            // import workflow used -> render import page with preview (exists in inputFlashMap)
            return "transactions/import";
        }
    }

    @PostMapping("/apply")
    public String importFileApply(@ModelAttribute("preview") ImportPreviewDto dto, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            // TODO does this work with validation errors?
            redirectAttributes.addFlashAttribute("preview", dto);
            return "redirect:/transactions/import";
        }

        final List<NewTransaction> transactions = dto.getTransactions().stream().map(TransactionImportController::toTransaction).toList();
        transactionService.addTransactions(transactions);

        redirectAttributes.addFlashAttribute("importSuccess", true);
        redirectAttributes.addFlashAttribute("turboReload", true);

        return "redirect:/transactions";
    }

    private List<NewTransaction> readFile(MultipartFile multipartFile) {
        File file = null;
        try {
            file = toTemporaryFile(multipartFile);
            return transactionFileReader.readFile(file);
        } catch (IOException e) {
            LOG.error("Could not read file.", e);
            return List.of();
        } finally {
            deleteFile(file);
        }
    }

    private static NewTransaction toTransaction(TransactionImportDto dto) {
        IBAN iban = hasText(dto.getIban()) ? new IBAN(dto.getIban()) : null;
        return new NewTransaction(
            dto.getBookingDate(),
            Optional.ofNullable(dto.getValueDate()),
            dto.getProcedure(),
            Optional.empty(),
            dto.getPayer(),
            Optional.ofNullable(iban),
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

    private static File toTemporaryFile(MultipartFile multipartFile) throws IOException {
        final String contentType = multipartFile.getContentType();
        final Path tempFilePath = Files.createTempFile("import-" + UUID.randomUUID(), contentTypeToSuffix(contentType));
        multipartFile.transferTo(tempFilePath);
        return tempFilePath.toFile();
    }

    private static String contentTypeToSuffix(String contentType) {
        if (!hasText(contentType)) {
            // or should we throw since not supported?
            LOG.info("Received empty contentType. Using empty suffix for file name.");
            return "";
        }
        return switch (contentType) {
            case "application/pdf" -> ".pdf";
            case "text/csv" -> ".csv";
            default -> throw new IllegalStateException("contentType %s not supported yet".formatted(contentType));
        };
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
            transaction.payeeIban().map(IBAN::value).orElse(""),
            transaction.amount().amount(),
            transaction.customerReference(),
            transaction.status()
        );
    }
}
