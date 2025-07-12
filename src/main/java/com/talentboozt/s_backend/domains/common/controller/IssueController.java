package com.talentboozt.s_backend.domains.common.controller;

import com.talentboozt.s_backend.domains.common.model.IssueModel;
import com.talentboozt.s_backend.domains.common.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/portal_report-issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping("/add")
    public IssueModel addIssue(@RequestBody IssueModel issue) {
        return issueService.addIssue(issue);
    }

    @PostMapping("/all")
    public List<IssueModel> getAllIssues() {
        return issueService.getAllIssues();
    }

    @PutMapping("/update/updateAttachment")
    public IssueModel updateAttachment(@RequestBody IssueModel issue) {
        return issueService.updateAttachment(issue);
    }
}
