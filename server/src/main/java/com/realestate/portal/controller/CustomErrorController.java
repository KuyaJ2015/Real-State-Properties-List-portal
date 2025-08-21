package com.realestate.portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        
        model.addAttribute("status", status != null ? status : "Error");
        
        if (exception != null) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("exception", exception);
        } else {
            model.addAttribute("message", "Unknown error occurred");
        }
        
        // If it's a 404 error, return the specific 404 page
        if (status != null && status.equals(404)) {
            return "error/404";
        }
        
        return "error/error";
    }
}
