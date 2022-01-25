package com.scoperetail.fusion.connector.route;

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

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqRoute extends RouteBuilder {

  @Value("${rabbitmq.exchange}")
  private String exchange;

  @Value("${rabbitmq.queue}")
  private String queue;

  @Override
  public void configure() throws Exception {
    from("direct:rabbitmq")
        .to("rabbitmq:"
                + exchange
                + "?queue="
                + queue
                + "&autoDelete=false")
        .log("Sent message to queue :: " + queue)
        .end();
  }

}
