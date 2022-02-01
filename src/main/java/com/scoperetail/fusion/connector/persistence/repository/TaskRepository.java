package com.scoperetail.fusion.connector.persistence.repository;

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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.scoperetail.fusion.connector.persistence.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

  static final String FIND_TASK_BY_SCHEDULER_NAME =
      "select tt from Task tt JOIN FETCH tt.tenant t "
          + "WHERE t.isEnabled=1 and tt.schedulerName=:schedulerName";

  @Query(value = FIND_TASK_BY_SCHEDULER_NAME)
  List<Task> findBySchedulerName(@Param("schedulerName") String schedularName);

}
