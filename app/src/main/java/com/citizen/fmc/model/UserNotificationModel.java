package com.citizen.fmc.model;

/**
 * ======> Created by dheeraj-gangwar on 2018-03-16 <======
 */

public class UserNotificationModel {
    private String messageId;
    private String messageFrom;
    private String messageDateTimeStamp;
    private String messageTitle;
    private String messageBody;
    private String messageImagePath;

    public UserNotificationModel(String messageId, String messageFrom,
                                 String messageDateTimeStamp, String messageTitle,
                                 String messageBody, String messageImagePath) {
        this.messageId = messageId;
        this.messageFrom = messageFrom;
        this.messageDateTimeStamp = messageDateTimeStamp;
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.messageImagePath = messageImagePath;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public String getMessageDateTimeStamp() {
        return messageDateTimeStamp;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getMessageImagePath() {
        return messageImagePath;
    }

    @Override
    public String toString() {
        return "UserNotificationModel{" +
                "messageId='" + messageId + '\'' +
                ", messageFrom='" + messageFrom + '\'' +
                ", messageDateTimeStamp='" + messageDateTimeStamp + '\'' +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageBody='" + messageBody + '\'' +
                ", messageImagePath='" + messageImagePath + '\'' +
                '}';
    }
}
