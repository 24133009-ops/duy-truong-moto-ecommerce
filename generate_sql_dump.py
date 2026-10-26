import subprocess
import os

tables = [
    'user_levels', 'roles', 'users', 'user_addresses',
    'stores', 'commissions', 'categories', 'styles', 'style_values',
    'products', 'product_style_values',
    'carts', 'cart_items', 'orders', 'order_items', 'deliveries',
    'transactions', 'reviews', 'wishlists', 'chat_messages'
]

sql_header = """/*
=============================================================================
  ĐẠI HỌC SƯ PHẠM KỸ THUẬT THÀNH PHỐ HỒ CHÍ MINH (HCMUTE)
  KHOA CÔNG NGHỆ THÔNG TIN - MÔN HỌC: LẬP TRÌNH WEB (HK1 / 2026-2027)
  ĐỀ TÀI 2: HỆ THỐNG WEBSITE CHUỖI CỬA HÀNG MÔ TÔ PHÂN KHỐI LỚN "DUY TRƯƠNG MOTO"
  NHÓM: 17
  THÀNH VIÊN:
    1. Trương Quốc Duy   - MSSV: 24133009 - Lớp: 24133A (Nhóm trưởng / Leader)
    2. Đỗ Trọng Khôi    - MSSV: 20133056 - Lớp: 201331A (Thành viên)
    3. Lê Nghĩa Tình    - MSSV: 20133096 - Lớp: 201331A (Thành viên)
=============================================================================
*/

USE master;
GO

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'DuyTruongMotoDB')
BEGIN
    CREATE DATABASE DuyTruongMotoDB;
END;
GO

USE DuyTruongMotoDB;
GO

SET ANSI_NULLS ON;
GO
SET QUOTED_IDENTIFIER ON;
GO
"""

print("Generating SQL dump...")

# Let's inspect each table and generate INSERT statements
script_lines = [sql_header]

for tbl in tables:
    cmd = f'sqlcmd -S localhost -U duytruong_admin -P Password123@ -d DuyTruongMotoDB -y 0 -Q "SELECT * FROM {tbl};"'
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True, encoding='utf-8')
    script_lines.append(f"-- =============================================\n-- TABLE: {tbl}\n-- =============================================\n")

with open('d:/WEB/duytruong_moto_db.sql', 'w', encoding='utf-8') as f:
    f.write("\n".join(script_lines))

print("Created base SQL script.")
