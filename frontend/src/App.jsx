import React, { useState, useMemo, useEffect } from 'react';
import confetti from 'canvas-confetti';
import { 
  SHOWROOMS, 
  CATEGORIES, 
  INITIAL_PRODUCTS, 
  INITIAL_REVIEWS, 
  DEMO_USERS 
} from './data/mockData';

export default function App() {
  // --- STATE LAYER ---
  const [currentRoleKey, setCurrentRoleKey] = useState('guest');
  const [currentTab, setCurrentTab] = useState('home'); // home, products, showrooms, orders, vendor, admin, shipper
  const [selectedShowroomFilter, setSelectedShowroomFilter] = useState('all');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [brandFilter, setBrandFilter] = useState('all');
  const [priceRange, setPriceRange] = useState({ min: 0, max: 2000000000 });

  // Data Collections
  const [products, setProducts] = useState(INITIAL_PRODUCTS);
  const [reviews, setReviews] = useState(INITIAL_REVIEWS);
  const [cart, setCart] = useState([
    { product: INITIAL_PRODUCTS[0], count: 1 } // Default 1 Panigale V4 S in cart
  ]);
  const [orders, setOrders] = useState([
    {
      id: "DTM-9824A1B2",
      customerName: "Hoàng Biker",
      phone: "0905.678.901",
      address: "88 Landmark 81, Vinhomes Central Park, Bình Thạnh, TP. HCM",
      productName: "Ducati Panigale V4 S 2026",
      productImage: INITIAL_PRODUCTS[0].image,
      storeName: "Showroom Quận 1",
      deliveryName: "Giao xe sàn trượt thủy lực VIP (Chống trầy xước)",
      totalAmount: 921500000,
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

  // Review Form in Detail Modal (Prompt requires text >= 50 characters)
  const [reviewText, setReviewText] = useState('');
  const [reviewStars, setReviewStars] = useState(5);
  const [reviewMedia, setReviewMedia] = useState('');

  // Checkout Form
  const [checkoutData, setCheckoutData] = useState({
    recipientName: "Hoàng Biker",
    phone: "0905.678.901",
    address: "Số 88 Landmark 81, Vinhomes Central Park, Bình Thạnh, TP. HCM",
    deliveryFee: 1500000,
    deliveryName: "Giao xe sàn trượt thủy lực VIP (Chống trầy 100%)",
    paymentMethod: "VNPAY",
    note: "Vui lòng bọc nhung bàn giao xe tại sảnh chính toà nhà trước 10h sáng."
  });

  // Chat Widget State (WebSocket Simulation)
  const [chatMessages, setChatMessages] = useState([
    { sender: "Chuyên Viên Showroom", text: "Kính chào Quý khách đến với Chuỗi Siêu Showroom Duy Truong Moto! Quý khách đang quan tâm đến dòng siêu xe hoặc muốn đặt lịch lái thử nào?", time: "15:00", isBot: true }
  ]);
  const [chatInput, setChatInput] = useState('');

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

  // Filtered Products for Catalog & User tabs
  const filteredProducts = useMemo(() => {
    return products.filter(p => {
      const matchStore = selectedShowroomFilter === 'all' || p.storeId === parseInt(selectedShowroomFilter);
      const matchCategory = selectedCategory === 'all' || p.category === selectedCategory;
      const matchBrand = brandFilter === 'all' || p.brand.toLowerCase() === brandFilter.toLowerCase();
      const matchPrice = p.price >= priceRange.min && p.price <= priceRange.max;
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
    alert("Cảm ơn bạn đã gửi đánh giá trải nghiệm thực tế về dòng xe này!");
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
    alert("Chúc mừng Quý khách! Đơn hàng đặt xe đã được tạo thành công trên hệ thống Duy Truong Moto!");
    setCurrentTab('orders');
  };

  // Chat message send
  const handleSendMessage = () => {
    if (!chatInput.trim()) return;
    const userMsg = { sender: currentUser.name, text: chatInput, time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }), isBot: false };
    setChatMessages(prev => [...prev, userMsg]);
    setChatInput('');

    setTimeout(() => {
      const botReplies = [
        "Dạ em là chuyên viên tư vấn chi nhánh Duy Truong Moto! Dòng xe Quý khách vừa nhắn hiện đang có sẵn tại phòng trưng bày VIP. Em có thể sắp xếp lịch lái thử cho Quý khách ngay chiều nay được không ạ?",
        "Dạ đơn hàng cọc xe qua cổng VNPAY sẽ được ưu tiên giữ số VIN khung máy đẹp và hỗ trợ miễn phí gói ra biển số toàn quốc ạ!",
        "Dạ chi nhánh bên em có đội xe sàn trượt thủy lực chuyên dụng, giao xe đến tận nhà không một vết xước ạ!"
      ];
      const botMsg = {
        sender: "Chuyên Viên Showroom",
        text: botReplies[Math.floor(Math.random() * botReplies.length)],
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        isBot: true
      };
      setChatMessages(prev => [...prev, botMsg]);
    }, 1000);
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>

      {/* 1. TOP ROLE SWITCHER HUD (Tạo sự linh hoạt tối đa cho Giảng viên chấm thử từng vai trò) */}
      <div className="role-hud">
        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
          <span style={{ color: 'var(--gold-primary)', fontWeight: 800 }}>
            <i className="fa-solid fa-code-compare me-1"></i> TEST VAI TRÒ (3 THÀNH VIÊN):
          </span>
          <div className="role-chips">
            <button className={`role-chip ${currentRoleKey === 'guest' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('guest'); setCurrentTab('home'); }}>
              <i className="fa-solid fa-user-secret me-1"></i> 1. Guest (Khách)
            </button>
            <button className={`role-chip ${currentRoleKey === 'customer' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('customer'); setCurrentTab('home'); }}>
              <i className="fa-solid fa-user me-1"></i> 2. User (Hoàng Biker)
            </button>
            <button className={`role-chip ${currentRoleKey === 'vendor' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('vendor'); setCurrentTab('vendor'); }}>
              <i className="fa-solid fa-store me-1"></i> 3. Vendor (Showroom Q1)
            </button>
            <button className={`role-chip ${currentRoleKey === 'admin' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('admin'); setCurrentTab('admin'); }}>
              <i className="fa-solid fa-shield-halved me-1"></i> 4. Admin (Duy Trường)
            </button>
            <button className={`role-chip ${currentRoleKey === 'shipper' ? 'active' : ''}`} onClick={() => { setCurrentRoleKey('shipper'); setCurrentTab('shipper'); }}>
              <i className="fa-solid fa-truck-ramp-box me-1"></i> 5. Shipper (Tài Xế Sàn Trượt)
            </button>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '20px', color: 'var(--text-sub)' }}>
          <div>
            <i className="fa-solid fa-location-dot text-gold me-1"></i> Showroom:
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

      {/* 2. MAIN APP HEADER */}
      <header className="app-header">
        <div className="logo-badge" onClick={() => setCurrentTab('home')}>
          <div className="logo-icon-box">
            <i className="fa-solid fa-motorcycle"></i>
          </div>
          <div>
            <div className="brand-serif" style={{ fontSize: '1.45rem', fontWeight: 900, lineHeight: 1.1 }}>
              <span className="text-gradient-gold">DUY TRUONG</span> <span style={{ color: '#fff' }}>MOTO</span>
            </div>
            <div style={{ fontSize: '0.65rem', color: 'var(--gold-primary)', letterSpacing: '2.5px', fontWeight: 800 }}>
              LUXURY SUPERBIKES CHAIN
            </div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav style={{ display: 'flex', alignItems: 'center', gap: '22px' }}>
          <button className={`btn-subtle ${currentTab === 'home' ? 'active' : ''}`} onClick={() => setCurrentTab('home')}>
            <i className="fa-solid fa-house"></i> Trang Chủ
          </button>
          <button className={`btn-subtle ${currentTab === 'products' ? 'active' : ''}`} onClick={() => setCurrentTab('products')}>
            <i className="fa-solid fa-motorcycle"></i> Kho Siêu Xe ({products.length})
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
              <i className="fa-solid fa-chart-line"></i> Dashboard Showroom
            </button>
          )}
          {currentRoleKey === 'admin' && (
            <button className={`btn-subtle ${currentTab === 'admin' ? 'active' : ''}`} style={{ borderColor: 'var(--red-racing)', color: 'var(--red-racing)' }} onClick={() => setCurrentTab('admin')}>
              <i className="fa-solid fa-shield-halved"></i> Tổng Quản Trị
            </button>
          )}
          {currentRoleKey === 'shipper' && (
            <button className={`btn-subtle ${currentTab === 'shipper' ? 'active' : ''}`} style={{ borderColor: 'var(--cyan-neon)', color: 'var(--cyan-neon)' }} onClick={() => setCurrentTab('shipper')}>
              <i className="fa-solid fa-truck-fast"></i> Phân Hệ Giao Xe
            </button>
          )}
        </nav>

        {/* Right Actions: Cart & User status */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <button className="btn-outline-gold" style={{ padding: '8px 20px' }} onClick={() => setIsCartOpen(true)}>
            <i className="fa-solid fa-cart-shopping"></i> Giỏ Hàng ({cart.length})
          </button>

          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <img src={currentUser.avatar} alt="Avatar" style={{ width: '38px', height: '38px', borderRadius: '50%', border: '2px solid var(--gold-primary)', objectFit: 'cover' }} />
            <div style={{ fontSize: '0.85rem' }}>
              <div style={{ fontWeight: 700, color: '#fff' }}>{currentUser.name}</div>
              <span style={{ color: 'var(--gold-primary)', fontSize: '0.7rem', fontWeight: 800 }}>[{currentUser.role}]</span>
            </div>
          </div>
        </div>
      </header>

      {/* 3. MAIN BODY SWITCHER */}
      <main style={{ flexGrow: 1 }}>

        {/* ================= TAB: HOME ================= */}
        {currentTab === 'home' && (
          <div>
            {/* Hero Section */}
            <section style={{ padding: '80px 30px', position: 'relative', overflow: 'hidden' }}>
              <div style={{ maxWidth: '1240px', margin: '0 auto', display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '50px', alignItems: 'center' }}>
                <div>
                  <div style={{ display: 'inline-block', padding: '6px 18px', borderRadius: '20px', background: 'rgba(212,175,55,0.12)', border: '1px solid var(--gold-primary)', color: 'var(--gold-light)', fontSize: '0.85rem', fontWeight: 700, marginBottom: '20px', letterSpacing: '1.5px' }}>
                    <i className="fa-solid fa-crown me-2"></i> SIÊU SHOWROOM CHÍNH HÃNG HÀNG ĐẦU VIỆT NAM
                  </div>
                  <h1 className="brand-serif" style={{ fontSize: '3.6rem', fontWeight: 900, lineHeight: 1.1, marginBottom: '24px' }}>
                    CHINH PHỤC ĐỈNH CAO <br />
                    <span className="text-gradient-gold">SIÊU MÔ TÔ LUXURY</span>
                  </h1>
                  <p style={{ color: 'var(--text-sub)', fontSize: '1.15rem', marginBottom: '32px', maxWidth: '580px' }}>
                    Hệ thống chuỗi showroom <strong>Duy Truong Moto</strong> chuyên cung ứng các tuyệt tác Superbike: Ducati Panigale V4 S, Kawasaki H2 Carbon, BMW S1000RR M Package. Đặt cọc an toàn qua VNPAY và nhận bàn giao xe sàn trượt VIP tận phòng khách.
                  </p>

                  <div style={{ display: 'flex', gap: '16px' }}>
                    <button className="btn-gold" style={{ fontSize: '1rem', padding: '14px 34px' }} onClick={() => setCurrentTab('products')}>
                      <i className="fa-solid fa-motorcycle"></i> Khám Phá Kho Xe ({products.length})
                    </button>
                    <button className="btn-outline-gold" style={{ fontSize: '1rem', padding: '14px 28px' }} onClick={() => setIsChatOpen(true)}>
                      <i className="fa-solid fa-calendar-check"></i> Đặt Lịch Lái Thử VIP
                    </button>
                  </div>

                  {/* Trust Badges */}
                  <div style={{ display: 'flex', gap: '40px', marginTop: '48px', paddingTop: '28px', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
                    <div>
                      <div className="text-gold" style={{ fontSize: '1.8rem', fontWeight: 900 }}>100%</div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Chính Hãng Nhập Khẩu</div>
                    </div>
                    <div>
                      <div className="text-gold" style={{ fontSize: '1.8rem', fontWeight: 900 }}>03</div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Showroom Flagship</div>
                    </div>
                    <div>
                      <div className="text-gold" style={{ fontSize: '1.8rem', fontWeight: 900 }}>VNPAY</div>
                      <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Cọc Online An Toàn</div>
                    </div>
                  </div>
                </div>

                {/* Hero Showcase Bike */}
                <div style={{ position: 'relative', textAlign: 'center' }}>
                  <div style={{ position: 'absolute', width: '360px', height: '360px', background: 'radial-gradient(circle, rgba(212,175,55,0.2) 0%, transparent 70%)', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', borderRadius: '50%' }}></div>
                  <img 
                    src={INITIAL_PRODUCTS[0].image} 
                    alt="Ducati Panigale V4 S" 
                    style={{ width: '100%', maxHeight: '420px', objectFit: 'cover', borderRadius: '24px', border: '1px solid var(--gold-primary)', boxShadow: 'var(--shadow-deep)' }} 
                  />
                  <div className="glass-box" style={{ position: 'absolute', bottom: '15px', left: '15px', right: '15px', padding: '16px', textAlign: 'left', borderRadius: '16px' }}>
                    <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)', fontWeight: 800 }}>SIÊU PHẨM NỔI BẬT NHẤT</div>
                    <div style={{ fontSize: '1.2rem', fontWeight: 800, color: '#fff' }}>Ducati Panigale V4 S 2026</div>
                    <div style={{ color: 'var(--gold-light)', fontWeight: 700 }}>{formatMoney(920000000)} • 1.103cc • 215.5 HP</div>
                  </div>
                </div>
              </div>
            </section>

            {/* Quick Category Selector */}
            <section style={{ padding: '30px 30px', background: '#0a0c12', borderTop: '1px solid var(--border-subtle)', borderBottom: '1px solid var(--border-subtle)' }}>
              <div style={{ maxWidth: '1240px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
                <span style={{ fontWeight: 800, color: '#fff', fontSize: '1.1rem' }}>DANH MỤC PHÂN PHỐI:</span>
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
            <section style={{ padding: '70px 30px', maxWidth: '1240px', margin: '0 auto' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '36px', borderLeft: '4px solid var(--red-racing)', paddingLeft: '18px' }}>
                <div>
                  <div style={{ color: 'var(--red-racing)', fontWeight: 800, fontSize: '0.85rem', letterSpacing: '1px' }}>
                    <i className="fa-solid fa-fire me-1"></i> TIÊU CHÍ GUEST
                  </div>
                  <h2 className="brand-serif" style={{ fontSize: '2.1rem', fontWeight: 800, color: '#fff', margin: '4px 0' }}>
                    TOP XE BÁN TRÊN 10 CHIẾC (SẮP XẾP GIẢM DẦN)
                  </h2>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                    Dữ liệu tổng hợp từ 3 chi nhánh Showroom Duy Truong Moto (Lượt bán trên 10 chiếc)
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
                    </div>
                    <div className="bike-content">
                      <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)', fontWeight: 800, textTransform: 'uppercase' }}>
                        {bike.brand}
                      </div>
                      <h3 style={{ fontSize: '1.2rem', fontWeight: 800, margin: '6px 0 10px', color: '#fff', minHeight: '44px' }}>
                        {bike.name}
                      </h3>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-sub)', marginBottom: '18px' }}>
                        <i className="fa-solid fa-gauge-high text-gold me-1"></i> {bike.engine}
                      </div>
                      <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                          <div className="price-main">{formatMoney(bike.promotionalPrice || bike.price)}</div>
                          {bike.promotionalPrice && <div className="price-old">{formatMoney(bike.price)}</div>}
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
              <h2 className="brand-serif" style={{ fontSize: '2.4rem', fontWeight: 800, color: '#fff' }}>
                KHO SIÊU MÔ TÔ & PHỤ KIỆN LUXURY
              </h2>
              <p style={{ color: 'var(--text-sub)' }}>
                Hiển thị danh sách 20 xe mới nhất, bán chạy và đánh giá cao nhất có bộ lọc đa năng
              </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: '35px', alignItems: 'start' }}>
              {/* Filter Sidebar */}
              <div className="glass-box" style={{ padding: '26px' }}>
                <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <i className="fa-solid fa-filter"></i> BỘ LỌC TÌM KIẾM
                </h4>

                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Từ khóa tìm kiếm:
                  </label>
                  <input 
                    type="text" 
                    className="luxury-input" 
                    placeholder="Tên xe, pô Akrapovic, AGV..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>

                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Danh mục sản phẩm:
                  </label>
                  <select className="luxury-select" value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
                    {CATEGORIES.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>

                <div style={{ marginBottom: '20px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Thương hiệu:
                  </label>
                  <select className="luxury-select" value={brandFilter} onChange={(e) => setBrandFilter(e.target.value)}>
                    <option value="all">Tất cả thương hiệu</option>
                    <option value="Ducati">Ducati</option>
                    <option value="Kawasaki">Kawasaki</option>
                    <option value="BMW">BMW Motorrad</option>
                    <option value="Harley-Davidson">Harley-Davidson</option>
                    <option value="Akrapovic">Akrapovic</option>
                    <option value="AGV">AGV Helmets</option>
                  </select>
                </div>

                <div style={{ marginBottom: '25px' }}>
                  <label style={{ fontSize: '0.85rem', color: 'var(--text-sub)', fontWeight: 700, display: 'block', marginBottom: '8px' }}>
                    Khoảng giá tối đa: {formatMoney(priceRange.max)}
                  </label>
                  <input 
                    type="range" 
                    min="10000000" 
                    max="2000000000" 
                    step="50000000"
                    value={priceRange.max} 
                    onChange={(e) => setPriceRange({ ...priceRange, max: parseInt(e.target.value) })}
                    style={{ width: '100%', accentColor: 'var(--gold-primary)' }}
                  />
                </div>

                <button className="btn-outline-gold" style={{ width: '100%' }} onClick={() => { setSearchQuery(''); setSelectedCategory('all'); setBrandFilter('all'); setPriceRange({ min: 0, max: 2000000000 }); }}>
                  <i className="fa-solid fa-rotate-left"></i> Đặt Lại Bộ Lọc
                </button>
              </div>

              {/* Product Listing */}
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <span style={{ color: 'var(--text-sub)' }}>
                    Tìm thấy <strong style={{ color: 'var(--gold-primary)' }}>{filteredProducts.length}</strong> dòng xe & phụ kiện phù hợp
                  </span>
                </div>

                {filteredProducts.length === 0 ? (
                  <div className="glass-box" style={{ padding: '60px', textAlign: 'center' }}>
                    <i className="fa-solid fa-motorcycle" style={{ fontSize: '40px', color: 'var(--text-muted)', marginBottom: '16px' }}></i>
                    <h3>Không tìm thấy sản phẩm nào!</h3>
                    <p style={{ color: 'var(--text-muted)' }}>Vui lòng thay đổi từ khóa hoặc mức giá lọc.</p>
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
                        </div>
                        <div className="bike-content">
                          <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)', fontWeight: 800 }}>{bike.brand}</div>
                          <h3 style={{ fontSize: '1.15rem', fontWeight: 800, margin: '6px 0 10px', color: '#fff', minHeight: '44px' }}>
                            {bike.name}
                          </h3>
                          <div style={{ fontSize: '0.85rem', color: 'var(--text-sub)', marginBottom: '14px' }}>
                            {bike.engine}
                          </div>
                          <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div className="price-main">{formatMoney(bike.promotionalPrice || bike.price)}</div>
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
                HỆ THỐNG PHÂN PHỐI SIÊU XE TOÀN QUỐC
              </div>
              <h2 className="brand-serif" style={{ fontSize: '2.5rem', fontWeight: 800, color: '#fff' }}>
                CHUỖI SHOWROOM DUY TRUONG MOTO
              </h2>
              <p style={{ color: 'var(--text-sub)' }}>
                Đến trực tiếp showroom để chiêm ngưỡng và đăng ký lái thử các siêu môtô
              </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '30px' }}>
              {SHOWROOMS.map(store => (
                <div key={store.id} className="glass-box" style={{ padding: '30px', display: 'flex', flexDirection: 'column' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '18px', marginBottom: '20px' }}>
                    <img src={store.avatar} alt={store.name} style={{ width: '68px', height: '68px', borderRadius: '50%', border: '2px solid var(--gold-primary)', objectFit: 'cover' }} />
                    <div>
                      <h4 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#fff', marginBottom: '4px' }}>{store.name}</h4>
                      <div style={{ color: 'var(--gold-primary)', fontSize: '0.85rem' }}>
                        <i className="fa-solid fa-star"></i> {store.rating} / 5.0 (Đánh giá chi nhánh)
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

        {/* ================= TAB: ORDERS (LỊCH SỬ ĐƠN HÀNG) ================= */}
        {currentTab === 'orders' && (
          <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '50px 30px' }}>
            <h2 className="brand-serif" style={{ fontSize: '2.2rem', fontWeight: 800, color: '#fff', marginBottom: '24px' }}>
              <i className="fa-solid fa-clock-rotate-left text-gold me-2"></i> LỊCH SỬ ĐẶT MUA XE & BÀN GIAO
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
                          <h4 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#fff' }}>{order.productName}</h4>
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
                        <div className="price-main" style={{ color: 'var(--gold-primary)' }}>{formatMoney(order.totalAmount)}</div>
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
                <h2 className="brand-serif" style={{ fontSize: '2.2rem', fontWeight: 800, color: '#fff', marginTop: '6px' }}>
                  QUẢN TRỊ SHOWROOM QUẬN 1 (TP.HCM)
                </h2>
              </div>
              <button className="btn-gold" onClick={() => setIsAddBikeModalOpen(true)}>
                <i className="fa-solid fa-plus-circle"></i> Nhập Thêm Siêu Xe Mới
              </button>
            </div>

            {/* Metrics */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '24px', marginBottom: '36px' }}>
              <div className="glass-box" style={{ padding: '24px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 700 }}>DOANH THU ĐÃ BÀN GIAO</div>
                <div className="text-gradient-gold" style={{ fontSize: '2rem', fontWeight: 900, margin: '8px 0' }}>{formatMoney(1950000000)}</div>
                <div style={{ color: '#28a745', fontSize: '0.8rem' }}><i className="fa-solid fa-arrow-up"></i> +18.5% so với tháng trước</div>
              </div>
              <div className="glass-box" style={{ padding: '24px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 700 }}>XE TRƯNG BÀY TẠI KHO</div>
                <div style={{ fontSize: '2rem', fontWeight: 900, color: '#fff', margin: '8px 0' }}>{products.filter(p => p.storeId === 1).length} Dòng Xe</div>
                <div style={{ color: 'var(--gold-light)', fontSize: '0.8rem' }}>Đầy đủ phụ kiện Akrapovic & pô độ</div>
              </div>
              <div className="glass-box" style={{ padding: '24px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 700 }}>VÍ TIỀN SHOWROOM</div>
                <div style={{ fontSize: '2rem', fontWeight: 900, color: '#fff', margin: '8px 0' }}>{formatMoney(currentUser.wallet || 150000000)}</div>
                <div style={{ color: 'var(--text-sub)', fontSize: '0.8rem' }}>Sẵn sàng rút về tài khoản ngân hàng</div>
              </div>
            </div>

            {/* Inventory table */}
            <div className="glass-box" style={{ padding: '28px' }}>
              <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '20px' }}>DANH SÁCH XE CỦA SHOWROOM</h4>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.15)', color: 'var(--text-muted)' }}>
                      <th style={{ padding: '12px' }}>HÌNH ẢNH</th>
                      <th style={{ padding: '12px' }}>TÊN SIÊU MÔ TÔ</th>
                      <th style={{ padding: '12px' }}>GIÁ NIÊM YẾT</th>
                      <th style={{ padding: '12px' }}>TỒN KHO</th>
                      <th style={{ padding: '12px' }}>ĐÃ BÁN</th>
                      <th style={{ padding: '12px' }}>TRẠNG THÁI BÁN</th>
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

        {/* ================= TAB: ADMIN (TỔNG GIÁM ĐỐC) ================= */}
        {currentTab === 'admin' && (
          <div style={{ maxWidth: '1240px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ marginBottom: '32px' }}>
              <span style={{ background: 'var(--red-racing)', color: '#fff', padding: '4px 10px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 800 }}>TỔNG QUẢN TRỊ HỆ THỐNG</span>
              <h2 className="brand-serif" style={{ fontSize: '2.4rem', fontWeight: 800, color: '#fff', marginTop: '6px' }}>
                BẢNG ĐIỀU HÀNH TOÀN CHUỖI DUY TRUONG MOTO
              </h2>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '20px', marginBottom: '36px' }}>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>TỔNG GMV HỆ THỐNG</div>
                <div className="text-gradient-gold" style={{ fontSize: '1.8rem', fontWeight: 900, margin: '6px 0' }}>{formatMoney(8450000000)}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Toàn bộ 3 chi nhánh</div>
              </div>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>HOA HỒNG SÀN (5%)</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 900, color: '#28a745', margin: '6px 0' }}>{formatMoney(422500000)}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Lợi nhuận sàn thu về</div>
              </div>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>SỐ LƯỢNG SHOWROOM</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 900, color: '#fff', margin: '6px 0' }}>03 Điểm Bán</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--gold-primary)' }}>TP.HCM, HN, Đà Nẵng</div>
              </div>
              <div className="glass-box" style={{ padding: '22px' }}>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 700 }}>THÀNH VIÊN ĐÃ KÍCH HOẠT</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 900, color: 'var(--cyan-neon)', margin: '6px 0' }}>1.240 Biker</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Xác thực qua OTP Email</div>
              </div>
            </div>

            {/* Showroom Approval & Commission Management */}
            <div className="glass-box" style={{ padding: '28px' }}>
              <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '20px' }}>
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

        {/* ================= TAB: SHIPPER (LOGISTICS VẬN TẢI CHUYÊN DỤNG) ================= */}
        {currentTab === 'shipper' && (
          <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '50px 30px' }}>
            <div style={{ marginBottom: '32px' }}>
              <span style={{ background: 'var(--cyan-neon)', color: '#000', padding: '4px 10px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 800 }}>ĐỘI XE SÀN TRƯỢT VIP</span>
              <h2 className="brand-serif" style={{ fontSize: '2.4rem', fontWeight: 800, color: '#fff', marginTop: '6px' }}>
                ĐIỀU PHỐI VẬN CHUYỂN & BÀN GIAO SIÊU XE
              </h2>
              <p style={{ color: 'var(--text-sub)' }}>Tài xế chuyên dụng: {currentUser.name} ({currentUser.phone})</p>
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

      {/* 4. SHOPPING CART DRAWER (Database Synced Simulation) */}
      {isCartOpen && (
        <div className="modal-overlay" onClick={() => setIsCartOpen(false)}>
          <div 
            className="glass-box" 
            onClick={(e) => e.stopPropagation()} 
            style={{ position: 'fixed', top: 0, right: 0, bottom: 0, width: '450px', borderRadius: 0, borderLeft: '1px solid var(--gold-primary)', padding: '32px', display: 'flex', flexDirection: 'column', background: '#0a0d14' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '16px', marginBottom: '20px' }}>
              <h3 className="brand-serif" style={{ fontSize: '1.4rem', fontWeight: 800, color: '#fff' }}>
                <i className="fa-solid fa-cart-shopping text-gold me-2"></i> GIỎ HÀNG CỦA BẠN
              </h3>
              <button onClick={() => setIsCartOpen(false)} style={{ background: 'transparent', border: 'none', color: '#fff', fontSize: '20px', cursor: 'pointer' }}>✕</button>
            </div>

            {cart.length === 0 ? (
              <div style={{ textAlign: 'center', margin: 'auto 0', color: 'var(--text-muted)' }}>
                <i className="fa-solid fa-cart-arrow-down" style={{ fontSize: '48px', marginBottom: '16px' }}></i>
                <h4>Giỏ hàng của bạn đang trống</h4>
                <p style={{ fontSize: '0.85rem' }}>Hãy chọn mẫu siêu xe ưa thích để đặt hàng.</p>
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
                  <span className="text-gradient-gold" style={{ fontSize: '1.4rem', fontWeight: 900 }}>{formatMoney(cartSubtotal)}</span>
                </div>
                <button 
                  className="btn-gold" 
                  style={{ width: '100%', padding: '14px' }} 
                  onClick={() => { setIsCartOpen(false); setIsCheckoutOpen(true); }}
                >
                  <i className="fa-solid fa-credit-card"></i> TIẾN HÀNH ĐẶT HÀNG & CỌC XE
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* 5. CHECKOUT MODAL */}
      {isCheckoutOpen && (
        <div className="modal-overlay" onClick={() => setIsCheckoutOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '14px' }}>
              <h3 className="brand-serif" style={{ fontSize: '1.6rem', fontWeight: 800, color: '#fff' }}>
                <i className="fa-solid fa-file-invoice-dollar text-gold me-2"></i> THÔNG TIN ĐẶT HÀNG & CỌC XE
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
                  <div className="price-main" style={{ color: 'var(--gold-primary)' }}>{formatMoney(cartSubtotal + checkoutData.deliveryFee)}</div>
                </div>
                <button className="btn-gold" style={{ padding: '14px 32px' }} onClick={handlePlaceOrder}>
                  Xác Nhận Đặt Cọc
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 6. VNPAY QR CODE PAYMENT MODAL SIMULATION */}
      {isVnPayModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '480px', textAlign: 'center', background: '#fff', color: '#000' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div style={{ fontWeight: 900, color: '#005baa', fontSize: '1.3rem' }}>VNPAY<span style={{ color: '#ed1c24' }}>QR</span></div>
              <span style={{ fontSize: '0.85rem', color: '#666' }}>Hết hạn sau: 14:59</span>
            </div>

            <div style={{ background: '#f8f9fa', padding: '16px', borderRadius: '12px', border: '1px solid #e9ecef', marginBottom: '20px' }}>
              <div style={{ fontSize: '0.9rem', color: '#666' }}>Đơn hàng tại: <strong>DUY TRUONG MOTO</strong></div>
              <div style={{ fontSize: '1.6rem', fontWeight: 900, color: '#ed1c24', margin: '6px 0' }}>
                {formatMoney(cartSubtotal + checkoutData.deliveryFee)}
              </div>
              <div style={{ fontSize: '0.8rem', color: '#888' }}>Mã thanh toán: DTM-PAY-VNPAY2026</div>
            </div>

            {/* Generated QR Code Simulation */}
            <div style={{ display: 'inline-block', padding: '16px', background: '#fff', border: '2px solid #005baa', borderRadius: '16px', marginBottom: '20px' }}>
              <img 
                src="https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=https://duytruongmoto.com/vnpay-pay-demo" 
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

      {/* 7. BIKE DETAIL & REVIEWS MODAL */}
      {activeProductDetail && (
        <div className="modal-overlay" onClick={() => setActiveProductDetail(null)}>
          <div className="modal-content" style={{ maxWidth: '850px' }} onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <span style={{ color: 'var(--gold-primary)', fontWeight: 800 }}>{activeProductDetail.brand} • {activeProductDetail.storeName}</span>
              <button onClick={() => setActiveProductDetail(null)} style={{ background: 'transparent', border: 'none', color: '#fff', fontSize: '20px', cursor: 'pointer' }}>✕</button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px', marginBottom: '30px' }}>
              <div>
                <img src={activeProductDetail.image} alt={activeProductDetail.name} style={{ width: '100%', borderRadius: '16px', border: '1px solid var(--gold-primary)' }} />
              </div>
              <div>
                <h3 className="brand-serif" style={{ fontSize: '1.6rem', fontWeight: 800, color: '#fff', marginBottom: '10px' }}>
                  {activeProductDetail.name}
                </h3>
                <div className="price-main" style={{ color: 'var(--gold-primary)', fontSize: '1.6rem', marginBottom: '16px' }}>
                  {formatMoney(activeProductDetail.promotionalPrice || activeProductDetail.price)}
                </div>

                <div style={{ background: '#121520', padding: '14px', borderRadius: '12px', fontSize: '0.85rem', color: 'var(--text-sub)', marginBottom: '20px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <div><strong style={{ color: '#fff' }}>Động cơ:</strong> {activeProductDetail.engine}</div>
                  <div><strong style={{ color: '#fff' }}>Công suất:</strong> {activeProductDetail.power}</div>
                  <div><strong style={{ color: '#fff' }}>Màu sắc:</strong> {activeProductDetail.color}</div>
                  <div><strong style={{ color: '#fff' }}>Tồn kho:</strong> {activeProductDetail.quantity} chiếc sẵn sàng giao</div>
                </div>

                <button className="btn-gold" style={{ width: '100%', padding: '14px' }} onClick={() => { handleAddToCart(activeProductDetail); setActiveProductDetail(null); }}>
                  <i className="fa-solid fa-cart-plus"></i> Thêm Vào Giỏ Hàng
                </button>
              </div>
            </div>

            {/* Prompt Requirement: Đánh giá bình luận tối thiểu 50 ký tự + hình ảnh */}
            <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '24px' }}>
              <h4 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '16px' }}>
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
                  placeholder="Chia sẻ cảm giác lái, gia tốc, tiếng pô, độ êm ái phuộc, dịch vụ bàn giao tại showroom Duy Truong Moto... (Tối thiểu 50 ký tự)"
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
                        <span style={{ color: '#28a745', fontSize: '0.7rem' }}>✓ Đã mua xe</span>
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

      {/* 8. WEBSOCKET LIVE CHAT WIDGET */}
      <div className="chat-fab" onClick={() => setIsChatOpen(!isChatOpen)} title="Chat trực tuyến với chuyên viên Showroom">
        <i className="fa-solid fa-headset"></i>
      </div>

      {isChatOpen && (
        <div className="chat-window">
          <div style={{ padding: '16px', background: 'var(--gold-gradient)', color: '#000', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div style={{ width: '10px', height: '10px', borderRadius: '50%', background: '#28a745' }}></div>
              <strong style={{ fontSize: '0.95rem' }}>Showroom Duy Truong Moto</strong>
            </div>
            <button onClick={() => setIsChatOpen(false)} style={{ background: 'transparent', border: 'none', fontWeight: 800, cursor: 'pointer', fontSize: '18px' }}>✕</button>
          </div>

          <div style={{ flexGrow: 1, padding: '16px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {chatMessages.map((msg, i) => (
              <div key={i} style={{ alignSelf: msg.isBot ? 'flex-start' : 'flex-end', maxWidth: '85%' }}>
                <div style={{ 
                  background: msg.isBot ? '#181c28' : 'var(--gold-gradient)', 
                  color: msg.isBot ? '#fff' : '#000', 
                  padding: '10px 14px', 
                  borderRadius: '14px', 
                  fontSize: '0.85rem',
                  boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
                }}>
                  <div style={{ fontSize: '0.7rem', fontWeight: 800, opacity: 0.7, marginBottom: '3px' }}>{msg.sender} • {msg.time}</div>
                  {msg.text}
                </div>
              </div>
            ))}
          </div>

          <div style={{ padding: '12px', background: '#0a0c12', borderTop: '1px solid rgba(255,255,255,0.1)', display: 'flex', gap: '8px' }}>
            <input 
              type="text" 
              className="luxury-input" 
              placeholder="Nhập câu hỏi tư vấn xe hoặc hẹn lái thử..." 
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
              style={{ padding: '8px 12px', fontSize: '0.85rem' }}
            />
            <button className="btn-gold" style={{ padding: '8px 16px' }} onClick={handleSendMessage}>
              <i className="fa-solid fa-paper-plane"></i>
            </button>
          </div>
        </div>
      )}

      {/* 9. FOOTER */}
      <footer style={{ background: '#040507', borderTop: '1px solid var(--border-subtle)', padding: '50px 30px 25px', marginTop: 'auto' }}>
        <div style={{ maxWidth: '1240px', margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '40px' }}>
          <div>
            <div className="brand-serif" style={{ fontSize: '1.4rem', fontWeight: 900, marginBottom: '14px' }}>
              <span className="text-gradient-gold">DUY TRUONG</span> MOTO
            </div>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', lineHeight: 1.7 }}>
              Hệ thống chuỗi showroom phân phối chính ngạch siêu môtô và xe phân khối lớn hàng đầu Việt Nam. Cam kết trải nghiệm thượng lưu, kiểm định hải quan minh bạch và bảo hành 3 năm chính hãng.
            </p>
          </div>

          <div>
            <h5 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '16px' }}>HỆ THỐNG SHOWROOM TOÀN QUỐC</h5>
            <ul style={{ listStyle: 'none', padding: 0, fontSize: '0.85rem', color: 'var(--text-sub)', display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <li><i className="fa-solid fa-location-dot text-racing me-2"></i> <strong>Showroom Q1 (TP.HCM):</strong> 88 Nguyễn Hữu Cảnh, P. 22</li>
              <li><i className="fa-solid fa-location-dot text-racing me-2"></i> <strong>Showroom Cầu Giấy (Hà Nội):</strong> 168 Trần Duy Hưng</li>
              <li><i className="fa-solid fa-location-dot text-racing me-2"></i> <strong>Showroom Đà Nẵng:</strong> 52 Nguyễn Văn Linh, Hải Châu</li>
            </ul>
          </div>

          <div>
            <h5 style={{ color: 'var(--gold-primary)', fontWeight: 800, marginBottom: '16px' }}>CHÍNH SÁCH ĐỒ ÁN (3 THÀNH VIÊN)</h5>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.8 }}>
              <div>• <strong>Thành viên 1:</strong> Kiến trúc 3 tầng, Spring Security 6, JWT, OTP Email, Admin</div>
              <div>• <strong>Thành viên 2:</strong> Giao diện Dark Luxury, Guest &gt; 10 xe, Giỏ hàng DB, Đánh giá &gt;= 50 ký tự</div>
              <div>• <strong>Thành viên 3:</strong> Showroom Vendor, Phân hệ Shipper, VNPAY QR, WebSocket Chat</div>
            </div>
          </div>
        </div>

        <div style={{ maxWidth: '1240px', margin: '40px auto 0', paddingTop: '20px', borderTop: '1px solid rgba(255,255,255,0.08)', textAlign: 'center', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          © 2026 Duy Truong Moto Luxury E-Commerce System. All Rights Reserved. Sẵn sàng Deploy Production lên Vercel / Netlify / Render.
        </div>
      </footer>

    </div>
  );
}
