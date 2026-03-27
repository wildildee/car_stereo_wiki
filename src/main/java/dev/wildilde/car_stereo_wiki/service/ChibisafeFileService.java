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

    public String uploadFile(MultipartFile file, String tag) throws IOException {
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
            // Apply native tag if tag is provided
            if (tag != null && !tag.trim().isEmpty() && response.getUuid() != null) {
                applyTag(response.getUuid(), tag);
            }
            return response.getUrl();
        }

        throw new IOException("Failed to upload file to Chibisafe: " + (response != null ? response : "Unknown error"));
    }

    private void applyTag(String fileUuid, String tagName) {
        try {
            ChibisafeTagsResponse tagsResponse = restClient.get()
                    .uri("/api/tags")
                    .header("x-api-key", apiKey)
                    .retrieve()
                    .body(ChibisafeTagsResponse.class);

            if (tagsResponse != null && tagsResponse.getTags() != null) {
                String tagUuid = tagsResponse.getTags().stream()
                        .filter(t -> tagName.equalsIgnoreCase(t.getName()))
                        .map(ChibisafeTag::getUuid)
                        .findFirst()
                        .orElse(null);

                if (tagUuid != null) {
                    restClient.post()
                            .uri("/api/file/{uuid}/tag/{tagUuid}", fileUuid, tagUuid)
                            .header("x-api-key", apiKey)
                            .retrieve()
                            .toBodilessEntity();
                } else {
                    System.out.println("[DEBUG_LOG] Tag not found: " + tagName);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to apply tag " + tagName + " to file " + fileUuid + ": " + e.getMessage());
        }
    }

    public static class ChibisafeTagsResponse {
        private List<ChibisafeTag> tags;

        public List<ChibisafeTag> getTags() {
            return tags;
        }

        public void setTags(List<ChibisafeTag> tags) {
            this.tags = tags;
        }
    }

    public static class ChibisafeTag {
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