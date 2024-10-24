package com.example.demo.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ASTNode {

    private String type;  // "operator" for AND/OR, "operand" for conditions
    private ASTNode left; // left child node
    private ASTNode right; // right child node
    private String value;  // operand value (like "age > 30")


}
