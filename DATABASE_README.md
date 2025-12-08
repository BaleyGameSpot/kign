# Database Setup Guide

## Overview
This repository contains a MySQL database dump for the ride-hailing and multi-service platform.

## Database File
- **Compressed File:** `database.zip` (24 MB)
- **Extracted File:** `database.sql` (132 MB - not tracked in git)

## Database Structure
The database contains **260 tables** organized into the following categories:

### Main Features:
1. **User Management** - User profiles, addresses, wallets, emergency contacts
2. **Driver Management** - Driver registration, documents, vehicles, preferences
3. **Trip/Ride System** - Cab booking, ride sharing, scheduled rides
4. **Payment Processing** - Payment transactions, wallets, withdrawals
5. **Location & Fare** - Location-based pricing, airport surcharges
6. **Food Ordering** - Restaurant menus, orders, delivery
7. **Bidding System** - Service bidding and offers
8. **Hotel & Parking** - Hotel bookings, parking space reservations
9. **Rewards & Coupons** - Loyalty programs, gift cards
10. **Ratings & Reviews** - User and driver ratings
11. **Admin Panel** - Administrative controls and permissions
12. **Multi-language Support** - Internationalization tables

## How to Extract the Database

### On Linux/Mac:
```bash
unzip database.zip
```

### On Windows:
Right-click on `database.zip` and select "Extract All..."

## How to Import the Database

### Using MySQL Command Line:
```bash
mysql -u your_username -p your_database_name < database.sql
```

### Using phpMyAdmin:
1. Open phpMyAdmin
2. Create a new database
3. Click on "Import" tab
4. Select `database.sql` file
5. Click "Go"

### Using MySQL Workbench:
1. Open MySQL Workbench
2. Connect to your server
3. Go to Server → Data Import
4. Select "Import from Self-Contained File"
5. Choose `database.sql`
6. Click "Start Import"

## Important Notes

⚠️ **File Size:** The uncompressed SQL file is 132 MB and is not tracked in git due to GitHub's 100 MB file size limit. Always use the compressed `database.zip` file which is included in the repository.

⚠️ **Local Development:** After cloning the repository, extract `database.zip` to get `database.sql` for local development.

## Database Engine
- **Type:** MySQL
- **Engine:** InnoDB
- **Charset:** utf8mb4
- **Collation:** utf8mb4_general_ci

## Support
For any database-related issues, please contact the development team.
