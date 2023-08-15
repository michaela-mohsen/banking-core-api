package com.banking.springboot.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.banking.springboot.dto.BranchDto;
import com.banking.springboot.entity.Branch;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.springboot.repository.BranchRepository;
import com.banking.springboot.service.BranchService;

@Service
@Slf4j
public class BranchServiceImpl implements BranchService {

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private Utility util;

	@Override
	public List<BranchDto> getAllBranches() {
		List<Branch> branches = branchRepository.findAll();
		List<BranchDto> branchesToJson = new ArrayList<>();
		for(Branch branch : branches) {
			BranchDto dto = util.convertBranchToJson(branch);
			branchesToJson.add(dto);
		}
		return branchesToJson;
	}

	@Override
	public BranchDto getBranchByName(String name) throws BranchDoesNotExistException {
		log.info("Inside getBranchByName: {}", name);
		Branch branch = branchRepository.findByName(name);
		if(branch != null) {
			return util.convertBranchToJson(branch);
		} else {
			log.error("Branch not found with name {}", name);
			throw new BranchDoesNotExistException("Branch does not exist with name " + name);
		}
	}

}
