package com.ispan.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Integer> {
    // 移除 findByGroupId 方法
}
