package com.talentboozt.s_backend.Service.common;

import com.talentboozt.s_backend.Model.EmpFollowersModel;
import com.talentboozt.s_backend.Model.EmpFollowingModel;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


@Service
public class ProfileUpdateService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void bulkUpdateFollowingsAndFollowers(String userId, String fullName, String occupation, String profileImage) {
        if(userId == null) return;

        // Update followers
        Query followersQuery = new Query();
        followersQuery.addCriteria(Criteria.where("followers.followerId").is(userId));
        Update followersUpdate = new Update()
                .set("followers.$.followerName", fullName)
                .set("followers.$.followerOccupation", occupation)
                .set("followers.$.followerImage", profileImage);

        UpdateResult followersResult = mongoTemplate.updateMulti(followersQuery, followersUpdate, EmpFollowersModel.class);

        // Update followings
        Query followingsQuery = new Query();
        followingsQuery.addCriteria(Criteria.where("followings.followingId").is(userId));
        Update followingsUpdate = new Update()
                .set("followings.$.followingName", fullName)
                .set("followings.$.followingOccupation", occupation)
                .set("followings.$.followingImage", profileImage);

        UpdateResult followingsResult = mongoTemplate.updateMulti(followingsQuery, followingsUpdate, EmpFollowingModel.class);
    }
}

