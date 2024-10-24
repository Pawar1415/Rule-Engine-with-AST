package com.example.demo.service.Impl;

import com.example.demo.model.ASTNode;
import com.example.demo.model.Rule;
import com.example.demo.repository.RuleRepository;
import com.example.demo.service.RuleEngineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class RuleEngineServiceImpl implements RuleEngineService {
    private  RuleRepository ruleRepository;
    private  ObjectMapper objectMapper; // Used to serialize and deserialize ASTNode to JSON


    @Override
    public ASTNode createRule(String ruleString) {
        ruleString = ruleString.trim();
        Stack<ASTNode> nodeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        String[] tokens = ruleString.split(" ");

        for (String token : tokens) {
            token = token.trim();
            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    String operator = operatorStack.pop();
                    ASTNode right = nodeStack.pop();
                    ASTNode left = nodeStack.pop();
                    nodeStack.push(new ASTNode("operator", left, right, operator));
                }
                operatorStack.pop();
            } else if (token.equals("AND") || token.equals("OR")) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                    String operator = operatorStack.pop();
                    ASTNode right = nodeStack.pop();
                    ASTNode left = nodeStack.pop();
                    nodeStack.push(new ASTNode("operator", left, right, operator));
                }
                operatorStack.push(token);
            } else {
                nodeStack.push(new ASTNode("operand", null, null, token));
            }
        }

        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            ASTNode right = nodeStack.pop();
            ASTNode left = nodeStack.pop();
            nodeStack.push(new ASTNode("operator", left, right, operator));
        }

        return nodeStack.pop(); // The final node is the root of the AST
    }
    @Override
    // Combine multiple rules (ASTNodes) using the OR operator
    public ASTNode combineRules(List<ASTNode> rules) {
        if (rules == null || rules.isEmpty()) {
            return null;  // No rules to combine
        }

        ASTNode combined = rules.get(0);  // Start with the first rule
        for (int i = 1; i < rules.size(); i++) {
            combined = new ASTNode("operator", combined, rules.get(i), "OR");  // Combine with OR
        }

        return combined;  // Return the root of the combined AST
    }
    @Override
    // Evaluate the combined AST rule against a set of attributes
    public boolean evaluateRule(ASTNode root, Map<String, Object> attributes) {
        return evaluateAST(root, attributes);
    }

    // Recursive function to evaluate the AST
    private boolean evaluateAST(ASTNode node, Map<String, Object> attributes) {
        if (node == null) return false;

        if (node.getType().equals("operator")) {
            boolean leftResult = evaluateAST(node.getLeft(), attributes);
            boolean rightResult = evaluateAST(node.getRight(), attributes);
            if (node.getValue().equals("AND")) {
                return leftResult && rightResult;
            } else if (node.getValue().equals("OR")) {
                return leftResult || rightResult;
            }
        } else if (node.getType().equals("operand")) {
            return evaluateOperand(node.getValue(), attributes);
        }

        return false;
    }

    // Evaluate operand conditions (like "age > 30")
    private boolean evaluateOperand(String condition, Map<String, Object> attributes) {
        String[] parts = condition.split(" ");
        if (parts.length != 3) return false;

        String attributeName = parts[0];
        String operator = parts[1];
        String value = parts[2];

        Object attributeValue = attributes.get(attributeName);
        if (attributeValue == null) return false;

        if (attributeValue instanceof Integer) {
            int attrVal = (Integer) attributeValue;
            int compVal = Integer.parseInt(value);

            switch (operator) {
                case ">":
                    return attrVal > compVal;
                case "<":
                    return attrVal < compVal;
                case ">=":
                    return attrVal >= compVal;
                case "<=":
                    return attrVal <= compVal;
                case "==":
                    return attrVal == compVal;
                case "!=":
                    return attrVal != compVal;
            }
        } else if (attributeValue instanceof String) {
            String attrVal = (String) attributeValue;
            switch (operator) {
                case "==":
                    return attrVal.equals(value);
                case "!=":
                    return !attrVal.equals(value);
            }
        }

        return false;  // Default to false if no matching case
    }

    // Helper method to define precedence of operators
    private int precedence(String operator) {
        if (operator.equals("AND")) {
            return 2;
        } else if (operator.equals("OR")) {
            return 1;
        }
        return 0;
    }



}
