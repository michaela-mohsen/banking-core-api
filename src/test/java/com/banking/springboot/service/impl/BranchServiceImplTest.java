package com.banking.springboot.service.impl;

import com.banking.springboot.dto.BranchDto;
import com.banking.springboot.entity.Branch;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.repository.BranchRepository;
import com.banking.springboot.util.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.config.BeanComponentDefinitionBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class BranchServiceImplTest {
    @Autowired
    private BranchServiceImpl service;

    @MockBean
    private BranchRepository repository;

    @MockBean
    private Utility utility;

    @Test
    void getAllBranchesTest() {
        Branch branch = new Branch();
        List<Branch> branches = new ArrayList<>();
        branches.add(branch);
        when(repository.findAll()).thenReturn(branches);
        when(utility.convertBranchToJson(any())).thenReturn(new BranchDto());
        Assertions.assertEquals(1, service.getAllBranches().size());
    }

    @Test
    void getBranchByNameTest() throws BranchDoesNotExistException {
        when(repository.findByName("Branch")).thenReturn(new Branch());
        when(repository.findByName("Unknown Branch")).thenReturn(null);
        when(utility.convertBranchToJson(any())).thenReturn(new BranchDto());
        Assertions.assertNotNull(service.getBranchByName("Branch"));
        Assertions.assertThrows(BranchDoesNotExistException.class, () -> service.getBranchByName("Unknown Branch"));
    }
}
