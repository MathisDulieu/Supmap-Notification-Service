package com.novus.notification_service.services;

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
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MutationService {

    private final UserDaoUtils userDaoUtils;
    private final LogUtils logUtils;

    public void updateAuthenticatedUserNotificationPreferences(KafkaMessage kafkaMessage) {
        Map<String, String> request = kafkaMessage.getRequest();
        String isEmail = request.get("isEmail");
        User user = kafkaMessage.getAuthenticatedUser();

        try {
            user.getNotificationSettings().setEmailEnabled(Boolean.parseBoolean(isEmail));
            user.setUpdatedAt(new Date());
            user.setLastActivityDate(new Date());

            userDaoUtils.save(user);

            logUtils.buildAndSaveLog(
                    LogLevel.INFO,
                    "USER_NOTIFICATION_PREFERENCES_UPDATED",
                    kafkaMessage.getIpAddress(),
                    "User notification preferences updated successfully: Email notifications = " + isEmail,
                    HttpMethod.PUT,
                    "/notification/preferences",
                    "notification-service",
                    null,
                    user.getId()
            );
        } catch (Exception e) {
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
                    "/notification/preferences",
                    "notification-service",
                    stackTrace,
                    user.getId()
            );
            throw new RuntimeException("Failed to update notification preferences: " + e.getMessage(), e);
        }
    }
}

