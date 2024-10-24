package com.example.demo.service;

import com.example.demo.model.ASTNode;

import java.util.List;
import java.util.Map;

public interface RuleEngineService {
    public ASTNode createRule(String ruleString);
    public ASTNode combineRules(List<ASTNode> rules);
    public boolean evaluateRule(ASTNode root, Map<String, Object> attributes);

    }
