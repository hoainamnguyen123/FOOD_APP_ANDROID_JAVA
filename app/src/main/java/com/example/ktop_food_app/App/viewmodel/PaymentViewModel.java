package com.example.ktop_food_app.App.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ktop_food_app.App.model.data.entity.CartItem;
import com.example.ktop_food_app.App.model.data.entity.Order;
import com.example.ktop_food_app.App.model.data.entity.PaymentItem;
import com.example.ktop_food_app.App.model.data.remote.FirebasePaymentData;
import com.example.ktop_food_app.App.model.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;

// Lớp PaymentViewModel quản lý dữ liệu và logic nghiệp vụ cho PaymentActivity
public class PaymentViewModel extends ViewModel {
    private final PaymentRepository repository;
    // LiveData để lưu trữ và thông báo thay đổi về địa chỉ người dùng
    private final MutableLiveData<String> address = new MutableLiveData<>();
    // LiveData để lưu trữ và thông báo thay đổi về tên người dùng
    private final MutableLiveData<String> username = new MutableLiveData<>();
    // LiveData để lưu trữ và thông báo thay đổi về tổng tiền
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    // LiveData để lưu trữ và thông báo thay đổi về giá trị giảm giá
    private final MutableLiveData<Double> discount = new MutableLiveData<>(0.0);
    // LiveData để lưu trữ và thông báo thay đổi về tổng tiền sau khi áp dụng giảm giá
    private final MutableLiveData<Double> finalPrice = new MutableLiveData<>(0.0);
    // LiveData để lưu trữ và thông báo thay đổi về danh sách món ăn trong giỏ hàng
    private final MutableLiveData<List<PaymentItem>> paymentItems = new MutableLiveData<>(new ArrayList<>());
    // LiveData để thông báo lỗi hoặc thông điệp thành công
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    // LiveData để thông báo trạng thái thanh toán (thành công hay thất bại)
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();
    // LiveData để lưu trữ số lần hủy đơn hàng
    private final MutableLiveData<Long> cancelledCount = new MutableLiveData<>(0L);

    // Constructor: Nhận PaymentRepository để giao tiếp với dữ liệu
    public PaymentViewModel(PaymentRepository repository) {
        this.repository = repository;
    }

    // Getter để PaymentActivity quan sát địa chỉ
    public LiveData<String> getAddress() {
        return address;
    }

    // Getter để PaymentActivity quan sát tên người dùng
    public LiveData<String> getUsername() {
        return username;
    }

    // Getter để PaymentActivity quan sát tổng tiền
    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    // Getter để PaymentActivity quan sát giá trị giảm giá
    public LiveData<Double> getDiscount() {
        return discount;
    }

    // Getter để PaymentActivity quan sát tổng tiền sau giảm giá
    public LiveData<Double> getFinalPrice() {
        return finalPrice;
    }

    // Getter để PaymentActivity quan sát danh sách món ăn
    public LiveData<List<PaymentItem>> getPaymentItems() {
        return paymentItems;
    }

    // Getter để PaymentActivity quan sát thông điệp lỗi/thành công
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Getter để PaymentActivity quan sát trạng thái thanh toán
    public LiveData<Boolean> getPaymentSuccess() {
        return paymentSuccess;
    }

    // Getter để PaymentActivity quan sát số lần hủy đơn hàng
    public LiveData<Long> getCancelledCount() {
        return cancelledCount;
    }

    // Phương thức cập nhật giá cuối cùng
    private void updateFinalPrice() {
        Double price = totalPrice.getValue();
        Double disc = discount.getValue();
        if (price != null && disc != null) {
            // Đảm bảo finalPrice không nhỏ hơn 0
            double finalAmount = Math.max(0, price - disc);
            finalPrice.setValue(finalAmount);
        }
    }

    // Phương thức lấy thông tin người dùng từ repository
    public void loadUserInfo(String userId) {
        repository.loadUserInfo(userId, new FirebasePaymentData.OnUserInfoLoadedListener() {
            @Override
            public void onUserInfoLoaded(String userAddress, String userName, String phoneNumber) {
                // Cập nhật địa chỉ và tên người dùng vào LiveData
                address.setValue(userAddress != null && !userAddress.isEmpty() ? userAddress : "Chưa có địa chỉ, vui lòng cập nhật!");
                username.setValue(userName != null && !userName.isEmpty() ? userName + ", " + phoneNumber : "Khách hàng");
            }

            @Override
            public void onUserInfoError(String error) {
                // Thông báo lỗi nếu không lấy được thông tin
                errorMessage.setValue(error);
            }
        });
    }

    // Phương thức kiểm tra số lần hủy đơn hàng
    public void checkCancelledCount(String userId) {
        repository.checkCancelledCount(userId, new FirebasePaymentData.OnCancelledCountCheckedListener() {
            @Override
            public void onCancelledCountChecked(long count) {
                cancelledCount.setValue(count);
            }

            @Override
            public void onCancelledCountError(String error) {
                errorMessage.setValue(error);
                cancelledCount.setValue(0L); // Mặc định là 0 nếu có lỗi
            }
        });
    }

    // Phương thức hiển thị danh sách món ăn trong giỏ hàng
    public void displayCartItems(List<CartItem> cartItems) {
        List<PaymentItem> items = new ArrayList<>();
        double price = 0.0;
        // Chuyển đổi CartItem thành PaymentItem để hiển thị trên RecyclerView
        for (CartItem cartItem : cartItems) {
            double itemTotalPrice = cartItem.getPrice() * cartItem.getQuantity();
            price += itemTotalPrice;
            items.add(new PaymentItem(
                    cartItem.getName(),
                    cartItem.getPrice(),
                    cartItem.getQuantity(),
                    itemTotalPrice,
                    cartItem.getImagePath()
            ));
        }
        // Cập nhật danh sách món ăn và tổng tiền vào LiveData
        paymentItems.setValue(items);
        totalPrice.setValue(price);
        updateFinalPrice(); // Cập nhật giá cuối cùng
    }

    // Phương thức áp dụng mã giảm giá
    public void applyVoucherCode(String userId, String voucherCode) {
        // Kiểm tra nếu mã rỗng thì thông báo lỗi
        if (voucherCode.isEmpty()) {
            errorMessage.setValue("Vui lòng nhập mã giảm giá!");
            return;
        }
        // Gọi repository để kiểm tra mã giảm giá
        repository.applyVoucherCode(userId, voucherCode, new FirebasePaymentData.OnVoucherAppliedListener() {
            @Override
            public void onVoucherApplied(double appliedDiscount) {
                // Cập nhật giá trị giảm giá và thông báo thành công
                discount.setValue(appliedDiscount);
                updateFinalPrice(); // Cập nhật giá cuối cùng sau khi áp dụng giảm giá
                errorMessage.setValue("Áp dụng mã giảm giá thành công!");
            }

            @Override
            public void onVoucherError(String error) {
                // Nếu mã không hợp lệ, đặt giảm giá về 0 và thông báo lỗi
                discount.setValue(0.0);
                updateFinalPrice(); // Cập nhật giá cuối cùng
                errorMessage.setValue(error);
            }
        });
    }

    // Phương thức xử lý thanh toán
    public void processPayment(Order order, String userId) {
        // Gọi repository để lưu đơn hàng
        repository.processPayment(order, userId, new FirebasePaymentData.OnPaymentProcessedListener() {
            @Override
            public void onPaymentSuccess() {
                // Thông báo thanh toán thành công
                paymentSuccess.setValue(true);
                errorMessage.setValue("Đặt hàng thành công!");
            }

            @Override
            public void onPaymentFailure(String error) {
                // Thông báo thanh toán thất bại
                paymentSuccess.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
}