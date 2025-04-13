package com.novus.notification_service.services;

import com.novus.notification_service.configuration.DateConfiguration;
import com.novus.notification_service.dao.UserDaoUtils;
import com.novus.notification_service.utils.LogUtils;
import com.novus.shared_models.common.Kafka.KafkaMessage;
import com.novus.shared_models.common.Log.HttpMethod;
import com.novus.shared_models.common.Log.LogLevel;
import com.novus.shared_models.common.User.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MutationService {

    private final UserDaoUtils userDaoUtils;
    private final LogUtils logUtils;
    private final DateConfiguration dateConfiguration;

    public void updateAuthenticatedUserNotificationPreferences(KafkaMessage kafkaMessage) {
        Map<String, String> request = kafkaMessage.getRequest();
        String isEmail = request.get("isEmail");
        User user = kafkaMessage.getAuthenticatedUser();
        log.info("Starting to process update notification preferences request for user with ID: {}", user.getId());

        try {
            user.getNotificationSettings().setEmailEnabled(Boolean.parseBoolean(isEmail));
            user.setUpdatedAt(dateConfiguration.newDate());
            user.setLastActivityDate(dateConfiguration.newDate());

            userDaoUtils.save(user);

            logUtils.buildAndSaveLog(
                    LogLevel.INFO,
                    "USER_NOTIFICATION_PREFERENCES_UPDATED",
                    kafkaMessage.getIpAddress(),
                    "User notification preferences updated successfully: Email notifications = " + isEmail,
                    HttpMethod.PUT,
                    "/private/notification/preferences",
                    "notification-service",
                    null,
                    user.getId()
            );
            log.info("Notification preferences successfully updated for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Error occurred while processing update notification preferences request: {}", e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();

            logUtils.buildAndSaveLog(
                    LogLevel.ERROR,
                    "USER_NOTIFICATION_PREFERENCES_UPDATE_ERROR",
                    kafkaMessage.getIpAddress(),
                    "Error updating user notification preferences for userId: " + user.getId() + ", error: " + e.getMessage(),
                    HttpMethod.PUT,
                    "/private/notification/preferences",
                    "notification-service",
                    stackTrace,
                    user.getId()
            );
            throw new RuntimeException("Failed to update notification preferences: " + e.getMessage(), e);
        }
    }
}

