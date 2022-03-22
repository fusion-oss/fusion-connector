package com.scoperetail.fusion.connector.route;

/*-
 * *****
 * fusion-connector
 * -----
 * Copyright (C) 2018 - 2022 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.connector.common.JsonUtils;
import com.scoperetail.fusion.connector.persistence.dto.HttpTaskData;
import com.scoperetail.fusion.connector.persistence.dto.TaskData;
import com.scoperetail.fusion.connector.persistence.entity.Task;
import com.scoperetail.fusion.connector.route.beans.PostProcessorBean;

@Service
public class ExecuteTaskRoute extends RouteBuilder {

  @Value("${tenantIdentifierHeader:tenantId}")
  private String tenantIdentifierHeader;

  @Override
  public void configure() throws Exception {

    from("direct:executeTask")
        .process(
            exchange -> {
              Integer index = (Integer) exchange.getProperty(Exchange.LOOP_INDEX);
              Task tenantTask = (Task) exchange.getProperty("activeTasks", List.class).get(index);
              String tenantTaskData = tenantTask.getTaskData();
              TaskData task =
                  JsonUtils.unmarshal(
                      Optional.of(tenantTaskData), Optional.of(new TypeReference<TaskData>() {}));
              String uuid = UUID.randomUUID().toString();
              exchange.setProperty(CORRELATION_ID, uuid);
              exchange.setProperty(tenantIdentifierHeader, tenantTask.getTenant().getName());
              exchange.setProperty("tenantTask", tenantTask);
              log.info(
                  "TenantId :: {}, TaskId :: {}, CorrelationId :: {}",
                  tenantTask.getTenant().getId(),
                  tenantTask.getId(),
                  uuid);
              if (task.getTaskType().equals("http")) {
                buildHttpTask(exchange, tenantTask, tenantTaskData);
              }
            })
        .toD("${header.CamelHttpUrl}")
        .streamCaching()
        .removeHeaders("*")
        .setHeader(EVENT_TYPE, exchangeProperty(EVENT_TYPE))
        .setHeader(CORRELATION_ID, exchangeProperty(CORRELATION_ID))
        .setHeader(tenantIdentifierHeader, exchangeProperty(tenantIdentifierHeader))
        .to("direct:jsonSplitter")
        .bean(PostProcessorBean.class)
        .end();
  }

  private void buildHttpTask(Exchange exchange, Task task, String taskData) throws IOException {
    HttpTaskData httpTask =
        JsonUtils.unmarshal(
            Optional.of(taskData), Optional.of(new TypeReference<HttpTaskData>() {}));

    String url = httpTask.getUrl();
    String port = httpTask.getPort();
    String methodType = httpTask.getMethodType();
    Map<String, String> queryParams = httpTask.getQueryParams();

    exchange.setProperty("fromTime", getLastCheckPoint(task));
    exchange.setProperty(EVENT_TYPE, task.getTaskName());

    exchange.getIn().setHeader(Exchange.HTTP_URL, url);
    exchange.getIn().setHeader(Exchange.HTTP_METHOD, methodType);
    exchange.getIn().setHeader(Exchange.HTTP_PORT, port);
    String params = getQueryParams(exchange, queryParams);
    exchange.getIn().setHeader(Exchange.HTTP_QUERY, params);

    Optional.ofNullable(httpTask.getHeaders())
        .ifPresent(
            map ->
                map.entrySet()
                    .forEach(
                        header -> {
                          exchange.getIn().setHeader(header.getKey(), header.getValue());
                        }));
    log.info("URL :: {}?{}", url, params);
  }

  private LocalDateTime getLastCheckPoint(Task task) {
    LocalDateTime latestCheckpoint = task.getLatestCheckpoint();
    return Objects.isNull(latestCheckpoint) ? task.getInitialCheckPoint() : latestCheckpoint;
  }

  private String getQueryParams(Exchange exchange, Map<String, String> queryParams) {
    String params = null;
    List<String> paramList = new ArrayList<>();

    Optional.ofNullable(queryParams)
        .ifPresent(
            map ->
                map.entrySet()
                    .forEach(
                        param -> {
                          String paramValue =
                              exchange.getProperty(param.getValue()) != null
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
