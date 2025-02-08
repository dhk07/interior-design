package com.madhurya.interiordesign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.madhurya.interiordesign.service.AppointmentService;
import com.madhurya.interiordesign.service.GalleryService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/admin/login")
    public String showLoginPage() {
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        // Replace with your actual admin credentials
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "admin/login";
        }
    }

    @GetMapping("/admin")
    public String adminPanel(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
        if (isLoggedIn != null && isLoggedIn) {
            return "admin/dashboard";
        }
        return "redirect:/admin/login";
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

    @GetMapping("/admin/gallery")
    public String showGalleryManager(HttpSession session, Model model) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
        if (isLoggedIn == null || !isLoggedIn) {
            return "redirect:/admin/login";
        }
        model.addAttribute("images", galleryService.getAllImages());
        return "admin/gallery-manager";
    }

    @GetMapping("/admin/appointments")
    public String showAppointments(HttpSession session, Model model) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
        if (isLoggedIn == null || !isLoggedIn) {
            return "redirect:/admin/login";
        }
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "admin/appointments";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminLoggedIn");
        return "redirect:/admin/login";
    }

    @PostMapping("/admin/appointments/confirm/{id}")
    public String confirmAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.confirmAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Appointment confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to confirm appointment: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @PostMapping("/admin/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.deleteAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Appointment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete appointment: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }
} 