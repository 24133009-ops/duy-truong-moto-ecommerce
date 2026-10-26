// DUY TRƯƠNG MOTOR - AI CSKH CHATBOT ENGINE
// Kiến trúc tri thức tư vấn môtô PKL & xe cào cào chuyên nghiệp

export const SUGGESTION_CHIPS = [
  { id: 'caocao', label: '🏍️ Báo giá Xe Cào Cào', query: 'Tư vấn các dòng xe cào cào địa hình đang có sẵn' },
  { id: 'doimau', label: '🎨 Đổi màu xe / Wrap Decal', query: 'Showroom có hỗ trợ đổi màu xe, wrap decal hay làm lại cavet màu mới không?' },
  { id: 'thuongluong', label: '🏷️ Bớt lộc & Thương lượng giá', query: 'Chính sách bớt lộc, giảm giá và thương lượng giá xe như thế nào?' },
  { id: 'over1b', label: '⚡ Siêu môtô trên 1 tỷ', query: 'Showroom có những siêu môtô nào trên 1 tỷ?' },
  { id: 'range300_500', label: '💰 Xe tầm 300 - 500 triệu', query: 'Tầm giá 300 đến 500 triệu mua được xe gì?' },
  { id: 'quaydau', label: '🔄 Chính sách Bao quay đầu', query: 'Chính sách bao quay đầu và thu mua xe cũ thế nào?' },
  { id: 'giaoxe', label: '🚚 Giao xe Sàn Trượt VIP', query: 'Quy trình giao xe sàn trượt tận nhà 63 tỉnh thành' },
  { id: 'tragop', label: '💳 Thủ tục Trả góp HQCN', query: 'Tư vấn thủ tục mua xe môtô trả góp qua CCCD' },
  { id: 'showroom', label: '📍 Địa chỉ 3 Showroom', query: 'Địa chỉ các chi nhánh showroom và hotline' }
];

// Helper to remove Vietnamese tones for flexible keyword matching
function removeVietnameseTones(str) {
  if (!str) return '';
  str = str.toLowerCase();
  str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
  str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
  str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
  str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
  str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
  str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
  str = str.replace(/đ/g, "d");
  str = str.replace(/\u0300|\u0301|\u0303|\u0309|\u0323/g, "");
  str = str.replace(/\u02C6|\u0306|\u031B/g, "");
  return str.trim();
}

export function generateSmartBotReply(rawInput, allProducts = [], formatMoney) {
  const text = rawInput.trim();
  const clean = removeVietnameseTones(text);

  // 1. CHÍNH SÁCH BAO QUAY ĐẦU & THU MUA XE CŨ
  if (clean.includes('quay dau') || clean.includes('ban lai') || clean.includes('doi xe') || clean.includes('thu mua') || clean.includes('mua lai') || clean.includes('trade-in') || clean.includes('doi cac') || clean.includes('mua cua nguoi chan')) {
    return `🏍️ **CHÍNH SÁCH BAO QUAY ĐẦU & ĐỔI XE DUY TRƯƠNG MOTOR**
Slogan: *"Ở đây, chúng tôi MUA CỦA NGƯỜI CHÁN - & BÁN CHO NGƯỜI CẦN."*

Bên em áp dụng cam kết bảo hộ giá trị xe minh bạch số 1 Việt Nam:
• **0 - 3 tháng:** Khấu trừ chỉ **5% - 7%** giá trị xe nếu anh muốn trải nghiệm xe mới.
• **3 - 6 tháng:** Khấu trừ **8% - 10%** (tùy ODO và tình trạng xe).
• **6 - 12 tháng:** Khấu trừ **10% - 15%** theo thỏa thuận hợp lý nhất.
• **Thu mua môtô cũ tận nhà:** Định giá qua Zalo/ảnh chụp chỉ 15 phút, nhân viên đến tận nơi 63 tỉnh thành giải ngân tiền mặt 100%!

👉 Anh có xe cũ muốn đổi hoặc nâng đời không ạ? Anh gửi hình ảnh & cavet xe để em báo giá thu mua cao nhất nhé!`;
  }

  // 2. GIAO XE SÀN TRƯỢT VIP TẬN NHÀ
  if (clean.includes('giao xe') || clean.includes('ship') || clean.includes('van chuyen') || clean.includes('san truot') || clean.includes('ve nha') || clean.includes('tinh xa')) {
    return `🚚 **DỊCH VỤ BÀN GIAO XE SÀN TRƯỢT VIP 63 TỈNH THÀNH**

Duy Trương Motor sở hữu đội xe cứu hộ sàn trượt chuyên dụng chuẩn quốc tế:
• **Sàn trượt hạ sát đất:** Đảm bảo môtô không một vết trầy gầm hay cạ lốp.
• **Nghi thức bàn giao:** Xe được rửa chi tiết, phủ bạt nhung hoàng gia, mở nắp chụp ảnh lưu niệm ngay tại cửa nhà khách hàng.
• **Kiểm tra nổ máy:** Quý khách nổ máy thử, kiểm tra đầy đủ giấy tờ HQCN rồi mới thanh toán phần tiền còn lại.
• **Ưu đãi:** Miễn phí 100% chi phí vận chuyển toàn quốc cho khách hàng chốt cọc qua cổng VNPAY trực tuyến!`;
  }

  // 3. TRẢ GÓP & THỦ TỤC NGÂN HÀNG
  if (clean.includes('tra gop') || clean.includes('vay') || clean.includes('lai suat') || clean.includes('ngan hang') || clean.includes('cccd') || clean.includes('ho so')) {
    return `💳 **CHƯƠNG TRÌNH TRẢ GÓP MÔTÔ PKL HQCN SIÊU TỐC**

Duy Trương Motor liên kết trực tiếp với HD SAISON, VPBank, Shinhan Bank & Mirae Asset:
• **Hạn mức tài trợ:** Hỗ trợ vay từ **30% đến 80%** giá trị xe.
• **Thủ tục đơn giản:** Chỉ cần **CCCD gắn chip**, không cần chứng minh thu nhập phức tạp.
• **Thời gian duyệt:** Xét duyệt hồ sơ online trong **30 phút**, nhận xe trong ngày.
• **Kỳ hạn linh hoạt:** 12 - 24 - 36 tháng với lãi suất ưu đãi chỉ từ 0.79%/tháng.

Ví dụ: Mẫu xe **400 triệu**, Quý khách chỉ cần trả trước khoảng **80 - 120 triệu** là có thể nhận xe ngay ạ!`;
  }

  // 4. ĐỊA CHỈ SHOWROOM & HOTLINE
  if (clean.includes('dia chi') || clean.includes('o dau') || clean.includes('showroom') || clean.includes('chi nhanh') || clean.includes('hotline') || clean.includes('gio mo cua') || clean.includes('so dien thoai') || clean.includes('lien he')) {
    return `📍 **HỆ THỐNG 3 MEGA SHOWROOM DUY TRƯƠNG MOTOR**

Mở cửa: **08:30 - 21:00** (Tất cả các ngày trong tuần, kể cả T7, CN & Lễ)

1. **Trụ sở chính TP.HCM:**
   • Địa chỉ: Số 123 Hoàng Văn Thụ, Phường 8, Q. Phú Nhuận, TP. HCM
   • Hotline VIP: **0903.456.789** - **0933.333.382**
2. **Chi nhánh Cầu Giấy (Hà Nội):**
   • Địa chỉ: Số 168 Trần Duy Hưng, Phường Trung Hòa, Cầu Giấy, Hà Nội
   • Hotline: **0901.234.567**
3. **Chi nhánh Hải Châu (Đà Nẵng):**
   • Địa chỉ: Số 52 Nguyễn Văn Linh, Phường Nam Dương, Hải Châu, Đà Nẵng
   • Hotline: **0902.999.888**

Showroom luôn có sẵn cà phê, phòng chờ VIP và khu lái thử cho anh em Biker ghé trải nghiệm!`;
  }

  // 5. PHÁP LÝ & GIẤY TỜ HQCN
  if (clean.includes('hqcn') || clean.includes('giay to') || clean.includes('sang ten') || clean.includes('bien so') || clean.includes('phap ly') || clean.includes('rut goc')) {
    return `📜 **CAM KẾT PHÁP LÝ 100% HẢI QUAN CHÍNH NGẠCH (HQCN)**

Duy Trương Motor nói **KHÔNG** với xe Nopp, GTHL, xe gian hay xe mẹ bồng con:
• **100% xe HQCN:** Bao sang tên bấm biển số toàn quốc trong vòng **24 - 48h**.
• **Bao rút gốc hồ sơ:** Thủ tục công chứng mua bán nhanh gọn, hợp lệ theo quy định pháp luật.
• **Bồi thường uy tín:** Cam kết hoàn tiền **200% giá trị hợp đồng** nếu xe có tranh chấp pháp lý hoặc không thể sang tên!`;
  }

  // 5.1. ĐỔI MÀU XE, SƠN XE, WRAP DECAL & LÀM CAVET MÀU MỚI
  if (clean.includes('doi mau') || clean.includes('mau sac') || clean.includes('wrap') || clean.includes('decal') || clean.includes('son lai') || clean.includes('phong thuy') || clean.includes('mau khac') || clean.includes('cavet mau') || clean.includes('son xe')) {
    return `🎨 **CHÍNH SÁCH ĐỔI MÀU XE, WRAP DECAL & LÀM CAVET MÀU MỚI TẠI DUY TRƯƠNG MOTOR**

Dạ Quý khách hoàn toàn an tâm! Duy Trương Motor hỗ trợ đổi màu xe 100% theo đúng ý muốn của Quý khách:
• **Kho màu hãng đa dạng:** Mỗi dòng xe đều hỗ trợ 3 - 5 tùy chọn màu sắc zin chính hãng (Quý khách bấm vào mục "Chi Tiết" để chọn màu ưng ý).
• **Dịch vụ Wrap Decal TPU Cao Cấp:** Showroom có phòng dán Wrap chuyên dụng TeckWrap / 3M Mỹ chống xước, tự phục hồi vết xước liti, giữ gìn 100% nước sơn zin nguyên bản.
• **Sơn đổi màu công nghệ lò hấp nhiệt:** Đạt tiêu chuẩn nước sơn siêu xe Ferrari, Porsche, Ducati, bảo hành lớp sơn 5 năm không bong tróc.
• **Bao trọn gói thủ tục đổi màu cavet xe:** Showroom đại diện làm thủ tục đổi cavet màu sơn mới tại CSGT trên toàn quốc chỉ trong **24h - 48h**, hợp pháp 100% khi lưu thông!

👉 Quý khách đang nhắm mẫu xe nào và muốn đổi sang tông màu gì để em tư vấn phối màu 3D chuẩn xác ạ?`;
  }

  // 5.2. THƯƠNG LƯỢNG GIÁ, BỚT LỘC & VOUCHER GIẢM GIÁ
  if (clean.includes('thuong luong') || clean.includes('bot gia') || clean.includes('giam gia') || clean.includes('tra gia') || clean.includes('fix gia') || clean.includes('bot loc') || clean.includes('voucher') || clean.includes('khuyen mai') || clean.includes('gia tot nhat')) {
    return `🏷️ **CHÍNH SÁCH THƯƠNG LƯỢNG GIÁ & BỚT LỘC SHOWROOM DUY TRƯƠNG MOTOR**

Showroom cam kết: *"MUA CỦA NGƯỜI CHÁN - BÁN CHO NGƯỜI CẦN - LỘC LÁ VUI VẺ ĐÔI BÊN"*:
• **Voucher Bớt Lộc Cấp Tốc:** Giảm ngay **15.000.000 đ** tiền mặt khi bấm cọc giữ xe!
• **Tài trợ 100% Lệ Phí Trước Bạ & Biển Số:** Tiết kiệm lên đến **25.000.000 đ - 35.000.000 đ**.
• **Tự đề xuất giá mong muốn:** Trên mỗi trang chi tiết xe, Quý khách chỉ cần bấm nút **[🤝 Bớt Lộc / Giảm Giá]**, nhập mức giá Quý khách muốn mua. Giám đốc Showroom tự động phê duyệt ngay nếu mức giảm trong biên độ 10%!
• **Trao đổi trực tiếp:** Quý khách có thể để lại số điện thoại hoặc gọi Hotline VIP Giám Đốc: **0901.234.567** để chốt mức giá tốt nhất thị trường!`;
  }

  // 6. CÀO CÀO / OFF-ROAD / ĐỊA HÌNH
  if (clean.includes('cao cao') || clean.includes('off road') || clean.includes('dia hinh') || clean.includes('enduro') || clean.includes('motocross') || clean.includes('supermoto')) {
    const dirtBikes = allProducts.filter(p => p.category === 'dirtbike' || p.name.includes('KTM') || p.name.includes('Husqvarna') || p.name.includes('CRF') || p.name.includes('KLX') || p.name.includes('DesertX') || p.name.includes('WR450F') || p.name.includes('SX 125'));
    
    let reply = `🏍️ **DANH SÁCH CHIẾN MÃ CÀO CÀO ĐỊA HÌNH SẴN HÀNG:**\n\n`;
    dirtBikes.forEach((b, idx) => {
      reply += `${idx + 1}. **${b.name}**\n   • Giá: **${formatMoney ? formatMoney(b.price) : b.price + 'đ'}** | Đời: ${b.year} | ODO: ${b.odo}\n   • Đặc điểm: ${b.engine} - ${b.description.substring(0, 75)}...\n\n`;
    });
    reply += `🔥 *Tất cả xe cào cào đều được kỹ thuật viên kiểm tra phuộc, nhông sên dĩa và canh chỉnh map động cơ hoàn hảo. Anh đang muốn dòng chuyên bay nhảy cào cào sa mạc hay phố Supermoto ạ?*`;
    return reply;
  }

  // 7. SIÊU MÔTÔ TRÊN 1 TỶ
  if (clean.includes('1 ty') || clean.includes('tren 1 ty') || clean.includes('sieu moto') || clean.includes('dat nhat') || clean.includes('vip nhat')) {
    const over1b = allProducts.filter(p => p.price >= 1000000000);
    let reply = `⚡ **BỘ SƯU TẬP SIÊU MÔTÔ ĐẲNG CẤP TRÊN 1 TỶ ĐỒNG:**\n\n`;
    over1b.forEach((b, idx) => {
      reply += `🏆 ${idx + 1}. **${b.name}**\n   • Giá niêm yết: **${formatMoney ? formatMoney(b.price) : b.price + 'đ'}**\n   • Thông số: ${b.engine} | ${b.power}\n   • Điểm đặc biệt: ${b.description.substring(0, 85)}...\n\n`;
    });
    reply += `💎 *Các siêu phẩm này đều trưng bày tại phòng kính VIP có điều hòa riêng. Quý khách có thể đặt cọc VNPAY ngay để được giữ xe số khung đẹp và tặng gói bọc sứ bảo vệ sơn Ceramic!*`;
    return reply;
  }

  // 8. TẦM GIÁ 300 - 500 TRIỆU
  if (clean.includes('300') || clean.includes('400') || clean.includes('500') || clean.includes('300 - 500') || clean.includes('300 den 500')) {
    const midRange = allProducts.filter(p => p.price >= 300000000 && p.price <= 500000000);
    let reply = `💰 **DANH SÁCH XE PHÂN KHÚC 300 - 500 TRIỆU CỰC HOT:**\n\n`;
    midRange.forEach((b, idx) => {
      reply += `⭐ ${idx + 1}. **${b.name}**\n   • Giá: **${formatMoney ? formatMoney(b.price) : b.price + 'đ'}** (${b.year})\n   • Dòng: ${b.category.toUpperCase()} | ${b.engine}\n\n`;
    });
    reply += `👉 *Phân khúc này cực kỳ được chuộng vì sức mạnh vượt trội và tính thanh khoản cao khi quay đầu đổi xe tại Duy Trương Motor!*`;
    return reply;
  }

  // 9. DƯỚI 200 TRIỆU
  if (clean.includes('duoi 200') || clean.includes('100') || clean.includes('re nhat') || clean.includes('it tien')) {
    const under200 = allProducts.filter(p => p.price < 250000000);
    let reply = `🎯 **CÁC DÒNG XE GIÁ MỀM DƯỚI 200 TRIỆU CHÍNH NGẠCH:**\n\n`;
    under200.forEach((b, idx) => {
      reply += `• **${b.name}** - Giá: **${formatMoney ? formatMoney(b.price) : b.price + 'đ'}**\n  (Đời ${b.year}, ${b.engine}, giấy tờ HQCN sang tên tức thì).\n\n`;
    });
    reply += `Rất thích hợp cho anh em mới làm quen môtô địa hình cào cào hoặc Supermoto dạo phố!`;
    return reply;
  }

  // 10. TÌM KIẾM THEO HÃNG CỤ THỂ
  if (clean.includes('ktm')) {
    const ktmBikes = allProducts.filter(p => p.name.toUpperCase().includes('KTM'));
    let reply = `🟠 **DÒNG XE KTM ÁO CAM SẴN TẠI SHOWROOM:**\n\n`;
    ktmBikes.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine})\n`;
    });
    return reply + `\nXe trang bị giảm xóc WP cao cấp và phụ tùng chính hãng nhập khẩu châu Âu!`;
  }

  if (clean.includes('ducati')) {
    const ducatiBikes = allProducts.filter(p => p.name.toUpperCase().includes('DUCATI'));
    let reply = `🔴 **SIÊU PHẨM DUCATI Ý TẠI DUY TRƯƠNG MOTOR:**\n\n`;
    ducatiBikes.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine} - ${b.power})\n`;
    });
    return reply + `\nĐầy đủ phiên bản Panigale V4 S, Streetfighter V4 S, DesertX Rally và Diavel V4!`;
  }

  if (clean.includes('bmw')) {
    const bmwBikes = allProducts.filter(p => p.name.toUpperCase().includes('BMW'));
    let reply = `⚪ **SIÊU XE BMW MOTORRAD CHÂU ÂU:**\n\n`;
    bmwBikes.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine})\n`;
    });
    return reply + `\nCó đủ Cá Mập S1000RR M-Package và Vua Phượt Địa Hình R1250GS Adventure!`;
  }

  if (clean.includes('kawasaki') || clean.includes('ninja') || clean.includes('h2') || clean.includes('z1000')) {
    const kawaBikes = allProducts.filter(p => p.name.toUpperCase().includes('KAWASAKI') || p.name.includes('H2') || p.name.includes('Z1000'));
    let reply = `🟢 **ĐỘI HÌNH CHIẾN BINH KAWASAKI:**\n\n`;
    kawaBikes.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine})\n`;
    });
    return reply + `\nĐặc biệt có siêu quái vật Ninja H2 Siêu Nạp 231 mã lực và Z1000R Edition huyền thoại!`;
  }

  if (clean.includes('yamaha') || clean.includes('r1') || clean.includes('r1m') || clean.includes('tmax') || clean.includes('wr450')) {
    const yamahaBikes = allProducts.filter(p => p.name.toUpperCase().includes('YAMAHA') || p.name.includes('R1M'));
    let reply = `🔵 **CHIẾN MÃ YAMAHA RACING CHÍNH HÃNG:**\n\n`;
    yamahaBikes.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine})\n`;
    });
    return reply + `\nTừ cào cào Dakar Rally WR450F, Vua Ga Lớn TMAX 560 đến cỗ máy MotoGP YZF-R1M Carbon!`;
  }

  if (clean.includes('honda') || clean.includes('cbr') || clean.includes('africa') || clean.includes('crf')) {
    const hondaBikes = allProducts.filter(p => p.name.toUpperCase().includes('HONDA') || p.name.includes('CBR') || p.name.includes('Africa'));
    let reply = `🔴 **DÒNG XE HONDA CÔNG NGHỆ CAO:**\n\n`;
    hondaBikes.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine})\n`;
    });
    return reply + `\nĐầy đủ CBR1000RR-R Fireblade SP 217 HP, Africa Twin 1100cc và CRF450R!`;
  }

  if (clean.includes('harley') || clean.includes('rocket') || clean.includes('triumph') || clean.includes('cruiser') || clean.includes('diavel')) {
    const cruisers = allProducts.filter(p => p.category === 'cruiser' || p.name.includes('Harley') || p.name.includes('Rocket') || p.name.includes('Diavel'));
    let reply = `🦅 **CƠ BẮP CRUISER & MUSCLE BIKE ĐỈNH CAO:**\n\n`;
    cruisers.forEach(b => {
      reply += `• **${b.name}** - ${formatMoney ? formatMoney(b.price) : b.price + 'đ'} (${b.engine})\n`;
    });
    return reply + `\nÂm thanh uy lực, tư thế lái bệ vệ phong trần cho anh em thích đi tour xa!`;
  }

  // 11. BẢO HÀNH & BẢO DƯỠNG
  if (clean.includes('bao hanh') || clean.includes('bao duong') || clean.includes('nhot') || clean.includes('kiem tra xe')) {
    return `🔧 **CHẾ ĐỘ BẢO HÀNH & HẬU MÃI BIKER CHU ĐÁO**

• **Bảo hành toàn diện:** 2 - 3 năm hoặc 30.000 km cho toàn bộ động cơ & ECU.
• **Gói quà tặng khi mua xe:**
  - Miễn phí thay nhớt **Motul 300V Factory Line** lần đầu.
  - Tặng full bộ giáp bảo hộ hoặc mũ bảo hiểm fullface carbon cao cấp.
  - Vệ sinh, căn chỉnh nhông sên dĩa định kỳ miễn phí trọn đời tại showroom.
• **Cứu hộ tận nơi 24/7:** Gặp sự cố trên đường tour, chỉ cần gọi hotline **0903.456.789**, xe cứu hộ có mặt ứng cứu ngay!`;
  }

  // 12. CHÀO HỎI / GREETINGS
  if (clean.includes('chao') || clean.includes('alo') || clean.includes('hello') || clean.includes('hi') || clean.includes('ad') || clean.includes('shop') || clean.includes('admin') || clean.includes('oi')) {
    return `Chào anh/chị Biker! Em là Chuyên Viên Tư Vấn Showroom **DUY TRƯƠNG MOTOR**! 🏍️

Tại hệ thống của chúng em:
👉 *"Ở đây, chúng tôi MUA CỦA NGƯỜI CHÁN - & BÁN CHO NGƯỜI CẦN."*

Hiện tại showroom đang có **${allProducts.length} mẫu môtô PKL & cào cào địa hình** tuyển chọn cực đẹp. Anh đang quan tâm dòng xe nào, phân khúc giá bao nhiêu, hay muốn đổi xe cũ nâng đời thì bảo em ngay nhé!`;
  }

  // 13. CẢM ƠN
  if (clean.includes('cam on') || clean.includes('thank') || clean.includes('ok') || clean.includes('duoc roi')) {
    return `Dạ không có gì anh ơi! Rất hân hạnh được hỗ trợ anh. Anh có thể bấm cọc xe online trên website hoặc ghé trực tiếp showroom để nổ máy chạy thử nhé! Chúc anh luôn vững tay lái trên mọi cung đường! 🏁`;
  }

  // DEFAULT SMART RESPONSE
  return `Dạ em đã nhận được câu hỏi của anh: "${text}".

Hiện tại DUY TRƯƠNG MOTOR có đầy đủ các dòng:
• **Xe Cào Cào Địa Hình / Supermoto:** KTM EXC, Husqvarna, Aprilia SX, Yamaha WR450F, Ducati DesertX...
• **Siêu Môtô Superbike:** Ninja H2 Carbon, Panigale V4 S, BMW S1000RR M, Yamaha R1M, Fireblade SP...
• **Hypernaked & Cruiser Cơ Bắp:** Z1000R, Super Duke R, Streetfighter V4, Triumph Rocket 3, Fat Boy...

Anh có thể click nhanh vào các gợi ý bên dưới hoặc để lại số điện thoại/Zalo, em sẽ gửi hình ảnh chi tiết và video nổ máy cho anh tham khảo ngay ạ! Hotline hỗ trợ 24/7: **0903.456.789**`;
}
