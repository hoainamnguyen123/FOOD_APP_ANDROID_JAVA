package com.example.ktop_food_app.App.model.repository;
import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.model.data.remote.FirebasePaymentData;

// Lớp PaymentRepository đóng vai trò trung gian giữa PaymentViewModel và FirebasePaymentData
public class PaymentRepository {
    private final FirebasePaymentData firebasePaymentData;

    // Constructor: Nhận instance của FirebasePaymentData để giao tiếp với dữ liệu
    public PaymentRepository(FirebasePaymentData firebasePaymentData) {
        this.firebasePaymentData = firebasePaymentData;
    }

    // Phương thức chuyển tiếp yêu cầu lấy thông tin người dùng đến FirebasePaymentData
    public void loadUserInfo(String userId, FirebasePaymentData.OnUserInfoLoadedListener listener) {
        firebasePaymentData.loadUserInfo(userId, listener);
    }

    // Phương thức chuyển tiếp yêu cầu kiểm tra số lần hủy đơn hàng
    public void checkCancelledCount(String userId, FirebasePaymentData.OnCancelledCountCheckedListener listener) {
        firebasePaymentData.checkCancelledCount(userId, listener);
    }

    // Phương thức chuyển tiếp yêu cầu áp dụng mã giảm giá đến FirebasePaymentData
    public void applyVoucherCode(String userId, String voucherCode, FirebasePaymentData.OnVoucherAppliedListener listener) {
        firebasePaymentData.applyVoucherCode(userId, voucherCode, listener);
    }

    // Phương thức chuyển tiếp yêu cầu xử lý thanh toán đến FirebasePaymentData
    public void processPayment(Order order, String userId, FirebasePaymentData.OnPaymentProcessedListener listener) {
        firebasePaymentData.processPayment(order, userId, listener);
    }
}