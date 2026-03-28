package school.sptech.controller;

import org.springframework.http.ResponseEntity;
import school.sptech.DTO.PixRequest;
import school.sptech.model.Pagamento;
import school.sptech.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin(origins = "http://localhost:5173")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    public ResponseEntity<Map<String, Object>> gerarPix(@RequestBody PixRequest body) throws Exception {
        Pagamento pagamento = new Pagamento();

        pagamento.setIdCurso(body.getIdCurso());
        pagamento.setIdUsuario(body.getIdUsuario());
        pagamento.setMetodoPagamento(body.getMetodoPagamento());
        pagamento.setStatus(body.getStatus());
        pagamento.setValor(body.getValor());
        pagamento.setDataPagamento(LocalDateTime.now());

        var payment = service.criarPagamentoPix(pagamento, body.getEmail());

        var transactionData = payment.getPointOfInteraction().getTransactionData();

        return ResponseEntity.ok(Map.of(
                "qr_code", transactionData.getQrCode(),
                "qr_code_base64", transactionData.getQrCodeBase64()
        ));
    }

    @RestController
    @RequestMapping("/webhook")
    public class WebhookController {

        @PostMapping
        public ResponseEntity<String> receberWebhook(@RequestBody Map<String, Object> payload) {

            System.out.println("Webhook recebido: " + payload);

            try {
                // pega o data
                Map<String, Object> data = (Map<String, Object>) payload.get("data");

                if (data != null && data.get("id") != null) {

                    Long paymentId = Long.valueOf(data.get("id").toString());

                    PaymentClient client = new PaymentClient();
                    Payment payment = client.get(paymentId);

                    System.out.println("==== PAGAMENTO DETALHADO ====");
                    System.out.println("ID: " + payment.getId());
                    System.out.println("Status: " + payment.getStatus());
                    System.out.println("Status Detail: " + payment.getStatusDetail());
                    System.out.println("Valor: " + payment.getTransactionAmount());
                    Long idPagamento = null;

                    if (payment.getExternalReference() != null) {
                        idPagamento = Long.valueOf(payment.getExternalReference());
                    }


                    // regra de negócio
                    if ("approved".equals(payment.getStatus())) {
                        System.out.println("✅ PAGAMENTO APROVADO");
                        service.atulizarStatus(payment.getStatus(), idPagamento);
                    }

                    if ("pending".equals(payment.getStatus())) {
                        System.out.println("⏳ AGUARDANDO PAGAMENTO");
                    }

                    if ("cancelled".equals(payment.getStatus())) {
                        System.out.println("❌ PAGAMENTO REJEITADO");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok("ok");
        }
    }
}