package com.auctionaa.backend.Service;

import com.auctionaa.backend.DTO.Response.MbTxnResponse;
import com.auctionaa.backend.Entity.MbTxn;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MbClient {

    @Value("${mb.api.url}")
    private String apiUrl;

    @Value("${mb.api.username}")
    private String username;

    @Value("${mb.api.password}")
    private String password;

    @Value("${mb.api.accountNo}")
    private String accountNo;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<MbTxn> fetchRecentTransactions(LocalDate from, LocalDate to) {
        System.out.println("============== MB CLIENT DEBUG ==============");
        System.out.println("apiUrl: " + apiUrl);
        System.out.println("username: " + username);
        System.out.println("password: " + (password != null ? "********" : "null"));
        System.out.println("accountNo: " + accountNo);
        System.out.println("from: " + from + " | to: " + to);
        System.out.println("=============================================");

        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, String> body = new HashMap<>();
        body.put("USERNAME", username);
        body.put("PASSWORD", password);
        body.put("DAY_BEGIN", from.format(f));
        body.put("DAY_END", to.format(f));
        body.put("NUMBER_MB", accountNo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "AuctionAA/1.0");
        HttpEntity<Map<String, String>> req = new HttpEntity<>(body, headers);

        System.out.println("[MB] Sending POST to: " + apiUrl);
        System.out.println("[MB] Body: " + body);

        try {
            // Đọc body thô để log & parse linh hoạt
            ResponseEntity<String> res =
                    restTemplate.exchange(apiUrl, HttpMethod.POST, req, String.class);

            System.out.println("[MB] Response status: " + res.getStatusCode());
            String raw = res.getBody();
            System.out.println("[MB] Raw body: " + raw);

            if (!res.getStatusCode().is2xxSuccessful() || raw == null || raw.isBlank()) {
                return Collections.emptyList();
            }

            JsonNode root = mapper.readTree(raw);
            JsonNode array = root.path("data").path("transactionHistoryList");
            if (array.isArray()) {
                // Lưu ý: import đúng MbTxn bạn vừa tạo ở package dto
                return mapper.convertValue(array, new com.fasterxml.jackson.core.type.TypeReference<List<com.auctionaa.backend.Entity.MbTxn>>() {});
            }

            // 2) Nếu trả về object, thử lần lượt các khả năng
            //    a) data là mảng
            JsonNode data = root.path("data");
            if (data.isArray()) {
                return mapper.convertValue(data, new TypeReference<List<MbTxn>>() {
                });
            }

            //    b) data là object chứa mảng: transactions / list / items ...
            if (data.isObject()) {
                // danh sách key có thể
                String[] candidateArrays = {"transactions", "list", "items", "records", "result"};
                for (String key : candidateArrays) {
                    JsonNode arr = data.path(key);
                    if (arr.isArray()) {
                        return mapper.convertValue(arr, new TypeReference<List<MbTxn>>() {
                        });
                    }
                }
                // data là 1 giao dịch đơn (START_OBJECT) → bọc thành list
                if (looksLikeTxn(data)) {
                    MbTxn one = mapper.convertValue(data, MbTxn.class);
                    return List.of(one);
                }
            }

            // 3) Một số nhà cung cấp để mảng ngay trên các key khác ở root
            String[] rootArrays = {"transactions", "list", "items", "records", "result"};
            for (String key : rootArrays) {
                JsonNode arr = root.path(key);
                if (arr.isArray()) {
                    return mapper.convertValue(arr, new TypeReference<List<MbTxn>>() {
                    });
                }
                if (looksLikeTxn(arr)) { // object đơn
                    MbTxn one = mapper.convertValue(arr, MbTxn.class);
                    return List.of(one);
                }
            }

            // 4) Không tìm thấy mảng hợp lệ
            System.err.println("⚠️ Không tìm thấy mảng giao dịch trong JSON (xem Raw body ở trên).");
            return Collections.emptyList();

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gọi/parse API MB: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Heuristic xem 1 node có giống object giao dịch hay không
    private boolean looksLikeTxn(JsonNode n) {
        if (n == null || !n.isObject()) return false;
        return n.has("creditAmount") || n.has("debitAmount") || n.has("refNo") || n.has("transactionDate");
    }
}