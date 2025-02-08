package com.madhurya.interiordesign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.madhurya.interiordesign.model.Appointment;
import com.madhurya.interiordesign.service.AppointmentService;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointment")
    public String showAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "appointment";
    }

    @PostMapping("/appointment/book")
    public String bookAppointment(@ModelAttribute Appointment appointment, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.saveAppointment(appointment);
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to book appointment. Please try again.");
        }
        return "redirect:/appointment";
    }
} 