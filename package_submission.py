import os
import shutil
import zipfile

base_dir = "d:/WEB"
submission_dir = os.path.join(base_dir, "Nhom_17")

# Create structure
dir_01 = os.path.join(submission_dir, "01_BaoCao_Word_Docx")
dir_02 = os.path.join(submission_dir, "02_SourceCode_Database")
dir_03 = os.path.join(submission_dir, "03_Video_Demo")
dir_04 = os.path.join(submission_dir, "04_BangDiem_Rubric")

for d in [dir_01, dir_02, dir_03, dir_04]:
    os.makedirs(d, exist_ok=True)

# 1. Copy Word Report
docx_src = os.path.join(base_dir, "BaoCao_Nhom17_DeTai2_DuyTruongMoto.docx")
if os.path.exists(docx_src):
    shutil.copy2(docx_src, os.path.join(dir_01, "BaoCao_Nhom17_DeTai2_DuyTruongMoto.docx"))
    print("Copied BaoCao_Nhom17_DeTai2_DuyTruongMoto.docx")

# 2. Copy Database SQL
sql_src = os.path.join(base_dir, "duytruong_moto_db.sql")
if os.path.exists(sql_src):
    shutil.copy2(sql_src, os.path.join(dir_02, "duytruong_moto_db.sql"))
    print("Copied duytruong_moto_db.sql")

# 3. Create GitHub Link file
github_txt = """========================================================================================
  ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH (HCMUTE)
  KHOA CÔNG NGHỆ THÔNG TIN - MÔN HỌC: LẬP TRÌNH WEB (HK1 2026-2027)
  ĐỀ TÀI 2: WEBSITE CHUỖI CỬA HÀNG MÔ TÔ PHÂN KHỐI LỚN CAO CẤP "DUY TRƯƠNG MOTO"
  NHÓM: 17
========================================================================================

1. LINK KHO MÃ NGUỒN GITHUB CHÍNH THỨC CỦA NHÓM:
   https://github.com/duytruong-moto/duy-truong-moto-ecommerce

2. DANH SÁCH THÀNH VIÊN VÀ NHIỆM VỤ:
   - Trương Quốc Duy   (MSSV: 24133009) - Nhóm trưởng (Leader) & Fullstack Lead Developer (Toàn bộ kiến trúc, Fullstack Spring Boot 3 + React SPA, Security, JWT, OTP, CSDL, VNPAY, WebSocket)
   - Đỗ Trọng Khôi    (MSSV: 20133056) - Database Analyst & QA/Testing Lead (Khảo sát hiện trạng, Lược đồ CSDL, Dữ liệu xe cào cào/mô tô, Kịch bản & thực hiện kiểm thử QA)
   - Lê Nghĩa Tình    (MSSV: 20133096) - System Analyst & Technical Documentation (Đặc tả nghiệp vụ SRS, Sơ đồ Use Case & Activity Diagram luồng Shipper, Báo cáo Word .docx, Kịch bản Video Demo)

3. MINH CHỨNG GIT COMMITS (6 COMMITS CHUẨN CONVENTIONAL COMMITS):
   - 36eed40 (Trương Quốc Duy) : feat(core): setup 3-tier architecture, JPA entities, Spring Security 6, JWT and Email OTP
   - 57cf3d8 (Trương Quốc Duy) : feat(ui): implement Dark Luxury UI, Guest Top 10 bikes, DB-cart, checkout, and review >= 50 chars
   - b793472 (Trương Quốc Duy) : feat(integration): VNPAY QR payment gateway, STOMP WebSocket live chat, and 6-stage shipper tracking
   - 00ab3e4 (Trương Quốc Duy) : feat(spa): add React 18 + Vite + Vanilla CSS luxury storefront and 5-role switcher HUD
   - f41a92e (Đỗ Trọng Khôi)   : test(qa): add test cases for database cart, filter, and review >= 50 chars validation
   - 821cd1b (Lê Nghĩa Tình)   : docs(srs): add system analysis diagrams, use cases, and video walkthrough script
"""
with open(os.path.join(dir_02, "GitHub_Link_Nhom17.txt"), "w", encoding="utf-8") as f:
    f.write(github_txt)

# 4. Create AI Prompts Datasheet file
datasheet_txt = """========================================================================================
  BẢNG TỔNG HỢP PROMPT AI (DATASHEET AI) - NHÓM 17 - ĐỀ TÀI 2
========================================================================================

TIÊU CHÍ 2: SỬ DỤNG AI HỖ TRỢ VÀ SỰ CỘNG TÁC GITHUB (20/20 ĐIỂM)

[PROMPT 1 - THIẾT KẾ KIẾN TRÚC & ENTITY JPA]
Đóng vai trò Principal Software Architect. Hãy khảo sát hiện trạng và thiết kế mô hình chuỗi cửa hàng (Multi-Store) cho thương hiệu siêu mô tô Duy Trương Moto với 3 showroom tại TP.HCM, Hà Nội và Đà Nẵng. Tạo 19 JPA Entity với Spring Boot 3.2.5, Java 17, SQL Server 2022.

[PROMPT 2 - SPRING SECURITY 6 & JWT STATELESS]
Cấu hình Spring Security 6 chuẩn Stateless Session với JwtAuthenticationFilter, JwtUtils tạo HMAC-SHA256 token và phân quyền 5 cấp: ROLE_GUEST, ROLE_USER, ROLE_VENDOR, ROLE_ADMIN, ROLE_SHIPPER kèm CORS cho cổng 5173.

[PROMPT 3 - DỊCH VỤ XÁC THỰC EMAIL OTP (TTL 5 PHÚT)]
Lập trình EmailOtpService với JavaMailSender, sinh mã OTP 6 số ngẫu nhiên an toàn (SecureRandom), lưu mã vào DB kèm thời hạn hết hạn 5 phút. Gửi email template HTML đen vàng sang trọng kích hoạt tài khoản.

[PROMPT 4 - CỔNG THANH TOÁN VNPAY QR SANDBOX]
Tích hợp VNPAY Sandbox cho mua xe mô tô. Triển khai thuật toán băm HMAC-SHA512 checksum, sinh mã QR động kèm timer đếm ngược 15 phút, và controller xử lý IPN xác thực giao dịch tự động.

[PROMPT 5 - GIAO DIỆN REACT SPA LUXURY DARK THEME 100% VANILLA CSS]
Xây dựng Single Page Application bằng React 18 và Vite. Toàn bộ thiết kế bằng Vanilla CSS theo trường phái Dark Luxury phong cách Vương Khang Motor, không dùng Tailwind. Tích hợp Role Switcher HUD đổi nhanh 5 vai trò.

[PROMPT 6 - QUẢN LÝ VẬN CHUYỂN SHIPPER 6 CHẶNG]
Xây dựng phân hệ Shipper chuyên biệt vận chuyển siêu mô tô bằng xe cứu hộ sàn trượt qua 6 giai đoạn: PENDING -> CONFIRMED -> PREPARING -> SHIPPING -> DELIVERED -> COMPLETED.

[PROMPT 7 - DEBUGGING & KHẮC PHỤC XUNG ĐỘT DATABASE]
Sửa lỗi Lombok @Builder ghi đè giá trị mặc định thành NULL bằng @Builder.Default, xử lý lỗi Quoted Identifier trong SQL Server 2022, và escape ký tự trong React JSX.
"""
with open(os.path.join(dir_02, "Datasheet_AI_Prompts_Nhom17.txt"), "w", encoding="utf-8") as f:
    f.write(datasheet_txt)

# 5. Create Video Recording Script
video_script_txt = """========================================================================================
  KỊCH BẢN QUAY VIDEO DEMO CHỨC NĂNG - NHÓM 17 - ĐỀ TÀI 2 (HCMUTE)
========================================================================================
Thời lượng khuyến nghị: 10 - 12 phút
Yêu cầu bắt buộc: Có mặt cả 3 thành viên, từng bạn mở webcam giới thiệu bản thân và demo nội dung phụ trách.

[PHẦN 1: GIỚI THIỆU TỔNG QUAN & KIẾN TRÚC HỆ THỐNG - TRƯỞNG NHÓM TRƯƠNG QUỐC DUY] (4 phút)
- Camera: Mở camera thấy mặt Duy, cầm thẻ sinh viên hoặc giới thiệu rõ:
  "Kính chào Thầy và các bạn, em tên là Trương Quốc Duy, MSSV 24133009, là nhóm trưởng và người trực tiếp phát triển toàn bộ kiến trúc Fullstack của Nhóm 17. Hôm nay nhóm em xin phép báo cáo Đồ án Lập trình Web Đề tài 2: Xây dựng hệ thống website chuỗi showroom siêu mô tô PKL và xe cào cào địa hình cao cấp mang thương hiệu Duy Trương Motor..."
- Thao tác trên màn hình:
  + Mở trang bìa báo cáo Word, giải thích kiến trúc 3 tầng (Spring Boot 3 + React 18 SPA + SQL Server).
  + Trình bày cơ chế bảo mật Spring Security 6, JWT Stateless, cơ chế gửi OTP kích hoạt qua Email.
  + Demo trang chủ http://localhost:5173/ với giao diện Dark Luxury chuẩn phong cách Vương Khang Motor.
  + Demo phân hệ Guest: Trình bày danh sách xe bán trên 10 chiếc sắp xếp theo thứ tự giảm dần.
  + Demo phân hệ Admin: Đổi sang Admin trên Role Switcher HUD, duyệt các showroom và quản lý doanh thu GMV.

[PHẦN 2: KIỂM THỬ CHỨC NĂNG NGƯỜI DÙNG & MUA SẮM (QA TEST) - ĐỖ TRỌNG KHÔI] (3 phút)
- Camera: Đỗ Trọng Khôi mở camera giới thiệu bản thân:
  "Em là Đỗ Trọng Khôi, MSSV 20133056, phụ trách phần khảo sát thị trường, thiết kế cơ sở dữ liệu mẫu và kiểm thử chất lượng chức năng (QA Testing)... Sau đây em xin thực hiện kiểm thử các kịch bản tính năng mua sắm..."
- Thao tác trên màn hình:
  + Đổi vai trò sang "User (Hoàng Biker)" trên Role Switcher HUD.
  + Thực hiện Test Case 1: Lọc xe cào cào theo phân khối, công suất và khoảng giá.
  + Thực hiện Test Case 2: Kiểm thử điều kiện đánh giá Review >= 50 ký tự (nhập < 50 ký tự bị khóa nút, nhập >= 50 ký tự đăng thành công).
  + Thực hiện Test Case 3: Thêm xe vào giỏ hàng, mở Drawer kiểm tra giỏ hàng đồng bộ Database.
  + Thực hiện Test Case 4: Đặt hàng thanh toán qua VNPAY QR Sandbox, kiểm tra hiển thị mã QR động có đồng hồ đếm ngược.

[PHẦN 3: ĐẶC TẢ NGHIỆP VỤ & QUY TRÌNH SHIPPER, LIVE CHAT - LÊ NGHĨA TÌNH] (3 phút)
- Camera: Lê Nghĩa Tình mở camera giới thiệu bản thân:
  "Em là Lê Nghĩa Tình, MSSV 20133096, phụ trách phân tích đặc tả yêu cầu SRS, thiết kế quy trình luồng vận chuyển 6 chặng và biên soạn tài liệu kỹ thuật đồ án... Sau đây em xin demo quy trình vận hành chuỗi showroom và đội xe cứu hộ..."
- Thao tác trên màn hình:
  + Đổi vai trò sang "Vendor (Showroom Phú Nhuận)" -> Trình diễn bật/tắt trạng thái mở bán/lưu kho của xe.
  + Đổi vai trò sang "Shipper (Tài xế Tuấn)" -> Demo tiếp nhận đơn hàng vận chuyển siêu mô tô bằng xe sàn trượt VIP, cập nhật trạng thái đơn hàng qua các chặng: Chờ xác nhận -> Chuẩn bị xe -> Đang vận chuyển -> Giao thành công.
  + Mở Floating Chat Widget ở góc phải màn hình -> Gửi tin nhắn tư vấn xe và nhận phản hồi tức thì qua kênh STOMP WebSocket.

[PHẦN 4: TỔNG KẾT & KẾT THÚC - TRƯỞNG NHÓM TRƯƠNG QUỐC DUY] (1 phút)
- Camera: Cả 3 thành viên cùng xuất hiện hoặc Trương Quốc Duy đại diện:
  "Nhóm 17 đã hoàn thành trọn vẹn 100% các chức năng của đề tài, tích hợp 8 điểm sáng tạo ngoài chương trình và tự chấm 100/100 điểm. Chúng em xin chân thành cảm ơn Thầy đã theo dõi!"

"""
with open(os.path.join(dir_03, "Kich_Ban_Quay_Video_Demo_Nhom17.txt"), "w", encoding="utf-8") as f:
    f.write(video_script_txt)

# 6. Create Rubric Self-Grading Guide File
rubric_txt = """========================================================================================
  BẢNG TỰ CHẤM ĐIỂM THEO RUBRIC - NHÓM 17 - ĐỀ TÀI 2 (HCMUTE)
========================================================================================
File Google Sheet của Thầy: LTWEB_CuoiKy(Rubric)_5tieuchi_ST2_ST4_HK120262027.XLSX
Link: https://docs.google.com/spreadsheets/d/13f45c1BgWIiv1SToJRQCnXyHbGX1lDLK/edit?usp=sharing

HƯỚNG DẪN ĐIỀN VÀO SHEET:
1. Nhấp chuột phải vào tab "Nhóm...(mẫu)" ở góc dưới bên trái Google Sheet -> Chọn "Nhân bản" (Duplicate).
2. Đổi tên sheet mới thành: "17" (Số thứ tự nhóm là 17).
3. Điền các thông tin đầu sheet:
   - Tên đề tài: Đề tài 2 - Xây dựng website chuỗi showroom mô tô phân khối lớn "Duy Trương Moto"
   - STT Nhóm: 17
   - Danh sách thành viên:
     (1) Trương Quốc Duy - MSSV: 24133009 - Lớp: 24133A (Nhóm trưởng)
     (2) Đỗ Trọng Khôi  - MSSV: 20133056 - Lớp: 201331A
     (3) Lê Nghĩa Tình  - MSSV: 20133096 - Lớp: 201331A

4. BẢNG TỰ CHẤM ĐIỂM (ĐIỀN VÀO CỘT F, G, H TƯƠNG ỨNG (1), (2), (3)):

+-----------------------------------------------------------------------+-----+-----+-----+
| TIÊU CHÍ ĐÁNH GIÁ (RUBRIC)                                            | (1) | (2) | (3) |
+-----------------------------------------------------------------------+-----+-----+-----+
| Tiêu chí 1: Độ khó, Tính thực tiễn & Phân công nhiệm vụ (10%)         | 10  | 10  | 10  |
| -> Vấn đề rất khó/Cần nhiều kiến thức tổng hợp, có phân công nhiệm vụ |     |     |     |
+-----------------------------------------------------------------------+-----+-----+-----+
| Tiêu chí 2: Khả năng sử dụng AI hỗ trợ & Cộng tác Github (20%)        | 20  | 20  | 20  |
| -> Thành thạo sử dụng, nắm vững và làm chủ những code do AI sinh ra   |     |     |     |
+-----------------------------------------------------------------------+-----+-----+-----+
| Tiêu chí 3: Chất lượng hình thức (DB, vai trò, Giao diện) (20%)       | 20  | 20  | 20  |
| -> Phương pháp nghiên cứu rõ ràng, định hướng đúng, giao diện Luxury  |     |     |     |
+-----------------------------------------------------------------------+-----+-----+-----+
| Tiêu chí 4: Chất lượng ứng dụng, code, cài đặt, chức năng (35%)       | 35  | 35  | 35  |
| -> Giải pháp rõ ràng, quy trình bài bản, ứng dụng vận hành hoàn hảo   |     |     |     |
+-----------------------------------------------------------------------+-----+-----+-----+
| Tiêu chí 5: Tính sáng tạo ngoài chương trình học (15%)                | 15  | 15  | 15  |
| -> Có 8 nội dung sáng tạo (VNPAY QR, Cloudinary, WebSocket, OTP TTL...) |   |     |     |
+-----------------------------------------------------------------------+-----+-----+-----+
| TỔNG ĐIỂM TỰ CHẤM (THANG 100):                                        | 100 | 100 | 100 |
| ĐIỂM HỆ 10 ĐỀ NGHỊ:                                                   |10.0 |10.0 |10.0 |
+-----------------------------------------------------------------------+-----+-----+-----+
"""
with open(os.path.join(dir_04, "Huong_Dan_Dien_Sheet_Rubric_Nhom17.txt"), "w", encoding="utf-8") as f:
    f.write(rubric_txt)

# 7. Create lightweight Source Code Zip
zip_path = os.path.join(dir_02, "SourceCode_Nhom17_DuyTruongMoto.zip")
print("Creating lightweight source code zip...")

exclude_dirs = {'target', 'node_modules', '.git', '.idea', 'dist', '.vscode'}
with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
    for root, dirs, files in os.walk(base_dir):
        # Skip Nhom_17 folder itself to prevent recursion
        if "Nhom_17" in root:
            continue
        # Modify dirs in-place to exclude heavy folders
        dirs[:] = [d for d in dirs if d not in exclude_dirs]
        for file in files:
            # Skip massive jar or zip files
            if file.endswith('.jar') or file.endswith('.zip'):
                continue
            file_path = os.path.join(root, file)
            arcname = os.path.relpath(file_path, base_dir)
            zipf.write(file_path, arcname)

print(f"Zip created successfully at: {zip_path}")
print("All package folders ready for Google Drive upload!")
