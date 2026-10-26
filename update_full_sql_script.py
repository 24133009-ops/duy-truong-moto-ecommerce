with open("d:/WEB/generate_full_sql.py", "r", encoding="utf-8") as f:
    code = f.read()

# Add categories if not present
if "catOto" not in code and "Siêu Xe Ô Tô & Xe Sang" not in code:
    old_cat = "(7, N'Pô Độ & Đồ Bảo Hộ Cào Cào', 'gear-parts', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80', 0);"
    new_cat = """(7, N'Pô Độ & Đồ Bảo Hộ Cào Cào', 'gear-parts', 'https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?auto=format&fit=crop&w=800&q=80', 0),
(8, N'Siêu Xe Ô Tô & Xe Sang', 'oto', '/images/lamborghini_huracan.jpg', 0),
(9, N'Xe Nhỏ & Tay Ga Sưu Tầm', 'xenho', '/images/honda_monkey_125.jpg', 0);"""
    code = code.replace(old_cat, new_cat)

with open("d:/WEB/generate_full_sql.py", "w", encoding="utf-8") as f:
    f.write(code)

print("Updated generate_full_sql.py")
