    package com.example.Grand.controller;

    import com.example.Grand.models.Image;
    import com.example.Grand.repositories.ImageRepository;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import lombok.RequiredArgsConstructor;
    import org.springframework.core.io.InputStreamResource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.io.ByteArrayInputStream;

    @RestController
    @RequestMapping("/api/images")
    @RequiredArgsConstructor
    public class ImageController {

        private final ImageRepository imageRepository;

        @GetMapping("/{id}")
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> getImageById(@PathVariable Long id) {
            return imageRepository.findById(id)
                    .map(image -> {
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFileName() + "\"")
                                .contentType(MediaType.parseMediaType(image.getContentType()))
                                .contentLength(image.getSize())
                                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }
