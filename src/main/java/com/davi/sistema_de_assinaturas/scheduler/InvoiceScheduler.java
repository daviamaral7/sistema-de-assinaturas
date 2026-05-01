package com.davi.sistema_de_assinaturas.scheduler;

import com.davi.sistema_de_assinaturas.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final InvoiceService invoiceService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runOverdueJob() {
        invoiceService.automaticallyMarkAllOverdue();
    }
}
