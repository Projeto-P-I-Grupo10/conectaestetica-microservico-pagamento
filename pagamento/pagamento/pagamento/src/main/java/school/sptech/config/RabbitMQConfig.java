package school.sptech.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queue.pagamento-criado}")
    private String filaPagamentoCriado;

    @Value("${app.rabbitmq.routingkey.pagamento-criado}")
    private String routingKeyPagamentoCriado;

    @Value("${app.rabbitmq.dlx}")
    private String deadLetterExchange;

    @Value("${app.rabbitmq.queue.pagamento-criado-dlq}")
    private String filaPagamentoCriadoDlq;

    @Value("${app.rabbitmq.routingkey.pagamento-criado-dlq}")
    private String routingKeyPagamentoCriadoDlq;

    @Bean
    public TopicExchange pagamentosExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue pagamentoCriadoQueue() {
        // durable + encaminha mensagens rejeitadas (AmqpRejectAndDontRequeueException)
        // para a dead-letter exchange, que as roteia para a DLQ.
        return QueueBuilder.durable(filaPagamentoCriado)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", routingKeyPagamentoCriadoDlq)
                .build();
    }

    @Bean
    public Binding pagamentoCriadoBinding(Queue pagamentoCriadoQueue, TopicExchange pagamentosExchange) {
        return BindingBuilder
                .bind(pagamentoCriadoQueue)
                .to(pagamentosExchange)
                .with(routingKeyPagamentoCriado);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchange);
    }

    @Bean
    public Queue pagamentoCriadoDlq() {
        return QueueBuilder.durable(filaPagamentoCriadoDlq).build();
    }

    @Bean
    public Binding pagamentoCriadoDlqBinding(Queue pagamentoCriadoDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(pagamentoCriadoDlq)
                .to(deadLetterExchange)
                .with(routingKeyPagamentoCriadoDlq);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
