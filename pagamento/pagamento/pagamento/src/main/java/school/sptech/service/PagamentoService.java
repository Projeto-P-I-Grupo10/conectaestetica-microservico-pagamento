package school.sptech.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import school.sptech.DTO.PixRequest;
import school.sptech.model.Pagamento;
import school.sptech.repository.IPagamentoRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class PagamentoService {

    private final IPagamentoRepository repository;

    public PagamentoService(IPagamentoRepository repository) {
        this.repository = repository;
    }

    public Payment criarPagamentoPix(Pagamento pagamento, String email) throws Exception {
        PaymentClient client = new PaymentClient();
        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(email)
                .build();

        System.out.println("Agora UTC: " + OffsetDateTime.now(ZoneOffset.UTC));
        OffsetDateTime expiracao = OffsetDateTime
                .now(ZoneOffset.UTC)
                .plusMinutes(10);
        System.out.println("Expiração enviada: " + expiracao);

        // salva primero no banco para gerar o ID
        Pagamento salvo = repository.save(pagamento);

        PaymentCreateRequest request =
                PaymentCreateRequest.builder()
                        .transactionAmount(pagamento.getValor())
                        .paymentMethodId("pix")
                        .payer(payer)
                        .dateOfExpiration(expiracao)
                        .description("Pagamento PIX teste")
                        // referenciando o id do pagamento da nossas base em um atributo do marcado pago
                        .externalReference(salvo.getId().toString())
                        .build();

        try {
            var resposta = client.create(request);
            System.out.println("==== RESPOSTA DO MP ====");
            System.out.println("ID: " + resposta.getId());
            System.out.println("Status: " + resposta.getStatus());
            System.out.println("Expiração MP: " + resposta.getDateOfExpiration());
            return resposta;
        } catch (MPApiException e) {
            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("RESPONSE: " + e.getApiResponse().getContent());

            throw e;
        }
    }

    public Pagamento atulizarStatus(String status, Long id){
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("pagamento não encontrado"));

        pagamento.setStatus(status);

        repository.save(pagamento);

        return pagamento;
    }


}