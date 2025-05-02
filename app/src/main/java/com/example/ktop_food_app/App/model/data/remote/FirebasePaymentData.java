package com.example.ktop_food_app.App.model.data.remote;

import com.example.ktop_food_app.App.model.data.entity.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Lớp FirebasePaymentData chịu trách nhiệm giao tiếp trực tiếp với Firebase Realtime Database
public class FirebasePaymentData {
    private final DatabaseReference databaseReference;
    private String appliedVoucherCode; // Lưu tạm mã giảm giá đã áp dụng trong phiên thanh toán

    // Constructor: Khởi tạo tham chiếu đến Firebase Realtime Database với URL cụ thể
    public FirebasePaymentData() {
        databaseReference = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        appliedVoucherCode = null; // Khởi tạo mã giảm giá là null
    }

    // Phương thức lấy thông tin người dùng từ Firebase
    public void loadUserInfo(String userId, OnUserInfoLoadedListener listener) {
        // Truy vấn node users/{userId}/profile để lấy thông tin người dùng
        databaseReference.child("users").child(userId).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy địa chỉ, tên, và số điện thoại từ dữ liệu trả về
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String username = dataSnapshot.child("displayName").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phone").getValue(String.class);
                    // Gọi callback để thông báo thành công và trả về thông tin
                    listener.onUserInfoLoaded(address, username, phoneNumber);
                } else {
                    // Nếu không tìm thấy thông tin, thông báo lỗi
                    listener.onUserInfoError("Không tìm thấy thông tin người dùng");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nếu truy vấn bị hủy hoặc lỗi, thông báo lỗi
                listener.onUserInfoError("Lỗi khi tải thông tin: " + databaseError.getMessage());
            }
        });
    }

    // Phương thức kiểm tra số lần hủy đơn hàng của người dùng
    public void checkCancelledCount(String userId, OnCancelledCountCheckedListener listener) {
        databaseReference.child("users").child(userId).child("cancelled").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long cancelledCount = dataSnapshot.getValue(Long.class);
                    listener.onCancelledCountChecked(cancelledCount != null ? cancelledCount : 0);
                } else {
                    // Nếu không có node cancelled, mặc định là 0
                    listener.onCancelledCountChecked(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelledCountError("Lỗi khi kiểm tra số lần hủy đơn: " + databaseError.getMessage());
            }
        });
    }

    // Phương thức kiểm tra và áp dụng mã giảm giá từ Firebase
    public void applyVoucherCode(String userId, String voucherCode, OnVoucherAppliedListener listener) {
        // Tham chiếu đến node users/{userId}/voucher/{voucherCode}
        DatabaseReference voucherRef = databaseReference.child("users").child(userId).child("voucher").child(voucherCode);
        voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy giá trị maxVoucher (số lần sử dụng hợp lệ) và discount từ dữ liệu
                    Long maxVoucher = dataSnapshot.child("maxVoucher").getValue(Long.class);
                    Long dbDiscount = dataSnapshot.child("discount").getValue(Long.class);

                    if (maxVoucher != null && maxVoucher > 0) {
                        double discount = (dbDiscount != null) ? dbDiscount.doubleValue() : 0.0;
                        // Lưu mã giảm giá đã áp dụng để sử dụng sau khi thanh toán
                        appliedVoucherCode = voucherCode;
                        // Trả về giá trị giảm giá nếu mã còn hợp lệ
                        listener.onVoucherApplied(discount);
                    } else {
                        // Nếu maxVoucher <= 0, mã không còn hợp lệ
                        listener.onVoucherError("Mã giảm giá đã hết lượt sử dụng!");
                    }
                } else {
                    // Nếu không tìm thấy mã, thông báo lỗi
                    listener.onVoucherError("Mã giảm giá không hợp lệ hoặc đã hết lượt sử dụng!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nếu truy vấn bị hủy hoặc lỗi, thông báo lỗi
                listener.onVoucherError("Lỗi khi kiểm tra mã giảm giá: " + databaseError.getMessage());
            }
        });
    }

    // Phương thức xử lý thanh toán và lưu đơn hàng vào Firebase
    public void processPayment(Order order, String userId, OnPaymentProcessedListener listener) {
        String orderId = order.getOrderId();
        // Lưu đơn hàng vào node orders
        databaseReference.child("orders").child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    // Xóa giỏ hàng của người dùng sau khi đặt hàng thành công
                    databaseReference.child("users").child(userId).child("cart").removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                // Kiểm tra nếu có mã giảm giá đã áp dụng
                                if (appliedVoucherCode != null && !appliedVoucherCode.isEmpty()) {
                                    updateVoucherUsage(userId, appliedVoucherCode);
                                    appliedVoucherCode = null; // Đặt lại mã giảm giá sau khi sử dụng
                                }
                                listener.onPaymentSuccess();
                            })
                            .addOnFailureListener(e -> listener.onPaymentFailure("Lỗi khi xóa giỏ hàng: " + e.getMessage()));
                })
                .addOnFailureListener(e -> listener.onPaymentFailure("Lỗi khi đặt hàng: " + e.getMessage()));
    }

    // Phương thức cập nhật số lần sử dụng mã giảm giá
    private void updateVoucherUsage(String userId, String voucherCode) {
        DatabaseReference voucherRef = databaseReference.child("users").child(userId).child("voucher").child(voucherCode);
        voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long maxVoucher = dataSnapshot.child("maxVoucher").getValue(Long.class);
                    if (maxVoucher != null && maxVoucher > 0) {
                        long newMaxVoucher = maxVoucher - 1;
                        if (newMaxVoucher <= 0) {
                            // Xóa node nếu số lần sử dụng còn lại bằng 0
                            voucherRef.removeValue();
                        } else {
                            // Cập nhật số lần sử dụng mới
                            voucherRef.child("maxVoucher").setValue(newMaxVoucher);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Có thể ghi log lỗi nếu cần
            }
        });
    }

    // Interface callback để thông báo kết quả lấy thông tin người dùng
    public interface OnUserInfoLoadedListener {
        void onUserInfoLoaded(String address, String username, String phoneNumber);
        void onUserInfoError(String error);
    }

    // Interface callback để thông báo kết quả kiểm tra số lần hủy đơn
    public interface OnCancelledCountCheckedListener {
        void onCancelledCountChecked(long count);
        void onCancelledCountError(String error);
    }

    // Interface callback để thông báo kết quả áp dụng mã giảm giá
    public interface OnVoucherAppliedListener {
        void onVoucherApplied(double discount);
        void onVoucherError(String error);
    }

    // Interface callback để thông báo kết quả xử lý thanh toán
    public interface OnPaymentProcessedListener {
        void onPaymentSuccess();
        void onPaymentFailure(String error);
    }
}