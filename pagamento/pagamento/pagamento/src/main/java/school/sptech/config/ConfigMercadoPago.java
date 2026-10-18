package school.sptech.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configura o SDK do Mercado Pago uma unica vez na inicializacao.
 *
 * O access token vem da variavel de ambiente MP_ACCESS_TOKEN
 * (property mercadopago.access-token). O SDK usa configuracao estatica global,
 * entao basta definir aqui uma vez para que PagamentoService e WebhookController
 * usem o mesmo token.
 */
@Configuration
public class ConfigMercadoPago {

    private static final Logger log = LoggerFactory.getLogger(ConfigMercadoPago.class);

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("[PAGAMENTO] MP_ACCESS_TOKEN não configurado; chamadas ao Mercado Pago irão falhar.");
            return;
        }
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}
