package com.auctionaa.backend.Service;

import com.auctionaa.backend.Entity.Wallet;
import com.auctionaa.backend.Entity.WalletTransaction;
import com.auctionaa.backend.Repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendTopupSucceeded(Wallet wallet, WalletTransaction txn) {
        if (wallet.getCallbackUrl() == null || wallet.getWebhookSecret() == null) return;

        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "wallet.topup.succeeded");
        payload.put("walletId", wallet.getId());
        payload.put("transactionId", txn.getId());
        payload.put("amount", txn.getBalance());
        payload.put("status", txn.getStatus());
        payload.put("bankTxnId", wallet.getBankTxnId());

        String body = Jsons.toJson(payload);
        String signature = hmacSha256(wallet.getWebhookSecret(), body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Signature", signature);

        HttpEntity<String> req = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(wallet.getCallbackUrl(), HttpMethod.POST, req, String.class);
        } catch (Exception ignored) { /* có thể log retry */ }
    }

    private String hmacSha256(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Hex.encodeHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return "";
        }
    }

    // simple JSON util
    static class Jsons {
        static String toJson(Map<String, Object> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(e.getKey()).append("\":");
                Object v = e.getValue();
                if (v == null) sb.append("null");
                else if (v instanceof Number || v instanceof Boolean) sb.append(v);
                else sb.append("\"").append(String.valueOf(v).replace("\"","\\\"")).append("\"");
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
