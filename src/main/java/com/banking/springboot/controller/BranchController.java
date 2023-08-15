package com.banking.springboot.controller;

import com.banking.springboot.dto.BranchDto;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.service.impl.BranchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@Slf4j
public class BranchController {

    @Autowired
    private BranchServiceImpl branchService;

    @GetMapping("/branches")
    public ResponseEntity<Object> listAllBranches() {
        List<BranchDto> branches = branchService.getAllBranches();
        return new ResponseEntity<>(branches, HttpStatus.OK);
    }

    @GetMapping("/branches/search")
    public ResponseEntity<Object> getBranchByName(@RequestParam String name) {
        log.info("Inside getBranchByName: {}", name);
        try {
            BranchDto dto = branchService.getBranchByName(name);
            return new ResponseEntity<>(dto,HttpStatus.OK);
        } catch (BranchDoesNotExistException be) {
            log.error("Error inside getBranchByName: {}", be.getMessage());
            return new ResponseEntity<>(be.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error inside getBranchByName: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
