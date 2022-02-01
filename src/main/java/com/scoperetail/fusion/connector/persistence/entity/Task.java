package com.scoperetail.fusion.connector.persistence.entity;

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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne()
  @JoinColumn(referencedColumnName = "id")
  private Tenant tenant;

  @Column(name = "scheduler_name")
  private String schedulerName;

  @Column(name = "task_name")
  private String taskName;

  @Column(name = "task_data")
  private String taskData;

  @Column(name = "latest_checkpoint")
  private LocalDateTime latestCheckpoint;

  @Column(name = "initial_checkpoint")
  private LocalDateTime initialCheckPoint;

  @CreationTimestamp
  @Column(name = "create_ts")
  private LocalDateTime createTs;
  
  @UpdateTimestamp
  @Column(name = "update_ts")
  private LocalDateTime updateTs;

}
