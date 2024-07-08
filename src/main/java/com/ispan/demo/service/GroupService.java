package com.ispan.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.demo.model.Group;
import com.ispan.demo.model.GroupRepository;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepo;

    public Group createOrUpdateGroup(Group group) {
        return groupRepo.save(group);
    }

    public Group findGroupById(Integer id) {
        Optional<Group> optional = groupRepo.findById(id);
        return optional.orElse(null);
    }

    public void deleteGroup(Integer id) {
        groupRepo.deleteById(id);
    }

    public List<Group> findAllGroups() {
        return groupRepo.findAll();
    }
}
