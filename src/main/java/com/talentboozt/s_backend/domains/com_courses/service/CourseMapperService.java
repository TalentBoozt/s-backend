package com.talentboozt.s_backend.domains.com_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.CourseResponseDTO;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import org.springframework.stereotype.Service;

@Service
public class CourseMapperService {

    public CourseResponseDTO toResponseDTO(CourseModel course, CourseBatchModel batch) {
        CourseResponseDTO dto = new CourseResponseDTO();
        if (course == null) {
            return null;
        }
        dto.setId(course.getId());
        dto.setCompanyId(course.getCompanyId());
        dto.setName(course.getName());
        dto.setOverview(course.getOverview());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setOrganizer(course.getOrganizer());
        dto.setLevel(course.getLevel());
        dto.setRating(course.getRating());
        dto.setSkills(course.getSkills());
        dto.setRequirements(course.getRequirements());
        dto.setCertificate(course.isCertificate());

        if (batch != null) {
            dto.setCurrency(batch.getCurrency());
            dto.setPrice(batch.getPrice());
            dto.setOnetimePayment(batch.isOnetimePayment());
            dto.setInstallment(batch.getInstallment());
            dto.setDuration(batch.getDuration());
            dto.setModules(batch.getModules());
            dto.setLanguage(batch.getLanguage());
            dto.setLecturer(batch.getLecturer());
            dto.setImage(batch.getImage());
            dto.setPlatform(batch.getPlatform());
            dto.setLocation(batch.getLocation());
            dto.setStartDate(batch.getStartDate());
            dto.setFromTime(batch.getFromTime());
            dto.setToTime(batch.getToTime());
            dto.setUtcStart(batch.getUtcStart());
            dto.setUtcEnd(batch.getUtcEnd());
            dto.setTrainerTimezone(batch.getTrainerTimezone());
            dto.setCourseStatus(batch.getCourseStatus());
            dto.setPaymentMethod(batch.getPaymentMethod());
            dto.setPublicity(batch.isPublicity());
            dto.setMaterials(batch.getMaterials());
            dto.setQuizzes(batch.getQuizzes());
            dto.setBatchId(batch.getId());
        } else {
            dto.setCurrency(course.getCurrency());
            dto.setPrice(course.getPrice());
            dto.setOnetimePayment(course.isOnetimePayment());
            dto.setInstallment(course.getInstallment());
            dto.setDuration(course.getDuration());
            dto.setModules(course.getModules());
            dto.setLanguage(course.getLanguage());
            dto.setLecturer(course.getLecturer());
            dto.setImage(course.getImage());
            dto.setPlatform(course.getPlatform());
            dto.setLocation(course.getLocation());
            dto.setStartDate(course.getStartDate());
            dto.setFromTime(course.getFromTime());
            dto.setToTime(course.getToTime());
            dto.setUtcStart(course.getUtcStart());
            dto.setUtcEnd(course.getUtcEnd());
            dto.setTrainerTimezone(course.getTrainerTimezone());
            dto.setCourseStatus(course.getCourseStatus());
            dto.setPaymentMethod(course.getPaymentMethod());
            dto.setPublicity(course.isPublicity());
            dto.setMaterials(course.getMaterials());
            dto.setQuizzes(course.getQuizzes());
        }

        return dto;
    }
}
