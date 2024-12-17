package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Response.GpaDTO;
import com.example.KLTN.DTOS.Response.ScoreResponseDTO;
import com.example.KLTN.Entity.Score;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Entity.User;
import com.example.KLTN.Repository.ScoreRepository;
import com.example.KLTN.Repository.SubjectRepository;
import com.example.KLTN.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {
    public final UserService userService;
    public final SubjectService subjectService;
    public final SubjectRepository subjectRepository;
    public final UserRepository userRepository;
    public final ScoreRepository scoreRepository;


    public ResponseEntity<?> addScore(Long subjectId, Double grade) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        if (scoreRepository.findByUserAndSubject(user, subject).isPresent()) {
            throw new RuntimeException("User has already enrolled in this subject");
        }
        Score score = Score.builder()
                .user(user)
                .subject(subject)
                .grade(grade)
                .build();
        scoreRepository.save(score);
        return ResponseEntity.ok("Create score successful?");
    }

    public ResponseEntity<?> updateScore(Long subjectId, Double grade) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Score score = scoreRepository.findByUserAndSubject(user, subject)
                .orElseThrow(() -> new RuntimeException(""));
        score.setGrade(grade);
        scoreRepository.save(score);
        return ResponseEntity.ok("Update score successful?");
    }

    public List<ScoreResponseDTO> getScore() {
        User user = userService.getCurrentUser();
        List<Score> scoreList = scoreRepository.findByUser(user);

        return scoreList.stream().map(score -> {
            Double grade = score.getGrade();
            Double gradeFor = convertTo4Scale(grade); // Chuyển từ hệ 10 sang hệ 4
            String gradeABC = convertToABC(grade); // Chuyển từ hệ 10 sang hệ ABC

            return ScoreResponseDTO.builder()
                    .subjectName(score.getSubject().getSubjectName())
                    .subjectId(score.getSubject().getSubjectId())
                    .credit(score.getSubject().getCredit())
                    .grade(grade)
                    .gradeFor(gradeFor)
                    .gradeABC(gradeABC)
                    .build();
        }).collect(Collectors.toList());
    }

    public ResponseEntity<GpaDTO> calculateGPA(){
        User user = userService.getCurrentUser();
        List<Score> scoreList = scoreRepository.findByUser(user);
        double totalPoints = 0;
        int totalCredits = 0;
        for (Score score : scoreList) {
            Double gradeFor = convertTo4Scale(score.getGrade());
            Integer credit = score.getSubject().getCredit();

            totalPoints += gradeFor * credit;
            totalCredits += credit;
        }
        double gpa = totalPoints / totalCredits;
        BigDecimal roundedGPA = new BigDecimal(gpa).setScale(2, RoundingMode.HALF_UP);
        return new ResponseEntity<>(GpaDTO.builder()
                .gpa(roundedGPA)
                .totalCredits(totalCredits)
                .build(), HttpStatus.OK);
    }

    private String convertToABC(Double grade) {
        if (grade >= 9.0) {
            return "A+";
        } else if (grade <= 8.9 && grade >= 8.5) {
            return "A";
        } else if (grade <= 8.4 && grade >= 8.0) {
            return "B+";
        } else if (grade <= 7.9 && grade >= 7.0) {
            return "B";
        } else if (grade <= 6.9 && grade >= 6.5) {
            return "C+";
        } else if (grade <= 6.4 && grade >= 5.5) {
            return "C";
        } else if (grade <= 5.4 && grade >= 5.0) {
            return "D+";
        } else if (grade <= 4.9 && grade >= 4.0) {
            return "D";
        } else {
            return "F";
        }
    }

    private Double convertTo4Scale(Double grade) {
        if (grade >= 9.0) {
            return 4.0;
        } else if (grade <= 8.9 && grade >= 8.5) {
            return 3.7;
        } else if (grade <= 8.4 && grade >= 8.0) {
            return 3.5;
        } else if (grade <= 7.9 && grade >= 7.0) {
            return 3.0;
        } else if (grade <= 6.9 && grade >= 6.5) {
            return 2.5;
        } else if (grade <= 6.4 && grade >= 5.5) {
            return 2.0;
        } else if (grade <= 5.4 && grade >= 5.0) {
            return 1.5;
        } else if (grade <= 4.9 && grade >= 4.0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    public ResponseEntity<byte[]> exportScoresToDoc() throws IOException {
        // Lấy thông tin người dùng hiện tại
        User user = userService.getCurrentUser();
        String userName = user.getUsername(); // Giả sử User có phương thức getUsername()

        // Lấy danh sách điểm của người dùng
        List<Score> scoreList = scoreRepository.findByUser(user);

        // Tạo đối tượng tài liệu Word mới
        XWPFDocument document = new XWPFDocument();

        // Tạo tiêu đề cho tài liệu
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);  // Căn giữa tiêu đề

        // Tạo run cho đoạn văn và định dạng tiêu đề
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Kết quả học");

        // Thay đổi kích thước chữ (ví dụ: 16px), in đậm
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // Thêm tên người dùng vào tài liệu
        XWPFParagraph userParagraph = document.createParagraph();
        userParagraph.createRun().setText("Họ tên: " + userName);

        // Tạo bảng để hiển thị thông tin điểm số
        XWPFTable table = document.createTable();

        // Thêm hàng tiêu đề cho bảng
        XWPFTableRow headerRow = table.getRow(0);
        XWPFTableCell cell1 = headerRow.getCell(0);
        cell1.setText("Môn học");
        XWPFTableCell cell2 = headerRow.addNewTableCell();
        cell2.setText("Tín chỉ");
        XWPFTableCell cell3 = headerRow.addNewTableCell();
        cell3.setText("Điểm (Hệ 4)");
        XWPFTableCell cell4 = headerRow.addNewTableCell();
        cell4.setText("Điểm (Hệ chữ)");

        // Căn giữa tiêu đề
        setCellAlignment(cell1);
        setCellAlignment(cell2);
        setCellAlignment(cell3);
        setCellAlignment(cell4);

        // Thêm các hàng dữ liệu vào bảng
        for (Score score : scoreList) {
            XWPFTableRow row = table.createRow();

            // Cột "Môn học"
            row.getCell(0).setText(score.getSubject().getSubjectName());
            setCellAlignment(row.getCell(0)); // Căn giữa

            // Cột "Số tín chỉ"
            row.getCell(1).setText(String.valueOf(score.getSubject().getCredit()));
            setCellAlignment(row.getCell(1)); // Căn giữa

            // Cột "Điểm (Hệ 4)"
            row.getCell(2).setText(String.valueOf(convertTo4Scale(score.getGrade())));
            setCellAlignment(row.getCell(2)); // Căn giữa

            // Cột "Điểm (ABC)"
            row.getCell(3).setText(convertToABC(score.getGrade()));
            setCellAlignment(row.getCell(3)); // Căn giữa
        }

        // Tính GPA
        double totalPoints = 0;
        int totalCredits = 0;
        for (Score score : scoreList) {
            Double gradeFor = convertTo4Scale(score.getGrade());
            Integer credit = score.getSubject().getCredit();
            totalPoints += gradeFor * credit;
            totalCredits += credit;
        }
        double gpa = totalPoints / totalCredits;
        BigDecimal roundedGPA = new BigDecimal(gpa).setScale(2, RoundingMode.HALF_UP);

        // Thêm phần GPA vào tài liệu, chia thành 2 đoạn
        XWPFParagraph gpaParagraph = document.createParagraph();
        gpaParagraph.createRun().setText("GPA: " + roundedGPA.toString());

        XWPFParagraph totalCreditsParagraph = document.createParagraph();
        totalCreditsParagraph.createRun().setText("Tổng tín chỉ: " + totalCredits);

        // Xuất tài liệu ra file byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);

        // Trả về file .docx dưới dạng ResponseEntity
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=score_report.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    // Phương thức căn giữa cho ô của bảng
    private void setCellAlignment(XWPFTableCell cell) {
        // Lấy đoạn văn trong ô bảng
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Căn giữa nội dung
        paragraph.setVerticalAlignment(TextAlignment.CENTER); // Căn giữa theo chiều dọc
    }

}
