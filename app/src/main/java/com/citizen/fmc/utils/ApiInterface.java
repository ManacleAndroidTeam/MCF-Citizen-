package com.citizen.fmc.utils;

import com.citizen.fmc.model.CheckAccountResponse.CheckAccountResponse;
import com.citizen.fmc.model.ForgotPasswordResponse.ForgotPasswordResponse;
import com.citizen.fmc.model.GenerateOtpResponse.GenerateOtpResponse;
import com.citizen.fmc.model.NearByTreeListModel;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * ======> Created by dheeraj-gangwar on 2017-12-30 <======
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST(Constants.REGISTER_URL)
    Call<JsonObject> registerUser(@Header("deviceId") String deviceId,
                                  @Header("versionName") String versionName,
                                  @Header("fcmToken") String fcmToken,
                                  @Header("username") String userName,
                                  @Header("password") String password,
                                  @Field("first_name") String firstName,
                                  @Field("last_name") String lastName,
                                  @Field("email") String email,
                                  @Field("mobile_no") String mobileNumber,
                                  @Field("gender") int genderId
    );


    @FormUrlEncoded
    @POST(Constants.LOGIN_URL)
    Call<JsonObject> loginUser(@Header("username") String email,
                               @Header("password") String password,
                               @Header("deviceId") String deviceId,
                               @Header("versionName") String versionName,
                               @Header("fcmToken") String fcmToken,
                               @Field("loginType") int loginType,
                               @Field("first_name") String firstName,
                               @Field("last_name") String lastName,
                               @Field("gender") int gender,
                               @Field("mobile_no") String mobileNum,
                               @Field("image_source") int imageSource,
                               @Field("media_type") int socialMediaTypeId,
                               @Field("social_id") String socialAccountId,
                               @Field("device_type") String ostype,
                               @Field("device_name") String deviceBrand,
                               @Field("device_model") String deviceModal,
                               @Field("os_version") String deviceOsName);


    @FormUrlEncoded
    @POST("notification-list")
    Call<JsonObject> getAllNotifications(@Field("app_type") int appType,
                                         @Field("user_id") int userId,
                                         @Field("version_name") String currentVersion,
                                         @Field("device_type") int deviceType,
                                         @Header("Authorization") String userAuthorization,
                                         @Header("Accept") String userAccept);


    @POST(Constants.COMPLAINT_CATEGORIES_URL)
    Call<JsonObject> getComplaintCategories(@Header("Authorization") String token,
                                            @Header("Accept") String accept);
    @POST(Constants.COMPLAINT_CATEGORIES_AIR_POLLUTION_URL)
    Call<JsonObject> getComplaintAirPollution(@Header("Authorization") String token,
                                            @Header("Accept") String accept);
    @FormUrlEncoded
    @POST(Constants.CITIZEN_COMPLAINT_COUNT_URL)
    Call<JsonObject> getComplaintCount(@Header("Authorization") String token,
                                            @Header("Accept") String accept,
                                       @Field("user_id")int civilian_id);

    @FormUrlEncoded
    @POST(Constants.CITIZEN_COMPLAINT_CAMERA_TYPE_URL)
    Call<JsonObject> getCameraType(@Header("Authorization") String token,
                                       @Header("Accept") String accept,
                                       @Field("user_id")int civilian_id);

    @FormUrlEncoded
    @POST(Constants.CITIZEN_DETAILS_URL)
    Call<JsonObject> getCitizenDetails(@Header("Authorization") String token,
                                       @Header("Accept") String accept,
                                       @Field("user_id")int civilian_id);

    @FormUrlEncoded
    @POST(Constants.UPVOTESCOMPLAINTS)
    Call<JsonObject> getupVotesComplaints(@Header("Authorization") String token,
                                          @Header("Accept") String accept,
                                          @Field("complaint_no") String complaint_no,
                                          @Field("civilian_id") int civilian_id);

    @FormUrlEncoded
    @POST(Constants.GET_MY_COMPLAINTS_URL)
    Call<JsonObject> getAllComplaints(@Header("Authorization") String token,
                                          @Header("Accept") String accept,
                                      @Header("CivilianId") int civilianId,
                                      @Field("air_pollution_status") String status,
                                      @Field("dept_id") String dept_id);
    @FormUrlEncoded
    @POST(Constants.GET_CIVIL_USER_FEEDBACK_URL)
    Call<JsonObject> getStatus(@Field("user_id") int civilianId);


    @FormUrlEncoded
    @POST(Constants.GET_ALL_COMPLAINTS_URL)
    Call<JsonObject> getAllComplaintsCivilian(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Header("CivilianId") int civilianId,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("complaint_status") int complaint_status,
            @Field("page") int page);

    @FormUrlEncoded
    @POST("return_lat_lng_epc_code")
    Call<JsonObject> getLatlngEPCCode(
            @Field("epc_code") String epc_code
    );


    @FormUrlEncoded
    @POST(Constants.GET_PAGINATED_COMPLAINTS_URL)
    Call<JsonObject> getPaginatedComplaints(@Field("filter") String filterJsonObject,
                                            @Field("page") int pageNum,
                                            @Header("Authorization") String token,
                                            @Header("Accept") String accept,
                                            @Header("CivilianId") int civilianId);

    @GET(Constants.CURRENT_WEATHER)
    Call<JsonObject> getWheather(@Query("lat") double lat,
                                 @Query("lon") double lng,
                                 @Query("appid") String app_id);


    @Multipart
    @POST(Constants.SUBMIT_COMPLAINT_URL)
    Call<JsonObject> submitComplaint(@Part("remarks") RequestBody compDesc,
                                     @Part("lat") RequestBody latitude,
                                     @Part("lng") RequestBody longitude,
                                     @Part("dept_id") RequestBody deptId,
                                     @Part("complaint_type_id") RequestBody subDeptId,
                                     @Part("data_unique_id") RequestBody uniqueId,
                                     @Part("geo_address") RequestBody geoAdd,
                                     @Part("complaint_address") RequestBody compAdd,
                                     @Part("complaint_source") RequestBody appType,
                                     @Header("complainedBy") String civilianId,
                                     @Part MultipartBody.Part image,
                                     @Part("landmark") RequestBody landmark,
                                     @Part("air_pollution_status") RequestBody air_pollution_status);

    @Multipart
    @POST("push_notification")
    Call<JsonObject> submitMessageForNotificaton(@Part("epc_code") RequestBody epc_code,
                                   @Part("order_id") RequestBody order_id,
                                   @Part("mobile_no") RequestBody mobile_no,
                                   @Part("status") RequestBody status


                                                 )
            ;


    @Multipart
    @POST("submit_wastage")
    Call<JsonObject> submitWastage(@Part("complainedBy") RequestBody civilianId,
                                   @Part("epc_code") RequestBody epc_code,
                                   @Part("app_type") RequestBody appType,
                                   @Part("status") RequestBody status,
    @Part("remarks") RequestBody remarks,
                                   @Part("complaint_id") RequestBody complaint_id,

                                   @Part("ratting") RequestBody ratting,
                                   @Part("feedback_type") RequestBody feedback_type,

    @Part MultipartBody.Part image)

    ;


    @FormUrlEncoded
    @POST("ComplaintCheckInOutStatus")
    Call<JsonObject> complaintInOrOut(@Field("lat") double lat,
                                       @Field("lng") double lng,
                                       @Field("user_id") int user_id,
                                       @Header("Authorization") String userAuthorization,
                                       @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST("getInOutStatus")
    Call<JsonObject> attendanceInOrOut(@Field("lat") double lat,
                                       @Field("lng") double lng,
                                       @Field("user_id") int user_id,
                                       @Header("Authorization") String userAuthorization,
                                       @Header("Accept") String userAccept);
    @Multipart
    @POST(Constants.RE_OPEN_COMPLAINT)
    Call<JsonObject> getReOpenComplaint(
            @Part("remarks") RequestBody remark,
            @Part("civilian_id") RequestBody civilian_id,
            @Part("complaint_no") RequestBody complaint_no,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("geo_address") RequestBody geo_address,
            @Part MultipartBody.Part image);


    @POST(Constants.GET_DRAWER_MODULES_URL)
    Call<JsonObject> getDrawerModules(@Header("app_type") int appType,@Header("civil_id" ) int civil_id
                                      );


    @POST(Constants.GET_HELP_LINE_NUMBERS_URL)
    Call<JsonObject> getHelpLineNumbers(@Header("Authorization") String token,
                                        @Header("Accept") String accept,
                                        @Header("CivilianId") int civilianId);

    @FormUrlEncoded
    @POST(Constants.GET_TREE_DATA)
    Call<List<NearByTreeListModel>> getTreeData(@Field("latitude") double lat,
                                                @Field("longitude") double lng,
                                                @Field("creds") String creds,
                                                @Field("projectid") String projectid,
                                                @Header("token") String token,
                                                @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST(Constants.GET_FAQs_URL)
    Call<JsonObject> getFrequentlyAskedQuestions(@Field("user_id") int userId,
                                                 @Field("app_type") int appType,
                                                 @Header("Authorization") String userAuthorization,
                                                 @Header("Accept") String userAccept);


    @Multipart
    @POST(Constants.SUBMIT_FEEDBACK_URL)
    Call<JsonObject> sendFeedbackData(@Part MultipartBody.Part image,
                                      @Part("user_id") RequestBody userId,
                                      @Part("app_type") RequestBody app_type,
                                      @Part("feedback") RequestBody feedback,
                                      @Part("data_unique_id") RequestBody data_unique_id,
                                      @Header("Authorization") String userAuthorization,
                                      @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST(Constants.GET_WHATS_NEAR_ME_MODULES_NEW)
    Call<JsonObject> getWhatsNearMeModules(@Field("sub_module_id") int subModuleId,
                                           @Header("CivilianId") int userId,
                                           @Header("Authorization") String userAuthorization,
                                           @Header("Accept") String userAccept,
                                           @Field("page") int nextPage);


    @Multipart
    @POST(Constants.UPDATE_USER_PROFILE_URL)
    Call<JsonObject> updateUserProfile(@Header("CivilianId") int userId,
                                       @Header("Authorization") String userAuthorization,
                                       @Header("Accept") String userAccept,
                                       @Part("first_name") RequestBody firstName,
                                       @Part("last_name") RequestBody lastName,
                                       @Part("mobile_no") RequestBody mobileNum,
                                       @Part("alter_mobile_no") RequestBody alternateMobNum,
                                       @Part("email") RequestBody email,
                                       @Part("gender") RequestBody genderId,
                                       @Part("dob") RequestBody dob,
                                       @Part("image_status") RequestBody imageStatus,
                                       @Part MultipartBody.Part imageFile);

    @Multipart
    @POST(Constants.UPDATE_USER_MOBILE_URL)
    Call<JsonObject> updateUserMobile(@Header("CivilianId") int userId,
                                       @Header("Authorization") String userAuthorization,
                                       @Header("Accept") String userAccept,
                                       @Part("mobile_no") RequestBody mobileNum
                                      );


    @FormUrlEncoded
    @POST(Constants.GET_WHO_SERVE_YOU_DATA)
    Call<JsonObject> getWhoServeYouData(@Field("dept_id") int subModuleId,
                                        @Header("CivilianId") int userId,
                                        @Header("Authorization") String userAuthorization,
                                        @Header("Accept") String userAccept);


    @POST(Constants.GET_COMPLAINT_HISTORY)
    Call<JsonObject> getComplaintHistory(@Header("complaint_no") String complaintNum,
                                         @Header("CivilianId") int userId,
                                         @Header("Authorization") String userAuthorization,
                                         @Header("Accept") String userAccept);


    @Multipart
    @POST(Constants.ADD_COMPLAINT_COMMENT_URL)
    Call<JsonObject> addComplaintComment(@Part MultipartBody.Part imageFile,
                                         @Part("lat") RequestBody latitude,
                                         @Part("lng") RequestBody longitude,
                                         @Part("geo_address") RequestBody geoAddress,
                                         @Part("comment") RequestBody comment,
                                         @Header("CivilianId") int userId,
                                         @Header("complaintNo") String complaintNum,
                                         @Header("Authorization") String userAuthorization,
                                         @Header("Accept") String userAccept);


    @POST(Constants.GET_COMPLAINT_COMMENT)
    Call<JsonObject> getComplaintComments(@Header("CivilianId") int userId,
                                          @Header("complaintNo") String complaintNum,
                                          @Header("Authorization") String userAuthorization,
                                          @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST(Constants.CHANGE_PASSWORD_URL)
    Call<JsonObject> changePassword(@Header("CivilianId") int userId,
                                    @Header("Authorization") String userAuthorization,
                                    @Header("Accept") String userAccept,
                                    @Field("old_pass") String oldPassword,
                                    @Field("new_pass") String newPassword);

    @FormUrlEncoded
    @POST("notification-list")
    Call<JsonObject> getAllNotifications(@Field("app_type") int appType,
                                         @Field("user_id") int userId,
                                         @Header("Authorization") String userAuthorization,
                                         @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST("appDownloadLogSubmit")
    Call<JsonObject> getDownloadCount(@Field("device_id") String device_id,
                                      @Field("app_type") String app_type,
                                      @Field("version_name") String version_name);


    @FormUrlEncoded
    @POST("markRead")
    Call<JsonObject> updateNotificationReadStatus(@Field("notification_id") int notificationId,
                                                  @Header("Authorization") String userAuthorization,
                                                  @Header("Accept") String userAccept);


    @POST("data")
    Call<JsonObject> getParkingSummery(@Body JSONObject jsonObject,
                                       @Header("Content-Type") String contenttype);


    @FormUrlEncoded
    @POST("checkAccount")
    Call<CheckAccountResponse> checkAccount(@Field("app_type") int app_type,
                                            @Field("os_type") int os_type,
                                            @Header("username") String username,
                                            @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST("generateOTP")
    Call<GenerateOtpResponse> generateOtp(@Field("app_type") int app_type,
                                          @Field("os_type") int os_type,
                                          @Header("email_id") String email_id,
                                          @Header("mobile_no") String mobile_no,
                                          @Header("user_id") int user_id,
                                          @Header("Accept") String userAccept);

    @POST("verifyOTP")
    Call<JsonObject> verifiedOTP(@Header("app_type") String appType,
                                 @Header("user_id") String userId,
                                 @Header("otp") String otp,
                                 @Header("mobile_no") String mobileNo,
                                 @Header("email_id") String emailId,
                                 @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST("forgotPassword")
    Call<ForgotPasswordResponse> resetPassword(@Field("app_type") int app_type,
                                               @Header("password") String password,
                                               @Header("user_id") String user_id,
                                               @Header("Accept") String userAccept);

    @FormUrlEncoded
    @POST("AddDepartmentFeedback")
    Call<JsonObject> addFeedbackDepartmentWise(@Field("user_id") int userId,
                                               @Field("department_id") int department_id,
                                               @Field("app_type") int apptype,
                                               @Field("feedback") String feedback,
                                               @Field("data_unique_id") String data_unique_id,
                                               @Header("Authorization") String userAuthorization,
                                               @Header("Accept") String userAccept);


    @FormUrlEncoded
    @POST("complaintFeedback")
    Call<JsonObject> addFeedbackOption(@Field("civilian_id") int civilian_id,
                                               @Field("complaint_no") String complaint_no,
                                               @Field("option_id") int option_id,
                                               @Field("remarks") String remarks,
                                               @Header("Authorization") String userAuthorization,
                                               @Header("Accept") String userAccept);

}