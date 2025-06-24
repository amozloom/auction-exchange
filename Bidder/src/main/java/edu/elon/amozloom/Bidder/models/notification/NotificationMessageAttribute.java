package edu.elon.amozloom.Bidder.models.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NotificationMessageAttribute {
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Value")
    private String value;

    public NotificationMessageAttribute() {
    }

    @JsonProperty("Type")
    public void setType(final String type) {
        this.type = type;
    }

    @JsonProperty("Value")
    public void setValue(final String value) {
        this.value = value;
    }
}

