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
import org.springframework.stereotype.Service;

@Service
public class JsonSplitterRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:jsonSplitter")
    .split(jsonpath("$"))
    .streaming()
    .marshal()
    .json(true)
    .toD("direct:rabbitmq");    
  }

}
