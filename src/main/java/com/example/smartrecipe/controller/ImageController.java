    package com.example.smartrecipe.controller;

    import com.example.smartrecipe.repositories.ImageRepository;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.InputStreamResource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.io.ByteArrayInputStream;

    @RestController
    @RequestMapping("/api/images")
    public class ImageController {

        private final ImageRepository imageRepository;


        @Autowired
        public ImageController(ImageRepository imageRepository) {
            this.imageRepository = imageRepository;
        }

        @GetMapping("/{id}")
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<?> getImageById(@PathVariable Long id) {
            return imageRepository.findById(id)
                    .map(image -> {
                        // Получаем тип контента по расширению файла (например, для jpg, png и т. д.)
                        String contentType = "image/jpeg"; // Можно добавить логику для других типов файлов

                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""  + "\"")
                                .contentType(MediaType.parseMediaType(contentType)) // Устанавливаем тип контента
                                .contentLength(image.getBytes().length) // Размер файла в байтах
                                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
    }
