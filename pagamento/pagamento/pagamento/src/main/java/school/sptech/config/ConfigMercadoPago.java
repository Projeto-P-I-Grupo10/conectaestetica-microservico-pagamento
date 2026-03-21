package school.sptech.config;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ConfigMercadoPago {

    @PostConstruct
    public void init(){
        MercadoPagoConfig.setAccessToken("APP_USR-5423849464279431-032111-2124f85365cf2dff53299b538a7ae7d2-2562961358");
    }
}
