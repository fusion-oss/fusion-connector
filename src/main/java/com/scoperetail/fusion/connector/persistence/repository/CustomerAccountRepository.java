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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.scoperetail.fusion.connector.persistence.entity.CustomerAccount;

@Repository
public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Integer> {

}