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

import java.time.LocalDateTime;
import java.util.List;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.scoperetail.fusion.connector.persistence.entity.Task;
import com.scoperetail.fusion.connector.persistence.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskBean {

  @Value("${schedularName}")
  private String schedularName;

  @Autowired
  private TaskRepository taskRepository;

  public void setTasks(Exchange exchange) {
    List<Task> tasks = taskRepository.findBySchedulerName(schedularName);
    log.info("Acitve tasks :: {}", tasks.size());
    exchange.setProperty("toTime", LocalDateTime.now());
    exchange.setProperty("activeTenants", tasks);
    exchange.setProperty("tenantTaskCount", tasks.size());
  }

}
