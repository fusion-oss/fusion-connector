package com.scoperetail.fusion.connector.persistence.repository;

/*-
 * *****
 * ezsell-selleractive-connectors
 * -----
 * Copyright (C) 2018 - 2022 Scope Retail Systems Inc.
 * -----
 * This software is owned exclusively by Scope Retail Systems Inc.
 * As such, this software may not be copied, modified, or
 * distributed without express permission from Scope Retail Systems Inc.
 * =====
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.scoperetail.fusion.connector.persistence.entity.TaskLog;

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, String> {

}
