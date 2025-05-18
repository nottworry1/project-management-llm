package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Label;
import org.kupchenko.projectmanagementllm.model.Task;

public interface LabelSuggestService {
    Label suggestLabel(Task task);
}
