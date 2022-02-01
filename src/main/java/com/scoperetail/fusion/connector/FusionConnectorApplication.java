package com.scoperetail.fusion.connector;

import static com.scoperetail.fusion.connector.common.Constants.UTC;
import java.util.TimeZone;
import org.springframework.beans.factory.InitializingBean;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.scoperetail.fusion.connector.persistence.repository")
public class FusionConnectorApplication implements InitializingBean {

  public static void main(String[] args) {
    SpringApplication.run(FusionConnectorApplication.class, args);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone(UTC));
    log.info("Application timezone set to : {}", TimeZone.getDefault());
  }

}
