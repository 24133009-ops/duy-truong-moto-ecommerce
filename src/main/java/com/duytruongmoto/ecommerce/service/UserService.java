package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Address;
import com.duytruongmoto.ecommerce.entity.Role;
import com.duytruongmoto.ecommerce.entity.User;
import com.duytruongmoto.ecommerce.repository.AddressRepository;
import com.duytruongmoto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailOtpService emailOtpService;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User registerUser(String firstname, String lastname, String email, String phone, String rawPassword) {
        String cleanEmail = email.trim().toLowerCase();
        String cleanPhone = (phone != null && !phone.isBlank()) ? phone.trim() : null;

        User existingByEmail = userRepository.findByEmail(cleanEmail).orElse(null);
        if (existingByEmail != null) {
            if (Boolean.TRUE.equals(existingByEmail.getIsEmailActive())) {
                throw new IllegalArgumentException("Email " + cleanEmail + " đã được đăng ký và kích hoạt trong hệ thống Duy Truong Motor! Vui lòng đăng nhập.");
            }
            // Tài khoản chưa kích hoạt OTP: cho phép cập nhật thông tin và cấp mã OTP mới
            if (cleanPhone != null) {
                User phoneOwner = userRepository.findByPhone(cleanPhone).orElse(null);
                if (phoneOwner != null && !phoneOwner.getId().equals(existingByEmail.getId())) {
                    throw new IllegalArgumentException("Số điện thoại " + cleanPhone + " đã được liên kết với một tài khoản khác!");
                }
            }
            String otp = emailOtpService.generateOtp();
            existingByEmail.setFirstname(firstname);
            existingByEmail.setLastname(lastname);
            existingByEmail.setPhone(cleanPhone);
            existingByEmail.setPassword(passwordEncoder.encode(rawPassword));
            existingByEmail.setOtpCode(otp);
            existingByEmail.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            User saved = userRepository.save(existingByEmail);
            emailOtpService.sendOtpEmail(cleanEmail, otp, "Kích hoạt tài khoản thành viên");
            return saved;
        }

        // Kiểm tra trùng số điện thoại với tài khoản khác
        if (cleanPhone != null && userRepository.existsByPhone(cleanPhone)) {
            throw new IllegalArgumentException("Số điện thoại " + cleanPhone + " đã được đăng ký cho một tài khoản khác trong hệ thống!");
        }

        String otp = emailOtpService.generateOtp();
        String slug = (firstname + "-" + lastname).toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + System.currentTimeMillis();

        User user = User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .slug(slug)
                .email(cleanEmail)
                .phone(cleanPhone)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ROLE_USER)
                .isEmailActive(false)
                .otpCode(otp)
                .otpExpiry(LocalDateTime.now().plusMinutes(5))
                .avatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80")
                .point(0L)
                .build();

        User saved = userRepository.save(user);
        emailOtpService.sendOtpEmail(cleanEmail, otp, "Kích hoạt tài khoản thành viên");
        return saved;
    }

    private final java.util.concurrent.ConcurrentHashMap<String, Integer> otpAttempts = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, Long> otpSendCount = new java.util.concurrent.ConcurrentHashMap<>();

    public void resendRegistrationOtp(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Vui lòng cung cấp email hợp lệ!");
        }
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản với email: " + email));

        if (Boolean.TRUE.equals(user.getIsEmailActive())) {
            throw new IllegalArgumentException("Tài khoản của bạn đã được kích hoạt thành công từ trước. Vui lòng đăng nhập!");
        }

        String otp = emailOtpService.generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailOtpService.sendOtpEmail(user.getEmail(), otp, "Kích hoạt tài khoản thành viên");
    }

    @Transactional
    public boolean verifyOtp(String email, String otpCode) {
        if (email == null || otpCode == null) return false;

        int currentAttempts = otpAttempts.getOrDefault(email.toLowerCase(), 0);
        if (currentAttempts >= 5) {
            Optional<User> uOpt = userRepository.findByEmail(email.trim().toLowerCase());
            if (uOpt.isPresent()) {
                User u = uOpt.get();
                u.setOtpCode(null);
                u.setOtpExpiry(null);
                userRepository.save(u);
            }
            otpAttempts.remove(email.toLowerCase());
            throw new IllegalArgumentException("Bạn đã nhập sai OTP quá 5 lần. Mã xác thực đã bị hủy vì lý do bảo mật. Vui lòng yêu cầu mã mới!");
        }

        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // CHỈ CHẤP NHẬN MÃ OTP THẬT ĐƯỢC GỬI VỀ EMAIL CỦA KHÁCH HÀNG (CÒN HIỆU LỰC 5 PHÚT)
            boolean isValid = user.getOtpCode() != null
                    && user.getOtpCode().trim().equals(otpCode.trim())
                    && user.getOtpExpiry() != null
                    && user.getOtpExpiry().isAfter(LocalDateTime.now());
            if (isValid) {
                user.setIsEmailActive(true);
                user.setOtpCode(null);
                user.setOtpExpiry(null);
                userRepository.save(user);
                otpAttempts.remove(email.toLowerCase());
                return true;
            } else {
                otpAttempts.put(email.toLowerCase(), currentAttempts + 1);
            }
        }
        return false;
    }

    @Transactional
    public void sendForgotPasswordOtp(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập địa chỉ email!");
        }
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với email này!"));

        String otp = emailOtpService.generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailOtpService.sendOtpEmail(user.getEmail(), otp, "Khôi phục mật khẩu đăng nhập");
    }

    @Transactional
    public boolean resetPasswordWithOtp(String email, String otpCode, String newPassword) {
        if (email == null || otpCode == null) return false;
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // CHỈ CHẤP NHẬN MÃ OTP THẬT ĐƯỢC GỬI VỀ EMAIL (CÒN HIỆU LỰC 5 PHÚT)
            boolean isValid = user.getOtpCode() != null
                    && user.getOtpCode().trim().equals(otpCode.trim())
                    && user.getOtpExpiry() != null
                    && user.getOtpExpiry().isAfter(LocalDateTime.now());
            if (isValid) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setOtpCode(null);
                user.setOtpExpiry(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public User updateProfile(Long userId, String firstname, String lastname, String phone, String idCard, String avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhone(phone);
        user.setIdCard(idCard);
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public Address addAddress(Long userId, String recipientName, String phone, String streetAddress, String city, boolean isDefault) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        List<Address> existing = addressRepository.findByUserId(userId);
        if (existing.size() >= 6) {
            throw new IllegalStateException("Bạn chỉ có thể lưu tối đa 6 địa chỉ nhận hàng!");
        }

        if (isDefault || existing.isEmpty()) {
            existing.forEach(a -> {
                a.setIsDefault(false);
                addressRepository.save(a);
            });
            isDefault = true;
        }

        Address address = Address.builder()
                .user(user)
                .recipientName(recipientName)
                .phone(phone)
                .streetAddress(streetAddress)
                .city(city)
                .isDefault(isDefault)
                .build();

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        addressRepository.findById(addressId).ifPresent(addr -> {
            if (addr.getUser().getId().equals(userId)) {
                addressRepository.delete(addr);
            }
        });
    }


    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllUsers();
        }
        return userRepository.searchUsers(keyword.trim());
    }

    @Transactional
    public User toggleLock(Long userId, String currentAdminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        if (user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
            throw new IllegalArgumentException("Admin không thể tự khóa tài khoản của chính mình!");
        }

        user.setIsBlocked(!Boolean.TRUE.equals(user.getIsBlocked()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
