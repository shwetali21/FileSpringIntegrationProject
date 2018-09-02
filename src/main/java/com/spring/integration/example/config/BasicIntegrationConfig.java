package com.spring.integration.example.config;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.MessageCountReleaseStrategy;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.splitter.FileSplitter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.spring.integration.example.utils.InputFileConstants;

/**
 * The Class BasicIntegrationConfig.
 */
@EnableIntegration	// designates this class as a Spring Integration configuration.
@Configuration
public class BasicIntegrationConfig {

	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(BasicIntegrationConfig.class);

	/*The org.springframework.integration.Message interface defines the spring Message: 
	 * the unit of data transfer within a Spring Integration context.
	 * This bean is initialized by Spring Integration. (not a compulsory bean)
	 */
	@Bean
	public MessageChannel newFileChannel() {
		LOGGER.info("-----------Inside Message Channel");
		return new DirectChannel();
	}


	/**
	 * File reading message source.
	 *
	 * @return the message source
	 */
	/*A channel in Spring Integration (and indeed, EAI) is the basic plumbing in an integration architecture. 
	 * It’s the pipe by which messages are relayed from one system to another.
	 */
	@Bean
	@InboundChannelAdapter(value = "newfileChannel", poller = @Poller(fixedDelay = "1000"))
	public MessageSource<File> fileReadingMessageSource() {
		LOGGER.info("------------ Inside Message Source - @InboundChannelAdapter");
		FileReadingMessageSource sourceReader = new FileReadingMessageSource();
		LOGGER.info("-----------Set file directory to read file");
		sourceReader.setDirectory(new File(InputFileConstants.INPUT_DIR));
		sourceReader.setFilter(new AcceptOnceFileListFilter<>());
		return sourceReader;
	}

	/*@Transformer(inputChannel = "newfileChannel", outputChannel = "transformedfile")
	@Bean
	public FileToStringTransformer transform() {
		LOGGER.info("------------ Inside @Transformer");
		return new FileToStringTransformer();
	}*/

	/**
	 * File splitter.
	 *
	 * @return the file splitter
	 */
	@Bean
	@Splitter(inputChannel = "newfileChannel") 
	public FileSplitter fileSplitter() {
		LOGGER.info("------------ Inside @Splitter");
		FileSplitter splitter = new FileSplitter(true, false);
		splitter.setApplySequence(true);
		splitter.setOutputChannelName("fileSplit");
		return splitter;
	}

	/**
	 * Chunker.
	 *
	 * @return the aggregating message handler
	 */
	@Bean
	@ServiceActivator(inputChannel = "fileSplit")
	public AggregatingMessageHandler chunker() {
		LOGGER.info("------------ Inside AggregatingMessageHandler");
		AggregatingMessageHandler aggregator = new AggregatingMessageHandler(new DefaultAggregatingMessageGroupProcessor());
		aggregator.setReleaseStrategy(new MessageCountReleaseStrategy(5));
		aggregator.setExpireGroupsUponCompletion(true);
		aggregator.setGroupTimeoutExpression(new ValueExpression<>(100L));
		aggregator.setSendPartialResultOnExpiry(true);
		aggregator.setOutputChannelName("data");
		LOGGER.info("------------ Aggregating complete");

		return aggregator;
	}

	/**
	 * Handler.
	 *
	 * @return the message handler
	 */
	//@Bean
	//@ServiceActivator(inputChannel = "data")
	/*public MessageHandler handler() {
		LOGGER.info("------------ Inside @ServiceActivator");
		return new MessageHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message<?> message) {
				LOGGER.info("Inside handleMessage method");
				List<String> strings = (List<String>) message.getPayload();
				LOGGER.info("Message Headers : {}", message.getHeaders());
				LOGGER.info("Message Payload : {}, List Size: {}", strings, strings.size());
			}
		};
	}*/

	@SuppressWarnings("unchecked")
	@Bean
	@ServiceActivator(inputChannel = "data")
	public MessageHandler handler() {
		LOGGER.info("------------ Inside @ServiceActivator");
		return message -> {
			LOGGER.info("Inside handleMessage method");
			List<String> strings = (List<String>) message.getPayload();
			LOGGER.info("Message Headers : {}", message.getHeaders());
			LOGGER.info("Message Payload : {}, List Size: {}", strings, strings.size());
		};
	}

}