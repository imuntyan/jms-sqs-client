package com.example.jmssqs;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Session;
import java.util.function.Function;

/*
Resources:
https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-jms-code-examples.html
https://aws.amazon.com/blogs/developer/using-amazon-sqs-with-spring-boot-and-spring-jms/
 */

@SpringBootApplication
@EnableJms
public class JmsSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmsSqsApplication.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return value -> value.toUpperCase();
	}

	@JmsListener(destination = "sqs_jms_test.fifo", containerFactory = "sqsContainerFactory")
	public void receiveMessage(Message message) {
		System.out.println("Received <" + message + ">");
	}


	@Bean(name = "sqsContainerFactory")
	public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
			@Qualifier("sqsConnectionFactory") ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency("1");
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		factory.setSessionTransacted(false);
		return factory;
	}

	@Bean
	public SQSConnectionFactory sqsConnectionFactory() {
		AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard();
		builder.setCredentials(new AWSCredentialsProvider() {
			@Override
			public AWSCredentials getCredentials() {
				AWSCredentials credentials = new AWSCredentials() {
					@Override
					public String getAWSAccessKeyId() {
						return "access key";
					}

					@Override
					public String getAWSSecretKey() {
						return "secret key";
					}
				};
				return credentials;
			}

			@Override
			public void refresh() {

			}
		});
		builder.setRegion("us-east-2");

		SQSConnectionFactory sqsConnectionFactory = new SQSConnectionFactory(
				new ProviderConfiguration().withNumberOfMessagesToPrefetch(25),
				builder);
		return sqsConnectionFactory;
	}


}
