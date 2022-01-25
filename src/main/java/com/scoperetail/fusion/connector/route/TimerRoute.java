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

import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.scoperetail.fusion.connector.persistence.entity.CustomerTask;
import com.scoperetail.fusion.connector.route.beans.CustomerTaskBean;

@Service
public class TimerRoute extends RouteBuilder {

  @Value("${timer.period.in.ms}")
  private String period;
  
  @Override
  public void configure() throws Exception {

    from("timer://customerTask?period=" + period)
        .bean(CustomerTaskBean.class)
        .loop(exchangeProperty("customerTaskCount"))
        .process(
            exchange -> {
              Integer index = (Integer) exchange.getProperty(Exchange.LOOP_INDEX);
              CustomerTask customerTask =
                  (CustomerTask) exchange.getProperty("activeCustomers", List.class).get(index);
              exchange.setProperty("customerTask", customerTask);
            })
        .to("direct:executeTask")
        .end()
        .log("Scheduler job completed successfully.");
  }
  
}
