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


}