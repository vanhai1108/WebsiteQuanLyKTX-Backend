package com.ktx.quanlykytucxa.config;

import com.ktx.quanlykytucxa.entities.*;
import com.ktx.quanlykytucxa.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            RoomRepository roomRepository,
            StudentRepository studentRepository,
            ContractRepository contractRepository,
            InvoiceRepository invoiceRepository,
            NotificationRepository notificationRepository,
            RegistrationRepository registrationRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            if (userRepository.count() > 0) {
                System.out.println("Data already exists, skipping seeding.");
                return;
            }

            String defaultPassword = "123456";
            User admin = User.builder().username("admin").password(passwordEncoder.encode(defaultPassword)).role(Role.ADMIN).build();
            User user1 = User.builder().username("sv001").password(passwordEncoder.encode(defaultPassword)).role(Role.STUDENT).build();
            User user2 = User.builder().username("sv002").password(passwordEncoder.encode(defaultPassword)).role(Role.STUDENT).build();
            User user3 = User.builder().username("sv003").password(passwordEncoder.encode(defaultPassword)).role(Role.STUDENT).build();
            User user4 = User.builder().username("sv004").password(passwordEncoder.encode(defaultPassword)).role(Role.STUDENT).build();
            User user5 = User.builder().username("sv005").password(passwordEncoder.encode(defaultPassword)).role(Role.STUDENT).build();
            userRepository.saveAll(List.of(admin, user1, user2, user3, user4, user5));

            Room room1 = Room.builder().roomCode("P101").building("A1").maxCapacity(4).currentCapacity(2).price(500000.0).build();
            Room room2 = Room.builder().roomCode("P102").building("A1").maxCapacity(4).currentCapacity(1).price(500000.0).build();
            Room room3 = Room.builder().roomCode("P201").building("A2").maxCapacity(6).currentCapacity(1).price(400000.0).build();
            Room room4 = Room.builder().roomCode("P202").building("A2").maxCapacity(6).currentCapacity(0).price(400000.0).build();
            roomRepository.saveAll(List.of(room1, room2, room3, room4));

            Student s1 = Student.builder().studentCode("SV001").fullName("Nguyễn Văn A").className("K65-CNTT").phone("0987654321").email("s1@gmail.com").user(user1).room(room1).build();
            Student s2 = Student.builder().studentCode("SV002").fullName("Trần Thị B").className("K65-KHMT").phone("0987654322").email("s2@gmail.com").user(user2).room(room1).build();
            Student s3 = Student.builder().studentCode("SV003").fullName("Lê Văn C").className("K64-KTPM").phone("0987654323").email("s3@gmail.com").user(user3).room(room2).build();
            Student s4 = Student.builder().studentCode("SV004").fullName("Phạm Thị D").className("K66-HTTT").phone("0987654324").email("s4@gmail.com").user(user4).room(room3).build();
            Student s5 = Student.builder().studentCode("SV005").fullName("Hoàng Văn E").className("K65-CNTT").phone("0987654325").email("s5@gmail.com").user(user5).room(null).build();
            studentRepository.saveAll(List.of(s1, s2, s3, s4, s5));

            Contract c1 = Contract.builder().student(s1).room(room1).startDate(LocalDate.now().minusMonths(2)).endDate(LocalDate.now().plusMonths(4)).status("ACTIVE").build();
            Contract c2 = Contract.builder().student(s2).room(room1).startDate(LocalDate.now().minusMonths(1)).endDate(LocalDate.now().plusMonths(5)).status("ACTIVE").build();
            Contract c3 = Contract.builder().student(s3).room(room2).startDate(LocalDate.now().minusDays(15)).endDate(LocalDate.now().plusMonths(6)).status("ACTIVE").build();
            contractRepository.saveAll(List.of(c1, c2, c3));

            Invoice i1 = Invoice.builder().student(s1).description("Tiền phòng tháng hiện tại").amount(500000.0).createDate(LocalDate.now()).status(InvoiceStatus.UNPAID).build();
            Invoice i2 = Invoice.builder().student(s2).description("Tiền phòng tháng trước").amount(500000.0).createDate(LocalDate.now().minusMonths(1)).status(InvoiceStatus.PAID).build();
            invoiceRepository.saveAll(List.of(i1, i2));

            Registration reg1 = Registration.builder().student(s5).room(room4).requestDate(LocalDateTime.now().minusDays(2)).status(RegistrationStatus.PENDING).build();
            registrationRepository.save(reg1);

            Notification n1 = Notification.builder().title("Thông báo thu tiền phòng").content("Yêu cầu các sinh viên nộp tiền phòng đúng hạn").createDate(LocalDateTime.now().minusDays(1)).build();
            Notification n2 = Notification.builder().title("Thông báo vệ sinh chung").content("Cuối tuần này sẽ tổ chức tổng vệ sinh toàn khu A1 và A2.").createDate(LocalDateTime.now().minusHours(5)).build();
            notificationRepository.saveAll(List.of(n1, n2));

            System.out.println("Data Seeding Completed!");
        };
    }
}
