package com.sreejesh.demo.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
@EqualsAndHashCode(callSuper = true)

public class CamelDemoRoute extends RouteBuilder {

		
	@Override
	public void configure() throws Exception {

		// @formatter:off
		
		
		
		//SELECT name FROM person WHERE id=1;
		
		//Table creation script
		/*
		
		CREATE TABLE `person` (
				`id` INT(11) NOT NULL AUTO_INCREMENT,
				`Name` VARCHAR(5) NOT NULL,
				PRIMARY KEY (`id`)
			)
			COLLATE='latin1_swedish_ci'
			ENGINE=InnoDB
			AUTO_INCREMENT=12
			;
		*/
						
		from("timer://dbQueryTimer?period=10s&repeatCount=1")
		.routeId("SqlPaginationRoute")
		//.setBody(new ParameterMapSupplier())
		//.to("sql:SELECT version()?dataSource=#dataSource")	
		//.to("sql:SELECT name FROM person WHERE id=:#id?dataSource=#dataSource")
		.loopDoWhile(new CheckIfResultSetIsEmptyPredicate())
		//.setBody(new ParameterMapSupplier())
		.process(new SqlQueryParameterSetterProcessor())
		.to("sql:SELECT name FROM person limit :#startRowNum,:#pageSize?dataSource=#dataSource")
		.log("******STEP 20: Database query executed - body:${body}******")
		.end()
		.log("******STEP 100: Pagination Completed!!!******")
		;
		
		
		// @formatter:on

	}
	
	
	private final class SqlQueryParameterSetterProcessor implements Processor {
		@Override
		public void process(Exchange exchange) throws Exception {
			int camelLoopIndex = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
			int startRowNum = camelLoopIndex*3;
			Map<String,Integer> sqlQueryParameterMap = new HashMap<>();
			sqlQueryParameterMap.put("startRowNum", startRowNum);
			sqlQueryParameterMap.put("pageSize", 3);
			exchange.getIn().setBody(sqlQueryParameterMap);
		}
	}


	private final class CheckIfResultSetIsEmptyPredicate implements Predicate {
		@Override
		public boolean matches(Exchange exchange) {
			List resultSetList = exchange.getIn().getBody(List.class);
			boolean resultSetIsNotEmpty = (!resultSetList.isEmpty());
			return resultSetIsNotEmpty;
		}
	}


	//This inner class not required. Written while testing this.
	private final class ParameterMapSupplier implements Supplier<Map> {
		@Override
		public Map<String,Integer> get() {
			Map<String,Integer> sqlQueryParameterMap = new HashMap<>();
			sqlQueryParameterMap.put("startRowNum", 0);
			sqlQueryParameterMap.put("pageSize", 3);
			/*sqlQueryParameterMap.put("startRowNum", 10);
			sqlQueryParameterMap.put("pageSize", 3);*/
			return sqlQueryParameterMap;
		}
	}


}
