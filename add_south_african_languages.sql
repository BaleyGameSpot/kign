-- ============================================================
-- Add South African Official Languages to Database
-- ============================================================
--
-- INSTRUCTIONS FOR PHPMYADMIN:
-- 1. Open phpMyAdmin
-- 2. Select your database from the left sidebar
-- 3. Click on "SQL" tab at the top
-- 4. Copy and paste this entire file content into the SQL query box
-- 5. Click "Go" button to execute
--
-- This script adds 10 South African languages to language_master table
-- (English already exists as ID 1)
-- All languages use ZAR (South African Rand) as currency
-- ============================================================

-- 1. Zulu (isiZulu)
INSERT INTO language_master VALUES("26","isiZulu","Zulu","ZU","en","zu","ZAR","R","26","Active","No","ltr","No","");

-- 2. Xhosa (isiXhosa)
INSERT INTO language_master VALUES("27","isiXhosa","Xhosa","XH","en","xh","ZAR","R","27","Active","No","ltr","No","");

-- 3. Afrikaans
INSERT INTO language_master VALUES("28","Afrikaans","Afrikaans","AF","en","af","ZAR","R","28","Active","No","ltr","No","");

-- 4. Sepedi (Northern Sotho)
INSERT INTO language_master VALUES("29","Sepedi","Sepedi","NSO","en","nso","ZAR","R","29","Active","No","ltr","No","");

-- 5. Setswana
INSERT INTO language_master VALUES("30","Setswana","Setswana","TN","en","tn","ZAR","R","30","Active","No","ltr","No","");

-- 6. Sesotho (Southern Sotho)
INSERT INTO language_master VALUES("31","Sesotho","Sesotho","ST","en","st","ZAR","R","31","Active","No","ltr","No","");

-- 7. Xitsonga
INSERT INTO language_master VALUES("32","Xitsonga","Xitsonga","TS","en","ts","ZAR","R","32","Active","No","ltr","No","");

-- 8. siSwati (Swazi)
INSERT INTO language_master VALUES("33","siSwati","siSwati","SS","en","ss","ZAR","R","33","Active","No","ltr","No","");

-- 9. Tshivenda (Venda)
INSERT INTO language_master VALUES("34","Tshivenda","Tshivenda","VE","en","ve","ZAR","R","34","Active","No","ltr","No","");

-- 10. isiNdebele (Southern Ndebele)
INSERT INTO language_master VALUES("35","isiNdebele","isiNdebele","NR","en","nr","ZAR","R","35","Active","No","ltr","No","");

-- ============================================================
-- VERIFICATION QUERY (Optional)
-- ============================================================
-- Run this query to verify all South African languages were added:
-- SELECT * FROM language_master WHERE vCurrencyCode = 'ZAR' ORDER BY iLanguageMasId;
--
-- You should see 10 languages with ZAR currency
-- ============================================================

-- Note: English is already in the database as ID 1
-- Total South African languages: 11 (10 added + 1 existing)
