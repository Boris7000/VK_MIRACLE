package com.vkontakte.miracle.login;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthState {

    public final static int STATE_NONE = 0;
    public final static int STATE_SUCCESS = 1;
    public final static int STATE_NEED_VALIDATION = 2;
    public final static int STATE_NEED_CAPTCHA = 3;
    public final static int STATE_HAS_ERROR = 4;


    public final static int VALIDATION_CODE_HAS_ALREADY_BEEN_RESENT = 0;
    public final static int VALIDATION_CODE_RESENDS_LIMIT = 1;


    public final static String VALIDATION_TYPE_SMS = "2fa_sms";
    public final static String VALIDATION_TYPE_APP = "2fa_app";
    public final static String VALIDATION_TYPE_CALL = "2fa_callreset";
    //public final static String VALIDATION_TYPE_LIBVERIFY = "2fa_libverify";

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
    private int forceCodeUnableReason = 0;


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

    public void resetValidation(){
        validationSid = null;
        phoneMask = null;
        validationType = null;
    }

    public void resetCaptcha(){
        captchaSid = null;
        captchaImg = null;
    }

    public void resetForceCode(){
        delay = 0;
        forceCodeUnableReason = 0;
    }


    public void updateValidation(JSONObject jsonObject) throws JSONException {
        resetCaptcha();
        resetForceCode();
        validationSid = jsonObject.getString("validation_sid");
        phoneMask = jsonObject.getString("phone_mask");
        validationType = jsonObject.getString("validation_type");
    }

    public void updateForceCode(JSONObject jsonObject) throws JSONException {
        validationType = "2fa_"+jsonObject.getString("validation_type");
        responseTime = System.currentTimeMillis();
        delay = jsonObject.getInt("delay");
    }

    public void updateCaptcha(JSONObject jsonObject) throws JSONException {
        resetValidation();
        resetForceCode();
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

    public String getValidationType() {
        return validationType;
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

    public int getForceCodeUnableReason() {
        return forceCodeUnableReason;
    }

    public void setForceCodeUnableReason(int forceCodeUnableReason) {
        this.forceCodeUnableReason = forceCodeUnableReason;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
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
