package com.sreejesh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.Data;

@Data
@SpringBootApplication
//@ImportResource("classpath:META-INF/spring/applicationContext.xml") 
public class CamelSqlMainClass {
	
/*	@Autowired 
	private JdbcTemplate jdbcTemplate;*/

	public static void main(String[] args) {
		SpringApplication.run(CamelSqlMainClass.class, args);

	}

	/*@Override
	public void run(String... args) throws Exception {
		// Add your application logic here
        System.out.println("Hello Spring Boot from console!");
        List resultSetList = jdbcTemplate.queryForList("SELECT version()");
        System.exit(0);
		
	}*/

}
