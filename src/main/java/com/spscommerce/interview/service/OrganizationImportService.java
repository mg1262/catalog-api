package com.spscommerce.interview.service;

import com.spscommerce.interview.dao.entity.Location;
import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.error.OrganizationManagementRuntimeException;
import com.spscommerce.interview.model.ImportState;
import com.spscommerce.interview.model.organization.CreateUpdateOrganizationResult;
import com.spscommerce.interview.model.organization.OrganizationImportJob;
import com.spscommerce.interview.model.organization.OrganizationImportResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
public class OrganizationImportService {

    private Map<String, OrganizationImportJob> importJobs;
    private LinkedBlockingQueue<String> importQueue;

    private final OrganizationService organizationService;

    @PostConstruct
    public void init() {
        this.importJobs = new HashMap<>();
        this.importQueue = new LinkedBlockingQueue<>();
        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    String importId = importQueue.take();
                    runImport(importId);
                } catch (InterruptedException e) {
                    // Throw exception
                }
            }
        });
    }

    public OrganizationImportResult importOrganizations(File file) {
        OrganizationImportResult result = new OrganizationImportResult();
        OrganizationImportJob importJob = new OrganizationImportJob();
        importJob.setImportId(UUID.randomUUID().toString());
        importJob.setImportState(ImportState.PENDING);
        importJob.setFile(file);
        importJob.setPercentageComplete(0d);
        this.importJobs.put(importJob.getImportId(), importJob);
        result.setImportId(importJob.getImportId());
        importQueue.add(importJob.getImportId());
        return result;
    }

    public OrganizationImportJob importStatus(String importId) {
        return this.importJobs.get(importId);
    }

    private void runImport(String importId) {
        OrganizationImportJob importJob = this.importJobs.get(importId);
        importJob.setImportState(ImportState.IMPORTING);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(importJob.getFile()));
            bufferedReader.readLine();
            String line = bufferedReader.readLine();
            while (line != null) {
                OrganizationEntity organizationEntity = convert(line);
                try {
                    CreateUpdateOrganizationResult result = this.organizationService.createOrganization(organizationEntity);
                } catch (OrganizationManagementRuntimeException ex) {

                }
                line = bufferedReader.readLine();
            }
            importJob.setImportState(ImportState.FINISHED);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OrganizationEntity convert(String line) {
        String[] tokens = line.split(",");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(tokens[0]);
        organization.setName(tokens[1]);
        organization.setLocation(new Location());
        organization.getLocation().setAddress(tokens[2]);
        organization.getLocation().setCity(tokens[3]);
        organization.getLocation().setState(tokens[4]);
        organization.getLocation().setZipCode(tokens[5]);
        return organization;
    }



}
