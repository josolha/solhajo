package com.josolha.solhajo.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class BaseEntity {

    // Entity가 생성되어 저장될 때 시간이 자동 저장
    @CreatedDate
    private LocalDateTime createdDate;

    // 조회한 Entity 값을 변경할 때 시간이 자동 저장
    @LastModifiedDate
    private LocalDateTime updateDate;
}
