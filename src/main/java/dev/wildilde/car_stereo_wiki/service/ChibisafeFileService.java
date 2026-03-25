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

@Service
public class ChibisafeFileService implements FileService {

    private final String apiUrl;
    private final String apiKey;
    private final RestClient restClient;

    public ChibisafeFileService(
            @Value("${chibisafe.api.url}") String apiUrl,
            @Value("${chibisafe.api.key}") String apiKey,
            RestClient.Builder restClientBuilder) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
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

        System.out.println(response);

        if (response != null && response.getUrl() != null && !response.getUrl().isEmpty()) {
            return response.getUrl();
        }

        throw new IOException("Failed to upload file to Chibisafe: " + (response != null ? response : "Unknown error"));
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