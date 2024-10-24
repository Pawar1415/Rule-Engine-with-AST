package com.example.demo.DTO;

import com.example.demo.model.ASTNode;
import lombok.Data;

import java.util.Map;

@Data
public class EvaluationRequest {
    private ASTNode rule;
    private Map<String, Object> attributes;
}
