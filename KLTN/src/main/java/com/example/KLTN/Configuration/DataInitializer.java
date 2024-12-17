//package com.example.KLTN.Configuration;
//
//import com.example.KLTN.Service.DataInitializerService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class DataInitializer {
//
//    private final DataInitializerService dataInitializationService;
//
//    @Bean
//    CommandLineRunner initDatabase() {
//        return args -> {
//            dataInitializationService.initData();
//        };
//    }
//}
//
//
