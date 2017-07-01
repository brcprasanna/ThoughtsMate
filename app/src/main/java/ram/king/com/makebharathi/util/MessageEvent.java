package ram.king.com.makebharathi.util;

import android.app.Activity;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

public class MessageEvent {
    private final String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
