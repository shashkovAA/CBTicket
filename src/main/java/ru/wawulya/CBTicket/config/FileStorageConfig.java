package ru.wawulya.CBTicket.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {
    private String uploadDir;
    private String downloadDir;

    public String getUploadDir() {

        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {

        this.uploadDir = uploadDir;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }
}