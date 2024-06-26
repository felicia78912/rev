package com.fitness.fitness.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fitness.fitness.model.Manager;
import com.fitness.fitness.model.Trainer;
import com.fitness.fitness.model.User;
import com.fitness.fitness.repository.TrainerRepo;
import com.fitness.fitness.service.ManagerService;
import com.fitness.fitness.service.UserService;

@Controller
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @Autowired
    private TrainerRepo trainerRepo; 
    
    @GetMapping("/manager_add_appointment")
    public String showAddAppointmentForm(Model model) {
        List<Trainer> trainers = trainerRepo.findAll();
        Manager manager = new Manager();
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("trainers", trainers);
        model.addAttribute("appointment", manager);
        model.addAttribute("currentDate", currentDate);
        return "manager-add-appointment";
    }

    @PostMapping("/manager_add_appointment")
    public String addAppointment(@ModelAttribute("appointment") Manager manager, Model model) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        // Check if the entered email corresponds to an existing user
        User user = userService.getUserByEmail(manager.getCustomerEmail());
        if (user == null) {
            model.addAttribute("error", "No user found with the entered email.");
            // Retrieve the list of trainers and add it to the model
            List<Trainer> trainers = trainerRepo.findAll();
            model.addAttribute("trainers", trainers);
            model.addAttribute("currentDate", currentDate); // Add currentDate to the model
            return "manager-add-appointment";
        }

        if (manager.getPreferredTrainer().isEmpty()
                || manager.getClassName().isEmpty() || manager.getDate() == null
                || manager.getTimeSlot() == null) {
            model.addAttribute("error", "All fields are required.");
        } else if (manager.getDate().isBefore(currentDate) ||
                   (manager.getDate().isEqual(currentDate) && manager.getTimeSlot().isBefore(currentTime))) {
            model.addAttribute("error", "You cannot book appointments in the past.");
        } else {
            managerService.manageraddAppointment(manager);
            model.addAttribute("message", "Appointment added successfully!");
        }
        
        // Retrieve the list of trainers and add it to the model
        List<Trainer> trainers = trainerRepo.findAll();
        model.addAttribute("trainers", trainers);
        model.addAttribute("currentDate", currentDate); // Add currentDate to the model
        
        return "manager-add-appointment";
    }

}
