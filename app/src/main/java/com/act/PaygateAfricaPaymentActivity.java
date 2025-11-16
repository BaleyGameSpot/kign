package com.act;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.general.files.GeneralFunctions;
import com.opleq.user.R;
import com.paygate.PaygateAfricaService;
import com.utils.Logger;
import com.view.ErrorView;
import com.view.WKWebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * PayGate Africa Payment Activity
 * Handles payment processing using PayGate Africa payment gateway
 *
 * This activity integrates with PayGate Africa API to:
 * 1. Authenticate with the payment gateway
 * 2. Create a payment transaction
 * 3. Display payment page in WebView (if payment URL is provided)
 * 4. Handle payment callbacks and return results
 */
@SuppressLint("SetJavaScriptEnabled")
public class PaygateAfricaPaymentActivity extends ParentActivity implements ErrorView.RetryListener {

    private static final String TAG = "PaygateAfricaPayment";

    private LottieAnimationView loaderView;
    private WKWebView paymentWebview;
    private ImageView cancelImg;
    private ErrorView errorView;

    private PaygateAfricaService paygateService;
    private String transactionId;
    private String paymentUrl;
    private boolean handleResponse = false;

    // Payment details
    private String amount;
    private String currency;
    private String orderRef;
    private String paymentOptions = "card"; // Default to card, can be: card, mobile_money, wallet

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        // Initialize views
        initViews();

        // Get payment details from intent
        getPaymentDetails();

        // Initialize PayGate Africa service
        paygateService = new PaygateAfricaService(getActContext(), generalFunc);

        // Show loader
        showLoader();

        // Start payment process
        initiatePayment();
    }

    private void initViews() {
        paymentWebview = findViewById(R.id.paymentWebview);
        loaderView = findViewById(R.id.loaderView);
        cancelImg = findViewById(R.id.cancelImg);
        errorView = findViewById(R.id.errorView);

        cancelImg.setOnClickListener(v -> onCancelPayment());

        paymentWebview.setWebViewClient(new PaymentWebClient());
        paymentWebview.getSettings().setDomStorageEnabled(true);
        paymentWebview.setWebChromeClient(new MyWebChromeClient());

        // Clear all data for security
        clearWebViewData();

        errorView.setVisibility(View.GONE);
        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");
        errorView.setOnRetryListener(this);
    }

    private void getPaymentDetails() {
        Intent intent = getIntent();
        if (intent != null) {
            amount = intent.getStringExtra("amount");
            currency = intent.getStringExtra("currency");
            orderRef = intent.getStringExtra("orderRef");
            paymentOptions = intent.getStringExtra("paymentOptions");
            handleResponse = intent.getBooleanExtra("handleResponse", true);

            if (paymentOptions == null || paymentOptions.isEmpty()) {
                paymentOptions = "card";
            }
        }
    }

    private void clearWebViewData() {
        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        paymentWebview.clearCache(true);
        paymentWebview.clearFormData();
        paymentWebview.clearHistory();
        paymentWebview.clearSslPreferences();
    }

    private void initiatePayment() {
        // Step 1: Authenticate with PayGate Africa
        paygateService.authenticate(new PaygateAfricaService.PaygateCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                // Step 2: Create transaction
                createTransaction();
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    hideLoader();
                    showError("Authentication failed: " + errorMessage);
                });
            }
        });
    }

    private void createTransaction() {
        try {
            // Create cart items
            HashMap<String, String> orderData = new HashMap<>();
            orderData.put("product_name", "Order Payment");
            orderData.put("order_id", orderRef);
            orderData.put("quantity", "1");
            orderData.put("amount", amount);
            orderData.put("description", "Payment for order " + orderRef);

            JSONArray cartItems = PaygateAfricaService.createCartItems(orderData);

            // Create transaction
            paygateService.createTransaction(amount, currency, orderRef, paymentOptions, cartItems,
                    new PaygateAfricaService.PaygateCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            runOnUiThread(() -> handleTransactionResponse(response));
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                hideLoader();
                                showError("Transaction creation failed: " + errorMessage);
                            });
                        }
                    });
        } catch (Exception e) {
            Logger.e(TAG, "Error creating transaction: " + e.getMessage());
            hideLoader();
            showError("Error creating transaction: " + e.getMessage());
        }
    }

    private void handleTransactionResponse(JSONObject response) {
        try {
            Logger.d(TAG, "Transaction response: " + response.toString());

            // Extract transaction ID
            transactionId = response.optString("transaction_id");

            // Extract payment URL if available
            paymentUrl = response.optString("payment_url");

            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                // Load payment page in WebView
                loadPaymentPage(paymentUrl);
            } else {
                // If no payment URL, check if transaction is already completed
                String status = response.optString("status");
                if ("success".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
                    handlePaymentSuccess();
                } else {
                    showError("No payment URL provided. Please try again.");
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error handling transaction response: " + e.getMessage());
            showError("Error processing transaction: " + e.getMessage());
        }
    }

    private void loadPaymentPage(String url) {
        Logger.d(TAG, "Loading payment URL: " + url);
        paymentWebview.loadUrl(url);
        paymentWebview.setVisibility(View.VISIBLE);
        hideLoader();
        cancelImg.setVisibility(View.VISIBLE);
    }

    private void handlePaymentSuccess() {
        hideLoader();
        if (handleResponse) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("transaction_id", transactionId);
            returnIntent.putExtra("order_ref", orderRef);
            returnIntent.putExtra("payment_status", "success");
            setResult(Activity.RESULT_OK, returnIntent);
        }
        finish();
    }

    private void handlePaymentFailure(String reason) {
        hideLoader();
        if (handleResponse) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("transaction_id", transactionId);
            returnIntent.putExtra("order_ref", orderRef);
            returnIntent.putExtra("payment_status", "failed");
            returnIntent.putExtra("failure_reason", reason);
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }

    private void onCancelPayment() {
        generalFunc.showGeneralMessage("",
            generalFunc.retrieveLangLBl("", "LBL_CANCEL_PAYMENT_PROCESS"),
            generalFunc.retrieveLangLBl("", "LBL_NO"),
            generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                if (buttonId == 1) {
                    handlePaymentFailure("User cancelled payment");
                }
            });
    }

    private void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
        cancelImg.setVisibility(View.GONE);
    }

    private void hideLoader() {
        loaderView.setVisibility(View.GONE);
    }

    private void showError(String message) {
        generalFunc.showGeneralMessage("", message);
        errorView.setVisibility(View.VISIBLE);
        cancelImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRetry() {
        errorView.setVisibility(View.GONE);
        showLoader();
        initiatePayment();
    }

    @Override
    public void onBackPressed() {
        onCancelPayment();
    }

    private Context getActContext() {
        return PaygateAfricaPaymentActivity.this;
    }

    /**
     * WebView client for handling payment page
     */
    private class PaymentWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d(TAG, "URL Loading: " + url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.d(TAG, "Page started: " + url);
            loaderView.setVisibility(View.VISIBLE);

            // Check for success/failure indicators in URL
            if (url.contains("success=1") || url.contains("status=success") ||
                url.contains("payment_status=success")) {
                handlePaymentSuccess();
            } else if (url.contains("success=0") || url.contains("status=failed") ||
                      url.contains("payment_status=failed") || url.contains("status=cancelled")) {
                handlePaymentFailure("Payment failed or cancelled");
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Logger.d(TAG, "Page finished: " + url);
            loaderView.setVisibility(View.GONE);
            cancelImg.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Logger.e(TAG, "WebView error: " + error.toString());
            view.stopLoading();
            errorView.setVisibility(View.VISIBLE);
            cancelImg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * WebChromeClient for handling JavaScript dialogs
     */
    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            Logger.d(TAG, "JS Alert: " + message);
            generalFunc.showGeneralMessage("", message,
                generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TXT"),
                generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"), buttonId -> {
                    if (buttonId == 1) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                });
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            Logger.d(TAG, "JS Confirm: " + message);
            generalFunc.showGeneralMessage("", message,
                generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TXT"),
                generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"), buttonId -> {
                    if (buttonId == 1) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                });
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                 JsPromptResult result) {
            Logger.d(TAG, "JS Prompt: " + message);
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }
}
