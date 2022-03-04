package com.vkontakte.miracle.login;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthState {

    public final static int STATE_NONE = -1;
    public final static int STATE_SUCCESS = 0;
    public final static int STATE_NEED_SMS_VALIDATION = 1;
    public final static int STATE_NEED_APP_VALIDATION = 2;
    public final static int STATE_NEED_VALIDATION = 3;
    public final static int STATE_NEED_LIBVERIFY_VALIDATION = 4;
    public final static int STATE_SMS_CODE_HAS_ALREADY_BEEN_RESENT = 5;
    public final static int STATE_SMS_CODE_RESENDS_LIMIT = 6;
    public final static int STATE_NEED_CAPTCHA = 7;
    public final static int STATE_HAS_ERROR = 8;

    private final static String VALIDATION_TYPE_SMS = "2fa_sms";
    private final static String VALIDATION_TYPE_APP = "2fa_app";
    private final static String VALIDATION_TYPE_LIBVERIFY = "2fa_libverify";

    private int state = STATE_NONE;
    private long responseTime;

    private String username;
    private String password;
    private String receipt;
    private String token;
    private String userId;

    private String validationSid;
    private String phoneMask;
    private String validationType;
    private String validationCode;

    private String captchaSid;
    private String captchaImg;
    private String captchaKey;

    private int delay = 0;


    public static AuthState fromFields(String login, String pass, String receipt){
        AuthState authState = new AuthState();
        authState.username = login;
        authState.password = pass;
        authState.receipt = receipt;
        return authState;
    }

    public static AuthState fromAccount(String token, String userId, String receipt){
        AuthState authState = new AuthState();
        authState.token = token;
        authState.userId = userId;
        authState.receipt = receipt;
        return authState;
    }

    public void updateValidation(JSONObject jsonObject) throws JSONException {
        validationSid = jsonObject.getString("validation_sid");
        phoneMask = jsonObject.getString("phone_mask");
        validationType = jsonObject.getString("validation_type");
        updateValidationState(validationType);
    }

    public void updatePhoneValidation(JSONObject jsonObject) throws JSONException {
        responseTime = System.currentTimeMillis();
        delay = jsonObject.getInt("delay");
        validationType = "2fa_"+jsonObject.getString("validation_type");
        updateValidationState(validationType);
    }

    public void updateValidationState(String validationType){
        switch (validationType){
            case VALIDATION_TYPE_SMS:{
                state = STATE_NEED_SMS_VALIDATION;
                break;
            }
            case VALIDATION_TYPE_APP:{
                state = STATE_NEED_APP_VALIDATION;
                break;
            }
            case VALIDATION_TYPE_LIBVERIFY:{
                state = STATE_NEED_LIBVERIFY_VALIDATION;
                break;
            }
            default:{
                state = STATE_NEED_VALIDATION;
                break;
            }
        }
    }

    public void updateFakePhoneValidationAlready(){
        updateFakePhoneValidation();
        delay = 120;
    }

    public void updateFakePhoneValidation(){
        validationType = VALIDATION_TYPE_SMS;
    }

    public void updateCaptcha(JSONObject jsonObject) throws JSONException {
        state = STATE_NEED_CAPTCHA;
        captchaSid = jsonObject.getString("captcha_sid");
        captchaImg = jsonObject.getString("captcha_img");
    }


    public int getState() {
        return state;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getValidationSid() {
        return validationSid;
    }

    public String getPhoneMask() {
        return phoneMask;
    }

    public String getCaptchaSid() {
        return captchaSid;
    }

    public String getCaptchaImg() {
        return captchaImg;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public int getDelay() {
        return delay;
    }


    public void setState(int state) {
        this.state = state;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

}
