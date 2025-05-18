package org.kupchenko.projectmanagementllm.service.impl;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Label;
import org.kupchenko.projectmanagementllm.repository.LabelRepository;
import org.kupchenko.projectmanagementllm.service.LabelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;

    @Override
    public Optional<Label> findByNameOptional(String name) {
        return labelRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    @Override
    public Label save(Label label) {
        return labelRepository.save(label);
    }
}

