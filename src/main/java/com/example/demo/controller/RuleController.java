package com.example.demo.controller;

import com.example.demo.DTO.EvaluationRequest;
import com.example.demo.model.ASTNode;
import com.example.demo.response.ResponseHandler;
import com.example.demo.service.RuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
@CrossOrigin("/")
public class RuleController {
    @Autowired
    private RuleEngineService ruleEngineService;




    // API to create and evaluate rules
    @PostMapping("/create")
    public ResponseEntity<Object> createRule(@RequestBody String ruleString) {
        try {
            ASTNode rule = ruleEngineService.createRule(ruleString);
            return ResponseHandler.generateResponse("Rule created successfully", HttpStatus.OK, rule);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("An error occurred while processing the rule.", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    // API to combine rules
    @PostMapping("/combine")
    public ResponseEntity<Object> combineRules(@RequestBody List<ASTNode> rules) {
        try {
            ASTNode combinedRule = ruleEngineService.combineRules(rules);
            return ResponseHandler.generateResponse("Rules combined successfully", HttpStatus.OK, combinedRule);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error combining rules.", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    // API to evaluate rules
    @PostMapping("/evaluate")
    public ResponseEntity<Object> evaluateRule(@RequestBody EvaluationRequest evaluationRequest) {
        try {
            boolean result = ruleEngineService.evaluateRule(evaluationRequest.getRule(), evaluationRequest.getAttributes());
            return ResponseHandler.generateResponse("Rule evaluated successfully", HttpStatus.OK, result);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("Error evaluating rule.", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
