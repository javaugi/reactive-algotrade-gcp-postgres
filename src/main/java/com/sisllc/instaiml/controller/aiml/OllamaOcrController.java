/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.controller.aiml;

import com.sisllc.instaiml.dto.OllamaRequest;
import com.sisllc.instaiml.service.aiml.OllamaService;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/ollama")
public class OllamaOcrController {
    @Value("${spring.ai.deepseek.openai.base-url}")
    private String baseUrl;
    @Value("${spring.ai.deepseek.openai.api-key}")
    private String apiKey;

    @Autowired
    private OllamaService ollamaService;

    @PostMapping
    public Mono<ResponseEntity<String>> queryOllamaByWebClient(@RequestBody OllamaRequest ollamaRequest) {
        log.debug("queryOllamaByWebClient {}", ollamaRequest);
        return ollamaService.queryOllamaByWebClient(ollamaRequest)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(Exception.class, ex -> {
            log.error("queryOllamaByWebClient ", ex);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }

    @PostMapping("/stream")
    public Flux<ResponseEntity<String>> streamOllamaByWebClient(@RequestBody OllamaRequest ollamaRequest) {
        log.debug("queryOllamaByWebClient {}", ollamaRequest);
        return ollamaService.streamOllamaByWebClient(ollamaRequest)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(Exception.class, ex -> {
            log.error("streamOllamaByWebClient ", ex);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }

    @PostMapping("/query")
    public Mono<ResponseEntity<String>> queryOllamaByTemplate(@RequestBody OllamaRequest ollamaRequest) {
        log.debug("queryOllamaByTemplate ollamaRequest {}", ollamaRequest);
        return ollamaService.queryOllamaByTemplate(ollamaRequest)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(Exception.class, ex -> {
                log.error("queryOllamaByTemplate ", ex);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
        @RequestParam(defaultValue = "pdf") String format) throws Exception {
        // Step 1: Send image to OpenAI
        String extractedText = callOpenAIVision(file);

        // Step 2: Generate file
        String filename = "converted_" + System.currentTimeMillis();
        Path output;
        if (format.equalsIgnoreCase("docx")) {
            output = generateDocx(extractedText, filename);
        } else {
            output = generatePdf(extractedText, filename);
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + output.getFileName())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(Files.readAllBytes(output));
    }

    private String callOpenAIVision(MultipartFile file) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(baseUrl);
        request.setHeader("Authorization", "Bearer " + apiKey);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file.getBytes(), ContentType.IMAGE_JPEG, file.getOriginalFilename());

        // For GPT-4-vision you structure the request like this:
        String jsonPayload = """
        {
          "model": "gpt-4o",
          "messages": [
            {
              "role": "user",
              "content": [
                {
                  "type": "image_url",
                  "image_url": {
                    "url": "data:image/jpeg;base64,""" + Base64.getEncoder().encodeToString(file.getBytes()) + """
                  }
                },
                {
                  "type": "text",
                  "text": "Extract the readable text from this scanned image."
                }
              ]
            }
          ],
          "max_tokens": 2048
        }
    """;

        request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());

        // Parse JSON response
        JSONObject json = new JSONObject(responseBody);
        return json.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content");
    }

    private Path generateDocx(String text, String filename) throws IOException {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);

        Path output = Files.createTempFile(filename, ".docx");
        try (FileOutputStream out = new FileOutputStream(output.toFile())) {
            doc.write(out);
        }
        return output;
    }

    private Path generatePdf(String text, String filename) throws IOException {
        Path output;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.beginText();
                PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                content.setFont(font, 12);
                content.setLeading(14.5f);
                content.newLineAtOffset(50, 700);
                for (String line : text.split("\n")) {
                    content.showText(line);
                    content.newLine();
                }
                content.endText();
            }
            output = Files.createTempFile(filename, ".pdf");
            doc.save(output.toFile());
        }

        return output;
    }

}
