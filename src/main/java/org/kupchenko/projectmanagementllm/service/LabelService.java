package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Label;

import java.util.List;
import java.util.Optional;

public interface LabelService {

    Optional<Label> findByNameOptional(String name);

    List<Label> findAll();

    Label save(Label label);
}
