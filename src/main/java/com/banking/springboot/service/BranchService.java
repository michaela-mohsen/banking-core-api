package com.banking.springboot.service;

import java.util.List;

import com.banking.springboot.dto.BranchDto;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import org.springframework.stereotype.Component;

@Component
public interface BranchService {
	List<BranchDto> getAllBranches();

	BranchDto getBranchByName(String name) throws BranchDoesNotExistException;
}
