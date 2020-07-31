package ru.wawulya.CBTicket.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.wawulya.CBTicket.config.FileStorageConfig;
import ru.wawulya.CBTicket.error.FileStorageException;
import ru.wawulya.CBTicket.error.MyFileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Data
@Slf4j
@Service
public class FileStorageService {

    private final Path uploadFileStorageLocation;
    private final Path downloadFileStorageLocation;
    private final Path configStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.uploadFileStorageLocation = Paths
                                    .get(fileStorageConfig.getUploadDir())
                                    .toAbsolutePath()
                                    .normalize();

        this.downloadFileStorageLocation = Paths
                .get(fileStorageConfig.getDownloadDir())
                .toAbsolutePath()
                .normalize();

        this.configStorageLocation = Paths
                                    .get(fileStorageConfig.getConfigDir())
                                    .toAbsolutePath()
                                    .normalize();

        try {
            Files.createDirectories(this.uploadFileStorageLocation);
            Files.createDirectories(this.downloadFileStorageLocation);
            Files.createDirectories(this.configStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directories ...", ex);
        }
    }

    public Path uploadFile(UUID sessionId, MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info(sessionId + " | file.getOriginalFilename() : " + file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            log.info(sessionId + " | fileStorageLocation :" + uploadFileStorageLocation);
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.uploadFileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Path storeConfigFile(UUID sessionId, MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info(sessionId + " | file.getOriginalFilename() : " + file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            log.info(sessionId + " | fileStorageLocation :" + configStorageLocation);
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.configStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

       public boolean deleteFile(UUID sessionId, String filename) {
            // Check if the file's name contains invalid characters
            Path targetFilePath = this.configStorageLocation.resolve(filename);

            File deletingFile = targetFilePath.toFile();

            if (deletingFile.exists()) {
                deletingFile.delete();
                log.debug("File deleted successfully [" + targetFilePath.toString() + "]");
                return true;
            }
            else {
                log.warn("File : " + targetFilePath.toString() + " not found!");
                return false;
            }

    }

    /*public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }*/
}
