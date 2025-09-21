package com.heart.mapper;

import com.heart.entity.HeartJobInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartJobInfoRepository extends JpaRepository<HeartJobInfoEntity, Long> {

    // 复杂查询示例
    @Query("SELECT h FROM HeartJobInfoEntity h WHERE h.loadServerName IN :names")
    List<HeartJobInfoEntity> findByServerNames(@Param("names") List<String> names);

    Page<HeartJobInfoEntity> findAll(Pageable pageable);
}
