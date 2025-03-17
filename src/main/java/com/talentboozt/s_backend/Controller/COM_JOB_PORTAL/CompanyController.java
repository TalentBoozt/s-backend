package com.talentboozt.s_backend.Controller.COM_JOB_PORTAL;

import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CompanyModel;
import com.talentboozt.s_backend.Service.COM_JOB_PORTAL.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/company")
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @GetMapping("/all")
    public List<CompanyModel> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/getAll")
    public List<CompanyModel> getAllCompanies(@RequestParam int page, @RequestParam int size) {
        return companyService.getCompaniesPaginated(page, size);
    }

    @GetMapping("/get/{id}")
    public CompanyModel getCompany(@PathVariable String id) {
        return companyService.getCompany(id);
    }

    @GetMapping("/getByType/{type}")
    public Optional<List<CompanyModel>> getCompanyByType(@PathVariable String type) {
        return companyService.getCompanyByType(type);
    }

    @PostMapping("/add")
    public CompanyModel addCompany(@RequestBody CompanyModel company) {
        return companyService.addCompany(company);
    }

    @PutMapping("/update/updateLogo")
    public CompanyModel updateLogoPic(@RequestBody CompanyModel company) {
        return companyService.updateLogoPic(company);
    }

    @PutMapping("/update/updateCover")
    public CompanyModel updateCoverPic(@RequestBody CompanyModel company) {
        return companyService.updateCoverPic(company);
    }

    @PutMapping("/update/updateThumb1")
    public CompanyModel updateThumb1Pic(@RequestBody CompanyModel company) {
        return companyService.updateThumb1Pic(company);
    }

    @PutMapping("/update/updateThumb2")
    public CompanyModel updateThumb2Pic(@RequestBody CompanyModel company) {
        return companyService.updateThumb2Pic(company);
    }

    @PutMapping("/update/updateThumb3")
    public CompanyModel updateThumb3Pic(@RequestBody CompanyModel company) {
        return companyService.updateThumb3Pic(company);
    }

    @PutMapping("/update")
    public CompanyModel updateCompany(@RequestBody CompanyModel company) {
        return companyService.updateCompany(company);
    }

    @PutMapping("/update/notifications")
    public CompanyModel updateNotifications(@RequestBody CompanyModel company) {
        return companyService.updateNotifications(company);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCompany(@PathVariable String id) {
        companyService.deleteCompany(id);
    }
}
