package com.jizhang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jizhang.dto.LoginRequest;
import com.jizhang.dto.LoginResponse;
import com.jizhang.dto.RegisterRequest;
import com.jizhang.dto.RegisterResponse;
import com.jizhang.dto.StatisticsResponse;
import com.jizhang.dto.UpdateProfileRequest;
import com.jizhang.entity.OperationLog;
import com.jizhang.entity.User;
import com.jizhang.enums.EntityType;
import com.jizhang.enums.ErrorCode;
import com.jizhang.enums.OperationType;
import com.jizhang.enums.TransactionType;
import com.jizhang.exception.BusinessException;
import com.jizhang.repository.OperationLogRepository;
import com.jizhang.repository.TransactionRepository;
import com.jizhang.repository.UserRepository;
import com.jizhang.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OperationLogRepository operationLogRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setOccupation(request.getOccupation());
        user.setGender(request.getGender());

        User savedUser = userRepository.save(user);

        return new RegisterResponse(savedUser.getId(), savedUser.getPhone());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getId());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getPhone(),
                user.getName()
        );

        return new LoginResponse(token, userInfo);
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Map<String, Object> oldValue = buildUserMap(user);

        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setOccupation(request.getOccupation());
        user.setGender(request.getGender());

        User updatedUser = userRepository.save(user);

        Map<String, Object> newValue = buildUserMap(updatedUser);
        logOperation(userId, OperationType.UPDATE, EntityType.USER, userId, oldValue, newValue);

        return updatedUser;
    }

    private void logOperation(Long userId, OperationType operationType, EntityType entityType,
                             Long entityId, Object oldValue, Object newValue) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setOperationType(operationType);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOldValue(oldValue != null ? mapper.writeValueAsString(oldValue) : null);
            log.setNewValue(newValue != null ? mapper.writeValueAsString(newValue) : null);
            operationLogRepository.save(log);
        } catch (Exception e) {
            log.error("操作日志记录失败", e);
        }
    }

    public StatisticsResponse getStatistics(Long userId) {
        Long totalTransactions = transactionRepository.countByUserId(userId);
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        
        return new StatisticsResponse(totalTransactions, totalIncome, totalExpense);
    }

    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("age", user.getAge());
        map.put("occupation", user.getOccupation());
        map.put("gender", user.getGender());
        return map;
    }
}
