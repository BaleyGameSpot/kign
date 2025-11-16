package com.paygate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.act.PaygateAfricaPaymentActivity;
import com.general.files.GeneralFunctions;
import com.utils.Logger;

import org.json.JSONObject;

/**
 * PayGate Africa Helper
 * Utility class to easily integrate PayGate Africa payments into your app
 *
 * Usage Example:
 * <pre>
 * PaygateAfricaHelper helper = new PaygateAfricaHelper(context, generalFunc);
 * helper.initiatePayment(activity, "100.00", "XOF", "ORD-12345", requestCode);
 * </pre>
 */
public class PaygateAfricaHelper {

    private static final String TAG = "PaygateAfricaHelper";
    public static final int PAYGATE_AFRICA_REQUEST_CODE = 9001;

    private Context context;
    private GeneralFunctions generalFunc;

    public PaygateAfricaHelper(Context context, GeneralFunctions generalFunc) {
        this.context = context;
        this.generalFunc = generalFunc;
    }

    /**
     * Check if PayGate Africa is enabled and configured
     *
     * @return true if PayGate Africa is enabled and configured
     */
    public boolean isPaygateAfricaEnabled() {
        try {
            JSONObject userProfile = generalFunc.getUserProfileJson();
            if (userProfile != null) {
                String enabled = generalFunc.getJsonValueStr("ENABLE_PAYGATE_AFRICA", userProfile);
                String appId = generalFunc.getJsonValueStr("PAYGATE_AFRICA_APP_ID", userProfile);

                return "Yes".equalsIgnoreCase(enabled) &&
                       appId != null && !appId.isEmpty();
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error checking PayGate Africa status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Initiate a PayGate Africa payment
     *
     * @param activity Activity to start payment from
     * @param amount Payment amount (e.g., "100.00")
     * @param currency Currency code (e.g., "XOF", "USD", "EUR")
     * @param orderRef Order reference/ID
     * @param requestCode Request code for onActivityResult
     */
    public void initiatePayment(Activity activity, String amount, String currency,
                               String orderRef, int requestCode) {
        initiatePayment(activity, amount, currency, orderRef, "card", requestCode);
    }

    /**
     * Initiate a PayGate Africa payment with specific payment option
     *
     * @param activity Activity to start payment from
     * @param amount Payment amount (e.g., "100.00")
     * @param currency Currency code (e.g., "XOF", "USD", "EUR")
     * @param orderRef Order reference/ID
     * @param paymentOptions Payment option: "card", "mobile_money", "wallet"
     * @param requestCode Request code for onActivityResult
     */
    public void initiatePayment(Activity activity, String amount, String currency,
                               String orderRef, String paymentOptions, int requestCode) {
        if (!isPaygateAfricaEnabled()) {
            Logger.e(TAG, "PayGate Africa is not enabled or configured");
            if (generalFunc != null) {
                generalFunc.showGeneralMessage("",
                    "PayGate Africa is not configured. Please contact support.");
            }
            return;
        }

        try {
            Intent intent = new Intent(context, PaygateAfricaPaymentActivity.class);
            intent.putExtra("amount", amount);
            intent.putExtra("currency", currency);
            intent.putExtra("orderRef", orderRef);
            intent.putExtra("paymentOptions", paymentOptions);
            intent.putExtra("handleResponse", true);

            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Logger.e(TAG, "Error initiating PayGate Africa payment: " + e.getMessage());
            if (generalFunc != null) {
                generalFunc.showGeneralMessage("",
                    "Error initiating payment: " + e.getMessage());
            }
        }
    }

    /**
     * Initiate payment with bundle parameters
     *
     * @param activity Activity to start payment from
     * @param bundle Bundle containing payment parameters
     * @param requestCode Request code for onActivityResult
     */
    public void initiatePaymentWithBundle(Activity activity, Bundle bundle, int requestCode) {
        if (!isPaygateAfricaEnabled()) {
            Logger.e(TAG, "PayGate Africa is not enabled or configured");
            return;
        }

        try {
            Intent intent = new Intent(context, PaygateAfricaPaymentActivity.class);
            intent.putExtras(bundle);

            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Logger.e(TAG, "Error initiating PayGate Africa payment: " + e.getMessage());
        }
    }

    /**
     * Handle payment result in onActivityResult
     *
     * Usage in your activity:
     * <pre>
     * @Override
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *     if (requestCode == PaygateAfricaHelper.PAYGATE_AFRICA_REQUEST_CODE) {
     *         PaymentResult result = PaygateAfricaHelper.handlePaymentResult(resultCode, data);
     *         if (result.isSuccess()) {
     *             // Payment successful
     *             String transactionId = result.getTransactionId();
     *         } else {
     *             // Payment failed
     *             String reason = result.getFailureReason();
     *         }
     *     }
     * }
     * </pre>
     *
     * @param resultCode Result code from onActivityResult
     * @param data Intent data from onActivityResult
     * @return PaymentResult object containing payment details
     */
    public static PaymentResult handlePaymentResult(int resultCode, Intent data) {
        PaymentResult result = new PaymentResult();

        if (resultCode == Activity.RESULT_OK && data != null) {
            result.success = true;
            result.transactionId = data.getStringExtra("transaction_id");
            result.orderRef = data.getStringExtra("order_ref");
            result.paymentStatus = data.getStringExtra("payment_status");
        } else if (data != null) {
            result.success = false;
            result.transactionId = data.getStringExtra("transaction_id");
            result.orderRef = data.getStringExtra("order_ref");
            result.paymentStatus = data.getStringExtra("payment_status");
            result.failureReason = data.getStringExtra("failure_reason");
        } else {
            result.success = false;
            result.failureReason = "Payment cancelled or failed";
        }

        return result;
    }

    /**
     * Get default currency from user profile
     */
    public String getDefaultCurrency() {
        try {
            JSONObject userProfile = generalFunc.getUserProfileJson();
            if (userProfile != null) {
                return generalFunc.getJsonValueStr("vCurrencyPassenger", userProfile);
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error getting default currency: " + e.getMessage());
        }
        return "USD"; // Default to USD
    }

    /**
     * Payment Result class
     */
    public static class PaymentResult {
        private boolean success;
        private String transactionId;
        private String orderRef;
        private String paymentStatus;
        private String failureReason;

        public boolean isSuccess() {
            return success;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getOrderRef() {
            return orderRef;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public String getFailureReason() {
            return failureReason;
        }

        @Override
        public String toString() {
            return "PaymentResult{" +
                    "success=" + success +
                    ", transactionId='" + transactionId + '\'' +
                    ", orderRef='" + orderRef + '\'' +
                    ", paymentStatus='" + paymentStatus + '\'' +
                    ", failureReason='" + failureReason + '\'' +
                    '}';
        }
    }
}
