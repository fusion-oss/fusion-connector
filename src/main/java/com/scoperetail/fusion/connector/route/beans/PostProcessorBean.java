package com.scoperetail.fusion.connector.route.beans;

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
import java.time.LocalDateTime;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import com.scoperetail.fusion.connector.common.TaskStatus;
import com.scoperetail.fusion.connector.persistence.entity.CustomerTask;
import com.scoperetail.fusion.connector.persistence.entity.TaskLog;
import com.scoperetail.fusion.connector.persistence.repository.CustomerTaskRepository;
import com.scoperetail.fusion.connector.persistence.repository.TaskLogRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostProcessorBean {

  @Autowired
  private CustomerTaskRepository customerTaskRepository;

  @Autowired
  private TaskLogRepository logRepository;

  public void setTasks(Exchange exchange) {
    CustomerTask customerTask = exchange.getProperty("customerTask", CustomerTask.class);

    String response = exchange.getIn().getBody(String.class);
    String correlationId = exchange.getProperty(CORRELATION_ID, String.class);
    log.info("Response :: {}", response);
    saveLogIEntity((LocalDateTime) exchange.getProperty("fromTime"),
        (LocalDateTime) exchange.getProperty("toTime"), customerTask, response, correlationId);
    customerTask.setLatestCheckpoint((LocalDateTime) exchange.getProperty("toTime"));
    customerTaskRepository.save(customerTask);
    exchange.getIn().setBody(response);
  }

  private void saveLogIEntity(final LocalDateTime from, final LocalDateTime to,
      final CustomerTask customerTask, String response, String correlationId) {
    Integer orderCount = new JSONArray(response).length();
    log.info("Customer :: {}, task :: {}, correlationId :: {},  order count :: {}",
        customerTask.getCustomer().getId(), customerTask.getTaskName(), correlationId, orderCount);
    TaskLog taskLog =
        TaskLog.builder().customerTask(customerTask).from(from).to(to).correlationId(correlationId)
            .received(orderCount.longValue()).taskStatus(TaskStatus.SUCCESS.name()).build();
    logRepository.save(taskLog);
    log.info("Saved successfully");
  }

}
