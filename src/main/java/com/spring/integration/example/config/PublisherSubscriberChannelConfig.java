package com.spring.integration.example.config;

//@Configuration
//@EnableIntegration
public class PublisherSubscriberChannelConfig {

	/*public String INPUT_DIR = "F:\\Shwetali Office\\SampleJavaFiles\\";
	public String OUTPUT_DIR = "F:\\Shwetali Office\\SampleJavaFiles\\DestinationFolder\\";
	public String FILE_PATTERN = "*.txt";

	@Bean
	public MessageChannel pubSubFileChannel() {
		return new PublishSubscribeChannel();
	}

	@Bean
	@InboundChannelAdapter(value = "publisherSubscriberChannel", poller = @Poller(fixedDelay = "1000"))
	public MessageSource<File> fileReadingMessagingSource() {
		FileReadingMessageSource source = new FileReadingMessageSource();
		source.setDirectory(new File(INPUT_DIR));
		source.setFilter(new SimplePatternFileListFilter("*.txt"));
		return source;

	}
	
	public MessageHandler fileWritingMessageHandler() {
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
		handler.setFileExistsMode(FileExistsMode.REPLACE);
		handler.setExpectReply(false);

		return handler;
	}*/
}
