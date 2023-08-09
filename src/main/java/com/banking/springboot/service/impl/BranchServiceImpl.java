package com.banking.springboot.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.banking.springboot.dto.BranchDto;
import com.banking.springboot.entity.Branch;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.springboot.repository.BranchRepository;
import com.banking.springboot.service.BranchService;

@Service
public class BranchServiceImpl implements BranchService {

	@Autowired
	private BranchRepository branchRepository;

	public BranchServiceImpl(BranchRepository branchRepository) {
		super();
		this.branchRepository = branchRepository;
	}

	@Override
	public List<BranchDto> getAllBranches() {
		List<Branch> branches = branchRepository.findAll();
		List<BranchDto> branchesToJson = new ArrayList<>();
		for(Branch branch : branches) {
			BranchDto dto = branchToJson(branch);
			branchesToJson.add(dto);
		}
		return branchesToJson;
	}

	@Override
	public BranchDto getBranchByName(String name) throws BranchDoesNotExistException {

		Branch branch = branchRepository.findByName(name);
		if(branch != null) {
			return branchToJson(branch);
		} else {
			throw new BranchDoesNotExistException("Branch does not exist with name " + name);
		}
	}

	private BranchDto branchToJson(Branch b) {
		BranchDto dto = new BranchDto();
		dto.setName(b.getName());
		dto.setAddress(b.getAddress());
		dto.setCity(b.getCity());
		dto.setState(b.getState());
		dto.setZipCode(b.getZipCode());

		return dto;
	}

}
