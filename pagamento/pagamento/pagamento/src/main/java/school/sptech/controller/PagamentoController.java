package school.sptech.controller;

import org.springframework.http.ResponseEntity;
import school.sptech.DTO.DataForPagamentoResquest;
import school.sptech.service.PagamentoService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    public Map<String, Object> gerarPix(@RequestBody DataForPagamentoResquest body) throws Exception {

        var payment = service.criarPagamentoPix(body);

        var transactionData = payment.getPointOfInteraction().getTransactionData();

        return Map.of(
                "qr_code", transactionData.getQrCode(),
                "qr_code_base64", transactionData.getQrCodeBase64(),
                "ticket_url", transactionData.getTicketUrl()
        );
    }

    @RestController
    @RequestMapping("/webhook")
    public class WebhookController {

        @PostMapping
        public ResponseEntity<String> receberWebhook(@RequestBody Map<String, Object> payload) {

            System.out.println("Webhook recebido: " + payload);

            return ResponseEntity.ok("ok");
        }
    }
}