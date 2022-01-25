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

import static com.scoperetail.fusion.connector.common.Constants.CORRELATION_ID;
import static com.scoperetail.fusion.connector.common.Constants.EVENT_TYPE;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.connector.common.JsonUtils;
import com.scoperetail.fusion.connector.persistence.dto.HttpTaskData;
import com.scoperetail.fusion.connector.persistence.dto.TaskData;
import com.scoperetail.fusion.connector.persistence.entity.CustomerTask;
import com.scoperetail.fusion.connector.route.beans.PostProcessorBean;

@Service
public class ExecuteTaskRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    
    from("direct:executeTask")
    .process(
        exchange -> {
          CustomerTask customerTask = exchange.getProperty("customerTask", CustomerTask.class);
          String customerTaskData = customerTask.getTaskData();
          TaskData task =
              JsonUtils.unmarshal(
                  Optional.of(customerTaskData), Optional.of(new TypeReference<TaskData>() {}));
          String uuid =  UUID.randomUUID().toString();
          exchange.setProperty(CORRELATION_ID,uuid);
          log.info("CustomerId :: {}, TaskId :: {}, CorrelationId :: {}",
              customerTask.getCustomer().getId(), customerTask.getId(), uuid);
          if (task.getTaskType().equals("http")) {
            processHttpTask(exchange, customerTask, customerTaskData);
          }
        })
    .toD("${header.CamelHttpUrl}")
    .removeHeaders("*")
    .setHeader(EVENT_TYPE, exchangeProperty(EVENT_TYPE))
    .setHeader(CORRELATION_ID, exchangeProperty(CORRELATION_ID))
    .to("direct:jsonSplitter")
    .bean(PostProcessorBean.class)
    .end();
  }

  private void processHttpTask(Exchange exchange, CustomerTask customerTask,
      String customerTaskData) throws IOException {
    HttpTaskData httpTask = JsonUtils.unmarshal(Optional.of(customerTaskData),
        Optional.of(new TypeReference<HttpTaskData>() {}));

    String url = httpTask.getUrl();
    String port = httpTask.getPort();
    String methodType = httpTask.getMethodType();
    Map<String, String> queryParams = httpTask.getQueryParams();

    exchange.setProperty("fromTime", getLastCheckPoint(customerTask));
    exchange.setProperty(EVENT_TYPE, customerTask.getTaskName());

    exchange.getIn().setHeader(Exchange.HTTP_URL, url);
    exchange.getIn().setHeader(Exchange.HTTP_METHOD, methodType);
    exchange.getIn().setHeader(Exchange.HTTP_PORT, port);
    String params = setQueryParams(exchange, queryParams);
    exchange.getIn().setHeader(Exchange.HTTP_QUERY, params);
    exchange.getIn().setHeader(HttpHeaders.AUTHORIZATION, setAuthHeader(customerTask));

    Optional.ofNullable(httpTask.getHttpHeaders())
        .ifPresent(map -> map.entrySet().forEach(header -> {
          exchange.getIn().setHeader(header.getKey(), header.getValue());
        }));
    log.info("URL :: {}?{}", url, params);
  }

  private LocalDateTime getLastCheckPoint(CustomerTask customerTask) {
    LocalDateTime latestCheckpoint = customerTask.getLatestCheckpoint();
    return Objects.isNull(latestCheckpoint) ? customerTask.getInitialCheckPoint()
        : latestCheckpoint;
  }

  private String setAuthHeader(CustomerTask customerTask) {
    return "Basic " + HttpHeaders.encodeBasicAuth(customerTask.getCustomer().getAuthName(),
        customerTask.getCustomer().getAuthPassword(), null);
  }

  private String setQueryParams(Exchange exchange, Map<String, String> queryParams) {
    String params = null;
    List<String> paramList = new ArrayList<>();

    Optional.ofNullable(queryParams).ifPresent(map -> map.entrySet().forEach(param -> {
      String paramValue = exchange.getProperty(param.getValue()) != null
          ? exchange.getProperty(param.getValue()).toString()
          : param.getValue();
      paramList.add(param.getKey() + "=" + paramValue);
    }));
    if (!CollectionUtils.isEmpty(paramList)) {
      params = paramList.stream().collect(Collectors.joining("&"));
    }
    return params;
  }

}
