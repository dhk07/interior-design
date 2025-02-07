package com.madhurya.interiordesign.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.madhurya.interiordesign.model.Gallery;
import com.madhurya.interiordesign.service.GalleryService;

@Controller
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/gallery")
    public String showGallery(Model model) {
        Map<String, List<Gallery>> imagesByCategory = galleryService.getAllImagesGroupedByCategory();
        model.addAttribute("imagesByCategory", imagesByCategory);
        return "gallery";
    }
} 