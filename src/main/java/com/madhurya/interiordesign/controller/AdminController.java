package com.madhurya.interiordesign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.madhurya.interiordesign.service.GalleryService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping
    public String adminPanel(Model model) {
        model.addAttribute("images", galleryService.getAllImages());
        return "admin/dashboard";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                  @RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("category") String category,
                                  RedirectAttributes redirectAttributes) {
        try {
            galleryService.saveImage(file, title, description, category);
            redirectAttributes.addFlashAttribute("message", "Image uploaded successfully!");
            redirectAttributes.addFlashAttribute("images", galleryService.getAllImages());
        } catch (Exception e) {
            e.printStackTrace(); // Add this for debugging
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            galleryService.deleteImage(id);
            redirectAttributes.addFlashAttribute("message", "Image deleted successfully!");
            redirectAttributes.addFlashAttribute("images", galleryService.getAllImages());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete image: " + e.getMessage());
        }
        return "redirect:/admin";
    }
} 