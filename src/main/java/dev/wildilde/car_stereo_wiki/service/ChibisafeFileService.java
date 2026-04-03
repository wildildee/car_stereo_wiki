package dev.wildilde.car_stereo_wiki.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ChibisafeFileService implements FileService {

    private final String apiKey;
    private final RestClient restClient;

    public ChibisafeFileService(
            @Value("${chibisafe.api.url}") String apiUrl,
            @Value("${chibisafe.api.key}") String apiKey,
            RestClient.Builder restClientBuilder) {
        this.apiKey = apiKey;
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
    }

    public String uploadFile(MultipartFile file, String albumName) throws IOException {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));
        fileHeaders.setContentDispositionFormData("files[]", file.getOriginalFilename());
        HttpEntity<Resource> filePart = new HttpEntity<>(file.getResource(), fileHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files[]", filePart);

        ChibisafeUploadResponse response = restClient.post()
                .uri("/api/upload")
                .header("x-api-key", apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(ChibisafeUploadResponse.class);

        if (response != null && response.getUrl() != null && !response.getUrl().isEmpty()) {
            // Add to album if albumName is provided
            System.out.println(albumName);
            if (albumName != null && !albumName.trim().isEmpty() && response.getUuid() != null) {

                addToAlbum(response.getUuid(), albumName);
            }
            return response.getUrl();
        }

        throw new IOException("Failed to upload file to Chibisafe: " + (response != null ? response : "Unknown error"));
    }

    private void addToAlbum(String fileUuid, String albumName) {
        try {
            ChibisafeAlbumsResponse albumsResponse = restClient.get()
                    .uri("/api/albums")
                    .header("x-api-key", apiKey)
                    .retrieve()
                    .body(ChibisafeAlbumsResponse.class);

            if (albumsResponse != null && albumsResponse.getAlbums() != null) {
                String albumUuid = albumsResponse.getAlbums().stream()
                        .filter(a -> albumName.equalsIgnoreCase(a.getName()))
                        .map(ChibisafeAlbum::getUuid)
                        .findFirst()
                        .orElse(null);

                if (albumUuid != null) {
                    restClient.post()
                            .uri("/api/file/{uuid}/album/{albumUuid}", fileUuid, albumUuid)
                            .header("x-api-key", apiKey)
                            .retrieve()
                            .toBodilessEntity();
                } else {
                    System.err.println("Album with name '" + albumName + "' not found.");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to add file " + fileUuid + " to album " + albumName + ": " + e.getMessage());
        }
    }

    public static class ChibisafeAlbumsResponse {
        private List<ChibisafeAlbum> albums;

        public List<ChibisafeAlbum> getAlbums() {
            return albums;
        }

        public void setAlbums(List<ChibisafeAlbum> albums) {
            this.albums = albums;
        }
    }

    public static class ChibisafeAlbum {
        private String uuid;
        private String name;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ChibisafeUploadResponse {
        private String name;
        private String uuid;
        private String url;
        private String identifier;
        private String publicUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getPublicUrl() {
            return publicUrl;
        }

        public void setPublicUrl(String publicUrl) {
            this.publicUrl = publicUrl;
        }

        public String toString() {
            return "ChibisafeUploadResponse{" +
                    "name='" + name + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", url='" + url + '\'' +
                    ", identifier='" + identifier + '\'' +
                    ", publicUrl='" + publicUrl + '\'' +
                    '}';
        }
    }
}