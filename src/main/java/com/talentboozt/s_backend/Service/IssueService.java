package com.talentboozt.s_backend.Service;

import com.talentboozt.s_backend.Model.IssueModel;
import com.talentboozt.s_backend.Repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    public IssueModel addIssue(IssueModel issue) {
        return issueRepository.save(issue);
    }

    public List<IssueModel> getAllIssues() {
        return issueRepository.findAll();
    }

    public IssueModel updateAttachment(IssueModel issue) {
        Optional<IssueModel> issueModel = issueRepository.findById(issue.getId());
        if (issueModel.isPresent()) {
            IssueModel existingIssue = issueModel.get();
            existingIssue.setAttachment(issue.getAttachment());

            return issueRepository.save(existingIssue);
        }
        return null;
    }
}
