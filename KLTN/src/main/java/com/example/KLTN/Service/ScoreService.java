package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Response.GpaDTO;
import com.example.KLTN.DTOS.Response.ScoreResponseDTO;
import com.example.KLTN.Entity.Score;
import com.example.KLTN.Entity.Semester;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Entity.User;
import com.example.KLTN.Repository.ScoreRepository;
import com.example.KLTN.Repository.SemesterRepository;
import com.example.KLTN.Repository.SubjectRepository;
import com.example.KLTN.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {
    public final UserService userService;
    public final SubjectService subjectService;
    public final SubjectRepository subjectRepository;
    public final UserRepository userRepository;
    public final ScoreRepository scoreRepository;
    public final SemesterRepository semesterRepository;


    public ResponseEntity<?> addScore(Long subjectId, Double grade, Long semesterId) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));
        if (scoreRepository.findByUserAndSubjectAndSemester(user, subject, semester).isPresent()) {
            throw new RuntimeException("User has already enrolled in this subject");
        }
        Score score = Score.builder()
                .user(user)
                .subject(subject)
                .semester(semester)
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

    public Map<String, List<ScoreResponseDTO>> getScore() {
        User user = userService.getCurrentUser();
        List<Score> scoreList = scoreRepository.findByUser(user);

        // Nhóm theo kỳ học
        return scoreList.stream().collect(Collectors.groupingBy(score -> score.getSemester().getSemesterName(),
                Collectors.mapping(score -> {
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
                            .semesterName(score.getSemester().getSemesterName())
                            .build();
                }, Collectors.toList())));
    }

    public ResponseEntity<GpaDTO> calculateGPA() {
        User user = userService.getCurrentUser();
        List<Score> scoreList = scoreRepository.findByUser(user);
        double totalPoints = 0;
        int totalCredits = 0;

        for (Score score : scoreList) {
            Double grade = score.getGrade();
            String gradeABC = convertToABC(grade);

            // Bỏ qua các môn học có điểm là "F"
            if (!gradeABC.equals("F")) {
                Double gradeFor = convertTo4Scale(grade);
                Integer credit = score.getSubject().getCredit();

                totalPoints += gradeFor * credit;
                totalCredits += credit;
            }
        }

        if (totalCredits == 0) {
            // Trường hợp không có tín chỉ nào để tính GPA
            return new ResponseEntity<>(GpaDTO.builder()
                    .gpa(BigDecimal.ZERO)
                    .totalCredits(0)
                    .build(), HttpStatus.OK);
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
        String userName = user.getUsername();

        // Lấy danh sách điểm của người dùng
        List<Score> scoreList = scoreRepository.findByUser(user);

        // Nhóm các điểm theo kỳ học và sắp xếp kỳ học theo thứ tự từ nhỏ đến lớn
        Map<String, List<Score>> scoresBySemester = scoreList.stream()
                .collect(Collectors.groupingBy(score -> score.getSemester().getSemesterName()));

        // Sắp xếp các khóa học theo tên kỳ học (sắp xếp theo số)
        List<String> sortedSemesterNames = scoresBySemester.keySet().stream()
                .sorted((semester1, semester2) -> {
                    // Extract the semester number from the name (e.g., "Semester 1" -> 1)
                    int semester1Number = Integer.parseInt(semester1.replaceAll("\\D+", ""));
                    int semester2Number = Integer.parseInt(semester2.replaceAll("\\D+", ""));
                    return Integer.compare(semester1Number, semester2Number);
                })
                .collect(Collectors.toList());

        // Tạo đối tượng tài liệu Word mới
        XWPFDocument document = new XWPFDocument();

        // Tạo tiêu đề cho tài liệu
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);  // Căn giữa tiêu đề
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Kết quả học");
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // Thêm tên người dùng vào tài liệu
        XWPFParagraph userParagraph = document.createParagraph();
        userParagraph.createRun().setText("Họ tên: " + userName);

        // Lấy GPA và tổng tín chỉ từ phương thức calculateGPA
        ResponseEntity<GpaDTO> gpaResponse = calculateGPA();
        GpaDTO gpaDTO = gpaResponse.getBody();
        BigDecimal gpa = gpaDTO.getGpa();
        int totalCredits = gpaDTO.getTotalCredits();

        // Thêm thông tin GPA và tổng tín chỉ vào tài liệu
        XWPFParagraph gpaParagraph = document.createParagraph();
        gpaParagraph.createRun().setText("GPA : " + gpa.toString());

        XWPFParagraph totalCreditsParagraph = document.createParagraph();
        totalCreditsParagraph.createRun().setText("Tổng tín chỉ: " + totalCredits);

        // Lặp qua các kỳ học đã được sắp xếp và xuất từng kỳ vào bảng
        for (String semesterName : sortedSemesterNames) {
            // Tạo tiêu đề cho từng kỳ học
            XWPFParagraph semesterTitle = document.createParagraph();
            semesterTitle.setAlignment(ParagraphAlignment.CENTER);  // Căn giữa tiêu đề
            XWPFRun semesterTitleRun = semesterTitle.createRun();
            semesterTitleRun.setText("Kết quả học kỳ " + semesterName);
            semesterTitleRun.setBold(true);
            semesterTitleRun.setFontSize(14);

            // Tạo bảng cho các môn học của kỳ học này
            XWPFTable table = document.createTable();

            // Thêm hàng tiêu đề cho bảng
            XWPFTableRow headerRow = table.getRow(0);
            XWPFTableCell cell1 = headerRow.getCell(0);
            cell1.setText("STT");
            setCellAlignment(cell1);
            setCellWidth(cell1, 1500); // Set width of the column

            XWPFTableCell cell2 = headerRow.addNewTableCell();
            cell2.setText("Môn học");
            setCellAlignment(cell2);
            setCellWidth(cell2, 3000); // Set width of the column

            XWPFTableCell cell3 = headerRow.addNewTableCell();
            cell3.setText("Tín chỉ");
            setCellAlignment(cell3);
            setCellWidth(cell3, 1500); // Set width of the column

            XWPFTableCell cell4 = headerRow.addNewTableCell();
            cell4.setText("Điểm (Hệ 4)");
            setCellAlignment(cell4);
            setCellWidth(cell4, 2000); // Set width of the column

            XWPFTableCell cell5 = headerRow.addNewTableCell();
            cell5.setText("Điểm (ABC)");
            setCellAlignment(cell5);
            setCellWidth(cell5, 2000); // Set width of the column

            // Thêm các môn học vào bảng cho kỳ học này
            List<Score> semesterScores = scoresBySemester.get(semesterName);
            int subjectCounter = 1;

            for (Score score : semesterScores) {
                XWPFTableRow row = table.createRow();

                // Cột "STT"
                row.getCell(0).setText(String.valueOf(subjectCounter++));
                setCellAlignment(row.getCell(0));

                // Cột "Môn học"
                row.getCell(1).setText(score.getSubject().getSubjectName());
                setCellAlignment(row.getCell(1));

                // Cột "Số tín chỉ"
                row.getCell(2).setText(String.valueOf(score.getSubject().getCredit()));
                setCellAlignment(row.getCell(2));

                // Cột "Điểm (Hệ 4)"
                row.getCell(3).setText(String.valueOf(convertTo4Scale(score.getGrade())));
                setCellAlignment(row.getCell(3));

                // Cột "Điểm (ABC)"
                row.getCell(4).setText(convertToABC(score.getGrade()));
                setCellAlignment(row.getCell(4));
            }
        }

        // Xuất tài liệu ra file byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);

        // Trả về file .docx dưới dạng ResponseEntity
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Kết quả học.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    // Phương thức căn giữa cho ô của bảng
    private void setCellAlignment(XWPFTableCell cell) {
        XWPFParagraph paragraph = cell.addParagraph(); // Add paragraph if not already present
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Horizontal alignment (center)
        paragraph.setVerticalAlignment(TextAlignment.CENTER); // Vertical alignment (center)
    }

    // Phương thức đặt chiều rộng cho các ô của bảng (không dùng CTTableWidth)
    private void setCellWidth(XWPFTableCell cell, int width) {
        // Đặt chiều rộng cho các ô mà không cần dùng CTTableWidth
        CTTcPr cellProperties = cell.getCTTc().addNewTcPr(); // Access cell properties
        CTTblWidth tblWidth = cellProperties.addNewTcW(); // Access width property directly
        tblWidth.setType(STTblWidth.DXA); // Set type to DXA (twips)
        tblWidth.setW(BigInteger.valueOf(width)); // Set the width in twips (1 twip = 1/1440 inch)
    }


    public void importScoresFromDocx(MultipartFile file) throws Exception {
        // Đọc file DOCX
        XWPFDocument document = new XWPFDocument(file.getInputStream());

        // Lấy tên kỳ học từ tiêu đề
        String semesterName = getSemesterName(document);

        // Kiểm tra xem kỳ học có tồn tại không
        Semester semester = semesterRepository.findBySemesterName(semesterName);
        if (semester == null) {
            semester = new Semester();
            semester.setSemesterName(semesterName);
            semesterRepository.save(semester);
        }
        // Lấy người dùng hiện tại (sinh viên)
        User currentUser = userService.getCurrentUser();

        // Lặp qua các dòng trong file và lưu điểm
        for (XWPFTable table : document.getTables()) {
            // Bỏ qua dòng tiêu đề (STT, Mã môn học, Tên môn học, Tín chỉ, Điểm)
            for (int i = 1; i < table.getRows().size(); i++) {
                XWPFTableRow row = table.getRow(i);
                String subjectCode = row.getCell(1).getText().trim();
                String subjectName = row.getCell(2).getText().trim();
                int credit = Integer.parseInt(row.getCell(3).getText().trim());
                double grade = Double.parseDouble(row.getCell(4).getText().trim());

                // Kiểm tra xem môn học có tồn tại không
                Subject subject = subjectRepository.findBySubjectId(subjectCode);
                if (subject == null) {
                    subject = new Subject();
                    subject.setSubjectId(subjectCode);
                    subject.setSubjectName(subjectName);
                    subject.setCredit(credit);
                    subjectRepository.save(subject);
                }

                // Lưu điểm vào bảng Score
                Score score = Score.builder()
                        .user(currentUser)
                        .semester(semester)
                        .subject(subject)
                        .grade(grade)
                        .build();

                scoreRepository.save(score);
            }
        }
    }

    private String getSemesterName(XWPFDocument document) {
        // Lấy tên kỳ học từ dòng tiêu đề đầu tiên
        XWPFParagraph firstParagraph = document.getParagraphs().get(0);
        return firstParagraph.getText().replace("Kì học", "").trim();  // Điều chỉnh nếu cần
    }
}
