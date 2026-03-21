package school.sptech.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import school.sptech.DTO.DataForPagamentoResquest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class PagamentoService {

    public Payment criarPagamentoPix(DataForPagamentoResquest dataForPagamentoResquest) throws Exception {
        MercadoPagoConfig.setAccessToken("APP_USR-5423849464279431-032111-2124f85365cf2dff53299b538a7ae7d2-2562961358");

        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(dataForPagamentoResquest.getEmail())
                .firstName(dataForPagamentoResquest.getNome())
                .lastName(dataForPagamentoResquest.getSobrenome())
                .build();
        OffsetDateTime expiracao = OffsetDateTime.now().plusMinutes(10);

        PaymentCreateRequest request =
                PaymentCreateRequest.builder()
                        .transactionAmount(dataForPagamentoResquest.getValor())
                        .description(dataForPagamentoResquest.getDescricao())
                        .paymentMethodId("pix")
                        .payer(payer)
                        .dateOfExpiration(expiracao)
                        .build();

        try {
            return client.create(request);

        } catch (MPApiException e) {

            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("RESPONSE: " + e.getApiResponse().getContent());

            throw e;
        }
    }

}