package com.spscommerce.interview.model.organization;

import com.spscommerce.interview.model.ImportState;
import lombok.Data;

import java.io.File;

@Data
public class OrganizationImportJob {

    private String importId;
    private ImportState importState;
    private Double percentageComplete;
    private String failureMessage;
    private File file;

}
