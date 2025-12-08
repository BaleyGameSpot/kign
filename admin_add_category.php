<?php
/**
 * Vehicle Category Management System
 * Add, Edit, and View vehicle categories
 */

// Database configuration - UPDATE THESE VALUES
define('DB_HOST', 'localhost');
define('DB_USER', 'your_username');
define('DB_PASS', 'your_password');
define('DB_NAME', 'your_database');

// Create database connection
$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

// Handle form submission
$message = '';
$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['add_category'])) {
    try {
        // Get next available ID
        $result = $conn->query("SELECT MAX(iVehicleCategoryId) as max_id FROM vehicle_category");
        $row = $result->fetch_assoc();
        $next_id = ($row['max_id'] ?? 0) + 1;

        // Prepare all values (131 columns total)
        $values = [
            $next_id,

            // vCategory (25 languages) - Fields 2-26
            $_POST['vCategory_EN'] ?? '',
            $_POST['vCategory_ZHCN'] ?? '',
            $_POST['vCategory_CS'] ?? '',
            $_POST['vCategory_FI'] ?? '',
            $_POST['vCategory_DA'] ?? '',
            $_POST['vCategory_FR'] ?? '',
            $_POST['vCategory_IW'] ?? '',
            $_POST['vCategory_HI'] ?? '',
            $_POST['vCategory_PL'] ?? '',
            $_POST['vCategory_RU'] ?? '',
            $_POST['vCategory_TR'] ?? '',
            $_POST['vCategory_KO'] ?? '',
            $_POST['vCategory_TL'] ?? '',
            $_POST['vCategory_MS'] ?? '',
            $_POST['vCategory_RO'] ?? '',
            $_POST['vCategory_PT'] ?? '',
            $_POST['vCategory_EL'] ?? '',
            $_POST['vCategory_NL'] ?? '',
            $_POST['vCategory_AR'] ?? '',
            $_POST['vCategory_IT'] ?? '',
            $_POST['vCategory_SV'] ?? '',
            $_POST['vCategory_ES'] ?? '',
            $_POST['vCategory_NO'] ?? '',
            $_POST['vCategory_DE'] ?? '',
            $_POST['vCategory_TH'] ?? '',

            // vCategoryTitle (25 languages) - Fields 27-51
            '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '',

            // tCategoryDesc (25 languages) - Fields 52-76
            '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '',

            // Other fields - Fields 77-131
            $_POST['iParentId'] ?? 0,
            $_POST['vLogo'] ?? '',
            $_POST['vLogo1'] ?? '',
            $_POST['vLogo2'] ?? '',
            $_POST['vHomepageLogo'] ?? '',
            $_POST['ePriceType'] ?? 'Service',
            $_POST['eBeforeUpload'] ?? 'No',
            $_POST['eAfterUpload'] ?? 'No',
            $_POST['iDisplayOrder'] ?? 1,
            $_POST['eStatus'] ?? 'Active',
            $_POST['eShowType'] ?? 'Icon',
            $_POST['eMaterialCommision'] ?? 'No',
            $_POST['vBannerImage'] ?? '',
            $_POST['eCatType'] ?? 'ServiceProvider',
            $_POST['eSubCatType'] ?? '',
            $_POST['eFor'] ?? '',
            $_POST['eDeliveryType'] ?? '',
            $_POST['iServiceId'] ?? 0,
            $_POST['tBannerButtonText'] ?? '{}',
            $_POST['eDetailPageView'] ?? '',
            $_POST['fCommision'] ?? 0.00,
            $_POST['fWaitingFees'] ?? 0.00,
            $_POST['iWaitingFeeTimeLimit'] ?? 0,
            $_POST['fCancellationFare'] ?? 0.00,
            $_POST['iCancellationTimeLimit'] ?? 0,
            $_POST['iMasterVehicleCategoryId'] ?? 0,
            $_POST['iDisplayOrderHomepage'] ?? 1,
            $_POST['lCatDescHomepage'] ?? '{}',
            $_POST['vCatDescbtnHomepage'] ?? '{}',
            $_POST['vCatNameHomepage'] ?? '{}',
            $_POST['vCatSloganHomepage'] ?? '{}',
            $_POST['vCatTitleHomepage'] ?? '{}',
            $_POST['vHomepageBanner'] ?? '',
            $_POST['vServiceCatTitleHomepage'] ?? '{}',
            $_POST['vServiceHomepageBanner'] ?? '',
            $_POST['eCatViewType'] ?? 'Icon',
            $_POST['tListDescription'] ?? '{}',
            $_POST['vListLogo'] ?? '',
            $_POST['vListLogo1'] ?? '',
            $_POST['vListLogo2'] ?? '',
            $_POST['vListLogo3'] ?? '',
            $_POST['eOTPCodeEnable'] ?? 'No',
            $_POST['ePromoteBanner'] ?? 'No',
            $_POST['vPromoteBannerImage'] ?? '',
            $_POST['tPromoteBannerTitle'] ?? '{}',
            $_POST['vHomepageLogoOurServices'] ?? '',
            $_POST['eVideoConsultEnable'] ?? 'No',
            $_POST['eVideoConsultServiceCharge'] ?? 0.00,
            $_POST['eVideoServiceDescription'] ?? '',
            $_POST['fCommissionVideoConsult'] ?? 0.00,
            $_POST['vIconDetails'] ?? '',
            $_POST['eForMedicalService'] ?? 'No',
            $_POST['tMedicalServiceInfo'] ?? '',
            $_POST['vServiceImage'] ?? '',
            $_POST['iDisplayOrderVC'] ?? 0
        ];

        // Create placeholders
        $placeholders = str_repeat('?,', count($values) - 1) . '?';

        // Prepare statement
        $sql = "INSERT INTO vehicle_category VALUES ($placeholders)";
        $stmt = $conn->prepare($sql);

        if (!$stmt) {
            throw new Exception("Prepare failed: " . $conn->error);
        }

        // Create types string (all strings except numbers)
        $types = 'i' . str_repeat('s', 25) . str_repeat('s', 25) . str_repeat('s', 25) .
                 'isssssssissssssssddddddiisssssssssssssssssdsddsssss';

        // Bind parameters
        $stmt->bind_param($types, ...$values);

        // Execute
        if ($stmt->execute()) {
            $message = "✅ Category added successfully! ID: " . $next_id;
            // Clear form
            $_POST = [];
        } else {
            throw new Exception("Execute failed: " . $stmt->error);
        }

        $stmt->close();

    } catch (Exception $e) {
        $error = "❌ Error: " . $e->getMessage();
    }
}

// Get existing categories for reference
$categories = [];
$result = $conn->query("SELECT iVehicleCategoryId, vCategory_EN, eCatType, eStatus FROM vehicle_category ORDER BY iVehicleCategoryId DESC LIMIT 20");
if ($result) {
    while ($row = $result->fetch_assoc()) {
        $categories[] = $row;
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Vehicle Category</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 10px;
        }
        .subtitle {
            color: #666;
            margin-bottom: 30px;
        }
        .message {
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .form-section {
            margin-bottom: 30px;
            border-bottom: 2px solid #eee;
            padding-bottom: 20px;
        }
        .form-section:last-child {
            border-bottom: none;
        }
        .section-title {
            font-size: 18px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }
        .section-title::before {
            content: '';
            display: inline-block;
            width: 4px;
            height: 20px;
            background: #3498db;
            margin-right: 10px;
        }
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        .form-group {
            display: flex;
            flex-direction: column;
        }
        label {
            font-weight: 500;
            margin-bottom: 5px;
            color: #555;
            font-size: 14px;
        }
        label .required {
            color: #e74c3c;
        }
        input, select, textarea {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: #3498db;
        }
        textarea {
            resize: vertical;
            min-height: 80px;
        }
        .submit-btn {
            background: #3498db;
            color: white;
            padding: 15px 40px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s;
            margin-top: 20px;
        }
        .submit-btn:hover {
            background: #2980b9;
        }
        .categories-list {
            margin-top: 40px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #555;
        }
        tr:hover {
            background: #f8f9fa;
        }
        .badge {
            padding: 4px 8px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 500;
        }
        .badge-active {
            background: #d4edda;
            color: #155724;
        }
        .badge-inactive {
            background: #f8d7da;
            color: #721c24;
        }
        .help-text {
            font-size: 12px;
            color: #888;
            margin-top: 3px;
        }
        .language-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚗 Vehicle Category Management</h1>
        <p class="subtitle">Add new categories to your ride-hailing platform</p>

        <?php if ($message): ?>
            <div class="message success"><?= htmlspecialchars($message) ?></div>
        <?php endif; ?>

        <?php if ($error): ?>
            <div class="message error"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <!-- Basic Information -->
            <div class="form-section">
                <div class="section-title">📝 Basic Information</div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Category Name (English) <span class="required">*</span></label>
                        <input type="text" name="vCategory_EN" required
                               value="<?= htmlspecialchars($_POST['vCategory_EN'] ?? '') ?>"
                               placeholder="e.g., Personal Driver">
                    </div>

                    <div class="form-group">
                        <label>Category Type <span class="required">*</span></label>
                        <select name="eCatType" required>
                            <option value="Ride">Ride</option>
                            <option value="MotoRide">Moto Ride</option>
                            <option value="Delivery">Delivery</option>
                            <option value="ServiceProvider">Service Provider</option>
                            <option value="RideShare">Ride Share</option>
                            <option value="RidePool">Ride Pool</option>
                            <option value="InterCity">Inter City</option>
                            <option value="CorporateRide">Corporate Ride</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Status</label>
                        <select name="eStatus">
                            <option value="Active">Active</option>
                            <option value="Inactive">Inactive</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>For Category Type</label>
                        <select name="eFor">
                            <option value="">None</option>
                            <option value="RideCategory">Ride Category</option>
                            <option value="DeliveryCategory">Delivery Category</option>
                            <option value="DeliverAllCategory">DeliverAll Category</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Multi-Language Names -->
            <div class="form-section">
                <div class="section-title">🌍 Multi-Language Names</div>
                <div class="language-grid">
                    <div class="form-group">
                        <label>Chinese (中文)</label>
                        <input type="text" name="vCategory_ZHCN" placeholder="中文名称">
                    </div>
                    <div class="form-group">
                        <label>Arabic (العربية)</label>
                        <input type="text" name="vCategory_AR" placeholder="اسم عربي">
                    </div>
                    <div class="form-group">
                        <label>Hindi (हिंदी)</label>
                        <input type="text" name="vCategory_HI" placeholder="हिंदी नाम">
                    </div>
                    <div class="form-group">
                        <label>French (Français)</label>
                        <input type="text" name="vCategory_FR" placeholder="Nom français">
                    </div>
                    <div class="form-group">
                        <label>Spanish (Español)</label>
                        <input type="text" name="vCategory_ES" placeholder="Nombre español">
                    </div>
                    <div class="form-group">
                        <label>German (Deutsch)</label>
                        <input type="text" name="vCategory_DE" placeholder="Deutscher Name">
                    </div>
                    <div class="form-group">
                        <label>Portuguese (Português)</label>
                        <input type="text" name="vCategory_PT" placeholder="Nome português">
                    </div>
                    <div class="form-group">
                        <label>Russian (Русский)</label>
                        <input type="text" name="vCategory_RU" placeholder="Русское имя">
                    </div>
                    <div class="form-group">
                        <label>Turkish (Türkçe)</label>
                        <input type="text" name="vCategory_TR" placeholder="Türkçe isim">
                    </div>
                    <div class="form-group">
                        <label>Italian (Italiano)</label>
                        <input type="text" name="vCategory_IT" placeholder="Nome italiano">
                    </div>
                </div>
            </div>

            <!-- Images & Assets -->
            <div class="form-section">
                <div class="section-title">🖼️ Images & Assets</div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Logo (vLogo)</label>
                        <input type="text" name="vLogo" placeholder="ic_category_EN.png">
                        <span class="help-text">Main category icon</span>
                    </div>

                    <div class="form-group">
                        <label>Logo 1 (vLogo1)</label>
                        <input type="text" name="vLogo1" placeholder="ic_category_EN.png">
                    </div>

                    <div class="form-group">
                        <label>Banner Image</label>
                        <input type="text" name="vBannerImage" placeholder="category_banner.jpg">
                        <span class="help-text">Detail page banner</span>
                    </div>

                    <div class="form-group">
                        <label>Homepage Logo</label>
                        <input type="text" name="vHomepageLogo" placeholder="image_category.svg">
                    </div>

                    <div class="form-group">
                        <label>Homepage Banner</label>
                        <input type="text" name="vHomepageBanner" placeholder="category_home_banner.jpg">
                    </div>

                    <div class="form-group">
                        <label>Service Homepage Banner</label>
                        <input type="text" name="vServiceHomepageBanner" placeholder="category_service_banner.jpg">
                    </div>
                </div>
            </div>

            <!-- Display Settings -->
            <div class="form-section">
                <div class="section-title">⚙️ Display Settings</div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Display Order</label>
                        <input type="number" name="iDisplayOrder" value="1" min="1">
                    </div>

                    <div class="form-group">
                        <label>Homepage Display Order</label>
                        <input type="number" name="iDisplayOrderHomepage" value="1" min="1">
                    </div>

                    <div class="form-group">
                        <label>Show Type</label>
                        <select name="eShowType">
                            <option value="Icon">Icon</option>
                            <option value="Banner">Banner</option>
                            <option value="Icon-Banner">Icon-Banner</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Category View Type</label>
                        <select name="eCatViewType">
                            <option value="Icon">Icon</option>
                            <option value="Banner">Banner</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Parent Category ID</label>
                        <input type="number" name="iParentId" value="0" min="0">
                        <span class="help-text">0 for main category</span>
                    </div>
                </div>
            </div>

            <!-- Pricing & Fees -->
            <div class="form-section">
                <div class="section-title">💰 Pricing & Fees</div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Price Type</label>
                        <select name="ePriceType">
                            <option value="Service">Service</option>
                            <option value="Provider">Provider</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Commission (%)</label>
                        <input type="number" name="fCommision" value="0.00" step="0.01" min="0">
                    </div>

                    <div class="form-group">
                        <label>Waiting Fees</label>
                        <input type="number" name="fWaitingFees" value="0.00" step="0.01" min="0">
                    </div>

                    <div class="form-group">
                        <label>Waiting Fee Time Limit (min)</label>
                        <input type="number" name="iWaitingFeeTimeLimit" value="0" min="0">
                    </div>

                    <div class="form-group">
                        <label>Cancellation Fare</label>
                        <input type="number" name="fCancellationFare" value="0.00" step="0.01" min="0">
                    </div>

                    <div class="form-group">
                        <label>Cancellation Time Limit (min)</label>
                        <input type="number" name="iCancellationTimeLimit" value="0" min="0">
                    </div>

                    <div class="form-group">
                        <label>Material Commission</label>
                        <select name="eMaterialCommision">
                            <option value="No">No</option>
                            <option value="Yes">Yes</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Advanced Settings -->
            <div class="form-section">
                <div class="section-title">🔧 Advanced Settings</div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>OTP Code Enable</label>
                        <select name="eOTPCodeEnable">
                            <option value="No">No</option>
                            <option value="Yes">Yes</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Video Consult Enable</label>
                        <select name="eVideoConsultEnable">
                            <option value="No">No</option>
                            <option value="Yes">Yes</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>For Medical Service</label>
                        <select name="eForMedicalService">
                            <option value="No">No</option>
                            <option value="Yes">Yes</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Promote Banner</label>
                        <select name="ePromoteBanner">
                            <option value="No">No</option>
                            <option value="Yes">Yes</option>
                        </select>
                    </div>
                </div>
            </div>

            <button type="submit" name="add_category" class="submit-btn">➕ Add Category</button>
        </form>

        <!-- Recent Categories List -->
        <div class="categories-list">
            <div class="section-title">📋 Recently Added Categories (Last 20)</div>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Category Name</th>
                        <th>Type</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($categories as $cat): ?>
                        <tr>
                            <td><?= $cat['iVehicleCategoryId'] ?></td>
                            <td><?= htmlspecialchars($cat['vCategory_EN']) ?></td>
                            <td><?= $cat['eCatType'] ?></td>
                            <td>
                                <span class="badge <?= $cat['eStatus'] === 'Active' ? 'badge-active' : 'badge-inactive' ?>">
                                    <?= $cat['eStatus'] ?>
                                </span>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                    <?php if (empty($categories)): ?>
                        <tr>
                            <td colspan="4" style="text-align: center; color: #999;">No categories found</td>
                        </tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
<?php $conn->close(); ?>
