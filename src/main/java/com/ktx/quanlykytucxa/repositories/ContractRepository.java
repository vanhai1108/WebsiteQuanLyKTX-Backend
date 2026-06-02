package com.ktx.quanlykytucxa.repositories;

import com.ktx.quanlykytucxa.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByStudentId(Long studentId);
    List<Contract> findByRoomId(Long roomId);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.student.id = :studentId AND c.status = 'ACTIVE'")
    long countActiveContractsByStudentId(@Param("studentId") Long studentId);
}
