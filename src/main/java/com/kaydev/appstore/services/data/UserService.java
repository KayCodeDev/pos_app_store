package com.kaydev.appstore.services.data;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kaydev.appstore.models.dto.objects.UserMinObj;
import com.kaydev.appstore.models.dto.objects.UserObj;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.models.entities.UserLog;
import com.kaydev.appstore.models.enums.StatusType;
import com.kaydev.appstore.models.enums.UserType;
import com.kaydev.appstore.repository.RoleRepository;
import com.kaydev.appstore.repository.UserLogRepository;
import com.kaydev.appstore.repository.UserRepository;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public Page<UserMinObj> getAllUserByFilter(Pageable pageable, String search,
            UserType userType,
            Long developerId,
            StatusType status, String role) {
        // Specification<User> spec = userSpecification.buildSpecification(
        // search,
        // userType,
        // developerId,
        // status, role);

        return userRepository.findAllUsersByFilter(pageable, search, userType, developerId, role);
    }

    public User getUserByUuid(@NonNull String uuid) {
        return userRepository.findByUuid(uuid).orElse(null);
    }

    public List<String> getDeveloperUserEmails(Long developerId) {
        return userRepository.findAllEmailsByDeveloperId(developerId);
    }

    public UserObj getUserByUuidAndDeveloper(@NonNull String uuid, Developer developer) {
        return userRepository.findByUuidAndDeveloper(uuid, developer).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserLogRepository getUserLogRepository() {
        return userLogRepository;
    }

    public void saveUserLog(@NonNull User user, @NonNull String message) {
        UserLog log = UserLog.builder().activity(message).user(user).build();
        userLogRepository.save(log);
    }

    public Page<UserLog> getUserLogs(Pageable pageable, Long userId, LocalDateTime start, LocalDateTime end) {
        return userLogRepository.findAllByUserIdAndDate(pageable, userId, start, end);
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }

}
