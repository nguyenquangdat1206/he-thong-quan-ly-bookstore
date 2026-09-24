public class Main {
    // Khai báo mã màu ANSI để trang trí giao diện Console (Ghi điểm sáng tạo)
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        int chon = -1;
        
        do {
            System.out.println(CYAN + "\n========== HỆ THỐNG QUẢN LÝ KHO SÁCH ==========" + RESET);
            System.out.println(YELLOW + "1. Quản lý Mặt hàng (Kho sách/Vở/Dụng cụ)" + RESET);
            System.out.println(YELLOW + "2. Quản lý Khách hàng & Giao dịch (Hóa đơn)" + RESET);
            System.out.println(YELLOW + "3. Quản lý Nhập hàng & Nhà cung cấp" + RESET);
            System.out.println(YELLOW + "4. Quản lý Nhân sự" + RESET);
            System.out.println(GREEN  + "0. Thoát chương trình & Lưu toàn bộ dữ liệu" + RESET);
            System.out.println(CYAN + "===============================================" + RESET);

            // Gọi hàm an toàn từ TienIch, ép người dùng chỉ được gõ từ 0 đến 4
            chon = TienIch.nhapSoTuKhoang("Mời bạn chọn chức năng", 0, 4);

            switch (chon) {
                case 1:
                    System.out.println(GREEN + ">> Đang vào module Quản lý Mặt hàng..." + RESET);
                    // TODO: Thành viên 5 sau này sẽ gọi Menu con của Thành viên 2 ở đây
                    break;
                case 2:
                    System.out.println(GREEN + ">> Đang vào module Khách hàng & Giao dịch..." + RESET);
                    // TODO: Thành viên 5 gọi Menu con của Thành viên 3 ở đây
                    break;
                case 3:
                    System.out.println(GREEN + ">> Đang vào module Nhập hàng & Kho bãi..." + RESET);
                    // TODO: Thành viên 5 gọi Menu con của Thành viên 4 ở đây
                    break;
                case 4:
                    System.out.println(GREEN + ">> Đang vào module Nhân sự..." + RESET);
                    // TODO: Thành viên 5 gọi Menu con của chính bạn ấy ở đây
                    break;
                case 0:
                    System.out.println(RED + "Đang lưu dữ liệu xuống file text... Hẹn gặp lại!" + RESET);
                    break;
            }
        } while (chon != 0);
    }
}