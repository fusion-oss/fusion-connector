package com.scoperetail.fusion.connector.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.scoperetail.fusion.connector.route.beans.TaskBean;

@Service
public class TimerRoute extends RouteBuilder {

  @Value("${timer.period.in.ms}")
  private String period;

  @Override
  public void configure() throws Exception {

    from("timer://tenantTask?period=" + period).bean(TaskBean.class)
        .loop(exchangeProperty("activeTaskCount"))
        .to("direct:executeTask")
        .end()
        .log("Scheduler job completed successfully.");
  }

}
