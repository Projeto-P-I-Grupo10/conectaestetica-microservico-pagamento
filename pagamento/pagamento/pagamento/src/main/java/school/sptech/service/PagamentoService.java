package school.sptech.service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import school.sptech.DTO.PagamentoCriadoMessage;
import school.sptech.DTO.PagamentoResponse;
import school.sptech.model.Pagamento;
import school.sptech.repository.IPagamentoRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    private final IPagamentoRepository repository;

    public PagamentoService(IPagamentoRepository repository) {
        this.repository = repository;
    }

    public void processarPagamentoCriado(PagamentoCriadoMessage mensagem) throws Exception {
        log.info("[PAGAMENTO] Processando pagamento.criado para idCurso={} idUsuario={}",
                mensagem.idCurso(), mensagem.idUsuario());

        Pagamento pagamento = new Pagamento();
        pagamento.setIdCurso(mensagem.idCurso());
        pagamento.setIdUsuario(mensagem.idUsuario());
        pagamento.setMetodoPagamento(mensagem.metodoPagamento());
        pagamento.setValor(mensagem.valor());
        pagamento.setDataPagamento(LocalDateTime.now());

        criarPagamentoPix(pagamento, mensagem.email());
    }

    /**
     * Retorna o QR Code persistido para o par (idCurso, idUsuario), usado pelo
     * polling do front. Lanca se nao houver pagamento para a chave.
     */
    public PagamentoResponse consultarQrCode(Long idCurso, Long idUsuario) {
        Pagamento pagamento = repository.findByIdCursoAndIdUsuario(idCurso, idUsuario)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        return new PagamentoResponse(pagamento.getQrCodeBase64(), pagamento.getQrCode());
    }

    public PagamentoResponse criarPagamentoPix(Pagamento pagamento, String email) throws Exception {
        Optional<Pagamento> existente = repository.findByIdUsuarioAndIdCursoAndStatus(
                pagamento.getIdUsuario(),
                pagamento.getIdCurso(),
                "pendente"
        );

        if (existente.isPresent()) {
            throw new IllegalStateException("Já existe um pagamento PIX pendente para este curso e usuário.");
        }

        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payer = PaymentPayerRequest.builder()
                .email(email)
                .build();

        System.out.println("Agora UTC: " + OffsetDateTime.now(ZoneOffset.UTC));
        OffsetDateTime expiracao = OffsetDateTime
                .now(ZoneOffset.UTC)
                .plusMinutes(10);
        System.out.println("Expiração enviada: " + expiracao);
        pagamento.setStatus("pendente");
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
            var transactionData = resposta.getPointOfInteraction().getTransactionData();
            PagamentoResponse pagamentoResponse = new PagamentoResponse(transactionData.getQrCodeBase64(), transactionData.getQrCode());
            System.out.println("==== RESPOSTA DO MP ====");
            System.out.println("ID: " + resposta.getId());
            System.out.println("Status: " + resposta.getStatus());
            System.out.println("Expiração MP: " + resposta.getDateOfExpiration());
            salvo.setIdMercadoPago(resposta.getId());
            // persiste o QR para que o polling do front consiga recupera-lo depois
            salvo.setQrCode(transactionData.getQrCode());
            salvo.setQrCodeBase64(transactionData.getQrCodeBase64());
            repository.save(salvo);

            return pagamentoResponse;
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

    public String consultarStatusPagamento(Long idCurso, Long idUsuario) throws Exception {
        Pagamento pagamento = repository.findByIdCursoAndIdUsuario(idCurso, idUsuario)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        PaymentClient client = new PaymentClient();
        Payment resposta = client.get(pagamento.getIdMercadoPago());

        pagamento.setStatus(resposta.getStatus());
        repository.save(pagamento);

        return resposta.getStatus();
    }





}