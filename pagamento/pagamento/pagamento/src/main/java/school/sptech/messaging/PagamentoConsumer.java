package school.sptech.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import school.sptech.DTO.PagamentoCriadoMessage;
import school.sptech.service.PagamentoService;

@Component
public class PagamentoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PagamentoConsumer.class);

    private final PagamentoService service;

    public PagamentoConsumer(PagamentoService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.pagamento-criado}")
    public void consumirPagamentoCriado(PagamentoCriadoMessage mensagem) {
        log.info("[PAGAMENTO] Mensagem recebida da fila: idCurso={} idUsuario={}",
                mensagem.idCurso(), mensagem.idUsuario());
        try {
            service.processarPagamentoCriado(mensagem);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Erro de negocio / mensagem invalida: reprocessar nao resolve.
            // Descarta a mensagem (sem requeue) para nao entrar em loop infinito.
            log.warn("[PAGAMENTO] Mensagem descartada (erro de negócio) para idCurso={} idUsuario={}: {}",
                    mensagem.idCurso(), mensagem.idUsuario(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        } catch (Exception e) {
            // Erro possivelmente transitorio (ex.: Mercado Pago indisponivel):
            // relanca para o container aplicar retry/requeue.
            log.error("[PAGAMENTO] Falha ao processar pagamento.criado para idCurso={} idUsuario={}",
                    mensagem.idCurso(), mensagem.idUsuario(), e);
            throw new RuntimeException("Falha ao processar pagamento.criado", e);
        }
    }
}
