//package school.sptech.service;
//
//import com.mercadopago.MercadoPagoConfig;
//import com.mercadopago.client.common.IdentificationRequest;
//import com.mercadopago.client.payment.PaymentClient;
//import com.mercadopago.client.payment.PaymentCreateRequest;
//import com.mercadopago.client.payment.PaymentPayerRequest;
//import com.mercadopago.exceptions.MPApiException;
//import com.mercadopago.resources.payment.Payment;
//import org.springframework.stereotype.Service;
//import school.sptech.model.PagamentoCartao;
//import school.sptech.repository.IPagamentoCartaoRepository;
//
//@Service
//public class PagamentoCartaoService {
//
//    private final IPagamentoCartaoRepository repository;
//
//    public PagamentoCartaoService(IPagamentoCartaoRepository repository) {
//        this.repository = repository;
//    }
//
//    public Payment realizarPagamentoCartao(PagamentoCartao pagamento) throws Exception {
//        MercadoPagoConfig.setAccessToken("APP_USR-5423849464279431-032111-2124f85365cf2dff53299b538a7ae7d2-2562961358");
//
//        PaymentClient client = new PaymentClient();
//
//        PaymentPayerRequest payer = PaymentPayerRequest.builder()
//                .email(pagamento.getEmailPagador())
//                .identification(
//                        IdentificationRequest.builder()
//                                .type(pagamento.getTipoDocumento())
//                                .number(pagamento.getNumeroDocumento())
//                                .build()
//                )
//                .build();
//
//        PaymentCreateRequest request = PaymentCreateRequest.builder()
//                .transactionAmount(pagamento.getValor())
//                .token(pagamento.getToken())
//                .installments(pagamento.getParcelas())
//                .paymentMethodId(pagamento.getBandeira())
//                .issuerId(pagamento.getIssuerId())
//                .description("Pagamento curso")
//                .payer(payer)
//                .build();
//
//        // salva primeiro no banco para gerar o ID
//        repository.save(pagamento);
//
//        try {
//            Payment resposta = client.create(request);
//
//            System.out.println("==== RESPOSTA DO MP ====");
//            System.out.println("ID: " + resposta.getId());
//            System.out.println("Status: " + resposta.getStatus());
//            System.out.println("Detalhe: " + resposta.getStatusDetail());
//
//            pagamento.setMpPaymentId(resposta.getId());
//            pagamento.setStatus(resposta.getStatus());
//            pagamento.setStatusDetalhe(resposta.getStatusDetail());
//            repository.save(pagamento);
//
//            return resposta;
//
//        } catch (MPApiException e) {
//            System.out.println("STATUS: " + e.getStatusCode());
//            System.out.println("RESPONSE: " + e.getApiResponse().getContent());
//            throw e;
//        }
//    }
//}
