// Autogenerated from Pigeon (v0.1.14), do not edit directly.
// See also: https://pub.dev/packages/pigeon

package dr.achim.sleep_timer;

import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import java.util.ArrayList;
import java.util.HashMap;

/** Generated class from Pigeon. */
@SuppressWarnings("unused")
public class Messages {

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class InitializationRequest {
    private Long callbackHandle;
    public Long getCallbackHandle() { return callbackHandle; }
    public void setCallbackHandle(Long setterArg) { this.callbackHandle = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("callbackHandle", callbackHandle);
      return toMapResult;
    }
    static InitializationRequest fromMap(HashMap map) {
      InitializationRequest fromMapResult = new InitializationRequest();
      Object callbackHandle = map.get("callbackHandle");
      fromMapResult.callbackHandle = (callbackHandle == null) ? null : ((callbackHandle instanceof Integer) ? (Integer)callbackHandle : (Long)callbackHandle);
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class NotificationResponse {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    private Boolean success;
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean setterArg) { this.success = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      toMapResult.put("success", success);
      return toMapResult;
    }
    static NotificationResponse fromMap(HashMap map) {
      NotificationResponse fromMapResult = new NotificationResponse();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      Object success = map.get("success");
      fromMapResult.success = (Boolean)success;
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class TimeNotificationRequest {
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String setterArg) { this.description = setterArg; }

    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String setterArg) { this.title = setterArg; }

    private String restartAction;
    public String getRestartAction() { return restartAction; }
    public void setRestartAction(String setterArg) { this.restartAction = setterArg; }

    private String continueAction;
    public String getContinueAction() { return continueAction; }
    public void setContinueAction(String setterArg) { this.continueAction = setterArg; }

    private String pauseAction;
    public String getPauseAction() { return pauseAction; }
    public void setPauseAction(String setterArg) { this.pauseAction = setterArg; }

    private String cancelAction;
    public String getCancelAction() { return cancelAction; }
    public void setCancelAction(String setterArg) { this.cancelAction = setterArg; }

    private ArrayList extendActions;
    public ArrayList getExtendActions() { return extendActions; }
    public void setExtendActions(ArrayList setterArg) { this.extendActions = setterArg; }

    private Long duration;
    public Long getDuration() { return duration; }
    public void setDuration(Long setterArg) { this.duration = setterArg; }

    private Long remainingTime;
    public Long getRemainingTime() { return remainingTime; }
    public void setRemainingTime(Long setterArg) { this.remainingTime = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("description", description);
      toMapResult.put("timerId", timerId);
      toMapResult.put("title", title);
      toMapResult.put("restartAction", restartAction);
      toMapResult.put("continueAction", continueAction);
      toMapResult.put("pauseAction", pauseAction);
      toMapResult.put("cancelAction", cancelAction);
      toMapResult.put("extendActions", extendActions);
      toMapResult.put("duration", duration);
      toMapResult.put("remainingTime", remainingTime);
      return toMapResult;
    }
    static TimeNotificationRequest fromMap(HashMap map) {
      TimeNotificationRequest fromMapResult = new TimeNotificationRequest();
      Object description = map.get("description");
      fromMapResult.description = (String)description;
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      Object title = map.get("title");
      fromMapResult.title = (String)title;
      Object restartAction = map.get("restartAction");
      fromMapResult.restartAction = (String)restartAction;
      Object continueAction = map.get("continueAction");
      fromMapResult.continueAction = (String)continueAction;
      Object pauseAction = map.get("pauseAction");
      fromMapResult.pauseAction = (String)pauseAction;
      Object cancelAction = map.get("cancelAction");
      fromMapResult.cancelAction = (String)cancelAction;
      Object extendActions = map.get("extendActions");
      fromMapResult.extendActions = (ArrayList)extendActions;
      Object duration = map.get("duration");
      fromMapResult.duration = (duration == null) ? null : ((duration instanceof Integer) ? (Integer)duration : (Long)duration);
      Object remainingTime = map.get("remainingTime");
      fromMapResult.remainingTime = (remainingTime == null) ? null : ((remainingTime instanceof Integer) ? (Integer)remainingTime : (Long)remainingTime);
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class NotificationRequest {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String setterArg) { this.title = setterArg; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String setterArg) { this.description = setterArg; }

    private String restartAction;
    public String getRestartAction() { return restartAction; }
    public void setRestartAction(String setterArg) { this.restartAction = setterArg; }

    private String continueAction;
    public String getContinueAction() { return continueAction; }
    public void setContinueAction(String setterArg) { this.continueAction = setterArg; }

    private String pauseAction;
    public String getPauseAction() { return pauseAction; }
    public void setPauseAction(String setterArg) { this.pauseAction = setterArg; }

    private String cancelAction;
    public String getCancelAction() { return cancelAction; }
    public void setCancelAction(String setterArg) { this.cancelAction = setterArg; }

    private ArrayList extendActions;
    public ArrayList getExtendActions() { return extendActions; }
    public void setExtendActions(ArrayList setterArg) { this.extendActions = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      toMapResult.put("title", title);
      toMapResult.put("description", description);
      toMapResult.put("restartAction", restartAction);
      toMapResult.put("continueAction", continueAction);
      toMapResult.put("pauseAction", pauseAction);
      toMapResult.put("cancelAction", cancelAction);
      toMapResult.put("extendActions", extendActions);
      return toMapResult;
    }
    static NotificationRequest fromMap(HashMap map) {
      NotificationRequest fromMapResult = new NotificationRequest();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      Object title = map.get("title");
      fromMapResult.title = (String)title;
      Object description = map.get("description");
      fromMapResult.description = (String)description;
      Object restartAction = map.get("restartAction");
      fromMapResult.restartAction = (String)restartAction;
      Object continueAction = map.get("continueAction");
      fromMapResult.continueAction = (String)continueAction;
      Object pauseAction = map.get("pauseAction");
      fromMapResult.pauseAction = (String)pauseAction;
      Object cancelAction = map.get("cancelAction");
      fromMapResult.cancelAction = (String)cancelAction;
      Object extendActions = map.get("extendActions");
      fromMapResult.extendActions = (ArrayList)extendActions;
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class CancelResponse {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    private Boolean success;
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean setterArg) { this.success = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      toMapResult.put("success", success);
      return toMapResult;
    }
    static CancelResponse fromMap(HashMap map) {
      CancelResponse fromMapResult = new CancelResponse();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      Object success = map.get("success");
      fromMapResult.success = (Boolean)success;
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class CancelRequest {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      return toMapResult;
    }
    static CancelRequest fromMap(HashMap map) {
      CancelRequest fromMapResult = new CancelRequest();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class ExtendTimeResponse {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    private Long additionalTime;
    public Long getAdditionalTime() { return additionalTime; }
    public void setAdditionalTime(Long setterArg) { this.additionalTime = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      toMapResult.put("additionalTime", additionalTime);
      return toMapResult;
    }
    static ExtendTimeResponse fromMap(HashMap map) {
      ExtendTimeResponse fromMapResult = new ExtendTimeResponse();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      Object additionalTime = map.get("additionalTime");
      fromMapResult.additionalTime = (additionalTime == null) ? null : ((additionalTime instanceof Integer) ? (Integer)additionalTime : (Long)additionalTime);
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class TimerRequest {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      return toMapResult;
    }
    static TimerRequest fromMap(HashMap map) {
      TimerRequest fromMapResult = new TimerRequest();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      return fromMapResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static class OpenRequest {
    private String timerId;
    public String getTimerId() { return timerId; }
    public void setTimerId(String setterArg) { this.timerId = setterArg; }

    HashMap toMap() {
      HashMap<String, Object> toMapResult = new HashMap<>();
      toMapResult.put("timerId", timerId);
      return toMapResult;
    }
    static OpenRequest fromMap(HashMap map) {
      OpenRequest fromMapResult = new OpenRequest();
      Object timerId = map.get("timerId");
      fromMapResult.timerId = (String)timerId;
      return fromMapResult;
    }
  }

  /** Generated interface from Pigeon that represents a handler of messages from Flutter.*/
  public interface HostTimerApi {
    void init(InitializationRequest arg);
    NotificationResponse showRunningNotification(TimeNotificationRequest arg);
    NotificationResponse showPausingNotification(TimeNotificationRequest arg);
    NotificationResponse showElapsedNotification(NotificationRequest arg);
    CancelResponse cancelTimer(CancelRequest arg);

    /** Sets up an instance of `HostTimerApi` to handle messages through the `binaryMessenger` */
    static void setup(BinaryMessenger binaryMessenger, HostTimerApi api) {
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.HostTimerApi.init", new StandardMessageCodec());
        if (api != null) {
          channel.setMessageHandler((message, reply) -> {
            HashMap<String, HashMap> wrapped = new HashMap<>();
            try {
              @SuppressWarnings("ConstantConditions")
              InitializationRequest input = InitializationRequest.fromMap((HashMap)message);
              api.init(input);
              wrapped.put("result", null);
            }
            catch (Exception exception) {
              wrapped.put("error", wrapError(exception));
            }
            reply.reply(wrapped);
          });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.HostTimerApi.showRunningNotification", new StandardMessageCodec());
        if (api != null) {
          channel.setMessageHandler((message, reply) -> {
            HashMap<String, HashMap> wrapped = new HashMap<>();
            try {
              @SuppressWarnings("ConstantConditions")
              TimeNotificationRequest input = TimeNotificationRequest.fromMap((HashMap)message);
              NotificationResponse output = api.showRunningNotification(input);
              wrapped.put("result", output.toMap());
            }
            catch (Exception exception) {
              wrapped.put("error", wrapError(exception));
            }
            reply.reply(wrapped);
          });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.HostTimerApi.showPausingNotification", new StandardMessageCodec());
        if (api != null) {
          channel.setMessageHandler((message, reply) -> {
            HashMap<String, HashMap> wrapped = new HashMap<>();
            try {
              @SuppressWarnings("ConstantConditions")
              TimeNotificationRequest input = TimeNotificationRequest.fromMap((HashMap)message);
              NotificationResponse output = api.showPausingNotification(input);
              wrapped.put("result", output.toMap());
            }
            catch (Exception exception) {
              wrapped.put("error", wrapError(exception));
            }
            reply.reply(wrapped);
          });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.HostTimerApi.showElapsedNotification", new StandardMessageCodec());
        if (api != null) {
          channel.setMessageHandler((message, reply) -> {
            HashMap<String, HashMap> wrapped = new HashMap<>();
            try {
              @SuppressWarnings("ConstantConditions")
              NotificationRequest input = NotificationRequest.fromMap((HashMap)message);
              NotificationResponse output = api.showElapsedNotification(input);
              wrapped.put("result", output.toMap());
            }
            catch (Exception exception) {
              wrapped.put("error", wrapError(exception));
            }
            reply.reply(wrapped);
          });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.HostTimerApi.cancelTimer", new StandardMessageCodec());
        if (api != null) {
          channel.setMessageHandler((message, reply) -> {
            HashMap<String, HashMap> wrapped = new HashMap<>();
            try {
              @SuppressWarnings("ConstantConditions")
              CancelRequest input = CancelRequest.fromMap((HashMap)message);
              CancelResponse output = api.cancelTimer(input);
              wrapped.put("result", output.toMap());
            }
            catch (Exception exception) {
              wrapped.put("error", wrapError(exception));
            }
            reply.reply(wrapped);
          });
        } else {
          channel.setMessageHandler(null);
        }
      }
    }
  }

  /** Generated class from Pigeon that represents Flutter messages that can be called from Java.*/
  public static class FlutterTimerApi {
    private final BinaryMessenger binaryMessenger;
    public FlutterTimerApi(BinaryMessenger argBinaryMessenger){
      this.binaryMessenger = argBinaryMessenger;
    }
    public interface Reply<T> {
      void reply(T reply);
    }
    public void onExtendTime(ExtendTimeResponse argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onExtendTime", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
    public void onContinueRequest(TimerRequest argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onContinueRequest", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
    public void onPauseRequest(TimerRequest argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onPauseRequest", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
    public void onCancelRequest(TimerRequest argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onCancelRequest", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
    public void onRestartRequest(TimerRequest argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onRestartRequest", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
    public void onOpen(OpenRequest argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onOpen", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
    public void onAlarm(TimerRequest argInput, Reply<Void> callback) {
      BasicMessageChannel<Object> channel =
          new BasicMessageChannel<>(binaryMessenger, "dev.flutter.pigeon.FlutterTimerApi.onAlarm", new StandardMessageCodec());
      HashMap inputMap = argInput.toMap();
      channel.send(inputMap, channelReply -> {
        callback.reply(null);
      });
    }
  }
  private static HashMap wrapError(Exception exception) {
    HashMap<String, Object> errorMap = new HashMap<>();
    errorMap.put("message", exception.toString());
    errorMap.put("code", exception.getClass().getSimpleName());
    errorMap.put("details", null);
    return errorMap;
  }
}
