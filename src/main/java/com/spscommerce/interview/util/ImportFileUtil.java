package com.spscommerce.interview.util;

import com.spscommerce.interview.error.ErrorCodes;
import lombok.Data;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix="import")
public class ImportFileUtil {

    private String location;
    private List<String> fileTypes;

    @PostConstruct
    public void init() {
        File importLocationFile = new File(location);
        if (!importLocationFile.exists()) {
            importLocationFile.mkdir();
        }
    }

    public File downloadFile(HttpServletRequest servletRequest) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(servletRequest);
            if (!isMultipart) {
                ErrorCodes.SUB_IMPORT_NOT_MULTIPART_REQUEST.throwException();
            }
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request
            FileItemIterator iter = upload.getItemIterator(servletRequest);
            if (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getName();
                String extension = FilenameUtils.getExtension(name);
                if (!this.fileTypes.contains(extension.toLowerCase())) {
                    ErrorCodes.SUB_IMPORT_FILE_TYPE_NOT_SUPPORTED.throwException();
                }
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    String filename = new StringBuilder(location).append("/subscriptions-").append(System.currentTimeMillis()).append(".csv").toString();
                    // Process the input stream
                    OutputStream out = new FileOutputStream(filename);
                    IOUtils.copy(stream, out);
                    stream.close();
                    out.close();
                    return new File(filename);
                }
            } else {
                ErrorCodes.SUB_IMPORT_MULTIPART_NOT_FOUND.throwException();
            }
        } catch (FileUploadException e) {
            ErrorCodes.SUB_IMPORT_UPLOAD_ERROR.throwException();
        } catch (IOException e) {
            ErrorCodes.SUB_IMPORT_UPLOAD_IO_ERROR.throwException();
        }
        return null;
    }

}
