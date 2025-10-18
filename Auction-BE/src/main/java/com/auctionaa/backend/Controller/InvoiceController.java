package com.auctionaa.backend.Controller;

import com.auctionaa.backend.DTO.Request.CreateInvoiceRequest;
import com.auctionaa.backend.DTO.Response.InvoiceListItemDTO;
import com.auctionaa.backend.Entity.Invoice;
import com.auctionaa.backend.Jwt.JwtUtil;
import com.auctionaa.backend.Service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final JwtUtil jwtUtil;
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService, JwtUtil jwtUtil) {
        this.invoiceService = invoiceService;
        this.jwtUtil = jwtUtil;
    }

    // Admin/list: có phân trang
    @GetMapping("/list")
    public Page<Invoice> getAllInvoice(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        return invoiceService.getAllInvoice(pageable);
    }

    @PostMapping("/add")
    public Invoice saveInvoice(@RequestBody CreateInvoiceRequest req) {
        return invoiceService.createInvoice(req);
    }

    // FE hiện tại: trả mảng đơn giản (không Page)
    @GetMapping("/my-invoice")
    public List<InvoiceListItemDTO> getMyInvoices(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUserId(token);
        return invoiceService.getMyInvoicesArray(email);
    }
}
