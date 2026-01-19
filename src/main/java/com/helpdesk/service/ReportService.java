package com.helpdesk.service;

import com.helpdesk.entity.Ticket;
import com.helpdesk.repository.TicketRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private TicketRepository ticketRepository;

    public byte[] generateTicketReport() {
        try {
            List<Ticket> tickets = ticketRepository.findAll();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Заголовок
            document.add(new Paragraph("Helpdesk Report - Ticket Statistics")
                    .setFontSize(18)
                    .setBold());
            document.add(new Paragraph("\n"));

            // Общая статистика
            document.add(new Paragraph("Total Tickets: " + tickets.size())
                    .setFontSize(12));

            // Подсчёт по статусам
            Map<String, Long> statusCount = new HashMap<>();
            for (Ticket ticket : tickets) {
                String status = ticket.getStatus().getName();
                statusCount.put(status, statusCount.getOrDefault(status, 0L) + 1);
            }

            document.add(new Paragraph("\nTickets by Status:").setFontSize(14).setBold());
            for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
                document.add(new Paragraph("  " + entry.getKey() + ": " + entry.getValue()));
            }

            // Подсчёт по категориям
            Map<String, Long> categoryCount = new HashMap<>();
            for (Ticket ticket : tickets) {
                String category = ticket.getCategory().getName();
                categoryCount.put(category, categoryCount.getOrDefault(category, 0L) + 1);
            }

            document.add(new Paragraph("\nTickets by Category:").setFontSize(14).setBold());
            for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
                document.add(new Paragraph("  " + entry.getKey() + ": " + entry.getValue()));
            }

            // Среднее время решения (для решённых заявок)
            List<Ticket> resolvedTickets = tickets.stream()
                    .filter(t -> t.getStatus().getName().equals("Решена") ||
                                 t.getStatus().getName().equals("Закрыта"))
                    .toList();

            if (!resolvedTickets.isEmpty()) {
                long totalMinutes = 0;
                for (Ticket ticket : resolvedTickets) {
                    if (ticket.getCreatedAt() != null && ticket.getUpdatedAt() != null) {
                        Duration duration = Duration.between(ticket.getCreatedAt(), ticket.getUpdatedAt());
                        totalMinutes += duration.toMinutes();
                    }
                }
                long avgMinutes = totalMinutes / resolvedTickets.size();
                long hours = avgMinutes / 60;
                long minutes = avgMinutes % 60;

                document.add(new Paragraph("\nAverage Resolution Time: " +
                        hours + " hours " + minutes + " minutes")
                        .setFontSize(12));
            }

            // Загрузка операторов
            Map<String, Long> operatorLoad = new HashMap<>();
            for (Ticket ticket : tickets) {
                if (ticket.getAssignedTo() != null) {
                    String operator = ticket.getAssignedTo().getFullName();
                    operatorLoad.put(operator, operatorLoad.getOrDefault(operator, 0L) + 1);
                }
            }

            if (!operatorLoad.isEmpty()) {
                document.add(new Paragraph("\nOperator Workload:").setFontSize(14).setBold());
                for (Map.Entry<String, Long> entry : operatorLoad.entrySet()) {
                    document.add(new Paragraph("  " + entry.getKey() + ": " + entry.getValue() + " tickets"));
                }
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}
