package com.scoperetail.fusion.connector.config;

/*-
 * *****
 * fusion-connector
 * -----
 * Copyright (C) 2018 - 2022 Scope Retail Systems Inc.
 * -----
 * This software is owned exclusively by Scope Retail Systems Inc.
 * As such, this software may not be copied, modified, or
 * distributed without express permission from Scope Retail Systems Inc.
 * =====
 */

import static com.scoperetail.fusion.connector.common.Constants.UTC;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.scoperetail.fusion.connector.persistence.repository")
@ComponentScan
@Slf4j
public class EzsellServiceConfig {
  
  @Value("${rabbitmq.host}")
  private String host;

  @Value("${rabbitmq.port}")
  private Integer port;

  @Value("${rabbitmq.username}")
  private String username;

  @Value("${rabbitmq.password}")
  private String password;

  @Bean
  public ConnectionFactory rabbitConnectionFactory() {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(host);
      factory.setPort(port);
      factory.setUsername(username);
      factory.setPassword(password);
      return factory;
  }
  
  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone(UTC));
    log.info("Application timezone set to : {}", TimeZone.getDefault());
  }

}
