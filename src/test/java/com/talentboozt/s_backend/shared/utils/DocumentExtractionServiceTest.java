package com.talentboozt.s_backend.shared.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.cos.COSName;
import java.io.File;

class DocumentExtractionServiceTest {

    private final DocumentExtractionService service = new DocumentExtractionService();

    @Test
    void testForensics() throws IOException {
        String[] files = {
            "../scratch/actual_failing_resume.pdf",
            "../scratch/working_resume.pdf"
        };
        for (String filePath : files) {
            File file = new File(filePath);
            System.out.println("\n==========================================");
            System.out.println("FORENSICS FOR: " + file.getName());
            System.out.println("==========================================");
            if (!file.exists()) {
                System.out.println("File does not exist: " + file.getAbsolutePath());
                continue;
            }
            System.out.println("File Size: " + file.length() + " bytes");
            try (PDDocument doc = Loader.loadPDF(java.nio.file.Files.readAllBytes(file.toPath()))) {
                System.out.println("PDF Version: " + doc.getVersion());
                System.out.println("Page Count: " + doc.getNumberOfPages());
                
                var info = doc.getDocumentInformation();
                if (info != null) {
                    System.out.println("Producer: " + info.getProducer());
                    System.out.println("Creator: " + info.getCreator());
                    System.out.println("Title: " + info.getTitle());
                }
                
                System.out.println("\n--- Fonts List ---");
                int pageNum = 1;
                for (PDPage page : doc.getPages()) {
                    System.out.println("Page " + pageNum + ":");
                    PDResources resources = page.getResources();
                    if (resources != null) {
                        for (COSName fontName : resources.getFontNames()) {
                            try {
                                PDFont font = resources.getFont(fontName);
                                System.out.println("  - Name: " + font.getName());
                                System.out.println("    Type: " + font.getType());
                                System.out.println("    SubType: " + font.getSubType());
                                System.out.println("    IsEmbedded: " + font.isEmbedded());
                                System.out.println("    HasToUnicode: " + font.getCOSObject().containsKey(COSName.TO_UNICODE));
                            } catch (Exception e) {
                                System.out.println("  - Error reading font " + fontName.getName() + ": " + e.getMessage());
                            }
                        }
                    }
                    pageNum++;
                }
                
                System.out.println("\n--- Text Extraction Sample ---");
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String text = stripper.getText(doc);
                System.out.println("Extracted text length: " + text.length());
                if (text.length() > 0) {
                    String sample = text.substring(0, Math.min(text.length(), 1000));
                    System.out.println("Content:\n" + sample);
                } else {
                    System.out.println("[NO TEXT EXTRACTED]");
                }
            } catch (Exception e) {
                System.out.println("Error reading PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Test
    void testExtractTextFromPdf() throws IOException {
        // Create a simple PDF in memory using PDFBox
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Talnova Resume Extraction Test");
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            byte[] pdfBytes = outputStream.toByteArray();

            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    "resume.pdf",
                    "application/pdf",
                    pdfBytes
            );

            String text = service.extractText(multipartFile);
            assertNotNull(text);
            assertTrue(text.contains("Talnova Resume Extraction Test"));
        }
    }

    @Test
    void testUnsupportedFormatThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                "Plain text resume content".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> service.extractText(file));
    }
}
