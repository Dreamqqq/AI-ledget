package com.jizhang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jizhang.entity.OperationLog;
import com.jizhang.enums.EntityType;
import com.jizhang.enums.OperationType;
import com.jizhang.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void log(Long userId, OperationType operationType, EntityType entityType, 
                   Long entityId, Object oldValue, Object newValue) {
        try {
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setOperationType(operationType);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null);
            log.setNewValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null);
            
            operationLogRepository.save(log);
        } catch (JsonProcessingException e) {
            log.error("操作日志记录失败", e);
        }
    }
}
