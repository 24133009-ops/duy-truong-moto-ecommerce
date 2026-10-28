package com.duytruongmoto.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                               HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {
        log.error(">>> [DataIntegrityViolation] URL: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        String msg = "Dữ liệu nhập vào bị trùng lặp (Email hoặc Số điện thoại đã tồn tại trong hệ thống)! Vui lòng kiểm tra lại.";
        redirectAttributes.addFlashAttribute("errorMessage", msg);

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/home";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        log.warn(">>> [IllegalArgument] URL: {} | Message: {}", request.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/home";
    }
}
