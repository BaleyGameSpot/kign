# PayGate Africa Payment Gateway Integration

## Overview

This document describes the integration of PayGate Africa payment gateway into the KIGN application. PayGate Africa is an all-in-one payment REST API that supports major African payment providers, international bank cards, PayPal, and cryptocurrencies.

## Architecture

The PayGate Africa integration consists of three main components:

1. **PaygateAfricaService** (`com.paygate.PaygateAfricaService`)
   - Handles API communication with PayGate Africa
   - Manages authentication and transaction creation
   - Provides payment status checking

2. **PaygateAfricaPaymentActivity** (`com.act.PaygateAfricaPaymentActivity`)
   - UI activity for processing payments
   - Displays payment page in WebView when required
   - Handles payment callbacks and results

3. **PaygateAfricaHelper** (`com.paygate.PaygateAfricaHelper`)
   - Utility class for easy integration
   - Simplifies launching payments from any activity
   - Handles payment result processing

## Configuration

### Server-Side Configuration

The PayGate Africa gateway requires the following configuration parameters to be added to the user profile JSON response from the server:

```json
{
  "ENABLE_PAYGATE_AFRICA": "Yes",
  "PAYGATE_AFRICA_APP_ID": "your-app-id",
  "PAYGATE_AFRICA_CLIENT_ID": "your-client-id",
  "PAYGATE_AFRICA_CLIENT_SECRET": "your-client-secret",
  "PAYGATE_AFRICA_REFRESH_TOKEN": "your-refresh-token"
}
```

#### Getting PayGate Africa Credentials

1. Create an account at [https://paygate.africa](https://paygate.africa)
2. Complete the account verification process
3. Navigate to the API section in your dashboard
4. Generate or retrieve your API credentials:
   - **APP_ID**: Application identifier
   - **CLIENT_ID**: OAuth client ID
   - **CLIENT_SECRET**: OAuth client secret
   - **REFRESH_TOKEN**: Long-lived refresh token

5. Add these credentials to your backend system's user profile configuration

### Security Considerations

- **Never** hardcode credentials in the mobile app
- Always fetch credentials from the server via secure API
- Store credentials securely on the server
- Use HTTPS for all API communications
- Implement proper authentication and authorization on your backend

## Payment Flow

### 1. Authentication Flow

```
Mobile App
    ↓
1. User initiates payment
    ↓
2. PaygateAfricaService.authenticate()
    ↓
3. POST https://api.paygate.africa/auth/connect/application
   Headers:
   - Authorization: Basic base64(client_id:client_secret)
   - app_id: APP_ID
    ↓
4. Receive access_token and refresh_token
    ↓
5. Create transaction
```

### 2. Transaction Creation Flow

```
PaygateAfricaService
    ↓
1. createTransaction(amount, currency, orderRef, paymentOptions, cartItems)
    ↓
2. POST https://api.paygate.africa/transactions
   Headers:
   - Content-Type: application/json
   - refresh_token: REFRESH_TOKEN
   - app_id: APP_ID
   - Authorization: Bearer ACCESS_TOKEN
   Body:
   {
     "amount": "100.00",
     "currency": "XOF",
     "order_ref": "ORD-12345",
     "payment_options": "card",
     "items": 1,
     "cart": [...]
   }
    ↓
3. Receive transaction_id and payment_url
    ↓
4. Load payment_url in WebView (if provided)
    ↓
5. Handle payment callback
```

### 3. Payment Status Check Flow

```
PaygateAfricaService
    ↓
1. checkPaymentStatus(transactionId)
    ↓
2. GET https://api.paygate.africa/transactions/{transaction_id}/payment
   Headers:
   - refresh_token: REFRESH_TOKEN
   - app_id: APP_ID
   - Authorization: Bearer ACCESS_TOKEN
    ↓
3. Receive payment status
```

## Usage Examples

### Example 1: Simple Payment

```java
import com.paygate.PaygateAfricaHelper;

public class CheckOutActivity extends ParentActivity {

    private PaygateAfricaHelper paygateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize PayGate Africa helper
        paygateHelper = new PaygateAfricaHelper(this, generalFunc);

        // Check if PayGate Africa is enabled
        if (paygateHelper.isPaygateAfricaEnabled()) {
            // PayGate Africa is available as a payment option
        }
    }

    private void processPayment() {
        String amount = "100.00";
        String currency = paygateHelper.getDefaultCurrency(); // or "XOF", "USD", etc.
        String orderRef = "ORD-" + System.currentTimeMillis();

        // Launch PayGate Africa payment
        paygateHelper.initiatePayment(
            this,
            amount,
            currency,
            orderRef,
            PaygateAfricaHelper.PAYGATE_AFRICA_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PaygateAfricaHelper.PAYGATE_AFRICA_REQUEST_CODE) {
            PaygateAfricaHelper.PaymentResult result =
                PaygateAfricaHelper.handlePaymentResult(resultCode, data);

            if (result.isSuccess()) {
                // Payment successful
                String transactionId = result.getTransactionId();
                String orderRef = result.getOrderRef();

                generalFunc.showMessage(getCurrentView(),
                    "Payment successful! Transaction ID: " + transactionId);

                // Update your order status, etc.
            } else {
                // Payment failed
                String reason = result.getFailureReason();

                generalFunc.showMessage(getCurrentView(),
                    "Payment failed: " + reason);
            }
        }
    }
}
```

### Example 2: Payment with Specific Payment Option

```java
// Pay with mobile money
paygateHelper.initiatePayment(
    this,
    "100.00",
    "XOF",
    "ORD-12345",
    "mobile_money", // Payment option: "card", "mobile_money", "wallet"
    PaygateAfricaHelper.PAYGATE_AFRICA_REQUEST_CODE
);
```

### Example 3: Payment with Custom Bundle

```java
Bundle paymentBundle = new Bundle();
paymentBundle.putString("amount", "100.00");
paymentBundle.putString("currency", "XOF");
paymentBundle.putString("orderRef", "ORD-12345");
paymentBundle.putString("paymentOptions", "card");
paymentBundle.putBoolean("handleResponse", true);

paygateHelper.initiatePaymentWithBundle(
    this,
    paymentBundle,
    PaygateAfricaHelper.PAYGATE_AFRICA_REQUEST_CODE
);
```

## Supported Payment Methods

PayGate Africa supports multiple payment options:

- **card**: Credit/Debit card payments
- **mobile_money**: Mobile money (Orange Money, MTN Mobile Money, Moov Money, etc.)
- **wallet**: Digital wallet payments
- **bank_transfer**: Bank transfers (where supported)
- **crypto**: Cryptocurrency payments (where supported)

## Supported Currencies

PayGate Africa supports multiple African and international currencies:

### West African Currencies
- **XOF**: West African CFA Franc (Benin, Burkina Faso, Côte d'Ivoire, Guinea-Bissau, Mali, Niger, Senegal, Togo)
- **XAF**: Central African CFA Franc
- **NGN**: Nigerian Naira
- **GHS**: Ghanaian Cedi

### International Currencies
- **USD**: US Dollar
- **EUR**: Euro
- **GBP**: British Pound

### Mobile Money Integration
- Orange Money (XOF, XAF)
- MTN Mobile Money (XOF, GHS, NGN)
- Moov Money (XOF)
- Wave (XOF)

## Testing

### Test Credentials

For testing purposes, PayGate Africa provides a sandbox environment. Contact PayGate Africa support to obtain test credentials.

### Test Cards

PayGate Africa provides test card numbers for different scenarios:

- **Successful Payment**: Use test card numbers provided by PayGate Africa
- **Failed Payment**: Use specific test card numbers for failure scenarios
- **3D Secure**: Test cards with 3D Secure authentication

### Testing Checklist

- [ ] Test successful card payment
- [ ] Test failed card payment
- [ ] Test cancelled payment
- [ ] Test mobile money payment (if applicable)
- [ ] Test with different currencies
- [ ] Test with different amounts (small, large, decimal values)
- [ ] Test network failure scenarios
- [ ] Test payment timeout scenarios
- [ ] Verify transaction IDs are stored correctly
- [ ] Verify order status is updated correctly

## Error Handling

The integration includes comprehensive error handling:

### Authentication Errors
- Invalid credentials
- Expired tokens
- Network errors

### Transaction Errors
- Insufficient funds
- Invalid amount
- Invalid currency
- Transaction timeout

### Payment Errors
- Payment declined
- Payment cancelled by user
- Network errors during payment
- WebView errors

### Error Messages

All errors are displayed to the user via `GeneralFunctions.showGeneralMessage()` with appropriate error messages.

## API Endpoints

### Base URL
```
https://api.paygate.africa
```

### Endpoints

1. **Authentication**
   ```
   POST /auth/connect/application
   ```

2. **Create Transaction**
   ```
   POST /transactions
   ```

3. **Check Payment Status**
   ```
   GET /transactions/{transaction_id}/payment
   ```

## Files Modified/Created

### New Files
1. `/app/src/main/java/com/paygate/PaygateAfricaService.java` - Payment gateway service
2. `/app/src/main/java/com/act/PaygateAfricaPaymentActivity.java` - Payment activity
3. `/app/src/main/java/com/paygate/PaygateAfricaHelper.java` - Helper utility class

### Modified Files
1. `/app/src/main/AndroidManifest.xml` - Added PaygateAfricaPaymentActivity declaration

## Troubleshooting

### Issue: "PayGate Africa is not configured"

**Solution**: Ensure the server is providing the required configuration parameters in the user profile JSON:
- `ENABLE_PAYGATE_AFRICA`
- `PAYGATE_AFRICA_APP_ID`
- `PAYGATE_AFRICA_CLIENT_ID`
- `PAYGATE_AFRICA_CLIENT_SECRET`
- `PAYGATE_AFRICA_REFRESH_TOKEN`

### Issue: Authentication fails

**Solution**:
1. Verify credentials are correct
2. Check if credentials are not expired
3. Ensure network connectivity
4. Check PayGate Africa service status

### Issue: Transaction creation fails

**Solution**:
1. Verify authentication was successful
2. Check if amount and currency are valid
3. Ensure cart items are properly formatted
4. Check PayGate Africa API logs

### Issue: Payment page doesn't load

**Solution**:
1. Verify transaction was created successfully
2. Check if payment_url was returned
3. Ensure WebView has internet permission
4. Check for SSL certificate errors

## Security Best Practices

1. **Credentials**: Never hardcode credentials in the app
2. **HTTPS**: Always use HTTPS for API calls
3. **Validation**: Validate all payment amounts and currencies
4. **Logging**: Don't log sensitive payment information
5. **Error Messages**: Don't expose sensitive error details to users
6. **Token Management**: Implement token refresh logic
7. **Session Management**: Clear WebView data after each payment

## Support

For PayGate Africa integration support:

- **PayGate Africa Documentation**: [https://developers.paygate.africa](https://developers.paygate.africa)
- **PayGate Africa Support**: Contact PayGate Africa support team
- **API Issues**: Check API status and documentation

## License

This integration is part of the KIGN application and follows the same license terms.

## Changelog

### Version 1.0.0 (Initial Release)
- PayGate Africa service integration
- Payment activity with WebView support
- Helper utility for easy integration
- Comprehensive error handling
- Support for multiple payment methods
- Support for multiple currencies
- Documentation and usage examples
