package com.madhurya.interiordesign.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.madhurya.interiordesign.model.Gallery;
import com.madhurya.interiordesign.repository.GalleryRepository;

@Service
public class GalleryService {
    
    @Autowired
    private GalleryRepository galleryRepository;

    private final Path uploadPath;

    public GalleryService(@Value("${upload.path}") String uploadPath) {
        this.uploadPath = Paths.get(uploadPath);
        
        // Create directories if they don't exist
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
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