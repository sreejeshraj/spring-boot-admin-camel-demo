package com.ustglobal.demo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@ConfigurationProperties(prefix="camel-demo-route")
@Data
@EqualsAndHashCode(callSuper=true)

public class CamelDemoRoute extends RouteBuilder {

	// The value of this property is injected from application.properties based on the profile chosen.
	private String injectedName;
	
	@Override
	public void configure() throws Exception {

		// @formatter:off
		
		errorHandler(deadLetterChannel("seda:errorQueue").maximumRedeliveries(5).redeliveryDelay(1000));

		from("file://{{inputFolder}}?delay=10s&noop=true")
		.routeId("InputFolderToTestSedaRoute")
		.setProperty("myProperty", constant("myPropertyValue"))
		.setHeader("myHeader", constant("MY_HEADER_CONSTANT_VALUE"))
		.to("seda://testSeda")
		.log("***** InputFolderToTestSedaRoute - exchangeProperty.myProperty:${exchangeProperty.myProperty}")
		.log("***** InputFolderToTestSedaRoute - exchangeId:${exchangeId}")
		.log(LoggingLevel.DEBUG, "**** Input File Pushed To Output Folder ***** :"+injectedName);

		from("seda://testSeda")
		.routeId("TestSedaToOutputFolderRoute")
		.log("***** TestSedaToOutputFolderRoute - exchangeProperty.myProperty:${exchangeProperty.myProperty}")
		.log("***** TestSedaToOutputFolderRoute - exchangeId:${exchangeId}")
		.to("file://{{outputFolder}}")
		.log("***** myHeader: ${header.myHeader} ***** :"+injectedName);
		
		//Error Handling route!
		
		from("seda:errorQueue")
		.routeId("ErrorHandlingRoute")
		.log("***** error body: ${body} *****")
		.log("***** Exception Caught: ${exception} *****");
		
		
		// @formatter:on

	}

}
