package dev.wildilde.car_stereo_wiki.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileService {
    /**
     * Uploads a file to the remote storage service.
     * @param file the file to upload
     * @param albumName the name of the album to add the file to (optional)
     * @return the URL of the uploaded file
     * @throws IOException if an I/O error occurs
     */
    String uploadFile(MultipartFile file, String albumName) throws IOException;
}
