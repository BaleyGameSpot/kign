package com.paygate;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.general.files.GeneralFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * PayGate Africa Payment Gateway Service
 * Handles API communication with PayGate Africa (api.paygate.africa)
 *
 * Integration Flow:
 * 1. Authenticate with PayGate Africa API
 * 2. Create payment transaction
 * 3. Get payment URL for WebView
 * 4. Handle payment status callbacks
 */
public class PaygateAfricaService {

    private static final String TAG = "PaygateAfricaService";
    private static final String BASE_URL = "https://api.paygate.africa";
    private static final String AUTH_ENDPOINT = "/auth/connect/application";
    private static final String TRANSACTIONS_ENDPOINT = "/transactions";

    private Context context;
    private GeneralFunctions generalFunc;
    private OkHttpClient client;

    // PayGate Africa credentials - should be configured from server
    private String appId;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private String accessToken;

    public PaygateAfricaService(Context context, GeneralFunctions generalFunc) {
        this.context = context;
        this.generalFunc = generalFunc;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Load credentials from user profile
        loadCredentials();
    }

    /**
     * Load PayGate Africa credentials from user profile
     */
    private void loadCredentials() {
        try {
            String userProfileJsonStr = generalFunc.retrieveValue(com.utils.Utils.USER_PROFILE_JSON);
            JSONObject userProfile = generalFunc.getJsonObject(userProfileJsonStr);
            if (userProfile != null) {
                appId = generalFunc.getJsonValueStr("PAYGATE_AFRICA_APP_ID", userProfile);
                clientId = generalFunc.getJsonValueStr("PAYGATE_AFRICA_CLIENT_ID", userProfile);
                clientSecret = generalFunc.getJsonValueStr("PAYGATE_AFRICA_CLIENT_SECRET", userProfile);
                refreshToken = generalFunc.getJsonValueStr("PAYGATE_AFRICA_REFRESH_TOKEN", userProfile);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading credentials: " + e.getMessage());
        }
    }

    /**
     * Authenticate with PayGate Africa API
     */
    public void authenticate(PaygateCallback callback) {
        if (!isCredentialsConfigured()) {
            callback.onError("PayGate Africa credentials not configured. Please contact support.");
            return;
        }

        try {
            // Create Basic Auth token
            String credentials = clientId + ":" + clientSecret;
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            Request request = new Request.Builder()
                    .url(BASE_URL + AUTH_ENDPOINT)
                    .addHeader("Authorization", basicAuth)
                    .addHeader("app_id", appId)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Authentication failed: " + e.getMessage());
                    callback.onError("Authentication failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            accessToken = jsonResponse.optString("access_token");
                            refreshToken = jsonResponse.optString("refresh_token");

                            Log.d(TAG, "Authentication successful");
                            callback.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse authentication response");
                        }
                    } else {
                        callback.onError("Authentication failed with code: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error during authentication: " + e.getMessage());
            callback.onError("Error during authentication: " + e.getMessage());
        }
    }

    /**
     * Create a payment transaction
     *
     * @param amount Payment amount
     * @param currency Currency code (e.g., "XOF", "USD")
     * @param orderRef Order reference
     * @param paymentOptions Payment type (e.g., "card", "mobile_money")
     * @param cartItems Cart items array
     * @param callback Callback for response
     */
    public void createTransaction(String amount, String currency, String orderRef,
                                 String paymentOptions, JSONArray cartItems,
                                 PaygateCallback callback) {
        if (!isAuthenticated()) {
            callback.onError("Not authenticated. Please authenticate first.");
            return;
        }

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("amount", amount);
            requestBody.put("currency", currency);
            requestBody.put("order_ref", orderRef);
            requestBody.put("payment_options", paymentOptions);
            requestBody.put("items", cartItems.length());
            requestBody.put("cart", cartItems);

            Log.d(TAG, "Creating transaction: " + requestBody.toString());

            Request request = new Request.Builder()
                    .url(BASE_URL + TRANSACTIONS_ENDPOINT)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("refresh_token", refreshToken)
                    .addHeader("app_id", appId)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Transaction creation failed: " + e.getMessage());
                    callback.onError("Transaction creation failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            Log.d(TAG, "Transaction created successfully");
                            callback.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse transaction response");
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "";
                        Log.e(TAG, "Transaction failed: " + response.code() + " - " + errorBody);
                        callback.onError("Transaction failed with code: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating transaction: " + e.getMessage());
            callback.onError("Error creating transaction: " + e.getMessage());
        }
    }

    /**
     * Check payment status
     *
     * @param transactionId Transaction ID
     * @param callback Callback for response
     */
    public void checkPaymentStatus(String transactionId, PaygateCallback callback) {
        if (!isAuthenticated()) {
            callback.onError("Not authenticated. Please authenticate first.");
            return;
        }

        try {
            String url = BASE_URL + TRANSACTIONS_ENDPOINT + "/" + transactionId + "/payment";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("refresh_token", refreshToken)
                    .addHeader("app_id", appId)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Payment status check failed: " + e.getMessage());
                    callback.onError("Payment status check failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            Log.d(TAG, "Payment status retrieved successfully");
                            callback.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse payment status response");
                        }
                    } else {
                        callback.onError("Payment status check failed with code: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error checking payment status: " + e.getMessage());
            callback.onError("Error checking payment status: " + e.getMessage());
        }
    }

    /**
     * Check if credentials are configured
     */
    private boolean isCredentialsConfigured() {
        return appId != null && !appId.isEmpty() &&
               clientId != null && !clientId.isEmpty() &&
               clientSecret != null && !clientSecret.isEmpty();
    }

    /**
     * Check if authenticated
     */
    private boolean isAuthenticated() {
        return accessToken != null && !accessToken.isEmpty() &&
               refreshToken != null && !refreshToken.isEmpty() &&
               appId != null && !appId.isEmpty();
    }

    /**
     * Create cart items JSON array from order data
     */
    public static JSONArray createCartItems(HashMap<String, String> orderData) {
        JSONArray cartItems = new JSONArray();
        try {
            JSONObject item = new JSONObject();
            item.put("product_name", orderData.getOrDefault("product_name", "Order"));
            item.put("product_code", orderData.getOrDefault("order_id", "ORD-" + System.currentTimeMillis()));
            item.put("quantity", orderData.getOrDefault("quantity", "1"));
            item.put("price", orderData.getOrDefault("amount", "0"));
            item.put("total", orderData.getOrDefault("amount", "0"));
            item.put("description", orderData.getOrDefault("description", "Payment for order"));
            cartItems.put(item);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating cart items: " + e.getMessage());
        }
        return cartItems;
    }

    /**
     * Callback interface for PayGate Africa API responses
     */
    public interface PaygateCallback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }
}
