package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Response.GpaDTO;
import com.example.KLTN.DTOS.Response.ScoreResponseDTO;
import com.example.KLTN.Entity.*;
import com.example.KLTN.Repository.*;
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
    public final LecturerRepository lecturerRepository;


    public ResponseEntity<?> addScore(Long subjectId, Double grade, Long semesterId, Long lecturerId) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found"));
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        List<Lecturer> lecturerList = lecturerRepository.findBySubjectId(subjectId);
        if (scoreRepository.findByUserAndSubjectAndSemester(user, subject, semester).isPresent()) {
            throw new RuntimeException("User has already enrolled in this subject");
        }
        for (Lecturer l : lecturerList){
            if (l.equals(lecturer)){
                Score score = Score.builder()
                        .user(user)
                        .subject(subject)
                        .semester(semester)
                        .grade(grade)
                        .lecturer(lecturer)
                        .build();
                scoreRepository.save(score);
                return ResponseEntity.ok("Create score successful?");
            }
        }
        throw new RuntimeException("Giang vien khong day mon hoc nay");
    }

    public ResponseEntity<?> updateScore(Long subjectId, Double grade) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Score score = scoreRepository.findByUserAndSubject(user, subject);
        if (score == null) {
            return ResponseEntity.badRequest().body("abc");
        }
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
        String userId = user.getStudentId();

        // Lấy danh sách điểm của người dùng
        List<Score> scoreList = scoreRepository.findByUser(user);

        // Nhóm các điểm theo kỳ học và sắp xếp kỳ học theo thứ tự từ nhỏ đến lớn
        Map<String, List<Score>> scoresBySemester = scoreList.stream()
                .collect(Collectors.groupingBy(score -> score.getSemester().getSemesterName()));

        // Sắp xếp các kỳ học theo thứ tự tăng dần
        List<String> sortedSemesterNames = scoresBySemester.keySet().stream()
                .sorted((semester1, semester2) -> {
                    int semester1Number = Integer.parseInt(semester1.replaceAll("\\D+", ""));
                    int semester2Number = Integer.parseInt(semester2.replaceAll("\\D+", ""));
                    return Integer.compare(semester1Number, semester2Number);
                })
                .collect(Collectors.toList());

        // Tạo đối tượng tài liệu Word
        XWPFDocument document = new XWPFDocument();

        // Thiết lập khổ giấy A4 ngang và lề
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSize = sectPr.addNewPgSz();
        pageSize.setW(BigInteger.valueOf(16840)); // Chiều rộng A4 ngang (twips)
        pageSize.setH(BigInteger.valueOf(11907)); // Chiều cao A4 ngang (twips)
        pageSize.setOrient(STPageOrientation.LANDSCAPE); // Đặt khổ ngang

        // Thiết lập lề (lề trái phải 1 cm, lề trên dưới 1 inch)
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(567));   // Lề trái: 1 cm (~567 twips)
        pageMar.setRight(BigInteger.valueOf(567));  // Lề phải: 1 cm (~567 twips)
        pageMar.setTop(BigInteger.valueOf(1440));   // Lề trên: 1 inch (~1440 twips)
        pageMar.setBottom(BigInteger.valueOf(1440));// Lề dưới: 1 inch (~1440 twips)

        // Tiêu đề chính của tài liệu
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText("KẾT QUẢ HỌC TẬP");
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // Thêm thông tin sinh viên
        XWPFParagraph userInfoParagraph = document.createParagraph();
        userInfoParagraph.createRun().setText("Sinh viên: " + userName);

        XWPFParagraph userIdParagraph = document.createParagraph();
        userIdParagraph.createRun().setText("Mã số: " + userId);

        // Lấy GPA và tổng tín chỉ
        ResponseEntity<GpaDTO> gpaResponse = calculateGPA();
        GpaDTO gpaDTO = gpaResponse.getBody();
        BigDecimal gpa = gpaDTO.getGpa();
        int totalCredits = gpaDTO.getTotalCredits();

        // Thêm thông tin GPA và tín chỉ
        XWPFParagraph gpaParagraph = document.createParagraph();
        gpaParagraph.createRun().setText("GPA: " + gpa);

        XWPFParagraph creditsParagraph = document.createParagraph();
        creditsParagraph.createRun().setText("Tổng tín chỉ: " + totalCredits);

        // Xuất từng kỳ học
        for (String semesterName : sortedSemesterNames) {
            // Tiêu đề kỳ học
            XWPFParagraph semesterTitle = document.createParagraph();
            semesterTitle.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun semesterRun = semesterTitle.createRun();
            semesterRun.setText("Học kỳ: " + semesterName);
            semesterRun.setBold(true);
            semesterRun.setFontSize(14);

            // Tạo bảng cho kỳ học này
            XWPFTable table = document.createTable();

            // Hàng tiêu đề
            XWPFTableRow headerRow = table.getRow(0);
            createHeaderCell(headerRow.getCell(0), "STT", 1000);
            createHeaderCell(headerRow.addNewTableCell(), "Mã MH", 2000);
            createHeaderCell(headerRow.addNewTableCell(), "Tên môn học", 6000);
            createHeaderCell(headerRow.addNewTableCell(), "Số TC", 2500);
            createHeaderCell(headerRow.addNewTableCell(), "Điểm Hệ 10", 3000);
            createHeaderCell(headerRow.addNewTableCell(), "Điểm chữ", 3000);
            createHeaderCell(headerRow.addNewTableCell(), "Điểm Hệ 4", 3000);

            // Dữ liệu kỳ học
            List<Score> semesterScores = scoresBySemester.get(semesterName);
            int subjectCounter = 1;

            for (Score score : semesterScores) {
                XWPFTableRow row = table.createRow();
                createContentCell(row.getCell(0), String.valueOf(subjectCounter++));
                createContentCell(row.getCell(1), score.getSubject().getSubjectId());
                createContentCell(row.getCell(2), score.getSubject().getSubjectName());
                createContentCell(row.getCell(3), String.valueOf(score.getSubject().getCredit()));
                createContentCell(row.getCell(4), String.valueOf(score.getGrade()));
                createContentCell(row.getCell(5), convertToABC(score.getGrade()));
                createContentCell(row.getCell(6), String.valueOf(convertTo4Scale(score.getGrade())));
            }
        }

        // Xuất tài liệu ra file byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);

        // Trả về file .docx
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=KetQuaHocTap.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    // Tạo ô tiêu đề bảng
    private void createHeaderCell(XWPFTableCell cell, String text, int width) {
        cell.setText(text);
        setCellAlignment(cell); // Đặt căn chỉnh cho ô
        setCellWidth(cell, width); // Đặt chiều rộng cho ô
    }

    // Tạo ô nội dung bảng
    private void createContentCell(XWPFTableCell cell, String text) {
        cell.setText(text);
        setCellAlignment(cell); // Đặt căn chỉnh cho ô
    }

    // Căn chỉnh nội dung ô (cả ngang và dọc)
    private void setCellAlignment(XWPFTableCell cell) {
        XWPFParagraph paragraph = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Căn giữa ngang
        paragraph.setVerticalAlignment(TextAlignment.CENTER); // Căn giữa dọc trong đoạn văn
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER); // Căn giữa dọc trong ô
    }

    // Đặt chiều rộng ô
    private void setCellWidth(XWPFTableCell cell, int width) {
        CTTcPr cellProperties = cell.getCTTc().addNewTcPr();
        CTTblWidth tblWidth = cellProperties.addNewTcW();
        tblWidth.setType(STTblWidth.DXA);
        tblWidth.setW(BigInteger.valueOf(width));
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
                Score score1 = scoreRepository.findByUserAndSubject(currentUser,subject);
                if (score1 == null) {
                    // Lưu điểm vào bảng Score
                    Score score = Score.builder()
                            .user(currentUser)
                            .semester(semester)
                            .subject(subject)
                            .grade(grade)
                            .build();
                    scoreRepository.save(score);
                } else {
                    scoreRepository.delete(score1);
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
    }

    private String getSemesterName(XWPFDocument document) {
        // Lấy tên kỳ học từ dòng tiêu đề đầu tiên
        XWPFParagraph firstParagraph = document.getParagraphs().get(0);
        return firstParagraph.getText().replace("Kì học", "").trim();  // Điều chỉnh nếu cần
    }
}
