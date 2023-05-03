package com.example.demo.repository.Ban;

import com.example.demo.model.entity.Ban.BanHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedHistory extends JpaRepository<BanHistory,Long> {
    Page<BanHistory> findAllByBannedUserId(long bannedId, PageRequest request);

}
