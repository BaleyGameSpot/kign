# 🚗 Vehicle Category Admin Panel - User Guide
## گاڑیوں کی کیٹگریز ایڈ کرنے کا آسان طریقہ

## 📌 Features / خصوصیات

- ✅ **Web-based Interface** - Browser میں کام کریں
- ✅ **131 Columns Support** - مکمل ڈیٹابیس سپورٹ
- ✅ **Multi-language** - 25+ زبانوں کی سپورٹ
- ✅ **Auto ID Generation** - آٹومیٹک ID بنتا ہے
- ✅ **Recent Categories** - آخری 20 کیٹگریز دیکھیں
- ✅ **Error Handling** - غلطیوں کی جانچ
- ✅ **Beautiful UI** - خوبصورت ڈیزائن

---

## 🔧 Setup Instructions / سیٹ اپ کی ہدایات

### Step 1: Database Configuration

`admin_add_category.php` فائل کھولیں اور lines 9-12 میں اپنی ڈیٹابیس کی معلومات ڈالیں:

```php
define('DB_HOST', 'localhost');        // Your database host
define('DB_USER', 'your_username');    // Your MySQL username
define('DB_PASS', 'your_password');    // Your MySQL password
define('DB_NAME', 'your_database');    // Your database name
```

### Step 2: Upload to Server

فائل کو اپنے سرور پر اپلوڈ کریں:
- FTP سے اپلوڈ کریں
- یا cPanel File Manager استعمال کریں
- Root directory میں رکھیں

### Step 3: Access the Panel

اپنے browser میں کھولیں:
```
http://yourdomain.com/admin_add_category.php
```

---

## 📝 How to Add Categories / کیٹگری کیسے ایڈ کریں

### Required Fields (لازمی فیلڈز):

1. **Category Name (English)** - انگلش میں نام
   - مثال: "Personal Driver", "Taxi Service", "Food Delivery"

2. **Category Type** - کیٹگری کی قسم:
   - `Ride` - سواری کی سروس
   - `Delivery` - ڈیلیوری سروس
   - `ServiceProvider` - دوسری سروسیں
   - `RideShare` - Carpool / Ride Sharing
   - `InterCity` - شہروں کے درمیان

3. **Status** - حالت:
   - `Active` - فعال (دکھائی دے گی)
   - `Inactive` - غیر فعال (نہیں دکھے گی)

### Optional Fields (اختیاری فیلڈز):

#### Multi-Language Names (دوسری زبانوں میں نام):
- Chinese (中文)
- Arabic (العربية)
- Hindi (हिंदी)
- French (Français)
- Spanish (Español)
- German (Deutsch)
- Portuguese (Português)
- Russian (Русский)
- Turkish (Türkçe)
- Italian (Italiano)
- اور بھی...

#### Images (تصاویر):
- **Logo** - Category کا icon
- **Banner Image** - Detail page کی تصویر
- **Homepage Logo** - Home page کا icon
- **Homepage Banner** - Home page کی بڑی تصویر

#### Pricing (قیمتیں):
- **Commission** - کمیشن کی فیصد
- **Waiting Fees** - انتظار کی فیس
- **Cancellation Fare** - منسوخی کی فیس

---

## 💡 Usage Examples / استعمال کی مثالیں

### Example 1: Adding "Taxi Service"

```
Category Name: Taxi Service
Category Type: Ride
Status: Active
For Category Type: RideCategory
Display Order: 1
```

### Example 2: Adding "Food Delivery"

```
Category Name: Food Delivery
Category Name (Arabic): توصيل الطعام
Category Name (Hindi): फूड डिलीवरी
Category Type: Delivery
Status: Active
Commission: 15.00
```

### Example 3: Adding "Personal Driver"

```
Category Name: Personal Driver
Category Name (Arabic): سائق شخصي
Category Name (Hindi): निजी ड्राइवर
Category Type: Ride
For Category Type: RideCategory
Status: Active
Logo: ic_personal_driver_EN.png
Banner Image: personal_driver_banner.jpg
Display Order: 2
```

---

## 🎯 Important Notes / اہم نوٹس

### Security (سیکیورٹی):
⚠️ **Important**: یہ admin panel ہے، اسے public نہ کریں!

**حفاظت کے لیے:**
1. `.htaccess` سے protect کریں
2. Password authentication لگائیں
3. یا صرف local server پر استعمال کریں

#### .htaccess Protection Example:
```apache
AuthType Basic
AuthName "Admin Area"
AuthUserFile /path/to/.htpasswd
Require valid-user
```

### Image Files (تصویری فائلیں):
- تصاویر پہلے server پر اپلوڈ کریں
- پھر form میں فائل کا نام لکھیں
- مثال: `ic_category_EN.png`

### JSON Fields:
کچھ fields JSON format میں ہیں:
```json
{
  "tBannerButtonText_EN": "Book Now",
  "tBannerButtonText_HI": "अभी बुक करें",
  "tBannerButtonText_AR": "احجز الآن"
}
```

---

## 🐛 Troubleshooting / مسائل کا حل

### "Connection failed" Error:
- Database credentials چیک کریں
- MySQL server چل رہا ہے؟
- Database name صحیح ہے؟

### "Column count doesn't match":
- یہ نہیں ہونا چاہیے، PHP script میں سب handle ہے
- اگر پھر بھی آئے تو database structure چیک کریں

### Cannot Insert:
- Database user کو INSERT permission ہے؟
- Table `vehicle_category` موجود ہے؟

---

## 📊 Database Structure Reference

کل columns: **131**

1. `iVehicleCategoryId` (Auto-generated)
2-26. Category names in 25 languages
27-51. Category titles in 25 languages
52-76. Category descriptions in 25 languages
77-131. Other settings (logos, prices, etc.)

---

## 🔄 Alternative: SQL File Method

اگر PHP استعمال نہیں کر سکتے:

1. `add_personal_driver_category.sql` کو مثال کے طور پر دیکھیں
2. اپنی values بدلیں
3. phpMyAdmin یا MySQL command line سے run کریں

---

## 📞 Support

کوئی مسئلہ ہو تو:
- Database logs چیک کریں
- PHP error log دیکھیں
- Browser console کھولیں (F12)

---

## ✅ Benefits کیوں بہتر ہے؟

| SQL Manual | PHP Admin Panel |
|------------|-----------------|
| ❌ ہر بار 131 values گننا | ✅ صرف ضروری fields بھریں |
| ❌ Syntax errors کا خطرہ | ✅ Auto validation |
| ❌ Language fields confuse | ✅ Organized sections |
| ❌ ID خود بنانا پڑے | ✅ Auto ID generation |
| ❌ Recent categories نہیں دیکھ سکتے | ✅ Last 20 categories دکھائی دیتی ہیں |

---

## 🎓 Tips & Tricks

1. **پہلے test کریں**: Inactive status سے شروع کریں
2. **Images organized رکھیں**: Folder structure بنائیں
3. **Naming convention**: Consistent نام رکھیں
4. **Backup لیں**: Add کرنے سے پہلے database backup
5. **Documentation**: اپنی categories کی list رکھیں

---

**Happy Category Management! 🚀**
**کیٹگریز ایڈ کرنے میں آسانی ہو! 🎉**
