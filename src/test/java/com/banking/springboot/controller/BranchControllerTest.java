package com.banking.springboot.controller;

import com.banking.springboot.dto.BranchDto;
import com.banking.springboot.entity.Branch;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.service.impl.BranchServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BranchControllerTest {
    @InjectMocks
    private BranchController controller;

    @Mock
    private BranchServiceImpl service;

    @BeforeEach
    public void init() {
        openMocks(this);
    }

    @Test
    void listAllBranchesTest() {
        when(service.getAllBranches()).thenReturn(new ArrayList<>());
        Assertions.assertEquals(HttpStatus.OK, controller.listAllBranches().getStatusCode());
    }

    @Test
    void getBranchByNameTest() throws BranchDoesNotExistException {
        when(service.getBranchByName("Branch")).thenReturn(new BranchDto());
        when(service.getBranchByName("Nonexistent Branch")).thenThrow(BranchDoesNotExistException.class);
        when(service.getBranchByName("Other Error")).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.getBranchByName("Branch").getStatusCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getBranchByName("Nonexistent Branch").getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getBranchByName("Other Error").getStatusCode());
    }
}
