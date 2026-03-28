package school.sptech.controller;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.service.PagamentoService;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final PagamentoService service;

    public WebhookController(PagamentoService service) {
        this.service = service;
    }


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

                if ("approved".equals(payment.getStatus())) {
                    System.out.println("✅ PAGAMENTO APROVADO");
                    service.atulizarStatus("APPROVED", idPagamento);
                }

                if ("pending".equals(payment.getStatus())) {
                    System.out.println("⏳ AGUARDANDO PAGAMENTO");
                }

                if ("cancelled".equals(payment.getStatus())) {
                    if ("expired".equals(payment.getStatusDetail())) {
                        service.atulizarStatus("EXPIRED", idPagamento);
                    } else {
                        service.atulizarStatus("CANCELLED", idPagamento);
                    };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("ok");
    }
}