package hello;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    static final String topicExchangeName = "spring-boot-exchange";
    static final String queueName = "spring-boot";

    // creates AMQP queue
    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    // creates a topic exchange
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    // binds the queue and topic exchange together which defines the behavior that occurs when RabbitTemplate
    // publishes to an exchange. The value in the with is a routing key, so any message sent with a routing key
    // beginning with "foo.bar." will be routed to the queue
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    // configures the class that will be the message listener
    // MessageListenerAdapter is a wrapper for the "Receiver" POJO
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    // creating the container which will listen for messages
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args).close();
    }
}
