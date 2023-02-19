package com.zerobase.fastlms.admin.repository;

import com.zerobase.fastlms.admin.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    Optional<LoginHistory> findFirstByUserIdOrderByLoggedDtDesc(String userId);

    List<LoginHistory> findAllByUserIdOrderByLoggedDtDesc(String userId);
}
