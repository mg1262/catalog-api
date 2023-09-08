package com.spscommerce.interview.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {

    TOO_MANY_REQUESTS("too.many.requests", "Too Many Requests have been received within the allotted timeframe. Please try again in a few moments", HttpStatus.TOO_MANY_REQUESTS),
    ORG_READ_NOT_FOUND("org.read.not_found", "Requested Organization does not exist.", HttpStatus.BAD_REQUEST),
    ORG_UPDATE_ID_MISMATCH("org.update.id_mismatch", "The ID in the context path does not match the ID in payload", HttpStatus.BAD_REQUEST),
    ORG_UPDATE_NOT_FOUND("org.update.not_found", "Requested Organization does not exist.", HttpStatus.BAD_REQUEST),
    ORG_DELETE_NOT_FOUND("org.delete.not_found", "Requested Organization does not exist.", HttpStatus.BAD_REQUEST),
    ORG_CREATE_EXISTING_SUB_NOT_ALLOWED("org.create.existing_subs_not_allowed", "Existing Subscriptions are not allowed when creating a new Organization", HttpStatus.BAD_REQUEST),
    ORG_CREATE_SUB_ASSOCIATED_WITH_OTHER("org.create.sub_already_associated", "A subscription in the payload is already associated with another Organization", HttpStatus.BAD_REQUEST),
    SUB_NOT_FOUND_UPDATE_ORG("org.update.sub_not_found", "A subscription in the payload does not exist.", HttpStatus.BAD_REQUEST),
    SUB_READ_NOT_FOUND("sub.read.not_found", "Requested Subscription does not exist.", HttpStatus.BAD_REQUEST),
    SUB_UPDATE_ID_MISMATCH("sub.update.id_mismatch", "The ID in the context path does not match the ID in payload", HttpStatus.BAD_REQUEST),
    SUB_UPDATE_NOT_FOUND("sub.update.not_found", "Requested Subscription does not exist.", HttpStatus.BAD_REQUEST),
    SUB_DELETE_NOT_FOUND("sub.delete.not_found", "Requested Subscription does not exist.", HttpStatus.BAD_REQUEST),
    SUB_PRD_NOT_FOUND("sub.product.not_found", "Provided product with id not found.", HttpStatus.BAD_REQUEST),
    PRD_READ_NOT_FOUND("prd.read.not_found", "Requested Product does not exist.", HttpStatus.BAD_REQUEST),
    PRD_UPDATE_ID_MISMATCH("prd.update.id_mismatch", "The ID in the context path does not match the ID in payload", HttpStatus.BAD_REQUEST),
    PRD_UPDATE_NOT_FOUND("prd.update.not_found", "Requested Product does not exist.", HttpStatus.BAD_REQUEST),
    PRD_DELETE_NOT_FOUND("prd.delete.not_found", "Requested Product does not exist.", HttpStatus.BAD_REQUEST),
    SUB_IMPORT_NOT_MULTIPART_REQUEST("sub.import.not_multipart_request", "The request is not a multipart request.", HttpStatus.BAD_REQUEST),
    SUB_IMPORT_MULTIPART_NOT_FOUND("sub.import.multipart_not_found", "The request does not include any multipart content.", HttpStatus.BAD_REQUEST),
    SUB_IMPORT_UPLOAD_ERROR("sub.import.upload_error", "An error occurred while uploading the file.", HttpStatus.INTERNAL_SERVER_ERROR),
    SUB_IMPORT_UPLOAD_IO_ERROR("sub.import.upload_io_error", "An IO error occurred while uploading the file.", HttpStatus.INTERNAL_SERVER_ERROR),
    SUB_IMPORT_FILE_TYPE_NOT_SUPPORTED("sub.import.file_type_not_supported", "The file type in the request payload is not supported.", HttpStatus.BAD_REQUEST),
    SEARCH_QUERY_ERROR("search.query_error", "A parsing error occurred.  Please review your query.", HttpStatus.BAD_REQUEST),
    SEARCH_IO_ERROR("search.io_error", "An error occurred while processing the search request.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String errorCode;
    private final String errorDescription;
    private final HttpStatus httpStatus;

    ErrorCodes(String errorCode, String errorDescription, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.httpStatus = httpStatus;
    }

    public void throwException() {
        throw new OrganizationManagementRuntimeException(this);
    }

    public void throwException(Throwable e) {
        throw new OrganizationManagementRuntimeException(this, e);
    }
}
