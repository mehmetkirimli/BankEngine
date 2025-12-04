package com.BankEngine.repository;

import com.BankEngine.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEvent,Long>
{

}
