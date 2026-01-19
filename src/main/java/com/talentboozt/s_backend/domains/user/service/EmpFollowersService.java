package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.user.dto.EmpFollowersDTO;
import com.talentboozt.s_backend.domains.user.model.EmpFollowersModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.EmpFollowersRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpFollowersService {
    @Autowired
    EmpFollowersRepository empFollowersRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public List<EmpFollowersModel> getEmpFollowersByEmployeeId(String employeeId) {
        return empFollowersRepository.findByEmployeeId(employeeId);
    }

    public EmpFollowersModel addEmpFollowers(EmpFollowersModel empFollowers) {
        List<EmpFollowersModel> empFollowersList = getEmpFollowersByEmployeeId(empFollowers.getEmployeeId());
        EmpFollowersModel empFollowersModel;

        if (!empFollowersList.isEmpty()) {
            empFollowersModel = empFollowersList.get(0);
            List<EmpFollowersDTO> followers = empFollowersModel.getFollowers();

            if (followers == null) {
                followers = new ArrayList<>();
            }
            followers.addAll(empFollowers.getFollowers());
            empFollowersModel.setFollowers(followers);
        } else {
            empFollowersModel = empFollowersRepository.save(empFollowers);
        }

        empFollowersRepository.save(empFollowersModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(empFollowers.getEmployeeId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setFollowers(empFollowersModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            profileCompleted.put("followers", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }

        return empFollowersModel;
    }

    public EmpFollowersModel updateEmpFollowers(String id, EmpFollowersModel empFollowers) {
        EmpFollowersModel empFollowersModel = empFollowersRepository.findById(Objects.requireNonNull(id)).orElse(null);

        if (empFollowersModel != null) {
            List<EmpFollowersDTO> followers = empFollowersModel.getFollowers();

            empFollowersModel.setEmployeeId(empFollowers.getEmployeeId());
            empFollowersModel.setFollowers(followers);

            return empFollowersRepository.save(empFollowersModel);
        }
        return null;
    }

    public void deleteEmpFollowers(String employeeId) {
        List<EmpFollowersModel> followers = getEmpFollowersByEmployeeId(employeeId);
        if (!followers.isEmpty()) {
            empFollowersRepository.deleteByEmployeeId(employeeId);
        }
    }

    public EmpFollowersModel deleteEmpFollower(String employeeId, String followerId) {
        List<EmpFollowersModel> followers = empFollowersRepository.findByEmployeeId(employeeId);
        if (!followers.isEmpty()) {
            EmpFollowersModel empFollowersModel = followers.get(0);
            List<EmpFollowersDTO> followings = empFollowersModel.getFollowers();
            if (followings != null) {
                followings.removeIf(follower -> follower.getFollowerId().equals(followerId));
                empFollowersModel.setFollowers(followings);
                empFollowersRepository.save(empFollowersModel);

                return empFollowersModel;
            }
        } else {
            throw new RuntimeException("Followers not found for employeeId: " + employeeId);
        }
        return null;
    }

    public EmpFollowersModel editFollower(String employeeId, EmpFollowersDTO followersDto) {
        List<EmpFollowersModel> followersList = getEmpFollowersByEmployeeId(employeeId);
        if (!followersList.isEmpty()) {
            EmpFollowersModel empFollowersModel = followersList.get(0);
            List<EmpFollowersDTO> followers = empFollowersModel.getFollowers();
            if (followers != null) {
                for (EmpFollowersDTO follower : followers) {
                    if (follower.getId().equals(followersDto.getId())) {
                        follower.setFollowerId(followersDto.getFollowerId());
                        follower.setFollowerName(followersDto.getFollowerName());
                        follower.setFollowerOccupation(followersDto.getFollowerOccupation());
                        follower.setFollowerImage(followersDto.getFollowerImage());
                        follower.setFollowerLocation(followersDto.getFollowerLocation());
                        break;
                    }
                }
                empFollowersModel.setFollowers(followers);
                empFollowersRepository.save(empFollowersModel);
            }
            return empFollowersModel;
        }
        throw new RuntimeException("Followers not found for employeeId: " + employeeId);
    }

    @Async
    public CompletableFuture<List<EmpFollowersModel>> getEmpFollowersByEmployeeIdAsync(String employeeId) {
        List<EmpFollowersModel> empFollowers = getEmpFollowersByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empFollowers);
    }

    // Events
    public void updateFollowersForUser(String userId, String fullName, String occupation, String profileImage) {
        List<EmpFollowersModel> followersList = empFollowersRepository.findByEmployeeId(userId);
        for (EmpFollowersModel followersModel : followersList) {
            List<EmpFollowersDTO> followers = followersModel.getFollowers();
            if (followers != null) {
                for (EmpFollowersDTO follower : followers) {
                    if (follower.getFollowerId().equals(userId)) {
                        follower.setFollowerName(fullName);
                        follower.setFollowerOccupation(occupation);
                        follower.setFollowerImage(profileImage);
                    }
                }
                empFollowersRepository.save(followersModel);
            }
        }
    }
}
