package com.madhurya.interiordesign.service;

import com.madhurya.interiordesign.model.Gallery;
import com.madhurya.interiordesign.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GalleryService {
    
    @Autowired
    private GalleryRepository galleryRepository;

    private final Path uploadPath;

    public GalleryService() throws IOException {
        // Initialize upload path in static/uploads directory
        this.uploadPath = Paths.get(new ClassPathResource("static/uploads").getFile().getAbsolutePath());
        Files.createDirectories(uploadPath);
    }

    public Gallery saveImage(MultipartFile file, String title, String description, String category) {
        try {
            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            
            // Copy file to uploads directory
            Files.copy(file.getInputStream(), this.uploadPath.resolve(fileName));

            // Create gallery entry
            Gallery gallery = new Gallery();
            gallery.setTitle(title);
            gallery.setDescription(description);
            gallery.setFileName("/uploads/" + fileName);
            gallery.setCategory(category);
            gallery.setUploadDate(LocalDateTime.now());

            return galleryRepository.save(gallery);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public List<Gallery> getAllImages() {
        return galleryRepository.findAll();
    }

    public void deleteImage(Long id) {
        Gallery gallery = galleryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Image not found"));

        try {
            // Delete file from disk
            String fileName = gallery.getFileName().replace("/uploads/", "");
            Files.deleteIfExists(this.uploadPath.resolve(fileName));
            
            // Delete database entry
            galleryRepository.deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public Map<String, List<Gallery>> getAllImagesGroupedByCategory() {
        List<Gallery> allImages = galleryRepository.findAll();
        return allImages.stream()
                .collect(Collectors.groupingBy(Gallery::getCategory));
    }
} 