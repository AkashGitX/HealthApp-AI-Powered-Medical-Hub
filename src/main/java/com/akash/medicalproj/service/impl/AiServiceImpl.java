package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.AiAnalysisResponse;
import com.akash.medicalproj.dto.MedicineDTO;
import com.akash.medicalproj.service.AiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * AI Service — calls OpenAI Vision API directly via RestTemplate.
 * Spring AI auto-configuration stays excluded so startup never crashes.
 * When OPENAI_API_KEY is set, real AI analysis is performed.
 * When key is absent, a clear "not configured" message is returned.
 * FIXED system prompts only — no user input ever injected into AI calls.
 */
@Service
@Slf4j
public class AiServiceImpl implements AiService {

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    private static final String REPORT_PROMPT =
            "You are a medical report explainer for patients. The image is a medical report. " +
            "Explain its contents in simple, patient-friendly language. " +
            "Highlight key findings, note which values are normal or abnormal. " +
            "Keep your response under 250 words. " +
            "Do NOT diagnose or prescribe treatments. " +
            "End with: 'Please discuss these results with your doctor.'";

    /**
     * FIXED medicine prompt — instructs GPT to return strict JSON only.
     * No user input is ever injected. Output is parsed and validated before rendering.
     */
    private static final String MEDICINE_PROMPT =
            "You are a pharmacy assistant. The image is a prescription. " +
            "Identify each medicine and suggest a cheaper generic alternative available in India. " +
            "Return ONLY a valid JSON array, no markdown, no extra text. Each element must have exactly these keys: " +
            "\"name\" (brand/prescribed name), \"generic\" (generic alternative name), \"price\" (approx INR range as string, e.g. '₹5–15'). " +
            "Limit to at most 6 medicines. Example: [{\"name\":\"Crocin\",\"generic\":\"Paracetamol IP\",\"price\":\"₹2–8\"}]";

    @Value("${openai.api.key:}")
    private String openAiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper   = new ObjectMapper();

    @Override
    public AiAnalysisResponse analyzeReport(MultipartFile file) {
        return callVisionApi(file, REPORT_PROMPT, "REPORT_ANALYSIS",
                "AI report analysis is not configured. Please set the OPENAI_API_KEY secret to enable this feature.");
    }

    @Override
    public AiAnalysisResponse suggestCheaperMedicines(MultipartFile prescription) {
        if (openAiKey == null || openAiKey.isBlank()) {
            return AiAnalysisResponse.builder()
                    .success(false).type("MEDICINE_SUGGESTION")
                    .errorMessage("AI medicine suggestions are not configured. Please set the OPENAI_API_KEY secret.")
                    .build();
        }
        try {
            String raw = fetchRawAiContent(prescription, MEDICINE_PROMPT);
            List<MedicineDTO> medicines = parseMedicines(raw);
            return AiAnalysisResponse.builder()
                    .success(true).type("MEDICINE_SUGGESTION")
                    .explanation(raw)
                    .medicines(medicines)
                    .build();
        } catch (Exception e) {
            log.error("Medicine suggestion failed: {}", e.getMessage());
            return AiAnalysisResponse.builder().success(false).type("MEDICINE_SUGGESTION")
                    .errorMessage("AI service error. Please try again later.").build();
        }
    }

    // ── Parsing ─────────────────────────────────────────────────────────────

    /**
     * Parses the AI JSON array response into MedicineDTO list.
     * Handles markdown code-block wrapping (```json ... ```) that GPT sometimes adds.
     * For each medicine, generates per-medicine purchase links.
     */
    @SuppressWarnings("unchecked")
    private List<MedicineDTO> parseMedicines(String raw) {
        String json = raw.trim();
        // Strip markdown code fences if present
        if (json.startsWith("```")) {
            int start = json.indexOf('\n');
            int end   = json.lastIndexOf("```");
            if (start >= 0 && end > start) json = json.substring(start + 1, end).trim();
        }
        // Find the JSON array boundaries as a fallback
        int arrStart = json.indexOf('[');
        int arrEnd   = json.lastIndexOf(']');
        if (arrStart >= 0 && arrEnd > arrStart) json = json.substring(arrStart, arrEnd + 1);

        List<MedicineDTO> result = new ArrayList<>();
        try {
            List<Map<String, String>> items = objectMapper.readValue(json, new TypeReference<>() {});
            for (Map<String, String> item : items) {
                String genericName = item.getOrDefault("generic", "").trim();
                String brandName   = item.getOrDefault("name",    "").trim();
                // Use generic name for purchase links (more widely sold)
                String searchTerm  = genericName.isBlank() ? brandName : genericName;
                if (searchTerm.isBlank()) continue;

                String q = searchTerm.replace(" ", "+");

                result.add(MedicineDTO.builder()
                        .name(brandName)
                        .genericName(genericName)
                        .price(item.getOrDefault("price", "—"))
                        .amazonLink("https://www.amazon.in/s?k=" + q)
                        .flipkartLink("https://www.flipkart.com/search?q=" + q)
                        .oneMgLink("https://www.1mg.com/search/all?name=" + q)
                        .pharmEasyLink("https://pharmeasy.in/search/all?name=" + q)
                        .build());
            }
        } catch (Exception e) {
            log.warn("Could not parse medicine JSON from AI response, falling back to text. raw={}", raw);
        }
        return result;
    }

    // ── Shared Vision API call ───────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String fetchRawAiContent(MultipartFile file, String systemPrompt) throws Exception {
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        String mime   = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        Map<String, Object> imgPart  = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", "data:" + mime + ";base64," + base64, "detail", "low")
        );
        Map<String, Object> textPart = Map.of("type", "text", "text", "Please analyze this prescription.");

        Map<String, Object> body = Map.of(
                "model",       "gpt-4o-mini",
                "max_tokens",  600,
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", List.of(textPart, imgPart))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);

        ResponseEntity<Map> resp = restTemplate.postForEntity(
                OPENAI_CHAT_URL, new HttpEntity<>(body, headers), Map.class);

        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) resp.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
            }
        }
        throw new RuntimeException("Empty response from OpenAI");
    }

    @SuppressWarnings("unchecked")
    private AiAnalysisResponse callVisionApi(MultipartFile file, String systemPrompt,
                                              String type, String fallbackMsg) {
        if (openAiKey == null || openAiKey.isBlank()) {
            log.info("OpenAI API key not set — returning fallback for type={}", type);
            return AiAnalysisResponse.builder()
                    .success(false).type(type).errorMessage(fallbackMsg).build();
        }
        try {
            String content = fetchRawAiContent(file, systemPrompt);
            return AiAnalysisResponse.builder().success(true).type(type).explanation(content).build();
        } catch (Exception e) {
            log.error("OpenAI API call failed [{}]: {}", type, e.getMessage());
            return AiAnalysisResponse.builder().success(false).type(type)
                    .errorMessage("AI service error. Please try again later.").build();
        }
    }
}
