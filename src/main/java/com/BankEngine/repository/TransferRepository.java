package com.BankEngine.repository;

import com.BankEngine.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer,Long>
{

}
