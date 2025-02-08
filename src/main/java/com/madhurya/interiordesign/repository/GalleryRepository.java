package com.madhurya.interiordesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.madhurya.interiordesign.model.Gallery;
import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByOrderByIdDesc();
    List<Gallery> findByTitleContainingIgnoreCase(String title);
} 