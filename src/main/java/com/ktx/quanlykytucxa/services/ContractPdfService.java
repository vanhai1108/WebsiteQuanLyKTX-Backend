package com.ktx.quanlykytucxa.services;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.ktx.quanlykytucxa.entities.Contract;

@Service
public class ContractPdfService {

    public byte[] generateContractPdf(Contract contract) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Set Unicode font to render Vietnamese characters correctly
            try {
                URL fontUrl = getClass().getClassLoader().getResource("fonts/DejaVuSans.ttf");
                if (fontUrl != null) {
                    String fontPath = Paths.get(fontUrl.toURI()).toString();
                    PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
                    document.setFont(font);
                }
            } catch (Exception fontEx) {
                fontEx.printStackTrace();
            }

            document.add(new Paragraph("HỢP ĐỒNG THUÊ PHÒNG KÝ TÚC XÁ")
                    .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Mã hợp đồng: #" + contract.getId()));
            document.add(new Paragraph("Sinh viên: " + contract.getStudent().getFullName() + " (" + contract.getStudent().getStudentCode() + ")"));
            document.add(new Paragraph("Phòng: " + contract.getRoom().getRoomCode() + " - Tòa: " + contract.getRoom().getBuilding()));
            document.add(new Paragraph("Ngày bắt đầu: " + contract.getStartDate()));
            document.add(new Paragraph("Ngày kết thúc: " + contract.getEndDate()));
            document.add(new Paragraph("Giá trị hợp đồng: " + (contract.getAmount() != null ? String.format("%,.0f", contract.getAmount()) : "0") + " VNĐ"));
            document.add(new Paragraph("Trạng thái: " + contract.getStatus()));
            
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("ĐIỀU KHOẢN CHUNG:"));
            document.add(new Paragraph("1. Bên thuê có trách nhiệm bảo quản tài sản trong phòng."));
            document.add(new Paragraph("2. Tuân thủ nội quy của ký túc xá."));
            document.add(new Paragraph("3. Thanh toán tiền phòng đúng hạn."));
            
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Đại diện Ký túc xá                                  Người thuê"));
            document.add(new Paragraph("(Ký và ghi rõ họ tên)                             (Ký và ghi rõ họ tên)"));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}
