import React, { useState, useMemo } from 'react';
import confetti from 'canvas-confetti';
import { 
  SHOWROOMS, 
  CATEGORIES, 
  MEGA_MENU_DATA,
  INITIAL_PRODUCTS, 
  INITIAL_REVIEWS, 
  DEMO_USERS 
} from './data/mockData';
import { generateSmartBotReply, SUGGESTION_CHIPS } from './utils/chatbotEngine';

export default function App() {
  // --- STATE LAYER ---
  const [currentRoleKey, setCurrentRoleKey] = useState('guest');
  const [currentTab, setCurrentTab] = useState('home'); // home, products, showrooms, orders, vendor, admin, shipper
  const [selectedShowroomFilter, setSelectedShowroomFilter] = useState('all');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [selectedMegaKey, setSelectedMegaKey] = useState('oto'); // 'motor', 'oto', 'xenho', 'phukien'
  const [searchQuery, setSearchQuery] = useState('');
  const [brandFilter, setBrandFilter] = useState('all');
  const [priceRange, setPriceRange] = useState({ min: 0, max: 30000000000 });

  // Data Collections
  const [products, setProducts] = useState(INITIAL_PRODUCTS);
  const [reviews, setReviews] = useState(INITIAL_REVIEWS);
  const [cart, setCart] = useState([
    { product: INITIAL_PRODUCTS[0], count: 1 } // Default 1 KTM 300 EXC TPI in cart
  ]);
  const [orders, setOrders] = useState([
    {
      id: "DTM-9824A1B2",
      customerName: "Hoàng Biker",
      phone: "0905.678.901",
      address: "123 Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM",
      productName: "KTM 300 EXC TPI Factory Edition 2024",
      productImage: INITIAL_PRODUCTS[0].image,
      storeName: "Showroom Phú Nhuận TP.HCM",
      deliveryName: "Giao xe cào cào sàn trượt thủy lực VIP tận nhà",
      totalAmount: 369500000,
      paymentMethod: "VNPAY",
      isPaid: true,
      status: "CONFIRMED", // NOT_PROCESSED, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
      date: "25/10/2026 14:30"
    }
  ]);

  // Modal States
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const [isVnPayModalOpen, setIsVnPayModalOpen] = useState(false);
  const [activeProductDetail, setActiveProductDetail] = useState(null);
  const [isAddBikeModalOpen, setIsAddBikeModalOpen] = useState(false);
  const [isChatOpen, setIsChatOpen] = useState(false);

  // New Bike Form for Vendor
  const [newBike, setNewBike] = useState({
    name: '',
    brand: 'KTM',
    category: 'dirtbike',
    price: 300000000,
    engine: '',
    power: '',
    color: '',
    image: '/images/ktm_300_exc.jpg',
    description: '',
    badges: ['HQCN', 'Bao Quay Đầu']
  });

  // Review Form in Detail Modal (Yêu cầu văn bản >= 50 ký tự)
  const [reviewText, setReviewText] = useState('');
  const [reviewStars, setReviewStars] = useState(5);
  const [reviewMedia, setReviewMedia] = useState('');

  // Checkout Form
  const [checkoutData, setCheckoutData] = useState({
    recipientName: "Hoàng Biker",
    phone: "0905.678.901",
    address: "Số 123 Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM",
    deliveryFee: 1500000,
    deliveryName: "Giao xe sàn trượt thủy lực VIP (Chống trầy 100%)",
    paymentMethod: "VNPAY",
    note: "Vui lòng bọc bạt nhung bàn giao xe tại nhà riêng, hỗ trợ thủ tục sang tên bấm biển số."
  });

  // Chat Widget State (WebSocket Simulation & Smart AI Engine)
  const [chatMessages, setChatMessages] = useState([
    { 
      sender: "Chuyên Viên Showroom", 
      text: "Kính chào Quý khách đến với DUY TRƯƠNG MOTOR! Chúng tôi chuyên mua bán môtô PKL & xe cào cào địa hình: MUA CỦA NGƯỜI CHÁN - BÁN CHO NGƯỜI CẦN. Quý khách đang quan tâm dòng xe nào hay muốn đổi xe, thu mua xe cũ ạ?", 
      time: "15:00", 
      isBot: true 
    }
  ]);
  const [chatInput, setChatInput] = useState('');
  const [isBotTyping, setIsBotTyping] = useState(false);

  const currentUser = DEMO_USERS[currentRoleKey];

  // Format VND Currency
  const formatMoney = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

  // --- DERIVED / FILTERED PRODUCTS ---
  // 1. Guest Requirement: Top sản phẩm bán trên 10 sản phẩm (sắp xếp giảm dần theo lượt bán)
  const topOver10Sold = useMemo(() => {
    return products
      .filter(p => p.sold > 10 && p.isSelling)
      .sort((a, b) => b.sold - a.sold);
  }, [products]);

  // Distinct Brand List for Dynamic Select
  const allBrandList = useMemo(() => {
    const brandMap = new Map();
    products.forEach(p => {
      const bUpper = (p.brand || '').trim().toUpperCase();
      if (bUpper) {
        brandMap.set(bUpper, p.brand);
      }
    });
    return Array.from(brandMap.keys()).sort();
  }, [products]);

  // Filtered Products for Catalog & User tabs
  const filteredProducts = useMemo(() => {
    const normalize = (str) => (str || '').toLowerCase().replace(/[^a-z0-9]/g, '');
    return products.filter(p => {
      const matchStore = selectedShowroomFilter === 'all' || p.storeId === parseInt(selectedShowroomFilter);
      const matchCategory = selectedCategory === 'all' || 
                            (selectedCategory === 'all-motor' ? (p.category !== 'oto' && p.category !== 'xenho' && p.category !== 'gear-parts') : p.category === selectedCategory);
      
      const pNorm = normalize(p.brand);
      const bNorm = normalize(brandFilter);
      const matchBrand = brandFilter === 'all' || 
                         pNorm === bNorm ||
                         pNorm.includes(bNorm) ||
                         bNorm.includes(pNorm);

      const matchPrice = (p.promotionalPrice || p.price) >= priceRange.min && (p.promotionalPrice || p.price) <= priceRange.max;
      const matchSearch = p.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          p.brand.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          p.engine.toLowerCase().includes(searchQuery.toLowerCase());
      return matchStore && matchCategory && matchBrand && matchPrice && matchSearch;
    });
  }, [products, selectedShowroomFilter, selectedCategory, brandFilter, priceRange, searchQuery]);

  // Cart Total Calculation
  const cartSubtotal = useMemo(() => {
    return cart.reduce((sum, item) => sum + (item.product.promotionalPrice || item.product.price) * item.count, 0);
  }, [cart]);

  // --- ACTIONS ---
  const handleAddToCart = (product, count = 1) => {
    setCart(prev => {
      const existing = prev.find(item => item.product.id === product.id);
      if (existing) {
        return prev.map(item => item.product.id === product.id ? { ...item, count: item.count + count } : item);
      }
      return [...prev, { product, count }];
    });
    setIsCartOpen(true);
  };

  const handleUpdateCartCount = (productId, delta) => {
    setCart(prev => prev.map(item => {
      if (item.product.id === productId) {
        const newCount = item.count + delta;
        return newCount > 0 ? { ...item, count: newCount } : null;
      }
      return item;
    }).filter(Boolean));
  };

  const handleRemoveFromCart = (productId) => {
    setCart(prev => prev.filter(item => item.product.id !== productId));
  };

  // Submit Review (enforcing >= 50 characters)
  const handleSubmitReview = (e) => {
    e.preventDefault();
    if (reviewText.trim().length < 50) {
      alert("Nội dung đánh giá xe phải đạt tối thiểu 50 ký tự để cung cấp đánh giá chất lượng!");
      return;
    }
    const newRev = {
      id: Date.now(),
      productId: activeProductDetail.id,
      userName: currentUser.name || "Khách hàng VIP",
      userAvatar: currentUser.avatar,
      stars: reviewStars,
      date: "Hôm nay",
      content: reviewText.trim(),
      mediaUrl: reviewMedia || null
    };
    setReviews([newRev, ...reviews]);
    setReviewText('');
    setReviewMedia('');
    alert("Cảm ơn bạn đã gửi đánh giá trải nghiệm thực tế về dòng xe này tại Duy Trương Motor!");
  };

  // Submit Order & Trigger VNPAY
  const handlePlaceOrder = () => {
    if (cart.length === 0) return;

    if (checkoutData.paymentMethod === "VNPAY") {
      setIsCheckoutOpen(false);
      setIsVnPayModalOpen(true);
    } else {
      finalizeOrder(false);
    }
  };

  const finalizeOrder = (isPaid = false) => {
    const newOrder = {
      id: "DTM-" + Math.random().toString(36).substr(2, 8).toUpperCase(),
      customerName: checkoutData.recipientName,
      phone: checkoutData.phone,
      address: checkoutData.address,
      productName: cart[0].product.name,
      productImage: cart[0].product.image,
      storeName: cart[0].product.storeName,
      deliveryName: checkoutData.deliveryName,
      totalAmount: cartSubtotal + checkoutData.deliveryFee,
      paymentMethod: checkoutData.paymentMethod,
      isPaid: isPaid,
      status: isPaid ? "CONFIRMED" : "NOT_PROCESSED",
      date: new Date().toLocaleString('vi-VN')
    };

    setOrders([newOrder, ...orders]);
    setCart([]);
    setIsCheckoutOpen(false);
    setIsVnPayModalOpen(false);
    confetti({ particleCount: 120, spread: 70, origin: { y: 0.6 } });
    alert("Chúc mừng Quý khách! Đơn hàng đặt xe đã được tạo thành công trên hệ thống Duy Trương Motor!");
    setCurrentTab('orders');
  };

  // Chat message send (Smart AI CSKH Engine)
  const handleSendMessage = (overrideQuery) => {
    const textToSend = typeof overrideQuery === 'string' ? overrideQuery : chatInput;
    if (!textToSend || !textToSend.trim()) return;

    const userMsg = { 
      sender: currentUser.name || "Khách Hàng", 
      text: textToSend.trim(), 
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }), 
      isBot: false 
    };
    setChatMessages(prev => [...prev, userMsg]);
    if (typeof overrideQuery !== 'string') {
      setChatInput('');
    }
    setIsBotTyping(true);

    setTimeout(() => {
      const reply = generateSmartBotReply(textToSend, products, formatMoney);
      const botMsg = {
        sender: "Chuyên Viên Duy Trương Motor",
        text: reply,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        isBot: true
      };
      setChatMessages(prev => [...prev, botMsg]);
      setIsBotTyping(false);
    }, 650);
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>

      {/* 1. TOP SLOGAN & HOTLINE BAR (Chuẩn phong cách Vương Khang Motor) */}
      <div className="top-slogan-bar">
        <div className="slogan-highlight">
          <i className="fa-solid fa-quote-left text-gold"></i>
          <span>Ở đây, chúng tôi <strong>MUA CỦA NGƯỜI CHÁN</strong> - & <strong>BÁN CHO NGƯỜI CẦN</strong>. Uy tín tạo nên thương hiệu.</span>
        </div>
        <div className="top-contact-info">
          <span><i className="fa-solid fa-phone text-gold me-1"></i> Hotline: <a href="tel:0903456789">0903.456.789</a> - <a href="tel:0933333382">0933.333.382</a></span>
          <span><i className="fa-solid fa-clock text-gold me-1"></i> Giờ mở cửa: 08:30 - 21:00 (Cả T7 & CN)</span>
        </div>
      </div>

      {/* 2. TOP ROLE SWITCHER HUD (Cho Giảng viên & Nhóm 3 thành viên kiểm thử) */}
      <div className="role-hud">
        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
          <span style={{ color: 'var(--gold-primary)', fontWeight: 800 }}>
            <i className="fa-solid fa-code-compare me-1"></i> ROLE SWITCHER HUD:
          </span>
          <div className="role-chips">
            <button className={`role-chip ${currentRoleKey === 'guest' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('guest'); setCurrentTab('home'); }}>
              <i className="fa-solid fa-user-secret me-1"></i> 1. Guest (Khách)
            </button>
            <button className={`role-chip ${currentRoleKey === 'customer' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('customer'); setCurrentTab('home'); }}>
              <i className="fa-solid fa-user me-1"></i> 2. User (Hoàng Biker)
            </button>
            <button className={`role-chip ${currentRoleKey === 'vendor' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('vendor'); setCurrentTab('vendor'); }}>
              <i className="fa-solid fa-store me-1"></i> 3. Vendor (Showroom Phú Nhuận)
            </button>
            <button className={`role-chip ${currentRoleKey === 'admin' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('admin'); setCurrentTab('admin'); }}>
              <i className="fa-solid fa-shield-halved me-1"></i> 4. Admin (Duy Trương)
            </button>
            <button className={`role-chip ${currentRoleKey === 'shipper' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('shipper'); setCurrentTab('shipper'); }}>
              <i className="fa-solid fa-truck-ramp-box me-1"></i> 5. Shipper (Tài Xế Sàn Trượt)
            </button>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '20px', color: 'var(--text-sub)' }}>
          <div>
            <i className="fa-solid fa-location-dot text-gold me-1"></i> Khu vực:
            <select 
              value={selectedShowroomFilter} 
              onChange={(e) => setSelectedShowroomFilter(e.target.value)}
              style={{ background: 'transparent', color: '#fff', border: 'none', marginLeft: '5px', fontWeight: 'bold', cursor: 'pointer' }}
            >
              <option value="all" style={{ background: '#0e111a' }}>Toàn Quốc (3 Showroom)</option>
              {SHOWROOMS.map(s => (
                <option key={s.id} value={s.id} style={{ background: '#0e111a' }}>{s.name}</option>
              ))}
            </select>
          </div>
          {currentUser.points && (
            <span style={{ color: 'var(--gold-light)' }}><i className="fa-solid fa-gem me-1"></i> {currentUser.points} pts</span>
          )}
        </div>
      </div>

      {/* 3. MAIN APP HEADER */}
      <header className="app-header">
        <div className="logo-badge" onClick={() => setCurrentTab('home')}>
          <div className="logo-icon-box">
            <i className="fa-solid fa-motorcycle"></i>
          </div>
          <div>
            <div className="brand-title" style={{ fontSize: '1.45rem', lineHeight: 1.1 }}>
              <span className="text-gradient-gold">DUY TRƯƠNG</span> <span style={{ color: '#fff' }}>MOTOR</span>
            </div>
            <div style={{ fontSize: '0.62rem', color: 'var(--gold-primary)', letterSpacing: '2px', fontWeight: 800 }}>
              SIÊU THỊ MÔ TÔ PKL & SIÊU XE UY TÍN
            </div>
          </div>
        </div>

        {/* Center Golden Shield Logo (Chuẩn phong cách Vương Khang Motor) */}
        <div className="shield-logo-center" onClick={() => setCurrentTab('home')}>
          <div className="shield-badge-box">
            <div className="shield-badge-title">DUY TRƯƠNG</div>
            <div className="shield-badge-sub">- MOTOR -</div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <button className={`btn-subtle ${currentTab === 'home' ? 'active' : ''}`} onClick={() => setCurrentTab('home')}>
            <i className="fa-solid fa-house"></i> Trang Chủ
          </button>
          <button className={`btn-subtle ${currentTab === 'products' ? 'active' : ''}`} onClick={() => { setSelectedCategory('all'); setBrandFilter('all'); setCurrentTab('products'); }}>
            <i className="fa-solid fa-motorcycle"></i> Kho Xe ({products.length})
          </button>
          <button className={`btn-subtle ${currentTab === 'showrooms' ? 'active' : ''}`} onClick={() => setCurrentTab('showrooms')}>
            <i className="fa-solid fa-store"></i> Chuỗi Showroom
          </button>
          <button className={`btn-subtle ${currentTab === 'orders' ? 'active' : ''}`} onClick={() => setCurrentTab('orders')}>
            <i className="fa-solid fa-receipt"></i> Lịch Sử Đơn ({orders.length})
          </button>

          {/* Conditional Role Portals */}
          {currentRoleKey === 'vendor' && (
            <button className={`btn-subtle ${currentTab === 'vendor' ? 'active' : ''}`} style={{ borderColor: 'var(--gold-primary)', color: 'var(--gold-primary)' }} onClick={() => setCurrentTab('vendor')}>
              <i className="fa-solid fa-chart-line"></i> Quản Lý Showroom
            </button>
          )}
          {currentRoleKey === 'admin' && (
            <button className={`btn-subtle ${currentTab === 'admin' ? 'active' : ''}`} style={{ borderColor: 'var(--red-racing)', color: 'var(--red-racing)' }} onClick={() => setCurrentTab('admin')}>
              <i className="fa-solid fa-shield-halved"></i> Quản Trị Sàn
            </button>
          )}
          {currentRoleKey === 'shipper' && (
            <button className={`btn-subtle ${currentTab === 'shipper' ? 'active' : ''}`} style={{ borderColor: 'var(--cyan-neon)', color: 'var(--cyan-neon)' }} onClick={() => setCurrentTab('shipper')}>
              <i className="fa-solid fa-truck-fast"></i> Phân Hệ Giao Xe
            </button>
          )}
        </nav>

        {/* Right Actions: Search Pill, Cart & User status */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          {/* Vuong Khang Style Search Pill */}
          <div className="search-pill-btn" onClick={() => { setSelectedCategory('all'); setBrandFilter('all'); setCurrentTab('products'); }}>
            <i className="fa-solid fa-magnifying-glass text-gold"></i>
            <span>Các dòng xe khác...</span>
            <i className="fa-solid fa-chevron-right text-gold" style={{ fontSize: '0.72rem' }}></i>
          </div>

          <button className="btn-outline-gold" style={{ padding: '8px 16px' }} onClick={() => setIsCartOpen(true)}>
            <i className="fa-solid fa-cart-shopping"></i> Giỏ ({cart.length})
          </button>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <img src={currentUser.avatar} alt="Avatar" style={{ width: '36px', height: '36px', borderRadius: '50%', border: '2px solid var(--gold-primary)', objectFit: 'cover' }} />
            <div style={{ fontSize: '0.82rem' }}>
              <div style={{ fontWeight: 700, color: '#fff' }}>{currentUser.name}</div>
              <span style={{ color: 'var(--gold-primary)', fontSize: '0.68rem', fontWeight: 800 }}>[{currentUser.role}]</span>
            </div>
          </div>
        </div>
      </header>

      {/* 4. COMMITMENT BAR: 5 CAM KẾT VÀNG NHƯ VƯƠNG KHANG MOTOR */}
      <div className="commitment-bar">
        <div className="commitment-grid">
          <div className="commitment-item">
            <div className="commitment-icon"><i className="fa-solid fa-shield-halved"></i></div>
            <div className="commitment-text">
              <h4>HẢI QUAN CHÍNH NGẠCH</h4>
              <p>100% Bao sang tên toàn quốc, bao rút gốc 24h</p>
            </div>
          </div>
          <div className="commitment-item">
            <div className="commitment-icon"><i className="fa-solid fa-rotate"></i></div>
            <div className="commitment-text">
              <h4>BAO QUAY ĐẦU - ĐỔI XE</h4>
              <p>Thu mua lại xe với mức khấu hao thấp nhất</p>
            </div>
          </div>
          <div className="commitment-item">
            <div className="commitment-icon"><i className="fa-solid fa-credit-card"></i></div>
            <div className="commitment-text">
              <h4>HỖ TRỢ TRẢ GÓP 70%</h4>
              <p>Trả trước từ 20-30%, duyệt hồ sơ nhận xe ngay</p>
            </div>
          </div>
          <div className="commitment-item">
            <div className="commitment-icon"><i className="fa-solid fa-certificate"></i></div>
            <div className="commitment-text">
              <h4>BAO TEST HÃNG TOÀN QUỐC</h4>
              <p>Cam kết máy zin, không tai nạn đâm đụng</p>
            </div>
          </div>
          <div className="commitment-item">
            <div className="commitment-icon"><i className="fa-solid fa-truck-fast"></i></div>
            <div className="commitment-text">
              <h4>GIAO XE SÀN TRƯỢT VIP</h4>
              <p>Bàn giao tận nhà an toàn 63 tỉnh thành</p>
            </div>
          </div>
        </div>
      </div>

      {/* 5. MAIN BODY */}
      <main style={{ flexGrow: 1 }}>

        {/* ================= TAB: HOME ================= */}
        {currentTab === 'home' && (
          <div>
            {/* VƯƠNG KHANG MOTOR MEGA HERO & SELECTION INTERFACE */}
            <section className="vk-mega-hero">
              {/* Giant Watermark Text & Tag */}
              <div className="vk-watermark-text">MÊ XE MÀ CHÊ XE</div>
              <div className="vk-channel-tag"><i className="fa-brands fa-youtube me-1"></i> DT CHANNEL</div>

              {/* Left Sidebar Menu */}
              <div className="vk-sidebar-menu">
                <div className="vk-menu-header">
                  <span>MENU</span>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '3px', width: '20px' }}>
                    <div style={{ height: '3px', background: '#ffd700', borderRadius: '2px' }}></div>
                    <div style={{ height: '3px', background: '#ffd700', borderRadius: '2px' }}></div>
                    <div style={{ height: '3px', background: '#ffd700', borderRadius: '2px' }}></div>
                  </div>
                </div>

                <button 
                  className={`vk-menu-item ${selectedMegaKey === 'motor' ? 'active' : ''}`}
                  onMouseEnter={() => setSelectedMegaKey('motor')}
                  onClick={() => { setSelectedMegaKey('motor'); setSelectedCategory('all-motor'); setBrandFilter('all'); setCurrentTab('products'); }}
                >
                  {selectedMegaKey === 'motor' && <span style={{ color: '#ffd700' }}>–</span>} MOTOR
                </button>

                <button 
                  className={`vk-menu-item ${selectedMegaKey === 'oto' ? 'active' : ''}`}
                  onMouseEnter={() => setSelectedMegaKey('oto')}
                  onClick={() => { setSelectedMegaKey('oto'); setSelectedCategory('oto'); setBrandFilter('all'); setCurrentTab('products'); }}
                >
                  {selectedMegaKey === 'oto' && <span style={{ color: '#ffd700' }}>–</span>} Ô TÔ
                </button>

                <button 
                  className={`vk-menu-item ${selectedMegaKey === 'xenho' ? 'active' : ''}`}
                  onMouseEnter={() => setSelectedMegaKey('xenho')}
                  onClick={() => { setSelectedMegaKey('xenho'); setSelectedCategory('xenho'); setBrandFilter('all'); setCurrentTab('products'); }}
                >
                  {selectedMegaKey === 'xenho' && <span style={{ color: '#ffd700' }}>–</span>} XE NHỎ
                </button>

                <button 
                  className={`vk-menu-item ${selectedMegaKey === 'phukien' ? 'active' : ''}`}
                  onMouseEnter={() => setSelectedMegaKey('phukien')}
                  onClick={() => { setSelectedMegaKey('phukien'); setSelectedCategory('gear-parts'); setBrandFilter('all'); setCurrentTab('products'); }}
                >
                  {selectedMegaKey === 'phukien' && <span style={{ color: '#ffd700' }}>–</span>} PHỤ KIỆN
                </button>

                <button className="vk-menu-item" onClick={() => alert("Chức năng BOOKING: Quý khách vui lòng gọi Hotline 0903.456.789 để đặt lịch lái thử siêu xe và thẩm định xe tại nhà!")}>
                  BOOKING
                </button>

                <button className="vk-menu-item" onClick={() => alert("DỊCH VỤ DUY TRƯƠNG MOTOR:\n1. Cứu hộ giao xe sàn trượt VIP 24/7\n2. Căn chỉnh Dynojet & Bảo dưỡng siêu xe\n3. Hỗ trợ rút hồ sơ gốc sang tên HQCN trong 24h")}>
                  DỊCH VỤ
                </button>

                <button className="vk-menu-item" onClick={() => alert("BLOG DUY TRƯƠNG MOTOR:\nCập nhật tin tức thị trường siêu xe PKL, xe cào cào và siêu xe thể thao mới nhất tại Việt Nam.")}>
                  BLOG
                </button>

                <button className="vk-menu-item" onClick={() => setCurrentTab('showrooms')}>
                  LIÊN HỆ
                </button>
              </div>

              {/* Frosted Glass Floating Panel displaying Brands */}
              <div className="vk-frosted-panel">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px', borderBottom: '1px solid rgba(0,0,0,0.08)', paddingBottom: '12px' }}>
                  <div style={{ fontSize: '1.25rem', fontWeight: 900, color: '#0f172a', fontFamily: 'var(--font-brand)' }}>
                    <i className="fa-solid fa-gem text-gold me-2"></i> BỘ SƯU TẬP {MEGA_MENU_DATA[selectedMegaKey]?.title || "XE"}
                  </div>
                  <button 
                    className="btn-gold" 
                    style={{ padding: '6px 16px', fontSize: '0.85rem' }}
                    onClick={() => {
                      setSelectedCategory(MEGA_MENU_DATA[selectedMegaKey]?.categoryKey || 'all');
                      setBrandFilter('all');
                      setCurrentTab('products');
                    }}
                  >
                    Xem Toàn Bộ Kho Xe <i className="fa-solid fa-arrow-right ms-1"></i>
                  </button>
                </div>

                <div className={`vk-brands-grid ${selectedMegaKey === 'oto' ? 'cols-4' : selectedMegaKey === 'xenho' ? 'cols-3' : 'cols-2'}`}>
                  {MEGA_MENU_DATA[selectedMegaKey]?.columns.map((col, colIdx) => (
                    <div key={colIdx} style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                      {col.map((bName, bIdx) => (
                        <div 
                          key={bIdx} 
                          className="vk-brand-link"
                          onClick={() => {
                            setBrandFilter(bName);
                            if (selectedMegaKey === 'oto') setSelectedCategory('oto');
                            else if (selectedMegaKey === 'xenho') setSelectedCategory('xenho');
                            else if (selectedMegaKey === 'motor') setSelectedCategory('all-motor');
                            else if (selectedMegaKey === 'phukien') setSelectedCategory('gear-parts');
                            setCurrentTab('products');
                          }}
                        >
                          {bName}
                        </div>
                      ))}
                    </div>
                  ))}
                </div>
              </div>

              {/* Bottom Center Social Pill */}
              <div className="vk-social-pill">
                <a href="https://youtube.com" target="_blank" rel="noreferrer" className="vk-social-icon" title="YouTube"><i className="fa-brands fa-youtube"></i></a>
                <a href="https://tiktok.com" target="_blank" rel="noreferrer" className="vk-social-icon" title="TikTok"><i className="fa-brands fa-tiktok"></i></a>
                <a href="https://facebook.com" target="_blank" rel="noreferrer" className="vk-social-icon" title="Facebook"><i className="fa-brands fa-facebook-f"></i></a>
              </div>
            </section>
            {/* Hero Section */}
            <section style={{ padding: '70px 30px', position: 'relative', overflow: 'hidden' }}>
              <div style={{ maxWidth: '1240px', margin: '0 auto', display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '50px', alignItems: 'center' }}>
                <div>
                  <div style={{ display: 'inline-block', padding: '6px 18px', borderRadius: '20px', background: 'rgba(212,175,55,0.12)', border: '1px solid var(--gold-primary)', color: 'var(--gold-light)', fontSize: '0.85rem', fontWeight: 800, marginBottom: '20px', letterSpacing: '1px' }}>
                    <i className="fa-solid fa-crown me-2"></i> SIÊU THỊ MÔ TÔ PHÂN KHỐI LỚN &amp; XE CÀO CÀO UY TÍN
                  </div>
                  <h1 className="brand-title" style={{ fontSize: 'clamp(2.1rem, 3.6vw, 3.1rem)', lineHeight: 1.25, marginBottom: '22px' }}>
                    DUY TRƯƠNG MOTOR <br />
                    <span className="text-gradient-gold" style={{ display: 'inline-block', whiteSpace: 'nowrap' }}>MÔ TÔ PKL &amp; XE CÀO CÀO</span>
                  </h1>
                  <p style={{ color: 'var(--text-sub)', fontSize: '1.05rem', lineHeight: 1.7, marginBottom: '30px', maxWidth: '620px' }}>
                    Hệ thống chuỗi showroom <strong>Duy Trương Motor</strong> chuyên kinh doanh, thu mua trao đổi siêu môtô PKL và các dòng xe cào cào địa hình đỉnh cao: KTM 300 EXC, Husqvarna FE 350, Honda CRF450R, Kawasaki KLX300, Ducati Panigale, Ninja H2, BMW S1000RR. Hải quan chính ngạch (HQCN), bao test hãng, bao quay đầu, giao xe sàn trượt VIP toàn quốc.
                  </p>

                  <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
                    <button className="btn-gold" style={{ fontSize: '1rem', padding: '14px 32px' }} onClick={() => setCurrentTab('products')}>
                      <i className="fa-solid fa-motorcycle"></i> Khám Phá Kho Xe ({products.length})
                    </button>
                    <button className="btn-racing" style={{ fontSize: '1rem', padding: '14px 28px' }} onClick={() => { setSelectedCategory('dirtbike'); setCurrentTab('products'); }}>
                      <i className="fa-solid fa-mountain"></i> Xem Xe Cào Cào Địa Hình
                    </button>
                    <button className="btn-outline-gold" style={{ fontSize: '1rem', padding: '14px 24px' }} onClick={() => setIsChatOpen(true)}>
                      <i className="fa-solid fa-phone"></i> Định Giá Thu Mua Xe Cũ
                    </button>
                  </div>

                  {/* Trust Numbers */}
                  <div style={{ display: 'flex', gap: '40px', marginTop: '40px', paddingTop: '24px', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
                    <div>
                      <div className="font-racing text-gold" style={{ fontSize: '2.2rem', fontWeight: 900 }}>100%</div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>HQCN Bao Sang Tên</div>
                    </div>
                    <div>
                      <div className="font-racing text-gold" style={{ fontSize: '2.2rem', fontWeight: 900 }}>03</div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Showroom 3 Miền</div>
                    </div>
                    <div>
                      <div className="font-racing text-gold" style={{ fontSize: '2.2rem', fontWeight: 900 }}>VNPAY</div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Đặt Cọc Online 24/7</div>
                    </div>
                  </div>
                </div>

                {/* Hero Showcase Bike */}
                <div style={{ position: 'relative', textAlign: 'center' }}>
                  <div style={{ position: 'absolute', width: '380px', height: '380px', background: 'radial-gradient(circle, rgba(255,102,0,0.2) 0%, transparent 70%)', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', borderRadius: '50%' }}></div>
                  <img 
                    src={INITIAL_PRODUCTS[0].image} 
                    alt="KTM 300 EXC TPI" 
                    style={{ width: '100%', maxHeight: '420px', objectFit: 'cover', borderRadius: '24px', border: '2px solid var(--gold-primary)', boxShadow: 'var(--shadow-deep)' }} 
                  />
                  <div className="glass-box" style={{ position: 'absolute', bottom: '15px', left: '15px', right: '15px', padding: '16px', textAlign: 'left', borderRadius: '16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span className="badge-tag-orange">VUA CÀO CÀO ENDURO 2 THÌ</span>
                      <span className="badge-hqcn">HQCN SANG TÊN 24H</span>
                    </div>
                    <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#fff', marginTop: '6px' }}>KTM 300 EXC TPI Factory Edition 2024</div>
                    <div style={{ color: 'var(--gold-light)', fontWeight: 700, fontSize: '1rem', marginTop: '4px' }}>
                      <span className="price-racing">{formatMoney(368000000)}</span> ({INITIAL_PRODUCTS[0].priceDisplay}) • 293cc TPI • 54 HP
                    </div>
                  </div>
                </div>
              </div>
            </section>

            {/* Quick Category Selector */}
            <section style={{ padding: '24px 30px', background: '#0a0c12', borderTop: '1px solid var(--border-subtle)', borderBottom: '1px solid var(--border-subtle)' }}>
              <div style={{ maxWidth: '1240px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
                <span style={{ fontWeight: 800, color: '#fff', fontSize: '1.05rem', fontFamily: 'var(--font-brand)' }}>
                  <i className="fa-solid fa-list me-2 text-gold"></i> DANH MỤC XE &amp; ĐỒ CHƠI:
                </span>
                <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                  {CATEGORIES.map(cat => (
                    <button 
                      key={cat.id} 
                      className={`btn-subtle ${selectedCategory === cat.id ? 'active' : ''}`}
                      onClick={() => { setSelectedCategory(cat.id); setCurrentTab('products'); }}
                    >
                      <i className={`fa-solid ${cat.icon}`}></i> {cat.name}
                    </button>
                  ))}
                </div>
              </div>
            </section>

            {/* GUEST CRITERIA HIGHLIGHT: SẢN PHẨM BÁN TRÊN 10 SẢN PHẨM CỦA CÁC SHOP (SẮP XẾP TỪ LỚN ĐẾN NHỎ) */}
            <section style={{ padding: '65px 30px', maxWidth: '1240px', margin: '0 auto' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '32px', borderLeft: '4px solid var(--red-racing)', paddingLeft: '18px' }}>
                <div>
                  <div style={{ color: 'var(--red-racing)', fontWeight: 800, fontSize: '0.85rem', letterSpacing: '1px' }}>
                    <i className="fa-solid fa-fire me-1"></i> TIÊU CHÍ GUEST (ĐỀ TÀI YÊU CẦU)
                  </div>
                  <h2 className="brand-title" style={{ fontSize: '2.1rem', color: '#fff', margin: '4px 0' }}>
                    TOP XE BÁN CHẠY TRÊN 10 CHIẾC (SẮP XẾP GIẢM DẦN)
                  </h2>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                    Tổng hợp từ hệ thống 3 Showroom Duy Trương Motor (Các dòng xe cào cào và siêu môtô bán trên 10 chiếc)
                  </p>
                </div>
                <div style={{ background: '#121520', border: '1px solid var(--gold-primary)', color: 'var(--gold-light)', padding: '6px 16px', borderRadius: '20px', fontSize: '0.85rem', fontWeight: 700 }}>
                  <i className="fa-solid fa-trophy text-gold me-1"></i> Bán Chạy Kỷ Lục
                </div>
              </div>

              <div className="bike-grid">
                {topOver10Sold.map(bike => (
                  <div key={bike.id} className="bike-card">
                    <div className="bike-img-box">
                      <img src={bike.image} alt={bike.name} />
                      <div className="badge-sold">
                        <i className="fa-solid fa-fire text-racing me-1"></i> Đã bán: <strong>{bike.sold} xe</strong>
                      </div>
                      <div className="badge-showroom">
                        <i className="fa-solid fa-store text-gold me-1"></i> {bike.storeName}
                      </div>
                      <div className="badge-hqcn-box">
                        <span className="badge-hqcn">HQCN</span>
                        {bike.odo && <span className="badge-odo">{bike.odo}</span>}
                        {bike.category === 'dirtbike' && <span className="badge-tag-orange">XE CÀO CÀO</span>}
                      </div>
                    </div>
                    <div className="bike-content">
                      <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)', fontWeight: 800, textTransform: 'uppercase' }}>
                        {bike.brand} • {bike.category === 'dirtbike' ? 'Xe Cào Cào Địa Hình' : 'Phân Khối Lớn'}
                      </div>
                      <h3 style={{ fontSize: '1.15rem', fontWeight: 800, margin: '6px 0 10px', color: '#fff', minHeight: '44px', fontFamily: 'var(--font-brand)' }}>
                        {bike.name}
                      </h3>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-sub)', marginBottom: '16px' }}>
                        <i className="fa-solid fa-gauge-high text-gold me-1"></i> {bike.engine}
                      </div>
                      <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                          <div className="price-racing">{formatMoney(bike.promotionalPrice || bike.price)}</div>
                          {bike.priceDisplay && <span className="price-text-tag">({bike.priceDisplay})</span>}
                        </div>
                        <button className="btn-gold" style={{ padding: '8px 16px', fontSize: '0.85rem' }} onClick={() => setActiveProductDetail(bike)}>
                          Chi Tiết
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </section>
          </div>
        )}

        {/* ================= TAB: PRODUCTS (CATALOG & FILTERS) ================= */}
        {currentTab === 'products' && (
          <div style={{ maxWidth: '1240px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ marginBottom: '32px' }}>
              <h2 className="brand-title" style={{ fontSize: '2.4rem', color: '#fff' }}>
                KHO SIÊU MÔ TÔ &amp; XE CÀO CÀO ĐỊA HÌNH
              </h2>
              <p style={{ color: 'var(--text-sub)' }}>
                Hiển thị danh sách đầy đủ các mẫu xe cào cào KTM, Husqvarna, Honda CRF, Kawasaki KLX và siêu môtô phân khối lớn có bộ lọc chi tiết
              </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '290px 1fr', gap: '35px', alignItems: 'start' }}>
              {/* Filter Sidebar */}
              <div className="glass-box" style={{ padding: '26px' }}>
                <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px', fontFamily: 'var(--font-brand)' }}>
                  <i className="fa-solid fa-filter"></i> BỘ LỌC TÌM KIẾM
                </h4>

                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Từ khóa tìm kiếm:
                  </label>
                  <input 
                    type="text" 
                    className="luxury-input" 
                    placeholder="KTM cào cào, KLX, Ducati, H2..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>

                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Phân loại xe:
                  </label>
                  <select className="luxury-select" value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
                    {CATEGORIES.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>

                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Hãng sản xuất:
                  </label>
                  <select className="luxury-select" value={brandFilter} onChange={(e) => setBrandFilter(e.target.value)}>
                    <option value="all">Tất cả thương hiệu ({products.length} mẫu xe)</option>
                    {allBrandList.map(b => (
                      <option key={b} value={b}>{b}</option>
                    ))}
                  </select>
                </div>

                <div style={{ marginBottom: '25px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Khoảng giá tối đa: <strong style={{ color: 'var(--gold-primary)' }}>{formatMoney(priceRange.max)}</strong>
                  </label>
                  <input 
                    type="range" 
                    min="10000000" 
                    max="30000000000" 
                    step="200000000"
                    value={priceRange.max} 
                    onChange={(e) => setPriceRange({ ...priceRange, max: parseInt(e.target.value) })}
                    style={{ width: '100%', accentColor: 'var(--gold-primary)' }}
                  />
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                    <span>10 Tr</span>
                    <span>15 Tỷ</span>
                    <span>30 Tỷ</span>
                  </div>
                </div>

                <button className="btn-outline-gold" style={{ width: '100%' }} onClick={() => { setSearchQuery(''); setSelectedCategory('all'); setBrandFilter('all'); setPriceRange({ min: 0, max: 30000000000 }); }}>
                  <i className="fa-solid fa-rotate-left"></i> Đặt Lại Bộ Lọc
                </button>
              </div>

              {/* Product Listing */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <span style={{ color: 'var(--text-sub)' }}>
                    Tìm thấy <strong style={{ color: 'var(--gold-primary)' }}>{filteredProducts.length}</strong> mẫu xe &amp; phụ kiện phù hợp
                  </span>
                </div>

                {filteredProducts.length === 0 ? (
                  <div className="glass-box" style={{ padding: '60px', textAlign: 'center' }}>
                    <i className="fa-solid fa-motorcycle" style={{ fontSize: '40px', color: 'var(--text-muted)', marginBottom: '16px' }}></i>
                    <h3>Không tìm thấy mẫu xe nào!</h3>
                    <p style={{ color: 'var(--text-muted)' }}>Vui lòng thay đổi từ khóa hoặc khoảng giá lọc.</p>
                    <div style={{ marginTop: '18px' }}>
                      <button 
                        className="btn-gold" 
                        style={{ padding: '10px 24px', fontSize: '0.9rem' }} 
                        onClick={() => { setBrandFilter('all'); setSelectedCategory('all'); setSearchQuery(''); setPriceRange({ min: 0, max: 30000000000 }); }}
                      >
                        <i className="fa-solid fa-rotate-left me-2"></i> Xem Toàn Bộ Kho Xe ({products.length} xe)
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="bike-grid">
                    {filteredProducts.map(bike => (
                      <div key={bike.id} className="bike-card">
                        <div className="bike-img-box">
                          <img src={bike.image} alt={bike.name} />
                          <div className="badge-showroom">
                            <i className="fa-solid fa-store text-gold me-1"></i> {bike.storeName}
                          </div>
                          <div className="badge-hqcn-box">
                            <span className="badge-hqcn">HQCN</span>
                            {bike.odo && <span className="badge-odo">{bike.odo}</span>}
                            {bike.category === 'dirtbike' && <span className="badge-tag-orange">CÀO CÀO</span>}
                          </div>
                        </div>
                        <div className="bike-content">
                          <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)', fontWeight: 800 }}>
                            {bike.brand} • {bike.year || '2024'}
                          </div>
                          <h3 style={{ fontSize: '1.15rem', fontWeight: 800, margin: '6px 0 10px', color: '#fff', minHeight: '44px', fontFamily: 'var(--font-brand)' }}>
                            {bike.name}
                          </h3>
                          <div style={{ fontSize: '0.85rem', color: 'var(--text-sub)', marginBottom: '14px' }}>
                            <i className="fa-solid fa-gauge-high text-gold me-1"></i> {bike.engine}
                          </div>
                          <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div>
                              <div className="price-racing">{formatMoney(bike.promotionalPrice || bike.price)}</div>
                              {bike.priceDisplay && <span className="price-text-tag">({bike.priceDisplay})</span>}
                            </div>
                            <div style={{ display: 'flex', gap: '8px' }}>
                              <button className="btn-gold" style={{ padding: '8px 14px', fontSize: '0.85rem' }} onClick={() => setActiveProductDetail(bike)}>
                                Xem Xe
                              </button>
                              <button className="btn-outline-gold" style={{ padding: '8px 12px' }} title="Thêm vào giỏ" onClick={() => handleAddToCart(bike)}>
                                <i className="fa-solid fa-cart-plus"></i>
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ================= TAB: SHOWROOMS ================= */}
        {currentTab === 'showrooms' && (
          <div style={{ maxWidth: '1240px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ textAlign: 'center', marginBottom: '45px' }}>
              <div style={{ color: 'var(--gold-primary)', fontWeight: 800, fontSize: '0.9rem', letterSpacing: '1px' }}>
                HỆ THỐNG PHÂN PHỐI TOÀN QUỐC
              </div>
              <h2 className="brand-title" style={{ fontSize: '2.5rem', color: '#fff' }}>
                CHUỖI SHOWROOM DUY TRƯƠNG MOTOR
              </h2>
              <p style={{ color: 'var(--text-sub)' }}>
                Đến trực tiếp showroom để chiêm ngưỡng, lái thử các dòng siêu môtô PKL &amp; xe cào cào địa hình
              </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '30px' }}>
              {SHOWROOMS.map(store => (
                <div key={store.id} className="glass-box" style={{ padding: '30px', display: 'flex', flexDirection: 'column' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '18px', marginBottom: '20px' }}>
                    <img src={store.avatar} alt={store.name} style={{ width: '68px', height: '68px', borderRadius: '50%', border: '2px solid var(--gold-primary)', objectFit: 'cover' }} />
                    <div>
                      <h4 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#fff', marginBottom: '4px', fontFamily: 'var(--font-brand)' }}>{store.name}</h4>
                      <div style={{ color: 'var(--gold-primary)', fontSize: '0.85rem' }}>
                        <i className="fa-solid fa-star"></i> {store.rating} / 5.0 (Chi nhánh đạt chuẩn)
                      </div>
                    </div>
                  </div>
                  <p style={{ color: 'var(--text-sub)', fontSize: '0.9rem', marginBottom: '20px' }}>{store.bio}</p>
                  <div style={{ marginTop: 'auto', borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '16px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    <div><i className="fa-solid fa-map-pin text-racing me-2"></i> {store.address}</div>
                    <div style={{ marginTop: '6px' }}><i className="fa-solid fa-phone text-gold me-2"></i> Hotline: <strong style={{ color: '#fff' }}>{store.phone}</strong></div>
                  </div>
                  <button className="btn-outline-gold" style={{ marginTop: '20px', width: '100%' }} onClick={() => setIsChatOpen(true)}>
                    <i className="fa-solid fa-comments"></i> Liên Hệ Tư Vấn Trực Tiếp
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* ================= TAB: ORDERS ================= */}
        {currentTab === 'orders' && (
          <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '50px 30px' }}>
            <h2 className="brand-title" style={{ fontSize: '2.2rem', color: '#fff', marginBottom: '24px' }}>
              <i className="fa-solid fa-clock-rotate-left text-gold me-2"></i> LỊCH SỬ ĐẶT MUA XE &amp; BÀN GIAO
            </h2>

            {orders.length === 0 ? (
              <div className="glass-box" style={{ padding: '60px', textAlign: 'center' }}>
                <h4>Chưa có đơn hàng nào</h4>
                <p style={{ color: 'var(--text-muted)' }}>Hãy chọn mẫu xe ưa thích và đặt cọc online ngay hôm nay.</p>
                <button className="btn-gold" onClick={() => setCurrentTab('products')}>Khám Phá Kho Xe</button>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '22px' }}>
                {orders.map(order => (
                  <div key={order.id} className="glass-box" style={{ padding: '26px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '14px', marginBottom: '16px' }}>
                      <div>
                        <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>MÃ ĐƠN HÀNG:</span>
                        <strong className="text-gold" style={{ fontSize: '1.1rem', marginLeft: '8px' }}>{order.id}</strong>
                        <span style={{ marginLeft: '12px', background: '#0e111a', border: '1px solid rgba(255,255,255,0.1)', padding: '2px 8px', borderRadius: '4px', fontSize: '0.75rem', color: 'var(--text-sub)' }}>
                          {order.storeName}
                        </span>
                      </div>
                      <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                        <span style={{ 
                          padding: '4px 12px', borderRadius: '20px', fontSize: '0.75rem', fontWeight: 700,
                          background: order.status === 'CONFIRMED' ? 'rgba(212,175,55,0.15)' : (order.status === 'DELIVERED' ? 'rgba(40,167,69,0.15)' : 'rgba(0,242,254,0.15)'),
                          color: order.status === 'CONFIRMED' ? 'var(--gold-light)' : (order.status === 'DELIVERED' ? '#28a745' : 'var(--cyan-neon)'),
                          border: '1px solid currentColor'
                        }}>
                          {order.status === 'CONFIRMED' ? 'Đã Xác Nhận Đơn' : (order.status === 'DELIVERED' ? 'Đã Bàn Giao Xe' : order.status)}
                        </span>
                        {order.isPaid && (
                          <span style={{ background: '#28a745', color: '#fff', padding: '3px 8px', borderRadius: '4px', fontSize: '0.7rem', fontWeight: 700 }}>
                            <i className="fa-solid fa-check-circle me-1"></i> ĐÃ THANH TOÁN VNPAY
                          </span>
                        )}
                      </div>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr auto', gap: '20px', alignItems: 'center' }}>
                      <div style={{ display: 'flex', gap: '18px', alignItems: 'center' }}>
                        <img src={order.productImage} alt={order.productName} style={{ width: '85px', height: '60px', objectFit: 'cover', borderRadius: '10px' }} />
                        <div>
                          <h4 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#fff', fontFamily: 'var(--font-brand)' }}>{order.productName}</h4>
                          <div style={{ fontSize: '0.85rem', color: 'var(--text-sub)' }}>
                            <i className="fa-solid fa-location-dot text-racing me-1"></i> {order.address}
                          </div>
                          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                            {order.deliveryName} • Đặt ngày: {order.date}
                          </div>
                        </div>
                      </div>

                      <div style={{ textAlign: 'right' }}>
                        <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Tổng thanh toán:</div>
                        <div className="price-racing" style={{ color: 'var(--gold-primary)' }}>{formatMoney(order.totalAmount)}</div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* ================= TAB: VENDOR (SHOWROOM MANAGER) ================= */}
        {currentTab === 'vendor' && (
          <div style={{ maxWidth: '1240px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
              <div>
                <span style={{ background: 'var(--gold-primary)', color: '#000', padding: '4px 10px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 800 }}>KÊNH VENDOR</span>
                <h2 className="brand-title" style={{ fontSize: '2.2rem', color: '#fff', marginTop: '6px' }}>
                  QUẢN TRỊ SHOWROOM PHÚ NHUẬN (TP.HCM)
                </h2>
              </div>
              <button className="btn-gold" onClick={() => setIsAddBikeModalOpen(true)}>
                <i className="fa-solid fa-plus-circle"></i> Nhập Thêm Xe / Cào Cào Mới
              </button>
            </div>

            {/* Metrics */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '24px', marginBottom: '36px' }}>
              <div className="glass-box" style={{ padding: '24px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 700 }}>DOANH THU ĐÃ BÀN GIAO</div>
                <div className="price-racing text-gradient-gold" style={{ fontSize: '2.2rem', margin: '8px 0' }}>{formatMoney(2450000000)}</div>
                <div style={{ color: '#28a745', fontSize: '0.8rem' }}><i className="fa-solid fa-arrow-up"></i> +22.5% so với tháng trước</div>
              </div>
              <div className="glass-box" style={{ padding: '24px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 700 }}>XE TRƯNG BÀY TẠI KHO</div>
                <div style={{ fontSize: '2rem', fontWeight: 900, color: '#fff', margin: '8px 0', fontFamily: 'var(--font-brand)' }}>{products.filter(p => p.storeId === 1).length} Dòng Xe</div>
                <div style={{ color: 'var(--gold-light)', fontSize: '0.8rem' }}>Đầy đủ xe cào cào KTM &amp; môtô Superbike</div>
              </div>
              <div className="glass-box" style={{ padding: '24px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 700 }}>VÍ TIỀN SHOWROOM</div>
                <div className="price-racing" style={{ fontSize: '2.2rem', color: '#fff', margin: '8px 0' }}>{formatMoney(currentUser.wallet || 280000000)}</div>
                <div style={{ color: 'var(--text-sub)', fontSize: '0.8rem' }}>Sẵn sàng rút về tài khoản ngân hàng</div>
              </div>
            </div>

            {/* Inventory table */}
            <div className="glass-box" style={{ padding: '28px' }}>
              <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '20px', fontFamily: 'var(--font-brand)' }}>DANH SÁCH XE CỦA SHOWROOM</h4>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.15)', color: 'var(--text-muted)' }}>
                      <th style={{ padding: '12px' }}>HÌNH ẢNH</th>
                      <th style={{ padding: '12px' }}>TÊN DÒNG XE</th>
                      <th style={{ padding: '12px' }}>GIÁ NIÊM YẾT</th>
                      <th style={{ padding: '12px' }}>TỒN KHO</th>
                      <th style={{ padding: '12px' }}>ĐÃ BÁN</th>
                      <th style={{ padding: '12px' }}>TRẠNG THÁI</th>
                    </tr>
                  </thead>
                  <tbody>
                    {products.filter(p => p.storeId === 1).map(p => (
                      <tr key={p.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                        <td style={{ padding: '12px' }}>
                          <img src={p.image} alt={p.name} style={{ width: '60px', height: '42px', objectFit: 'cover', borderRadius: '6px' }} />
                        </td>
                        <td style={{ padding: '12px', fontWeight: 700, color: '#fff' }}>{p.name}</td>
                        <td style={{ padding: '12px', color: 'var(--gold-primary)', fontWeight: 700 }}>{formatMoney(p.price)}</td>
                        <td style={{ padding: '12px' }}>{p.quantity} chiếc</td>
                        <td style={{ padding: '12px', color: 'var(--red-racing)', fontWeight: 700 }}>{p.sold} xe</td>
                        <td style={{ padding: '12px' }}>
                          <span style={{ background: '#28a745', color: '#fff', padding: '3px 10px', borderRadius: '12px', fontSize: '0.75rem', fontWeight: 700 }}>
                            Đang mở bán
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* ================= TAB: ADMIN ================= */}
        {currentTab === 'admin' && (
          <div style={{ maxWidth: '1240px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ marginBottom: '32px' }}>
              <span style={{ background: 'var(--red-racing)', color: '#fff', padding: '4px 10px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 800 }}>TỔNG QUẢN TRỊ HỆ THỐNG</span>
              <h2 className="brand-title" style={{ fontSize: '2.4rem', color: '#fff', marginTop: '6px' }}>
                BẢNG ĐIỀU HÀNH TOÀN CHUỖI DUY TRƯƠNG MOTOR
              </h2>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '20px', marginBottom: '36px' }}>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>TỔNG GMV TOÀN SÀN</div>
                <div className="price-racing text-gradient-gold" style={{ fontSize: '2rem', margin: '6px 0' }}>{formatMoney(9850000000)}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Toàn bộ 3 chi nhánh</div>
              </div>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>HOA HỒNG SÀN (5%)</div>
                <div className="price-racing" style={{ fontSize: '2rem', color: '#28a745', margin: '6px 0' }}>{formatMoney(492500000)}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Lợi nhuận trích sàn 5%</div>
              </div>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>HỆ THỐNG SHOWROOM</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 900, color: '#fff', margin: '6px 0', fontFamily: 'var(--font-brand)' }}>03 Chi Nhánh</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)' }}>TP.HCM, HN, Đà Nẵng</div>
              </div>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>THÀNH VIÊN ĐÃ KÍCH HOẠT</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 900, color: 'var(--cyan-neon)', margin: '6px 0', fontFamily: 'var(--font-brand)' }}>1.450 Biker</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Xác thực qua Email OTP</div>
              </div>
            </div>

            {/* Showroom Approval & Commission Management */}
            <div className="glass-box" style={{ padding: '28px' }}>
              <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '20px', fontFamily: 'var(--font-brand)' }}>
                <i className="fa-solid fa-building-circle-check me-2"></i> QUẢN LÝ CẤP PHÉP SHOWROOM (UC_018)
              </h4>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.15)', color: 'var(--text-muted)' }}>
                      <th style={{ padding: '12px' }}>SHOWROOM</th>
                      <th style={{ padding: '12px' }}>KHU VỰC</th>
                      <th style={{ padding: '12px' }}>HOTLINE</th>
                      <th style={{ padding: '12px' }}>CHIẾT KHẤU SÀN</th>
                      <th style={{ padding: '12px' }}>TRẠNG THÁI</th>
                      <th style={{ padding: '12px' }}>THAO TÁC</th>
                    </tr>
                  </thead>
                  <tbody>
                    {SHOWROOMS.map(s => (
                      <tr key={s.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                        <td style={{ padding: '12px', fontWeight: 700, color: '#fff' }}>{s.name}</td>
                        <td style={{ padding: '12px', color: 'var(--text-sub)' }}>{s.city}</td>
                        <td style={{ padding: '12px', color: 'var(--gold-primary)' }}>{s.phone}</td>
                        <td style={{ padding: '12px', fontWeight: 700 }}>5.0%</td>
                        <td style={{ padding: '12px' }}><span style={{ color: '#28a745', fontWeight: 700 }}><i className="fa-solid fa-check"></i> Đang hoạt động</span></td>
                        <td style={{ padding: '12px' }}>
                          <button className="btn-subtle" style={{ fontSize: '0.75rem', padding: '4px 10px' }} onClick={() => alert("Đã cập nhật trạng thái cấp phép chi nhánh!")}>
                            Cấp Phép Lại
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* ================= TAB: SHIPPER ================= */}
        {currentTab === 'shipper' && (
          <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ marginBottom: '32px' }}>
              <span style={{ background: 'var(--cyan-neon)', color: '#000', padding: '4px 10px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 800 }}>ĐỘI XE SÀN TRƯỢT VIP</span>
              <h2 className="brand-title" style={{ fontSize: '2.4rem', color: '#fff', marginTop: '6px' }}>
                ĐIỀU PHỐI VẬN CHUYỂN &amp; BÀN GIAO XE TẬN NHÀ
              </h2>
              <p style={{ color: 'var(--text-sub)' }}>Tài xế chuyên dụng: {currentUser.name} ({currentUser.phone || '0904.567.890'})</p>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '22px' }}>
              {orders.map(order => (
                <div key={order.id} className="glass-box" style={{ padding: '26px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '12px' }}>
                    <div>
                      <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>CUỐC XE VẬN TẢI:</span>
                      <strong className="text-gold" style={{ fontSize: '1.1rem', marginLeft: '8px' }}>{order.id}</strong>
                    </div>
                    <span style={{ background: 'rgba(0,242,254,0.15)', color: 'var(--cyan-neon)', border: '1px solid var(--cyan-neon)', padding: '4px 12px', borderRadius: '20px', fontSize: '0.8rem', fontWeight: 700 }}>
                      {order.deliveryName}
                    </span>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr auto', gap: '20px', alignItems: 'center' }}>
                    <div>
                      <div style={{ fontSize: '1rem', color: '#fff', fontWeight: 700 }}>Khách nhận: {order.customerName} - {order.phone}</div>
                      <div style={{ color: 'var(--text-sub)', fontSize: '0.9rem', marginTop: '4px' }}>
                        <i className="fa-solid fa-map-pin text-racing me-1"></i> {order.address}
                      </div>
                      <div style={{ color: 'var(--gold-primary)', fontSize: '0.85rem', marginTop: '6px' }}>
                        <i className="fa-solid fa-motorcycle me-1"></i> Bàn giao dòng xe: <strong>{order.productName}</strong>
                      </div>
                    </div>

                    <div>
                      {order.status !== 'DELIVERED' ? (
                        <button 
                          className="btn-gold" 
                          onClick={() => {
                            setOrders(orders.map(o => o.id === order.id ? { ...o, status: 'DELIVERED' } : o));
                            confetti({ particleCount: 100, spread: 60 });
                            alert("Xác nhận bàn giao xe và biên bản kiểm tra kỹ thuật thành công!");
                          }}
                        >
                          <i className="fa-solid fa-signature"></i> XÁC NHẬN ĐÃ BÀN GIAO XE
                        </button>
                      ) : (
                        <span style={{ color: '#28a745', fontWeight: 800, fontSize: '1rem' }}>
                          <i className="fa-solid fa-circle-check me-1"></i> ĐÃ BÀN GIAO HOÀN TẤT
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

      </main>

      {/* 6. SHOPPING CART DRAWER */}
      {isCartOpen && (
        <div className="modal-overlay" onClick={() => setIsCartOpen(false)}>
          <div 
            className="glass-box" 
            onClick={(e) => e.stopPropagation()} 
            style={{ position: 'fixed', top: 0, right: 0, bottom: 0, width: '450px', borderRadius: 0, borderLeft: '1px solid var(--gold-primary)', padding: '32px', display: 'flex', flexDirection: 'column', background: '#0a0d14' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '16px', marginBottom: '20px' }}>
              <h3 className="brand-title" style={{ fontSize: '1.4rem', color: '#fff' }}>
                <i className="fa-solid fa-cart-shopping text-gold me-2"></i> GIỎ HÀNG CỦA BẠN
              </h3>
              <button onClick={() => setIsCartOpen(false)} style={{ background: 'transparent', border: 'none', color: '#fff', fontSize: '20px', cursor: 'pointer' }}>✕</button>
            </div>

            {cart.length === 0 ? (
              <div style={{ textAlign: 'center', margin: 'auto 0', color: 'var(--text-muted)' }}>
                <i className="fa-solid fa-cart-arrow-down" style={{ fontSize: '48px', marginBottom: '16px' }}></i>
                <h4>Giỏ hàng của bạn đang trống</h4>
                <p style={{ fontSize: '0.85rem' }}>Hãy chọn mẫu xe ưa thích để đặt hàng.</p>
              </div>
            ) : (
              <div style={{ flexGrow: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {cart.map(item => (
                  <div key={item.product.id} style={{ display: 'flex', gap: '14px', alignItems: 'center', background: 'rgba(255,255,255,0.03)', padding: '12px', borderRadius: '12px' }}>
                    <img src={item.product.image} alt={item.product.name} style={{ width: '70px', height: '50px', objectFit: 'cover', borderRadius: '8px' }} />
                    <div style={{ flexGrow: 1 }}>
                      <div style={{ fontSize: '0.9rem', fontWeight: 700, color: '#fff' }}>{item.product.name}</div>
                      <div style={{ color: 'var(--gold-primary)', fontWeight: 800, fontSize: '0.85rem' }}>
                        {formatMoney(item.product.promotionalPrice || item.product.price)}
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: '6px' }}>
                        <button className="btn-subtle" style={{ padding: '2px 8px' }} onClick={() => handleUpdateCartCount(item.product.id, -1)}>-</button>
                        <span style={{ fontSize: '0.85rem', fontWeight: 800 }}>{item.count}</span>
                        <button className="btn-subtle" style={{ padding: '2px 8px' }} onClick={() => handleUpdateCartCount(item.product.id, 1)}>+</button>
                      </div>
                    </div>
                    <button onClick={() => handleRemoveFromCart(item.product.id)} style={{ background: 'transparent', border: 'none', color: 'var(--red-racing)', cursor: 'pointer' }}>
                      <i className="fa-solid fa-trash"></i>
                    </button>
                  </div>
                ))}
              </div>
            )}

            {cart.length > 0 && (
              <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '20px', marginTop: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', color: 'var(--text-sub)' }}>
                  <span>Tạm tính giỏ hàng:</span>
                  <span style={{ color: '#fff', fontWeight: 700 }}>{formatMoney(cartSubtotal)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                  <span style={{ fontWeight: 800, color: '#fff' }}>Tổng cộng:</span>
                  <span className="price-racing text-gradient-gold" style={{ fontSize: '1.6rem' }}>{formatMoney(cartSubtotal)}</span>
                </div>
                <button 
                  className="btn-gold" 
                  style={{ width: '100%', padding: '14px' }} 
                  onClick={() => { setIsCartOpen(false); setIsCheckoutOpen(true); }}
                >
                  <i className="fa-solid fa-credit-card"></i> TIẾN HÀNH ĐẶT HÀNG &amp; CỌC XE
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* 7. CHECKOUT MODAL */}
      {isCheckoutOpen && (
        <div className="modal-overlay" onClick={() => setIsCheckoutOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '14px' }}>
              <h3 className="brand-title" style={{ fontSize: '1.6rem', color: '#fff' }}>
                <i className="fa-solid fa-file-invoice-dollar text-gold me-2"></i> THÔNG TIN ĐẶT HÀNG &amp; CỌC XE
              </h3>
              <button onClick={() => setIsCheckoutOpen(false)} style={{ background: 'transparent', border: 'none', color: '#fff', fontSize: '20px', cursor: 'pointer' }}>✕</button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Họ và tên người nhận:</label>
                  <input 
                    type="text" 
                    className="luxury-input" 
                    value={checkoutData.recipientName} 
                    onChange={(e) => setCheckoutData({ ...checkoutData, recipientName: e.target.value })} 
                  />
                </div>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Số điện thoại liên hệ:</label>
                  <input 
                    type="text" 
                    className="luxury-input" 
                    value={checkoutData.phone} 
                    onChange={(e) => setCheckoutData({ ...checkoutData, phone: e.target.value })} 
                  />
                </div>
              </div>

              <div>
                <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Địa chỉ nhận bàn giao xe:</label>
                <input 
                  type="text" 
                  className="luxury-input" 
                  value={checkoutData.address} 
                  onChange={(e) => setCheckoutData({ ...checkoutData, address: e.target.value })} 
                />
              </div>

              {/* Delivery method */}
              <div>
                <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Phương thức vận chuyển chuyên dụng:</label>
                <select 
                  className="luxury-select" 
                  value={checkoutData.deliveryFee} 
                  onChange={(e) => setCheckoutData({ 
                    ...checkoutData, 
                    deliveryFee: parseInt(e.target.value),
                    deliveryName: e.target.value === "1500000" ? "Giao xe sàn trượt thủy lực VIP (Chống trầy 100%)" : "Xe tải thùng kín chuyên chở môtô" 
                  })}
                >
                  <option value="1500000">Giao xe sàn trượt thủy lực VIP (1.500.000 VNĐ) - Bàn giao tận phòng khách</option>
                  <option value="800000">Xe tải thùng kín chuyển môtô (800.000 VNĐ)</option>
                  <option value="50000">Giao tiêu chuẩn phụ kiện (50.000 VNĐ)</option>
                </select>
              </div>

              {/* Payment method */}
              <div>
                <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, marginBottom: '8px', display: 'block' }}>Phương thức thanh toán:</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                  <div 
                    onClick={() => setCheckoutData({ ...checkoutData, paymentMethod: 'VNPAY' })}
                    style={{ 
                      padding: '14px', borderRadius: '12px', border: `2px solid ${checkoutData.paymentMethod === 'VNPAY' ? 'var(--gold-primary)' : 'rgba(255,255,255,0.1)'}`,
                      background: checkoutData.paymentMethod === 'VNPAY' ? 'rgba(212,175,55,0.1)' : 'transparent', cursor: 'pointer' 
                    }}
                  >
                    <div style={{ fontWeight: 800, color: 'var(--gold-primary)' }}><i className="fa-solid fa-qrcode me-2"></i> CỔNG VNPAY QR</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Quét mã QR qua ứng dụng ngân hàng</div>
                  </div>

                  <div 
                    onClick={() => setCheckoutData({ ...checkoutData, paymentMethod: 'COD' })}
                    style={{ 
                      padding: '14px', borderRadius: '12px', border: `2px solid ${checkoutData.paymentMethod === 'COD' ? 'var(--gold-primary)' : 'rgba(255,255,255,0.1)'}`,
                      background: checkoutData.paymentMethod === 'COD' ? 'rgba(212,175,55,0.1)' : 'transparent', cursor: 'pointer' 
                    }}
                  >
                    <div style={{ fontWeight: 800, color: '#fff' }}><i className="fa-solid fa-hand-holding-dollar me-2"></i> THANH TOÁN KHI NHẬN XE</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Trả tiền mặt sau khi nghiệm thu xe</div>
                  </div>
                </div>
              </div>

              {/* Order total */}
              <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Tổng thanh toán (Xe + Cước VIP):</div>
                  <div className="price-racing" style={{ color: 'var(--gold-primary)', fontSize: '1.8rem' }}>{formatMoney(cartSubtotal + checkoutData.deliveryFee)}</div>
                </div>
                <button className="btn-gold" style={{ padding: '14px 32px' }} onClick={handlePlaceOrder}>
                  Xác Nhận Đặt Cọc
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 8. VNPAY QR CODE PAYMENT MODAL SIMULATION */}
      {isVnPayModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '480px', textAlign: 'center', background: '#fff', color: '#000' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div style={{ fontWeight: 900, color: '#005baa', fontSize: '1.3rem' }}>VNPAY<span style={{ color: '#ed1c24' }}>QR</span></div>
              <span style={{ fontSize: '0.85rem', color: '#666' }}>Hết hạn sau: 14:59</span>
            </div>

            <div style={{ background: '#f8f9fa', padding: '16px', borderRadius: '12px', border: '1px solid #e9ecef', marginBottom: '20px' }}>
              <div style={{ fontSize: '0.9rem', color: '#666' }}>Đơn hàng tại: <strong>DUY TRƯƠNG MOTOR</strong></div>
              <div className="price-racing" style={{ fontSize: '2rem', color: '#ed1c24', margin: '6px 0' }}>
                {formatMoney(cartSubtotal + checkoutData.deliveryFee)}
              </div>
              <div style={{ fontSize: '0.8rem', color: '#888' }}>Mã thanh toán: DTM-PAY-VNPAY2026</div>
            </div>

            {/* Generated QR Code */}
            <div style={{ display: 'inline-block', padding: '16px', background: '#fff', border: '2px solid #005baa', borderRadius: '16px', marginBottom: '20px' }}>
              <img 
                src="https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=https://duytruongmotor.com/vnpay-pay-demo" 
                alt="VNPAY QR" 
                style={{ width: '200px', height: '200px' }} 
              />
            </div>

            <p style={{ fontSize: '0.85rem', color: '#555', marginBottom: '24px' }}>
              Mở ứng dụng Ngân hàng (Vietcombank, MB, Techcombank, BIDV) quét mã QR để thanh toán cọc tức thì.
            </p>

            <div style={{ display: 'flex', gap: '12px' }}>
              <button 
                onClick={() => finalizeOrder(true)} 
                style={{ flexGrow: 1, background: '#005baa', color: '#fff', border: 'none', padding: '12px', borderRadius: '8px', fontWeight: 800, cursor: 'pointer' }}
              >
                <i className="fa-solid fa-check-circle me-1"></i> [Giả lập] ĐÃ QUÉT MÃ THÀNH CÔNG
              </button>
              <button 
                onClick={() => setIsVnPayModalOpen(false)} 
                style={{ background: '#e9ecef', border: 'none', padding: '12px 18px', borderRadius: '8px', cursor: 'pointer' }}
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 9. BIKE DETAIL & REVIEWS MODAL */}
      {activeProductDetail && (
        <div className="modal-overlay" onClick={() => setActiveProductDetail(null)}>
          <div className="modal-content" style={{ maxWidth: '850px' }} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={{ color: 'var(--gold-primary)', fontWeight: 800 }}>{activeProductDetail.brand} • {activeProductDetail.storeName}</span>
                <span className="badge-hqcn">HQCN SANG TÊN TOÀN QUỐC</span>
              </div>
              <button onClick={() => setActiveProductDetail(null)} className="modal-close-btn">✕</button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px', marginBottom: '30px' }}>
              <div>
                <img src={activeProductDetail.image} alt={activeProductDetail.name} style={{ width: '100%', borderRadius: '16px', border: '1px solid var(--gold-primary)', objectFit: 'cover' }} />
                <div style={{ display: 'flex', gap: '8px', marginTop: '12px', flexWrap: 'wrap' }}>
                  <span className="badge-hqcn">Bao Test Hãng</span>
                  <span className="badge-odo">Odo: {activeProductDetail.odo || 'Mới 100%'}</span>
                  <span className="badge-odo">Đời xe: {activeProductDetail.year || '2024'}</span>
                  <span className="badge-tag-orange">Bao Quay Đầu</span>
                </div>
              </div>
              <div>
                <h3 className="brand-title" style={{ fontSize: '1.6rem', color: '#fff', marginBottom: '10px' }}>
                  {activeProductDetail.name}
                </h3>
                <div style={{ display: 'flex', alignItems: 'baseline', gap: '8px', marginBottom: '16px' }}>
                  <span className="price-racing" style={{ fontSize: '1.8rem', color: 'var(--gold-primary)' }}>
                    {formatMoney(activeProductDetail.promotionalPrice || activeProductDetail.price)}
                  </span>
                  {activeProductDetail.priceDisplay && (
                    <span style={{ fontSize: '1rem', color: 'var(--gold-light)', fontWeight: 700 }}>
                      ({activeProductDetail.priceDisplay})
                    </span>
                  )}
                </div>

                <div style={{ background: '#121520', padding: '14px', borderRadius: '12px', fontSize: '0.85rem', color: 'var(--text-sub)', marginBottom: '20px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <div><strong style={{ color: '#fff' }}>Động cơ:</strong> {activeProductDetail.engine}</div>
                  <div><strong style={{ color: '#fff' }}>Công suất:</strong> {activeProductDetail.power}</div>
                  <div><strong style={{ color: '#fff' }}>Màu sắc:</strong> {activeProductDetail.color}</div>
                  <div><strong style={{ color: '#fff' }}>Tình trạng:</strong> {activeProductDetail.odo || 'Xe mới 100%'} (Bao rút gốc, sang tên trong 24h)</div>
                  <div><strong style={{ color: '#fff' }}>Tồn kho:</strong> {activeProductDetail.quantity} chiếc sẵn sàng giao</div>
                </div>

                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '20px', lineHeight: 1.5 }}>
                  {activeProductDetail.description}
                </p>

                <div style={{ display: 'flex', gap: '12px' }}>
                  <button className="btn-gold" style={{ flexGrow: 1, padding: '14px' }} onClick={() => { handleAddToCart(activeProductDetail); setActiveProductDetail(null); }}>
                    <i className="fa-solid fa-cart-plus"></i> Đặt Mua &amp; Cọc Xe
                  </button>
                  <button className="btn-outline-gold" style={{ padding: '14px 20px' }} onClick={() => { setIsChatOpen(true); setActiveProductDetail(null); }}>
                    <i className="fa-solid fa-comments"></i> Hẹn Lái Thử
                  </button>
                </div>
              </div>
            </div>

            {/* Prompt Requirement: Đánh giá bình luận tối thiểu 50 ký tự + hình ảnh */}
            <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '24px' }}>
              <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '16px', fontFamily: 'var(--font-brand)' }}>
                <i className="fa-solid fa-comments me-2"></i> ĐÁNH GIÁ CHỦ XE (YÊU CẦU TỐI THIỂU 50 KÝ TỰ)
              </h4>

              {/* Review Input Box */}
              <form onSubmit={handleSubmitReview} style={{ background: '#090b10', padding: '18px', borderRadius: '14px', border: '1px solid rgba(212,175,55,0.3)', marginBottom: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Số sao trải nghiệm:</label>
                  <select className="luxury-select" style={{ width: '160px', padding: '6px 12px' }} value={reviewStars} onChange={(e) => setReviewStars(parseInt(e.target.value))}>
                    <option value="5">⭐⭐⭐⭐⭐ 5 Sao</option>
                    <option value="4">⭐⭐⭐⭐ 4 Sao</option>
                    <option value="3">⭐⭐⭐ 3 Sao</option>
                  </select>
                </div>

                <textarea 
                  className="luxury-input" 
                  rows="3" 
                  placeholder="Chia sẻ cảm giác lái, gia tốc máy, tiếng pô rít, độ nhún phuộc địa hình, thủ tục rút hồ sơ tại Duy Trương Motor... (Bắt buộc tối thiểu 50 ký tự)"
                  value={reviewText}
                  onChange={(e) => setReviewText(e.target.value)}
                  style={{ marginBottom: '8px' }}
                />

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.8rem', color: reviewText.length >= 50 ? '#28a745' : 'var(--red-racing)', marginBottom: '14px' }}>
                  <span>{reviewText.length >= 50 ? '✓ Đã đạt độ dài hợp lệ (>= 50 ký tự)' : '⚠️ Cần thêm ít nhất ' + (50 - reviewText.length) + ' ký tự nữa'}</span>
                  <span>{reviewText.length} / 50 ký tự</span>
                </div>

                <div style={{ display: 'flex', gap: '10px' }}>
                  <input 
                    type="text" 
                    className="luxury-input" 
                    placeholder="Link hình ảnh/video xe thực tế (Cloudinary URL)..." 
                    value={reviewMedia}
                    onChange={(e) => setReviewMedia(e.target.value)}
                    style={{ flexGrow: 1 }}
                  />
                  <button type="submit" className="btn-gold" disabled={reviewText.length < 50}>
                    Gửi Đánh Giá
                  </button>
                </div>
              </form>

              {/* Review List */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                {reviews.filter(r => r.productId === activeProductDetail.id).map(r => (
                  <div key={r.id} style={{ background: '#121520', padding: '16px', borderRadius: '12px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <img src={r.userAvatar} alt={r.userName} style={{ width: '32px', height: '32px', borderRadius: '50%' }} />
                        <strong style={{ color: '#fff', fontSize: '0.9rem' }}>{r.userName}</strong>
                        <span style={{ color: '#28a745', fontSize: '0.7rem' }}>✓ Đã mua xe tại showroom</span>
                      </div>
                      <span style={{ color: '#ffc107', fontSize: '0.85rem' }}>{'★'.repeat(r.stars)}</span>
                    </div>
                    <p style={{ color: 'var(--text-sub)', fontSize: '0.85rem', margin: '6px 0' }}>{r.content}</p>
                    {r.mediaUrl && <img src={r.mediaUrl} alt="Review Media" style={{ height: '90px', borderRadius: '8px', marginTop: '6px', objectFit: 'cover' }} />}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 10. MODAL: VENDOR NHẬP THÊM XE CÀO CÀO / MÔ TÔ */}
      {isAddBikeModalOpen && (
        <div className="modal-overlay" onClick={() => setIsAddBikeModalOpen(false)}>
          <div className="modal-content" style={{ maxWidth: '600px' }} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h3 className="brand-title" style={{ fontSize: '1.4rem', color: '#fff' }}>
                <i className="fa-solid fa-motorcycle text-gold me-2"></i> NHẬP THÊM XE / CÀO CÀO VÀO KHO SHOWROOM
              </h3>
              <button onClick={() => setIsAddBikeModalOpen(false)} className="modal-close-btn">✕</button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Tên dòng xe:</label>
                <input 
                  type="text" 
                  className="luxury-input" 
                  placeholder="Ví dụ: KTM 250 SX-F Motocross 2024"
                  value={newBike.name}
                  onChange={(e) => setNewBike({ ...newBike, name: e.target.value })}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Hãng xe:</label>
                  <select className="luxury-select" value={newBike.brand} onChange={(e) => setNewBike({ ...newBike, brand: e.target.value })}>
                    <option value="KTM">KTM</option>
                    <option value="Husqvarna">Husqvarna</option>
                    <option value="Honda">Honda</option>
                    <option value="Kawasaki">Kawasaki</option>
                    <option value="Ducati">Ducati</option>
                    <option value="BMW">BMW</option>
                    <option value="Harley-Davidson">Harley-Davidson</option>
                  </select>
                </div>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Phân loại:</label>
                  <select className="luxury-select" value={newBike.category} onChange={(e) => setNewBike({ ...newBike, category: e.target.value })}>
                    <option value="dirtbike">Xe Cào Cào - Địa Hình</option>
                    <option value="superbike">Superbike / Thể Thao</option>
                    <option value="hyper-naked">Hyper Naked</option>
                    <option value="cruiser">Cruiser</option>
                    <option value="adventure">Adventure</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Giá bán (VNĐ):</label>
                  <input 
                    type="number" 
                    className="luxury-input" 
                    value={newBike.price}
                    onChange={(e) => setNewBike({ ...newBike, price: parseInt(e.target.value) })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Động cơ:</label>
                  <input 
                    type="text" 
                    className="luxury-input" 
                    placeholder="250cc DOHC 4-Thì"
                    value={newBike.engine}
                    onChange={(e) => setNewBike({ ...newBike, engine: e.target.value })}
                  />
                </div>
              </div>

              <div>
                <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700 }}>Mô tả chi tiết &amp; tình trạng xe:</label>
                <textarea 
                  className="luxury-input" 
                  rows="3"
                  placeholder="Giấy tờ HQCN, tình trạng xe, pô độ, phuộc..."
                  value={newBike.description}
                  onChange={(e) => setNewBike({ ...newBike, description: e.target.value })}
                />
              </div>

              <button 
                className="btn-gold" 
                style={{ marginTop: '10px', padding: '14px' }}
                onClick={() => {
                  if (!newBike.name) { alert("Vui lòng nhập tên dòng xe!"); return; }
                  const bikeToAdd = {
                    id: Date.now(),
                    name: newBike.name,
                    brand: newBike.brand,
                    category: newBike.category,
                    storeId: 1,
                    storeName: "Showroom Phú Nhuận TP.HCM",
                    price: newBike.price,
                    promotionalPrice: newBike.price * 0.96,
                    priceDisplay: `${Math.round(newBike.price / 1000000)} Triệu`,
                    quantity: 2,
                    sold: 12,
                    rating: 5.0,
                    engine: newBike.engine || "Động cơ hiệu năng cao",
                    power: "Chính hãng",
                    color: "Đen Cam Thể Thao",
                    odo: "Xe mới 100%",
                    year: "2024",
                    badges: ["HQCN", "Bao Test Hãng", "Mới Nhập"],
                    image: newBike.image,
                    description: newBike.description || "Xe nhập khẩu hải quan chính ngạch nguyên chiếc, bảo hành 3 năm chính hãng tại Duy Trương Motor.",
                    isSelling: true
                  };
                  setProducts([bikeToAdd, ...products]);
                  setIsAddBikeModalOpen(false);
                  alert(`Đã nhập thành công dòng xe: ${bikeToAdd.name} vào kho Showroom!`);
                }}
              >
                <i className="fa-solid fa-plus-circle me-1"></i> Xác Nhận Nhập Xe Vào Kho
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 11. WEBSOCKET LIVE CHAT WIDGET */}
      <button className="floating-chat-btn" onClick={() => setIsChatOpen(!isChatOpen)} title="Chat trực tuyến với chuyên viên Showroom">
        <i className="fa-solid fa-headset"></i>
      </button>

      {isChatOpen && (
        <div className="chat-box-floating">
          <div style={{ padding: '14px 18px', background: 'var(--gold-gradient)', color: '#000', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div style={{ width: '10px', height: '10px', borderRadius: '50%', background: '#28a745' }}></div>
              <strong style={{ fontSize: '0.95rem', fontFamily: 'var(--font-brand)' }}>Tư Vấn Xe • Duy Trương Motor</strong>
            </div>
            <button onClick={() => setIsChatOpen(false)} style={{ background: 'transparent', border: 'none', fontWeight: 800, cursor: 'pointer', fontSize: '18px' }}>✕</button>
          </div>

          <div style={{ flexGrow: 1, padding: '16px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {chatMessages.map((msg, i) => (
              <div key={i} style={{ alignSelf: msg.isBot ? 'flex-start' : 'flex-end', maxWidth: '88%' }}>
                <div style={{ 
                  background: msg.isBot ? '#181c28' : 'var(--gold-gradient)', 
                  color: msg.isBot ? '#fff' : '#000', 
                  padding: '10px 14px', 
                  borderRadius: '14px', 
                  fontSize: '0.85rem',
                  lineHeight: '1.55',
                  boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
                  border: msg.isBot ? '1px solid rgba(255,255,255,0.08)' : 'none',
                  whiteSpace: 'pre-line'
                }}>
                  <div style={{ fontSize: '0.7rem', fontWeight: 800, opacity: 0.7, marginBottom: '4px' }}>
                    {msg.sender} • {msg.time}
                  </div>
                  {msg.text.split(/(\*\*.*?\*\*)/g).map((part, pIdx) => {
                    if (part.startsWith('**') && part.endsWith('**')) {
                      return <strong key={pIdx} style={{ color: msg.isBot ? 'var(--gold-light)' : '#000' }}>{part.slice(2, -2)}</strong>;
                    }
                    return part;
                  })}
                </div>
              </div>
            ))}

            {isBotTyping && (
              <div style={{ alignSelf: 'flex-start', maxWidth: '85%' }}>
                <div style={{
                  background: '#181c28',
                  color: '#fff',
                  padding: '8px 14px',
                  borderRadius: '14px',
                  fontSize: '0.8rem',
                  border: '1px solid rgba(255,255,255,0.08)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--gold-light)' }}>Chuyên viên đang soạn câu trả lời</span>
                  <div className="chat-typing-dots">
                    <span></span><span></span><span></span>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Quick Suggestion Chips */}
          <div className="chat-chips-bar">
            {SUGGESTION_CHIPS.map(chip => (
              <button 
                key={chip.id} 
                className="chat-chip-btn" 
                onClick={() => handleSendMessage(chip.query)}
                title={chip.query}
              >
                {chip.label}
              </button>
            ))}
          </div>

          <div style={{ padding: '12px', background: '#0a0c12', borderTop: '1px solid rgba(255,255,255,0.1)', display: 'flex', gap: '8px' }}>
            <input 
              type="text" 
              className="luxury-input" 
              placeholder="Hỏi giá xe, cào cào KTM, đổi xe cũ..." 
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
              style={{ padding: '8px 12px', fontSize: '0.85rem' }}
            />
            <button className="btn-gold" style={{ padding: '8px 16px' }} onClick={() => handleSendMessage()}>
              <i className="fa-solid fa-paper-plane"></i>
            </button>
          </div>
        </div>
      )}

      {/* 12. FOOTER (Chuẩn phong cách Vương Khang Motor) */}
      <footer style={{ background: '#040507', borderTop: '1px solid var(--border-subtle)', padding: '50px 30px 25px', marginTop: 'auto' }}>
        <div style={{ maxWidth: '1240px', margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '40px' }}>
          <div>
            <div className="brand-title" style={{ fontSize: '1.45rem', marginBottom: '14px' }}>
              <span className="text-gradient-gold">DUY TRƯƠNG</span> MOTOR
            </div>
            <p style={{ color: 'var(--gold-light)', fontSize: '0.9rem', fontWeight: 700, fontStyle: 'italic', marginBottom: '12px' }}>
              "Ở đây, chúng tôi MUA CỦA NGƯỜI CHÁN - &amp; BÁN CHO NGƯỜI CẦN. Uy tín tạo nên thương hiệu."
            </p>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', lineHeight: 1.7 }}>
              Hệ thống chuỗi showroom phân phối chính ngạch siêu môtô PKL &amp; xe cào cào địa hình lớn nhất Việt Nam. Cam kết 100% xe Hải Quan Chính Ngạch (HQCN), bao sang tên toàn quốc, bao quay đầu lỗ thấp nhất và bảo hành 3 năm chính hãng.
            </p>
          </div>

          <div>
            <h5 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '16px', fontFamily: 'var(--font-brand)' }}>HỆ THỐNG SHOWROOM TOÀN QUỐC</h5>
            <ul style={{ listStyle: 'none', padding: 0, fontSize: '0.85rem', color: 'var(--text-sub)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <li>
                <i className="fa-solid fa-location-dot text-racing me-2"></i> 
                <strong>Trụ sở chính (TP.HCM):</strong> Số 123 Hoàng Văn Thụ, Phường 8, Quận Phú Nhuận, TP. HCM <br />
                <span style={{ color: 'var(--gold-primary)', fontSize: '0.8rem' }}>Hotline: 0903.456.789 - 0933.333.382</span>
              </li>
              <li>
                <i className="fa-solid fa-location-dot text-racing me-2"></i> 
                <strong>Chi nhánh Cầu Giấy (Hà Nội):</strong> Số 168 Trần Duy Hưng, Trung Hòa, Cầu Giấy, Hà Nội <br />
                <span style={{ color: 'var(--gold-primary)', fontSize: '0.8rem' }}>Hotline: 0901.234.567</span>
              </li>
              <li>
                <i className="fa-solid fa-location-dot text-racing me-2"></i> 
                <strong>Chi nhánh Hải Châu (Đà Nẵng):</strong> Số 52 Nguyễn Văn Linh, Nam Dương, Hải Châu, Đà Nẵng <br />
                <span style={{ color: 'var(--gold-primary)', fontSize: '0.8rem' }}>Hotline: 0902.999.888</span>
              </li>
            </ul>
          </div>

          <div>
            <h5 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '16px', fontFamily: 'var(--font-brand)' }}>NHÓM PHÁT TRIỂN ĐỀ TÀI (NHÓM 17)</h5>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.8 }}>
              <div>• <strong>Trương Quốc Duy (Leader &amp; Fullstack Dev):</strong> Kiến trúc toàn hệ thống, phát triển Fullstack Spring Boot 3 &amp; React SPA, Spring Security, JWT, OTP Email, VNPAY, WebSocket Live Chat</div>
              <div>• <strong>Đỗ Trọng Khôi:</strong> Khảo sát hiện trạng chuỗi showroom, thiết kế lược đồ CSDL &amp; Data xe cào cào/mô tô, Kiểm thử chất lượng (QA/Tester)</div>
              <div>• <strong>Lê Nghĩa Tình:</strong> Phân tích đặc tả nghiệp vụ SRS, Thiết kế sơ đồ luồng hệ thống &amp; UseCase, Xây dựng tài liệu kỹ thuật &amp; Video demo</div>
            </div>
          </div>
        </div>

        <div style={{ maxWidth: '1240px', margin: '40px auto 0', paddingTop: '20px', borderTop: '1px solid rgba(255,255,255,0.08)', textAlign: 'center', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          © 2026 DUY TRƯƠNG MOTOR - Hệ Thống Siêu Thị Mô Tô PKL &amp; Xe Cào Cào Uy Tín Số 1 Việt Nam. Bảo lưu mọi quyền.
        </div>
      </footer>

      {/* 13. RIGHT FLOATING QUICK ACTION DOCK (VƯƠNG KHANG MOTOR STYLE) */}
      <div className="vk-floating-dock">
        <button className="vk-dock-btn" onClick={() => setIsChatOpen(true)} title="Định giá thu mua xe cũ">
          <i className="fa-solid fa-handshake"></i>
          <span className="dock-tooltip">Định Giá Thu Mua Xe</span>
        </button>
        <button className="vk-dock-btn" onClick={() => alert("CAM KẾT PHÁP LÝ DUY TRƯƠNG MOTOR:\n• 100% Xe Hải Quan Chính Ngạch (HQCN)\n• Bao sang tên bấm biển số toàn quốc\n• Bao rút hồ sơ gốc trong 24 giờ\n• Hoàn tiền 100% nếu phát hiện xe tranh chấp pháp lý!")} title="Hồ sơ pháp lý HQCN">
          <i className="fa-solid fa-shield-halved"></i>
          <span className="dock-tooltip">Hồ Sơ Pháp Lý HQCN</span>
        </button>
        <button className="vk-dock-btn" onClick={() => { setSelectedCategory('all'); setBrandFilter('all'); setCurrentTab('products'); }} title="Kho hình ảnh thực tế">
          <i className="fa-solid fa-images"></i>
          <span className="dock-tooltip">Kho Ảnh Xe Thực Tế</span>
        </button>
        <a href="tel:0903456789" className="vk-dock-btn" title="Hotline gọi ngay">
          <i className="fa-solid fa-phone"></i>
          <span className="dock-tooltip">Hotline: 0903.456.789</span>
        </a>
        <a href="https://zalo.me/0903456789" target="_blank" rel="noreferrer" className="vk-dock-btn" style={{ background: '#0068ff', color: '#fff' }} title="Zalo tư vấn">
          <i className="fa-solid fa-comment-dots"></i>
          <span className="dock-tooltip">Zalo: 0903.456.789</span>
        </a>
      </div>

    </div>
  );
}
