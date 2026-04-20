package school.sptech.controller;

import com.mercadopago.client.payment.PaymentClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.sptech.DTO.CartaoResquest;
import school.sptech.DTO.PixRequest;
import school.sptech.model.Pagamento;
import school.sptech.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import com.mercadopago.resources.payment.Payment;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoService service;
   // private final PagamentoCartaoService serviceCartao;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    public ResponseEntity<Map<String, Object>> gerarPix(@RequestBody PixRequest body) throws Exception {
        try {
            Pagamento pagamento = new Pagamento();
            pagamento.setIdCurso(body.getIdCurso());
            pagamento.setIdUsuario(body.getIdUsuario());
            pagamento.setMetodoPagamento(body.getMetodoPagamento());
            pagamento.setValor(body.getValor());
            pagamento.setDataPagamento(LocalDateTime.now());

            var payment = service.criarPagamentoPix(pagamento, body.getEmail());
            var transactionData = payment.getPointOfInteraction().getTransactionData();

            return ResponseEntity.ok(Map.of(
                    "qr_code", transactionData.getQrCode(),
                    "qr_code_base64", transactionData.getQrCodeBase64()
            ));
        } catch (IllegalStateException e) {
            // já existe pagamento pendente
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro ao gerar pagamento PIX"));
        }
    }

    @GetMapping("/status/{idCurso}/{idUsuario}")
    public ResponseEntity<Map<String, Object>> consultarStatus(
            @PathVariable Long idCurso,
            @PathVariable Long idUsuario) throws Exception {

        String status = service.consultarStatusPagamento(idCurso, idUsuario);

        return ResponseEntity.ok(Map.of("status", status));
    }



//    @PostMapping("/cartao")
//    public ResponseEntity<Map<String, Object>> realizarPagamento(
//            @RequestBody @Valid CartaoResquest dto) throws Exception {
//
//        PagamentoCartao pagamento = new PagamentoCartao();
//        pagamento.setIdCurso(dto.getIdCurso());
//        pagamento.setIdUsuario(dto.getIdUsuario());
//        pagamento.setMetodoPagamento("credit_card");
//        pagamento.setStatus("pending");
//        pagamento.setValor(dto.getValor());
//        pagamento.setToken(dto.getToken());
//        pagamento.setParcelas(dto.getParcelas());
//        pagamento.setBandeira(dto.getPaymentMethodId());
//        pagamento.setEmailPagador(dto.getEmailPagador());
//        pagamento.setIssuerId(dto.getIssuerId());
//        pagamento.setTipoDocumento(dto.getTipoDocumento());
//        pagamento.setNumeroDocumento(dto.getNumeroDocumento());
//        pagamento.setDataPagamento(LocalDateTime.now());
//
//        Payment resposta = serviceCartao.realizarPagamentoCartao(pagamento);
//
//        return ResponseEntity.status(200).body(Map.of(
//                "id", resposta.getId(),
//                "status", resposta.getStatus(),
//                "status_detalhe", resposta.getStatusDetail(),
//                "valor", resposta.getTransactionAmount(),
//                "parcelas", resposta.getInstallments(),
//                "bandeira", resposta.getPaymentMethodId()
//        ));
//    }


}