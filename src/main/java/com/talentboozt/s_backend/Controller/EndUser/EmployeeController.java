package com.talentboozt.s_backend.Controller.EndUser;

import com.talentboozt.s_backend.DTO.PLAT_JOB_PORTAL.FavJobDTO;
import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import com.talentboozt.s_backend.Service.EndUser.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/all")
    public List<EmployeeModel> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/getAll")
    public List<EmployeeModel> getAllEmployees(@RequestParam int page, @RequestParam int size) {
        return employeeService.getEmployeesPaginated(page, size);
    }

    @GetMapping("/get/{id}")
    public EmployeeModel getEmployee(@PathVariable String id) {
        return employeeService.getEmployee(id);
    }

    @PostMapping("/add")
    public EmployeeModel addEmployee(@RequestBody EmployeeModel employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/update")
    public EmployeeModel updateEmployee(@RequestBody EmployeeModel employee) {
        return employeeService.updateEmployee(employee);
    }

    @PutMapping("/update/sa")
    public EmployeeModel updateSearchAppearanceEmployee(@RequestBody EmployeeModel employee) {
        return employeeService.updateSearchAppearance(employee);
    }

    @PutMapping("/update/notifications")
    public EmployeeModel updateNotifications(@RequestBody EmployeeModel employee) {
        return employeeService.updateNotifications(employee);
    }

    @PutMapping("/update/updateProfilePic")
    public EmployeeModel updateProfilePic(@RequestBody EmployeeModel employee) {
        return employeeService.updateProfilePic(employee);
    }

    @PutMapping("/update/updateCoverPic")
    public EmployeeModel updateCoverPic(@RequestBody EmployeeModel employee) {
        return employeeService.updateCoverPic(employee);
    }

    @PutMapping("/update/updateResume")
    public EmployeeModel updateResume(@RequestBody EmployeeModel employee) {
        return employeeService.updateResume(employee);
    }

    @PutMapping("/save-job/{empId}")
    public EmployeeModel saveJob(@PathVariable String empId, @RequestBody FavJobDTO jobDto) {
        return employeeService.saveFavoriteJob(empId, jobDto);
    }

    @PutMapping("/remove-job/{empId}/{jobId}")
    public EmployeeModel deleteJob(@PathVariable String empId, @PathVariable String jobId) {
        return employeeService.removeFavoriteJob(empId, jobId);
    }

    @PutMapping("/update/saved-job/status/{empId}")
    public EmployeeModel changeFavoriteJobStatus(@PathVariable String empId, @RequestBody FavJobDTO jobDto) {
        return employeeService.changeFavoriteJobStatus(empId, jobDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
    }

    @DeleteMapping("/delete/company/{id}")
    public void deleteCompany(@PathVariable String id) {
        employeeService.deleteCompany(id);
    }
}
